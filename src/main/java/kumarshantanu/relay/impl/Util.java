package kumarshantanu.relay.impl;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kumarshantanu.relay.Mailbox;
import kumarshantanu.relay.MailboxException;

public class Util {
	
	public static final String JVM_ID = UUID.randomUUID().toString();
	
	
	public static <T> void assertNotNull(T x, String name) {
		if (x == null) {
			throw new IllegalArgumentException("Expected "
					+ name + " but found null");
		}
	}

	// optimum for CPU-bound jobs only
	public static int optimumThreadCount() {
		return Runtime.getRuntime().availableProcessors() * 2 + 1;
	}

	public static ExecutorService newThreadPool() {
		return newThreadPool(optimumThreadCount());
	}
	public static ExecutorService newThreadPool(int threadCount) {
		return threadCount > 0? Executors.newFixedThreadPool(threadCount):
			Executors.newCachedThreadPool();
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static <RequestType, ReturnType>
	Mailbox<RequestType, RequestType> createMailbox(final Queue<RequestType> queue) {
		assertNotNull(queue, "queue");
		return new Mailbox<RequestType, RequestType>() {
			public void add(RequestType message) throws MailboxException {
				try {
					queue.add(message);
				} catch (RuntimeException e) {
					throw new MailboxException(this, e);
				}
			}
			public RequestType poll() {
				return queue.poll();
			}
			public boolean cancel(RequestType message) {
				return false;
			}
		};
	}

}
