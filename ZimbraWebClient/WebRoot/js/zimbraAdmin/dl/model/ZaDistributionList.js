/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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
 * @author EMC
 **/
ZaDistributionList = function(id, name, memberList, description, notes) {
	ZaItem.call(this, "ZaDistributionList");
    this._init();

    this.attrs = new Object();
	this.attrs[ZaAccount.A_zimbraMailAlias] = [];
	this.id = (id != null)? id: null;
	this.type = ZaItem.DL;
	this.name = (name != null) ? name: null;
	this._selfMember = new ZaDistributionListMember(this.name);
	if (description != null) this.attrs.description = description;
	if (notes != null) this.attrs.zimbraNotes = notes;

    this.attrs[ZaDistributionList.A_zimbraIsACLGroup] = "TRUE";
    this[ZaDistributionList.A2_dlType] = ZaDistributionList.STATIC_DL_TYPE;
	this[ZaDistributionList.A2_numMembers] = 0;
	this[ZaDistributionList.A2_memberList] = (memberList != null) ? memberList: new Array();
	this[ZaDistributionList.A2_memberPool] = new Array();
    this[ZaAccount.A2_autoMailServer] = "TRUE";
    this[ZaAccount.A2_memberOf] = {directMemberList: [],indirectMemberList: [],nonMemberList: []};
    this[ZaAccount.A2_directMemberList + "_more"] = 0;
    this[ZaAccount.A2_indirectMemberList + "_more"] = 0;
    this[ZaDistributionList.A2_DLOwners] = new Array();
}

ZaDistributionList.prototype = new ZaItem;
ZaDistributionList.prototype.constructor = ZaDistributionList;
ZaItem.modifyMethods["ZaDistributionList"] = new Array();
ZaItem.loadMethods["ZaDistributionList"] = new Array();
ZaItem.initMethods["ZaDistributionList"] = new Array();
ZaItem.createMethods["ZaDistributionList"] = new Array();
ZaItem.ObjectModifiers["ZaDistributionList"] = new Array();
ZaItem.getRelatedMethods["ZaDistributionList"] = new Array();

ZaDistributionList.EMAIL_ADDRESS = "ZDLEA";
ZaDistributionList.DESCRIPTION = "ZDLDESC";
ZaDistributionList.ID = "ZDLID";
ZaDistributionList.MEMBER_QUERY_LIMIT = 25;
ZaDistributionList.MEMBER_LIST_PAGE_SIZE = 15;
ZaDistributionList.MEMBER_POOL_PAGE_SIZE = 15;
ZaDistributionList.A_zimbraGroupId = "zimbraGroupId";
ZaDistributionList.A_zimbraCreateTimestamp = "zimbraCreateTimestamp";

ZaDistributionList.A_mailStatus = "zimbraMailStatus";
ZaDistributionList.A2_dlType = "dlType";
ZaDistributionList.A_memberOfURL = "memberURL";
ZaDistributionList.A_zimbraIsACLGroup = "zimbraIsACLGroup";
ZaDistributionList.A2_members = "members";
ZaDistributionList.A2_allMemberHash = "allMemberHash";
ZaDistributionList.A2_allMemberPages = "allMemberPages";
ZaDistributionList.A2_allMemberHash = "allMemberHash";
ZaDistributionList.A2_memberList = "memberList";
ZaDistributionList.A2_origList = "origList";
ZaDistributionList.A2_addList = "addList";
ZaDistributionList.A2_removeList = "removeList";
ZaDistributionList.A2_query = "query";
ZaDistributionList.A2_pagenum = "pagenum";
ZaDistributionList.A2_poolPagenum = "poolPagenum";
ZaDistributionList.A2_poolNumPages = "poolNumPages";
ZaDistributionList.A2_totalNumInPool = "totalNumInPool";
ZaDistributionList.A2_numMembers = "numMembers";
ZaDistributionList.A2_memPagenum = "memPagenum";
ZaDistributionList.A2_memNumPages = "memNumPages";
ZaDistributionList.A2_memberPool = "memberPool";
ZaDistributionList.A2_optionalAdd = "optionalAdd";
ZaDistributionList.A2_membersSelected = "membersSelected";
ZaDistributionList.A2_nonmembersSelected = "nonmembersSelected";
ZaDistributionList.A2_memberPoolSelected = "memberPoolSelected";
ZaDistributionList.A2_directMemberSelected = "directMemberSelected";
ZaDistributionList.A2_indirectMemberSelected = "indirectMemberSelected";
ZaDistributionList.A2_directMemberList = "directMemberList";
ZaDistributionList.A2_indirectMemberList = "indirectMemberList";
ZaDistributionList.A2_nonMemberList = "nonMemberList";
ZaDistributionList.A2_alias_selection_cache = "alias_selection_cache";
ZaDistributionList.A_isAdminGroup = "zimbraIsAdminGroup" ;

ZaDistributionList.A_zimbraPrefReplyToAddress = "zimbraPrefReplyToAddress";
ZaDistributionList.A_zimbraPrefReplyToDisplay = "zimbraPrefReplyToDisplay";
ZaDistributionList.A_zimbraPrefReplyToEnabled = "zimbraPrefReplyToEnabled";

ZaDistributionList.A_zimbraDistributionListSubscriptionPolicy = "zimbraDistributionListSubscriptionPolicy";
ZaDistributionList.A_zimbraDistributionListUnsubscriptionPolicy = "zimbraDistributionListUnsubscriptionPolicy";

ZaDistributionList.A_zimbraDistributionListSendShareMessageToNewMembers = "zimbraDistributionListSendShareMessageToNewMembers";

ZaDistributionList.A2_zimbraDLSubscriptionPolicyAccept = "ACCEPT";
ZaDistributionList.A2_zimbraDLSubscriptionPolicyReject = "REJECT";
ZaDistributionList.A2_zimbraDLSubscriptionPolicyApproval = "APPROVAL";

ZaDistributionList.A2_DLOwners = "DLOwner";
ZaDistributionList.A2_owners_selection_cache = "owners_selection_cache";
ZaDistributionList.getDLStatus = function (status) {
    if (status == "enabled") {
        return ZaMsg.DL_Status_enabled ;
    } else if (status == "disabled" ) {
        return ZaMsg.DL_Status_disabled ;
    } else {
        return ZaMsg.ERROR_UNKNOWN ;
    }
}

ZaDistributionList.RENAME_DL_RIGHT = "renameDistributionList";
ZaDistributionList.ADD_DL_ALIAS_RIGHT = "addDistributionListAlias";
ZaDistributionList.REMOVE_DL_ALIAS_RIGHT = "removeDistributionListAlias";
ZaDistributionList.REMOVE_DL_MEMBER_RIGHT = "removeDistributionListMember";
ZaDistributionList.ADD_DL_MEMBER_RIGHT = "addDistributionListMember";
ZaDistributionList.GET_DL_MEMBERSHIP_RIGHT = "getDistributionListMembership";
ZaDistributionList.GET_DL_SHARE_INFO_RIGHT = "getDistributionListShareInfo";
ZaDistributionList.RIGHT_VIEW_ADMINUI_COMPONENTS = "viewDistributionListAdminUI";

ZaDistributionList.searchAttributes = AjxBuffer.concat(ZaAccount.A_displayname,",",
													   ZaItem.A_zimbraId,  "," , 
													   ZaAccount.A_mailHost , "," , 
													   ZaAccount.A_uid ,"," , 
													   ZaAccount.A_description, ",",
                                                       ZaDistributionList.A_isAdminGroup,",", 
													   ZaDistributionList.A_mailStatus);

