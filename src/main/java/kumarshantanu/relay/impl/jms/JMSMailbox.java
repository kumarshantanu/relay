package kumarshantanu.relay.impl.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.impl.AbstractMailbox;
import kumarshantanu.relay.impl.Util;

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
		Util.assertNotNull(context, "context");
		Util.assertNotNull(serde, "serde");
		this.context = context;
		this.serde = serde;
	}

	protected Message preProcess(RequestType message, Message jmsMessage) {
		return jmsMessage;
	}

	// ----- Mailbox methods -----

	public void add(RequestType message) throws MailboxException {
		try {
			Message msg = preProcess(message, serde.serialize(message));
			context.getProducer().send(msg);
			context.commit();
		} catch (JMSException e) {
			context.rollback();
			context.onException(e);
			throw new MailboxException(this, e);
		}
	}

	public boolean cancel(RequestType message) {
		throw new MailboxException(this, "cancel is not supported on this mailbox");
	}

	public Message poll() throws MailboxException {
		try {
			return context.getConsumer().receiveNoWait();
		} catch (JMSException e) {
			context.rollback();
			context.onException(e);
			throw new MailboxException(this, e);
		}
	}

}
