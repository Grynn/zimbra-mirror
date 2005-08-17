/**
* @class ZaDomain
* @ constructor ZaDomain
* @param app reference to the application instance
* Data model for zimbraDomain object
* @author Greg Solovyev
**/

function ZaDomain(app) {
	ZaItem.call(this, "domain");
	this.attrs = new Object();
	this.id = "";
	this.name="";
	this._app = app;
	//default attributes
	this.attrs[ZaDomain.A_GalMode] = ZaDomain.GAL_Mode_internal;
	this.attrs[ZaDomain.A_GalMaxResults] = 100;
	this.attrs[ZaDomain.A_AuthMech] = ZaDomain.AuthMech_zimbra;
}

ZaDomain.prototype = new ZaItem;
ZaDomain.prototype.constructor = ZaDomain;

//attribute name constants, this values are taken from zimbra.schema
ZaDomain.A_description = "description";
ZaDomain.A_notes = "zimbraNotes";
ZaDomain.A_domainName = "zimbraDomainName";
//GAL
ZaDomain.A_GalMaxResults = "zimbraGalMaxResults";
ZaDomain.A_GalMode = "zimbraGalMode";
ZaDomain.A_GalLdapURL = "zimbraGalLdapURL";
ZaDomain.A_GalLdapSearchBase = "zimbraGalLdapSearchBase";
ZaDomain.A_GalLdapBindDn = "zimbraGalLdapBindDn";
ZaDomain.A_GalLdapBindPassword = "zimbraGalLdapBindPassword";
ZaDomain.A_GalLdapBindPasswordConfirm = "zimbraGalLdapBindPasswordConfirm";
ZaDomain.A_GalLdapFilter = "zimbraGalLdapFilter";
//Auth
ZaDomain.A_AuthMech = "zimbraAuthMech";
ZaDomain.A_AuthLdapURL = "zimbraAuthLdapURL";
ZaDomain.A_AuthLdapUserDn = "zimbraAuthLdapBindDn";

//internal attributes - not synched with the server code yet
//GAL
ZaDomain.A_GALServerType = "galservertype";
ZaDomain.A_GALServerName = "galservername";
ZaDomain.A_GALServerPort = "galserverport";
ZaDomain.A_GALUseSSL = "galusessl";
ZaDomain.A_GALTestMessage = "galtestmessage";
ZaDomain.A_GALTestResultCode = "galtestresutcode";
ZaDomain.A_GALSampleQuery = "samplequery";
ZaDomain.A_UseBindPassword = "usebindpassword";

//values
ZaDomain.GAL_Mode_internal = "zimbra";
ZaDomain.GAL_Mode_external = "ldap";
ZaDomain.GAL_Mode_both = "both";
ZaDomain.GAL_ServerType_ad = "ad";
ZaDomain.GAL_ServerType_ldap = "ldap";

//Auth
ZaDomain.A_AuthADDomainName = "zimbraAuthADDomainName";
ZaDomain.A_AuthLDAPServerName = "zimbraAuthLDAPServerName";
ZaDomain.A_AuthLDAPSearchBase = "zimbraAuthLDAPSearchBase";
ZaDomain.A_AuthLDAPServerPort = "zimbraAuthLDAPServerPort";
ZaDomain.A_AuthLDAPUseSSL = "authldapusessl";
ZaDomain.A_AuthTestUserName = "authtestusername";
ZaDomain.A_AuthTestPassword = "authtestpassword";
ZaDomain.A_AuthTestMessage = "authtestmessage";
ZaDomain.A_AuthTestResultCode = "authtestresutcode";
ZaDomain.A_AuthComputedBindDn = "authcomputedbinddn";


//server value constants
ZaDomain.AuthMech_ad = "ad";
ZaDomain.AuthMech_ldap = "ldap";
ZaDomain.AuthMech_zimbra = "zimbra";

