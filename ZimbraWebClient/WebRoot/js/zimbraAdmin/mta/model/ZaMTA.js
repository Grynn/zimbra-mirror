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
ZaMTA.A_count = "count";
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
	//update my fields
	if(resp.Body && resp.GetMailQueueInfoResponse.server && resp.GetMailQueueInfoResponse.server[0]) {
		this.initFromJS(resp.Body.GetMailQueueInfoResponse.server[0]);
	} else {
		var ex = new ZmCsfeException(ZMsg.errorUnknownDoc,SVC_UNKNOWN_DOCUMENT,"ZaMTA.prototype.QCountsCallback");
		this._app.getCurrentController()._handleException(ex, "ZaMTA.prototype.QCountsCallback");
		this.goPrev();
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
			{id:"deferred_yahoo.com",name:"yahoo.com", count:131, toString:function() {return this.name+this.count} },
			{id:"deferred_gmail.com",name:"gmail.com", count:101, toString:function() {return this.name+this.count}},			
			{id:"deferred_hotmail.com",name:"hotmail.com", count:121, toString:function() {return this.name+this.count}},						
			{id:"deferred_usa.net",name:"usa.net", count:50, toString:function() {return this.name+this.count}}									
		];
	mta1[ZaMTA.A_DeferredQ][ZaMTA.A_origin]=[
			{id:"deferred_64.23.45.222",name:"64.23.45.222", count:231, toString:function() {return this.name+this.count}},
			{id:"deferred_221.23.45.26",name:"221.23.45.26", count:201, toString:function() {return this.name+this.count}},			
			{id:"deferred_121.23.45.123",name:"121.23.45.123", count:221, toString:function() {return this.name+this.count}},						
			{id:"deferred_220.63.45.201",name:"220.63.45.201", count:21, toString:function() {return this.name+this.count}}		
		];
	mta1[ZaMTA.A_DeferredQ][ZaMTA.A_error]=[
			{id:"deferred_blah-blah",name:"blah-blah", count:331, toString:function() {return this.name+this.count}},
			{id:"deferred_rant-rant",name:"rant-rant", count:301, toString:function() {return this.name+this.count}},			
			{id:"deferred_wait-wait",name:"wait-wait", count:321, toString:function() {return this.name+this.count}}
		];
	mta1[ZaMTA.A_DeferredQ][ZaMTA.A_count]=1001;

	mta1[ZaMTA.A_IncomingQ] = {query:("mta:(mta1) queue:("+ZaMTA.A_IncomingQ+")")};
	mta1[ZaMTA.A_IncomingQ][ZaMTA.A_count] = 1021;

	mta1[ZaMTA.A_IncomingQ][ZaMTA.A_destination] = [
			{id:"incoming_yahoo.com",name:"yahoo.com", count:132, toString:function() {return this.name+this.count}},
			{id:"incoming_gmail.com",name:"gmail.com", count:102, toString:function() {return this.name+this.count}},			
			{id:"incoming_hotmail.com",name:"hotmail.com",count:122, toString:function() {return this.name+this.count}},						
			{id:"incoming_usa.net",name:"usa.net", count:12}									
		];
	mta1[ZaMTA.A_IncomingQ][ZaMTA.A_origin]=[
			{id:"incoming_64.23.45.222",name:"64.23.45.222", count:232, toString:function() {return this.name+this.count}},
			{id:"incoming_221.23.45.26",name:"221.23.45.26", count:202, toString:function() {return this.name+this.count}},			
			{id:"incoming_121.23.45.123",name:"121.23.45.123", count:222, toString:function() {return this.name+this.count}},						
			{id:"incoming_220.63.45.201",name:"220.63.45.201", count:22, toString:function() {return this.name+this.count}}		
		];

	mta1[ZaMTA.A_ActiveQ] = {query:("mta:(mta1) queue:("+ZaMTA.A_ActiveQ+")")};
	mta1[ZaMTA.A_ActiveQ][ZaMTA.A_count]=101;
	mta1[ZaMTA.A_ActiveQ][ZaMTA.A_destination]=[
			{id:"yahoo.com",name:"yahoo.com", count:233, toString:function() {return this.name+this.count}},
			{id:"gmail.com",name:"gmail.com", count:203, toString:function() {return this.name+this.count}},			
			{id:"hotmail.com",name:"hotmail.com", count:123, toString:function() {return this.name+this.count}},						
			{id:"usa.net",name:"usa.net", count:50, toString:function() {return this.name+this.count}}									
		]
	mta1[ZaMTA.A_ActiveQ][ZaMTA.A_origin]=[
			{id:"64.23.45.222",name:"64.23.45.222", count:233, toString:function() {return this.name+this.count}},
			{id:"221.23.45.26",name:"221.23.45.26", count:203, toString:function() {return this.name+this.count}},			
			{id:"121.23.45.123",name:"121.23.45.123", count:123, toString:function() {return this.name+this.count}},						
			{id:"220.63.45.201",name:"220.63.45.201", count:50, toString:function() {return this.name+this.count}}		
		];	
	
	mta1[ZaMTA.A_CorruptQ] = {query:("mta:(mta1) queue:("+ZaMTA.A_CorruptQ+")")};
	mta1[ZaMTA.A_CorruptQ][ZaMTA.A_count]=2131;			
	mta1[ZaMTA.A_CorruptQ][ZaMTA.A_destination]=[
			{id:"yahoo.com",name:"yahoo.com", count:233, toString:function() {return this.name+this.count}},
			{id:"gmail.com",name:"gmail.com", count:203, toString:function() {return this.name+this.count}},			
			{id:"hotmail.com",name:"hotmail.com", count:123, toString:function() {return this.name+this.count}},						
			{id:"usa.net",name:"usa.net", count:50, toString:function() {return this.name+this.count}}									
		];
	mta1[ZaMTA.A_CorruptQ][ZaMTA.A_origin]=[
			{id:"64.23.45.222",name:"64.23.45.222", count:233, toString:function() {return this.name+this.count}},
			{id:"221.23.45.26",name:"221.23.45.26", count:203},			
			{id:"121.23.45.123",name:"121.23.45.123", count:123, toString:function() {return this.name+this.count}},						
			{id:"220.63.45.201",name:"220.63.45.201", count:50, toString:function() {return this.name+this.count}}		
		];	

	mta1[ZaMTA.A_HoldQ] = {query:("mta:(mta1) queue:("+ZaMTA.A_HoldQ+")")};
	mta1[ZaMTA.A_HoldQ][ZaMTA.A_count]=1603;
	mta1[ZaMTA.A_HoldQ][ZaMTA.A_destination]=[
			{id:"yahoo.com",name:"yahoo.com", count:233, toString:function() {return this.name+this.count}},
			{id:"gmail.com",name:"gmail.com", count:203, toString:function() {return this.name+this.count}},			
			{id:"hotmail.com",name:"hotmail.com", count:123, toString:function() {return this.name+this.count}},						
			{id:"usa.net",name:"usa.net", count:50, toString:function() {return this.name+this.count}}									
		];
	mta1[ZaMTA.A_HoldQ][ZaMTA.A_origin]=[
			{id:"64.23.45.222",name:"64.23.45.222", count:233, toString:function() {return this.name+this.count}},
			{id:"221.23.45.26",name:"221.23.45.26", count:203, toString:function() {return this.name+this.count}},			
			{id:"121.23.45.123",name:"121.23.45.123", count:123, toString:function() {return this.name+this.count}},						
			{id:"220.63.45.201",name:"220.63.45.201", count:50, toString:function() {return this.name+this.count}}		
		];	

	var mta2 = new ZaMTA(app);
	mta2[ZaMTA.A_Servername] = "gregsolo.liquidsys.com";	
	mta2[ZaMTA.A_LastError] = null;
	mta2[ZaMTA.A_MTAName] = "MTA2";
	mta2[ZaItem.A_zimbraId] = "mta2";
	mta2.id = "mta2";	
	
	mta2[ZaMTA.A_DeferredQ] = {query:("mta:(mta2) queue:("+ZaMTA.A_DeferredQ+")")};
	mta2[ZaMTA.A_DeferredQ][ZaMTA.A_destination]=[
			{id:"yahoo.com",name:"yahoo.com", count:233, toString:function() {return this.name+this.count}},
			{id:"gmail.com",name:"gmail.com", count:203, toString:function() {return this.name+this.count}},			
			{id:"hotmail.com",name:"hotmail.com", count:123, toString:function() {return this.name+this.count}},						
			{id:"usa.net",name:"usa.net", count:50, toString:function() {return this.name+this.count}}									
		];
	mta2[ZaMTA.A_DeferredQ][ZaMTA.A_origin]=[
			{id:"64.23.45.222",name:"64.23.45.222", count:233, toString:function() {return this.name+this.count}},
			{id:"221.23.45.26",name:"221.23.45.26", count:203, toString:function() {return this.name+this.count}},			
			{id:"121.23.45.123",name:"121.23.45.123", count:123, toString:function() {return this.name+this.count}},						
			{id:"220.63.45.201",name:"220.63.45.201", count:50, toString:function() {return this.name+this.count}}		
		];
	mta2[ZaMTA.A_DeferredQ][ZaMTA.A_error]=[
			{id:"blah-blah",name:"blah-blah", count:233, toString:function() {return this.name+this.count}},
			{id:"rant-rant",name:"rant-rant", count:203, toString:function() {return this.name+this.count}},			
			{id:"wait-wait",name:"wait-wait", count:123, toString:function() {return this.name+this.count}}
		];
	mta2[ZaMTA.A_DeferredQ][ZaMTA.A_count]=1001;

	mta2[ZaMTA.A_IncomingQ] = {query:("mta:(mta2) queue:("+ZaMTA.A_IncomingQ+")")};
	mta2[ZaMTA.A_IncomingQ][ZaMTA.A_count] = 1021;

	mta2[ZaMTA.A_IncomingQ][ZaMTA.A_destination] = [
			{id:"yahoo.com",name:"yahoo.com", count:233, toString:function() {return this.name+this.count}},
			{id:"gmail.com",name:"gmail.com", count:203, toString:function() {return this.name+this.count}},			
			{id:"hotmail.com",name:"hotmail.com",count:123, toString:function() {return this.name+this.count}},						
			{id:"usa.net",name:"usa.net", count:50, toString:function() {return this.name+this.count}}									
		];
	mta2[ZaMTA.A_IncomingQ][ZaMTA.A_origin]=[
			{id:"64.23.45.222",name:"64.23.45.222", count:233, toString:function() {return this.name+this.count}},
			{id:"221.23.45.26",name:"221.23.45.26", count:203, toString:function() {return this.name+this.count}},			
			{id:"121.23.45.123",name:"121.23.45.123", count:123, toString:function() {return this.name+this.count}},						
			{id:"220.63.45.201",name:"220.63.45.201", count:50, toString:function() {return this.name+this.count}}		
		];
	mta2[ZaMTA.A_IncomingQ][ZaMTA.A_error]=[
			{id:"blah-blah",name:"blah-blah", count:233, toString:function() {return this.name+this.count}},
			{id:"rant-rant",name:"rant-rant", count:203, toString:function() {return this.name+this.count}},			
			{id:"wait-wait",name:"wait-wait", count:123, toString:function() {return this.name+this.count}}
		];		

	mta2[ZaMTA.A_ActiveQ] = {query:("mta:(mta2) queue:("+ZaMTA.A_ActiveQ+")")};
	mta2[ZaMTA.A_ActiveQ][ZaMTA.A_count]=101;
	mta2[ZaMTA.A_ActiveQ][ZaMTA.A_destination]=[
			{id:"yahoo.com",name:"yahoo.com", count:233, toString:function() {return this.name+this.count}},
			{id:"gmail.com",name:"gmail.com", count:203, toString:function() {return this.name+this.count}},			
			{id:"hotmail.com",name:"hotmail.com", count:123, toString:function() {return this.name+this.count}},						
			{id:"usa.net",name:"usa.net", count:50}									
		]
	mta2[ZaMTA.A_ActiveQ][ZaMTA.A_origin]=[
			{id:"64.23.45.222",name:"64.23.45.222", count:233, toString:function() {return this.name+this.count}},
			{id:"221.23.45.26",name:"221.23.45.26", count:203, toString:function() {return this.name+this.count}},			
			{id:"121.23.45.123",name:"121.23.45.123", count:123, toString:function() {return this.name+this.count}},						
			{id:"220.63.45.201",name:"220.63.45.201", count:50, toString:function() {return this.name+this.count}}		
		];	

	mta2[ZaMTA.A_CorruptQ] = {query:("mta:(mta2) queue:("+ZaMTA.A_CorruptQ+")")};
	mta2[ZaMTA.A_CorruptQ][ZaMTA.A_count]=2131;			
	mta2[ZaMTA.A_CorruptQ][ZaMTA.A_destination]=[
			{id:"yahoo.com",name:"yahoo.com", count:233, toString:function() {return this.name+this.count}},
			{id:"gmail.com",name:"gmail.com", count:203, toString:function() {return this.name+this.count}},			
			{id:"hotmail.com",name:"hotmail.com", count:123, toString:function() {return this.name+this.count}},						
			{id:"usa.net",name:"usa.net", count:50, toString:function() {return this.name+this.count}}									
		];
	mta2[ZaMTA.A_CorruptQ][ZaMTA.A_origin]=[
			{id:"64.23.45.222",name:"64.23.45.222", count:233, toString:function() {return this.name+this.count}},
			{id:"221.23.45.26",name:"221.23.45.26", count:203, toString:function() {return this.name+this.count}},			
			{id:"121.23.45.123",name:"121.23.45.123", count:123, toString:function() {return this.name+this.count}},						
			{id:"220.63.45.201",name:"220.63.45.201", count:50, toString:function() {return this.name+this.count}}		
		];	

	mta2[ZaMTA.A_HoldQ] = {query:("mta:(mta2) queue:("+ZaMTA.A_HoldQ+")")};
	mta2[ZaMTA.A_HoldQ][ZaMTA.A_count]=1603;
	mta2[ZaMTA.A_HoldQ][ZaMTA.A_destination]=[
			{id:"yahoo.com",name:"yahoo.com", count:233, toString:function() {return this.name+this.count}},
			{id:"gmail.com",name:"gmail.com", count:203, toString:function() {return this.name+this.count}},			
			{id:"hotmail.com",name:"hotmail.com", count:123, toString:function() {return this.name+this.count}},						
			{id:"usa.net",name:"usa.net", count:50, toString:function() {return this.name+this.count}}									
		];
	mta2[ZaMTA.A_HoldQ][ZaMTA.A_origin]=[
			{id:"64.23.45.222",name:"64.23.45.222", count:233, toString:function() {return this.name+this.count}},
			{id:"221.23.45.26",name:"221.23.45.26", count:203, toString:function() {return this.name+this.count}},			
			{id:"121.23.45.123",name:"121.23.45.123", count:123, toString:function() {return this.name+this.count}},						
			{id:"220.63.45.201",name:"220.63.45.201", count:50, toString:function() {return this.name+this.count}}		
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
ZaMTA.prototype.getMailQStatus = function () {
}

/**
* this method is called when the server returns MailQStatusResponse 
**/
ZaMTA.prototype.mailQStatusCallback = function () {
	//update my fields

	this._app.getMTAController().fireChangeEvent();
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
	this[ZaMTA.A_DeferredQ] = {count:"N/A"};
	this[ZaMTA.A_IncomingQ] = {count:"N/A"};
	this[ZaMTA.A_ActiveQ] = {count:"N/A"};	
	this[ZaMTA.A_HoldQ] = {count:"N/A"};	
	this[ZaMTA.A_CorruptQ] = {count:"N/A"};			
}
ZaItem.initMethods["ZaMTA"].push(ZaMTA.initMethod);