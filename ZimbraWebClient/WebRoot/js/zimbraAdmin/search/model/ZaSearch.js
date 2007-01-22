/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaSearch
* @contructor ZaSearch
* this is a static class taht provides method for searching LDAP
* @author Greg Solovyev
**/
function ZaSearch(app) {
	if(app)
		this._app = app;
		
	this[ZaSearch.A_selected] = null;
	this[ZaSearch.A_query] = "";
	this[ZaSearch.A_fAliases] = "TRUE";
	this[ZaSearch.A_fAccounts] = "TRUE";	
	this[ZaSearch.A_fdistributionlists] = "TRUE";
	this[ZaSearch.A_fResources] = "TRUE";
	this[ZaSearch.A_fDomains] = "TRUE";
	this[ZaSearch.A_pagenum]=1;	
}
ZaSearch.ALIASES = "aliases";
ZaSearch.DLS = "distributionlists";
ZaSearch.ACCOUNTS = "accounts";
ZaSearch.RESOURCES = "resources";
ZaSearch.DOMAINS = "domains";

ZaSearch.TYPES = new Object();
ZaSearch.TYPES[ZaItem.ALIAS] = ZaSearch.ALIASES;
ZaSearch.TYPES[ZaItem.DL] = ZaSearch.DLS;
ZaSearch.TYPES[ZaItem.ACCOUNT] = ZaSearch.ACCOUNTS;
ZaSearch.TYPES[ZaItem.RESOURCE] = ZaSearch.RESOURCES;
ZaSearch.TYPES[ZaItem.DOMAIN] = ZaSearch.DOMAINS;


ZaSearch.A_query = "query";
ZaSearch.A_selected = "selected";
ZaSearch.A_pagenum = "pagenum";
ZaSearch.A_fAliases = "f_aliases";
ZaSearch.A_fAccounts = "f_accounts";
ZaSearch.A_fDomains = "f_domains";
ZaSearch.A_fdistributionlists = "f_distributionlists";
ZaSearch.A_fResources = "f_resources";

ZaSearch._currentQuery = null;

/**
* @param app reference to ZaApp
**/
ZaSearch.getAll =
function(app) {
	return ZaSearch.search("", [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS, ZaSearch.RESOURCES,ZaSearch.DOMAINS], 1, ZaAccount.A_uid, true, app);
}


ZaSearch.standardAttributes = [ZaAccount.A_displayname, 
							ZaItem.A_zimbraId,
							ZaAccount.A_mailHost, 
							ZaAccount.A_uid ,
							ZaAccount.A_accountStatus,
							ZaAccount.A_description,
							ZaDistributionList.A_mailStatus,
							ZaResource.A_zimbraCalResType,
							ZaDomain.A_domainType,
							ZaDomain.A_domainName].join();
