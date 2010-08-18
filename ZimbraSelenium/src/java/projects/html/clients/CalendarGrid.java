package projects.html.clients;

import framework.core.SelNGBase;



 
public class CalendarGrid extends ZObject {
	public CalendarGrid() {
		super("calGridCore_html", "HTML Calendar Grid");
	} 
	public String zGetApptDateTime(String apptName) {
		String res= SelNGBase.selenium.get().call("calGridCore_html", apptName, "getDT", true, "", "");
		return res.replace("  ","");
	}
	public String zGetApptCount(String apptName) {
		return SelNGBase.selenium.get().call("calGridCore_html", apptName, "getCount", true, "", "");

	}	
}	