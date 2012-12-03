package kumarshantanu.relay.impl;

import javax.jms.JMSException;
import javax.jms.Message;

public interface JMSMessageSerializer<RequestType> {

	public RequestType deserialize(Message format) throws JMSException;

	public Message serialize(RequestType message) throws JMSException;

}