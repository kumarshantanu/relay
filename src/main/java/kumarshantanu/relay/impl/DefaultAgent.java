package kumarshantanu.relay.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.lifecycle.LifecycleState.LifecycleStateEnum;

public class DefaultAgent extends AbstractAgent {

	private final AtomicInteger currentJobs = new AtomicInteger(0);

	public final long idleMillis;
	public final ExecutorService threadPool;
	public final int maxjobs;
	
	public DefaultAgent(String name, ExecutorService threadPool, int maxjobs, long idleMillis) {
		super(name);
		this.idleMillis = idleMillis;
		this.threadPool = threadPool;
		this.maxjobs = maxjobs;
	}

	public DefaultAgent(ExecutorService threadPool, int maxjobs, long idleMillis) {
		this(null, threadPool, maxjobs, idleMillis);
	}

	public DefaultAgent(ExecutorService threadPool, int maxjobs) {
		this(null, threadPool, maxjobs, 200L);
	}
	
	public void runInternal() {
		ALL_AGENTS.add(this);
		boolean toSleep = false;
		long steppingIdleMillis = 1;
		LOOP: while (true) {
			if (toSleep) {
				steppingIdleMillis = Math.min(steppingIdleMillis * 2, idleMillis);
			}
			toSleep = true;
			for (String name: ACTORS.keySet()) {
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
				if (currentJobs.get() < maxjobs) {
					Actor<?> a = findActor(name);
					if (a != null && a.getState() == LifecycleStateEnum.RUNNING) {
						final Runnable r = a.poll(a.getActorID());
						if (r != null) {
							currentJobs.incrementAndGet();
							toSleep = false;
							steppingIdleMillis = 1;
							threadPool.execute(new Runnable() {
								public void run() {
									try { r.run(); }
									finally { currentJobs.decrementAndGet(); }
								}
							});
						}
					}
				}
			}
			if (toSleep) {
				Util.sleep(steppingIdleMillis);
			}
		}
	}
	
}
