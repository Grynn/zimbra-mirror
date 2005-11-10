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

function ZaDistributionList(app, id, name, memberList, description, notes) {
	ZaItem.call(this, app);
	this.attrs = new Object();
	this.id = (id != null)? id: null;
	this.name = (name != null) ? name: null;
	this._selfMember = new ZaDistributionListMember(this.name);
	this._memberList = (memberList != null)? AjxVector.fromArray(memberList): new AjxVector();
	this._origList = (memberList != null)? AjxVector.fromArray(memberList): new AjxVector();
	this._addList = new AjxVector();
	this._removeList = new AjxVector();
	this._dirty = true;
	if (description != null) this.attrs.description = description;
	if (notes != null) this.attrs.zimbraNotes = notes;
}

ZaDistributionList.prototype = new ZaItem;
ZaDistributionList.prototype.constructor = ZaDistributionList;

ZaDistributionList.EMAIL_ADDRESS = "ZDLEA";
ZaDistributionList.DESCRIPTION = "ZDLDESC";
ZaDistributionList.ID = "ZDLID";

ZaDistributionList.A_mailStatus = "zimbraMailStatus";
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
	return dl;
};

/**
 * Removes a list of members
 * This keeps the internal add, and remove lists up to date.
 * @param arr (Array) - array of ZaDistributionListMembers
 */
ZaDistributionList.prototype.removeMembers = function (arr) {
	var removed = this._removeFromList(arr, this._memberList);
	this._removeFromList(arr, this._addList);
	if (removed) {
		this._addToRemoveList(arr, this._removeList);
	}
};

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

ZaDistributionList.prototype.remove = function () {
	return this._remove(this.id);
};

ZaDistributionList.prototype._remove = function (id) {
	var sd = AjxSoapDoc.create("DeleteDistributionListRequest", "urn:zimbraAdmin", null);
	sd.set("id", id);
	var resp = ZmCsfeCommand.invoke(sd, null, null, null, false);
	return resp;
};

/**
 * Saves all changes to a list
 */
ZaDistributionList.prototype.saveEdits = function () {
	if (this.isDirty()) {
		if (this._origName != null && this._origName != this.name) {
			// move all members to the add list, to force a re add.
			var sd = AjxSoapDoc.create("RenameDistributionListRequest", "urn:zimbraAdmin", null);
			sd.set("id", this.id);
			sd.set("newName", this.name);
			var resp = ZmCsfeCommand.invoke(sd, null, null, null, false);
		}
		sd = AjxSoapDoc.create("ModifyDistributionListRequest", "urn:zimbraAdmin", null);
		sd.set("id", this.id);
		return this._save(sd, "ModifyDistributionListResponse", true, true);
	}
		
};

/**
 * Creates a new distribution list
 */
ZaDistributionList.prototype.saveNew = function () {
	if (this.isDirty()) {
		var sd = AjxSoapDoc.create("CreateDistributionListRequest", "urn:zimbraAdmin", null);
		return this._save(sd, "CreateDistributionListResponse", true, false);
	}
};

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

ZaDistributionList.prototype.setDescription = function (description) {
	this.attrs.description = description;
};

ZaDistributionList.prototype.getDescription = function () {
	return this.attrs.description;
};

ZaDistributionList.prototype.setNotes = function (notes) {
	this.attrs.zimbraNotes = notes;
};

ZaDistributionList.prototype.getNotes = function () {
	return this.attrs.zimbraNotes;
};

ZaDistributionList.prototype.setMailStatus = function (status) {
	this.attrs.zimbraMailStatus = status;
};

ZaDistributionList.prototype.getMailStatus = function () {
	return this.attrs.zimbraMailStatus;
};


/**
 * Makes a server call to get the distribution list details, if the
 * internal list of members is null
 */