ZaDistributionList.DYNAMIC_DL_TYPE = "1";
ZaDistributionList.STATIC_DL_TYPE = "0";
ZaDistributionList.postLoadDataFunction = new Array();

// ==============================================================
// public methods
// ==============================================================

ZaDistributionList.prototype.remove = 
function(callback) {
	var soapDoc = AjxSoapDoc.create("DeleteDistributionListRequest", ZaZimbraAdmin.URN, null);
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


/**
* public rename; sends RenameDistributionListRequest soap request
**/
ZaDistributionList.prototype.rename = 
function (newName) {
	var soapDoc = AjxSoapDoc.create("RenameDistributionListRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	soapDoc.set("newName", newName);	
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_RENAME_DL
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.RenameDistributionListResponse;	
	this.initFromJS(resp.dl[0]);	
}

/**
* Creates a new ZaDistributionList. This method makes SOAP request to create a new account record. 
* @param tmpObj
* @param app 
* @return ZaDistributionList
**/
ZaDistributionList.createMethod =
function(tmpObj, dl) {	
	tmpObj.attrs[ZaAccount.A_mail] = tmpObj.name;	
	var resp;	
	//create SOAP request
	var soapDoc = AjxSoapDoc.create("CreateDistributionListRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set(ZaAccount.A_name, tmpObj.name);

    if(tmpObj[ZaDistributionList.A2_dlType] == ZaDistributionList.DYNAMIC_DL_TYPE) {
        soapDoc.setMethodAttribute("dynamic", ZaDistributionList.DYNAMIC_DL_TYPE);
    }

	if(tmpObj[ZaAccount.A2_autoMailServer] == "TRUE") {
		tmpObj.attrs[ZaAccount.A_mailHost] = null;
	}
	
	//check if we need to set the cosId
	if (tmpObj[ZaAccount.A2_autoCos] == "TRUE" ) {
		tmpObj.attrs[ZaAccount.A_COSId] = null ;
	}
	
	for (var aname in tmpObj.attrs) {
		if( aname == ZaAccount.A_zimbraMailAlias || aname == ZaItem.A_objectClass || aname == ZaAccount.A2_mbxsize || aname == ZaAccount.A_mail) {
			continue;
		}	

        if (tmpObj[ZaDistributionList.A2_dlType] != ZaDistributionList.DYNAMIC_DL_TYPE && aname == ZaDistributionList.A_zimbraIsACLGroup) {
            continue;
        }
		if(tmpObj.attrs[aname] instanceof Array) {
			var cnt = tmpObj.attrs[aname].length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					if(typeof(tmpObj.attrs[aname][ix])=="object") {
						var attr = soapDoc.set("a", tmpObj.attrs[aname][ix].toString());
						attr.setAttribute("n", aname);
					} else {
						var attr = soapDoc.set("a", tmpObj.attrs[aname][ix]);
						attr.setAttribute("n", aname);						
					}
				}
			} 
		} else if (tmpObj.attrs[aname] instanceof AjxVector) {
			var tmpArray = tmpObj.attrs[aname].getArray();
			var cnt = tmpArray.length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					if(tmpArray[ix] !=null) {
						if(typeof(tmpArray[ix])=="object") {
							var attr = soapDoc.set("a", tmpArray[ix].toString());
							attr.setAttribute("n", aname);
						} else {
							var attr = soapDoc.set("a", tmpArray[ix]);
							attr.setAttribute("n", aname);
						}
					}
				}
			} 			
			
		} else {	
			if(tmpObj.attrs[aname] != null) {
				if(typeof(tmpObj.attrs[aname]) == "object") {				
					var attr = soapDoc.set("a", tmpObj.attrs[aname].toString());
					attr.setAttribute("n", aname);
				} else {
					var attr = soapDoc.set("a", tmpObj.attrs[aname]);
					attr.setAttribute("n", aname);					
				}
			}
		}
	}
	try {

		//var createAccCommand = new ZmCsfeCommand();
		var csfeParams = new Object();
		csfeParams.soapDoc = soapDoc;	
		var reqMgrParams = {} ;
		reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
		reqMgrParams.busyMsg = ZaMsg.BUSY_CREATE_ACCOUNTS ;
		//reqMgrParams.busyMsg = "Creating Accounts ...";
		//resp = createAccCommand.invoke(params).Body.CreateAccountResponse;
		resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.CreateDistributionListResponse;
	} catch (ex) {
		throw ex;
		return null;
	}
	dl.initFromJS(resp.dl[0]);
	dl.loadEffectiveRights("id", dl.id,false);
	tmpObj.rights = dl.rights;
	tmpObj.setAttrs = dl.setAttrs;
	tmpObj.getAttrs = dl.getAttrs;
	tmpObj.id = dl.id;
	tmpObj.attrs[ZaItem.A_zimbraId] = dl.attrs[ZaItem.A_zimbraId];
}
ZaItem.createMethods["ZaDistributionList"].push(ZaDistributionList.createMethod);

/**
* @method modify
* Updates ZaDistributionList attributes (SOAP)
* @param mods set of modified attributes and their new values
*/
ZaDistributionList.modifyMethod = function(mods, obj) {
	var gotSomething = false;
	var soapDoc = AjxSoapDoc.create("ModifyDistributionListRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	//transfer the fields from the tmpObj to the _currentObject
	for (var a in obj.attrs) {
		if(a == ZaItem.A_objectClass || a==ZaAccount.A_mail || a == ZaItem.A_zimbraId
                || a == ZaAccount.A_zimbraMailAlias || a == ZaItem.A_zimbraACE) {
			continue;
		}	
		//check if the value has been modified
		if ((this.attrs[a] != obj.attrs[a]) && !(this.attrs[a] == undefined && obj.attrs[a] === "")) {
			if(a==ZaAccount.A_uid) {
				continue; //skip uid, it is changed throw a separate request
			}
			if(!ZaItem.hasWritePermission(a,obj)) {
				continue;
			}			
			gotSomething = true;
			if(obj.attrs[a] instanceof Array) {
	   			if (!this.attrs[a]) {
	   				this.attrs[a] = [] ;
                 }else if (!(this.attrs[a] instanceof Array)) {
                       this.attrs[a] = [this.attrs[a]] ;
                   }

	       		if( obj.attrs[a].join(",").valueOf() !=  this.attrs[a].join(",").valueOf()) {
					//mods[a] = obj.attrs[a];
					var cnt = obj.attrs[a].length;
					if(cnt) {
						for(var ix=0; ix <cnt; ix++) {
							var attr = null;
                            if(obj.attrs[a][ix] instanceof String )
								var attr = soapDoc.set("a", obj.attrs[a][ix].toString());
							else if(obj.attrs[a][ix] instanceof Object)
								var attr = soapDoc.set("a", obj.attrs[a][ix].toString());
							else if(obj.attrs[a][ix])
								var attr = soapDoc.set("a", obj.attrs[a][ix]);
			
							if(attr)
								attr.setAttribute("n", a);
						}						
					} else {
						var attr = soapDoc.set("a", "");
						attr.setAttribute("n", a);
					}
				}
			} else {
				var attr = soapDoc.set("a",obj.attrs[a]);
				attr.setAttribute("n", a);
			}				
		}
	}	
	if(!gotSomething)
		return;
		
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller:ZaApp.getInstance().getCurrentController(),
		busyMsg: ZaMsg.BUSY_MODIFY_DL 
	} ;
	
	//resp = modifyAccCommand.invoke(params).Body.ModifyAccountResponse;
	resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.ModifyDistributionListResponse ;
	
	this.initFromJS(resp.dl[0]);
	this._toolTip = null ;
	return;	
}
ZaItem.modifyMethods["ZaDistributionList"].push(ZaDistributionList.modifyMethod);


