package kumarshantanu.relay;


/**
 * Mailbox to hold messages
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <RequestType>
 */
public interface Mailbox<RequestType, PollType> {

	/**
	 * Poll the mailbox and return message if available, null otherwise. Note
	 * that the messages may or may not be returned in the order they were
	 * inserted subject to the data structure type used to hold messages.
	 * @return
	 */
	public PollType poll();

	/**
	 * Add message to the mailbox.
	 * @param message message to be added to mailbox
	 * @param actorID ActorID instance the message is associated with
	 * @param correlationID the message correlation ID (for 2-way communication)
	 * @throws MailboxException
	 * @see poll
	 */
	public void add(RequestType message, ActorID actorID, String correlationID) throws MailboxException;

	/**
	 * Cancel a message and return true if message was cancelled, false
	 * otherwise. The semantics are same as in the interface
	 * java.util.concurrent.Future<V>.cancel(false).
	 * @param message
	 * @return
	 */
	public boolean cancel(RequestType message, ActorID actorID) throws MailboxException;

}
