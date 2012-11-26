package kumarshantanu.relay.batch;

import java.util.ArrayList;
import java.util.List;

import kumarshantanu.relay.Callback;
import kumarshantanu.relay.impl.DummyCallback;
import kumarshantanu.relay.impl.Util;

public class BatchingCallback<ReturnType> implements Callback<ReturnType> {

	public final Callback<ReturnType> orig;
	public final int batchFactor;
	public final List<ReturnType> buffer;
	public final BatchHandler<ReturnType> batchHandler;
	public final long flushIntervalMillis;
	
	public final Object FLUSH_LOCK = new Object();
	public final Thread flushThread = new Thread() {
		public void run() {
			while (!quitFlushThread) {
				if (lastFlushedAt + flushIntervalMillis > System.currentTimeMillis()) {
					flush();
				} else {
					Util.sleep(flushIntervalMillis);
				}
			}
		};
	};
	
	private volatile boolean quitFlushThread = false;
	private volatile long lastFlushedAt = System.currentTimeMillis();

	protected void finalize() throws Throwable {
		quitFlushThread = true;
	};
	
	public BatchingCallback(Callback<ReturnType> orig,
			BatchHandler<ReturnType> batchHandler,
			int bufSize, long flushIntervalMillis) {
		this.orig = orig;
		this.batchFactor = bufSize;
		this.buffer = new ArrayList<ReturnType>(bufSize);
		this.batchHandler = batchHandler;
		this.flushIntervalMillis = flushIntervalMillis;
		flushThread.start();
	}

	public BatchingCallback(Callback<ReturnType> orig,
			BatchHandler<ReturnType> batchHandler) {
		this(orig, batchHandler, 50, 1000);
	}

	public BatchingCallback(BatchHandler<ReturnType> batchHandler) {
		this(new DummyCallback<ReturnType>(), batchHandler);
	}

	public void flush() {
		List<ReturnType> data = null;
		synchronized (this) {
			data = new ArrayList<ReturnType>(buffer);
			buffer.clear();
		}
		synchronized (FLUSH_LOCK) {
			if (data.size() > 0) {
				try {
					batchHandler.handle(data);
					try {
						orig.onReturn(null);
					} catch(Exception e) { /* ignore */ }
				} catch(Exception ex) {
					try {
						orig.onException(ex);
					} catch(Exception e) { /* ignore */ }
				}
				lastFlushedAt = System.currentTimeMillis();
			}
		}
	}

	public synchronized void onReturn(ReturnType value) {
		buffer.add(value);
		if (buffer.size() >= batchFactor) {
			flush();
		}
	}

	public synchronized void onException(Exception ex) {
		flush();
		try {
			orig.onException(ex);
		} catch(Exception e) { /* ignore */ }
	}

}
