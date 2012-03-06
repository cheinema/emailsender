package net.chlab.emailsender.ejb;

import java.util.concurrent.CountDownLatch;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.inject.Specializes;
import javax.jms.Message;

@Specializes
@MessageDriven(mappedName = "jms/EmailSenderQueue", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class SyncEmailSenderMessageBean extends EmailSenderMessageBean {

	public static CountDownLatch LATCH = new CountDownLatch(1);
	public static ThreadLocal<Long> threadValue = new ThreadLocal<>();

	@Override
	public void onMessage(Message message) {
		super.onMessage(message);
		threadValue.set(Thread.currentThread().getId());
		LATCH.countDown();
	}

}
