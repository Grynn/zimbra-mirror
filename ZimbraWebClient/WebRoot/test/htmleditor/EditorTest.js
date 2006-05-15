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
