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

/**
* @class ZaSearch
* @contructor ZaSearch
* this is a static class taht provides method for searching LDAP
* @author Greg Solovyev
**/
function ZaSearch() {
}

ZaSearch.ALIASES = "aliases";
ZaSearch.DLS = "distributionlists";
ZaSearch.ACCOUNTS = "accounts";

/**
* @param app reference to ZaApp
**/
ZaSearch.getAll =
function(app) {
	return ZaSearch.search("", [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS], 1, ZaAccount.A_uid, true, app);
}


ZaSearch.search =
function(query, types, pagenum, orderby, isascending, app) {
	if(!orderby) orderby = ZaAccount.A_uid;
	var myisascending = "0";
	
	if(isascending) {
		myisascending = "1";
	} 
	
	var offset = (pagenum-1) * ZaAccount.RESULTSPERPAGE;
	var attrs = ZaAccount.A_displayname + "," + ZaItem.A_zimbraId + "," + ZaAccount.A_mailHost + "," + ZaAccount.A_uid + "," + ZaAccount.A_accountStatus + "," + ZaAccount.A_description;
	var soapDoc = AjxSoapDoc.create("SearchAccountsRequest", "urn:zimbraAdmin", null);
	soapDoc.set("query", query);
	soapDoc.getMethod().setAttribute("offset", offset);
	soapDoc.getMethod().setAttribute("limit", ZaAccount.RESULTSPERPAGE);
	soapDoc.getMethod().setAttribute("applyCos", "0");
	soapDoc.getMethod().setAttribute("attrs", attrs);
	soapDoc.getMethod().setAttribute("sortBy", orderby);
	soapDoc.getMethod().setAttribute("sortAscending", myisascending);
	if(types != null && types.length>0) {
		soapDoc.getMethod().setAttribute("types", types.toString());
	}
	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;
	var list = new ZaItemList(null, app);
	list.loadFromDom(resp);
	var searchTotal = resp.getAttribute("searchTotal");
	var numPages = Math.ceil(searchTotal/ZaAccount.RESULTSPERPAGE);
	return {"list":list, "numPages":numPages};
}

ZaSearch.searchByDomain = 
function (domainName, types, pagenum, orderby, isascending, app) {
	if(!orderby) orderby = ZaAccount.A_uid;
	var myisascending = "0";
	
	if(isascending) {
		myisascending = "1";
	} 
	
	var offset = (pagenum-1) * ZaAccount.RESULTSPERPAGE;
	var attrs = ZaAccount.A_displayname + "," + ZaItem.A_zimbraId + "," + ZaAccount.A_mailHost + "," + ZaAccount.A_uid + "," + ZaAccount.A_accountStatus + "," + ZaAccount.A_description;
	var soapDoc = AjxSoapDoc.create("SearchAccountsRequest", "urn:zimbraAdmin", null);
	soapDoc.set("query", "");
	soapDoc.getMethod().setAttribute("domain", domainName);
	soapDoc.getMethod().setAttribute("limit", ZaAccount.RESULTSPERPAGE);
	soapDoc.getMethod().setAttribute("offset", offset);
	soapDoc.getMethod().setAttribute("applyCos", "0");
	soapDoc.getMethod().setAttribute("attrs", attrs);
	soapDoc.getMethod().setAttribute("sortBy", orderby);
	soapDoc.getMethod().setAttribute("sortAscending", myisascending);
	if(types != null && types.length>0) {
		soapDoc.getMethod().setAttribute("types", types.toString());
	}	
	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, true).firstChild;
	var list = new ZaItemList(null, app);
	list.loadFromDom(resp);
	var searchTotal = resp.getAttribute("searchTotal");
	var numPages = Math.ceil(searchTotal/ZaAccount.RESULTSPERPAGE);
	return {"list":list, "numPages":numPages};
	
}


ZaSearch.getSearchByNameQuery =
function(n) {
	if (n == null || n == "") {
		return "";
	} else {
		return ("(|(cn=*"+n+"*)(sn=*"+n+"*)(gn=*"+n+"*)(displayName=*"+n+"*)(zimbraMailAlias=*"+n+"*)(zimbraId="+n+")(zimbraMailAddress=*"+n+"*)(zimbraMailDeliveryAddress=*"+n+"*))");
	}
}

ZaSearch.searchByQueryHolder = 
function (queryHolder, pagenum, orderby, isascending, app) {
	if(queryHolder.isByDomain) {
 		return ZaSearch.searchByDomain(queryHolder.byValAttr, queryHolder.types, pagenum, orderby, isascending, app);
	} else {
		return ZaSearch.search(queryHolder.queryString, queryHolder.types, pagenum, orderby, isascending, app);	
	}
}


function ZaSearchQuery (queryString, types, byDomain, byVal) {
	this.query = queryString;
	this.isByDomain = byDomain;
	this.byValAttr = byVal;
	this.types = types;
}