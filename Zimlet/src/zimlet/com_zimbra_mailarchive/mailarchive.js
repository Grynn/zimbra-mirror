/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2011, 2012, 2013 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV
 */

//Zimlet Class
ZmArchiveZimlet = function() {
	this._msgMap = {};
};

//Make Zimlet class a subclass of ZmZimletBase class - this makes a Zimlet a Zimlet
ZmArchiveZimlet.prototype = new ZmZimletBase();
ZmArchiveZimlet.prototype.constructor = ZmArchiveZimlet;

ZmArchiveZimlet.ARCHIVE_BUTTON_ID = "ARCHIVE_ZIMLET_BUTTON_ID";
ZmArchiveZimlet.view = "message";

ZmArchiveZimlet.prototype.init =
function() {
	this.metaData = appCtxt.getActiveAccount().metaData;
	this.metaData.get("archiveZimlet", null, new AjxCallback(this, this._handleGetMetaData));
};

/**
 * (Implemented w/in com_zimbra_archive Zimlet)
 * Add a new function to ZmListController that basically moves selected items to archive folder
 * @param archiveFolder ZmMailFolder Folder where mail items should be moved to Archive
 */
ZmListController.prototype._doArchiveViaZimlet =
function(archiveFolder, zimlet, skip) {
	var archiveFromCache = appCtxt.getById(zimlet._archiveFolderId);
	if (!archiveFolder || (!skip && !archiveFromCache)) {
		zimlet.resetArchiveFolder();
		this._archiveViaZimletListener(zimlet);
		return;
	}

	this._doMove(this._pendingActionData, archiveFolder);
};

/**
 * Listens to Archive Button  (Implemented w/in com_zimbra_archive Zimlet)
 * @param zimlet ZmZimlet This Zimlet or any Zimlet that implements getArchiveFolder method
 *							 getArchiveFolder function must return folder where we should Archive
 * @param ev
 **/
ZmListController.prototype._archiveViaZimletListener =
function(zimlet, ev) {
	this._pendingActionData = this.getSelection();
	var postCallback = this._doArchiveViaZimlet.bind(this);
	if (!zimlet._archiveFolder) {
		zimlet._chooseArchiveFolder(postCallback);
	}
	else {
		postCallback.run(zimlet._archiveFolder, zimlet, false);	
	}
};

ZmArchiveZimlet.prototype.resetArchiveFolder = 
function() {
	this._archiveFolderId = null;
	this._archiveFolder = null;
};

ZmArchiveZimlet.prototype._chooseArchiveFolder =
function(postCallback) {
	var dialog = appCtxt.getChooseFolderDialog();
	dialog.registerCallback(DwtDialog.OK_BUTTON, new AjxCallback(this, this._handleChooseFolder, postCallback));
	var params = {overviewId: dialog.getOverviewId(ZmApp.MAIL), appName:ZmApp.MAIL, skipReadOnly:true, skipRemote:false};
	dialog.popup(params);
};

ZmArchiveZimlet.prototype._handleChooseFolder = 
function(postCallback, organizer) {
	var dialog = appCtxt.getChooseFolderDialog();
	dialog.popdown();
	
	if (organizer) {
		this._archiveFolder = organizer;
		this._archiveFolderId = organizer.id;
		var keyVal = this._getMetaKeyVal();
		this.metaData.set("archiveZimlet", keyVal, null, this._saveAccPrefsHandler.bind(this), null, true);
	}
	if (postCallback) {
		postCallback.run(organizer, this, true);
	}
};

/**
 * Saves Account preferences.
 */
ZmArchiveZimlet.prototype._saveAccPrefsHandler =
function() {
	appCtxt.getAppController().setStatusMsg(this.getMessage("archiveZimletPrefsSaved"), ZmStatusView.LEVEL_INFO);
};

