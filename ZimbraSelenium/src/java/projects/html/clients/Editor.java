package projects.html.clients;

import framework.util.HarnessException;



public class Editor extends ZFieldObject {
	public Editor() {
		super("editorCore_html", "EmailBodyField");
	}
	public void zType(String data)  throws HarnessException  {
		if (data != "")
			ZObjectCore("", "type", true, data);
	}

}
