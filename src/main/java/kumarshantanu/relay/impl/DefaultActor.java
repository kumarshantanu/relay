package kumarshantanu.relay.impl;

import kumarshantanu.relay.ActorID;

/**
 * DefaultActor is an actor with LocalMailbox.
 * @author Shantanu Kumar (kumar.shantanu@gmail.com)
 *
 * @param <RequestType>
 * @param <ReturnType>
 */
public abstract class DefaultActor<RequestType> extends GenericActor<RequestType, RequestType> {

	public DefaultActor(AbstractMailbox<RequestType, RequestType> mailbox,
			String actorName, ActorID parentActor) {
		super(mailbox==null? new DefaultMailbox<RequestType>(): mailbox,
				new LocalPollConverter<RequestType>(), actorName, parentActor);
	}

	public DefaultActor() {
		this(null, null, null);
	}

}
