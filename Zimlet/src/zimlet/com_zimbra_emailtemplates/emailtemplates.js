/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV
 */

function com_zimbra_emailtemplates() {
}
com_zimbra_emailtemplates.prototype = new ZmZimletBase();
com_zimbra_emailtemplates.prototype.constructor = com_zimbra_emailtemplates;

//--------------------------------------------------------------------------------------------------
// INIT AND INITIALIZE TOOLBAR MENU BUTTON
//--------------------------------------------------------------------------------------------------
com_zimbra_emailtemplates.prototype.init =
function() {
	this._folderPath = this.getUserProperty("etemplates_sourcefolderPath");
};

com_zimbra_emailtemplates.prototype.initializeToolbar =
function(app, toolbar, controller, viewId) {
	this._currentViewId = viewId;
	if (!this._viewIdAndMenuMap) {
		this._viewIdAndMenuMap = [];
	}
	this.viewId = viewId;
	if (viewId.indexOf("COMPOSE") >= 0 || viewId == "APPT") {
		if (toolbar.getOp("EMAIL_TEMPLATES_ZIMLET_TOOLBAR_BUTTON")) {
			return;
		}
		//get the index of View menu so we can display it after that.
		var buttonIndex = 3;

		//create params obj with button details
		var buttonArgs = {
			text	: "Templates",
			tooltip: "This button shows up in Conversation view, traditional view, and in convlist view",
			index: buttonIndex, //position of the button
			image: "zimbraicon" //icon
		};


		//toolbar.createOp api creates the button with some id and  params containing button details.
		var button = toolbar.createOp("EMAIL_TEMPLATES_ZIMLET_TOOLBAR_BUTTON", buttonArgs);
		var menu = new ZmPopupMenu(button); //create menu
		button.setMenu(menu);//add menu to button
		button.noMenuBar = true;
		this._viewIdAndMenuMap[viewId] = {menu:menu, controller:controller, button:button};
		button.removeAllListeners();
		button.removeDropDownSelectionListener();
		button.addSelectionListener(new AjxListener(this, this._addMenuItems, [button, menu]));
		button.addDropDownSelectionListener(new AjxListener(this, this._addMenuItems, [button, menu]));
	}
};

com_zimbra_emailtemplates.prototype._addMenuItems =
function(button, menu) {
	if (!menu._loaded) {
		this._getRecentEmails(false);
		menu._loaded = true;
	} else {
		var bounds = button.getBounds();
		menu.popup(0, bounds.x, bounds.y + bounds.height, false);
	}
};


//--------------------------------------------------------------------------------------------------
// TEST TEMPLATE FOR GENERIC WORDS AND THEN INSERT
//--------------------------------------------------------------------------------------------------
com_zimbra_emailtemplates.prototype._getRecentEmails =
function(removeChildren) {
	if (this._folderPath == "") {
		this._getRecentEmailsHdlr(removeChildren);
		return;
	}
	var getHtml = appCtxt.get(ZmSetting.VIEW_AS_HTML);
	var callbck = new AjxCallback(this, this._getRecentEmailsHdlr, removeChildren);
	var _types = new AjxVector();
	_types.add("MSG");

	appCtxt.getSearchController().search({query: ["in:(\"",this._folderPath,"\")"].join(""), userText: true, limit:25,  searchFor: ZmId.SEARCH_MAIL,
		offset:0, types:_types, noRender:true, getHtml: getHtml, callback:callbck, errorCallback:callbck});
};

