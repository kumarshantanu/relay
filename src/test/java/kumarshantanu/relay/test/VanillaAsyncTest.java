package kumarshantanu.relay.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

public class VanillaAsyncTest {

	@Test
	public void test() {
		final AtomicLong counter = new AtomicLong();
		assertTrue("Test started", true);
		final ExecutorService threadPool = Executors.newFixedThreadPool(8);
		Runnable sender = new Runnable() {
			public void run() {
				threadPool.execute(new Runnable() {
					public void run() {
						counter.incrementAndGet();
					}
				});
			}
		};
		Helper h = new Helper();
		h.doTimes(1000000, counter, sender, "Warm up");
		h.doTimes(2000000, counter, sender, "Perf test");
		assertTrue("Test finished", true);
	}

}
