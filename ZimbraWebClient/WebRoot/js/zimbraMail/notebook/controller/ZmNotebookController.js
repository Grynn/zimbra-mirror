/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is: Zimbra Collaboration Suite Web Client
 *
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

function ZmNotebookController(appCtxt, container, app) {
	if (arguments.length == 0) return;
	ZmListController.call(this, appCtxt, container, app);

	this._listeners[ZmOperation.REFRESH] = new AjxListener(this, this._refreshListener);
	this._listeners[ZmOperation.EDIT] = new AjxListener(this, this._editListener);
	//this._listeners[ZmOperation.ATTACHMENT] = new AjxListener(this, this._uploadListener);
	this._listeners[ZmOperation.SEND_PAGE] = new AjxListener(this, this._sendPageListener);
	this._listeners[ZmOperation.DETACH] = new AjxListener(this, this._detachListener);
}
ZmNotebookController.prototype = new ZmListController;
ZmNotebookController.prototype.constructor = ZmNotebookController;

ZmNotebookController.prototype.toString = function() {
	return "ZmNotebookController";
};

// Constants

ZmNotebookController._VIEWS = {};
ZmNotebookController._VIEWS[ZmController.NOTEBOOK_PAGE_VIEW] = ZmNotebookPageView;
//ZmNotebookController._VIEWS[ZmController.NOTEBOOK_FILE_VIEW] = ZmPageEditView;

//
// Public methods
//

// view management

ZmNotebookController.prototype.show = function(arg) {
	throw "TODO: show method not implemented"
};

ZmNotebookController.prototype.switchView = function(view, force) {
	var viewChanged = force || view != this._currentView;

	if (viewChanged) {
		this._currentView = view;
		this._setup(view);
	}
	this._resetOperations(this._toolbar[view], 1);

	if (viewChanged) {
		var elements = {};
		elements[ZmAppViewMgr.C_TOOLBAR_TOP] = this._toolbar[this._currentView];
		elements[ZmAppViewMgr.C_APP_CONTENT] = this._listView[this._currentView];

		var ok = this._setView(view, elements, true);
		if (ok) {
			this._setViewMenu(view);
		}
	}
	Dwt.setTitle(this.getCurrentView().getTitle());
};

//
// Protected methods
//

// initialization

ZmNotebookController.prototype._getToolBarOps = function() {
	var list = [];
	list = list.concat(this._getBasicToolBarOps())
	list.push(ZmOperation.SEP);
	list = list.concat(this._getItemToolBarOps());
	list.push(ZmOperation.FILLER);
	list = list.concat(this._getNaviToolBarOps());
	return list;
};

ZmNotebookController.prototype._getBasicToolBarOps = function() {
	return [
		ZmOperation.NEW_MENU, ZmOperation.REFRESH, ZmOperation.EDIT,
	];
};
ZmNotebookController.prototype._getItemToolBarOps = function() {
	var list = [];
	if (this._appCtxt.get(ZmSetting.TAGGING_ENABLED)) {
		list.push(ZmOperation.TAG_MENU, ZmOperation.SEP);
	}
	list.push(
		ZmOperation.DELETE,
		ZmOperation.PRINT
		// ZmOperation.MOVE
	);
	/***
	if (this._appCtxt.get(ZmSetting.PRINT_ENABLED)) {
		list.push(ZmOperation.PRINT);
	}
	/***/
	return list;
};
ZmNotebookController.prototype._getNaviToolBarOps = function() {
	return [
		ZmOperation.SEND_PAGE,
		ZmOperation.SEP,
		ZmOperation.DETACH
	];
};

ZmNotebookController.prototype._initializeToolBar = function(view) {
	ZmListController.prototype._initializeToolBar.call(this, view);

	this._setNewButtonProps(view, ZmMsg.createNewPage, "NewPage", "NewPageDis", ZmOperation.NEW_PAGE);

	var toolbar = this._toolbar[this._currentView];
	var button = toolbar.getButton(ZmOperation.REFRESH);
	button.setImage("SendReceive");
	button.setDisabledImage("SendReceiveDis");

	/***
	var button = toolbar.getButton(ZmOperation.ATTACHMENT);
	button.setText(ZmMsg.addDocuments);
	button.setToolTipContent(ZmMsg.addDocumentsTT);
	/***/
};

ZmNotebookController.prototype._resetOperations = function(toolbarOrActionMenu, num) {
	if (!toolbarOrActionMenu) return;
	ZmListController.prototype._resetOperations.call(this, toolbarOrActionMenu, num);
	toolbarOrActionMenu.enable(ZmOperation.REFRESH, true);
	//toolbarOrActionMenu.enable(ZmOperation.ATTACHMENT, true);
	//toolbarOrActionMenu.enable(ZmOperation.DETACH, false);

	var buttons = [ZmOperation.EDIT, ZmOperation.TAG_MENU, ZmOperation.DELETE]; 
	var enabled = this._object && this._object.name != ZmNotebook.PAGE_INDEX;
	toolbarOrActionMenu.enable(buttons, enabled);
};

ZmNotebookController.prototype._getTagMenuMsg = function() {
	return ZmMsg.tagPage;
};

ZmNotebookController.prototype._doDelete = function(items) {
	var ids = ZmNotebookController.__itemize(items);
	if (!ids) return;

	var soapDoc = AjxSoapDoc.create("ItemActionRequest", "urn:zimbraMail");
	var actionNode = soapDoc.set("action");
	actionNode.setAttribute("id", ids);
	actionNode.setAttribute("op", "delete");

	var responseHandler = this._current == ZmController.NOTEBOOK_PAGE_VIEW ? this._listeners[ZmOperation.PAGE_BACK] : null;
	var params = {
		soapDoc: soapDoc,
		asyncMode: true,
		callback: responseHandler,
		errorCallback: null,
		noBusyOverlay: false
	};

	var appController = this._appCtxt.getAppController();
	var response = appController.sendRequest(params);
	return response;
};

