package orgMiJmeterSockjsSampler;

import org.springframework.messaging.simp.stomp.StompSession;

public class WebSocketSessionManager {

    private static WebSocketSessionManager instance;
    private StompSession session;

    private WebSocketSessionManager() {
    }

    public static synchronized WebSocketSessionManager getInstance() {
        if (instance == null) {
            instance = new WebSocketSessionManager();
        }
        return instance;
    }

    public synchronized StompSession getSession() {
        return session;
    }

    public synchronized void setSession(StompSession session) {
        this.session = session;
    }
}