/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* Display the Mailbox disk usage statistics per serer.
* 1) Top N diskspace consumer
* 2) Top N quota consumer
* 
* @class ZaServerMBXStatsPage
* @contructor ZaServerMBXStatsPage
* @param parent
* @param app
* @author Charles Cao
**/
		
function ZaServerMBXStatsPage (parent, app) {
	DwtTabViewPage.call(this, parent);
	//this._fieldIds = new Object(); //stores the ids of all the form elements
	//this._app = app;
	//this.initialized=false; //? Why need this ?
	this._rendered = false;
	this._startOffset = 0;
}

ZaServerMBXStatsPage.prototype = new DwtTabViewPage;
ZaServerMBXStatsPage.prototype.constructor = ZaServerMBXStatsPage;

//operations
ZaServerMBXStatsPage.QUOTASTATS = 1;
ZaServerMBXStatsPage.DISKSTATS = 2;

ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT = 25;
ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT = "account";
ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE = "diskUsage";
ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE = "quotaUsage";
ZaServerMBXStatsPage.XFORM_ITEM_DISKMBX = "diskMbx";
ZaServerMBXStatsPage.XFORM_ITEM_QUOTAMBX = "quotaMbx";

ZaServerMBXStatsPage.prototype.toString = function() {
	return "ZaServerMBXStatsPage";
};

ZaServerMBXStatsPage.prototype.setObject = function (item) {
	this._server = item;
	this._render(item);
};

ZaServerMBXStatsPage.prototype._render = function (server) {
	if (!this._rendered) {
		//xform instance/object
		var instance = {currentTab: ZaServerMBXStatsPage.QUOTASTATS, //used for the intial display
						diskMbx: 	this.getMbxes(ZaServerMBXStatsPage.DISKSTATS),
						quotaMbx: 	this.getMbxes(ZaServerMBXStatsPage.QUOTASTATS)
						};				
	
	    this._view = new XForm(this._getXForm(), null, instance, this);
		this._view.setController(this); //and the later getFormController will refer to this obj
		this._view.draw();
		this._rendered = true;
	} else {
		var cTab = this._view.getInstance().currentTab;
				
		if (cTab == ZaServerMBXStatsPage.DISKSTATS){
			var mbxes = this._view.getItemsById(ZaServerMBXStatsPage.XFORM_ITEM_DISKMBX);
		}else { //by default display the quotaMbx
			var mbxes = this._view.getItemsById(ZaServerMBXStatsPage.XFORM_ITEM_QUOTAMBX);
		}
			
		for (var i = 0 ; i < mbxes.length; ++i ){
			mbxes[i].dirtyDisplay(); 
		}
		this._view.refresh();
	}
};

//data instance of the xform
ZaServerMBXStatsPage.prototype.getMbxes = function (tabId){

	var serverName = this._server.name ;
	var targetServer = this._server.id ;
	//var app = this._app ;
	
	var soapDoc = AjxSoapDoc.create("GetQuotaUsageRequest", "urn:zimbraAdmin", null);
	
	if (tabId == ZaServerMBXStatsPage.DISKSTATS) {
		soapDoc.getMethod().setAttribute("sortBy", "totalUsed");
	}else if (tabId == ZaServerMBXStatsPage.QUOTASTATS ){
		soapDoc.getMethod().setAttribute("sortBy", "percentUsed");
	}
	
	soapDoc.getMethod().setAttribute("offset", this._startOffset);
	soapDoc.getMethod().setAttribute("limit", ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT);
	
	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, targetServer, true).firstChild; 
	
	var nodes = resp.childNodes ;
	var accountArr = new Array ();
	var respArr = new Array ();
	var quotaLimit = 0;
	var percentage = 0 ;
	var diskUsed = 0;
	for (var i=0; i<nodes.length; i ++){
		respArr [i] = {  	name: nodes[i].getAttribute("name") ,
							used: nodes[i].getAttribute("used") ,
							limit: nodes[i].getAttribute("limit") 
						    };	
		
		diskUsed = ( respArr[i].used / 1048576 ).toPrecision(3) ;
		
		if (respArr[i].limit == 0 ){
			quotaLimit = "unlimited" ;
			percentage = 0 ;	
		}else{
			quotaLimit = ( respArr[i].limit / 1048576 ).toPrecision(3) ;
			percentage = ((diskUsed * 100) / quotaLimit).toPrecision(2) ;
		}
						    
		accountArr [i] = { 	account : respArr[i].name,
							diskUsage :  AjxMessageFormat.format (ZaMsg.MBXStats_DISK_MSB, [diskUsed]),
							quotaUsage : AjxMessageFormat.format (ZaMsg.MBXStats_QUOTA_MSG, [diskUsed, quotaLimit, percentage] )
							};
	}
	
	return accountArr;	
};

