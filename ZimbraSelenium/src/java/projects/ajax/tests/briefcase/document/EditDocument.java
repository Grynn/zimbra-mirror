package projects.ajax.tests.briefcase.document;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.DefaultSelenium;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.briefcase.DocumentBriefcaseEdit;
import projects.ajax.ui.briefcase.DocumentBriefcaseNew;
import projects.ajax.ui.briefcase.DocumentBriefcaseOpen;
import projects.ajax.ui.briefcase.PageBriefcase.Locators;
import framework.core.ClientSessionFactory;
import framework.items.DocumentItem;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import framework.util.ZimbraSeleniumProperties;

public class EditDocument extends AjaxCommonTest {

	public EditDocument() {
		logger.info("New " + EditDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccount = null;

	}

	@BeforeClass(groups = { "always" })
	public void EditDocumentBeforeClass() throws HarnessException {
		logger.info(this.getClass().getSimpleName() + "BeforeClass start");
		if (startingAccount == null) {
			if (app.zPageMain.zIsActive())
				((DefaultSelenium) ClientSessionFactory.session().selenium())
						.click("css=[onclick='ZmZimbraMail._onClickLogOff();']");
			app.zPageLogin.zWaitForActive();
			logger.info(this.getClass().getSimpleName() + "BeforeClass finish");
		}
	}

	@Test(description = "Create document through SOAP - edit name & verify through GUI", groups = { "smoke" })
	public void EditDocument_01() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();

		ZimbraAccount account = app.zGetActiveAccount();
		String briefcaseFolderId = document.GetBriefcaseIdUsingSOAP(account);

		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ document.getDocName()
						+ "' l='"
						+ briefcaseFolderId
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>&lt;html>&lt;body>"
						+ document.getDocText()
						+ "&lt;/body>&lt;/html></content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// Select Briefcase tab
		SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Click on created document
		SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on Edit document icon in toolbar
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase
				.zToolbarPressButton(Button.B_EDIT_FILE);

		// Select edit document window
		SleepUtil.sleepLong();
		String windowName = document.getDocName();
		try {
			documentBriefcaseEdit.zSelectWindow(windowName);

			// if name field appears in the toolbar then document page is opened
			int i = 0;
			for (; i < 90; i++) {
				if (documentBriefcaseEdit
						.sIsElementPresent("//*[@id='DWT2_item_1']")) {
					logger.info("page loaded after " + i + " seconds");
					break;
				}
				SleepUtil.sleepSmall();
			}

			if (!documentBriefcaseEdit.sIsVisible("//*[@id='DWT2_item_1']")) {
				throw new HarnessException(
						"could not open an edit file page after " + i
								+ " seconds");
			}
			// Fill out the document with the new data
			document.setDocName("name"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentName(document.getDocName());

			// Save and close
			documentBriefcaseEdit.zSubmit();
		} catch (Exception ex) {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
			throw new HarnessException("error in editing document "
					+ windowName, ex);
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Verify document was saved with new data
		SleepUtil.sleepLong();

		String name = "";
		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			name = app.zPageBriefcase
					.sGetText("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		ZAssert.assertStringContains(name, document.getDocName(),
				"Verify document name through GUI");

		/*
		 * //name =ClientSessionFactory.session().selenium().getText(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div[id^=zlif__BDLV__]"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto']>div:contains[id*='zlif__BDLV__']"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] div:contains('name')"
		 * );
		 */
	}

	@Test(description = "Create document through SOAP - edit text through GUI & verify through SOAP", groups = { "smoke" })
	public void EditDocument_02() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();

		ZimbraAccount account = app.zGetActiveAccount();
		String briefcaseFolderId = document.GetBriefcaseIdUsingSOAP(account);

		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ document.getDocName()
						+ "' l='"
						+ briefcaseFolderId
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>&lt;html>&lt;body>"
						+ document.getDocText()
						+ "&lt;/body>&lt;/html></content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// Select Briefcase tab
		SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Click on created document
		SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on Edit document icon in toolbar
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase
				.zToolbarPressButton(Button.B_EDIT_FILE);

		// Select document window opened for editing
		SleepUtil.sleepLong();
		String windowName = document.getDocName();
		try {
			documentBriefcaseEdit.zSelectWindow(windowName);

			// if name field appears in the toolbar then document page is opened
			int i = 0;
			for (; i < 90; i++) {
				if (documentBriefcaseEdit
						.sIsElementPresent("//*[@id='DWT2_item_1']")) {
					logger.info("page loaded after " + i + " seconds");
					break;
				}
				SleepUtil.sleepSmall();
			}

			if (!documentBriefcaseEdit.sIsVisible("//*[@id='DWT2_item_1']")) {
				throw new HarnessException(
						"could not open an edit file page after " + i
								+ " seconds");
			}

			// Fill out the document with the new data
			document.setDocText("text"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentText(document.getDocText());

			// Save and close
			documentBriefcaseEdit.zSelectWindow(windowName);

			documentBriefcaseEdit.zSubmit();
		} catch (Exception ex) {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
			throw new HarnessException("error in editing document "
					+ windowName, ex);
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Verify document name & text through SOAP
		int i = 0;
		while (i < 20) {
			SleepUtil.sleepSmall();
			account.soapSend(

			"<SearchRequest xmlns='urn:zimbraMail' types='document'>" +

			"<query>" + document.getDocName() + "</query>" +

			"</SearchRequest>");

			if (account.soapSelectValue("//mail:doc", "fr") != null)
				break;
			i++;
			if (i == 20)
				logger
						.info("after "
								+ i
								+ " seconds account.soapSelectValue(//mail:doc,fr) is null");
		}

		String name = account.soapSelectValue("//mail:doc", "name");
		String text = account.soapSelectValue("//mail:doc", "fr").trim();

		ZAssert.assertEquals(document.getDocName(), name,
				" Verify document name through SOAP");
		ZAssert.assertEquals(document.getDocText(), text,
				" Verify document text through SOAP");

		/*
		 * // Verify document was saved with new data SleepUtil.sleepLong();
		 * 
		 * //Select document in a list view if
		 * (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']") &&
		 * app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
		 * app.zPageBriefcase.zClick(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
		 * + document.getDocName() + ")"); }
		 * 
		 * // Click on open in a separate window icon in toolbar
		 * DocumentBriefcaseOpen documentBriefcaseOpen = (DocumentBriefcaseOpen)
		 * app.zPageBriefcase
		 * .zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW);
		 * 
		 * // Select document opened in a separate window SleepUtil.sleepLong();
		 * 
		 * windowName = document.getDocName(); String text = ""; try {
		 * documentBriefcaseOpen.zSelectWindow(windowName);
		 * 
		 * // if name field appears in the toolbar then document page is opened
		 * int i = 0; for(; i < 90; i++){ if
		 * (documentBriefcaseOpen.sIsElementPresent("css=div[id='zdocument']"))
		 * { break; } SleepUtil.sleepSmall(); }
		 * 
		 * if (!documentBriefcaseOpen.sIsVisible("css=div[id='zdocument']") ) {
		 * throw new
		 * HarnessException("could not open a file in a separate window"); }
		 * 
		 * text = documentBriefcaseOpen.retriveDocumentText();
		 * 
		 * // close documentBriefcaseOpen.zSelectWindow(windowName);
		 * 
		 * ClientSessionFactory.session().selenium().close(); } finally {
		 * app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase"); }
		 * 
		 * ZAssert.assertEquals(text, document.getDocText(),
		 * "Verify document name through GUI");
		 */

		/*
		 * //name =ClientSessionFactory.session().selenium().getText(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div[id^=zlif__BDLV__]"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto']>div:contains[id*='zlif__BDLV__']"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] div:contains('name')"
		 * );
		 */
	}
}
