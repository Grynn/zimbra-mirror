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
* @class ZaMTAController controls display of a single Server's Postfix Queue
* @contructor ZaMTAController
* @param appCtxt
* @param container
* @param abApp
* @author Greg Solovyev
**/

function ZaMTAController(appCtxt, container,app) {
	ZaXFormViewController.call(this, appCtxt, container,app,"ZaMTAController");
	this._UICreated = false;
	this._helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/managing_servers/managing_servers.htm";				
	this._toolbarOperations = new Array();
	this.objType = ZaEvent.S_MTA;	
}

ZaMTAController.prototype = new ZaXFormViewController();
ZaMTAController.prototype.constructor = ZaMTAController;

ZaController.initToolbarMethods["ZaMTAController"] = new Array();
ZaController.setViewMethods["ZaMTAController"] = new Array();
/**
*	@method show
*	@param entry - isntance of ZaServer class
*/
ZaMTAController.prototype.show = 
function(entry) {
	this._setView(entry);
	this.setDirty(false);
}

/**
*	@method setViewMethod 
*	@param entry - isntance of ZaDomain class
*/
ZaMTAController.setViewMethod =
function(entry) {
	entry.load();
	if(!this._UICreated) {
		this._createUI();
	} 
	this._app.pushView(ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW);
	this._view.setDirty(false);
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
}
ZaController.setViewMethods["ZaMTAController"].push(ZaMTAController.setViewMethod);

/**
* @method initToolbarMethod
* This method creates ZaOperation objects 
* All the ZaOperation objects are added to this._toolbarOperations array which is then used to 
* create the toolbar for this view.
* Each ZaOperation object defines one toolbar button.
* Help button is always the last button in the toolbar
**/
ZaMTAController.initToolbarMethod = 
function () {
	this._toolbarOperations.push(new ZaOperation(ZaOperation.LABEL, ZaMsg.TBB_LastUpdated, ZaMsg.TBB_LastUpdated_tt, null, null, null,null,null,null,"refreshTime"));	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SEP));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.REFRESH, ZaMsg.TBB_Refresh, ZaMsg.TBB_Refresh_tt, null, null, new AjxListener(this, this.refreshListener)));	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.SERTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener)));    	
}
ZaController.initToolbarMethods["ZaMTAController"].push(ZaMTAController.initToolbarMethod);

/**
* @method _createUI
**/
ZaMTAController.prototype._createUI =
function () {
	this._view = new ZaMTAXFormView(this._container, this._app);

	this._initToolbar();
	//always add Help button at the end of the toolbar
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));							
	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations);		
	
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
    this._app.createView(ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW, elements);
	this._UICreated = true;
}


ZaMTAController.prototype.refreshListener = function () {
	this._currentObject.getMailQStatus();
}

/**
* @param ev
* This listener is invoked by ZaMTAController or any other controller that can change a ZaMTA object
**/
ZaMTAListController.prototype.handleMTAChange = 
function (ev) {
	//if any of the data that is currently visible has changed - update the view
	if(ev) {
		//update "Refresh" label
	}
}