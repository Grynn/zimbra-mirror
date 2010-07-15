package projects.zcs.clients;

import org.testng.Assert;

public class TaskItem extends ListItem{
	public TaskItem() {
		super("listItemCore", "TaskItem");
	} 
			
	
	public void zVerifyHasHighPriority(String messageOrId) {
		String actual = ZObjectCore(messageOrId, "hasHighPriority");
		Assert.assertEquals("true", actual);
	}	
	public void zVerifyHasLowPriority(String messageOrId) {
		String actual = ZObjectCore(messageOrId, "hasLowPriority");
		Assert.assertEquals("true", actual);
	}	
	public void zVerifyIsSelected(String messageOrId) {
		String actual = ZObjectCore(messageOrId, "isSelected");
		Assert.assertEquals("true", actual);
	}
	
	public void zVerifyCurrentTaskBodyText(String requiredTxt) {
		String actual =  selenium.call("msgBodyCore", "", "gettext", true, "", "");
		Assert.assertTrue(actual.indexOf(requiredTxt)>=0);
	}
	public void zVerifyCurrentTaskBodyHasImage() {
		String actual =  selenium.call("msgBodyCore", "", "gethtml", true, "", "");		
		Assert.assertTrue(actual.indexOf("dfsrc=")>=0);
	}	
	public String zGetTaskBodyHTML() {
		return selenium.call("msgBodyCore", "", "gethtml", true, "", "");		
	}	


		
}

