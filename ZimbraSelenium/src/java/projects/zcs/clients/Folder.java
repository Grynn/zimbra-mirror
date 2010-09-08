package projects.zcs.clients;

import framework.core.*;



public class Folder extends ZObject {

	public Folder() {
		super("folderCore", "Folder");
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
			rc = ClientSessionFactory.session().selenium().call("folderCore",  currentFolder, action, retryOnFalse, panel, param1);
		}
		return rc;		
	}	
	
	public  void zExpand(String folder) {
		ClientSessionFactory.session().selenium().call("folderExpandBtnCore",  folder, "click", true, "", "");
	}

	public  void zCollapse(String folder) {
		ClientSessionFactory.session().selenium().call("folderCollapseBtnCore",  folder, "click", true, "", "");
	}

	private  void _expndFldrIfRequired(String folder, String panel, String param1) {

	//	String rc = selenium.call("this.doZfolderExpandBtnExists("
	//			+ doubleQuote + folder + doubleQuote + ")");
		String rc = ClientSessionFactory.session().selenium().call("folderExpandBtnCore",  folder, "exists", true, panel, param1);
		if(rc.equals("true"))
			ClientSessionFactory.session().selenium().call("folderExpandBtnCore",  folder, "click", true, panel, param1);
	}	

}