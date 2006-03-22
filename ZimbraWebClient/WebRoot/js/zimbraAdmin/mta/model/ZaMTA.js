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
* @class ZaMTA
* This class represents Postfix Queue object
* @author Greg Solovyev
* @contructor
* @param app reference to the application instance
**/
function ZaMTA(app) {
	ZaItem.call(this, app,"ZaMTA");
	this._init(app);
}
ZaMTA.RESULTSPERPAGE = 50;

ZaMTA.prototype = new ZaItem;
ZaMTA.prototype.constructor = ZaMTA;
ZaItem.loadMethods["ZaMTA"] = new Array();
ZaItem.initMethods["ZaMTA"] = new Array();
/**
* attribute names
**/
ZaMTA.A_Servername = "servername";
ZaMTA.A_Status = "status";
ZaMTA.A_LastError = "lasterror";
ZaMTA.A_MTAName = "mtaname";
ZaMTA.A_refreshTime = "refreshTime";
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
ZaMTA.A_destination = "destination";
ZaMTA.A_origin = "origin";
ZaMTA.A_error = "error";
ZaMTA.A_messages = "messages";
/**
* names of attributes in summary fields fields
**/
ZaMTA.A_name = "name";
ZaMTA.A_count = "n";
ZaMTA.A_Qid = "qid";
ZaMTA.A_query = "query";
ZaMTA.A_queue_filter_name = "_queue_filter_name";
ZaMTA.A_queue_filter_value = "_queue_filter_value";
ZaMTA.A_progress = "progress";

ZaMTA.prototype.QCountsCallback = function (resp) {
	if(!resp) {
		var ex = new ZmCsfeException(ZMsg.errorEmptyResponse,CSFE_SVC_ERROR,"ZaMTA.prototype.QCountsCallback");
		this._app.getCurrentController()._handleException(ex, "ZaMTA.prototype.QCountsCallback");
		this.goPrev();
		return;		
	}
	if(resp.isException && resp.isException()) {
		this._app.getCurrentController()._handleException(resp.getException(), "ZaMTA.prototype.QCountsCallback");
		return;
	} 	
	var response = resp.getResponse();
	var body = response.Body;
	//update my fields
	if(body && body.GetMailQueueInfoResponse.server && body.GetMailQueueInfoResponse.server[0]) {
		this.initFromJS(body.GetMailQueueInfoResponse.server[0]);
	} else {
		var ex = new ZmCsfeException(ZMsg.errorUnknownDoc,ZmCsfeException.SVC_UNKNOWN_DOCUMENT,"ZaMTA.prototype.QCountsCallback");
		this._app.getCurrentController()._handleException(ex, "ZaMTA.prototype.QCountsCallback");
		return;	
	}
	//notify listeners 
	this._app.getMTAController().fireChangeEvent(this);
}

/**
* @param app {ZaApp}
* @return {ZaItemList} a list of ZaMTA objects {@link ZaItemList}
**/
ZaMTA.getAll = function (app) {
	var soapDoc = AjxSoapDoc.create("GetAllServersRequest", "urn:zimbraAdmin", null);	
	soapDoc.getMethod().setAttribute("service", "mta");
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = command.invoke(params).Body.GetAllServersResponse;	
	var list = new ZaItemList(ZaMTA, app);
	list.loadFromJS(resp);	
	return list;	
//	return ZaMTA.returnTestData1();
}


ZaMTA.prototype.refresh = 
function() {
	this.load();	
}

