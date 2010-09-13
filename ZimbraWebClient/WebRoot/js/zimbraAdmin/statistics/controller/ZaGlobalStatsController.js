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
* @class ZaGlobalStatsController 
* @contructor ZaGlobalStatsController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
ZaGlobalStatsController = function(appCtxt, container) {
   	this._toolbarOperations = new Array();
   	this._toolbarOrder = new Array();
	ZaController.call(this, appCtxt, container, "ZaGlobalStatsController");
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "monitoring/checking_usage_statistics.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this.tabConstructor = ZaGlobalStatsView;		
}

ZaGlobalStatsController.prototype = new ZaController();
ZaGlobalStatsController.prototype.constructor = ZaGlobalStatsController;

/**
 * This array contains function references. The functions referenced in this array will be called by ZaController.prototype._setView method
 * @see ZaController#_setView
 */
ZaController.setViewMethods["ZaGlobalStatsController"] = [];

/**
 * 'show' method iof every controller s responsible for two actions:
 *  - calling _setView method
 *  - instantiating data object and feeding the data to the view controlled by this controller
 * see also: {@link ZaController#_setView}, {@link ZaApp#pushView} 
 * 
 */
ZaGlobalStatsController.prototype.show = 
function() {
	/**
	 * this call will create the view object {@link ZaGlobalStatsView } @see ZaController#_setView
	 */
	this._setView();
	/**
	 * This call will push the view on top of the visible stack of views
	 */
	ZaApp.getInstance().pushView(this.getContentViewId());
	
	/**
	 * this statement creates a data object. In most of other cases, we will use a special model class such as ZaServer, ZaDomain, etc
	 * however, in this case, the data object does not have any special features to be encapsulated
	 **/
	var item=new Object();
	try {		
		
		item[ZaModel.currentTab] = "1"
		/**
		 * This statement feeds the data to the view @see ZaGlobalStatsView#setObject
		 */
		this._contentView.setObject(item);
	} catch (ex) {
		this._handleException(ex, "ZaGlobalConfigViewController.prototype.show", null, false);
	}
	this._currentObject = item;	
}

/**
 * We do not directly overwrite ZaController.prototype._setView method of ZaController class,
 * instead we add function references to ZaController.setViewMethods map 
 * @see ZaController#setViewMethods
 * @see ZaController#_setView
 */
ZaGlobalStatsController.setViewMethod =
function() {	
    if (!this._contentView) {
    	/**
    	 * This call instantiates ZaGlobalStatsView
    	 */
		this._contentView  = new this.tabConstructor(this._container);
		
		/**
		 * This object tells ZaAppViewMgr which components to put on the screen for this view. 
		 * Usualy, these are: toolbar and the view contents.
		 */
		var elements = new Object();

		/**
		 * Appearance of the toolbar is controlled by two maps:
		 *  - this._toolbarOperations is a map of ZaOperation instances
		 *  - this._toolbarOrder is an array that controls the order of the buttons in the toolbar
		 */
		this._toolbarOperations[ZaOperation.REFRESH] = new ZaOperation(ZaOperation.REFRESH, ZaMsg.TBB_Refresh, ZaMsg.TBB_Refresh_tt, "Refresh", "Refresh", new AjxListener(this, this.refreshListener));
		this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
		this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));				
		
		
		this._toolbarOrder.push(ZaOperation.REFRESH);
		this._toolbarOrder.push(ZaOperation.NONE);
		this._toolbarOrder.push(ZaOperation.HELP);
			
		this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder, null, null, ZaId.VIEW_STATISLIST);    		
		
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab()
		}
		
		/**
		 * This statement will tell the view manager to make the view visible
		 */
		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
		
		/**
		 * We need this in order to be able to get a handle of this controler instance
		 */
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;		
	}
}
/**
 * This statement adds ZaGlobalStatsController.setViewMethod method to the map of methods that will be called by ZaController.prototype._setView 
 * whenever this._setView();
 */
ZaController.setViewMethods["ZaGlobalStatsController"].push(ZaGlobalStatsController.setViewMethod);


ZaGlobalStatsController.prototype.refreshListener =
function (ev) {
	var currentTabView = this._contentView._tabs[this._contentView._currentTabKey]["view"];
	if (currentTabView && currentTabView.showMe) {
		currentTabView.showMe(2) ; //force server side cache to be refreshed.
	}
}
