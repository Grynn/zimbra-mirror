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

/**
* @class ZaSearch
* @contructor ZaSearch
* this is a static class taht provides method for searching LDAP
* @author Greg Solovyev
**/
function ZaSearch() {
	this[ZaSearch.A_selected] = null;
	this[ZaSearch.A_query] = "";
	this[ZaSearch.A_fAliases] = "TRUE";
	this[ZaSearch.A_fAccounts] = "TRUE";	
	this[ZaSearch.A_fdistributionlists] = "TRUE";	
	this[ZaSearch.A_pagenum]=1;	
}
ZaSearch.ALIASES = "aliases";
ZaSearch.DLS = "distributionlists";
ZaSearch.ACCOUNTS = "accounts";

ZaSearch.TYPES = new Object();
ZaSearch.TYPES[ZaItem.ALIAS] = ZaSearch.ALIASES;
ZaSearch.TYPES[ZaItem.DL] = ZaSearch.DLS;
ZaSearch.TYPES[ZaItem.ACCOUNT] = ZaSearch.ACCOUNTS;


ZaSearch.A_query = "query";
ZaSearch.A_selected = "selected";
ZaSearch.A_pagenum = "pagenum";
ZaSearch.A_fAliases = "f_aliases";
ZaSearch.A_fAccounts = "f_accounts";
ZaSearch.A_fdistributionlists = "f_distributionlists";

/**
* @param app reference to ZaApp
**/
ZaSearch.getAll =
function(app) {
	return ZaSearch.search("", [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS], 1, ZaAccount.A_uid, true, app);
}


ZaSearch.standardAttributes = AjxBuffer.concat(ZaAccount.A_displayname,",",
											   ZaItem.A_zimbraId,  "," , 
											   ZaAccount.A_mailHost , "," , 
											   ZaAccount.A_uid ,"," , 
											   ZaAccount.A_accountStatus , "," , 
											   ZaAccount.A_description, ",",
											   ZaDistributionList.A_mailStatus);

