package kumarshantanu.relay.impl;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.lifecycle.AbstractLifecycleAware;

public abstract class AbstractAgent extends AbstractLifecycleAware implements Agent {

	private static final AtomicCounter COUNTER = new AtomicCounter();

	public final Map<String, Actor<?>> ACTORS =
			new ConcurrentHashMap<String, Actor<?>>();

	public AbstractAgent(String name) {
		super(name==null? "Agent_" + COUNTER.incrementAndGet(): name);
	}

	// ----- Agent methods -----
	
	public void register(Actor<?> actor) {
		Util.assertNotNull(actor, "actor");
		ACTORS.put(actor.getActorID().getActorName(), actor);
	}
	
	public void unregister(Actor<?> actor) {
		Util.assertNotNull(actor, "actor");
		ACTORS.remove(actor.getActorID().getActorName());
	}
	
	public Actor<?> findActor(String name) {
		Util.assertNotNull(name, "name");
		return ACTORS.get(name);
	}

	public Iterable<Actor<?>> listActors() {
		return new ArrayList<Actor<?>>(ACTORS.values());
	}

}
