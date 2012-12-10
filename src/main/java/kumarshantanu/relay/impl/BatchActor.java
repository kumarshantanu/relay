package kumarshantanu.relay.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.Callback;
import kumarshantanu.relay.CorrelatedMessage;
import kumarshantanu.relay.MailboxException;

public final class BatchActor<RequestType> extends
		AbstractActor<RequestType, RequestType> {

	public final Callback<List<RequestType>> callback;
	public final BatchBuffer<RequestType> batchBuffer = new BatchBuffer<RequestType>();
	public final int maxBatchSize;
	public final long flushMillis;

	public BatchActor(Agent agent, Callback<List<RequestType>> callback,
			int maxBatchSize, long flushMillis) {
		super(null, null);
		this.maxBatchSize = maxBatchSize;
		this.flushMillis = flushMillis;
		Util.notNull(agent, "agent");
		Util.notNull(callback, "callback");
		this.callback = callback;
		agent.register(this);
	}

	public BatchActor(Agent ag, Callback<List<RequestType>> callback) {
		this(ag, callback, 50, 1000);
	}

	@Override
	public RequestType execute(RequestType req) {
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
			try {
				tvcKeeper.incrementBy(messages.size());
				callback.onReturn(messages);
			} catch (Exception e) {
				try {
					callback.onException(e);
				} catch (Exception f) {
					// ignore callback exceptions
				}
			}
		}
	}

	// ----- Actor methods -----

	public boolean isMailboxEmpty() {
		return batchBuffer.isEmpty();
	}

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
