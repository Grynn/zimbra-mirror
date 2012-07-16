/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
* @class ZaFlushCacheXDialog
* @contructor ZaFlushCacheXDialog
* @author Greg Solovyev
* @param parent
* @param w
* @param h
* @param title
**/
ZaFlushCacheXDialog = function(parent) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON];	
	ZaXDialog.call(this, parent,null, ZaMsg.FlushCacheDlgTitle, "480px", "380px","ZaFlushCacheXDialog");
	this._containedObject = {};
	this.initForm(ZaServer.volumeObjModel,this.getMyXForm());
    this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_servers/flushing_the_server_cache.htm?locid="+AjxEnv.DEFAULT_LOCALE;
}

ZaFlushCacheXDialog.prototype = new ZaXDialog;
ZaFlushCacheXDialog.prototype.supportMinimize = true;
ZaFlushCacheXDialog.prototype.constructor = ZaFlushCacheXDialog;

ZaFlushCacheXDialog.flushingServerModel = {
	type:_OBJECT_,
	items:[
		{id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/"+ZaItem.A_zimbraId},
		{id:"status", type:_NUMBER_,ref:"status"},
		{id:ZaServer.A_name, ref:"attrs/" + ZaServer.A_name, type:_STRING_},
		{id:ZaServer.A_ServiceHostname, ref:"attrs/"+ZaServer.A_ServiceHostname, type:_HOSTNAME_OR_IP_, maxLength:256},
		{id:ZaServer.A_zimbraMailProxyServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraMailProxyServiceEnabled, type:_ENUM_, choices:[false,true]},
		{id:ZaServer.A_zimbraMailboxServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraMailboxServiceInstalled, type:_ENUM_, choices:[false,true]}
	]
};

ZaFlushCacheXDialog.xModel = {
	items: [
		{id:"statusMessage", ref:"statusMessage", type:_STRING_},
		{id:"flushZimlet", ref:"flushZimlet", type: _ENUM_, choices: [false,true] },
		{id:"flushSkin", ref:"flushSkin", type: _ENUM_, choices: [false,true] },
		{id:"flushLocale", ref:"flushSkin", type: _ENUM_, choices: [false,true] },
		{id:"serverList", ref:"serverList", type:_LIST_,listItem:ZaFlushCacheXDialog.flushingServerModel},
		{id:"status", type:_NUMBER_,ref:"status"}

	]	
};

ZaFlushCacheXDialog.flushCacheCalback = 
function (params, resp) {
	if(params.busyId)
		ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);	
	
	var ix = params.ix;
	var newServerList = [];
	var serverList = this.getInstanceValue("serverList");
	newServerList._version = serverList._version+1;
	for(var i=0; i< serverList.length; i++) {
		newServerList.push(serverList[i]);
	}	
	if(resp.isException && resp.isException()) {
		this.getModel().setInstanceValue(this.getInstance(),"status",0);
		var msg = AjxMessageFormat.format (ZaMsg.ERROR_FAILED_FLUSH_CACHE,[params.serverList[params.ix].attrs[ZaServer.A_name]]);
		this.getModel().setInstanceValue(this.getInstance(),"statusMessage",msg);
		newServerList[ix].status = "error";		
		this.getModel().setInstanceValue(this.getInstance(),"serverList",newServerList);
		ZaApp.getInstance().getCurrentController()._handleException(resp.getException(),"ZaFlushCacheXDialog.flushCacheCalback", null, false);
	} else if(resp.getResponse() && resp.getResponse().Body && resp.getResponse().Body.BatchResponse && resp.getResponse().Body.BatchResponse.Fault) {
		this.getModel().setInstanceValue(this.getInstance(),"status",0);
		this.getModel().setInstanceValue(this.getInstance(),"statusMessage",msg);
		var fault = resp.getResponse().Body.BatchResponse.Fault;
		if(fault instanceof Array)
			fault = fault[0];
			
		if (fault) {
			// JS response with fault
			var ex = ZmCsfeCommand.faultToEx(fault);
			ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaFlushCacheXDialog.flushCacheCalback", null, false);
		}		
	} else {
		var busyid = Dwt.getNextId ();
		ix++;
		newServerList._version = serverList._version+1;
		newServerList[ix-1].status = "success";
		if(ix >= serverList.length) {
			this.getModel().setInstanceValue(this.getInstance(),"status",0);
			this.getModel().setInstanceValue(this.getInstance(),"statusMessage",ZaMsg.FinishedFlushCache);
			this.getModel().setInstanceValue(this.getInstance(),"serverList",newServerList);	
		} else {
			newServerList[ix].status = "processing";
			this.getModel().setInstanceValue(this.getInstance(),"serverList",newServerList);
			params.busyid = busyid;
			params.ix = ix;
			var callback = new AjxCallback(this, ZaFlushCacheXDialog.flushCacheCalback, params);
			params.callback = callback;
			this.getModel().setInstanceValue(this.getInstance(),"status",1);
			this.getModel().setInstanceValue(this.getInstance(),"statusMessage",AjxMessageFormat.format(ZaMsg.BUSY_FLUSH_CACHE_SERVER,[params.serverList[params.ix].attrs[ZaServer.A_name]]));
			ZaServer.flushCache(params);		
		}
	}
}
ZaFlushCacheXDialog.flushButtonListener = 
function () {
	var busyid = Dwt.getNextId ();
	var params = this.getInstance();
	params.ix = 0;
	params.busyId = busyid;
	var callback = new AjxCallback(this, ZaFlushCacheXDialog.flushCacheCalback, params);
	params.callback = callback;
	this.getModel().setInstanceValue(this.getInstance(),"status",1);
	this.getModel().setInstanceValue(this.getInstance(),"statusMessage",AjxMessageFormat.format(ZaMsg.BUSY_FLUSH_CACHE_SERVER,[params.serverList[params.ix].attrs[ZaServer.A_name]]));
	ZaServer.flushCache(params);
}

