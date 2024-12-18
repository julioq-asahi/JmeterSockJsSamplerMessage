package orgMiJmeterSockjsSampler;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public class SockJsWebsocketStompSessionHandler extends StompSessionHandlerAdapter
{
	private String subscribeHeaders;
	private long connectionTime;
	private long responseBufferTime;
	private String messageStorage = "";
	private ResponseMessage responseMessage;
	private boolean isConnected = false;
		
	public SockJsWebsocketStompSessionHandler(String subscribeHeaders, long connectionTime, long responseBufferTime, ResponseMessage responseMessage) {
		this.subscribeHeaders = subscribeHeaders;
		this.connectionTime = connectionTime;
		this.responseBufferTime = responseBufferTime;
		this.responseMessage = responseMessage;
	}
	
	public String getMessageStorage() {
		return this.messageStorage;
	}
	
	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		this.isConnected = true;
		String connectionMessage = "Session id: " + session.getSessionId()
								 + "\n - Waiting for the server connection for " + this.connectionTime + " MILLISECONDS"
								 + "\n - WebSocket connection has been opened"
								 + "\n - Connection established";

		this.responseMessage.addMessage(connectionMessage);	
	    this.subscribeTo(session);
	}
	
	@Override
	public void handleException(
		StompSession session, 
		StompCommand command, 
		StompHeaders headers,
		byte[] payload, 
		Throwable exception
	) {
		String exceptionMessage = " - Received exception: " + exception.getMessage();
		
		this.responseMessage.addProblem(exceptionMessage);
	}
	
	/**
	 * This implementation is empty.
	 */
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		String handleFrameMessage = " - Received frame: " + payload.toString();

		this.responseMessage.addMessage(handleFrameMessage);
	}


	/**
	 * This implementation is empty.
	 */
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		String exceptionMessage = " - Received exception: " + exception.getMessage();
		
		this.responseMessage.addProblem(exceptionMessage);
	}
	
	private void subscribeTo(StompSession session)
	{
		StompHeaders headers = new StompHeaders();
		String[] splitHeaders = subscribeHeaders.split("\n");
		for (int i = 0; i < splitHeaders.length; i++) {
			int key = 0;
			int value = 1;
			String[] headerParameter = splitHeaders[i].split(":");
			headers.add(headerParameter[key], headerParameter[value]);			
		}
	    
	    session.subscribe(headers, new SockJsWebsocketSubscriptionHandler(this.responseMessage, this.responseBufferTime));
	}

    public void sendMessage(StompSession session, String destination, String message) {
        if (session != null && destination != null && message != null) {
            synchronized (session) {
                try {
                    StompHeaders stompHeaders = new StompHeaders();
                    stompHeaders.setDestination(destination);
                    session.send(stompHeaders, message.getBytes());
                } catch (Exception e) {
                    responseMessage.addProblem("Error on sending message: " + e.getMessage());
                }
            }
        }
    }

    // Notify the wait method in sendMessage when the connection is ready
    public synchronized void notifyConnection() {
        isConnected = true;
        notifyAll();
    }
}
