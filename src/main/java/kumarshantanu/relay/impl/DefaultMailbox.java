package kumarshantanu.relay.impl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Default unbounded local mailbox. Note that this implementation stores object
 * references; hence it is not space efficient for storing primitives.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <RequestType>
 */
public class DefaultMailbox<RequestType> extends AbstractMailbox<RequestType, RequestType> {
	
	protected final Semaphore sem = new Semaphore(1);

	protected final Queue<RequestType> queue;


	public DefaultMailbox(Queue<RequestType> queue) {
		this.queue = queue;
	}

	public DefaultMailbox() {
		this(new LinkedList<RequestType>());
	}

	public void add(RequestType message) {
		sem.acquireUninterruptibly();
		try {
			queue.add(message);
		} finally {
			sem.release();
		}
	}
	
	public RequestType poll() {
		sem.acquireUninterruptibly();
		try {
			return queue.poll();
		} finally {
			sem.release();
		}
	}

	public boolean cancel(RequestType message) {
		return false;
	}

}
