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

//Zimlet Class
function ZmArchiveZimlet() {
}

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
	var params = {overviewId: dialog.getOverviewId(ZmApp.MAIL), appName:ZmApp.MAIL, skipReadOnly:true, skipRemote:false,};
	dialog.popup(params);
};

ZmArchiveZimlet.prototype._handleChooseFolder = 
function(postCallback, organizer) {
	var dialog = appCtxt.getChooseFolderDialog();
	dialog.popdown();
	
	if (organizer) {
		this._archiveFolder = organizer;
		this._archiveFolderId = organizer.id;
		var keyVal = [];
		keyVal["archivedFolder"] = this._archiveFolderId;
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
	var viewType = appCtxt.getViewTypeFromId(viewId);
	if (viewType == ZmId.VIEW_CONVLIST || viewType == ZmId.VIEW_CONV || viewType == ZmId.VIEW_TRAD || viewType == ZmId.VIEW_MSG) {
		//if (this._hideDeletePref) {
		//	this._hideDeleteButton(toolbar);
		//}
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

			// override the function to reset the operations in the toolbar as there is no method to
			// set when to enable or disable buttons based on the selection in the button api
			var originalFunction = controller._resetOperations;
			controller._resetOperations = function(parent, num) {
				originalFunction.apply(controller, arguments);
				parent.enable(ZmArchiveZimlet.ARCHIVE_BUTTON_ID, num);
			};
			
			//add listener to listview so that we can enable button when multiple items are selected
			var listView = controller.getListView();
			if (listView && listView.addSelectionListener) {
				listView.addSelectionListener(new AjxListener(this, this._listActionListener, button));
			}
		}
	}
};

ZmArchiveZimlet.prototype._handleGetMetaData = 
function(result) {
	try {
		var response = result.getResponse().BatchResponse.GetMailboxMetadataResponse[0];
		if (response.meta && response.meta[0] && response.meta[0]._attrs ) {
			this._hideDeletePref = response.meta[0]._attrs["hideDeleteButton"];
			if (this._hideDeletePref) {
				this._hideDeleteButton();
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
	var keyVal = [];
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

	return [
		"<div class='mailArchivePrefDialog'>",
		"<table class='ZPropertySheet' cellspacing='6'>",
		"<tr><td style='text-align:right;'>" + this.getMessage("archiveFolderPrefLabel") + ": </td><td id='ARCHIVE_ZIMLET_FOLDER'></td></tr>" +
		"<tr><td style='text-align:right;'>" + this.getMessage("archiveHideDeleteButton") + ": </td><td>",
		"<table class='ZRadioButtonTable'><tr>",
        "<td><input id='archiveHideDelete2' name='archiveHideDelete' value='false' " + noHideDelete + " type='radio'/></td>",
        "<td><label style='margin-right:1em;'>" + this.getMessage("archiveNo") + "</label></td>",
        "<td><input id='archiveHideDelete1' name='archiveHideDelete' value='true' " + hideDelete + " type='radio'/></td>",
        "<td><label>" + this.getMessage("archiveYes") + "</label></td>",
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
	this._preferenceDialog.popdown();
	
	var hideDeletePref1 = document.getElementById("archiveHideDelete1").checked;
	var hideDeletePref2 = document.getElementById("archiveHideDelete2").checked;

	if (hideDeletePref1 && this._hideDeletePref) {
		return;
	}
	if (hideDeletePref2 && !this._hideDeletePref) {
		return;
	}
	this._hideDeletePref = hideDeletePref1 ? hideDeletePref1 : !hideDeletePref2;
	var keyVal = [];
	keyVal["hideDeleteButton"] = this._hideDeletePref;
	keyVal["archivedFolder"] = this._archiveFolderId;
	this.metaData.set("archiveZimlet", keyVal, null, new AjxCallback(this, this._handleSetArchivePrefs), null, true);

};

ZmArchiveZimlet.prototype._hideDeleteButton = 
function(display) {
	var style = display ? "inline-block" : "none";
	if (appCtxt.getCurrentAppName() === ZmApp.MAIL) {
		var mlc = appCtxt.getCurrentApp().getMailListController();
		var toolbar = mlc._toolbar[appCtxt.getCurrentViewId()];
		var deleteBtn = toolbar.getButton(ZmOperation.DELETE) || toolbar.getButton(ZmOperation.DELETE_MENU);
		if(deleteBtn) {
			deleteBtn.getHtmlElement().style.display = style;
		}
	}
}; 

ZmArchiveZimlet.prototype._handleSetArchivePrefs = 
function() {
	if (this._hideDeletePref) {
		this._hideDeleteButton();
	}
	else {
		this._hideDeleteButton(true);
	}
	appCtxt.getAppController().setStatusMsg(this.getMessage("archiveZimletPrefsSaved"), ZmStatusView.LEVEL_INFO);
};