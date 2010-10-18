package projects.html.clients;

import org.testng.Assert;

import framework.util.HarnessException;

public class ContactListItem extends ListItem{
	public ContactListItem() {
		super("listItemCore", "ContactListItem");
	} 
			
		
	public void zVerifyIsSelected(String messageOrId)  throws HarnessException  {
		String actual = ZObjectCore(messageOrId, "isSelected");
		Assert.assertEquals("true", actual);
	}
	
	
		
}

