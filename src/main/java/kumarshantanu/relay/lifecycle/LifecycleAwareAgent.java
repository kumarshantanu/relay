package kumarshantanu.relay.lifecycle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.impl.AgentCommon;
import kumarshantanu.relay.impl.Util;
import kumarshantanu.relay.lifecycle.LifecycleState.LifecycleStateEnum;

public class LifecycleAwareAgent extends AbstractLifecycleAware implements Agent {

	public final AgentCommon agentCommon = new AgentCommon();
	
	public final long idleMillis;
	public final ExecutorService threadPool;
	
	public LifecycleAwareAgent(String name, ExecutorService threadPool,
			long idleMillis) {
		super(name);
		this.idleMillis = idleMillis;
		this.threadPool = threadPool;
	}
	
	public LifecycleAwareAgent(String name, int threadCount, long idleMillis) {
		this(name, threadCount > 0? Executors.newFixedThreadPool(threadCount):
			Executors.newCachedThreadPool(), idleMillis);
	}
	
	public LifecycleAwareAgent(String name, int threadCount) {
		this(name, threadCount, 200L);
	}
	
	public LifecycleAwareAgent(String name) {
		this(name, Runtime.getRuntime().availableProcessors() * 2 + 1);
	}
	
	@Override
	public void execute() {
		ALL_AGENTS.add(this);
		boolean toSleep = false;
		Future<?> drainer = null;
		LOOP: while (true) {
			toSleep = true;
			drainer = agentCommon.removeDrained(threadPool, drainer);
			for (String name: agentCommon.ACTORS.keySet()) {
				// check Lifecycle status
				final LifecycleStateEnum state = getState();
				if (state == LifecycleStateEnum.STOPPED ||
						state == LifecycleStateEnum.FORCE_STOPPED) {
					ALL_AGENTS.remove(this);
					break LOOP;
				}
				if (state == LifecycleStateEnum.SUSPENDED) {
					toSleep = true;
					break;
				}
				// usual business
				Actor<?, ?> a = agentCommon.findActor(name);
				if (a != null) {
					Runnable r = a.poll(a.getActorId());
					if (r != null) {
						toSleep = false;
						threadPool.execute(r);
					}
				}
			}
			if (toSleep) {
				Util.sleep(idleMillis);
			}
		}
	}
	
	// ----- Agent methods -----
	
	public void register(Actor<?, ?> actor) {
		agentCommon.register(actor);
	}
	
	public Actor<?, ?> findActor(String name) {
		return agentCommon.findActor(name);
	}
	
	public Iterable<Actor<?, ?>> listActors() {
		return agentCommon.listActors();
	}
	
	public void drain(Actor<?, ?> actor) {
		agentCommon.drain(actor);
	}
	
}
