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

function MemLeakTests(appCtxt, domain) {
/*
	ZmController.call(this, appCtxt);

	ZmCsfeCommand.setServerUri(location.protocol + "//" + domain + appCtxt.get(ZmSetting.CSFE_SERVER_URI));
	appCtxt.setAppController(this);
	appCtxt.setClientCmdHdlr(new ZmClientCmdHandler(appCtxt));
*/
	this._shell = appCtxt.getShell();

	this.startup();
};

//MemLeakTests.prototype = new ZmController;
//MemLeakTests.prototype.constructor = MemLeakTests;


MemLeakTests.run =
function(domain) {
	// Create the global app context
	var appCtxt = new ZmAppCtxt();

	//appCtxt.setIsPublicComputer(false);

	// Create the shell
	//var settings = appCtxt.getSettings();
	var shell = new DwtShell();

	appCtxt.setShell(shell);
	//appCtxt.setItemCache(new AjxCache());
	//appCtxt.setUploadManager(new AjxPost(appCtxt.getUploadFrameId()));

	// Go!
	new MemLeakTests(appCtxt, domain);
};


// Public methods

MemLeakTests.prototype.toString = 
function() {
	return "MemLeakTests";
};

MemLeakTests.prototype.startup =
function() {
	this._shell.getHtmlElement().innerHTML = "hello world.";
};
