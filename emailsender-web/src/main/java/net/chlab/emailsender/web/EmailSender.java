package net.chlab.emailsender.web;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import net.chlab.emailsender.ejb.EmailSenderService;

@Named
@RequestScoped
public class EmailSender {

	@EJB
	private EmailSenderService emailSenderService;
	
	public void send() {
		emailSenderService.send("ch@chlab.net", "EmailSender");
	}
	
}
