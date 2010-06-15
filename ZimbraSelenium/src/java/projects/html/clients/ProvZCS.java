package projects.html.clients;

import java.util.Date;
import framework.core.SelNGBase;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.CliUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.servlet.ZimbraServlet;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import com.zimbra.common.util.EasySSLProtocolSocketFactory;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.lmtpserver.utils.LmtpClient;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;

import com.zimbra.cs.zclient.ZMailbox;

/**
 * @author raodv
 * 
 */
public class ProvZCS extends SelNGBase {
	public static final String DEFAULT_PASSWORD = "test123";
	public static final long MILLIS_PER_SECOND = 1000;
	public static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
	public static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
	public static Provisioning preferences;

	public static void setupZCSTestBed() throws ServiceException {
		try {
			EasySSLProtocolSocketFactory.init();
			CliUtil.toolSetup();

		} catch (Exception e) {
			e.printStackTrace();
		}
		SoapProvisioning sp = new SoapProvisioning();
		String soapuri = "https://" + config.getString("server") + ":7071"
				+ ZimbraServlet.ADMIN_SERVICE_URI;
		sp.soapSetURI(soapuri);
		sp.soapAdminAuthenticate(config.getString("adminName"), config
				.getString("adminPwd"));
		// sp.soapZimbraAdminAuthenticate();
		Provisioning.setInstance(sp);
		preferences = Provisioning.getInstance();
		createDomain(config.getString("testdomain"));

	}

	public static String randomizeAccntName(String username) {
		long systimestamp = System.currentTimeMillis();
		String testdomain = config.getString("testdomain");
		return config.getString("locale") + username + "_" + systimestamp + "@"
				+ testdomain;

	}

	public static String getRandomAccount() throws ServiceException {
		return getRandomAccount("");
	}

	public static String getRandomAccount(Map<String, Object> accntAttrs)
			throws ServiceException {
		return getRandomAccount(randomizeAccntName(""), accntAttrs);
	}

	public static void createAccount(String username) {
		try {
			getRandomAccount(username, new HashMap<String, Object>());
		} catch (ServiceException e) {

		}
	}

	public static String getRandomAccount(String username)
			throws ServiceException {

		String accnt = randomizeAccntName(username);
		getRandomAccount(accnt, new HashMap<String, Object>());

		return accnt;
	}

	private static String getRandomAccount(String username,
			Map<String, Object> accntAttrs) throws ServiceException {
		Provisioning prov = Provisioning.getInstance();
		accntAttrs.put(Provisioning.A_zimbraPrefLocale, config
				.getString("locale"));
		accntAttrs.put("zimbraPrefAutoAddAddressEnabled", "FALSE");
		accntAttrs.put("zimbraPrefCalendarInitialView", "week");
		accntAttrs.put("zimbraPrefCalendarApptReminderWarningTime", "0");
		accntAttrs.put("zimbraPrefSkin", "beach");
		

		accntAttrs.put("zimbraPrefReplyIncludeOriginalText",
				"includeBody");
		accntAttrs.put("zimbraPrefForwardIncludeOriginalText",
				"includeBody");
		prov.createAccount(username, "test123", accntAttrs);

		return username;
	}

	/**
	 * Modifies an account's preferences
	 * 
	 * ex: modifyAccount(Provisioning.A_zimbraPrefAutoAddAddressEnabled,
	 * "FALSE");
	 * 
	 * @param accountEmail
	 *            full email of the account
	 * @param attrName
	 *            get it from framework/docs/provisioning.java.txt
	 * @param attrVal
	 *            value of the attrName
	 * @throws ServiceException
	 */
	public static void modifyAccount(String accountEmail, String attrName,
			String attrVal) throws ServiceException {
		Account accnt = getAccount(accountEmail);
		Map<String, Object> acctAttrs = new HashMap<String, Object>();
		acctAttrs.put(attrName, attrVal);
		Provisioning prov = Provisioning.getInstance();
		prov.authAccount(accnt, "test123", "test");
		prov.modifyAttrs(accnt, acctAttrs);
	}

	public static void createDomain(String domain) throws ServiceException {
		Provisioning prov = Provisioning.getInstance();
		try {
			prov.createDomain(domain, null);
		} catch (ServiceException e) {
			// ignore expn if domain already exists
		}
	}

	public static String getAddress(String userName) throws ServiceException {
		if (userName.indexOf("@") > 0)
			return userName;

		return userName + "@" + "testdomain.com";
	}

