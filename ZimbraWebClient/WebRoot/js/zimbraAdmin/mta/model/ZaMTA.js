/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaMTA
* This class represents Postfix Queue object
* @author Greg Solovyev
* @contructor
* @param app reference to the application instance
**/
ZaMTA = function() {
	ZaItem.call(this, "ZaMTA");
	this._init();
}


ZaMTA.prototype = new ZaItem;
ZaMTA.prototype.constructor = ZaMTA;
ZaItem.loadMethods["ZaMTA"] = new Array();
ZaItem.initMethods["ZaMTA"] = new Array();

ZaMTA.RESULTSPERPAGE = 25;
ZaMTA.POLL_INTERVAL = 1000;
ZaMTA.STATUS_IDLE = 0;
ZaMTA.STATUS_SCANNING = 1;
ZaMTA.STATUS_SCAN_COMPLETE = 2;
ZaMTA.STATUS_STALE = 3;
ZaMTA.ID_ALL = "ALL";

ZaMTA.SCANNER_STATUS_CHOICES = [{value:ZaMTA.STATUS_IDLE, label:ZaMsg.PQ_ScannerIdle}, 
	{value:ZaMTA.STATUS_SCANNING, label:ZaMsg.PQ_ScannerScanning},
	{value:ZaMTA.STATUS_SCAN_COMPLETE, label:ZaMsg.PQ_ScannerScanComplete},
	{value:ZaMTA.STATUS_STALE, label:ZaMsg.PQ_ScannerStaleData}];

/**
* attribute names
**/
ZaMTA.A_Servername = "servername";
ZaMTA.A_Status = "status";
ZaMTA.A_Stale = "stale";
ZaMTA.A_LastError = "lasterror";
ZaMTA.A_MTAName = "mtaname";
ZaMTA.A_refreshTime = "time";
ZaMTA.A_totalComplete = "total";
/**
* names of queues
**/
ZaMTA.A_DeferredQ = "deferred";
ZaMTA.A_IncomingQ = "incoming";
ZaMTA.A_ActiveQ = "active";
ZaMTA.A_CorruptQ = "corrupt";
ZaMTA.A_HoldQ = "hold";
/**
* names of summary fields
**/
ZaMTA.A_rdomain = "todomain";
ZaMTA.A_sdomain = "fromdomain";
ZaMTA.A_origip = "addr";
ZaMTA.A_raddress = "to";
ZaMTA.A_saddress = "from";
ZaMTA.A_error = "reason";
ZaMTA.A_host = "host";
ZaMTA.A_messages = "messages";
/**
* names of attributes in summary fields fields
**/
ZaMTA.A_pageNum = "pagenum";
ZaMTA.A_name = "name";
ZaMTA.A_count = "n";
ZaMTA.A_Qid = "qid";
ZaMTA.A_query = "query";
ZaMTA.A_more = "more";
ZaMTA.A_scan = "scan";
ZaMTA.A_selection_cache = "_selection_cache";
ZaMTA.A_queue_filter_name = "_queue_filter_name";
ZaMTA.A_queue_filter_value = "_queue_filter_value";
ZaMTA.MsgIDS = "message_ids";

ZaMTA._quecountsArr = new Array();
ZaMTA.threashHold;
ZaMTA.ActionRequeue = "requeue";
ZaMTA.ActionDelete = "delete";
ZaMTA.ActionHold = "hold";
ZaMTA.ActionRelease = "release";

ZaMTA.MANAGE_MAIL_QUEUE_RIGHT = "manageMailQueue";

ZaMTA.prototype.getTabToolTip =
function () {
	return ZaMsg.tt_tab_MTA + " " + this.type + " " + this.name ;
}

ZaMTA.prototype.getTabIcon = 
function () {
	return "Queue" ;
}

