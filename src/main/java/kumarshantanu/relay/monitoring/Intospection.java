package kumarshantanu.relay.monitoring;

import java.util.ArrayList;
import java.util.List;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.Agent;

public class Intospection {

	public static List<AgentInfo> takeSnapshot() {
		List<AgentInfo> agentInfo = new ArrayList<AgentInfo>();
		for (Agent ag: Agent.ALL_AGENTS) {
			Iterable<Actor<?, ?>> actors = ag.listActors();
			List<ActorInfo> actorInfo = new ArrayList<ActorInfo>();
			for (Actor<?, ?> each: actors) {
				actorInfo.add(new ActorInfo(each));
			}
			agentInfo.add(new AgentInfo(actorInfo));
		}
		return agentInfo;
	}

}
