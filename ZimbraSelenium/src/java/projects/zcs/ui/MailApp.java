package projects.zcs.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.testng.Assert;

import framework.core.SelNGBase;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

/**
 * This Class have UI-level methods related Mail-app(conversation view). e.g:
 * zNavigateToMailCompose, zEnterComposeValues etc It also has static-final
 * variables that holds ids of icons on the mailList-toolbar(like
 * zNewMenuIconBtn, zGetMailIconBtn etc). If you are dealing with the toolbar
 * buttons, use these icons since in vmware resolutions and in some languages
 * button-labels are not displayed(but just their icons)
 * 
 * @author Raja Rao DV
 * 
 */
@SuppressWarnings("static-access")
public class MailApp extends CommonTest {

	public static String zNewFolderOverviewPaneIcon = "id=ztih__main_Mail__FOLDER_textCell";

	// folders
	public static String zInboxFldr = "id=zti__main_Mail__2_textCell";
	public static String zSentFldr = "id=zti__main_Mail__5_textCell";
	public static String zDraftsFldr = "id=zti__main_Mail__6_textCell";
	public static String zJunkFldr = "id=zti__main_Mail__4_textCell";
	public static String zTrashFldr = "id=zti__main_Mail__3_textCell";

	public static String zInboxFldrMoveDlg = "id=zti__ZmChooseFolderDialog_Mail__2_textCell";
	public static String zSentFldrMoveDlg = "id=zti__ZmChooseFolderDialog_Mail__5_textCell";
	public static String zJunkFldrMoveDlg = "id=zti__ZmChooseFolderDialog_Mail__4_textCell";
	public static String zTrashFldrMoveDlg = "id=zti__ZmChooseFolderDialog_Mail__3_textCell";

	public static String zFoldersNewFolderDlg = "id=ztih__ZmNewFolderDialog__FOLDER_textCell";

	public static final String zNewIconBtn = "id=zb__TV__NEW_MENU_left_icon";
	public static final String zNewMenuIconBtn = "id=zb__CLV__NEW_MENU_left_icon";
	public static final String zNewMenuBtn = "id=zb__CLV__NEW_MENU";
	public static final String zNewMenuDropDown = "id=zb__CLV__NEW_MENU_dropdown";
	public static final String zGetMailIconBtn = "id=zb__CLV__CHECK_MAIL_left_icon";
	public static final String zGetMailBtn = "id=zb__CLV__CHECK_MAIL";
	public static final String zDeleteIconBtn = "id=zb__CLV__DELETE_left_icon";
	public static final String zDeleteBtn = "id=zb__CLV__DELETE";
	public static final String zMoveIconBtn = "id=zb__CLV__MOVE_left_icon";
	public static final String zMoveBtn = "id=zb__CLV__MOVE";
	public static final String zPrintIconBtn = "id=zb__CLV__PRINT_left_icon";
	public static final String zPrintBtn = "id=zb__CLV__PRINT";
	public static final String zReplyIconBtn = "id=zb__CLV__REPLY_left_icon";
	public static final String zReplyBtn = "id=zb__CLV__REPLY";
	public static final String zReplyAllIconBtn = "id=zb__CLV__REPLY_ALL_left_icon";
	public static final String zReplyAllBtn = "id=zb__CLV__REPLY_ALL";
	public static final String zForwardIconBtn = "id=zb__CLV__FORWARD_left_icon";
	public static final String zForwardBtn = "id=zb__CLV__FORWARD";
	public static final String zJunkIconBtn = "id=zb__CLV__SPAM_left_icon";
	public static final String zJunkBtn = "id=zb__CLV__SPAM";
	public static final String zTagIconBtn = "id=zb__CLV__TAG_MENU_left_icon";
	public static final String zTagBtn = "id=zb__CLV__TAG_MENU";
	public static final String zDetachIconBtn = "id=zb__TV__DETACH_left_icon";
	public static final String zDetachBtn = "id=zb__TV__DETACH";
	public static final String zDetachIconBtn2 = "id=zb__CLV__DETACH_left_icon";
	public static final String zDetachBtn2 = "id=zb__CLV__DETACH";
	public static final String zViewIconBtn = "id=zb__CLV__VIEW_MENU_left_icon";
	public static final String zViewBtn = "id=zb__CLV__VIEW_MENU";

