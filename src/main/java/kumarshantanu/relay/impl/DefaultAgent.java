package kumarshantanu.relay.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import kumarshantanu.relay.Actor;

public class DefaultAgent extends AbstractAgent {

	public final long idleMillis;
	public final ExecutorService threadPool;
	
	public DefaultAgent(String name, ExecutorService threadPool, long idleMillis) {
		super(name);
		this.idleMillis = idleMillis;
		this.threadPool = threadPool;
	}

	public DefaultAgent(ExecutorService threadPool, long idleMillis) {
		this(null, threadPool, idleMillis);
	}

	public DefaultAgent(ExecutorService threadPool) {
		this(null, threadPool, 200L);
	}
	
	public DefaultAgent(int threadCount) {
		this(null, Util.newThreadPool(threadCount), 200L);
	}

	public DefaultAgent() {
		this(Util.optimumThreadCount());
	}
	
	public void runInternal() {
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
				drainer = removeDrained(threadPool, drainer);
			}
			for (String name: ACTORS.keySet()) {
				Actor<?, ?> a = findActor(name);
				if (a != null) {
					Runnable r = a.poll(a.getActorID());
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
	
}
