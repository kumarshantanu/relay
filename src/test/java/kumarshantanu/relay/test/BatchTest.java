package kumarshantanu.relay.test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import kumarshantanu.relay.Callback;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.impl.BatchActor;
import kumarshantanu.relay.impl.DefaultAgent;
import kumarshantanu.relay.impl.Util;

import org.junit.Assert;
import org.junit.Test;

public class BatchTest {

	private class BatchProcessor implements Callback<List<String>> {
		public final AtomicInteger count;
		public BatchProcessor(AtomicInteger count) {
			this.count = count;
		}
		public void onReturn(List<String> value) {
			count.addAndGet(value.size());
		}
		public void onException(Exception ex) {}
	}

	private void sendMessages(BatchActor<String> actor, int howMany) {
		for (int i = 0; i < howMany; i++) {
			try { actor.send("hello" + i); } catch (MailboxException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void test() {
		Assert.assertTrue("Test started", true);
		ExecutorService threadPool = Util.newThreadPool();
		DefaultAgent ag = new DefaultAgent(threadPool);
		final AtomicInteger count = new AtomicInteger(0);
		BatchProcessor batchProcessor = new BatchProcessor(count);
		BatchActor<String> actor = new BatchActor<String>(ag, batchProcessor, 5, 500);
		threadPool.execute(ag);
		sendMessages(actor, 5);
		Util.sleep(200);
		Assert.assertEquals(5, batchProcessor.count.get());
		sendMessages(actor, 3);
		Util.sleep(400);
		Assert.assertEquals(5, batchProcessor.count.get());
		Util.sleep(200);
		Assert.assertEquals(8, batchProcessor.count.get());
	}

}
