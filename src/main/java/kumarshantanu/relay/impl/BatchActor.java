package kumarshantanu.relay.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.CorrelatedMessage;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.Worker;

public final class BatchActor<RequestType> extends
		AbstractActor<RequestType, RequestType> {

	public final Worker<List<RequestType>, ?> worker;
	public final BatchBuffer<RequestType> batchBuffer = new BatchBuffer<RequestType>();
	public final int maxBatchSize;
	public final long flushMillis;

	public BatchActor(Agent agent, Worker<List<RequestType>, ?> worker,
			int maxBatchSize, long flushMillis) {
		super(null, null);
		this.maxBatchSize = maxBatchSize;
		this.flushMillis = flushMillis;
		Util.notNull(agent, "agent");
		Util.notNull(worker, "worker");
		this.worker = worker;
		agent.register(this);
	}

	public BatchActor(Agent ag, Worker<List<RequestType>, ?> worker) {
		this(ag, worker, 50, 1000);
	}

	@Override
	public RequestType act(RequestType req) {
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
			worker.act(messages);
		}
	}

	// ----- Actor methods -----

	public Runnable poll(ActorID actorID) {
		if (batchBuffer.size() < maxBatchSize &&
				batchBuffer.flushedAt + flushMillis > System.currentTimeMillis()) {
			return null;
		}
		List<CorrelatedMessage<RequestType>> messages = batchBuffer.remove(maxBatchSize);
		if (messages.isEmpty()) {
			return null;
		}
		List<RequestType> onlyMessages = new ArrayList<RequestType>(messages.size());
		for (CorrelatedMessage<RequestType> each: messages) {
			onlyMessages.add(each.message);
		}
		return new Job(onlyMessages, actorID);
	}

	public void send(RequestType message) throws MailboxException {
		send(message, false);
	}

	public Future<RequestType> send(RequestType message, boolean returnFuture)
			throws MailboxException {
		if (returnFuture == false) {
			batchBuffer.add(message, currentActorID, null);
			return null;
		}
		throw new UnsupportedOperationException(
				"'send' with returnFuture=true is not supported on this actor");
	}

}
