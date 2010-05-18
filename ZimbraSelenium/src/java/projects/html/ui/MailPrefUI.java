package projects.html.ui;

import org.testng.Assert;

import framework.core.SelNGBase;
import projects.html.clients.ProvZCS;
import projects.html.tests.CommonTest;

/**
 * This Class have UI-level methods related to preference-Mail tab
 * 
 * @author Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class MailPrefUI extends CommonTest {
	public static final String zFindEditFiled = "id=searchField";
	public static final String zSearchBtn = "name=search";

	// mail tab related id's
	public static final String zEmailsPerPageDrpDwn = "id=itemsPP";
	public static final String zGroupMailByDrpDwn = "id=groupMailBy";
	public static final String zDisplayAsHtmlRadioBtn = "id=viewHtml";
	public static final String zDisplayAsTextRadioBtn = "id=viewText";

	public static final String zDsplyTxtOfMsgInEmailListChkBox = "id=zimbraPrefShowFragments";
	public static final String zDefaultMailSearchEditField = "id=zimbraPrefMailInitialSearch";

	public static final String zFwdCopyToChkBox = "id=FORWARDCHECKED";
	public static final String zFwdingAddEditField = "id=zimbraPrefMailForwardingAddress";
	public static final String zDontKeepLocalCopyChkBox = "id=zimbraPrefMailLocalDeliveryDisabled";

	public static final String zSendANotificationMsgToChkBox = "id=zimbraPrefNewMailNotificationEnabled";
	public static final String zNotificationAddEditField = "id=zimbraPrefNewMailNotificationAddress";

	public static final String zSendASendAutoReplyMsgChkBox = "id=zimbraPrefOutOfOfficeReplyEnabled";
	public static final String zAutoReplyMsgEditField = "id=zimbraPrefOutOfOfficeReply";
	public static final String zStartDateEditField = "id=zimbraPrefOutOfOfficeFromDate";
	public static final String zEndDateEditField = "id=zimbraPrefOutOfOfficeUntilDate";

	public static final String zPlaceInInboxRadioBtn = "id=dedupeNone";
	public static final String zPlaceInInboxIfInToOrCCRadioBtn = "id=secondCopy";
	public static final String zIgnoreMsgRadioBtn = "id=dedupeall";

	public static final String zAllowAllMailRadioBtn = "id=pop3DownloadAll";
	public static final String zAllowOnlyMailFromNowRadioBtn = "id=pop3DownloadFromNow";

	// to navigate to preference mail
	public static void zNavigateToPrefMail() throws Exception {
		obj.zButton.zClick("id=TAB_OPTIONS");
		// obj.zTab.zClick(localize(locator.preferences));
		Thread.sleep(2000);
		if (config.getString("locale").equals("zh_CN")
				|| config.getString("locale").equals("de")
				|| config.getString("locale").equals("ko")) {
			obj.zTab.zClick(localize(locator.mail), "3");
		} else {
			obj.zTab.zClick(localize(locator.mail), "2");
		}
		Thread.sleep(2000);
	}

	/**
	 * To navigate to pref mail and select no of display emails per page
	 * 
	 * @param noOfEmailsToBeDisplayed
	 * @throws Exception
	 */
	public static void zNavigatePrefMailAndSelectEmailsPerPage(
			String noOfEmailsToBeDisplayed) throws Exception {
		zNavigateToPrefMail();
		obj.zHtmlMenu.zClick(page.zMailPrefUI.zEmailsPerPageDrpDwn,
				noOfEmailsToBeDisplayed);

		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
		Thread.sleep(2000);

	}

	/**
	 * To navigate ti pref mail and to select default mail search
	 * 
	 * @param newSearchFolder
	 * @throws Exception
	 */
	public static void zNavigateToPrefAndEditDefaultMailSearch(
			String newSearchFolder) throws Exception {

		zNavigateToPrefMail();
		obj.zEditField.zType(page.zMailPrefUI.zDefaultMailSearchEditField,
				newSearchFolder);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
	}

	/**
	 * To navigate to mail pref and to select fwd a copy to option
	 * 
	 * @param fwdToAcc
	 * @throws Exception
	 */
	public static void zNavigateToPrefMailAndSetFwdCopyTo(String fwdToAcc)
			throws Exception {
		zNavigateToPrefMail();
		obj.zCheckbox.zClick(zFwdCopyToChkBox);
		obj.zEditField.zType(zFwdingAddEditField, fwdToAcc);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
	}

	/**
	 * To navigate to pref mail and set notification msg specific address to
	 * 
	 * @param notificationToAcc
	 * @throws Exception
	 */
	public static void zNavigateToPrefMailAndSetSendNotificationMsgTo(
			String notificationToAcc) throws Exception {
		zNavigateToPrefMail();
		obj.zCheckbox.zClick(zSendANotificationMsgToChkBox);
		obj.zEditField.zType(zNotificationAddEditField, notificationToAcc);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
	}

	/**
	 * To navigate to pref mail and to set auto reply
	 * 
	 * @param autoReplyMsg
	 * @throws Exception
	 */
	public static void zNavigateToPrefMailAndSetAutoReply(String autoReplyMsg)
			throws Exception {
		zNavigateToPrefMail();
		obj.zCheckbox.zClick(zSendASendAutoReplyMsgChkBox);
		obj.zEditField.zType(zAutoReplyMsgEditField, autoReplyMsg);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
	}

	/**
	 * To navigate to pref mail and to select what to do when messsage is sent
	 * from self
	 * 
	 * @param whereToPlace
	 * @throws Exception
	 */
	public static void zNavigateToPrefMailAndSelectMsgFrmMe(String whereToPlace)
			throws Exception {
		page.zMailPrefUI.zNavigateToPrefMail();
		if (whereToPlace.equals("PlaceInInbox"))
			obj.zRadioBtn.zClick(zPlaceInInboxRadioBtn);
		else if (whereToPlace.equals("PlaceInInboxIfInToOrCc"))
			obj.zRadioBtn.zClick(zPlaceInInboxIfInToOrCCRadioBtn);
		else if (whereToPlace.equals("IgnoreMsg"))
			obj.zRadioBtn.zClick(zIgnoreMsgRadioBtn);
		Thread.sleep(500);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
		Thread.sleep(2000);
	}

	/**
	 * To inject specific message using lmtp inject
	 * 
	 * @param mailSubject
	 * @param numberOfMails
	 * @throws Exception
	 */
	public static void zInjectSpecificNoOfMails(String[] mailSubject,
			int numberOfMails) throws Exception {
		String mailBody = getLocalizedData_NoSpecialChar();
		String to = SelNGBase.selfAccountName;
		String[] recipients = { to };
		String sender = ProvZCS.getRandomAccount();

		for (int i = 0; i < numberOfMails; i++) {

			ProvZCS.injectMessage(sender, recipients, ProvZCS
					.getRandomAccount(), mailSubject[i], mailBody);
		}

	}

	/*
	 * To send mail to self and move it to junk folder
	 * 
	 * @param subject
	 * 
	 * @throws Exception
	 */
	public static void zSendMailToSelfAndMoveItToJunkAndVerify(String subject)
			throws Exception {
		String selfAccName = SelNGBase.selfAccountName;

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt(selfAccName, "", "",
				subject, "", "");
		obj.zCheckbox.zClick(subject);
		Thread.sleep(1000);
		page.zMailApp.zMoreActions(localize(locator.actionSpam));
		obj.zFolder.zClick(page.zMailApp.zJunkFldr);
		Thread.sleep(1000);
		obj.zMessageItem.zExists(subject);

	}

	/**
	 * To verify auto reply functionality
	 * 
	 * @param sentMsgSubject
	 * @param autoReplyMsg
	 * @param accWhereAutoReplyIsSet
	 * @throws Exception
	 */
	public static void zVerifyAutoReplyMsg(String sentMsgSubject,
			String autoReplyMsg, String accWhereAutoReplyIsSet)
			throws Exception {
		String replyMsgSubject = "Re: " + sentMsgSubject;
		page.zMailApp.zClickCheckMailUntilMailShowsUp(replyMsgSubject);
		obj.zMessageItem.zClick(replyMsgSubject);
		String replyMailBody = obj.zMessageItem.zGetCurrentMsgBodyText();
		Assert.assertTrue(replyMailBody.contains(autoReplyMsg),
				"The auto reply msg " + autoReplyMsg + " set at user "
						+ accWhereAutoReplyIsSet
						+ " is not present in auto reply email");

	}
}