ZmArchiveZimlet.prototype.initializeToolbar =
function(app, toolbar, controller, viewId) {
	//conversation-list-view or conversation-view or traditional-view(aka message-view)
	if(appCtxt.isChildWindow) { return; }
	var viewType = appCtxt.getViewTypeFromId(viewId);
	if (viewType == ZmId.VIEW_CONVLIST || viewType == ZmId.VIEW_CONV || viewType == ZmId.VIEW_TRAD || viewType == ZmId.VIEW_MSG) {
		var buttonIndex = 0;
		for (var i = 0, count = toolbar.opList.length; i < count; i++) {
			if (toolbar.opList[i] == ZmOperation.DELETE ||toolbar.opList[i] == ZmOperation.DELETE_MENU) {
				buttonIndex = i;
				break;
			}
		}
		var buttonArgs = {
			text	: this.getMessage("label"),
			tooltip: this.getMessage("description"),
			index: buttonIndex,
			image: "archiveZimletIcon",
			showImageInToolbar: false,
			showTextInToolbar: true,
			enabled: false
		};
		if (!toolbar.getOp(ZmArchiveZimlet.ARCHIVE_BUTTON_ID)) {
			var button = toolbar.createOp(ZmArchiveZimlet.ARCHIVE_BUTTON_ID, buttonArgs);
			button.addSelectionListener(new AjxListener(controller, controller._archiveViaZimletListener, [this]));
			button.archiveZimlet = this;
			// override the function to reset the operations in the toolbar as there is no method to
			// set when to enable or disable buttons based on the selection in the button api
			var originalFunction = controller._resetOperations;
			controller._resetOperations = function(parent, num) {
				var showArchive = true;
				var obj = parent.getOp(ZmArchiveZimlet.ARCHIVE_BUTTON_ID);
				var msg = controller.getMsg();
				
				if (msg && obj && obj.archiveZimlet && msg.folderId == obj.archiveZimlet._archiveFolderId) {
					showArchive = false;
				}
				originalFunction.apply(controller, arguments);
				parent.enable(ZmArchiveZimlet.ARCHIVE_BUTTON_ID, num && showArchive);
			};
			
			//add listener to listview so that we can enable button when multiple items are selected
			var listView = controller.getListView();
			if (listView && listView.addSelectionListener) {
				listView.addSelectionListener(new AjxListener(this, this._listActionListener, button));
			}
		}
	}
	else if (viewId.indexOf("COMPOSE") >= 0 && this._archiveFolderId) {
		var visible = true;
		var msg = controller && controller.getMsg();
		if (!msg) {
			visible = false;
		}
		else if (msg.folderId == this._archiveFolderId || msg.folderId == ZmFolder.ID_SENT || msg.folderId == ZmFolder.ID_TRASH	|| msg.folderId == ZmFolder.ID_SPAM) { 
			visible = false; 
		}
		var buttonArgs = {
			text: this.getMessage("sendAndArchiveButton"),
			tooltip: this.getMessage("sendAndArchiveTooltip"),
			index: 0,
			image: "archiveZimletIcon",
			showImageInToolbar: false,
			showTextInToolbar: true,
			enabled: true
		};
		if (!toolbar.getOp("SEND_ARCHIVE_ZIMLET_BUTTON_ID") && this.isActionForArchive(controller._action) && this._showSendAndArchive && visible) {
			var button = toolbar.createOp("SEND_ARCHIVE_ZIMLET_BUTTON_ID", buttonArgs);
			button.addSelectionListener(new AjxListener(this, this.sendAndArchiveListener, [button]));
		}
		else if (toolbar.getOp("SEND_ARCHIVE_ZIMLET_BUTTON_ID")){
			var button = toolbar.getOp("SEND_ARCHIVE_ZIMLET_BUTTON_ID");
			if (this._showSendAndArchive && visible) {
				visible = this.isActionForArchive(controller._action);
			}
			button.setEnabled(true);
			button.setVisible(visible);
		}
	}
};

ZmArchiveZimlet.prototype.isActionForArchive = 
function(action) {
	if (!action) {
		return false;
	}
	
	if (action == ZmId.OP_REPLY || action == ZmId.OP_REPLY || action == ZmId.OP_REPLY_BY_EMAIL || action == ZmId.OP_REPLY_ALL || action == ZmId.OP_FORWARD ||
	    action == ZmId.OP_FORWARD_ATT  || action == ZmId.OP_FORWARD_INLINE || action == ZmId.OP_FORWARD_BY_EMAIL || action == ZmId.OP_FORWARD_INLINE) {
		return true;
	}
};

ZmArchiveZimlet.prototype._handleGetMetaData = 
function(result) {
	try {
		var response = result.getResponse().BatchResponse.GetMailboxMetadataResponse[0];
		if (response.meta && response.meta[0] && response.meta[0]._attrs ) {
			var hideDeletePref = response.meta[0]._attrs["hideDeleteButton"];
			if (!hideDeletePref) {
				this._hideDeletePref = false;
			}
			else if (hideDeletePref == "false") {
				this._hideDeletePref = false;
			}
			else {
				this._hideDeletePref = true;
			}
			this._hideDeleteButton(this._hideDeletePref);
			
			var showSendAndArchive = response.meta[0]._attrs["showSendAndArchive"];
			if (!showSendAndArchive) {
				this._showSendAndArchive = false;
			}
			else if (showSendAndArchive == "false") {
				this._showSendAndArchive = false;
			}
			else {
				this._showSendAndArchive = true;
			}
			
			this._archiveFolderId = response.meta[0]._attrs["archivedFolder"];
			if (this._archiveFolderId == -1) {
				this._archiveFolderId = null;
				return;
			}
			this._archiveFolder = appCtxt.getById(this._archiveFolderId);
			if (!this._archiveFolder) {
				this._clearArchivedFolder();
			}
		}	
	} catch(ex) {	
		return;
	}
};

