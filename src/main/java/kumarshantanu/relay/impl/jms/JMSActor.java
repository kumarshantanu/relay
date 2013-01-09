package kumarshantanu.relay.impl.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.impl.GenericActor;
import kumarshantanu.relay.impl.Util;

public abstract class JMSActor<RequestType, ReturnType>
extends GenericActor<RequestType, Message, ReturnType> {

	public final JMSContext context;
	public final JMSMessageSerializer<ReturnType> returnSerde;

	public JMSActor(Agent agent, JMSContext context,
			JMSMailbox<RequestType> mailbox,
			JMSPollConverter<RequestType> pollConverter,
			JMSMessageSerializer<ReturnType> returnSerde,
			String actorName, ActorID parentActor) {
		super(agent, mailbox, pollConverter, actorName, parentActor);
		Util.notNull(context, "context");
		Util.notNull(returnSerde, "returnSerde");
		this.context = context;
		this.returnSerde = returnSerde;
	}

	public JMSActor(Agent agent, JMSContext context,
			JMSMailbox<RequestType> mailbox,
			JMSPollConverter<RequestType> pollConverter,
			JMSMessageSerializer<ReturnType> returnSerde) {
		this(agent, context, mailbox, pollConverter, returnSerde, null, null);
	}

	@Override
	protected void onSuccess(Message poll, ReturnType val) {
		try {
			String corID = poll.getJMSCorrelationID();
			if (corID!=null) {
				Message response = returnSerde.serialize(val);
				response.setJMSCorrelationID(corID);
				Destination replyToDestination = poll.getJMSReplyTo();
				Session session = context.getSession();
				MessageProducer replyToMessageProducer =
						session.createProducer(replyToDestination);
				replyToMessageProducer.send(response);
				context.commit();
			}
		} catch(JMSException e) {
			context.rollback();
			context.onException(e);
		}
	}

	@Override
	protected void onFailure(Message poll, Throwable err) {
		onSuccess(poll, null);
	}

}