ZaMTA.prototype.QCountsCallback = function (resp) {
	try {
		if(!resp) {
			var ex = new ZmCsfeException(ZMsg.errorEmptyResponse,ZmCsfeException.CSFE_SVC_ERROR,"ZaMTA.prototype.QCountsCallback");
			throw ex;
		}
		if(resp.isException && resp.isException()) {
			var details = {obj:this,qName:null,poll:false};
			ZaApp.getInstance().getMTAController(this._viewInternalId).fireChangeEvent(details);		
			throw (resp.getException());
		} 	
		var response = resp.getResponse();
		var body = response.Body;
		//update my fields
		if(body && body.GetMailQueueInfoResponse.server && body.GetMailQueueInfoResponse.server[0]) {
			this.initFromJS(body.GetMailQueueInfoResponse.server[0], true);
			ZaMTA._quecountsArr.sort();
			ZaMTA.threashHold = ZaMTA._quecountsArr[Math.round(ZaMTA._quecountsArr.length/2)];
			var details = {obj:this,qName:null,poll:false};
			ZaApp.getInstance().getMTAController(this._viewInternalId).fireChangeEvent(details);
		} else {
			var ex = new ZmCsfeException(ZMsg["service.UNKNOWN_DOCUMENT"],ZmCsfeException.SVC_UNKNOWN_DOCUMENT,"ZaMTA.prototype.QCountsCallback");
			throw(ex);
			//ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaMTA.prototype.QCountsCallback");
		}
	} catch (ex) {
		this[ZaMTA.A_DeferredQ] = {n:ZaMsg.PQ_Error};
		this[ZaMTA.A_IncomingQ] = {n:ZaMsg.PQ_Error};
		this[ZaMTA.A_ActiveQ] = {n:ZaMsg.PQ_Error};	
		this[ZaMTA.A_HoldQ] = {n:ZaMsg.PQ_Error};	
		this[ZaMTA.A_CorruptQ] = {n:ZaMsg.PQ_Error};		
			
		this[ZaMTA.A_DeferredQ][ZaMTA.A_refreshTime] = ZaMsg.PQ_Error;
		this[ZaMTA.A_IncomingQ][ZaMTA.A_refreshTime] = ZaMsg.PQ_Error;	
		this[ZaMTA.A_ActiveQ][ZaMTA.A_refreshTime] = ZaMsg.PQ_Error;
		this[ZaMTA.A_HoldQ][ZaMTA.A_refreshTime] = ZaMsg.PQ_Error;
		this[ZaMTA.A_CorruptQ][ZaMTA.A_refreshTime] = ZaMsg.PQ_Error;	
		var details = {obj:this,qName:null,poll:false};
		ZaApp.getInstance().getMTAController(this._viewInternalId).fireChangeEvent(details);		
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaMTA.prototype.QCountsCallback");				
	}
}

/**
* @param app {ZaApp}
* @return {ZaItemList} a list of ZaMTA objects {@link ZaItemList}
**/
ZaMTA.getAll = function () {
	var soapDoc = AjxSoapDoc.create("GetAllServersRequest", ZaZimbraAdmin.URN, null);	
	soapDoc.getMethod().setAttribute("service", "mta");
	soapDoc.getMethod().setAttribute("applyConfig", "false");
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.asyncMode=false;
	params.soapDoc = soapDoc;
	soapDoc.setMethodAttribute("attrs", [ZaServer.A_ServiceHostname, ZaServer.A_description, ZaServer.A_zimbraServiceEnabled, ZaServer.A_zimbraServiceInstalled, ZaItem.A_zimbraId].join(","));	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_ALL_SERVER
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllServersResponse;	
	var list = new ZaItemList(ZaMTA);
	var retVal = new ZaItemList(ZaMTA);
	list.loadFromJS(resp);	
	if(!list.loadedRights)
		list.loadEffectiveRights();
	
	var servers = list.getArray();
	var cnt = servers.length;
	for(var i = 0; i < cnt; i++) {
		if(ZaItem.hasRight(ZaMTA.MANAGE_MAIL_QUEUE_RIGHT, servers[i])) {
			retVal.add(servers[i]);
		}
	}
	
	retVal.loadedRights = list.loadedRights;
	return retVal;	
}


ZaMTA.prototype.refresh = 
function() {
	this.load();	
}

