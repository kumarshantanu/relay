package kumarshantanu.relay.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import kumarshantanu.relay.impl.DefaultAgent;
import kumarshantanu.relay.impl.PollingActor;
import kumarshantanu.relay.impl.Util;

import org.junit.Assert;
import org.junit.Test;

public class PollingTest {

	@Test
	public void test() {
		Assert.assertTrue("Test started", true);
		ExecutorService threadPool = Util.newThreadPool();
		DefaultAgent ag = new DefaultAgent(threadPool);
		final AtomicBoolean pollState = new AtomicBoolean(false);
		final AtomicBoolean executeRan = new AtomicBoolean(false);
		PollingActor<String> actor = new PollingActor<String>(ag) {
			@Override
			public boolean poll() {
				return pollState.get();
			}
			@Override
			public String execute(Object req) {
				executeRan.set(true);
				return "foo";
			}
		};
		threadPool.execute(ag);
		Assert.assertFalse("poll() should return false", actor.poll());
		Assert.assertFalse("executeRan should return false", executeRan.get());
		pollState.set(true);
		Util.sleep(500);
		Assert.assertTrue("poll() should return true", actor.poll());
		Assert.assertTrue("executeRan should return true", executeRan.get());
		pollState.set(false);
	}

}
