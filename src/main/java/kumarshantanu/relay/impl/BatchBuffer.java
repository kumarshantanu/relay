package kumarshantanu.relay.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Mailbox;

public class BatchBuffer<T> implements Mailbox<T> {

	private final List<T> buffer = new LinkedList<T>();
	public volatile long flushedAt = System.currentTimeMillis();

	public synchronized List<T> remove() {
		return remove(Integer.MAX_VALUE);
	}

	public synchronized List<T> remove(int max) {
		int size = Math.min(size(), max);
		List<T> result = new ArrayList<T>();
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

	public void add(T element, ActorID actorID, boolean twoWay) {
		buffer.add(element);
	}

	public boolean isEmpty() {
		return buffer.isEmpty();
	}

	public synchronized T poll() {
		if (!buffer.isEmpty()) {
			return buffer.remove(0);
		}
		return null;
	}

	public boolean cancel(T message, ActorID actorID) {
		return false;
	}

}
