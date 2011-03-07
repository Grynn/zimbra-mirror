package com.zimbra.qa.selenium.projects.html2.clients;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;




 
public class CalendarGrid extends ZObject {
	public CalendarGrid() {
		super("calGridCore_html", "HTML Calendar Grid");
	} 
	public String zGetApptDateTime(String apptName) throws HarnessException   {
		String res= ClientSessionFactory.session().selenium().call("calGridCore_html", apptName, "getDT", true, "", "");
		return res.replace("  ","");
	}
	public String zGetApptCount(String apptName)  throws HarnessException  {
		return ClientSessionFactory.session().selenium().call("calGridCore_html", apptName, "getCount", true, "", "");

	}	
}	