/**
* Sends SearchAccountsRequest to the SOAP Servlet
* @param query - query string
* @param types - array of object types to search for([ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS])
* @pagenum - results page number
* @orderby - attribute to sort by
* @isascending - sort order (boolean)
* @app - reference to ZaApp instance (will be passed on to ZaItemList contructor)
* @attrs - coma separated list of attributes to return (default: ZaSearch.standardAttributes)
* @limit - maximum number of records to return
* @domainName - domain name (optional, if searching within one domain)
**/
ZaSearch.search =
function(query, types, pagenum, orderby, isascending, app, attrs, limit, domainName) {
	if(!orderby) orderby = ZaAccount.A_uid;
	var myisascending = "0";
	
	if(isascending) {
		myisascending = "1";
	} 
	
	limit = (limit != null)? limit: ZaAccount.RESULTSPERPAGE;
	
	var offset = (pagenum-1) * limit;
	attrs = (attrs != null)? attrs: ZaSearch.standardAttributes;
	var soapDoc = AjxSoapDoc.create("SearchAccountsRequest", "urn:zimbraAdmin", null);
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
	
	

	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = command.invoke(params).Body.SearchAccountsResponse;
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
		return ("(|(uid=*"+n+"*)(cn=*"+n+"*)(sn=*"+n+"*)(gn=*"+n+"*)(displayName=*"+n+"*)(zimbraId="+n+")(mail=*"+n+"*)(zimbraMailAlias=*"+n+"*)(zimbraMailDeliveryAddress=*"+n+"*))");
	}
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
/**
* static data for temporary use with queue management untill the server part is ready
**/
ZaSearch._temporaryMailQSearchResults = {};
ZaSearch._temporaryMailQSearchResults["deferred"] = {};
ZaSearch._temporaryMailQSearchResults["deferred"]["yahoo.com"] = [
	{qid:111, destination:"yahoo.com", origin:"64.23.45.222", toString:function() {return this.qid;}},
	{qid:112, destination:"yahoo.com", origin:"221.23.45.26", toString:function() {return this.qid;}},
	{qid:113, destination:"yahoo.com", origin:"121.23.45.123", toString:function() {return this.qid;}},	
	{qid:114, destination:"yahoo.com", origin:"220.63.45.201", toString:function() {return this.qid;}},		
	{qid:115, destination:"yahoo.com", origin:"220.63.45.101", toString:function() {return this.qid;}},			
	{qid:116, destination:"yahoo.com", origin:"62.63.45.101", toString:function() {return this.qid;}},				
	{qid:117, destination:"yahoo.com", origin:"12.13.15.101", toString:function() {return this.qid;}}					
]; 
ZaSearch._temporaryMailQSearchResults["deferred"]["gmail.com"] = [
	{qid:211, destination:"gmail.com", origin:"64.23.45.222", toString:function() {return this.qid;}},
	{qid:212, destination:"gmail.com", origin:"221.23.45.26", toString:function() {return this.qid;}},
	{qid:213, destination:"gmail.com", origin:"121.23.45.123", toString:function() {return this.qid;}},	
	{qid:214, destination:"gmail.com", origin:"220.63.45.201", toString:function() {return this.qid;}},		
	{qid:215, destination:"gmail.com", origin:"220.63.45.101", toString:function() {return this.qid;}},			
	{qid:216, destination:"gmail.com", origin:"62.63.45.101", toString:function() {return this.qid;}},				
	{qid:217, destination:"gmail.com", origin:"12.13.15.101", toString:function() {return this.qid;}}
]; 
ZaSearch._temporaryMailQSearchResults["deferred"]["hotmail.com"] = [
	{qid:311, destination:"hotmail.com", origin:"64.23.45.222", toString:function() {return this.qid;}},
	{qid:312, destination:"hotmail.com", origin:"221.23.45.26", toString:function() {return this.qid;}},
	{qid:313, destination:"hotmail.com", origin:"121.23.45.123", toString:function() {return this.qid;}},	
	{qid:314, destination:"hotmail.com", origin:"220.63.45.201", toString:function() {return this.qid;}},		
	{qid:315, destination:"hotmail.com", origin:"220.63.45.101", toString:function() {return this.qid;}},			
	{qid:316, destination:"hotmail.com", origin:"62.63.45.101", toString:function() {return this.qid;}},				
	{qid:317, destination:"hotmail.com", origin:"12.13.15.101", toString:function() {return this.qid;}}
]; 
ZaSearch._temporaryMailQSearchResults["deferred"]["usa.net"] = [
	{qid:411, destination:"usa.net", origin:"64.23.45.222", toString:function() {return this.qid;}},
	{qid:412, destination:"usa.net", origin:"221.23.45.26", toString:function() {return this.qid;}},
	{qid:413, destination:"usa.net", origin:"121.23.45.123", toString:function() {return this.qid;}},	
	{qid:414, destination:"usa.net", origin:"220.63.45.201", toString:function() {return this.qid;}},		
	{qid:415, destination:"usa.net", origin:"220.63.45.101", toString:function() {return this.qid;}},			
	{qid:416, destination:"usa.net", origin:"62.63.45.101", toString:function() {return this.qid;}},				
	{qid:417, destination:"usa.net", origin:"12.13.15.101", toString:function() {return this.qid;}}
]; 

