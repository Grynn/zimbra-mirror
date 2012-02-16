package com.zimbra.qa.selenium.framework.items;

import java.util.*;
import org.apache.log4j.*;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.*;

public class AppointmentItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	// Data values (SOAP)
	protected String dSubject = null;
	protected String dFragment = null;
	protected String dAttendees = null;
	protected String dOptionals = null;
	protected String dLocation = null;
	protected String dEquipment = null;
	protected ZDate dStartTime = null;
	protected ZDate dEndTime = null;
	protected String dAllDay = null;
	protected String dDisplay = null;
	protected String dFolder = null;
	protected String dPrivate = null;
	protected String dRepeat = null;
	protected String dReminder = null;
	protected String dContent = null;
	protected boolean dIsChecked = false;
	protected boolean dIsTagged = false;
	protected String dRecurring = null;
	protected boolean dIsAllDay = false;
	protected boolean dIsPrivate = false;
	protected boolean dHasAttachments = false;
	
	// GUI values
	protected String gSubject = null;
	protected String gFragment = null;
	protected String gAttendees = null;
	protected String gOptionals = null;	
	protected String gLocation = null;
	protected String gEquipment = null;
	protected String gStartDate = null;
	protected ZDate gStartTime = null;
	protected String gEndDate = null;
	protected ZDate gEndTime = null;
	protected String gDisplay = null;
	protected String gFolder = null;
	protected String gRepeat = null;
	protected String gReminder = null;
	protected String gContent = null;
	protected boolean gIsChecked = false;
	protected boolean gIsTagged = false;
	protected boolean gIsRecurring = false;
	protected boolean gIsAllDay = false;
	protected boolean gIsPrivate = false;
	protected boolean gHasAttachments = false;
	protected String gStatus = null;
	protected String TheLocator = null;

	public AppointmentItem() {	
	}
	
	@Override
	public String getName() {
		return (getSubject());
	}

	public String getLocator() {
		return (TheLocator);
	}
	
	public void setLocator(String locator) {
		TheLocator = locator;
	}
	
