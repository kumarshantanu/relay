package kumarshantanu.relay.impl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.CorrelatedMessage;

/**
 * Default unbounded mailbox. Note that this implementation stores object
 * references; hence it is not space efficient for storing primitives.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <RequestType>
 */
public class DefaultMailbox<RequestType> extends AbstractMailbox<RequestType> {
	
	protected final Semaphore sem = new Semaphore(1);

	protected final Queue<CorrelatedMessage<RequestType>> queue;


	public DefaultMailbox(Queue<CorrelatedMessage<RequestType>> queue) {
		this.queue = queue;
	}

	public DefaultMailbox() {
		this(new LinkedList<CorrelatedMessage<RequestType>>());
	}

	public boolean isEmpty() {
		sem.acquireUninterruptibly();
		try {
			return queue.isEmpty();
		} finally {
			sem.release();
		}
	}
	
	public void add(RequestType message, ActorID actorID, String correlationID) {
		sem.acquireUninterruptibly();
		try {
			CorrelatedMessage<RequestType> element =
					new CorrelatedMessage<RequestType>(message, correlationID);
			queue.add(element);
		} finally {
			sem.release();
		}
	}
	
	public CorrelatedMessage<RequestType> poll() {
		sem.acquireUninterruptibly();
		try {
			return queue.poll();
		} finally {
			sem.release();
		}
	}

	public boolean cancel(RequestType message, ActorID actorID) {
		return false;
	}

}
