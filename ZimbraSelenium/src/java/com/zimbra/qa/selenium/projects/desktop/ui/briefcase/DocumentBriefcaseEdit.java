package com.zimbra.qa.selenium.projects.desktop.ui.briefcase;

import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.desktop.ui.AppAjaxClient;

public class DocumentBriefcaseEdit extends AbsForm {

	public static class Locators {
		public static final String zFrame = "css=iframe[id^='DWT'][class='ZDEditor']";
		public static final String zSaveAndCloseIconBtn = "//*[@id='DWT8_left_icon']";
		public static final String zBodyField = "css=body";
		public static final String zNameField = "css=[class=DwtInputField] input";
	}

	private DocumentItem docItem;

	public DocumentItem getDocItem() {
		return docItem;
	}

	public DocumentBriefcaseEdit(AbsApplication application,
			DocumentItem document) {
		super(application);

		docItem = document;

		logger.info("new " + DocumentBriefcaseEdit.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		return this.getClass().getName();
	}

	public void typeDocumentText(String text) throws HarnessException {
		// ClientSessionFactory.session().selenium().getEval("var x = selenium.browserbot.findElementOrNull(\""+Locators.zFrame+"\");if(x!=null)x=x.contentWindow.document.body;if(browserVersion.isChrome){x.textContent='"+text+"';}else if(browserVersion.isIE){x.innerText='"+text+"';}");
		sSelectFrame(Locators.zFrame);
		logger.info("typing Document Text" + text);
		// SleepUtil.sleepSmall();
		sType(Locators.zBodyField, text);
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
		zSelectWindow(docItem.getName());
		sType(Locators.zNameField, text);
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
	}

	@Override
	public void zSubmit() throws HarnessException {
		zSelectWindow(docItem.getName());

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

		// add version notes
		DialogAddVersionNotes dlgAddNotes = new DialogAddVersionNotes(
				MyApplication, ((AppAjaxClient) MyApplication).zPageBriefcase);

		dlgAddNotes.zDismissAddVersionNotesDlg(docItem.getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info("DocumentBriefcaseEdit.zIsActive()");
		if (docItem != null) {

			zWaitForWindow(docItem.getName());

			zSelectWindow(docItem.getName());

			zWaitForElementPresent("css=div[class='ZDToolBar ZWidget']");

			zWaitForElementPresent("css=iframe[id*='DWT'][class='ZDEditor']");

			zWaitForIframeText("css=iframe[id*='DWT'][class='ZDEditor']",
					docItem.getDocText());

			logger.info("DocumentBriefcaseEdit is Active()");

			return true;
		} else {
			logger.info("DocumentBriefcaseEdit.docItem is null");

			return false;
		}
	}
}
