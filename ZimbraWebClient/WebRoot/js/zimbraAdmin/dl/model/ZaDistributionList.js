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
 /**
 * @author EMC
 **/
ZaDistributionList = function(app, id, name, memberList, description, notes) {
	ZaItem.call(this, app);
	this.attrs = new Object();
	this.attrs[ZaDistributionList.A_mailStatus] = "enabled";
	this.attrs[ZaAccount.A_zimbraMailAlias] = [];
	this.id = (id != null)? id: null;
	this.type = ZaItem.DL;
	this.name = (name != null) ? name: null;
	this._selfMember = new ZaDistributionListMember(this.name);
	this._memberList = (memberList != null)? AjxVector.fromArray(memberList): new AjxVector();
	this.memberPool = new Array();
	if (description != null) this.attrs.description = description;
	if (notes != null) this.attrs.zimbraNotes = notes;
	this.numMembers = 0;
	//Utility members
	this._origList = (memberList != null)? AjxVector.fromArray(memberList): new AjxVector();
	this._addList = new AjxVector();
	this._removeList = new AjxVector();
	this._dirty = true;
	this.poolPagenum=1;
	this.poolNumPages=1;
	this.memPagenum=1;
	this.memNumPages=1;
	this.query="";
	//membership related instance variables
	this[ZaAccount.A2_memberOf] = {	directMemberList: [],
									indirectMemberList: [],
									nonMemberList: []
								};
	this[ZaAccount.A2_directMemberList + "_more"] = 0;
	this[ZaAccount.A2_directMemberList + "_offset"] = 0;
	this[ZaAccount.A2_indirectMemberList + "_more"] = 0;
	this[ZaAccount.A2_indirectMemberList + "_offset"] = 0;	
	this[ZaAccount.A2_nonMemberList + "_more"] = 0;
	this[ZaAccount.A2_nonMemberList + "_offset"] = 0;
}

ZaDistributionList.prototype = new ZaItem;
ZaDistributionList.prototype.constructor = ZaDistributionList;

ZaDistributionList.EMAIL_ADDRESS = "ZDLEA";
ZaDistributionList.DESCRIPTION = "ZDLDESC";
ZaDistributionList.ID = "ZDLID";
ZaDistributionList.MEMBER_QUERY_LIMIT = 25;
//ZaDistributionList.A_isgroup = "isgroup";
ZaDistributionList.A_zimbraGroupId = "zimbraGroupId";

ZaDistributionList.A_mailStatus = "zimbraMailStatus";

ZaDistributionList._dlStatus = {
	enabled  : ZaMsg.DL_Status_enabled ,
	disabled : ZaMsg.DL_Status_disabled
}

ZaDistributionList.searchAttributes = AjxBuffer.concat(ZaAccount.A_displayname,",",
													   ZaItem.A_zimbraId,  "," , 
													   ZaAccount.A_mailHost , "," , 
													   ZaAccount.A_uid ,"," , 
													   ZaAccount.A_description, ",",
													   ZaDistributionList.A_mailStatus);


// ==============================================================
// public methods
// ==============================================================

ZaDistributionList.prototype.clone = function () {
	var memberList;
	if(this._memberList) {
		memberList = this._memberList.getArray();
	}
	var dl = new ZaDistributionList(this._app, this.id, this.name, memberList, this.description, this.notes);
 	if (memberList != null) {
 		dl._memberList = new AjxVector();
 		for (var i = 0 ; i < memberList.length; ++i) {
 			dl._memberList.add(memberList[i]);
 		}
 		dl._origList = new AjxVector();
 		for (var i = 0 ; i < memberList.length; ++i) {
 			dl._origList.add(memberList[i]);
 		}
 	} else {
 		this._memberList = null;
 		this._origList = null;
 	}

	var val, tmp;
	for (key in this.attrs) {
		val = this.attrs[key];
		if (AjxUtil.isArray(val)){
			tmp = new Array();
			for (var i = 0; i < val.length; ++i){
				tmp[i] = val[i];
			}
			val = tmp;
		}
		dl.attrs[key] = val;
	}
	dl.pagenum = this.pagenum;
	dl.query = this.query;
	dl.poolPagenum = this.poolPagenum;
	dl.poolNumPages = this.poolNumPages;
	dl.memPagenum = this.memPagenum;
	dl.memNumPages = this.memNumPages;	
	//dl.isgroup = this.isgroup ;
	
	//clone the membership information
	dl[ZaAccount.A2_memberOf] = this [ZaAccount.A2_memberOf];	
	return dl;
};

