/**
 * 
 */
package projects.ajax.ui;

import java.awt.event.KeyEvent;
import projects.ajax.ui.Actions.Action;
import projects.ajax.ui.Buttons.Button;
import framework.core.ClientSessionFactory;
import framework.ui.AbsApplication;
import framework.ui.AbsSeleniumObject;
import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * @author 
 *
 */
public class PageBriefcase extends AbsAjaxPage {

	public static class Locators {
		public static final String zNewBriefcaseOverviewPaneIcon = "id=ztih__main_Briefcase__BRIEFCASE_textCell";
		public static final String zBriefcaseFolder = "id=zti__main_Briefcase__16_textCell";
		public static final String zBriefcaseFolderIcon = "xpath=//div[@id='zti__main_Briefcase__16']";
		public static final String zTrashFolder = "id=zti__main_Briefcase__3_textCell";
		public static final String zBriefcaseAppIconBtn = "id=zb__App__Briefcase_left_icon";
		public static final String zNewMenuIconBtn = "id=zb__BCD__NEW_FILE_left_icon";
		public static final String zNewMenuLeftIconBtn  = "id=zb__BDLV__NEW_MENU_left_icon";
		public static final String zUploadFileIconBtn = "id=zb__BDLV__NEW_FILE_left_icon";
		public static final String zDeleteIconBtn = "id=zb__BCD__DELETE_left_icon";
		public static final String zDeleteBtn = "id=zb__BCD__DELETE";
		public static final String zMoveItemIconBtn = "id=zb__BCD__MOVE_left_icon";
		public static final String zTagItemIconBtn = "id=zb__BCD__TAG_MENU_left_icon";
		public static final String zViewIconBtn = "id=zb__BCD__VIEW_MENU_left_icon";
		public static final String zSendBtnIconBtn = "id=zb__BCD__SEND_FILE_left_icon";
		public static final String zNewDocumentIconBtn = "id=zb__BCD__NEW_DOC_left_icon";
		public static final String zNewSpreadsheetIconBtn = "id=zb__BCD__NEW_SPREADSHEET_left_icon";
		public static final String zNewPresentationIconBtn = "id=zb__BCD__NEW_PRESENTATION_left_icon";

	}
	
