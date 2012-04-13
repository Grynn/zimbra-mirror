/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaSearch
* @contructor ZaSearch
* this is a static class taht provides method for searching LDAP
* @author Greg Solovyev
**/
ZaSearch = function() {
	this[ZaSearch.A_selected] = null;
	this[ZaSearch.A_query] = "";
	this[ZaSearch.A_fAliases] = "TRUE";
	this[ZaSearch.A_fAccounts] = "TRUE";	
	this[ZaSearch.A_fdistributionlists] = "TRUE";
	this[ZaSearch.A_fResources] = "TRUE";
	this[ZaSearch.A_fDomains] = "TRUE";
	this[ZaSearch.A_fCoses] = "TRUE";
	this[ZaSearch.A_pagenum]=1;	
}
ZaSearch.ALIASES = "aliases";
ZaSearch.DLS = "distributionlists";
ZaSearch.DDLS = "dynamicgroups";
ZaSearch.ACCOUNTS = "accounts";
ZaSearch.RESOURCES = "resources";
ZaSearch.DOMAINS = "domains";
ZaSearch.COSES = "coses";
//Limit of result returned for best match.
ZaSearch.BEST_MATCH_LIMIT = 5;

ZaSearch.TYPES = new Object();
ZaSearch.TYPES[ZaItem.ALIAS] = ZaSearch.ALIASES;
ZaSearch.TYPES[ZaItem.DL] = ZaSearch.DLS;
ZaSearch.TYPES[ZaItem.ACCOUNT] = ZaSearch.ACCOUNTS;
ZaSearch.TYPES[ZaItem.RESOURCE] = ZaSearch.RESOURCES;
ZaSearch.TYPES[ZaItem.DOMAIN] = ZaSearch.DOMAINS;
ZaSearch.TYPES[ZaItem.COS] = ZaSearch.COSES;


ZaSearch.A_query = "query";
ZaSearch.A_selected = "selected";
ZaSearch.A_pagenum = "pagenum";
ZaSearch.A_fAliases = "f_aliases";
ZaSearch.A_fAccounts = "f_accounts";
ZaSearch.A_fDomains = "f_domains";
ZaSearch.A_fCoses = "f_coses";
ZaSearch.A_fdistributionlists = "f_distributionlists";
ZaSearch.A_fResources = "f_resources";
ZaSearch.A_ResultMsg = "resultMsg";

ZaSearch._currentQuery = null;
ZaSearch._domain = null; //current searchDirectory's domain parameter
ZaSearch._savedSearchToBeUpdated = true ; //initial value to be true

ZaSearch.getPredefinedSavedSearchesForAdminOnly = function () {
    return   [
        {name: ZaMsg.ss_admin_account , query: "(|(zimbraIsAdminAccount=TRUE)(zimbraIsDelegatedAdminAccount=TRUE))"}
    ];
}
                  
ZaSearch.getPredefinedSavedSearches =  function () {
    return [
        {name: ZaMsg.ss_external_accounts, query: "(zimbraIsExternalVirtualAccount=TRUE)"},
        {name: ZaMsg.ss_locked_out_accounts, query: "(zimbraAccountStatus=*lockout*)"},
        {name: ZaMsg.ss_closed_accounts, query: "(zimbraAccountStatus=*closed*)"},
        {name: ZaMsg.ss_maintenance_accounts, query: "(zimbraAccountStatus=*maintenance*)"},
        {name: ZaMsg.ss_non_active_accounts, query: "(!(zimbraAccountStatus=*active*))" },
        {name: ZaMsg.ss_inactive_accounts_30, query: "(zimbraLastLogonTimestamp<=###JSON:{func: ZaSearch.getTimestampByDays, args:[-30]}###)"},
        {name: ZaMsg.ss_inactive_accounts_90, query: "(zimbraLastLogonTimestamp<=###JSON:{func: ZaSearch.getTimestampByDays, args:[-90]}###)"}
    ];
}

/**
* @param app reference to ZaApp
**/

ZaSearch.getAll =
function() {
	return ZaSearch.search("", [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS, ZaSearch.RESOURCES,ZaSearch.DOMAINS, ZaSearch.COSES], 1, ZaAccount.A_uid, true);
}