/**
 * Removes a list of members
 * This keeps the internal add, and remove lists up to date.
 * @param arr (Array) - array of ZaDistributionListMembers to remove
 * @return boolean (true if at least one member was removed)
 */
ZaDistributionList.prototype.removeMembers = function (arr) {
	var removed = this._removeFromList(arr, this._memberList);
	this._removeFromList(arr, this._addList);
	if (removed) {
		this._addToRemoveList(arr, this._removeList);
	}
	return removed;
};

ZaDistributionList.prototype.refresh = function () {
	this.getMembers();
}

/**
 * Adds a list of members
 * This keeps the internal add, and remove lists up to date.
 * @param arr (newMembersArrayOrVector) - array or AjxVector of ZaDistributionListMembers
 */
ZaDistributionList.prototype.addMembers = function (newMembersArrayOrVector) {
	var added = false;
	if (newMembersArrayOrVector != null) {
		// Rules:
		// Don't add yourself -- currently if you add yourself, we just do nothing.
		// Don't add duplicates.
		added = this._addToMemberList(newMembersArrayOrVector);
		this._addToAddList(newMembersArrayOrVector);
		this._removeFromRemoveList(newMembersArrayOrVector);
	}
	return added;
};

/**
 * Remove duplicates from the members list
 */
ZaDistributionList.prototype.dedupMembers = function () {
	this._dedupList(this._memberList);
};

/*
ZaDistributionList.prototype.remove = function () {
	return this._remove(this.id);
};*/

ZaDistributionList.prototype.remove = 
function(callback) {
	var soapDoc = AjxSoapDoc.create("DeleteDistributionListRequest", ZaZimbraAdmin.URN, null);
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
		controller : this._app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_RENAME_DL
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.RenameDistributionListResponse;	
	this.initFromJS(resp.dl[0]);	
}

/**
* @method modify
* Updates ZaDistributionList attributes (SOAP)
* @param mods set of modified attributes and their new values
*/
ZaDistributionList.prototype.modify =
function(tmpObj, callback) {
	//update the object
	var soapDoc = AjxSoapDoc.create("ModifyDistributionListRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);

	for (var aname in tmpObj.attrs) {
		if(aname == ZaItem.A_objectClass || aname==ZaAccount.A_mail || aname == ZaItem.A_cn
			|| aname == ZaItem.A_zimbraId || aname == ZaAccount.A_uid
			|| aname == ZaDistributionList.A_zimbraGroupId || aname == ZaAccount.A_zimbraMailAlias) {
			continue;
		}		
		//multi-value attribute
		if(tmpObj.attrs[aname] instanceof Array) {
			var cnt = tmpObj.attrs[aname].length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					if(tmpObj.attrs[aname][ix]) { //if there is an empty element in the array - don't send it
						var attr = soapDoc.set("a", tmpObj.attrs[aname][ix]);
						attr.setAttribute("n", aname);
					}
				}
			} else {
				var attr = soapDoc.set("a", "");
				attr.setAttribute("n", aname);
			}
		} else {
			var attr = soapDoc.set("a", tmpObj.attrs[aname]);
			attr.setAttribute("n", aname);
		}
	}
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	if(callback) {
		params.asyncMode = true;
		params.callback = callback;
		command.invoke(params);
	} else {
		var reqMgrParams = {
			controller : this._app.getCurrentController(),
			busyMsg : ZaMsg.BUSY_MODIFY_DL
		}
		var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.ModifyDistributionListResponse;	
		this.initFromJS(resp.dl[0]);		
	}
	return true;
}

