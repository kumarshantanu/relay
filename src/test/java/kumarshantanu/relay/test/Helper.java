package kumarshantanu.relay.test;

import java.util.concurrent.atomic.AtomicLong;

public class Helper {
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void statDump(long start, String msg, long n) {
		long stop = System.currentTimeMillis();
		long dur = stop - start;
		System.out.println("" + msg + " done at " + stop + "ms, dur="
		+ dur + "ms, count=" + n + ", avg/ms=" + (n / dur));
	}

	public void doTimes(int n, AtomicLong counter, Runnable sender, String phase) {
		counter.set(0);
		long start = System.currentTimeMillis();
		System.out.println("" + phase + " starting at " + start + "ms");
		for (int i = 0; i < n; i++) {
			sender.run();
		}
		statDump(start, "Sending messages", n);
		while (true) {
			long c = counter.get();
			if (c >= n) break;
			System.out.println("" + c + " < " + n);
			if (n - c > 100000) {
				sleep(1000);
			} else {
				sleep(100);
			}
		}
		statDump(start, phase, n);
	}

}
