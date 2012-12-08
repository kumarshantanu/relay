package kumarshantanu.relay.impl;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

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
	public final Destination replyTo;

	public JMSMailbox(MessageProducer producer, MessageConsumer consumer,
			JMSMessageSerializer<RequestType> serde, Destination replyTo) {
		this.producer = producer;
		this.consumer = consumer;
		this.serde = serde;
		this.replyTo = replyTo;
	}

	// factory method
	public static <T> JMSMailbox<T> create(Session session, String queueName,
			JMSMessageSerializer<T> serde) throws JMSException {
		Destination destination = session.createQueue(queueName);
		MessageProducer producer = session.createProducer(destination);
		MessageConsumer consumer = session.createConsumer(destination);
		Destination replyTo = session.createTemporaryQueue();
		return new JMSMailbox<T>(producer, consumer, serde, replyTo);
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
