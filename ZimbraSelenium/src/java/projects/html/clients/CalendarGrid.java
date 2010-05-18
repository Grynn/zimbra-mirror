package projects.html.clients;



 
public class CalendarGrid extends ZObject {
	public CalendarGrid() {
		super("calGridCore_html", "HTML Calendar Grid");
	} 
	public String zGetApptDateTime(String apptName) {
		String res= selenium.call("calGridCore_html", apptName, "getDT", "", "");
		return res.replace("  ","");
	}
	public String zGetApptCount(String apptName) {
		String res= selenium.call("calGridCore_html", apptName, "getCount", "", "");
		return res;
	}	
}	