/**
* Sends SearchDirectoryRequest to the SOAP Servlet
* params {
* 	query - query string should be an LDAP-style filter string (RFC 2254)
* 	sortBy - LDAP attribute name, default is zimbraId
* 	limit - the number of objects to return (0 is default and means all)
* 	offset - the starting offset (0, 25, etc)
* 	domain - the domain name to limit the search to (do not use if searching for domains)
*	applyCos - whether or not (0/1) to apply the COS policy to account. specify 0 if only
*	           requesting attrs that aren't inherited from COS
*	attrs - array of of attributes to return ("displayName", "zimbraId", "zimbraAccountStatus")
*	sortAscending - whether to sort in ascending order (0/1), 1 is default
*   types = array of types to return. legal values are: accounts|distributionlists|aliases|resources|domains, 
* 		default is accounts
* 	callback - an AjxCallback
* }
* */
ZaSearch.searchDirectory = 
function (params) {
	var soapDoc = AjxSoapDoc.create("SearchDirectoryRequest", "urn:zimbraAdmin", null);
	if(params.query) {
		soapDoc.set("query", params.query);
		ZaSearch._currentQuery = params.query ;
	} else {
		soapDoc.set("query", "");		
		ZaSearch._currentQuery = "";
	}

	var sortBy = (params.sortBy != undefined)? params.sortBy: ZaAccount.A_name;
	var limit = (params.limit != undefined)? params.limit: ZaAccount.RESULTSPERPAGE;
	var offset = (params.offset != undefined) ? params.offset : "0";
	var sortAscending = (params.sortAscending != null)? params.sortAscending : "1";
	
	soapDoc.getMethod().setAttribute("offset", offset);
	soapDoc.getMethod().setAttribute("limit", limit);
	soapDoc.getMethod().setAttribute("sortBy", sortBy);
	soapDoc.getMethod().setAttribute("sortAscending", sortAscending);	

	if(params.applyCos)	
		soapDoc.getMethod().setAttribute("applyCos", params.applyCos);
	
	if(params.domain)	
		soapDoc.getMethod().setAttribute("domain", params.domain);

	if(params.attrs && params.attrs.length>0)
		soapDoc.getMethod().setAttribute("attrs", params.attrs.toString());
		
	if(params.types && params.types.length>0)
		soapDoc.getMethod().setAttribute("types", params.types.toString());
	
	//set the maxResults to 2 for testing
	//params.maxResults = 2;
	if(params.maxResults) {
		soapDoc.getMethod().setAttribute("maxResults", params.maxResults.toString());
	}	
	
	var command = new ZmCsfeCommand();
	var cmdParams = new Object();
	cmdParams.soapDoc = soapDoc;	
	if(params.callback) {
		cmdParams.asyncMode = true;
		cmdParams.callback = params.callback;
	}
	var resp = command.invoke(cmdParams);
	return resp ;	
}

ZaSearch.findAccount = function(by, val) {
	var soapDoc = AjxSoapDoc.create("SearchDirectoryRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("limit", "1");
	var query = ["(",by,"=",val,")"].join("");
	soapDoc.set("query", query);
	var command = new ZmCsfeCommand();
	var cmdParams = new Object();
	cmdParams.soapDoc = soapDoc;	
	var resp = command.invoke(cmdParams).Body.SearchDirectoryResponse;	
	var list = new ZaItemList(ZaAccount, this._app);	
	list.loadFromJS(resp);	
	return list.getArray()[0];
}

ZaSearch.prototype.dynSelectDataCallback = function (callback, resp) {
	if(!callback)	
		return;
	try {
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListViewController.prototype.searchCallback"));
		}
		if(resp.isException()) {
			throw(resp.getException());
		} else {
			var response = resp.getResponse().Body.SearchDirectoryResponse;
			var list = new ZaItemList(null, this._app);	
			list.loadFromJS(response);	
			callback.run(list.getArray());
		}
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectDataCallback");	
	}
	
}

ZaSearch.prototype.dynSelectSearchAccounts = function (value, event, callback) {
	try {
		var params = new Object();
		dataCallback = new AjxCallback(this, this.dynSelectDataCallback, callback);
		params.types = [ZaSearch.ACCOUNTS];
		params.callback = dataCallback;
		params.sortBy = ZaAccount.A_name;
		params.query = ZaSearch.getSearchByNameQuery(value);
		ZaSearch.searchDirectory(params);
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectDataFetcher");		
	}
}

ZaSearch.prototype.dynSelectSearchGroups = function (value, event, callback) {
	try {
		var params = new Object();
		dataCallback = new AjxCallback(this, this.dynSelectDataCallback, callback);
		params.types = [ZaSearch.DLS];
		params.callback = dataCallback;
		params.sortBy = ZaAccount.A_name;
		params.query = ZaSearch.getSearchByNameQuery(value);
		ZaSearch.searchDirectory(params);
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectDataFetcher");		
	}
}

