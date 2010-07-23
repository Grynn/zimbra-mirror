/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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

//////////////////////////////////////////////////////////////////////////////
// Add Stickynotes to *individual* emails. Also automatically attaches a tag "Emails with StickyNotes" so user
// can search for such mails.
// @author Zimlet author: Raja Rao DV(rrao@zimbra.com)
//////////////////////////////////////////////////////////////////////////////

function com_zimbra_stickyNotes_HandlerObject() {
}

com_zimbra_stickyNotes_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_stickyNotes_HandlerObject.prototype.constructor = com_zimbra_stickyNotes_HandlerObject;


/**
 * Simplify handler object
 *
 */
var StickyNotesZimlet = com_zimbra_stickyNotes_HandlerObject;


StickyNotesZimlet.stickyNotes = "STICKYNOTES";

/**
 * Called by Zimbra upon login
 */
StickyNotesZimlet.prototype.init =
function() {
	this._tagName = this.getMessage("sn_EmailsWithStickyNotes");
	this.turnONstickynotesZimlet = this.getUserProperty("turnONstickynotesZimlet") == "true";
	if (!this.turnONstickynotesZimlet) {
		return;
	}

	this.stickyNotes_ToolbarBtn = this.getUserProperty("stickyNotes_ToolbarBtn") == "true";
	this._createTagAndStoreId();
	this._migrateOldData();
};

StickyNotesZimlet.prototype.onShowView =
function(viewId, isNewView) {
	if(viewId == "CNS" || viewId == "CAL") {
		var controller = appCtxt.getCurrentController();
		try{
			if(viewId == "CAL") {//in calendar, there are multiple views and viewId doesnt match internal views
				for(var vid in controller._listView) {
					controller._listView[vid].addSelectionListener(new AjxListener(this, this._onContactOrApptView, [controller]));
				}
			} else {
				controller._listView[viewId].addSelectionListener(new AjxListener(this, this._onContactOrApptView, [controller]));
			}
		} catch(e) {
		}
	}
};

/**
 * Creates Tags and stores its id
 */
StickyNotesZimlet.prototype._createTagAndStoreId =
function() {
	var tagObj = appCtxt.getActiveAccount().trees.TAG.getByName(this._tagName);
	if (!tagObj) {
		this._createTag({name:this._tagName, color:ZmOrganizer.C_YELLOW, callback: new AjxCallback(this, this._handleTagCreation)});
	} else {
		this._tagId = tagObj.nId;
	}
};
/**
 * Creates tags
 * @param {Object} params Object that defines a tag like: name, color etc
 */
StickyNotesZimlet.prototype._createTag =
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

/**
 * Stores 'StickyNotes' Tag id
 * @param {Object} response  Create Tag response
 */
StickyNotesZimlet.prototype._handleTagCreation =
function(response) {
	try {
		this._tagId = response.getResponse().CreateTagResponse.tag[0].id;
	} catch(e) {
	}
};
/**
 * Displays stickynotes widget
 * @param {Boolean} createNew if true, creates a new StickyNotes
 * @param String content StickyNotes content to be set
 */
StickyNotesZimlet.prototype._showStickyNotes =
function(createNew, content) {
	if (!content || !content.notes) {
		content = "";
	} else {
		content = content.notes;
	}
	if (content == "" && !createNew) {
		return;
	}
	this._createNewStickyNotes = createNew;
	this._stickyNotesView(content);
};

/**
 * Saves StickyNotes content
 */
StickyNotesZimlet.prototype._saveAndHideStickyNotes =
function() {
	if (!this._mainContainer) {
		return;
	}
	var content = this._getStickyContent();
	if (!this._createNewStickyNotes || (this._createNewStickyNotes && content != "")) {
		this._saveStickyNotesDataToServer(this._itemId, content);
	}
	if (this._createNewStickyNotes && content != "") {
		this._tagAction(true);
	}
	this._hideStickyNotes();
};

/**
 * Gets StickyNotes Zimlet metadata
 *
 * @param {string} msgId Mail Id
 * @param {AjxCallback} postCallback  A callback
 */
