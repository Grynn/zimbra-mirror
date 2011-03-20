package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;

public class DocumentBriefcaseNew extends AbsForm {

	public static class Locators {
		public static final String zFrame = "css=iframe[id*='DWT']";
		public static final String zSaveAndCloseIconBtn = "//*[@id='DWT9_left_icon']";
		public static final String zBodyField = "css=body";
		public static final String zNameField = "css=[id^=DWT4]>input";
		public static final String zEditNameField = "css=[class=DwtInputField] [input$=]";
	}

	public static class Field {
		public static final Field Name = new Field("Name");
		public static final Field Body = new Field("Body");

		private String field;

		private Field(String name) {
			field = name;
		}
	}

	public static final String pageTitle = "Zimbra Docs";

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
	}

	public void zFillField(Field field, String value) throws HarnessException {

		String locator = null;

		if (field == Field.Name) {

			locator = Locators.zNameField;

			zSelectWindow("Zimbra Docs");

			// FALL THROUGH

		} else if (field == Field.Body) {

			locator = Locators.zBodyField;

			sSelectFrame(Locators.zFrame);

			// FALL THROUGH

		} else {
			throw new HarnessException("not implemented for field " + field);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for field " + field);
		}

		// Default behavior, enter value into locator field

		// Make sure the button exists
		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Field is not present field=" + field
					+ " locator=" + locator);

		// Enter text
		this.sType(locator, value);

		this.zWaitForBusyOverlay();
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

		// Add Document version notes in the popped up dialog
		DialogAddVersionNotes dlgAddNotes = new DialogAddVersionNotes(
				MyApplication, ((AppAjaxClient) MyApplication).zPageBriefcase);

		if (dlgAddNotes.zIsActive()) {
			dlgAddNotes.zEnterVersionNotes("notes"
					+ ZimbraSeleniumProperties.getUniqueString());

			dlgAddNotes.zClickButton(Button.B_OK);
		}
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		zWaitForWindow(pageTitle);

		zSelectWindow(pageTitle);

		zWaitForElementPresent("css=div[class='ZDToolBar ZWidget']");

		zWaitForElementPresent("css=iframe[id*='DWT'][class='ZDEditor']");

		zWaitForIframeText("css=iframe[id*='DWT'][class='ZDEditor']", "");
		
		return true;
	}
}