//result codes returned from Check* requests
ZaDomain.Check_OK = "check.OK";
ZaDomain.Check_UNKNOWN_HOST="check.UNKNOWN_HOST";
ZaDomain.Check_CONNECTION_REFUSED = "check.CONNECTION_REFUSED";
ZaDomain.Check_SSL_HANDSHAKE_FAILURE = "check.SSL_HANDSHAKE_FAILURE";
ZaDomain.Check_COMMUNICATION_FAILURE = "check.COMMUNICATION_FAILURE";
ZaDomain.Check_AUTH_FAILED = "check.AUTH_FAILED";
ZaDomain.Check_AUTH_NOT_SUPPORTED = "check.AUTH_NOT_SUPPORTED";
ZaDomain.Check_NAME_NOT_FOUND = "check.NAME_NOT_FOUND";
ZaDomain.Check_INVALID_SEARCH_FILTER = "check.INVALID_SEARCH_FILTER";
ZaDomain.Check_FAILURE = "check.FAILURE"; 
ZaDomain.Check_FAULT = "Fault";

ZaDomain.AUTH_MECH_CHOICES = [ZaDomain.AuthMech_ad,ZaDomain.AuthMech_ldap,ZaDomain.AuthMech_zimbra];
/**
* static method getAll fetches zimbraDomain objects from SOAP servlet using GetAllDomainsRequest
* returns a ZaItemList of ZaDomain objects
**/
ZaDomain.getAll =
function() {
	var soapDoc = AjxSoapDoc.create("GetAllDomainsRequest", "urn:zimbraAdmin", null);	
	var resp = AjxCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;
	var list = new ZaItemList("domain", ZaDomain);
	list.loadFromDom(resp);
//	list.sortByName();		
	return list;
}

/**
* Creates a new ZaDomain. This method makes SOAP request (CreateDomainRequest) to create a new domain record in LDAP. 
* @param attrs
* @param name 
* @return ZaDomain
**/
ZaDomain.create =
function(tmpObj, app) {

	if(tmpObj.attrs == null) {
		//show error msg
		app.getCurrentController().popupMsgDialog(ZaMsg.ERROR_UNKNOWN, null);
		return null;	
	}
	
	//name
	if(tmpObj.attrs[ZaDomain.A_domainName] ==null || tmpObj.attrs[ZaDomain.A_domainName].length < 1) {
		//show error msg
		app.getCurrentController().popupMsgDialog(ZaMsg.ERROR_DOMAIN_NAME_REQUIRED);
		return null;
	}
	tmpObj.name = tmpObj.attrs[ZaDomain.A_domainName];
	//check values
	if(!AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaDomain.A_GalMaxResults])) {
		//show error msg
		app.getCurrentController().popupMsgDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_GalMaxResults + " ! ");
		return null;
	}
	
	if(tmpObj.name.length > 256 || tmpObj.attrs[ZaDomain.A_domainName].length > 256) {
		//show error msg
		app.getCurrentController().popupMsgDialog(ZaMsg.ERROR_DOMAIN_NAME_TOOLONG);
		return null;
	}
	
	var domainRegEx = /(^([a-zA-Z0-9]))(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if( !domainRegEx.test(tmpObj.attrs[ZaDomain.A_domainName]) ) {
		//show error msg
		app.getCurrentController().popupMsgDialog(ZaMsg.ERROR_DOMAIN_NAME_INVALID);
		return null;
	}
	var nonAlphaNumEx = /[^a-zA-Z0-9\-\.]+/
	if(nonAlphaNumEx.test(tmpObj.attrs[ZaDomain.A_domainName]) ) {
		//show error msg
		app.getCurrentController().popupMsgDialog(ZaMsg.ERROR_DOMAIN_NAME_INVALID);
		return null;
	}	

	var soapDoc = AjxSoapDoc.create("CreateDomainRequest", "urn:zimbraAdmin", null);
	soapDoc.set("name", tmpObj.attrs[ZaDomain.A_domainName]);
	var attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalMode]);
	attr.setAttribute("n", ZaDomain.A_GalMode);	

	attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalMaxResults]);
	attr.setAttribute("n", ZaDomain.A_GalMaxResults);

	attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_notes]);
	attr.setAttribute("n", ZaDomain.A_notes);	
	
	attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapURL]);
	attr.setAttribute("n", ZaDomain.A_AuthLdapURL);		
	
	attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_description]);
	attr.setAttribute("n", ZaDomain.A_description);		

	if(tmpObj.attrs[ZaDomain.A_GalMode] != ZaDomain.GAL_Mode_internal) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapURL]);
		attr.setAttribute("n", ZaDomain.A_GalLdapURL);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapSearchBase]);
		attr.setAttribute("n", ZaDomain.A_GalLdapSearchBase);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapBindDn]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindDn);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapBindPassword]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindPassword);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapFilter]);
		attr.setAttribute("n", ZaDomain.A_GalLdapFilter);	
	}

	if(tmpObj.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ad) {
		//set bind DN to default for AD
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapURL]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapURL);	
		
		attr = soapDoc.set("a", "%u@"+tmpObj.attrs[ZaDomain.A_AuthADDomainName]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapUserDn);	

	} else if(tmpObj.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ldap) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapURL]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapURL);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapUserDn]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapUserDn);	
	}

	var attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthMech]);
	attr.setAttribute("n", ZaDomain.A_AuthMech);	

	var newDomain = new ZaDomain();
	var resp = AjxCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;
	newDomain.initFromDom(resp.firstChild);
	return newDomain;
}

