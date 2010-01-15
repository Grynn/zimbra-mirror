/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////////////////////
// Trains basic and important Keyboard shortcuts by displaying them whenever user uses mouse instead of keyboard shortcut
//Author: Raja Rao D.V.
//////////////////////////////////////////////////////////////////////////////

function com_zimbra_shortcutstrainer() {
}

com_zimbra_shortcutstrainer.prototype = new ZmZimletBase();
com_zimbra_shortcutstrainer.prototype.constructor = com_zimbra_shortcutstrainer;


// Public methods

com_zimbra_shortcutstrainer.prototype.toString =
function() {
	return "com_zimbra_shortcutstrainer";
};

com_zimbra_shortcutstrainer.prototype.init =
function() {
	this.turnOnShortcutsTrainer = this.getUserProperty("turnOnShortcutsTrainer") == "true";
	if (!this.turnOnShortcutsTrainer)
		return;

	this.loadedViewsCount = 0;
	this.loadedViews = new Array();
	this.noActionMenuInThisView = new Array();
	this.transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
	this.onShowView();
	this._storeKeys();
};

com_zimbra_shortcutstrainer.prototype._storeKeys =
function() {
	this._ZmKeysReverse = new Array();
	for (var key in ZmKeys) {
		if (key.indexOf("description") >= 0 || key.indexOf("display") >= 0) {
			this._ZmKeysReverse[ZmKeys[key]] = key;
		}
	}
};

com_zimbra_shortcutstrainer.prototype.selectionListener =
function(ev) {
	if (ev.detail != undefined && (ev.detail == 1 ||  ev.detail == 3))
		return;
	var item = ev.item;
	if (item._data != undefined) {
		var id = "";
		var dataObj = item._data;
		if (dataObj._buttonId)
			id = dataObj._buttonId;
		else if (dataObj.buttonId)
			id = dataObj.buttonId;
		else if (dataObj._opId)
				id = dataObj._opId;
			else if (dataObj._id_)
					id = dataObj._id_;

		var text = "";
		if (item instanceof ZmAppButton) {
			text = this.getAppShortCutFromId(id);
			itemType = "Application";
		} else	 if (item instanceof DwtTreeItem) {
			//pass dataObj._object_ which is ZmTreeItem
			if (dataObj._object_ != undefined) {
				var zmfolder = dataObj._object_;
				text = this.getAppShortCutFromZmTreeItem(zmfolder);
				itemType = zmfolder.type.toLowerCase();
			}
		} else {
			var opDesc = ZmOperation._operationDesc[id] || ZmOperation.defineOperation(id);
			if (opDesc.shortcut == undefined) {//lot of ops dont have sc set on them
				text = this.getShortcutFromOp(opDesc);
			} else {
				text = this.getShortcutFromShortcutKey(opDesc.shortcut);
			}
			itemType = "button";
		}
		this.showShortCut(itemType, item.getText(), text);

	}
};

com_zimbra_shortcutstrainer.prototype.showShortCut =
function(objType, objName, text) {

	if (text != undefined && text != "") {
		// try to pick first single-character shortcut
		var list = text.split(/;\s*/);
		var sc = list[0];
		for (var i = 0; i < list.length; i++) {
			var s = list[i];
			if (s.indexOf(",") == -1) {
				sc = list[i];
				break;
			}
		}
	}
	if (sc == undefined)
		return;

	//appCtxt.getAppController().setStatusMsg("OBJ: "+ txt + " id: "+ id + "SC: "+ sc, ZmStatusView.LEVEL_INFO);
	sc = sc.toLowerCase();
	if (sc.indexOf(",") > 0) {
		var arry = sc.split(",");
		sc = [ "[ ",  arry[0],  " then ", arry[1],  " ]" ].join("");
	} else {
		sc = ["[ ", sc , " ]" ].join("");
	}

	if (objName.indexOf("(") > 0) {
		objName = objName.substring(0, objName.indexOf("(") - 1);
	}

	appCtxt.getAppController().setStatusMsg(["Shortcut for ", objType, " '", objName , "' = ", sc].join(""), ZmStatusView.LEVEL_INFO, null, this.transitions);

};

