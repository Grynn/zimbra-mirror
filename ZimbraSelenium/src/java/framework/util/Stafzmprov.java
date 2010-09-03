package framework.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ibm.staf.STAFMarshallingContext;

public class Stafzmprov extends StafAbstract {
	private static Logger logger = LogManager.getLogger(Stafzmprov.class);
	
	// Set the default account settings
	@SuppressWarnings("serial")
	private static final Map<String, String> accountAttrs = new HashMap<String, String>() {{
		put("zimbraPrefLocale", ZimbraSeleniumProperties.getStringProperty("locale"));
		put("zimbraPrefAutoAddAddressEnabled", "FALSE");
		put("zimbraPrefCalendarInitialView", "week");
		put("zimbraPrefCalendarApptReminderWarningTime", "0");
		put("zimbraPrefTimeZoneId", "Asia/Calcutta");
		put("zimbraFeatureReadReceiptsEnabled", "TRUE");
		put("zimbraPrefCalendarAlwaysShowMiniCal", "FALSE");
		put("zimbraPrefSkin", "beach");
		put("zimbraPrefReplyIncludeOriginalText", "includeBodyAndHeaders");
		put("zimbraPrefForwardIncludeOriginalText", "includeBodyAndHeaders");
	}};

	// Set the default Equipment settings
	@SuppressWarnings("serial")
	private static final Map<String, String> equipmentAttrs = new HashMap<String, String>() {{
		put("zimbraPrefLocale", ZimbraSeleniumProperties.getStringProperty("locale"));
	}};

	// Set the default Equipment settings
	@SuppressWarnings("serial")
	private static final Map<String, String> locationAttrs = new HashMap<String, String>() {{
		put("zimbraPrefLocale", ZimbraSeleniumProperties.getStringProperty("locale"));
	}};

	
	public static boolean zmprov(String command) throws HarnessException {
		Stafzmprov zmprov = new Stafzmprov();
		return (zmprov.execute(command));
	}
	
	public static String createAccount(String emailaddress) throws HarnessException {
		Stafzmprov zmprov = new Stafzmprov();
		zmprov.getRandomAccountStaf(emailaddress);
		return (emailaddress);
	}
	
	public static String getRandomAccount() throws HarnessException {

		// Get the account name
		long now = System.currentTimeMillis();
		String testdomain = ZimbraSeleniumProperties.getStringProperty("testdomain");
		String locale = ZimbraSeleniumProperties.getStringProperty("locale");
		String emailaddress = locale + "_" + now + "@" + testdomain;

		return (createAccount(emailaddress));
		
	}
	
	public static void modifyAccount(String emailaddress, String attr, String value) throws HarnessException {
		
		Stafzmprov zmprov = new Stafzmprov();
		zmprov.modifyAccountStaf(emailaddress, attr, value);

	}
	
	public static String getAccountPreferenceValue(String emailaddress, String attr) throws HarnessException {
		String value = null;
		
		Stafzmprov zmprov = new Stafzmprov();
		value = zmprov.getAccountPreferenceValueStaf(emailaddress, attr);

		return (value);

	}
	
	public static String createEquipment(String emailaddress) throws HarnessException {
		Stafzmprov zmprov = new Stafzmprov();
		zmprov.createCalendarEquipmentStaf(emailaddress);
		return (emailaddress);
	}
	
	public static String createLocation(String emailaddress) throws HarnessException {
		Stafzmprov zmprov = new Stafzmprov();
		zmprov.createCalendarLocationStaf(emailaddress);
		return (emailaddress);
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

//	private boolean getRandomAccountSOAP(String address) throws HarnessException {
//		
//		// Build the SOAP request
//		
//		// First, create the attribute list
//		StringBuilder attributes = new StringBuilder();
//		for (Map.Entry<String, String> entry : accountAttrs.entrySet()) {
//			attributes.append(String.format("<a name='%s'>%s</a>", entry.getKey(), entry.getValue()));
//		}
//
//		String request = String.format(
//				"<CreateAccountRequest xmlns='urn:zimbraAdmin'>" +
//                	"<name>%s</name>" +
//                	"<password>%s</password>" +
//                	"%s" +
//                "</CreateAccountRequest>", address, "test123", attributes.toString());
//		
//		ZimbraAdminAccount.GlobalAdmin().soapSend(request);
//		Element[] response = ZimbraAdminAccount.GlobalAdmin().soapSelectNodes("//admin:CreateAccountResponse");
//		
//		return ((response != null) && (response.length > 0));
//
//	}
	

	private boolean getRandomAccountStaf(String address) throws HarnessException {
		

		// Build the zmprov command
		StringBuilder command = new StringBuilder();
		command.append(String.format("zmprov ca %s test123 ", address));
		
		for (Map.Entry<String, String> entry : accountAttrs.entrySet()) {
			command.append(String.format(" %s %s ", entry.getKey(), entry.getValue()));
		}
		
		return (execute(command.toString()));
		
	}
	
	private boolean createCalendarEquipmentStaf(String address) throws HarnessException {
		
		// Build the zmprov command
		StringBuilder command = new StringBuilder();
		command.append(String.format("zmprov ccr %s test123 displayName %s zimbraCalResType Equipment  ", address, address));
		
		for (Map.Entry<String, String> entry : equipmentAttrs.entrySet()) {
			command.append(String.format(" %s %s ", entry.getKey(), entry.getValue()));
		}
		
		return (execute(command.toString()));

	}
	
	private boolean createCalendarLocationStaf(String address) throws HarnessException {
		
		// Build the zmprov command
		StringBuilder command = new StringBuilder();
		command.append(String.format("zmprov ccr %s test123 displayName %s zimbraCalResType Location  ", address, address));
		
		for (Map.Entry<String, String> entry : locationAttrs.entrySet()) {
			command.append(String.format(" %s %s ", entry.getKey(), entry.getValue()));
		}
		
		return (execute(command.toString()));

	}
	
	private void modifyAccountStaf(String address, String attr, String value) throws HarnessException {

		// Build the zmprov command
		String command = String.format("zmprov ma %s %s %s ", address, attr, value);
		execute(command);
		
	}
 
	@SuppressWarnings("unchecked")
	private String getAccountPreferenceValueStaf(String address, String attr) throws HarnessException {
		
		String command = String.format("zmprov ga %s %s ", address, attr);
		
		if ( !execute(command) )
			throw new HarnessException("Unable to execute "+ command);
		
        if ( StafResult.result == null )
        	throw new HarnessException("Unable to execute "+ command +".  Result was null");
        	
    	if ( !STAFMarshallingContext.isMarshalledData(StafResult.result) )
        	throw new HarnessException("Unable to execute "+ command +".  Result was not marshalled");

        	
		STAFMarshallingContext mc = STAFMarshallingContext.unmarshall(StafResult.result);
		Map map = (Map) mc.getRootObject();
		List list = (List) map.get("fileList");
		Map stdoutMap = (Map) list.get(0);
		String stdout = (String) stdoutMap.get("data");
		
		/*
			stdout looks like:
			# name admin@qa62.lab.zimbra.com
			attr: value
		 */

		// TODO: handle Multi-valued attributes?

		
		Pattern pattern = Pattern.compile("((" + attr +"): (.*))");
		Matcher matcher = pattern.matcher(stdout);
		if (!matcher.find())
			return ("");
		
		if (matcher.groupCount() != 3)
			return ("");
		
		return (matcher.group(3));
		
	}


}