/**
* Creates a new ZaDistributionList. This method makes SOAP request to create a new account record. 
* @param tmpObj
* @param app 
* @return ZaDistributionList
**/
ZaDistributionList.create =
function(tmpObj, callback) {	
	//create SOAP request
	var soapDoc = AjxSoapDoc.create("CreateDistributionListRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set(ZaAccount.A_name, tmpObj.name);
	var resp;
	for (var aname in tmpObj.attrs) {
		if(aname == ZaItem.A_objectClass || aname == ZaAccount.A_mail 
			|| aname == ZaItem.A_zimbraId || aname == ZaAccount.A_uid
			|| aname == ZaAccount.A_zimbraMailAlias) {
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
		var command = new ZmCsfeCommand();
		var params = new Object();
		params.asyncMode = true;
		params.callback = callback;
		//command.invoke(params);		
		params.soapDoc = soapDoc;	
		command.invoke(params);
		return true;
		//resp = command.invoke(params).Body.CreateDistributionListResponse;	
	} catch (ex) {
		switch(ex.code) {
			case ZmCsfeException.DISTRIBUTION_LIST_EXISTS:
				app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_EXISTS);
			break;
			case ZmCsfeException.ACCT_EXISTS:
				app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_EXISTS);
			break;
			default:
				app.getCurrentController()._handleException(ex, "ZaDistributionList.create", null, false);
			break;
		}
		return null;
	}
	/*var dl = new ZaDistributionList(app);
	dl.initFromJS(resp.dl[0]);
	//dl.initFromDom(resp.firstChild);
		
	if(tmpObj._addList) {
		dl.addNewMembers(tmpObj._addList);
		dl.refresh();
	}
	
	//add the membership information
	//update the member of first
	try {
		if (ZaAccountMemberOfListView._addList.length >0) { //you have new membership to be added.
			ZaAccountMemberOfListView.addNewGroupsBySoap(dl, ZaAccountMemberOfListView._addList);
		}	
		ZaAccountMemberOfListView._addList = []; //reset
	}catch (ex){
		ZaAccountMemberOfListView._addList = []; //reset
		this._app.getCurrentController()._handleException(ex, "ZaDistributionList.create: add group failed", null, false);	//try not to halt the account modification	
	}
	//remvoe may not needed during the creation time.
	try {
		if (ZaAccountMemberOfListView._removeList.length >0){//you have membership to be removed
			ZaAccountMemberOfListView.removeGroupsBySoap(dl, ZaAccountMemberOfListView._removeList);
		}
		ZaAccountMemberOfListView._removeList = []; //reset
	}catch (ex){
		ZaAccountMemberOfListView._removeList = []; //reset
		this._app.getCurrentController()._handleException(ex, "ZaDistributionList.create: remove group failed", null, false);		
	}
	
	dl.markClean();	
	return dl;*/
}

ZaDistributionList.checkValues = function(tmpObj, app) {
	if(tmpObj.name == null || tmpObj.name.length < 1) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_DL_NAME_REQUIRED);
		return false;
	}
	
	//var emailRegEx = /^([a-zA-Z0-9_\-])+((\.)?([a-zA-Z0-9_\-])+)*@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	/*if(!AjxUtil.EMAIL_SHORT_RE.test(tmpObj.name) ) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_DL_NAME_INVALID);
		return false;
	}*/	
	
	if(tmpObj.name.lastIndexOf ("@")!=tmpObj.name.indexOf ("@")) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_DL_NAME_INVALID);
		return false;
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

