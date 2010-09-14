package projects.admin.clients;

import framework.util.ZimbraSeleniumProperties;

public class AccountItem extends Item {

	public String EmailAddress;
	public String Password;

	public String FirstName;
	public String MiddleInitial;
	public String LastName;
	
	public AccountItem() {
		super();
		
		EmailAddress = 
			"email" + ZimbraSeleniumProperties.getUniqueString() + 
			"@" + ZimbraSeleniumProperties.getStringProperty("testdomain");
		
		LastName = "Lastname"+ ZimbraSeleniumProperties.getUniqueString();

	}
	
	public AccountItem(String EmailAddress, String LastName) {
		
		this.EmailAddress = EmailAddress;
		this.LastName = LastName;
		
	}
	
	
}
