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
* @class ZaServerStatsController 
* @contructor ZaServerStatsController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
ZaServerStatsController = function(appCtxt, container) {
   	this._toolbarOperations = new Array();
   	this._toolbarOrder = new Array();
      	
	ZaController.call(this, appCtxt, container,"ZaServerStatsController");
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "monitoring/checking_usage_statistics.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this._helpButtonText = ZaServerStatsController.helpButtonText;
	this.tabConstructor = ZaServerStatsView;
}

ZaServerStatsController.prototype = new ZaController();
ZaServerStatsController.prototype.constructor = ZaServerStatsController;
ZaServerStatsController.helpButtonText = ZaMsg.helpCheckStatistics

ZaController.setViewMethods["ZaServerStatsController"] = [];
//ZaServerStatsController.STATUS_VIEW = "ZaServerStatsController.STATUS_VIEW";

ZaServerStatsController.prototype.show = 
function(entry, openInNewTab, skipRefresh) {
	if (! this.selectExistingTabByItemId(entry.id)){	
		this._setView(entry, openInNewTab, skipRefresh);
	}
}

ZaServerStatsController.setViewMethod =
function(item,openInNewTab) {	
    if (!this._contentView) {
		this._view = this._contentView = new this.tabConstructor(this._container);
		var elements = new Object();


		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		if (!appNewUI) {
			this._toolbarOperations[ZaOperation.REFRESH] = new ZaOperation(ZaOperation.REFRESH, ZaMsg.TBB_Refresh, ZaMsg.TBB_Refresh_tt, "Refresh", "Refresh", new AjxListener(this, this.refreshListener));
			this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
			this._toolbarOperations[ZaOperation.PAGE_BACK] = new ZaOperation(ZaOperation.PAGE_BACK, ZaMsg.Previous, ZaMsg.PrevPage_tt, 
										"LeftArrow", "LeftArrowDis",  
										new AjxListener(this, ZaServerStatsController.prototype._prevPageListener));

			this._toolbarOperations[ZaOperation.SEP]=new ZaOperation(ZaOperation.SEP);
			this._toolbarOperations[ZaOperation.LABEL] = new ZaOperation(ZaOperation.LABEL, AjxMessageFormat.format (ZaMsg.MBXStats_PAGEINFO, [1,1]),
															 null, null, null, null,null,null, "ZaSearchResultCountLabel", "PageInfo");
					
			this._toolbarOperations[ZaOperation.PAGE_FORWARD] = new ZaOperation(ZaOperation.PAGE_FORWARD, ZaMsg.Next, ZaMsg.NextPage_tt,
										"RightArrow", "RightArrowDis", 
										new AjxListener(this, ZaServerStatsController.prototype._nextPageListener));

			this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));				

			this._toolbarOrder.push(ZaOperation.REFRESH);
			this._toolbarOrder.push(ZaOperation.NONE);
			this._toolbarOrder.push(ZaOperation.PAGE_BACK);
			this._toolbarOrder.push(ZaOperation.SEP);
			this._toolbarOrder.push(ZaOperation.LABEL);
			this._toolbarOrder.push(ZaOperation.SEP);
			this._toolbarOrder.push(ZaOperation.PAGE_FORWARD);
			this._toolbarOrder.push(ZaOperation.HELP);
			this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder, null, null, ZaId.VIEW_STATIS);

			//disable the page_forward and page_back at the beginning
			this._toolbar.enable([ZaOperation.PAGE_FORWARD, ZaOperation.PAGE_BACK, ZaOperation.LABEL], false);
			elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;

			var tabParams = {
				openInNewTab: openInNewTab,
				tabId: this.getContentViewId()
			};
			if(!openInNewTab) {
				tabParams.tab = this.getMainTab();
			}
			ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
		}
		else{
			ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
		}
		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	}
//	ZaApp.getInstance().pushView(ZaZimbraAdmin._STATISTICS_BY_SERVER);
	ZaApp.getInstance().pushView(this.getContentViewId());