ZaFlushCacheXDialog.prototype.isFlushBtnEnabled = 
function () {
	return (
		(this.getInstanceValue("status") < 1) && (this.getInstanceValue("serverList").length > 0) && 
		(this.getInstanceValue("flushZimlet") || this.getInstanceValue("flushSkin") || this.getInstanceValue("flushLocale"))
	);
	
}

ZaFlushCacheXDialog.prototype.getMyXForm = 
function() {	
	var srvHeaderList = []; 
	var sortable=1;
	srvHeaderList[0] = new ZaListHeaderItem(ZaServer.A_ServiceHostname, ZaMsg.SLV_ServiceHName_col, null, "auto", sortable++, ZaServer.A_ServiceHostname, true, true);
	srvHeaderList[1] = new ZaListHeaderItem("status", ZaMsg.STV_Status_col, null, 40, sortable++, "status", true, true);
	
	var xFormObject = {
		numCols:1,
		items:[
			{type:_ZAWIZGROUP_, isTabGroup:true,
                colSizes:["200px", "275px"],
				items:[
					{ type: _DWT_ALERT_,
						  style: DwtAlert.WARNING,
						  iconVisible: true, 
						  content: ZaMsg.Alert_FlushCache,
						  align:_CENTER_, valign:_MIDDLE_,colSpan:2,width:"460px",
						  visibilityChecks:[]
					},
					{ type: _DWT_ALERT_,
						  style: DwtAlert.INFORMATION,
						  iconVisible: true, 
						  content: null,
						  ref:"statusMessage",
						  align:_CENTER_, valign:_MIDDLE_,colSpan:2,width:"460px",
						  visibilityChecks:[[XForm.checkInstanceValueNotEmty,"statusMessage"]],
						  visibilityChangeEventSources:["statusMessage"],bmolsnr:true
					},
					{ref:"serverList", type:_DWT_LIST_, labelLocation:_NONE_, label:null,  height:"120", width:"460",colSpan:2,
						headerList:srvHeaderList,align:_CENTER_,
						visibilityChecks:[],enableDisableChecks:[],widgetClass:ZaServerMiniListView,valueChangeEventSources:["serverList"]
					},
					{ref:"flushZimlet",
						type:_WIZ_CHECKBOX_, label:ZaMsg.Flush_zimlet_cache,
						trueValue:true, falseValue:false, visibilityChecks:[],enableDisableChecks:[]
					},
					{ref:"flushSkin",
						type:_WIZ_CHECKBOX_, label:ZaMsg.Flush_theme_cache,
						trueValue:true, falseValue:false, visibilityChecks:[],enableDisableChecks:[]
					},
					{ref:"flushLocale",
						type:_WIZ_CHECKBOX_, label:ZaMsg.Flush_locale_cache,
						trueValue:true, falseValue:false, visibilityChecks:[],enableDisableChecks:[]
					},					
					{type:_CELLSPACER_},
					{
						type:_DWT_BUTTON_, label:ZaMsg.FlushCacheBtn,width:"100px",
						onActivate:"ZaFlushCacheXDialog.flushButtonListener.call(this);",
						enableDisableChangeEventSources:["status","flushZimlet","flushSkin","flushLocale"],
						enableDisableChecks:[ZaFlushCacheXDialog.prototype.isFlushBtnEnabled]						
					}
				]
			}
		]
	};
	return xFormObject;
}
