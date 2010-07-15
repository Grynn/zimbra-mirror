package projects.html.clients;



/**
 * @author raodv
 * This works for html-menus(not ajax menus) with ids and/or name value. It has built-in time-sync and also works for 
 * multiple items with same name. 
 */
public class HtmlMenu extends ZObject {
	public HtmlMenu() {
		super("htmlMenuCore_html", "Html Menu");
	} 	
	public void zClick(String objNameOrId,  String itemToSelect) {
		 ZObjectCore(objNameOrId,  "click", true, itemToSelect);
	}
	public void zClick(String objNameOrId,  String itemToSelect, String itemNumber) {
		 ZObjectCore(objNameOrId,  "click", true, itemToSelect, itemNumber);
	}
	/**
	 * @param objNameOrId
	 * @param itemToSelect
	 * @param itemNumber if there are multiple items in the menu, enter the item number. starts from 1
	 * @param menuNumber if there are multiple menus iteslf, pass the menu number. Count starts from top-left of the screen to bottom-right
	 */
	public void zClick(String objNameOrId,  String itemToSelect, String itemNumber, String menuNumber) {
		 ZObjectCore(objNameOrId,  "click", true, itemToSelect, itemNumber, menuNumber);
	}
	
	/**
	 * @param objNameOrId
	 * @param itemToSelect
	 * @param itemNumber if there are multiple items in the menu, enter the item number. starts from 1
	 * @param menuNumber if there are multiple menus iteslf, pass the menu number. Count starts from top-left of the screen to bottom-right
	 */
	public void zClickMenuByLocation(String objNameOrId,  String itemToSelect, String menuNumber) {
		 ZObjectCore( objNameOrId,  "click", true, itemToSelect, "", menuNumber);
	}	
	/**
	 * @param objNameOrId
	 * @param itemToSelect
	 * @param itemNumber if there are multiple items in the menu, enter the item number. starts from 1
	 * @param menuNumber if there are multiple menus iteslf, pass the menu number. Count starts from top-left of the screen to bottom-right
	 */
	public void zClickMenuByLocation(String objNameOrId,  String itemToSelect, String itemNumber, String menuNumber) {
		 ZObjectCore( objNameOrId,  "click", true, itemToSelect, itemNumber, menuNumber);
	}		
	public String zGetCount(String objNameOrId) {
		 return ZObjectCore(objNameOrId,  "getCount");
	}
	public String zGetAllItemNames(String objNameOrId) {
		 return ZObjectCore( objNameOrId,  "getAllItems");
	}	
	public String zGetSelectedItemName(String objNameOrId) {
		 return ZObjectCore( objNameOrId,  "getSelected");
	}	
	
	public String zGetCount(String objNameOrId, String menuNumber) {
		 return ZObjectCore(objNameOrId,  "getCount", true, "",  "", menuNumber);
	}
	public String zGetAllItemNames(String objNameOrId, String menuNumber) {
		 return ZObjectCore( objNameOrId,  "getAllItems", true, "",  "", menuNumber);
	}	
	public String zGetSelectedItemName(String objNameOrId, String menuNumber) {
		 return ZObjectCore( objNameOrId,  "getSelected", true, "",  "", menuNumber);
	}	
	
}
