/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaZimletListController
* This is a singleton object that controls all the user interaction with the list of ZaZimlet objects
* @author Greg Solovyev
**/
ZaZimletListController = function(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container, "ZaZimletListController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
	this.objType = ZaEvent.S_ZIMLET;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "zimlets/about_zimlets.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this._helpButtonText = ZaZimletListController.helpButtonText;
}

ZaZimletListController.prototype = new ZaListViewController();
ZaZimletListController.prototype.constructor = ZaZimletListController;
ZaZimletListController.helpButtonText = ZaMsg.helpManageZimlets;

ZaController.initPopupMenuMethods["ZaZimletListController"] = new Array();
ZaController.changeActionsStateMethods["ZaZimletListController"] = new Array(); 
/**
* @param list {ZaItemList} a list of ZaZimlet {@link ZaZimlet} objects
**/
ZaZimletListController.prototype.show = 
function(list, openInNewTab) {
    	if (!this._UICreated) {
		this._createUI();
	}
 	
	if (list != null && list instanceof ZaItemList) {
		this._list = list;
	} else {
		this._list = ZaZimlet.getAll(ZaZimlet.EXCLUDE_EXTENSIONS, new AjxCallback(this, this.show));
		return;				
	}

	this._contentView.set(this._list.getVector());	
	ZaApp.getInstance().pushView(this.getContentViewId());
	this._removeList = new Array();
		
	this.changeActionsState();		
	/*
	if (openInNewTab) {//when a ctrl shortcut is pressed
		
	}else{ //open in the main tab
		this.updateMainTab ("Zimlet") ;	
	}*/
}

ZaZimletListController.initPopupMenuMethod =
function () {
    this._popupOperations[ZaOperation.DEPLOY_ZIMLET]=new ZaOperation(ZaOperation.DEPLOY_ZIMLET,ZaMsg.TBB_DeployNew, ZaMsg.TBB_DeployNew_tt, "Deploy", "Deploy", new AjxListener(this, this.deployZimletListener));
   	this._popupOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Undeploy, ZaMsg.DTBB_Undeploy_tt, "Undeploy", "Undeploy", new AjxListener(this, this._undeployButtonListener));    	    		
   	this._popupOperations[ZaOperation.TOGGLE]=new ZaOperation(ZaOperation.TOGGLE,ZaMsg.TBB_Toggle, ZaMsg.TBB_Toggle_tt, "ToggleStatus", "ToggleStatus", new AjxListener(this, this._toggleButtonListener));
   	this._popupOrder.push(ZaOperation.DEPLOY_ZIMLET);
   	this._popupOrder.push(ZaOperation.DELETE);
   	this._popupOrder.push(ZaOperation.TOGGLE);

   
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
		//if(!this._deployZimletWizard)
			this._deployZimletWizard = new ZaZimletDeployXWizard(this._container);		
	
		var zimlet = new ZaZimlet();
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
		this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder, null, null, ZaId.VIEW_ZIMLIST);

		this._initPopupMenu();
		this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations, ZaId.VIEW_ZIMLIST, ZaId.MENU_POP);

		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		//ZaApp.getInstance().createView(ZaZimbraAdmin._ZIMLET_LIST_VIEW, elements);
		//ZaApp.getInstance().createView(ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW, elements);
        if(!appNewUI) {
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
		this._removeConfirmMessageDialog = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], null, ZaId.CTR_PREFIX + ZaId.VIEW_ZIMLIST + "_removeConfirm");					
		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
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
		if(ev.item) {
			this._selectedItem = ev.item;
			ZaApp.getInstance().getZimletController().show(ev.item);

            if (appNewUI) {
                var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, ZaMsg.OVP_zimlets]);
                ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, ev.item.name, null, false, false, ev.item);
            }
		}
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
		ZaApp.getInstance().getZimletController().show(item);
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

ZaZimletListController.changeActionsStateMethod = 
function () {
	var cnt = this._contentView.getSelectionCount();
	var offArray = [];
	var onArray = [];
	if(cnt < 1) {
			
		if(this._popupOperations[ZaOperation.EDIT])	
			this._popupOperations[ZaOperation.EDIT].enabled = false;
					
		if(this._popupOperations[ZaOperation.DELETE])	
			this._popupOperations[ZaOperation.DELETE].enabled = false;

		if(this._popupOperations[ZaOperation.TOGGLE])	
			this._popupOperations[ZaOperation.TOGGLE].enabled = false;			
	} 
}
ZaController.changeActionsStateMethods["ZaZimletListController"].push(ZaZimletListController.changeActionsStateMethod);