	public PageBriefcase(AbsApplication application) {
		super(application);		
		logger.info("new " + PageBriefcase.class.getCanonicalName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean isActive() throws HarnessException {
		
		// Make sure the main page is active
		if ( !this.MyApplication.zPageMain.isActive() ) {
			this.MyApplication.zPageMain.navigateTo();
		}

		// If the "folders" tree is visible, then Briefcase tab is active
		boolean loaded = this.sIsElementPresent(Locators.zBriefcaseFolderIcon);
		if ( !loaded )
			return (loaded);		
		boolean active = this.zIsVisiblePerPosition(Locators.zBriefcaseFolderIcon, 4, 74);
		return (active);

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());		
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void navigateTo() throws HarnessException {

		// Check if this page is already active.
		if ( isActive() ) {
			return;
		}
		
		// Make sure we are logged into the Ajax app
		if ( !MyApplication.zPageMain.isActive() ) {
			MyApplication.zPageMain.navigateTo();
		}
		
		//make sure mail page is loaded
		long l = 20;
		while(l > 0){
			SleepUtil.sleepSmall();
			if(this.sIsElementPresent("xpath=//div[@id='zov__main_Mail']"))
				break;
			l--;
		}	
		
		// Click on Briefcase icon
		if(this.sIsElementPresent(PageMain.Locators.zAppbarBriefcase) && 
				this.sIsVisible(PageMain.Locators.zAppbarBriefcase))
		zClick(PageMain.Locators.zAppbarBriefcase);
		
		waitForActive();

	}
	
	
	@Override
	public AbsSeleniumObject zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		if ( button == null )
			throw new HarnessException("Button cannot be null!");
			
		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsSeleniumObject page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( button == Buttons.B_NEW ) {
			
			// For "NEW" without a specified pulldown option, just return the default item
			// To use "NEW" with a pulldown option, see  zToolbarPressPulldown(Button, Button)
			//
			
			this.zPressKeyboardShortcut(KeyEvent.VK_N);
			
			// Not default behavior (zPressKeyboardShortcut vs. zClick)			
			return (new DocumentBriefcaseNew(this.MyApplication));
			
		} else if ( button == Buttons.O_NEW_BRIEFCASE ) {
			locator = "id="+ Locators.zNewMenuIconBtn;			
		} else if ( button == Buttons.O_NEW_DOCUMENT) {			
			// Check if the button is visible
			String attrs = sGetAttribute("xpath=(//div[@id='zb__BDLV__NEW_MENU'])@style");
			if ( !attrs.contains("visible") ) {
				throw new HarnessException(button + " not visible "+ attrs);
			}
			locator = Locators.zNewMenuLeftIconBtn;
			page = new DocumentBriefcaseNew(this.MyApplication);			
	    } else if ( button == Buttons.O_UPLOAD_FILE) {			
			// Check if the button is visible
			String attrs = sGetAttribute("xpath=(//div[@id='zb__BDLV__NEW_FILE'])@style");
			if ( !attrs.contains("visible") ) {
				throw new HarnessException(button + " not visible "+ attrs);
			}
			locator = Locators.zUploadFileIconBtn;
			page = null;			
	    } else if ( button == Buttons.B_MOVE ) {
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ "Locators.zMoveIconBtnID" +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ "Locators.zMoveIconBtnID";
			page = null;	// TODO
			throw new HarnessException("implement Move dialog");
			
		} else if ( button == Buttons.B_PRINT ) {
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ "Locators.zPrintIconBtnID" +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ "Locators.zPrintIconBtnID";
			page = null;	// TODO
			throw new HarnessException("implement Print dialog");
			
		}else if ( button == Buttons.B_TAG ) {
			
			// For "TAG" without a specified pulldown option, just click on the pulldown
			// To use "TAG" with a pulldown option, see  zToolbarPressPulldown(Button, Button)
			//
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ "Locators.zTagMenuDropdownBtnID" +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ "Locators.zTagMenuDropdownBtnID" +"'";
			
		} else if ( button == Buttons.B_LISTVIEW ) {
			
			// For "TAG" without a specified pulldown option, just click on the pulldown
			// To use "TAG" with a pulldown option, see  zToolbarPressPulldown(Button, Button)
			//
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ "Locators.zViewMenuDropdownBtnID" +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ "Locators.zViewMenuDropdownBtnID" +"'";
			
		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}
		
		// Default behavior, process the locator by clicking on it
		//
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Button is not present locator="+ locator +" button="+ button);
		
		// Click it
		this.zClick(locator);

		return (page);
	}

	@Override
	public AbsSeleniumObject zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +")");
		
		if ( pulldown == null )
			throw new HarnessException("Button cannot be null!");
		
		if ( pulldown == null )
			throw new HarnessException("Button cannot be null!");

		// Default behavior variables
		//
		String pulldownLocator = null;	// If set, this will be expanded
		String optionLocator = null;	// If set, this will be clicked
		AbsSeleniumObject page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( pulldown == Buttons.B_NEW ) {
			
			if ( option == Buttons.O_NEW_ADDRESSBOOK ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_APPOINTMENT ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_BRIEFCASE ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_CALENDAR ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_CONTACT ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_CONTACTGROUP ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_DOCUMENT ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_FOLDER ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_MESSAGE ) {
				
				// TODO: should this actually click New followed by Message?
				
				pulldownLocator = null;
				optionLocator = null;
				page = zToolbarPressButton(pulldown);
				
			} else if ( option == Buttons.O_NEW_TAG ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_TASK ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_TASKFOLDER ) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
			}
			
		} else if ( pulldown == Buttons.B_LISTVIEW ) { 

			if ( option == Buttons.O_LISTVIEW_BYCONVERSATION ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_LISTVIEW_BYMESSAGE ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_LISTVIEW_READINGPANEBOTTOM ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_LISTVIEW_READINGPANEOFF ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_LISTVIEW_READINGPANERIGHT ) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
			}

		} else if ( pulldown == Buttons.B_TAG ) {
			
			if ( option == Buttons.O_TAG_NEWTAG ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_TAG_REMOVETAG ) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
			}
			
		} else {
			throw new HarnessException("no logic defined for pulldown "+ pulldown);
		}

		// Default behavior
		if ( pulldownLocator != null ) {
			
			// TODO: Expand pulldownLocator
			
			if ( optionLocator != null ) {
				// TODO: Click optionLocator
			}
			
			throw new HarnessException("implement me!");
		}
		
		// Return the specified page, or null if not set
		return (page);
	}

	@Override
	public AbsSeleniumObject zListItem(Action action, String subject) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Override
	public AbsSeleniumObject zListItem(Action action, Action option, String subject) throws HarnessException {
		throw new HarnessException("implement me!");
	}
}
