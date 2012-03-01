/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

ZaAlias = function() {
	ZaItem.call(this);
	this.attrs = new Object();
	this.id = "";
	this.name="";
	this.type=ZaItem.ALIAS;
}

ZaAlias.prototype = new ZaItem;
ZaAlias.prototype.constructor = ZaAlias;
ZaAlias.A_AliasTargetId = "zimbraAliasTargetId";
ZaAlias.A_targetAccount = "targetName";
ZaAlias.A_targetType = "type";
ZaAlias.A_index = "index";
ZaAlias.A_uid = "uid";

ZaAlias.TARGET_TYPE_DL = ZaItem.DL ;
ZaAlias.TARGET_TYPE_ACCOUNT = ZaItem.ACCOUNT ;
ZaAlias.TARGET_TYPE_RESOURCE = ZaItem.RESOURCE;

ZaItem._ATTR[ZaAlias.A_targetAccount] = ZaMsg.attrDesc_aliasFor;

ZaAlias.searchAttributes = AjxBuffer.concat(ZaAlias.A_AliasTargetId,",",
											   ZaItem.A_zimbraId,  "," , 
											   ZaAlias.A_targetAccount, "," , 
											   ZaAlias.A_uid,"," , 
											   ZaAlias.A_targetType, "," , 
											   ZaAccount.A_description);
											   
ZaAlias.prototype.remove = 
function(callback) {
	var soapCmd  ;
	
	switch(this.attrs[ZaAlias.A_targetType]) {
		case ZaAlias.TARGET_TYPE_ACCOUNT: soapCmd = "RemoveAccountAliasRequest" ; break ;
		case ZaAlias.TARGET_TYPE_DL  : soapCmd = "RemoveDistributionListAliasRequest" ; break ;
		case ZaAlias.TARGET_TYPE_RESOURCE : soapCmd = "RemoveAccountAliasRequest" ; break ;
		default: throw new Error("Can't add alias for account type: " + this.attrs[ZaAlias.A_targetType]) ;				
	}
	
	var soapDoc = AjxSoapDoc.create(soapCmd, ZaZimbraAdmin.URN, null);
	
	soapDoc.set("id", this.attrs[ZaAlias.A_AliasTargetId]);
	soapDoc.set("alias", this.name);
	this.deleteCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	params.noAuthToken = true;	
	if(callback) {
		params.asyncMode = true;
		params.callback = callback;
	}
	this.deleteCommand.invoke(params);		
}

/**
* Returns HTML for a tool tip for this account.
*/
ZaAlias.prototype.getToolTip =
function() {
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0'>";
		html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
		html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap; overflow:hidden;width:350' >";
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' style='width:100%;'>";
		html[idx++] = "<tr valign='center'>";
		html[idx++] = "<td><b>" + AjxStringUtil.htmlEncode(this.name) + "</b></td>";
		html[idx++] = "<td align='right'>";
		html[idx++] = AjxImg.getImageHtml("AccountAlias");		
		html[idx++] = "</td>";
		html[idx++] = "</table></div></td></tr>";
		html[idx++] = "<tr></tr>";
		//get my account
//		var account = ZaApp.getInstance().getAccountList().getItemById(this.attrs[ZaAlias.A_AliasTargetId]);
		var target = this.getAliasTargetObj();
		if(target && (this.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_ACCOUNT)) {
			idx = this._addRow(ZaItem._attrDesc(ZaAlias.A_targetAccount), 
						target.attrs[ZaAccount.A_displayname], html, idx);
		
			idx = this._addRow(ZaMsg.NAD_AccountStatus, 
						ZaAccount._accountStatus(target.attrs[ZaAccount.A_accountStatus]), html, idx);		
			
			if(target.getAttrs[ZaAccount.A_mailHost]) {
				idx = this._addRow(ZaMsg.NAD_MailServer, 
				target.attrs[ZaAccount.A_mailHost], html, idx);
			}			
		}else if (target && (this.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_DL)){
			idx = this._addRow(ZaItem._attrDesc(ZaAlias.A_targetAccount), 
						target.attrs[ZaAccount.A_displayname], html, idx);
		
			idx = this._addRow(ZaMsg.NAD_AccountStatus, 
						ZaDistributionList.getDLStatus(target.attrs[ZaDistributionList.A_mailStatus]), html, idx);		
			
		}else if (target && (this.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_RESOURCE)){
			idx = this._addRow(ZaItem._attrDesc(ZaAlias.A_targetAccount),
                                                target.attrs[ZaAccount.A_displayname], html, idx);

			idx = this._addRow(ZaMsg.NAD_AccountStatus,
                                                ZaResource.getAccountStatusLabel(target.attrs[ZaResource.A_accountStatus]), html, idx);
			if(target.getAttrs && target.getAttrs[ZaResource.A_mailHost]) {
				idx = this._addRow(ZaMsg.NAD_MailServer, target.attrs[ZaResource.A_mailHost], html, idx);
			}	
		}
		idx = this._addAttrRow(ZaItem.A_zimbraId, html, idx);
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}

ZaAlias.myXModel = { 
	items: [
	    {id:"getAttrs",type:_LIST_},
    	{id:"setAttrs",type:_LIST_},
    	{id:"rights",type:_LIST_},
		{id:ZaAccount.A_name, type:_STRING_, ref:"name", 
			constraints: {type:"method", value:
			   function (value, form, formItem, instance) {				   
				   if (value){
					  	if(AjxUtil.isValidEmailNonReg(value)) {
						   return value;
					   } else {
						   throw ZaMsg.ErrorInvalidEmailAddress;
					   }
				   }
			   }
			}
		},
		{id:ZaAlias.A_AliasTargetId, type:_STRING_, ref:ZaAlias.A_AliasTargetId},
		{id:ZaAlias.A_targetType, type:_STRING_, ref:ZaAlias.A_targetType},
		{id:ZaAlias.A_targetAccount, ref:ZaAlias.A_targetAccount},
		{id:ZaAlias.A_index, type:_NUMBER_, ref:ZaAlias.A_index}
	]
}

