/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.preferences;

import org.apache.commons.lang.StringUtils;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.*;



/**
 * Represents a "Rename Folder" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogEditQuickCommand extends AbsDialog {

	public static class QuickCommandType {

		public static final QuickCommandType Message = new QuickCommandType("Message");
		public static final QuickCommandType Contact = new QuickCommandType("Contact");
		public static final QuickCommandType Appointment = new QuickCommandType("Appointment");

		protected String ID = null;
		public QuickCommandType(String id) {
			ID = id;
		}
		
		public String toString() {
			return (ID);
		}
	}
	
	public static class QuickCommandOperation {

		public static final QuickCommandOperation TagWith = new QuickCommandOperation("TagWith");
		public static final QuickCommandOperation MarkAs = new QuickCommandOperation("MarkAs");
		public static final QuickCommandOperation MoveToFolder = new QuickCommandOperation("MoveToFolder");

		protected String ID = null;
		public QuickCommandOperation(String id) {
			ID = id;
		}
		
		public String toString() {
			return (ID);
		}
	}
	
	public static class QuickCommandTarget {
		
		public static final QuickCommandTarget MarkAsRead = new QuickCommandTarget("MarkAsRead");
		public static final QuickCommandTarget MarkAsUnRead = new QuickCommandTarget("MarkAsUnRead");
		public static final QuickCommandTarget MarkAsFlagged = new QuickCommandTarget("MarkAsFlagged");
		public static final QuickCommandTarget MarkAsUnFlagged = new QuickCommandTarget("MarkAsUnFlagged");
		public static final QuickCommandTarget Browse = new QuickCommandTarget("Browse");

		protected String ID = null;
		public QuickCommandTarget(String id) {
			ID = id;
		}

		public String toString() {
			return (ID);
		}

	}

	public static class Locators {

		public static final String MainDivCss = "css=div[id^='ZmQuickCommandDialog']";

	}




	public DialogEditQuickCommand(AbsApplication application, AbsTab tab) {
		super(application, tab);
	}


	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		AbsPage page = null;

		if ( button == Button.B_OK ) {

			locator = Locators.MainDivCss + " div[id$='_buttons'] td[id^='OK_'] td[id$='_title']";

		} else if ( button == Button.B_CANCEL ) {

			locator = Locators.MainDivCss + " div[id$='_buttons'] td[id^='Cancel_'] td[id$='_title']";

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}

		// Default behavior, click the locator
		//

		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}

		zClick(locator);
		zWaitForBusyOverlay();

		if ( page != null ) {
			page.zWaitForActive();
		}

		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {

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
		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.MainDivCss;

		boolean present = this.sIsElementPresent(locator);
		if ( !present ) {
			logger.info("Locator was not present: " + locator);
			return (false);
		}

		boolean visible = this.zIsVisiblePerPosition(locator, 0, 0);
		if ( !visible ) {
			logger.info("Locator was not visible: " + locator);
			return (false);
		}

		return (true);

	}



	public static class Field {
		public static final Field Name = new Field("Name");
		public static final Field Description = new Field("Description");

		// private String field;

		private Field(String name) {
			// field = name;
		}
	}


	/**
	 * Enable/Disable the Quick Command
	 * @param active
	 * @throws HarnessException
	 */
	public void zSetQuickCommandActive(boolean active)
	throws HarnessException 
	{
		logger.info(myPageName() + " zSetQuickCommandActive("+ active +")");
		throw new HarnessException("implement me!");
	}
	
	/**
	 * Set the name of the Quick Command
	 * @param name
	 * @throws HarnessException
	 */
	public void zSetQuickCommandName(String name)
	throws HarnessException {
		logger.info(myPageName() + " zSetQuickCommandName("+ name +")");

		String locator = Locators.MainDivCss + " div[id$='_content'] input[id$='_name']";
		this.sType(locator, name);
		this.zWaitForBusyOverlay();

	}

	/**
	 * Set the description of the Quick Command
	 * @param description
	 * @throws HarnessException
	 */
	public void zSetQuickCommandDescription(String description)
	throws HarnessException 
	{
		logger.info(myPageName() + " zSetQuickCommandDescription("+ description +")");

		String locator = Locators.MainDivCss + " div[id$='_content'] input[id$='_description']";
		this.sType(locator, description);
		this.zWaitForBusyOverlay();

	}



	/**
	 * Set the type of items this quick command applies to (Mail, Contacts, Appointments)
	 * @param type
	 * @throws HarnessException
	 */
	public void zSetQuickCommandType(QuickCommandType type)
	throws HarnessException 
	{
		logger.info(myPageName() + " zSetQuickCommandType("+ type +")");

		String pulldownLocator = Locators.MainDivCss + " td[id$='_itemTypeContainer'] td[id$='_dropdown']>div";
		String optionLocator = null;

		if ( type.equals(QuickCommandType.Message) ) {

			pulldownLocator = null;
			optionLocator = null;
			// See https://bugzilla.zimbra.com/show_bug.cgi?id=65620

		} else if ( type.equals(QuickCommandType.Contact) ) {

			optionLocator = null;
			throw new HarnessException("See https://bugzilla.zimbra.com/show_bug.cgi?id=65620");

		} else if ( type.equals(QuickCommandType.Appointment) ) {

			optionLocator = null;
			throw new HarnessException("See https://bugzilla.zimbra.com/show_bug.cgi?id=65620");

		} else {
			throw new HarnessException("invalid type: "+ type.toString());
		}

		if ( pulldownLocator != null ) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("zSetQuickCommandType() " + pulldownLocator + " not present");
			}

			this.zClickAt(pulldownLocator, "0,0");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if ( optionLocator != null ) {
				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("zSetQuickCommandType() " + optionLocator + " not present");
				}

				this.zClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();

			}
		}

	}


	/**
	 * Enable/disable the specified action
	 * @param index The action to modify (1-based)
	 * @param active
	 * @throws HarnessException
	 */
	public void zSetQuickCommandActionActive(int index, boolean active)
	throws HarnessException
	{
		logger.info(myPageName() + " zSetQuickCommandActionActive("+ index +", "+ active +")");

		if ( index < 1 )
			throw new IndexOutOfBoundsException("index is 1 based");
	
		logger.warn("Not yet implemented");
	}


	/**
	 * Set the operation for the specified action
	 * @param index The action to modify (1-based)
	 * @param operation
	 * @throws HarnessException
	 */
	public void zSetQuickCommandActionOperation(int index, QuickCommandOperation operation)
	throws HarnessException
	{
		logger.info(myPageName() + " zSetQuickCommandActionOperation("+ index +", "+ operation +")");

		if ( index < 1 )
			throw new IndexOutOfBoundsException("index is 1 based");
		
		// Locate the <tr/> element corresponding to the index
		// StringUtil.repeat(" tr", index) == " tr tr tr"
		//
		
		String pulldownLocator = "css=div[id='ZmQuickCommandDialog1'] tbody[id='ZmQuickCommandDialog1_actionsTbody']" 
			+ StringUtils.repeat(" tr", index) + 
			" td[id$='_select_container'] td[id$='_dropdown']>div";
		String optionLocator = null;
		
		
		if ( operation.equals(QuickCommandOperation.MarkAs) ) {
			
			optionLocator = "See http://bugzilla.zimbra.com/show_bug.cgi?id=65658";

			// FALL THROUGH
			
		} else if ( operation.equals(QuickCommandOperation.MoveToFolder) ) {
			
			optionLocator = "See http://bugzilla.zimbra.com/show_bug.cgi?id=65658";

			// FALL THROUGH
			
		} else if ( operation.equals(QuickCommandOperation.TagWith) ) {
			
			optionLocator = "See http://bugzilla.zimbra.com/show_bug.cgi?id=65658";

			// FALL THROUGH
			
		} else {
			throw new HarnessException("Operation "+ operation +" not yet implemented");
		}
		
		if ( pulldownLocator != null ) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("zSetQuickCommandActionOperation() " + pulldownLocator + " not present");
			}

			this.zClickAt(pulldownLocator, "0,0");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if ( optionLocator != null ) {
				
				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("zSetQuickCommandActionOperation() " + optionLocator + " not present");
				}

				this.zClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();

			}
		}

	}


	/**
	 * Set the target for the specified action
	 * @param index The action to modify (1-based)
	 * @param target
	 * @throws HarnessException
	 */
	public AbsPage zSetQuickCommandActionTarget(int index, QuickCommandTarget target) 
	throws HarnessException
	{
		logger.info(myPageName() + " zSetQuickCommandActionTarget("+ index +", "+ target +")");

		if ( index < 1 )
			throw new IndexOutOfBoundsException("index is 1 based");
	
		AbsPage page = null;
		String pulldownLocator = "css=div[id='ZmQuickCommandDialog1'] tbody[id='ZmQuickCommandDialog1_actionsTbody']" 
			+ StringUtils.repeat(" tr", index) 
			+ " td[id$='_select_container'] td[id$='_dropdown']>div";
		String optionLocator = null;
		
		if ( target.equals(QuickCommandTarget.Browse) ) {
			
			

			// Depending on what the operation is (Move To vs Tag With), the resulting
			// dialog will be different
			
			String locator = "css=div[id='ZmQuickCommandDialog1'] tbody[id='ZmQuickCommandDialog1_actionsTbody']" 
				+ StringUtils.repeat(" tr", index) 
				+ " td[id$='_select_container'] td[id$='_title']";
			
			String operation = this.sGetText(locator);
			
			if ( operation.equals("Move into folder") ) { // TODO: I18N
				
				page = new DialogMove(MyApplication, ((AppAjaxClient) MyApplication).zPagePreferences);
				
			} else if ( operation.equals("Tag with") ) {// TODO: I18N
				
				page = new DialogTag(MyApplication, ((AppAjaxClient) MyApplication).zPagePreferences);
				
			} else {
				throw new HarnessException("Unknown operation: "+ operation);
			}

			locator = "css=div[id='ZmQuickCommandDialog1'] tbody[id='ZmQuickCommandDialog1_actionsTbody']" 
				+ StringUtils.repeat(" tr", index) 
				+ " td[id$='_valueContainer'] td[id$='_title']";
			
			zClickAt(locator, "");
			
			zWaitForBusyOverlay();
			
			return (page);
			
		} else if ( target.equals(QuickCommandTarget.MarkAsFlagged)) {
			
			optionLocator = "See http://bugzilla.zimbra.com/show_bug.cgi?id=65668";
			
		} else if ( target.equals(QuickCommandTarget.MarkAsUnFlagged)) {
			
			optionLocator = "See http://bugzilla.zimbra.com/show_bug.cgi?id=65668";
			
		} else if ( target.equals(QuickCommandTarget.MarkAsRead)) {
			
			optionLocator = "See http://bugzilla.zimbra.com/show_bug.cgi?id=65668";
			
		} else if ( target.equals(QuickCommandTarget.MarkAsUnRead)) {
			
			optionLocator = "See http://bugzilla.zimbra.com/show_bug.cgi?id=65668";
			
		} else {
			throw new HarnessException("target "+ target +" not implemented");
		}
	
		if ( pulldownLocator != null ) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("zSetQuickCommandActionTarget() " + pulldownLocator + " not present");
			}

			this.zClickAt(pulldownLocator, "0,0");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if ( optionLocator != null ) {
				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("zSetQuickCommandActionTarget() " + optionLocator + " not present");
				}

				this.zClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();

			}
		}

		return (page);
	}



}