//	ZaApp.getInstance().setCurrentController(this);

	this._contentView.setObject(item);
	//show the view in the new tab

	//var tab = new ZaAppTab (ZaApp.getInstance().getTabGroup(),  
	//			item.name, "StatisticsByServer" , null, null, true, true, ZaApp.getInstance()._currentViewId) ;
	//tab.setToolTipContent(ZaMsg.tt_tab_View + " " + item.type + " " + item.name + " " + ZaMsg.tt_tab_Statistics) ;

}
ZaController.setViewMethods["ZaServerStatsController"].push(ZaServerStatsController.setViewMethod);

ZaServerStatsController.prototype._prevPageListener = 
function (ev) {
	var currentView = this.getCurrentStatsView() ;
	var mbxPage = this._contentView._mbxPage ;
	var sessPage = this._contentView._sessionPage ;
	if (currentView == mbxPage) {
		var xform = mbxPage._view ;
		var curInst = xform.getInstance();
		mbxPage.updateMbxLists(curInst, null, curInst.offset - ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT, null, null );
	}	
	
	if (currentView == sessPage) {
		sessPage._pageListener(true);
	}
};

ZaServerStatsController.prototype._nextPageListener = 
function (ev) {
	var currentView = this.getCurrentStatsView() ;
	var mbxPage = this._contentView._mbxPage ;
	var sessPage = this._contentView._sessionPage ;
	if (currentView == mbxPage) {
		var xform = mbxPage._view ;
		var curInst = xform.getInstance();
		mbxPage.updateMbxLists(curInst, null, curInst.offset + ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT, null, null );
	}
	
	if (currentView == sessPage) {
		sessPage._pageListener();
	}
}; 

ZaServerStatsController.prototype.getCurrentStatsView =
function () {
	return this._contentView._tabs[this._contentView._currentTabKey].view ;
}

ZaServerStatsController.prototype.refreshListener =
function (ev) {
	var currentTabView = this._contentView._tabs[this._contentView._currentTabKey]["view"];
	if (currentTabView && currentTabView.showMe) {
		currentTabView.showMe(2) ; //force server side cache to be refreshed.
	}
}



//////////////////////////////////////////////////////////////////////////////////////////

/**
* @constructor
* @class ZaServerStatsListController
* This is a singleton object that controls all the user interaction with the list of ZaServer objects
* @author wgan
**/
ZaServerStatsListController = function(appCtxt, container) {
    ZaListViewController.call(this, appCtxt, container, "ZaServerStatsListController");
    this._popupOperations = new Array();

    this._helpURL = location.pathname + ZaUtil.HELP_URL + "monitoring/checking_usage_statistics.htm?locid="+AjxEnv.DEFAULT_LOCALE;
    this._helpButtonText = ZaServerStatsListController.helpButtonText;
}

ZaServerStatsListController.prototype = new ZaListViewController();
ZaServerStatsListController.prototype.constructor = ZaServerStatsListController;
ZaServerStatsListController.helpButtonText = ZaMsg.helpCheckStatistics

ZaController.initPopupMenuMethods["ZaServerStatsListController"] = new Array();
ZaController.changeActionsStateMethods["ZaServerStatsListController"] = new Array();

/**
* @param list {ZaItemList} a list of ZaServer {@link ZaServer} objects
**/
ZaServerStatsListController.prototype.show =
function(list) {
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


ZaServerStatsListController.initPopupMenuMethod =
function () {
        this._popupOperations[ZaOperation.VIEW] = new ZaOperation(ZaOperation.VIEW,ZaMsg.TBB_View, ZaMsg.PQTBB_View_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaServerStatsListController.prototype._viewButtonListener));
}
ZaController.initPopupMenuMethods["ZaServerStatsListController"].push(ZaServerStatsListController.initPopupMenuMethod);