ZaDistributionList.prototype.setName = function (name) {
	if (name != this.name) {
		if (this._origName == null) {
			this._origName = this.name;
		}
		this.name = name;
	} 
};

/**
 * Makes a server call to get the distribution list details, if the
 * internal list of members is null
 */
// TODO -- handle dynamic limit and offset
ZaDistributionList.prototype.getMembers = function (limit) {
	//DBG.println("Get members: memberList = " , this._memberList, "$");
	if (this.id != null) {
		this._memberList = null;
		var soapDoc = AjxSoapDoc.create("GetDistributionListRequest", ZaZimbraAdmin.URN, null);
		if(!limit)
			limit = ZaDistributionList.MEMBER_QUERY_LIMIT;
			
		soapDoc.setMethodAttribute("limit", limit);
		
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
				controller : this._app.getCurrentController(),
				busyMsg : ZaMsg.BUSY_GET_DL
			}
			var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetDistributionListResponse;	
			//DBG.dumpObj(resp);
			var members = resp.dl[0].dlm;
			this.numMembers = resp.total;
			this.memNumPages = Math.ceil(this.numMembers/limit);
			var len = members ? members.length : 0;
			if (len > 0) {
				this._memberList = new AjxVector();
				this._origList = new AjxVector();
				for (var i =0; i < len; ++i) {
					var mem = new ZaDistributionListMember(members[i]._content);
					this._memberList.add(mem);
					this._origList.add(mem);
				}
				this._memberList.sort();
				this._origList.sort();
			}
			this.id = resp.dl[0].id;
			this.initFromJS(resp.dl[0]);
			
			//Make a GetAccountMembershipRequest
	this[ZaAccount.A2_memberOf] = ZaAccountMemberOfListView.getDlMemberShip(this._app, this.id, "id" ) ;
	this[ZaAccount.A2_directMemberList + "_more"] = 
			(this[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList].length > ZaAccountMemberOfListView.SEARCH_LIMIT) ? 1: 0;
	this[ZaAccount.A2_indirectMemberList + "_more"] = 
			(this[ZaAccount.A2_memberOf][ZaAccount.A2_indirectMemberList].length > ZaAccountMemberOfListView.SEARCH_LIMIT) ? 1: 0;
			
		} catch (ex) {
			this._app.getCurrentController()._handleException(ex, "ZaDistributionList.prototype.getMembers", null, false);
			//DBG.dumpObj(ex);
		}
	} else if (this._memberList == null){
		this._memberList = new AjxVector();
	}
	return this._memberList;
};

ZaDistributionList.prototype.getMembersArray = function () {
	if (this._memberList != null){
		return this._memberList.getArray();
	}
	return [];
};

// ==============================================================
// private internal methods
// ==============================================================

ZaDistributionList.prototype._addToMemberList = function (newMembersArrayOrVector) {
	return this._addToList(newMembersArrayOrVector, this._memberList);
};

ZaDistributionList.prototype._addToAddList = function (arrayOrVector) {
	var list = this._origList;
	var func = function (item) {
		if (list.binarySearch(item) != -1) {
			return false;
		}
		return true;
	}
	return this._addToList(arrayOrVector, this._addList, func);
};

ZaDistributionList.prototype._addToRemoveList = function (arrayOrVector) {
	var list = this._origList;
	var func = function (item) {
		if (list.binarySearch(item) == -1) {
			return false;
		}
		return true;
	}
	return this._addToList(arrayOrVector, this._removeList, func);
};

ZaDistributionList.prototype._removeFromRemoveList = function (arrayOrVector) {
	this._removeFromList(arrayOrVector, this._removeList);
};

ZaDistributionList.prototype._addToList  = function (arrayOrVector, vector, preAddCallback) {
	var added = false;
	if (AjxUtil.isArray(arrayOrVector)) {
		added = this._addArrayToList(arrayOrVector, vector, preAddCallback);
	} else if (AjxUtil.isInstance(arrayOrVector, AjxVector)){
		added = this._addVectorToList(arrayOrVector, vector, preeAddCallback);
	}
	this._dedupList(vector);
	return added;
};

