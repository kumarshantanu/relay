package kumarshantanu.relay.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.lifecycle.AbstractLifecycleAware;
import kumarshantanu.relay.lifecycle.LifecycleState.LifecycleStateEnum;
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
		setState(LifecycleStateEnum.RUNNING);
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

	protected void correlateSuccess(String correlationID, ReturnType val) {
		if (correlationID!=null) {
			ResponseFuture<ReturnType> future = futures.remove(correlationID);
			if (future!=null) {
				future.finalizeDone(val);
			}
		}
	}

	protected void correlateFailure(String correlationID, Throwable err) {
		if (correlationID!=null) {
			ResponseFuture<ReturnType> future = futures.remove(correlationID);
			if (future!=null) {
				future.finalizeCancel(err);
			}
		}
	}

	// ----- Worker methods (dummy implementation) -----

	public boolean isIdempotent() { return false; }

	public abstract ReturnType act(RequestType req);

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
