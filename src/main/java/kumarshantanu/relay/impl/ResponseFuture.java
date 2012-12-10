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

	public void finalizeDone(ReturnType value) {
		this.done = true;
		this.value = value;
	}

	public void finalizeCancel() {
		this.done = true;
		this.cancelled = true;
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
			Util.sleep(200);
		}
		return value;
	}

	public ReturnType get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		long start = System.nanoTime();
		while (!done) {
			Util.sleep(Math.min(200, unit.toMillis(timeout)));
			if (unit.toNanos(timeout) >= (System.nanoTime() - start)) break;
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