ZaMTA.prototype.initFromJS = function (obj, summary) {
	if(obj.a) {
		ZaItem.prototype.initFromJS.call(this,obj);
	}
	if(obj.queue) {
		var cnt = obj.queue.length;
		for (var ix=0; ix < cnt; ix++) {
			
			var queue = obj.queue[ix];
			var qName = queue.name;

			if(!this[qName])
				this[qName] = new Object();
				

			if(queue[ZaMTA.A_count] != undefined) {
				this[qName][ZaMTA.A_count] = queue[ZaMTA.A_count];
				ZaMTA._quecountsArr.push(queue[ZaMTA.A_count]);
			}
			if(queue[ZaMTA.A_refreshTime] != undefined) {
				this[qName][ZaMTA.A_refreshTime] = AjxDateUtil.computeDateStr(new Date(), parseInt(queue[ZaMTA.A_refreshTime]));
			}
			if(summary)
				continue;
				
			this[qName][ZaMTA.MsgIDS] = null;
			try {
				if(queue[ZaMTA.A_totalComplete] != undefined) {
					this[qName][ZaMTA.A_totalComplete] = parseInt(queue[ZaMTA.A_totalComplete]);
				}
			} catch (ex) {
				this[qName][ZaMTA.A_totalComplete] = 0;
			}
							
			if(queue[ZaMTA.A_scan] != undefined) {
				if(queue[ZaMTA.A_scan]) {
					this[qName][ZaMTA.A_Status] = ZaMTA.STATUS_SCANNING;
				} else {
					this[qName][ZaMTA.A_Status] = ZaMTA.STATUS_SCAN_COMPLETE;						 
					this[qName][ZaMTA.A_count] = this[qName][ZaMTA.A_totalComplete];
				}
			}	

			if(queue[ZaMTA.A_Stale]) {
				this[qName][ZaMTA.A_Status] = ZaMTA.STATUS_STALE;
			} 
			
			this[qName][ZaMTA.A_more] = queue[ZaMTA.A_more];

			this[qName][ZaMTA.A_rdomain] = [];
			this[qName][ZaMTA.A_sdomain]  = [];
			this[qName][ZaMTA.A_origip] = [];
			this[qName][ZaMTA.A_raddress] = [];
			this[qName][ZaMTA.A_saddress] = [];
			this[qName][ZaMTA.A_error] = [];
			this[qName][ZaMTA.A_host] = [];
			this[qName][ZaMTA.A_messages] = [];
			if(queue.qs) {
				var qs = obj.queue[ix].qs;
				var cnt2 = qs.length;
				for (var j = 0; j < cnt2; j++) {
					if(!this[qName][qs[j].type])
						this[qName][qs[j].type] = [];

					if(qs[j].qsi) {
						
						var item = qs[j].qsi;
						var cnt3 = item.length;
						for (var k = 0; k < cnt3; k++) {
						//	var item = qs[j].item[k];
							item[k].prototype = new ZaMTAQSummaryItem;
							item[k].getToolTip = ZaMTAQSummaryItem.prototype.getToolTip;
							item[k].toString = ZaMTAQSummaryItem.prototype.toString;
							//this[qName][qs[j].type].push(item);
							//this[qName][qs[j].type].push(new ZaMTAQSummaryItem( item[ZaMTAQSummaryItem.A_description], item[ZaMTAQSummaryItem.A_text], item[ZaMTAQSummaryItem.A_count]));
						}
						this[qName][qs[j].type] = item;
					}
				}	
			}	
			this[qName][ZaMTA.A_messages] = [];
			if(queue.qi) {
				var qi = obj.queue[ix].qi;
				var cnt4 = qi.length;
				for (var j = 0; j < cnt4; j++) {
					qi[j].prototype = new ZaMTAQMsgItem;
					qi[j].getToolTip = ZaMTAQMsgItem.prototype.getToolTip;
					qi[j].toString = ZaMTAQMsgItem.prototype.toString;					
				}	
				this[qName][ZaMTA.A_messages] = qi;
			}			
		}
	}

}
/**
* Make a SOAP call to get file counts in queue folders
**/
ZaMTA.loadMethod = 
function(by, val) {
	var soapDoc = AjxSoapDoc.create("GetMailQueueInfoRequest", ZaZimbraAdmin.URN, null);
	var attr = soapDoc.set("server", "");
	attr.setAttribute("name", this.name);		
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = true;
	var callback = new AjxCallback(this, this.QCountsCallback);	
	params.callback = callback;
	command.invoke(params);		
}
ZaItem.loadMethods["ZaMTA"].push(ZaMTA.loadMethod);

