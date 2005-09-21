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

function ZaDistributionList(app, id, name, memberList, description) {
	ZaItem.call(this, app);
	this.attrs = new Object();
	this.id = (id != null)? id: null;
	this.name = (name != null) ? name: null;
	this._memberList = (memberList != null)? AjxVector.fromArray(memberList): null;
	this.description = (description != null)? description : null;
	this._dirty = true;
}

ZaDistributionList.prototype = new ZaItem;
ZaDistributionList.prototype.constructor = ZaDistributionList;

ZaDistributionList.EMAIL_ADDRESS = "ZDLEA";
ZaDistributionList.DESCRIPTION = "ZDLDESC";
ZaDistributionList.ID = "ZDLID";

/**
 * inserts items into the list at the given index.
 * If index is -1, then all items are inserted at the end of the list
 */
ZaDistributionList.prototype.insert = function (index, emailList) {
	if (emailList instanceof Array) {
		for (var i = emailList.length; i > 0; --i ){
			this._memberList.add(emailList[i], index);
		}
	} else {
		this._memberList.add(email, index);
	}
	this.markChanged();
};

/**
 * Removes items from the list starting at the given index.
 * 
 */
ZaDistributionList.prototype.remove = function (index, emailList) {
	if (list instanceof Array) {
		for (var i = emailList.length; i > 0; --i ){
			this._memberList.remove(emailList[i]);
		}
	}
	this._memberList.removeAt(index);
	this.markChanged();
};

/**
 * Save any changes made to the list. If no changes have been
 * made, the function returns false;
 */
//<CreateDistributionListRequest>
//   <name>...</name>
//   <a n="...">...</a>+
// </CreateDistributionListRequest>

ZaDistributionList.prototype.save = function () {
	DBG.println("Is list dirty? ", this.isDirty());
	var app = this._app;
	if (this.isDirty()) {
		var sd, reqName, respName;
		if (!this.editMode) {
			reqName = "CreateDistributionListRequest";
			respName = "CreateDistributionListResponse";
			sd = AjxSoapDoc.create("CreateDistributionListRequest", "urn:zimbraAdmin", null);
		} else {
			reqName = "ModifyDistributionListRequest";
			respName = "ModifyDistributionListResponse";
		}
		sd = AjxSoapDoc.create(reqName, "urn:zimbraAdmin", null);
		sd.set("name", this.getName());
		var a, key;
		for (key in this.attrs) {
			if (this.attrs[key] != null) {
				a = createSoapDoc.set("a", this.attrs[key]);
				a.setAttribute("n", key);
			}
		}
		
		try {
			var resp = ZmCsfeCommand.invoke(sd, null, null, null, false).Body[respName];
			this.id = resp.dl[0].id;
			DBG.dumpObj(resp);

			var membersArray = this.getMembersArray();
			var len = membersArray.length;
			var addMemberSoapDoc, r;
			DBG.println("membersArray = ", membersArray);
			for (var i = 0; i < len; ++i) {
				addMemberSoapDoc = AjxSoapDoc.create("AddDistributionListMemberRequest", "urn:zimbraAdmin", null);
				addMemberSoapDoc.set("id", this.id);
				addMemberSoapDoc.set("dlm", membersArray[i].toString());
				r = ZmCsfeCommand.invoke(addMemberSoapDoc, null, null, null, false).AddDistributionListMemberResponse;
				DBG.dumpObj(r);
			}
			
		} catch (ex) {
			DBG.dumpObj(ex);
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
			this._memberList.add(child.getAttribute('name'));
		}
	}
	DBG.dumpObj(this, false, 1);
	DBG.dumpObj(this._memberList);
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

ZaDistributionList.ATTR_MEMBER = "zimbraMailForwardingAddress";
ZaDistributionList.prototype.getMembers = function () {
	if (this._memberList == null) {
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
			var len = members.length;
			if (len > 0) {
				this._memberList = new AjxVector();
				for (var i =0; i < len; ++i) {
					this._memberList.add(members[i]._content);
				}
			}
// 			var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;
// 			var children = resp.childNodes[0].childNodes;
// 			var len = children.length;
// 			if (len > 0) {
// 				this._memberList = new AjxVector();
// 				var node, name;
// 				for (var i=0; i< children.length;  ++i) {
// 					node = children[i]; 
// 					name = node.getAttribute("n");
// 					if (name == ZaDistributionList.ATTR_MEMBER) {
// 						this._memberList.add(node.firstChild.nodeValue);
// 					}
// 				}
// 			}
			this.id = resp.dl[0].id;
			//this._p
			DBG.println("memberList after processing response");
			DBG.dumpObj(this._memberList);
			//DBG.printXML(resp);
		} catch (ex) {
			// TODO -- exception handling
			DBG.dumpObj(ex);
		}
	}
	return this._memberList;
};

ZaDistributionList.prototype.getMembersArray = function () {
	return this._memberList.getArray();
};

ZaDistributionList.prototype.setMembers = function (list) {
	if (list == null) list = [];
	return this._memberList = AjxVector.fromArray(list);
};
