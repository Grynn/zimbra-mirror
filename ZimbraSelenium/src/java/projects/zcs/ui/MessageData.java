package projects.zcs.ui;

public class MessageData {
	public String to = "";
	public String cc = "";
	public String bcc = "";
	public String subject = "";
	public String body = "";
	public String attachments = "";

	public MessageData(String to, String cc, String bcc, String subject,
			String body, String attachments) {
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.attachments = attachments;
	}
}
