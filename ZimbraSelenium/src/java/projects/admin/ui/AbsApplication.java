package projects.admin.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.util.ZimbraAccount;

/**
 * This class defines an abstract Zimbra "Application"
 * 
 * Example, Admin Console, Ajax Client, HTML Client
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsApplication {
	protected static Logger logger = LogManager.getLogger(AbsPage.class);
	
	private ZimbraAccount authenticatedAccount = null;

	public AbsApplication() {
		logger.info("new " + AbsApplication.class.getCanonicalName());

	}
	
	protected ZimbraAccount setActiveAcount(ZimbraAccount account) {
		authenticatedAccount = account;
		return (authenticatedAccount);
	}
	
	public ZimbraAccount getActiveAccount() {
		return (authenticatedAccount);
	}
}
