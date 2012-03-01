package net.chlab.emailsender.ejb;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.chlab.emailsender.bo.EmailMessage;

@MessageDriven(mappedName = "jms/EmailSenderQueue", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class EmailSenderMessageBean implements MessageListener {

	private static Logger logger = Logger.getLogger(EmailSenderMessageBean.class.getName());

	@Resource(mappedName = "mail/EmailSender")
	private Session session;

	public void onMessage(final Message jmsMessage) {
		if (jmsMessage instanceof ObjectMessage) {
			final ObjectMessage jmsObjectMessage = (ObjectMessage) jmsMessage;
			try {
				final Serializable jmsRawObject = jmsObjectMessage.getObject();
				if (jmsRawObject instanceof EmailMessage) {
					final EmailMessage emailMessage = (EmailMessage) jmsRawObject;
					logger.log(Level.INFO, "MESSAGE BEAN: JMS message received");
					sendMail(emailMessage);

				} else {
					throw new EJBException("Object message of wrong type: " + jmsObjectMessage.getClass().getName());
				}
			} catch (JMSException ex) {
				throw new EJBException("JMS error", ex);
			}
		} else {
			throw new EJBException("JMS Message of wrong type: " + jmsMessage.getClass().getName());
		}

	}

	private void sendMail(final EmailMessage emailMessage) {
		try {
			final MimeMessage message = new MimeMessage(session);

			message.setFrom();
			message.setRecipients(MimeMessage.RecipientType.TO,
					InternetAddress.parse(emailMessage.getToAddress(), false));
			message.setSubject(emailMessage.getSubject());
			message.setHeader("X-Mailer", "JavaMail");
			message.setText(emailMessage.getBody());
			message.setSentDate(new Date());

			Transport.send(message);
			logger.log(Level.INFO, "Mail sent to {0}", emailMessage.getToAddress());
		} catch (MessagingException ex) {
			throw new EJBException("Error in sending email.", ex);
		}
	}

}
