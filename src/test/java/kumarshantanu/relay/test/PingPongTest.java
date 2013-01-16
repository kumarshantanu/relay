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

public class PingPongTest {

	volatile long MAX_COUNT = 1000000;
	final AtomicLong counter = new AtomicLong(0);

	@Test
	public void test() {
		Assert.assertTrue("Test started", true);
		ExecutorService threadPool = Util.newThreadPool();
		DefaultAgent ag = new DefaultAgent(threadPool);
		PingActor ping = new PingActor();
		ag.register(ping);
		PongActor pong = new PongActor();
		ag.register(pong);
		ping.setPong(pong);
		pong.setPing(ping);
		// --
		Runnable dummy = new Runnable() {
			public void run() {}
		};
		final int WARM_UP = 10000;
		final int PERF_TEST = 25000;
		Helper h = new Helper();
		threadPool.execute(ag);
		System.out.println(Actor.CURRENT_ACTOR_ID.get());
		MAX_COUNT = WARM_UP;
		try { ping.send("start"); } catch (MailboxException e) { e.printStackTrace(); }
		h.doTimes(WARM_UP, counter, dummy, "Warm up");
		System.out.println(((ThroughputAware) ping).getThroughputString());
		MAX_COUNT = PERF_TEST;
		try { ping.send("start"); } catch (MailboxException e) { e.printStackTrace(); }
		h.doTimes(PERF_TEST, counter, dummy, "Perf test");
		System.out.println(((ThroughputAware) ping).getThroughputString());
		Assert.assertTrue("Test finished", true);
		ag.stop();
	}

	public class PingActor extends DefaultActor<String> {
		private PongActor pong = null;
		public void setPong(PongActor pong) {
			this.pong = pong;
		}
		public void act(String req) {
			try {
				long val = counter.incrementAndGet();
				if (val < MAX_COUNT) {
					pong.send("ping");
				}
			} catch (MailboxException e) {
				e.printStackTrace();
			}
		}
	}

	public class PongActor extends DefaultActor<String> {
		private PingActor ping = null;
		public void setPing(PingActor ping) {
			this.ping = ping;
		}
		public void act(String req) {
			try {
				ping.send("pong");
			} catch (MailboxException e) {
				e.printStackTrace();
			}
		}
	}

}
