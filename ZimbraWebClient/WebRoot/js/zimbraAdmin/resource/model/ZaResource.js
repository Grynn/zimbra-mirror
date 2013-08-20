/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaResource
* @contructor ZaResource
* @param ZaApp app
* this class is a model for zimbra calendar resource account 
* @author Charles Cao
**/
ZaResource = function() {
	ZaItem.call(this, "ZaResource");
	this._init();
	this.type=ZaItem.RESOURCE;
}

ZaResource.prototype = new ZaAccount;
ZaResource.prototype.constructor = ZaResource;

ZaItem.loadMethods["ZaResource"] = new Array();
ZaItem.initMethods["ZaResource"] = new Array();
ZaItem.modifyMethods["ZaResource"] = new Array();
ZaItem.createMethods["ZaResource"] = new Array();

//object attributes
ZaResource.A_name = ZaAccount.A_name;
//ZaResource.A_resourceName = "resourceName";
ZaResource.A_uid = ZaAccount.A_uid;
ZaResource.A_accountName = ZaAccount.A_accountName; 
ZaResource.A_mail = ZaAccount.A_mail;
ZaResource.A_description = ZaAccount.A_description;
ZaResource.A_displayname = ZaAccount.A_displayname;
ZaResource.A_country = ZaAccount.A_country; //country
ZaResource.A_street = ZaAccount.A_street;
ZaResource.A_city = ZaAccount.A_city;
ZaResource.A_zip = ZaAccount.A_zip;
ZaResource.A_state = ZaAccount.A_state;
ZaResource.A_password  = ZaAccount.A_password;
ZaResource.A2_confirmPassword = ZaAccount.A2_confirmPassword;
//ZaResource.A_mailDeliveryAddress = ZaAccount.A_mailDeliveryAddress;
ZaResource.A_accountStatus = ZaAccount.A_accountStatus;
ZaResource.A_notes = ZaAccount.A_notes;
ZaResource.A_mailHost = ZaAccount.A_mailHost;
ZaResource.A_COSId = ZaAccount.A_COSId;
ZaResource.A_zimbraMinPwdLength = ZaAccount.A_zimbraMinPwdLength;
ZaResource.A_zimbraMaxPwdLength = ZaAccount.A_zimbraMaxPwdLength;

ZaResource.A_zimbraPrefCalendarForwardInvitesTo = "zimbraPrefCalendarForwardInvitesTo";
ZaResource.A_zimbraCalResMaxNumConflictsAllowed = "zimbraCalResMaxNumConflictsAllowed";
ZaResource.A_zimbraCalResMaxPercentConflictsAllowed = "zimbraCalResMaxPercentConflictsAllowed";
ZaResource.A_locationDisplayName = "zimbraCalResLocationDisplayName";
ZaResource.A_zimbraAccountCalendarUserType = "zimbraAccountCalendarUserType";
ZaResource.A_zimbraCalResAlwaysFree = "zimbraCalResAlwaysFree";
ZaResource.A_zimbraCalResAutoAcceptDecline = "zimbraCalResAutoAcceptDecline";
ZaResource.A_zimbraCalResAutoDeclineIfBusy = "zimbraCalResAutoDeclineIfBusy";
ZaResource.A_zimbraCalResAutoDeclineRecurring = "zimbraCalResAutoDeclineRecurring";
ZaResource.A_zimbraCalResBuilding = "zimbraCalResBuilding";
ZaResource.A_zimbraCalResCapacity = "zimbraCalResCapacity";
ZaResource.A_zimbraCalResContactEmail = "zimbraCalResContactEmail";
ZaResource.A_zimbraCalResContactName = "zimbraCalResContactName";
ZaResource.A_zimbraCalResContactPhone = "zimbraCalResContactPhone";
ZaResource.A_zimbraCalResFloor = "zimbraCalResFloor";
ZaResource.A_zimbraCalResRoom = "zimbraCalResRoom";
ZaResource.A_zimbraCalResSite = "zimbraCalResSite";
ZaResource.A_zimbraCalResType = "zimbraCalResType";
ZaResource.A_contactInfoAutoComplete = "contactInfoAutoComplete";
ZaResource.A_zimbraMailForwardingAddressMaxLength = "zimbraMailForwardingAddressMaxLength";
ZaResource.A_zimbraMailForwardingAddressMaxNumAddrs = "zimbraMailForwardingAddressMaxNumAddrs";

