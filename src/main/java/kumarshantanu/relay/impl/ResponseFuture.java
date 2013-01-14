package kumarshantanu.relay.impl;

import java.util.Formatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResponseFuture<ReturnType> implements Future<ReturnType> {

	public volatile boolean done = false;
	public volatile boolean cancelled = false;
	public volatile ReturnType value = null;
	public volatile Throwable error = null;

	public void finalizeDone(ReturnType value) {
		this.done = true;
		this.value = value;
	}

	public void finalizeCancel(Throwable err) {
		this.done = true;
		this.cancelled = true;
		this.error = err;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;  // cannot cancel, so return false
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isDone() {
		return done;
	}

	public ReturnType get() throws InterruptedException, ExecutionException {
		while (!done) {
			Thread.sleep(200); // may throw InterruptedException
		}
		if (cancelled) {
			throw new ExecutionException(error);
		}
		return value;
	}

	public ReturnType get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		long start = System.nanoTime();
		while (!done) {
			Thread.sleep(Math.min(200, unit.toMillis(timeout))); // may throw InterruptedException
			if (unit.toNanos(timeout) >= (System.nanoTime() - start)) break;
		}
		if (cancelled) {
			throw new ExecutionException(error);
		}
		if (done) {
			return value;
		}
		Formatter fmt = new Formatter();
		String msg = fmt.format("Timeout after %d %s", timeout, unit.toString()).toString();
		fmt.close();
		throw new TimeoutException(msg);
	}

}
