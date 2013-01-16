package kumarshantanu.relay;

import kumarshantanu.relay.lifecycle.LifecycleAware;


/**
 * An actor is a Worker with a Mailbox. One should be able to call an actor
 * both synchronously and asynchronously.
 * @author shantanu
 *
 * @param <RequestType>
 * @param <ReturnType>
 */
public interface Actor<RequestType> extends Worker<RequestType>, LifecycleAware {

	public static final ThreadLocal<ActorID> CURRENT_ACTOR_ID =
			new ThreadLocal<ActorID>();

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

}