ZaResource.ACCOUNT_STATUS_ACTIVE = "active";
ZaResource.ACCOUNT_STATUS_MAINTENANCE = "maintenance";
ZaResource.ACCOUNT_STATUS_LOCKED = "locked";
ZaResource.ACCOUNT_STATUS_CLOSED = "closed";

ZaResource.RESOURCE_TYPE_LOCATION = "Location";
ZaResource.RESOURCE_TYPE_EQUIPMENT = "Equipment";

ZaResource.SCHEDULE_POLICY_TT = "scheduleTT";
ZaResource.SCHEDULE_POLICY_FT = "scheduleFT";
ZaResource.SCHEDULE_POLICY_TF = "scheduleTF";
ZaResource.SCHEDULE_POLICY_FF = "scheduleFF";

ZaResource.A_zimbraPrefCalendarAutoAcceptSignatureId = "zimbraPrefCalendarAutoAcceptSignatureId";
ZaResource.A_zimbraPrefCalendarAutoDeclineSignatureId = "zimbraPrefCalendarAutoDeclineSignatureId";
ZaResource.A_zimbraPrefCalendarAutoDenySignatureId = "zimbraPrefCalendarAutoDenySignatureId";
ZaResource.A2_signatureList = "signatureList";
ZaResource.A2_signature_selection_cache = "signatureSelectionCache";

//this attributes are not used in the XML object, but is used in the model
ZaResource.A2_schedulePolicy = "schedulePolicy";
ZaResource.A2_autodisplayname = "autodisplayname";
ZaResource.A2_autoMailServer = "automailserver";
ZaResource.A2_autoCos = "autoCos";
ZaResource.A2_autoLocationName = "autolocationname";
ZaResource.A2_myCOS = "mycos";
ZaResource.A2_calFwdAddr_selection_cache = "calFwdAddr_selection_cache";

ZaResource.MAXSEARCHRESULTS = ZaSettings.MAXSEARCHRESULTS;
ZaResource.RESULTSPERPAGE = ZaSettings.RESULTSPERPAGE;

ZaResource.searchAttributes = AjxBuffer.concat(ZaResource.A_displayname,",",
											   ZaItem.A_zimbraId,  "," , 
											   ZaResource.A_mailHost , "," , 
											   ZaResource.A_uid ,"," , 
											   ZaResource.A_accountStatus , "," , 
											   ZaResource.A_description, ",",
											   ZaResource.A_zimbraCalResType);

