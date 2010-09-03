package framework.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ZimbraResource extends ZimbraAccount {
	private static Logger logger = LogManager.getLogger(ZimbraResource.class);

	public static enum Type {
		LOCATION,
		EQUIPMENT
	};
	
	public String EmailAddress;
	public String Password;
	public Type ResourceType;
    public String ZimbraMailHost = null;
    public String ZimbraId = null;

	public ZimbraResource(Type type) {
		this(type, null, null);
	}
	
	public ZimbraResource(Type type, String email, String password) {
		ResourceType = type;
		
		if ( email == null ) {
			email = ZimbraSeleniumProperties.getStringProperty("locale") + "_" + ResourceType +"_" + System.currentTimeMillis();
		}
		EmailAddress = email;
		
		if ( password == null ) {
			password = ZimbraSeleniumProperties.getStringProperty("adminPwd", "test123");
		}
		Password = password;
		        
        
        provision();
        authenticate();
 
		
	}
	
	// Set the default Equipment settings
	@SuppressWarnings("serial")
	private static final Map<String, String> equipmentAttrs = new HashMap<String, String>() {{
		put("zimbraPrefLocale", ZimbraSeleniumProperties.getStringProperty("locale"));
		put("zimbraCalResAutoAcceptDecline", "TRUE");
		put("zimbraCalResAutoDeclineIfBusy", "TRUE");
	}};

	// Set the default Location settings
	@SuppressWarnings("serial")
	private static final Map<String, String> locationAttrs = new HashMap<String, String>() {{
		put("zimbraPrefLocale", ZimbraSeleniumProperties.getStringProperty("locale"));
		put("zimbraCalResAutoAcceptDecline", "TRUE");
		put("zimbraCalResAutoDeclineIfBusy", "TRUE");
	}};


	public void provision() {
		try {
			
			Map<String, String> attrs = null;
			StringBuilder prefs = null;
			if ( ResourceType == Type.EQUIPMENT ) {
				prefs = new StringBuilder("<a n='zimbraCalResType'>Equipment</a>");
				attrs = equipmentAttrs;
			} else if ( ResourceType == Type.LOCATION ) {
				prefs = new StringBuilder("<a n='zimbraCalResType'>Location</a>");
				attrs = locationAttrs;
			} else {
				throw new HarnessException("Unknown resource type "+ ResourceType);
			}
			
			// Build the list of default preferences
    		for (Map.Entry<String, String> entry : attrs.entrySet()) {
    			prefs.append(String.format("<a n='%s'>%s</a>", entry.getKey(), entry.getValue()));
    		}

			ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<CreateCalendarResourceRequest xmlns='urn:zimbraAdmin'>" +
						"<name>" + EmailAddress + "</name>" +
						"<password>" + Password + "</password>" +
						prefs.toString() +
					"</CreateCalendarResourceRequest>");
			
			ZimbraId = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:calresource", "id");
			ZimbraMailHost = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:account/admin:a[@n='zimbraMailHost']", null);
			
		} catch (HarnessException e) {
			logger.error("Unable to provision account: "+ EmailAddress, e);
			ZimbraId = null;
			ZimbraMailHost = null;
		}
	}
	
}
