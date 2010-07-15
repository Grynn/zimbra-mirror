package projects.zcs.clients;



public class Editor extends ZFieldObject {
	public Editor() {
		super("editorCore", "EmailBodyField");
	}
	public void zType(String data) {
		if (data != "")
			ZObjectCore("", "type", true, data, "", "", "");
	}

}
