package net.chlab.emailsender.web;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import net.chlab.emailsender.bo.EmailMessage;
import net.chlab.emailsender.ejb.EmailSenderService;

@Named
@RequestScoped
public class EmailSender {

	@EJB
	private EmailSenderService emailSenderService;

	private EmailMessage message = new EmailMessage();

	public void send() {
		emailSenderService.send(message);
	}

	public EmailMessage getMessage() {
		return message;
	}

}
