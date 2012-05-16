package com.zimbra.qa.selenium.projects.octopus.tests.myfiles.folders;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles.Locators;
public class FolderNavigation extends OctopusCommonTest{

	public FolderNavigation()
	{
		super.startingPage = app.zPageMyFiles;
	}
	
	@Test(description="verify folder navigation from child to parent using back button for 2 child folders",groups={"smoke"})
	public void verifyFolderNavigationUsingBackButton()throws HarnessException
	{
		//Get current active account
		ZimbraAccount a= app.zGetActiveAccount();

		int [] testInput = {2,5};

		//Create array list variable
		ArrayList<FolderItem> listFolder;

		// Get the root folder of a
		FolderItem aBriefcase = FolderItem.importFromSOAP(a, SystemFolder.Briefcase);

		String aFolderName = "aFolder_Parent"+ ZimbraSeleniumProperties.getUniqueString();
		//Create folder Using SOAP under a root folder.

		a.soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+"<folder name='" + aFolderName + "' l='" + aBriefcase.getId() + "' view='document'/>"
				+"</CreateFolderRequest>");
		//Call function to create multiple subfolders under parent folder
		for (int input : testInput) {


			listFolder= createMultipleSubfolders(a, aFolderName, input);

			// Navigate till the last child folder
			for (FolderItem subFolderName : listFolder)
			{

				app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, subFolderName.getName());

			}
			//Navigate Back till top folder using back button
			while(app.zPageMyFiles.sIsVisible(Locators.zBackButton.locator))
			{
				// Click back button to navigate back.
				app.zPageMyFiles.zClick(Locators.zMyFilesView.locator);
				app.zPageMyFiles.zClick(Locators.zBackButton.locator);

				if(app.zPageMyFiles.sIsElementPresent(Locators.zMyFilesCurrentMenuLabel.locator)==true)
				{
					ZAssert.assertTrue(app.zPageOctopus.zIsFolderParent(aBriefcase, aFolderName), "Verify if current folder is a top folder under briefcase");
					break;
				}
			}  

		}
	}

	@Test(description="Verify navigation to TOP folder using My Files tab",groups={"smoke"})
	public void NavigateToParentFolderUsingMyFilesTab() throws HarnessException
	{
		//Get current active account
		ZimbraAccount a= app.zGetActiveAccount();

		//Create array list variable
		ArrayList<FolderItem> listFolder= new ArrayList<FolderItem>();

		// Get the root folder of a
		FolderItem aBriefcase = FolderItem.importFromSOAP(a, SystemFolder.Briefcase);

		String aFolderName = "aFolder_Parent"+ ZimbraSeleniumProperties.getUniqueString();
		//Create folder Using SOAP under a root folder.

		a.soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+"<folder name='" + aFolderName + "' l='" + aBriefcase.getId() + "' view='document'/>"
				+"</CreateFolderRequest>");
		//Call function to create multiple subfolders under parent folder
		listFolder=createMultipleSubfolders(a, aFolderName, 5);

		for (FolderItem subFolderName : listFolder)
		{

			app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, subFolderName.getName());

		}
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		boolean isPresent=app.zPageMyFiles.sIsElementPresent(Locators.zMyFilesListViewItems.locator+":contains("+aFolderName+")");

		ZAssert.assertTrue(isPresent, "Verify If user is displayed with top folder");

		ZAssert.assertTrue(app.zPageMyFiles.sIsElementPresent(Locators.zMyFilesCurrentMenuLabel.locator), "Check if Label My Files is present");

	}
}