	public static final String zCloseIconBtn_newWindow = "id=zb__MSG1__CLOSE_left_icon";
	public static final String zDeleteIconBtn_newWindow = "id=zb__MSG1__DELETE_title";
	public static final String zReplyIconBtn_newWindow = "id=zb__MSG1__REPLY_left_icon";
	public static final String zReplyAllIconBtn_newWindow = "id=zb__MSG1__REPLY_ALL_left_icon";
	public static final String zForwardIconBtn_newWindow = "id=zb__MSG1__FORWARD_left_icon";
	public static final String zJunkIconBtn_newWindow = "id=zb__MSG1__SPAM_left_icon";
	public static final String zTagIconBtn_newWindow = "id=zb__MSG1__TAG_MENU_left_icon";

	public static final String zSendBtn_newWindow = "id=zb__COMPOSE1__SEND";
	public static final String zCancelBtn_newWindow = "id=zb__COMPOSE1__CANCEL";
	public static final String zSaveDraftsBtn_newWindow = "id=zb__COMPOSE1__SAVE_DRAFT";
	public static final String zAddAttachmentBtn_newWindow = "id=zb__COMPOSE1__ATTACHMENT";
	public static final String zSpellCheckBtn_newWindow = "id=zb__COMPOSE1__SPELL_CHECK";
	public static final String zSignatureBtn_newWindow = "id=zb__COMPOSE1__ADD_SIGNATURE";
	public static final String zOptionsBtn_newWindow = "id=zb__COMPOSE1__COMPOSE_OPTIONS";

	public static final String zEditDraftIconBtn = "id=zb__CLV__EDIT_left_icon";
	public static final String zEditDraftBtn = "id=zb__CLV__EDIT";
	public static final String zMailTabIconBtn = "id=zb__App__Mail_left_icon";
	public static final String zMailViewIconBtn = "id=zb__CLV__VIEW_MENU_left_icon";
	public static final String zCancelIconBtn = "id=zb__COMPOSE1__CANCEL_left_icon";
	public static final String zTagOverViewHeader = "id=ztih__main_Mail__TAG_div";
	public static final String zSearchIconBtn = "id=zb__Search__SEARCH_left_icon";

	public static final String zSearchMenuIconBtn = "id=zmi__CLV__Par__SEARCH_left_icon";
	public static final String zAdvancedSearchMenuIconBtn = "id=zmi__CLV__Par__BROWSE_left_icon";
	public static final String zNewEmailMenuIconBtn = "id=zmi__CLV__Par__NEW_MESSAGE_left_icon";
	public static final String zAddToContactsMenuIconBtn = "id=zmi__CLV__Par__CONTACT_left_icon";
	public static final String zMarkReadMenuIconBtn = "id=zmi__CLV__MARK_READ_left_icon";
	public static final String zMarkReadMenuEnaDisaBtn = "id=zmi__CLV__MARK_READ";
	public static final String zMarkUnReadMenuIconBtn = "id=zmi__CLV__MARK_UNREAD_left_icon";
	public static final String zMarkUnReadMenuEnaDisaBtn = "id=zmi__CLV__MARK_UNREAD";
	public static final String zReplyMenuIconBtn = "id=zmi__CLV__REPLY_left_icon";
	public static final String zReplyMenuEnaDisaBtn = "id=zmi__CLV__REPLY";
	public static final String zReplyAllMenuIconBtn = "id=zmi__CLV__REPLY_ALL_left_icon";
	public static final String zReplyAllMenuEnaDisaBtn = "id=zmi__CLV__REPLY_ALL";
	public static final String zForwardMenuIconBtn = "id=zmi__CLV__FORWARD_left_icon";
	public static final String zForwardMenuEnaDisaBtn = "id=zmi__CLV__FORWARD";
	public static final String zEditAsNewMenuIconBtn = "id=zmi__CLV__EDIT_left_icon";
	public static final String zEditAsNewMenuEnaDisaBtn = "id=zmi__CLV__EDIT";
	public static final String zTagMenuIconBtn = "id=zmi__CLV__TAG_MENU_left_icon";
	public static final String zNewTagMenuIconBtn = "id=zmi__CLV__TAG_MENU|MENU|NEWTAG_title";
	public static final String zRemoveTagMenuIconBtn = "id=zmi__CLV__TAG_MENU|MENU|REMOVETAG_title";
	public static final String zDeleteMenuIconBtn = "id=zmi__CLV__DELETE_left_icon";
	public static final String zMoveMenuIconBtn = "id=zmi__CLV__MOVE_left_icon";
	public static final String zPrintMenuIconBtn = "id=zmi__CLV__PRINT_left_icon";
	public static final String zPrintMenuEnaDisaBtn = "id=zmi__CLV__PRINT";
	public static final String zJunkMenuIconBtn = "id=zmi__CLV__SPAM_left_icon";
	public static final String zShowOriginalMenuIconBtn = "id=zmi__CLV__SHOW_ORIG_left_icon";
	public static final String zShowOriginalMenuEnaDisaBtn = "id=zmi__CLV__SHOW_ORIG";
	public static final String zNewFilterMenuIconBtn = "id=zmi__CLV__ADD_FILTER_RULE_left_icon";
	public static final String zNewFilterMenuEnaDisaBtn = "id=zmi__CLV__ADD_FILTER_RULE";
	public static final String zCreateApptMenuIconBtn = "id=zmi__CLV__CREATE_APPT_left_icon";
	public static final String zCreateApptEnaDisaBtn = "id=zmi__CLV__CREATE_APPT";
	public static final String zCreateTaskMenuEnaDisaBtn = "id=zmi__CLV__CREATE_TASK_left_icon";
	public static final String zCreateTaskEnaDisaBtn = "id=zmi__CLV__CREATE_TASK";

