/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
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
function EditorTest() {};

EditorTest.content = [ "<h1>Test</h1><p>a paragraph here</p>",
		       "<table width='100%' style='border: 1px solid #aaf; border-collapse: collapse;'>",
		       "<tr> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> </tr>",
		       "<tr> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> </tr>",
		       "<tr> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> </tr>",
		       "<tr> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> </tr>",
		       "<tr> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> </tr>",
		       "<tr> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> <td style='border: 1px solid #aaf'><br /></td> </tr>",
		       "</table>",
		       "<p>another paragraph here</p>" ].join("");

EditorTest.run = function() {
	DBG = new AjxDebug(AjxDebug.NONE, null, false);

	var appCtxt = new ZmAppCtxt();
	var shell = new DwtShell();
	appCtxt.setShell(shell);

	// dirty hacks to be able to run without this
	var kbMgr = shell.getKeyboardMgr();
	kbMgr.registerKeyMap(new ZmKeyMap());
	kbMgr.registerGlobalKeyActionHandler({
		    handleKeyAction : function() {}
		});
	kbMgr.__currTabGroup = {
	    setFocusMember : function() { return false; }
	};

	var cont = new DwtComposite(shell, null, DwtControl.ABSOLUTE_STYLE);
	cont.zShow(true);
	cont.setLocation(10, 10);
	cont.setSize(800, 550);

	var editor = new ZmHtmlEditor(cont, null, EditorTest.content, DwtHtmlEditor.HTML, appCtxt);
	editor.setSize(800, 500);
};
