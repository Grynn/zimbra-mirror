package projects.ajax.ui.briefcase;

import framework.core.ClientSessionFactory;
import framework.items.DocumentItem;
import framework.items.IItem;
import framework.ui.AbsApplication;
import framework.ui.AbsForm;
import framework.util.HarnessException;
import framework.util.SleepUtil;

public class DocumentBriefcaseNew extends AbsForm {
	
	public static class Locators {		
		public static final String zFrame = "css=iframe[id='DWT10']";
		public static final String zSaveAndCloseIconBtn = "//*[@id='DWT9_left_icon']";
		public static final String zBodyField = "css=body"; 
		public static final String zNameField = "css=[id^=DWT4] [input$=]"; 	
		public static final String zEditNameField = "css=[class=DwtInputField] [input$=]"; 
	}
	
	public static String pageTitle;
	
	public DocumentBriefcaseNew(AbsApplication application) {
		super(application);		
		logger.info("new " + DocumentBriefcaseNew.class.getCanonicalName());	
	}

	
	@Override
	public String myPageName() {
	 return this.getClass().getName();	
	}

	public void typeDocumentText(String text) throws HarnessException {
		ClientSessionFactory.session().selenium().selectFrame(Locators.zFrame);
		//ClientSessionFactory.session().selenium().selectFrame("css=iframe[id='DWT10',class='ZDEditor']");
		//ClientSessionFactory.session().selenium().type("xpath=(//html/body)",text);
		if(sIsElementPresent(Locators.zBodyField)){
			logger.info("typing Document Text" + text);
			SleepUtil.sleepSmall();
			ClientSessionFactory.session().selenium().type(Locators.zBodyField,text);			
		}	
	}
	
	public void typeDocumentName(String text) throws HarnessException {
		this.zSelectWindow("Zimbra Docs");
		if(sIsElementPresent(Locators.zNameField))
			sType(Locators.zNameField, text);	
	}
		
	public void editDocumentName(DocumentItem docItem) throws HarnessException {
		if(sIsElementPresent(Locators.zEditNameField))
			sType(Locators.zEditNameField, docItem.getDocName());	
	}
	
	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info("DocumentBriefcaseNew.fill(ZimbraItem)");
		logger.info(item.prettyPrint());

		// Make sure the item is a DocumentItem
		if ( !(item instanceof DocumentItem) ) {
			throw new HarnessException("Invalid item type - must be DocumentItem");
		}
		
		// Convert object to DocumentItem
		DocumentItem docItem = (DocumentItem)item;
		
		// Fill out the form
		typeDocumentText(docItem.getDocText());
		typeDocumentName(docItem.getDocName());	
	}

	
	@Override
	public void zSubmit() throws HarnessException {
		logger.info("DocumentBriefcaseNew.SaveAndClose()");
		
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
		throw new HarnessException("implement me");
	}	
}
