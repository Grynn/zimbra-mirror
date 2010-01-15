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
* @class ZaPublishShareXDialog
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaPublishShareXDialog = function(parent, w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent,null, title, w, h,"ZaPublishShareXDialog");
	this.initForm(ZaDistributionList.myXModel,this.getMyXForm());
	this._containedObject = {};
}

ZaPublishShareXDialog.prototype = new ZaXDialog;
ZaPublishShareXDialog.prototype.constructor = ZaPublishShareXDialog;
ZaXDialog.XFormModifiers["ZaPublishShareXDialog"] = new Array();

/**
* sets the object contained in the view
**/
ZaPublishShareXDialog.prototype.setObject =
function(entry) {
	this._containedObject = {attrs:{}};
	
	if(entry.rights)
		this._containedObject.rights = entry.rights;

	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
		
	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type;
	this._containedObject.id = entry.id;	
	this._containedObject[ZaDistributionList.A2_share_selection_cache] = new Array();
	this._containedObject[ZaDistributionList.A2_share_selection_cache]._version = 1;
	this._containedObject[ZaDistributionList.A2_sharesPool] = new Array();
	this._containedObject[ZaDistributionList.A2_sharesPool]._version = 1;	
	this._localXForm.setInstance(this._containedObject);
}

ZaPublishShareXDialog.publishSelectedButtonListener = function () {
	var form = this.getForm();
	var dl = this.getInstance();
	var shares = this.getInstanceValue(ZaDistributionList.A2_share_selection_cache);
	ZaDistributionList.publishShare.call(dl,shares,false, new AjxCallback(form,ZaPublishShareXDialog.publishShareCallback));		
}

ZaPublishShareXDialog.publishAllButtonListener = function () {
	var form = this.getForm();
	var dl = this.getInstance();
	var shares = this.getInstanceValue(ZaDistributionList.A2_sharesPool);
	ZaDistributionList.publishShare.call(dl,shares,false,new AjxCallback(form,ZaPublishShareXDialog.publishShareCallback));		
}

ZaPublishShareXDialog.publishShareCallback = function (respObj) {
	if(respObj.isException && respObj.isException()) {
		ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(),"ZaPublishShareXDialog.publishShareCallback", null, false);
	} else if(respObj.getResponse().Body.BatchResponse.Fault) {
		var fault = respObj.getResponse().Body.BatchResponse.Fault;
		if(fault instanceof Array)
			fault = fault[0];
					
		if (fault) {
			// JS response with fault
			var ex = ZmCsfeCommand.faultToEx(fault);
			ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaDLXFormView.unpublishShareCallback", null, false);
		}
	}
	var dl = this.getInstance();
	var owner = this.getModel().getInstanceValue(dl,ZaDistributionList.A2_sharesOwner);
	var oldList = this.getModel().getInstanceValue(dl,ZaDistributionList.A2_sharesPool);
	var list = ZaDistributionList.getUnpublishedShares.call(dl,"name", owner);
	if(!list) {
		list = new Array();
	}
	list._version = oldList ? oldList._version+1 : 2;
	this.getModel().setInstanceValue(dl,ZaDistributionList.A2_sharesPool,list);
	
	var newSelectionCache = new Array();
	var oldSelectionCache = this.getModel().getInstanceValue(dl,ZaDistributionList.A2_share_selection_cache);
	if(oldSelectionCache)
		newSelectionCache._version = oldSelectionCache._version+1;
	this.getModel().setInstanceValue(dl,ZaDistributionList.A2_share_selection_cache,newSelectionCache);
}

ZaPublishShareXDialog.findSharesButtonListener = function () {
	var dl = this.getInstance();
	var owner = this.getInstanceValue(ZaDistributionList.A2_sharesOwner);
	var list = ZaDistributionList.getUnpublishedShares.call(dl,"name", owner);
	var oldList = this.getInstanceValue(ZaDistributionList.A2_sharesPool);
	if(!list) {
		list = new Array();
	}
	list._version = oldList ? oldList._version+1 : 2;
	this.getModel().setInstanceValue(dl,ZaDistributionList.A2_sharesPool,list);
}

