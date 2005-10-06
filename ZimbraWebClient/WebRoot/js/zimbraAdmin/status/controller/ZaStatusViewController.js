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
	var globalConfig = this._app.getGlobalConfig();
	//globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_cluster] = "true";
    if (!this._contentView) {
		var elements = new Object();
		if (AjxUtil.isSpecified(globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_cluster])) {
			var ops = [
				   new ZaOperation(ZaOperation.CLOSE, ZaMsg.STATUSTBB_Failover, ZaMsg.STATUSTBB_Failover_tt, null, null,
								   new AjxListener(this, this._failoverListener))
				   ];
			this._toolbar = new ZaToolBar(this._container, ops);
			this._toolbar.enable([ZaOperation.CLOSE], false);
			elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
		}
		this._contentView = new ZaStatusView(this._container, this._app);


		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		this._app.createView(ZaZimbraAdmin._STATUS, elements);

		
	}
	var mystatusVector = this._app.getStatusList(true).getVector();
	//var mystatusVector = this.getDummyVector();
	mystatusVector.sort(ZaStatus.compare);

	this._contentView.set(mystatusVector, globalConfig);
	this._contentView.addClusterSelectionListener(new AjxListener(this, this._selectionUpdated));
	this._app.pushView(ZaZimbraAdmin._STATUS);
};

ZaStatusViewController.prototype._selectionUpdated = function (event) {
	var list = event.dwtObj;
	this._updateOperations(list.getSelection().length);
};

ZaStatusViewController.prototype._updateOperations = function (selectedCount) {
	if (selectedCount != 1) {
		this._toolbar.enable([ZaOperation.CLOSE], false);
	} else {
		this._toolbar.enable([ZaOperation.CLOSE], true);
	}
};

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

ZaStatusViewController.prototype._failoverListener = function (event) {
	var btn = event.item;
	// popup dialog with list of possible servers to fail over to.
	this._popupServerSelectDialog();
};

ZaStatusViewController.prototype._popupServerSelectDialog = function () {
	if (this._failoverDialog == null) {
		this._failoverDialog = new DwtDialog(DwtShell.getShell(window), "ZaStatusFailoverDialog", "Fail service to server");
		this._failoverDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this,this._handleFailoverOkButton));
		var form = {
			items:[
			{type:_SPACER_, height:10},
			{ref: "selVal", type:_OSELECT1_, choices:this._app.getServerListChoices2(), label:"Choose a server:",},
			{type:_SPACER_, height:10}
			]
		}
		this._failoverInstance = {};
		var view = new XForm(form, new XModel({id:"selVal", type:_UNTYPED_}), this._failoverInstance, this._failoverDialog);
		view.setController(this);
		view.draw();
		this._failoverDialog.setView(view);
	}
	this._failoverDialog.popup();
};

ZaStatusViewController.prototype._handleFailoverOkButton = function (event) {
	var soapDoc = AjxSoapDoc.create("FailoverClusterServiceRequest", "urn:zimbraAdmin", null);
	var service = soapDoc.set("service");
	service.setAttribute("name", this._contentView.getSelection()[0].serverName);
	service.setAttribute("newServer", this._failoverInstance.selVal);
	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false).Body.FailoverServiceResponse;
	this._failoverDialog.popdown();
};
