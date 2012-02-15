package com.zimbra.qa.selenium.framework.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.lmtp.LmtpClient;
import com.zimbra.common.lmtp.LmtpProtocolException;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;

/**
 * @deprecated As of version 7.0.  See LmtpInject class instead
 * @author zimbra
 *
 */
public class LmtpUtil {

	/**
	 * Inject a mime file to a list of email addresses
	 * @param recipients an array of email addresses
	 * @param fullpath the full path of the file
	 * @throws HarnessException
	 */
	public static void injectFile(String recipient, String fullpath) throws HarnessException {
		// conver the recipient to an array, and call the same method
		injectFile(new String[]{ recipient }, fullpath);
	}
	
	/**
	 * Inject a mime file to a list of email addresses
	 * @param recipients an array of email addresses
	 * @param fullpath the full path of the file
	 * @throws HarnessException
	 */
	public static void injectFile(String[] recipients, String fullpath) throws HarnessException {
		String sender = "foo@example.com";
		File mime = new File(fullpath);
		
		try {
			
			// Convert the file contents to a String
			StringBuffer contents = new StringBuffer();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(mime));
				String line;
				while ( (line = reader.readLine()) != null) {
					contents.append(line).append(System.getProperty("line.separator"));				
				}
			} finally {
				if ( reader != null )
					reader.close();
			}
			
			// Inject the mime
			LmtpUtil.addMessageLmtp(recipients, sender, contents.toString());

			// Make sure the mime is delivered
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();

		} catch (Exception e) {
			throw new HarnessException(e);
		}

	}

	public static void addMessageLmtp(String[] recipients, String sender,
			String message) throws IOException, LmtpProtocolException  {

		LmtpClient lmtp = new LmtpClient( ZimbraSeleniumProperties.getStringProperty("server.host"),	7025);
		byte[] data = message.getBytes();
		lmtp.sendMessage(new ByteArrayInputStream(data), recipients, sender,
				"TestUtil", (long) data.length);
		lmtp.close();
	}

	public static void injectMessage(String sender, String[] recipients,
			String ccUser, String subject, String body) throws Exception {

		LmtpClient lmtp = new LmtpClient(
				ZimbraSeleniumProperties.getStringProperty("server.host"),
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
			recipient = "touser@" + ZimbraSeleniumProperties.getStringProperty("server.host");
			recipientName = "touser";
		} else {
			recipientName = recipient.substring(0, recipient.indexOf("@"));
		}
		String senderName = "";
		if (sender == null) {
			sender = "sender@" + ZimbraSeleniumProperties.getStringProperty("server.host");
			senderName = "senderName";
		} else {
			senderName = sender.substring(0, sender.indexOf("@"));
		}
		String ccUserName = "";
		if (ccUser == null) {
			ccUser = "ccuserName@" + ZimbraSeleniumProperties.getStringProperty("server.host");
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
