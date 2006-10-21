/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
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
function ZaServerStatsController(appCtxt, container, app) {

	ZaController.call(this, appCtxt, container, app);
	this._helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/monitoring/checking_usage_statistics.htm";
}

ZaServerStatsController.prototype = new ZaController();
ZaServerStatsController.prototype.constructor = ZaServerStatsController;

//ZaServerStatsController.STATUS_VIEW = "ZaServerStatsController.STATUS_VIEW";

ZaServerStatsController.prototype.show = 
function(item) {
    if (!this._contentView) {
		this._contentView = new ZaServerStatsView(this._container, this._app);
		var elements = new Object();
		this._ops = new Array();
		this._ops.push(new ZaOperation(ZaOperation.NONE));
		
		this._ops.push(new ZaOperation(ZaOperation.PAGE_BACK, ZaMsg.Previous, ZaMsg.PrevPage_tt, 
									"LeftArrow", "LeftArrowDis",  
									new AjxListener(this, ZaServerStatsController.prototype._prevPageListener)));
		
		this._ops.push(new ZaOperation(ZaOperation.SEP));								
		this._ops.push(new ZaOperation(ZaOperation.LABEL, AjxMessageFormat.format (ZaMsg.MBXStats_PAGEINFO, [1,1]),
														 null, null, null, null,null,null,null,"mbxPageInfo"));	
		this._ops.push(new ZaOperation(ZaOperation.SEP));							
		
		this._ops.push(new ZaOperation(ZaOperation.PAGE_FORWARD, ZaMsg.Next, ZaMsg.NextPage_tt,
									"RightArrow", "RightArrowDis", 
									new AjxListener(this, ZaServerStatsController.prototype._nextPageListener)));
		
		this._ops.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));				
		this._toolbar = new ZaToolBar(this._container, this._ops);    		
		
		//disable the page_forward and page_back at the beginning
		this._toolbar.enable([ZaOperation.PAGE_FORWARD, ZaOperation.PAGE_BACK, ZaOperation.LABEL], false);
		
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;	
		this._app.createView(ZaZimbraAdmin._STATISTICS_BY_SERVER, elements);
	}
	this._app.pushView(ZaZimbraAdmin._STATISTICS_BY_SERVER);
//	this._app.setCurrentController(this);
	this._contentView.setObject(item);
}

ZaServerStatsController.prototype._prevPageListener = 
function (ev) {
	var mbxPage = this._contentView._mbxPage ;
	var xform = mbxPage._view ;
	var curInst = xform.getInstance();
	
	mbxPage.updateMbxLists(curInst, null, curInst.offset - ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT, null, null );	
};

ZaServerStatsController.prototype._nextPageListener = 
function (ev) {
	var mbxPage = this._contentView._mbxPage ;
	var xform = mbxPage._view ;
	var curInst = xform.getInstance();
	
	mbxPage.updateMbxLists(curInst, null, curInst.offset + ZaServerMBXStatsPage.MBX_DISPLAY_LIMIT, null, null );
}; 

