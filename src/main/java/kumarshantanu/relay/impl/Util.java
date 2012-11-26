package kumarshantanu.relay.impl;

import java.util.Queue;
import java.util.UUID;

import kumarshantanu.relay.Callback;
import kumarshantanu.relay.Mailbox;
import kumarshantanu.relay.MailboxException;

public class Util {
	
	public static final String JVM_ID = UUID.randomUUID().toString();
	
	
	public static <T> void notNull(T x, String name) {
		if (x == null) {
			throw new IllegalArgumentException("Expected "
					+ name + " but found null");
		}
	}

	public static void positiveInteger(int x) {
		if (x <= 0) throw new IllegalArgumentException(
				"Expected positive integer but found " + x);
	}

	public static void positiveLong(long x) {
		if (x <= 0) throw new IllegalArgumentException(
				"Expected positive long but found " + x);
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static <T> Mailbox<T> createMailbox(final Queue<T> queue) {
		notNull(queue, "queue");
		return new Mailbox<T>() {
			public boolean isEmpty() {
				return queue.isEmpty();
			}
			public void add(T message) throws MailboxException {
				try {
					queue.add(message);
				} catch (RuntimeException e) {
					throw new MailboxException(this, e);
				}
			}
			public T poll() {
				return queue.poll();
			}
			public boolean cancel(T message) {
				return false;
			};
		};
	}

	public static <T> Callback<T> ignoreResult(Class<T> clazz) {
		return new Callback<T>() {
			public void onReturn(T value) { /* ignore */ };
			public void onException(Exception ex) { /* ignore */ }
		};
	}

}
