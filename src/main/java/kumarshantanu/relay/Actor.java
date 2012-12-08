package kumarshantanu.relay;

import java.util.concurrent.Future;


/**
 * An actor is a Worker with a Mailbox. One should be able to call an actor
 * both synchronously and asynchronously.
 * @author shantanu
 *
 * @param <RequestType>
 * @param <ReturnType>
 */
public interface Actor<RequestType, ReturnType> extends Worker<RequestType, ReturnType> {

	public static final ThreadLocal<ActorID> CURRENT_ACTOR_ID =
			new ThreadLocal<ActorID>();

	public abstract boolean isMailboxEmpty();

	/**
	 * Poll the mailbox for messages. If mailbox is empty, return null. If a
	 * message is found, return a Runnable instance that processes the message
	 * taken from the mailbox.
	 * @param actorId
	 * @return instance of Runnable that can be executed by agent thread-pool
	 */
	public abstract Runnable poll(ActorID actorID);

	/**
	 * Get ActorId instance of the actor.
	 * @return
	 */
	public abstract ActorID getActorID();

	/**
	 * Send a message to the mailbox of the actor. The message is queued until
	 * it is processed by an agent the actor is registered with. What happens
	 * to the result of processing is implementation dependent.
	 * @param message
	 * @throws MailboxException
	 */
	public void send(RequestType message) throws MailboxException;

	/**
	 * Send a message to the mailbox of the actor. The message is queued until
	 * it is processed by an agent the actor is registered with. What happens
	 * to the result of processing is implementation dependent.
	 * Returns a Future<ReturnType> when <tt>returnFuture</tt> is true, null
	 * otherwise. Note that returning a Future<ReturnType> may increase memory
	 * footprint of the message.
	 * @param message
	 * @param returnFuture
	 * @return
	 * @throws MailboxException
	 */
	public Future<ReturnType> send(RequestType message, boolean returnFuture)
			throws MailboxException;

	/**
	 * Send a message to the mailbox of the actor, with a callback to be invoked
	 * with the result (optional operation). The message is queued until
	 * it is processed by an agent the actor is registered with. After the
	 * message is processed the callback handles the result.
	 * @param message
	 * @throws MailboxException
     * @throws UnsupportedOperationException if the <tt>send(message, callback)</tt>
     *         operation is not supported by this actor
	 */
	public void send(RequestType message,
			Callback<ReturnType> handler) throws MailboxException;

	/**
	 * Send a message to the mailbox of the actor, with a callback to be invoked
	 * with the result (optional operation). The message is queued until
	 * it is processed by an agent the actor is registered with. After the
	 * message is processed the callback handles the result.
	 * Returns a Future<ReturnType> when <tt>returnFuture</tt> is true, null
	 * otherwise. Note that returning a Future<ReturnType> may increase memory
	 * footprint of the message.
	 * @param message the message to the actor
	 * @param handler the callback handler
	 * @param returnFuture if true then call returns a valid Future<ReturnRype>
	 * @return Future<ReturnType> if <tt>returnFuture</tt> is true, null otherwise
	 * @throws MailboxException
	 */
	public Future<ReturnType> send(RequestType message,
			Callback<ReturnType> handler, boolean returnFuture) throws MailboxException;

}