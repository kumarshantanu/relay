package kumarshantanu.relay.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kumarshantanu.relay.Mailbox;

public class BatchBuffer<RequestType> implements Mailbox<RequestType, RequestType> {

	private final List<RequestType> buffer = new LinkedList<RequestType>();

	public volatile long flushedAt = System.currentTimeMillis();

	public synchronized List<RequestType> remove() {
		return remove(Integer.MAX_VALUE);
	}

	public synchronized List<RequestType> remove(int max) {
		int size = Math.min(size(), max);
		List<RequestType> result = new ArrayList<RequestType>();
		for (int i = 0; i < size; i++) {
			result.add(buffer.remove(0));
		}
		flushedAt = System.currentTimeMillis();
		return result;
	}

	public synchronized int size() {
		return buffer.size();
	}

	// ----- Mailbox methods -----

	public void add(RequestType element) {
		buffer.add(element);
	}

	public synchronized RequestType poll() {
		if (!buffer.isEmpty()) {
			return buffer.remove(0);
		}
		return null;
	}

	public boolean cancel(RequestType message) {
		return false;
	}

}
