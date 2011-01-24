package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DocumentBriefcaseOpen extends AbsForm {

	public static class Locators {
		public static final String zFrame = "css=iframe[id='DWT9']";
		public static final String zSaveAndCloseIconBtn = "//*[@id='DWT8_left_icon']";
		public static final String zBodyField = "css=body";
		public static final String zDocumentBodyField = "css=td[class='ZhAppContent'] div[id='zdocument']";
		public static final String zNameField = "css=[class=DwtInputField] [input$=]";
		public static final String zDocumentNameField = "css=[class=TbTop] [b$=]";
	}

	public static String pageTitle;

	public DocumentBriefcaseOpen(AbsApplication application) {
		super(application);
		logger.info("new " + DocumentBriefcaseEdit.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		return this.getClass().getName();
	}

	public void typeDocumentText(String text) throws HarnessException {
		ClientSessionFactory.session().selenium().selectFrame(Locators.zFrame);
		// ClientSessionFactory.session().selenium().selectFrame("css=iframe[id='DWT9',class='ZDEditor']");
		// ClientSessionFactory.session().selenium().type("xpath=(//html/body)","fghjghj");
		if (sIsElementPresent(Locators.zBodyField)) {
			ClientSessionFactory.session().selenium().type(Locators.zBodyField,
					text);
		}
	}

	public String retriveDocumentText() throws HarnessException {
		// ClientSessionFactory.session().selenium().selectFrame(Locators.zFrame);
		// ClientSessionFactory.session().selenium().selectFrame("css=iframe[id='DWT9',class='ZDEditor']");
		String text = null;
		if (zIsVisiblePerPosition(Locators.zDocumentBodyField, 0, 0)) {
			// text = zGetHtml(Locators.zBodyField);
			// text = sGetText(Locators.zBodyField);
			text = sGetText(Locators.zDocumentBodyField);
		}
		return text;
	}

	public String retriveDocumentName() throws HarnessException {
		String name = "";
		if (sIsElementPresent(Locators.zDocumentNameField)) {
			name = ClientSessionFactory.session().selenium().getText(
					Locators.zDocumentNameField);
		}
		return name;
	}

	public void typeDocumentName(String text) throws HarnessException {
		if (sIsElementPresent(Locators.zNameField))
			sType(Locators.zNameField, text);
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info("DocumentBriefcaseEdit(ZimbraItem)");
		logger.info(item.prettyPrint());

		// Make sure the item is a DocumentItem
		if (!(item instanceof DocumentItem)) {
			throw new HarnessException(
					"Invalid item type - must be DocumentItem");
		}

		// Convert object to DocumentItem
		DocumentItem docItem = (DocumentItem) item;

		// Fill out the form
		typeDocumentText(docItem.getDocText());
		typeDocumentName(docItem.getDocName());
	}

	@Override
	public void zSubmit() throws HarnessException {
		logger.info("DocumentBriefcaseEdit.SaveAndClose()");

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
		// TODO Auto-generated method stub
		return false;
	}
}