ZaSearch.standardAttributes = [ZaAccount.A_displayname,
							ZaItem.A_zimbraId,
							ZaAlias.A_AliasTargetId,
							ZaAccount.A_accountName,
							ZaAccount.A_lastName,
							ZaAccount.A_mailHost,
							ZaAccount.A_uid,
							ZaAccount.A_COSId,
							ZaAccount.A_accountStatus,
							ZaAccount.A_zimbraLastLogonTimestamp,
							ZaAccount.A_description,
							ZaAccount.A_zimbraIsSystemAccount,
                            ZaAccount.A_zimbraIsDelegatedAdminAccount,
                            ZaAccount.A_zimbraIsAdminAccount,
                            ZaAccount.A_zimbraIsSystemResource,
                            ZaAccount.A_zimbraAuthTokenValidityValue,
                            ZaAccount.A_zimbraIsExternalVirtualAccount,
							ZaDistributionList.A_mailStatus,
                            ZaDistributionList.A_isAdminGroup,
							ZaResource.A_zimbraCalResType,
							ZaDomain.A_domainType,
							ZaDomain.A_domainName,
							ZaDomain.A_zimbraDomainStatus].join();
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
	var soapDoc = AjxSoapDoc.create("SearchDirectoryRequest", ZaZimbraAdmin.URN, null);
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
	else 
		soapDoc.getMethod().setAttribute("applyCos", false);
		
	
	if(params.applyConfig)
		soapDoc.getMethod().setAttribute("applyConfig", params.applyConfig);
	else
		soapDoc.getMethod().setAttribute("applyConfig", "false");
	
	if(params.domain)  {
		soapDoc.getMethod().setAttribute("domain", params.domain);
        ZaSearch._domain = params.domain;
    } else{
        ZaSearch._domain = null;        
    }

    if(params.attrs && params.attrs.length>0)
		soapDoc.getMethod().setAttribute("attrs", params.attrs.toString());
		
	if(params.types && params.types.length>0)
        soapDoc.getMethod().setAttribute("types", ZaSearch.checkDynamicGroupType(params.types).toString());
	//set the maxResults to 2 for testing
	//params.maxResults = 2;
	if(params.maxResults) {
		soapDoc.getMethod().setAttribute("maxResults", params.maxResults.toString());
	}

	var cmdParams = new Object();
	cmdParams.soapDoc = soapDoc;	
	if(params.callback) {
		cmdParams.asyncMode = true;
		cmdParams.callback = params.callback;
		cmdParams.skipCallbackIfCancelled = params.skipCallbackIfCancelled;
	}
	
	
	try {
		//only returned for synchronous calls
		return ZaRequestMgr.invoke(cmdParams, params);
		
	}catch(ex) {
		if (params.ignoreTooManyResultsException ) {
			ZaSearch.handleTooManyResultsException (ex, cmdParams.exceptionFrom || "ZaSearch.searchDirectory") ;
			return null;
		}else{
			throw (ex) ;
		}
	}
}

ZaSearch.checkDynamicGroupType = function(type) {
    for (var i = 0; i < type.length; i ++) {
        if (type[i] == ZaSearch.DLS) {
            type.push(ZaSearch.DDLS);
            return type;
        }
    }
    return type;
}

ZaSearch.TOO_MANY_RESULTS_FLAG = false ; //control the no result text of the list view
ZaSearch.handleTooManyResultsException = function (ex, from) {
	if (ex.code == ZmCsfeException.TOO_MANY_SEARCH_RESULTS) {
		//supress the result
		/*if(window.console && window.console.log) {
			console.log("Suppressed Exception: " + ex.msg + " from: " + from );
		}*/
		ZaSearch.TOO_MANY_RESULTS_FLAG = true ;
	}else{
		throw (ex) ;
	}
}

ZaSearch.findAccount = function(by, val) {
	var soapDoc = AjxSoapDoc.create("SearchDirectoryRequest", ZaZimbraAdmin.URN, null);
	soapDoc.getMethod().setAttribute("limit", "1");
	var query = ["(",by,"=",val,")"].join("");
	soapDoc.set("query", query);
	var command = new ZmCsfeCommand();
	var cmdParams = new Object();
	cmdParams.soapDoc = soapDoc;
	cmdParams.noAuthToken = true;	
	var resp = command.invoke(cmdParams).Body.SearchDirectoryResponse;	
	var list = new ZaItemList(ZaAccount);	
	list.loadFromJS(resp);	
	return list.getArray()[0];
}

ZaSearch.prototype.dynSelectDataCallback = function (params, resp) {
	var callback = params.callback;
	
	if(params.busyId)
		ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);
	
	if(!callback)	
		return;
	try {
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListViewController.prototype.dynSelectDataCallback"));
		}
		if(resp.isException()) {
			throw(resp.getException());
		} else {
			var response = resp.getResponse().Body.SearchDirectoryResponse;
            var list = new ZaItemList(null);
            list.loadFromJS(response);
            callback.run(list.getArray(), response.more, response.searchTotal);
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectDataCallback");	
	}
}

ZaSearch.prototype.dynSelectSearchCosesCallback = function (params, resp) {
	var callback = params.callback;
	
	if(params.busyId)
		ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);

	if(!callback)	
		return;
	try {
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListViewController.prototype.dynSelectSearchCosesCallback"));
		}
		if(resp.isException()) {
			throw(resp.getException());
		} else {
			var response = resp.getResponse().Body.SearchDirectoryResponse;
			var list = new ZaItemList(null);	
			list.loadFromJS(response);	
			var choices = new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");
			choices.setChoices(list.getArray());
			callback.run(list.getArray(), response.more, response.searchTotal);
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectSearchCosesCallback");	
	}
}
/**
 * @argument callArgs {value, event, callback}
 */