public static AppointmentItem importFromSOAP(Element GetAppointmentResponse) throws HarnessException {
	
		
		if ( GetAppointmentResponse == null )
			throw new HarnessException("Element cannot be null");
		
			
		AppointmentItem appt = null;
		
		try {

			// Make sure we only have the GetMsgResponse part
			Element getAppointmentResponse = ZimbraAccount.SoapClient.selectNode(GetAppointmentResponse, "//mail:GetAppointmentResponse");
			if ( getAppointmentResponse == null )
				throw new HarnessException("Element does not contain GetAppointmentResponse");
	
			Element m = ZimbraAccount.SoapClient.selectNode(getAppointmentResponse, "//mail:appt");
			if ( m == null )
				throw new HarnessException("Element does not contain an appt element");
			
			// Create the object
			appt = new AppointmentItem();
						
			Element sElement = ZimbraAccount.SoapClient.selectNode(m, "//mail:s");
			if ( sElement != null ) {
				
				// Start time
				appt.dStartTime = new ZDate(sElement);

			}

			Element eElement = ZimbraAccount.SoapClient.selectNode(m, "//mail:e");
			if ( eElement != null ) {
				
				// End time
				appt.dEndTime = new ZDate(eElement);

			}

			Element compElement = ZimbraAccount.SoapClient.selectNode(m, "//mail:comp");
			if ( compElement != null ) {

				// Subject
				appt.dSubject = compElement.getAttribute("name");
				
				// Location
				appt.dLocation = compElement.getAttribute("loc");
				
				// Display
				appt.dDisplay = compElement.getAttribute("fb");
			}
				
			// Parse the required attendees
			ArrayList<String> attendees = new ArrayList<String>();
			Element[] requiredElements = ZimbraAccount.SoapClient.selectNodes(m, "//mail:at[@role='REQ']");
			for ( Element e : requiredElements ) {
				attendees.add(e.getAttribute("a"));
			}
			if ( attendees.size() > 0 ) {
				appt.dAttendees = AppointmentItem.StringListToCommaSeparated(attendees);
			}
			
			// Parse the optional attendees
			ArrayList<String> optionals = new ArrayList<String>();
			Element[] optionalElements = ZimbraAccount.SoapClient.selectNodes(m, "//mail:at[@role='OPT']");
			for ( Element e : optionalElements ) {
				optionals.add(e.getAttribute("a"));
			}
			if ( optionals.size() > 0 ) {
				appt.dOptionals = AppointmentItem.StringListToCommaSeparated(optionals);
			}
			
			if (appt.dLocation == "") {
				
				Element equipElement = ZimbraAccount.SoapClient.selectNode(m, "//mail:at[@cutype='RES']");
				if ( equipElement != null ) {
				
					// Equipment
					appt.dEquipment = equipElement.getAttribute("a");
			
				}
				
			} else if (appt.dLocation != null) {
				
				Element equipElement = ZimbraAccount.SoapClient.selectNode(m, "//mail:at[@cutype='RES'][2]");
				if ( equipElement != null ) {
				
					// Equipment
					appt.dEquipment = equipElement.getAttribute("a");
			
				}
			}
			
			Element descElement = ZimbraAccount.SoapClient.selectNode(m, "//mail:fr");
			if ( descElement != null ) {
				
				// Body
				appt.dContent = descElement.getTextTrim();
				
			}

			return (appt);
			
		} catch (Exception e) {
			throw new HarnessException("Could not parse GetMsgResponse: "+ GetAppointmentResponse.prettyPrint(), e);
		} finally {
			if ( appt != null )	logger.info(appt.prettyPrint());
		}
		
	}

	/**
	 * Get an AppointmentItem using start/end +/- 31 days
	 * @param account
	 * @param query
	 * @return
	 * @throws HarnessException
	 */
	public static AppointmentItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		Calendar now = Calendar.getInstance();
		ZDate date = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		return (importFromSOAP(account, query, date.addDays(-31), date.addDays(31)));
	}


	/**
	 * Get an AppointmentItem using soap
	 * @param account
	 * @param query
	 * @param start
	 * @param end
	 * @return
	 * @throws HarnessException
	 */
	public static AppointmentItem importFromSOAP(ZimbraAccount account, String query, ZDate start, ZDate end) throws HarnessException {
		
		try {
			account.soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ start.toMillis() +"' calExpandInstEnd='"+ end.toMillis() +"'>" +
						"<query>"+ query +"</query>" +
					"</SearchRequest>");
			
			Element[] results = account.soapSelectNodes("//mail:SearchResponse/mail:appt");
			if (results.length != 1)
				//throw new HarnessException("Query should return 1 result, not "+ results.length);
				return null;
			
			String id = account.soapSelectValue("//mail:appt", "id");
			
			account.soapSend(
					"<GetAppointmentRequest xmlns='urn:zimbraMail' id='"+ id +"' includeContent='1'>" +
	                "</GetAppointmentRequest>");
			Element getAppointmentResponse = account.soapSelectNode("//mail:GetAppointmentResponse", 1);
			
			// Using the response, create this item
			return (importFromSOAP(getAppointmentResponse));
			
		} catch (Exception e) {
			throw new HarnessException("Unable to import using SOAP query("+ query +") and account("+ account.EmailAddress +")", e);
		}
	}
	

	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		// TODO Auto-generated method stub

	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(MailItem.class.getSimpleName()).append('\n');
		sb.append('\n').append(prettyPrintSOAP());
		sb.append('\n').append(prettyPrintGUI());
		return (sb.toString());
	}

	public String prettyPrintSOAP() {
		StringBuilder sb = new StringBuilder();
		sb.append("SOAP Data:\n");
		sb.append("Subject: ").append(dSubject).append('\n');
		sb.append("Fragment: ").append(dFragment).append('\n');
		sb.append("Attendees: ").append(dAttendees).append('\n');
		sb.append("Optional: ").append(dOptionals).append('\n');
		sb.append("Location: ").append(dLocation).append('\n');
		sb.append("Equipment: ").append(dEquipment).append('\n');
		sb.append("Start Time: ").append(dStartTime).append('\n');	
		sb.append("End Time: ").append(dEndTime).append('\n');
		sb.append("Display: ").append(dDisplay).append('\n');
		sb.append("Calendar: ").append(dFolder).append('\n');
		sb.append("Repeat: ").append(dRepeat).append('\n');
		sb.append("Reminder: ").append(dReminder).append('\n');
		sb.append("Content: ").append(dContent).append('\n');
		sb.append("Is Allday: ").append(dIsAllDay).append('\n');
		sb.append("Is Private: ").append(dIsPrivate).append('\n');
		sb.append("Is Tagged: ").append(dIsTagged).append('\n');
		sb.append("Is Recurring: ").append(dRecurring).append('\n');		
		sb.append("Has Attachments: ").append(dHasAttachments).append('\n');
		return (sb.toString());
	}

	public String prettyPrintGUI() {
		StringBuilder sb = new StringBuilder();
		sb.append("GUI Data:\n");
		sb.append("Subject: ").append(gSubject).append('\n');
		sb.append("Fragment: ").append(gFragment).append('\n');
		sb.append("Attendees: ").append(gAttendees).append('\n');
		sb.append("Optional: ").append(gOptionals).append('\n');
		sb.append("Location: ").append(gLocation).append('\n');
		sb.append("Equipment: ").append(gEquipment).append('\n');		
		sb.append("Start Time: ").append(gStartTime).append('\n');
		sb.append("End Time: ").append(gEndTime).append('\n');
		sb.append("Display: ").append(gDisplay).append('\n');
		sb.append("Calendar: ").append(gFolder).append('\n');
		sb.append("Repeat: ").append(gRepeat).append('\n');
		sb.append("Reminder: ").append(gReminder).append('\n');
		sb.append("Content: ").append(gContent).append('\n');
		sb.append("Is Allday: ").append(gIsAllDay).append('\n');
		sb.append("Is Private: ").append(gIsPrivate).append('\n');
		sb.append("Is Tagged: ").append(gIsTagged).append('\n');
		sb.append("Is Recurring: ").append(gIsRecurring).append('\n');		
		sb.append("Has Attachments: ").append(gHasAttachments).append('\n');
		return (sb.toString());
	}

	// --------------------- SOAP -----------------------------------
	public String getSubject() {
		return (dSubject);
	}
	
	public void setSubject(String subject) {
		dSubject = subject;
	}
	
	public String getFragment() {
		return (dFragment);
	}
	
	public void setFragment(String subject) {
		dFragment = subject;
	}
	
	public String getAttendees() {
		return (dAttendees);
	}
	
	public void setAttendees(String attendees) {
		dAttendees = attendees;
	}
	
	public String getOptional() {
		return (dOptionals);
	}
	
	public void setOptional(String optional) {
		dOptionals = optional;
	}
	
	public String getLocation() {
		return (dLocation);
	}
	
	public void setLocation(String location) {
		dLocation = location;
	}
	
	public String getEquipment() {
		return (dEquipment);
	}

	public void setEquipment(String equipment) {
		dEquipment = equipment;
	}
	
	public String getContent() {
		return (dContent);
	}

	public void setContent(String content) {
		dContent = content;
	}
	
	public ZDate getStartTime() {
		return (dStartTime);
	}

	public void setStartTime(ZDate date) {
		dStartTime = date;
	}
	
	public ZDate getEndTime() {
		return (dEndTime);
	}
	
	public void setEndTime(ZDate date) {
		dEndTime = date;
	}

	public String getDisplay() {
		return (dDisplay);
	}

	public void setDisplay(String display) {
		dDisplay = display;
	}

	public void setFolder(String id) {
		dFolder = id;
	}
	
	public String getReminder() {
		return (dReminder);
	}

	public void setReminder(String reminder) {
		dReminder = reminder;
	}
	
	public boolean getIsAllDay() {
		return (dIsAllDay);
	}
	
	public boolean setIsAllDay(boolean isAllDay) {
		dIsAllDay = true;
		return (dIsAllDay);
	}
	
	public String getRecurring() {
		return (dRecurring);
	}
	
	public String setRecurring(String recurringType, String endBy) {
		dRecurring = recurringType + "," + endBy;
		return dRecurring;
	}
	
	public String setRecurring(String recurringType, int noOfOccurrences) {
		dRecurring = recurringType + "," + noOfOccurrences;
		return dRecurring;
	}
	
	public boolean getIsPrivate() {
		return (dIsPrivate);
	}
	
	public boolean setIsPrivate(boolean isPrivate) {
		dIsPrivate = true;
		return (dIsPrivate);
	}
	
	public boolean getIsChecked() {
		return (dIsChecked);
	}
	
	public boolean setIsChecked() {
		return (dIsChecked);
	}

	public boolean getIsTagged() {
		return (dIsTagged);
	}
	
	public boolean setIsTagged() {
		return (dIsTagged);
	}

	public boolean getHasAttachments() {
		return (dHasAttachments);
	}
	
	public boolean setHasAttachments() {
		return (dHasAttachments);
	}

	// --------------------- GUI -----------------------------------
	public String getGSubject() {
		return (gSubject);
	}
	
	public void setGSubject(String subject) {
		gSubject = subject;
	}
	
	public String getGFragment() {
		return (gFragment);
	}
	
	public void setGFragment(String subject) {
		gFragment = subject;
	}
	
	public String getGAttendees() {
		return (gAttendees);
	}
	
	public void setGAttendees(String attendees) {
		gAttendees = attendees;
	}
	
	public String getGOptional() {
		return (gOptionals);
	}
	
	public void setGOptional(String optional) {
		gOptionals = optional;
	}
	
	public String getGLocation() {
		return (gLocation);
	}
	
	public void setGLocation(String location) {
		gLocation = location;
	}
	
	public String getGEquipment() {
		return (gEquipment);
	}
	
	public void setGEquipment(String equipment) {
		gEquipment = equipment;
	}
	
	public String getGContent() {
		return (gContent);
	}
	
	public void setGContent(String content) {
		gContent = content;
	}
	
	public String getGStartDate() {
		return (gStartDate);
	}
	
	public void setGStartDate(String string) {
		gStartDate = string;
	}
	
	public String getGEndDate() {
		return (gEndDate);
	}
	
	public void setGEndDate(String string) {
		gEndDate = string;
	}
	
	public ZDate getGStartTime() {
		return (gStartTime);
	}
	
	public void setGStartTime(ZDate date) {
		gStartTime = date;
	}
	
	public ZDate getGEndTime() {
		return (gEndTime);
	}
	
	public void setGEndTime(ZDate date) {
		gEndTime = date;
	}
	
	public String getGDisplay() {
		return (gDisplay);
	}

	public void setGDisplay(String display) {
		gDisplay = display;
	}
	
	public String getGFolder() {
		return (gFolder);
	}

	public void setGFolder(String folder) {
		gFolder = folder;
	}
	
	public String getGReminder() {
		return (gReminder);
	}

	public void setGReminder(String reminder) {
		gReminder = reminder;
	}
	
	public boolean getGIsAllDay() {
		return (gIsAllDay);
	}
	
	public boolean setGIsAllDay() {
		return (gIsAllDay);
	}
	
	public boolean getGIsRecurring() {
		return (gIsRecurring);
	}
	
	public boolean setGIsRecurring(boolean b) {
		return (gIsRecurring);
	}
	
	public boolean getGIsPrivate() {
		return (gIsPrivate);
	}
	
	public boolean setGIsPrivate() {
		return (gIsPrivate);
	}
	
	public boolean getGIsChecked() {
		return (gIsChecked);
	}

	public boolean setGIsChecked(boolean b) {
		return (gIsChecked);
	}
	
	public boolean getGIsTagged() {
		return (gIsTagged);
	}

	public boolean setGIsTagged() {
		return (gIsTagged);
	}
	
	public boolean getGHasAttachments() {
		return (gHasAttachments);
	}
	
	public boolean setGHasAttachments(boolean b) {
		return (gHasAttachments);
	}
	
	public String getFolder() {
		return (dFolder);
	}

	public boolean getGHasAttachment() {
		return (gHasAttachments);
	}

	public void setGIsTagged(boolean tagged) {
		gIsTagged = tagged;		
	}

	public void setGHasAttachment(boolean hasAttachment) {
		gHasAttachments = hasAttachment;
	}

	public void setGIsAllDay(boolean allDay) {
		gIsAllDay = allDay;
	}

	public void setGStatus(String status) {
		gStatus = status;
	}

	public String getGStatus() {
		return (gStatus);
	}


	/**
	 * Create a single-day appointment on the server
	 * 
	 * @param account Appointment Organizer
	 * @param start Start time of the appointment, which will be rounded to the nearest hour
	 * @param duration Duration of the appointment, in minutes
	 * @param timezone Timezone of the appointment (null if default)
	 * @param subject Subject of the appointment
	 * @param content Content of the appointment (text)
	 * @param location Location of the appointment (null if none)
	 * @param attendees List of attendees for the appointment
	 * @return
	 * @throws HarnessException
	 */
	public static AppointmentItem createAppointmentSingleDay(ZimbraAccount account, Calendar start, int duration, TimeZone tz, String subject, String content, String location, List<ZimbraAccount> attendees)
	throws HarnessException {
		
		// If location is null, don't specify the loc attribute
		String loc = (location == null ? "" : "loc='"+ location + "'");

		// TODO: determine the timezone
		String timezoneString = ZTimeZone.TimeZoneEST.getID();

		
		// Convert the calendar to a ZDate
		ZDate beginning = new ZDate(start.get(Calendar.YEAR), start.get(Calendar.MONTH) + 1, start.get(Calendar.DAY_OF_MONTH), start.get(Calendar.HOUR_OF_DAY), 0, 0);
		ZDate ending = beginning.addMinutes(duration);
		
		account.soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
			+		"<m l='10'>"
			+			"<inv>"
			+				"<comp name='"+ subject +"' "+ loc + " draft='0' status='CONF' class='PUB' transp='O' fb='F'>"
			+					"<s d='"+ beginning.toTimeZone(timezoneString).toYYYYMMDDTHHMMSS() +"' tz='"+ timezoneString +"'/>"
			+					"<e d='"+ ending.toTimeZone(timezoneString).toYYYYMMDDTHHMMSS() +"' tz='"+ timezoneString +"'/>"
			+					"<or a='" + account.EmailAddress +"'/>"
			+				"</comp>"
			+			"</inv>"
			+			"<su>"+ subject + "</su>"
			+			"<mp ct='text/plain'>"
			+				"<content>" + content + "</content>"
			+			"</mp>"
			+		"</m>"
			+	"</CreateAppointmentRequest>");

		AppointmentItem result = AppointmentItem.importFromSOAP(account, "subject:("+ subject +")", beginning.addDays(-7), beginning.addDays(7));

		return (result);


	}
	
	/**
	 * Create an all-day appointment on the server
	 * 
	 * @param account The appointment organizer
	 * @param date The appointment start date
	 * @param duration The appointment duration (in days)
	 * @param subject The appointment subject
	 * @param content The appointment text content
	 * @param location The appointment location (null if none)
	 * @param attendees A list of attendees (null for none)
	 * @return
	 * @throws HarnessException
	 */
	public static AppointmentItem createAppointmentAllDay(ZimbraAccount account, Calendar date, int duration, String subject, String content, String location, List<ZimbraAccount> attendees)
	throws HarnessException {

		// If location is null, don't specify the loc attribute
		String loc = (location == null ? "" : "loc='"+ location + "'");
		
		// Convert the calendar to a ZDate
		ZDate start = new ZDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH), 12, 0, 0);

		account.soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
			+		"<m l='10'>"
			+			"<inv>"
			+				"<comp allDay='1' name='"+ subject +"' "+ loc + " draft='0' status='CONF' class='PUB' transp='O' fb='F'>"
			+					"<s d='" + start.toYYYYMMDD() +"'/>"
			+					"<e d='" + start.addDays(duration > 0 ? duration - 1 : 0).toYYYYMMDD() +"'/>"
			+					"<or a='" + account.EmailAddress +"'/>"
			+				"</comp>"
			+			"</inv>"
			+			"<su>"+ subject + "</su>"
			+			"<mp ct='text/plain'>"
			+				"<content>" + content + "</content>"
			+			"</mp>"
			+		"</m>"
			+	"</CreateAppointmentRequest>");

		AppointmentItem result = AppointmentItem.importFromSOAP(account, "subject:("+ subject +")", start.addDays(-7), start.addDays(7));

		return (result);

	}

	
	private static String StringListToCommaSeparated(List<String> strings) {
		StringBuilder sb = new StringBuilder("");
		String delimiter = ""; // First entry does not get a comma
		for ( String s : strings ) {
			sb.append(delimiter).append(s);
			delimiter = ","; // Next entry, if any, will get a comma
		}
		return (sb.toString());
	}

}
