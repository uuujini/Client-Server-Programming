public class TokenDifferentException extends Exception {
	private static final long serialVersionUID = 1L;
	private String actionMessage;
	TokenDifferentException(String actionMessage) {
        super(actionMessage);
        this.actionMessage = actionMessage;}
	public String doAction() { return actionMessage; }}