ZaSearch.prototype.dynSelectSearchDomains = function (value, event, callback) {
	try {
		var params = new Object();
		dataCallback = new AjxCallback(this, this.dynSelectDataCallback, callback);
		params.types = [ZaSearch.DOMAINS];
		params.callback = dataCallback;
		params.sortBy = ZaDomain.A_domainName;
		params.query = ZaSearch.getSearchByNameQuery(value);
		ZaSearch.searchDirectory(params);
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectSearchDomains");		
	}
}

/**
* Sends SearchDirectoryRequest to the SOAP Servlet
* @param query - query string
* @param types - array of object types to search for([ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS, ZaSearch.RESOURCES])
* @pagenum - results page number
* @orderby - attribute to sort by
* @isascending - sort order (boolean)
* @app - reference to ZaApp instance (will be passed on to ZaItemList contructor)
* @attrs - coma separated list of attributes to return (default: ZaSearch.standardAttributes)
* @limit - maximum number of records to return
* @domainName - domain name (optional, if searching within one domain)
**/
ZaSearch.search =
function(query, types, pagenum, orderby, isascending, app, attrs, limit, domainName, maxResults) {
	//if(!orderby) orderby = ZaAccount.A_uid;
	if(!orderby) orderby = ZaAccount.A_name;
	var myisascending = "1";
	
	if(!isascending ) {
		myisascending = "0";
	}
	
	limit = (limit != null)? limit: ZaAccount.RESULTSPERPAGE;
	
	var offset = (pagenum-1) * limit;
	attrs = (attrs != null)? attrs: ZaSearch.standardAttributes;
	var soapDoc = AjxSoapDoc.create("SearchDirectoryRequest", "urn:zimbraAdmin", null);
/*	if(query)
		query = String(query).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
	*/	
	soapDoc.set("query", query);
	if (domainName != null) {
		soapDoc.getMethod().setAttribute("domain", domainName);
	}
	soapDoc.getMethod().setAttribute("offset", offset);
	soapDoc.getMethod().setAttribute("limit", limit);
	soapDoc.getMethod().setAttribute("applyCos", "0");
	soapDoc.getMethod().setAttribute("attrs", attrs);
	soapDoc.getMethod().setAttribute("sortBy", orderby);
	soapDoc.getMethod().setAttribute("sortAscending", myisascending);
	if(types != null && types.length>0) {
		soapDoc.getMethod().setAttribute("types", types.toString());
	}
	//For testing: maxResults = 2; 
	if(maxResults) {
		soapDoc.getMethod().setAttribute("maxResults", maxResults.toString());
	}	

	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = command.invoke(params).Body.SearchDirectoryResponse;
	var list = new ZaItemList(null, app);	
	list.loadFromJS(resp);
		
	var searchTotal = resp.searchTotal;
	var numPages = Math.ceil(searchTotal/limit);
	return {"list":list, "numPages":numPages};
}

ZaSearch.searchByDomain = 
function (domainName, types, pagenum, orderby, isascending, app, attrs, limit) {
	return ZaSearch.search("", types, pagenum, orderby, isascending, app, attrs, limit, domainName);
}


ZaSearch.getSearchByNameQuery =
function(n) {
	if (n == null || n == "") {
		return "";
	} else {
		n = String(n).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
		return ("(|(uid=*"+n+"*)(cn=*"+n+"*)(sn=*"+n+"*)(gn=*"+n+"*)(displayName=*"+n+"*)(zimbraId="+n+")(mail=*"+n+"*)(zimbraMailAlias=*"+n+"*)(zimbraMailDeliveryAddress=*"+n+"*)(zimbraDomainName=*"+n+"*))");
	}
}

ZaSearch.getSearchByDisplayNameQuery =
function(n) {
	if (n == null || n == "") {
		return "";
	} else {
		n = String(n).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
		//return ("(|(uid=*"+n+"*)(cn=*"+n+"*)(sn=*"+n+"*)(gn=*"+n+"*)(displayName=*"+n+"*)(zimbraId="+n+")(mail=*"+n+"*)(zimbraMailAlias=*"+n+"*)(zimbraMailDeliveryAddress=*"+n+"*)(zimbraDomainName=*"+n+"*))");
		return ("(displayName=*"+n+"*)");
	}
}