ZaSearch.prototype.dynSelectSearch = function (callArgs) {
	try {
		var value = callArgs["value"];
		var event = callArgs["event"];
		var callback = callArgs["callback"];
		var busyId = Dwt.getNextId ();
		
		var params = new Object();
		dataCallback = new AjxCallback(this, this.dynSelectDataCallback, {callback:callback,busyId:busyId });
		params.types = callArgs.types ? callArgs.types : [ZaSearch.ACCOUNTS, ZaSearch.DLS];
		if(!AjxUtil.isEmpty(callArgs.attrs)) {
			params.attrs = callArgs.attrs;
		}
		params.callback = dataCallback;
		params.sortBy = ZaAccount.A_name;
        if (callArgs["needCategorized"]) {
            params.limit = ZaSearch.BEST_MATCH_LIMIT;
            params.query = ZaSearch.getBestMatchSearchByNameQuery(value, params.types);
        } else {
            params.query = ZaSearch.getSearchByNameQuery(value, params.types);
        }
		params.controller = ZaApp.getInstance().getCurrentController();
		params.showBusy = true;
		params.busyId = busyId;
		params.busyMsg = ZaMsg.BUSY_SEARCHING;
		params.skipCallbackIfCancelled = false;
		if(callArgs.domain)
			params.domain = callArgs.domain;
			 		
		ZaSearch.searchDirectory(params);		
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectSearch");		
	}
}

ZaSearch.prototype.dynSearchField = function (callArgs) {
    var newCallArgs = {};
	newCallArgs.value = callArgs["value"];
    newCallArgs.event = callArgs["event"];
    newCallArgs.callback = callArgs["callback"];
    newCallArgs.needCategorized = true;
    newCallArgs.types = ZaApp.getInstance().getSearchListController()._searchField.getSearchTypes();
    ZaSearch.prototype.dynSelectSearch.call(this, newCallArgs);

}
/**
 * @argument callArgs {value, event, callback, extraLdapQuery}
 */
ZaSearch.prototype.dynSelectSearchGroups = function (callArgs) {
	try {
		var value = callArgs["value"];
		var event = callArgs["event"];
		var callback = callArgs["callback"];
		var extraLdapQuery = callArgs["extraLdapQuery"];
		var busyId = Dwt.getNextId ();
		
		var params = new Object();
		dataCallback = new AjxCallback(this, this.dynSelectDataCallback, {callback:callback,busyId:busyId});
		params.types = [ZaSearch.DLS];
        params.callback = dataCallback;
		params.sortBy = ZaAccount.A_name;
		params.query = ZaSearch.getSearchByNameQuery(value, params.types);
        if (extraLdapQuery) params.query = "(&" + extraLdapQuery + params.query + ")" ; 
        params.controller = ZaApp.getInstance().getCurrentController();
		params.busyId = busyId;
		params.showBusy = true;
		params.busyMsg = ZaMsg.BUSY_SEARCHING;
		params.skipCallbackIfCancelled = false; 		
		ZaSearch.searchDirectory(params);
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectSearchGroups");		
	}
}

/**
 * @argument callArgs {value, event, callback}
 */
ZaSearch.prototype.dynSelectSearchDomains = function (callArgs) {
	try {
		var value = callArgs["value"];
		var event = callArgs["event"];
		var callback = callArgs["callback"];
		var applyConfig = callArgs["applyConfig"];
		var busyId = Dwt.getNextId();
				
		var params = new Object();
		dataCallback = new AjxCallback(this, this.dynSelectDataCallback, {callback:callback,busyId:busyId});
		params.types = [ZaSearch.DOMAINS];
		params.callback = dataCallback;
		params.sortBy = ZaDomain.A_domainName;
        	params.query = "";
        	if(!ZaZimbraAdmin.hasGlobalDomainListAccess()) {
            		var domainNameList = ZaApp.getInstance()._domainNameList;
            		if(domainNameList && domainNameList instanceof Array) {
                		for(var i = 0; i < domainNameList.length; i++) {
                    			if(!value || domainNameList[i].indexOf(value) != -1)
                    			params.query += "(" + ZaDomain.A_domainName + "=" + domainNameList[i] + ")";
                		}
                		if(domainNameList.length > 1)
                    		params.query = "(|" + params.query + ")";
            		}
        	} else
		params.query = ZaSearch.getSearchDomainByNameQuery(value);
		params.controller = ZaApp.getInstance().getCurrentController();
		params.showBusy = true;
		params.busyId = busyId;
		params.applyConfig = applyConfig;
		params.busyMsg = ZaMsg.BUSY_SEARCHING_DOMAINS;
		params.skipCallbackIfCancelled = false;
        params.attrs = [ZaDomain.A_domainName,ZaDomain.A_zimbraDomainStatus,ZaItem.A_zimbraId, ZaDomain.A_domainType, ZaDomain.A_zimbraMailAddressValidationRegex];
		ZaSearch.searchDirectory(params);
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectSearchDomains");		
	}
}