ZaSearch._temporaryMailQSearchResults["incoming"] = {};
ZaSearch._temporaryMailQSearchResults["incoming"]["yahoo.com"] = [
	{qid:111, destination:"yahoo.com", origin:"64.23.45.222", toString:function() {return this.qid;}},
	{qid:112, destination:"yahoo.com", origin:"221.23.45.26", toString:function() {return this.qid;}},
	{qid:113, destination:"yahoo.com", origin:"121.23.45.123", toString:function() {return this.qid;}},	
	{qid:114, destination:"yahoo.com", origin:"220.63.45.201", toString:function() {return this.qid;}},		
	{qid:115, destination:"yahoo.com", origin:"220.63.45.101", toString:function() {return this.qid;}},			
	{qid:116, destination:"yahoo.com", origin:"62.63.45.101", toString:function() {return this.qid;}},				
	{qid:117, destination:"yahoo.com", origin:"12.13.15.101", toString:function() {return this.qid;}}					
]; 
ZaSearch._temporaryMailQSearchResults["incoming"]["gmail.com"] = [
	{qid:211, destination:"gmail.com", origin:"64.23.45.222", toString:function() {return this.qid;}},
	{qid:212, destination:"gmail.com", origin:"221.23.45.26", toString:function() {return this.qid;}},
	{qid:213, destination:"gmail.com", origin:"121.23.45.123", toString:function() {return this.qid;}},	
	{qid:214, destination:"gmail.com", origin:"220.63.45.201", toString:function() {return this.qid;}},		
	{qid:215, destination:"gmail.com", origin:"220.63.45.101", toString:function() {return this.qid;}},			
	{qid:216, destination:"gmail.com", origin:"62.63.45.101", toString:function() {return this.qid;}},				
	{qid:217, destination:"gmail.com", origin:"12.13.15.101", toString:function() {return this.qid;}}
]; 
ZaSearch._temporaryMailQSearchResults["incoming"]["hotmail.com"] = [
	{qid:311, destination:"hotmail.com", origin:"64.23.45.222", toString:function() {return this.qid;}},
	{qid:312, destination:"hotmail.com", origin:"221.23.45.26", toString:function() {return this.qid;}},
	{qid:313, destination:"hotmail.com", origin:"121.23.45.123", toString:function() {return this.qid;}},	
	{qid:314, destination:"hotmail.com", origin:"220.63.45.201", toString:function() {return this.qid;}},		
	{qid:315, destination:"hotmail.com", origin:"220.63.45.101", toString:function() {return this.qid;}},			
	{qid:316, destination:"hotmail.com", origin:"62.63.45.101", toString:function() {return this.qid;}},				
	{qid:317, destination:"hotmail.com", origin:"12.13.15.101", toString:function() {return this.qid;}}
]; 
ZaSearch._temporaryMailQSearchResults["incoming"]["usa.net"] = [
	{qid:411, destination:"usa.net", origin:"64.23.45.222", toString:function() {return this.qid;}},
	{qid:412, destination:"usa.net", origin:"221.23.45.26", toString:function() {return this.qid;}},
	{qid:413, destination:"usa.net", origin:"121.23.45.123", toString:function() {return this.qid;}},	
	{qid:414, destination:"usa.net", origin:"220.63.45.201", toString:function() {return this.qid;}},		
	{qid:415, destination:"usa.net", origin:"220.63.45.101", toString:function() {return this.qid;}},			
	{qid:416, destination:"usa.net", origin:"62.63.45.101", toString:function() {return this.qid;}},				
	{qid:417, destination:"usa.net", origin:"12.13.15.101", toString:function() {return this.qid;}}
]; 

ZaSearch.searchMailQ = function (app, queue, limit, offset,destination,origin,error,sortBy,sortAscending) {
	var list = new ZaList(ZaPostQItem,app);
	//Temporary using static data
	var q =  ZaSearch._temporaryMailQSearchResults[queue];
	if(q) {
		var cnt = q.length;
		for(var i=0;i<cnt;i++) {
			if(destination && !origin) {
				if(q[i][ZaPostQ.A_destination] == destination) {
					list.add(q[i]);
				}
			} else if(!destination && origin) {
				if(q[i][ZaPostQ.A_origin] == origin) {
					list.add(q[i]);
				}
			} else if(destination && origin) {
				if( (q[i][ZaPostQ.A_origin] == origin) && (q[i][ZaPostQ.A_destination] == destination)){
					list.add(q[i]);
				}				
			} else if(!destination && !origin) {
				list.add(q[i]);
			}
		}
	} 
	//end static data
}

ZaSearch.getSearchFromQuery = function (query) {
	var searchObj = new ZaSearch();
	searchObj[ZaSearch.A_selected] = null;
	searchObj[ZaSearch.A_query] = query.queryString;
	searchObj[ZaSearch.A_fAliases] = "FALSE";
	searchObj[ZaSearch.A_fAccounts] = "FALSE";
	searchObj[ZaSearch.A_fdistributionlists] = "FALSE";
	
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
