package kumarshantanu.relay.monitoring;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorId;

public class ActorInfo {

	public final ActorId actorId;
	public final long[] throughput;
	public final long durMillis;
	public final String durName;

	public ActorInfo(Actor<?, ?> actor) {
		actorId = actor.getActorId();
		if (actor instanceof ThroughputAware) {
			ThroughputAware t = (ThroughputAware) actor;
			throughput = t.getThroughput();
			durMillis = t.getUnitDurationMillis();
			durName = t.getUnitDurationName();
		} else {
			throughput = new long[0];
			durMillis = -1;
			durName = "Unknown";
		}
	}
}
