package framework.util;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ZimbraAdminAccount extends ZimbraAccount {
	private static Logger logger = LogManager.getLogger(ZimbraAccount.class);

	public ZimbraAdminAccount(String email) {
		EmailAddress = email;
		Password = ZimbraSeleniumProperties.getStringProperty("adminPwd", "test123");
		ZimbraMailHost = EmailAddress.split("@")[1];
	}
	
	/**
	 * Creates the account on the ZCS using CreateAccountRequest
	 * zimbraIsAdminAccount is set to TRUE
	 */
	public ZimbraAccount provision() {
		try {
			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<CreateAccountRequest xmlns='urn:zimbraAdmin'>" +
			        	"<name>"+ EmailAddress +"</name>" +
			        	"<password>"+ Password +"</password>" +
			        	"<a n='zimbraIsAdminAccount'>TRUE</a>" +
			        "</CreateAccountRequest>");
			ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account", "id");
			ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account/admin:a[@n='zimbraMailHost']", null);
		} catch (HarnessException e) {
			logger.error("Unable to provision account: "+ EmailAddress);
			ZimbraId = null;
			ZimbraMailHost = null;
		}
		return (this);
	}
	
	/**
	 * Authenticates the admin account (using SOAP admin AuthRequest)
	 * Sets the authToken
	 */
	public ZimbraAccount authenticate() {
		try {
			soapSend(
					"<AuthRequest xmlns='urn:zimbraAdmin'>" +
						"<name>"+ EmailAddress +"</name>" +
						"<password>"+ Password +"</password>" +
					"</AuthRequest>");
			String token = soapSelectValue("//admin:authToken", null);
			soapClient.setAuthToken(token);
		} catch (HarnessException e) {
			logger.error("Unable to authenticate "+ EmailAddress, e);
			soapClient.setAuthToken(null);
		}
		return (this);
	}

	/**
	 * Get the global admin account
	 * This account is defined in config.properties as <adminName>@<server>
	 * @return The global admin account
	 */
	public static synchronized ZimbraAdminAccount GlobalAdmin() {
		if ( _GlobalAdmin == null ) {
			String name = ZimbraSeleniumProperties.getStringProperty("adminName", "admin");
			String domain = ZimbraSeleniumProperties.getStringProperty("server","qa60.lab.zimbra.com");
			_GlobalAdmin = new ZimbraAdminAccount(name +"@"+ domain);
			_GlobalAdmin.authenticate();
		}
		return (_GlobalAdmin);
	}
	private static ZimbraAdminAccount _GlobalAdmin = null;

	
	/**
	 * @param args
	 * @throws HarnessException 
	 */
	public static void main(String[] args) throws HarnessException {
		
		// Configure log4j using the basic configuration
		BasicConfigurator.configure();

		
		
		// Use the pre-provisioned global admin account to send a basic request
		ZimbraAdminAccount.GlobalAdmin().soapSend("<GetVersionInfoRequest xmlns='urn:zimbraAdmin'/>");
		if ( !ZimbraAdminAccount.GlobalAdmin().soapMatch("//admin:GetVersionInfoResponse", null, null) )
			throw new HarnessException("GetVersionInfoRequest did not return GetVersionInfoResponse");
		
		
		
		// Create a new global admin account
		String domain = ZimbraSeleniumProperties.getStringProperty("server","qa60.lab.zimbra.com");
		ZimbraAdminAccount admin = new ZimbraAdminAccount("admin"+ System.currentTimeMillis() +"@"+ domain);
		admin.provision();	// Create the account (CreateAccountRequest)
		admin.authenticate();		// Authenticate the account (AuthRequest)
		
		// Send a basic request as the new admin account
		admin.soapSend("<GetServiceStatusRequest xmlns='urn:zimbraAdmin'/>");
		if ( !admin.soapMatch("//admin:GetServiceStatusResponse", null, null) )
			throw new HarnessException("GetServiceStatusRequest did not return GetServiceStatusResponse");

		
		logger.info("Done!");
	}

}
