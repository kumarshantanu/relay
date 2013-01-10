package kumarshantanu.relay.impl.jms;

import java.util.Map;
import java.util.concurrent.Future;

import javax.jms.JMSException;
import javax.jms.Message;

import kumarshantanu.relay.ActorID;
import kumarshantanu.relay.Agent;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.impl.AbstractActor;
import kumarshantanu.relay.impl.ResponseFuture;
import kumarshantanu.relay.impl.Util;

/**
 * See: http://activemq.apache.org/how-should-i-implement-request-response-with-jms.html
 * @author Shantanu Kumar
 *
 * @param <ReturnType>
 */
public abstract class JMSResponseUpdater<ReturnType> extends AbstractActor<Message, ReturnType> {

	public final JMSContext context;

	public JMSResponseUpdater(Agent agent, JMSContext context,
			Map<String, ResponseFuture<ReturnType>> futures) {
		super(null, null, futures);
		Util.notNull(agent, "agent");
		Util.notNull(futures, "futures");
		Util.notNull(context, "context");
		this.context = context;
		agent.register(this);
	}

	public boolean isMailboxEmpty() {
		return true; // always return true because this actor has no mailbox
	}

	public Runnable poll(ActorID actorID) {
		final Message message;
		try {
			message = context.getReplyToConsumer().receiveNoWait();
		} catch (JMSException e) {
			context.onException(e);
			return null;
		}
		if (message != null) {
			return new Runnable() {
				public void run() {
					try {
						String corID = message.getJMSCorrelationID();
						try {
							ReturnType val = act(message);
							correlateSuccess(corID, val);
							context.commit();
						} catch(Throwable err) {
							correlateFailure(corID, err);
							context.rollback();
						}
					} catch (JMSException e) {
						context.rollback();
						context.onException(e);
					}
				}
			};
		}
		return null;
	}

	public void send(Message message) throws MailboxException {
		throw new UnsupportedOperationException("send is not supported on this actor type");
	};

	public Future<ReturnType> send(Message message, boolean returnFuture)
			throws MailboxException {
		throw new UnsupportedOperationException("send is not supported on this actor type");
	}

}
