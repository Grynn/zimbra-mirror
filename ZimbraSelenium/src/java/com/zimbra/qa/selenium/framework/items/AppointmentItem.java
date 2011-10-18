package com.zimbra.qa.selenium.framework.items;

import java.util.*;

import org.apache.log4j.*;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.*;

public class AppointmentItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	////
	// Data values (SOAP)
	////
	protected String dSubject = null;
	protected String dLocation = null;
	protected String dContent = null; // TODO: need to separate HTML from text
	protected ZDate dStart = null;
	protected ZDate dEnd = null;
	
	////
	// GUI values
	////
	protected boolean gIsChecked = false;
	protected boolean gIsTagged = false;
	protected boolean gHasAttachments = false;
	protected String gSubject = null;
	protected String gFragment = null;
	protected String gLocation = null;
	protected String gStatus = null;
	protected String gCalendar = null;
	protected boolean gIsRecurring = false;
	protected boolean gIsAllDay = false;
	protected String gStart = null;
	protected String gEnd = null;
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
				
				// Parse the start time
				appt.dStart = new ZDate(sElement);

			}

			Element eElement = ZimbraAccount.SoapClient.selectNode(m, "//mail:e");
			if ( eElement != null ) {
				
				// Parse the start time
				appt.dEnd = new ZDate(eElement);

			}

			Element compElement = ZimbraAccount.SoapClient.selectNode(m, "//mail:comp");
			if ( compElement != null ) {

				// If there is a subject, save it
				appt.dSubject = compElement.getAttribute("name");
				
				// If there is a location, save it
				appt.dLocation = compElement.getAttribute("loc");
			}
			
			Element descElement = ZimbraAccount.SoapClient.selectNode(m, "//mail:desc");
			if ( descElement != null ) {
				
				// If there is a description, save it
				appt.dContent = descElement.getTextTrim();
				
			}

						
			return (appt);
			
		} catch (Exception e) {
			throw new HarnessException("Could not parse GetMsgResponse: "+ GetAppointmentResponse.prettyPrint(), e);
		} finally {
			if ( appt != null )	logger.info(appt.prettyPrint());
		}
		
	}

	public static AppointmentItem importFromSOAP(ZimbraAccount account, String query, ZDate start, ZDate end) throws HarnessException {
		
		try {
			account.soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ start.addDays(-7).toMillis() +"' calExpandInstEnd='"+ start.addDays(7).toMillis() +"'>" +
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
		sb.append("Location: ").append(dLocation).append('\n');
		sb.append("Start: ").append(dStart).append('\n');
		sb.append("End: ").append(dEnd).append('\n');
		return (sb.toString());
		
	}

	public String prettyPrintGUI() {
		StringBuilder sb = new StringBuilder();
		sb.append("GUI Data:\n");
		sb.append("Is Checked: ").append(gIsChecked).append('\n');
		sb.append("Is Tagged: ").append(gIsTagged).append('\n');
		sb.append("Is Recurring: ").append(gIsRecurring).append('\n');
		sb.append("Has Attachments: ").append(gHasAttachments).append('\n');
		sb.append("Subject: ").append(gSubject).append('\n');
		sb.append("Fragment: ").append(gFragment).append('\n');
		sb.append("Location: ").append(gLocation).append('\n');
		sb.append("Start: ").append(gStart).append('\n');
		sb.append("End: ").append(gEnd).append('\n');
		return (sb.toString());
	}


	public void setSubject(String subject) {
		dSubject = subject;
	}
	
	public String getSubject() {
		return (dSubject);
	}
	
	public void setLocation(String location) {
		dLocation = location;
	}
	
	public String getLocation() {
		return (dLocation);
	}

	public void setContent(String content) {
		dContent = content;
	}
	
	public String getContent() {
		return (dContent);
	}

	public void setStartTime(ZDate date) {
		dStart = date;
	}
	
	public ZDate getStartTime() {
		return (dStart);
	}

	public void setEndTime(ZDate date) {
		dEnd = date;
	}
	
	public ZDate getEndTime() {
		return (dEnd);
	}

	public boolean getGIsChecked() {
		return (gIsChecked);
	}

	public boolean getGIsTagged() {
		return (gIsTagged);
	}

	public boolean getGHasAttachment() {
		return (gHasAttachments);
	}

	public String getGSubject() {
		return (gSubject);
	}

	public String getGFragment() {
		return (gFragment);
	}

	public boolean getGIsRecurring() {
		return (gIsRecurring);
	}

	public boolean getGIsAllDay() {
		return (gIsAllDay);
	}

	public String getGStartDate() {
		return (gStart);
	}

	public void setGIsChecked(boolean checked) {
		gIsChecked = checked;		
	}

	public void setGIsTagged(boolean tagged) {
		gIsTagged = tagged;		
	}

	public void setGHasAttachment(boolean hasAttachment) {
		gHasAttachments = hasAttachment;
	}

	public void setGSubject(String subject) {
		gSubject = subject;		
	}

	public void setGFragment(String fragment) {
		gFragment = fragment;
	}

	public void setGIsRecurring(boolean recurring) {
		gIsRecurring = recurring;
	}

	public void setGIsAllDay(boolean allDay) {
		gIsAllDay = allDay;
	}

	public void setGStartDate(String start) {
		gStart = start;
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



}
