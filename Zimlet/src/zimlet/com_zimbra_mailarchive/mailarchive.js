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
			this.archivefldrName = this.getMessage("archived");
			ZmMsg.archiveZimlet_archiveFolderNotFound = this.getMessage("archiveZimlet_archiveFolderNotFound");
			//comment this for now until we get support to move across accounts
			//this._updateFolderContextMenu();
		};

/**
 * (Implemented w/in com_zimbra_archive Zimlet)
 * Add a new function to ZmListController that basically moves selected items to archive folder
 * @param archiveFolder ZmMailFolder Folder where mail items should be moved to Archive
 */
ZmListController.prototype._doArchiveViaZimlet =
		function(archiveFolder) {
			if (!archiveFolder) {
				appCtxt.getAppController().setStatusMsg(ZmMsg.archiveZimlet_archiveFolderNotFound, ZmStatusView.LEVEL_WARNING);
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
			var postCallback = new AjxCallback(this, this._doArchiveViaZimlet);
			zimlet.getArchiveFolder(postCallback);
		};

//The below code adds "Archive" menu-item to folders and also tries to move and make the selected folder
//subfolder of "Archive" folder. This is mainly useful in ZD But, currently ZD doesnot support cross-account
//folder-move, so commenting until its supported.
/**
 * (Implemented w/in com_zimbra_archive Zimlet)
 * Add a new function to ZmTreeController that basically moves selected folder under archive folder
 * called "Archived" folder
 * Note: "Archived" is under Local account in Zimbra Desktop

 ZmTreeController.prototype._doArchiveFolderViaZimlet =
 function(archiveFolder) {
 if (!archiveFolder) {
 appCtxt.getAppController().setStatusMsg(ZmMsg.archiveZimlet_archiveFolderNotFound, ZmStatusView.LEVEL_WARNING);
 return;
 }
 this._doMove(this._pendingActionData, archiveFolder);
 };

 /**
 * (Implemented w/in com_zimbra_archive Zimlet)
 * Listens to "Archive" menu item
 * @param zimlet ZmZimlet This Zimlet or any Zimlet that implements getArchiveFolder method
 *							 getArchiveFolder function must return folder where we should Archive
 * @param ev	Event	Document Event
 *
 ZmTreeController.prototype._archiveFolderViaZimletMenuItemListener =
 function(zimlet, ev) {
 this._pendingActionData = this._getActionedOrganizer(ev);
 var postCallback = new AjxCallback(this, this._doArchiveFolderViaZimlet);
 zimlet.getArchiveFolder(postCallback);
 };

 ZmArchiveZimlet.prototype._updateFolderContextMenu =
 function() {
 var folderTreeController = appCtxt.getOverviewController().getTreeController(ZmOrganizer.FOLDER);
 if (!folderTreeController) {
 return;
 }
 var menu = folderTreeController._getActionMenu();
 var mi = menu.createMenuItem(Dwt.getNextId(), {image:"archiveZimletIcon", text:this.getMessage("label")});
 mi.addSelectionListener(new AjxListener(folderTreeController, folderTreeController._archiveFolderViaZimletMenuItemListener, this));

 };
 ****************/

ZmArchiveZimlet.prototype.onParticipantActionMenuInitialized =
		function(controller, menu) {
			this.onActionMenuInitialized(controller, menu);
		};

//called by zimbra-core when menu is initialized
ZmArchiveZimlet.prototype.onActionMenuInitialized =
		function(controller, menu) {
			this._hideMenuBtn(controller, menu);
		};

ZmArchiveZimlet.prototype._hideMenuBtn = function(controller, menu) {
	menu.getMenuItem(ZmOperation.DELETE).getHtmlElement().style.display = "none";
};

ZmArchiveZimlet.prototype.initializeToolbar =
		function(app, toolbar, controller, viewId) {
			//conversation-list-view or conversation-view or traditional-view(aka message-view)
			var viewType = appCtxt.getViewTypeFromId(viewId);
			if (viewType == ZmId.VIEW_CONVLIST || viewType == ZmId.VIEW_CONV || viewType == ZmId.VIEW_TRAD ||
					viewType == ZmId.VIEW_MSG) {
				var deleteBtn = toolbar.getButton(ZmOperation.DELETE) || toolbar.getButton(ZmOperation.DELETE_MENU);
				if(deleteBtn) {
					deleteBtn.getHtmlElement().style.display = "none";
				}

				var buttonIndex = 0;
				for (var i = 0, count = toolbar.opList.length; i < count; i++) {
					if (toolbar.opList[i] == ZmOperation.DELETE ||toolbar.opList[i] == ZmOperation.DELETE_MENU) {
						buttonIndex = i + 1;
						break;
					}
				}
				var buttonArgs = {
					text	: this.getMessage("label"),
					tooltip: this.getMessage("description"),
					index: buttonIndex,
					image: "archiveZimletIcon"
				};
				if (!toolbar.getOp(ZmArchiveZimlet.ARCHIVE_BUTTON_ID)) {
					var button = toolbar.createOp(ZmArchiveZimlet.ARCHIVE_BUTTON_ID, buttonArgs);
					button.addSelectionListener(new AjxListener(controller, controller._archiveViaZimletListener, [this]));
					//add listener to listview so that we can enable button when multiple items are selected
					var listView = controller.getListView();
					if (listView && listView.addSelectionListener) {
						listView.addSelectionListener(new AjxListener(this, this._listActionListener, button));
					}
				}
			}
		};

ZmArchiveZimlet.prototype._listActionListener =
		function(button) {
			button.setEnabled(true);
		};
/**
 * Gets (or Creates) Archive folder and passes the folder to callback
 * @param callback
 */
ZmArchiveZimlet.prototype.getArchiveFolder =
		function(callback) {
			if (this.archiveFolder) {
				if (callback) {
					callback.run(this.archiveFolder);
				}
				return;
			}
			var soapDoc = AjxSoapDoc.create("GetFolderRequest", "urn:zimbraMail");
			var folderNode = soapDoc.set("folder");
			folderNode.setAttribute("l", "1");

			//if offline, use local-account
			if (appCtxt.isOffline) {
				var accountName = appCtxt.accountList.mainAccount.name;
			}
			var getFolderCallback = new AjxCallback(this, this._getFolderHandler, {callback:callback});
			appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode : true, noBusyOverlay : true, accountName:accountName, callback:getFolderCallback});
		};

