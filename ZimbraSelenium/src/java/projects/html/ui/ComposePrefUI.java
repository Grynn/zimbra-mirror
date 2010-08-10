package projects.html.ui;

import org.testng.Assert;

import framework.util.ZimbraSeleniumProperties;

import projects.html.tests.CommonTest;

/**
 * This Class contains navigate methods and High Level Wrappers related to
 * preference-compose tab
 * 
 * @author Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class ComposePrefUI extends CommonTest {

	public static final String zComposeAsHTMLRadioBtn = "id=composeAsHTML";
	public static final String zComposeAsTextRadioBtn = "id=composeAsText";
	public static final String zComposeFontMenu = "id=composeFont";
	public static final String zComposeSizeMenu = "id=composeSize";
	public static final String zComposeColorMenu = "id=composeColor";

	public static final String zReplyFwdUsingOrignalMailFormatChckBox = "id=zimbraPrefForwardReplyInOriginalFormat";

	public static final String zReplyReplyAllFullTextInlineRadioBtn = "id=replyIncludeBody";
	public static final String zReplyReplyAllFullTextInlineWithPrefixRadioBtn = "id=replyIncludeBodyWithPrefix";
	public static final String zReplyReplyAllAnAttachmentRadioBtn = "id=replyIncludeAsAttachment";
	public static final String zReplyReplyAllDoNotIncludeRadioBtn = "id=replyIncludeNone";

	public static final String zFwdFullTextInlineRadioBtn = "id=forwardIncludeBody";
	public static final String zFwdFullTextInlineWIthPrefixRadioBtn = "id=forwardIncludeBodyWithPrefix";
	public static final String zFwdAnAttachmentRadioBtn = "id=forwardIncludeAsAttachment";

	public static final String zPrefixCharMenu = "id=prefixText";

	public static final String zSaveACopyToSent = "id=saveSent";
	public static final String zDoNotSaveACopyToSent = "id=dontSave";

	public static final String zHtmlComposeBoldBtn = "id=yui-gen2";

	/**
	 * To navigate to preference compose tab
	 * 
	 * @throws Exception
	 */

	// calendarTab = "id=TAB_CALENDAR"
	public static void zNavigateToPrefCompose() throws Exception {
		zGoToApplication("Preferences");
		// obj.zTab.zClick(localize(locator.preferences));
		Thread.sleep(2000);
		String currentLocale = ZimbraSeleniumProperties.getStringProperty("locale");
		if (currentLocale.equals("zh_HK") || currentLocale.equals("ja")
				|| currentLocale.equals("nl") || currentLocale.equals("zh_CN")
				|| currentLocale.equals("de") || currentLocale.equals("es") || currentLocale.equals("ko"))
			obj.zTab.zClick(localize(locator.composing), "2");
		else
			obj.zTab.zClick(localize(locator.optionsComposing));
		Thread.sleep(2000);
	}

	/**
	 * To navigate to preference compose tab and to select composeAs option
	 * 
	 * @param composeAs
	 *            =AsHTML/Astext
	 * @throws Exception
	 */
	public static void zNavigateToPrefComposeAndSelectComposeAse(
			String composeAs) throws Exception {
		page.zComposePrefUI.zNavigateToPrefCompose();
		zSelectComposeAs(composeAs);
	}

	/**
	 * To navigate to preference compose tab and set Sent Msg option
	 * 
	 * @param action
	 *            =SaveInSent/DoNotSaveInSent
	 * @throws Exception
	 */
	public static void zNavigateToPrefComposeAndSelectSentMsgOption(
			String action) throws Exception {
		zNavigateToPrefCompose();
		zSetSentMsgSaveNotSaveOption(action);
	}

	/**
	 * To navigate to preference compose tab and set Reply/ReplyAll Include Msg
	 * As
	 * 
	 * @param includeAs
	 *            =FullTextInline/FullTextInlineWithPrefix/AnAttachment/DoNotInclude
	 * @param prefix
	 *            => or |
	 * @throws Exception
	 */
	public static void zNavigateToPrefComposeAndSetRplyRplyAllIncludeMsgAs(
			String includeAs, String prefix) throws Exception {
		page.zComposePrefUI.zNavigateToPrefCompose();
		page.zComposePrefUI.zSetRplyRplyAllIncludeMsgAs(includeAs, prefix);

	}

	/**
	 * To navigate to preference compose tab and set Fwd Include Msg As
	 * 
	 * @param includeAs
	 *            =FullTextInline/FullTextInlineWithPrefix/AnAttachment
	 * @param prefix
	 *            :> or |
	 * @throws Exception
	 */
	public static void zNavigateToPrefComposeAndSetFwdIncludeMsgAs(
			String includeAs, String prefix) throws Exception {
		page.zComposePrefUI.zNavigateToPrefCompose();
		page.zComposePrefUI.zSetFwdIncludeMsgAs(includeAs, prefix);

	}

	/**
	 * To set compose as option
	 * 
	 * @param composeAs
	 *            =AsHTML/AsText
	 * @throws Exception
	 */
	public static void zSelectComposeAs(String composeAs) throws Exception {
		if (composeAs.equals("AsHTML"))
			obj.zCheckbox.zClick(zComposeAsHTMLRadioBtn);
		else if (composeAs.equals("AsText"))
			obj.zCheckbox.zClick(zComposeAsTextRadioBtn);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
		Thread.sleep(1000);
	}

	/**
	 * To set compose as option and to verify setting in mail compose
	 * 
	 * @param composeAs
	 *            =AsHTML/AsText
	 * @throws Exception
	 */
	public static void zSetComposeAsAndVerifyInMailCompose(String composeAs)
			throws Exception {
		if (composeAs.equals("AsHTML"))
			zSelectComposeAs("AsHTML");
		else if (composeAs.equals("AsText"))
			zSelectComposeAs("AsText");
		page.zComposeView.zNavigateToMailCompose();
		Thread.sleep(3000);
		if (composeAs.equals("AsHTML"))
			obj.zButton.zExists(zHtmlComposeBoldBtn);
		else if (composeAs.equals("AsText"))
			obj.zButton.zNotExists(zHtmlComposeBoldBtn);
	}

	/**
	 * To set Reply/ReplyAll Include Msg As
	 * 
	 * @param includeAs
	 *            =FullTextInline/FullTextInlineWithPrefix/AnAttachment/DoNotInclude
	 */
	public static void zSelectReplyReplyAllIncludeMsgAs(String includeAs) {
		if (includeAs.equals("FullTextInline"))
			obj.zRadioBtn.zClick(zReplyReplyAllFullTextInlineRadioBtn);
		else if (includeAs.equals("FullTextInlineWithPrefix"))
			obj.zRadioBtn
					.zClick(zReplyReplyAllFullTextInlineWithPrefixRadioBtn);
		else if (includeAs.equals("AnAttachment"))
			obj.zRadioBtn.zClick(zReplyReplyAllAnAttachmentRadioBtn);
		else if (includeAs.equals("DoNotInclude"))
			obj.zRadioBtn.zClick(zReplyReplyAllDoNotIncludeRadioBtn);
	}

	/**
	 * To check untill the mail shows up and click on reply button
	 * 
	 * @param subject
	 * @throws Exception
	 */
	public static void zClickCheckMailUntilMailShowsUpAndClickReply(
			String subject) throws Exception {
		page.zMailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zMessageItem.zClick(page.zComposeView.zReplyBtn);
		Thread.sleep(500);
	}

	/**
	 * To select prefix
	 * 
	 * @param prefix
	 *            =">" or "|"
	 * @throws Exception
	 */
	public static void zSelectPrefix(String prefix) throws Exception {
		obj.zHtmlMenu.zClick(zPrefixCharMenu, prefix);
		Thread.sleep(500);
	}

	/**
	 * To set Reply/ReplyAll Include Msg As
	 * 
	 * @param includeAs
	 *            =FullTextInline/FullTextInlineWithPrefix/AnAttachment/DoNotInclude
	 * @param prefix
	 *            = ">" or "|"
	 */
	public static void zSetRplyRplyAllIncludeMsgAs(String includeAs,
			String prefix) throws Exception {
		zSelectReplyReplyAllIncludeMsgAs(includeAs);
		if (!prefix.equals(""))
			zSelectPrefix(prefix);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);

	}

	/**
	 * To verify the reply /reply all Include message As option when replying
	 * 
	 * @param includeAs
	 *            =FullTextInline/FullTextInlineWithPrefix/AnAttachment/DoNotInclude
	 * @param prefix
	 *            = ">" or "|"
	 * @param subject
	 * @param body
	 * @throws Exception
	 */
	public static void zVerifyReplyReplyAllIncludeMsgAs(String includeAs,
			String prefix, String subject, String body) throws Exception {
		zVerifyIncludeMsgAs("ReplyReplyAll", includeAs, prefix, subject, body);
	}

	/**
	 * To verify the forward Include message As option when forwarding
	 * 
	 * @param includeAs
	 *            =FullTextInline/FullTextInlineWithPrefix/AnAttachment
	 * @param prefix
	 *            =">" or "|"
	 * @param subject
	 * @param body
	 * @throws Exception
	 */
	public static void zVerifyFwdIncludeMsgAs(String includeAs, String prefix,
			String subject, String body) throws Exception {
		zVerifyIncludeMsgAs("Forward", includeAs, prefix, subject, body);
	}

	/**
	 * To verify the forward or Reply/ReplyAll Include message As option when
	 * forwarding or replying
	 * 
	 * @param isReplyOrFwd
	 *            =ReplyReplyAll/Forward
	 * @param includeAs
	 *            =FullTextInline/FullTextInlineWithPrefix/AnAttachment/DoNotInclude
	 * @param prefix
	 *            =">" or "|"
	 * @param subject
	 * @param body
	 * @throws Exception
	 */
	public static void zVerifyIncludeMsgAs(String isReplyOrFwd,
			String includeAs, String prefix, String subject, String body)
			throws Exception {
		obj.zTab.zClick(localize(locator.mail));
		Thread.sleep(2000);
		String isMailExist = obj.zMessageItem.zExistsDontWait(subject);
		if (isMailExist.equals("true")) {
			obj.zMessageItem.zClick(subject);
		} else {
			obj.zFolder.zClick(page.zMailApp.zJunkFldr);
			obj.zMessageItem.zClick(subject);
		}
		Thread.sleep(2000);
		if (isReplyOrFwd.equals("ReplyReplyAll"))
			obj.zButton.zClick(page.zComposeView.zReplyBtn);
		else if (isReplyOrFwd.equals("Forward"))
			obj.zButton.zClick(page.zComposeView.zForwardBtn);

		if (includeAs.equals("FullTextInline")) {
			String actualBody = obj.zTextAreaField.zGetInnerText("id=body");
			Assert
					.assertTrue(actualBody.contains(body),
							"Reply/ReplyAll as Full text Inline setting is NOT applied");
		} else if (includeAs.equals("FullTextInlineWithPrefix")) {
			String actualBody = obj.zTextAreaField.zGetInnerText("id=body");
			String expectedBody = prefix + " " + body;
			Assert.assertTrue(actualBody.contains(expectedBody),
					"Reply/ReplyAll as Full text Inline With prefix " + prefix
							+ " setting is NOT applied");
		} else if (includeAs.equals("AnAttachment")) {
			// to write about opened mail has attachment
		} else if (includeAs.equals("DoNotInclude")) {
			String actualBody = obj.zTextAreaField.zGetInnerText("id=body");
			Assert
					.assertTrue(actualBody.equals("<blank>"),
							"Reply/ReplyAll as 'Do Not Include' setting is NOT applied");
		}
	}

	/**
	 * To select include message as option when forwarding
	 * 
	 * @param includeAs
	 *            =FullTextInline/FullTextInlineWithPrefix/AnAttachment
	 * @throws Exception
	 */
	public static void zSelectFwdIncludeMsgAs(String includeAs)
			throws Exception {
		Thread.sleep(1000);
		if (includeAs.equals("FullTextInline"))
			obj.zRadioBtn.zClick(zFwdFullTextInlineRadioBtn);
		else if (includeAs.equals("FullTextInlineWithPrefix"))
			obj.zRadioBtn.zClick(zFwdFullTextInlineWIthPrefixRadioBtn);
		else if (includeAs.equals("AnAttachment"))
			obj.zRadioBtn.zClick(zFwdAnAttachmentRadioBtn);
		Thread.sleep(1000);
	}

	/**
	 * To set include message as option when forwarding
	 * 
	 * @param includeAs
	 *            =FullTextInline/FullTextInlineWithPrefix/AnAttachment
	 * @param prefix
	 */
	public static void zSetFwdIncludeMsgAs(String includeAs, String prefix)
			throws Exception {
		zSelectFwdIncludeMsgAs(includeAs);
		if (!prefix.equals(""))
			zSelectPrefix(prefix);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
		Thread.sleep(1000);
	}

	/**
	 * To set save or do not save sent message in sent folder
	 * 
	 * @param action
	 */
	public static void zSetSentMsgSaveNotSaveOption(String action) {
		if (action.equals("SaveInSent"))
			obj.zRadioBtn.zClick(page.zComposePrefUI.zSaveACopyToSent);
		else if (action.equals("DoNotSaveInSent"))
			obj.zRadioBtn.zClick(page.zComposePrefUI.zDoNotSaveACopyToSent);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
	}

}