com_zimbra_emailtemplates.prototype._getRecentEmailsHdlr =
function(removeChildren, result) {
	var menu = this._viewIdAndMenuMap[this._currentViewId].menu;
	if (removeChildren) {
		menu.removeChildren();
	}
	if (result) {
		if (result instanceof ZmCsfeException) {
			appCtxt.setStatusMsg("Template's folder does not exist " + result.getErrorMsg(), ZmStatusView.LEVEL_WARNING);
			this._addStandardMenuItems(menu);
			return;
		}
		var array = result.getResponse().getResults("MSG").getVector().getArray();
		for (var i = 0; i < array.length; i++) {
			var msg = array[i];
			var id = msg.id;
			var mi = menu.createMenuItem(id, {image:"zimbraIcon", text:msg.subject, style:DwtMenuItem.CASCADE_STYLE});
			var submenu = new ZmPopupMenu(mi); //create submenu
			mi.setMenu(submenu);//add submenu to menuitem

			var subMi = submenu.createMenuItem("subMenu_" + Dwt.getNextId(), {image:"Edit", text:"Insert (body only)"});
			subMi.addSelectionListener(new AjxListener(this, this._insertMsg, {msg:msg, insertMode:"body"}));
			subMi = submenu.createMenuItem("subMenu_" + Dwt.getNextId(), {image:"Edit", text:"Insert (body & subject)"});
			subMi.addSelectionListener(new AjxListener(this, this._insertMsg, {msg:msg, insertMode:"bodyAndSubject"}));
			subMi = submenu.createMenuItem("subMenu_" + Dwt.getNextId(), {image:"Edit", text:"Insert (body, subject & participants)"});
			subMi.addSelectionListener(new AjxListener(this, this._insertMsg, {msg:msg, insertMode:"all"}));
		}
		if (array.length != 0) {
			mi = menu.createMenuItem(id, {style:DwtMenuItem.SEPARATOR_STYLE});
		}
	}

	this._addStandardMenuItems(menu);

	var button = this._viewIdAndMenuMap[this._currentViewId].button;
	var bounds = button.getBounds();
	menu.popup(0, bounds.x, bounds.y + bounds.height, false);
};

com_zimbra_emailtemplates.prototype._addStandardMenuItems =
function(menu) {
	var mi = menu.createMenuItem("reloadTemplates", {image:"Refresh", text:"Reload Templates"});
	mi.addSelectionListener(new AjxListener(this, this._getRecentEmails, true));
	var mi = menu.createMenuItem("preferences", {image:"Resource", text:"Preferences"});
	mi.addSelectionListener(new AjxListener(this, this._displayPrefDialog));
};


//--------------------------------------------------------------------------------------------------
// LOAD SELECTED MESSAGE/TEMPLATE
//--------------------------------------------------------------------------------------------------
com_zimbra_emailtemplates.prototype._insertMsg =
function(params) {
	this.msg = params.msg;
	this.msg.load({callback: new AjxCallback(this, this._handleLoadedMsg, params.insertMode)});
};

com_zimbra_emailtemplates.prototype._handleLoadedMsg =
function(insertMode) {
	this.viewId = appCtxt.getCurrentViewId(); // make sure we use proper viewId to support multiple-compose views
	var controller = this._viewIdAndMenuMap[this._currentViewId].controller;
	var composeView = appCtxt.getCurrentView();
	var currentBodyContent = currentBodyContent = appCtxt.getCurrentView().getHtmlEditor().getContent();
	this._composeMode = appCtxt.getCurrentView().getHtmlEditor().getMode();
	var templateBody = this.getTemplateContent(this.msg, this._composeMode);
	var params = {templateSubject:this.msg.subject, templateBody: templateBody,  currentBodyContent:currentBodyContent, composeView:composeView, insertMode:insertMode};
	this._testTemplateContentForKeys(params);
};

//--------------------------------------------------------------------------------------------------
// TEST TEMPLATE FOR GENERIC WORDS AND THEN INSERT
//--------------------------------------------------------------------------------------------------

com_zimbra_emailtemplates.prototype._testTemplateContentForKeys = function(params) {
	var regex = new RegExp("\\breplace__[a-z0-9A-Z]*", "ig");
	var templateBody = params.templateBody;
	var templateSubject = params.templateSubject;

	var bodyArry = templateBody.match(regex);
	var subjectArry;
	if (templateSubject) {
		subjectArry = templateSubject.match(regex);
	}
	if (bodyArry != null || subjectArry != null) {
		params["bodyArry"] = bodyArry;
		params["subjectArry"] = subjectArry;
		this._showReplaceStringsDlg(params);
	} else {
		this._doInsert(params.composeView, params.templateSubject, params.templateBody, params.currentBodyContent, params.insertMode);
	}
};

