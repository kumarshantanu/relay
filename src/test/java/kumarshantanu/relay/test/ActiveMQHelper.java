package kumarshantanu.relay.test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

/**
 * References:
 * http://activemq.apache.org/how-do-i-embed-a-broker-inside-a-connection.html
 * http://activemq.apache.org/how-to-unit-test-jms-code.html
 * http://activemq.apache.org/hello-world.html
 * @author shantanu
 *
 */
public class ActiveMQHelper {

	public final BrokerService broker;
	public final Connection connection;
	public final Session session;
	public final Destination destination;

	public ActiveMQHelper(String brokerURL, String queueName) throws Exception {
		broker = new BrokerService();
		broker.setPersistent(false);
		broker.start();
		
		// Create a ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);

        // Create a Connection
        connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        destination = session.createQueue(queueName);
	}

	public ActiveMQHelper() throws Exception {
		this("vm://localhost", "TEST.FOO");
	}

	public void close() throws Exception {
		session.close();
        connection.close();
        broker.stop();
	}

	public MessageProducer getProducer() throws JMSException {
		return session.createProducer(destination);
	}

	public MessageConsumer getConsumer() throws JMSException {
		return session.createConsumer(destination);
	}

}
