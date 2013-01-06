package kumarshantanu.relay.impl;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Agent;
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

	public DefaultActor(Agent agent, AbstractMailbox<RequestType, CorrelatedMessage<RequestType>> mailbox,
			String actorName, ActorID parentActor) {
		super(agent, mailbox==null? new DefaultMailbox<RequestType>(): mailbox,
				new LocalPollConverter<RequestType>(), actorName, parentActor);
	}

	public DefaultActor(Agent agent) {
		this(agent, null, null, null);
	}

}