/**
* Returns HTML for a tool tip for this resource.
*/
ZaDistributionList.prototype.getToolTip =
function() {
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = "<table>";
		html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
		html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap; overflow:hidden;'>";
		html[idx++] = "<table style='width:100%;'>";
		html[idx++] = "<tr valign='center'>";
		html[idx++] = "<td><b>" + AjxStringUtil.htmlEncode(this.name) + "</b></td>";
		html[idx++] = "<td align='right'>";
	     if (this.attrs[ZaDistributionList.A_isAdminGroup] == "TRUE") {
			    html[idx++] = AjxImg.getImageHtml("DistributionListGroup");
         }else {
             html[idx++] = AjxImg.getImageHtml("DistributionList");
         }			
		html[idx++] = "</td>";
		html[idx++] = "</table></div></td></tr>";
		html[idx++] = "<tr></tr>";
		idx = this._addAttrRow(ZaItem.A_description, html, idx);
		idx = this._addAttrRow(ZaItem.A_zimbraId, html, idx);
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}

ZaDistributionList.addRemoveAliases = function (mods, obj) {
	//add-remove aliases
	var tmpObjCnt = -1;
	var currentObjCnt = -1;
	if(ZaItem.hasRight(ZaDistributionList.REMOVE_DL_ALIAS_RIGHT, this) || ZaItem.hasRight(ZaDistributionList.ADD_DL_ALIAS_RIGHT, this)) {
		if(obj.attrs[ZaAccount.A_zimbraMailAlias]) {
			if(typeof obj.attrs[ZaAccount.A_zimbraMailAlias] == "string") {
				var tmpStr = obj.attrs[ZaAccount.A_zimbraMailAlias];
				obj.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
				obj.attrs[ZaAccount.A_zimbraMailAlias].push(tmpStr);
			}
			tmpObjCnt = obj.attrs[ZaAccount.A_zimbraMailAlias].length - 1;
		}
		
		if(this.attrs[ZaAccount.A_zimbraMailAlias]) {
			if(typeof this.attrs[ZaAccount.A_zimbraMailAlias] == "string") {
				var tmpStr = this.attrs[ZaAccount.A_zimbraMailAlias];
				this.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
				this.attrs[ZaAccount.A_zimbraMailAlias].push(tmpStr);
			}
			currentObjCnt = this.attrs[ZaAccount.A_zimbraMailAlias].length - 1;
		}
	
		//diff two arrays
		for(var tmpIx=tmpObjCnt; tmpIx >= 0; tmpIx--) {
			for(var currIx=currentObjCnt; currIx >=0; currIx--) {
				if(obj.attrs[ZaAccount.A_zimbraMailAlias][tmpIx] == this.attrs[ZaAccount.A_zimbraMailAlias][currIx]) {
					//this alias already exists
					obj.attrs[ZaAccount.A_zimbraMailAlias].splice(tmpIx,1);
					this.attrs[ZaAccount.A_zimbraMailAlias].splice(currIx,1);
					break;
				}
			}
		}
		//remove the aliases 
		if(currentObjCnt != -1) {
			currentObjCnt = this.attrs[ZaAccount.A_zimbraMailAlias].length;
		} 
		if(ZaItem.hasRight(ZaDistributionList.REMOVE_DL_ALIAS_RIGHT, this)) {
			try {
				for(var ix=0; ix < currentObjCnt; ix++) {
					this.removeAlias(this.attrs[ZaAccount.A_zimbraMailAlias][ix]);
				}
			} catch (ex) {
				ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDistributionList.addRemoveAliases", null, false);
				return false;
			}
		}
		if(tmpObjCnt != -1) {
			tmpObjCnt = obj.attrs[ZaAccount.A_zimbraMailAlias].length;
		}
		var failedAliases = "";
		var failedAliasesCnt = 0;
		if(ZaItem.hasRight(ZaDistributionList.ADD_DL_ALIAS_RIGHT, this)) {
			try {
				for(var ix=0; ix < tmpObjCnt; ix++) {
					var aliasName = obj.attrs[ZaAccount.A_zimbraMailAlias][ix];
					try {
						if(aliasName) {
							if(aliasName.indexOf("@") != aliasName.lastIndexOf("@")) {
								//show error msg
								ZaApp.getInstance().getCurrentController()._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_ALIAS_INVALID,[aliasName]), null, DwtMessageDialog.CRITICAL_STYLE, null);
								ZaApp.getInstance().getCurrentController()._errorDialog.popup();		
								break;						
							}						
							this.addAlias(aliasName);
						}
					} catch (ex) {
						if((ex.code == ZmCsfeException.ACCT_EXISTS) && aliasName) {
							//if failed because account exists just show a warning
							var account = ZaXFormViewController.prototype._findAlias.call(this, aliasName);
							switch(account.type) {
								case ZaItem.DL:
									if(account.name == aliasName) {
										failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS3,[account.name]);								
									} else {
										failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS4,[account.name, aliasName]);
									}
								break;
								case ZaItem.ACCOUNT:
									if(account.name == aliasName) {
										failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS2,[account.name]);								
									} else {
										failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS1,[account.name, aliasName]);
									}							
								break;	
								case ZaItem.RESOURCE:
									if(account.name == aliasName) {
										failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS5,[account.name]);								
									} else {
										failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS6,[account.name, aliasName]);
									}							
								break;							
								default:
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS0,[aliasName]);
								break;
							}
							failedAliasesCnt++;
						} else {
							//if failed for another reason - jump out
							throw (ex);
						}
					}
				}
		
				if(failedAliasesCnt == 1) {
					ZaApp.getInstance().getCurrentController()._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.WARNING_ALIAS_EXISTS, [failedAliases]), "", DwtMessageDialog.WARNING_STYLE, ZabMsg.zimbraAdminTitle);
					ZaApp.getInstance().getCurrentController()._errorDialog.popup();			
				} else if(failedAliasesCnt > 1) {
					ZaApp.getInstance().getCurrentController()._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.WARNING_ALIASES_EXIST, [failedAliases]), "", DwtMessageDialog.WARNING_STYLE, ZabMsg.zimbraAdminTitle);
					ZaApp.getInstance().getCurrentController()._errorDialog.popup();			
				}
			} catch (ex) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.FAILED_ADD_ALIASES, ex);	
				return false;
			}
		}
	}
}
ZaItem.modifyMethods["ZaDistributionList"].push(ZaDistributionList.addRemoveAliases);

