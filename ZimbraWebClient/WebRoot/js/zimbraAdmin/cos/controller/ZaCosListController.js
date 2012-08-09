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

ZaCosListController = function(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container,"ZaCosListController");
	this.objType = ZaEvent.S_COS;	
	this._currentSortField = ZaCos.A_name;
	this._currentPageNum = 1;
	this._currentSortOrder = "1";
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "cos/class_of_service.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this._helpButtonText = ZaMsg.helpManageCOS;
	this._currentQuery = "";
	this.fetchAttrs = [ZaCos.A_name,ZaCos.A_description].join();
	this.RESULTSPERPAGE = ZaDomain.RESULTSPERPAGE; 
	this.MAXSEARCHRESULTS = ZaDomain.MAXSEARCHRESULTS;	
}

ZaCosListController.prototype = new ZaListViewController();
ZaCosListController.prototype.constructor = ZaCosListController;
ZaController.initToolbarMethods["ZaCosListController"] = new Array();
ZaController.initPopupMenuMethods["ZaCosListController"] = new Array();
ZaController.changeActionsStateMethods["ZaCosListController"] = new Array(); 

//ZaCosListController.COS_VIEW = "ZaCosListController.COS_VIEW";

ZaCosListController.prototype.show = function (doPush,openInNewTab) {

    if(!ZaZimbraAdmin.hasGlobalCOSSListAccess() && this._currentQuery == "") {
        var cosNameList = ZaApp.getInstance()._cosNameList;
        if(!cosNameList || !(cosNameList instanceof Array) || cosNameList.length == 0) {
            this._list = new ZaItemList(ZaCos);
            this.numPages = 0;
            this._searchTotal = 0;
            if(doPush) this._show(this._list);
            else this._updateUI(this._list);
            return;
        }
        for(var i = 0; i < cosNameList.length; i++)
            this._currentQuery += "(" + ZaCos.A_name + "=" + cosNameList[i] + ")";
        if(cosNameList.length > 1)
            this._currentQuery = "(|" + this._currentQuery + ")";
    }

	var busyId = Dwt.getNextId () ;
	openInNewTab = openInNewTab ? openInNewTab : false;
	var callback = new AjxCallback(this, this.searchCallback, {openInNewTab:openInNewTab,limit:this.RESULTSPERPAGE,CONS:null,show:doPush,busyId:busyId});
	
	var searchParams = {
			query:this._currentQuery ,
			types:[ZaSearch.COSES],
			sortBy:this._currentSortField,
			offset:this.RESULTSPERPAGE*(this._currentPageNum-1),
			sortAscending:this._currentSortOrder,
			limit:this.RESULTSPERPAGE,
			callback:callback,
			attrs:this.fetchAttrs,
			controller: this,
			showBusy:true,
			busyId:busyId,
			busyMsg:ZaMsg.BUSY_SEARCHING_COSES,
			skipCallbackIfCancelled:false			
	}
    this.scrollSearchParams={
        query:this._currentQuery ,
			types:[ZaSearch.COSES],
			sortBy:this._currentSortField,
			sortAscending:this._currentSortOrder,
			attrs:this.fetchAttrs,
			controller: this,
			showBusy:true,
			busyMsg:ZaMsg.BUSY_SEARCHING_COSES,
			skipCallbackIfCancelled:false
    };
	ZaSearch.searchDirectory(searchParams);
}

ZaCosListController.prototype._show = 
function (list, openInNewTab, openInSearchTab, hasMore) {
	this._updateUI(list, openInNewTab, openInSearchTab, hasMore);
	ZaApp.getInstance().pushView(this.getContentViewId (), openInNewTab, openInSearchTab);
    if (appNewUI)
        return;
	if (openInSearchTab) {
		ZaApp.getInstance().updateSearchTab();
	} else if(openInNewTab) {
		var cTab = ZaApp.getInstance().getTabGroup().getTabById( this.getContentViewId());
		ZaApp.getInstance().updateTab(cTab, ZaApp.getInstance()._currentViewId );
	} else {
		ZaApp.getInstance().updateTab(this.getMainTab(), ZaApp.getInstance()._currentViewId );
	} 
}


