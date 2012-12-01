package kumarshantanu.relay.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import kumarshantanu.relay.Callback;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.impl.AmbientActor;
import kumarshantanu.relay.impl.DefaultAgent;
import kumarshantanu.relay.impl.Util;

import org.junit.Assert;
import org.junit.Test;

public class AmbienceTest {

	@Test
	public void test() {
		Assert.assertTrue("Test started", true);
		ExecutorService threadPool = Util.newThreadPool();
		DefaultAgent ag = new DefaultAgent(threadPool);
		AmbientActor<String, String> ac = new AmbientActor<String, String>(ag) {
			@Override
			public String execute(String req) {
				return req;
			}
		};
		Future<String> ret = null;
		threadPool.execute(ag);
		// use of default callback
		try { ret = ac.send("foo", true); } catch (MailboxException e) { e.printStackTrace(); }
		testResult(ret, "foo");
		// use of custom callback
		final AtomicBoolean state = new AtomicBoolean(false);
		Callback<String> myCallback = new Callback<String>() {
			public void onReturn(String value) { state.set(true); }
			public void onException(Exception ex) {}
		};
		try { ret = ac.send("bar", myCallback, true); } catch (MailboxException e) { e.printStackTrace(); }
		testResult(ret, "bar");
		Assert.assertTrue("custom callback should be called", state.get());
	}

	private void testResult(Future<String> ret, String expected) {
		Util.sleep(500);
		Assert.assertTrue("message should be processed", ret.isDone());
		try {
			Assert.assertEquals(expected, ret.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (ExecutionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
