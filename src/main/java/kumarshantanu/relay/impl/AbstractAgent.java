package kumarshantanu.relay.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.lifecycle.AbstractLifecycleAware;

public abstract class AbstractAgent extends AbstractLifecycleAware implements Agent {

	private static final AtomicCounter COUNTER = new AtomicCounter();

	public final Map<String, Actor<?, ?>> ACTORS =
			new ConcurrentHashMap<String, Actor<?, ?>>();

	public final Collection<Actor<?, ?>> DRAIN_ACTORS =
			new ConcurrentLinkedQueue<Actor<?,?>>();

	public final Runnable drainer = new Runnable() {
		public void run() {
			for (Actor<?, ?> each: DRAIN_ACTORS) {
				if (each.isMailboxEmpty()) {
					DRAIN_ACTORS.remove(each);
				}
			}
		}
	};

	public AbstractAgent(String name) {
		super(name==null? "Agent_" + COUNTER.incrementAndGet(): name);
	}

	public Future<?> removeDrained(ExecutorService threadPool , Future<?> f) {
		if (f == null) {
			return threadPool.submit(drainer);
		}
		if (f.isDone()) {
			if (DRAIN_ACTORS.isEmpty()) {
				return null;
			}
			return threadPool.submit(drainer);
		}
		if (f.isCancelled()) {
			throw new IllegalStateException("Drainer thread was aborted");
		}
		return f;
	}
	
	// ----- Agent methods -----
	
	public void register(Actor<?, ?> actor) {
		Util.notNull(actor, "actor");
		ACTORS.put(actor.getActorID().getActorName(), actor);
	}
	
	public void unregister(Actor<?, ?> actor) {
		Util.notNull(actor, "actor");
		ACTORS.remove(actor.getActorID().getActorName());
	}
	
	public Actor<?, ?> findActor(String name) {
		Util.notNull(name, "name");
		return ACTORS.get(name);
	}

	public Iterable<Actor<?, ?>> listActors() {
		return new ArrayList<Actor<?, ?>>(ACTORS.values());
	}

	public void drain(Actor<?, ?> actor) {
		if (!DRAIN_ACTORS.contains(actor)) {
			DRAIN_ACTORS.add(actor);
		}
	}

}