ZaMTA.luceneEscape = function (str) {
	return String(str).replace(/([\-\+\&\\!\(\)\{\}\[\]\^\"\~\*\?\:\\])/g, "\\$1");
}
/**
* send a MailQStatusRequest 
**/
ZaMTA.prototype.getMailQStatus = function (qName,query,offset,limit,force) {
	if(force) {
		var cnt = this[qName].n;
		this[qName] = {n:cnt};
	}
	limit = (limit != null) ? limit: ZaMTA.RESULTSPERPAGE;
	offset = (offset != null) ? offset: "0";
	//query = (query != null) ? query: "";	
	
	var soapDoc = AjxSoapDoc.create("GetMailQueueRequest", ZaZimbraAdmin.URN, null);

	var serverEl = soapDoc.set("server", "");
	serverEl.setAttribute("name", this.name);		

	var qEl = soapDoc.getDoc().createElement("queue");
	qEl.setAttribute("name", qName);		
	
	if(force) {
		qEl.setAttribute("scan", 1);	
		this[qName][ZaMTA.A_Status] = ZaMTA.STATUS_SCANNING;	
	}
		
	serverEl.appendChild(qEl);
	
	var queryEl = soapDoc.getDoc().createElement("query");
	if( !AjxUtil.isEmpty(query) ) {
		for (var key in query) {
			var arr = query[key];
			if(arr) {
				var cnt = arr.length;	
				if(cnt) {
					var fieldEl = soapDoc.getDoc().createElement("field");
					fieldEl.setAttribute("name", key);			
					for(var i=0;i<cnt;i++) {
						var matchEl = soapDoc.getDoc().createElement("match");
						matchEl.setAttribute("value", arr[i][ZaMTAQSummaryItem.A_text]);
						fieldEl.appendChild(matchEl);	
					}	
					queryEl.appendChild	(fieldEl);	
				}
			}
		}
	}
	
	if (offset != null) {
		queryEl.setAttribute("offset", offset);
	}
	
	if (limit != null) {
		queryEl.setAttribute("limit", limit);
	}


	qEl.appendChild(queryEl);
	
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = true;
	var callback = new AjxCallback(this, this.mailQStatusCallback,{qName:qName,query:query,offset:offset,limit:limit,force:force});	
	params.callback = callback;

	command.invoke(params);		
}

/**
* this method is called when the server returns MailQStatusResponse 
**/
ZaMTA.prototype.mailQStatusCallback = function (arg,resp) {
	//update my fields
	//ZaMTA.makeTestData1(this);
	var qName = arg.qName;
	if(!resp) {
		var ex = new ZmCsfeException(ZMsg.errorEmptyResponse,ZmCsfeException.CSFE_SVC_ERROR,"ZaMTA.prototype.mailQStatusCallback");
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaMTA.prototype.mailQStatusCallback");
		//this.goPrev();
		return;		
	}
	if(resp.isException && resp.isException()) {
		if(resp.getException().code == ZmCsfeException.SVC_ALREADY_IN_PROGRESS) {
			var details = {obj:this,qName:qName,poll:true};
			ZaApp.getInstance().getMTAController(this._viewInternalId).fireChangeEvent(details);				
		} else if (resp.getException().code == ZmCsfeException.SVC_TEMPORARILY_UNAVAILABLE) {
			ZaApp.getInstance().getCurrentController().popupMsgDialog(ZaMsg.ERROR_PQ_SERVICE_UNAVAILABLE);
		} else {
			ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaMTA.prototype.mailQStatusCallback");
		}
		return;
	} 	
	var response = resp.getResponse();
	var body = response.Body;
	//update my fields
	if(body && body.GetMailQueueResponse.server && body.GetMailQueueResponse.server[0]) {
		this.initFromJS(body.GetMailQueueResponse.server[0], false);
		var details = {obj:this,poll:true};
		for(var ix in arg) {
			details[ix] = arg[ix];
		}

		ZaApp.getInstance().getMTAController(this._viewInternalId).fireChangeEvent(details);
	} else {
		var ex = new ZmCsfeException(ZMsg["service.UNKNOWN_DOCUMENT"],ZmCsfeException.SVC_UNKNOWN_DOCUMENT,"ZaMTA.prototype.mailQStatusCallback");
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaMTA.prototype.mailQStatusCallback");
		return;	
	}	
}

ZaMTA.prototype.mailQueueAction = function (qName, action, by, val) {
	var soapDoc = AjxSoapDoc.create("MailQueueActionRequest", ZaZimbraAdmin.URN, null);
	var serverEl = soapDoc.set("server", "");
	serverEl.setAttribute("name", this.name);		
	var qEl = soapDoc.getDoc().createElement("queue");
	qEl.setAttribute("name", qName);		
	serverEl.appendChild(qEl);
	
	//var actionEl = 	soapDoc.getDoc().createElement("action");
	var actionEl;
	if(by == "id") {
		actionEl = soapDoc.set("action", val,qEl);
	} else {
		actionEl = soapDoc.getDoc().createElement("action");
		var queryEl = soapDoc.getDoc().createElement("query");
		if(val != null) {
			for (var key in val) {
				var arr = val[key];
				if(arr) {
					var cnt = arr.length;	
					if(cnt) {
						var fieldEl = soapDoc.getDoc().createElement("field");
						fieldEl.setAttribute("name", key);				
						for(var i=0;i<cnt;i++) {
							var matchEl = soapDoc.getDoc().createElement("match");
							matchEl.setAttribute("value", arr[i][ZaMTAQSummaryItem.A_text]);
							fieldEl.appendChild(matchEl);	
						}	
						queryEl.appendChild	(fieldEl);	
					}
				}
			}
		}
		actionEl.appendChild(queryEl);	
		qEl.appendChild(actionEl);	
	}
	
	actionEl.setAttribute("op", action);
	actionEl.setAttribute("by", by);
	


	//qEl.appendChild(actionEl);
	
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = true;
	var callback = new AjxCallback(this, this.mailQueueActionClbck, qName);	
	params.callback = callback;

	command.invoke(params);		
}

ZaMTA.prototype.mailQueueActionClbck = function (qName, resp) {
	this[qName][ZaMTA.A_DeferredQ] = ZaMTA.STATUS_STALE;
	this[qName][ZaMTA.A_IncomingQ] = ZaMTA.STATUS_STALE;
	this[qName][ZaMTA.A_ActiveQ] = ZaMTA.STATUS_STALE;
	this[qName][ZaMTA.A_CorruptQ] = ZaMTA.STATUS_STALE;
	this[qName][ZaMTA.A_HoldQ] = ZaMTA.STATUS_STALE;				

	if(resp.isException && resp.isException()) {
		if(resp.getException().code == ZmCsfeException.SVC_ALREADY_IN_PROGRESS) {
			var details = {obj:this,qName:qName};
			ZaApp.getInstance().getMTAController(this._viewInternalId).fireChangeEvent(details);				
		} else if (resp.getException().code == ZmCsfeException.SVC_TEMPORARILY_UNAVAILABLE) {
			ZaApp.getInstance().getCurrentController().popupMsgDialog(ZaMsg.ERROR_PQ_SERVICE_UNAVAILABLE);
		} else {
			ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaMTA.prototype.mailQueueActionClbck");
		}
	} else {
		this.getMailQStatus(qName);
	}	
}

ZaMTA.prototype.flushQueues = function () {
	var soapDoc = AjxSoapDoc.create("MailQueueFlushRequest", ZaZimbraAdmin.URN, null);
	var serverEl = soapDoc.set("server", "");
	serverEl.setAttribute("name", this.name);		

	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = false;
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_FLUSH_QUEUE
	}
	ZaRequestMgr.invoke(params, reqMgrParams);		
}

ZaMTA.initMethod = function () {
	this.attrs = new Object();
	this.id = "";
	this.name="";
	this[ZaItem.A_zimbraId] = "000"
	this[ZaMTA.A_DeferredQ] = {n:ZaMsg.PQ_Loading};
	this[ZaMTA.A_IncomingQ] = {n:ZaMsg.PQ_Loading};
	this[ZaMTA.A_ActiveQ] = {n:ZaMsg.PQ_Loading};	
	this[ZaMTA.A_HoldQ] = {n:ZaMsg.PQ_Loading};	
	this[ZaMTA.A_CorruptQ] = {n:ZaMsg.PQ_Loading};		
		
	this[ZaMTA.A_DeferredQ][ZaMTA.A_refreshTime] = ZaMsg.PQ_Loading;
	this[ZaMTA.A_IncomingQ][ZaMTA.A_refreshTime] = ZaMsg.PQ_Loading;	
	this[ZaMTA.A_ActiveQ][ZaMTA.A_refreshTime] = ZaMsg.PQ_Loading;
	this[ZaMTA.A_HoldQ][ZaMTA.A_refreshTime] = ZaMsg.PQ_Loading;
	this[ZaMTA.A_CorruptQ][ZaMTA.A_refreshTime] = ZaMsg.PQ_Loading;	
	
	this[ZaMTA.A_DeferredQ][ZaMTA.A_pageNum] = 0;
	this[ZaMTA.A_IncomingQ][ZaMTA.A_pageNum] = 0;	
	this[ZaMTA.A_ActiveQ][ZaMTA.A_pageNum] = 0;
	this[ZaMTA.A_HoldQ][ZaMTA.A_pageNum] = 0;
	this[ZaMTA.A_CorruptQ][ZaMTA.A_pageNum] = 0;		
	
	this[ZaMTA.A_DeferredQ][ZaMTA.A_Status] = ZaMTA.STATUS_IDLE;	
	this[ZaMTA.A_IncomingQ][ZaMTA.A_Status] = ZaMTA.STATUS_IDLE;	
	this[ZaMTA.A_ActiveQ][ZaMTA.A_Status] = ZaMTA.STATUS_IDLE;	
	this[ZaMTA.A_HoldQ][ZaMTA.A_Status] = ZaMTA.STATUS_IDLE;	
	this[ZaMTA.A_CorruptQ][ZaMTA.A_Status] = ZaMTA.STATUS_IDLE;					
}
ZaItem.initMethods["ZaMTA"].push(ZaMTA.initMethod);

ZaMTAQSummaryItem = function (d, t, n) {
	if (arguments.length == 0) return;
	ZaItem.call(this,"ZaMTAQSummaryItem");
	this._init();
	if(d) 
		this[ZaMTAQSummaryItem.A_description] = d;
	if(t)
		this[ZaMTAQSummaryItem.A_text] = t;
	if(n)
		this[ZaMTAQSummaryItem.A_count] = n;

}


ZaMTAQSummaryItem.A_text = "t";
ZaMTAQSummaryItem.A_text_col = "textColumn";
ZaMTAQSummaryItem.A_description = "d";
ZaMTAQSummaryItem.A_count = "n";
ZaMTAQSummaryItem.A_count_col = "countColumn";

ZaMTAQSummaryItem.prototype = new ZaItem;
ZaMTAQSummaryItem.prototype.constructor = ZaMTAQSummaryItem;
ZaItem.loadMethods["ZaMTAQSummaryItem"] = new Array();
ZaItem.initMethods["ZaMTAQSummaryItem"] = new Array();

ZaMTAQSummaryItem.prototype.toString = function () {
	return this[ZaMTAQSummaryItem.A_text]+this[ZaMTAQSummaryItem.A_count];
}

/**
* Returns HTML for a tool tip for this account.
*/
ZaMTAQSummaryItem.prototype.getToolTip =
function() {

	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = AjxStringUtil.htmlEncode(this[ZaMTAQSummaryItem.A_text]);
		html[idx++] = "<br>";
		html[idx++] = this[ZaMTAQSummaryItem.A_count];
		html[idx++] = " ";
		html[idx++] = ZaMsg.PQV_Messages;
		this._toolTip = html.join("");
	}
	return this._toolTip;
}


ZaMTAQMsgItem = function () {
	if (arguments.length == 0) return;
	ZaItem.call(this, "ZaMTAQMsgItem");
	this._init();
}


ZaMTAQMsgItem.A_time = "time";
ZaMTAQMsgItem.A_content_filter = "filter";
ZaMTAQMsgItem.A_origin_host = "host";
ZaMTAQMsgItem.A_sender = "from";
ZaMTAQMsgItem.A_fromdomain = "fromdomain";
ZaMTAQMsgItem.A_todomain = "todomain";
ZaMTAQMsgItem.A_id = "id";
ZaMTAQMsgItem.A_recipients = "to";
ZaMTAQMsgItem.A_size = "size";
ZaMTAQMsgItem.A_origin_ip = "addr";
ZaMTAQMsgItem.A_error = "reason";

ZaMTAQMsgItem.prototype = new ZaItem;
ZaMTAQMsgItem.prototype.constructor = ZaMTAQMsgItem;
ZaItem.loadMethods["ZaMTAQMsgItem"] = new Array();
ZaItem.initMethods["ZaMTAQMsgItem"] = new Array();

ZaMTAQMsgItem.prototype.toString = function () {
	return this[ZaMTAQMsgItem.A_id];
}

ZaMTAQMsgItem.prototype.initFromJS = function (obj) {
	this[ZaMTAQMsgItem.A_time] = obj[ZaMTAQMsgItem.A_time];
	this[ZaMTAQMsgItem.A_content_filter] = obj[ZaMTAQMsgItem.A_content_filter];
	this[ZaMTAQMsgItem.A_origin_host] = obj[ZaMTAQMsgItem.A_origin_host];
	this[ZaMTAQMsgItem.A_sender] = obj[ZaMTAQMsgItem.A_sender];
	this[ZaMTAQMsgItem.A_id] = obj[ZaMTAQMsgItem.A_id];
	this[ZaMTAQMsgItem.A_recipients] = obj[ZaMTAQMsgItem.A_recipients];
	this[ZaMTAQMsgItem.A_origin_ip] = obj[ZaMTAQMsgItem.A_origin_ip];
}

/**
* Returns HTML for a tool tip for this account.
*/
ZaMTAQMsgItem.prototype.getToolTip =
function() {
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = AjxStringUtil.htmlEncode(ZaMsg.PQ_Sender + " " + this[ZaMTAQMsgItem.A_sender]);		
		html[idx++] = "<br/>";
		html[idx++] = AjxStringUtil.htmlEncode(ZaMsg.PQ_OriginHost + " " + this[ZaMTAQMsgItem.A_origin_host]);		
		html[idx++] = "<br/>";
		html[idx++] = AjxStringUtil.htmlEncode(ZaMsg.PQ_OriginDomain + " " + this[ZaMTAQMsgItem.A_fromdomain]);		
		html[idx++] = "<br/>";
		html[idx++] = AjxStringUtil.htmlEncode(ZaMsg.PQ_OriginIP + " " + this[ZaMTAQMsgItem.A_origin_ip]);		
		html[idx++] = "<br/>";
		html[idx++] = AjxStringUtil.htmlEncode(ZaMsg.PQ_Recipients + " " + this[ZaMTAQMsgItem.A_recipients]);
		html[idx++] = "<br/>";
		var arr = this[ZaMTAQMsgItem.A_todomain] instanceof Array ? this[ZaMTAQMsgItem.A_todomain] : [this[ZaMTAQMsgItem.A_todomain]];
		html[idx++] = AjxStringUtil.htmlEncode(ZaMsg.PQ_DestinationDomain + " " + ZaUtil.getUniqueArrayElements(arr));		
		html[idx++] = "<br/>";
		html[idx++] = AjxStringUtil.htmlEncode(ZaMsg.PQ_ContentFilter + " " + this[ZaMTAQMsgItem.A_content_filter]);		
		html[idx++] = "<br/>";
		html[idx++] = AjxStringUtil.htmlEncode(ZaMsg.PQ_Size + " " + this[ZaMTAQMsgItem.A_size]);		
		html[idx++] = "<br/>";
		if(this[ZaMTAQMsgItem.A_error] !=null && this[ZaMTAQMsgItem.A_error] != undefined && this[ZaMTAQMsgItem.A_error].length>0)
			html[idx++] = AjxStringUtil.htmlEncode(ZaMsg.PQ_Reason + " " + this[ZaMTAQMsgItem.A_error]);				

		this._toolTip = html.join("");
	}
	return this._toolTip;
}



