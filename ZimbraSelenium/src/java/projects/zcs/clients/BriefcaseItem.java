package projects.zcs.clients;

import org.testng.Assert;

public class BriefcaseItem extends ListItem{
	public BriefcaseItem() {
		super("listItemCore", "BriefcaseItem");
	}		
	public void zVerifyBFItemIsSelected(String briefcaseItemOrId) {
		String actual = ZObjectCore(briefcaseItemOrId, "isSelected");
		Assert.assertEquals("true", actual);
	}
}