ZaDomain.testAuthSettings = 
function (obj, callback) {
	var soapDoc = AjxSoapDoc.create("CheckAuthConfigRequest", "urn:zimbraAdmin", null);
	var attr = soapDoc.set("a", obj.attrs[ZaDomain.A_AuthMech]);
	attr.setAttribute("n", ZaDomain.A_AuthMech);
	
	attr = soapDoc.set("a", obj.attrs[ZaDomain.A_AuthLdapURL]);
	attr.setAttribute("n", ZaDomain.A_AuthLdapURL);	
	
	attr = soapDoc.set("a", obj.attrs[ZaDomain.A_AuthLdapUserDn]);
	attr.setAttribute("n", ZaDomain.A_AuthLdapUserDn);	
	
	attr = soapDoc.set("name", obj[ZaDomain.A_AuthTestUserName]);
	attr = soapDoc.set("password", obj[ZaDomain.A_AuthTestPassword]);	
	
	var asynCommand = new AjxCsfeAsynchCommand();
	asynCommand.addInvokeListener(callback);
	asynCommand.invoke(soapDoc, null, null, null, true);	
}

ZaDomain.testGALSettings =
function (obj, callback, sampleQuery) {
	var soapDoc = AjxSoapDoc.create("CheckGalConfigRequest", "urn:zimbraAdmin", null);
	var attr = soapDoc.set("a", ZaDomain.GAL_Mode_external);
	attr.setAttribute("n", ZaDomain.A_GalMode);

	attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapURL]);
	attr.setAttribute("n", ZaDomain.A_GalLdapURL);	
	
	attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapSearchBase]);
	attr.setAttribute("n", ZaDomain.A_GalLdapSearchBase);	

	attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapFilter]);
	attr.setAttribute("n", ZaDomain.A_GalLdapFilter);	

	if(obj.attrs[ZaDomain.A_GalLdapBindDn]) {
		attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapBindDn]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindDn);
	}

	if(obj.attrs[ZaDomain.A_GalLdapBindPassword]) {
		attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapBindPassword]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindPassword);
	}
	soapDoc.set("query", "cn=*" + sampleQuery + "*");

	var asynCommand = new AjxCsfeAsynchCommand();
	asynCommand.addInvokeListener(callback);
	asynCommand.invoke(soapDoc, null, null, null, true);
}

