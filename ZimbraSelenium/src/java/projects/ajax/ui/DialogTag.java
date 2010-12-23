/**
 * 
 */
package projects.ajax.ui;

import framework.ui.AbsApplication;
import framework.ui.AbsDialog;
import framework.ui.Button;
import framework.util.HarnessException;

/**
 * Represents a "New Tag", "Rename Tag" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogTag extends AbsDialog {

	public static class Locators {
	
		// TODO:  See https://bugzilla.zimbra.com/show_bug.cgi?id=54173
		public static final String zTagDialogId		= "CreateTagDialog";
		
		public static final String zTitleId	 		= "CreateTagDialog_title";

		public static final String zTagNameFieldId	= "CreateTagDialog_name";

		public static final String zTagColorPulldownId = "ZmTagColorMenu_dropdown";
		
		public static final String zButtonsId 		= "CreateTagDialog_buttons";
		public static final String zButtonOkId 		= "DWT178_title";
		public static final String zButtonCancelId 	= "DWT179_title";


	}
	
	
	public DialogTag(AbsApplication application) {
		super(application);
	}
	
	public boolean isActive() {
		
		return ( this.sIsElementPresent(Locators.zTagDialogId) );
		
	}
	
	public void zSetTagName(String name) throws HarnessException {
		logger.info(myPageName() + " zSetTagName("+ name +")");

		String locator = "//input[@id='"+ Locators.zTagNameFieldId +"']";
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Tag name locator "+ locator +" is not present");
		}
		
		this.sType(locator, name);
		
	}
	
	public void zSetTagColor(String color) throws HarnessException {
		logger.info(myPageName() + " zSetTagColor("+ color +")");

		throw new HarnessException("implement me!");
		
	}
	
	@Override
	public void zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		
		if ( button == Button.B_OK ) {
			
			// TODO: L10N this
			locator =  "//div[@id='"+ Locators.zTagDialogId +"']//div[@id='"+ Locators.zButtonsId +"']//td[text()='OK']";
			
		} else if ( button == Button.B_CANCEL ) {
			
			// TODO: L10N this
			locator =  "//div[@id='"+ Locators.zTagDialogId +"']//div[@id='"+ Locators.zButtonsId +"']//td[text()='Cancel']";

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Button "+ button +" locator "+ locator +" not present!");
		}
		
		this.zClick(locator);
		
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		
		// Need to implement for:
		
		// "Create New Tag"
		// "Tag name:"
		// "Blue", "Cyan", ..., "Orange", "More colors ..." (Tag color pulldown)
		// OK
		// Cancel
		
		throw new HarnessException("implement me");
	}


	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		throw new HarnessException("implement me!");
	}



}