ZaServerMBXStatsPage.prototype._getXForm = function () {
	if (this._xform != null) return this._xform;
	var accountWidth = "300px";
	var diskUsageWidth = "100px";
	var quotaUsageWidth = "200px" ;
	var headerStyle = "font-size: 120%; font-weight: bold;";
	var topN = "25";
	var startN = this._startOffset ;
	if (startN == 0){
		topN = "25";
	}else{
		topN =  ( startN + 1 ) + " - " + (startN + ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT );
	}
	this._xform = {
		x_showBorder:1,
	    numCols:1, 
	    cssClass:"ZaServerMBXStatsPage", 
		tableCssStyle:"width:100%",
	    itemDefaults:{ },
	    items:[
		   {type:_SPACER_, height:10, colSpan:"*" },
		
		   {type:_TAB_BAR_,  ref:ZaModel.currentTab, colSpan:"*",
		    choices:[
			     {value:1, label:AjxMessageFormat.format (ZaMsg.TABT_StatsMBXQuota, [topN])},
			     {value:2, label:AjxMessageFormat.format (ZaMsg.TABT_StatsMBXDisk, [topN])}
			    ],
		    cssClass:"ZaTabBar"
		   },

		   {type:_SWITCH_, align:_LEFT_, valign:_TOP_, 
		    items:[
			   {type:_CASE_,  relevant:"instance[ZaModel.currentTab] == 1", align:_LEFT_, valign:_TOP_, 
			    items:[
			    //table header
			    	{type:_GROUP_, numCols:3, 
							items: [
								{ type:_OUTPUT_, label:null, value: ZaMsg.MBXStats_ACCOUNT, cssStyle: headerStyle, width: accountWidth},
								{ type:_OUTPUT_, label:null, value: ZaMsg.MBXStats_DISKUSAGE, cssStyle: headerStyle, width: diskUsageWidth},
								{ type:_OUTPUT_, label:null, value: ZaMsg.MBXStats_QUOTAUSAGE, cssStyle: headerStyle, width: quotaUsageWidth}
							]
					},
				   {type:_SPACER_, height:15, colSpan:"*" },
				   
				   {ref: ZaServerMBXStatsPage.XFORM_ITEM_QUOTAMBX, type:_REPEAT_ , showAddButton:false, showRemoveButton:false,
				   			items: [
				   				{ ref:ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT, type:_OUTPUT_, width: accountWidth },
				   				{ ref:ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE, type:_OUTPUT_, width: diskUsageWidth },
				   				{ ref:ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE, type:_OUTPUT_, width: quotaUsageWidth }	
				   			]
				   } 
				 ]
			   },
			   {type:_CASE_,  relevant:"instance[ZaModel.currentTab] == 2", align:_LEFT_, valign:_TOP_, 
			    items:[
			    //table header
			    	{type:_GROUP_, numCols:3,
							items: [
								{ type:_OUTPUT_, label:null, value: ZaMsg.MBXStats_ACCOUNT, cssStyle: headerStyle, width: accountWidth},
								{ type:_OUTPUT_, label:null, value: ZaMsg.MBXStats_DISKUSAGE, cssStyle: headerStyle, width: diskUsageWidth},
								{ type:_OUTPUT_, label:null, value: ZaMsg.MBXStats_QUOTAUSAGE, cssStyle: headerStyle, width: quotaUsageWidth}
							]
					},
				   {type:_SPACER_, height:15, colSpan:"*" },
				   {ref:ZaServerMBXStatsPage.XFORM_ITEM_DISKMBX, type:_REPEAT_ , showAddButton:false, showRemoveButton:false,
				   			items: [
				   				{ ref: ZaServerMBXStatsPage.XFORM_ITEM_ACCOUNT, type:_OUTPUT_, width: accountWidth },
				   				{ ref: ZaServerMBXStatsPage.XFORM_ITEM_DISKUSAGE, type:_OUTPUT_, width: diskUsageWidth },
				   				{ ref: ZaServerMBXStatsPage.XFORM_ITEM_QUOTAUSAGE, type:_OUTPUT_, width: quotaUsageWidth }	
				   			]
				   } 
				 ]
			   }			   
			   ]
		   }
		   ]
	};		   

	return this._xform;
};

/*
ZaServerMBXStatsPage.prototype._getLocalizedTabString = function (tabId){
	switch (tabId) {
	case 1:
		return ZaMsg.TABT_StatsMBXQuota;
	case 2:
		return ZaMsg.TABT_StatsMBXDisk;
	}
	return null;
};

*/

///////////////////////////////////////////////////////////////////////////////////////////
//Controller: enable the buttons on the toolbar
///////////////////////////////////////////////////////////////////////////////////////////
/*
function ZaServerMBXStatsController(appCtxt, container, app) {
	ZaServerStatsController.call(this, appCtxt, container, app);
}

ZaServerMBXStatsController.prototype = new ZaServerStatsController();
ZaServerMBXStatsController.prototype.constructor = ZaServerMBXStatsController;

//ZaServerStatsController.STATUS_VIEW = "ZaServerStatsController.STATUS_VIEW";

ZaServerMBXStatsController.prototype.show = 
function(item) {
    if (!this._contentView) {
		this._contentView = new ZaServerStatsView(this._container);
		var elements = new Object(); 
		this._ops = new Array();
		this._ops.push(new ZaOperation(ZaOperation.NONE));
		this._ops.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));				
		this._toolbar = new ZaToolBar(this._container, this._ops);    		
		
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;	
		this._app.createView(ZaZimbraAdmin._STATISTICS_BY_SERVER, elements);
	}
	this._app.pushView(ZaZimbraAdmin._STATISTICS_BY_SERVER);
//	this._app.setCurrentController(this);
	this._contentView.setObject(item);
}


ZaServerMBXStatsController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
}
*/