ZaDomain.modifyGalSettings = 
function(tmpObj, oldObj) {
	var soapDoc = AjxSoapDoc.create("ModifyDomainRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", oldObj.id);
	
	var attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalMode]);
	attr.setAttribute("n", ZaDomain.A_GalMode);	
	
	if(tmpObj.attrs[ZaDomain.A_GalMode] != ZaDomain.GAL_Mode_internal) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapURL]);
		attr.setAttribute("n", ZaDomain.A_GalLdapURL);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapSearchBase]);
		attr.setAttribute("n", ZaDomain.A_GalLdapSearchBase);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapBindDn]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindDn);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapBindPassword]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindPassword);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapFilter]);
		attr.setAttribute("n", ZaDomain.A_GalLdapFilter);	
	}
	if(oldObj[ZaDomain.A_GalMaxResults] != tmpObj.attrs[ZaDomain.A_GalMaxResults]) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalMaxResults]);
		attr.setAttribute("n", ZaDomain.A_GalMaxResults);	
	}

	var resp = AjxCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;
	oldObj.initFromDom(resp.firstChild);
}

ZaDomain.modifyAuthSettings = 
function(tmpObj, oldObj) {

	var soapDoc = AjxSoapDoc.create("ModifyDomainRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", oldObj.id);
	
	var attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthMech]);
	attr.setAttribute("n", ZaDomain.A_AuthMech);	
	
	if(tmpObj.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ad) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapURL]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapURL);	

		attr = soapDoc.set("a", "%u@"+tmpObj.attrs[ZaDomain.A_AuthADDomainName]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapUserDn);	
	} else if (tmpObj.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ldap) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapURL]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapURL);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapUserDn]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapUserDn);	
	
	}
	var resp = AjxCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;
	oldObj.initFromDom(resp.firstChild);
}

/**
* @param mods - map of modified attributes that will be sent to the server
* modifies object's information in the database
**/
ZaDomain.prototype.modify =
function(mods) {
	var soapDoc = AjxSoapDoc.create("ModifyDomainRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	for (var aname in mods) {
		var attr = soapDoc.set("a", mods[aname]);
		attr.setAttribute("n", aname);
	}
		
	var resp = AjxCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;
	//update itself
	this.initFromDom(resp.firstChild);
}