ZaSearch.prototype.dynSelectSearchOnlyDomains = function (callArgs) {
        try {
                var value = callArgs["value"];
                var event = callArgs["event"];
                var callback = callArgs["callback"];
                var busyId = Dwt.getNextId();

                var params = new Object();
                dataCallback = new AjxCallback(this, this.dynSelectDataCallback, {callback:callback,busyId:busyId});
                params.types = [ZaSearch.DOMAINS];
                params.callback = dataCallback;
                params.sortBy = ZaDomain.A_domainName;
                params.query = ZaSearch.getSearchOnlyDomainByNameQuery(value);
                params.controller = ZaApp.getInstance().getCurrentController();
                params.showBusy = true;
                params.busyId = busyId;
                params.busyMsg = ZaMsg.BUSY_SEARCHING_DOMAINS;
                params.skipCallbackIfCancelled = false;
        params.attrs = [ZaDomain.A_domainName,ZaDomain.A_zimbraDomainStatus,ZaItem.A_zimbraId, ZaDomain.A_domainType];
                ZaSearch.searchDirectory(params);
        } catch (ex) {
                ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectSearchOnlyDomains"); 
        }
}

/**
 * @argument callArgs {value, event, callback}
 */
ZaSearch.prototype.dynSelectSearchCoses = function (callArgs) {
	try {
		var value = callArgs["value"];
		var event = callArgs["event"];
		var callback = callArgs["callback"];
		var busyId = Dwt.getNextId();
				
		var params = new Object();
		dataCallback = new AjxCallback(this, this.dynSelectSearchCosesCallback, {callback:callback,busyId:busyId});
		params.types = [ZaSearch.COSES];
		params.callback = dataCallback;
		params.sortBy = ZaCos.A_name;
                params.query = "";
                if(!ZaZimbraAdmin.hasGlobalCOSSListAccess()) {
                        var cosNameList = ZaApp.getInstance()._cosNameList;
                        if(cosNameList && (cosNameList instanceof Array) && cosNameList.length == 0) {
                            for(var i = 0; i < cosNameList.length; i++)
                                query += "(" + ZaCos.A_name + "=" + cosNameList[i] + ")";
                            if(cosNameList.length > 1)
                                query = "(|" + query + ")";
                        } else params.query = ZaSearch.getSearchCosByNameQuery(value);
                } else
		params.query = ZaSearch.getSearchCosByNameQuery(value);
		params.controller = ZaApp.getInstance().getCurrentController();
		params.showBusy = true;
		params.busyId = busyId;
		params.busyMsg = ZaMsg.BUSY_SEARCHING_COSES;
		params.skipCallbackIfCancelled = false;		
		ZaSearch.searchDirectory(params);
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectSearchCoses");		
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
function(query, types, pagenum, orderby, isascending,  attrs, limit, domainName, maxResults) {
	//if(!orderby) orderby = ZaAccount.A_uid;
	if(!orderby) orderby = ZaAccount.A_name;
	var myisascending = "1";
	
	if(!isascending ) {
		myisascending = "0";
	}
	
	limit = (limit != null)? limit: ZaAccount.RESULTSPERPAGE;
	
	var offset = (pagenum-1) * limit;
	attrs = (attrs != null)? attrs: ZaSearch.standardAttributes;
	/*
	var soapDoc = AjxSoapDoc.create("SearchDirectoryRequest", ZaZimbraAdmin.URN, null);
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
	var resp = command.invoke(params).Body.SearchDirectoryResponse; */
	
	//Use SearchDirectory
	var params = {
		"query": query,
		"offset": offset,
		"limit": limit,
		"applyCos" : "0",
		"attrs" : attrs ,
		"sortBy" : orderby ,
		"sortAscending": myisascending
	}	
	if (domainName != null) {
		params["domain"] = domainName;
	}
	if(types != null && types.length>0) {
		params["types"] = types.toString();
	}
	if(maxResults) {
		params["maxResults"] = maxResults.toString();
	}	
	params.controller = ZaApp.getInstance().getCurrentController ();
	var resp = ZaSearch.searchDirectory(params).Body.SearchDirectoryResponse ;
	
	var list = new ZaItemList(null);	
	list.loadFromJS(resp);
		
	var searchTotal = resp.searchTotal;
	var numPages = Math.ceil(searchTotal/limit);
	return {"list":list, "numPages":numPages, "searchTotal":searchTotal};
}

ZaSearch.searchByDomain = 
function (domainName, types, pagenum, orderby, isascending, attrs, limit) {
	return ZaSearch.search("", types, pagenum, orderby, isascending,  attrs, limit, domainName);
}

ZaSearch.getAccountStats =
function() {
	var soapDoc, params, retObj;
    retObj = {};
    retObj[ZaItem.ACCOUNT] = 0;
    retObj[ZaItem.ALIAS] = 0;
    retObj[ZaItem.RESOURCE] = 0;
    retObj[ZaItem.DL] = 0;

	//batch the rest of the requests
	soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
    soapDoc.setMethodAttribute("onerror", "continue");

    //ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS, ZaSearch.RESOURCES,ZaSearch.DOMAINS, ZaSearch.COSES
    var accDoc = soapDoc.set("SearchDirectoryRequest", null, null, ZaZimbraAdmin.URN);
    var queryString = "(!("+ ZaAccount.A_zimbraIsSystemAccount +"=TRUE))";
	accDoc.setAttribute("limit", "1");
    var elBy = soapDoc.set("query", queryString, accDoc);
    //elBy.setAttribute("by", by);
    soapDoc.set("limit", 1, accDoc);
    soapDoc.set("types", ZaSearch.ACCOUNTS, accDoc);

    accDoc = soapDoc.set("SearchDirectoryRequest", null, null, ZaZimbraAdmin.URN);
	accDoc.setAttribute("limit", "1");
    elBy = soapDoc.set("query", "", accDoc);
    soapDoc.set("limit", 1, accDoc);
    soapDoc.set("types", ZaSearch.ALIASES, accDoc);

    accDoc = soapDoc.set("SearchDirectoryRequest", null, null, ZaZimbraAdmin.URN);
	accDoc.setAttribute("limit", "1");
    elBy = soapDoc.set("query", "", accDoc);
    soapDoc.set("limit", 1, accDoc);
    soapDoc.set("types", ZaSearch.RESOURCES, accDoc);

    accDoc = soapDoc.set("SearchDirectoryRequest", null, null, ZaZimbraAdmin.URN);
	accDoc.setAttribute("limit", "1");
    elBy = soapDoc.set("query", "", accDoc);
    soapDoc.set("limit", 1, accDoc);
    soapDoc.set("types", [ZaSearch.DLS, ZaSearch.DDLS].toString(), accDoc);

    params = new Object();
    params.soapDoc = soapDoc;
    var reqMgrParams ={
        controller:ZaApp.getInstance().getCurrentController()
    }
    var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
    if(respObj && respObj.Body.BatchResponse) {
        var response = respObj.Body.BatchResponse.SearchDirectoryResponse;
        var attr;
        for(var i = 0; i < response.length; i++) {
            //if(response[i].account) retObj[ZaSearch.ACCOUNTS] = response[i].searchTotal;
            //else if(response[i].account) retObj[ZaSearch.ACCOUNTS] = response[i].searchTotal;
            if(response[i][ZaItem.ACCOUNT])  attr = ZaItem.ACCOUNT;
            else if(response[i][ZaItem.ALIAS])  attr = ZaItem.ALIAS;
            else if(response[i][ZaItem.RESOURCE])  attr = ZaItem.RESOURCE;
            else if(response[i][ZaItem.DL])  attr = ZaItem.DL;
            else attr = null;

            if(attr)
                retObj[attr] = response[i].searchTotal;
        }
    }
    return retObj;
}

ZaSearch.getSearchCosByNameQuery =
function(n) {
	if (n == null || n == "") {
		return "";
	} else {
		n = String(n).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
		return ("(|(uid=*"+n+"*)(cn=*"+n+"*)(sn=*"+n+"*)(gn=*"+n+"*)(zimbraId="+n+"))");
	}
}

ZaSearch.getSearchDomainByNameQuery =
function(n) {
	if (n == null || n == "") {
		return "";
	} else {
		n = String(n).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
		return ("(|(uid=*"+n+"*)(cn=*"+n+"*)(sn=*"+n+"*)(gn=*"+n+"*)(zimbraId="+n+")(zimbraDomainName=*"+n+"*))");
	}
}

ZaSearch.getSearchOnlyDomainByNameQuery =
function(n) {
        if (n == null || n == "") {
                return "";
        } else {
                n = String(n).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
                return ("(&(|(uid=*"+n+"*)(cn=*"+n+"*)(sn=*"+n+"*)(gn=*"+n+"*)(zimbraId="+n+")(zimbraDomainName=*"+n+"*))(zimbraDomainType=local))");
        }
}

ZaSearch.getSearchByNameQuery =
function(n, types,excludeClosed) {
	excludeClosed = excludeClosed ? excludeClosed : false;
	var query = [];
	/*if (n == null || n == "") {
		if(excludeClosed) {
			return "!(zimbraAccountStatus=closed)";
		} else {
			return "";
		}*/
	if(excludeClosed) {
		query.push("(&(!(zimbraAccountStatus=closed))");
	} 
	
	if (!AjxUtil.isEmpty(n)) {
		query.push("(|");
		// bug 67477, escape special symbols "(", ")", "*", "\"
		n = String(n).replace(/\\/g, "\\5C");
		n = String(n).replace(/\(/g, "\\28");
		n = String(n).replace(/\)/g, "\\29");
		n = String(n).replace(/\*/g, "\\2A");
        if (!types) types = [ZaSearch.ALIASES, ZaSearch.ACCOUNTS, ZaSearch.DLS, ZaSearch.RESOURCES, ZaSearch.DOMAINS, ZaSearch.COSES] ;
        var addedAddrFields = false;
        var addedAccResFields = false;
        var addedDLAliasFields = false;
        for (var i = 0 ; i < types.length; i ++) {
            if (types[i] == "domains") {
                query.push ("(zimbraDomainName=*"+n+"*)") ;
            } else if(types[i] == ZaSearch.COSES) {
		query.push("(cn=*" + n + "*)");
	    } else if(types[i] == ZaSearch.ALIASES) {
		query.push("(zimbraDomainName=*" + n + "*)(uid=*"+n+"*)");

	    }else {
            	if(!addedAddrFields) {
            		query.push("(mail=*"+n+"*)(cn=*"+n+"*)(sn=*"+n+"*)(gn=*"+n+"*)(displayName=*"+n+"*)") ;
            		addedAddrFields = true;
            	}
                if (!addedAccResFields && (types[i] == "accounts" || types[i] == "resources")) {
                    query.push ("(zimbraMailDeliveryAddress=*"+n+"*)");
                    addedAccResFields = true;
                } else if (!addedDLAliasFields && (types[i] == "distributionlists" || types[i] == "aliases")) {
                    query.push("(zimbraMailAlias=*"+n+"*)(uid=*"+n+"*)");
                    addedDLAliasFields = true;
                } 
            }
        }
	}
	if(excludeClosed) {
		query.push(")");
	} 
	if (!AjxUtil.isEmpty(n)) {
		query.push(")");
	}
	//return "(|" + query.join("") + ")" ;
	return query.join("");
	
}

/**
 * Get the query to search attributes that start or end with <code>n</code>.
 * @param n
 * @param types Array object that contains item types
 */
ZaSearch.getBestMatchSearchByNameQuery =
function(n, types) {
    var query = ZaSearch.getSearchByNameQuery(n, types);
    var orig = new RegExp("\\*" + n + "\\*","g");
    var lReg = new RegExp("^\\\s*\\(\\\s*\\|");
    var rReg = new RegExp("\\)\\\s*$");
    var beginWithQuery = query.replace(orig, n + "*");
    beginWithQuery = beginWithQuery.replace(lReg, "");
    beginWithQuery = beginWithQuery.replace(rReg,"");
    var endWithQuery = query.replace(orig, "*" + n);
    endWithQuery = endWithQuery.replace(lReg, "");
    endWithQuery = endWithQuery.replace(rReg,"");
    return "(|" + beginWithQuery + endWithQuery + ")";
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

ZaSearch.searchByQueryHolder = 
function (queryHolder, pagenum, orderby, isascending) {
	if(queryHolder.isByDomain) {
 		return ZaSearch.searchByDomain(queryHolder.byValAttr, queryHolder.types, pagenum, orderby, isascending);
	} else {
		return ZaSearch.search(queryHolder.queryString, queryHolder.types, pagenum, orderby, isascending,  queryHolder.fetchAttrs,
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
	searchObj[ZaSearch.A_fDomains] = "FALSE" ;
	searchObj[ZaSearch.A_fCoses] = "FALSE" ;
	
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
				searchObj[ZaSearch.A_fDomains] = "TRUE";
			}
                        if(query.types[i]==ZaSearch.COSES) {
                                searchObj[ZaSearch.A_fCoses] = "TRUE";
                        }
		}
	}
	return searchObj;
};

ZaSearch.myXModel = {
	items: [
		{id:ZaSearch.A_query, type:(appNewUI? _OBJECT_ : _STRING_)},
		{id:ZaSearch.A_selected, type:_OBJECT_, items:ZaAccount.myXModel},		
		{id:ZaSearch.A_fAliases, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaSearch.A_fdistributionlists, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaSearch.A_fAccounts, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaSearch.A_pagenum, type:_NUMBER_},
		{id:ZaSearch.A_ResultMsg, type:_STRING_}
	]
}

ZaSearchQuery = function(queryString, types, byDomain, byVal, attrsCommaSeparatedString, limit) {
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
function (opArr, orderArr) {
    opArr[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);								
	opArr[ZaOperation.LABEL] = new ZaOperation(ZaOperation.LABEL, AjxMessageFormat.format (ZaMsg.searchResultCount, [0,0]),
													 null, null, null, null,null,null,"ZaSearchResultCountLabel",ZaOperation.SEARCH_RESULT_COUNT);	
	opArr[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
    for (var i =0; i < orderArr.length; i ++) {
        if (orderArr[i] == ZaOperation.PAGE_BACK) {
            orderArr.splice(i + 1, 0, ZaOperation.SEP, ZaOperation.LABEL,ZaOperation.SEP) ;
            break ;
        }
    }
}

ZaSearch.isAccountExist = function (params) {
    var currentController =  ZaApp.getInstance().getCurrentController() ;

    var accountName = params.name ;
    var isPopupErrorDialog = params.popupError ? true : false;

    if (!accountName) {
        currentController.popupErrorDialog(ZaMsg.error_account_missing);
        return true ;
    }

    var params = { 	query: ["(mail=",accountName,")"].join(""),
                    limit : 2,
                    applyCos: 0,
                    types: [ZaSearch.DLS,ZaSearch.ALIASES,ZaSearch.ACCOUNTS,ZaSearch.RESOURCES],
                    controller: currentController
                 };
    try {
        var resp = ZaSearch.searchDirectory(params).Body.SearchDirectoryResponse;
    } catch (ex) {
        currentController._handleException(ex, "ZaSearch.isAccountExist", null, false);
    }
    var list = new ZaItemList(null);
    list.loadFromJS(resp);
    if(list.size() > 0) {
        if (isPopupErrorDialog) {
            var acc = list.getArray()[0];
            if(acc.type==ZaItem.ALIAS) {
                ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_aliasWithThisNameExists);
            } else if (acc.type==ZaItem.RESOURCE) {
                ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_resourceWithThisNameExists);
            } else if (acc.type==ZaItem.DL) {
                ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_dlWithThisNameExists);
            } else {
                ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_accountWithThisNameExists);
            }
        }

    }else {
        return false ;
    }

    return true;
}