ZaDistributionList.addAliases = function (obj, dl) {
	//add-remove aliases
	if(ZaItem.hasRight(ZaDistributionList.ADD_DL_ALIAS_RIGHT, dl)) {
		if(obj.attrs[ZaAccount.A_zimbraMailAlias]) {
			if(typeof obj.attrs[ZaAccount.A_zimbraMailAlias] == "string") {
				var tmpStr = obj.attrs[ZaAccount.A_zimbraMailAlias];
				obj.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
				obj.attrs[ZaAccount.A_zimbraMailAlias].push(tmpStr);
			}
			tmpObjCnt = obj.attrs[ZaAccount.A_zimbraMailAlias].length;
		}

		var failedAliases = "";
		var failedAliasesCnt = 0;
		try {
			for(var ix=0; ix < tmpObjCnt; ix++) {
				var aliasName = obj.attrs[ZaAccount.A_zimbraMailAlias][ix];
				try {
					if(aliasName) {
						if(aliasName.indexOf("@") != aliasName.lastIndexOf("@")) {
							//show error msg
							ZaApp.getInstance().getCurrentController()._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_ALIAS_INVALID,[aliasName]), null, DwtMessageDialog.CRITICAL_STYLE, null);
							ZaApp.getInstance().getCurrentController()._errorDialog.popup();		
							break;						
						}						
						dl.addAlias(aliasName);
					}
				} catch (ex) {
					if((ex.code == ZmCsfeException.ACCT_EXISTS) && aliasName) {
						//if failed because account exists just show a warning
						var account =  ZaXFormViewController.prototype._findAlias.call(this, aliasName);
						switch(account.type) {
							case ZaItem.DL:
								if(account.name == aliasName) {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS3,[account.name]);								
								} else {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS4,[account.name, aliasName]);
								}
							break;
							case ZaItem.ACCOUNT:
								if(account.name == aliasName) {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS2,[account.name]);								
								} else {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS1,[account.name, aliasName]);
								}							
							break;	
							case ZaItem.RESOURCE:
								if(account.name == aliasName) {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS5,[account.name]);								
								} else {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS6,[account.name, aliasName]);
								}							
							break;							
							default:
								failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS0,[aliasName]);
							break;
						}
						failedAliasesCnt++;
					} else {
						//if failed for another reason - jump out
						throw (ex);
					}
				}
			}
	
			if(failedAliasesCnt == 1) {
				ZaApp.getInstance().getCurrentController()._errorDialog.setMessage(ZaMsg.WARNING_ALIAS_EXISTS + failedAliases, "", DwtMessageDialog.WARNING_STYLE, ZabMsg.zimbraAdminTitle);
				ZaApp.getInstance().getCurrentController()._errorDialog.popup();			
			} else if(failedAliasesCnt > 1) {
				ZaApp.getInstance().getCurrentController()._errorDialog.setMessage(ZaMsg.WARNING_ALIASES_EXIST + failedAliases, "", DwtMessageDialog.WARNING_STYLE, ZabMsg.zimbraAdminTitle);
				ZaApp.getInstance().getCurrentController()._errorDialog.popup();			
			}
		} catch (ex) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.FAILED_ADD_ALIASES, ex);	
			return false;
		}
	}
}
ZaItem.createMethods["ZaDistributionList"].push(ZaDistributionList.addAliases);


ZaDistributionList.addRemoveOwners = function (mods, obj) {
	//add-remove Owners
	var tmpObjCnt = -1;
	var currentObjCnt = -1;
    // Used for ACL in future
    var hasAddandRemoveRight = true;
	if(hasAddandRemoveRight) {
		if(obj[ZaDistributionList.A2_DLOwners]) {
			if(!(obj[ZaDistributionList.A2_DLOwners] instanceof Array)) {
				var tmpStr = obj[ZaDistributionList.A2_DLOwners] ;
				obj[ZaDistributionList.A2_DLOwners]  = new Array();
				obj[ZaDistributionList.A2_DLOwners].push(tmpStr);
			}
			tmpObjCnt = obj[ZaDistributionList.A2_DLOwners].length - 1;
		}

		if(this[ZaDistributionList.A2_DLOwners]) {
			if(!(this[ZaDistributionList.A2_DLOwners] instanceof Array)) {
				var tmpStr = this[ZaDistributionList.A2_DLOwners] ;
				this[ZaDistributionList.A2_DLOwners]  = new Array();
				this[ZaDistributionList.A2_DLOwners].push(tmpStr);
			}
			currentObjCnt = this[ZaDistributionList.A2_DLOwners].length - 1;
		}

		//diff two arrays
		for(var tmpIx=tmpObjCnt; tmpIx >= 0; tmpIx--) {
			for(var currIx=currentObjCnt; currIx >=0; currIx--) {
				if(obj[ZaDistributionList.A2_DLOwners][tmpIx] == this[ZaDistributionList.A2_DLOwners][currIx]) {
					//this alias already exists
					obj[ZaDistributionList.A2_DLOwners].splice(tmpIx,1);
					this[ZaDistributionList.A2_DLOwners].splice(currIx,1);
					break;
				}
			}
		}
		//remove owners
		if(currentObjCnt != -1) {
			currentObjCnt = this[ZaDistributionList.A2_DLOwners].length;
		}
        var hasRemoveRight =  true;
		if(hasRemoveRight) {
			try {
				for(var ix=0; ix < currentObjCnt; ix++) {
					this.addRemoveOwner(this[ZaDistributionList.A2_DLOwners][ix]);
				}
			} catch (ex) {
				ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDistributionList.addRemoveOwner", null, false);
				return false;
			}
		}

        // add owners
		if(tmpObjCnt != -1) {
			tmpObjCnt = obj[ZaDistributionList.A2_DLOwners].length;
		}

        var hasAddRight = true;
		if(hasAddRight) {
			try {
				for(var ix=0; ix < tmpObjCnt; ix++) {
					try {
						if(obj[ZaDistributionList.A2_DLOwners][ix]) {
							this.addRemoveOwner(obj[ZaDistributionList.A2_DLOwners][ix], true);
						}
					} catch (ex) {
							//if failed for another reason - jump out
							throw (ex);
					}
				}
			} catch (ex) {
				ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDistributionList.addRemoveOwner", null, false);
				return false;
			}
		}
	}
}
ZaItem.modifyMethods["ZaDistributionList"].push(ZaDistributionList.addRemoveOwners);

ZaDistributionList.addOwners = function (obj, dl) {
	//add-remove Owners
	var tmpObjCnt = -1;
    // Used for ACL in future
    var hasAddRight = true;
	if(hasAddRight) {
		if(obj[ZaDistributionList.A2_DLOwners]) {
			if(!(obj[ZaDistributionList.A2_DLOwners] instanceof Array)) {
				var tmpStr = obj[ZaDistributionList.A2_DLOwners] ;
				obj[ZaDistributionList.A2_DLOwners]  = new Array();
				obj[ZaDistributionList.A2_DLOwners].push(tmpStr);
			}
			tmpObjCnt = obj[ZaDistributionList.A2_DLOwners].length - 1;
		}

        // add owners
		if(tmpObjCnt != -1) {
			tmpObjCnt = obj[ZaDistributionList.A2_DLOwners].length;
		}

        var hasAddRight = true;
		if(hasAddRight) {
			try {
				for(var ix=0; ix < tmpObjCnt; ix++) {
					try {
						if(obj[ZaDistributionList.A2_DLOwners][ix]) {
							obj.addRemoveOwner(obj[ZaDistributionList.A2_DLOwners][ix], true);
						}
					} catch (ex) {
							//if failed for another reason - jump out
							throw (ex);
					}
				}
			} catch (ex) {
				ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDistributionList.addOwner", null, false);
				return false;
			}
		}
	}
}
ZaItem.createMethods["ZaDistributionList"].push(ZaDistributionList.addOwners);

