package projects.admin.ui;

import java.util.List;

import framework.items.IItem;
import framework.ui.AbsApplication;
import framework.util.HarnessException;


public class PageSearchResults extends AbsAdminPage {

	public PageSearchResults(AbsApplication application) {
		super(application);
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zNavigateTo() throws HarnessException {
		throw new HarnessException("implement me");
	}
	
	public List<IItem> getSearchResults(String query) throws HarnessException {
		throw new HarnessException("implement me");
	}

}
