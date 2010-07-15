package projects.zcs.clients;

import org.testng.Assert;

public class ListItem extends ZObject{
	public ListItem() {
		super("listItemCore", "ListItem");
	} 
	public ListItem(String coreName, String objTypeName) {
		super(coreName, objTypeName);
	} 
	/**
	 * Clicks on a listItem in a specific list when there are multiple lists
	 * @param objNameOrId listItemName or listItemId
	 * @param listNumber if there are two lists, enter "1" for list1 and "2" for list2
	 */
	public void zClickItemInSpecificList(String objNameOrId, String listNumber) {
		ZObjectCore(objNameOrId, "click", true, "", "", listNumber, "");
	}
	/**
	 * Clicks on a LINK within a listItem in a specific list when there are multiple lists
	 * @param listItemName Some text thats unique to list item
	 * @param listNumber if there are two lists, enter "1" for list1 and "2" for list2
	 * @param linkName name of the link to click
	 */
	public void zClickLinkWithInListItemInSpecificList(String listItemName, String listNumber, String linkName) {
		ZObjectCore(listItemName, "clickLink="+linkName, true, "", "", listNumber, "");
	}
	
	/**
	 * DblClicks on a listItem in a specific list when there are multiple lists
	 * @param objNameOrId listItemName or listItemId
	 * @param listNumber if there are two lists, enter "1" for list1 and "2" for list2
	 */	
	public void zDblClickItemInSpecificList(String objNameOrId,  String listNumber) {
	    ZObjectCore(objNameOrId, "dblclick", true, "", "", listNumber, ""); 
	}
	/**
	 * @param objNameOrId listItemName or listItemId
 	 * @param objNumber If there are multiple items with same name, pass "1" to select 1st item, "2" to select 2nd etc 
	 * @param listNumber if there are two lists, enter "1" for list1 and "2" for list2
	 */
	public void zClickItemInSpecificList(String objNameOrId, String objNumber, String listNumber) {
		ZObjectCore(objNameOrId, "click", true, "", objNumber, listNumber, "");
	}
	
	/**
	 *  Clicks on a listItem in a specific list (within a dialog) when there are multiple lists
	 * @param objNameOrId listItemName or listItemId
 	 * @param objNumber If there are multiple items with same name, pass "1" to select 1st item, "2" to select 2nd etc 
	 * @param listNumber if there are two lists, enter "1" for list1 and "2" for list2
	 */	
	public void zClickItemInSpecificListInDlg(String objNameOrId, String objNumber, String listNumber) {
		ZObjectCore(objNameOrId, "click", true, "dialog", objNumber, listNumber, "");
	}
	/**
	 *  Verifies if a listItem exists in a specific list (within a dialog) when there are multiple lists
	 * @param objNameOrId listItemName or listItemId
 	 * @param objNumber If there are multiple items with same name, pass "1" to select 1st item, "2" to select 2nd etc 
	 * @param listNumber if there are two lists, enter "1" for list1 and "2" for list2
	 */	
	public void zVerifyItemInSpecificListInDlg(String objNameOrId, String objNumber, String listNumber) {
	    	String actual = ZObjectCore(objNameOrId, "exists", true, "dialog", objNumber, listNumber, "");
		if(actual.indexOf("false") == -1)//convert OK to true and OK,false to false
			actual = "true";
		else
			actual = "false";
		Assert.assertEquals(actual,"true", objTypeName+"(" + objNameOrId
				+ ") Not Found.");		
	}
	/**
	 *  Verifies if a listItem exists in a specific list  when there are multiple lists
	 * @param objNameOrId listItemName or listItemId
 	 * @param objNumber If there are multiple items with same name, pass "1" to select 1st item, "2" to select 2nd etc 
	 * @param listNumber if there are two lists, enter "1" for list1 and "2" for list2
	 */	
	public void zVerifyItemInSpecificList(String objNameOrId, String objNumber, String listNumber) {
	    	String actual = ZObjectCore(objNameOrId, "exists", true, "", objNumber, listNumber, "");
		if(actual.indexOf("false") == -1)//convert OK to true and OK,false to false
			actual = "true";
		else
			actual = "false";
		Assert.assertEquals(actual,"true", objTypeName+"(" + objNameOrId
				+ ") Not Found.");			
	}	
	/**
	 *  Verifies if a listItem exists in a specific list (within a dialog) when there are multiple lists
	 * @param objNameOrId listItemName or listItemId
 	 * @param objNumber If there are multiple items with same name, pass "1" to select 1st item, "2" to select 2nd etc 
	 * @param listNumber if there are two lists, enter "1" for list1 and "2" for list2
	 */	
	public void zVerifyItemInSpecificListInDlgNotExist(String objNameOrId, String objNumber, String listNumber) {
	    	String actual = ZObjectCore(objNameOrId, "notexist", false, "dialog", objNumber, listNumber, "");
		if(actual.indexOf("false") == -1)//convert OK to true and OK,false to false
			actual = "true";
		else
			actual = "false";	    
		Assert.assertEquals("true", actual, objTypeName+"(" + objNameOrId
			+ ") Found, which should not be present.");
	}
	/**
	 *  Verifies if a listItem exists in a specific list  when there are multiple lists
	 * @param objNameOrId listItemName or listItemId
 	 * @param objNumber If there are multiple items with same name, pass "1" to select 1st item, "2" to select 2nd etc 
	 * @param listNumber if there are two lists, enter "1" for list1 and "2" for list2
	 */	
	public void zVerifyItemInSpecificListNotExist(String objNameOrId, String objNumber, String listNumber) {
		String actual = ZObjectCore(objNameOrId, "notexist", false, "", objNumber, listNumber, "");
		if(actual.indexOf("false") == -1)//convert OK to true and OK,false to false
			actual = "true";
		else
			actual = "false";	    
		Assert.assertEquals("true", actual, objTypeName+"(" + objNameOrId
			+ ") Found, which should not be present.");		
	}	
	
	public void zVerifyIsTagged(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "isTagged");
		Assert.assertEquals("true", actual);
	}
	public void zVerifyIsNotTagged(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "isNotTagged");
		Assert.assertEquals("true", actual);
	}
	public void zVerifyHasAttachment(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "hasAttachment");
		Assert.assertEquals("true", actual);
	}
	public void zVerifyHasNoAttachment(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "hasNoAttachment");
		Assert.assertEquals("true", actual);
	}		
	public void zVerifyHasHighPriority(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "hasHighPriority");
		Assert.assertEquals("true", actual);
	}	
	public void zVerifyHasLowPriority(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "hasLowPriority");
		Assert.assertEquals("true", actual);
	}	
	public void zVerifyIsSelected(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "isSelected");
		Assert.assertEquals("true", actual);
	}	
}