/**
* Removes @param arrayOrVector from @vector, then
* removes duplicates from @param vector
* @return boolean (true if at least one member of arrayOrVector was removed from vector)
**/
ZaDistributionList.prototype._removeFromList  = function (arrayOrVector, vector) {
	var removed = false;
	if (AjxUtil.isArray(arrayOrVector)) {
		removed = this._removeArrayFromList(arrayOrVector, vector);
	}
	this._dedupList(vector);
	return removed;
};


ZaDistributionList.prototype._addArrayToList = function (newArray, vector, preAddCallback) {
	var add = true;
	var cnt = newArray.length;
	for (var i = 0; i < cnt; i++) {
		if(!newArray[i])
			continue;
			
		if (newArray[i].toString() != this._selfMember.toString()) {
			add = true;
			if (preAddCallback != null){
				add = preAddCallback(newArray[i]);
			}
			if (add) vector.add(newArray[i]);
		}
	}
	return (newArray.length > 0)? true: false;
};

ZaDistributionList.prototype._addVectorToList = function (newVector, vector) {
	var i = -1;
	var added = false;
	if ( (i = newVector.binarySearch(this._selfMember)) != -1) {
		if (i > 0){
			vector.merge(vector.size(),newVector.slice(0,i));
		}
		if (i+1 < newVector.length) {
			vector.merge(vector.size(),newVector.slice(i+1));
		}
	} else {
		vector.merge(vector.size(),newVector);
	}
	return (vector.size() > 0)? true: false;
};

/**
* removes members of @param newArray from @param vector
* @param newArray - contains members to remove 
* @param vector  - List to remove from
* @return boolean (true if at least one member was removed)
**/
ZaDistributionList.prototype._removeArrayFromList = function (newArray, vector) {
	var vecArray = vector.getArray(); //get direct reference to underlying array
	var ret = false;
	for (var i = 0; i < newArray.length ; ++i) {
		for (var j = 0; j < vecArray.length; ++j) {
			if (vecArray[j].toString() == newArray[i].toString()) {
				vecArray.splice(j,1);
				ret = true;
			}
		}
	}
	return ret;
};

ZaDistributionList.prototype._dedupList = function (vector) {
	vector.sort();
	var arr = vector.getArray();
	var len = arr.length;
	var i;
	var prev = null;
	var curr = null;
	for (i = len; i >= 0; --i) {
		curr = arr[i];
		if((curr!=null) && (prev!=null) && curr.toString() == prev.toString()) {
			arr.splice(i,1);
		} else {
			prev = curr;
		}
	}
};
/*
ZaDistributionList.prototype.addMemberCallback = function (params, resp) {
	if(resp.isException && resp.isException()) {
		if(params.finishedCallback)
			params.finishedCallback.run(false, resp.getException());
			
		return;
	} 
	if(this.stopAddingMembers) {
		if(params.finishedCallback)
			params.finishedCallback.run(false);
		return;
	} else if(params.list && (params.list instanceof Array) && params.list.length) {
		this.addNewMembersAsync(params.list,params.finishedCallback);
	} else {
		if(params.finishedCallback)
			params.finishedCallback.run(true);
	}
};*/

ZaDistributionList.prototype.addNewMembersAsync = function (obj, finishedCallback) {
	var addMemberSoapDoc, r;
	var command = new ZmCsfeCommand();
//	var member = list.getLast();
	addMemberSoapDoc = AjxSoapDoc.create("AddDistributionListMemberRequest", ZaZimbraAdmin.URN, null);
	addMemberSoapDoc.set("id", this.id);
	var len = obj._addList.getArray().length;
	for (var i = 0; i < len; i++) {
		addMemberSoapDoc.set("dlm", obj._addList.getArray()[i].toString());
	}
	var params = new Object();
	params.soapDoc = addMemberSoapDoc;	
	params.asyncMode = true;
	params.callback = finishedCallback;
	//params.callback = new AjxCallback(this, this.addMemberCallback, {list:list,finishedCallback:finishedCallback});
	obj._addList = new AjxVector();
	command.invoke(params);
};

