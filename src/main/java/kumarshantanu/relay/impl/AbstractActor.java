package kumarshantanu.relay.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.lifecycle.AbstractLifecycleAware;
import kumarshantanu.relay.monitoring.ThroughputAware;
import kumarshantanu.relay.monitoring.TimeVersusCountKeeper;

public abstract class AbstractActor<RequestType, ReturnType>
extends AbstractLifecycleAware
implements Actor<RequestType, ReturnType>, ThroughputAware {

	private static final AtomicCounter COUNTER = new AtomicCounter();

	public final TimeVersusCountKeeper tvcKeeper = new TimeVersusCountKeeper();

	public final ActorID currentActorID;
	public final ActorID parentActorID;
	public final Map<String, ResponseFuture<ReturnType>> futures;


	public AbstractActor(ActorID parentActorId, String actorName,
			Map<String, ResponseFuture<ReturnType>> futures) {
		super(actorName);
		this.parentActorID = parentActorId;
		if (actorName == null) {
			this.currentActorID = new ActorID(getDefaultName());
		} else {
			this.currentActorID = new ActorID(actorName);
		}
		if (futures == null) {
			this.futures = new ConcurrentHashMap<String, ResponseFuture<ReturnType>>();
		} else {
			this.futures = futures;
		}
	}

	public AbstractActor(ActorID parentActorId, String actorName) {
		this(parentActorId, actorName, null);
	}

	protected static String getDefaultName() {
		return "Actor_" + COUNTER.incrementAndGet();
	}

	public ActorID getActorID() {
		return currentActorID;
	}

	@Override
	public void runInternal() {
		// do nothing, because actors are not instances of Runnable
	}

	// ----- helper methods -----

	protected void executeLocal(RequestType message, String correlationID) {
		if (correlationID==null) {
			execute(message);
			return;
		}
		ResponseFuture<ReturnType> future = futures.get(correlationID);
		futures.remove(correlationID);
		if (future==null) {
			execute(message);
		} else {
			try {
				future.finalizeDone(execute(message));
			} catch(RuntimeException e) {
				future.finalizeCancel();
				throw e;
			}
			finally {
				futures.remove(correlationID);
			}
		}
	}

	// ----- Worker methods (dummy implementation) -----

	public boolean isIdempotent() { return false; }

	public abstract ReturnType execute(RequestType req);

	// ----- ThroughputAware implementation -----

	public long[] getThroughput() {
		return tvcKeeper.getThroughput();
	}

	public String getThroughputString() {
		return tvcKeeper.getThroughputString();
	}

	public long getUnitDurationMillis() {
		return tvcKeeper.getUnitDurationMillis();
	}

	public String getUnitDurationName() {
		return tvcKeeper.getUnitDurationName();
	}

}
