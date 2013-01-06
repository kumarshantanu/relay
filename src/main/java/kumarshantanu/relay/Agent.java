package kumarshantanu.relay;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import kumarshantanu.relay.lifecycle.LifecycleAware;


public interface Agent extends Runnable, LifecycleAware {

	/**
	 * All agents should add themselves to this set upon startup.
	 */
	public static final Set<Agent> ALL_AGENTS = new CopyOnWriteArraySet<Agent>();

	/**
	 * Register actor with the agent, which processes messages for registered
	 * actors only.
	 * @param actor
	 */
	public void register(Actor<?, ?> actor);

	/**
	 * Unregister actor from the agent, and stop processing its mailbox
	 * messages.
	 * @param actor
	 */
	public void unregister(Actor<?, ?> actor);

	/**
	 * Ensure that the actor will not be garbage collected until its mailbox is
	 * empty. Whether the actor will be garbage collected after the mailbox is
	 * empty depends on whether you hold a reference to the actor.
	 * @param actor
	 */
	public void drain(Actor<?, ?> actor);

	/**
	 * Given the actor name find the actor instance and return it. Return null
	 * if no actor instance is found.
	 * @param name
	 * @return
	 */
	public Actor<?, ?> findActor(String name);

	/**
	 * Return a list of all registered actors.
	 * @return
	 */
	public Iterable<Actor<?, ?>> listActors();

}