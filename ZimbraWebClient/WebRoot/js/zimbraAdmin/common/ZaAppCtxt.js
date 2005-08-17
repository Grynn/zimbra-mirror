/**
* Does nothing.
* @constructor
* @class
* This class is a container for stuff that the overall app may want to know about. That
* includes environment information (such as whether the browser in use is public), and
* stuff that is common to the app as a whole (such as tags). The methods are almost all
* just getters and setters.
*/
function ZaAppCtxt() {
}

ZaAppCtxt.LABEL = "appCtxt";

ZaAppCtxt.prototype.toString = 
function() {
	return "ZaAppCtxt";
}

/**
* Gets the app context from the given shell.
*
* @param shell		the shell
* @returns			the app context
*/
ZaAppCtxt.getFromShell =
function(shell) {
	return shell.getData(ZaAppCtxt.LABEL);
}



ZaAppCtxt.prototype.setAppController =
function(appController) {
	this._appController = appController;
}

ZaAppCtxt.prototype.getAppController =
function() {
	return this._appController;
}


ZaAppCtxt.prototype.getApp =
function(appName) {
	return this._appController.getApp(appName);
}

ZaAppCtxt.prototype.getAppViewMgr =
function() {
	return this._appController.getAppViewMgr();
}

ZaAppCtxt.prototype.setClientCmdHdlr =
function(clientCmdHdlr) {
	this._clientCmdHdlr = clientCmdHdlr;
}

ZaAppCtxt.prototype.getClientCmdHdlr =
function() {
	return this._clientCmdHdlr;
}

ZaAppCtxt.prototype.getSearchController =
function() {
	return this._appController.getSearchController();
}

ZaAppCtxt.prototype.getOverviewPanelController =
function() {
	return this._appController.getOverviewPanelController();
}

ZaAppCtxt.prototype.getLoginDialog =
function(isAdmin) {
	if (!this._loginDialog)
		this._loginDialog = new ZaLoginDialog(this.getShell(), null, null, isAdmin);
	return this._loginDialog;
}

ZaAppCtxt.prototype.getMsgDialog =
function() {
	if (!this._msgDialog)
		this._msgDialog = new ZaMsgDialog(this.getShell());
	return this._msgDialog;
}

ZaAppCtxt.prototype.getShell =
function() {
	return this._shell;
}

ZaAppCtxt.prototype.setShell =
function(shell) {
	this._shell = shell;
	shell.setData(ZaAppCtxt.LABEL, this);
}


ZaAppCtxt.prototype.getFolderTree =
function() {
	return this._folderTree;
}

ZaAppCtxt.prototype.setFolderTree =
function(folderTree) {
	this._folderTree = folderTree;
}

ZaAppCtxt.prototype.getUsername = 
function() { 
	return this._username;
}

ZaAppCtxt.prototype.setUsername = 
function(username) {
	this._username = username;
}

ZaAppCtxt.prototype.getCurrentSearch =
function() { 
	return this._currentSearch;
}

ZaAppCtxt.prototype.setCurrentSearch =
function(search) {
	this._currentSearch = search;
}