ZaResource.VIEW_RESOURCE_MAIL_RIGHT = "adminLoginCalendarResourceAs";
ZaResource.SET_CALRES_PASSWORD_RIGHT = "setCalendarResourcePassword";
ZaResource.CHANGE_CALRES_PASSWORD_RIGHT = "changeCalendarResourcePassword";
ZaResource.ADD_CALRES_ALIAS_RIGHT = "addCalendarResourceAlias";
ZaResource.REMOVE_CALRES_ALIAS_RIGHT = "removeCalendarResourceAlias";
ZaResource.DELETE_CALRES_RIGHT = "deleteCalendarResource";
ZaResource.GET_CALRES_SHAREINFO_RIGHT = "getCalendarResourceShareInfo";
ZaResource.LIST_CALRES_RIGHT = "listCalendarResource";
ZaResource.PUBLISH_CALRES_SHAREINFO = "publishCalendarResourceShareInfo";
ZaResource.RENAME_CALRES_RIGHT = "renameCalendarResource";
ZaResource.GET_CALRES_INFO_RIGHT = "getCalendarResourceInfo";
ZaResource.checkValues = 
function(tmpObj) {
	/**
	* check values
	**/
	if(ZaItem.hasWritePermission(ZaAccount.A_name,tmpObj)) {
		if(tmpObj.name == null || tmpObj.name.length < 1) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_NAME_REQUIRED);
			return false;
		}
		
		/*if(!AjxUtil.EMAIL_SHORT_RE.test(tmpObj.name) ) {*/
		if(!AjxUtil.isValidEmailNonReg(tmpObj.name)) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_RESOURCE_EMAIL_INVALID);
			return false;
		}
	}

	var maxPwdLen = Number.POSITIVE_INFINITY;
	var minPwdLen = 1;	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMinPwdLength,tmpObj) && ZaItem.hasWritePermission(ZaAccount.A_zimbraMaxPwdLength,tmpObj)) {
		//validate password length against this account's COS setting
		if(tmpObj.attrs[ZaResource.A_zimbraMinPwdLength] != null) {
			minPwdLen = tmpObj.attrs[ZaResource.A_zimbraMinPwdLength];
		} else  {
			minPwdLen = tmpObj._defaultValues.attrs[ZaResource.A_zimbraMinPwdLength];
		}
		
		if(tmpObj.attrs[ZaResource.A_zimbraMaxPwdLength] != null) {
			maxPwdLen = tmpObj.attrs[ZaResource.A_zimbraMaxPwdLength];
		} else  {
			maxPwdLen = tmpObj._defaultValues.attrs[ZaResource.A_zimbraMaxPwdLength];
		}
	}
	if(ZaItem.hasAnyRight([ZaResource.SET_CALRES_PASSWORD_RIGHT, ZaResource.CHANGE_CALRES_PASSWORD_RIGHT],tmpObj)) {
		//if there is a password - validate it
		if(tmpObj.attrs[ZaResource.A_password]!=null || tmpObj[ZaResource.A2_confirmPassword]!=null) {
			if(tmpObj.attrs[ZaResource.A_password] != tmpObj[ZaResource.A2_confirmPassword]) {
				//show error msg
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
				return false;
			} 			
			if(tmpObj.attrs[ZaResource.A_password].length < minPwdLen || AjxStringUtil.trim(tmpObj.attrs[ZaResource.A_password]).length < minPwdLen) { 
				//show error msg
                var minpassMsg;
                if (minPwdLen > 1) {
                    minpassMsg =  String(ZaMsg.NAD_passMinLengthMsg_p).replace("{0}",minPwdLen);
                } else {
                    minpassMsg =  String(ZaMsg.NAD_passMinLengthMsg_s).replace("{0}",minPwdLen);
                }
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_TOOSHORT + "<br>" + minpassMsg);
				return false;		
			}
			
			if(AjxStringUtil.trim(tmpObj.attrs[ZaResource.A_password]).length > maxPwdLen) { 
				//show error msg
                var maxpassMsg;
                if (maxPwdLen > 1) {
                    maxpassMsg =  String(ZaMsg.NAD_passMinLengthMsg_p).replace("{0}",minPwdLen);
                } else {
                    maxpassMsg =  String(ZaMsg.NAD_passMinLengthMsg_s).replace("{0}",minPwdLen);
                }
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_TOOLONG+ "<br>" + maxpassMsg);
				return false;		
			}
		} 	
	}		
	return true;
}


