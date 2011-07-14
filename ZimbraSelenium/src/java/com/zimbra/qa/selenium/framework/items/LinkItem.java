/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;

/**
 * This class represents a new document item
 * 
 * 
 */
public class LinkItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);
	
	/**
	 * The link name
	 */
	private String linkName;

	public void setName(String name) {
		linkName = name;
	}
	
	public String getName() {
		return (linkName);
	}
		
	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(LinkItem.class.getSimpleName()).append('\n');
		sb.append("Link: \n").append(linkName).append('\n');
		return (sb.toString());
	}

	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		// TODO Auto-generated method stub		
	}	
}
