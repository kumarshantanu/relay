package kumarshantanu.relay.test;

import java.util.concurrent.atomic.AtomicLong;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.Callback;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.impl.AmbientActor;
import kumarshantanu.relay.impl.DefaultAgent;
import kumarshantanu.relay.monitoring.ThroughputAware;

import org.junit.Assert;
import org.junit.Test;

public class RelayAsyncTest {
	
	@Test
	public void test() {
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
		final Actor<String, String> ac =
				new AmbientActor<String, String>(ag, callback, null, null, null) {
			@Override
			public String execute(String req) {
				return req;
			}
		};
		Runnable sender = new Runnable() {
			public void run() {
				try {
					ac.send("Hello World!", callback);
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
