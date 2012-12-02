package kumarshantanu.relay;

/**
 * Exception when dealing with a mailbox.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 */
public class MailboxException extends RuntimeException {
	
	/**
	 * serialVersionUUID
	 */
	private static final long serialVersionUID = -6219838237777468064L;
	
	
	public final Mailbox<?> mailbox;
	
	public MailboxException(Mailbox<?> mbox, String message) {
		super(message);
		this.mailbox = mbox;
	}
	
	public MailboxException(Mailbox<?> mbox, Throwable cause) {
		super(cause);
		this.mailbox = mbox;
	}
	
	public MailboxException(Mailbox<?> mbox, String message, Throwable cause) {
		super(message, cause);
		this.mailbox = mbox;
	}

}