// view management

ZmNotebookController.prototype._getViewType = function() {
	return this._currentView;
};

ZmNotebookController.prototype._defaultView = function() {
	return ZmController.NOTEBOOK_PAGE_VIEW;
};

ZmNotebookController.prototype._createNewView = function(view) {
	if (!this._listView[view]) {
		var viewCtor = ZmNotebookController._VIEWS[view];
		this._listView[view] = new viewCtor(this._container, this._appCtxt, this, this._dropTgt);
	}
	return this._listView[view];
};

ZmNotebookController.prototype._setViewContents = function(view) {
	this._listView[view].set(this._object);

	// Select the appropriate notebook in the tree view.
	if (this._object) {
		var overviewController = this._appCtxt.getOverviewController();
		var treeController = overviewController.getTreeController(ZmOrganizer.NOTEBOOK);
		var treeView = treeController.getTreeView(ZmZimbraMail._OVERVIEW_ID);
		if (treeView) {
			var folderId = this._object.getFolderId();
			var skipNotify = true;
			treeView.setSelected(folderId, skipNotify);
		}
	}
};

/*** TODO: This will be exposed later.
ZmNotebookController.prototype._setViewMenu = function(view) {
	var appToolbar = this._appCtxt.getCurrentAppToolbar();
	var menu = appToolbar.getViewMenu(view);
	if (!menu) {
		var listener = this._listeners[ZmOperation.VIEW];

		menu = new ZmPopupMenu(appToolbar.getViewButton());

		var item = menu.createMenuItem(ZmNotebookApp.PAGE, "Page", ZmMsg.notebookPageView, null, true, DwtMenuItem.RADIO_STYLE);
		item.setData(ZmOperation.MENUITEM_ID, ZmController.NOTEBOOK_PAGE_VIEW);
		item.addSelectionListener(listener);

		var item = menu.createMenuItem(ZmNotebookApp.FILE, "Folder", ZmMsg.notebookFileView, null, true, DwtMenuItem.RADIO_STYLE);
		item.setData(ZmOperation.MENUITEM_ID, ZmController.NOTEBOOK_FILE_VIEW);
		item.addSelectionListener(listener);
	}

	var item = menu.getItemById(ZmOperation.MENUITEM_ID, view);
	item.setChecked(true, true);

	appToolbar.setViewMenu(view, menu);
};
/***/

// listeners

ZmNotebookController.prototype._refreshListener = function(event) {
	var pageRef = this._history[this._place];
	if (pageRef) {
		if (this._place == 0) {
			this._showIndex(pageRef.folderId);
		}
		else {
			var cache = this._app.getNotebookCache();
			var page = cache.getPageByName(pageRef.folderId, pageRef.name);
			page.load();
			this._listView[this._currentView].set(page);
		}
	}
};

ZmNotebookController.prototype._editListener = function(event) {
	var pageEditController = this._app.getPageEditController();
	var page = this._listView[this._currentView].getVisiblePage();
	pageEditController.show(page);
};

/***
ZmNotebookController.prototype._uploadListener = function(event) {
	var tree = this._appCtxt.getTree(ZmOrganizer.NOTEBOOK);
	var notebook = tree.getById(this._folderId || ZmPage.DEFAULT_FOLDER);
	var callback = null;

	var dialog = this._appCtxt.getUploadDialog();
	dialog.popup(notebook, callback);
};
/***/

ZmNotebookController.prototype._sendPageListener = function(event) {
	var view = this._listView[this._currentView];
	var items = view.getSelection();
	items = items instanceof Array ? items : [ items ];

	var names = [];
	var urls = [];
	var content = "<wiklet class='NAME'/>";
	for (var i = 0; i < items.length; i++) {
		var item = items[i];
		urls.push(item.getRestUrl());
		names.push(ZmWikletProcessor.process(this._appCtxt, item, content));
	}

	var app = this._appCtxt.getApp(ZmZimbraMail.MAIL_APP);
	var controller = app.getComposeController();

	var action = ZmOperation.NEW_MESSAGE;
	var inNewWindow = this._appCtxt.get(ZmSetting.NEW_WINDOW_COMPOSE);
	var msg = new ZmMailMsg(this._appCtxt);
	var toOverride = null;
	var subjOverride = new AjxListFormat().format(names);
	var extraBodyText = urls.join("\n");

	controller.doAction(action, inNewWindow, msg, toOverride, subjOverride, extraBodyText);
};

ZmNotebookController.prototype._detachListener = function(event) {
	var view = this._listView[this._currentView];
	var items = view.getSelection();
	items = items instanceof Array ? items : [ items ];

	for (var i = 0; i < items.length; i++) {
		var item = items[i];

		var winurl = item.getRestUrl();
		var winname = "_new";
		var winfeatures = [
			"width=",(window.outerWidth || 640),",",
			"height=",(window.outerHeight || 480),",",
			"location,menubar,",
			"resizable,scrollbars,status,toolbar"
		].join("");

		var win = open(winurl, winname, winfeatures);
	}
};

//
// Private functions
//

ZmNotebookController.__itemize = function(objects) {
	if (objects instanceof Array) {
		var ids = [];
		for (var i = 0; i < objects.length; i++) {
			var object = objects[i];
			if (object.id) {
				ids.push(object.id);
			}
		}
		return ids.join();
	}
	return objects.id;
};