ZaDistributionList.prototype.addNewMembers = function (list) {
	var addArray = list.getArray();
	var len = addArray.length;
	var addMemberSoapDoc;
	//var command = new ZmCsfeCommand();
	for (var i = 0; i < len; ++i) {
		addMemberSoapDoc = AjxSoapDoc.create("AddDistributionListMemberRequest", ZaZimbraAdmin.URN, null);
		addMemberSoapDoc.set("id", this.id);
		addMemberSoapDoc.set("dlm", addArray[i].toString());
		var params = new Object();
		params.soapDoc = addMemberSoapDoc;	
		var reqMgrParams = {
			controller : this._app.getCurrentController(),
			busyMsg : ZaMsg.BUSY_ADD_DL_MEMBER
		}
		ZaRequestMgr.invoke(params, reqMgrParams).Body.AddDistributionListMemberResponse;
	}
};

ZaDistributionList.prototype.removeDeletedMembersAsync = function (list, finishedCallback) {
	var removeMemberSoapDoc, r;
	var command = new ZmCsfeCommand();
	var member = list.getLast();
	removeMemberSoapDoc = AjxSoapDoc.create("RemoveDistributionListMemberRequest", ZaZimbraAdmin.URN, null);
	removeMemberSoapDoc.set("id", this.id);
	removeMemberSoapDoc.set("dlm", member.toString());
	var params = new Object();
	params.soapDoc = removeMemberSoapDoc;	
	params.asyncMode = true;
	params.callback = finishedCallback;
	//params.callback = new AjxCallback(this, this.addMemberCallback, {list:list,finishedCallback:finishedCallback});
	list.removeLast();
	command.invoke(params);
};

ZaDistributionList.prototype.removeDeletedMembers = function (list) {
	var removeArray = list.getArray();
	var len = removeArray.length;
	var addMemberSoapDoc, r, removeMemberSoapDoc;
	//var command = new ZmCsfeCommand();	
	for (var i = 0; i < len; ++i) {
		removeMemberSoapDoc = AjxSoapDoc.create("RemoveDistributionListMemberRequest", ZaZimbraAdmin.URN, null);
		removeMemberSoapDoc.set("id", this.id);
		removeMemberSoapDoc.set("dlm", removeArray[i].toString());
		var params = new Object();
		params.soapDoc = removeMemberSoapDoc;	
		var reqMgrParams = {
			controller : this._app.getCurrentController(),
			busyMsg : ZaMsg.BUSY_REMOVE_DL_MEMBER
		}
		ZaRequestMgr.invoke(params, reqMgrParams).Body.RemoveDistributionListMemberResponse;		
	}
};

ZaDistributionList.prototype.initFromDom = function(node) {
	this.name = node.getAttribute("name");
	this._selfMember = new ZaDistributionListMember(this.name);
	this.id = node.getAttribute("id");
	this.attrs = new Object();

	var children = node.childNodes;
	for (var i=0; i< children.length;  i++) {
		var child = children[i];
		if (child.nodeName == 'a'){
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
		} else if (child.nodeName == 'member') {
			if (this._memberList == null) this._memberList = new AjxVector();
			this._memberList.add(child.getAttribute('name'));
		}
	}
	if (this._memberList != null){
		this._origList = new AjxVector();
 		for (var i = 0 ; i < this._memberList.length; ++i) {
 			this._origList.add(this._memberList[i]);
 		}
		this._memberList.sort();
		this._origList.sort();
	}
};

