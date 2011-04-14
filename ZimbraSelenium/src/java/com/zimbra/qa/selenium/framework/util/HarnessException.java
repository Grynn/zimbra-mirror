package com.zimbra.qa.selenium.framework.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;

public class HarnessException extends Exception {
	Logger logger = LogManager.getLogger(HarnessException.class);

	private static final long serialVersionUID = 4657095353247341818L;

	protected void resetAccounts() {
		logger.error("Reset AccountZWC due to exception");
		ZimbraAccount.ResetAccountZWC();
		ZimbraAccount.ResetAccountHTML();
		ZimbraAccount.ResetAccountZMC();
		ZimbraAccount.ResetAccountZDC();
		ZimbraAdminAccount.ResetAccountAdminConsoleAdmin();
		if (ZimbraSeleniumProperties.getAppType() == AppType.ADMIN) {
			// WORKAROUND for all the dialogs that need to be dismissed
			// Reload the app
			ClientSessionFactory.session().selenium().refresh();
			SleepUtil.sleep(10000);
		}
	}
	
	public HarnessException(String message) {
		super(message);
		logger.error(message, this);
		resetAccounts();
	}

	public HarnessException(Throwable cause) {
		super(cause);
		logger.error(cause.getMessage(), cause);
		resetAccounts();
	}

	public HarnessException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message, cause);
		resetAccounts();
	}

}
