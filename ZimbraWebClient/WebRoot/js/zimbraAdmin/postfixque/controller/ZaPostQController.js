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
* @class ZaPostQController controls display of a single Server's Postfix Queue
* @contructor ZaPostQController
* @param appCtxt
* @param container
* @param abApp
* @author Greg Solovyev
**/

function ZaPostQController(appCtxt, container,app) {
	ZaXFormViewController.call(this, appCtxt, container,app,"ZaPostQController");
	this._UICreated = false;
	this._helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/managing_servers/managing_servers.htm";				
	this._toolbarOperations = new Array();
	this.objType = ZaEvent.S_SERVER;	
}

ZaPostQController.prototype = new ZaXFormViewController();
ZaPostQController.prototype.constructor = ZaPostQController;

ZaController.initToolbarMethods["ZaPostQController"] = new Array();
ZaController.setViewMethods["ZaPostQController"] = new Array();
/**
*	@method show
*	@param entry - isntance of ZaServer class
*/
ZaPostQController.prototype.show = 
function(entry) {
	this._setView(entry);
	this.setDirty(false);
}

/**
*	@method setViewMethod 
*	@param entry - isntance of ZaDomain class
*/
ZaPostQController.setViewMethod =
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
ZaController.setViewMethods["ZaPostQController"].push(ZaPostQController.setViewMethod);

/**
* @method _createUI
**/
ZaPostQController.prototype._createUI =
function () {
	this._view = new ZaPostQXFormView(this._container, this._app);

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


