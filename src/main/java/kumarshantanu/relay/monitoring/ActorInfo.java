package kumarshantanu.relay.monitoring;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.ActorID;

public class ActorInfo {

	public final ActorID actorID;
	public final long[] throughput;
	public final long durMillis;
	public final String durName;

	public ActorInfo(Actor<?> actor) {
		actorID = actor.getActorID();
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
