package  projects.ajax.ui.Addressbook;

import projects.ajax.ui.*;
import projects.ajax.ui.PageMain.Locators;
import framework.core.ClientSessionFactory;
import framework.ui.*;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.items.*;
import framework.items.FolderItem.FolderView;

public class PageAddressbook extends AbsAjaxPage{

	public static class AddressbookFolder extends FolderItem {
	    public static final StringItem CONTACTS=new StringItem("Contacts",0);
	    public static final StringItem EMAILED_CONTACTS=new StringItem("Emailed Contacts",1);
	    public static final StringItem TRASH=new StringItem("Trash",2);
		  
		public AddressbookFolder() {
			view = FolderView.Contact;	
		}
	
		public static void click(StringItem item) {
			ClientSessionFactory.session().selenium().click("div[@id='DwtTreeItemLevel1ChildDiv']/div[" + item.getOrder() + "]");
		}
		
	}
	
	public static class NewDropDown extends AbsSeleniumObject{
		public static final String NEW="xpath=//div[@id='zb__CNS__NEW_MENU']";
	    //TODO other fixed items
		//create an appointment
		public static void clickNew() {
			ClientSessionFactory.session().selenium().click(NEW);
		}
		
		public static void mouseOverNew() {
			ClientSessionFactory.session().selenium().mouseOver(NEW);
		}
	}
	
	
	public static class Toolbar extends AbsSeleniumObject{
		public static final String EDIT="id=zb__CNS__EDIT";
		public static final String DELETE="id=zb__CNS__DELETE";
		public static final String MOVE="id=zb__CNS__MOVE";		
		public static final String PRINT="id=zb__CNS__PRINT";
		//TODO print selected contact
		//TODO print addressbook
		public static final String TAG="id=zb__CNS__TAG_MENU";
		public static final String FORWARD="id=zb__CNS__SEND_CONTACTS_IN_EMAIL";
		
		public static final String NEWTAG="id=zb__CNS__TAG_MENU|MENU|NEWTAG";
		public static final String REMOVETAG="id=zb__CNS__TAG_MENU|MENU|REMOVETAG";
	
	}
	
	
	public static class IndexBar extends AbsSeleniumObject{
		//ABC 
		
	}
	 
	public static class LeftPanel extends AbsSeleniumObject{
		public static final String DIV="xpath=//div[@id='zv__CNS']";
		public static final String NO_RESULTS_FOUND="No results found.";
		
		public static boolean isEmpty() {
			 //System.out.println(">>>>> getText >>>>>>" + ClientSessionFactory.session().selenium().getText(DIV));
			 return ClientSessionFactory.session().selenium().getText(DIV).equals(NO_RESULTS_FOUND);
		}
			
		public static boolean isContained(String first, String last) {
			boolean result=false;
	        
			//TODO lastname
			//System.out.println(">>>>> getText >>>>>>" + ClientSessionFactory.session().selenium().getText(DIV + "/div[1]"));
			result= (ClientSessionFactory.session().selenium().getText(DIV + "/div[" + "1]").contains(first)); 
			
			return result;
		}
	}
	
	public static class RightPanel extends AbsSeleniumObject{
		public static final String DIV="xpath=//div[@class='ZmContactInfoView']";
		
		public static boolean isEmpty() {
			//System.out.println(">>>>> getText >>>>>>" + ClientSessionFactory.session().selenium().getText(DIV));
		    return (ClientSessionFactory.session().selenium().getText(DIV).trim().length() == 0);
		}

		public static boolean isContained(String first, String last) {
			boolean result=false;
			//System.out.println(">>>>> getText >>>>>>" + ClientSessionFactory.session().selenium().getText(DIV + "/div[2]/table/tbody"));

			//TODO lastname
			result= (ClientSessionFactory.session().selenium().getText(DIV + "/div[2]/table/tbody").contains(first)); 

			return result;
		}

		
	}
	
	public PageAddressbook(AbsApplication application) {
		super(application);		
		logger.info("new " + PageAddressbook.class.getCanonicalName());

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
		
		// If the "folders" tree is visible, then mail is active
		String locator = "xpath=//div[@id='ztih__main_Contacts__ADDRBOOK_div']";
		
		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (loaded);
	
		//TODO?????
		//boolean active = this.zIsVisiblePerPosition(locator, 4, 74);
	   
		boolean active=	ClientSessionFactory.session().selenium().isElementPresent(PageAddressbook.LeftPanel.DIV);
		
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
		
	
		if ( !MyApplication.zPageMain.isActive() ) {
			MyApplication.zPageMain.navigateTo();
		}
	
		// Click on Addressbook icon
		zClick(PageMain.Locators.zAppbarContact);
		
		
		waitForActive();

		
		//ClientSessionFactory.session().selenium().click(PageMain.Locators.zAppbarContact);
		//zClick(PageMain.Locators.zAppbarContact);
		//SleepUtil.sleepMedium();
		
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
		
		locator=button.toString();
		zClick(locator);
	    return page;
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
	
		return page;
	}
	
	@Override
	public AbsSeleniumObject zListItem(Action action, Action option, String subject) throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	@Override
	public AbsSeleniumObject zListItem(Action action, String subject) throws HarnessException {
		throw new HarnessException("implement me!");
	}


}
