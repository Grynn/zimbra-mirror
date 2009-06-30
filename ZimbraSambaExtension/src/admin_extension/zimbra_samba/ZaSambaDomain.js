if(ZaItem) {
	ZaItem.SAMBA_DOMAIN = "sambaDomain";
}

function ZaSambaDomain() {
	//if (arguments.length == 0) return;	
	ZaItem.call(this, "ZaSambaDomain");
	this.type = ZaItem.SAMBA_DOMAIN;
	this.attrs = [];
	this._init();
}


ZaSambaDomain.prototype = new ZaItem;
ZaSambaDomain.prototype.constructor = ZaSambaDomain;

ZaSambaDomain.A_sambaSID = "sambaSID";
ZaSambaDomain.A_sambaDomainName = "sambaDomainName";
ZaSambaDomain.A_sambaAlgorithmicRidBase = "sambaAlgorithmicRidBase";

ZaItem.loadMethods["ZaSambaDomain"] = new Array();
ZaItem.initMethods["ZaSambaDomain"] = new Array();
ZaItem.modifyMethods["ZaSambaDomain"] = new Array();
ZaItem.createMethods["ZaSambaDomain"] = new Array()

ZaSambaDomain.prototype.loadEffectiveRights = function () {
	this.getAttrs = {all:true};
	this.setAttrs = {all:true};
	this.rights = {};
}

ZaSambaDomain.loadMethod = function(by, val) {
	if(!val)
		return;
		
	var soapDoc = AjxSoapDoc.create("GetLDAPEntriesRequest", "urn:zimbraAdmin", null);	
	soapDoc.set("ldapSearchBase", Zambra.ldapSuffix);
	soapDoc.set("query", "(&(objectClass=sambaDomain)(sambaDomainName="+val+"))");	
	
	/*var getSambaDomainsCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = getSambaDomainsCommand.invoke(params).Body.GetLDAPEntriesResponse.LDAPEntry[0];
	this.initFromJS(resp);*/
	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;

	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = zimbra_samba.BUSY_GETTING_SAMBA_DOMAIN;
	var resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GetLDAPEntriesResponse;
	if(resp && resp.LDAPEntry) {	
		this.initFromJS(resp.LDAPEntry[0]);
	}
	
}

if(ZaItem.loadMethods["ZaSambaDomain"]) {
	ZaItem.loadMethods["ZaSambaDomain"].push(ZaSambaDomain.loadMethod);
}

ZaSambaDomain.prototype.initFromJS = function(sambaDomain) {
	ZaItem.prototype.initFromJS.call(this, sambaDomain);
	if(this.attrs && this.attrs[ZaSambaDomain.A_sambaSID])
		this.id = this.attrs[ZaSambaDomain.A_sambaSID];
	if(this.attrs && this.attrs[ZaSambaDomain.A_sambaDomainName])
		this.name = this.attrs[ZaSambaDomain.A_sambaDomainName];

}