ZmArchiveZimlet.prototype._getFolderHandler =
		function (params, response) {
			var callback = params.callback;
			var respObj = response.getResponse();
			var folders;
			var validResponse = false;
			if (respObj && respObj.GetFolderResponse && respObj.GetFolderResponse.folder) {
				var resp = respObj.GetFolderResponse.folder;
				if(resp && (resp instanceof Array)) {
					var top = resp[0];
					if(top && top.folder) {
						folders = top.folder;
						validResponse = true;
					}
				}
			}
			if (folders) {
				for (var i = 0; i < folders.length; i++) {
					var f = folders[i];
					if (f && f.name == this.archivefldrName && f.view == ZmArchiveZimlet.view) {
						this.archiveFolder = appCtxt.getById(f.id);
						break;
					}
				}
			}
			if (this.archiveFolder) {
				if (callback) {
					callback.run(this.archiveFolder);
				}
			} else if(validResponse) { //valid response but there was no such folder, create one.
				this.createFolder(callback);
			}
		};

ZmArchiveZimlet.prototype.createFolder =
		function(postCallback) {
			var params = {color:null, name:this.archivefldrName, url:null, view:ZmArchiveZimlet.view, l:"1", postCallback:postCallback};
			this._createFolder(params);
		};


ZmArchiveZimlet.prototype._createFolder =
		function(params) {
			var jsonObj = {CreateFolderRequest:{_jsns:"urn:zimbraMail"}};
			var folder = jsonObj.CreateFolderRequest.folder = {};
			for (var i in params) {
				if (i == "callback" || i == "errorCallback" || i == "postCallback") {
					continue;
				}

				var value = params[i];
				if (value) {
					folder[i] = value;
				}
			}
			var _createFldrCallback = new AjxCallback(this, this._createFldrCallback, params);
			var _createFldrErrCallback = new AjxCallback(this, this._createFldrErrCallback, params);

			//if offline, use local-account
			if (appCtxt.isOffline) {
				var accountName = appCtxt.accountList.mainAccount.name;
			}
			return appCtxt.getAppController().sendRequest({jsonObj:jsonObj, asyncMode : true, noBusyOverlay : true, accountName:accountName, asyncMode:true, errorCallback:_createFldrErrCallback, callback:_createFldrCallback});
		};

ZmArchiveZimlet.prototype._createFldrCallback =
		function(params, response) {
			if (params.name == this.archivefldrName) {
				var archiveFldrId = response.getResponse().CreateFolderResponse.folder[0].id;
				if (params.postCallback) {
					setTimeout(AjxCallback.simpleClosure(this._setFolderAndPostCallback_delayed, this, params.postCallback, archiveFldrId), 500);
				}
			} else {
				appCtxt.getAppController().setStatusMsg(this.getMessage("archivedFolderCreated"), ZmStatusView.LEVEL_INFO);
			}
		};

//call postcallback on the folder after about a second to allow folder to be created
ZmArchiveZimlet.prototype._setFolderAndPostCallback_delayed =
		function(postCallback, archiveFldrId) {
			this.archiveFolder = appCtxt.getById(archiveFldrId);
			postCallback.run(this.archiveFolder);
		};

ZmArchiveZimlet.prototype._createFldrErrCallback =
		function(params, ex) {
			if (!params.url && !params.name) {
				return false;
			}
			var msg;
			if (params.name && (ex.code == ZmCsfeException.MAIL_ALREADY_EXISTS)) {
				msg = AjxMessageFormat.format(ZmMsg.errorAlreadyExists, [params.name]);
			} else if (params.url) {
				var errorMsg = (ex.code == ZmCsfeException.SVC_RESOURCE_UNREACHABLE) ? ZmMsg.feedUnreachable : ZmMsg.feedInvalid;
				msg = AjxMessageFormat.format(errorMsg, params.url);
			}
			appCtxt.getAppController().setStatusMsg(this.getMessage("couldNotCreateArchivedFolder"), ZmStatusView.LEVEL_WARNING);
			if (msg) {
				this._showErrorMsg(msg);
				return true;
			}
			return false;
		};

/**
 * Shows Error message dialog.
 *
 * @param {string} msg  	the error message
 */
ZmArchiveZimlet.prototype._showErrorMsg =
function(msg) {
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.reset();
	msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE);
	msgDialog.popup();
};