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
* @class ZaResource
* @contructor ZaResource
* @param ZaApp app
* this class is a model for zimbra calendar resource account ldap objects
* @author Charles Cao
**/
function ZaResource(app) {
	ZaItem.call(this, app,"ZaResource");
	this._init(app);
}

ZaResource.prototype = new ZaItem;
ZaResource.prototype.constructor = ZaResource;

ZaItem.loadMethods["ZaResource"] = new Array();
ZaItem.initMethods["ZaResource"] = new Array();
ZaItem.modifyMethods["ZaResource"] = new Array();
ZaItem.createMethods["ZaResource"] = new Array();

//object attributes
ZaResource.A_name = ZaAccount.A_name;
ZaResource.A_resourceName = "resourceName";
ZaResource.A_uid = ZaAccount.A_uid;
ZaResource.A_accountName = ZaAccount.A_accountName; 
ZaResource.A_mail = ZaAccount.A_mail;
ZaResource.A_description = "description";
ZaResource.A_displayname = ZaAccount.A_displayname;
ZaResource.A_country = ZaAccount.A_country; //country
ZaResource.A_city = ZaAccount.A_city;
ZaResource.A_zip = ZaAccount.A_zip;
ZaResource.A_state = ZaAccount.A_state;

ZaResource.A_mailDeliveryAddress = ZaAccount.A_mailDeliveryAddress;
ZaResource.A_accountStatus = ZaAccount.A_accountStatus;
ZaResource.A_notes = ZaAccount.A_notes;
ZaResource.A_mailHost = ZaAccount.A_mailHost;
ZaResource.A_COSId = ZaAccount.A_COSId;
ZaResource.A_zimbraDomainName = "zimbraDomainName";
ZaResource.A_schedulePolicy = "schedulePolicy";
ZaResource.A_locationDisplayName = "locationDisplayName";
ZaResource.A_street = "street" ;

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
//ZaResource.A_zimbraMailStatus = "zimbraMailStatus";

ZaResource.ACCOUNT_STATUS_ACTIVE = "active";
ZaResource.ACCOUNT_STATUS_MAINTENANCE = "maintenance";
ZaResource.ACCOUNT_STATUS_LOCKED = "locked";
ZaResource.ACCOUNT_STATUS_CLOSED = "closed";

ZaResource.RESOURCE_TYPE_LOCATION = "location";
ZaResource.RESOURCE_TYPE_EQUIPMENT = "equipment";

ZaResource.SCHEDULE_POLICY_ACCEPT_ALL = "acceptAll";
ZaResource.SCHEDULE_POLICY_ACCEPT_UNLESS_BUSY = "acceptUnlessBusy";

//this attributes are not used in the XML object, but is used in the model
ZaResource.A2_autodisplayname = "autodisplayname";
ZaResource.A2_autoMailServer = "automailserver";
ZaResource.A2_myCOS = "mycos";

ZaResource.MAXSEARCHRESULTS = "500";
ZaResource.RESULTSPERPAGE = "25";

ZaResource.checkValues = 
function(tmpObj, app) {
	/**
	* check values
	**/

	if(tmpObj.name == null || tmpObj.name.length < 1) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_NAME_REQUIRED);
		return false;
	}
	/*
	if(tmpObj.attrs[ZaResource.A_lastName] == null || tmpObj.attrs[ZaResource.A_lastName].length < 1) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_LAST_NAME_REQUIRED);
		return false;
	}*/

	//var emailRegEx = /^([a-zA-Z0-9_\-])+((\.)?([a-zA-Z0-9_\-])+)*@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if(!AjxUtil.EMAIL_RE.test(tmpObj.name) ) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_NAME_INVALID);
		return false;
	}
	
	var myCos = null;	
	
	//find out what is this account's COS
	if(ZaSettings.COSES_ENABLED) {
		var cosList = app.getCosList().getArray();
		for(var ix in cosList) {
			if(cosList[ix].id == tmpObj.attrs[ZaResource.A_COSId]) {
				myCos = cosList[ix];
				break;
			}
		}
		if(!myCos && cosList.length > 0) {
			myCos = cosList[0];
			tmpObj.attrs[ZaResource.A_COSId] = cosList[0].id;
		}		
	}
	
	return true;
}