/**
* Domain configuration is tricky, therefore have to massage the values into the instace
**/
ZaDomain.prototype.initFromDom = 
function (node) {
	ZaItem.prototype.initFromDom.call(this, node);
	if(!this.attrs[ZaDomain.A_AuthMech]) {
		this.attrs[ZaDomain.A_AuthMech] = ZaDomain.AuthMech_zimbra; //default value
	}
	if(!this.attrs[ZaDomain.A_GalMode]) {
		this.attrs[ZaDomain.A_GalMode] = ZaDomain.GAL_Mode_internal; //default value
	}

	if(this.attrs[ZaDomain.A_AuthLdapURL]) {
		/* analyze Auth URL */
		var pieces = this.attrs[ZaDomain.A_AuthLdapURL].split(/[:\/]/);
		if (pieces.length < 4) {
			//the URL is invalid - use default values
			this.attrs[ZaDomain.A_AuthLDAPUseSSL] = "FALSE";	
			this.attrs[ZaDomain.A_AuthLDAPServerPort] = 389;
			this.attrs[ZaDomain.A_AuthLDAPServerName] = "";
		} else {
			if(pieces[0] == "ldaps") {
				this.attrs[ZaDomain.A_AuthLDAPUseSSL] = "TRUE";
			} else {
				this.attrs[ZaDomain.A_AuthLDAPUseSSL] = "FALSE";	
			}
			var ix = 1;
			var cnt = pieces.length;
			while( (pieces[ix] == null || pieces[ix] == "" || pieces[ix].length == 0) && ix < cnt) {
				ix++; //skip empty tokens
			}
			this.attrs[ZaDomain.A_AuthLDAPServerName] = pieces[ix];
			ix++;
			if(ix < cnt && pieces[ix] != null && pieces[ix] != "" && pieces[ix].length >0) {
				this.attrs[ZaDomain.A_AuthLDAPServerPort] = pieces[ix]; //got port token
			} else {
			 	//URL does not contain port, use default values
			 	if(this.attrs[ZaDomain.A_AuthLDAPUseSSL] == "TRUE") {
				 	this.attrs[ZaDomain.A_AuthLDAPServerPort] = 636;
			 	} else {
		 			this.attrs[ZaDomain.A_AuthLDAPServerPort] = 389;
			 	}
			}
			
		}
	
		if (pieces.length == 2) {
			this.attrs[ZaDomain.A_AuthLDAPServerPort] == pieces[1];
		}
	}
	if(this.attrs[ZaDomain.A_GalLdapURL])	{	
		/* analyze GAL URL */
		var pieces = this.attrs[ZaDomain.A_GalLdapURL].split(/[:\/]/);
		if (pieces.length < 4) {
			//the URL is invalid - use default values
			this.attrs[ZaDomain.A_GALUseSSL] = "FALSE";	
			this.attrs[ZaDomain.A_GALServerPort] = 389;
			this.attrs[ZaDomain.A_GALServerName] = "";
		} else {
			if(pieces[0] == "ldaps") {
				this.attrs[ZaDomain.A_GALUseSSL] = "TRUE";
			} else {
				this.attrs[ZaDomain.A_GALUseSSL] = "FALSE";	
			}
			var ix = 1;
			var cnt = pieces.length;
			while( (pieces[ix] == null || pieces[ix] == "" || pieces[ix].length == 0) && ix < cnt) {
				ix++; //skip empty tokens
			}
			this.attrs[ZaDomain.A_GALServerName] = pieces[ix];
			ix++;
			if(ix < cnt && pieces[ix] != null && pieces[ix] != "" && pieces[ix].length >0) {
				this.attrs[ZaDomain.A_GALServerPort] = pieces[ix]; //got port token
			} else {
			 	//URL does not contain port, use default values
			 	if(this.attrs[ZaDomain.A_GALUseSSL] == "TRUE") {
				 	this.attrs[ZaDomain.A_GALServerPort] = 636;
			 	} else {
		 			this.attrs[ZaDomain.A_GALServerPort] = 389;
			 	}
			}
			
		}
	}	
	
	if(this.attrs[ZaDomain.A_GalMode]) {
		if(this.attrs[ZaDomain.A_GalMode] == "ldap" || this.attrs[ZaDomain.A_GalMode] == "both") {
			if(this.attrs[ZaDomain.A_GalLdapFilter] == "ad") {
				this.attrs[ZaDomain.A_GALServerType] = "ad";
			} else {
				this.attrs[ZaDomain.A_GALServerType] = "ldap";
			}
		}
	} else {
		this.attrs[ZaDomain.A_GalMode] = "zimbra";
	}
	
	if(this.attrs[ZaDomain.A_GalLdapBindDn] || this.attrs[ZaDomain.A_GalLdapBindPassword]) {
		this.attrs[ZaDomain.A_UseBindPassword] = "TRUE";
	} else {
		this.attrs[ZaDomain.A_UseBindPassword] = "FALSE";
	}
	
	this[ZaDomain.A_GALSampleQuery] = "john";

}
/**
* Returns HTML for a tool tip for this domain.
*/
ZaDomain.prototype.getToolTip =
function() {
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0'>";
		html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
		html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap; overflow:hidden;width:350'>";
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' style='width:100%;'>";
		html[idx++] = "<tr valign='center'>";
		html[idx++] = "<td><b>" + AjxStringUtil.htmlEncode(this.name) + "</b></td>";
		html[idx++] = "<td align='right'>";
		html[idx++] = AjxImg.getImageHtml(ZaImg.I_DOMAIN);			
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

ZaDomain.prototype.remove = 
function() {
	var soapDoc = AjxSoapDoc.create("DeleteDomainRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	AjxCsfeCommand.invoke(soapDoc, null, null, null, true);	
}

ZaDomain.myXModel = {
	items: [
		{id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId},
		{id:ZaDomain.A_domainName, type:_STRING_, ref:"attrs/" + ZaDomain.A_domainName, pattern:/(^([a-zA-Z0-9]))(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/},
		{id:ZaDomain.A_description, type:_STRING_, ref:"attrs/" + ZaDomain.A_description}, 
		{id:ZaDomain.A_notes, type:_STRING_, ref:"attrs/" + ZaDomain.A_notes},
		{id:ZaDomain.A_GalMode, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalMode},
		{id:ZaDomain.A_GalMaxResults, type:_NUMBER_, ref:"attrs/" + ZaDomain.A_GalMaxResults, maxInclusive:2147483647, minInclusive:1},					
		{id:ZaDomain.A_GALServerType, type:_STRING_, ref:"attrs/" + ZaDomain.A_GALServerType},
		{id:ZaDomain.A_GALServerName, type:_STRING_, ref:"attrs/" + ZaDomain.A_GALServerName},					
		{id:ZaDomain.A_GALServerPort, type:_NUMBER_, ref:"attrs/" + ZaDomain.A_GALServerPort, maxInclusive:2147483647},
		{id:ZaDomain.A_GALUseSSL, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaDomain.A_GALUseSSL},
		{id:ZaDomain.A_GalLdapFilter, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalLdapFilter},
		{id:ZaDomain.A_GalLdapSearchBase, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalLdapSearchBase},
		{id:ZaDomain.A_UseBindPassword, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaDomain.A_UseBindPassword},
		{id:ZaDomain.A_GalLdapURL, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalLdapURL},
		{id:ZaDomain.A_GalLdapBindDn, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalLdapBindDn},
		{id:ZaDomain.A_GalLdapBindPassword, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalLdapBindPassword},
		{id:ZaDomain.A_GalLdapBindPasswordConfirm, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalLdapBindPasswordConfirm},		
		{id:ZaDomain.A_AuthLdapUserDn, type:_STRING_,ref:"attrs/" + ZaDomain.A_AuthLdapUserDn},
		{id:ZaDomain.A_AuthLDAPUseSSL, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaDomain.A_AuthLDAPUseSSL},
		{id:ZaDomain.A_AuthLDAPServerName, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthLDAPServerName},
		{id:ZaDomain.A_AuthLDAPSearchBase, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthLDAPSearchBase},
		{id:ZaDomain.A_AuthLDAPServerPort, type:_NUMBER_, ref:"attrs/" + ZaDomain.A_AuthLDAPServerPort, maxInclusive:2147483647},
		{id:ZaDomain.A_AuthMech, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthMech},
		{id:ZaDomain.A_AuthLdapURL, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthLdapURL},
		{id:ZaDomain.A_AuthADDomainName, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthADDomainName},
		{id:ZaDomain.A_AuthTestUserName, type:_STRING_},
		{id:ZaDomain.A_AuthTestPassword, type:_STRING_},
		{id:ZaDomain.A_AuthTestMessage, type:_STRING_},
		{id:ZaDomain.A_AuthTestResultCode, type:_STRING_},
		{id:ZaDomain.A_AuthTestMessage, type:_STRING_},
		{id:ZaDomain.A_AuthComputedBindDn, type:_STRING_},
		{id:ZaDomain.A_GALTestMessage, type:_STRING_},
		{id:ZaDomain.A_GALTestResultCode, type:_STRING_},
		{id:ZaDomain.A_GALSampleQuery, type:_STRING_},
		{id:ZaModel.currentStep, type:_NUMBER_, ref:ZaModel.currentStep, maxInclusive:2147483647}
	]
};
