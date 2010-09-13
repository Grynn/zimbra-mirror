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
	this.tabConstructor = ZaServerStatsView;
}

ZaServerStatsController.prototype = new ZaController();
ZaServerStatsController.prototype.constructor = ZaServerStatsController;
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
		
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;	

		var tabParams = {
			openInNewTab: openInNewTab,
			tabId: this.getContentViewId()
		};
		if(!openInNewTab) {
			tabParams.tab = this.getMainTab();
		}
		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	}
//	ZaApp.getInstance().pushView(ZaZimbraAdmin._STATISTICS_BY_SERVER);
	ZaApp.getInstance().pushView(this.getContentViewId());
//	ZaApp.getInstance().setCurrentController(this);

	this._contentView.setObject(item);

	
	//show the view in the new tab
	/*
	var tab = new ZaAppTab (ZaApp.getInstance().getTabGroup(),  
				item.name, "StatisticsByServer" , null, null, true, true, ZaApp.getInstance()._currentViewId) ;
	tab.setToolTipContent(ZaMsg.tt_tab_View + " " + item.type + " " + item.name + " " + ZaMsg.tt_tab_Statistics) ;
	*/	
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
