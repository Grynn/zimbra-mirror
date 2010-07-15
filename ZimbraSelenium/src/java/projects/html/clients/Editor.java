package projects.html.clients;



public class Editor extends ZFieldObject {
	public Editor() {
		super("editorCore_html", "EmailBodyField");
	}
	public void zType(String data) {
		if (data != "")
			ZObjectCore("", "type", true, data);
	}

}
