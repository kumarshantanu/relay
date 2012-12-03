package kumarshantanu.relay.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;

import kumarshantanu.relay.Mailbox;
import kumarshantanu.relay.MailboxException;

/**
 * A JMS queue based Mailbox implementation.
 * @author shantanu
 *
 * @param <RequestType>
 */
public class JMSMailbox<RequestType> implements Mailbox<RequestType> {

	public final MessageProducer producer;
	public final MessageConsumer consumer;
	public final JMSMessageSerializer<RequestType> serde;

	public JMSMailbox(MessageProducer producer, MessageConsumer consumer,
			JMSMessageSerializer<RequestType> serde) {
		this.producer = producer;
		this.consumer = consumer;
		this.serde = serde;
	}

	// ----- Mailbox methods -----

	public void add(RequestType message) throws MailboxException {
		try {
			producer.send(serde.serialize(message));
		} catch (JMSException e) {
			throw new MailboxException(this, e);
		}
	}

	public boolean cancel(RequestType message) {
		throw new MailboxException(this, "cancel is not supported on JmsMailbox");
	}

	/**
	 * Cannot determine if JMS queue is empty or not, so always return false;
	 */
	public boolean isEmpty() {
		return false;
	}

	public RequestType poll() throws MailboxException {
		Message message = null;
		try {
			message = consumer.receiveNoWait();
		} catch (JMSException e) {
			throw new MailboxException(this, e);
		}
		if (message != null) {
			try {
				return serde.deserialize(message);
			} catch (JMSException e) {
				throw new MailboxException(this, e);
			}
		}
		return null;
	}

}
