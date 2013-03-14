/**
 * 
 */
package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.AbsWizard;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;


/**
 * @author zimbra
 *
 */
public class WizardCreateAdminAccount extends AbsWizard {
	public static class Locators {
		public static final String zdlg_NEW_ACCT = "zdlg__NEW_ACCT";
		public static final String zdlg_ACCT_NAME = "zdlgv__NEW_ADMIN_name_2";
		public static final String zdlg_DOMAIN_NAME="zdlgv__NEW_ADMIN_name_3_display";
		public static final String zdlg_DL_NAME = "zdlgv__NEW_ADMIN_name_5";
		public static final String zdlg_DL_DOMAIN_NAME="zdlgv__NEW_ADMIN_name_6_display";
		public static final String zdlg_LAST_NAME="zdlgv__NEW_ACCT_sn";
		public static final String zdlg_OK="zdlg__MSG_button2_title";
		public static final String ADMIN_TYPE="css=div[id$='zdlgv__NEW_ADMIN'] div.ImgSelectPullDownArrow";
		public static final String ADMIN_USER="css=div[id$='new_admin_type_choice_1']";
		public static final String ADMIN_GROUP="css=div[id$='new_admin_type_choice_0']";
		public static final String GLOBAL_ADMIN_CHECK_BOX="zdlgv__NEW_ADMIN_";
	}

	public WizardCreateAdminAccount(AbsTab page) {
		super(page);
		logger.info("New "+ WizardCreateAdminAccount.class.getName());
	}
	
	public String adminType="";
	public static boolean IsGlobalAdmin=false;
		
	public static boolean isGlobalAdmin() {
		return IsGlobalAdmin;
	}

	public void setGlobalAdmin(boolean isGlobalAdmin) throws HarnessException {
		IsGlobalAdmin = isGlobalAdmin;
		if(IsGlobalAdmin) {
			for(int i=10;i>=1;i--) {
				if(sIsElementPresent(Locators.GLOBAL_ADMIN_CHECK_BOX+i+"_zimbraIsAdminAccount")) {
					sCheck(Locators.GLOBAL_ADMIN_CHECK_BOX+i+"_zimbraIsAdminAccount");
					return;
				}
			}	
			sCheck(Locators.GLOBAL_ADMIN_CHECK_BOX+"zimbraIsAdminAccount");
		}
	}	

	public String getAdminType() {
		return adminType;
	}

	public void setAdminType(String adminType) throws HarnessException {
		if(adminType!="") {
			sClick(Locators.ADMIN_TYPE);
			if(adminType.equals(Locators.ADMIN_USER)) {
				adminType=Locators.ADMIN_USER;
			} else if(adminType.equals(Locators.ADMIN_GROUP)) {
				adminType=Locators.ADMIN_GROUP;
			}
			sClick(adminType);
			clickNext(AbsWizard.Locators.ADMIN_DIALOG);
		}
		this.adminType=adminType;
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsWizard#completeWizard(projects.admin.clients.Item)
	 */
	@Override
	public IItem zCompleteWizard(IItem item) throws HarnessException {

		if ( !(item instanceof AccountItem) )
			throw new HarnessException("item must be an AccountItem, was "+ item.getClass().getCanonicalName());
		
		AccountItem account = (AccountItem)item;

		String CN = account.getLocalName();
		String domain = account.getDomainName();

		if(adminType.equals(Locators.ADMIN_USER)) {
			zType(Locators.zdlg_ACCT_NAME, CN);
			if(ZimbraSeleniumProperties.isWebDriver()) {
				SleepUtil.sleepSmall();
				this.clearField(Locators.zdlg_DOMAIN_NAME);	
			}
			zType(Locators.zdlg_DOMAIN_NAME,"");
			this.zKeyboard.zTypeCharacters(domain);
			clickNext(AbsWizard.Locators.ADMIN_DIALOG);
			clickFinish(AbsWizard.Locators.ADMIN_DIALOG);
		}else {
			zType(Locators.zdlg_DL_NAME, CN);
			if(ZimbraSeleniumProperties.isWebDriver()) {
				SleepUtil.sleepSmall();
				this.clearField(Locators.zdlg_DL_DOMAIN_NAME);
			}
			zType(Locators.zdlg_DL_DOMAIN_NAME,"");
			this.zKeyboard.zTypeCharacters(domain);
			clickFinish(AbsWizard.Locators.ADMIN_DIALOG);
		}
		return (account);
	}

	
	@Override
	public boolean zIsActive() throws HarnessException {

		boolean present = sIsElementPresent(Locators.zdlg_NEW_ACCT);
		if ( !present ) {
			return (false);
		}

		boolean visible = this.zIsVisiblePerPosition(Locators.zdlg_NEW_ACCT, 0, 0);
		if ( !visible ) {
			return (false);
		}

		return (true);
	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}


}
