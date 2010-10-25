/**
 * 
 */
package projects.mobile.ui;

import java.util.List;

import framework.items.MailItem;
import framework.ui.AbsApplication;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class PageMail extends AbsMobilePage {

	
	public PageMail(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageMail.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean isActive() throws HarnessException {

		// TODO
		return (true);

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void navigateTo() throws HarnessException {

		// TODO
		
	}

	/**
	 * Return a list of all messages in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<MailItem> getMailList() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	/**
	 * Refresh the inbox list by clicking "Get Mail"
	 * @throws HarnessException 
	 */
	public void getMail() throws HarnessException {
		this.click(PageMain.appbarMail);
	}


}