/** Never use this call, always use CountAccountRequest
ZaSearch.getUsedDomainAccounts =
function (domainName, controller) {
	var params = {
		domain: domainName,
		limit: "0",
		type: "accounts",
		offset: "0",
		applyCos: "0",
		attrs: "",
		controller: controller
	}
	
	var resp = ZaSearch.searchDirectory(params) ;
	return resp.Body.SearchDirectoryResponse.searchTotal ;
}
*/

//modify the saved search 
//@param savedSearchArray : the array contains the saved searches obj to be modified.
//         		The object is {name: "saved search name", query : "saved search query" }
ZaSearch.modifySavedSearches =
function (savedSearchArray, callback) {
    if (ZaSearchField.canSaveSearch()){
        var soapDoc = AjxSoapDoc.create("ModifyAdminSavedSearchesRequest", ZaZimbraAdmin.URN, null);
        for (var i=0; i < savedSearchArray.length; i ++) {
            var cSavedSearch = savedSearchArray[i] ;
            var el = soapDoc.set("search", cSavedSearch.query) ;
            el.setAttribute("name", cSavedSearch.name) ;
        }
            
        var command = new ZmCsfeCommand();
        var cmdParams = new Object();
        cmdParams.soapDoc = soapDoc;
		cmdParams.noAuthToken = true;
        if (callback) {
            cmdParams.asyncMode = true;
            cmdParams.callback = callback;
        }
        command.invoke(cmdParams);
    }
}

