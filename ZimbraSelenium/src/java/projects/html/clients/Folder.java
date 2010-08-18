package projects.html.clients;

import framework.core.SelNGBase;



/**
 * @author raodv
 *
 */
public class Folder extends ZObject {

	public Folder() {
		super("folderCore_html", "Folder");
	} 

	public  String ZObjectCore(String folderNameseparatedBySlash, String action, Boolean retryOnFalse,
			String panel, String param1) {
		String rc = "false";
		String[] fldrs = folderNameseparatedBySlash.split("/");

		for (int i = 0; i < fldrs.length; i++) {
			String currentFolder = fldrs[i];
			//dont wait if we are checking for not exist
			if(!action.equals("notexists"))
			    zWait(currentFolder, panel, param1);
			if (i < fldrs.length-1){
				this._expndFldrIfRequired(currentFolder, panel, param1);
				continue;
			}
			rc = SelNGBase.selenium.get().call("folderCore_html",  currentFolder, action, retryOnFalse, panel, param1);
		}
		return rc;		
	}	
	
	public  void zExpand(String folder) {
		SelNGBase.selenium.get().call("folderCore_html",  folder+"_expand", "click", true, "", "");
	}

	public  void zCollapse(String folder) {
		SelNGBase.selenium.get().call("folderCore_html",  folder+"_collapse", "click", true, "", "");
	}

	/**
	 * Clicks on the edit-link on folder-headers
	 * @param folder
	 */
	public  void zEdit(String folder) {
		SelNGBase.selenium.get().call("folderCore_html",  folder+"_edit", "click", true, "", "");
	}	


	
	
	private  void _expndFldrIfRequired(String folder, String panel, String param1) {
	//	String rc = selenium.call("this.doZfolderExpandBtnExists("
	//			+ doubleQuote + folder + doubleQuote + ")");
		String rc = SelNGBase.selenium.get().call("folderCore_html",  folder+"_expand", "exists", true, panel, param1);
		if(rc.equals("true"))
			SelNGBase.selenium.get().call("folderCore_html",  folder+"_expand", "click", true, panel, param1);
	}	

}