ZaDistributionList.prototype.addRemoveOwner = function (ownerName, isAdd){
	var soapDoc = AjxSoapDoc.create("DistributionListActionRequest", "urn:zimbraAccount", null);
    var dlBy = soapDoc.set("dl", this.id);
    dlBy.setAttribute("by", "id");

    var opBy = soapDoc.set("action", null);
    var busyMsg;
    var actionName;
    if (isAdd) {
        actionName = "addOwners";
        busyMsg = ZaMsg.BUSY_ADD_DL_OWNER;
    }  else {
        actionName = "removeOwners";
        busyMsg = ZaMsg.BUSY_REMOVE_DL_OWNER;
    }
    opBy.setAttribute("op", actionName);

    var ownerBy = soapDoc.set("owner", ownerName, opBy);
    ownerBy.setAttribute("by", "name");
    ownerBy.setAttribute("type", "usr");
	var params = new Object();
	params.soapDoc = soapDoc;
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : busyMsg
	}
	ZaRequestMgr.invoke(params, reqMgrParams);
}

ZaDistributionList.checkValues = function(tmpObj) {
	if(ZaItem.hasWritePermission(ZaAccount.A_name,tmpObj)) {
		if(tmpObj.name == null || tmpObj.name.length < 1) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_DL_NAME_REQUIRED);
			return false;
		}
		if(!AjxUtil.isValidEmailNonReg(tmpObj.name)) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_DL_NAME_INVALID);
			return false;
		}	
	}
	

	return true;
}
// ==============================================================
// public accessor methods
// ==============================================================

ZaDistributionList.prototype.markChanged = function () {
	this._dirty = true;
};

ZaDistributionList.prototype.markClean = function () {
	this._dirty = false;
};

ZaDistributionList.prototype.isDirty = function () {
	return this._dirty;
};

ZaDistributionList.prototype.getId = function () {
	return this.id;
};

ZaDistributionList.prototype.setId = function (id) {
	this.id = id;
};

ZaDistributionList.prototype.getName = function () {
	return this.name;
};

ZaDistributionList.prototype.getMemberQueryParams = function(callbackQuery, offset, limit, controller, ajxCallbackWhenCompleted){
		if ( offset == null ) {
			offset = 0;
		}
		if ( limit == null ){
			limit = ZaDistributionList.MEMBER_QUERY_LIMIT;
		}

		var soapDoc = AjxSoapDoc.create("GetDistributionListRequest", ZaZimbraAdmin.URN, null);
		if(!this.getAttrs.all && !AjxUtil.isEmpty(this.attrsToGet)) {
			soapDoc.setMethodAttribute("attrs", this.attrsToGet.join(","));
		}

		soapDoc.setMethodAttribute("limit", limit);
		soapDoc.setMethodAttribute("offset", offset ); //start from the beginning

		var dl = soapDoc.set("dl", this.id);
		dl.setAttribute("by", "id");
		soapDoc.set("name", this.getName());

		var busyId = Dwt.getNextId();
		var argsOfCallback = {
			controller : controller,
			offset: offset,
			limit: limit,
			busyId: busyId,
			ajxCallbackWhenCompleted: ajxCallbackWhenCompleted
		};

		var reqCtrlParams = {
			soapDoc : soapDoc,
			noAuthToken : true,
			asyncMode : true
		}
		if ( callbackQuery != null ) {
			reqCtrlParams.callback = new AjxCallback(this, callbackQuery, argsOfCallback);
		}

		var reqMgrParams = {
			controller : controller,
			showBusy: true,
			busyMsg : ZaMsg.BUSY_GET_DL,
			busyId: busyId,
			skipCallbackIfCancelled: true
		}

		var queryParams = {
			reqCtrlParams: reqCtrlParams,
			reqMgrParams: reqMgrParams
		}
		return queryParams;
}


ZaDistributionList.prototype.getAllMembers = function ( params ) {
	if ( AjxUtil.isEmpty(params) || AjxUtil.isEmpty(params.controller) || AjxUtil.isEmpty(params.controller._view) ) {
		return;
	}
	var controller = params.controller, view = params.controller._view;

	if (this.id != null) {
		var limit = 0; //0 means unlimited
		var offset = 0;
		var callbackQuery = ZaDistributionList.prototype.getAllMembersCallback;
		this[ZaDistributionList.A2_allMemberHash] = {}; // make a new one

		try {
			var ajxCallbackWhenCompleted = new AjxCallback(view, ZaDLXFormView.prototype.updateMemberList);
            var updateTreeWhenCompleted = new AjxCallback(this, this.updateTree);
			var queryParams = ZaDistributionList.prototype.getMemberQueryParams.call( this, callbackQuery, offset, 
																	limit, controller, [ajxCallbackWhenCompleted, updateTreeWhenCompleted] );
			ZaRequestMgr.invoke(queryParams.reqCtrlParams, queryParams.reqMgrParams);
		} catch (ex) {
			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDistributionList.prototype.getAllMembers", null, false);
		}
	}
}

ZaDistributionList.prototype.updateTree = function () {
    var treeCtrl = ZaZimbraAdmin.getInstance().getOverviewPanelController();
    treeCtrl.refreshRelatedTree(this, true, true);
}

ZaDistributionList.prototype.getAllMembersCallback = function ( params, resp ) {
	try {

		if(params.busyId) {
			ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);
		}

		if(!resp && !this._currentRequest.cancelled) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListViewController.prototype.getAllMembersCallback"));

		} else if( !resp.isException() ) {
			if ( !resp._data || !resp._data || !resp._data.Body ||
				!resp._data.Body.GetDistributionListResponse ) {
				return;
			}
			var dlBody = resp._data.Body.GetDistributionListResponse;
			if (!dlBody.dl || !(dlBody.dl[0]) ) {
				return;
			}
			var dlResp = dlBody.dl[0];

			//whether is dynamic group
			if (dlResp.dynamic === true) {
				this[ZaDistributionList.A2_dlType] = ZaDistributionList.DYNAMIC_DL_TYPE;
			}


			//all its members
			var members = dlResp.dlm;
			var len = (members && members.length) ? members.length : 0;

			var allMembers = this[ZaDistributionList.A2_allMemberHash];
			for (var i =0; i < len; ++i) {
				var name = members[i]._content;
				if ( !AjxUtil.isEmpty(name) ) {
					var member = new ZaDistributionListMember(name);
					allMembers[name] = member;
				}
			}

			this[ZaDistributionList.A2_numMembers] = len;
			this[ZaDistributionList.A2_memNumPages] = Math.ceil(len/ZaDistributionList.MEMBER_LIST_PAGE_SIZE);
			ZaDistributionList.prototype.pageAllMembers.call(this);

			this.id = dlResp.id;
			this.initFromJS(dlResp);

			//membership related instance variables, Make a GetAccountMembershipRequest
			if (this[ZaDistributionList.A2_dlType] !== ZaDistributionList.DYNAMIC_DL_TYPE)
				this[ZaAccount.A2_memberOf] = ZaAccountMemberOfListView.getDlMemberShip(this.id, "id" ) ;
			else
				this[ZaAccount.A2_memberOf] = { directMemberList: [],
												indirectMemberList: [],
												nonMemberList: []
												};
			this[ZaAccount.A2_directMemberList + "_more"] = 
				(this[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList].length > ZaAccountMemberOfListView.SEARCH_LIMIT) ? 1: 0;
			this[ZaAccount.A2_indirectMemberList + "_more"] = 
				(this[ZaAccount.A2_memberOf][ZaAccount.A2_indirectMemberList].length > ZaAccountMemberOfListView.SEARCH_LIMIT) ? 1: 0;

			//dl owners
			var owners = dlResp.owners;
			var ownerLen = (owners && owners.length) ? owners.length: 0;
			this[ZaDistributionList.A2_DLOwners] = new Array();
			if ( ownerLen > 0 ) {
				var ownerSet = owners[0].owner;
				for (var i = 0; i < ownerSet.length; i++) {
					var owner = new ZaDistributionListOwner(ownerSet[i]);
					this[ZaDistributionList.A2_DLOwners].push(owner.name);
				}
			}

			//run the CallBack finall
			var ajxCallbackWhenCompleted = params.ajxCallbackWhenCompleted || null;
			if (ajxCallbackWhenCompleted instanceof Array) {
                for (var i = 0; i < ajxCallbackWhenCompleted.length; i++)
				    ajxCallbackWhenCompleted[i].run1([this])
			} else if (ajxCallbackWhenCompleted instanceof AjxCallback) {
                ajxCallbackWhenCompleted.run1([this])
            }

		}

	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDistributionList.prototype.getAllMembersCallback", null, false);
	}
}


