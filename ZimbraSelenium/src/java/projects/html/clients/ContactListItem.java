package projects.html.clients;

import org.testng.Assert;

public class ContactListItem extends ListItem{
	public ContactListItem() {
		super("listItemCore", "ContactListItem");
	} 
			
		
	public void zVerifyIsSelected(String messageOrId) {
		String actual = ZObjectCore(messageOrId, "isSelected");
		Assert.assertEquals("true", actual);
	}
	
	
		
}

