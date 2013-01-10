package kumarshantanu.relay.impl;

import java.util.concurrent.Future;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.MailboxException;

public abstract class GenericActor<RequestType, PollType, ReturnType>
extends AbstractActor<RequestType, ReturnType> {

	public final AbstractMailbox<RequestType, PollType> mailbox;
	public final PollConverter<RequestType, PollType> pollConverter;

	public GenericActor(Agent agent, AbstractMailbox<RequestType, PollType> mailbox,
			PollConverter<RequestType, PollType> pollConverter,
			String actorName, ActorID parentActor) {
		super(parentActor, actorName);
		Util.notNull(agent, "agent");
		Util.notNull(mailbox, "mailbox");
		Util.notNull(pollConverter, "pollConverter");
		this.mailbox = mailbox;
		this.pollConverter = pollConverter;
		agent.register(this);
	}

	public GenericActor(Agent agent, AbstractMailbox<RequestType, PollType> mailbox,
			PollConverter<RequestType, PollType> pollConverter) {
		this(agent, mailbox, pollConverter, null, null);
	}

	public GenericActor(Agent agent, AbstractMailbox<RequestType, PollType> mailbox) {
		this(agent, mailbox, null, null, null);
	}

	// ----- internal stuff -----

	protected abstract void onSuccess(PollType poll, ReturnType val);
	protected abstract void onFailure(PollType poll, Throwable err);

	private class Job implements Runnable {
		public final PollType message;
		public final ActorID actorID;
		public Job(PollType message, ActorID actorID) {
			this.message = message;
			this.actorID = actorID;
		}
		public void run() {
			CURRENT_ACTOR_ID.set(actorID);
			tvcKeeper.incrementBy(1);
			try {
				ReturnType val = act(pollConverter.getMessage(message));
				onSuccess(message, val);
			} catch(Throwable t) {
				onFailure(message, t);
			}
		}
	}

	// ----- Actor methods -----

	public boolean isMailboxEmpty() {
		return mailbox.isEmpty();
	}

	public Runnable poll(ActorID actorID) {
		final PollType message = mailbox.poll();
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
		ResponseFuture<ReturnType> future = new ResponseFuture<ReturnType>();
		if (returnFuture) {
			futures.put(corID, future);
		}
		return future;
	}

}
