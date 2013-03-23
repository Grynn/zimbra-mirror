/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012 VMware, Inc.
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
* @class ZaMTAListController
**/
ZaMTAListController = function(appCtxt, container) {
	ZaController.call(this, appCtxt, container, "ZaMTAListController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
	this.MTAPool = [];
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "monitoring/monitoring_zimbra_mta_mail_queues.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this._helpButtonText = ZaMsg.helpManageMailQueue;
}

ZaMTAListController.prototype = new ZaController();
ZaMTAListController.prototype.constructor = ZaMTAListController;

ZaController.initToolbarMethods["ZaMTAListController"] = new Array();
ZaController.initPopupMenuMethods["ZaMTAListController"] = new Array();
ZaController.changeActionsStateMethods["ZaMTAListController"] = new Array(); 

ZaMTAListController.prototype.show = 
function(list, openInNewTab) {

    if (!this._UICreated) {
		this._createUI();
	} 	

	if (list != null) {
		this._contentView.set(list.getVector());
		//start loading queue info
		this.MTAPool = [];
		var tmpList = list.getArray();
		var numMTAs = tmpList.length;

		for(var ix = 0; ix < numMTAs; ix++) {
			this.MTAPool.push(tmpList[ix]);
		}
		
		var i=0;
		var tmp = [];
		var cnt = numMTAs > 5 ? 5 : numMTAs;
		for(i = 0; i < cnt; i++) {
			tmp[i] = this.MTAPool.shift();
		}
		ZaMTA._quecountsArr = new Array();
		for(i = cnt-1; i >= 0; i--) {
			tmp[i].load("id",tmp[i].id,false,true);
		}
	}	
	//ZaApp.getInstance().pushView(ZaZimbraAdmin._POSTQ_VIEW);			
	ZaApp.getInstance().pushView(this.getContentViewId());
	this._removeList = new Array();
	if (list != null)
		this._list = list;
		
	this.changeActionsState();		
	/*
	if (openInNewTab) {//when a ctrl shortcut is pressed
		
	}else{ //open in the main tab
		this.updateMainTab ("Queue") ;	
	}*/
}

ZaMTAListController.initPopupMenuMethod =
function () {
    this._popupOperations[ZaOperation.VIEW]=new ZaOperation(ZaOperation.VIEW,ZaMsg.TBB_View, ZaMsg.PQTBB_View_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaMTAListController.prototype._viewButtonListener));
}
ZaController.initPopupMenuMethods["ZaMTAListController"].push(ZaMTAListController.initPopupMenuMethod);

ZaMTAListController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaMTAListView(this._container);

		this._initPopupMenu();
		this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations, ZaId.VIEW_MTALIST, ZaId.MENU_POP);

		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
        ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
		this._removeConfirmMessageDialog = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON],null,ZaId.CTR_PREFIX + ZaId.VIEW_MTALIST + "_removeConfirm");					
		
		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	} catch (ex) {
		this._handleException(ex, "ZaMTAListController.prototype._createUI", null, false);
		return;
	}	
}

/**
* @return ZaItemList - the list currently displaid in the list view
**/
ZaMTAListController.prototype.getList = 
function() {
	return this._list;
}

/*
ZaMTAListController.prototype.refresh = 
function() {
	try {
		this._contentView.set(ZaApp.getInstance().getServerList(true).getVector());
	} catch (ex) {
		this._handleException(ex, ZaMTAListController.prototype.refresh, null, false);
	}
}
*/

ZaMTAListController.prototype.set = 
function(serverList) {
	this.show(serverList);
}

/**
* This listener is called when the item in the list is double clicked. It call ZaMTAController.show method
* in order to display the MailQ View
**/
ZaMTAListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._selectedItem = ev.item;
			ZaApp.getInstance().getMTAController().show(ev.item);
		}
	} else {
		this.changeActionsState();	
	}
}

ZaMTAListController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}
/**
* This listener is called when the Edit button is clicked. 
* It call ZaMTAController.show method
* in order to display the MailQ View
**/
ZaMTAListController.prototype._viewButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		ZaApp.getInstance().getMTAController().show(item);
	}
}


ZaMTAListController.changeActionsState = 
function () {
	var cnt = this._contentView.getSelectionCount();
	if (cnt != 1){
		if(this._popupOperations[ZaOperation.EDIT])
			this._popupOperations[ZaOperation.EDIT].enabled=false;			
	}
}
ZaController.changeActionsStateMethods["ZaMTAListController"].push(ZaMTAListController.changeActionsStateMethod);
/**
* @param ev
* This listener is invoked by ZaMTAController or any other controller that can change a ZaMTA object
**/
ZaMTAListController.prototype.handleMTAChange = 
function (ev) {
	//if any of the data that is currently visible has changed - update the view
	if(ev && this._contentView) {
		if(ev.getDetail("obj")) {
			this._contentView.setUI();
			//check if we have any MTAs in the queue waiting to be loaded
			if(this.MTAPool.length) {
				var mta = this.MTAPool.shift();
				mta.load();
			}
		}
	}
}

/**
* Asynchronously calls ZaItem.load {@link ZaItem#load}
**/
ZaMTAListController.prototype.getQCounts = function () {
	this.MTAPool = [];
	var tmpList = this._list.getArray();
	var numMTAs = tmpList.length;
	for(var ix = 0; ix < numMTAs; ix++) {
		this.MTAPool.push(tmpList[ix]);
	}
	var cnt = numMTAs > 5 ? 5 : numMTAs;
	var i=0;
	var tmp = [];
	for(i = 0; i < cnt; i++) {
		tmp[i] = this.MTAPool.shift();
	}
	ZaMTA._quecountsArr = new Array();
	for(i = cnt-1; i >= 0; i--) {
		tmp[i].load();
	}
}

ZaMTAListController.prototype.refreshListener = 
function () {
	this.getQCounts();
}