/**
* Make a SOAP call to get file counts in queue folders
**/
ZaMTA.loadMethod = 
function(by, val, withConfig) {
	var soapDoc = AjxSoapDoc.create("GetMailQueueInfoRequest", "urn:zimbraAdmin", null);
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

ZaMTA.makeTestData1 = function (obj) {
//	mta1[ZaMTA.A_DeferredQ] = {query:("mta:(mta1) queue:("+ZaMTA.A_DeferredQ+")")};
	obj[ZaMTA.A_DeferredQ][ZaMTA.A_destination]=[
			new ZaMTAQSummaryItem(obj.app, "deferred_yahoo.com", "yahoo.com", 131),
			new ZaMTAQSummaryItem(obj.app, "deferred_gmail.com", "gmail.com", 101),			
			new ZaMTAQSummaryItem(obj.app, "deferred_hotmail.com", "hotmail.com", 121),						
			new ZaMTAQSummaryItem(obj.app, "deferred_usa.net", "usa.net", 50)									
		];
	obj[ZaMTA.A_DeferredQ][ZaMTA.A_origin]=[
			new ZaMTAQSummaryItem(obj.app, null, "64.23.45.222", 231),
			new ZaMTAQSummaryItem(obj.app, "deferred_221.23.45.26", "221.23.45.26", 201),			
			new ZaMTAQSummaryItem(obj.app, "deferred_121.23.45.123", "121.23.45.123", 221),						
			new ZaMTAQSummaryItem(obj.app, "deferred_220.63.45.201", "220.63.45.201", 21)		
		];
	obj[ZaMTA.A_DeferredQ][ZaMTA.A_error]=[
			new ZaMTAQSummaryItem(obj.app, "deferred_blah-blah", "blah-blah", 331),
			new ZaMTAQSummaryItem(obj.app, "deferred_rant-rant", "rant-rant", 301),			
			new ZaMTAQSummaryItem(obj.app, "deferred_wait-wait", "wait-wait", 321)
		];
	obj[ZaMTA.A_DeferredQ][ZaMTA.A_count]=1001;
}

ZaMTA.returnTestData1 = function (app) {
	var list = new ZaItemList(ZaMTA, app);
	var mta1 = new ZaMTA(app);
	mta1[ZaMTA.A_Status] = "running";
	mta1[ZaMTA.A_progress] = 50;
	mta1[ZaMTA.A_Servername] = "greg-d610.liquidsys.com";
	mta1[ZaMTA.A_LastError] = null;
	mta1[ZaMTA.A_MTAName] = "MTA1";
	mta1[ZaItem.A_zimbraId] = "mta1";
	mta1.id = "mta1";	
	mta1[ZaMTA.A_DeferredQ] = {query:("mta:(mta1) queue:("+ZaMTA.A_DeferredQ+")")};
	mta1[ZaMTA.A_DeferredQ][ZaMTA.A_destination]=[
			new ZaMTAQSummaryItem(app, "deferred_yahoo.com", "yahoo.com", 131),
			new ZaMTAQSummaryItem(app, "deferred_gmail.com", "gmail.com", 101),			
			new ZaMTAQSummaryItem(app, "deferred_hotmail.com", "hotmail.com", 121),						
			new ZaMTAQSummaryItem(app, "deferred_usa.net", "usa.net", 50)									
		];
	mta1[ZaMTA.A_DeferredQ][ZaMTA.A_origin]=[
			new ZaMTAQSummaryItem(app, "deferred_64.23.45.222", "64.23.45.222", 231),
			new ZaMTAQSummaryItem(app, "deferred_221.23.45.26", "221.23.45.26", 201),			
			new ZaMTAQSummaryItem(app, "deferred_121.23.45.123", "121.23.45.123", 221),						
			new ZaMTAQSummaryItem(app, "deferred_220.63.45.201", "220.63.45.201", 21)		
		];
	mta1[ZaMTA.A_DeferredQ][ZaMTA.A_error]=[
			new ZaMTAQSummaryItem(app, "deferred_blah-blah", "blah-blah", 331),
			new ZaMTAQSummaryItem(app, "deferred_rant-rant", "rant-rant", 301),			
			new ZaMTAQSummaryItem(app, "deferred_wait-wait", "wait-wait", 321)
		];
	mta1[ZaMTA.A_DeferredQ][ZaMTA.A_count]=1001;

	mta1[ZaMTA.A_IncomingQ] = {query:("mta:(mta1) queue:("+ZaMTA.A_IncomingQ+")")};
	mta1[ZaMTA.A_IncomingQ][ZaMTA.A_count] = 1021;

	mta1[ZaMTA.A_IncomingQ][ZaMTA.A_destination] = [
			new ZaMTAQSummaryItem(app, "incoming_yahoo.com", "yahoo.com", 132),
			new ZaMTAQSummaryItem(app, "incoming_gmail.com", "gmail.com", 102),			
			new ZaMTAQSummaryItem(app, "incoming_hotmail.com", "hotmail.com", 122),						
			new ZaMTAQSummaryItem(app, "incoming_usa.net", "usa.net", 12)									
		];
	mta1[ZaMTA.A_IncomingQ][ZaMTA.A_origin]=[
			new ZaMTAQSummaryItem(app, "incoming_64.23.45.222", "64.23.45.222", 232),
			new ZaMTAQSummaryItem(app, "incoming_221.23.45.26", "221.23.45.26", 202),			
			new ZaMTAQSummaryItem(app, "incoming_121.23.45.123", "121.23.45.123", 222),						
			new ZaMTAQSummaryItem(app, "incoming_220.63.45.201", "220.63.45.201", 22)		
		];

	mta1[ZaMTA.A_ActiveQ] = {query:("mta:(mta1) queue:("+ZaMTA.A_ActiveQ+")")};
	mta1[ZaMTA.A_ActiveQ][ZaMTA.A_count]=101;
	mta1[ZaMTA.A_ActiveQ][ZaMTA.A_destination]=[
			new ZaMTAQSummaryItem(app, "yahoo.com", "yahoo.com", 233),
			new ZaMTAQSummaryItem(app, "gmail.com", "gmail.com", 203),			
			new ZaMTAQSummaryItem(app, "hotmail.com", "hotmail.com", 123),						
			new ZaMTAQSummaryItem(app, "usa.net", "usa.net", 50)									
		]
	mta1[ZaMTA.A_ActiveQ][ZaMTA.A_origin]=[
			new ZaMTAQSummaryItem(app, "64.23.45.222", "64.23.45.222", 233),
			new ZaMTAQSummaryItem(app, "221.23.45.26", "221.23.45.26", 203),			
			new ZaMTAQSummaryItem(app, "121.23.45.123", "121.23.45.123", 123),						
			new ZaMTAQSummaryItem(app, "220.63.45.201", "220.63.45.201", 50)		
		];	
	
	mta1[ZaMTA.A_CorruptQ] = {query:("mta:(mta1) queue:("+ZaMTA.A_CorruptQ+")")};
	mta1[ZaMTA.A_CorruptQ][ZaMTA.A_count]=2131;			
	mta1[ZaMTA.A_CorruptQ][ZaMTA.A_destination]=[
			new ZaMTAQSummaryItem(app, "yahoo.com", "yahoo.com", 233),
			new ZaMTAQSummaryItem(app, "gmail.com", "gmail.com", 203),			
			new ZaMTAQSummaryItem(app, "hotmail.com", "hotmail.com", 123),						
			new ZaMTAQSummaryItem(app, "usa.net", "usa.net", 50)									
		];
	mta1[ZaMTA.A_CorruptQ][ZaMTA.A_origin]=[
			new ZaMTAQSummaryItem(app, "64.23.45.222", "64.23.45.222", 233),
			new ZaMTAQSummaryItem(app, "221.23.45.26", "221.23.45.26", 203),			
			new ZaMTAQSummaryItem(app, "121.23.45.123", "121.23.45.123", 123),						
			new ZaMTAQSummaryItem(app, "220.63.45.201", "220.63.45.201", 50)		
		];	

	mta1[ZaMTA.A_HoldQ] = {query:("mta:(mta1) queue:("+ZaMTA.A_HoldQ+")")};
	mta1[ZaMTA.A_HoldQ][ZaMTA.A_count]=1603;
	mta1[ZaMTA.A_HoldQ][ZaMTA.A_destination]=[
			new ZaMTAQSummaryItem(app, "yahoo.com", "yahoo.com", 233),
			new ZaMTAQSummaryItem(app, "gmail.com", "gmail.com", 203),			
			new ZaMTAQSummaryItem(app, "hotmail.com", "hotmail.com", 123),						
			new ZaMTAQSummaryItem(app, "usa.net", "usa.net", 50)									
		];
	mta1[ZaMTA.A_HoldQ][ZaMTA.A_origin]=[
			new ZaMTAQSummaryItem(app, "64.23.45.222", "64.23.45.222", 233),
			new ZaMTAQSummaryItem(app, "221.23.45.26", "221.23.45.26", 203),			
			new ZaMTAQSummaryItem(app, "121.23.45.123", "121.23.45.123", 123),						
			new ZaMTAQSummaryItem(app, "220.63.45.201", "220.63.45.201", 50)		
		];	

	var mta2 = new ZaMTA(app);
	mta2[ZaMTA.A_Servername] = "gregsolo.liquidsys.com";	
	mta2[ZaMTA.A_LastError] = null;
	mta2[ZaMTA.A_MTAName] = "MTA2";
	mta2[ZaItem.A_zimbraId] = "mta2";
	mta2.id = "mta2";	
	
	mta2[ZaMTA.A_DeferredQ] = {query:("mta:(mta2) queue:("+ZaMTA.A_DeferredQ+")")};
	mta2[ZaMTA.A_DeferredQ][ZaMTA.A_destination]=[
			new ZaMTAQSummaryItem(app, "yahoo.com", "yahoo.com", 233),
			new ZaMTAQSummaryItem(app, "gmail.com", "gmail.com", 203),			
			new ZaMTAQSummaryItem(app, "hotmail.com", "hotmail.com", 123),						
			new ZaMTAQSummaryItem(app, "usa.net", "usa.net", 50)									
		];
	mta2[ZaMTA.A_DeferredQ][ZaMTA.A_origin]=[
			new ZaMTAQSummaryItem(app, "64.23.45.222", "64.23.45.222", 233),
			new ZaMTAQSummaryItem(app, "221.23.45.26", "221.23.45.26", 203),			
			new ZaMTAQSummaryItem(app, "121.23.45.123", "121.23.45.123", 123),						
			new ZaMTAQSummaryItem(app, "220.63.45.201", "220.63.45.201", 50)		
		];
	mta2[ZaMTA.A_DeferredQ][ZaMTA.A_error]=[
			new ZaMTAQSummaryItem(app, "blah-blah", "blah-blah", 233),
			new ZaMTAQSummaryItem(app, "rant-rant", "rant-rant", 203),			
			new ZaMTAQSummaryItem(app, "wait-wait", "wait-wait", 123)
		];
	mta2[ZaMTA.A_DeferredQ][ZaMTA.A_count]=1001;

	mta2[ZaMTA.A_IncomingQ] = {query:("mta:(mta2) queue:("+ZaMTA.A_IncomingQ+")")};
	mta2[ZaMTA.A_IncomingQ][ZaMTA.A_count] = 1021;

	mta2[ZaMTA.A_IncomingQ][ZaMTA.A_destination] = [
			new ZaMTAQSummaryItem(app, "yahoo.com", "yahoo.com", 233),
			new ZaMTAQSummaryItem(app, "gmail.com", "gmail.com", 203),			
			new ZaMTAQSummaryItem(app, "hotmail.com", "hotmail.com", 123),						
			new ZaMTAQSummaryItem(app, "usa.net", "usa.net", 50)									
		];
	mta2[ZaMTA.A_IncomingQ][ZaMTA.A_origin]=[
			new ZaMTAQSummaryItem(app, "64.23.45.222", "64.23.45.222", 233),
			new ZaMTAQSummaryItem(app, "221.23.45.26", "221.23.45.26", 203),			
			new ZaMTAQSummaryItem(app, "121.23.45.123", "121.23.45.123", 123),						
			new ZaMTAQSummaryItem(app, "220.63.45.201", "220.63.45.201", 50)		
		];
	mta2[ZaMTA.A_IncomingQ][ZaMTA.A_error]=[
			new ZaMTAQSummaryItem(app, "blah-blah", "blah-blah", 233),
			new ZaMTAQSummaryItem(app, "rant-rant", "rant-rant", 203),			
			new ZaMTAQSummaryItem(app, "wait-wait", "wait-wait", 123)
		];		

	mta2[ZaMTA.A_ActiveQ] = {query:("mta:(mta2) queue:("+ZaMTA.A_ActiveQ+")")};
	mta2[ZaMTA.A_ActiveQ][ZaMTA.A_count]=101;
	mta2[ZaMTA.A_ActiveQ][ZaMTA.A_destination]=[
			new ZaMTAQSummaryItem(app, "yahoo.com", "yahoo.com", 233),
			new ZaMTAQSummaryItem(app, "gmail.com", "gmail.com", 203),			
			new ZaMTAQSummaryItem(app, "hotmail.com", "hotmail.com", 123),						
			new ZaMTAQSummaryItem(app, "usa.net", "usa.net", 50)									
		]
	mta2[ZaMTA.A_ActiveQ][ZaMTA.A_origin]=[
			new ZaMTAQSummaryItem(app, "64.23.45.222", "64.23.45.222", 233),
			new ZaMTAQSummaryItem(app, "221.23.45.26", "221.23.45.26", 203),			
			new ZaMTAQSummaryItem(app, "121.23.45.123", "121.23.45.123", 123),						
			new ZaMTAQSummaryItem(app, "220.63.45.201", "220.63.45.201", 50)		
		];	

	mta2[ZaMTA.A_CorruptQ] = {query:("mta:(mta2) queue:("+ZaMTA.A_CorruptQ+")")};
	mta2[ZaMTA.A_CorruptQ][ZaMTA.A_count]=2131;			
	mta2[ZaMTA.A_CorruptQ][ZaMTA.A_destination]=[
			new ZaMTAQSummaryItem(app, "yahoo.com", "yahoo.com", 233),
			new ZaMTAQSummaryItem(app, "gmail.com", "gmail.com", 203),			
			new ZaMTAQSummaryItem(app, "hotmail.com", "hotmail.com", 123),						
			new ZaMTAQSummaryItem(app, "usa.net", "usa.net", 50)									
		];
	mta2[ZaMTA.A_CorruptQ][ZaMTA.A_origin]=[
			new ZaMTAQSummaryItem(app, "64.23.45.222", "64.23.45.222", 233),
			new ZaMTAQSummaryItem(app, "221.23.45.26", "221.23.45.26", 203),			
			new ZaMTAQSummaryItem(app, "121.23.45.123", "121.23.45.123", 123),						
			new ZaMTAQSummaryItem(app, "220.63.45.201", "220.63.45.201", 50)		
		];	

	mta2[ZaMTA.A_HoldQ] = {query:("mta:(mta2) queue:("+ZaMTA.A_HoldQ+")")};
	mta2[ZaMTA.A_HoldQ][ZaMTA.A_count]=1603;
	mta2[ZaMTA.A_HoldQ][ZaMTA.A_destination]=[
			new ZaMTAQSummaryItem(app, "yahoo.com", "yahoo.com", 233),
			new ZaMTAQSummaryItem(app, "gmail.com", "gmail.com", 203),			
			new ZaMTAQSummaryItem(app, "hotmail.com", "hotmail.com", 123),						
			new ZaMTAQSummaryItem(app, "usa.net", "usa.net", 50)									
		];
	mta2[ZaMTA.A_HoldQ][ZaMTA.A_origin]=[
			new ZaMTAQSummaryItem(app, "64.23.45.222", "64.23.45.222", 233),
			new ZaMTAQSummaryItem(app, "221.23.45.26", "221.23.45.26", 203),			
			new ZaMTAQSummaryItem(app, "121.23.45.123", "121.23.45.123", 123),						
			new ZaMTAQSummaryItem(app, "220.63.45.201", "220.63.45.201", 50)		
		];
	list.add(mta1);
	list.add(mta2);
	return list;
}

ZaMTA.prototype.getMessages = function(app, queue, destination, origin, error,limit, offset,sortBy,sortAscending) {
	this[queue][ZaMTA.A_messages] = ZaSearch.searchMailQ(app, queue, destination, origin, error,limit, offset,sortBy,sortAscending);
}

PostQSummary_XModelItem = function (){}
XModelItemFactory.createItemType("_POSTQSUMMARY_", "postqsummary", PostQSummary_XModelItem);
PostQSummary_XModelItem.prototype.items = [
				{id:ZaMTA.A_destination, type:_LIST_, listItem:
					{type:_OBJECT_, 
						items: [
							{id:ZaMTA.A_name, type:_STRING_},
							{id:ZaMTA.A_count, type:_NUMBER_}
						]
					}
				},
				{id:ZaMTA.A_origin, type:_LIST_, listItem:
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
				{id:ZaMTA.A_count, type:_NUMBER_}	
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

/**
* send a MailQStatusRequest 
**/
ZaMTA.prototype.getMailQStatus = function (qName,query,offset,limit,force) {
	
	limit = (limit != null) ? limit: ZaMTA.RESULTSPERPAGE;
	
	var soapDoc = AjxSoapDoc.create("GetMailQueueRequest", "urn:zimbraAdmin", null);

	var serverEl = soapDoc.set("server", "");
	serverEl.setAttribute("name", this.name);		

	var qEl = soapDoc.getDoc().createElement("queue");
	qEl.setAttribute("name", qName);		
	
	if(force) {
		qEl.setAttribute("scan", 1);		
	}
		
	serverEl.appendChild(qEl);
	
	var queryEl = soapDoc.getDoc().createElement("query");
	if(query != null) {
		queryEl.appendChild(soapDoc.getDoc().createTextNode(query));
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
	var callback = new AjxCallback(this, this.mailQStatusCallback);	
	params.callback = callback;
	command.invoke(params);		
}

/**
* this method is called when the server returns MailQStatusResponse 
**/
ZaMTA.prototype.mailQStatusCallback = function () {
	//update my fields
	ZaMTA.makeTestData1(this);
	this._app.getMTAController().fireChangeEvent(this);
	//if status is "running" call getMailQStatus again
}

ZaMTAItem = function (app) {
	ZaItem.call(this, app,"ZaMTAItem");
	this._init(app);
}

ZaMTAProgress = function (app) {
	ZaItem.call(this,app,"ZaMTAProgress");
	this._init(app);
}

ZaMTA.initMethod = function (app) {
	this.attrs = new Object();
	this.id = "";
	this.name="";
	this[ZaItem.A_zimbraId] = "000"
	this[ZaMTA.A_Status] = ZaMsg.Idle;
	this[ZaMTA.A_DeferredQ] = {n:"N/A"};
	this[ZaMTA.A_IncomingQ] = {n:"N/A"};
	this[ZaMTA.A_ActiveQ] = {n:"N/A"};	
	this[ZaMTA.A_HoldQ] = {n:"N/A"};	
	this[ZaMTA.A_CorruptQ] = {n:"N/A"};		
		
	this[ZaMTA.A_DeferredQ][ZaMTA.A_refreshTime] = "N/A";
	this[ZaMTA.A_IncomingQ][ZaMTA.A_refreshTime] = "N/A";	
	this[ZaMTA.A_ActiveQ][ZaMTA.A_refreshTime] = "N/A";
	this[ZaMTA.A_HoldQ][ZaMTA.A_refreshTime] = "N/A";
	this[ZaMTA.A_CorruptQ][ZaMTA.A_refreshTime] = "N/A";	
}
ZaItem.initMethods["ZaMTA"].push(ZaMTA.initMethod);

ZaMTAQSummaryItem = function (app, d, t, n) {
	ZaItem.call(this, app,"ZaMTAQSummaryItem");
	this._init(app);
	if(d) 
		this[ZaMTAQSummaryItem.A_description] = d;
	if(t)
		this[ZaMTAQSummaryItem.A_text] = t;
	if(n)
		this[ZaMTAQSummaryItem.A_count] = t;

}


ZaMTAQSummaryItem.A_text = "t";
ZaMTAQSummaryItem.A_description = "d";
ZaMTAQSummaryItem.A_count = "n";

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
	if(!this[ZaMTAQSummaryItem.A_description])
		return null;
		
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = AjxStringUtil.htmlEncode(this[ZaMTAQSummaryItem.A_description]);
		this._toolTip = html.join("");
	}
	return this._toolTip;
}