if(ZaItem) {
	ZaItem.POSIX_GROUP = "posixGroup";
}

function ZaPosixGroup(app) {
	if (arguments.length == 0) return;	
	ZaItem.call(this, app,"ZaPosixGroup");
	this.type = ZaItem.POSIX_GROUP;
	this.attrs = [];
	this.attrs[ZaItem.A_objectClass] = [];
	this._init(app);
}


ZaPosixGroup.prototype = new ZaItem;
ZaPosixGroup.prototype.constructor = ZaPosixGroup;

ZaPosixGroup.A_cn = "cn";
ZaPosixGroup.A_gidNumber = "gidNumber";
ZaPosixGroup.A_description = "description";
ZaPosixGroup.A_memberUid = "memberUid";
ZaPosixGroup.A_userPassword = "userPassword";

ZaItem.loadMethods["ZaPosixGroup"] = new Array();
ZaItem.initMethods["ZaPosixGroup"] = new Array();
ZaItem.modifyMethods["ZaPosixGroup"] = new Array();
ZaItem.createMethods["ZaPosixGroup"] = new Array()

ZaPosixGroup.loadMethod = function(by, val) {
	if(!val)
		return;
		
	var soapDoc = AjxSoapDoc.create("GetLDAPEntriesRequest", "urn:zimbraAdmin", null);	
	soapDoc.set("ldapSearchBase", zimbra_posixaccount.ldapSearchBase);
	soapDoc.set("query", "(&(objectClass=posixGroup)(cn="+val+"))");	
	var getSambaDomainsCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = getSambaDomainsCommand.invoke(params).Body.GetLDAPEntriesResponse.LDAPEntry[0];
	this.initFromJS(resp);
}

if(ZaItem.loadMethods["ZaPosixGroup"]) {
	ZaItem.loadMethods["ZaPosixGroup"].push(ZaPosixGroup.loadMethod);
}

ZaPosixGroup.initMethod = function (app) {
	this.attrs[ZaItem.A_objectClass].push("posixGroup");
}
if(ZaItem.initMethods["ZaPosixGroup"]) {
	ZaItem.initMethods["ZaPosixGroup"].push(ZaPosixGroup.initMethod);
}

ZaPosixGroup.getNextGid = function () {
	var soapDoc = AjxSoapDoc.create("GetLDAPEntriesRequest", "urn:zimbraAdmin", null);	
	soapDoc.set("ldapSearchBase", zimbra_posixaccount.ldapSearchBase);
	soapDoc.set("query", "(objectClass=posixGroup)");	
	soapDoc.set("sortBy", ZaPosixGroup.A_gidNumber);	
	soapDoc.set("sortAscending", "false");		
	soapDoc.set("limit", "1");			
	var getPosixGroupsCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var nextId = !isNaN(zimbra_posixaccount.gidBase) ?  parseInt(zimbra_posixaccount.gidBase) + 1 : 10001;
	try {
		var resp = getPosixGroupsCommand.invoke(params).Body.GetLDAPEntriesResponse.LDAPEntry[0];
		if(resp) {
			var grp = new ZaPosixGroup(new Object());;
			grp.initFromJS(resp);
			nextId = parseInt(grp.attrs[ZaPosixGroup.A_gidNumber])+1;
		}
	} catch (ex) {
		//do nothing - fallback to default id for now, ideally should show a warning
	}
	return 	nextId;
}


ZaPosixGroup.prototype.initFromJS = function(posixGroup) {
	ZaItem.prototype.initFromJS.call(this, posixGroup);
	
	if(this.attrs && this.attrs[ZaPosixGroup.A_gidNumber])
		this.id = this.attrs[ZaPosixGroup.A_gidNumber];
		
	if(!this.name && this.attrs && this.attrs[ZaPosixGroup.A_cn])
		this.name = this.attrs[ZaPosixGroup.A_cn];

}

ZaPosixGroup.prototype.remove = 
function(callback) {
	var soapDoc = AjxSoapDoc.create("DeleteLDAPEntryRequest", "urn:zimbraAdmin", null);

	var dn = [["cn=",this.attrs["cn"]].join("")];

	if(zimbra_posixaccount.ldapGroupSuffix)
		dn.push(zimbra_posixaccount.ldapGroupSuffix);
		
	if(zimbra_posixaccount.ldapSuffix)
		dn.push(zimbra_posixaccount.ldapSuffix);
		

	soapDoc.set("dn", dn.join(","));	

	this.deleteCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	if(callback) {
		params.asyncMode = true;
		params.callback = callback;
	}
	this.deleteCommand.invoke(params);		
}

ZaPosixGroup.getAll =
function(app) {
	var soapDoc = AjxSoapDoc.create("GetLDAPEntriesRequest", "urn:zimbraAdmin", null);	
	soapDoc.set("ldapSearchBase", zimbra_posixaccount.ldapSearchBase);
	soapDoc.set("query", "objectClass=posixGroup");	
	var getSambaDomainsCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = getSambaDomainsCommand.invoke(params).Body.GetLDAPEntriesResponse;
	var list = new ZaItemList(ZaPosixGroup, app);
	list.loadFromJS(resp);		
	return list;
}

