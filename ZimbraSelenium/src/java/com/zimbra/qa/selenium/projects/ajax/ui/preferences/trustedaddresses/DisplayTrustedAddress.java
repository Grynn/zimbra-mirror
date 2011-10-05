package com.zimbra.qa.selenium.projects.ajax.ui.preferences.trustedaddresses;

import java.util.Arrays;
import java.util.List;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDisplay;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.PageMail;

public class DisplayTrustedAddress extends AbsDisplay {

	public static class Locators {
		public static final String zMsgViewDisplayImgLink = "css=a#zv__TV__TV-main_displayImages_dispImgs";
		public static final String zMsgViewDomainLink = "css=a#zv__TV__TV-main_displayImages_domain";
		public static final String zMsgViewWarningIcon = "css=div#zv__TV__TV-main_displayImages.DisplayImages div div.ImgWarning";

		/*public static final String zConViewDisplayImgLink = "css=a#zv__CLV2-main__MSG_displayImages_dispImgs";
		public static final String zConViewDomainLink = "css=a#zv__CLV2-main__MSG_displayImages_domain";
		public static final String zConViewWarningIcon = "css=div#zv__CLV2-main__MSG_displayImages.DisplayImages div div.ImgWarning";*/
		public static final String zConViewDisplayImgLink = "css=a[id$='_displayImages_dispImgs']";
		public static final String zConViewDomainLink = "css=a[id$='_displayImages_domain']";
		public static final String zConViewWarningIcon = "css=div[id$='_displayImages'] div div[class='ImgWarning']";

	}

	public DisplayTrustedAddress(AbsApplication application) {
		super(application);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// logger.warn("implement me", new Throwable());
		return (sIsElementPresent(PageMail.Locators.IsConViewActiveCSS) || sIsElementPresent(PageMail.Locators.IsMsgViewActiveCSS));

	}

	public String zDisplayImageLink(String zimbraPrefGroupMailBy)
			throws HarnessException {

		String DisplayImgLink = null;
		if (zimbraPrefGroupMailBy == "message") {
			DisplayImgLink = sGetEval("selenium.browserbot.getCurrentWindow().document.getElementById('zv__TV-main__MSG_displayImages').style.display");
			return DisplayImgLink;
		} else if (zimbraPrefGroupMailBy == "conversation") {
			DisplayImgLink = sGetEval("selenium.browserbot.getCurrentWindow().document.getElementById('zv__CLV2-main__MSG_displayImages').style.display");
			return DisplayImgLink;
		} else {
			throw new HarnessException("no logic defined  ");
		}
	}

	/**
	 * Check warning icon,Display Image link,Domain link
	 * 
	 * @return
	 * @throws HarnessException
	 */
	public boolean zHasWDDLinks(String zimbraPrefGroupMailBy)
			throws HarnessException {

		if (zimbraPrefGroupMailBy == "message") {
			List<String> locators = Arrays.asList(
					Locators.zMsgViewDisplayImgLink,
					Locators.zMsgViewDomainLink, Locators.zMsgViewWarningIcon);

			for (String locator : locators) {
				if (!this.sIsElementPresent(locator))
					return (false);
			}

			return (true);

		} else if (zimbraPrefGroupMailBy == "conversation") {
			List<String> locators = Arrays.asList(
					Locators.zConViewDisplayImgLink,
					Locators.zConViewDomainLink, Locators.zConViewWarningIcon);

			for (String locator : locators) {
				if (!this.sIsElementPresent(locator))
					return (false);
			}

			return (true);
		} else {
			throw new HarnessException("no logic defined  ");
		}
	}

}