com_zimbra_emailtemplates.prototype._showReplaceStringsDlg =
function(params) {
	if (this.replaceDlg) {
		this._createReplaceView(params);
		this.replaceDlg.popup();
		return;
	}
	this.replaceDlgView = new DwtComposite(this.getShell());
	this.replaceDlgView.getHtmlElement().style.overflow = "auto";
	this._createReplaceView(params);
	this.replaceDlg = this._createDialog({title:"Replace Template Data", view:this.replaceDlgView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this.replaceDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._replaceOKBtnListener, params));
	this.replaceDlg.popup();
};

com_zimbra_emailtemplates.prototype._createReplaceView =
function(params) {
	var bodyArry = params.bodyArry;
	var subjectArry = params.subjectArry;
	var dataArry = [];
	if (subjectArry != null && subjectArry != undefined) {
		dataArry = subjectArry;
	}
	if (bodyArry != null && bodyArry != undefined) {
		dataArry = dataArry.concat(bodyArry);
	}
	var tmpArry = [];
	for (var j = 0; j < dataArry.length; j++) {
		tmpArry.push(AjxStringUtil.trim(dataArry[j].toLowerCase()));
	}
	dataArry = emailtemplates_unique(tmpArry);
	this._replaceFieldIdsMap = [];
	var i = 0;
	var html = new Array();
	html[i++] = "<div class='emailTemplates_yellow'>Please replace the following generic text(s) with valid data</div><BR/>";
	html[i++] = "<TABLE  class='emailTemplates_table' width=100% cellspacing=3 cellpadding=3>";
	for (var k = 0; k < dataArry.length; k++) {
		var key = dataArry[k];
		var id = Dwt.getNextId();
		this._replaceFieldIdsMap.push({key:key, id:id});
		html[i++] = ["<TR><TD><DIV style='font-weight:bold;'>",key,"</div></TD><TD><input type=text id='",id,"'></input></TD></TR>"].join("");
	}
	html[i++] = "</TABLE>";
	this.replaceDlgView.getHtmlElement().innerHTML = html.join("");
};

com_zimbra_emailtemplates.prototype._replaceOKBtnListener =
function(params) {
	var insertMode = params.insertMode;
	var templateBody = params.templateBody;
	var templateSubject = params.templateSubject;
	var currentBodyContent = params.currentBodyContent;
	for (var i = 0; i < this._replaceFieldIdsMap.length; i++) {
		var obj = this._replaceFieldIdsMap[i];
		var key = obj.key;
		var regEx = new RegExp(key, "ig");
		var val = document.getElementById(obj.id).value;
		if (val == "") {
			continue;
		}
		if (insertMode == "bodyAndSubject" || insertMode == "all") {
			templateSubject = templateSubject.replace(regEx, val);
		}
		templateBody = templateBody.replace(regEx, val);
	}
	this.replaceDlg.popdown();
	this._doInsert(params.composeView, templateSubject, templateBody, currentBodyContent, insertMode);
};