ZaDistributionList.prototype.initFromJS = 
function (dl) {
	if(!dl)
		return;
		
	this.attrs = new Object();	
	this.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
	this.name = dl.name;
	this.id = dl.id;
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
	
}

ZaDistributionList.prototype.removeAllMembers = function () {
	var arr = this._memberList.getArray();
	this.setMembers();
	this._removeFromList(arr, this._addList);
	this._addToList(arr, this._removeList);
};

ZaDistributionList.prototype.setMembers = function (list) {
	if (list == null) list = [];
	return this._memberList = AjxVector.fromArray(list);
};

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


ZaDistributionList.myXModel = {
	getMemberPool: function (model, instance) {
		return instance.memberPool;
	},
	setMemberPool: function (value, instance, parentValue, ref) {
		instance.memberPool = value;
	},
	// transform a vector into something the list view will be 
	// able to handle
	getMembersArray: function (model, instance) {
		var arr = instance.getMembersArray();
		var tmpArr = new Array();
		var tmp;
		for (var i = 0; i < arr.length; ++i ){
			tmp = arr[i];
			if (!AjxUtil.isObject(arr[i])){
				tmp = new ZaDistributionListMember(arr[i]);
			}
			tmpArr.push(tmp);
		}
		return tmpArr;
	},
	setMembersArray: function (value, instance, parentValue, ref) {
		instance.setMembers(value);
	},
	items: [
		{id:"query", type:_STRING_},
		{id:"poolPagenum", type:_NUMBER_, defaultValue:1},
		{id:"poolNumPages", type:_NUMBER_, defaultValue:1},		
		{id:"memPagenum", type:_NUMBER_, defaultValue:1},
		{id:"memNumPages", type:_NUMBER_, defaultValue:1},	
		{id: "memberPool", type:_LIST_, setter:"setMemberPool", setterScope:_MODEL_, getter: "getMemberPool", getterScope:_MODEL_},
		{id: "optionalAdd", type:_UNTYPED_},
		{id:ZaAccount.A_name, type:_EMAIL_ADDRESS_, setter:"setName", setterScope: _INSTANCE_, required:true,
		 constraints: {type:"method", value:
					   function (value, form, formItem, instance) {
						   var parts = value.split('@');
						   if (parts[0] == null || parts[0] == ""){
							   // set the name, so that on refresh, we don't display old data.
							   throw ZaMsg.DLXV_ErrorNoListName;
						   } else {
							   //var re = ZaDistributionList._validEmailPattern;
							   //if (AjxUtil.EMAIL_SHORT_RE.test(value)) {
							   if(value.lastIndexOf ("@")==value.indexOf ("@")) {
								   return value;
							   } else {
								   throw ZaMsg.DLXV_ErrorInvalidListName;
							   }
						   }
					   }
			}
		},
		{id: "members", type:_LIST_, getter: "getMembersArray", getterScope:_MODEL_, setter: "setMembersArray", setterScope:_MODEL_},
		{id:ZaAccount.A_description,ref:"attrs/"+ZaAccount.A_description, type:_STRING_},
		{id:ZaAccount.A_zimbraHideInGal, type:_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraHideInGal, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_notes, ref:"attrs/"+ZaAccount.A_notes, type:_STRING_},
		{id:ZaAccount.A_displayname, type:_STRING_, ref:"attrs/"+ZaAccount.A_displayname},
		{id:ZaAccount.A_zimbraMailAlias, type:_LIST_, ref:"attrs/"+ZaAccount.A_zimbraMailAlias, listItem:{type:_STRING_}},
		{id:ZaDistributionList.A_mailStatus, ref:"attrs/"+ZaDistributionList.A_mailStatus, type:_STRING_}
		//,{id:ZaDistributionList.A_isgroup, ref:ZaDistributionList.A_isgroup, type: _ENUM_, choices:ZaModel.BOOLEAN_CHOICES1}
	]
};
