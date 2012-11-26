package kumarshantanu.relay.impl;

import java.util.Formatter;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import kumarshantanu.relay.Callback;
import kumarshantanu.relay.Mailbox;

public class CallbackFuture<RequestType, ReturnType, MailboxType>
implements Callback<ReturnType>, Future<ReturnType> {

	public final Object LOCK = new Object();

	public final Callback<ReturnType> orig;
	public final Mailbox<MailboxType> mbox;
	public final MailboxType message;

	// You must synchronize upon LOCK to access the following variable values

	public volatile boolean cancelled = false;  // NOT related to isCancelled()
	public volatile boolean done = false;       // is related to isDone()
	public volatile ReturnType value = null;
	public volatile Exception exception = null; // is related to isCancelled()

	public CallbackFuture(Callback<ReturnType> orig,
			Mailbox<MailboxType> mbox, RequestType request,
			MsgAdapter<RequestType, ReturnType, MailboxType> adapter) {
		this.orig = orig;
		this.mbox = mbox;
		this.message = adapter.convert(request, this);
	}

	public boolean cancelMessage() {
		synchronized (LOCK) {
			return cancelled || setCancelled(mbox.cancel(message));
		}
	}

	public boolean setCancelled(boolean v) {
		synchronized (LOCK) {
			cancelled = v;
		}
		return v;
	}

	// ----- Callback methods -----

	public void onReturn(ReturnType value) {
		synchronized (LOCK) {
			done = true;
			this.value = value;
		}
		orig.onReturn(value);
	}

	public void onException(Exception ex) {
		synchronized (LOCK) {
			done = true;
			exception = ex;
		}
		orig.onException(ex);
	}

	// ----- Future methods -----

	public boolean cancel(boolean mayInterruptIfRunning) {
		synchronized (LOCK) {
			if (done) {
				return false;
			}
		}
		return cancelMessage();
	}

	public boolean isCancelled() {
		synchronized (LOCK) {
			return cancelled;
		}
	}

	public boolean isDone() {
		synchronized (LOCK) {
			return done;
		}
	}

	public ReturnType get() throws InterruptedException, ExecutionException {
		try {
			return get(0, null);
		} catch (TimeoutException e) {
			throw new IllegalStateException(
					"This exception should have never been thrown", e);
		}
	}

	public ReturnType get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		final long start = System.nanoTime();
		while (true) {
			// cancelled?
			boolean can = false;
			synchronized (LOCK) {
				can = cancelled;
			}
			if (can) {
				throw new CancellationException();
			}
			// exception?
			Exception ex = null;
			synchronized (LOCK) {
				ex = exception;
			}
			if (ex != null) {
				throw new ExecutionException(ex);
			}
			// done?
			boolean dn = false;
			ReturnType v = null;
			synchronized (LOCK) {
				dn = done;
				v = value;
			}
			if (dn) {
				return v;
			}
			// sleep
			Thread.sleep(200);
			// timeout?
			if (unit != null) {
				if (System.nanoTime() - start >= unit.toNanos(timeout)) {
					Formatter fmt = new Formatter();
					String msg = fmt.format("Timeout after %d %s",
							timeout, unit.toString()).toString();
					fmt.close();
					throw new TimeoutException(msg);
				}
			}
		}
	}

}