ZaServerStatsListController.prototype._createUI = function () {
    try {
        var elements = new Object();
        this._contentView = new ZaServerListView(this._container);

        this._initPopupMenu();
        this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations, ZaId.VIEW_SERLIST, ZaId.MENU_POP);
        elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;

        ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);

        this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
        this._contentView.addActionListener(new AjxListener(this, this._listActionListener));

        this._UICreated = true;
        ZaApp.getInstance()._controllers[this.getContentViewId ()] = this;
    } catch (ex) {
        this._handleException(ex, "ZaServerStatsListController.prototype._createUI", null, false);
    }
}

ZaServerStatsListController.prototype.set =
function(serverList) {
    this.show(serverList);
}

/**
* @param ev
* This listener is invoked by  any controller that can change an ZaServer object
**/
ZaServerStatsListController.prototype.handleServerChange =
function (ev) {
    //if any of the data that is currently visible has changed - update the view
    if(ev) {
        var details = ev.getDetails();
        //if(details["modFields"] && (details["modFields"][ZaServer.A_description] )) {
        if (details) {
            if (this._list) {
                this._list.replace (details);
            }
            if (this._contentView) {
                this._contentView.setUI();
            }
            if(ZaApp.getInstance().getCurrentController() == this) {
                this.show();
            }
            this.changeActionsState();
        }
    }
}


/**
* This listener is called when the item in the list is double clicked. It call ZaServerController.show method
* in order to display the Server View
**/
ZaServerStatsListController.mappingId2handlerMap = null;
ZaServerStatsListController._getMapForMappingId2handler =
function (){
    if (!ZaServerStatsListController.mappingId2handlerMap){
        ZaServerStatsListController.mappingId2handlerMap = {
            "mainNode": { "mappingId": ZaZimbraAdmin._STATISTICS_BY_SERVER,
                          "handler": ZaOverviewPanelController.statsByServerTreeListener
                        },
            "tabNodes": { "mappingId": ZaZimbraAdmin._SERVER_STATISTICS_TAB_VIEW,
                          "handler": ZaOverviewPanelController.statsByServerTabTreeListener
                        }
        }
    } //only be initialized once

    return ZaServerStatsListController.mappingId2handlerMap;
}

ZaServerStatsListController.parentPathInTree = null;
ZaServerStatsListController._getparentPathInTree =
function (){
    if (!ZaServerStatsListController.parentPathInTree){
        ZaServerStatsListController.parentPathInTree =
            ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_monitor, ZaMsg.OVP_statistics]);
    } //only be initialized once
    return ZaServerStatsListController.parentPathInTree;
}

ZaServerStatsListController.prototype._listSelectionListener =
function(ev) {
    if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
        var item = ev.item;
        this._switchToSubItem(item);
    } else {
        this.changeActionsState();
    }
}

ZaServerStatsListController.prototype._listActionListener =
function (ev) {
    this.changeActionsState();
    this._actionMenu.popup(0, ev.docX, ev.docY);
}

ZaServerStatsListController.prototype._viewButtonListener =
function(ev) {
    if(this._contentView.getSelectionCount() == 1) {
        var item = this._contentView.getSelection()[0];
        this._switchToSubItem(item);
    }
}

ZaServerStatsListController.prototype._switchToSubItem = function (item)
{
    if (item) {
        this._selectedItem = item;
        ZaApp.getInstance().getServerStatsController().show(item);

        if (appNewUI) {
            //must switch to the individual server stats view firstly,
            //then the ZaApp.getInstance().getAppViewMgr().getCurrentView() == server stats view,
            //let the  addObjectItemForAll get the right view id
            var overviewPanelController = ZaZimbraAdmin.getInstance().getOverviewPanelController();
            var parentPath = ZaServerStatsListController._getparentPathInTree();
            var map = ZaServerStatsListController._getMapForMappingId2handler();

            overviewPanelController.addObjectItem(parentPath, item.name, null,
                                                        false, false, item, map);
        }
    }
}

ZaServerStatsListController.changeActionsStateMethod =
function () {
    if(this._contentView) {
    }
}
ZaController.changeActionsStateMethods["ZaServerStatsListController"].push(ZaServerStatsListController.changeActionsStateMethod);
