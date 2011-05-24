package com.zimbra.qa.selenium.projects.desktop.ui.briefcase;

import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.projects.desktop.ui.AppAjaxClient;

public class DocumentBriefcaseNew extends AbsForm {

	public static class Locators {
		public static final String zFrame = "css=iframe[id*='DWT']";
		public static final String zSaveAndCloseIconBtn = "//*[@id='DWT9_left_icon']";
		public static final String zBodyField = "css=body";
		public static final String zNameField = "css=[id^=DWT4]>input";
		public static final String zEditNameField = "css=[class=DwtInputField] [input$=]";
		public static final String zEnableVersionNotes = "css=div[class=DwtComposite] input[id=enableDesc]";
	}

	public static class Field {
		public static final Field Name = new Field("Name");
		public static final Field Body = new Field("Body");

		// private String field;

		private Field(String name) {
			// field = name;
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
			sType(Locators.zEditNameField, docItem.getName());
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
	}

	public void zFillField(Field field, String value) throws HarnessException {

      if (field == Field.Name) {

         String nameFieldLocator = Locators.zNameField;

         zSelectWindow(pageTitle);

         // Make sure the locator exists
         if (!this.sIsElementPresent(nameFieldLocator))
            throw new HarnessException("Locator is not present: "
                  + nameFieldLocator);

         this.sMouseOver(nameFieldLocator);
         this.sFocus(nameFieldLocator);
         this.zClick(nameFieldLocator);
         this.sType(nameFieldLocator, value);
         logger.info("typed: " + value);

      } else if (field == Field.Body) {

         String iframeLocator = Locators.zFrame;

         // Make sure the locator exists
         if (!this.sIsElementPresent(iframeLocator))
            throw new HarnessException("Locator is not present: "
                  + iframeLocator);

         this.sMouseOver(iframeLocator);
         this.sFocus(iframeLocator);
         this.zClick(iframeLocator);

         this
               .sGetEval("var bodytext=\""
                     + value
                     + "\";"
                     + "var iframe_locator=\""
                     + iframeLocator
                     + "\";"
                     + "var iframe_body=selenium.browserbot.findElement(iframe_locator).contentWindow.document.body;"
                     + "if (browserVersion.isFirefox || browserVersion.isChrome){iframe_body.textContent=bodytext;}"
                     + "else if(browserVersion.isIE){iframe_body.innerText=bodytext;}"
                     + "else {iframe_body.innerText=bodytext;}");
      } else {
         throw new HarnessException("Not implemented field: " + field);
      }

      this.zWaitForBusyOverlay();
      SleepUtil.sleepVerySmall();
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

		if (!(sIsElementPresent(Locators.zEnableVersionNotes) && sIsChecked(Locators.zEnableVersionNotes))) {
			// Click on it
			zClick(Locators.zSaveAndCloseIconBtn);
		} else {
			// Click on it
			// this.sMouseDown(Locators.zSaveAndCloseIconBtn);
			// this.sMouseUp(Locators.zSaveAndCloseIconBtn);
			zClick(Locators.zSaveAndCloseIconBtn);

			DialogAddVersionNotes dlgAddNotes = new DialogAddVersionNotes(
			      MyApplication,
			      ((AppAjaxClient) MyApplication).zPageBriefcase);
			
			dlgAddNotes.zDismissAddVersionNotesDlg(pageTitle);
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