ZaPublishShareXDialog.shareSelectionListener = function () {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_share_selection_cache, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_share_selection_cache, null);
	}
}

ZaPublishShareXDialog.myXFormModifier = function(xFormObject) {
	var shareHeaderList = new Array();
	shareHeaderList[0] = new ZaListHeaderItem(ZaShare.A_folderPath, ZaMsg.Shares_FolderPath, null, "100px", null, null, false, true);
	shareHeaderList[1] = new ZaListHeaderItem(ZaShare.A_ownerName, ZaMsg.Shares_OwnerName, null, "106px", null, null, false, true);
	shareHeaderList[2] = new ZaListHeaderItem(ZaShare.A_granteeName, ZaMsg.Shares_GranteeName, null, "106px", null, null, false, true);

	xFormObject.items = [
		{type:_SPACER_,  align:_CENTER_, valign:_TOP_,colSpan:"*"},		
		{type:_GROUP_, colSizes:["200px","200px","100px"],numCols:3,colSpan:"*", items:[
			{ref:ZaDistributionList.A2_sharesOwner, type:_DYNSELECT_,editable:true,width:"200px", inputSize:30, label:ZaMsg.Shares_OwnersEmailAddress,
				dataFetcherClass:ZaSearch,
				toolTipContent:ZaMsg.tt_StartTypingAccountName,
				dataFetcherTypes:[ZaSearch.ACCOUNTS],
				dataFetcherAttrs:[ZaItem.A_zimbraId, ZaItem.A_cn, ZaAccount.A_name, ZaAccount.A_displayname, ZaAccount.A_mail],
				dataFetcherMethod:ZaSearch.prototype.dynSelectSearch,visibilityChecks:[],enableDisableChecks:[]
			},
			{type:_DWT_BUTTON_, label:ZaMsg.Shares_FindShares, width:80, 
			   enableDisableChangeEventSources:[ZaDistributionList.A2_sharesOwner],
			   enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_sharesOwner]],
			   onActivate:"ZaPublishShareXDialog.findSharesButtonListener.call(this,event)"
			}
		]},		
		{type:_SPACER_, height:"5px",colSpan:"*"},	
		{ref:ZaDistributionList.A2_sharesPool,colSpan:"*", 
    		type:_DWT_LIST_, height:"150", width:"500", cssClass: "DLSource",onSelection:ZaPublishShareXDialog.shareSelectionListener,
		 		multiselect:true, bmolsnr:true, widgetClass:ZaSharesListView, headerList:shareHeaderList, visibilityChecks:[], enableDisableChecks:[],emptyText:ZaMsg.Shares_DLNoUnpublishedResults
		},
		{type:_SPACER_, height:"5px",colSpan:"*"},
	   	{type:_GROUP_, width:"98%", numCols:4, colSizes:[200,5, 200,"auto"],items:[
	   		{type:_DWT_BUTTON_, label:ZaMsg.Shares_PublishSelected, width:200, 
		   		enableDisableChangeEventSources:[ZaDistributionList.A2_share_selection_cache],
		   		enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_share_selection_cache]],
		  	 	onActivate:"ZaPublishShareXDialog.publishSelectedButtonListener.call(this,event)"
			},
			{type:_CELLSPACER_},
			{type:_DWT_BUTTON_, label:ZaMsg.Shares_PublishAll, width:200, 
		   		enableDisableChangeEventSources:[ZaDistributionList.A2_sharesPool],
		   		enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_sharesPool]],
		   		onActivate:"ZaPublishShareXDialog.publishAllButtonListener.call(this,event)"
			},
			{type:_CELLSPACER_}
		]}	
	];
}
ZaXDialog.XFormModifiers["ZaPublishShareXDialog"].push(ZaPublishShareXDialog.myXFormModifier);


