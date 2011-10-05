/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * Represents a "Create New Folder" dialog box
 * 
 * Lots of methods not yet implemented. See
 * https://bugzilla.zimbra.com/show_bug.cgi?id=55923
 * <p>
 * 
 * @author Matt Rhoades
 * 
 */
public class DialogEditFolder extends com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder{

	
	public DialogEditFolder(AbsApplication application, AbsTab tab) {
		super(application, tab);
		logger.info("new " + DialogEditFolder.class.getCanonicalName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}


	
}
