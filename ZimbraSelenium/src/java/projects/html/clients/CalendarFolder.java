package projects.html.clients;

import framework.core.SelNGBase;



/**
 * @author raodv
 * Essentially same as Folder in html-client but with some extra apis like zCheck zunCheck etc.
 */
public class CalendarFolder extends Folder {

	public CalendarFolder() {
		super();
	} 
	/**
	 * Check Calendar folder
	 * @param folder
	 */
	public  void zCheck(String folder) {
		SelNGBase.selenium.get().call("folderCore_html",  folder+"_check", "click", true, "", "");
	}	
	/**
	 * unchecks Calendar folder
	 * @param folder
	 */
	public  void zUnCheck(String folder) {
		SelNGBase.selenium.get().call("folderCore_html",  folder+"_uncheck", "click", true, "", "");
	}		

	/**
	 * Checks if Calendar folder's checkbox is unchecked
	 * @param folder
	 */
	public  String zIsUnChecked(String folder) {
		return SelNGBase.selenium.get().call("folderCore_html",  folder+"_uncheck", "exists", true, "", "");
	}

	/**
	 * Checks if Calendar folder's checkbox is checked
	 * @param folder
	 */
	public  String zIsChecked(String folder) {
		return SelNGBase.selenium.get().call("folderCore_html",  folder+"_check", "exists", true, "", "");
	}	
}