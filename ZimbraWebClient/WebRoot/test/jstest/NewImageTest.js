/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

function NewImageTest(parent) {
}

NewImageTest.I_FLAG_ON  = ["FlagOnIcon", 16, 16];
NewImageTest.I_FLAG_OFF  = ["FlagOffIcon", 16, 16];

NewImageTest.run =
function() {
	var shell = new DwtShell();
	//shell.getHtmlElement().innerHTML = AjxImg.getImageHtml(NewImageTest.I_FLAG_ON);
	var div = document.createElement("div");
	AjxImg.setImage(div, NewImageTest.I_FLAG_ON);
	shell.getHtmlElement().appendChild(div);
}