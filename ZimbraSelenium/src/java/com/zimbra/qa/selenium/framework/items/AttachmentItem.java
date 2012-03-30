package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AttachmentItem {
	protected static Logger logger = LogManager.getLogger(AttachmentItem.class);

	private String Locator = null;
	private String AttachmentName = null;
	private String AttachmentIcon = null;
	
	
	public AttachmentItem() {
		logger.info("new "+ AttachmentItem.class.getCanonicalName());
	}


	public void setLocator(String locator) {
		Locator = locator;
	}


	public String getLocator() {
		return Locator;
	}


	public void setAttachmentName(String attachmentName) {
		AttachmentName = attachmentName;
	}


	public String getAttachmentName() {
		return AttachmentName;
	}

	public void setAttachmentIcon(String attachmentIcon) {
		AttachmentIcon = attachmentIcon;
	}


	public String getAttachmentIcon() {
		return AttachmentIcon;
	}


	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(AttachmentItem.class.getSimpleName()).append('\n');
		sb.append("Name: ").append(AttachmentName).append('\n');
		sb.append("Locator: ").append(Locator).append('\n');
		return (sb.toString());
	}
	
}
