package kumarshantanu.relay.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.CorrelatedMessage;
import kumarshantanu.relay.MailboxException;

/**
 * DefaultActor stores messages as-it-is in its mailbox, hence compared to
 * <tt>AmbientActor<tt> it can have a large number of messages in its mailbox
 * without memory overhead; on the flip side, it does not support the
 * interactive <tt>send(message, callback)</tt> method.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <RequestType>
 * @param <ReturnType>
 */
public abstract class DefaultActor<RequestType, ReturnType>
extends AbstractActor<RequestType, ReturnType> {

	public final Map<String, Future<ReturnType>> futures =
			new ConcurrentHashMap<String, Future<ReturnType>>();

	public final AbstractMailbox<RequestType> mailbox;

	public DefaultActor(Agent agent, AbstractMailbox<RequestType> mailbox,
			String actorName, ActorID parentActor) {
		super(parentActor, actorName);
		Util.notNull(agent, "agent");
		if (mailbox != null) {
			this.mailbox = mailbox;
		} else {
			this.mailbox = new DefaultMailbox<RequestType>();
		}
		agent.register(this);
	}

	public DefaultActor(Agent agent) {
		this(agent, null, null, null);
	}

	// ----- internal stuff -----

	private class Job implements Runnable {
		public final CorrelatedMessage<RequestType> corMessage;
		public final ActorID actorID;
		public Job(CorrelatedMessage<RequestType> corMessage, ActorID actorID) {
			this.corMessage = corMessage;
			this.actorID = actorID;
		}
		public void run() {
			CURRENT_ACTOR_ID.set(actorID);
			tvcKeeper.incrementBy(1);
			execute(corMessage.message, corMessage.correlationID);
		}
	}

	// ----- Actor methods -----

	public boolean isMailboxEmpty() {
		return mailbox.isEmpty();
	}

	public Runnable poll(ActorID actorID) {
		final CorrelatedMessage<RequestType> message = mailbox.poll();
		if (message == null) {
			return null;
		}
		return new Job(message, actorID);
	}

	public void send(RequestType message) throws MailboxException {
		send(message, false);
	}

	public Future<ReturnType> send(RequestType message, boolean returnFuture) throws MailboxException {
		if (returnFuture == false) {
			mailbox.add(message, currentActorID, null);
			return null;
		}
		String corID = returnFuture? mailbox.nextCorrelationID(currentActorID): null;
		mailbox.add(message, currentActorID, corID);
		Future<ReturnType> future = new ResponseFuture<ReturnType>();
		if (returnFuture) {
			futures.put(corID, future);
		}
		return future;
	}

}