ZaAlias.prototype.addAlias = 
function (form) {
	
	var instance = form.getInstance() ;
	var newAlias = instance [ZaAccount.A_name] ;
	var targetName = instance [ZaAlias.A_targetAccount] ;
	
	try {
		var targetObj ;
		var targetType = ZaAlias.TARGET_TYPE_ACCOUNT ;
		
		try {
			targetObj = ZaAlias.getTargetByName( targetName, targetType) ;
		}catch (ex) {
			if (ex.code == ZmCsfeException.ACCT_NO_SUCH_ACCOUNT) {
				//the target is Distribution List
				targetType =  ZaAlias.TARGET_TYPE_DL ;
				targetObj = ZaAlias.getTargetByName(targetName, targetType) ;
			}else{
				throw ex ;
			}
		}
		
		targetObj.addAlias ( newAlias ) ;
        this.targetObj = targetObj;
		//TODO Need to refresh the alias list view.
		ZaApp.getInstance().getAccountViewController(true).fireCreationEvent(this);
		form.parent.popdown();
        ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.AliasCreated,[newAlias]));
	} catch (ex) {
		if(ex.code == ZmCsfeException.ACCT_EXISTS ) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.WARNING_ALIAS_EXISTS, [newAlias]) 
					+ "<BR />" + ex.msg );
		} else if (ex.code == ZmCsfeException.NO_SUCH_DISTRIBUTION_LIST || ex.code == ZmCsfeException.ACCT_NO_SUCH_ACCOUNT){
			ZaApp.getInstance().getCurrentController().popupErrorDialog(
				AjxMessageFormat.format(ZaMsg.WARNING_ALIASES_TARGET_NON_EXIST,[targetName]));
		}else{
			//if failed for another reason - jump out
			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaAlias.prototype.addAlias", null, false);
		}
	}
}

/**
 * Use this method when creating alias using the popup dialog
 * val: target account/dl name
 * targetType: account/dl
 */
ZaAlias.getTargetByName =
function (val, targetType) {
	var soapDoc ;
	var elBy ;
	
	if (targetType == ZaAlias.TARGET_TYPE_DL) {
		soapDoc = AjxSoapDoc.create("GetDistributionListRequest", ZaZimbraAdmin.URN, null);
		elBy = soapDoc.set("dl", val);
	}else if (targetType == ZaAlias.TARGET_TYPE_ACCOUNT) {
		soapDoc = AjxSoapDoc.create("GetAccountRequest", ZaZimbraAdmin.URN, null);
		elBy = soapDoc.set("account", val);
	}else {
		throw new Error ("Alias type " + targetType + " is not valid.") ;
	}
	
	soapDoc.getMethod().setAttribute("applyCos", "0");		
	elBy.setAttribute("by", "name");

	//var getAccCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller: ZaApp.getInstance().getCurrentController()
	}
	var respBody = ZaRequestMgr.invoke(params, reqMgrParams).Body ;
	var resp ;
	var targetObj ; 
	
	if (targetType == ZaAlias.TARGET_TYPE_DL) {
		resp = respBody.GetDistributionListResponse.dl[0] ;
		targetObj = new ZaDistributionList() ;
	}else if (targetType == ZaAlias.TARGET_TYPE_ACCOUNT) {
		resp = respBody.GetAccountResponse.account[0];
		targetObj = new ZaAccount() ;
	}

	targetObj.attrs = new Object();
	targetObj.initFromJS(resp);
	
	return targetObj ;
}

/*
 * use this method when the alias obj exists
 */
ZaAlias.prototype.getAliasTargetObj =
function () {
	var targetObj;
	var targetType = this.attrs[ZaAlias.A_targetType] ;
	var targetName = this.attrs[ZaAlias.A_targetAccount] ;
	var targetId = this.attrs[ZaAlias.A_AliasTargetId] ;
	
	if (targetType == ZaAlias.TARGET_TYPE_DL) {
		targetObj = new ZaDistributionList(targetId, targetName) ;
	}else if (targetType == ZaAlias.TARGET_TYPE_ACCOUNT) {
		targetObj = new ZaAccount() ;
	}else if (targetType == ZaAlias.TARGET_TYPE_RESOURCE) {
		targetObj = new ZaResource();
	}else {
		throw new Error ("Alias type " + targetType + " is not valid.") ;
	}

	targetObj.load("name", targetName, false, true);
	
	return targetObj ;
}

ZaAlias.prototype.initEffectiveRightsFromJS = function(resp) {
	if(!this.targetObj) {
		var targetType = this.attrs[ZaAlias.A_targetType] ;
		var targetName = this.attrs[ZaAlias.A_targetAccount] ;
		var targetId = this.attrs[ZaAlias.A_AliasTargetId] ;

		if (targetType == ZaAlias.TARGET_TYPE_DL) {
			this.targetObj = new ZaDistributionList(targetId, targetName) ;
		} else if (targetType == ZaAlias.TARGET_TYPE_ACCOUNT) {
			this.targetObj = new ZaAccount();
			this.targetObj.id = targetId;
			this.targetObj.name = targetName;
			if(!this.targetObj.attrs)
				this.targetObj.attrs = {};
			this.targetObj.attrs[ZaItem.A_cn] = targetName;
			this.targetObj.attrs[ZaItem.A_zimbraId] = targetId;
		}
	}
	this.targetObj.initEffectiveRightsFromJS(resp);
}
