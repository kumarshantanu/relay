package kumarshantanu.relay.impl;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.CorrelatedMessage;

/**
 * DefaultActor is an actor with LocalMailbox.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <RequestType>
 * @param <ReturnType>
 */
public abstract class DefaultActor<RequestType, ReturnType>
extends GenericActor<RequestType, CorrelatedMessage<RequestType>, ReturnType> {

	public DefaultActor(AbstractMailbox<RequestType, CorrelatedMessage<RequestType>> mailbox,
			String actorName, ActorID parentActor) {
		super(mailbox==null? new DefaultMailbox<RequestType>(): mailbox,
				new LocalPollConverter<RequestType>(), actorName, parentActor);
	}

	public DefaultActor() {
		this(null, null, null);
	}

	@Override
	protected void onSuccess(CorrelatedMessage<RequestType> poll, ReturnType val) {
		correlateSuccess(poll.correlationID, val);
	}

	@Override
	protected void onFailure(CorrelatedMessage<RequestType>poll , Throwable err) {
		correlateFailure(poll.correlationID, err);
	}

}
