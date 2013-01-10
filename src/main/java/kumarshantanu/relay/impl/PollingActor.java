package kumarshantanu.relay.impl;

import java.util.concurrent.Future;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.MailboxException;

/**
 * Actor that polls some state in order to act. You must implement the
 * <tt>poll</tt> and <tt>execute</tt> methods. The <tt>execute</tt> method
 * receives <tt>null</tt> as argument. Note that <tt>poll</tt> must return as
 * quickly possible as it is called synchronously by the agent.
 * @author shantanu
 *
 * @param <ReturnType>
 */
public abstract class PollingActor<ReturnType> extends AbstractActor<Object, ReturnType> {

	public PollingActor(Agent agent, String actorName, ActorID parentActor) {
		super(parentActor, actorName);
		Util.notNull(agent, "agent");
		agent.register(this);
	}

	public PollingActor(Agent ag) {
		this(ag, null, null);
	}

	public abstract boolean poll();

	// ----- internal stuff -----

	private class Job implements Runnable {
		public final ActorID actorID;
		public Job(ActorID actorID) {
			this.actorID = actorID;
		}
		public void run() {
			CURRENT_ACTOR_ID.set(actorID);
			tvcKeeper.incrementBy(1);
			act(null);
		}
	}

	// ----- Actor methods -----

	public boolean isMailboxEmpty() {
		return true;
	}

	public Runnable poll(ActorID actorID) {
		if (poll()) {
			return new Job(actorID);
		}
		return null;
	}

	public void send(Object message) throws MailboxException {
		throw new IllegalStateException("send is not supported on this actor");
	}

	public Future<ReturnType> send(Object message, boolean returnFuture)
			throws MailboxException {
		throw new IllegalStateException("send is not supported on this actor");
	}

}
