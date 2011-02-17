package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DocumentBriefcaseNew extends AbsForm {

	public static class Locators {
		public static final String zFrame = "css=iframe[id*='DWT']";
		public static final String zSaveAndCloseIconBtn = "//*[@id='DWT9_left_icon']";
		public static final String zBodyField = "css=body";
		public static final String zNameField = "css=[id^=DWT4]>input";
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
		sSelectFrame(Locators.zFrame);
		// ClientSessionFactory.session().selenium().selectFrame("css=iframe[id='DWT10',class='ZDEditor']");
		// ClientSessionFactory.session().selenium().type("xpath=(//html/body)",text);
		logger.info("typing Document Text" + text);
		sType(Locators.zBodyField, text);
	}

	public void typeDocumentName(String text) throws HarnessException {
		this.zSelectWindow("Zimbra Docs");

		sType(Locators.zNameField, text);
	}

	public void editDocumentName(DocumentItem docItem) throws HarnessException {
		if (sIsElementPresent(Locators.zEditNameField))
			sType(Locators.zEditNameField, docItem.getDocName());
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info("DocumentBriefcaseNew.fill(ZimbraItem)");

		// Make sure the item is a DocumentItem
		if (!(item instanceof DocumentItem)) {
			throw new HarnessException(
					"Invalid item type - must be DocumentItem");
		}

		// Convert object to DocumentItem
		DocumentItem docItem = (DocumentItem) item;

		// Fill out the form
		typeDocumentName(docItem.getDocName());
		typeDocumentText(docItem.getDocText());
		logger.info(item.prettyPrint());
	}

	@Override
	public void zSubmit() throws HarnessException {
		logger.info("DocumentBriefcaseNew.SaveAndClose()");

		// Look for "Save & Close"
		if (!this.sIsElementPresent(Locators.zSaveAndCloseIconBtn))
			throw new HarnessException("Save & Close button is not present "
					+ Locators.zSaveAndCloseIconBtn);

		boolean visible = this.sIsVisible(Locators.zSaveAndCloseIconBtn);
		if (!visible)
			throw new HarnessException("Save & Close button is not visible "
					+ Locators.zSaveAndCloseIconBtn);

		// Click on it
		zClick(Locators.zSaveAndCloseIconBtn);
		// this.sMouseDown(Locators.zSaveAndCloseIconBtn);
		// this.sMouseUp(Locators.zSaveAndCloseIconBtn);

		// Wait for the page to be saved
		// SleepUtil.sleepSmall();
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		throw new HarnessException("implement me");
	}
}
