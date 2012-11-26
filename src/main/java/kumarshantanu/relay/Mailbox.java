package kumarshantanu.relay;

/**
 * Mailbox to hold messages
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <RequestType>
 */
public interface Mailbox<RequestType> {

	/**
	 * Return true if mailbox is empty, false otherwise.
	 * @return
	 */
	public boolean isEmpty();

	/**
	 * Poll the mailbox and return message if available, null otherwise. Note
	 * that the messages may or may not be returned in the order they were
	 * inserted subject to the data structure type used to hold messages.
	 * @return
	 */
	public RequestType poll();

	/**
	 * Add message to the mailbox.
	 * @param message
	 * @throws MailboxException
	 * @see poll
	 */
	public void add(RequestType message) throws MailboxException;

	/**
	 * Cancel a message and return true if message was cancelled, false
	 * otherwise. The semantics are same as in the interface
	 * java.util.concurrent.Future<V>.cancel(false).
	 * @param message
	 * @return
	 */
	public boolean cancel(RequestType message);

}
