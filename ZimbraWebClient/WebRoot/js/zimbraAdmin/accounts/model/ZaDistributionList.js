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
 * The Original Code is: Zimbra Collaboration Suite.
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
	this._memberList = (memberList != null)? AjxVector.fromArray(memberList): null;
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


ZaDistributionList.prototype.clone = function () {
	var memberList = this._memberList.getArray();
	var dl = new ZaDistributionList(this._app, this.id, this.name, memberList, this.description, this.notes);
 	if (memberList != null) {
 		dl._memberList = new AjxVector();
 		for (var i = 0 ; i < memberList.length; ++i) {
 			dl._memberList.add(memberList[i]);
 		}
 	} else {
 		this._memberList = null;
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

ZaDistributionList.prototype.addMembers = function (newMembersArrayOrVector) {
	var added = false;
	if (newMembersArrayOrVector != null) {
		if (AjxUtil.isArray(newMembersArrayOrVector)) {
			for (var i = 0; i < newMembersArrayOrVector.length; ++i) {
				this._memberList.add(newMembersArrayOrVector[i]);
			}
			if (newMembersArrayOrVector.length > 0){
				added = true;
			}
		} else if (AjxUtil.isInstance(newMembersArrayOrVector, AjxVector)){
			this._memberList.merge(this._memberList.size(),newMembersArrayOrVector);
		}
		this.dedupMembers();
	}
	return added;
};

ZaDistributionList.prototype.sortMembers = function () {
	//this._memberList.sort(ZaDistributionListMember.comparator);
	this._memberList.sort();
};

ZaDistributionList.prototype.dedupMembers = function () {
	this.sortMembers();
	var membersArray = this._memberList.getArray();
	var len = membersArray.length;
	var i;
	var prev = null;
	var curr = null;
	for (i = len; i >= 0; --i) {
		curr = membersArray[i];
		if((curr!=null) && (prev!=null) && curr.valueOf() == prev.valueOf()) {
			membersArray.splice(i,1);
		} else {
			prev = curr;
		}
	}
};

ZaDistributionList.prototype.remove = function () {
	var sd = AjxSoapDoc.create("DeleteDistributionListRequest", "urn:zimbraAdmin", null);
	sd.set("id", this.id);
	var resp = ZmCsfeCommand.invoke(sd, null, null, null, false);
	return resp;
};

ZaDistributionList.prototype.saveEdits = function () {
	if (this.isDirty()) {
		//var sd = AjxSoapDoc.create("ModifyDistributionListRequest", "urn:zimbraAdmin", null);
		//sd.set("id", this.id);
		//throw "Saving edited Distribution lists is not currently supported";
		//return this._save(sd, "ModifyDistributionListResponse");

		// TODO - when the backend api for Modify changes, fix this hack.
		// This is until there is a better way of modifying a distribution list.
		this.remove();
		return this.saveNew();
	}
};

ZaDistributionList.prototype.saveNew = function () {
	if (this.isDirty()) {
		var sd = AjxSoapDoc.create("CreateDistributionListRequest", "urn:zimbraAdmin", null);
		return this._save(sd, "CreateDistributionListResponse");
	}
};

/**
 * Save any changes made to the list. If no changes have been
 * made, the function returns false;
 */
ZaDistributionList.prototype._save = function (soapDoc, respName) {
	var app = this._app;
	if (this.isDirty()) {
		soapDoc.set("name", this.getName());
		var a, key;
		for (key in this.attrs) {
			if (this.attrs[key] != null) {
				if (key == "objectClass" || key == "zimbraId" || key == "uid") {
						continue;
				}
				a = soapDoc.set("a", this.attrs[key]);
				a.setAttribute("n", key);
			}
		}
		
		try {
			var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false).Body[respName];
			this.id = resp.dl[0].id;

			var membersArray = this.getMembersArray();
			var len = membersArray.length;
			var addMemberSoapDoc, r;
			for (var i = 0; i < len; ++i) {
				addMemberSoapDoc = AjxSoapDoc.create("AddDistributionListMemberRequest", "urn:zimbraAdmin", null);
				addMemberSoapDoc.set("id", this.id);
				addMemberSoapDoc.set("dlm", membersArray[i].toString());
				r = ZmCsfeCommand.invoke(addMemberSoapDoc, null, null, null, false).AddDistributionListMemberResponse;
			}
			
		} catch (ex) {
			DBG.dumpObj(ex);
			throw ex;
			// TODO:
			// hmm ... if we fail adding any one ofthe members, but the list creation succeeded ...
			// how should we tell the user this ....
			return false;
		}

		this.markClean();
		return true;
	}
	return false;
};

ZaDistributionList.prototype.markChanged = function () {
	this._dirty = true;
};

ZaDistributionList.prototype.markClean = function () {
	this._dirty = false;
};

ZaDistributionList.prototype.isDirty = function () {
	return this._dirty;
};

ZaDistributionList.prototype.initFromDom = function(node) {
	this.name = node.getAttribute("name");
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
	if (this._memberList != null) 	this.sortMembers();
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
	this.name = name;
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


// TODO -- handle dynamic limit and offset
ZaDistributionList.prototype.getMembers = function (force) {
	//DBG.println("Get members: memberList = " , this._memberList, "$");
	if ((this._memberList == null || (force == true)) && (this.id != null)) {
		var soapDoc = AjxSoapDoc.create("GetDistributionListRequest", "urn:zimbraAdmin", null);
		soapDoc.setMethodAttribute("limit", "25");
		soapDoc.setMethodAttribute("offset", "0");
		var dl = soapDoc.set("dl", this.id);
		dl.setAttribute("by", "id");
		soapDoc.set("name", this.getName());
		try {
			// We can't use javascript here, since the response is not returning the correct information
			var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false).Body.GetDistributionListResponse;
			var members = resp.dl[0].dlm;
			var len = members ? members.length : 0;
			if (len > 0) {
				this._memberList = new AjxVector();
				for (var i =0; i < len; ++i) {
					this._memberList.add(new ZaDistributionListMember(members[i]._content));
				}
				this.sortMembers();
			}
			this.id = resp.dl[0].id;
			this.attrs = resp.dl[0]._attrs;
		} catch (ex) {
			// TODO -- exception handling
			DBG.dumpObj(ex);
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

ZaDistributionList.prototype.setMembers = function (list) {
	if (list == null) list = [];
	return this._memberList = AjxVector.fromArray(list);
};

function ZaDistributionListMember (name) {
	this.name = name;
	this.id = "ZADLM_" + name;

}

ZaDistributionListMember.prototype.toString = function () {
	return this.name;
};

ZaDistributionListMember.prototype.valueOf = function () {
	return this.id;
};