StickyNotesZimlet.prototype._getStickyNotesMetaData =
function(msgId, postCallback) {
	this._currentMetaData = new ZmMetaData(appCtxt.getActiveAccount(), msgId);
	this._currentMetaData.get("stikyNotesZimletMetaData", null, new AjxCallback(this, this._handleGetStickyNotesMetaData, postCallback));
};

/**
 * Handles Get StickyNotesMetadata response & calls back another function
 *
 * @param {AjxCallback} postCallback A callback
 * @param {object} result Custom metadata response
 */
StickyNotesZimlet.prototype._handleGetStickyNotesMetaData =
function(postCallback, result) {
	this._stikyNotesMetaData = "";//nullify old data
	try {
		var response = result.getResponse().BatchResponse.GetCustomMetadataResponse[0];
		if (response.meta && response.meta[0]) {
			this._stikyNotesMetaData = response.meta[0]._attrs;
		}
		if (postCallback) {
			postCallback.run(this._stikyNotesMetaData);
		} else {
			return this._stikyNotesMetaData;
		}
	} catch(ex) {
		this._showErrorMessage(ex);
		return;
	}
};

/**
 * Saves StickyNotes meta data to server
 * @param {string} msgId Appointment id
 * @param {string} stickyNotesData data
 */
StickyNotesZimlet.prototype._saveStickyNotesDataToServer =
function(msgId, stickyNotesData) {
	this._currentMetaData = new ZmMetaData(appCtxt.getActiveAccount(), msgId);
	var keyValArry = [];
	keyValArry["notes"] = stickyNotesData;
	this._currentMetaData.set("stikyNotesZimletMetaData", keyValArry, null, null);
};

/**
 * Displays 'Notes saved' message
 */
StickyNotesZimlet.prototype._saveContentCallback =
function () {
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
	appCtxt.getAppController().setStatusMsg(this.getMessage("sn_notesSaved"), ZmStatusView.LEVEL_INFO, null, transitions);
};

/**
 * Tags or untags the email
 * @param Boolean trueOrFalse If <code>true</code>, tags an email
 */
