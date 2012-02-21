package net.chlab.emailsender.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

@Stateless
public class EmailSenderService {

	@Resource(mappedName = "jms/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "jms/EmailSenderQueue")
	private Queue queue;
	
	@EJB
	private SequenceCounter sequenceCounter;

	public void send(String recipient, String subject) {
		try {
			final Connection connection = connectionFactory.createConnection();
			final Session session = connection.createSession(true, 0);
			final MessageProducer messageProducer = session.createProducer(queue);
			final TextMessage message = session.createTextMessage();
			message.setText(Long.toString(sequenceCounter.nextValue()));
			messageProducer.send(message);
			messageProducer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			throw new EJBException("JMS error", e);
		}
	}

}
