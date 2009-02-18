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
 */

//////////////////////////////////////////////////////////////////////////////
// Add Stickynotes to *individual* emails. Also automatically attaches a tag "Emails with StickyNotes" so user
// can search for such mails.
// @author Zimlet author: Raja Rao DV(rrao@zimbra.com)
//////////////////////////////////////////////////////////////////////////////

function com_zimbra_stickynotes() {
}

com_zimbra_stickynotes.prototype = new ZmZimletBase();
com_zimbra_stickynotes.prototype.constructor = com_zimbra_stickynotes;
com_zimbra_stickynotes.tagName = "Emails with StickyNotes";
com_zimbra_stickynotes.stickyNotes = "STICKYNOTES";

com_zimbra_stickynotes.prototype.init =
function() {
	this.turnONstickynotesZimlet = this.getUserProperty("turnONstickynotesZimlet") == "true";
	if (!this.turnONstickynotesZimlet) {
		return;
	}
	this.stickyNotes_ToolbarBtn = this.getUserProperty("stickyNotes_ToolbarBtn")  == "true";
	this._createTagAndStoreId();
	
	if(this.stickyNotes_ToolbarBtn)
		this._initToolbarButton();
};

com_zimbra_stickynotes.prototype._createTagAndStoreId =
function() {
	var tagObj = appCtxt.getActiveAccount().trees.TAG.getByName(com_zimbra_stickynotes.tagName);
	if (!tagObj) {
		this._createTag({name:com_zimbra_stickynotes.tagName, color:ZmOrganizer.C_YELLOW, callback: new AjxCallback(this, this._handleTagCreation)});
	} else {
		this._tagId = tagObj.nId;
	}
};

com_zimbra_stickynotes.prototype._createTag =
function(params) {
	var soapDoc = AjxSoapDoc.create("CreateTagRequest", "urn:zimbraMail");
	var tagNode = soapDoc.set("tag");
	tagNode.setAttribute("name", params.name);
	var color = ZmOrganizer.checkColor(params.color);
	if (color && (color != ZmOrganizer.DEFAULT_COLOR[ZmOrganizer.TAG])) {
		tagNode.setAttribute("color", color);
	}
	appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:params.callback});
};

com_zimbra_stickynotes.prototype._handleTagCreation =
function(response) {
	try {
		this._tagId = response.getResponse().CreateTagResponse.tag[0].id;
	} catch(e) {
	}
};

com_zimbra_stickynotes.prototype._showStickyNotes =
function() {
	var content = "";
	if (this._msgIdAndDataArry[this._msgId]) {
		content = this._msgIdAndDataArry[this._msgId];
	}
	if (this._mainContainer) {
		this._mainContainer.style.display = "block";
		this._setStickyContent(content);
		if(content == "")//focus only when its empty
			this._focusStickyNotes();
		this.stickyNotesDisplayed = true;
		return;
	}
	this._stickyNotesView(content);
	if(content == "")//focus only when its empty
		this._focusStickyNotes();
};

com_zimbra_stickynotes.prototype._getStoredData =
function() {
	this.stickyNotes_data = this.getUserProperty("stickyNotes_data");
	var tmpArry = this.stickyNotes_data.split(":=:");
	this._msgIdAndDataArry = [];
	for (var i = 0; i < tmpArry.length; i++) {
		if (tmpArry[i] == "")
			continue;
		var tmp2Arry = tmpArry[i].split(",__data::");
		this._msgIdAndDataArry[tmp2Arry[0].replace("__id::", "")] = tmp2Arry[1];
	}
};

com_zimbra_stickynotes.prototype._saveAndHideStickyNotes =
function() {
	if (!this._mainContainer)
		return;

	var content = this._getStickyContent();
	
	var saveContent = true;
	if(this._msgIdAndDataArry[this._msgId]) {
		if(content == this._msgIdAndDataArry[this._msgId]){
			saveContent = false;
		}
	}
	//dont save it there is no content for *new*-stickynote
	if(this._msgIdAndDataArry[this._msgId] == undefined) {
		if(content == ""){
			saveContent = false;
		}
		
	}
	//also set isNewStickyNotes
	var isNewStickyNotes = false;
	if(this._msgIdAndDataArry[this._msgId] == undefined) {
		isNewStickyNotes = true;
	}
	if(saveContent) {
		this._msgIdAndDataArry[this._msgId] = content;
		var dbStr = "";
		for (var msgId in this._msgIdAndDataArry) {
			dbStr = "__id::" + msgId + ",__data::" + this._msgIdAndDataArry[msgId] + ":=:" + dbStr;
		}	
		this.setUserProperty("stickyNotes_data", dbStr, true, new AjxCallback(this, this._saveContentCallback));

		if(isNewStickyNotes)
			this._tagAction(true);
	}
	this._hideStickyNotes();
};
com_zimbra_stickynotes.prototype._saveContentCallback =
function () {
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
	appCtxt.getAppController().setStatusMsg("StickyNotes Saved", ZmStatusView.LEVEL_INFO, null, transitions);
};

