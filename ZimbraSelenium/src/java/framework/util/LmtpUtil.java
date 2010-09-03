package framework.util;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.lmtpserver.utils.LmtpClient;

public class LmtpUtil {

	public static void addMessageLmtp(String[] recipients, String sender,
			String message) throws Exception {

		LmtpClient lmtp = new LmtpClient( ZimbraSeleniumProperties.getStringProperty("server"),	7025);
		byte[] data = message.getBytes();
		lmtp.sendMessage(new ByteArrayInputStream(data), recipients, sender,
				"TestUtil", (long) data.length);
		lmtp.close();
	}

	public static void injectMessage(String sender, String[] recipients,
			String ccUser, String subject, String body) throws Exception {

		LmtpClient lmtp = new LmtpClient(
				ZimbraSeleniumProperties.getStringProperty("server"),
				7025);
		
		String message = getTestMessageContent(sender, recipients[0], ccUser,
				body, subject);
		byte[] data = message.getBytes();
		String[] allRecipients;
		if (ccUser != null) {
			allRecipients = new String[recipients.length + 1];
			for (int i = 0; i < recipients.length; i++) {
				allRecipients[i] = recipients[i];
			}
			allRecipients[recipients.length] = ccUser;
		} else {
			allRecipients = new String[recipients.length];
			allRecipients = recipients;
		}

		lmtp.sendMessage(new ByteArrayInputStream(data), allRecipients, sender,
				"TestUtil", (long) data.length);
		lmtp.close();

	}
	
	private static String[] MESSAGE_TEMPLATE_LINES = {
		"From: ${SENDER_NAME} <${SENDER_EMAIL}>",
		"To: ${RECIPIENT_NAME} <${RECIPIENT_EMAIL}>",
		"Cc: ${CCUSER_NAME} <${CCUSER_EMAIL}>", "Subject: ${SUBJECT}",
		"Date: ${DATE}", "Content-Type: text/plain", "", "${BODY}" };

	private static String MESSAGE_TEMPLATE = StringUtil.join("\r\n",
		MESSAGE_TEMPLATE_LINES);

	public static String getTestMessageContent(String sender, String recipient,
			String ccUser, String body, String subject) throws ServiceException {

		String recipientName = "";
		if (recipient == null) {
			recipient = "touser@" + ZimbraSeleniumProperties.getStringProperty("server");
			recipientName = "touser";
		} else {
			recipientName = recipient.substring(0, recipient.indexOf("@"));
		}
		String senderName = "";
		if (sender == null) {
			sender = "sender@" + ZimbraSeleniumProperties.getStringProperty("server");
			senderName = "senderName";
		} else {
			senderName = sender.substring(0, sender.indexOf("@"));
		}
		String ccUserName = "";
		if (ccUser == null) {
			ccUser = "ccuserName@" + ZimbraSeleniumProperties.getStringProperty("server");
			ccUserName = "ccuserName";
		} else {
			ccUserName = ccUser.substring(0, ccUser.indexOf("@"));
		}

		Map<String, Object> vars = new HashMap<String, Object>();

		vars.put("SENDER_NAME", senderName);
		vars.put("SENDER_EMAIL", sender);
		vars.put("RECIPIENT_NAME", recipientName);
		vars.put("RECIPIENT_EMAIL", recipient);
		vars.put("CCUSER_NAME", ccUserName);
		vars.put("CCUSER_EMAIL", ccUser);
		vars.put("DATE", String.format(
				"%1$ta, %1$td %1$tb %1$tY %1$tH:%1$tM:%1$tS %1$tz (%1$tZ)",
				new Date()));
		vars.put("SUBJECT", subject);
		vars.put("BODY", body);
		return StringUtil.fillTemplate(MESSAGE_TEMPLATE, vars);
	}


}
