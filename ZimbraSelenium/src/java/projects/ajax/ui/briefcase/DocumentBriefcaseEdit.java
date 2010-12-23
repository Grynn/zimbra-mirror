package projects.ajax.ui.briefcase;

import projects.ajax.ui.AppAjaxClient;
import framework.core.ClientSessionFactory;
import framework.items.DocumentItem;
import framework.items.IItem;
import framework.ui.AbsApplication;
import framework.ui.AbsForm;
import framework.util.HarnessException;
import framework.util.SleepUtil;

public class DocumentBriefcaseEdit extends AbsForm {
	
	public static class Locators {		
		public static final String zSaveAndCloseIconBtn = "//*[@id='DWT8_left_icon']";
		public static final String zBodyField = "css=[id=DWT12][html$=][body$=]"; 
		public static final String zNameField = "css=[class=DwtInputField] [input$=]"; 
	}
	
	public static String pageTitle;
	
	public DocumentBriefcaseEdit(AbsApplication application) {
		super(application);		
		logger.info("new " + DocumentBriefcaseEdit.class.getCanonicalName());	
	}

	
	@Override
	public String myPageName() {
	 return this.getClass().getName();	
	}

	public void typeDocumentText(String text) throws HarnessException {
		if(ClientSessionFactory.session().selenium().isElementPresent(Locators.zBodyField)){
			((AppAjaxClient)MyApplication).zKeyboard.zTypeCharacters(text);			
		}
		//ClientSessionFactory.session().selenium().type("xpath=(//html/body)",text);
	}
	
	public void typeDocumentName(String text) throws HarnessException {
		if(ClientSessionFactory.session().selenium().isElementPresent(Locators.zNameField))
			sType(Locators.zNameField, text);	
	}	
	
	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info("DocumentBriefcaseEdit(ZimbraItem)");
		logger.info(item.prettyPrint());

		// Make sure the item is a DocumentItem
		if ( !(item instanceof DocumentItem) ) {
			throw new HarnessException("Invalid item type - must be DocumentItem");
		}
		
		// Convert object to DocumentItem
		DocumentItem docItem = (DocumentItem)item;
		
		// Fill out the form
		//typeDocumentText(docItem.getDocText());
		typeDocumentName(docItem.getDocName());	
	}

	
	@Override
	public void zSubmit() throws HarnessException {
		logger.info("DocumentBriefcaseEdit.SaveAndClose()");
		
		// Look for "Save & Close"
		if(!this.sIsElementPresent(Locators.zSaveAndCloseIconBtn))
			throw new HarnessException("Save & Close button is not present "+ Locators.zSaveAndCloseIconBtn);

		boolean visible = this.sIsVisible(Locators.zSaveAndCloseIconBtn);
		if ( !visible )
			throw new HarnessException("Save & Close button is not visible "+ Locators.zSaveAndCloseIconBtn);
		
		// Click on it
		zClick(Locators.zSaveAndCloseIconBtn);
		//this.sMouseDown(Locators.zSaveAndCloseIconBtn);
		//this.sMouseUp(Locators.zSaveAndCloseIconBtn);
		
		// Wait for the page to be saved
		SleepUtil.sleepSmall();
	}


	@Override
	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}	
}
