package framework.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Stafzmprov extends StafAbstract {
	private static Logger logger = LogManager.getLogger(Stafzmprov.class);
	
	
	public static String createAccount(String emailaddress) throws HarnessException {
		ZimbraAccount a = new ZimbraAccount(emailaddress, "test123");
		a.provision();
		a.authenticate();
		return (a.EmailAddress);
	}
	
	public static String getRandomAccount() throws HarnessException {

		// Get the account name
		// Use <locale>_<timestamp>@domain.com
		//
		long now = System.currentTimeMillis();
		String testdomain = ZimbraSeleniumProperties.getStringProperty("testdomain");
		String locale = ZimbraSeleniumProperties.getStringProperty("locale");
		String emailaddress = locale + "_" + now + "@" + testdomain;

		return (createAccount(emailaddress));
		
	}
	
	public static void modifyAccount(ZimbraAccount account, String attr, String value) throws HarnessException {
		
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<ModifyAccountRequest xmlns='urn:zimbraAdmin'>" +
					"<id>"+ account.ZimbraId  +"</id>" +
					"<a n='" + attr +"'>"+ value +"</a>" +
				"</ModifyAccountRequest>");
		ZimbraAdminAccount.GlobalAdmin().soapSelectNode("//admin:ModifyAccountResponse", 1);

	}
	
	public static void modifyAccount(String emailaddress, String attr, String value) throws HarnessException {
		
		// Get the account id by calling provision
		ZimbraAccount account = new ZimbraAccount(emailaddress, "test123");
		account.provision();
		
		// Next, modify the account
		modifyAccount(account, attr, value);

	}
	
	public static String getAccountPreferenceValue(String emailaddress, String attr) throws HarnessException {
				
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<GetAccountRequest xmlns='urn:zimbraAdmin'>" +
					"<account by='name'>"+ emailaddress +"</account>" +
				"</GetAccountRequest>");
		
		ZimbraAdminAccount.GlobalAdmin().soapSelectNode("//admin:GetAccountResponse", 1);
		String value = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:a[@n='" + attr + "']", null);
		logger.debug("GetAccountRequest returned "+ attr +" "+ value);
		
		return (value);
		
	}
	
	public static ZimbraAccount addAccountAlias(ZimbraAccount account, String alias) throws HarnessException {
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<AddAccountAliasRequest xmlns='urn:zimbraAdmin'>" +
					"<id>"+ account.ZimbraId +"</id>" +
					"<alias>"+ alias +"</alias>" +
				"</AddAccountAliasRequest>");
		return (account);
	}
	
	public static String addAccountAlias(String emailaddress, String alias) throws HarnessException {
		
		// Get the account so we know the ID
		ZimbraAccount account = new ZimbraAccount(emailaddress, "test123");
		account.provision();
		
		// Add the alias
		addAccountAlias(account, alias);
		
		return (account.EmailAddress);
	}
	
	public static String createEquipment(String emailaddress) throws HarnessException {
		ZimbraResource resource = new ZimbraResource(ZimbraResource.Type.EQUIPMENT, emailaddress, "test123");
		return (resource.EmailAddress);
	}
	
	public static String createLocation(String emailaddress) throws HarnessException {
		ZimbraResource resource = new ZimbraResource(ZimbraResource.Type.LOCATION, emailaddress, "test123");
		return (resource.EmailAddress);
	}
	
	public static String createDomain(String domain) throws HarnessException {
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<CreateDomainRequest xmlns='urn:zimbraAdmin'>"+
                	"<name>"+ domain +"</name>" +
                "</CreateDomainRequest>");
		
		// No need to check the response, since the domain may already exist
		return (domain);
	}
	
	
	
	public Stafzmprov() {
		super();
		
		logger.info("new Stafzmprov");
		StafService = "PROCESS";

	}
	
	
	public boolean execute(String command) throws HarnessException {
		setCommand(command);
		return (super.execute());
	}
	
	protected String setCommand(String command) {
		
		// Make sure the full path is specified
		if ( command.trim().startsWith("zmprov") ) {
			command = "/opt/zimbra/bin/" + command;
		}
		// Running a command as 'zimbra' user.
		// We must convert the command to a special format
		// START SHELL COMMAND "su - zimbra -c \'<cmd>\'" RETURNSTDOUT RETURNSTDERR WAIT 30000</params>

		StafParms = String.format("START SHELL COMMAND \"su - zimbra -c '%s'\" RETURNSTDOUT RETURNSTDERR WAIT %d", command, StafTimeoutMillis);
		return (getStafCommand());
	}

 


}
