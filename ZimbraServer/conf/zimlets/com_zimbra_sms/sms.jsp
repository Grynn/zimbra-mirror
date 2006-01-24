<%@ page language="java" import="javax.mail.*,javax.mail.internet.*,java.util.*" %>
<%@ taglib prefix="z" uri="/WEB-INF/zimbra.tld" %>
<z:zimletconfig var="config" action="list" zimlet="com_zimbra_sms"/>
<%
    // Get account info for http://www.ipipi.com
    Map zConfig = (Map)request.getAttribute("config");
    String username = (String)((Map)zConfig.get("global")).get("smsusername");
	String password = (String)((Map)zConfig.get("global")).get("smspassword");
	String from = (String)((Map)zConfig.get("global")).get("smsfrom");
	
	String subject = "SMS from Zimbra";
	String smtphost = "ipipi.com";
	String phone_num = request.getParameter("to");
	String to;
	String body = request.getParameter("body");
	boolean success = true;

	phone_num = java.net.URLDecoder.decode(phone_num, "UTF8");
	body = java.net.URLDecoder.decode(body, "UTF8");
	body = body.replace('\n', ';');

    phone_num = phone_num.replace("(", "");
    phone_num = phone_num.replace(")", "");
    phone_num = phone_num.replace("-", "");
	phone_num = phone_num.trim();
	if (!phone_num.startsWith("+1")) {
		if (phone_num.startsWith("1")) {
			phone_num = "+" + phone_num;
		} else {
			phone_num = "+1" + phone_num;
		}
	}
	to = phone_num + "@sms.ipipi.com";
	Transport tr = null;

	try {
		Properties props = System.getProperties();
		props.put("mail.smtp.auth", "true");

		// Get a Session object
		Session mailSession = Session.getDefaultInstance(props, null);

		// construct the message
		Message msg = new MimeMessage(mailSession);

		//Set message attributes
		msg.setFrom(new InternetAddress(from));
		InternetAddress[] address = { new InternetAddress(to) };
		msg.setRecipients(Message.RecipientType.TO, address);
		msg.setSubject(subject);
		msg.setText(body);
		msg.setSentDate(new Date());

		tr = mailSession.getTransport("smtp");
		tr.connect(smtphost, username, password);
		msg.saveChanges();
		tr.sendMessage(msg, msg.getAllRecipients());
		tr.close();

	} catch (Exception e) {
		success = false;
		e.printStackTrace();
	}
	if (success) {
		out.println("SMS Sent to:" + phone_num);
	} else {
		out.println("Error: Cannot SMS\n" + phone_num);
	}
%>