/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
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

/**
* @constructor
* @class ZaZimletListController
* This is a singleton object that controls all the user interaction with the list of ZaZimlet objects
* @author Greg Solovyev
**/
ZaZimletListController = function(appCtxt, container, app) {
	ZaListViewController.call(this, appCtxt, container, app,"ZaZimletListController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
	this.objType = ZaEvent.S_ZIMLET;
	this._helpURL = location.pathname + "adminhelp/html/WebHelp/zimlets/about_zimlets.htm";					
}

ZaZimletListController.prototype = new ZaListViewController();
ZaZimletListController.prototype.constructor = ZaZimletListController;

ZaController.initToolbarMethods["ZaZimletListController"] = new Array();
ZaController.initPopupMenuMethods["ZaZimletListController"] = new Array();

/**
* @param list {ZaItemList} a list of ZaZimlet {@link ZaZimlet} objects
**/
ZaZimletListController.prototype.show = 
function(list, openInNewTab) {
    if (!this._UICreated) {
		this._createUI();
	} 	
	if (list != null && list instanceof ZaItemList) {
		this._contentView.set(list.getVector());
	
		this._list = list;
	} else {
		this._list = ZaZimlet.getAll(this._app,ZaZimlet.EXCLUDE_EXTENSIONS);			
		this._contentView.set(this._list.getVector());
		
	}	
	this._app.pushView(this.getContentViewId());
	this._removeList = new Array();
		
	this.changeActionsState();		
	/*
	if (openInNewTab) {//when a ctrl shortcut is pressed
		
	}else{ //open in the main tab
		this.updateMainTab ("Zimlet") ;	
	}*/
}

ZaZimletListController.initToolbarMethod =
function () {
	this._toolbarOperations.push(new ZaOperation(ZaOperation.DEPLOY_ZIMLET, ZaMsg.TBB_DeployNew, ZaMsg.TBB_DeployNew_tt, "Deploy", "Deploy", new AjxListener(this, this.deployZimletListener)));				
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Undeploy, ZaMsg.DTBB_Undeploy_tt, "Undeploy", "Undeploy", new AjxListener(this, this._undeployButtonListener)));    	    		
	this._toolbarOperations.push(new ZaOperation(ZaOperation.TOGGLE, ZaMsg.TBB_Toggle, ZaMsg.TBB_Toggle_tt, "ToggleStatus", "ToggleStatus", new AjxListener(this, this._toggleButtonListener)));    	    		
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));				
}
ZaController.initToolbarMethods["ZaZimletListController"].push(ZaZimletListController.initToolbarMethod);

ZaZimletListController.initPopupMenuMethod =
function () {
   	this._popupOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Undeploy, ZaMsg.DTBB_Undeploy_tt, "Undeploy", "Undeploy", new AjxListener(this, this._undeployButtonListener)));    	    		
   	this._popupOperations.push(new ZaOperation(ZaOperation.TOGGLE, ZaMsg.TBB_Toggle, ZaMsg.TBB_Toggle_tt, "ToggleStatus", "ToggleStatus", new AjxListener(this, this._toggleButtonListener)));    	    		
   
}
ZaController.initPopupMenuMethods["ZaZimletListController"].push(ZaZimletListController.initPopupMenuMethod);

ZaZimletListController.prototype._toggleButtonListener = 
function (ev) {
	try {
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		for (var i=0; i<cnt; i++) {
			if(arrItems[i] ) {
				arrItems[i].enable( ! arrItems[i].isEnabled(), new AjxCallback(this, this.fireChangeEvent,arrItems[i] ));
			}
		}
	} catch (ex) {
		this._handleException(ex, "ZaZimletListController.prototype._toggleZimletListener", null, false);
	}
	
	return;
}