com_zimbra_emailtemplates.prototype._doInsert =
function(composeView, templateSubject, templateBody, currentBodyContent, insertMode) {
	//insert subject
	if (insertMode == "bodyAndSubject" || insertMode == "all") {
		if (this.viewId == "APPT") {
			composeView._apptEditView._subjectField.setValue(templateSubject);
		} else {
			composeView._subjectField.value = templateSubject;
		}
	}
	//insert to & cc
	if (insertMode == "all") {
		var addrs = this.msg.participants.getArray();
		var toStr = [];
		var ccStr = [];
		for (var i = 0; i < addrs.length; i++) {
			var email = addrs[i];
			var name = "";
			if (email.name && email.name != "") {
				name = ["\"",email.name,"\" <", email.address,">"].join("");
			} else {
				name = email.address;
			}
			if (email.type == AjxEmailAddress.TO) {
				toStr.push(name);
			} else if (email.type == AjxEmailAddress.CC) {
				ccStr.push(name);
			}
		}

		if (this.viewId == "APPT") {
			try{
				composeView._apptEditView._attInputField.PERSON.setValue(toStr.concat(ccStr).join(";"));
			} catch(e) {
				appCtxt.setStatusMsg("Could not insert appointment's attendees " + result.getErrorMsg(), ZmStatusView.LEVEL_WARNING);
			}
		} else {
			if (toStr.length != 0) {
				composeView.setAddress(AjxEmailAddress.TO, toStr.join(";"));
			}
			if (ccStr.length != 0) {
				composeView.setAddress(AjxEmailAddress.CC, ccStr.join(";"));
			}
		}
	}

	//insert body
	var saperator = "\r\n";
	if ((this._composeMode == DwtHtmlEditor.HTML)) {
		saperator = "</br>";
	}
	var newData = [templateBody, saperator, currentBodyContent,saperator].join("");
	if (this.viewId == "APPT") {
		composeView.getHtmlEditor().setContent(newData);
	} else {
		composeView._htmlEditor.setContent(newData);
	}
};

com_zimbra_emailtemplates.arrayContainsElement =
function(array, val) {
	for (var i = 0; i < array.length; i++) {
		if (array[i] == val) {
			return true;
		}
	}
	return false;
}

function emailtemplates_unique(b) {
	var a = [], i, l = b.length;
	for (i = 0; i < l; i++) {
		if (!com_zimbra_emailtemplates.arrayContainsElement(a, b[i])) {
			a.push(b[i]);
		}
	}
	return a;
}

com_zimbra_emailtemplates.prototype.getTemplateContent = function(note, mode) {
	var body = "";
	var body = note.getBodyContent();
	if (note.isHtmlMail() && mode == ZmMimeTable.TEXT_PLAIN) {
		var div = document.createElement("div");
		div.innerHTML = note.getBodyContent();
		return AjxStringUtil.convertHtml2Text(div);
	} else if (!note.isHtmlMail() && mode == ZmMimeTable.TEXT_HTML) {
		return AjxStringUtil.convertToHtml(note.getBodyContent());
	} else {
		return body;
	}
};

