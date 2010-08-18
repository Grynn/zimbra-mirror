package projects.zcs.tests.documents.tags;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class TagDocumentPageTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "tagDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("createRenameDeleteTagForPageAndVerify")
				|| test.equals("verifyTagFunctionalityFor2PageAndRemoveTag")
				|| test.equals("applyMutlipleTagToPageAndVerify")
				|| test.equals("applyTagByDnDTagToPageAndViceVersa")
				|| test.equals("tryToCreateDuplicateTagInDocuments")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Documents");
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------

	/**
	 * Verify create, rename & delete functionality for tag for page
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameDeleteTagForPageAndVerify(String pageName)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String tag1, newTag1;
		page.zDocumentCompose.zCreateBasicPage(pageName, "");
		page.zDocumentCompose.zCreateBasicPage(pageName, "");
		tag1 = getLocalizedData_NoSpecialChar();
		newTag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zDocumentPage.zClick(pageName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zDocumentApp.zTagPageIconBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);

		zRenameTag(tag1, newTag1);
		obj.zFolder.zNotExists(tag1);
		obj.zFolder.zClick(newTag1);
		obj.zListItem.zVerifyIsTagged(pageName);

		zDeleteTag(newTag1);
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsNotTagged(pageName);
		obj.zButton.zClick(page.zDocumentApp.zTagPageIconBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		obj.zFolder.zClick(page.zDocumentApp.zNotebookFolder);
		obj.zDocumentPage.zClick(pageName);
		obj.zButton.zClick(page.zDocumentApp.zTagPageIconBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Create 2 tag, apply 1 tag to each page and verify page exist / not exist
	 * by clicking to tag
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTagFunctionalityFor2PageAndRemoveTag(String pageName)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String pageName2, tag1, tag2;
		pageName2 = getLocalizedData_NoSpecialChar();
		page.zDocumentCompose.zCreateBasicPage(pageName, "");
		page.zDocumentCompose.zCreateBasicPage(pageName2, "");
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		obj.zDocumentPage.zClick(pageName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zDocumentApp.zTagPageIconBtn);
		obj.zMenuItem.zClick(tag1);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zDocumentApp.zBrowseIconBtn);
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(pageName);
		Thread.sleep(1000);
		obj.zFolder.zClick(page.zDocumentApp.zNotebookFolder);
		obj.zDocumentPage.zClick(pageName2);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zDocumentApp.zTagPageIconBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zDocumentApp.zBrowseIconBtn);
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(pageName2);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zExists(pageName);
		assertReport("false", obj.zListItem.zExistsDontWait(pageName2),
				"Verify page2 not exists");
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zListItem.zExists(pageName2);
		assertReport("false", obj.zListItem.zExistsDontWait(pageName),
				"Verify page1 not exists");
		obj.zFolder.zClick(page.zDocumentApp.zNotebookFolder);
		obj.zButton.zClick(page.zDocumentApp.zBrowseIconBtn);
		Thread.sleep(1000);
		assertReport("true", obj.zListItem.zExistsDontWait(pageName),
				"Verify page1 not exists");
		assertReport("true", obj.zListItem.zExistsDontWait(pageName2),
				"Verify page2 not exists");
		obj.zFolder.zClick(page.zDocumentApp.zNotebookFolder);
		obj.zDocumentPage.zClick(pageName2);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zDocumentApp.zTagPageIconBtn);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zDocumentApp.zTagPageIconBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Create 2 tag, apply both tag to page and verify both page exists after
	 * clicking to tag
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyMutlipleTagToPageAndVerify(String pageName)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String tag1, tag2;
		page.zDocumentCompose.zCreateBasicPage(pageName, "");
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		obj.zDocumentPage.zClick(pageName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zDocumentApp.zTagPageIconBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag1);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zDocumentApp.zBrowseIconBtn);
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(pageName);
		obj.zListItem.zClick(pageName);
		obj.zButton.zClick(page.zDocumentApp.zTagPageBtn_ListView);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zListItem.zVerifyIsTagged(pageName);
		obj.zButton.zClick(page.zDocumentApp.zTagPageBtn_ListView);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zNotExists(tag2);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zExists(pageName);
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zListItem.zExists(pageName);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verify drag n drop functionality for tag and page. Drag page to tag and
	 * verify tag applied & same way drag tag to page and verify tag applied
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyTagByDnDTagToPageAndViceVersa(String pageName)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String pageName2, tag1, tag2;
		pageName2 = getLocalizedData_NoSpecialChar();
		page.zDocumentCompose.zCreateBasicPage(pageName, "");
		page.zDocumentCompose.zCreateBasicPage(pageName2, "");
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zCreateTag(tag2);
		obj.zButton.zClick(page.zDocumentApp.zBrowseIconBtn);
		Thread.sleep(1000);

		zDragAndDrop("//tr[contains(@id, 'zlif__NBF')]//div[contains(text(), '"
				+ pageName + "')]",
				"//td[contains(@id, 'zti__main_Notebook') and contains(text(), '"
						+ tag1 + "')]");
		obj.zListItem.zVerifyIsTagged(pageName);
		Thread.sleep(1000);
		obj.zFolder.zClick(tag1);
		Thread.sleep(1000);
		obj.zListItem.zExists(pageName);

		obj.zFolder.zClick(page.zDocumentApp.zNotebookFolder);
		obj.zButton.zClick(page.zDocumentApp.zBrowseIconBtn);
		Thread.sleep(1000);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Notebook') and contains(text(), '"
						+ tag2 + "')]",
				"//tr[contains(@id, 'zlif__NBF')]//div[contains(text(), '"
						+ pageName2 + "')]");
		obj.zListItem.zVerifyIsTagged(pageName2);
		Thread.sleep(1000);
		obj.zFolder.zClick(page.zDocumentApp.zNotebookFolder);
		Thread.sleep(1000);
		obj.zFolder.zClick(tag2);
		Thread.sleep(1000);
		obj.zListItem.zExists(pageName2);
		assertReport("false", obj.zListItem.zExistsDontWait(pageName),
				"Verify page1 not exists");

		SelNGBase.needReset.set(false);
	}

	/**
	 * Try to create duplicate tag and verify its not allowed
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateTagInDocuments(String pageName)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String tag1;
		tag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zDuplicateTag(tag1);

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}