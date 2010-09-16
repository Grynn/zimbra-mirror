package projects.admin.ui;

import java.util.List;

import framework.util.HarnessException;
import projects.admin.clients.Item;


public class PageSearchResults extends AbsPage {

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