/*
ZaZimletListController.prototype.disableZimletListener = 
function (ev) {
	try {
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		for (var i=0; i<cnt; i++) {
			if(arrItems[i]) {
				arrItems[i].enable(false,new AjxCallback(this, this.fireChangeEvent,arrItems[i] ));
			}
		}
	} catch (ex) {
		this._handleException(ex, "ZaZimletListController.prototype.disableZimletListener", null, false);
	}
	return;
}

ZaZimletListController.prototype.enableZimletListener = 
function (ev) {
	try {
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		for (var i=0; i<cnt; i++) {
			if(arrItems[i]) {
				arrItems[i].enable(true, new AjxCallback(this, this.fireChangeEvent,arrItems[i] ));
			}
		}
	} catch (ex) {
		this._handleException(ex, "ZaZimletListController.prototype.enableZimletListener", null, false);
	}
	return;
}*/

ZaZimletListController.prototype.deployZimletListener = 
function (ev) {
	try {
		if(!this._deployZimletWizard)
			this._deployZimletWizard = new ZaZimletDeployXWizard(this._container, this._app);		
	
		var zimlet = new ZaZimlet(this._app);
		this._deployZimletWizard.setObject(zimlet);		
		this._deployZimletWizard.popup();
	} catch (ex) {
		this._handleException(ex, "ZaZimletListController.prototype.deployZimletListener", null, false);
	}
	return;
}

ZaZimletListController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaZimletListView(this._container);
		this._initToolbar();
		if(this._toolbarOperations && this._toolbarOperations.length) {
			this._toolbar = new ZaToolBar(this._container, this._toolbarOperations); 
			elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
		}
		this._initPopupMenu();
		if(this._popupOperations && this._popupOperations.length) {
			this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
		}
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		//this._app.createView(ZaZimbraAdmin._ZIMLET_LIST_VIEW, elements);
		//this._app.createView(ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW, elements);
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab() 
		}
		this._app.createView(this.getContentViewId(), elements, tabParams) ;
		
		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
		this._removeConfirmMessageDialog = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);					
		this._UICreated = true;
		this._app._controllers[this.getContentViewId ()] = this ;
	} catch (ex) {
		this._handleException(ex, "ZaZimletListController.prototype._createUI", null, false);
		return;
	}	
}

ZaZimletListController.prototype.set = 
function(zimletList) {
	this.show(zimletList);
}


/**
* This listener is called when the item in the list is double clicked. It call ZaZimletController.show method
* in order to display the Zimlet View
**/
ZaZimletListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
	/*	if(ev.item) {
			this._selectedItem = ev.item;
			this._app.getZimletController().show(ev.item);
		}*/
	} else {
		this.changeActionsState();	
	}
}

ZaZimletListController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}
/**
* This listener is called when the Edit button is clicked. 
* It call ZaZimletController.show method
* in order to display the Zimlet View
**/
ZaZimletListController.prototype._editButtonListener =
function(ev) {
/*	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		this._app.getZimletController().show(item);
	}*/
}

/**
* This listener is called when the Undeploy button is clicked. 
**/
ZaZimletListController.prototype._undeployButtonListener =
function(ev) {
	this._removeList = new Array();
	if(this._contentView.getSelectionCount()>0) {
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		for(var key =0; key < cnt; key++) {
			if(arrItems[key]) {
				this._removeList.push(arrItems[key]);
			}
		}
	}
	if(this._removeList.length) {
		var dlgMsg;
		dlgMsg = ZaMsg.Q_UNDEPLOY_ZIMLETS;			

		dlgMsg += "<br>";
		for(var key in this._removeList) {
			if(i > 19) {
				dlgMsg += "<li>...</li>";
				break;
			}
			dlgMsg += "<li>";
			if(this._removeList[key].name.length > 50) {
				//split it
				var endIx = 49;
				var beginIx = 0; //
				while(endIx < this._removeList[key].name.length) { //
					dlgMsg +=  this._removeList[key].name.slice(beginIx, endIx); //
					beginIx = endIx + 1; //
					if(beginIx >= (this._removeList[key].name.length) ) //
						break;
					
					endIx = ( this._removeList[key].name.length <= (endIx + 50) ) ? this._removeList[key].name.length-1 : (endIx + 50);
					dlgMsg +=  "<br>";	
				}
			} else {
				dlgMsg += this._removeList[key].name;
			}
			dlgMsg += "</li>";
			i++;
		}
		dlgMsg += "</ul>";
		this._removeConfirmMessageDialog.setMessage(dlgMsg, DwtMessageDialog.INFO_STYLE);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaZimletListController.prototype._undeployZimletsCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaZimletListController.prototype._donotUndeployZimletsCallback, this);		
		this._removeConfirmMessageDialog.popup();
	}
}

