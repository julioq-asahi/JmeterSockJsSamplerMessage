package orgMiJmeterSockjsSampler;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.springframework.messaging.simp.stomp.StompSession;

public class WebSocketMessageSampler extends AbstractJavaSamplerClient {

    private WebSocketMessageSender messageSender;

    @Override
    public Arguments getDefaultParameters() {
        Arguments args = new Arguments();
        args.addArgument("messageDestination", "/app/chat/1");
        args.addArgument("messageBody", "{\"author\": \"John Doe\", \"text\": \"Hello, World!\"}");
        args.addArgument("maxRetries", "10");
        args.addArgument("delayMillis", "100");
        return args;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        messageSender = new WebSocketMessageSender();
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        result.sampleStart();

        String destination = context.getParameter("messageDestination");
        String message = context.getParameter("messageBody");
        int maxRetries = context.getIntParameter("maxRetries");
        int delayMillis = context.getIntParameter("delayMillis");

        ResponseMessage responseMessage = new ResponseMessage();
        StompSession session = WebSocketSessionManager.getInstance().getSession();

        if (session != null && session.isConnected()) {
            try {
                messageSender.sendMessageWithRetry(session, destination, message, maxRetries, delayMillis, responseMessage);
                result.setSuccessful(true);
                result.setResponseMessage(responseMessage.getMessage());
            } catch (Exception e) {
                result.setSuccessful(false);
                result.setResponseMessage("Message sending failed.\n" + e.getMessage());
            }
        } else {
            result.setSuccessful(false);
            result.setResponseMessage("No active session available.");
        }

        result.sampleEnd();
        return result;
    }
}