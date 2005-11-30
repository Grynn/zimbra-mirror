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


ZaStatusViewController.prototype.show = function() {
	try {
		var globalConfig = this._app.getGlobalConfig();

		var mystatusVector = this._getSimpleStatusData();
	  	//var mystatusVector = this.getDummyVector();
	
	    if (!this._contentView) {
			this._createView();
		}
		// if cluster software is installed on the server, get that data ( which will be )
		// returned sorted.
		// otherwise just sort the simple data that we have.
	
		if (this._clusterSoftwareIsInstalled()) {
			mystatusVector = this._getClusterStatusData(mystatusVector);
		} else {
			mystatusVector.sort(ZaStatus.compare);
		}
		this._contentView.set(mystatusVector, globalConfig);
		this._contentView.addClusterSelectionListener(new AjxListener(this, this._selectionUpdated));
		this._app.pushView(ZaZimbraAdmin._STATUS);
	} catch (ex) {
		this._handleException(ex, "ZaStatusViewController.prototype.show", null, false);
		return;
	}	
};

ZaStatusViewController.prototype._getSimpleStatusData = function () {
	try {
		return ZaStatus.loadStatusTable().getVector();
	} catch (ex) {
		return AjxVector.fromArray([]);
	}	
};

ZaStatusViewController.prototype._createView = function () {
	try {
		var elements = new Object();
		if (this._clusterSoftwareIsInstalled()){
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
	} catch (ex) {
		this._handleException(ex, "ZaStatusViewController.prototype._createView", null, false);
		return;
	}	
		
};

ZaStatusViewController.prototype._getClusterStatusData = function (simpleDataVector) {
	var retVector = null;
	try {
		if (simpleDataVector.size() > 0) {
			retVector = ZaClusterStatus.getCombinedStatus(simpleDataVector);
		}
	} catch (e) {
		this._handleException(e, "ZaStatusViewController.prototype._getClusterStatusData", null, false);
		// Make sure the results are empty if we only have partial data.
		retVector = AjxVector.fromArray([]);
	}
	return retVector;
};

ZaStatusViewController.prototype._clusterSoftwareIsInstalled = function () {
	try {
		if (this._globalConfig == null) {
			this._globalConfig = this._app.getGlobalConfig();
		}
		return AjxUtil.isSpecified(this._globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_cluster]);
	} catch (ex) {
		this._handleException(e, "ZaStatusViewController.prototype._clusterSoftwareIsInstalled", null, false);	
	}
	return false;
};


ZaStatusViewController.prototype._selectionUpdated = function (event) {
	var list = event.dwtObj;
	this._updateOperations(list.getSelection());
};

ZaStatusViewController.prototype._updateOperations = function (selectionArray) {
	var disable = false;
	if (selectionArray.length == 0 ) {
		disable = true;
	} else if (selectionArray[0].clustered) {
		// we are currently a single selection list
		disable = true;
	}

	if (!disable) {
		this._toolbar.enable([ZaOperation.CLOSE], false);
	} else {
		this._toolbar.enable([ZaOperation.CLOSE], true);
	}
};