PostQSummary_XModelItem = function (){}
XModelItemFactory.createItemType("_POSTQSUMMARY_", "postqsummary", PostQSummary_XModelItem);
PostQSummary_XModelItem.prototype.items = [
				{id:ZaMTA.A_rdomain, type:_LIST_, listItem:
					{type:_OBJECT_, 
						items: [
							{id:ZaMTA.A_name, type:_STRING_},
							{id:ZaMTA.A_count, type:_NUMBER_}
						]
					}
				},
				{id:ZaMTA.A_origip, type:_LIST_, listItem:
					{type:_OBJECT_, 
						items: [
							{id:ZaMTA.A_name, type:_STRING_},
							{id:ZaMTA.A_count, type:_NUMBER_}
						]
					}
				},
				{id:ZaMTA.A_raddress, type:_LIST_, listItem:
					{type:_OBJECT_, 
						items: [
							{id:ZaMTA.A_name, type:_STRING_},
							{id:ZaMTA.A_count, type:_NUMBER_}
						]
					}
				},		
				{id:ZaMTA.A_saddress, type:_LIST_, listItem:
					{type:_OBJECT_, 
						items: [
							{id:ZaMTA.A_name, type:_STRING_},
							{id:ZaMTA.A_count, type:_NUMBER_}
						]
					}
				},						
				{id:ZaMTA.A_error, type:_LIST_, listItem:
					{type:_OBJECT_, 
						items: [
							{id:ZaMTA.A_name, type:_STRING_},
							{id:ZaMTA.A_count, type:_NUMBER_}
						]
					}
				},
				{id:ZaMTA.A_messages, type:_LIST_, listItem:
					{type:_OBJECT_, 
						items: [
							{id:ZaMTAQMsgItem.A_id, type:_STRING_},
							{id:ZaMTAQMsgItem.A_recipients, type:_STRING_},
							{id:ZaMTAQMsgItem.A_content_filter, type:_STRING_},
							{id:ZaMTAQMsgItem.A_origin_host, type:_STRING_},
							{id:ZaMTAQMsgItem.A_sender, type:_STRING_},
							{id:ZaMTAQMsgItem.A_origin_ip, type:_STRING_}
						]
					}
				},				
				{id:ZaMTA.A_count, type:_NUMBER_},
				{id:ZaMTA.A_pageNum, type:_NUMBER_},
				{id:ZaMTA.A_query, type:_STRING_},
				{id:ZaMTA.A_Status, type:_NUMBER_,choices:ZaMTA.SCANNER_STATUS_CHOICES},
				{id:ZaMTA.A_refreshTime, type:_STRING_}
			];
ZaMTA.myXModel = {
	items: [
		{id:ZaMTA.A_Status, type:_STRING_, ref:ZaMTA.A_Status},
		{id:ZaMTA.A_MTAName, type:_STRING_, ref:ZaMTA.A_MTAName},
		{id:ZaMTA.A_LastError, type:_STRING_, ref:ZaMTA.A_LastError},
		{id:ZaMTA.A_DeferredQ , type:_POSTQSUMMARY_},
		{id:ZaMTA.A_IncomingQ , type:_POSTQSUMMARY_},
		{id:ZaMTA.A_ActiveQ , type:_POSTQSUMMARY_},
		{id:ZaMTA.A_CorruptQ , type:_POSTQSUMMARY_}, 
		{id:ZaMTA.A_HoldQ , type:_POSTQSUMMARY_}		
	]
};
