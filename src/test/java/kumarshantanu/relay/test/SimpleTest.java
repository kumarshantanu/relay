package kumarshantanu.relay.test;

import java.util.concurrent.atomic.AtomicLong;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.Callback;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.impl.AmbientActor;
import kumarshantanu.relay.impl.DefaultActor;
import kumarshantanu.relay.impl.DefaultAgent;
import kumarshantanu.relay.monitoring.ThroughputAware;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {
	
	private interface ActorFactory {
		public Actor<String, String> create(Agent ag, Callback<String> callback);
	}
	
	@Test
	public void ambientActorTest() {
		test(new ActorFactory() {
			public Actor<String, String> create(Agent ag,
					Callback<String> callback) {
				return new AmbientActor<String, String>(ag, callback, null, null, null) {
					@Override
					public String execute(String req) {
						return req;
					}
				};
			}
		});
	}
	
	@Test
	public void defaultActorTest() {
		test(new ActorFactory() {
			public Actor<String, String> create(Agent ag,
					Callback<String> callback) {
				return new DefaultActor<String, String>(ag, callback, null, null, null) {
					@Override
					public String execute(String req) {
						return req;
					}
				};
			}
		});
	}
	
	public void test(ActorFactory afactory) {
		Assert.assertTrue("Test started", true);
		final AtomicLong counter = new AtomicLong();
		DefaultAgent ag = new DefaultAgent(8);
		final Callback<String> callback = new Callback<String>() {
			public void onReturn(String value) {
				counter.incrementAndGet();
			}
			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};
		final Actor<String, String> ac = afactory.create(ag, callback);
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
		new Thread(ag).start();
		System.out.println(Actor.CURRENT_ACTOR_ID.get());
		h.doTimes(1000000, counter, sender, "Warm up");
		System.out.println(((ThroughputAware) ac).getThroughputString());
		h.doTimes(2500000, counter, sender, "Perf test");
		System.out.println(((ThroughputAware) ac).getThroughputString());
		Assert.assertTrue("Test finished", true);
	}
	
}