StickyNotesZimlet.prototype._tagAction =
function (trueOrFalse) {

	if (appCtxt.getActiveAccount().trees.TAG.getByName(this._tagName) == undefined)
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

/**
 * Deletes StickyNotes content
 */
StickyNotesZimlet.prototype._deleteSaveAndHideStickyNotes =
function() {
	if (!this._mainContainer)
		return;

	this._saveStickyNotesDataToServer(this._itemId, "");

	this._hideStickyNotes();
	if (this.srcMsgObj.hasTag(this._tagId))
		this._tagAction(false);

};

/**
 * Hides stickyNotes
 */
StickyNotesZimlet.prototype._hideStickyNotes =
function() {
	this._mainContainer.style.display = "none";
	this.stickyNotesDisplayed = false;
};

/**
 * Creates StickyNotes view
 * @param String content StickyNotes content
 */
StickyNotesZimlet.prototype._stickyNotesView =
function(content) {
	if (this._mainContainer) {
		this._mainContainer.style.display = "block";
		this._setStickyContent(content);
		if (content == "") {//focus only when its empty
			this._focusStickyNotes();
		}
		this.stickyNotesDisplayed = true;
		return;
	}
	this._mainContainer = document.getElementById("z_shell").appendChild(document.createElement('div'));
	this._mainContainer.style.left = "100px";
	this._mainContainer.style.top = "200px";
	this._mainContainer.style.position = "absolute";
	this._mainContainer.style.display = "block";
	this._mainContainer.style.zIndex = 9000;

	var html = new Array();
	var i = 0;
	html.push("<DIV id ='stickyn_actionContainerID' class='stickyn_axnClass'>",
	"<table width='100%'><tr><td width='85%'></td><td width='5%'>",
	"<img  style='cursor:pointer;' src=\"", this.getResource("sticky_deleteBtn.gif"), "\" id='stickyn_deleteBtn' /></td>",
	"<td width='5%'></td><td width='5%'><div  style='cursor:pointer;' class='ImgSave' id='stickyn_closeBtn'/></td></tr>",
	"</table></DIV>",
	"<TEXTAREA  id ='stickyn_textAreaID'class='stickyn_textAreaClass'>",
	"</TEXTAREA>");

	this._mainContainer.innerHTML = html.join("");
	this._addListeners();
	if (content) {
		this._setStickyContent(content);
	}
	this.stickyNotesDisplayed = true;
};

/**
 * Sets sticky notes content
 * @param String content StickyNotes content
 */
StickyNotesZimlet.prototype._setStickyContent =
function(content) {
	document.getElementById("stickyn_textAreaID").value = content;
};

/**
 * Sets cursor focus to StickyNotes widget
 */
StickyNotesZimlet.prototype._focusStickyNotes =
function() {
	document.getElementById("stickyn_textAreaID").focus();
};

/**
 * Gets StickyNotes Content
 */
StickyNotesZimlet.prototype._getStickyContent =
function() {
	return document.getElementById("stickyn_textAreaID").value;
};

/**
 * Adds listeners to save & delete buttons
 */
StickyNotesZimlet.prototype._addListeners =
function() {
	document.getElementById("stickyn_closeBtn").onclick = AjxCallback.simpleClosure(this._saveAndHideStickyNotes, this);
	document.getElementById("stickyn_deleteBtn").onclick = AjxCallback.simpleClosure(this._deleteSaveAndHideStickyNotes, this);
};

/**
 * Adds toolbar
 *@see ZmZimletBase
 */
StickyNotesZimlet.prototype.initializeToolbar =
function(app, toolbar, controller, view) {
	if (!this.turnONstickynotesZimlet) {
		return;
	}
	if (view == ZmId.VIEW_CONVLIST || view == ZmId.VIEW_CONV || view == ZmId.VIEW_TRAD || view == "CNS" || view == "CLD") {
		var buttonIndex = -1;
		for (var i = 0, count = toolbar.opList.length; i < count; i++) {
			if (toolbar.opList[i] == ZmOperation.PRINT) {
				buttonIndex = i + 1;
				break;
			}
		}

		ZmMsg.stickyNotesLabel = this.getMessage("sn_label");
		ZmMsg.stickyNotesTip = this.getMessage("sn_tooltip");
		var buttonArgs = {
			text	: ZmMsg.stickyNotesLabel,
			tooltip: ZmMsg.stickyNotesTip,
			index: buttonIndex,
			image: "stickynotes-panelIcon"
		};
		if(!toolbar.getOp(StickyNotesZimlet.stickyNotes)) {
			var button = toolbar.createOp(StickyNotesZimlet.stickyNotes, buttonArgs);
			button.addSelectionListener(new AjxListener(this, this._stickyTBListener, [controller]));
		}
	}
};

/**
 * Adds StickyNotes toolbar button listener
 * @param ZmComposeController controller Compose controller
 */
StickyNotesZimlet.prototype._stickyTBListener =
function(controller) {
	if (!this.turnONstickynotesZimlet)
		return;

	var selectedItms = controller.getCurrentView().getSelection();
	if (selectedItms.length > 0) {
		this.srcMsgObj = selectedItms[0];
		if (this.srcMsgObj.type == "CONV") {
			this.srcMsgObj = this.srcMsgObj.getFirstHotMsg();
		}
		this._createStickyNotes(this.srcMsgObj);
	}
};

/**
 * Handles Message drop
 * @param ZmMailMsg msgObj
 */
StickyNotesZimlet.prototype.doDrop =
function(msgObj) {
	if (!this.turnONstickynotesZimlet)
		return;

	this.srcMsgObj = msgObj.srcObj;
	if (this.srcMsgObj.type == "CONV") {
		this.srcMsgObj = this.srcMsgObj.getFirstHotMsg();
	}
	this._createStickyNotes(this.srcMsgObj);
};

/**
 * Passes Email object to create StickyNotes
 * @param {Object} obj Obj can be ZmMailMsg | ZmContact | ZmConv | ZmAppt
 */
StickyNotesZimlet.prototype._createStickyNotes =
function(obj) {
	if (obj.type == "CONV") {
		this._itemId = obj.cid;
	} else if (obj.type == "MSG" || obj.type == "CONTACT" || obj.type == "APPT") {
		this._itemId = obj.id;
	} else {
		return;
	}
	this._showStickyNotes(true, "");
};

/**
 * Called by Zimbra when an email is opened
 * @param ZmMailMsg mail
 */
StickyNotesZimlet.prototype.onMsgView =
function(msg) {
	if (!this.turnONstickynotesZimlet)
		return;

	this.srcMsgObj = msg;
	//if no tags, assume no sticknotes, so dont search DB(performance)
	if (this.srcMsgObj.tags.length == 0) {
		return;
	}
	if (msg.type == "CONV") {
		this._itemId = msg.cid;
	} else if (msg.type == "MSG") {
		this._itemId = msg.id;
	} else {
		if (this.stickyNotesDisplayed) {
			this._hideStickyNotes();
		}
		return;
	}
	this._handleItemSelect();
};

StickyNotesZimlet.prototype._onContactOrApptView =
function(controller) {
	if (!this.turnONstickynotesZimlet) {
		return;
	}
	var selectedItms = controller.getCurrentView().getSelection();
	if (selectedItms.length > 0) {
		this.srcMsgObj = selectedItms[0];
		this._itemId = this.srcMsgObj.id;
	}
	this._handleItemSelect();
};

/**
 * Checks if the item(this._itemId) has stickyNotes, if so, displays that
 * @param ZmMailMsg mail
 */
StickyNotesZimlet.prototype._handleItemSelect =
function() {
	this._getStickyNotesMetaData(this._itemId, new AjxCallback(this, this._showStickyNotes, false));
};

/**
 * Displays error message.
 *
 * @param {string} expnMsg Exception message string
 */
StickyNotesZimlet.prototype._showErrorMessage =
function(expnMsg) {
	var msg = "";
	if (expnMsg instanceof AjxException) {
		msg = expnMsg.msg;
	} else {
		msg = expnMsg;
	}
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();
	dlg.setMessage(msg, DwtMessageDialog.WARNING_STYLE);
	dlg.popup();
};

StickyNotesZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

StickyNotesZimlet.prototype.singleClicked = function() {
	this.showPrefDialog();
};

/**
 * Displays preferences dialog
 */
StickyNotesZimlet.prototype.showPrefDialog =
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

	this.pbDialog = this._createDialog({title:this.getMessage("sn_preferences"), view:this.pView, standardButtons:[DwtDialog.OK_BUTTON]});
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this.pbDialog.popup();
};

