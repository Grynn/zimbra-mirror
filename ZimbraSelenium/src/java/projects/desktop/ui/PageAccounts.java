package projects.desktop.ui;

import framework.ui.AbsApplication;
import framework.ui.AbsPage;
import framework.ui.AbsTab;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;

public class PageAccounts extends AbsTab {


   // Inner class that store all the locators relevant to the page
   public static class Locators {

      // Tabs
      public static final String zMyAccountsTab_Inactive = "xpath=//div[@class='ZPanelTabInactive ZPanelFirstTab']";
      public static final String zAddNewAccountTab_Inactive = "xpath=//div[@class='ZPanelTabInactive ZPanelTab']";
      public static final String zMyAccountsTab_Active = "xpath=//div[@class='ZPanelTabActive ZPanelFirstTab']";
      public static final String zAddNewAccountTab_Active = "xpath=//div[@class='ZPanelTabActive ZPanelTab']";

      // Drop Down List
      public static final String zAccountType = "xpath=//*[@id='accountFlavor']";
   }

   public PageAccounts(AbsApplication application) {
      super(application);
      
      logger.info("new " + PageAccounts.class.getCanonicalName());
   }

   @Override
   public String myPageName() {
      return this.getClass().getName();
   }

   @Override
   public boolean zIsActive() throws HarnessException {
      // TODO Auto-generated method stub
      return false;
   }
	
	@Override
	public AbsPage zListItem(Action action, String item) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public AbsPage zListItem(Action action, Action option, String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void zNavigateTo() throws HarnessException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}
}