/**
* Creates a new ZaResource. This method makes SOAP request to create a new calresource record. 
* @param tmpObj {Object}
* @param app {ZaApp}
* @param resource {ZaResource}
**/
ZaResource.createMethod = 
function (tmpObj, resource) {
	tmpObj.attrs[ZaResource.A_mail] = tmpObj.name;	
	var resp;	
	//create SOAP request
	var soapDoc = AjxSoapDoc.create("CreateCalendarResourceRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set(ZaResource.A_name, tmpObj.name);
	
	if(tmpObj.attrs[ZaResource.A_password] && tmpObj.attrs[ZaResource.A_password].length > 0)
		soapDoc.set(ZaResource.A_password, tmpObj.attrs[ZaResource.A_password]);
			
	//set mail host	
	if(tmpObj[ZaResource.A2_autoMailServer] == "TRUE") {
		tmpObj.attrs[ZaResource.A_mailHost] = null;
	}
	
	//check if we need to set the cosId
	if (tmpObj[ZaResource.A2_autoCos] == "TRUE" ) {
		tmpObj.attrs[ZaResource.A_COSId] = null ;
	}
	
	//set scheduling policy
	ZaResource.prototype.setLdapAttrsFromSchedulePolicy.call (tmpObj); 
		
	//set all the other attrs automatically
	for (var aname in tmpObj.attrs) {
		if(aname == ZaResource.A_password || aname == ZaItem.A_objectClass || aname == ZaResource.A_mail ||
           aname == ZaResource.A_zimbraPrefCalendarAutoAcceptSignatureId ||
           aname == ZaResource.A_zimbraPrefCalendarAutoDenySignatureId ||
           aname == ZaResource.A_zimbraPrefCalendarAutoDeclineSignatureId     ) {
			continue;
		}	
		
		if(tmpObj.attrs[aname] instanceof Array) {
			var cnt = tmpObj.attrs[aname].length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					var attr = soapDoc.set("a", tmpObj.attrs[aname][ix]);
					attr.setAttribute("n", aname);
				}
			} 
		} else {	
			if(tmpObj.attrs[aname] != null) {
				var attr = soapDoc.set("a", tmpObj.attrs[aname]);
				attr.setAttribute("n", aname);
			}
		}
	}
	try {
		//var createResCommand = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;
		var reqMgrParams = {
			controller : ZaApp.getInstance().getCurrentController(),
			busyMsg : ZaMsg.BUSY_CREATE_RESOURCE
		}	
		resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.CreateCalendarResourceResponse;
	} catch (ex) {
		throw ex;
		return null ;
	}
		
	resource.initFromJS(resp.calresource[0]);	
	return resource ;		
}
ZaItem.createMethods["ZaResource"].push(ZaResource.createMethod);
ZaItem.createMethods["ZaResource"].push(ZaSignature.CreateAccountSignature);

/**
* @method modify
* Updates ZaResource attributes (SOAP)
* @param mods set of modified attributes and their new values
*/
ZaResource.modifyMethod =
function(mods) {
	var hasSomething = false;
	//update the object
	var soapDoc = AjxSoapDoc.create("ModifyCalendarResourceRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	for (var aname in mods) {
		hasSomething = true;
		//multi value attribute
		if(mods[aname] instanceof Array) {
			var cnt = mods[aname].length;
			if(cnt) {
				var nonemptyElements = false;
				for(var ix=0; ix <cnt; ix++) {
					var attr = null;
					if(mods[aname][ix] instanceof String || AjxUtil.isString(mods[aname][ix])) {
						if(AjxUtil.isEmpty(mods[aname][ix])) {
							continue;
						} else {
							nonemptyElements = true;
						}
						var attr = soapDoc.set("a", mods[aname][ix].toString());
					} else if(mods[aname][ix] instanceof Object) {
						var attr = soapDoc.set("a", mods[aname][ix].toString());
						nonemptyElements = true;
					} else {
						var attr = soapDoc.set("a", mods[aname][ix]);
						nonemptyElements = true;
					}
					
					if(attr)
						attr.setAttribute("n", aname);
				}
				if(!nonemptyElements) {
					var attr = soapDoc.set("a", "");
					attr.setAttribute("n", aname);
				}
			} else {
				var attr = soapDoc.set("a", "");
				attr.setAttribute("n", aname);
			}
		} else {
			var attr = soapDoc.set("a", mods[aname]);
			attr.setAttribute("n", aname);
		}
	}
	if(!hasSomething) {
		return;
	}
	//var modifyAccCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_MODIFY_RESOURCE
	}
	resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.ModifyCalendarResourceResponse;
	this.initFromJS(resp.calresource[0]);
	this[ZaResource.A2_confirmPassword] = null;
	//invalidate the original tooltip
	this._toolTip = null ;
	return;
}
ZaItem.modifyMethods["ZaResource"].push(ZaResource.modifyMethod);

