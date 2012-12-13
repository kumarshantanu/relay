package kumarshantanu.relay.impl;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;

public interface JMSContext {

	public MessageProducer getProducer();

	public MessageConsumer getConsumer();

	public MessageConsumer getReplyToConsumer();

	public Destination getReplyToDestination();

	public void onException(JMSException e);

}
