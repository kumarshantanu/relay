package kumarshantanu.relay.impl;

import java.util.concurrent.atomic.AtomicLong;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Mailbox;

public abstract class AbstractMailbox<RequestType> implements Mailbox<RequestType> {

	public static final AtomicLong INSTANCE_COUNTER = new AtomicLong(0L);

	public final long instanceIndex = INSTANCE_COUNTER.incrementAndGet();
	public final AtomicCounter MESSAGE_COUNTER = new AtomicCounter();

	public final String nextCorrelationID(ActorID actorID) {
		return actorID.toString() + '_' + instanceIndex + '_' +
				MESSAGE_COUNTER.incrementAndGet();
	}

}
