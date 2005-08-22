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
* @class ZaBackup
* @contructor ZaBackup
* @param ZaApp app
* this class is a model for doing backup and restore operations
* @author Greg Solovyev
**/
function ZaBackup(app) {
	ZaItem.call(this, app);
	this.label = "";
	this.server = "";
	this.accounts = null;
	this.live=0;
	this[ZaModel.currentStep] = 1;
}

ZaBackup.prototype = new ZaItem;
ZaBackup.prototype.constructor = ZaBackup;

ZaBackup.prototype.initFromDom = 
function (node) {
	this.accounts = new Array();
	var queryNode = node.firstChild;
	var accountsList = queryNode.getAttribute("accounts");
	if (accountsList) {
		if(accountsList.indexOf(",") > 0) {
			this.accounts = accountsList.split(",");
		} else {
			this.accounts.push(accountsList);
		}
	}
	this.label = queryNode.getAttribute("label");
	this.live = parseInt(queryNode.getAttribute("live"));
}

/**
* @method static queryBackups
* @param serverId:string - zimbraId of the server to which the SOAP request will be sent
* @param target:string - path to the location of backups
* @param label:string
* @param fromDate:timestamp
* @param verbose:Boolean
* @param callback:AjxCallback - callback that will be invoked by ZmCsfeAsynchCommand
**/
ZaBackup.queryBackups = 
function (serverId, target, label, fromDate, verbose, callback) {
	var soapDoc = AjxSoapDoc.create("BackupQueryRequest", "urn:zimbraAdmin", null);
	var queryEl = soapDoc.set("query", "");
	if(target) {
		queryEl.setAttribute("target", target);
	}
	if(label) {
		queryEl.setAttribute("label", label);
	}
	if(fromDate) {
		queryEl.setAttribute("list", fromDate);
	}
	if(verbose) {
		queryEl.setAttribute("verbose", verbose);
	}
	var asynCommand = new ZmCsfeAsynchCommand();
	asynCommand.addInvokeListener(callback);
	asynCommand.invoke(soapDoc, false, null, serverId, true);	
}

/**
* @method static queryAccountBackup
* @param serverId:string - zimbraId of the server to which the SOAP request will be sent
* @param target:string - path to the location of backups
* @param accounts:Array - array of account names 
* @param callback:AjxCallback - callback that will be invoked by ZmCsfeAsynchCommand
**/
ZaBackup.queryAccountBackup = 
function (serverId, target, accounts, callback) {
	var soapDoc = AjxSoapDoc.create("BackupAccountQueryRequest", "urn:zimbraAdmin", null);
	var queryEl = soapDoc.set("query", "");
	if(target) {
		queryEl.setAttribute("target", target);
	}
	if(!accounts) {
		throw(new AjxException("accounts parameter cannot be null", AjxException.INVALID_PARAM, "ZaBackup.queryAccountBackup", ZaMsg.ERROR_BACKUP_1));
	} 
	var cnt = accounts.length;
	for(var i = 0; i < cnt; i ++) {
		var accEl = soapDoc.set("a", "", queryEl);
		accEl.setAttribute("name", accounts[i]);
	}
	var asynCommand = new ZmCsfeAsynchCommand();
	asynCommand.addInvokeListener(callback);
	asynCommand.invoke(soapDoc, false, null, serverId, true);	
}

