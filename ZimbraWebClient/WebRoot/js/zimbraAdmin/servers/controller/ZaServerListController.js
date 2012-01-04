/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @constructor
* @class ZaServerListController
* This is a singleton object that controls all the user interaction with the list of ZaServer objects
* @author Greg Solovyev
**/
ZaServerListController = function(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container,"ZaServerListController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
	
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_servers/managing_servers.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this._helpButtonText = ZaServerListController.helpButtonText;
}

ZaServerListController.prototype = new ZaListViewController();
ZaServerListController.prototype.constructor = ZaServerListController;
ZaServerListController.helpButtonText = ZaMsg.helpManageServers;

ZaController.initToolbarMethods["ZaServerListController"] = new Array();
ZaController.initPopupMenuMethods["ZaServerListController"] = new Array();
ZaController.changeActionsStateMethods["ZaServerListController"] = new Array();

/**
* @param list {ZaItemList} a list of ZaServer {@link ZaServer} objects
**/
ZaServerListController.prototype.show = 
function(list, openInNewTab) {
    if (!this._UICreated) {
		this._createUI();
	} 	
	if (list != null)
		this._contentView.set(list.getVector());
				
	ZaApp.getInstance().pushView(this.getContentViewId());
	if (list != null)
		this._list = list;
		
	this.changeActionsState();		
}

ZaServerListController.initToolbarMethod =
function () {
   	this._toolbarOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.SERTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaServerListController.prototype._editButtonListener));    	
   	this._toolbarOperations[ZaOperation.FLUSH_CACHE] = new ZaOperation(ZaOperation.FLUSH_CACHE, ZaMsg.SERTBB_FlushCache, ZaMsg.SERTBB_FlushCache_tt, "FlushCache", "FlushCache", new AjxListener(this, ZaServerListController.prototype._flushCacheButtonListener));	
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
	
	this._toolbarOrder.push(ZaOperation.EDIT);
	this._toolbarOrder.push(ZaOperation.FLUSH_CACHE);
	this._toolbarOrder.push(ZaOperation.NONE);	
	this._toolbarOrder.push(ZaOperation.HELP);					
}
ZaController.initToolbarMethods["ZaServerListController"].push(ZaServerListController.initToolbarMethod);

ZaServerListController.initPopupMenuMethod =
function () {
   	this._popupOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.SERTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaServerListController.prototype._editButtonListener));
	this._popupOperations[ZaOperation.FLUSH_CACHE] = new ZaOperation(ZaOperation.FLUSH_CACHE, ZaMsg.SERTBB_FlushCache, ZaMsg.SERTBB_FlushCache_tt, "FlushCache", "FlushCache", new AjxListener(this, ZaServerListController.prototype._flushCacheButtonListener));   	    	    	    	
}
ZaController.initPopupMenuMethods["ZaServerListController"].push(ZaServerListController.initPopupMenuMethod);

ZaServerListController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaServerListView(this._container);
		this._initToolbar();
		this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder, null, null, ZaId.VIEW_SERLIST);

		this._initPopupMenu();
		this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations, ZaId.VIEW_SERLIST, ZaId.MENU_POP);
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		//ZaApp.getInstance().createView(ZaZimbraAdmin._SERVERS_LIST_VIEW, elements);
        if (!appNewUI) {
            elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
		    var tabParams = {
			    openInNewTab: false,
			    tabId: this.getContentViewId(),
			    tab: this.getMainTab()
		    }
		    ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
        } else {
            ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
        }
		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));								
			
		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	} catch (ex) {
		this._handleException(ex, "ZaServerListController.prototype._createUI", null, false);
		return;
	}	
}

ZaServerListController.prototype._flushCacheButtonListener = 
function(ev) {
	try {
		if(this._contentView.getSelectionCount()>0) {
			var arrItems = this._contentView.getSelection();
			if(arrItems && arrItems.length) {
				if(!ZaApp.getInstance().dialogs["flushCacheDialog"]) {
					ZaApp.getInstance().dialogs["flushCacheDialog"] = new ZaFlushCacheXDialog(this._container);
				}
				srvList = [];
				srvList._version = 1;
				for(var i=0;i<arrItems.length;i++) {
					var srv = arrItems[i];
					srv["status"] = 0;
					srvList.push(srv);
				}
				obj = {statusMessage:null,flushZimlet:true,flushSkin:true,flushLocale:true,serverList:srvList,status:0};
				ZaApp.getInstance().dialogs["flushCacheDialog"].setObject(obj);
				ZaApp.getInstance().dialogs["flushCacheDialog"].popup();
			}
		}	
	} catch (ex) {
		this._handleException(ex, "ZaServerListController.prototype._flushCacheButtonListener", null, false);
	}
	return;
}

