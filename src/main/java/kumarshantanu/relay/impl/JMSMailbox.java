package kumarshantanu.relay.impl;

import javax.jms.JMSException;
import javax.jms.Message;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.MailboxException;

/**
 * A JMS queue based Mailbox implementation.
 * @author Shantanu Kumar
 *
 * @param <RequestType>
 */
public class JMSMailbox<RequestType> extends AbstractMailbox<RequestType, Message> {

	public final JMSContext context;
	public final JMSMessageSerializer<RequestType> serde;

	public JMSMailbox(JMSContext context, JMSMessageSerializer<RequestType> serde) {
		Util.notNull(context, "context");
		Util.notNull(serde, "serde");
		this.context = context;
		this.serde = serde;
	}

	// ----- Mailbox methods -----

	public void add(RequestType message, ActorID actorID, String correlationID) throws MailboxException {
		try {
			Message msg = serde.serialize(message);
			if (correlationID != null) {
				msg.setJMSReplyTo(context.getReplyToDestination());
				msg.setJMSCorrelationID(correlationID);
			}
			context.getProducer().send(msg);
		} catch (JMSException e) {
			context.onException(e);
			throw new MailboxException(this, e);
		}
	}

	public boolean cancel(RequestType message, ActorID actorID) {
		throw new MailboxException(this, "cancel is not supported on JmsMailbox");
	}

	/**
	 * Cannot determine if JMS queue is empty or not, so always return false;
	 */
	public boolean isEmpty() {
		return false;
	}

	public Message poll() throws MailboxException {
		try {
			return context.getConsumer().receiveNoWait();
		} catch (JMSException e) {
			context.onException(e);
			throw new MailboxException(this, e);
		}
	}

}