ZaResource.prototype.remove = 
function(callback) {
	var soapDoc = AjxSoapDoc.create("DeleteCalendarResourceRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
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

ZaResource.prototype.initFromJS = 
function (resource) {
	if(!resource)
		return;
	
	//ensure current attrs are empty. Otherwise, the old attributes will be included also	
	this.attrs = new Object();			
	this.name = resource.name;
	this.id = resource.id;
	this.attrs[ZaResource.A_zimbraPrefCalendarForwardInvitesTo] = new Array();
	var len = (resource.a ? resource.a.length : 0);
	for(var ix = 0; ix < len; ix++) {
		if(!this.attrs[[resource.a[ix].n]]) {
			this.attrs[[resource.a[ix].n]] = resource.a[ix]._content;
		} else {
			if(!(this.attrs[[resource.a[ix].n]] instanceof Array)) {
				this.attrs[[resource.a[ix].n]] = [this.attrs[[resource.a[ix].n]]];
			} 
			this.attrs[[resource.a[ix].n]].push(resource.a[ix]._content);
		}
	}
		
	this.setSchedulePolicyFromLdapAttrs();	
	if(!this.attrs[ZaAccount.A_description])
		this.attrs[ZaAccount.A_description] = [];
		
	if(!(this.attrs[ZaAccount.A_description] instanceof Array)) {
		this.attrs[ZaAccount.A_description] = [this.attrs[ZaAccount.A_description]];
	}	
	
}

//set the ldap attributes according to the schedule policy values
//the ldap attrs are "zimbraCalResAutoAcceptDecline" & "zimbraCalResAutoDeclineIfBusy";

ZaResource.prototype.setSchedulePolicyFromLdapAttrs =
function () {
	if (this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] == "TRUE" && this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] == "TRUE"){
		this[ZaResource.A2_schedulePolicy] = ZaResource.SCHEDULE_POLICY_TT ;
	} else if (this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] == "TRUE" && this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] == "FALSE") {
		this[ZaResource.A2_schedulePolicy] = ZaResource.SCHEDULE_POLICY_TF;
	} else if (this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] == "FALSE" && this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] == "FALSE") {
		this[ZaResource.A2_schedulePolicy] = ZaResource.SCHEDULE_POLICY_FF;
	}  else if (this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] == "FALSE" && this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] == "TRUE") {
		this[ZaResource.A2_schedulePolicy] = ZaResource.SCHEDULE_POLICY_FT;
	}
	
}


ZaResource.prototype.setLdapAttrsFromSchedulePolicy =
function (){
	if (this[ZaResource.A2_schedulePolicy] == ZaResource.SCHEDULE_POLICY_TT ){
		this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] = "TRUE";
		this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] = "TRUE";		
	} else if (this[ZaResource.A2_schedulePolicy] == ZaResource.SCHEDULE_POLICY_TF){
		this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] = "TRUE";
		this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] = "FALSE";
	} else if (this[ZaResource.A2_schedulePolicy] == ZaResource.SCHEDULE_POLICY_FT) {
		this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] = "FALSE";
		this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] = "TRUE";		
	} else if (this[ZaResource.A2_schedulePolicy] == ZaResource.SCHEDULE_POLICY_FF) {
		this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] = "FALSE";
		this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] = "FALSE";		
	}
};

/**
* Returns HTML for a tool tip for this resource.
*/
ZaResource.prototype.getToolTip =
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
		//display the image
		if (this.attrs[ZaResource.A_zimbraCalResType] == ZaResource.RESOURCE_TYPE_LOCATION){
			html[idx++] = AjxImg.getImageHtml("Location");	
		}else {//equipment or other resource types
			html[idx++] = AjxImg.getImageHtml("Resource");	
		}	
		html[idx++] = "</td>";
		html[idx++] = "</table></div></td></tr>";
		html[idx++] = "<tr></tr>";
		idx = this._addRow(ZaMsg.NAD_ResourceStatus, 
						ZaResource.getAccountStatusLabel(this.attrs[ZaResource.A_accountStatus]), html, idx);
		
		idx = this._addRow(ZaMsg.NAD_ResourceName, this.attrs[ZaResource.A_displayname], html, idx);
		idx = this._addRow(ZaMsg.NAD_ResType, 
						ZaResource.getResTypeLabel(this.attrs[ZaResource.A_zimbraCalResType]), html, idx);
		if(this.getAttrs && this.getAttrs[ZaResource.A_mailHost]) {
			idx = this._addRow(ZaMsg.NAD_MailServer, this.attrs[ZaResource.A_mailHost], html, idx);
		}
		idx = this._addAttrRow(ZaItem.A_zimbraId, html, idx);
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}