ZaCosListController.initPopupMenuMethod =
function () {
	if(ZaItem.hasRight(ZaCos.CREATE_COS_RIGHT, ZaZimbraAdmin.currentAdminAccount)) {
   		this._popupOperations[ZaOperation.NEW]=new ZaOperation(ZaOperation.NEW,ZaMsg.TBB_New, ZaMsg.COSTBB_New_tt, "NewCOS", "NewCOSDis", new AjxListener(this, ZaCosListController.prototype._newButtonListener));
   		this._popupOperations[ZaOperation.DUPLICATE]=new ZaOperation(ZaOperation.DUPLICATE,ZaMsg.TBB_Duplicate, ZaMsg.COSTBB_Duplicate_tt, "DuplicateCOS", "DuplicateCOSDis", new AjxListener(this, ZaCosListController.prototype._duplicateButtonListener));
	}
   	this._popupOperations[ZaOperation.EDIT]=new ZaOperation(ZaOperation.EDIT,ZaMsg.TBB_Edit, ZaMsg.COSTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaCosListController.prototype._editButtonListener));    	    	
	this._popupOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Delete, ZaMsg.COSTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaCosListController.prototype._deleteButtonListener));   		
}
ZaController.initPopupMenuMethods["ZaCosListController"].push(ZaCosListController.initPopupMenuMethod);

/**
* This method is called from {@link ZaController#_initToolbar}
**/
ZaCosListController.initToolbarMethod =
function () {
		
}
ZaController.initToolbarMethods["ZaCosListController"].push(ZaCosListController.initToolbarMethod);

//private and protected methods
ZaCosListController.prototype._createUI = 
function (openInNewTab, openInSearchTab) {
	this._contentView = new ZaCosListView(this._container);
	ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	// create the menu operations/listeners first	
    //this._initToolbar();
	//always add Help and navigation buttons at the end of the toolbar    
	//this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);	
	//this._toolbarOperations[ZaOperation.PAGE_BACK]=new ZaOperation(ZaOperation.PAGE_BACK,ZaMsg.Previous, ZaMsg.PrevPage_tt, "LeftArrow", "LeftArrowDis",  new AjxListener(this, this._prevPageListener));
	
	//add the acount number counts
	
	//this._toolbarOperations[ZaOperation.PAGE_FORWARD]=new ZaOperation(ZaOperation.PAGE_FORWARD,ZaMsg.Next, ZaMsg.NextPage_tt, "RightArrow", "RightArrowDis", new AjxListener(this, this._nextPageListener));
	//this._toolbarOperations[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));				

	//this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder, null, null, ZaId.VIEW_COSLIST);    
		
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
    ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);

	this._initPopupMenu();
	this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations, ZaId.VIEW_COSLIST, ZaId.MENU_POP);
	
	//set a selection listener on the account list view
	this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
	this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
	this._removeConfirmMessageDialog = ZaApp.getInstance().dialogs["removeConfirmMessageDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], null, ZaId.CTR_PREFIX + ZaId.VIEW_COSLIST + "_removeConfirm");			
		
	this._UICreated = true;
}


// refresh button was pressed
ZaCosListController.prototype._refreshButtonListener =
function(ev) {
	this.refresh();
}


// duplicate button was pressed
ZaCosListController.prototype._duplicateButtonListener =
function(ev) {
	if(this._contentView && (this._contentView.getSelectionCount() == 1)) {
		var newCos = new ZaCos(); //new COS
		var item = this._contentView.getSelection()[0];
		if(item) { //copy the attributes from the selected COS to the new COS
            //need to get the cos first since rights, getAttrs and setAttrs are not in the cos list object
            if (item.id) {
                item.load ("id", item.id) ;
            }

            if ( item.attrs ) {
                for(var aname in item.attrs) {
                    if( (aname == ZaItem.A_objectClass) || (aname == ZaItem.A_zimbraId) || (aname == ZaCos.A_name) || (aname == ZaCos.A_description) || (aname == ZaCos.A_zimbraNotes) || (aname == ZaItem.A_zimbraCreateTimestamp) )
                        continue;

                    if ( (typeof item.attrs[aname] == "object") || (item.attrs[aname] instanceof Array)) {
                        newCos.attrs[aname] = AjxUtil.createProxy(item.attrs[aname],3);
                        /*for(var a in item.attrs[aname]) {
                            newCos.attrs[aname][a]=item.attrs[aname][a];
                        }*/
                    } else {
                        newCos.attrs[aname] = item.attrs[aname];
                    }
                }
            }


            newCos.attrs[ZaCos.A_zimbraNotes] = AjxMessageFormat.format(ZaMsg.COSTBB_DuplicatingFrom_tt, [item.attrs[ZaCos.A_name]]);
            //explicitly note the user this is duplicated from the one they select,
            //instead of showing nothing at the first page of ZaNewCosXWizard

            if (item.getAttrs)   {
                newCos.getAttrs = item.getAttrs ;
            }

            if (item.setAttrs) {
                newCos.setAttrs = item.setAttrs ;
            }

            if (item.rights) {
                newCos.rights = item.rights ;
            }
        }

        ZaCosListController.showMe.call(this, newCos);
	}
}

