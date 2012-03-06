package net.chlab.emailsender.ejb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import net.chlab.emailsender.bo.EmailMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.mock_javamail.Mailbox;

@RunWith(Arquillian.class)
public class EmailSenderServiceIT {

	@Inject
	private EmailSenderService serviceUnderTest;

	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap.create(JavaArchive.class).addPackage("net.chlab.emailsender.bo")
				.addClasses(EmailSenderService.class, EmailSenderMessageBean.class, SyncEmailSenderMessageBean.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
	}

	@Before
	public void clearMailbox() {
		Mailbox.clearAll();
	}

	@Test
	public void testSendMessage() throws Exception {
		final EmailMessage message = new EmailMessage();
		message.setToAddress("user@example.com");
		message.setSubject("subject test");
		message.setBody("body test");

		serviceUnderTest.send(message);

		assertThat("MDB processing done in given time", SyncEmailSenderMessageBean.LATCH.await(30, TimeUnit.SECONDS),
				is(true));

		final Mailbox mailbox = Mailbox.get("user@example.com");
		assertThat("Number of mails in mailbox", mailbox.size(), is(1));
		assertThat("Mail subject", mailbox.get(0).getSubject(), is("subject test"));
		assertThat("Mail body", (String) mailbox.get(0).getContent(), is("body test"));

		assertThat("MDB correctly cleaned up", SyncEmailSenderMessageBean.threadValue.get(), is(nullValue()));
	}

}
