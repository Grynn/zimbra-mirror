/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
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
 * folder choosing widget to be used in a drop-down.
 * @constructor
 * @class
 *
 * @author Eran Yarkon
 *
 * @param {hash}		params			a hash of parameters
 * @param {DwtComposite}      params.parent			the parent widget
 * @param {string}      params.className			the CSS class
 * @param {constant}      params.posStyle			the positioning style (see {@link Dwt})
 *
 * @extends		DwtComposite
 */
DwtFolderChooser = function(params) {
	if (arguments.length == 0) { return; }
	params.className = params.className || "DwtFolderChooser";
	DwtComposite.call(this, params);

	this._overview = {};
	this._opc = appCtxt.getOverviewController();
	this._treeView = {};
	this._folderTreeDivId = this._htmlElId + "_folderTreeDivId";

	this._uuid = Dwt.getNextId();

	this._treeViewListener = this._treeViewSelectionListener.bind(this);

	var moveMenu = params.parent;
	moveMenu._addItem(this, params.index); //this is what DwtMenuItem does. Allows this item to be in the menu items table - better for layout purposes such as consistent widths

	if (!params.noNewItem) {
		//add separator menu item on the move menu (the parent)
		new DwtMenuItem({parent:moveMenu, style:DwtMenuItem.SEPARATOR_STYLE});
	
		// add static "New Folder" menu item
		var newFolderItem = this._newButton = new DwtMenuItem({parent:moveMenu, id: moveMenu.getHTMLElId() + "|NEWFOLDER"});
		var newText = ZmMsg.newFolder;
		var newImage = "NewFolder";
		var newShortcut = ZmKeyMap.NEW_FOLDER;
		var appName = appCtxt.getCurrentAppName();
		if (appName == ZmApp.CALENDAR) {
			newText = ZmMsg.newCalendar;
			newImage = "NewAppointment";
			newShortcut = ZmKeyMap.NEW_CALENDAR;
		}
		if (appName == ZmApp.TASKS) {
			newText = ZmMsg.newTaskFolder;
			newImage = "NewTaskList";
		}
		if (appName == ZmApp.CONTACTS) {
			newText = ZmMsg.newAddrBook;
			newImage = "NewContactsFolder";
		}
	
		newFolderItem.setText(newText);
		newFolderItem.setImage(newImage);
		newFolderItem.setShortcut(appCtxt.getShortcutHint(this._keyMap, newShortcut));
	
		newFolderItem.addSelectionListener(this._showNewDialog.bind(this));
	}

	this._init();

};

DwtFolderChooser.prototype = new DwtComposite;
DwtFolderChooser.prototype.constructor = DwtFolderChooser;

DwtFolderChooser.prototype.isDwtFolderChooser = true;
DwtFolderChooser.prototype.toString = function() { return "DwtFolderChooser"; };


/**
 *
 * see ZmChooseFolderDialog.prototype.popup
 */
DwtFolderChooser.prototype.setupFolderChooser =
function(params, selectionCallback) {

	this._selectionCallback = selectionCallback;
	this._overviewId = params.overviewId;

	ZmChooseFolderDialog.prototype.popup.call(this, params, true);
};

DwtFolderChooser.prototype._getNewButton =
function () {
	return this._newButton;
};


DwtFolderChooser.prototype.updateData =
function(data) {
	this._data = data;
};


DwtFolderChooser.prototype._focus =
function() {
	var overview = this._overview[this._overviewId];
	overview.focus();
};

/**
 * this is not really doing the popup, just setting more stuff up, but to reuse the caller (ZmChooseFolderDialog.prototype.popup)
 * from ZmChooseFolderDialog, I had to keep the name.
 *
 * @param params
 */
DwtFolderChooser.prototype._doPopup =
function(params) {
	ZmChooseFolderDialog.prototype._doPopup.call(this, params, true);
};


/**
 * this reuses ZmDialog stuff. With slight necessary changes. Might be fragile if this is changed in ZmDialog
 * in which case we might be better off with copy-paste. but for now it works.
 * 
 * @param params
 * @param forceSingle
 */
DwtFolderChooser.prototype._setOverview =
function(params, forceSingle) {
	params.overviewClass = "menuOverview";

	var overview = ZmDialog.prototype._setOverview.call(this, params, forceSingle); //reuse from ZmDialog

	overview.getHtmlElement().style.overflowX = "hidden"; //must do that or the vertical scrollbar causes a horizontal one to be added as well. might be some better solution to that, but not sure what.
	
	if (!appCtxt.multiAccounts || forceSingle) {
		//this  is needed for some reason
		this._overview[params.overviewId] = overview;
	}

	return overview;
};


/**
 * delegate to ZmDialog. called from ZmDialog.prototype._setOverview (which we delegate to from DwtFolderChooser.prototype._setOverview)
 */
DwtFolderChooser.prototype._renderOverview =
function() {
	ZmDialog.prototype._renderOverview.apply(this, arguments); //reuse code from ZmDialog
};

/**
 * delegate to ZmDialog. called from ZmDialog.prototype._setOverview (which we delegate to from DwtFolderChooser.prototype._setOverview)
 */
DwtFolderChooser.prototype._makeOverviewVisible =
function() {
	ZmDialog.prototype._makeOverviewVisible.apply(this, arguments); //reuse code from ZmDialog
};

DwtFolderChooser.prototype._resetTree =
function(treeIds, overview) {
	ZmChooseFolderDialog.prototype._resetTree.call(this, treeIds, overview);
};

DwtFolderChooser.prototype._getOverview =
function() {
	return ZmChooseFolderDialog.prototype._getOverview.call(this)
};