/**
* Creates a new ZaResource. This method makes SOAP request to create a new calresource record. 
* @param tmpObj
* @param app {ZaApp}
* @param account {ZaResource}
**/
ZaResource.createMethod = 
function (tmpObj, resource, app) {
	tmpObj.attrs[ZaResource.A_mail] = tmpObj.name;	
	var resp;	
	//create SOAP request
	var soapDoc = AjxSoapDoc.create("CreateCalendarResourceRequest", "urn:zimbraAdmin", null);
	soapDoc.set(ZaResource.A_name, tmpObj.name);
	
	//set mail host	
	if(tmpObj[ZaResource.A2_autoMailServer] == "TRUE") {
		tmpObj.attrs[ZaResource.A_mailHost] = null;
	}
	
	//set scheduling policy
	tmpObj.setLdapAttrsFromSchedulePolicy (); 
	/*
	if (tmpObj[ZaResource.A_schedulePolicy] == ZaResource.SCHEDULE_POLICY_ACCEPT_ALL ){
		tmpObj.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] = "TRUE";
		tmpObj.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] = "FALSE";		
	} else if (tmpObj[ZaResource.A_schedulePolicy] == ZaResource.SCHEDULE_POLICY_ACCEPT_UNLESS_BUSY){
		tmpObj.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] = "TRUE";
		tmpObj.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] = "TRUE";
	} //for delegation: tmpObj.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] = "FALSE";
	*/
	//make the account status value all lowercase
	
	//set all the other attrs automatically
	for (var aname in tmpObj.attrs) {
		if( aname == ZaItem.A_objectClass || aname == ZaResource.A_mail) {
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
		var createResCommand = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		resp = createResCommand.invoke(params).Body.CreateCalendarResourceResponse;
	} catch (ex) {
		switch(ex.code) {
			case ZmCsfeException.ACCT_EXISTS:
				app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_EXISTS);
			break;			
			default:
				app.getCurrentController()._handleException(ex, "ZaResource.create", null, false);
			break;
		}
		return null;
	}
		
	//resource.initFromJS(resp.calresource[0]);		
	
}
ZaItem.createMethods["ZaResource"].push(ZaResource.createMethod);

/**
* @method modify
* Updates ZaResource attributes (SOAP)
* @param mods set of modified attributes and their new values
*/
ZaResource.modifyMethod =
function(mods) {
	//update the object
	var soapDoc = AjxSoapDoc.create("ModifyCalendarResourceRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	for (var aname in mods) {
		//multy value attribute
		if(mods[aname] instanceof Array) {
			var cnt = mods[aname].length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					if(mods[aname][ix]) { //if there is an empty element in the array - don't send it
						var attr = soapDoc.set("a", mods[aname][ix]);
						attr.setAttribute("n", aname);
					}
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
	var modifyAccCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	resp = modifyAccCommand.invoke(params).Body.ModifyCalendarResourceResponse;
	this.initFromJS(resp.calresource[0]);
	return;
}
ZaItem.modifyMethods["ZaResource"].push(ZaResource.modifyMethod);

ZaResource.prototype.remove = 
function(callback) {
	var soapDoc = AjxSoapDoc.create("DeleteCalendarResourceRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	this.deleteCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	if(callback) {
		params.asyncMode = true;
		params.callback = callback;
	}
	this.deleteCommand.invoke(params);		
}
/*
ZaResource.prototype.initFromDom =
function(node) {
	this.name = node.getAttribute("name");
	this.id = node.getAttribute("id");
	this.attrs[ZaResource.A_zimbraMailAlias] = new Array();
	this.attrs[ZaResource.A_zimbraMailForwardingAddress] = new Array();
	var children = node.childNodes;
	for (var i=0; i< children.length;  i++) {
		child = children[i];
		if (child.nodeName != 'a') continue;
		var name = child.getAttribute("n");
		if (child.firstChild != null) {
			var value = child.firstChild.nodeValue;
			if (name in this.attrs) {
				var vc = this.attrs[name];
				if ((typeof vc) == "object") {
					vc.push(value);
				} else {
					this.attrs[name] = [vc, value];
				}
			} else {
				this.attrs[name] = value;
			}
		}
	}
} */

ZaResource.prototype.initFromJS = 
function (resource) {
	if(!resource)
		return;
	this.name = resource.name;
	this.id = resource.id;
	var len = resource.a.length;
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
	
	//TODO: define the A_schedulePolicy according
	this.setSchedulePolicyFromLdapAttrs();
}

//set the ldap attributes according to the schedule policy values
//the ldap attrs are "zimbraCalResAutoAcceptDecline" & "zimbraCalResAutoDeclineIfBusy";
ZaResource.prototype.setSchedulePolicyFromLdapAttrs =
function(){
	if (this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] == "TRUE" ){
		if (this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] == "TRUE") {
			this[ZaResource.A_schedulePolicy] = ZaResource.SCHEDULE_POLICY_ACCEPT_UNLESS_BUSY ;
		}else if (this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] == "FALSE"){
			this[ZaResource.A_schedulePolicy] = ZaResource.SCHEDULE_POLICY_ACCEPT_ALL
		}else {
			//invalid value
		}
	}else{
		//delegation
	}
};