//get saved searches
//@param searchNameArr: the array contains all the saved search names whose queries will be returned.
ZaSearch.getSavedSearches = 
function (searchNameArr, callback) {
    if (ZaSearchField.canViewSavedSearch()) {
        var soapDoc = AjxSoapDoc.create("GetAdminSavedSearchesRequest", ZaZimbraAdmin.URN, null);
        if (searchNameArr) {
            for (var i=0; i < searchNameArr.length; i ++) {
                var el = soapDoc.set("search", "") ;
                el.setAttribute("name", searchNameArr[i]) ;
            }
        }

        var command = new ZmCsfeCommand();
        var cmdParams = new Object();
        cmdParams.soapDoc = soapDoc;
		cmdParams.noAuthToken = true;
        if (callback) {
            cmdParams.asyncMode = true;
            cmdParams.callback = callback;
        }
        return command.invoke(cmdParams);
    }
}

ZaSearch.updateSavedSearch =
function (resp) {

	//if(window.console && window.console.log) console.debug("Update Saved Search ... ");
	ZaSearch.SAVED_SEARCHES = [] ;
	if (resp != null) {
        var respObj = resp._data || resp ;
        var searchResults = respObj.Body.GetAdminSavedSearchesResponse.search;

        if (searchResults) {
            for (var i=0; i < searchResults.length; i++) {
                ZaSearch.SAVED_SEARCHES.push ({
                    name: searchResults[i].name,
                    query: searchResults[i]._content
                })
            }
        }
    }
	
	ZaSearch._savedSearchToBeUpdated = false ;
}