	public static String getTestMessageContent(String sender, String recipient,
			String ccUser, String body, String subject) throws ServiceException {

		String recipientName = "";
		if (recipient == null) {
			recipient = "touser@" + getDomain();
			recipientName = "touser";
		} else {
			recipientName = recipient.substring(0, recipient.indexOf("@"));
		}
		String senderName = "";
		if (sender == null) {
			sender = "sender@" + getDomain();
			senderName = "senderName";
		} else {
			senderName = sender.substring(0, sender.indexOf("@"));
		}
		String ccUserName = "";
		if (ccUser == null) {
			ccUser = "ccuserName@" + getDomain();
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
		vars.put("DATE", getDateHeaderValue(new Date()));
		vars.put("SUBJECT", subject);
		vars.put("BODY", body);
		return StringUtil.fillTemplate(MESSAGE_TEMPLATE, vars);
	}

	public static String getTestMessage(String subject, String body,
			String recipient, String sender, Date date) throws ServiceException {
		if (recipient == null) {
			recipient = "user1";
		}
		if (sender == null) {
			sender = "jspiccoli";
		}
		if (date == null) {
			date = new Date();
		}

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("SUBJECT", subject);
		vars.put("DOMAIN", getDomain());
		vars.put("SENDER", sender);
		vars.put("RECIPIENT", recipient);
		vars.put("DATE", getDateHeaderValue(date));
		return StringUtil.fillTemplate(MESSAGE_TEMPLATE, vars);
	}

	public static String getDomain() throws ServiceException {
		// Config config = Provisioning.getInstance().getConfig();
		// String domain =
		// config.getAttr(Provisioning.A_zimbraDefaultDomainName, null);
		// assert(domain != null && domain.length() > 0);
		// return domain;
		return config.getString("server");
	}

	private static String getDateHeaderValue(Date date) {
		return String.format(
				"%1$ta, %1$td %1$tb %1$tY %1$tH:%1$tM:%1$tS %1$tz (%1$tZ)",
				date);
	}

	private static String[] MESSAGE_TEMPLATE_LINES = {
			"From: ${SENDER_NAME} <${SENDER_EMAIL}>",
			"To: ${RECIPIENT_NAME} <${RECIPIENT_EMAIL}>",
			"Cc: ${CCUSER_NAME} <${CCUSER_EMAIL}>", "Subject: ${SUBJECT}",
			"Date: ${DATE}", "Content-Type: text/plain", "", "${BODY}" };
	private static String MESSAGE_TEMPLATE = StringUtil.join("\r\n",
			MESSAGE_TEMPLATE_LINES);

	public static Mailbox getMailbox(String userName) throws ServiceException {
		Account account = getAccount(userName);
		return MailboxManager.getInstance().getMailboxByAccount(account);
	}

	/**
	 * returns account's current preference value example: "TRUE" =
	 * ProvZCS.getAccountPreferenceValue("zimbraPrefDeleteInviteOnReply")
	 * 
	 * @param accountName
	 * @param preferenceName
	 * @return string value
	 * @throws ServiceException
	 */
	public static String getAccountPreferenceValue(String accountName,
			String preferenceName) throws ServiceException {
		Account acnt = getAccount(accountName);
		return acnt.getAttr(preferenceName);
	}

	public static Account getAccount(String userName) throws ServiceException {
		String address = getAddress(userName);
		return Provisioning.getInstance().get(AccountBy.name, address);
	}

	public static ZMailbox getZMailbox(String username) throws ServiceException {
		ZMailbox.Options options = new ZMailbox.Options();
		options.setAccount("admin@qa32.liquidsys.com");
		options.setAccountBy(AccountBy.name);
		options.setPassword(DEFAULT_PASSWORD);
		String soapuri = "http://qa32.liquidsys.com:80"
				+ ZimbraServlet.USER_SERVICE_URI;
		options.setUri(soapuri);
		return ZMailbox.getMailbox(options);
	}

	public static void injectMessage(String sender, String[] recipients,
			String ccUser, String subject, String body) throws Exception {

		Provisioning prov = Provisioning.getInstance();
		LmtpClient lmtp = new LmtpClient(config.getString("server"), prov
				.getServer(getAccount(sender)).getIntAttr(
						Provisioning.A_zimbraLmtpBindPort, 7025));
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

	public static void addMessageLmtp(String[] recipients, String sender,
			String message) throws Exception {

		Provisioning prov = Provisioning.getInstance();
		LmtpClient lmtp = new LmtpClient("localhost", prov.getLocalServer()
				.getIntAttr(Provisioning.A_zimbraLmtpBindPort, 7025));
		byte[] data = message.getBytes();
		lmtp.sendMessage(new ByteArrayInputStream(data), recipients, sender,
				"TestUtil", (long) data.length);
		lmtp.close();
	}

}
