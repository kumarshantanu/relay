package kumarshantanu.relay.impl.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

public interface JMSContext {

	public Session getSession();

	public void commit();

	public void rollback();

	public MessageProducer getProducer();

	public MessageConsumer getConsumer();

	public MessageConsumer getReplyToConsumer();

	public Destination getReplyToDestination();

	public void onException(JMSException e);

}
