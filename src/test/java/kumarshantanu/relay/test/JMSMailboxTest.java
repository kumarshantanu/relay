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

import kumarshantanu.relay.MailboxException;
import kumarshantanu.relay.impl.AbstractActor;
import kumarshantanu.relay.impl.DefaultAgent;
import kumarshantanu.relay.impl.GenericActor;
import kumarshantanu.relay.impl.Util;
import kumarshantanu.relay.impl.jms.JMSContext;
import kumarshantanu.relay.impl.jms.JMSMailbox;
import kumarshantanu.relay.impl.jms.JMSMessageSerializer;
import kumarshantanu.relay.impl.jms.JMSPollConverter;
import kumarshantanu.relay.monitoring.ThroughputAware;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class JMSMailboxTest {

	private static ActiveMQHelper amq;
	private static JMSContext context;
	private static Helper h = new Helper();
	private static ExecutorService threadPool;
	private static DefaultAgent ag;
	private static JMSMessageSerializer<String> serde;
	private static JMSMailbox<String> mailbox;
	private static JMSPollConverter<String> pollConverter;
	private static AtomicLong counter;
	private static AbstractActor<String> ac;
	private static Runnable sender;

	@BeforeClass
	public static void setup() throws Exception {
		amq = new ActiveMQHelper();
		context = createContext(amq.session, amq.queueName);
		h = new Helper();
		threadPool = Util.newThreadPool();
		ag = new DefaultAgent(threadPool);
		serde = new JMSMessageSerializer<String>() {
			public Message serialize(String message) throws JMSException {
				return amq.session.createTextMessage(message);
			}
			public String deserialize(Message format) throws JMSException {
				return ((TextMessage) format).getText();
			}
		};
		mailbox = new JMSMailbox<String>(context, serde);
		pollConverter = new JMSPollConverter<String>(context, serde);
		counter = new AtomicLong();
		ac = new GenericActor<String, Message>(mailbox, pollConverter) {
			public void act(String req) {
				counter.incrementAndGet();
			}
		};
		ag.register(ac);
		sender = new Runnable() {
			public void run() {
				try {
					ac.send("hello");
				} catch (MailboxException e) {
					e.printStackTrace();
				}
			}
		};
		threadPool.execute(ag);
	}

	@AfterClass
	public static void cleanup() throws Exception {
		ag.unregister(ac);
		amq.close();
		ag.stop();
	}

	public static JMSContext createContext(final Session session, String queueName) throws JMSException {
		final Destination destination = session.createQueue(queueName);
		final MessageProducer producer = session.createProducer(destination);
		final MessageConsumer consumer = session.createConsumer(destination);
		final Destination replyTo = session.createTemporaryQueue();
		final MessageConsumer replyToConsumer = session.createConsumer(replyTo);
		return new JMSContext() {
			public void onException(JMSException e) { e.printStackTrace(); }
			public Destination getReplyToDestination() { return replyTo; }
			public MessageConsumer getReplyToConsumer() { return replyToConsumer; }
			public Session getSession() { return session; }
			public void commit() {}
			public void rollback() {}
			public MessageProducer getProducer() { return producer; }
			public MessageConsumer getConsumer() { return consumer; }
		};
	}

	@Test
	public void throughputTest() throws Exception {
		Assert.assertTrue("Test started", true);
		h.doTimes(100000, counter, sender, "Warm up");
		System.out.println(((ThroughputAware) ac).getThroughputString());
		h.doTimes(250000, counter, sender, "Perf test");
		System.out.println(((ThroughputAware) ac).getThroughputString());
		Util.sleep(200);
		Assert.assertTrue("Test finished", true);
	}

}