ZaStatusViewController.prototype.getDummyVector = function () {
	var formatter = AjxDateFormat.getDateTimeInstance(AjxDateFormat.MEDIUM, AjxDateFormat.SHORT);

	var i1 = new ZaStatus(this._app);
	i1.serverName = "timmy.liquidsys.com";
	i1.serviceName = "MTA";
	i1.timestamp = 1127945551;
	i1.time = (new Date(1127945551*1000).toLocaleString());
	i1.time = formatter.format(new Date(Number(i1.timestamp)*1000));
	i1.status = 1;

	var i2 = new ZaStatus(this._app);
	i2.serverName = "ebola.liquidsys.com";
	i2.serviceName = "ldap";
	i2.timestamp = 1127945551;
	i2.time = formatter.format(new Date(Number(i2.timestamp)*1000));
	i2.status = 0;

	var i3 = new ZaStatus(this._app);
	i3.serverName = "mail1.liquidsys.com";
	i3.serviceName = "mail";
	i3.timestamp = 1127945551;
	i3.time = formatter.format(new Date(Number(i3.timestamp)*1000));
	i3.status = 0;

	var i4 = new ZaStatus(this._app);
	i4.serverName = "mail1.liquidsys.com";
	i4.serviceName = "snmp";
	i4.timestamp = 1127945551;
	i4.time = formatter.format(new Date(Number(i4.timestamp)*1000));
	i4.status = 1;

	var i5 = new ZaStatus(this._app);
	i5.serverName = "mail2.liquidsys.com";
	i5.serviceName = "snmp";
	i5.timestamp = 1127945551;
	i5.time = formatter.format(new Date(Number(i5.timestamp)*1000));
	i5.status = 0;

	var i6 = new ZaStatus(this._app);
	i6.serverName = "mail2.liquidsys.com";
	i6.serviceName = "mail";
	i6.timestamp = 1127945551;
	i6.time = formatter.format(new Date(Number(i6.timestamp)*1000));
	i6.status = 0;

	var arr = [i6, i5,i4,i3, i2, i1]; 
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
	var serviceToFailover = this._contentView.getSelection()[0].serverName;
	this._failoverInstance = {selVal:"", service: serviceToFailover };
	if (this._failoverDialog == null) {
		this._failoverDialog = new DwtDialog(DwtShell.getShell(window), "ZaStatusFailoverDialog", "Fail service to server");
		this._failoverDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this,this._handleFailoverOkButton));
		var form = {
			numCols:2,
			colsizes: ["50%","50%"],
			items:[
			{type:_SPACER_, height:10},
			{type:_OUTPUT_, colSpan:"*", relevant:"instance.service == location.hostname", value:ZaMsg.FailoverWarningSameServer,
			 cssStyle:"color:red"},
			{ref: "selVal", type:_OSELECT1_, choices:this._app.getClusterServerChoices(), label:ZaMsg.ChooseFailoverServer},
			{type:_SPACER_, height:10}
			]
		}

		this._failoverView = new XForm(form, new XModel({id:"selVal", type:_UNTYPED_}), this._failoverInstance, this._failoverDialog);
		this._failoverView.setController(this);
		this._failoverView.draw();
		this._failoverDialog.setView(this._failoverView);
	} else {
		this._failoverView.setInstance(this._failoverInstance);
	}

	this._failoverDialog.popup();
};

ZaStatusViewController.prototype._handleFailoverOkButton = function (event) {
	var val = this._failoverInstance.selVal;
	var sendFailover = (val != null && val.length > 0);
	if (sendFailover){
		// check to see if this is your server --
		// If the admin is about to failover the server he's talking to,
		// we need to double check that he really, really wants to do that.
		// if he does, then we need to alert him to what's about to happen.

		// Note: If we can catch the network error, and retry in a few seconds
		// without killing the user's cpu ( on ff ), then we should do that.
		var serviceToFailover = this._contentView.getSelection()[0].serverName;
		var targetServer = this._failoverInstance.selVal;
		this._doFailover(serviceToFailover, targetServer);
	}
};

ZaStatusViewController.prototype._doFailover = function (targetService, newServer) {
	try {
		var soapDoc = AjxSoapDoc.create("FailoverClusterServiceRequest", "urn:zimbraAdmin", null);
		var service = soapDoc.set("service");
		service.setAttribute("name", targetService);
		service.setAttribute("newServer", newServer);
		var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, true);//.Body.FailoverServiceResponse;
		// refresh the query and show it again.
		this.show();
	} catch (ex) {
		this._handleException(ex, "ZaStatusViewController.prototype._doFailover", null, false);
	} finally {
		this._failoverDialog.popdown();
	}
}
