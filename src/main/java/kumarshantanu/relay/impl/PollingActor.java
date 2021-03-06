package kumarshantanu.relay.impl;

import kumarshantanu.relay.ActorID;
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
public abstract class PollingActor extends AbstractActor<Object> {

	public PollingActor(String actorName, ActorID parentActor) {
		super(parentActor, actorName);
	}

	public PollingActor() {
		this(null, null);
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
			try {
				act(null);
			} catch(Throwable error) {
				onFailure(error);
			}
		}
	}

	// ----- Actor methods -----

	public Runnable poll(ActorID actorID) {
		if (poll()) {
			return new Job(actorID);
		}
		return null;
	}

	public void send(Object message) throws MailboxException {
		throw new IllegalStateException("send is not supported on this actor");
	}

}