	public static String zGeneralPrefFolder = "id=zti__main_Options__PREF_PAGE_GENERAL_textCell";
	public static String zMailPrefFolder = "id=zti__main_Options__PREF_PAGE_MAIL_textCell";
	public static String zComposingPrefFolder = "id=zti__main_Options__PREF_PAGE_COMPOSING_textCell";
	public static String zSignaturesPrefFolder = "id=zti__main_Options__PREF_PAGE_SIGNATURES_textCell";
	public static String zAccountsPrefFolder = "id=zti__main_Options__PREF_PAGE_ACCOUNTS_textCell";
	public static String zFiltersPrefFolder = "id=zti__main_Options__PREF_PAGE_FILTERS_textCell";

	public static String zAddressBookPrefFolder = "id=zti__main_Options__PREF_PAGE_CONTACTS_textCell";
	public static String zCalendarPrefFolder = "id=zti__main_Options__PREF_PAGE_CALENDAR_textCell";
	public static String zSharingPrefFolder = "id=zti__main_Options__PREF_PAGE_SHARING_textCell";
	public static String zImportExportPrefFolder = "id=zti__main_Options__PREF_PAGE_IMPORT_EXPORT_textCell";
	public static String zShortcutsPrefFolder = "id=zti__main_Options__PREF_PAGE_SHORTCUTS_textCell";
	public static String zZimletsPrefFolder = "id=zti__main_Options__PREF_PAGE_PREF_ZIMLETS_textCell";
	public static final String zShowOriginalDraftMenuIconBtn = "id=zmi__CLV__Dra__SHOW_ORIG_left_icon";

	public static final String zPreferencesTabIconBtn = "id=zb__App__Options_left_icon";
	public static final String zPreferencesMailIconBtn = "id=ztab__PREF__"
			+ localize(locator.mail) + "_title";

	/**
	 * Clicks Get Mail button for up to 60seconds or until mail with subject
	 * appears.
	 * 
	 * @param mailSubject
	 *            mail's subject that needs to be selected
	 * @throws Exception
	 */
	public static void ClickCheckMailUntilMailShowsUp(String mailSubject)
			throws Exception {
		ClickCheckMailUntilMailShowsUp("", mailSubject);
	}

	/**
	 * Clicks Get Mail button for up to 60seconds or until mail with subject
	 * appears
	 * 
	 * @param mailSubject
	 *            mail's subject that needs to be selected
	 * @throws Exception
	 */
	public static void ClickCheckMailUntilMailShowsUp(String folderName,
			String mailSubject) throws Exception {
		boolean found = false;
		for (int i = 0; i <= 15; i++) {
			obj.zButton.zClick(zGetMailIconBtn);
			Thread.sleep(1000);
			if (!folderName.equals("")) {
				obj.zFolder.zClick(folderName);
				Thread.sleep(1000);
			}
			String rc = obj.zMessageItem.zExistsDontWait(mailSubject);
			if (rc.equals("false")) {
				obj.zFolder.zClick(localize(locator.junk));
				Thread.sleep(1000);
				rc = obj.zMessageItem.zExistsDontWait(mailSubject);
				if (rc.equals("false")) {
					Thread.sleep(1000);
				} else {
					found = true;
					Thread.sleep(1000);
					break;
				}
			} else {
				found = true;
				Thread.sleep(1000);
				break;
			}
		}
		if (!found) {
			if (folderName == "")
				folderName = "Inbox";
			Assert.fail("Mail(" + mailSubject + ") didn't appear even after "
					+ 30 + " seconds in (" + folderName + ")");
		}
	}

