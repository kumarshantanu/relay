package kumarshantanu.relay.impl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.Agent;

/**
 * Common agent functions factored out due to Java's lack of mixins.
 * @author Shantanu Kumar
 *
 */
public class AgentCommon implements Agent {

	public final Map<String, WeakReference<Actor<?, ?>>> ACTORS =
			new ConcurrentHashMap<String, WeakReference<Actor<?,?>>>();
	
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
	
	public void run() {
		// we do not implement Runnable in this class
	}

	// ----- Agent methods -----
	
	public void register(Actor<?, ?> actor) {
		Util.notNull(actor, "actor");
		ACTORS.put(actor.getActorId().getActorName(),
				new WeakReference<Actor<?,?>>(actor));
	}
	
	public Actor<?, ?> findActor(String name) {
		Util.notNull(name, "name");
		WeakReference<Actor<?, ?>> ref = ACTORS.get(name);
		if (ref == null) {
			return null;
		}
		Actor<?, ?> a = ref.get();
		if (a == null) {
			ACTORS.remove(name);
		}
		return a;
	}

	public Iterable<Actor<?, ?>> listActors() {
		List<Actor<?, ?>> actors = new ArrayList<Actor<?,?>>();
		for (String name: ACTORS.keySet()) {
			WeakReference<Actor<?, ?>> eachRef = ACTORS.get(name);
			Actor<?, ?> each = eachRef==null? null: eachRef.get();
			if (each!=null) {
				actors.add(each);
			}
		}
		return actors;
	}

	public void drain(Actor<?, ?> actor) {
		if (!DRAIN_ACTORS.contains(actor)) {
			DRAIN_ACTORS.add(actor);
		}
	}

}
