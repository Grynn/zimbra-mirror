/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Constructor.
 * 
 * @author Raja Rao DV
 */
function Com_Zimbra_EmailTemplates() {
}
Com_Zimbra_EmailTemplates.prototype = new ZmZimletBase();
Com_Zimbra_EmailTemplates.prototype.constructor = Com_Zimbra_EmailTemplates;

//--------------------------------------------------------------------------------------------------
// INIT AND INITIALIZE TOOLBAR MENU BUTTON
//--------------------------------------------------------------------------------------------------
Com_Zimbra_EmailTemplates.prototype.init =
function() {
	this._folderPath = this.getUserProperty("etemplates_sourcefolderPath");
};

Com_Zimbra_EmailTemplates.prototype.initializeToolbar =
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
			tooltip: this.getMessage("EmailTemplatesZimlet_tooltip"),
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

Com_Zimbra_EmailTemplates.prototype._addMenuItems =
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
Com_Zimbra_EmailTemplates.prototype._getRecentEmails =
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

Com_Zimbra_EmailTemplates.prototype._getRecentEmailsHdlr =
function(removeChildren, result) {
	var menu = this._viewIdAndMenuMap[this._currentViewId].menu;
	if (removeChildren) {
		menu.removeChildren();
	}
	if (result) {
		if (result instanceof ZmCsfeException) {
			appCtxt.setStatusMsg(this.getMessage("EmailTemplatesZimlet_folderNotExist")+" " + result.getErrorMsg(), ZmStatusView.LEVEL_WARNING);
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

			var subMi = submenu.createMenuItem("subMenu_" + Dwt.getNextId(), {image:"Edit", text:this.getMessage("EmailTemplatesZimlet_bodyOnly")});
			subMi.addSelectionListener(new AjxListener(this, this._insertMsg, {msg:msg, insertMode:"body"}));
			subMi = submenu.createMenuItem("subMenu_" + Dwt.getNextId(), {image:"Edit", text:this.getMessage("EmailTemplatesZimlet_bodyAndSubject")});
			subMi.addSelectionListener(new AjxListener(this, this._insertMsg, {msg:msg, insertMode:"bodyAndSubject"}));
			subMi = submenu.createMenuItem("subMenu_" + Dwt.getNextId(), {image:"Edit", text:this.getMessage("EmailTemplatesZimlet_bodySubjectAndParticipants")});
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

Com_Zimbra_EmailTemplates.prototype._addStandardMenuItems =
function(menu) {
	var mi = menu.createMenuItem("reloadTemplates", {image:"Refresh", text:this.getMessage("EmailTemplatesZimlet_reloadTemplates")});
	mi.addSelectionListener(new AjxListener(this, this._getRecentEmails, true));
	var mi = menu.createMenuItem("preferences", {image:"Resource", text:this.getMessage("EmailTemplatesZimlet_preferences")});
	mi.addSelectionListener(new AjxListener(this, this._displayPrefDialog));
};


//--------------------------------------------------------------------------------------------------
// LOAD SELECTED MESSAGE/TEMPLATE
//--------------------------------------------------------------------------------------------------
Com_Zimbra_EmailTemplates.prototype._insertMsg =
function(params) {
	this.msg = params.msg;
	this.msg.load({callback: new AjxCallback(this, this._handleLoadedMsg, params.insertMode)});
};

Com_Zimbra_EmailTemplates.prototype._handleLoadedMsg =
function(insertMode) {
	this.viewId = appCtxt.getCurrentViewId(); // make sure we use proper viewId to support multiple-compose views
	var controller = this._viewIdAndMenuMap[this._currentViewId].controller;
	var composeView = appCtxt.getCurrentView();
	var currentBodyContent = currentBodyContent = appCtxt.getCurrentView().getHtmlEditor().getContent();
	this._composeMode = appCtxt.getCurrentView().getHtmlEditor().getMode();
	var templateBody = this.getTemplateContent(this.msg, this._composeMode);
	var params = {controller:controller, templateSubject:this.msg.subject, templateBody: templateBody,  currentBodyContent:currentBodyContent, composeView:composeView, insertMode:insertMode};
	this._testTemplateContentForKeys(params);
};

//--------------------------------------------------------------------------------------------------
// TEST TEMPLATE FOR GENERIC WORDS AND THEN INSERT
//--------------------------------------------------------------------------------------------------

Com_Zimbra_EmailTemplates.prototype._testTemplateContentForKeys = function(params) {
	//var regex = new RegExp("\\breplace__[a-z0-9A-Z]*", "ig");
	var regex = new RegExp("\\$\\{[-a-zA-Z._0-9]+\\}", "ig");
	
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
		this._doInsert(params.controller, params.composeView, params.templateSubject, params.templateBody, params.currentBodyContent, params.insertMode);
	}
};

Com_Zimbra_EmailTemplates.prototype._showReplaceStringsDlg =
function(params) {
	if (this.replaceDlg) {
		this._createReplaceView(params);
		this.replaceDlg.popup();
		return;
	}
	this.replaceDlgView = new DwtComposite(this.getShell());
	this.replaceDlgView.getHtmlElement().style.overflow = "auto";
	this._createReplaceView(params);
	this.replaceDlg = this._createDialog({title:this.getMessage("EmailTemplatesZimlet_replaceTemplateData"), view:this.replaceDlgView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this.replaceDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._replaceOKBtnListener, params));
	this.replaceDlg.popup();
};

Com_Zimbra_EmailTemplates.prototype._createReplaceView =
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
		tmpArry.push(AjxStringUtil.trim(dataArry[j]));
	}
	dataArry = emailtemplates_unique(tmpArry);
	this._replaceFieldIdsMap = [];
	var i = 0;
	var html = new Array();
	html[i++] = "<div class='emailTemplates_yellow'>"+this.getMessage("EmailTemplatesZimlet_replaceGenericData")+"</div><BR/>";
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

Com_Zimbra_EmailTemplates.prototype._replaceOKBtnListener =
function(params) {
	var insertMode = params.insertMode;
	var templateBody = params.templateBody;
	var templateSubject = params.templateSubject;
	var currentBodyContent = params.currentBodyContent;
	for (var i = 0; i < this._replaceFieldIdsMap.length; i++) {
		var obj = this._replaceFieldIdsMap[i];
		var key = obj.key;
		key = key.replace(/\$\{/,"\\$\\{").replace(/\}$/, "\\}");
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
	this._doInsert(params.controller, params.composeView, templateSubject, templateBody, currentBodyContent, insertMode);
};

Com_Zimbra_EmailTemplates.prototype._doInsert =
function(controller, composeView, templateSubject, templateBody, currentBodyContent, insertMode) {
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
				appCtxt.setStatusMsg(this.getMessage("EmailTemplatesZimlet_couldNotInsertApptAttendees")+" " + result.getErrorMsg(), ZmStatusView.LEVEL_WARNING);
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
	if (this.viewId == "APPT") {
		//in appt, we append templateBody below currentBodyContent to facilitate things like conf-call templates
		composeView.getHtmlEditor().setContent([currentBodyContent, saperator, templateBody].join(""));
	} else {
		//in email, we append templatebody ABOVE currentBodyContent to facilitate Reply/Fwd emails
		composeView._htmlEditor.setContent([templateBody, saperator, currentBodyContent].join(""));
	}
	if(this.msg.attachments && this.msg.attachments.length > 0) {
		this._isDrafInitiatedByThisZimlet = true;
		controller.saveDraft(ZmComposeController.DRAFT_TYPE_AUTO);
	}
};

Com_Zimbra_EmailTemplates.prototype.addExtraMsgParts =
function(request, isDraft) {
	if(!isDraft || !this._isDrafInitiatedByThisZimlet) {
		return;
	}
	if(request && request.m) {
		if(!request.m.attach) {
			request.m.attach = {};
			request.m.attach.mp = [];
		} else if(!request.m.attach.mp) {
			request.m.attach.mp = [];
		}
		var attmnts = this.msg.attachments;
		if(attmnts) {			
			for(var i = 0; i < attmnts.length; i++) {
				request.m.attach.mp.push({mid:this.msg.id, part:attmnts[i].part});
			}
		}
	}
	this._isDrafInitiatedByThisZimlet = false;
};

Com_Zimbra_EmailTemplates.arrayContainsElement =
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
		if (!Com_Zimbra_EmailTemplates.arrayContainsElement(a, b[i])) {
			a.push(b[i]);
		}
	}
	return a;
}