ZaSambaDomain.prototype.remove = 
function(callback) {
	var soapDoc = AjxSoapDoc.create("DeleteLDAPEntryRequest", "urn:zimbraAdmin", null);

	var dn = [[ZaSambaDomain.A_sambaDomainName,"=",this.attrs[ZaSambaDomain.A_sambaDomainName]].join("")];

	
	if(zimbra_posixaccount.ldapSuffix)
		dn.push(Zambra.ldapSuffix);
		

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

ZaSambaDomain.getAll =
function() {
	var soapDoc = AjxSoapDoc.create("GetLDAPEntriesRequest", "urn:zimbraAdmin", null);	
	soapDoc.set("ldapSearchBase", Zambra.ldapSuffix);
	soapDoc.set("query", "objectClass=sambaDomain");	

	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	
	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = zimbra_samba.BUSY_GETTING_SAMBA_DOMAINS;
	var resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GetLDAPEntriesResponse;
	var list = new ZaItemList(ZaSambaDomain)
	if(resp) {	
		list.loadFromJS(resp);
	}
		
/*	var getSambaDomainsCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = getSambaDomainsCommand.invoke(params).Body.GetLDAPEntriesResponse;
	var list = new ZaItemList(ZaSambaDomain);*/
			
	return list;
}


ZaSambaDomain.myXModel = {
	items: [
		{id:ZaSambaDomain.A_sambaSID, type:_STRING_, ref:"attrs/" + ZaSambaDomain.A_sambaSID},
		{id:ZaSambaDomain.A_sambaDomainName, type:_STRING_, ref:"attrs/" + ZaSambaDomain.A_sambaDomainName},
		{id:ZaSambaDomain.A_sambaAlgorithmicRidBase, type:_NUMBER_, ref:"attrs/" + ZaSambaDomain.A_sambaAlgorithmicRidBase}
	]
};

ZaSambaDomain.createMethod = function(tmpObj, domain) {
	//test
	var soapDoc = AjxSoapDoc.create("CreateLDAPEntryRequest", "urn:zimbraAdmin", null);

	var sambaDomainName = tmpObj.attrs[ZaSambaDomain.A_sambaDomainName];
	
	var dn = [[ZaSambaDomain.A_sambaDomainName,"=",tmpObj.attrs[ZaSambaDomain.A_sambaDomainName]].join("")];

		
	if(Zambra.ldapSuffix)
		dn.push(Zambra.ldapSuffix);
		
	soapDoc.set("dn", dn.join(","));
	tmpObj.attrs["objectClass"] = "sambaDomain";
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
	

	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = zimbra_samba.BUSY_CREATING_SAMBA_DOMAIN;
	resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.CreateLDAPEntryResponse;
	
	if(resp.LDAPEntry)		
		domain.initFromJS(resp.LDAPEntry[0]);
}


if(ZaItem.createMethods["ZaSambaDomain"]) {
	ZaItem.createMethods["ZaSambaDomain"].push(ZaSambaDomain.createMethod);
}


/**
* @method modify
* Updates ZaSambaDomain attributes (SOAP)
* @param mods set of modified attributes and their new values
*/
ZaSambaDomain.modifyMethod =
function(mods) {

	var sambaDomainName = this.attrs[ZaSambaDomain.A_sambaDomainName];
	if(mods[ZaSambaDomain.A_sambaDomainName]) {
		sambaDomainName = mods[ZaSambaDomain.A_sambaDomainName];
		var soapDoc = AjxSoapDoc.create("RenameLDAPEntryRequest", "urn:zimbraAdmin", null);			
		var dn = [[ZaSambaDomain.A_sambaDomainName,"=",this.attrs[ZaSambaDomain.A_sambaDomainName]].join("")];		
		var new_dn = [[ZaSambaDomain.A_sambaDomainName,"=",mods[ZaSambaDomain.A_sambaDomainName]].join("")];				
		

		if(Zambra.ldapSuffix) {
			dn.push(Zambra.ldapSuffix);
			new_dn.push(Zambra.ldapSuffix);		
		}
		soapDoc.set("dn", dn.join(","));
		soapDoc.set("new_dn", new_dn.join(","));
		
		var params = new Object();
		params.soapDoc = soapDoc;	
		var reqMgrParams = {
			controller:ZaApp.getInstance().getCurrentController(),
			busyMsg: ZaMsg.BUSY_RENAMING_SAMBA_DOMAIN 
		} ;
		
		//resp = modifyAccCommand.invoke(params).Body.ModifyAccountResponse;
		var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.RenameLDAPEntryResponse ;
		
		if(resp.LDAPEntry[0])		
			this.initFromJS(resp.LDAPEntry[0]);
			
		this._toolTip = null ;
	}

	var needToModify = false;
	for(var a in mods) {
		if(a == ZaSambaDomain.A_sambaDomainName) {
			continue;
		} else {
			needToModify = true;
			this._toolTip = null ;
			break;
		}
	}
	if(!needToModify)
		return;
		
	//update the object
	var soapDoc = AjxSoapDoc.create("ModifyLDAPEntryRequest", "urn:zimbraAdmin", null);
	var dn = [[ZaSambaDomain.A_sambaDomainName,"=",this.attrs[ZaSambaDomain.A_sambaDomainName]].join("")];

		
	if(Zambra.ldapSuffix)
		dn.push(Zambra.ldapSuffix)
		
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
	
/*	var modifyLDAPEntryCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	resp = modifyLDAPEntryCommand.invoke(params).Body.ModifyLDAPEntryResponse;
	this.initFromJS(resp.LDAPEntry[0]);*/
	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = zimbra_samba.BUSY_UPDATING_SAMBA_DOMAIN;
	resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.ModifyLDAPEntryResponse;
	
	if(resp.LDAPEntry[0])	
		this.initFromJS(resp.LDAPEntry[0]);	
		
	return;
}
ZaItem.modifyMethods["ZaSambaDomain"].push(ZaSambaDomain.modifyMethod);


ZaApp.prototype.getSambaDomainSIDListChoices =
function(refresh) {
	if (refresh || this._sambaDomainList == null) {
		this._sambaDomainList = ZaSambaDomain.getAll(this);
	}
	if(refresh || this._sambaDomainSIDChoices == null) {
		var arr = this._sambaDomainList.getArray();
		var sambaDomainArr = [];
		for (var i = 0 ; i < arr.length; ++i) {
			var obj = new Object();
			obj.name = arr[i].name;
			obj.id = arr[i].id;
			sambaDomainArr.push(obj);
		}
		if(this._sambaDomainSIDChoices == null) {
			this._sambaDomainSIDChoices = new XFormChoices(sambaDomainArr, XFormChoices.OBJECT_LIST, "id", "name");
		} else {	
			this._sambaDomainSIDChoices.setChoices(sambaDomainArr);
			this._sambaDomainSIDChoices.dirtyChoices();
		}
	}
	return this._sambaDomainSIDChoices;	
}


ZaApp.prototype.getSambaDomainNameListChoices =
function(refresh) {
	if (refresh || this._sambaDomainList == null) {
		this._sambaDomainList = ZaSambaDomain.getAll(this);
	}
	if(refresh || this._sambaDomainNameChoices == null) {
		var arr = this._sambaDomainList.getArray();
		var sambaDomainArr = [];
		for (var i = 0 ; i < arr.length; ++i) {
			var obj = new Object();
			obj.name = arr[i].name;
			obj.id = arr[i].id;
			sambaDomainArr.push(obj);
		}
		if(this._sambaDomainNameChoices == null) {
			this._sambaDomainNameChoices = new XFormChoices(sambaDomainArr, XFormChoices.OBJECT_LIST, "name", "name");
		} else {	
			this._sambaDomainNameChoices.setChoices(sambaDomainArr);
			this._sambaDomainNameChoices.dirtyChoices();
		}
	}
	return this._sambaDomainNameChoices;	
}