ZaResource.prototype.setLdapAttrsFromSchedulePolicy =
function (){
	if (this[ZaResource.A_schedulePolicy] == ZaResource.SCHEDULE_POLICY_ACCEPT_ALL ){
		this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] = "TRUE";
		this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] = "FALSE";		
	} else if (this[ZaResource.A_schedulePolicy] == ZaResource.SCHEDULE_POLICY_ACCEPT_UNLESS_BUSY){
		this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] = "TRUE";
		this.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] = "TRUE";
	} //for delegation: this.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] = "FALSE";
};

/**
* Returns HTML for a tool tip for this account.
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
		html[idx++] = AjxImg.getImageHtml("Account");		
		html[idx++] = "</td>";
		html[idx++] = "</table></div></td></tr>";
		html[idx++] = "<tr></tr>";
		idx = this._addRow(ZaMsg.NAD_AccountStatus, 
						ZaResource.getAccountStatusLabel(this.attrs[ZaResource.A_accountStatus]), html, idx);
		// TODO: COS
		idx = this._addRow(ZaMsg.NAD_DisplayName, this.attrs[ZaResource.A_displayname], html, idx);
		if(ZaSettings.SERVERS_ENABLED) {
			idx = this._addRow(ZaMsg.NAD_MailServer, this.attrs[ZaResource.A_mailHost], html, idx);
		}
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}

ZaResource.loadMethod = 
function(by, val, withCos) {
	var soapDoc = AjxSoapDoc.create("GetCalendarResourceRequest", "urn:zimbraAdmin", null);
	if(withCos) {
		soapDoc.getMethod().setAttribute("applyCos", "1");	
	} else {
		soapDoc.getMethod().setAttribute("applyCos", "0");		
	}
	var elBy = soapDoc.set("calresource", val);
	elBy.setAttribute("by", by);

	var getAccCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = getAccCommand.invoke(params).Body.GetCalendarResourceResponse;
	this.attrs = new Object();
	this.initFromJS(resp.calresource[0]);
		
	/*			
	var autoDispName;
	if(this.attrs[ZaResource.A_firstName])
		autoDispName = this.attrs[ZaResource.A_firstName];
	else
		autoDispName = "";
		
	if(this.attrs[ZaResource.A_initials]) {
		autoDispName += " ";
		autoDispName += this.attrs[ZaResource.A_initials];
		autoDispName += ".";
	}
	if(this.attrs[ZaResource.A_lastName]) {
		if(autoDispName.length > 0)
			autoDispName += " ";
			
	    autoDispName += this.attrs[ZaResource.A_lastName];
	} 	
	
	if( autoDispName == this.attrs[ZaResource.A_displayname]) {
		this[ZaResource.A2_autodisplayname] = "TRUE";
	} else {
		this[ZaResource.A2_autodisplayname] = "FALSE";
	}*/
}
ZaItem.loadMethods["ZaResource"].push(ZaResource.loadMethod);

ZaResource.prototype.refresh = 
function(withCos) {
	this.load("id", this.id, withCos);
	
}