ZmArchiveZimlet.prototype._setArchivedFolder = 
function(organizer) {
	this._archiveFolder = organizer;
};

ZmArchiveZimlet.prototype._clearArchivedFolder = 
function() {
	this._archiveFolder = null;
	this._achiveFolderId = null;
	var keyVal = this._getMetaKeyVal();
	keyVal["archivedFolder"] = -1;
	this.metaData.set("archiveZimlet", keyVal, null, new AjxCallback(this, this._handleClearArchivedFolder), null, true);
};

ZmArchiveZimlet.prototype._handleClearArchivedFolder = 
function() {
	return;	
};

ZmArchiveZimlet.prototype.singleClicked =
function() {
	this._showPreferenceDlg();
};


ZmArchiveZimlet.prototype._showPreferenceDlg =
function() {
	//if zimlet dialog already exists...
	if (this._preferenceDialog) {
		this._updatePrefView(this._archiveFolder, this);
		this._preferenceDialog.popup();
		return;
	}
	this._preferenceView = new DwtComposite(this.getShell());
	this._preferenceView.getHtmlElement().style.overflow = "auto";
	this._preferenceView.getHtmlElement().innerHTML = this._createPrefView();
	this._preferenceDialog = this._createDialog({title:this.getMessage("archivePrefsTitle"), view:this._preferenceView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._preferenceDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okPreferenceBtnListener));

	var folderString = this.getMessage("archiveFolderBrowse");
	if (this._archiveFolder) {
		folderString = this._archiveFolder.getPath(false);
	}
	this._folderTxt = new DwtText({parent:appCtxt.getShell(), parentElement:document.getElementById("ARCHIVE_ZIMLET_FOLDER"), index:0, id:"ARCHIVE_ZIMLET_FOLDER_TEXT", className:"FakeAnchor"});
	this._folderTxt.isLinkText = true;
	this._folderTxt.setText(folderString);
	this._folderTxt.getHtmlElement().onclick = this._chooseArchiveFolder.bind(this, this._updatePrefView);
	this._preferenceDialog.popup();
};

ZmArchiveZimlet.prototype._createPrefView =
function() {
	var hideDelete = this._hideDeletePref ? "checked" : "";
	var noHideDelete = this._hideDeletePref ? "" : "checked";
	
	var hideSendAndArchive = this._showSendAndArchive ? "" : "checked";
	var showSendAndArchive = this._showSendAndArchive ? "checked" : "";

	return [
		"<div class='mailArchivePrefDialog'>",
		"<table class='ZPropertySheet' cellspacing='6'>",
		"<tr><td style='text-align:right;'>" + this.getMessage("archiveFolderPrefLabel") + ": </td><td id='ARCHIVE_ZIMLET_FOLDER'></td></tr>" +
		"<tr><td style='text-align:right;'>" + this.getMessage("archiveHideDeleteButton") + ": </td><td>",
		"<table class='ZRadioButtonTable'><tr>",
        "<td><input id='archiveHideDelete2' name='archiveHideDelete' value='false' " + noHideDelete + " type='radio'/></td>",
        "<td><label style='margin-right:1em;' for='archiveHideDelete2'>" + this.getMessage("archiveShow") + "</label></td>",
        "<td><input id='archiveHideDelete1' name='archiveHideDelete' value='true' " + hideDelete + " type='radio'/></td>",
        "<td><label for='archiveHideDelete1'>" + this.getMessage("archiveHide") + "</label></td>",
        "</tr></table>",
		"<tr><td style='text-align:right;'>" + this.getMessage("sendAndArchiveLabel") + ":</td><td>",
		"<table class='ZRadioButtonTable'><tr>",
		"<td><input id='sendAndArchive2' name='sendAndArchive' value='true' " + showSendAndArchive + " type='radio'/></td>",
		"<td><label style='margin-right:1em;' for='sendAndArchive1'>" + this.getMessage("archiveShow") + "</label></td>",
		"<td><input id='sendAndArchive1' name='sendAndArchive' value='false' " + hideSendAndArchive + " type='radio'/></td>",
		"<td><label for='sendAndArchive1'>" + this.getMessage("archiveHide") + "</label></td>",
		"</tr></table>",	
		"</td></tr>",
		"</table>",
		"</div>"
	].join("");
};
ZmArchiveZimlet.prototype._updatePrefView = 
function(organizer, zimlet) {
	if (organizer) {
		zimlet._folderTxt.setText(organizer.getPath(false));	
	}
};

