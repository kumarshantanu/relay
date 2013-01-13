package kumarshantanu.relay.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.CorrelatedMessage;
import kumarshantanu.relay.Mailbox;

public class BatchBuffer<RequestType> implements Mailbox<RequestType, CorrelatedMessage<RequestType>> {

	private final List<CorrelatedMessage<RequestType>> buffer =
			new LinkedList<CorrelatedMessage<RequestType>>();

	public volatile long flushedAt = System.currentTimeMillis();

	public synchronized List<CorrelatedMessage<RequestType>> remove() {
		return remove(Integer.MAX_VALUE);
	}

	public synchronized List<CorrelatedMessage<RequestType>> remove(int max) {
		int size = Math.min(size(), max);
		List<CorrelatedMessage<RequestType>> result = new ArrayList<CorrelatedMessage<RequestType>>();
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

	public void add(RequestType element, ActorID actorID, String correlationID) {
		buffer.add(new CorrelatedMessage<RequestType>(element, correlationID));
	}

	public synchronized CorrelatedMessage<RequestType> poll() {
		if (!buffer.isEmpty()) {
			return buffer.remove(0);
		}
		return null;
	}

	public boolean cancel(RequestType message, ActorID actorID) {
		return false;
	}

}
