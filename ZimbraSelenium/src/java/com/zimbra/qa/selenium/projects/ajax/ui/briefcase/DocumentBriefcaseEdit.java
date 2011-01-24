package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DocumentBriefcaseEdit extends AbsForm {

	public static class Locators {
		public static final String zFrame = "css=iframe[id='DWT9'][class='ZDEditor']";
		public static final String zSaveAndCloseIconBtn = "//*[@id='DWT8_left_icon']";
		public static final String zBodyField = "css=body";
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
		// ClientSessionFactory.session().selenium().getEval("var x = selenium.browserbot.findElementOrNull(\""+Locators.zFrame+"\");if(x!=null)x=x.contentWindow.document.body;if(browserVersion.isChrome){x.textContent='"+text+"';}else if(browserVersion.isIE){x.innerText='"+text+"';}");
		sSelectFrame(Locators.zFrame);
		if (sIsElementPresent(Locators.zBodyField)) {
			logger.info("typing Document Text" + text);
			// SleepUtil.sleepSmall();
			ClientSessionFactory.session().selenium().type(Locators.zBodyField,
					text);
		}
	}

	public String retriveDocumentText() throws HarnessException {
		// ClientSessionFactory.session().selenium().getEval("var x = selenium.browserbot.findElementOrNull(\""+Locators.zFrame+"\");if(x!=null)x=x.contentWindow.document.body;if(browserVersion.isChrome){x.textContent;}else if(browserVersion.isIE){x.innerText;}");
		sSelectFrame(Locators.zFrame);
		String text = "";
		if (sIsElementPresent(Locators.zBodyField)) {
			text = sGetText(Locators.zBodyField);
		}
		return text;
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
