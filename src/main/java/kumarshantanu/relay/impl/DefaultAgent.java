package kumarshantanu.relay.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.Agent;

public class DefaultAgent implements Agent {
	
	public final AgentCommon agentCommon = new AgentCommon();
	
	public final long idleMillis;
	public final ExecutorService threadPool;
	
	public DefaultAgent(ExecutorService threadPool, long idleMillis) {
		this.idleMillis = idleMillis;
		this.threadPool = threadPool;
	}
	
	public DefaultAgent(int threadCount, long idleMillis) {
		this(Util.newThreadPool(threadCount), idleMillis);
	}
	
	public DefaultAgent(int threadCount) {
		this(threadCount, 200L);
	}
	
	public DefaultAgent(ExecutorService threadPool) {
		this(threadPool, 200L);
	}
	
	public DefaultAgent() {
		this(Util.optimumThreadCount());
	}
	
	public void run() {
		ALL_AGENTS.add(this);
		boolean toSleep = false;
		long steppingIdleMillis = 1;
		long now = System.currentTimeMillis();
		long drainerTime = now;
		Future<?> drainer = null;
		while (true) {
			toSleep = true;
			steppingIdleMillis = Math.min(steppingIdleMillis * 2, idleMillis);
			now = System.currentTimeMillis();
			if (now - drainerTime > 1000) {  // check no more than once a second
				drainerTime = now;
				drainer = agentCommon.removeDrained(threadPool, drainer);
			}
			for (String name: agentCommon.ACTORS.keySet()) {
				Actor<?, ?> a = agentCommon.findActor(name);
				if (a != null) {
					Runnable r = a.poll(a.getActorId());
					if (r != null) {
						toSleep = false;
						steppingIdleMillis = 1;
						threadPool.execute(r);
					}
				}
			}
			if (toSleep) {
				Util.sleep(steppingIdleMillis);
			}
		}
	}
	
	// ----- Actor implementation -----
	
	public void register(Actor<?, ?> actor) {
		agentCommon.register(actor);
	}
	
	public void unregister(Actor<?, ?> actor) {
		agentCommon.unregister(actor);
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
