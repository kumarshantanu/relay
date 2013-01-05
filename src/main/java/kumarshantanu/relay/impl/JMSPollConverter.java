package kumarshantanu.relay.impl;

import javax.jms.JMSException;
import javax.jms.Message;

public class JMSPollConverter<RequestType> implements PollConverter<RequestType, Message> {

	public final JMSContext context;
	public final JMSMessageSerializer<RequestType> serde;

	public JMSPollConverter(JMSContext context, JMSMessageSerializer<RequestType> serde) {
		Util.notNull(context, "context");
		Util.notNull(serde, "serde");
		this.context = context;
		this.serde = serde;
	}

	public RequestType getMessage(Message encoded) {
		try {
			return serde.deserialize(encoded);
		} catch (JMSException e) {
			context.onException(e);
			throw new RuntimeException(e);
		}
	}

	public String getCorrelationID(Message encoded) {
		try {
			return encoded.getJMSCorrelationID();
		} catch (JMSException e) {
			context.onException(e);
			throw new RuntimeException(e);
		}
	}

}
