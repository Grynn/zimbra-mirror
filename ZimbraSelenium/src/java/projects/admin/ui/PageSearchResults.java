package projects.admin.ui;

import java.util.List;

import framework.ui.AbsApplication;
import framework.util.HarnessException;
import projects.admin.items.Item;


public class PageSearchResults extends AbsAdminPage {

	public PageSearchResults(AbsApplication application) {
		super(application);
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
	
	public List<Item> getSearchResults(String query) throws HarnessException {
		throw new HarnessException("implement me");
	}

}
