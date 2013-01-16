package kumarshantanu.relay.impl;

import java.util.ArrayList;
import java.util.List;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.Worker;

public class BatchActor<RequestType> extends AbstractActor<RequestType> {

	public final Worker<List<RequestType>> worker;
	public final BatchBuffer<RequestType> batchBuffer = new BatchBuffer<RequestType>();
	public final int maxBatchSize;
	public final long flushMillis;

	public BatchActor(Worker<List<RequestType>> worker,
			int maxBatchSize, long flushMillis) {
		super(null, null);
		this.maxBatchSize = maxBatchSize;
		this.flushMillis = flushMillis;
		Util.notNull(worker, "worker");
		this.worker = worker;
	}

	public BatchActor(Worker<List<RequestType>> worker) {
		this(worker, 50, 1000);
	}

	public void act(RequestType req) {
		throw new IllegalStateException("BatchActor.execute() should never be called");
	}

	// ----- internal stuff -----

	private class Job implements Runnable {
		public final List<RequestType> messages;
		public final ActorID actorID;
		public Job(List<RequestType> messages, ActorID actorID) {
			this.messages = messages;
			this.actorID = actorID;
		}
		public void run() {
			CURRENT_ACTOR_ID.set(actorID);
			tvcKeeper.incrementBy(messages.size());
			try {
				worker.act(messages);
			} catch(Throwable err) {
				onFailure(err);
			}
		}
	}

	// ----- Actor methods -----

	public Runnable poll(ActorID actorID) {
		if (batchBuffer.size() < maxBatchSize &&
				batchBuffer.flushedAt + flushMillis > System.currentTimeMillis()) {
			return null;
		}
		List<RequestType> messages = batchBuffer.remove(maxBatchSize);
		if (messages.isEmpty()) {
			return null;
		}
		List<RequestType> onlyMessages = new ArrayList<RequestType>(messages.size());
		for (RequestType each: messages) {
			onlyMessages.add(each);
		}
		return new Job(onlyMessages, actorID);
	}

	public void send(RequestType message) throws MailboxException {
		batchBuffer.add(message);
	}

}