com_zimbra_shortcutstrainer.prototype.getShortcutFromOp =
function(op) {
	var scKey = this._ZmKeysReverse[op.text];
	if (scKey == undefined)
		return;

	if (scKey.indexOf("description") >= 0) {
		scKey = scKey.replace("description", "display");
		return  AjxKeys[scKey] || ZmKeys[scKey];
	}
};

com_zimbra_shortcutstrainer.prototype.getShortcutFromShortcutKey =
function(shortcutKey) {

	var text = "";
	if (this.viewId.indexOf("COMPOSE") >= 0) {//try at view level
		scKey = [ "compose", shortcutKey, "display"].join(".");
		text = AjxKeys[scKey] || ZmKeys[scKey];
	}

	if (text == undefined || text == "") {//try at the app level
		var appName = appCtxt.getCurrentApp().getName().toLowerCase();
		scKey = [appName, shortcutKey, "display"].join(".");
		text = AjxKeys[scKey] || ZmKeys[scKey];
	}

	if (text == undefined || text == "") {//try at the global level
		scKey = [ "global", shortcutKey, "display"].join(".");
		text = AjxKeys[scKey] || ZmKeys[scKey];
	}
	return text;

};

com_zimbra_shortcutstrainer.prototype.getAppShortCutFromId =
function(id) {
	scKey = [ "global", "GoTo" + id, "display"].join(".");
	return AjxKeys[scKey] || ZmKeys[scKey];
};

com_zimbra_shortcutstrainer.prototype.getAppShortCutFromZmTreeItem =
function(zmTreeItem) {
	if (zmTreeItem == undefined)
		return null;

	var sysName = zmTreeItem._systemName;
	if (sysName != undefined && sysName != false && !(zmTreeItem instanceof ZmTag)) {
		scKey = [ "mixed", "GoTo" + sysName, "display"].join(".");
		return AjxKeys[scKey] || ZmKeys[scKey];
	} else {
		var firstKey = "";
		if (zmTreeItem.type == "FOLDER")
			firstKey = "v";
		else if (zmTreeItem.type == "SEARCH")
			firstKey = "s";
		else if (zmTreeItem.type == "TAG")
				firstKey = "y";


		return this.getFolderCustomShortCut(zmTreeItem.id, firstKey);
	}
};

com_zimbra_shortcutstrainer.prototype.getFolderCustomShortCut =
function(id, firstKey) {
	if (this.customShortcuts == undefined) {
		var kmm = appCtxt.getAppController().getKeyMapMgr();
		var setting = appCtxt.get(ZmSetting.SHORTCUTS);
		this.customShortcuts = kmm ? ZmShortcut.parse(setting, kmm) : null;
	}
	for (var i = 0; i < this.customShortcuts.length; i++) {
		var sc = this.customShortcuts[i];
		if (sc.arg == id) {
			return  firstKey + "," + sc.num;
		}
	}
};

com_zimbra_shortcutstrainer.prototype.doubleClicked =
function(canvas) {
	// do nothing
};

