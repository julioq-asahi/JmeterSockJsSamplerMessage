package orgMiJmeterSockjsSampler;

public class ResponseMessage {

    private StringBuilder messages = new StringBuilder();
    private StringBuilder problems = new StringBuilder();

    public String getMessage() {
        return messages.toString();
    }

    public void addMessage(String message) {
        messages.append(message).append("\n");
    }

    public String getProblems() {
        return problems.toString();
    }

    public void addProblem(String problem) {
        problems.append(problem).append("\n");
    }
}