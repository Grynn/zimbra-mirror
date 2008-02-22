/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

ZaAlias = function(app) {
	ZaItem.call(this, app);
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

ZaAlias.TARGET_TYPE_DL = "distributionlist" ;
ZaAlias.TARGET_TYPE_ACCOUNT = "account" ;

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
		default: throw new Error("Can't add alias for account type: " + this.attrs[ZaAlias.A_targetType]) ;				
	}
	
	var soapDoc = AjxSoapDoc.create(soapCmd, ZaZimbraAdmin.URN, null);
	
	soapDoc.set("id", this.attrs[ZaAlias.A_AliasTargetId]);
	soapDoc.set("alias", this.name);
	this.deleteCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
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
//		var account = this._app.getAccountList().getItemById(this.attrs[ZaAlias.A_AliasTargetId]);
		var target = this.getAliasTargetObj();
		if(target && (this.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_ACCOUNT)) {
			idx = this._addRow(ZaItem._attrDesc(ZaAlias.A_targetAccount), 
						target.attrs[ZaAccount.A_displayname], html, idx);
		
			idx = this._addRow(ZaMsg.NAD_AccountStatus, 
						ZaAccount._accountStatus(target.attrs[ZaAccount.A_accountStatus]), html, idx);		
			
			if(ZaSettings.SERVERS_ENABLED) {
				idx = this._addRow(ZaMsg.NAD_MailServer, 
				target.attrs[ZaAccount.A_mailHost], html, idx);
			}			
		}else if (target && (this.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_DL)){
			idx = this._addRow(ZaItem._attrDesc(ZaAlias.A_targetAccount), 
						target.attrs[ZaAccount.A_displayname], html, idx);
		
			idx = this._addRow(ZaMsg.NAD_AccountStatus, 
						ZaDistributionList._dlStatus[target.attrs[ZaDistributionList.A_mailStatus]], html, idx);		
			
		}
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}

ZaAlias.myXModel = { 
	items: [
		{id:ZaAccount.A_name, type:_STRING_, ref:"name"},
		{id:ZaAlias.A_AliasTargetId, type:_STRING_, ref:ZaAlias.A_AliasTargetId},
		{id:ZaAlias.A_targetType, type:_STRING_, ref:ZaAlias.A_targetType},
		{id:ZaAlias.A_targetAccount, type:_STRING_, ref:ZaAlias.A_targetAccount},		
		{id:ZaAlias.A_index, type:_NUMBER_, ref:ZaAlias.A_index}
	]
}

ZaAlias.prototype.addAlias = 
function (form) {
	var app = form.parent._app ;
	var instance = form.getInstance() ;
	var newAlias = instance [ZaAccount.A_name] ;
	var targetName = instance [ZaAlias.A_targetAccount] ;
	
	try {
		var targetObj ;
		var targetType = ZaAlias.TARGET_TYPE_ACCOUNT ;
		
		try {
			targetObj = ZaAlias.getTargetByName(app, targetName, targetType) ;
		}catch (ex) {
			if (ex.code == ZmCsfeException.ACCT_NO_SUCH_ACCOUNT) {
				//the target is Distribution List
				targetType =  ZaAlias.TARGET_TYPE_DL ;
				targetObj = ZaAlias.getTargetByName(app, targetName, targetType) ;
			}else{
				throw ex ;
			}
		}
		
		targetObj.addAlias ( newAlias ) ;  
		//TODO Need to refresh the alias list view.
		this._app.getAccountViewController(true).fireCreationEvent(this);
		form.parent.popdown();
	} catch (ex) {
		if(ex.code == ZmCsfeException.ACCT_EXISTS ) {
			app.getCurrentController().popupErrorDialog(ZaMsg.WARNING_ALIAS_EXISTS + " " + newAlias 
					+ "<BR />" + ex.msg );
		} else if (ex.code == ZmCsfeException.NO_SUCH_DISTRIBUTION_LIST || ex.code == ZmCsfeException.ACCT_NO_SUCH_ACCOUNT){
			app.getCurrentController().popupErrorDialog(
				AjxMessageFormat.format(ZaMsg.WARNING_ALIASES_TARGET_NON_EXIST,[targetName]));
		}else{
			//if failed for another reason - jump out
			app.getCurrentController()._handleException(ex, "ZaAlias.prototype.addAlias", null, false);
		}
	}
}

/**
 * Use this method when creating alias using the popup dialog
 * val: target account/dl name
 * targetType: account/dl
 */
ZaAlias.getTargetByName =
function (app, val, targetType) {
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
		controller: app.getCurrentController ()
	}
	var respBody = ZaRequestMgr.invoke(params, reqMgrParams).Body ;
	var resp ;
	var targetObj ; 
	
	if (targetType == ZaAlias.TARGET_TYPE_DL) {
		resp = respBody.GetDistributionListResponse.dl[0] ;
		targetObj = new ZaDistributionList(app) ;
	}else if (targetType == ZaAlias.TARGET_TYPE_ACCOUNT) {
		resp = respBody.GetAccountResponse.account[0];
		targetObj = new ZaAccount(app) ;
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
	var targetObj ; 
	var targetType = this.attrs[ZaAlias.A_targetType] ;
	var targetName = this.attrs[ZaAlias.A_targetAccount] ;
	var targetId = this.attrs[ZaAlias.A_AliasTargetId] ;
	
	if (targetType == ZaAlias.TARGET_TYPE_DL) {
		targetObj = new ZaDistributionList(this._app, targetId, targetName) ;
	}else if (targetType == ZaAlias.TARGET_TYPE_ACCOUNT) {
		targetObj = new ZaAccount(this._app) ;
	}else {
		throw new Error ("Alias type " + targetType + " is not valid.") ;
	}

	targetObj.load("name", targetName, (!ZaSettings.COSES_ENABLED));
	
	return targetObj ;
}
