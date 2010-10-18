package projects.zcs.clients;

import framework.util.HarnessException;



public class Editor extends ZFieldObject {
	public Editor() {
		super("editorCore", "EmailBodyField");
	}
	public void zType(String data) throws HarnessException   {
		if (data.length() != 0)
			ZObjectCore("", "type", true, data, "", "", "");
	}

}