/**
 * Creates Preferences dialog's view
 */
StickyNotesZimlet.prototype.createPrefView =
function() {
	var html = new Array();
	html.push("<DIV><input id='stickyNotes_ToolbarBtn_chkbx'  type='checkbox'/>", this.getMessage("sn_addToolbarButton"), "</DIV>",
	"<DIV><input id='turnONstickynotesZimlet_chkbx'  type='checkbox'/>", this.getMessage("sn_enableStickyNotesZimlet"), "</DIV>",
	"<BR>", this.getMessage("sn_notes"));
	return html.join("");
};

/**
 * Migrates old data from LDAP(ZCS5.0 - 6.0.6 & Zimlet v1.3) to DB(ZCS 6.0.7 & Zimlet V1.5)
 */
StickyNotesZimlet.prototype._migrateOldData =
function() {
	var stickyNotes_data = this.getUserProperty("stickyNotes_data");
	if(stickyNotes_data == "") {
		return;
	}
	var tmpArry = stickyNotes_data.split(":=:");
	this._msgIdAndDataArry = [];
	for (var i = 0; i < tmpArry.length; i++) {
		if (tmpArry[i] == "") {
			continue;
		}
		var tmp2Arry = tmpArry[i].split(",__data::");
		var msgId = tmp2Arry[0].replace("__id::", "").replace("MSG", "").replace("CONV", ""); 
		var data = tmp2Arry[1];
		this._saveStickyNotesDataToServer(msgId, data);
	}
	this.setUserProperty("stickyNotes_data", "", true);
};

/**
 * Handles OK button
 */
StickyNotesZimlet.prototype._okBtnListner =
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
		if (this.stickyNotes_ToolbarBtn) {
			this._reloadRequired = true;
		}
	}

	this.pbDialog.popdown();
	if (this._reloadRequired) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		this.saveUserProperties(new AjxCallback(this, this._refreshBrowser));
	}
};

/**
 * Refreshes browser
 */
StickyNotesZimlet.prototype._refreshBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};