ZaDistributionList.prototype.pageAllMembers = function ( ) {
	var allMembers = this[ZaDistributionList.A2_allMemberHash];
	if ( AjxUtil.isEmpty(allMembers) ) {
		return;
	}

	var pages = new Array();
	var names = AjxUtil.getHashKeys(allMembers); //sorted by name
	for ( var i = 0; i < names.length ; i+= ZaDistributionList.MEMBER_LIST_PAGE_SIZE ) {
		var page = new Array();
		for ( var j = 0; (i + j < names.length) && (j < ZaDistributionList.MEMBER_LIST_PAGE_SIZE); j++ ) {
			var member = allMembers[ names[i+j] ];
			page.push(member);
		}
		pages.push(page);
	}

	this[ZaDistributionList.A2_allMemberPages] = pages;
}

/**
 * Makes a server call to get the distribution list details, if the
 * internal list of members is null
 */

/*
ZaDistributionList.prototype.getMembers = function () {
	if (this.id != null) {
		var soapDoc = AjxSoapDoc.create("GetDistributionListRequest", ZaZimbraAdmin.URN, null);

		var limit = ZaDistributionList.MEMBER_LIST_PAGE_SIZE;
			
		soapDoc.setMethodAttribute("limit", limit);
		if(!this.getAttrs.all && !AjxUtil.isEmpty(this.attrsToGet)) {
			soapDoc.setMethodAttribute("attrs", this.attrsToGet.join(","));
		}
        if (!this.memPagenum) this.memPagenum = 1 ; //by default, first page.
        var offset = (this.memPagenum-1)*limit;
		soapDoc.setMethodAttribute("offset", offset);
			
		var dl = soapDoc.set("dl", this.id);
		dl.setAttribute("by", "id");
		soapDoc.set("name", this.getName());
		try {

			//var command = new ZmCsfeCommand();
			var params = new Object();
			params.soapDoc = soapDoc;
			var reqMgrParams = {
				controller : ZaApp.getInstance().getCurrentController(),
				busyMsg : ZaMsg.BUSY_GET_DL
			}
			var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetDistributionListResponse;	
			//DBG.dumpObj(resp);
            if (resp.dl[0].dynamic === true)
                this[ZaDistributionList.A2_dlType] = ZaDistributionList.DYNAMIC_DL_TYPE;
			var members = resp.dl[0].dlm;
			this[ZaDistributionList.A2_numMembers] = resp.total;
			this[ZaDistributionList.A2_memNumPages] = Math.ceil(resp.total/limit);
			this[ZaDistributionList.A2_memberList] = new Array();
			this[ZaDistributionList.A2_origList] = new Array();
			var len = members ? members.length : 0;
			if (len > 0) {
				for (var i =0; i < len; ++i) {
					var mem = new ZaDistributionListMember(members[i]._content);
					this[ZaDistributionList.A2_memberList].push(mem);
					this[ZaDistributionList.A2_origList].push(mem);
				}
				this[ZaDistributionList.A2_memberList].sort();
				this[ZaDistributionList.A2_origList].sort();
			}
			this.id = resp.dl[0].id;
			this.initFromJS(resp.dl[0]);
			
			//Make a GetAccountMembershipRequest
            if (this[ZaDistributionList.A2_dlType] !== ZaDistributionList.DYNAMIC_DL_TYPE)
			    this[ZaAccount.A2_memberOf] = ZaAccountMemberOfListView.getDlMemberShip(this.id, "id" ) ;
            else
                this[ZaAccount.A2_memberOf] = {	directMemberList: [],
						                        indirectMemberList: [],
		                        				nonMemberList: []
					                            };
			this[ZaAccount.A2_directMemberList + "_more"] = 
				(this[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList].length > ZaAccountMemberOfListView.SEARCH_LIMIT) ? 1: 0;
			this[ZaAccount.A2_indirectMemberList + "_more"] = 
				(this[ZaAccount.A2_memberOf][ZaAccount.A2_indirectMemberList].length > ZaAccountMemberOfListView.SEARCH_LIMIT) ? 1: 0;

            var owners = resp.dl[0].owners;
            var ownerLen = owners ? owners.length: 0;
            this[ZaDistributionList.A2_DLOwners] = new Array();
            if (ownerLen > 0) {
                var ownerSet = owners[0].owner;
                for (var i = 0; i < ownerSet.length; i++) {
                    var owner = new ZaDistributionListOwner(ownerSet[i]);
                    this[ZaDistributionList.A2_DLOwners].push(owner.name);
                }
            }
		} catch (ex) {
			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDistributionList.prototype.getMembers", null, false);
			//DBG.dumpObj(ex);
		}
	} else if (this[ZaDistributionList.A2_memberList] == null){
		this[ZaDistributionList.A2_memberList] = new Array();
	}
	return this[ZaDistributionList.A2_memberList];
};
*/
//ZaItem.loadMethods["ZaDistributionList"].push(ZaDistributionList.prototype.getMembers) ;



ZaDistributionList.removeDeletedMembers = function (mods, obj, dl, finishedCallback) {
	if(!ZaItem.hasRight(ZaDistributionList.REMOVE_DL_MEMBER_RIGHT, obj)) {
		if(finishedCallback && finishedCallback instanceof AjxCallback) {
			finishedCallback.run();
		}
		return;
	}
	var removeMemberSoapDoc, r;
	var command = new ZmCsfeCommand();
	//var member = list.getLast();
	removeMemberSoapDoc = AjxSoapDoc.create("RemoveDistributionListMemberRequest", ZaZimbraAdmin.URN, null);
	removeMemberSoapDoc.set("id", obj.id);
	var len = obj[ZaDistributionList.A2_removeList].length;
	if(len < 1)
		return;
		
	
	for (var i = 0; i < len; i++) {
		removeMemberSoapDoc.set("dlm", obj[ZaDistributionList.A2_removeList][i].toString());
	}

	var params = new Object();
	params.soapDoc = removeMemberSoapDoc;
	params.noAuthToken = true;
	if(finishedCallback && finishedCallback instanceof AjxCallback) {
		params.asyncMode = true;
		params.callback = finishedCallback;
	}

	//store the removelist to be used in modify the account's memberof
	//we only set the memberof after the server side is set. 
	//user a var to store removelist to avoid the removelist can't be cleared when some exception is throwed in command.invoke
	var removeList = obj[ZaDistributionList.A2_removeList];
	obj[ZaDistributionList.A2_removeList] = new Array();
	command.invoke(params);
	ZaDistributionList.modifyAccountDL(obj, removeList, false);
};
ZaItem.modifyMethods["ZaDistributionList"].push(ZaDistributionList.removeDeletedMembers);

