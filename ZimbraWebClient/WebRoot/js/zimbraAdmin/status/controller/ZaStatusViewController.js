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


ZaStatusViewController.prototype.show = 
function() {
    if (!this._contentView) {
//		this._toolbar = new ZaStatusToolBar(this._container);
		this._contentView = new ZaStatusView(this._container, this._app);
		//this._contentView = new ZaServicesListView(this._container, this._app);
		var elements = new Object();
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		this._app.createView(ZaZimbraAdmin._STATUS, elements);
	}
	var mystatusVector = this._app.getStatusList(true).getVector();
	//var mystatusVector = this.getDummyVector();
	var sortFunc = function (a,b) {
		return (a.serverName < b.serverName)? -1: ((a.serverName > b.serverName)? 1: 0);
	}
	mystatusVector.sort(sortFunc);

	this._contentView.set(mystatusVector);
	this._app.pushView(ZaZimbraAdmin._STATUS);
}

ZaStatusViewController.prototype.getDummyVector = function () {
	var i1 = new ZaStatus(this._app);
	i1.serverName = "barbara.liquidsys.com";
	i1.serviceName = "MTA";
	i1.timestamp = 1127945551;
	i1.time = (new Date(1127945551*1000).toLocaleString());
	i1.status = 1;
	var i2 = new ZaStatus(this._app);
	i2.serverName = "jenna.liquidsys.com";
	i2.serviceName = "ldap";
	i2.timestamp = 1127945551;
	i2.time = (new Date(1127945551*1000).toLocaleString());
	i2.status = 0;

	var i3 = new ZaStatus(this._app);
	i3.serverName = "tweak.liquidsys.com";
	i3.serviceName = "mta";
	i3.timestamp = 1127945551;
	i3.time = (new Date(1127945551*1000).toLocaleString());
	i3.status = 0;

	var i4 = new ZaStatus(this._app);
	i4.serverName = "jenna.liquidsys.com";
	i4.serviceName = "junk";
	i4.timestamp = 1127945551;
	i4.time = (new Date(1127945551*1000).toLocaleString());
	i4.status = 1;

	var i5 = new ZaStatus(this._app);
	i5.serverName = "dogfood.liquidsys.com";
	i5.serviceName = "service-two";
	i5.timestamp = 1127945551;
	i5.time = (new Date(1127945551*1000).toLocaleString());
	i5.status = 0;

	var arr = [i4,i3, i2, i1,i5];
	return AjxVector.fromArray(arr);
}

/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaStatusViewController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
}