// new button was pressed
ZaCosListController.prototype._newButtonListener =
function(ev) {
	var newCos = new ZaCos();
	//load default COS
	var defCos = ZaCos.getCosByName("default");
	newCos.loadNewObjectDefaults();
	newCos.rights[ZaCos.RENAME_COS_RIGHT]=true;
	newCos.rights[ZaCos.CREATE_COS_RIGHT]=true;
	//copy values from default cos to the new cos
	for(var aname in defCos.attrs) {
		if( (aname == ZaItem.A_objectClass) || (aname == ZaItem.A_zimbraId) || (aname == ZaCos.A_name) || (aname == ZaCos.A_description) || (aname == ZaCos.A_notes) || (aname == ZaItem.A_zimbraCreateTimestamp))
			continue;			
		newCos.attrs[aname] = defCos.attrs[aname];
	}

    ZaCosListController.showMe.call(this, newCos);
}

/**
* This listener is called when the item in the list is double clicked. It call ZaCosController.show method
* in order to display the Cos View
**/
ZaCosListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			ZaApp.getInstance().getCosController().show(ev.item);
            if (appNewUI) {
                var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, ZaMsg.OVP_cos]);
                ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, ev.item.name, null, false, false, ev.item, undefined, true);
            }
		}
	} else {
		this.changeActionsState();	
	}
}


ZaCosListController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

/**
* This listener is called when the Edit button is clicked. 
* It call ZaCosListController.show method
* in order to display the COS View
**/
ZaCosListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
        this._editItem(item);
	}
}

ZaCosListController.prototype._editItem =
function(item) {
    ZaApp.getInstance().getCosController().show(item);
    if (appNewUI) {
        var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, ZaMsg.OVP_cos]);
        ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, item.name, null, false, false, item);
    }
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaCosListController.prototype._deleteButtonListener =
function(ev) {
	this._removeList = new Array();
	this._itemsInTabList = [] ;
	if(this._contentView.getSelectionCount() > 0) {
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		for(var key =0; key < cnt; key++) {
		
		//	var item = DwtListView.prototype.getItemFromElement.call(this, arrDivs[key]);
			var item = arrItems[key];
			if (item) {
				//detect whether the deleting item is open in a tab
				if (ZaApp.getInstance().getTabGroup() && ZaApp.getInstance().getTabGroup().getTabByItemId (item.id)) {
					this._itemsInTabList.push (item) ;
				}else{
					this._removeList.push(item);
				}
			}
		}
	}
	
	if (this._itemsInTabList.length > 0) {
		if(!ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"]) {
			ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"] = 
				new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.CANCEL_BUTTON], 
						[ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON_DESC , ZaMsgDialog.NO_DELETE_BUTTON_DESC],
						ZaId.CTR_PREFIX + ZaId.VIEW_COSLIST + "_ConfirmDeleteItemsInTab");			
		}
		
		
		var msg = ZaMsg.dl_warning_delete_accounts_in_tab ; ;
		msg += ZaCosListController.getDlMsgFromList (this._itemsInTabList) ;
		
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].setMessage(msg, DwtMessageDialog.WARNING_STYLE);	
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON, ZaCosListController.prototype._closeTabsBeforeRemove, this);
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.NO_DELETE_BUTTON, ZaCosListController.prototype._deleteCosInRemoveList, this);		
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].popup();
		
	}else{
		this._deleteCosInRemoveList ();
	}
}

ZaCosListController.prototype._closeTabsBeforeRemove =
function () {
	//DBG.println (AjxDebug.DBG1, "Close the tabs before Remove ...");
	this.closeTabsInRemoveList() ;
	/*
	var tabGroup = ZaApp.getInstance().getTabGroup();
	for (var i=0; i< this._itemsInTabList.length ; i ++) {
		var item = this._itemsInTabList[i];
		tabGroup.removeTab (tabGroup.getTabByItemId(item.id)) ;
		this._removeList.push(item);
	}*/
	//ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].popdown();
	this._deleteCosInRemoveList();
}

ZaCosListController.prototype._deleteCosInRemoveList =
function () {
	if (ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"]) {
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].popdown();
	}
	if(this._removeList.length) {
		var dlgMsg = ZaMsg.Q_DELETE_COSES;
		dlgMsg += ZaCosListController.getDlMsgFromList (this._removeList) ;
		this._removeConfirmMessageDialog.setMessage(dlgMsg, DwtMessageDialog.INFO_STYLE);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaCosListController.prototype._deleteCosCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaCosListController.prototype._donotDeleteCosCallback, this);		
		this._removeConfirmMessageDialog.popup();
	}
	
} 