// TODO -- handle dynamic limit and offset
ZaDistributionList.prototype.getMembers = function (force) {
	//DBG.println("Get members: memberList = " , this._memberList, "$");
	if ((this._memberList == null || (force == true)) && (this.id != null)) {
		var soapDoc = AjxSoapDoc.create("GetDistributionListRequest", "urn:zimbraAdmin", null);
		soapDoc.setMethodAttribute("limit", "0");
		soapDoc.setMethodAttribute("offset", "0");
		var dl = soapDoc.set("dl", this.id);
		dl.setAttribute("by", "id");
		soapDoc.set("name", this.getName());
		try {
			// We can't use javascript here, since the response is not returning the correct information
			var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false).Body.GetDistributionListResponse;
			DBG.dumpObj(resp);
			var members = resp.dl[0].dlm;
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
			this.attrs = resp.dl[0]._attrs;
		} catch (ex) {
			app.getCurrentController()._handleException(ex, "ZaDistributionList.prototype.getMembers", null, false);
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
			
		if (newArray[i].valueOf() != this._selfMember.valueOf()) {
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

ZaDistributionList.prototype._removeArrayFromList = function (newArray, vector) {
	var vecArray = vector.getArray();
	var ret = false;
	for (var i = 0; i < newArray.length ; ++i) {
		for (var j = 0; j < vecArray.length; ++j) {
			if (vecArray[j].valueOf() == newArray[i].valueOf()) {
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
		if((curr!=null) && (prev!=null) && curr.valueOf() == prev.valueOf()) {
			arr.splice(i,1);
		} else {
			prev = curr;
		}
	}
};

/**
 * Save any changes made to the list. If no changes have been
 * made, the function returns false;
 */
ZaDistributionList.prototype._save = function (soapDoc, respName, add, remove) {
	var app = this._app;
	if (this.isDirty()) {
		soapDoc.set("name", this.getName());
		var a, key;
		for (key in this.attrs) {
			if (this.attrs[key] != null) {
				if (key == "objectClass" || key == "zimbraId" || key == "uid" ||
					key == "mail") {
						continue;
				}
				a = soapDoc.set("a", this.attrs[key]);
				a.setAttribute("n", key);
			}
		}
		
		try {
			var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false).Body[respName];
			this.id = resp.dl[0].id;
			if (add == true) this._addNewMembers();
			if (remove == true) this._removeDeletedMembers();
			this.markClean();
			return true;
		} catch (ex) {
			//DBG.dumpObj(ex);
			throw ex;
			// TODO:
			// hmm ... if we fail adding any one ofthe members, but the list creation succeeded ...
			// how should we tell the user this ....
			return false;
		}

	}
	return false;
};

ZaDistributionList.prototype._addNewMembers = function () {
	var addArray = this._addList.getArray();
	var len = addArray.length;
	var addMemberSoapDoc, r, addMemberSoapDoc;
	for (var i = 0; i < len; ++i) {
		addMemberSoapDoc = AjxSoapDoc.create("AddDistributionListMemberRequest", "urn:zimbraAdmin", null);
		addMemberSoapDoc.set("id", this.id);
		addMemberSoapDoc.set("dlm", addArray[i].toString());
		r = ZmCsfeCommand.invoke(addMemberSoapDoc, null, null, null, false).Body.AddDistributionListMemberResponse;
	}
};

ZaDistributionList.prototype._removeDeletedMembers = function () {
	var removeArray = this._removeList.getArray();
	var len = removeArray.length;
	var addMemberSoapDoc, r, removeMemberSoapDoc;
	for (var i = 0; i < len; ++i) {
		removeMemberSoapDoc = AjxSoapDoc.create("RemoveDistributionListMemberRequest", "urn:zimbraAdmin", null);
		removeMemberSoapDoc.set("id", this.id);
		removeMemberSoapDoc.set("dlm", removeArray[i].toString());
		r = ZmCsfeCommand.invoke(removeMemberSoapDoc, null, null, null, false).Body.RemoveDistributionListMemberResponse;
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
function ZaDistributionListMember (name) {
	this.name = name;
	this.id = "ZADLM_" + name;

}

ZaDistributionListMember.prototype.toString = function () {
	return this.name;
};

/**
 * Override valueOf to force comparisons to treat this much like a string.
 */
ZaDistributionListMember.prototype.valueOf = function () {
	return this.id;
};
