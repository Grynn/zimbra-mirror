package projects.zcs.ui;

import org.testng.Assert;

import framework.core.SelNGBase;
import framework.util.ZimbraSeleniumProperties;

/**
 * @author Jitesh Sojitra
 * 
 *         This class contains sharing functionality related methods (wrappers).
 *         For e.g create share, modify share, verify share related mails & its
 *         body and other so many useful methods
 * 
 */
@SuppressWarnings("static-access")
public class Sharing extends AppPage {
	public static final String zAcceptShareIconBtn = "css=#zv__CLV__MSG #zb__CLV__Shr__SHARE_ACCEPT_left_icon";
	public static final String zDeclineShareIconBtn = "css=#zv__CLV__MSG #zb__CLV__Shr__SHARE_DECLINE_left_icon";

	public void zShareFolder(String applicationtab, String sharingfoldername,
			String sharetype, String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt)
			throws Exception {
		zOpenFolderShareDialog(applicationtab, sharingfoldername);
		zEnterValuesInShareDialog(sharetype, invitedusers, role, message,
				sharingnoteifany, allowtoseeprivateappt);
	}

	public void zModifySharedFolder(String applicationtab,
			String sharingfoldername, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt)
			throws Exception {
		zEditFolderShareDialog(applicationtab, sharingfoldername);

		/*
		 * not sure but disable - enable stuff doesn't works (following object
		 * should remain disabled while edit share) /
		 * obj.zRadioBtn.zIsDisabled(localize(locator.shareWithUserOrGroup));
		 * obj.zRadioBtn.zIsDisabled(localize(locator.shareWithGuest));
		 * obj.zRadioBtn.zIsDisabled(localize(locator.shareWithPublic)); String
		 * emailLabel = ""; if (ZimbraSeleniumProperties.getStringProperty("locale").equals("fr") &&
		 * ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) emailLabel =
		 * localize(locator.email); else emailLabel =
		 * localize(locator.emailLabel); obj.zEditField.zIsDisabled(emailLabel);
		 */
		zEnterValuesInShareDialog("", "", role, message, sharingnoteifany,
				allowtoseeprivateappt);
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.folderProperties));
	}

	public void zOpenFolderShareDialog(String applicationtab,
			String sharingfoldername) throws Exception {
		if (sharingfoldername.equals(localize(locator.inbox))) {
			sharingfoldername = page.zMailApp.zInboxFldr;
		} else if (sharingfoldername.equals(localize(locator.sent))) {
			sharingfoldername = page.zMailApp.zSentFldr;
		}
		obj.zFolder.zRtClick(sharingfoldername);

		String lCaseapplicationtab = applicationtab.toLowerCase();
		if ((lCaseapplicationtab.equals("mail"))
				|| (applicationtab.equals(localize(locator.mail)))) {
			obj.zMenuItem.zClick(localize(locator.shareFolder));
		} else if ((lCaseapplicationtab.equals("address book"))
				|| (applicationtab.equals(localize(locator.addressBook)))) {
			obj.zMenuItem.zClick(localize(locator.shareAddrBook));
		} else if ((lCaseapplicationtab.equals("calendar"))
				|| (applicationtab.equals(localize(locator.calendar)))) {
			obj.zMenuItem.zClick(localize(locator.shareCalendar));
		} else if ((lCaseapplicationtab.equals("tasks"))
				|| (applicationtab.equals(localize(locator.tasks)))) {
			obj.zMenuItem.zClick(localize(locator.shareTaskFolder));
		} else if ((lCaseapplicationtab.equals("documents"))
				|| (applicationtab.equals(localize(locator.documents)))) {
			obj.zMenuItem.zClick(localize(locator.shareNotebook));
		} else if ((lCaseapplicationtab.equals("briefcase"))
				|| (applicationtab.equals(localize(locator.briefcase)))) {
			obj.zMenuItem.zClick(localize(locator.shareFolder));
		}
	}

	public void zEditFolderShareDialog(String applicationtab,
			String sharingfoldername) throws Exception {
		/* Right click to folder - edit properties */
		obj.zFolder.zRtClick(sharingfoldername);
		obj.zMenuItem.zClick(localize(locator.editProperties));
		zWaitTillObjectExist("dialog", localize(locator.folderProperties));
		if (selenium.isElementPresent("link=" + localize(locator.edit)))
			selenium.click("link=" + localize(locator.edit));
	}

	public void zEnterValuesInShareDialog(String sharetype,
			String invitedusers, String role, String message,
			String sharingnoteifany, String allowtoseeprivateappt)
			throws Exception {

		/*
		 * Share type is either /shareWithGuest/shareWithPublic);
		 */
		if (!sharetype.equals("")) {
			obj.zRadioBtn.zClickInDlgByName(sharetype,
					localize(locator.shareProperties));
		}

		/*
		 * Invited users (shared user) - used if condition only for edit share
		 * folder purpose because we don't have to write any other public method
		 * only for modify share
		 */
		if (!invitedusers.equals("")
				&& !sharetype.equals(localize(locator.shareWithPublicLong))) {
			String emailLabel = "";
			if (ZimbraSeleniumProperties.getStringProperty("locale").equals("fr")
					&& ZimbraSeleniumProperties.getStringProperty("browser").equals("IE"))
				emailLabel = localize(locator.email);
			else
				emailLabel = localize(locator.emailLabel);

			if (sharetype.equals(localize(locator.shareWithGuest))
					&& ZimbraSeleniumProperties.getStringProperty("locale").equals("fr")
					&& ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
				obj.zRadioBtn.zClickInDlgByName(
						localize(locator.shareWithUserOrGroup),
						localize(locator.shareProperties));
				obj.zEditField.zTypeInDlgByName(emailLabel, invitedusers,
						localize(locator.shareProperties));
				obj.zRadioBtn.zClickInDlgByName(sharetype,
						localize(locator.shareProperties));
			} else {
				obj.zEditField.zTypeInDlgByName(emailLabel, invitedusers,
						localize(locator.shareProperties));
			}
		}

		if (((sharetype.equals(""))
				&& (!sharetype.equals(localize(locator.shareWithGuest))) && (!sharetype
				.equals(localize(locator.shareWithPublic))))
				|| (sharetype.equals(localize(locator.shareWithUserOrGroup)))) {
			/*
			 * Specify role - either as Manager, Viewer, Admin or None if you
			 * are using labelStartsWith, make sure to set it to false
			 * immediately after using that
			 */

			/*
			 * if we will not select any role then OK button of share properties
			 * dialog doesn't get enables - so we must have to select role (by
			 * default select viewer role if "")
			 */

			SelNGBase.labelStartsWith = true;
			if (role.equals("")) {
				obj.zRadioBtn.zClickInDlgByName(
						localize(locator.shareRoleViewer),
						localize(locator.shareProperties));
			} else {
				obj.zRadioBtn.zClickInDlgByName(role,
						localize(locator.shareProperties));
			}
			SelNGBase.labelStartsWith = false;
		} else if ((!sharetype.equals(""))
				&& (!sharetype.equals(localize(locator.shareWithUserOrGroup)))
				&& (sharetype.equals(localize(locator.shareWithGuest)))
				&& (!sharetype.equals(localize(locator.shareWithPublic)))) {
			
			if(ZimbraSeleniumProperties.getStringProperty("locale").equals("es")){
				selenium.type("xpath=//div[contains(@id,'DWT')]/div[contains(@id,'DWT') and contains(@class,'DwtInputField')]/input", "test123");
				
			}else{
			obj.zEditField.zTypeInDlgByName(localize(locator.passwordLabel),
					"test123", localize(locator.shareProperties));
			}

			// Work around to enable OK button in share properties dialog
			obj.zRadioBtn.zClickInDlgByName(sharetype,
					localize(locator.shareProperties));
		}

		if (!sharetype.equals(localize(locator.shareWithPublic))) {
			if (allowtoseeprivateappt
					.equals(localize(locator.privatePermission))) {
				SelNGBase.labelStartsWith = true;
				obj.zCheckbox.zClickInDlgByName(
						localize(locator.privatePermission),
						localize(locator.shareProperties));
				SelNGBase.labelStartsWith = false;
			}
			zShareMessageAction(message, sharingnoteifany,
					localize(locator.shareProperties), localize(locator.ok));
		}
	}

	/*
	 * This is private method used while you create share, revoke, edit the
	 * share & you want to send mail, add note OR compose new mail
	 */
	private void zShareMessageAction(String message, String sharingnoteifany,
			String currentDialog, String actionButtonName) throws Exception {
		String Selectedmessage;
		if (currentDialog.equals(localize(locator.acceptShare))) {
			Selectedmessage = localize(locator.sendNoMailAboutShare);
		} else {
			Selectedmessage = localize(locator.sendStandardMailAboutShare);
		}

		if (!message.equals(localize(locator.sendComposedMailAboutShare))) {
			if (!message.equals("")) {
				obj.zButton.zClickInDlgByName(Selectedmessage, currentDialog);
				obj.zMenuItem.zClick(message);
				if (message
						.equals(localize(locator.sendStandardMailAboutSharePlusNote))) {
					/*
					 * get the first word before : of sendMailAboutShareNote
					 * locator
					 */

					if (ZimbraSeleniumProperties.getStringProperty("locale").equals("fr")
							&& ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
						obj.zTextAreaField
								.zTypeInDlgByName(
										localize(locator.sendStandardMailAboutSharePlusNote),
										sharingnoteifany, currentDialog);
					} else {
						String noteLabel = localize(
								locator.sendMailAboutShareNote).split(":")[0];
						obj.zTextAreaField.zTypeInDlgByName(noteLabel,
								sharingnoteifany, currentDialog);
					}
				}
			}
			obj.zButton.zClickInDlgByName(actionButtonName, currentDialog);
			obj.zDialog.zNotExists(currentDialog);
			if (message.equals("")
					&& Selectedmessage
							.equals(localize(locator.sendNoMailAboutShare))) {
				// don't wait
			} else if (message.equals("")
					&& Selectedmessage
							.equals(localize(locator.sendStandardMailAboutShare))
					|| !message.equals(localize(locator.sendNoMailAboutShare))) {
				Thread.sleep(2000);
				/*
				 * some time if you have selected to send a mail regarding share
				 * and killing browser then it immediately kills browser without
				 * sending mail and test fails
				 */
			}
		} else if (message.equals(localize(locator.sendComposedMailAboutShare))) {
			obj.zButton.zClickInDlgByName(Selectedmessage, currentDialog);
			obj.zMenuItem.zClick(message);
			obj.zButton.zClickInDlgByName(actionButtonName, currentDialog);
			obj.zDialog.zNotExists(currentDialog);
			selenium.selectWindow("_blank");
			obj.zButton.zClick(localize(locator.send));
			Thread.sleep(2000);
			/*
			 * some time if you have selected to send a mail regarding share and
			 * killing browser then it immediately kills browser without sending
			 * mail and test fails
			 */
		}
	}

	/*
	 * These methods helps to verify share accepted or declined mail in Inbox
	 * folder
	 */
	public void zVerifyShareCreatedMailInInboxFolder(
			String currentloggedinuser, String sharingfoldername,
			String sharetype, String invitedusers, String role,
			String sharingnoteifany) throws Exception {
		zVerifyShareAcceptDeclineCoreInInboxFolder(
				localize(locator.shareCreatedSubject), currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, role,
				sharingnoteifany);
	}

	public void zVerifyShareDeclinedMailInInboxFolder(
			String currentloggedinuser, String sharingfoldername,
			String sharetype, String invitedusers, String role,
			String sharingnoteifany) throws Exception {
		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.shareDeclinedSubject));
		obj.zMessageItem.zClick(localize(locator.shareDeclinedSubject));
		zVerifyFolderSharingMailBody(localize(locator.shareDeclinedSubject),
				currentloggedinuser, sharingfoldername, sharetype,
				invitedusers, role, sharingnoteifany);
	}

	private void zVerifyShareAcceptDeclineCoreInInboxFolder(String messagetype,
			String currentloggedinuser, String sharingfoldername,
			String sharetype, String invitedusers, String role,
			String sharingnoteifany) throws Exception {
		MailApp.ClickCheckMailUntilMailShowsUp(messagetype);
		/* obj.zMessageItem.zVerifyIsUnRead(messagetype); - fails in IE */
		obj.zMessageItem.zClick(messagetype);
		obj.zButton.zExists(localize(locator.acceptShare));
		obj.zButton.zExists(localize(locator.declineShare));
		zVerifyFolderSharingMailBody(messagetype, currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, role,
				sharingnoteifany);
	}

	public void zVerifyShareCreatedMailInSentFolder(String currentloggedinuser,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String sharingnoteifany) throws Exception {
		zVerifyShareAcceptDeclineCoreInSentFolder(
				localize(locator.shareCreatedSubject), currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, role,
				sharingnoteifany);
	}

	public void zVerifyShareDeclinedMailInSentFolder(
			String currentloggedinuser, String sharingfoldername,
			String sharetype, String invitedusers, String role,
			String sharingnoteifany) throws Exception {
		zVerifyShareAcceptDeclineCoreInSentFolder(
				localize(locator.shareDeclinedSubject), currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, role,
				sharingnoteifany);
	}

	/*
	 * Core private function to verify Accept/Decline mail, don't call this
	 * function directly
	 */
	private void zVerifyShareAcceptDeclineCoreInSentFolder(String messagetype,
			String currentloggedinuser, String sharingfoldername,
			String sharetype, String invitedusers, String role,
			String sharingnoteifany) throws Exception {
		MailApp.ClickCheckMailUntilMailShowsUp(localize(locator.sent),
				messagetype);
		obj.zMessageItem.zVerifyIsRead(messagetype);
		zVerifyFolderSharingMailBody(messagetype, currentloggedinuser,
				sharingfoldername, sharetype, invitedusers, role,
				sharingnoteifany);
	}

	/* These methods helps to verify share accepted */

	public void zVerifyShareAcceptedMail(String currentloggedinuser,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String sharingnoteifany) throws Exception {
		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.shareAcceptedSubject));
		obj.zMessageItem.zClick(localize(locator.shareAcceptedSubject));
		zVerifyFolderSharingMailBody(localize(locator.shareAcceptedSubject),
				currentloggedinuser, sharingfoldername, sharetype,
				invitedusers, role, sharingnoteifany);
	}

	public void zVerifyShareRevokedMail(String currentloggedinuser,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String sharingnoteifany) throws Exception {
		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.shareRevokedSubject));
		obj.zMessageItem.zClick(localize(locator.shareRevokedSubject));
		zVerifyFolderSharingMailBody(localize(locator.shareRevokedSubject),
				currentloggedinuser, sharingfoldername, sharetype,
				invitedusers, role, sharingnoteifany);
	}

	public void zVerifyShareModifiedMail(String currentloggedinuser,
			String sharingfoldername, String sharetype, String invitedusers,
			String role, String sharingnoteifany) throws Exception {
		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.shareModifiedSubject));
		obj.zMessageItem.zClick(localize(locator.shareModifiedSubject));
		zVerifyFolderSharingMailBody(localize(locator.shareModifiedSubject),
				currentloggedinuser, sharingfoldername, sharetype,
				invitedusers, role, sharingnoteifany);
	}

	/* This method verifies sharing mail body */
	public void zVerifyFolderSharingMailBody(String messagetype,
			String currentloggedinuser, String sharingfoldername,
			String sharetype, String invitedusers, String role,
			String sharingnoteifany) throws Exception {
		obj.zMessageItem.zClick(messagetype);
		Thread.sleep(1500);
		/*
		 * sometime message body doesn't returns correct value if there is no
		 * sleep
		 */
		String lCaseBodyTxt = obj.zMessageItem.zGetCurrentMsgBodyText()
				.toLowerCase();
		String bodyTxt = obj.zMessageItem.zGetCurrentMsgBodyText();

		sharingfoldername = sharingfoldername.toLowerCase();
		currentloggedinuser = currentloggedinuser.toLowerCase();
		invitedusers = invitedusers.toLowerCase();
		role = role.toLowerCase();
		sharingnoteifany = sharingnoteifany.toLowerCase();
		Assert.assertTrue(lCaseBodyTxt.indexOf(sharingfoldername) >= 0,
				"Shared folder name text mismatched"
						+ page.zComposeView.formatExpActValues(lCaseBodyTxt,
								sharingfoldername));
		Assert.assertTrue(lCaseBodyTxt.indexOf(currentloggedinuser) >= 0,
				"Owner emaid id text mismatched"
						+ page.zComposeView.formatExpActValues(lCaseBodyTxt,
								currentloggedinuser));
		Assert.assertTrue(lCaseBodyTxt.indexOf(invitedusers) >= 0,
				"Invited emaid id text mismatched"
						+ page.zComposeView.formatExpActValues(lCaseBodyTxt,
								invitedusers));
		if (!messagetype.equals(localize(locator.shareRevokedSubject))) {
			Assert.assertTrue(lCaseBodyTxt.indexOf(role) >= 0,
					"Role text mismatched"
							+ page.zComposeView.formatExpActValues(
									lCaseBodyTxt, role));
		}
		if (!sharingnoteifany.equals("")) {
			Assert.assertTrue(lCaseBodyTxt.indexOf(sharingnoteifany) >= 0,
					"Sharing note text mismatched"
							+ page.zComposeView.formatExpActValues(
									lCaseBodyTxt, sharingnoteifany));
		}
		if (sharetype.equals(localize(locator.shareWithGuest))) {
			Assert.assertTrue(lCaseBodyTxt.indexOf("url") >= 0,
					"URL text mismatched"
							+ page.zComposeView.formatExpActValues(
									lCaseBodyTxt, "URL"));
			String folderSharedURL = "http://" + ZimbraSeleniumProperties.getStringProperty("server")
					+ ":80/home/" + currentloggedinuser + "/";
			/*
			 * String folderSharedURL = "http://" + ZimbraSeleniumProperties.getStringProperty("server") +
			 * ":80/home/" + currentloggedinuser + "/" + sharingfoldername;
			 */

			Assert.assertTrue(lCaseBodyTxt.indexOf(folderSharedURL) >= 0,
					"URL mismatched"
							+ page.zComposeView.formatExpActValues(
									lCaseBodyTxt, folderSharedURL));

			if (!ZimbraSeleniumProperties.getStringProperty("locale").equals("ru")) {
				String username = localize(locator.usernameLabel);
				Assert.assertTrue(bodyTxt.indexOf(username) >= 0,
						"Username text mismatched"
								+ page.zComposeView.formatExpActValues(bodyTxt,
										"Username"));
			}

			String password = localize(locator.passwordLabel);
			Assert.assertTrue(bodyTxt.indexOf(password) >= 0,
					"Password text mismatched"
							+ page.zComposeView.formatExpActValues(bodyTxt,
									"Password"));

			Assert.assertTrue(lCaseBodyTxt.indexOf("test123") >= 0,
					"Password mismatched"
							+ page.zComposeView.formatExpActValues(
									lCaseBodyTxt, "test123"));
		}
	}

	/* These methods accepts share and verify for folder exists */
	public void zAcceptShare(String mountingfoldername) throws Exception {
		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.shareCreatedSubject));
		obj.zMessageItem.zClick(localize(locator.shareCreatedSubject));
		Thread.sleep(2000); /*
							 * this is necessary because selenium suddenly
							 * clicks to mail & presses to Accept Share button
							 * but accept share dialog doesn't appears
							 */
		obj.zButton.zClick(zAcceptShareIconBtn);
		Thread.sleep(1000); /*
							 * this is necessary because selenium suddenly
							 * clicks to mail & presses to Accept Share button
							 * but accept share dialog doesn't appears
							 */
		obj.zEditField.zTypeInDlgByName(localize(locator.name),
				mountingfoldername, localize(locator.acceptShare));
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.acceptShare));
		Thread.sleep(2000);
		Assert.assertEquals(
						obj.zMessageItem.zExistsDontWait(localize(locator.shareCreatedSubject)),
						"false",
						"Share created message doesn't move to Trash folder after accept/decline share");
		obj.zButton.zNotExists(zAcceptShareIconBtn);
		obj.zButton.zNotExists(zDeclineShareIconBtn);
	}

	public void zAcceptShare(String mountingfoldername, String color,
			String message, String sharingnoteifany) throws Exception {
		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.shareCreatedSubject));
		obj.zMessageItem.zClick(localize(locator.shareCreatedSubject));
		Thread.sleep(1000); /*
							 * this is necessary because selenium suddenly
							 * clicks to mail & presses to Accept Share button
							 * but accept share dialog doesn't appears
							 */
		obj.zButton.zClick(zAcceptShareIconBtn);
		Thread.sleep(1000); /*
							 * this is necessary because selenium suddenly
							 * clicks to mail & presses to Accept Share button
							 * but accept share dialog doesn't appears
							 */
		obj.zEditField.zTypeInDlgByName(localize(locator.name),
				mountingfoldername, localize(locator.acceptShare));
		if (!color.equals("")) {
			obj.zFeatureMenu.zClickInDlgByName(localize(locator.colorLabel),
					localize(locator.acceptShare));
			obj.zMenuItem.zClickInDlgByName(color,
					localize(locator.acceptShare));
		}
		zShareMessageAction(message, sharingnoteifany,
				localize(locator.acceptShare), localize(locator.yes));
	}

	public void zAcceptShareAndVerifyFolderExists(String applicationtab,
			String sharingfoldername, String color, String message,
			String sharingnoteifany, String mountingfoldername)
			throws Exception {
		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.shareCreatedSubject));
		obj.zMessageItem.zClick(localize(locator.shareCreatedSubject));
		Thread.sleep(1000); /*
							 * this is necessary because selenium suddenly
							 * clicks to mail & presses to Accept Share button
							 * but accept share dialog doesn't appears
							 */
		obj.zButton.zClick(zAcceptShareIconBtn);
		Thread.sleep(1000); /*
							 * this is necessary because selenium suddenly
							 * clicks to mail & presses to Accept Share button
							 * but accept share dialog doesn't appears
							 */
		obj.zEditField.zTypeInDlgByName(localize(locator.name),
				mountingfoldername, localize(locator.acceptShare));
		if (!color.equals("")) {
			obj.zFeatureMenu.zClickInDlgByName(localize(locator.colorLabel),
					localize(locator.acceptShare));
			obj.zMenuItem.zClickInDlgByName(color,
					localize(locator.acceptShare));
		}

		zShareMessageAction(message, sharingnoteifany,
				localize(locator.acceptShare), localize(locator.yes));

		String lCaseapplicationtab = applicationtab.toLowerCase();
		if ((lCaseapplicationtab.equals("mail"))
				|| (applicationtab.equals(localize(locator.mail)))) {
			obj.zButton.zClick(page.zMailApp.zMailTabIconBtn);
		} else if ((lCaseapplicationtab.equals("address book"))
				|| (applicationtab.equals(localize(locator.addressBook)))) {
			obj.zButton.zClick(localize(locator.addressBook));
		} else if ((lCaseapplicationtab.equals("calendar"))
				|| (applicationtab.equals(localize(locator.calendar)))) {
			obj.zButton.zClick(localize(locator.calendar));
		} else if ((lCaseapplicationtab.equals("tasks"))
				|| (applicationtab.equals(localize(locator.tasks)))) {
			obj.zButton.zClick(localize(locator.tasks));
		} else if ((lCaseapplicationtab.equals("documents"))
				|| (applicationtab.equals(localize(locator.documents)))) {
			obj.zButton.zClick(localize(locator.documents));
		} else if ((lCaseapplicationtab.equals("briefcase"))
				|| (applicationtab.equals(localize(locator.briefcase)))) {
			obj.zButton.zClick(localize(locator.briefcase));
		}

		obj.zFolder.zExists(mountingfoldername);
	}

	/* These methods declines share and verify for folder not exists */
	public void zDeclineShare() throws Exception {
		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.shareCreatedSubject));
		obj.zMessageItem.zClick(localize(locator.shareCreatedSubject));
		obj.zButton.zClick(localize(locator.declineShare));
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.declineShare));
		Thread.sleep(2000);
		/*
		 * some time if you have selected to send a mail regarding share and
		 * killing browser then it immediately kills browser without sending
		 * mail and test fails
		 */
		Assert
				.assertEquals(
						obj.zMessageItem
								.zExistsDontWait(localize(locator.shareCreatedSubject)),
						"false",
						"Share created message doesn't move to Trash folder after accept/decline share");
		obj.zButton.zNotExists(zAcceptShareIconBtn);
		obj.zButton.zNotExists(zDeclineShareIconBtn);
	}

	public void zDeclineShare(String message, String sharingnoteifany)
			throws Exception {
		MailApp
				.ClickCheckMailUntilMailShowsUp(localize(locator.shareCreatedSubject));
		obj.zMessageItem.zClick(localize(locator.shareCreatedSubject));
		obj.zButton.zClick(localize(locator.declineShare));
		zShareMessageAction(message, sharingnoteifany,
				localize(locator.declineShare), localize(locator.yes));
	}

	/* These methods helps to verify share revoked */
	public void zRevokeShare(String sharingfoldername, String message,
			String sharingnoteifany) throws Exception {
		obj.zFolder.zRtClick(sharingfoldername);
		obj.zMenuItem.zClick(localize(locator.editProperties));
		zWaitTillObjectExist("dialog", localize(locator.folderProperties));
		if (selenium.isElementPresent("link=" + localize(locator.revoke)))
			selenium.click("link=" + localize(locator.revoke));
		zShareMessageAction(message, sharingnoteifany,
				localize(locator.revokeShare), localize(locator.yes));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.folderProperties));
	}

	/* These methods helps to verify share revoked */
	public void zResendShare(String sharingfoldername) throws Exception {
		obj.zFolder.zRtClick(sharingfoldername);
		obj.zMenuItem.zClick(localize(locator.editProperties));
		zWaitTillObjectExist("dialog", localize(locator.folderProperties));
		if (selenium.isElementPresent("link=" + localize(locator.resend)))
			selenium.click("link=" + localize(locator.resend));
		String resendmessage = obj.zToastAlertMessage.zGetMsg();
		Assert.assertTrue(resendmessage
				.indexOf((localize(locator.resentShareMessage))) >= 0,
				"Resend share notice text mismatched"
						+ page.zComposeView.formatExpActValues(resendmessage,
								"Resend share notice"));
	}
}