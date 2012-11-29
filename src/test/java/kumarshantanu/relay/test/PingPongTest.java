package kumarshantanu.relay.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.Agent;
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
		ExecutorService threadPool = Util.newThreadPool(2);
		DefaultAgent ag = new DefaultAgent(threadPool);
		PingActor ping = new PingActor(ag);
		PongActor pong = new PongActor(ag);
		ping.setPong(pong);
		pong.setPing(ping);
		// --
		Runnable dummy = new Runnable() {
			public void run() {}
		};
		final int WARM_UP = 100;
		final int PERF_TEST = 250;
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
	}

	public class PingActor extends DefaultActor<String, Object> {
		private PongActor pong = null;
		public PingActor(Agent ag) {
			super(ag);
		}
		public void setPong(PongActor pong) {
			this.pong = pong;
		}
		@Override
		public Object execute(String req) {
			try {
				long val = counter.incrementAndGet();
				if (val < MAX_COUNT) {
					pong.send("ping");
				}
			} catch (MailboxException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public class PongActor extends DefaultActor<String, Object> {
		private PingActor ping = null;
		public PongActor(Agent ag) {
			super(ag);
		}
		public void setPing(PingActor ping) {
			this.ping = ping;
		}
		@Override
		public Object execute(String req) {
			try {
				ping.send("pong");
			} catch (MailboxException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}