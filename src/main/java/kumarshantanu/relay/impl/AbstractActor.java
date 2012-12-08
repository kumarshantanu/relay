package kumarshantanu.relay.impl;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.monitoring.ThroughputAware;
import kumarshantanu.relay.monitoring.TimeVersusCountKeeper;

public abstract class AbstractActor<RequestType, ReturnType>
implements Actor<RequestType, ReturnType>, ThroughputAware {

	private static final AtomicReference<BigInteger> COUNTER =
			new AtomicReference<BigInteger>(new BigInteger("0"));
	
	protected static BigInteger nextCounter() {
        for (;;) {
            BigInteger current = COUNTER.get();
            BigInteger next = current.add(new BigInteger("1"));
            if (COUNTER.compareAndSet(current, next))
                return next;
        }
	}

	public final TimeVersusCountKeeper tvcKeeper = new TimeVersusCountKeeper();

	public final ActorID currentActorID;
	public final ActorID parentActorID;

	public AbstractActor(ActorID parentActorId, String actorName) {
		this.parentActorID = parentActorId;
		if (actorName == null) {
			this.currentActorID = new ActorID(getDefaultName());
		} else {
			this.currentActorID = new ActorID(actorName);
		}
	}

	protected static String getDefaultName() {
		return "Actor_" + nextCounter();
	}

	public ActorID getActorID() {
		return currentActorID;
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
