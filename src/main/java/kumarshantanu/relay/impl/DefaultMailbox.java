package kumarshantanu.relay.impl;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Mailbox;

/**
 * Default unbounded mailbox. Note that this implementation stores object
 * references; hence it is not space efficient for storing primitives.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <RequestType>
 */
public class DefaultMailbox<RequestType> implements Mailbox<RequestType> {
	
	protected final LinkedList<RequestType> queue =
			new LinkedList<RequestType>();
	
	protected final Semaphore sem = new Semaphore(1);
	
	public boolean isEmpty() {
		sem.acquireUninterruptibly();
		try {
			return queue.isEmpty();
		} finally {
			sem.release();
		}
	}
	
	public void add(RequestType message, ActorID actorID) {
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

	public boolean cancel(RequestType message, ActorID actorID) {
		return false;
	}

}