/**
* public rename; sends RenameCalendarResourceRequest soap request
**/
ZaResource.prototype.rename = 
function (newName) {
	var soapDoc = AjxSoapDoc.create("RenameCalendarResourceRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	soapDoc.set("newName", newName);	
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	command.invoke(params);
}



/**
* ZaResource.myXModel - XModel for XForms
**/
ZaResource.myXModel = { 
	items: [
		{id:ZaResource.A_name, type:_STRING_, ref:"name", required:true, pattern:AjxUtil.EMAIL_RE},
		{id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId}, 
		{id:ZaResource.A_accountStatus, type:_STRING_, ref:"attrs/"+ZaResource.A_accountStatus},
		{id:ZaResource.A_mail, type:_STRING_, ref:"attrs/"+ZaResource.A_mail}, //email address
		
		/* //HC: TODO do we need description? What will it refer to? 
		{id:ZaResource.A_description, type:_STRING_, ref:"attrs/"+ZaResource.A_description},
		*/	
		
		//resource properties
		{id:ZaResource.A_displayname, type:_STRING_, ref:"attrs/"+ZaResource.A_displayname, required:true}, //resource name
		{id:ZaResource.A_zimbraCalResType, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResType},//type
		{id:ZaResource.A_COSId, type:_STRING_, ref:"attrs/" + ZaResource.A_COSId},
		{id:ZaResource.A_uid, type:_STRING_, ref:"attrs/"+ZaResource.A_uid}, //email address field of the account name
		{id:ZaResource.A_mailHost, type:_STRING_, ref:"attrs/"+ZaResource.A_mailHost}, //domain dropdown of the account name
		{id:ZaResource.A_zimbraCalResAutoDeclineIfBusy, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResAutoDeclineIfBusy}, //scheduling pocily
		{id:ZaResource.A_zimbraCalResAlwaysFree, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResAlwaysFree}, //scheduling pocily
		{id:ZaResource.A_zimbraCalResAutoDeclineRecurring, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResAutoDeclineRecurring},
		{id:ZaResource.A_notes, type:_STRING_, ref:"attrs/"+ZaResource.A_notes}, //?? zibmraNotes is not returned by the GetCalendarResourceResponse
		{id:ZaResource.A_schedulePolicy, type:_STRING_, ref:ZaResource.A_schedulePolicy},
		
		//Resource Location
		{id:ZaResource.A_zimbraCalResSite, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResSite},
		{id:ZaResource.A_zimbraCalResBuilding, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResBuilding},
		{id:ZaResource.A_zimbraCalResFloor, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResFloor},
		{id:ZaResource.A_zimbraCalResRoom, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResRoom},
		{id:ZaResource.A_zimbraCalResCapacity, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResCapacity},		
		/* No LDAP Attributes for Location  Display name & Street Address*/
		{id:ZaResource.A_locationDisplayName, type:_STRING_, ref:"attrs/"+ZaResource.A_locationDisplayName},
		{id:ZaResource.A_street, type:_STRING_, ref:"attrs/"+ZaResource.A_street},	
		{id:ZaResource.A_city, type:_STRING_, ref:"attrs/"+ZaResource.A_city},		
		{id:ZaResource.A_state, type:_STRING_, ref:"attrs/"+ZaResource.A_state},		
		{id:ZaResource.A_country, type:_STRING_, ref:"attrs/"+ZaResource.A_country},
		{id:ZaResource.A_zip, type:_STRING_, ref:"attrs/"+ZaResource.A_zip},						
		
		//Resource 	Contact					
		{id:ZaResource.A_zimbraCalResContactName, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResContactName},
		{id:ZaResource.A_zimbraCalResContactEmail, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResContactEmail},
		{id:ZaResource.A_zimbraCalResContactPhone, type:_STRING_, ref:"attrs/"+ZaResource.A_zimbraCalResContactPhone}
		
	]
};

ZaItem._ATTR[ZaResource.A_displayname] = ZaMsg.attrDesc_accountName;
ZaItem._ATTR[ZaResource.A_description] = ZaMsg.attrDesc_description;
ZaItem._ATTR[ZaResource.A_accountStatus] = ZaMsg.attrDesc_accountStatus;
ZaItem._ATTR[ZaResource.A_mailHost] =  ZaMsg.attrDesc_mailHost;
ZaItem._ATTR[ZaResource.A_notes] = ZaMsg.attrDesc_notes;

ZaResource.getAccountStatusLabel = 
function(val) {
	var desc = ZaResource._ACCOUNT_STATUS[val];
	return (desc == null) ? val : desc;
}

/* Translation of Account status values into screen names */
ZaResource._ACCOUNT_STATUS = new Object ();
ZaResource._ACCOUNT_STATUS[ZaResource.ACCOUNT_STATUS_ACTIVE] = ZaMsg.accountStatus_active;
ZaResource._ACCOUNT_STATUS[ZaResource.ACCOUNT_STATUS_CLOSED] = ZaMsg.accountStatus_closed;
ZaResource._ACCOUNT_STATUS[ZaResource.ACCOUNT_STATUS_LOCKED] = ZaMsg.accountStatus_locked;
ZaResource._ACCOUNT_STATUS[ZaResource.ACCOUNT_STATUS_MAINTENANCE] = ZaMsg.accountStatus_maintenance;

ZaResource.getResTypeLabel = 
function(val) {
	var desc = ZaResource._RESOURCE_TYPE[val];
	return (desc == null) ? val : desc;
}

ZaResource._RESOURCE_TYPE = new Object();
ZaResource._RESOURCE_TYPE [ ZaResource.RESOURCE_TYPE_LOCATION] = ZaMsg.resType_location;
ZaResource._RESOURCE_TYPE [ ZaResource.RESOURCE_TYPE_EQUIPMENT] = ZaMsg.resType_equipment;

ZaResource.getSchedulePolicyLabel = 
function(val) {
	var desc = ZaResource._SCHEDULE_POLICY_LABEL [val];
	return (desc == null) ? val : desc;
}

ZaResource._SCHEDULE_POLICY_LABEL = new Object();
ZaResource._SCHEDULE_POLICY_LABEL[ ZaResource.SCHEDULE_POLICY_ACCEPT_ALL] = ZaMsg.resScheduleAcceptAll;
ZaResource._SCHEDULE_POLICY_LABEL [ ZaResource.SCHEDULE_POLICY_ACCEPT_UNLESS_BUSY] = ZaMsg.resScheduleAcceptUnlessBusy;

ZaResource.initMethod = function (app) {
	this.attrs = new Object();
	this.id = "";
	this.name="";
	//this.attrs[ZaResource.A_zimbraMailAlias] = new Array();
}
ZaItem.initMethods["ZaResource"].push(ZaResource.initMethod);