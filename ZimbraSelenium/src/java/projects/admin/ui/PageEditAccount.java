package projects.admin.ui;

import framework.ui.AbsApplication;
import framework.util.HarnessException;

public class PageEditAccount extends AbsAdminPage {

	public static final String ztab__DOAMIN_EDIT__DWT192 = "xpath=//*[@id='ztab__DOAMIN_EDIT__DWT192']";
	public static final String ztab__DOAMIN_EDIT__DWT192_classAttr = "xpath=(//*[@id='ztab__DOAMIN_EDIT__DWT192'])@class";

	public PageEditAccount(AbsApplication application) {
		super(application);
		
		logger.info("new " + myPageName());

	}

	@Override
	public boolean isActive() throws HarnessException {

		// Make sure the Admin Console is loaded in the browser
		if ( !MyApplication.isLoaded() )
			throw new HarnessException("Admin Console application is not active!");

		
		boolean present = isElementPresent(ztab__DOAMIN_EDIT__DWT192);
		if ( !present ) {
			return (false);
		}
		
		String attrs = getAttribute(ztab__DOAMIN_EDIT__DWT192_classAttr);
		if ( !attrs.contains("ZSelected") ) {
			return (false);
		}

		return (true);
		
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void navigateTo() throws HarnessException {
		throw new HarnessException("implement me");
	}

}
