package projects.admin.items;

import java.util.HashMap;
import java.util.Map;

import framework.util.ZimbraSeleniumProperties;

public class AccountItem extends Item {

	public String EmailAddress;
	public String Id;
	
	public Map<String, String> AccountAttrs;
	
	public String Password;	// The password is encrypted in the attrs, so need to keep it separate
	
	public AccountItem() {
		super();
		
		AccountAttrs = new HashMap<String, String>();
		
		EmailAddress = 
			"email" + ZimbraSeleniumProperties.getUniqueString() + 
			"@" + ZimbraSeleniumProperties.getStringProperty("testdomain");
		Id = null;
		
		// Surname is required in Admin Console
		AccountAttrs.put("sn", "Lastname"+ ZimbraSeleniumProperties.getUniqueString());

	}
	
	public AccountItem(String emailAddress, String lastName) {
		
		AccountAttrs = new HashMap<String, String>();

		this.EmailAddress = emailAddress;
		Id = null;
		
		// Surname is required in Admin Console
		AccountAttrs.put("sn", lastName);
		
	}
	
	
}
