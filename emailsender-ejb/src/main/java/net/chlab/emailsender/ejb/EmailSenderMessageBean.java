package net.chlab.emailsender.ejb;

import java.text.DateFormat;
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
import javax.jms.TextMessage;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@MessageDriven(mappedName = "jms/EmailSenderQueue", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class EmailSenderMessageBean implements MessageListener {

	private static Logger logger = Logger.getLogger(EmailSenderMessageBean.class.getName());

	@Resource(mappedName = "mail/EmailSender")
	private Session session;

	public void onMessage(Message inMessage) {
		if (inMessage instanceof TextMessage) {
			final TextMessage msg = (TextMessage) inMessage;
			try {
				logger.log(Level.INFO, "MESSAGE BEAN: Message received: " + msg.getText());
				sendMail("christian.heinemann@nb058.saxsys.de", msg.getText());
			} catch (JMSException ex) {
				throw new EJBException("JMS error", ex);
			}
		} else {
			throw new EJBException("Message of wrong type: " + inMessage.getClass().getName());
		}

	}

	private void sendMail(String recipient, String subject) {
		try {
			final MimeMessage message = new MimeMessage(session);
			message.setFrom();
			message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(recipient, false));
			message.setSubject(subject);
			message.setHeader("X-Mailer", "JavaMail");
			DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
			Date timeStamp = new Date();
			String messageBody = "This is a test message from the async example "
					+ "of the Java EE Tutorial. It was sent on " + dateFormatter.format(timeStamp) + ".";
			message.setText(messageBody);
			message.setSentDate(timeStamp);
			Transport.send(message);
			logger.log(Level.INFO, "Mail sent to {0}", recipient);
		} catch (MessagingException ex) {
			throw new EJBException("Error in sending email.", ex);
		}
	}

}