Com_Zimbra_EmailTemplates.prototype.getTemplateContent = function(note, mode) {
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
Com_Zimbra_EmailTemplates.prototype._displayPrefDialog =
function() {
	if (this.prefDlg) {
		this.prefDlg.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	//this.pView.setSize("200", "50");
	this.pView.getHtmlElement().style.overflow = "auto";
	this.pView.getHtmlElement().innerHTML = this._createPreferenceView();
	this.prefDlg = this._createDialog({title:this.getMessage("EmailTemplatesZimlet_preferences"), view:this.pView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this.prefDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._prefOKBtnListener));
	this._initializePrefDialog();
	this.prefDlg.popup();
};

Com_Zimbra_EmailTemplates.prototype._createPreferenceView =
function() {
	var str = "Templates folder not set";
	if (this._folderPath != "") {
		str = this._folderPath;
	}
	var html = new Array();
	var i = 0;
	html.push("<TABLE cellspacing=3 cellpadding=3>",
		"<TR><TD><DIV style='font-weight:bold;'>",this.getMessage("EmailTemplatesZimlet_templateFolderPath"),
		"</div></TD><TD><DIV style='color:blue;font-weight:bold;' id='emailtemplates_folderInfo'>",str,"</div></TD></TR>",
		"<TR><TD colspan=2><DIV id='emailtemplates_folderLookupDiv'></DIV></TD></TR></TABLE>",
		"<br/><div class='emailTemplates_yellow'>",this.getMessage("EmailTemplatesZimlet_genericNames"),"</div><div  class='emailTemplates_yellowNormal'>",
		"<br/>",this.getMessage("EmailTemplatesZimlet_helpLine1"),
		"<br/> ",this.getMessage("EmailTemplatesZimlet_helpLine2"),
		"<br/><br/>",this.getMessage("EmailTemplatesZimlet_helpLine3"),
		"<br/>",this.getMessage("EmailTemplatesZimlet_helpLine4"),
		"<br/>", this.getMessage("EmailTemplatesZimlet_helpLine5"),
		"<br/>", this.getMessage("EmailTemplatesZimlet_helpLine6"));

	return html.join("");
};

Com_Zimbra_EmailTemplates.prototype._initializePrefDialog =
function() {
	var btn = new DwtButton({parent:this.getShell()});
	btn.setText(this.getMessage("EmailTemplatesZimlet_setTemplatesFolder"));
	btn.setImage("Search");
	btn.setToolTipContent(this.getMessage("EmailTemplatesZimlet_selectTemplatesFolder"));
	btn.addSelectionListener(new AjxListener(this, this._setFolderBtnListener));
	document.getElementById("emailtemplates_folderLookupDiv").appendChild(btn.getHtmlElement());
};


Com_Zimbra_EmailTemplates.prototype._prefOKBtnListener =
function() {
	if (this.needRefresh) {
		this.setUserProperty("etemplates_sourcefolderPath", this._folderPath);
		var callback = new AjxCallback(this, this._handleSaveProperties, this.needRefresh);
		this.saveUserProperties(callback);
	}
	this.prefDlg.popdown();
};

Com_Zimbra_EmailTemplates.prototype._setFolderBtnListener =
function() {
	if (!this._chooseFolderDialog) {
		AjxDispatcher.require("Extras");
		this._chooseFolderDialog = new ZmChooseFolderDialog(appCtxt.getShell());
	}
	this._chooseFolderDialog.reset();
	this._chooseFolderDialog.registerCallback(DwtDialog.OK_BUTTON, this._chooseFolderOkBtnListener, this, this._chooseFolderDialog);

	var params = {
		treeIds:		[ZmOrganizer.FOLDER],
		title:			this.getMessage("EmailTemplatesZimlet_selectTemplatesFolder"),
		overviewId:		this.toString(),
		description:	this.getMessage("EmailTemplatesZimlet_selectTemplatesFolder"),
		skipReadOnly:	false,
		hideNewButton:	false,
		appName:		ZmApp.MAIL,
		omit:			[]
	};
	this._chooseFolderDialog.popup(params);
};

Com_Zimbra_EmailTemplates.prototype._chooseFolderOkBtnListener =
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

Com_Zimbra_EmailTemplates.prototype._handleSaveProperties =
function(needRefresh) {
	appCtxt.setStatusMsg("Preferences Saved", ZmStatusView.LEVEL_INFO);
	if (needRefresh) {
		this.showYesNoDialog();
	}
};

//--------------------------------------------------------------------------------------------------
// SHOW YES NO DIALOG TO REFRESH BROWSER
//--------------------------------------------------------------------------------------------------
Com_Zimbra_EmailTemplates.prototype.showYesNoDialog =
function() {
	var dlg = appCtxt.getYesNoMsgDialog();
	dlg.registerCallback(DwtDialog.YES_BUTTON, this._yesButtonClicked, this, dlg);
	dlg.registerCallback(DwtDialog.NO_BUTTON, this._NoButtonClicked, this, dlg);
	dlg.setMessage("The browser must be refreshed for the changes to take effect.  Continue?", DwtMessageDialog.WARNING_STYLE);
	dlg.popup();
};

Com_Zimbra_EmailTemplates.prototype._yesButtonClicked =
function(dlg) {
	dlg.popdown();
	this._refreshBrowser();
};

Com_Zimbra_EmailTemplates.prototype._NoButtonClicked =
function(dlg) {
	dlg.popdown();
};

Com_Zimbra_EmailTemplates.prototype._refreshBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};