ZaSearch.loadPredefinedSearch =
function () {
    if (ZaSearchField.canViewSavedSearch()) {
        //var currentSavedSearches = ZaSearch.getSavedSearches().Body.GetAdminSavedSearchesResponse.search;
        var currentSavedSearches = ZaApp.getInstance().getSavedSearchList();
        /*
         * If we get saved search from server and have write-permission, we will         * replace all the "zimbraIsDomainAdminAccount" with "zimbraIsDelegatedA         * dminAccount" to update the query string for version update      
         */ 
        if ( (!AjxUtil.isEmpty(currentSavedSearches)) && ( ZaSearchField.canSaveSearch() )){
            var modifiedSearches = [];
 
            for (var i = 0; i < currentSavedSearches.length; i++ ){
               var currentName = currentSavedSearches[i].name;
               var currentContent = currentSavedSearches[i].query;
    
               if (currentContent && currentContent.search(/zimbraIsDomainAdminAccount/) != -1){
             
                  currentContent = currentContent.replace(/zimbraIsDomainAdminAccount/g , "zimbraIsDelegatedAdminAccount"); //'g' is used for global replace
                  modifiedSearches.push ({
                      name  : currentName,
                      query : currentContent
                  });
               }
            } 
            
            if ( modifiedSearches.length != 0 ){
               ZaSearch.modifySavedSearches (modifiedSearches);
               ZaSearch.SAVED_SEARCHES = savedSearchArr;
               ZaSearch._savedSearchToBeUpdated = false ;
            }   
        }
        
        if ((AjxUtil.isEmpty(currentSavedSearches)) && (ZaSearchField.canSaveSearch())){//load the predefined searches
            //if(window.console && window.console.log) console.log("Load the predefined saved searches ...") ;
            var savedSearchArr = [] ;
            //if (!ZaSettings.isDomainAdmin) { //admin only searches
                for (var m=0; m < ZaSearch.getPredefinedSavedSearchesForAdminOnly().length; m++){
                    savedSearchArr.push (ZaSearch.getPredefinedSavedSearchesForAdminOnly()[m]) ;
                }
            //}

            for (var n=0; n < ZaSearch.getPredefinedSavedSearches().length; n ++) {
                savedSearchArr.push (ZaSearch.getPredefinedSavedSearches()[n]) ;
            }

            ZaSearch.modifySavedSearches (savedSearchArr) ;
            ZaSearch.SAVED_SEARCHES = savedSearchArr;
            ZaSearch._savedSearchToBeUpdated = false ;
        }
    }
}