ZaPosixGroup.createMethod = function(tmpObj, group, app) {
	//test
	var soapDoc = AjxSoapDoc.create("CreateLDAPEntryRequest", "urn:zimbraAdmin", null);
	var dn = [["cn=",tmpObj.attrs["cn"]].join("")];

	if(zimbra_posixaccount.ldapGroupSuffix)
		dn.push(zimbra_posixaccount.ldapGroupSuffix);
		
	if(zimbra_posixaccount.ldapSuffix)
		dn.push(zimbra_posixaccount.ldapSuffix);
		

	soapDoc.set("dn", dn.join(","));	
	for (var aname in tmpObj.attrs) {
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
	


	var testCommand = new ZmCsfeCommand();
	var params = new Object();

	params.soapDoc = soapDoc;	
	var resp = testCommand.invoke(params).Body.CreateLDAPEntryResponse;
	
	group.initFromJS(resp.LDAPEntry[0]);
}


if(ZaItem.createMethods["ZaPosixGroup"]) {
	ZaItem.createMethods["ZaPosixGroup"].push(ZaPosixGroup.createMethod);
}


/**
* @method modify
* Updates ZaPosixGroup attributes (SOAP)
* @param mods set of modified attributes and their new values
*/
ZaPosixGroup.modifyMethod =
function(mods) {
	var cn = this.attrs["cn"];
	if(mods["cn"]) {
		cn = mods["cn"];
		var soapDoc = AjxSoapDoc.create("RenameLDAPEntryRequest", "urn:zimbraAdmin", null);			
		var dn = [["cn=",this.attrs["cn"]].join("")];		
		var new_dn = [["cn=",mods["cn"]].join("")];				
		
		if(zimbra_posixaccount.ldapGroupSuffix) {
			dn.push(zimbra_posixaccount.ldapGroupSuffix);
			new_dn.push(zimbra_posixaccount.ldapGroupSuffix);			
		}	
		if(zimbra_posixaccount.ldapSuffix) {
			dn.push(zimbra_posixaccount.ldapSuffix);
			new_dn.push(zimbra_posixaccount.ldapSuffix);		
		}
		soapDoc.set("dn", dn.join(","));
		soapDoc.set("new_dn", new_dn.join(","));
		var renameLDAPEntryCommand = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		resp = renameLDAPEntryCommand.invoke(params).Body.RenameLDAPEntryResponse;
		this.initFromJS(resp.LDAPEntry[0]);
		this._toolTip = null ;
	}

	var needToMidify = false;
	for(var a in mods) {
		if(a == "cn") {
			continue;
		} else {
			needToMidify = true;
			this._toolTip = null;			
			break;
		}
	}
	if(!needToMidify)
		return;
		
	var soapDoc = AjxSoapDoc.create("ModifyLDAPEntryRequest", "urn:zimbraAdmin", null);	
	var dn = [["cn=",this.attrs["cn"]].join("")];

	if(zimbra_posixaccount.ldapGroupSuffix)
		dn.push(zimbra_posixaccount.ldapGroupSuffix);
		
	if(zimbra_posixaccount.ldapSuffix)
		dn.push(zimbra_posixaccount.ldapSuffix);
		

	soapDoc.set("dn", dn.join(","));	
	
	
	for (var aname in mods) {
		//multy value attribute
		if(mods[aname] instanceof Array) {
			var cnt = mods[aname].length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					var attr = null;
					if(mods[aname][ix] instanceof String)
						var attr = soapDoc.set("a", mods[aname][ix].toString());
					else if(mods[aname][ix] instanceof Object)
						var attr = soapDoc.set("a", mods[aname][ix].toString());
					else if(mods[aname][ix])
						var attr = soapDoc.set("a", mods[aname][ix]);
	
					if(attr)
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
	
	var modifyLDAPEntryCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	resp = modifyLDAPEntryCommand.invoke(params).Body.ModifyLDAPEntryResponse;
	this.initFromJS(resp.LDAPEntry[0]);
	//invalidate the original tooltip

	return;
}
ZaItem.modifyMethods["ZaPosixGroup"].push(ZaPosixGroup.modifyMethod);



ZaApp.prototype.getPosixGroupIdListChoices =
function(refresh) {
	if (refresh || this._posixGroupList == null) {
		this._posixGroupList = ZaPosixGroup.getAll(this);
	}
	if(refresh || this._posixGroupIdChoices == null) {
		var arr = this._posixGroupList.getArray();
		var posixGroupArr = [];
		for (var i = 0 ; i < arr.length; ++i) {
			var obj = new Object();
			obj.name = arr[i].name;
			obj.id = arr[i].id;
			posixGroupArr.push(obj);
		}
		if(this._posixGroupIdChoices == null) {
			this._posixGroupIdChoices = new XFormChoices(posixGroupArr, XFormChoices.OBJECT_LIST, "id", "name");
		} else {	
			this._posixGroupIdChoices.setChoices(posixGroupArr);
			this._posixGroupIdChoices.dirtyChoices();
		}
	}
	return this._posixGroupIdChoices;	
}

ZaPosixGroup.myXModel = {
	items: [
		{id:"id", type:_STRING_, ref:"id"},			
		{id:"name", type:_STRING_, ref:"name"},		
		{id:ZaPosixGroup.A_cn, type:_STRING_, ref:"attrs/" + ZaPosixGroup.A_cn},	
		{id:ZaPosixGroup.A_gidNumber, type:_NUMBER_, ref:"attrs/" + ZaPosixGroup.A_gidNumber},
		{id:ZaPosixGroup.A_description, type:_STRING_, ref:"attrs/" + ZaPosixGroup.A_description},
		{id:ZaPosixGroup.A_memberUid, type:_LIST_, ref:"attrs/"+ZaPosixGroup.A_memberUid, listItem:{type:_NUMBER_}}
	]
};
