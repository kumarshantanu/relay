package kumarshantanu.relay.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import kumarshantanu.relay.Actor;
import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.impl.DefaultAgent;
import kumarshantanu.relay.impl.GenericActor;
import kumarshantanu.relay.impl.JMSContext;
import kumarshantanu.relay.impl.JMSMailbox;
import kumarshantanu.relay.impl.JMSMessageSerializer;
import kumarshantanu.relay.impl.JMSPollConverter;
import kumarshantanu.relay.impl.Util;
import kumarshantanu.relay.monitoring.ThroughputAware;

import org.junit.Assert;
import org.junit.Test;

public class JMSMailboxTest {

	public static JMSContext createContext(Session session, String queueName) throws JMSException {
		final Destination destination = session.createQueue(queueName);
		final MessageProducer producer = session.createProducer(destination);
		final MessageConsumer consumer = session.createConsumer(destination);
		final Destination replyTo = session.createTemporaryQueue();
		final MessageConsumer replyToConsumer = session.createConsumer(replyTo);
		return new JMSContext() {
			public void onException(JMSException e) { e.printStackTrace(); }
			public Destination getReplyToDestination() { return replyTo; }
			public MessageConsumer getReplyToConsumer() { return replyToConsumer; }
			public MessageProducer getProducer() { return producer; }
			public MessageConsumer getConsumer() { return consumer; }
		};
	}

	@Test
	public void test() throws Exception {
		Assert.assertTrue("Test started", true);
		final AtomicLong counter = new AtomicLong();
		ExecutorService threadPool = Util.newThreadPool();
		DefaultAgent ag = new DefaultAgent(threadPool);
		final ActiveMQHelper amq = new ActiveMQHelper();
		JMSMessageSerializer<String> serde = new JMSMessageSerializer<String>() {
			public Message serialize(String message) throws JMSException {
				return amq.session.createTextMessage(message);
			}
			public String deserialize(Message format) throws JMSException {
				return ((TextMessage) format).getText();
			}
		};
		JMSContext context = createContext(amq.session, amq.queueName);
		JMSMailbox<String> mailbox = new JMSMailbox<String>(context, serde);
		JMSPollConverter<String> pollConverter = new JMSPollConverter<String>(context, serde);
		final Actor<String, ?> ac = new GenericActor<String, Message, Object>(ag, mailbox, pollConverter, null, null) {
			@Override
			public Object execute(String req) {
				counter.incrementAndGet();
				return null;
			}
		};
		final Runnable sender = new Runnable() {
			public void run() {
				try {
					ac.send("hello");
				} catch (MailboxException e) {
					e.printStackTrace();
				}
			}
		};
		Helper h = new Helper();
		threadPool.execute(ag);
		System.out.println(Actor.CURRENT_ACTOR_ID.get());
		h.doTimes(100000, counter, sender, "Warm up");
		System.out.println(((ThroughputAware) ac).getThroughputString());
		h.doTimes(250000, counter, sender, "Perf test");
		System.out.println(((ThroughputAware) ac).getThroughputString());
		ag.unregister(ac);
		Util.sleep(200);
		amq.close();
		Assert.assertTrue("Test finished", true);
	}

}