DwtFolderChooser.prototype._treeViewSelectionListener =
function(ev) {
	if (ev.detail != DwtTree.ITEM_SELECTED) {
		return;
	}
	if (!ev.clicked && !ev.enter) { //set in DwtTree.prototype._itemClicked and DwtTree.prototype.setSelection (which is called by DwtTreeItem.prototype.handleKeyAction)
		return;
	}

	//I kept this logic from ZmChooseFolderDialog.prototype._treeViewSelectionListener. Not sure what it means exactly
	if (this._getOverview() instanceof ZmAccountOverviewContainer) {
		if (ev.item instanceof DwtHeaderTreeItem) {
			return;
		}

		var oc = this._opc.getOverviewContainer(this._curOverviewId);
		var overview = oc.getOverview(ev.item.getData(ZmTreeView.KEY_ID));
		oc.deselectAll(overview);
	}

	var organizer = ev.item && ev.item.getData(Dwt.KEY_OBJECT);
	var value = organizer ? organizer.getName(null, null, true) : ev.item.getText();
	this._lastVal = value.toLowerCase();
	this._doSelection();
};

/**
 * copied mostly from ZmChooseFolderDialog.prototype._okButtonListener  
 * @param tgtFolder
 */
DwtFolderChooser.prototype._doSelection =
function(tgtFolder) {
    tgtFolder = tgtFolder || this._getOverview().getSelected();
    if  (!tgtFolder) {
        tgtFolder = appCtxt.getById(this._selected);
    }
	var folderList = (tgtFolder && (!(tgtFolder instanceof Array)))
		? [tgtFolder] : tgtFolder;

	var msg = (!folderList || (folderList && folderList.length == 0))
		? ZmMsg.noTargetFolder : null;

	//todo - what is that? can you move stuff to multiple targets?  annotation on ZmChooseFolderDialog show it might be for filters on multiple folders. obviously in that case we can't have a drop down. we might have to keep that folder dialog
	
	// check for valid target
	if (!msg && this._data) {
		for (var i = 0; i < folderList.length; i++) {
			var folder = folderList[i];
			if (folder.mayContain && !folder.mayContain(this._data, null, this._acceptFolderMatch)) {
				if (this._data instanceof ZmFolder) {
					msg = ZmMsg.badTargetFolder;
				}
				else {
					var items = AjxUtil.toArray(this._data);
					for (var i = 0; i < items.length; i++) {
						var item = items[i];
						if (!item) {
							continue;
						}
						if (item.isDraft && (folder.nId != ZmFolder.ID_TRASH && folder.nId != ZmFolder.ID_DRAFTS && folder.rid != ZmFolder.ID_DRAFTS)) {
							// can move drafts into Trash or Drafts
							msg = ZmMsg.badTargetFolderForDraftItem;
							break;
						}
						else if ((folder.nId == ZmFolder.ID_DRAFTS || folder.rid == ZmFolder.ID_DRAFTS) && !item.isDraft)	{
							// only drafts can be moved into Drafts
							msg = ZmMsg.badItemForDraftsFolder;
							break;
						}
					}
					if (!msg) {
						msg = ZmMsg.badTargetFolderItems;
					}
				}
				break;
			}
		}
	}

	if (msg) {
		ZmDialog.prototype._showError.call(this, msg);
		return;
	}
	if (this._selectionCallback) {
		this._selectionCallback(tgtFolder);
	}
};



DwtFolderChooser.prototype._resetTreeView =
function(visible) {
	ZmChooseFolderDialog.prototype._resetTreeView.call(this, visible);
};

DwtFolderChooser.prototype.getOverviewId =
function(part) {
	return appCtxt.getOverviewId([this.toString(), part], null);
};


DwtFolderChooser.prototype._loadFolders =
function() {
	ZmChooseFolderDialog.prototype._loadFolders.call(this);
};


DwtFolderChooser.prototype._init =
function() {
	var html = new Array(100);
	var idx = 0;

	html[idx++] =	"<table cellspacing='0' cellpadding='0' style='border-collapse:collapse;'>";
	html[idx++] =		"<tr><td><div id='" + this._folderTreeDivId + "'>";
	html[idx++] =		"</div></td></tr>";
	html[idx++] =	"</table>";

	this.getHtmlElement().innerHTML = html.join("");

};

DwtFolderChooser.prototype._showNewDialog =
function() {
	var item = this._getOverview().getSelected(true);
	var newType = (item && item.type) || this._treeIds[0];
	var ftc = this._opc.getTreeController(newType);
	var dialog = ftc._getNewDialog();
	dialog.reset();
	dialog.registerCallback(DwtDialog.OK_BUTTON, this._newCallback, this, [ftc, dialog]);
	this.parent.popdown(); //pop it down so it doenst pop down when user clicks on the "new" dialog, confusing them. this is also consistent with the tag menu "new".
	dialog.popup();
};

DwtFolderChooser.prototype._newCallback =
function(ftc, dialog, params) {
	//I created the callbackAfterNotifications param to allow for a callback to be called after the notifications at
	//ZmRequestMgr.prototype._handleResponseSendRequest. I need it here since I want the folder to exist (client side) when
	//the callback is called, and with the regular callback it does not since the notifications create it.
	// first I tried to make this synchronous (the creation step) and it works on Chrome but not on FF (which was stuck and no response was received on the subsequent move. no idea why)
	params.callbackAfterNotifications = this._postCreationCallback.bind(this);
	ftc._doCreate(params);
	dialog.popdown();
};

DwtFolderChooser.prototype._postCreationCallback =
function(result) {
	var folderId = result._data.CreateFolderResponse.folder[0].id;
	var folder = appCtxt.getFolderTree().getById(folderId);
	this._doSelection(folder);
};