ZaDistributionList.addNewMembers = function (mods, obj, dl, finishedCallback) {
	if(!ZaItem.hasRight(ZaDistributionList.ADD_DL_MEMBER_RIGHT,obj)) {
		if(finishedCallback && finishedCallback instanceof AjxCallback) {
			finishedCallback.run();
		}
		return;
	}
	
	var addMemberSoapDoc, r;
	var command = new ZmCsfeCommand();
	addMemberSoapDoc = AjxSoapDoc.create("AddDistributionListMemberRequest", ZaZimbraAdmin.URN, null);
	addMemberSoapDoc.set("id", obj.id);
	var len = obj[ZaDistributionList.A2_addList].length;
	if(len < 1)
		return;
		
	for (var i = 0; i < len; i++) {
		addMemberSoapDoc.set("dlm", obj[ZaDistributionList.A2_addList][i].toString());
	}
	var params = new Object();
	params.soapDoc = addMemberSoapDoc;	
	if(finishedCallback && finishedCallback instanceof AjxCallback) {	
		params.asyncMode = true;
		params.callback = finishedCallback;
	}
	params.noAuthToken = true;
	var addList = obj[ZaDistributionList.A2_addList];
	command.invoke(params);
	obj[ZaDistributionList.A2_addList] = new Array();
	ZaDistributionList.modifyAccountDL(obj, addList, true);
};
ZaItem.modifyMethods["ZaDistributionList"].push(ZaDistributionList.addNewMembers);

ZaDistributionList.modifyAccountDL = function (dl, modifyList, isAdd){
	var tabGroup = ZaApp.getInstance().getTabGroup();
	var currentDl =  { name: dl.name, id: dl.id } ;
	for(var i = 0; i < modifyList.length; i++){	
		var currentItem = modifyList[i];
		var accountName = null;
		
		accountName = currentItem.name;
		
		//when the item is added by search result item, it reduces the match time.
		if(currentItem.type == ZaItem.ALIAS && currentItem.attrs){
			accountName = currentItem.attrs[ZaAlias.A_targetAccount];
		}
		
		if(!accountName)
			continue;
		
		var currentView = null;
		for (var iTab=0; iTab < ZaAppTabGroup._TABS.size(); iTab++) {
			var tab = ZaAppTabGroup._TABS.get(iTab) ;
			var v = tab.getAppView() ;
			//Only update the data for account item opened on the tab. 
			//For the items haven't been opened, it will fetch newest data from server when it is opened.
			if (v && v._containedObject && v._containedObject.name && v.constructor && (v.constructor==ZaAccountXFormView)) {
				if (accountName == v._containedObject.name ) {//firstly check for account
					currentView = v;
					break;
				}else if(v._containedObject.attrs && v._containedObject.attrs[ZaAccount.A_zimbraMailAlias]){
					//secondly match the name for account's alias name
					var aliasList = v._containedObject.attrs[ZaAccount.A_zimbraMailAlias];
					var isAliasMatch = false;
					for(var iAlias = 0; iAlias < aliasList.length; iAlias++){
						if(accountName == aliasList[iAlias]){
							isAliasMatch = true;
							break;
						}
					}
					if(isAliasMatch){
						currentView = v;
						break;
					}
				}
			}
		}	
		
		if(!currentView)
			continue;
		
		var isFind = false;
		var currentInDL = currentView._containedObject[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList];
		for(var j = 0; j < currentInDL.length; j++){
			if(currentInDL[j].name == currentDl.name){
				isFind = true;
				break;
			}
		}	
		
		if(!isFind && isAdd){
			currentInDL.push(currentDl);
		}else if(isFind && !isAdd){
			currentInDL.splice(j, 1);
		}else{
			continue;
		}
		
		currentView._localXForm.setInstanceValue(currentInDL, ZaAccount.A2_directMemberList);
	}
}

ZaDistributionList.addNewMembersCreateMethod = function (obj, dl, finishedCallback) {
    ZaDistributionList.addNewMembers.call (this, null, obj, dl, finishedCallback) ;  
}
ZaItem.createMethods["ZaDistributionList"].push(ZaDistributionList.addNewMembersCreateMethod);


ZaDistributionList.prototype.initFromJS = 
function (dl) {
	if(!dl)
		return;
		
	this.attrs = new Object();	
	this.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
	this.name = dl.name;
	this.id = dl.id;
    this.dynamic = dl.dynamic;
	var len = dl.a.length;

	for(var ix = 0; ix < len; ix++) {
		//we have to handle the special case for DL because server returns the dl itself as the zimbraMailAlias
		if ( dl.a[ix].n == ZaAccount.A_zimbraMailAlias
					&& dl.a[ix]._content == this.name) {				
			continue ;
		}
		
		if(!this.attrs[[dl.a[ix].n]]) {
			this.attrs[[dl.a[ix].n]] = dl.a[ix]._content;
		} else {
			if(!(this.attrs[[dl.a[ix].n]] instanceof Array)) {
				this.attrs[[dl.a[ix].n]] = [this.attrs[[dl.a[ix].n]]];
			} 
			this.attrs[[dl.a[ix].n]].push(dl.a[ix]._content);
		}
	}
	
	if(!this.attrs[ZaAccount.A_description])
		this.attrs[ZaAccount.A_description] = [];
		
	if(!(this.attrs[ZaAccount.A_description] instanceof Array)) {
		this.attrs[ZaAccount.A_description] = [this.attrs[ZaAccount.A_description]];
	}	

}

ZaDistributionList.compareTwoMembers = function (val1, val2) {
	var a = AjxUtil.isEmpty(val1);
	var b = AjxUtil.isEmpty(val2);
	if(a && !b)
		return -1;
	
	if(!a && b)
		return 1;
		
	if(a && b)
		return 0;
		
	if(AjxUtil.isEmpty(val1.name) && AjxUtil.isEmpty(val2.name))
		return 0;
	
	if(val1.name == val2.name)
		return 0;
	
	if (val1.name < val2.name)
		return -1;
		
	if (val1.name > val2.name)
		return 1;
	
	return 0;
		
}
/**
 * Small wrapper class for a distribution list member.
 * The id is needed at a higher level for DwtLists to work correctly.
 */
ZaDistributionListMember = function(name) {
	this[ZaAccount.A_name] = name;
	this.id = "ZADLM_" + name;

}

ZaDistributionListMember.prototype.toString = function () {
	return this[ZaAccount.A_name];
};

ZaDistributionListOwner = function (entry) {
    this[ZaAccount.A_name] = entry.name;
    this.id = entry.id;
    this.type = entry.type;
}

ZaDistributionListOwner.prototype.toString = function () {
	return this[ZaAccount.A_name];
};