ZmArchiveZimlet.prototype._okPreferenceBtnListener =
function() {
	if (!this._archiveFolderId) {
		var dlg = appCtxt.getMsgDialog();
		dlg.reset();
		dlg.setMessage(this.getMessage("archiveMustChooseFolder"), DwtMessageDialog.WARNING_STYLE);
		dlg.popup();
		return;
	}
	var origHideDelete = this._hideDeletePref;
	var origShowSendAndArchive = this._showSendAndArchive;
	var hideDelete = document.getElementById("archiveHideDelete1").checked;
	var noHideDelete = document.getElementById("archiveHideDelete2").checked;

	var showSendAndArchive = document.getElementById("sendAndArchive2").checked;
	var hideSendAndArchive = document.getElementById("sendAndArchive1").checked;

	
	this._preferenceDialog.popdown();
	
	this._hideDeletePref = hideDelete ? true : false;
	this._showSendAndArchive = showSendAndArchive ? true : false;
	
	if (origHideDelete == this._hideDeletePref && origShowSendAndArchive == this._showSendAndArchive) {
		return;
	}
	
	var keyVal = this._getMetaKeyVal();
	
	this.metaData.set("archiveZimlet", keyVal, null, new AjxCallback(this, this._handleSetArchivePrefs), null, true);

};

ZmArchiveZimlet.prototype._hideDeleteButton = 
function(display) {
	var style = display ? "none" : "inline-block";
	var app = appCtxt.getApp(ZmApp.MAIL);
	if (appCtxt.getCurrentAppName() === ZmApp.MAIL) {
		var mlc = appCtxt.getCurrentApp().getMailListController();
		var toolbar = mlc._toolbar[appCtxt.getCurrentViewId()];
		if (!toolbar) { return; }
		var deleteBtn = toolbar.getButton(ZmOperation.DELETE) || toolbar.getButton(ZmOperation.DELETE_MENU);
		if(deleteBtn) {
			deleteBtn.getHtmlElement().style.display = style;
		}
	}
}; 

ZmArchiveZimlet.prototype._handleSetArchivePrefs = 
function() {
	this._hideDeleteButton(this._hideDeletePref);
	appCtxt.getAppController().setStatusMsg(this.getMessage("archiveZimletPrefsSaved"), ZmStatusView.LEVEL_INFO);
};

ZmArchiveZimlet.prototype.onSelectApp = 
function(appId) {
	if (appId == ZmApp.MAIL && this._hideDeletePref != null && !this.mailAlreadyLoaded)  {
		this._hideDeleteButton(this._hideDeletePref);
		this.mailAlreadyLoaded = true;
	}
};

ZmArchiveZimlet.prototype.sendAndArchiveListener = 
function(button) {
	if (button) {
		button.setEnabled(false);
	}
	var cc = appCtxt.getApp(ZmApp.MAIL).getComposeController(appCtxt.getApp(ZmApp.MAIL).getCurrentSessionId(ZmId.VIEW_COMPOSE));
	//var callback = new AjxCallback (this,this._handleArchive);
	if (cc && cc._msg && cc._msg.id) {
		this._msgMap[cc._msg.id] = true;
	}
	cc.sendMsg();
	//cc.sendMsg(null, null, callback);
};

ZmArchiveZimlet.prototype._handleArchive = 
function(result) {
	//get the ID from the response
	if (result && result.Body && result.Body.SendMsgResponse && result.Body.SendMsgResponse.m) {
		var msgId = result.Body.SendMsgResponse.m[0].id;
		this._msgMap[msgId] = true;
	}
	
};

ZmArchiveZimlet.prototype.onSendMsgSuccess = function(controller, msg) {
	var id = msg.id || (msg._origMsg && msg._origMsg.id);
	var cid = msg.cid || (msg._origMsg && msg._origMsg.cid);
	if (this._msgMap[id]) {
		var m = appCtxt.getById(id);
		var conv = cid ? appCtxt.getById(cid) : null;
		var obj = conv ? conv : m;
		if (obj && obj.list && obj.list.moveItems) {
			obj.list.moveItems({items:obj, folder:this._archiveFolder});
		}
		delete this._msgMap[id];
	}
};

ZmArchiveZimlet.prototype._getMetaKeyVal = 
function() {
	var keyVal = [];
	keyVal["hideDeleteButton"] = this._hideDeletePref ? "true" : "false";
	keyVal["showSendAndArchive"] = this._showSendAndArchive ? "true" : "false";
	keyVal["archivedFolder"] = this._archiveFolderId;
	return keyVal;
};