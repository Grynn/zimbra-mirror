/*
***** BEGIN LICENSE BLOCK *****
Version: ZPL 1.1

The contents of this file are subject to the Zimbra Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of
the License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY
OF ANY KIND, either express or implied. See the License for the specific language governing
rights and limitations under the License.

The Original Code is: Zimbra Collaboration Suite.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

/**
* @class ZaStatusViewController 
* @contructor ZaStatusViewController
* @param appCtxt
* @param container
* @param app
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaStatusViewController(appCtxt, container, app) {

	ZaController.call(this, appCtxt, container, app);
}

ZaStatusViewController.prototype = new ZaController();
ZaStatusViewController.prototype.constructor = ZaStatusViewController;

ZaStatusViewController.STATUS_VIEW = "ZaStatusViewController.STATUS_VIEW";

ZaStatusViewController.prototype.show = 
function() {
    if (!this._appView) {
//		this._toolbar = new ZaStatusToolBar(this._container);
		this._contentView = new ZaStatusView(this._container, this._app);
		this._appView = this._app.createView(ZaStatusViewController.STATUS_VIEW, [this._contentView]);
	}
	this._app.pushView(ZaStatusViewController.STATUS_VIEW);
	this._app.setCurrentController(this);
}


/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaStatusViewController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
}