ZaDistributionList.myXModel = {

	items: [
		{id:"id", type:_STRING_},
		{id:ZaDistributionList.A2_query, type:_STRING_},
        {id:ZaDistributionList.A2_dlType, type:_STRING_},
		{id:ZaDistributionList.A2_pagenum, type:_NUMBER_, defaultValue:1},
		{id:ZaDistributionList.A2_poolPagenum, type:_NUMBER_, defaultValue:1},
		{id:ZaDistributionList.A2_poolNumPages, type:_NUMBER_, defaultValue:1},		
		{id:ZaDistributionList.A2_memPagenum, type:_NUMBER_, defaultValue:1},
		{id:ZaDistributionList.A2_memNumPages, type:_NUMBER_, defaultValue:1},	
		{id:ZaDistributionList.A2_memberPool, type:_LIST_},
		{id:ZaDistributionList.A2_memberList, type:_LIST_},
		{id:ZaDistributionList.A2_allMemberHash, type:_OBJECT_, defaultValue:{}},
		{id:ZaDistributionList.A2_allMemberPages, type:_LIST_, defaultValue:[]},
		{id:ZaDistributionList.A2_origList, type:_LIST_},
		{id:ZaDistributionList.A2_addList, type:_LIST_},
		{id:ZaDistributionList.A2_removeList, type:_LIST_},
		{id:ZaDistributionList.A2_optionalAdd, type:_STRING_},
		{id:ZaAccount.A_name, type:_STRING_,  required:true,
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
        {id:ZaDistributionList.A2_DLOwners, type:_LIST_, ref:ZaDistributionList.A2_DLOwners, listItem:{type:_STRING_}},
        {id:ZaDistributionList.A2_owners_selection_cache, type:_LIST_},
		{id:ZaDistributionList.A2_members, type:_LIST_},
		ZaItem.descriptionModelItem,
		{id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId},
        {id:ZaDistributionList.A_memberOfURL, ref:"attrs/" + ZaDistributionList.A_memberOfURL, type:_STRING_,
            constraints: {type:"method", value:
			   function (value, form, formItem, instance) {
				   value = AjxStringUtil.trim(value);
                   var isAclGroup = instance.attrs[ZaDistributionList.A_zimbraIsACLGroup];
                   if (isAclGroup != "TRUE" && !value) {
                       throw AjxMsg.valueIsRequired;
                   }
                   return value;
			   }
			}
        },
        {id:ZaDistributionList.A_zimbraIsACLGroup, ref:"attrs/"+ZaDistributionList.A_zimbraIsACLGroup, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaItem.A_zimbraCreateTimestamp, ref:"attrs/" + ZaItem.A_zimbraCreateTimestamp},
        {id:ZaAccount.A_zimbraHideInGal, type:_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraHideInGal, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_notes, ref:"attrs/"+ZaAccount.A_notes, type:_STRING_},
        {id:ZaAccount.A_mailHost, type:_STRING_, ref:"attrs/"+ZaAccount.A_mailHost},
        {id:ZaAccount.A2_autoMailServer, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_displayname, type:_STRING_, ref:"attrs/"+ZaAccount.A_displayname},
		{id:ZaAccount.A_zimbraMailAlias, type:_LIST_, ref:"attrs/"+ZaAccount.A_zimbraMailAlias, listItem:{type:_STRING_}},
		{id:ZaDistributionList.A_mailStatus, ref:"attrs/"+ZaDistributionList.A_mailStatus, type:_STRING_},
		{id:ZaDistributionList.A_zimbraDistributionListSubscriptionPolicy, ref:"attrs/"+ZaDistributionList.A_zimbraDistributionListSubscriptionPolicy, type:_STRING_},
		{id:ZaDistributionList.A_zimbraDistributionListUnsubscriptionPolicy, ref:"attrs/"+ZaDistributionList.A_zimbraDistributionListUnsubscriptionPolicy, type:_STRING_},
        {
            id: ZaDistributionList.A_zimbraDistributionListSendShareMessageToNewMembers,
            ref: "attrs/" + ZaDistributionList.A_zimbraDistributionListSendShareMessageToNewMembers,
            type: _ENUM_,
            choices: ZaModel.BOOLEAN_CHOICES
        },
		{id:ZaDistributionList.A2_membersSelected, type:_LIST_},
		{id:ZaDistributionList.A2_nonmembersSelected, type:_LIST_},
		{id:ZaDistributionList.A2_memberPoolSelected, type:_LIST_},
		{id:ZaDistributionList.A2_directMemberSelected, type:_LIST_},
		{id:ZaDistributionList.A2_indirectMemberSelected, type:_LIST_},
		{id:ZaAccount.A2_memberOf, type:_OBJECT_, items: [
			{id:ZaDistributionList.A2_directMemberList, type:_LIST_},
			{id:ZaDistributionList.A2_indirectMemberList, type:_LIST_},
			{id:ZaDistributionList.A2_nonMemberList, type:_LIST_}
			]
		},		
		{id:(ZaAccount.A2_directMemberList + "_more"), type:_LIST_},
		{id:(ZaAccount.A2_directMemberList + "_offset"), type:_LIST_},
		{id:(ZaAccount.A2_indirectMemberList + "_more"), type:_LIST_},
		{id:(ZaAccount.A2_indirectMemberList + "_offset"), type:_LIST_},	
		{id:(ZaAccount.A2_nonMemberList + "_more"), type:_LIST_},
		{id:(ZaAccount.A2_nonMemberList + "_offset"), type:_LIST_},
		{id:ZaDistributionList.A2_alias_selection_cache, type:_LIST_},
        {id:ZaDistributionList.A_zimbraPrefReplyToEnabled, type:_ENUM_, ref:"attrs/"+ZaDistributionList.A_zimbraPrefReplyToEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaDistributionList.A_zimbraPrefReplyToDisplay, type:_STRING_, ref:"attrs/"+ZaDistributionList.A_zimbraPrefReplyToDisplay},
        {id:ZaDistributionList.A_zimbraPrefReplyToAddress, type:_EMAIL_ADDRESS_, ref:"attrs/"+ZaDistributionList.A_zimbraPrefReplyToAddress}
	]
};

// Don't disturbe the DL view rendering process, when view is realy, start to update data.
ZaDistributionList.prototype.schedulePostLoading = function (controller) {
    //async load the member
    var act = new AjxTimedAction(this, ZaDistributionList.prototype.getAllMembers, {controller:controller});
    AjxTimedAction.scheduleAction(act, 100);
}

ZaDistributionList.getRelatedList =
function (parentPath) {
    var alias = this.attrs[ZaAccount.A_zimbraMailAlias];
    var membersNum = this[ZaDistributionList.A2_numMembers];
    var Tis = [];
    if(alias.length > 0) {
        var aliasTi = new ZaTreeItemData({
                    text: ZaMsg.TABT_Aliases,
                    //type: 1,
                    count:alias.length,
                    image:"AccountAlias",
                    mappingId: ZaZimbraAdmin._DL_ALIAS_LIST_VIEW,
                    path: parentPath + ZaTree.SEPERATOR + this.name + ZaTree.SEPERATOR + ZaMsg.TABT_Aliases
                    }
                );
        aliasTi.setData("aliasTargetId", this.id);
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DL_ALIAS_LIST_VIEW] = ZaOverviewPanelController.aliasListTreeListener;
        Tis.push(aliasTi);
    }
    if(membersNum > 0) {
        var membersTi = new ZaTreeItemData({
                    text: ZaMsg.DLXV_LabelListMembers,
                    count:membersNum,
                    image:"DistributionList",
                    mappingId: ZaZimbraAdmin._DL_MEMBERS_LIST_VIEW,
                    path: parentPath + ZaTree.SEPERATOR + this.name + ZaTree.SEPERATOR + ZaMsg.DLXV_LabelListMembers
                    }
                );
        membersTi.setData("dlItem", this);
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DL_MEMBERS_LIST_VIEW] = ZaOverviewPanelController.memberListInDLTreeListener;
        Tis.push(membersTi);
    }
    return Tis;

}
ZaItem.getRelatedMethods["ZaDistributionList"].push(ZaDistributionList.getRelatedList);