ZaResource.loadMethod = 
function(by, val, withCos) {
	var soapDoc = AjxSoapDoc.create("GetCalendarResourceRequest", ZaZimbraAdmin.URN, null);
	if(withCos) {
		soapDoc.getMethod().setAttribute("applyCos", "1");	
	} else {
		soapDoc.getMethod().setAttribute("applyCos", "0");		
	}
	if(!this.getAttrs.all && !AjxUtil.isEmpty(this.attrsToGet)) {
		soapDoc.setMethodAttribute("attrs", this.attrsToGet.join(","));
	}	
	var elBy = soapDoc.set("calresource", val);
	elBy.setAttribute("by", by);

	//var getAccCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_RESOURCE
	}	
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetCalendarResourceResponse;
	this.attrs = new Object();
	this.initFromJS(resp.calresource[0]);
	
	//if(this.attrs[ZaResource.A_locationDisplayName] == null || this.getAutoLocationName() == this.attrs[ZaResource.A_locationDisplayName]) {
    var locationDisplayName = this.attrs[ZaResource.A_locationDisplayName]
    if (!locationDisplayName) {
        locationDisplayName = "";
    }
    if(this.getAutoLocationName() == locationDisplayName) {
		this[ZaResource.A2_autoLocationName] = "TRUE";
	} else {
		this[ZaResource.A2_autoLocationName] = "FALSE";
	}
	this[ZaResource.A2_confirmPassword] = null;	
}
ZaItem.loadMethods["ZaResource"].push(ZaResource.loadMethod);

ZaResource.loadInfoMethod = 
function(by, val, withCos) {

	if(!ZaItem.hasRight(ZaResource.GET_CALRES_INFO_RIGHT,this))
		return;
	
	var soapDoc = AjxSoapDoc.create("GetAccountInfoRequest", ZaZimbraAdmin.URN, null);

	var elBy = soapDoc.set("account", val);
	elBy.setAttribute("by", by);

	//var getAccCommand = new ZmCsfeCommand();
	var params = new Object();         i
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller: ZaApp.getInstance().getCurrentController()
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAccountInfoResponse;
	if(resp[ZaAccount.A2_publicMailURL] && resp[ZaAccount.A2_publicMailURL][0])
		this[ZaAccount.A2_publicMailURL] = resp[ZaAccount.A2_publicMailURL][0]._content;
	
	if(resp[ZaAccount.A2_adminSoapURL] && resp[ZaAccount.A2_adminSoapURL][0])
		this[ZaAccount.A2_adminSoapURL] = resp[ZaAccount.A2_adminSoapURL][0]._content;
	
	if(resp[ZaAccount.A2_soapURL] && resp[ZaAccount.A2_soapURL][0])
		this[ZaAccount.A2_soapURL] = resp[ZaAccount.A2_soapURL][0]._content;
}

ZaItem.loadMethods["ZaResource"].push(ZaSignature.GetSignatures);
ZaItem.loadMethods["ZaResource"].push(ZaResource.loadInfoMethod);

ZaResource.prototype.getAutoLocationName = 
function (){
	var autoLocName = "";
	if(this.attrs[ZaResource.A_zimbraCalResSite])
		autoLocName += ZaMsg.NAD_Site + " " + this.attrs[ZaResource.A_zimbraCalResSite] ;
	
	if(this.attrs[ZaResource.A_zimbraCalResBuilding])
		autoLocName += ", " + ZaMsg.NAD_Building + " " + this.attrs[ZaResource.A_zimbraCalResBuilding];
	
	if(this.attrs[ZaResource.A_zimbraCalResFloor])
		autoLocName += ", " + ZaMsg.NAD_Floor + " " + this.attrs[ZaResource.A_zimbraCalResFloor];
	
	if(this.attrs[ZaResource.A_zimbraCalResRoom])
		autoLocName += ", " + ZaMsg.NAD_Room + " " + this.attrs[ZaResource.A_zimbraCalResRoom];
	
	//remove the last ',' or spaces
	var regEx = /^\,\s*/;
	autoLocName = autoLocName.replace(regEx, "");
		
	return autoLocName ;
}

ZaResource.prototype.refresh = 
function(withCos) {
	this.load("id", this.id, withCos);	
}

