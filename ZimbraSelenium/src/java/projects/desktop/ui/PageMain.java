package projects.desktop.ui;

import projects.ajax.ui.PageMail;
import framework.ui.AbsApplication;
import framework.ui.AbsPage;
import framework.util.HarnessException;

public class PageMain extends AbsPage{

   public static class Locators {

      // TextFields
      public static final String zPeopleSearchField =
         "xpath=//*[@class=' people_search_input']";
      public static final String zPeopleSearchResultsField =
         "xpath=//*[@class='ZmPeopleSearch-noresults']";

      // Images
      // This is the search icon (magnifying glass)
      public static final String zPeopleSearchImage =
         "xpath=//*[@class='ImgSearch2']";
   }

   public PageMain(AbsApplication application) {
      super(application);

      logger.info("new " + PageMail.class.getCanonicalName());
   }

   @Override
   public String myPageName() {
      // TODO Auto-generated method stub
      return this.getClass().getName();
   }

   @Override
   public boolean zIsActive() throws HarnessException {
      // Check if the Search People textfield is present 
      boolean present = sIsElementPresent(Locators.zPeopleSearchField);
      if ( !present ) {
         logger.debug("isActive() present = "+ present);
         return (false);
      }

      // Check if the Search People textfield is visible per position 
      boolean visible = zIsVisiblePerPosition(Locators.zPeopleSearchField,
            0, 0);
      if ( !visible ) {
         logger.debug("isActive() visible = "+ visible);
         return (false);
      }
      
      logger.debug("isActive() = "+ true);
      return (true);
   }

   @Override
   public void zNavigateTo() throws HarnessException {
      // TODO Auto-generated method stub
   }

}
