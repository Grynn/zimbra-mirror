package com.zimbra.qa.selenium.framework.items;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;

/**
 * Used to define a Zimbra All Item Types Search Results
 * 
 * @author Matt Rhoades
 *
 */
public class AllItemTypesItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);
	
	public String tag = null;	
	public String imageType = null;
	public String from = null;
	public String attachment = null; //boolean??
	public String subject = null;
	public String date = null;
	
			
	public AllItemTypesItem() {
		super();
	}

	
	public AllItemTypesItem(String tag, String imageType, String from,String attachment, String subject, String date) {
		this.tag       =tag;
		this.imageType =imageType;
		this.from      =from;
		this.attachment=attachment;
		this.subject   =subject;
		this.date      =date;
		
	}

	@Override
    public String getName() {
    	return "AllItemTypes: " + subject;
    }
	
	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		// TODO Auto-generated method stub

	}

	private String myId;
	public String getId() {
		return (myId);
	}
	public void setId(String id) {
		myId=id;
	}
	

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(AllItemTypesItem.class.getSimpleName()).append('\n');
		
		sb.append(String.format("Tag:(%s)\n Image Type:(%s)\n From:(%s)\n Attachment:(%s)\n Subject:(%s)\n Date:(%s)\n",
				                 tag, imageType, from, attachment, subject,date));
		
		return (sb.toString());
	}



}