com_zimbra_stickynotes.prototype._tagAction =
function (trueOrFalse) {

	if(appCtxt.getActiveAccount().trees.TAG.getByName(com_zimbra_stickynotes.tagName) == undefined)
		return;
	var axnType = "";
	if (trueOrFalse)
		axnType = "tag";
	else
		axnType = "!tag";

	var soapCmd = ZmItem.SOAP_CMD[this.srcMsgObj.type] + "Request";
	var itemActionRequest = {};
	itemActionRequest[soapCmd] = {_jsns:"urn:zimbraMail"};
	var request = itemActionRequest[soapCmd];
	var action = request.action = {};
	action.id = this.srcMsgObj.id;
	action.op = axnType;
	action.tag = this._tagId;
	var params = {asyncMode: true, callback: null, jsonObj:itemActionRequest};
	appCtxt.getAppController().sendRequest(params);
};

com_zimbra_stickynotes.prototype._deleteSaveAndHideStickyNotes =
function() {
	if (!this._mainContainer)
		return;

	delete this._msgIdAndDataArry[this._msgId];//delete
	var dbStr = "";
	for (var msgId in this._msgIdAndDataArry) {
		dbStr = "__id::" + msgId + ",__data::" + this._msgIdAndDataArry[msgId] + ":=:" + dbStr;
	}

	this.setUserProperty("stickyNotes_data", dbStr, true);
	this._hideStickyNotes();
	if(this.srcMsgObj.hasTag(this._tagId))
		this._tagAction(false);
	
};

com_zimbra_stickynotes.prototype._hideStickyNotes =
function() {
	this._mainContainer.style.display = "none";
	this.stickyNotesDisplayed = false;
};

com_zimbra_stickynotes.prototype._stickyNotesView =
function(content) {
	this._mainContainer = document.getElementById("z_shell").appendChild(document.createElement('div'));
	this._mainContainer.style.left = "100px";
	this._mainContainer.style.top = "200px";
	this._mainContainer.style.position = "absolute";
	this._mainContainer.style.display = "block";
	this._mainContainer.style.zIndex = 9000;

	var html = new Array();
	var i = 0;
	html[i++] = "<DIV id ='stickyn_actionContainerID' class='stickyn_axnClass'>";
	html[i++] = "<table width='100%'>";
	html[i++] = "<tr><td width='85%'></td><td width='5%'><img   src=\"" + this.getResource("sticky_deleteBtn.gif") + "\" id='stickyn_deleteBtn' /></td><td width='5%'></td><td width='5%'><img   src=\"" + this.getResource("sticky_closeBtn.gif") + "\"  id='stickyn_closeBtn'/></td></tr>";
	html[i++] = "</table>";
	html[i++] = "</DIV>";
	html[i++] = "<TEXTAREA  id ='stickyn_textAreaID'class='stickyn_textAreaClass'>";
	html[i++] = "</TEXTAREA>";
	this._mainContainer.innerHTML = html.join("");
	this._addListeners();
	if (content) {
		this._setStickyContent(content);
	}
	this.stickyNotesDisplayed = true;
};

com_zimbra_stickynotes.prototype._setStickyContent =
function(content) {
	document.getElementById("stickyn_textAreaID").value = content;
};

com_zimbra_stickynotes.prototype._focusStickyNotes =
function() {
	document.getElementById("stickyn_textAreaID").focus();
};


com_zimbra_stickynotes.prototype._getStickyContent =
function() {
	return document.getElementById("stickyn_textAreaID").value;
};

com_zimbra_stickynotes.prototype._addListeners =
function() {
	document.getElementById("stickyn_closeBtn").onclick = AjxCallback.simpleClosure(this._saveAndHideStickyNotes, this);
	document.getElementById("stickyn_deleteBtn").onclick = AjxCallback.simpleClosure(this._deleteSaveAndHideStickyNotes, this);
};

com_zimbra_stickynotes.prototype._initToolbarButton = function() {
	if (!appCtxt.get(ZmSetting.MAIL_ENABLED))
		this._toolbar = true;

	if (this._toolbar)
		return;
	// Add the button to the conversation page
	var viewid = appCtxt.getAppViewMgr().getCurrentViewId();
	if(viewid == ZmId.VIEW_CONVLIST) {
		this._controller = AjxDispatcher.run("GetConvListController");
		this._controller._stickynotes = this;
		if (!this._controller._toolbar) {
			// initialize the conv controller's toolbar
			this._controller._initializeToolBar();
		}
		this._toolbar = this._controller._toolbar.CLV;
	} else if(viewid == ZmId.VIEW_TRAD) {
		this._controller = AjxDispatcher.run("GetTradController");
		this._controller._stickynotes = this;
		if (!this._controller._toolbar) {
			// initialize the trad controller's toolbar
			this._controller._initializeToolBar();
		}
		this._toolbar = this._controller._toolbar.TV;
	}

	if(!this._toolbar)
		return;//dont add button

	var indx = this._toolbar.getItemCount() + 1;

	// Add button to toolbar
	if (!this._toolbar.getButton(com_zimbra_stickynotes.stickyNotes)) {
		ZmMsg.stickyNotesLabel = "StickyNotes";
		ZmMsg.stickyNotesTip = "Adds StickyNotes to individual mail or conversation";

		var btn = this._toolbar.createOp(
			com_zimbra_stickynotes.stickyNotes,
		{
			text	: ZmMsg.stickyNotesLabel,
			tooltip : ZmMsg.stickyNotesTip,
			index   :indx,
			image   : "stickynotes-panelIcon"
		}
			);

		btn.addSelectionListener(new AjxListener(this, this._stickyTBListener));
	}
};

