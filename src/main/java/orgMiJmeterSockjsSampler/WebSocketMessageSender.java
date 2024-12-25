package orgMiJmeterSockjsSampler;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class WebSocketMessageSender {

    public void sendMessageWithRetry(StompSession session, String destination, String message, int maxRetries, int delayMillis, ResponseMessage responseMessage) {
        int attempts = 0;
        boolean sent = false;

        while (!sent && attempts < maxRetries) {
            try {
                attempts++;
                StompHeaders stompHeaders = new StompHeaders();
                stompHeaders.setDestination(destination);
                session.send(stompHeaders, message.getBytes());
                sent = true;
                responseMessage.addMessage("Message sent successfully to: " + destination);
            } catch (Exception e) {
                responseMessage.addProblem("Attempt " + attempts + " failed: " + e.getMessage());
                if (attempts >= maxRetries) {
                    responseMessage.addProblem("Max attempts reached. Giving up.");
                } else {
                    sleep(delayMillis);
                }
            }
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}