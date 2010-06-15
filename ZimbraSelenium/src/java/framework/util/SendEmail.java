package framework.util;

import javax.mail.*;
import javax.mail.internet.*;

import java.util.Properties;

public class SendEmail {
	private String subject="SelNG: Couldnt get subject";
	private String body="SelNG: Couldnt get body";
	
	public SendEmail(String sub, String bd){
		subject = sub;
		body = bd;
	}

   public void send() throws Exception{
      Properties props = new Properties();
      props.setProperty("mail.transport.protocol", "smtp");
      props.setProperty("mail.host", "mail.zimbra.com");

      Session mailSession = Session.getDefaultInstance(props, null);
      Transport transport = mailSession.getTransport();

      MimeMessage message = new MimeMessage(mailSession);
      subject= subject.replace("\n", "");
      subject.replace("\r", "");
      message.setSubject(subject);
      message.setContent(body, "text/plain");
      message.setFrom(new InternetAddress("qa-tms@zimbra.com"));
      message.addRecipient(Message.RecipientType.TO,
           new InternetAddress("qa-automation@zimbra.com"));

      transport.connect();
      transport.sendMessage(message,
          message.getRecipients(Message.RecipientType.TO));
      transport.close();
    }
}