ZaCosListController.getDlMsgFromList =
function (listArr) {
	dlgMsg =  "<br><ul>";
	var i=0;
	for(var key in listArr) {
		if(i > 19) {
			dlgMsg += "<li>...</li>";
			break;
		}
		dlgMsg += "<li>";
		if(listArr[key].name.length > 50) {
			//split it
			var endIx = 49;
			var beginIx = 0; //
			while(endIx < listArr[key].name.length) { //
				dlgMsg +=  listArr[key].name.slice(beginIx, endIx); //
				beginIx = endIx + 1; //
				if(beginIx >= (listArr[key].name.length) ) //
					break;
				
				endIx = ( listArr[key].name.length <= (endIx + 50) ) ? listArr[key].name.length-1 : (endIx + 50);
				dlgMsg +=  "<br>";	
			}
		} else {
			dlgMsg += listArr[key].name;
		}
		dlgMsg += "</li>";
		i++;
	}
	dlgMsg += "</ul>";
	
	return dlgMsg ;
}


ZaCosListController.prototype._deleteCosCallback = 
function () {
	var successRemList=new Array();
	for(var key in this._removeList) {
		if(this._removeList[key]) {
			try {
				this._removeList[key].remove();
				successRemList.push(this._removeList[key]);				
			} catch (ex) {
				this._removeConfirmMessageDialog.popdown();
				this._handleException(ex, ZaCosListController.prototype._deleteCosCallback, null, false);
				return;
			}
		}
		if (this._list) this._list.remove(this._removeList[key]); //remove from the list
	}
	this.fireRemovalEvent(successRemList); 	
	this._removeConfirmMessageDialog.popdown();
	if (this._contentView) this._contentView.setUI();
	this.show();
}

ZaCosListController.prototype._donotDeleteCosCallback = 
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}

ZaCosListController.changeActionsStateMethod = 
function (enableArray,disableArray) {
	if(!this._contentView)
		return;
	
	var cnt = this._contentView.getSelectionCount();
	var hasDefault = false;
	if(cnt >= 1) {
		var arrDivs = this._contentView.getSelectedItems().getArray();
		for(var key in arrDivs) {
			var item = this._contentView.getItemFromElement(arrDivs[key]);
			if(item) {
				if(item.name == "default") {
					hasDefault = true;
					break;
				}		
			}
		}
	}
	if(cnt == 1) {
		var item = this._contentView.getSelection()[0];
		if(item) {
			if(hasDefault) {
				if(this._popupOperations[ZaOperation.DELETE]) {
					this._popupOperations[ZaOperation.DELETE].enabled=false;
				}
			} else {
				if (AjxUtil.isEmpty(item.rights)) {
					item.loadEffectiveRights("id", item.id, false);
				}
				if(!ZaItem.hasRight(ZaCos.DELETE_COS_RIGHT, item)) {
					if(this._popupOperations[ZaOperation.DELETE]) {
						this._popupOperations[ZaOperation.DELETE].enabled=false;
					}
				}
			}
		}
	} else if (cnt > 1){
		if(hasDefault) {
			if(this._popupOperations[ZaOperation.DELETE]) {
				this._popupOperations[ZaOperation.DELETE].enabled=false;
			}					
		}
		
		if(this._popupOperations[ZaOperation.DUPLICATE] && this._popupOperations[ZaOperation.DUPLICATE].enabled) {
			this._popupOperations[ZaOperation.DUPLICATE].enabled=false;
		}		
		if(this._popupOperations[ZaOperation.EDIT]) {
			this._popupOperations[ZaOperation.EDIT].enabled=false;
		}					
	} else {
		if(this._popupOperations[ZaOperation.EDIT]) {
			this._popupOperations[ZaOperation.EDIT].enabled=false;
		}	
		if(this._popupOperations[ZaOperation.DELETE]) {
			this._popupOperations[ZaOperation.DELETE].enabled=false;
		}	
		if(this._popupOperations[ZaOperation.DUPLICATE] && this._popupOperations[ZaOperation.DUPLICATE].enabled) {
			this._popupOperations[ZaOperation.DUPLICATE].enabled=false;
		}		
	}
}
ZaController.changeActionsStateMethods["ZaCosListController"].push(ZaCosListController.changeActionsStateMethod);

ZaCosListController.showMe = function(newCos)
{
    if(!appNewUI)
        ZaApp.getInstance().getCosController().show(newCos);
    else{
        if(!ZaApp.getInstance().dialogs["newCosXWizard"]){
            ZaApp.getInstance().dialogs["newCosXWizard"] = new ZaNewCosXWizard(this._container,newCos);
        }
        ZaApp.getInstance().dialogs["newCosXWizard"].setObject(newCos);
        ZaApp.getInstance().dialogs["newCosXWizard"].popup();
    }
}