com_zimbra_stickynotes.prototype._stickyTBListener =
function() {
	if (!this.turnONstickynotesZimlet)
		return;

	var selectedItms = this._controller.getCurrentView().getSelection();
	if(selectedItms.length > 0) {
		this.srcMsgObj = selectedItms[0];
		if(this.srcMsgObj.type == "CONV"){
			this.srcMsgObj = this.srcMsgObj.getFirstHotMsg();
		}
		this._createStickyNotes(this.srcMsgObj);
	}
};

com_zimbra_stickynotes.prototype.doDrop =
function(msgObj) {
	if (!this.turnONstickynotesZimlet)
		return;

	this.srcMsgObj = msgObj.srcObj;
	if(this.srcMsgObj.type == "CONV"){
		this.srcMsgObj = this.srcMsgObj.getFirstHotMsg();
	}
	this._createStickyNotes(this.srcMsgObj);
};

com_zimbra_stickynotes.prototype._createStickyNotes =
function(mail) {
	if (mail.type == "CONV") {
		this._msgId = "CONV" + mail.id;
	} else if (mail.type == "MSG") {
		this._msgId = "MSG" + mail.id;
	} else {
		return;
	}
	this._getStoredData();
	this._showStickyNotes();
};

com_zimbra_stickynotes.prototype.onMsgView =
function(msg) {
	if (!this.turnONstickynotesZimlet)
		return;

	this.srcMsgObj = msg;
	var id = "MSG" + msg.id;
	var convId = "CONV" + msg.cid;
	this._getStoredData();
	//give msgId higher priority than ConvId(if we have stickynotes for both)
	if (this._msgIdAndDataArry[id]) {
		this._msgId = id;
	} else if (this._msgIdAndDataArry[convId]) {
		this._msgId = convId;
	} else {
		if (this.stickyNotesDisplayed) {
			this._hideStickyNotes();
		}
		return;
	}

	this._showStickyNotes();
};


com_zimbra_stickynotes.prototype.doubleClicked = function() {
	this.singleClicked();
};

com_zimbra_stickynotes.prototype.singleClicked = function() {
	this.showPrefDialog();
};

com_zimbra_stickynotes.prototype.showPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().innerHTML = this.createPrefView();

	if (this.getUserProperty("turnONstickynotesZimlet") == "true") {
		document.getElementById("turnONstickynotesZimlet_chkbx").checked = true;
	}
	if (this.getUserProperty("stickyNotes_ToolbarBtn") == "true") {
		document.getElementById("stickyNotes_ToolbarBtn_chkbx").checked = true;
	}
	
	this.pbDialog = this._createDialog({title:"'Sticky Notes' Zimlet Preferences", view:this.pView, standardButtons:[DwtDialog.OK_BUTTON]});
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this.pbDialog.popup();
};

com_zimbra_stickynotes.prototype.createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "<input id='stickyNotes_ToolbarBtn_chkbx'  type='checkbox'/>Add StickyNotes button to main toolbar";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='turnONstickynotesZimlet_chkbx'  type='checkbox'/>Enable 'Sticky Notes' Zimlet";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "*Changing above preferences would refresh browser";
	return html.join("");
};

com_zimbra_stickynotes.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
	if (document.getElementById("turnONstickynotesZimlet_chkbx").checked) {
		if (!this.turnONstickynotesZimlet) {
			this._reloadRequired = true;
		}
		this.setUserProperty("turnONstickynotesZimlet", "true");
	} else {
		this.setUserProperty("turnONstickynotesZimlet", "false");
		if (this.turnONstickynotesZimlet)
			this._reloadRequired = true;
	}

	if (document.getElementById("stickyNotes_ToolbarBtn_chkbx").checked) {
		if (!(this.getUserProperty("stickyNotes_ToolbarBtn") == "true")) {
			this._reloadRequired = true;
		}
		this.setUserProperty("stickyNotes_ToolbarBtn", "true");
	} else {
		this.setUserProperty("stickyNotes_ToolbarBtn", "false");
		if (this.stickyNotes_ToolbarBtn){
			this._reloadRequired = true;
		}
	}

	this.pbDialog.popdown();

	if (this._reloadRequired) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("Browser will be refreshed for changes to take effect..", ZmStatusView.LEVEL_INFO, null, transitions);
		this.saveUserProperties(new AjxCallback(this, this._refreshBrowser));
	}
};

com_zimbra_stickynotes.prototype._refreshBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};