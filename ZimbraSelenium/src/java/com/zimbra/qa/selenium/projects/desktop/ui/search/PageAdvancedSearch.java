/**
 * 
 */
package com.zimbra.qa.selenium.projects.desktop.ui.search;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * @author Matt Rhoades
 *
 */
public class PageAdvancedSearch extends AbsTab {

	public static class Locators {
		public static final String TOOLBAR="css=div[id='skin_container_search_builder_toolbar']";
		public static final String PANEL="css=div[id='skin_container_search_builder']";
				
		//TODO 
		//Fill in non-dynamic ID after bug 57628 fixed.
		public static final String CLOSE="css=div[id='WAIT_FOR_BUG_57628_TO_GET_THE_ID_FOR_CLOSE_BUTTON";
	}
	
	
	public PageAdvancedSearch(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageAdvancedSearch.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {
		
		// Should it really wait?  Maybe the caller just
		// wants to know that it is not visible?
		
		try {
			zWaitForElementVisible(Locators.TOOLBAR);
		}
		catch (Exception e) {
			return false;
		} 
		
		boolean toolbarVisible = sIsVisible(Locators.TOOLBAR);
		if ( !toolbarVisible ) {
			return false;
		}

		boolean panelVisible = sIsVisible(Locators.PANEL);
		if ( !panelVisible ) {
			return false;
		}


		return (true);
		
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
	public void zNavigateTo() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		AbsPage page=null;
		tracer.trace("Press the "+ button +" button");

		if (button == Button.B_CLOSE) {
           zClick(Locators.CLOSE);			
           throw new HarnessException("Wait for bug 57826 fixed");
		}
	 	zWaitForBusyOverlay();
	 	
		return page;
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		tracer.trace("Click pulldown "+ pulldown +" then "+ option);

		throw new HarnessException("implement me!");
	}

	@Override
	public AbsPage zListItem(Action action, String item) throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a list view");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item) throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a list view");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)  throws HarnessException {
	   throw new HarnessException(myPageName() + " does not have a list view");
    }
	
	

}
