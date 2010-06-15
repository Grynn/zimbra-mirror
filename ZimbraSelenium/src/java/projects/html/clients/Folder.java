package projects.html.clients;



/**
 * @author raodv
 *
 */
public class Folder extends ZObject {

	public Folder() {
		super("folderCore_html", "Folder");
	} 



	
	public  String ZObjectCore(String folderNameseparatedBySlash, String action,
			String panel, String param1) {
		String rc = "false";
		String[] fldrs = folderNameseparatedBySlash.split("/");

		for (int i = 0; i < fldrs.length; i++) {
			String currentFolder = fldrs[i];
			//dont wait if we are checking for not exist
			if(!action.equals("notexist"))
			    zWait(currentFolder, panel, param1);
			if (i < fldrs.length-1){
				this._expndFldrIfRequired(currentFolder, panel, param1);
				continue;
			}
			rc = selenium.call("folderCore_html",  currentFolder, action, panel, param1);
		}
		return rc;		
	}	
	
	public  void zExpand(String folder) {
		selenium.call("folderCore_html",  folder+"_expand", "click","", "");
	}

	public  void zCollapse(String folder) {
		selenium.call("folderCore_html",  folder+"_collapse", "click","", "");
	}

	/**
	 * Clicks on the edit-link on folder-headers
	 * @param folder
	 */
	public  void zEdit(String folder) {
		selenium.call("folderCore_html",  folder+"_edit", "click","", "");
	}	


	
	
	private  void _expndFldrIfRequired(String folder, String panel, String param1) {
	//	String rc = selenium.call("this.doZfolderExpandBtnExists("
	//			+ doubleQuote + folder + doubleQuote + ")");
		String rc = selenium.call("folderCore_html",  folder+"_expand", "exists", panel, param1);
		if(rc.equals("true"))
			selenium.call("folderCore_html",  folder+"_expand", "click", panel, param1);
	}	

}