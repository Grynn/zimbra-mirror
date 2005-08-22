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
* @class ZaStatus 
* @contructor ZaStatus
* @param app
* @author Greg Solovyev
**/
function ZaStatus(app) {
	ZaItem.call(this, ZaEvent.S_STATUS);
}

ZaStatus.prototype = new ZaItem;
ZaStatus.prototype.constructor = ZaStatus;

ZaStatus.loadStatusTable = 
function() {
	var soapDoc = AjxSoapDoc.create("GetServiceStatusRequest", "urn:zimbraAdmin", null);
	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;
	var list = new ZaItemList("status", ZaStatus);
	list.loadFromDom(resp);
	return list;
}

ZaStatus.prototype.initFromDom =
function (node) {
	this.serverName = node.getAttribute("server");
	this.serviceName = node.getAttribute("service");
	this.timestamp = node.getAttribute("t");
	this.time = new Date(Number(this.timestamp)*1000).toLocaleString();
	this.status = node.firstChild.nodeValue;
	DBG.println(AjxDebug.DBG3, "serverName=" + this.serverName+"<br>serviceName="+this.serviceName+"<br>time="+this.time+"<br>timestamp="+this.timestamp+"<br>status="+this.status); 
}

ZaStatus.PRFX_Server = "status_server";
ZaStatus.PRFX_Service = "status_service";
ZaStatus.PRFX_Time = "status_time";
ZaStatus.PRFX_Status = "status_status";
