package kumarshantanu.relay.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.impl.DefaultActor;
import kumarshantanu.relay.impl.DefaultAgent;
import kumarshantanu.relay.impl.Util;
import kumarshantanu.relay.monitoring.ThroughputAware;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {
	
	private interface ActorFactory {
		public Actor<String> create(AtomicLong counter);
	}
	
	@Test
	public void defaultActorTest() {
		test(new ActorFactory() {
			public Actor<String> create(final AtomicLong counter) {
				return new DefaultActor<String>() {
					public void act(String req) {
						counter.incrementAndGet();
					}
				};
			}
		});
	}
	
	public void test(ActorFactory afactory) {
		Assert.assertTrue("Test started", true);
		final AtomicLong counter = new AtomicLong();
		ExecutorService threadPool = Util.newThreadPool();
		DefaultAgent ag = new DefaultAgent(threadPool, Util.optimumThreadCount());
		final Actor<String> ac = afactory.create(counter);
		ag.register(ac);
		Runnable sender = new Runnable() {
			public void run() {
				try {
					ac.send("Hello World!");
				} catch (MailboxException e) {
					e.printStackTrace();
				}
			}
		};
		Helper h = new Helper();
		threadPool.execute(ag);
		System.out.println(Actor.CURRENT_ACTOR_ID.get());
		h.doTimes(1000000, counter, sender, "Warm up");
		System.out.println(((ThroughputAware) ac).getThroughputString());
		h.doTimes(2500000, counter, sender, "Perf test");
		System.out.println(((ThroughputAware) ac).getThroughputString());
		Assert.assertTrue("Test finished", true);
		ag.stop();
	}
	
}