	public static void ClickLoadFeedUntilFeedShowsUp(String feedFolderName,
			String feedSubject) throws Exception {
		ClickCheckMailUntilMailShowsUp(feedFolderName, feedSubject);
	}

	public static void zVerifyMailNotExists(String folderName,
			String mailSubject) throws Exception {
		Thread.sleep(1500); // required otherwise it will fail
		int i = 0;
		boolean found = false;
		for (i = 0; i < 5; i++) {
			obj.zButton.zClick(zGetMailIconBtn);
			if (!folderName.equals("")) {
				obj.zFolder.zClick(folderName);
			}
			String rc = obj.zMessageItem.zNotExistsDontWait(mailSubject);
			if (rc.equals("false")) {
				Thread.sleep(500);
			} else {
				found = true;
				break;
			}
		}
		if (found)
			Assert.fail("Mail(" + mailSubject + ") appeared after " + 30
					+ " seconds in (" + folderName + ")");
	}

	/**
	 * Zimbra doesnt show underscores, so we replace _ with " "and simply return
	 * the mail-name
	 * 
	 * @param actualEmail
	 * @return en us 123213 if "en_us_123213@test.com" was passed
	 */
	public static String zGetNameFromEmail(String actualEmail) {
		try {
			return actualEmail.substring(0, (actualEmail.indexOf("@") - 1))
					.replace("_", " ");
		} catch (Exception e) {
			return actualEmail;// resurn the same email upon expn
		}
	}

	/**
	 * This method creates folder - only first argument value is necessary
	 */
	/**
	 * @param folderName
	 *            - specify folder name in data provider according to test
	 * @param renameFolderName
	 *            - this argument used only when you are renaming folder
	 * @param errDlgName
	 *            - error dialog name specified in data provider according to
	 *            test (for e.g. critical)
	 * @param errMsg
	 *            - error message specified in data provider according to test
	 */