ZaSearch.getAdancedSearchQuery =
function (searchOptionsInstance) {
	DBG.println (AjxDebug.DBG1, "Process the options instance to get the LDAP query string ...");
}

ZaSearch.searchByQueryHolder = 
function (queryHolder, pagenum, orderby, isascending, app) {
	if(queryHolder.isByDomain) {
 		return ZaSearch.searchByDomain(queryHolder.byValAttr, queryHolder.types, pagenum, orderby, isascending, app);
	} else {
		return ZaSearch.search(queryHolder.queryString, queryHolder.types, pagenum, orderby, isascending, app, queryHolder.fetchAttrs,
							   queryHolder.limit);	
	}
}

ZaSearch.getSearchFromQuery = function (query) {
	var searchObj = new ZaSearch();
	searchObj[ZaSearch.A_selected] = null;
	searchObj[ZaSearch.A_query] = query.queryString;
	searchObj[ZaSearch.A_fAliases] = "FALSE";
	searchObj[ZaSearch.A_fAccounts] = "FALSE";
	searchObj[ZaSearch.A_fdistributionlists] = "FALSE";
	searchObj[ZaSearch.A_fResources] = "FALSE";
	
	if (query.types != null) {
		for (var i = 0; i < query.types.length; ++i) {
			if (query.types[i] == ZaSearch.ALIASES){
				searchObj[ZaSearch.A_fAliases] = "TRUE";
			}
			if (query.types[i] == ZaSearch.ACCOUNTS){
				searchObj[ZaSearch.A_fAccounts] = "TRUE";
			}
			if (query.types[i] == ZaSearch.DLS){
				searchObj[ZaSearch.A_fdistributionlists] = "TRUE";
			}
			if (query.types[i] == ZaSearch.RESOURCES){
				searchObj[ZaSearch.A_fResources] = "TRUE";
			}
			if(query.types[i]==ZaSearch.DOMAINS) {
				searchObj[ZaSearch.A_fResources] = "TRUE";
			}
		}
	}
	return searchObj;
};

ZaSearch.myXModel = {
	items: [
		{id:ZaSearch.A_query, type:_STRING_},
		{id:ZaSearch.A_selected, type:_OBJECT_, items:ZaAccount.myXModel},		
		{id:ZaSearch.A_fAliases, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaSearch.A_fdistributionlists, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaSearch.A_fAccounts, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaSearch.A_pagenum, type:_NUMBER_}
	]
}

function ZaSearchQuery (queryString, types, byDomain, byVal, attrsCommaSeparatedString, limit) {
	this.queryString = queryString;
	this.isByDomain = byDomain;
	this.byValAttr = byVal;
	this.types = types;
	this.fetchAttrs = (attrsCommaSeparatedString != null)? attrsCommaSeparatedString: ZaSearch.standardAttributes;
	this.limit = (limit != null)? limit: ZaAccount.RESULTSPERPAGE;
}

/**
 * add the search result count information to the toolbar
 */
ZaSearch.searchResultCountsView =
function (opArr) {
	opArr.push(new ZaOperation(ZaOperation.SEP));								
	opArr.push(new ZaOperation(ZaOperation.LABEL, AjxMessageFormat.format (ZaMsg.searchResultCount, [0,0]),
													 null, null, null, null,null,null,null,ZaOperation.SEARCH_RESULT_COUNT));	
	opArr.push(new ZaOperation(ZaOperation.SEP));
}

ZaSearch.getUsedDomainAccounts =
function (domainName) {
	var params = {
		domain: domainName,
		limit: "0",
		type: "accounts",
		offset: "0",
		applyCos: "0",
		attrs: ""
	}
	
	var resp = ZaSearch.searchDirectory(params) ;
	return resp.Body.SearchDirectoryResponse.searchTotal ;
}
