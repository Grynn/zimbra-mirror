/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsSeparateWindow;



/**
 * Represents a "Compose in New Window" form
 * <p>
 * @author Matt Rhoades
 *
 */
public class SeparateWindowFormMailNew extends AbsSeparateWindow {

	public static class Locators {

	}
	


	public SeparateWindowFormMailNew(AbsApplication application) {
		super(application);
		
		this.DialogWindowTitle = "Compose";
		
	}
	
	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

}