//--------------------------------------------------------------------------------------------------
// SHOW PREFERENCE DIALOG
//--------------------------------------------------------------------------------------------------
com_zimbra_emailtemplates.prototype._displayPrefDialog =
function() {
	if (this.prefDlg) {
		this.prefDlg.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	//this.pView.setSize("200", "50");
	this.pView.getHtmlElement().style.overflow = "auto";
	this.pView.getHtmlElement().innerHTML = this._createPreferenceView();
	this.prefDlg = this._createDialog({title:"Zimlet Preferences", view:this.pView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this.prefDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._prefOKBtnListener));
	this._initializePrefDialog();
	this.prefDlg.popup();
};

com_zimbra_emailtemplates.prototype._createPreferenceView =
function() {
	var str = "Templates folder not set";
	if (this._folderPath != "") {
		str = this._folderPath;
	}
	var html = new Array();
	var i = 0;
	html[i++] = "<TABLE cellspacing=3 cellpadding=3>";
	html[i++] = ["<TR><TD><DIV style='font-weight:bold;'>Template Folder's path:</div></TD><TD><DIV style='color:blue;font-weight:bold;' id='emailtemplates_folderInfo'>",str,"</div></TD></TR>"].join("");
	html[i++] = "<TR><TD colspan=2 align='center'><DIV id='emailtemplates_folderLookupDiv'></DIV></TD></TR></TABLE>";

	html[i++] = "<br/><br/><div class='emailTemplates_yellow'>Template Creation Tips: </div><div  class='emailTemplates_yellowNormal'><br/>1. You can use replace__<somename> to create generic words <br/> and Zimlet will alert you to replace them";
	html[i++] = "<br/>For example: You can have  <strong>hi replace__firstName</strong> in the body or in the subject";
	html[i++] = "<br/>2.  replace__ is word replace followed by <strong>two underscores</strong>";
	html[i++] = "<br/>3. Do not use signatures in templates as you would already have it in mail you are composing</div><BR/>";
	return html.join("");
};

com_zimbra_emailtemplates.prototype._initializePrefDialog =
function() {
	var btn = new DwtButton({parent:this.getShell()});
	btn.setText("Set Templates Folder");
	btn.setImage("Search");
	btn.setToolTipContent("Please select a folder where Templates are stored");
	btn.addSelectionListener(new AjxListener(this, this._setFolderBtnListener));
	document.getElementById("emailtemplates_folderLookupDiv").appendChild(btn.getHtmlElement());
};


com_zimbra_emailtemplates.prototype._prefOKBtnListener =
function() {
	if (this.needRefresh) {
		this.setUserProperty("etemplates_sourcefolderPath", this._folderPath);
		var callback = new AjxCallback(this, this._handleSaveProperties, this.needRefresh);
		this.saveUserProperties(callback);
	}
	this.prefDlg.popdown();
};

com_zimbra_emailtemplates.prototype._setFolderBtnListener =
function() {
	if (!this._chooseFolderDialog) {
		AjxDispatcher.require("Extras");
		this._chooseFolderDialog = new ZmChooseFolderDialog(appCtxt.getShell());
	}
	this._chooseFolderDialog.reset();
	this._chooseFolderDialog.registerCallback(DwtDialog.OK_BUTTON, this._chooseFolderOkBtnListener, this, this._chooseFolderDialog);

	var params = {
		treeIds:		[ZmOrganizer.FOLDER],
		title:			"Choose Folder For Email Tempates",
		overviewId:		this.toString(),
		description:	"Choose Folder For Email Tempates:",
		skipReadOnly:	false,
		hideNewButton:	false,
		appName:		ZmApp.MAIL,
		omit:			[]
	};
	this._chooseFolderDialog.popup(params);
};

com_zimbra_emailtemplates.prototype._chooseFolderOkBtnListener =
function(dlg, folder) {
	dlg.popdown();
	var fp = folder.getPath();
	this.needRefresh = false;
	if (this._folderPath != fp) {
		this.needRefresh = true;
	}
	this._folderPath = fp;
	document.getElementById("emailtemplates_folderInfo").innerHTML = this._folderPath;
};

com_zimbra_emailtemplates.prototype._handleSaveProperties =
function(needRefresh) {
	appCtxt.setStatusMsg("Preferences Saved", ZmStatusView.LEVEL_INFO);
	if (needRefresh) {
		this.showYesNoDialog();
	}
};

//--------------------------------------------------------------------------------------------------
// SHOW YES NO DIALOG TO REFRESH BROWSER
//--------------------------------------------------------------------------------------------------
com_zimbra_emailtemplates.prototype.showYesNoDialog =
function() {
	var dlg = appCtxt.getYesNoMsgDialog();
	dlg.registerCallback(DwtDialog.YES_BUTTON, this._yesButtonClicked, this, dlg);
	dlg.registerCallback(DwtDialog.NO_BUTTON, this._NoButtonClicked, this, dlg);
	dlg.setMessage("The browser must be refreshed for the changes to take effect.  Continue?", DwtMessageDialog.WARNING_STYLE);
	dlg.popup();
};

com_zimbra_emailtemplates.prototype._yesButtonClicked =
function(dlg) {
	dlg.popdown();
	this._refreshBrowser();
};

com_zimbra_emailtemplates.prototype._NoButtonClicked =
function(dlg) {
	dlg.popdown();
};

com_zimbra_emailtemplates.prototype._refreshBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};