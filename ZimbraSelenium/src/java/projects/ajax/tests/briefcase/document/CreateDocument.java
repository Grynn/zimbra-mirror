package projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.Buttons;
import projects.ajax.ui.DocumentBriefcaseNew;
import projects.ajax.ui.PageBriefcase.Locators;
import framework.core.ClientSessionFactory;
import framework.items.DocumentItem;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;

public class CreateDocument extends AjaxCommonTest {

	public CreateDocument() {
		logger.info("New "+ CreateDocument.class.getCanonicalName());
		
		super.startingPage = app.zPageBriefcase;
		
		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();		
		super.startingAccount = account;
		
	}
	
	@Test(description = "Create document through GUI - verify through SOAP",	groups = {"sanity"})
	public void CreateDocument_01() throws HarnessException {
		
		// Create document item
		DocumentItem document = new DocumentItem();
		
		//Select Briefcase tab	
		SleepUtil.sleepSmall();
		app.zPageBriefcase.navigateTo();
		
		// Open new document page
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase.zToolbarPressButton(Buttons.O_NEW_DOCUMENT);
		
		SleepUtil.sleepVeryLong();
		
		String newPageTitle = "Zimbra Docs";		
		ClientSessionFactory.session().selenium().selectWindow(newPageTitle);
		ClientSessionFactory.session().selenium().windowFocus();	
		ClientSessionFactory.session().selenium().windowMaximize();
		//ClientSessionFactory.session().selenium().waitForCondition("selenium.browserbot.getUserWindow()","10000");
		//ClientSessionFactory.session().selenium().getEval("selenium.browserbot.getCurrentWindow()");
		//ClientSessionFactory.session().selenium().getEval("selenium.browserbot.getUserWindow()");
		
		//if name field appears in the toolbar then document page is opened
		if(!documentBriefcaseNew.sIsElementPresent("//*[@id='DWT3_item_1']")){
			throw new HarnessException("could not open a new page");
		}
		else{
			DocumentBriefcaseNew.pageTitle = newPageTitle;			
		}
		
		//Fill out the document with the data
		documentBriefcaseNew.fill(document);
		
		// Save and close
		documentBriefcaseNew.submit();
		
		ClientSessionFactory.session().selenium().selectWindow("Zimbra: Briefcase");
		
		ZimbraAccount account = app.getActiveAccount();
		
		// Verify document name through SOAP
		document.importFromSOAP(account, document.getDocName());	
		String name = account.soapSelectValue("//mail:doc","name");	
		
		ZAssert.assertEquals(name,document.getDocName(), "Verify document name through SOAP");
		/*
		*/
	}
	
	@Test(description = "Create document through SOAP - verify through GUI", groups = {"sanity"})
	public void CreateDocument_02() throws HarnessException {
		
		// Create document item
		DocumentItem document = new DocumentItem();
		
		ZimbraAccount account = app.getActiveAccount();
		
		document.createUsingSOAP(account);
	
		//Select Briefcase tab	
		SleepUtil.sleepSmall();
		app.zPageBriefcase.navigateTo();	
		
		//ClientSessionFactory.session().selenium().refresh();
		//refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);
		
		// Verify document is created
		SleepUtil.sleepLong();
		ClientSessionFactory.session().selenium().selectWindow("Zimbra: Briefcase");
		ClientSessionFactory.session().selenium().windowFocus();	
		ClientSessionFactory.session().selenium().windowMaximize();
		
		String name = "";	
		if(ClientSessionFactory.session().selenium().isElementPresent("css=[id='zl__BDLV__rows']")&&
				ClientSessionFactory.session().selenium().isVisible("css=[id='zl__BDLV__rows']")){
			name = ClientSessionFactory.session().selenium().getText("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains(" + document.getDocName() + ")");
 		}
		
		ZAssert.assertEquals(name,document.getDocName(),"Verify document name through GUI");	
		
		/*
		//name = ClientSessionFactory.session().selenium().getText("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div[id^=zlif__BDLV__]");
 		//ClientSessionFactory.session().selenium().isElementPresent("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto']>div:contains[id*='zlif__BDLV__']");
 		//ClientSessionFactory.session().selenium().isElementPresent("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] div:contains('name')");
		*/
	}
}