com_zimbra_shortcutstrainer.prototype.onShowView =
function(viewId, isNewView) {
	this.turnOnShortcutsTrainer = this.getUserProperty("turnOnShortcutsTrainer") == "true";
	if (!this.turnOnShortcutsTrainer)
		return;
	if (viewId == undefined) {
		viewId = appCtxt.getAppViewMgr().getCurrentViewId();
	}
	this.viewId = viewId;

	if (this.loadedViews[viewId] == undefined) {
		this.loadedViews[viewId] = true;
		this.loadedViewsCount++;
	} else {
		return;
	}


	//folders
	/* folders no longer work
	if (this._folderListnersAdded == undefined) {
		try {
			var accordionController = ZmAppAccordionController.getInstance();
			var accordion = accordionController.getAccordion(true);
			var overview = (accordion) ? accordionController.getCurrentOverview() : null;
			overview._treeHash.FOLDER.addSelectionListener(new AjxListener(this, this.selectionListener));
			overview._treeHash.SEARCH.addSelectionListener(new AjxListener(this, this.selectionListener));
			overview._treeHash.TAG.addSelectionListener(new AjxListener(this, this.selectionListener));

			this._folderTree = overview._treeHash.FOLDER;
			this._folderListnersAdded = true;
		} catch(e) {
		}
	}
	*/

	if (appCtxt.getCurrentController() == undefined)
		return;

	//toolbar buttons
	try {
		var tb = "";
		if (viewId.indexOf("COMPOSE") == -1) {
			if(appCtxt.getCurrentController) {
				if(appCtxt.getCurrentController()._toolbar){
					tb = appCtxt.getCurrentController()._toolbar;
				} else if(appCtxt.getCurrentController().getCurrentToolbar) {
					tb = appCtxt.getCurrentController().getCurrentToolbar();
				}
			}
			if(tb != "") {		
				tb = tb[viewId];
				if(tb) {
					var btnArry = tb.getChildren();
					for (var i = 0; i < btnArry.length; i++) {
						btnArry[i].addSelectionListener(new AjxListener(this, this.selectionListener));
					}
				}
			}
		}

	} catch(e) {
		//do nothing
	}

	//action menu buttons
	try {
		if (this.noActionMenuInThisView[viewId] == undefined) {
			if(appCtxt.getCurrentController().getActionMenu) {
				var menu = appCtxt.getCurrentController().getActionMenu();
				if(menu) {
					var menuItemsArry = menu.getChildren();
					for (var i = 0; i < menuItemsArry.length; i++) {
						menuItemsArry[i].addSelectionListener(new AjxListener(this, this.selectionListener));
					}
				} else {
					this.noActionMenuInThisView[viewId] = true;
				}
			}
		}
	} catch(e) {
		this.noActionMenuInThisView[viewId] = true;
	}

	if (this.loadedViewsCount == 1) {
		//app buttons
		var appBtnArry = appCtxt.getAppController().getAppChooser().getChildren();
		for (var i = 0; i < appBtnArry.length; i++) {
			appBtnArry[i].addSelectionListener(new AjxListener(this, this.selectionListener));
		}
	}
};


//------------------------------------------------------------------------------------------
//			SHOW PREFERENCES DIALOG
//------------------------------------------------------------------------------------------

com_zimbra_shortcutstrainer.prototype.doubleClicked = function() {
	this.singleClicked();
};

com_zimbra_shortcutstrainer.prototype.singleClicked = function() {
	this.showPrefDialog();
};

com_zimbra_shortcutstrainer.prototype.showPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().innerHTML = this.createPrefView();
	if (this.getUserProperty("turnOnShortcutsTrainer") == "true") {
		document.getElementById("sct_turnOnShortcutsTrainer").checked = true;
	}
	this.pbDialog = this._createDialog({title:"'Shortcuts Trainer' Zimlet Preferences", view:this.pView, standardButtons:[DwtDialog.OK_BUTTON]});
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this.pbDialog.popup();

};

com_zimbra_shortcutstrainer.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
	if (document.getElementById("sct_turnOnShortcutsTrainer").checked) {
		if (!this.turnOnShortcutsTrainer) {
			this._reloadRequired = true;
		}
		this.setUserProperty("turnOnShortcutsTrainer", "true", true);

	} else {
		this.setUserProperty("turnOnShortcutsTrainer", "false", true);
		if (this.turnOnShortcutsTrainer)
			this._reloadRequired = true;
	}
	this.pbDialog.popdown();
	if (this._reloadRequired) {
		window.onbeforeunload = null;
		var url = AjxUtil.formatUrl({});
		ZmZimbraMail.sendRedirect(url);
	}
};

com_zimbra_shortcutstrainer.prototype.createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "<input id='sct_turnOnShortcutsTrainer'  type='checkbox'/>Turn ON Shortcut Trainer (changing this would refresh the browser)";
	html[i++] = "</DIV>";
	return html.join("");

};