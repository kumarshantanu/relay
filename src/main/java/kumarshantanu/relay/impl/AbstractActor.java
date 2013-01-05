package kumarshantanu.relay.impl;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.monitoring.ThroughputAware;
import kumarshantanu.relay.monitoring.TimeVersusCountKeeper;

public abstract class AbstractActor<RequestType, ReturnType>
implements Actor<RequestType, ReturnType>, ThroughputAware {

	private static final AtomicReference<BigInteger> COUNTER =
			new AtomicReference<BigInteger>(new BigInteger("0"));

	private static final BigInteger ONE = new BigInteger("1");

	protected static BigInteger nextCounter() {
        for (;;) {
            BigInteger current = COUNTER.get();
            BigInteger next = current.add(ONE);
            if (COUNTER.compareAndSet(current, next))
                return next;
        }
	}

	public final TimeVersusCountKeeper tvcKeeper = new TimeVersusCountKeeper();

	public final ActorID currentActorID;
	public final ActorID parentActorID;
	public final Map<String, ResponseFuture<ReturnType>> futures;


	public AbstractActor(ActorID parentActorId, String actorName,
			Map<String, ResponseFuture<ReturnType>> futures) {
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
		return "Actor_" + nextCounter();
	}

	public ActorID getActorID() {
		return currentActorID;
	}

	// ----- helper methods -----

	protected void execute(RequestType message, String correlationID) {
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