ZaServerListController.prototype.set = 
function(serverList) {
	this.show(serverList);
}

/**
* @param ev
* This listener is invoked by  any controller that can change an ZaServer object
**/
ZaServerListController.prototype.handleServerChange = 
function (ev) {
	//if any of the data that is currently visible has changed - update the view
	if(ev) {
		var details = ev.getDetails();
		//if(details["modFields"] && (details["modFields"][ZaServer.A_description] )) {
		if (details) {
			if (this._list) this._list.replace (details);
			if (this._contentView) this._contentView.setUI();
			if(ZaApp.getInstance().getCurrentController() == this) {
				this.show();			
			}
			this.changeActionsState();
		}
	}
}


// new button was pressed
ZaServerListController.prototype._newButtonListener =
function(ev) {
	var newServer = new ZaServer();
	ZaApp.getInstance().getServerController().show(newServer);
}

/**
* This listener is called when the item in the list is double clicked. It call ZaServerController.show method
* in order to display the Server View
**/
ZaServerListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._selectedItem = ev.item;
			ZaApp.getInstance().getServerController().show(ev.item);
            if (appNewUI) {
                var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, ZaMsg.OVP_servers]);
                ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, ev.item.name, null, false, false, ev.item);
            }
		}
	} else {
		this.changeActionsState();	
	}
}

ZaServerListController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}
/**
* This listener is called when the Edit button is clicked. 
* It call ZaServerController.show method
* in order to display the Server View
**/
ZaServerListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		ZaApp.getInstance().getServerController().show(item);
        if (appNewUI) {
            var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, ZaMsg.OVP_servers]);
            ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, item.name, null, false, false, item);
        }
	}
}

ZaServerListController.prototype.getPopUpOperation =
function () {
    return this._popupOperations;
}

ZaServerListController.changeActionsStateMethod = 
function () {
	if(this._contentView) {
		var cnt = this._contentView.getSelectionCount();
		var enableFlush = true;
		var servers = this._contentView.getSelection();
		if(servers) {
			var cnt = servers.length;
			for(var i=0; i<cnt; i++) {
				if(!ZaItem.hasRight(ZaServer.FLUSH_CACHE_RIGHT,servers[i]) || !servers[i].attrs[ZaServer.A_zimbraMailboxServiceEnabled] || !servers[i].attrs[ZaServer.A_zimbraMailboxServiceInstalled]) {
					enableFlush = false;
					break;
				} 
					
			}
		}
		if(cnt == 1) {
			if(!enableFlush) {
				if(this._toolbarOperations[ZaOperation.FLUSH_CACHE])	
					this._toolbarOperations[ZaOperation.FLUSH_CACHE].enabled = false;
					
				if(this._popupOperations[ZaOperation.FLUSH_CACHE])	
					this._popupOperations[ZaOperation.FLUSH_CACHE].enabled = false;
			}
		} else if (cnt > 1){
			if(this._toolbarOperations[ZaOperation.EDIT])	
				this._toolbarOperations[ZaOperation.EDIT].enabled = false;
				
			if(this._popupOperations[ZaOperation.EDIT])	
				this._popupOperations[ZaOperation.EDIT].enabled = false;
				
			if(!enableFlush) {
				if(this._toolbarOperations[ZaOperation.FLUSH_CACHE])	
					this._toolbarOperations[ZaOperation.FLUSH_CACHE].enabled = false;
					
				if(this._popupOperations[ZaOperation.FLUSH_CACHE])	
					this._popupOperations[ZaOperation.FLUSH_CACHE].enabled = false;
			}
		} else if (cnt <1) {
			if(this._toolbarOperations[ZaOperation.EDIT])	
				this._toolbarOperations[ZaOperation.EDIT].enabled = false;
				
			if(this._popupOperations[ZaOperation.EDIT])	
				this._popupOperations[ZaOperation.EDIT].enabled = false;

			if(this._toolbarOperations[ZaOperation.FLUSH_CACHE])	
				this._toolbarOperations[ZaOperation.FLUSH_CACHE].enabled = false;
				
			if(this._popupOperations[ZaOperation.FLUSH_CACHE])	
				this._popupOperations[ZaOperation.FLUSH_CACHE].enabled = false;
				
		}
	}
}
ZaController.changeActionsStateMethods["ZaServerListController"].push(ZaServerListController.changeActionsStateMethod);