/**
* public rename: sends RenameCalendarResourceRequest soap request
**/
ZaResource.prototype.rename = 
function (newName) {
	var soapDoc = AjxSoapDoc.create("RenameCalendarResourceRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	soapDoc.set("newName", newName);	
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_RENAME_RESOURCE
	}
	ZaRequestMgr.invoke(params, reqMgrParams);
}

/**
* ZaResource.myXModel - XModel for XForms
**/
ZaResource.myXModel = { 
	items: [
		{id:ZaResource.A_name, type:_STRING_, ref:"name", required:true,
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
		{id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId},
		{id:ZaItem.A_zimbraCreateTimestamp, ref:"attrs/" + ZaItem.A_zimbraCreateTimestamp}, 	
		{id:ZaResource.A_mail, type:_STRING_, ref:"attrs/"+ZaResource.A_mail,
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
		}, //email address
		{id:ZaResource.A2_schedulePolicy, type:_STRING_, ref:ZaResource.A2_schedulePolicy},
		{id:ZaResource.A_password, type:_STRING_, ref:"attrs/"+ZaAccount.A_password},
		{id:ZaResource.A2_confirmPassword, type:_STRING_},						 		
		//resource properties
		{id:ZaResource.A_displayname, type:_STRING_, ref:"attrs/"+ZaResource.A_displayname, required:true}, //resource name
		{id:ZaResource.A_zimbraCalResType, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResType},//type
		{id:ZaResource.A_uid, type:_STRING_, ref:"attrs/"+ZaResource.A_uid}, //email address field of the account name
		{id:ZaResource.A_mailHost, type:_STRING_, ref:"attrs/"+ZaResource.A_mailHost}, //domain dropdown of the account name
		{id:ZaResource.A_COSId, type:_STRING_, ref:"attrs/" + ZaResource.A_COSId},
		{id:ZaResource.A_accountStatus, type:_STRING_, ref:"attrs/"+ZaResource.A_accountStatus},		
		{id:ZaResource.A_zimbraCalResAutoDeclineIfBusy, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResAutoDeclineIfBusy}, //scheduling pocily
		{id:ZaResource.A_zimbraCalResAlwaysFree, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResAlwaysFree}, //scheduling pocily
		{id:ZaResource.A_zimbraCalResAutoDeclineRecurring, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResAutoDeclineRecurring},
		{id:ZaResource.A_zimbraCalResMaxNumConflictsAllowed, type:_NUMBER_, ref:"attrs/"+ZaResource.A_zimbraCalResMaxNumConflictsAllowed,defaultValue:0,minInclusive:0},
		{id:ZaResource.A_zimbraCalResMaxPercentConflictsAllowed, type:_NUMBER_, ref:"attrs/"+ZaResource.A_zimbraCalResMaxPercentConflictsAllowed,minInclusive:0,maxInclusive:100,defaultValue:0},
//		{id:ZaResource.A_description, type:_STRING_, ref:"attrs/"+ZaResource.A_description},
		ZaItem.descriptionModelItem ,
          {id:ZaResource.A_notes, type:_STRING_, ref:"attrs/"+ZaResource.A_notes},

        //Signature
        {id:ZaResource.A_zimbraPrefCalendarAutoAcceptSignatureId, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraPrefCalendarAutoAcceptSignatureId},
        {id:ZaResource.A_zimbraPrefCalendarAutoDenySignatureId, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraPrefCalendarAutoDenySignatureId},
        {id:ZaResource.A_zimbraPrefCalendarAutoDeclineSignatureId, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraPrefCalendarAutoDeclineSignatureId},
		//Resource Location
		{id:ZaResource.A_zimbraCalResSite, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResSite},
		{id:ZaResource.A_zimbraCalResBuilding, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResBuilding},
		{id:ZaResource.A_zimbraCalResFloor, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResFloor},
		{id:ZaResource.A_zimbraCalResRoom, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResRoom},
		{id:ZaResource.A_zimbraCalResCapacity, type:_NUMBER_, ref:"attrs/"+ZaResource.A_zimbraCalResCapacity},		
		{id:ZaResource.A_locationDisplayName, type:_STRING_, ref:"attrs/"+ZaResource.A_locationDisplayName},
		{id:ZaResource.A_street, type:_STRING_, ref:"attrs/"+ZaResource.A_street},	
		{id:ZaResource.A_city, type:_STRING_, ref:"attrs/"+ZaResource.A_city},		
		{id:ZaResource.A_state, type:_STRING_, ref:"attrs/"+ZaResource.A_state},		
		{id:ZaResource.A_country, type:_STRING_, ref:"attrs/"+ZaResource.A_country},
		{id:ZaResource.A_zip, type:_STRING_, ref:"attrs/"+ZaResource.A_zip},						
		
		//Resource 	Contact					
		{id:ZaResource.A_zimbraCalResContactName, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResContactName},
		{id:ZaResource.A_zimbraCalResContactEmail, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResContactEmail,
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
		{id:ZaResource.A_zimbraCalResContactPhone, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResContactPhone}, 
		{id:ZaResource.A_contactInfoAutoComplete, type:_LIST_, ref:"attrs/"+ZaResource.A_contactInfoAutoComplete},
		{id:ZaResource.A_zimbraPrefCalendarForwardInvitesTo, type:_LIST_, ref:"attrs/"+ZaResource.A_zimbraPrefCalendarForwardInvitesTo, listItem:{type:_EMAIL_ADDRESS_}},
		{id:ZaResource.A2_autodisplayname, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaResource.A2_autoMailServer, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaResource.A2_autoCos, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaResource.A2_autoLocationName, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A2_calFwdAddr_selection_cache, type:_LIST_},
        {id:ZaResource.A2_signatureList, type:_LIST_},
        {id:ZaResource.A2_signature_selection_cache, type:_LIST_}
	]
};

ZaItem._ATTR[ZaResource.A_displayname] = ZaMsg.attrDesc_accountName;
ZaItem._ATTR[ZaResource.A_description] = ZaMsg.attrDesc_description;
ZaItem._ATTR[ZaResource.A_accountStatus] = ZaMsg.attrDesc_accountStatus;
ZaItem._ATTR[ZaResource.A_mailHost] =  ZabMsg.attrDesc_mailHost;
ZaItem._ATTR[ZaResource.A_notes] = ZaMsg.attrDesc_notes;

ZaResource.getAccountStatusLabel = ZaAccount.getAccountStatusMsg ;

ZaResource.getResTypeLabel = 
function(val) {
	var desc = ZaResource._RESOURCE_TYPE[val];
	return (desc == null) ? val : desc;
}

ZaResource._RESOURCE_TYPE = new Object();
ZaResource._RESOURCE_TYPE [ ZaResource.RESOURCE_TYPE_LOCATION] = ZaMsg.resType_location;
ZaResource._RESOURCE_TYPE [ ZaResource.RESOURCE_TYPE_EQUIPMENT] = ZaMsg.resType_equipment;

ZaResource.initMethod = function () {
	this.attrs = new Object();
	this.id = "";
	this.name="";
	//this.attrs[ZaResource.A_zimbraMailAlias] = new Array();
}
ZaItem.initMethods["ZaResource"].push(ZaResource.initMethod);

ZaResource.setAutoLocationName = 
function (elementValue,instanceValue, event){
	var curInstance = this.getInstance() ;
	this.getForm().itemChanged(this, elementValue, event);	
	if(curInstance[ZaResource.A2_autoLocationName]=="TRUE") {
		curInstance.attrs [ZaResource.A_locationDisplayName] = ZaResource.prototype.getAutoLocationName.call (curInstance);
		this.getForm().refresh();
	}		
}

ZaResource.isLocation = function () {
	return (this.getInstanceValue(ZaResource.A_zimbraCalResType).toLowerCase() ==  ZaResource.RESOURCE_TYPE_LOCATION.toLowerCase());
}

ZaResource.isAutoDeclineEnabled = function () {
	return (this.getInstanceValue(ZaResource.A2_schedulePolicy) == ZaResource.SCHEDULE_POLICY_TT ||
	 this.getInstanceValue(ZaResource.A2_schedulePolicy) == ZaResource.SCHEDULE_POLICY_FT);
}

