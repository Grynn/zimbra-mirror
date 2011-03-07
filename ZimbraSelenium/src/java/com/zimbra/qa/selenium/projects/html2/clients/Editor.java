package com.zimbra.qa.selenium.projects.html2.clients;

import com.zimbra.qa.selenium.framework.util.HarnessException;



public class Editor extends ZFieldObject {
	public Editor() {
		super("editorCore_html", "EmailBodyField");
	}
	public void zType(String data)  throws HarnessException  {
		if (data != "")
			ZObjectCore("", "type", true, data);
	}

}
