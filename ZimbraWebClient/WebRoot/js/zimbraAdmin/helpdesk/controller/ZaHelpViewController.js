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
* @class ZaHelpViewController 
* @contructor ZaHelpViewController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
function ZaHelpViewController(appCtxt, container, app) {

	ZaController.call(this, appCtxt, container, app,"ZaHelpViewController");
}

ZaHelpViewController.prototype = new ZaController();
ZaHelpViewController.prototype.constructor = ZaHelpViewController;


ZaHelpViewController.prototype.show = 
function(openInNewTab) {
    if (!this._contentView) {
		var elements = new Object();
		this._contentView = new ZaHelpView(this._container, this._app);
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab() 
		}
		//this._app.createView(ZaZimbraAdmin._HELP_VIEW, elements);
		this._app.createView(this.getContentViewId(), elements, tabParams) ;
		this._UICreated = true;
		this._app._controllers[this.getContentViewId ()] = this ;
	}
	//this._app.pushView(ZaZimbraAdmin._HELP_VIEW);
	this._app.pushView(this.getContentViewId());
	
	/*
	if (openInNewTab) {//when a ctrl shortcut is pressed
		
	}else{ //open in the main tab
		this.updateMainTab ("Help", ZaMsg.Help_view_title ) ;	
	} */
};
