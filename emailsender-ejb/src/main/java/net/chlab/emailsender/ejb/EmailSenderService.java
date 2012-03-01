package net.chlab.emailsender.ejb;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import net.chlab.emailsender.bo.EmailMessage;

@Stateless
public class EmailSenderService {

	@Resource(mappedName = "jms/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "jms/EmailSenderQueue")
	private Queue queue;

	public void send(final EmailMessage emailMessage) {
		try {
			final Connection jmsConnection = connectionFactory.createConnection();
			final Session jmsSession = jmsConnection.createSession(true, 0);
			final MessageProducer jmsMessageProducer = jmsSession.createProducer(queue);
			final ObjectMessage jmsMessage = jmsSession.createObjectMessage(emailMessage);
			jmsMessageProducer.send(jmsMessage);
			jmsMessageProducer.close();
			jmsSession.close();
			jmsConnection.close();
		} catch (JMSException e) {
			throw new EJBException("JMS error", e);
		}
	}

}