ZaZimletListController.prototype._undeployZimletsCallback = 
function () {
	var successRemList=new Array();
	for(var key in this._removeList) {
		if(this._removeList[key]) {
			try {
				this._removeList[key].remove();
				successRemList.push(this._removeList[key]);					
			} catch (ex) {
				this._removeConfirmMessageDialog.popdown();
				this._handleException(ex, "ZaZimletListController.prototype._undeployZimletsCallback", null, false);				
				return;
			}
		}
		this._list.remove(this._removeList[key]); //remove from the list
	}
	this.fireRemovalEvent(successRemList); 		
	this._removeConfirmMessageDialog.popdown();
	this._contentView.setUI();
	this.show();
}

ZaZimletListController.prototype._donotUndeployZimletsCallback = 
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}

ZaZimletListController.prototype.changeActionsState = 
function () {
	var cnt = this._contentView.getSelectionCount();
	var offArray = [];
	var onArray = [];
	if(cnt >= 1) {
		onArray = [ZaOperation.EDIT];
		
		var arrItems = this._contentView.getSelection();
		onArray.push(ZaOperation.DELETE);	
		onArray.push(ZaOperation.TOGGLE);	
		/*	
		if(arrItems[0].attrs[ZaZimlet.A_zimbraZimletEnabled] == "FALSE") {
			onArray.push(ZaOperation.ENABLE_ZIMLET);
			offArray.push(ZaOperation.DISABLE_ZIMLET);	
		} else if (arrItems[0].attrs[ZaZimlet.A_zimbraZimletEnabled] == "TRUE") {
			onArray.push(ZaOperation.DISABLE_ZIMLET);
			offArray.push(ZaOperation.ENABLE_ZIMLET);			
		}*/		

	} 
	/*
	else if (cnt > 1){
		offArray = [ZaOperation.EDIT]; 
			
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		var gotEnabled = false;
		var gotDisabled = false;
		for(var i=0;i<cnt;i++) {
			if(arrItems[i].attrs[ZaZimlet.A_zimbraZimletEnabled] == "FALSE")	
				gotDisabled = true;
			else if(arrItems[i].attrs[ZaZimlet.A_zimbraZimletEnabled] == "TRUE")	
				gotEnabled = true;
			
			if(gotEnabled && gotDisabled) {
				//nothing else to look for
				break;
			}
			
		}
		onArray.push(ZaOperation.DELETE);
		onArray.push(ZaOperation.TOGGLE);
		if(gotEnabled && gotDisabled) {
			offArray.push(ZaOperation.ENABLE_ZIMLET);
			offArray.push(ZaOperation.DISABLE_ZIMLET);						
		} else if (gotEnabled) {
			onArray.push(ZaOperation.DISABLE_ZIMLET);									
			offArray.push(ZaOperation.ENABLE_ZIMLET);			
		} else if (gotDisabled) {
			onArray.push(ZaOperation.ENABLE_ZIMLET);									
			offArray.push(ZaOperation.DISABLE_ZIMLET);			
		}					
	}*/
    else {
		offArray = [ZaOperation.EDIT,ZaOperation.DISABLE_ZIMLET,ZaOperation.DELETE,ZaOperation.TOGGLE, ZaOperation.ENABLE_ZIMLET];
	}
	if(onArray.length) {
		this._toolbar.enable(onArray, true);
		this._actionMenu.enable(onArray, true);
	}
	if(offArray.length) {
		this._toolbar.enable(offArray, false);
		this._actionMenu.enable(offArray, false);	
	}
}