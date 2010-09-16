package projects.admin.ui;

import framework.ui.AbsApplication;
import framework.util.HarnessException;

public class PageEditAccount extends AbsAdminPage {

	public PageEditAccount(AbsApplication application) {
		super(application);
		
		logger.info("new " + myPageName());

	}

	@Override
	public boolean isActive() throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void navigateTo() throws HarnessException {
		throw new HarnessException("implement me");
	}

}
