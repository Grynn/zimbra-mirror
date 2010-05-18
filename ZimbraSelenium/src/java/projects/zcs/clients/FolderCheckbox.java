package projects.zcs.clients;


public class FolderCheckbox extends CheckBox{
	protected boolean isCheckbox = true;//if true then moveMouse's xy is adjusted differently for zActivate

	public FolderCheckbox() {
		super("folderCheckboxCore", "Folder Checkbox");
	} 	
}
