package projects.html.clients;

import org.testng.Assert;

import framework.util.HarnessException;

public class BriefcaseItem extends ListItem{
	public BriefcaseItem() {
		super("listItemCore", "BriefcaseItem");
	}		
	public void zVerifyBFItemIsSelected(String briefcaseItemOrId) throws HarnessException   {
		String actual = ZObjectCore(briefcaseItemOrId, "isSelected");
		Assert.assertEquals("true", actual);
	}
}