/**
 * parse saved search query to allow the following searches:
 * - "Inactive Accounts (30 Days)" -- returns all accounts not logged in in 30 days
 * - "Inactive Accounts (90 Days)" -- returns all accounts not logged in in 90 days
 * 
 * This is a temperary solution to allow some dynamic data to be kept in the saved search 
 * and parsed on the client side before we have a formal query language.
 * 
 * Currently, it supports
 * 1)###JSON:{func: %function_name%, args:[%days%]}### : 
 * 	{func: %function_name%, args:[%days%]} is a JSON object with function name and arguments
 * 	 examples: 	{func: ZaSearch.getTimestampByDays , args: [30] } 				
 * 			
 * 
 */
ZaSearch.parseSavedSearchQuery =
function (query) {
	if (query == null || query.length <= 0) return ;
	//if(window.console && window.console.log) console.log("Original Saved Search query: " + query) ;
	var regEx = /^(.+)#{3}JSON:(.+)#{3}(.+)$/ ;
	var results = query.match(regEx) ;
	if (results != null) {
		query = results[1];
		eval ("var jsonObj = " + results[2] ) ;
		//call the function
		query += jsonObj.func(jsonObj.args) ;
		query += results[3];
	}
	//if(window.console && window.console.log) console.log("Parsed Saved Search query: " + query) ;
	return query ;
}

/**
 * return the server time string yyyyMMddHHmmssZ by current time + days
 * days: signed integer, can be 30 or -90, etc.
 */
ZaSearch.getTimestampByDays =
function (days) {
	//if(window.console && window.console.log) console.log("Get the timestamp of " + days + " days.");	
	var d = parseInt(days)	;
	var dateObj = new Date();
	var now = dateObj.getTime();
	dateObj.setTime(now + d * 86400 * 1000);
	return ZaUtil.getAdminServerDateTime(dateObj, true) ;
}


//Keep the saved searches
//A sample saved search object:
// {name:"savedA", query:"users"};
ZaSearch.SAVED_SEARCHES = [];
