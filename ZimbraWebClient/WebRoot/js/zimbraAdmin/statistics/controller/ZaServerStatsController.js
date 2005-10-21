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
 * The Original Code is: Zimbra Collaboration Suite.
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
* @class ZaServerStatsController 
* @contructor ZaServerStatsController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
function ZaServerStatsController(appCtxt, container, app) {

	ZaController.call(this, appCtxt, container, app);
	this._helpURL = "/zimbraAdmin/adminhelp/html/OpenSourceAdminHelp/managing_servers/checking_usage_statistics.htm";
}

ZaServerStatsController.prototype = new ZaController();
ZaServerStatsController.prototype.constructor = ZaServerStatsController;

//ZaServerStatsController.STATUS_VIEW = "ZaServerStatsController.STATUS_VIEW";

ZaServerStatsController.prototype.show = 
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

/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaServerStatsController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
}