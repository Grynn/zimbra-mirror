package projects.mobile.ui;

import framework.items.ContactItem;
import framework.items.ZimbraItem;
import framework.ui.AbsApplication;
import framework.ui.AbsForm;
import framework.util.HarnessException;


public class FormContactNew extends AbsForm {

	public static class Locators {
		
		public static final String lSubmit = "";
		
		public static final String lLastName		= "xpath=//input[@id='lastName']";
		public static final String lFirstName		= "xpath=//input[@id='firstName']";
		public static final String lJobTitle		= "xpath=//input[@id='jobTitle']";
		public static final String lCompany			= "xpath=//input[@id='company']";
		public static final String lEmail			= "xpath=//input[@id='email']";

	}
	
	public FormContactNew(AbsApplication application) {
		super(application);
		
		logger.info("new " + FormContactNew.class.getCanonicalName());

	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zSubmit() throws HarnessException {
		if ( !(this.sIsElementPresent(Locators.lSubmit)) ) {
			throw new HarnessException("Submit button is not present "+ Locators.lSubmit);
		}
		this.sClick(Locators.lSubmit);
	}

	@Override
	public void zFill(ZimbraItem item) throws HarnessException {
		logger.debug(myPageName() + " fill()");
		logger.info(item.prettyPrint());

		if ( !(item instanceof ContactItem) ) {
			throw new HarnessException("Invalid item type - must be ContactItem");
		}
		
		ContactItem contact = (ContactItem)item;
		
		if ( contact.firstName != null ) {
			this.sType(Locators.lFirstName, contact.firstName);
		}
		
		if ( contact.lastName != null ) {
			this.sType(Locators.lLastName, contact.lastName);
		}

		if ( contact.email != null ) {
			this.sType(Locators.lEmail, contact.email);
		}

	}

}
