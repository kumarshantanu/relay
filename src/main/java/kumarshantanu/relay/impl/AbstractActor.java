package kumarshantanu.relay.impl;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.lifecycle.AbstractLifecycleAware;
import kumarshantanu.relay.lifecycle.LifecycleState.LifecycleStateEnum;
import kumarshantanu.relay.monitoring.ThroughputAware;
import kumarshantanu.relay.monitoring.TimeVersusCountKeeper;

public abstract class AbstractActor<RequestType>
extends AbstractLifecycleAware
implements Actor<RequestType>, ThroughputAware {

	private static final AtomicCounter COUNTER = new AtomicCounter();

	public final TimeVersusCountKeeper tvcKeeper = new TimeVersusCountKeeper();

	public final ActorID currentActorID;
	public final ActorID parentActorID;


	public AbstractActor(ActorID parentActorId, String actorName) {
		super(actorName);
		this.parentActorID = parentActorId;
		if (actorName == null) {
			this.currentActorID = new ActorID(getDefaultName());
		} else {
			this.currentActorID = new ActorID(actorName);
		}
		setState(LifecycleStateEnum.RUNNING);
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

	protected void onFailure(Throwable error) {
		error.printStackTrace();
	}

	// ----- Worker methods (dummy implementation) -----

	public boolean isIdempotent() { return false; }

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