	public void zCreateFolder(String folderName, String renameFolderName,
			String errDlgName, String errMsg) throws Exception {
		zWaitTillObjectExist("button",
				replaceUserNameInStaticId(zNewFolderOverviewPaneIcon));
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(zNewFolderOverviewPaneIcon));
		obj.zMenuItem.zClick(localize(locator.newFolder));
		obj.zDialog.zExists(localize(locator.createNewFolder));
		obj.zEditField.zTypeInDlg(localize(locator.nameLabel), folderName);
		obj.zFolder.zClickInDlg(zFoldersNewFolderDlg);
		obj.zButton.zClickInDlg(localize(locator.ok));
	}

	public void zCreateFolder(String folderName) throws Exception {
		zWaitTillObjectExist("button",
				replaceUserNameInStaticId(zNewFolderOverviewPaneIcon));
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(zNewFolderOverviewPaneIcon));
		obj.zMenuItem.zClick(localize(locator.newFolder));
		obj.zDialog.zExists(localize(locator.createNewFolder));
		obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel),
				folderName, localize(locator.createNewFolder));
		obj.zFolder.zClickInDlgByName(zFoldersNewFolderDlg,
				localize(locator.createNewFolder));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewFolder));
		zWaitTillObjectExist("folder", folderName);
	}

	public void zCreateFolder(String folderName, String subFolder)
			throws Exception {
		zWaitTillObjectExist("button",
				replaceUserNameInStaticId(zNewFolderOverviewPaneIcon));
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(zNewFolderOverviewPaneIcon));
		obj.zMenuItem.zClick(localize(locator.newFolder));
		obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel),
				folderName, localize(locator.createNewFolder));
		obj.zFolder.zClickInDlgByName(subFolder,
				localize(locator.createNewFolder));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewFolder));
		zWaitTillObjectExist("folder", folderName);
	}

	public void zDeleteFolder(String folderName) throws Exception {
		obj.zFolder.zRtClick(folderName);
		obj.zMenuItem.zClick(localize(locator.del));
		Thread.sleep(2000); // deletion takes sometime
	}

	public void zMoveFolder(String folderName) throws Exception {
		obj.zFolder.zClick(folderName);
		// Waiting for some core function for actual position of folder
	}

	public void zVerifyFolderEmpty(String folderName) throws Exception {
		obj.zFolder.zClick(folderName);
		// Waiting for some core function for actual position of folder
	}

	public void zCreateRssFeedFolder(String rssFeedFolderName, String rssFeedURL)
			throws Exception {
		zCreateRssFeedFolderCore(rssFeedFolderName, rssFeedURL);
		Thread.sleep(2000);
		boolean dialogfound = false;
		String dialogexistFlag = obj.zDialog
				.zExistsDontWait(localize(locator.criticalMsg));
		for (int i = 1; i <= 3; i++) {
			if (dialogexistFlag.equals("true")) {
				obj.zButton.zClickInDlgByName(localize(locator.ok),
						localize(locator.criticalMsg));
				zCreateRssFeedFolderCore(rssFeedFolderName, rssFeedURL);
			} else {
				dialogfound = true;
				break;
			}
		}

		if (!dialogfound)
			Assert.fail("Rss feed URL is not reachable(" + rssFeedURL + ")");
		boolean found = false;
		String existFlag = obj.zFolder.zExistsDontWait(rssFeedFolderName);
		for (int i = 1; i <= 30; i++) {
			if (existFlag.equals("false")) {
				Thread.sleep(1000); // takes some time to load feed
			} else {
				found = true;
				break;
			}
		}

		if (!found)
			Assert.fail("Rss feed folder(" + rssFeedFolderName
					+ ") didn't appear even after " + 30 + " seconds");
	}

	private void zCreateRssFeedFolderCore(String rssFeedFolderName,
			String rssFeedURL) throws Exception {
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(zNewFolderOverviewPaneIcon));
		obj.zMenuItem.zClick(localize(locator.newFolder));
		obj.zDialog.zExists(localize(locator.createNewFolder));
		obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel),
				rssFeedFolderName, localize(locator.createNewFolder));
		obj.zCheckbox.zClickInDlgByName(localize(locator.subscribeToFeed),
				localize(locator.createNewFolder));
		obj.zEditField.zTypeInDlgByName(localize(locator.urlLabel), rssFeedURL,
				localize(locator.createNewFolder));
		obj.zFolder.zClickInDlgByName(localize(locator.folders),
				localize(locator.createNewFolder));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewFolder));
	}

	public static String zInjectMessage(String fileName) throws Exception {
		String subject = null;
		Boolean foundFlag = false;
		File dir = new File("src/java/projects/zcs/data/lmtpInject");
		StringBuffer contents = new StringBuffer();
		File file = new File(dir.getAbsolutePath() + "/" + fileName + ".txt");
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(file));
		String text = null;
		while ((text = reader.readLine()) != null) {
			contents.append(text).append(System.getProperty("line.separator"));
			if (text.contains("Subject:") && foundFlag == false) {
				if (text.length() >= 19) {
					subject = text.substring(9, 19).trim();
					foundFlag = true;
				} else {
					subject = text.substring(9).trim();
					foundFlag = true;
				}
			}
		}
		String[] accounts = { SelNGBase.selfAccountName };
		ProvZCS.addMessageLmtp(accounts, SelNGBase.selfAccountName, contents
				.toString());
		zGoToApplication("Mail");
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);

		foundFlag = false;
		return subject;
	}

	/**
	 * Navigates to MailApp
	 */
	public static void zNavigateToMailApp() throws Exception {
		zGoToApplication("Mail");
	}

	public void zVerifyMailContentContains(String contentToVerify)
			throws Exception {
		Thread.sleep(500);
		String msgBody;
		msgBody = obj.zMessageItem.zGetCurrentMsgBodyText();
		Assert.assertTrue(msgBody.indexOf(contentToVerify) >= 0,
				contentToVerify
						+ " is not present in the mail body. Mail body is__:: "
						+ msgBody);
	}

	public void zNavigateToMailPreferences() throws Exception {
		zGoToApplication("Preferences");
		zGoToPreferences("Mail");
	}

	public void zNavigateToComposingPreferences() throws Exception {
		zGoToApplication("Preferences");
		zGoToPreferences("Composing");
	}
}
