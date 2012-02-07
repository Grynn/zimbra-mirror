/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaSearchBuilderController
* @contructor ZaSearchBuilderController
* Provides all the data and UI action controlls over the advanced search builder options
* @author Charles Cao
**/
ZaSearchBuilderController = function(appCtxt, container) {
	ZaController.call(this, appCtxt, container,"ZaSearchBuilderController");
   	this._option_views = [];
	this._searchBuildPanel = null;
	this._searchBuildTBPanel = null ;
	this._searchBuilderVisible = false ; //also indicate whether the advanced search query should be used
	this._query = null ;
	this._searchTypes = null ;
	this._objTypeOptionViewPosition = -1; //indicate the whether objTypeOptionView is visible or not by its position
	this._serverOptionViewPosition = -1 ;
	this._numberOfDomainOptions = 0; //how many domainOptions are visible
	this._cosids = null;
}

ZaSearchBuilderController.prototype = new ZaController();
ZaSearchBuilderController.prototype.constructor = ZaSearchBuilderController;

ZaSearchBuilderController.prototype.getSearchBuilderPanel =
function () {
	if (! this._searchBuildPanel) {
		DBG.println(AjxDebug.DBG3, "Initializing the search builder option panel.") ;
		this._searchBuildPanel = new ZaSearchBuilderView (ZaApp.getInstance().getAppCtxt().getShell());
		//always display the basic search when the search builder view is initialized.
		this.addOptionView (ZaSearchOption.BASIC_TYPE_ID) ;
	}
	return this._searchBuildPanel ;
}


ZaSearchBuilderController.prototype.getSearchBuilderTBPanel =
function () {
	if (! this._searchBuildTBPanel) {
		DBG.println(AjxDebug.DBG3, "Initialize the search builder toolbar panel.") ;
		this._searchBuildTBPanel = new ZaSearchBuilderToolbarView (ZaApp.getInstance().getAppCtxt().getShell());
	}
	return this._searchBuildTBPanel ;
}

ZaSearchBuilderController.prototype.toggleVisible =
function () {
	this._searchBuilderVisible = ! this._searchBuilderVisible ;
}

ZaSearchBuilderController.prototype.isSBVisible =
function () {
	return this._searchBuilderVisible ;
} 

//test if the current query string is LDAP query string or a basic search string.
ZaSearchBuilderController.prototype.isAdvancedSearch =
function (query) {
	var regEx =  /\([^\(\)\=]+=[^\(\)\=]+\)/ ; //ldap query string regEx
	if (query.match(regEx) != null) {
		return true ;
	}
	
	return  false ;
} 

ZaSearchBuilderController.handleOptions =
function (value, event, form){
//	DBG.println(AjxDebug.DBG3, "Handling the options on the search builder toolbar ...");
	
	var controller = ZaApp.getInstance().getSearchBuilderController();
	var searchField = ZaApp.getInstance().getSearchListController()._searchField ;
	
	var charCode = event.charCode;
	if (charCode == 13 || charCode == 3) {
	   searchField.invokeCallback();
	} else {
		this.setInstanceValue(value);
		
		//handle the special cases
		//1) domain admin and admin account search option is mutual exclusive.
		var invertValue ;
		if (value == "TRUE") { 
			invertValue = "FALSE";
		}

        var ref = this.getRef () ; 
        //can't each both be set
		if (invertValue == "FALSE" && ref == ZaSearchOption.A_objTypeAccountDomainAdmin) {
			this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeAccountAdmin) ;
			this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeSystemAccount);
		}else if (invertValue == "FALSE" && ref == ZaSearchOption.A_objTypeAccountAdmin) {
			this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeAccountDomainAdmin) ;
			this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeSystemAccount);
		} else if (invertValue == "FALSE" && ref == ZaSearchOption.A_objTypeSystemAccount) {
			this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeAccountAdmin) ;
			this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeAccountDomainAdmin) ;
		}

        //set the advanced login timestamp attributes value
        if ((ref == ZaSearchOption.A_enableAccountLastLoginTime_From || ref == ZaSearchOption.A_enableAccountLastLoginTime_To)
            && ( value == "TRUE" )) {
            var loginTimeRef ;
            if (ref == ZaSearchOption.A_enableAccountLastLoginTime_From) loginTimeRef = ZaSearchOption.A_accountLastLoginTime_From ;
            if (ref == ZaSearchOption.A_enableAccountLastLoginTime_To) loginTimeRef = ZaSearchOption.A_accountLastLoginTime_To ;

            var loginTime = this.getInstanceValue (loginTimeRef);
            if (!loginTime) {
                this.setInstanceValue (new Date(), loginTimeRef) ;
            }
        }

        //set the query value
		controller.setQuery () ;
	}
	
	//disable the domain option button if domain is checked
	/*
	if (this.getRef () == ZaSearchOption.A_objTypeDomain) {
		var domainBT = controller._searchBuildTBPanel.getButton (ZaOperation.SEARCH_BY_DOMAIN) ;
		if (domainBT) {
			domainBT.setEnabled (value == "FALSE" ? true : false);
		}
	}*/
}

ZaSearchBuilderController.filterDomains =
function (value, event, form) {
	this.setInstanceValue (value);
	var busyId = Dwt.getNextId();
	var callback = new AjxCallback(this, ZaSearchBuilderController.optionFilterCallback, {busyId:busyId});
	var searchParams = {
			query: "(" + ZaDomain.A_domainName + "=*" + value + "*)", 
			types: [ZaSearch.DOMAINS],
			sortBy: ZaDomain.A_domainName,
			attrs: [ZaDomain.A_domainName],
			applyCos: "0",
			callback:callback,
			controller: ZaApp.getInstance().getCurrentController(),
			showBusy:true,
			busyId:busyId,
			busyMsg:ZaMsg.BUSY_SEARCHING_DOMAINS,
			skipCallbackIfCancelled:false			
	}
	ZaSearch.searchDirectory(searchParams);
}

ZaSearchBuilderController.filterCOSES =
function (value, event, form) {
        this.setInstanceValue (value);
        var busyId = Dwt.getNextId();
        var callback = new AjxCallback(this, ZaSearchBuilderController.optionCosFilterCallback, {busyId:busyId});
        var searchParams = {
                        query: "(" + ZaCos.A_name + "=*" + value + "*)",
                        types: [ZaSearch.COSES],
                        sortBy: ZaCos.A_name,
                        attrs: [ZaCos.A_name],
                        applyCos: "0",
                        callback:callback,
                        controller: ZaApp.getInstance().getCurrentController(),
                        showBusy:true,
                        busyId:busyId,
                        busyMsg:ZaMsg.BUSY_SEARCHING_COSES,
                        skipCallbackIfCancelled:false
        }
        ZaSearch.searchDirectory(searchParams);
}


ZaSearchBuilderController.prototype.listAllServers =
function () {
	var serverView = this._option_views[this._option_views.length -1];
	
	var form = serverView._localXForm;
	var instance = form.getInstance ()
	var list = ZaServer.getAll().getArray ();
	var servers = new Array (list.length) ;
	for (var i = 0; i < servers.length; i ++) {
		servers [i] = list [i].name ;
	}
	
	//set the list and refresh the list UI
	instance["options"][ZaSearchOption.A_serverList] = servers ;
	
	//reset the checked domain list vector
	instance["options"][ZaSearchOption.A_serverListChecked] = new AjxVector ();
	
	form.refresh () ;
}

ZaSearchBuilderController.prototype.updateServerFilter=
function (dialog) {
	var serverView = dialog;

	var form = serverView._localXForm;
	var instance = form.getInstance ()
	var list = ZaServer.getAll().getArray ();
	var servers = new Array (list.length) ;
	for (var i = 0; i < servers.length; i ++) {
		servers [i] = list [i].name ;
	}

	//set the list and refresh the list UI
	instance["options"][ZaSearchOption.A_serverList] = servers ;

	//reset the checked domain list vector
	instance["options"][ZaSearchOption.A_serverListChecked] = new AjxVector ();

	form.refresh () ;
}

ZaSearchBuilderController.filterSelectionListener =
function (value) {
	var targetEl = value.target ;
	
//	if (targetEl instanceof HTMLInputElement) { //not Supported by IE
	if (targetEl.type && targetEl.type == "checkbox") {
		var ref = this.getRef ();
		var item = targetEl.value ;
		var form = this.getForm ();
		var instance = form.getInstance ();
		var checkedFiltersVector = null ;
		if (ref == ZaSearchOption.A_domainList) {
			checkedFiltersVector = instance["options"][ZaSearchOption.A_domainListChecked];
		}else if (ref == ZaSearchOption.A_serverList) {
			checkedFiltersVector = instance["options"][ZaSearchOption.A_serverListChecked];
		}else if (ref == ZaSearchOption.A_cosList) {
                        checkedFiltersVector = instance["options"][ZaSearchOption.A_cosListChecked];
                }

		var controller = form.parent._controller ;
		DBG.println(AjxDebug.DBG3, item + " is selected ... ");
		if (targetEl.checked) { //after the selection it will be uncheck
			DBG.println(AjxDebug.DBG3, "remove the choice " + targetEl.value );
			checkedFiltersVector.remove(item);
			//controller._domainFiltersVector.remove(domain) ;
		}else{
			DBG.println(AjxDebug.DBG3, "add the choice " + targetEl.value );
			checkedFiltersVector.add(item);
			//controller._domainFiltersVector.add (domain) ;
		}
		
		DBG.println(AjxDebug.DBG3, "Update the query ... ");
		controller.setQuery () ;
	}
}

ZaSearchBuilderController.optionFilterCallback =
function (params,resp) {
	DBG.println(AjxDebug.DBG3, "Check for the filter results ... ") ;
	if(params.busyId)
			ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);
	
	var form = this.getForm ();
	try {
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListViewController.prototype.searchCallback"));
		}
		if(resp.isException()) {
			throw(resp.getException());
		} else {
			var response = resp.getResponse().Body.SearchDirectoryResponse;
			if (response.domain && response.domain.length > 0) {
				var domains = new Array (response.domain.length);
				for (var i =0; i < domains.length; i ++) {
					domains[i] = response.domain[i].name ;
				}
				var searchTotal = response.searchTotal;
				
				//set the list and refresh the list UI
				this.setInstanceValue (domains, "/options/" + ZaSearchOption.A_domainList);
				
				//reset the checked domain list vector
				this.setInstanceValue (new AjxVector (), "/options/" + ZaSearchOption.A_domainListChecked);
				form.refresh () ;
			}else{//no match domain found
				this.setInstanceValue ([], "/options/" + ZaSearchOption.A_domainList);
				this.setInstanceValue (new AjxVector (), "/options/" + ZaSearchOption.A_domainListChecked);
				form.refresh () ;
			}
		}
	} catch (ex) {
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			form.parent._controller._handleException(ex, "ZaSearchBuilderController.optionFilterCallback");	
		} else {
			form.parent._controller.popupErrorDialog(ZaMsg.queryParseError, ex);
		}		
	}
}


ZaSearchBuilderController.optionCosFilterCallback =
function (params,resp) {
        if(params.busyId)
                        ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);

        var form = this.getForm ();
	var controller = ZaApp.getInstance().getSearchBuilderController();
        try {
                if(!resp) {
                        throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListViewController.prototype.searchCallback"));
                }
                if(resp.isException()) {
                        throw(resp.getException());
                } else {
                        var response = resp.getResponse().Body.SearchDirectoryResponse;
                        if (response.cos && response.cos.length > 0) {
                                var coses = new Array (response.cos.length);
				controller._cosids = new Array (response.cos.length);
                                for (var i =0; i < coses.length; i ++) {
                                        coses[i] = response.cos[i].name ;
					controller._cosids[coses[i]] = response.cos[i].id;

                                }
                                
                                this.setInstanceValue (coses, "/options/" + ZaSearchOption.A_cosList);

                                this.setInstanceValue (new AjxVector (), "/options/" + ZaSearchOption.A_cosListChecked);
                                form.refresh () ;
                        }else{
                                this.setInstanceValue ([], "/options/" + ZaSearchOption.A_cosList);
                                this.setInstanceValue (new AjxVector (), "/options/" + ZaSearchOption.A_cosListChecked);
                                form.refresh () ;
                        }
                }
        } catch (ex) {
                if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
                        form.parent._controller._handleException(ex, "ZaSearchBuilderController.optionCosFilterCallback");
                } else {
                        form.parent._controller.popupErrorDialog(ZaMsg.queryParseError, ex);
                }
        }
}



ZaSearchBuilderController.prototype.handleSpecialQueries = 
function () {
	var optionViews = this.getOptionViews () ;
	this._includeNeverLoggedInAccts = false ; //by default
    this._includeObjectWithoutCosId = false;  // by default
	for (var i =0 ; i < optionViews.length; i++) {
		var optionId = optionViews[i]._optionId ;
		var instance = optionViews[i]._localXForm.getInstance () ;
		//handle the special case never logged in accounts
		if (this._includeNeverLoggedInAccts == false //if it is set, then we won't change it again.
				&& instance[ZaSearchOption.A_includeNeverLoginedAccounts]
				&& instance[ZaSearchOption.A_includeNeverLoginedAccounts] == "TRUE" ) 
		{
			this._includeNeverLoggedInAccts = true ;	
		}
		if (this._includeObjectWithoutCosId == false && instance[ZaSearchOption.A2_cosNotSet]
				&& instance[ZaSearchOption.A2_cosNotSet] == "TRUE" )
		{
			this._includeObjectWithoutCosId = true ;
		}
	}
}

/**
 * Set the query value based on the LDAP query language for the advanced search 
 * and the query value will be displayed on the search bar also.
 * 
 */
ZaSearchBuilderController.prototype.setQuery =
function () {
	this.handleSpecialQueries () ;
	var optionViews = this.getOptionViews () ;
		
	this._query = null ;
	this._searchTypes = null ;
	//_filterObj holds all the options objects
	this._filterObj = {} ;
	this._filterObj [ZaSearchOption.BASIC_TYPE_ID] = [] ;
	this._filterObj [ZaSearchOption.OBJECT_TYPE_ID] = [] ;
	this._filterObj [ZaSearchOption.DOMAIN_ID] = [] ;
	this._filterObj [ZaSearchOption.SERVER_ID] = [] ;
	this._filterObj [ZaSearchOption.ADVANCED_ID] = [] ;
	this._filterObj [ZaSearchOption.COS_ID] = [] ;
	
	for (var i =0 ; i < optionViews.length; i++) {
		var optionId = optionViews[i]._optionId ;
		var instance = optionViews[i]._localXForm.getInstance () ;
		
		var options = instance ["options"] ;
		var filter = [];

        if(optionId == ZaSearchOption.COS_ID && this._includeObjectWithoutCosId
                && (!options[ZaSearchOption.A_cosListChecked] || options[ZaSearchOption.A_cosListChecked].size() == 0))
             filter.push("(!(" + ZaAccount.A_COSId + "=*))");

		for (var key in options) {
			var value = options[key] ;
			if (value != null){
				var op = null ; //the operator of the filter
				if (value instanceof Date) { //the date type options
					value = ZaUtil.getAdminServerDateTime(value, true) ;
				}
				if (key == ZaSearchOption.A_accountLastLoginTime_From) {
					if (instance[ZaSearchOption.A_enableAccountLastLoginTime_From] == "TRUE") {
						key = ZaAccount.A_zimbraLastLogonTimestamp ;
						op = ">=" ;
					}else{
						continue ;
					}
				}
				
				if (key == ZaSearchOption.A_accountLastLoginTime_To) {
					if (instance[ZaSearchOption.A_enableAccountLastLoginTime_To] == "TRUE") {
						key = ZaAccount.A_zimbraLastLogonTimestamp ;
						op = "<=" ;
					}else{
						continue ;
					}
				}
				
				if ((value.length > 0) 
						|| ((value instanceof AjxVector) && (value.size() > 0)))  {
					//TODO: handle the checkbox TRUE or FALSE value
					this._addFilter (filter, key, value, op) ;	
				}
			}
		}
		this._filterObj[optionId].push(filter);
	}
	this._query = this.getQueryFromFilters () ;
	this._searchTypes = this.getSearchTypesFromFilters ();
	DBG.println(AjxDebug.DBG1, "Current Query String = " + this._query) ;
	
	//update the search field textbox entry
	var searchFieldXform = ZaApp.getInstance().getSearchListController()._searchField._localXForm;
	var	searchFieldInstance = searchFieldXform.getInstance ();
	var searchFieldItem = searchFieldXform.getItemsById(ZaSearch.A_query)[0];
	if (this.isSBVisible()){
		searchFieldItem["toolTipContent"] = ZaMsg.tt_advancedSearchField ;
		searchFieldInstance[ZaSearch.A_query] = this._query ;
	}else{ //clear the search field is not advacned search
		searchFieldItem["toolTipContent"] = null ;
		searchFieldInstance[ZaSearch.A_query] = "" ;
	}
	searchFieldXform.refresh ();
}

//add the option value into the LDAP query filter
ZaSearchBuilderController.prototype._addFilter = 
function (filter, key, value, op) {
	if (value instanceof String ) {
		value = String(value).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
	}
	var entry = null ;
    var searchListController = ZaApp.getInstance().getSearchListController();
	if (key == ZaSearchOption.A_domainFilter 
		//|| key == ZaSearchOption.A_domainAll
		|| key == ZaSearchOption.A_domainList 
		|| key == ZaSearchOption.A_serverList
		|| key == ZaSearchOption.A_cosFilter
		|| key == ZaSearchOption.A_cosList) {
		//ignored 			
					
	}else if (key == ZaSearchOption.A_objTypeAccount) {
		if (value == "TRUE" && (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])) filter.push(ZaSearch.ACCOUNTS);
	}else if (key == ZaSearchOption.A_objTypeDl) {
		if (value == "TRUE"  && (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]))  filter.push(ZaSearch.DLS);
	}else if (key == ZaSearchOption.A_objTypeAlias ) {
		if (value == "TRUE" && (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]))  filter.push(ZaSearch.ALIASES);
	}else if (key == ZaSearchOption.A_objTypeResource) {
		if (value == "TRUE" && (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) )  filter.push(ZaSearch.RESOURCES);
	/*}else if (key == ZaSearchOption.A_objTypeDomain) {
		if (value == "TRUE")  filter.push(ZaSearch.DOMAINS);*/
        }else if (key == ZaSearchOption.A_objTypeSystemAccount) {
                if (value == "TRUE")  entry = "(" + key + "=" + value + ")" ;
	}else if (key == ZaSearchOption.A_objTypeAccountAdmin) {
		if (value == "TRUE")  entry = "(" + key + "=" + value + ")" ; //no * for the TRUE or FALSE value
	}else if (ZaSearchOption.A_objTypeAccountDomainAdmin && key == ZaSearchOption.A_objTypeAccountDomainAdmin){
		if (value == "TRUE")  entry = "(" + key + "=" + value + ")" ; //no * for the TRUE or FALSE value
	}else if (key == ZaSearchOption.A_domainListChecked) {	
		if (value.size () > 0) {
				entry = ZaSearchBuilderController.getOrFilter4ListArray (
						value.getArray(),
						ZaSearchOption.DOMAIN_ID
						);
		}
	}else if (key == ZaSearchOption.A_serverListChecked) {	
		if (value.size () > 0) {
				entry = ZaSearchBuilderController.getOrFilter4ListArray (
						value.getArray(),
						ZaSearchOption.SERVER_ID
						);
		}
	}else if (key == ZaSearchOption.A_cosListChecked) {
		if (value.size () > 0) {
			entry = ZaSearchBuilderController.getCosFilter4ListArray(value.getArray());
		}
    }else if (key == ZaAccount.A_zimbraLastLogonTimestamp){
		entry = "("	+ key + op + value + ")";
	}else if (key == ZaSearchOption.A_zimbraMailForwardingAddress ){
		entry = "(" + key + "=*" + value + "*)" ;
                entry += "(" + ZaSearchOption.A_zimbraPrefMailForwardingAddress + "=*" + value + "*)" ;
                entry = "(|" + entry + ")";
    } else if (searchListController.searchResultFilter.indexOf(key) >= 0) {
        if(!searchListController._filterObj || !(searchListController._filterObj instanceof Object))
             searchListController._filterObj = {};
        searchListController._filterObj[key] = value;
    } else {
		entry = "(" + key + "=*" + value + "*)" ;
	}
	
	if (entry != null && entry.length > 0) {
		DBG.println (AjxDebug.DBG1, "Add entry " + entry );
		filter.push (entry) ;
	}
	
}

ZaSearchBuilderController.prototype.getSearchTypesFromFilters =
function () {
	var searchTypeFilterArr = this._filterObj[ZaSearchOption.OBJECT_TYPE_ID] ;
	if (this._objTypeOptionViewPosition < 0){ //the objType option is hidden and search all the object types
		return ZaSearchOption.getDefaultObjectTypes();
	}else if (searchTypeFilterArr != null && searchTypeFilterArr.length > 0) {
		return searchTypeFilterArr ;
	}else {
		return [];
	}
}



ZaSearchBuilderController.prototype.getQueryFromFilters =
function () {
	var query = "";
	var i = 0 ; //count the number of non empty valid options.
	
	for (var key in this._filterObj) {
		if (key != ZaSearchOption.OBJECT_TYPE_ID) {
			var filter = this.getOrFilter4SameOptionType (this._filterObj [key], key) ;
			if (filter != null && filter.length > 0) {
				query += filter ;
				i ++ ;	
			}	
		}
	}
	
	if (i > 1) {
		query = "(&" + query + ")" ;
	}
	
	return query ;
	
	/*
	
	var basicOptions = this.getOrFilter4SameOptionType(this._filterObj[ZaSearchOption.BASIC_TYPE_ID]);
	if (basicOptions != null && basicOptions.length > 0) {
		query += basicOptions ;
		i ++ ;	
	}
	
	var objTypeOptions = this.getOrFilter4SameOptionType(this._filterObj[ZaSearchOption.OBJECT_TYPE_ID]);
	if (objTypeOptions != null && objTypeOptions.length > 0) {
		query += objTypeOptions ;
		i ++ ;	
	}
	
	var domainOptions = this.getOrFilter4SameOptionType(this._filterObj[ZaSearchOption.DOMAIN_ID]);
	if (domainOptions != null && domainOptions.length > 0) {
		query += domainOptions ;
		i ++ ;	
	}
	
	var serverOptions = this.getOrFilter4SameOptionType(this._filterObj[ZaSearchOption.SERVER_ID]);
	if (serverOptions != null && serverOptions.length > 0) {
		query += serverOptions ;
		i ++ ;	
	} */
	
	
}

//For the same option types
ZaSearchBuilderController.prototype.getOrFilter4SameOptionType =
function (arr, key) {
	var query = "";
	var numberOfFilters = 0;
	//special cases for the Never Logged In Accounts
	if (key == ZaSearchOption.ADVANCED_ID){ //for the advanced attribute tab
		if (this._includeNeverLoggedInAccts) {
			query += "(!(" + ZaAccount.A_zimbraLastLogonTimestamp + "=*))" ;
			numberOfFilters ++ ;
		}
	}
	
	for (var i=0; i < arr.length; i++) {
		query += this.getAndFilter4EntriesInOneOption (arr[i]);
		numberOfFilters ++ ;
	}
	if (numberOfFilters > 1) {
		query = "(|" + query + ")";	
	}
	DBG.println (AjxDebug.DBG3, "Same Option Type Filter = " + query) ;
	return query ;
}

//for the filter entries in an option type
ZaSearchBuilderController.prototype.getAndFilter4EntriesInOneOption =
function (arr) {
	var query = arr.join("") ;
	if (arr.length > 1) {
		query = "(&" + query + ")";	
	}
	DBG.println (AjxDebug.DBG3, "One Option Type Filter = " + query) ;
	
	return query ;
}

//for domain list and server list
ZaSearchBuilderController.getOrFilter4ListArray=
function (arr, optionId) {
	var query = "";
	for (var i =0; i < arr.length; i ++ ) {
		if (optionId == ZaSearchOption.DOMAIN_ID) {
			query += "(" + ZaAccount.A_mailDeliveryAddress + "=*@" + arr[i] + ")"
				   + "(" + ZaAccount.A_zimbraMailAlias+ "=*@" + arr[i] + ")";
		}else if (optionId == ZaSearchOption.SERVER_ID){
			query += "(" + ZaAccount.A_mailHost + "=" + arr[i] + ")";
		}
	}
	if (arr.length > 1 || (arr.length > 0 && optionId == ZaSearchOption.DOMAIN_ID)) {
		query = "(|" + query + ")";	
	}
	
	return query ;
}

ZaSearchBuilderController.getCosFilter4ListArray = 
function (arr) {
	var query = "";
	var controller = ZaApp.getInstance().getSearchBuilderController();
	var cosids = controller._cosids;

    if(controller._includeObjectWithoutCosId) {
        query += "(!(" + ZaAccount.A_COSId + "=*))";
    }
	if(cosids && cosids.length  > 0) {
		for(var i = 0; i < arr.length && i < cosids.length; i++) {
			query += "(" + ZaAccount.A_COSId  + "=" + cosids[arr[i]] + ")";
		}
	}
        if (cosids.length > 1 || (cosids.length == 1 && controller._includeObjectWithoutCosId)) {
                query = "(|" + query + ")";
        }
	return query;
}


ZaSearchBuilderController.prototype.getQuery =
function () {
	return this._query ;
}

ZaSearchBuilderController.prototype.getAddressTypes =
function () {
	return this._searchTypes ;
}

/**
 * add an option picker in the search options panel based on the optionId which is 
 * defined in the option button view.  And update the corresponding 
 * data structure this._option_views
 */
ZaSearchBuilderController.prototype.addOptionView =
function (optionId) {
	var position = this._option_views.length ;
	var searchPanel = this._searchBuildPanel || this.getSearchBuilderPanel() ;
	var width = ZaSearchOptionView.WIDTH ;
	if (optionId == ZaSearchOption.BASIC_TYPE_ID) {
		width = ZaSearchOptionView.BASIC_OPTION_WIDTH ;
	}else if (optionId == ZaSearchOption.ADVANCED_ID) {
		width = ZaSearchOptionView.ADVANCED_OPTION_WIDTH;
	}else if (optionId == ZaSearchOption.OBJECT_TYPE_ID){
		if (this._objTypeOptionViewPosition >= 0 ) {
			return ; //object type option only display for one time
		}
		else{
			this._objTypeOptionViewPosition = position;
		}
	}else if (optionId == ZaSearchOption.SERVER_ID){
		if (this._serverOptionViewPosition >= 0 ) {
			return ; //object type option only display for one time
		}else{
			this._serverOptionViewPosition = position;
		}
	}
	/*
	else if (optionId == ZaSearchOption.DOMAIN_ID){
			this._numberOfDomainOptions ++ ;
			//if object type view exists and this is the first domain option
			//refresh the object type view
			
			if (this._objTypeOptionViewPosition >= 0 && this._numberOfDomainOptions == 1) {
				this._option_views[this._objTypeOptionViewPosition]._localXForm.refresh();
			}
	}*/
	
	this._option_views.push(new ZaSearchOptionView (
			searchPanel,  optionId, 
			width,  position));
}

ZaSearchBuilderController.prototype.removeAllOptionViews=
function () {
	for (var i= this._option_views.length - 1; i >= 0 ; i --){
		this.removeOptionView (i);	
	}
}

//remove an option picker from the search option panel. And update the corresponding 
//data structure this._option_views
ZaSearchBuilderController.prototype.removeOptionView =
function (position, reposition){
	var option = this._option_views[position] ;
	var y = option.getY();
	option.dispose ();
	this._option_views.splice (position, 1) ;
	
	if (option._optionId == ZaSearchOption.OBJECT_TYPE_ID) {
		this._objTypeOptionViewPosition = -1 ;
		this._searchBuildTBPanel.getButton (ZaOperation.SEARCH_BY_ADDESS_TYPE).setEnabled (true) ;
	}else if (option._optionId == ZaSearchOption.SERVER_ID) {
		this._serverOptionViewPosition = -1 ;
		this._searchBuildTBPanel.getButton (ZaOperation.SEARCH_BY_SERVER).setEnabled (true) ;
	}
	
	
	 /*
	else if (option._optionId == ZaSearchOption.DOMAIN_ID) {
		this._numberOfDomainOptions -- ;
		//if object type view exists and this is the last domain option
		//refresh the object type view
		if (this._objTypeOptionViewPosition >= 0 && this._numberOfDomainOptions == 0) {
			this._option_views[this._objTypeOptionViewPosition]._localXForm.refresh();
		}
	}*/
	
	if (reposition) {
		var len = this._option_views.length ;
		//reset the following element's position and location
		for (var i = position ; i < len; i ++) {
			var x = this._searchBuildPanel.getNextOptionX(i);
			//var w = this._option_views[i-1].getW ();
			this._option_views[i].setLocation(x, y) ;
			this._option_views[i].setPosition (i);
		}
		//reset the query
		this.setQuery();
	}
}

ZaSearchBuilderController.prototype.getOptionViews =
function (){
	return this._option_views ;
}


/* New UI Start */

ZaSearchBuilderController.newHandleOptions =
function (value, event, form){
//	DBG.println(AjxDebug.DBG3, "Handling the options on the search builder toolbar ...");


	var charCode = event.charCode;
	if (charCode == 13 || charCode == 3) {
	   //;
	} else {
		this.setInstanceValue(value);

		//handle the special cases
		//1) domain admin and admin account search option is mutual exclusive.
		var invertValue ;
		if (value == "TRUE") {
			invertValue = "FALSE";
		}

        var ref = this.getRef () ;
        //can't each both be set
		if (invertValue == "FALSE" && ref == ZaSearchOption.A_objTypeAccountDomainAdmin) {
			this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeAccountAdmin) ;
			this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeSystemAccount);
		}else if (invertValue == "FALSE" && ref == ZaSearchOption.A_objTypeAccountAdmin) {
            if (ZaSearchOption.A_objTypeAccountDomainAdmin)
			    this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeAccountDomainAdmin) ;
			this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeSystemAccount);
		} else if (invertValue == "FALSE" && ref == ZaSearchOption.A_objTypeSystemAccount) {
			this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeAccountAdmin) ;
            if (ZaSearchOption.A_objTypeAccountDomainAdmin)
			    this.setInstanceValue (invertValue, ZaSearchOption.A_objTypeAccountDomainAdmin) ;
		}

        //set the advanced login timestamp attributes value
        if ((ref == ZaSearchOption.A_enableAccountLastLoginTime_From || ref == ZaSearchOption.A_enableAccountLastLoginTime_To)
            && ( value == "TRUE" )) {
            var loginTimeRef ;
            if (ref == ZaSearchOption.A_enableAccountLastLoginTime_From) loginTimeRef = ZaSearchOption.A_accountLastLoginTime_From ;
            if (ref == ZaSearchOption.A_enableAccountLastLoginTime_To) loginTimeRef = ZaSearchOption.A_accountLastLoginTime_To ;

            var loginTime = this.getInstanceValue (loginTimeRef);
            if (!loginTime) {
                this.setInstanceValue (new Date(), loginTimeRef) ;
            }
        }

        if ( ref == ZaSearchOption.A2_status_active
            || ref ==ZaSearchOption.A2_status_closed
            || ref ==ZaSearchOption.A2_status_locked
            || ref ==ZaSearchOption.A2_status_lockout
            || ref ==ZaSearchOption.A2_status_pending
            || ref ==ZaSearchOption.A2_status_maintenance) {
            var statusArr = [];
            if (this.getInstanceValue(ZaSearchOption.A2_status_active) == "TRUE") {
                statusArr.push(ZaAccount.ACCOUNT_STATUS_ACTIVE);
            }
            if (this.getInstanceValue(ZaSearchOption.A2_status_closed) == "TRUE") {
                statusArr.push(ZaAccount.ACCOUNT_STATUS_CLOSED);
            }
            if (this.getInstanceValue(ZaSearchOption.A2_status_locked) == "TRUE") {
                statusArr.push(ZaAccount.ACCOUNT_STATUS_LOCKED);
            }
            if (this.getInstanceValue(ZaSearchOption.A2_status_lockout) == "TRUE") {
                statusArr.push(ZaAccount.ACCOUNT_STATUS_LOCKOUT);
            }
            if (this.getInstanceValue(ZaSearchOption.A2_status_pending) == "TRUE") {
                statusArr.push(ZaAccount.ACCOUNT_STATUS_PENDING);
            }
            if (this.getInstanceValue(ZaSearchOption.A2_status_maintenance) == "TRUE") {
                statusArr.push(ZaAccount.ACCOUNT_STATUS_MAINTENANCE);
            }
            this.setInstanceValue(statusArr, ZaSearchOption.A2_status_list);
        }

	}
}

ZaSearchBuilderController.prototype.newHandleSpecialQueries =
function () {
	var optionViews = this.getFilterDialogArray ();
	this._includeNeverLoggedInAccts = false ; //by default
    this._includeObjectWithoutCosId = false;  // by default
	for (var i =0 ; i < optionViews.length; i++) {
		var optionId = optionViews[i]._optionId ;
		var instance = optionViews[i]._localXForm.getInstance () ;
		//handle the special case never logged in accounts
		if (this._includeNeverLoggedInAccts == false //if it is set, then we won't change it again.
				&& instance[ZaSearchOption.A_includeNeverLoginedAccounts]
				&& instance[ZaSearchOption.A_includeNeverLoginedAccounts] == "TRUE" )
		{
			this._includeNeverLoggedInAccts = true ;
		}
		if (this._includeObjectWithoutCosId == false && instance[ZaSearchOption.A2_cosNotSet]
				&& instance[ZaSearchOption.A2_cosNotSet] == "TRUE" )
		{
			this._includeObjectWithoutCosId = true ;
		}
	}
}

/**
 * Set the query value based on the LDAP query language for the advanced search
 * and the query value will be displayed on the search bar also.
 *
 */
ZaSearchBuilderController.prototype.setNewQuery =
function (dialog) {
	this.newHandleSpecialQueries () ;
	var optionViews = this.getFilterDialogArray () ;

	this._query = null ;
	this._searchTypes = null ;
	//_filterObj holds all the options objects
    this._filterObj = null ;


    var optionId = dialog._optionId;
	var instance = dialog._localXForm.getInstance () ;

	var options = instance ["options"] ;
	var filter = [];

    if(optionId == ZaSearchOption.COS_FILTER_ID && this._includeObjectWithoutCosId
                && (!options[ZaSearchOption.A_cosListChecked] || options[ZaSearchOption.A_cosListChecked].size() == 0))
        filter.push("(!(" + ZaAccount.A_COSId + "=*))");

	for (var key in options) {
		var value = options[key] ;
		if (value != null){
			var op = null ; //the operator of the filter
		    if (value instanceof Date) { //the date type options
		        value = ZaUtil.getAdminServerDateTime(value, true) ;
		    }
		    if (key == ZaSearchOption.A_accountLastLoginTime_From) {
			    if (instance[ZaSearchOption.A_enableAccountLastLoginTime_From] == "TRUE") {
				    key = ZaAccount.A_zimbraLastLogonTimestamp ;
				    op = ">=" ;
			    }else{
				    continue ;
			    }
		    }
            if (key == ZaSearchOption.A_accountLastLoginTime_To) {
			    if (instance[ZaSearchOption.A_enableAccountLastLoginTime_To] == "TRUE") {
				    key = ZaAccount.A_zimbraLastLogonTimestamp ;
					    op = "<=" ;
			    }else{
				    continue ;
			    }
		    }
            if ((value.length > 0)
						|| ((value instanceof AjxVector) && (value.size() > 0)))  {
			    //TODO: handle the checkbox TRUE or FALSE value
			    this._newAddFilter (filter, key, value, op) ;
		    }
		}
	}

	var slController = ZaApp.getInstance().getSearchListController();
    if (slController._uiContainer) {
        var query = this.newGetOrFilter4SameOptionType ([filter], optionId) ;
        var unique = false;
        if(optionId == ZaSearchOption.SERVER_FILTER_ID){
            unique = true;
        }
        var params = {
            type:2,
            unique:unique,
            query:query,
            optionalType: optionId,
            displayName: this.getFilterTypeName(dialog._optionId)
        };
        slController._uiContainer.addBubble(params);
    }
}

ZaSearchBuilderController.prototype.getFilterTypeName =
function (filterType) {
      var filterTypeName ;
      switch (filterType) {
        case ZaSearchOption.BASIC_FILTER_ID:
            filterTypeName = ZaMsg.OVP_basicFilter;
        break;
        case ZaSearchOption.STATUS_FILTER_ID:
            filterTypeName = ZaMsg.OVP_statutsFilter;
        break;
        case ZaSearchOption.LASTER_LOGIN_TIME_FILTER_ID:
            filterTypeName = ZaMsg.OVP_lltFilter;
        break;
        case ZaSearchOption.EXT_EMAIL_ADDRESS_FILTER_ID:
            filterTypeName = ZaMsg.OVP_eeaFilter;
        break;
        case ZaSearchOption.COS_FILTER_ID:
            filterTypeName = ZaMsg.OVP_cosFilter;
        break;
        case ZaSearchOption.SERVER_FILTER_ID:
            filterTypeName = ZaMsg.OVP_serverFilter;
        break;
        case ZaSearchOption.DOMAIN_FILTER_ID:
            filterTypeName = ZaMsg.OVP_domainFilter;
        break;
      }
    return filterTypeName;
}


//add the option value into the LDAP query filter
ZaSearchBuilderController.prototype._newAddFilter =
function (filter, key, value, op) {
	if (value instanceof String ) {
		value = String(value).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
	}
	var entry = null ;
    var searchListController = ZaApp.getInstance().getSearchListController();
	if (key == ZaSearchOption.A_domainFilter
		//|| key == ZaSearchOption.A_domainAll
		|| key == ZaSearchOption.A_domainList
		|| key == ZaSearchOption.A_serverList
		|| key == ZaSearchOption.A_cosFilter
		|| key == ZaSearchOption.A_cosList
        || key == ZaSearchOption.A2_status_active
        || key ==ZaSearchOption.A2_status_closed
        || key ==ZaSearchOption.A2_status_locked
        || key ==ZaSearchOption.A2_status_lockout
        || key ==ZaSearchOption.A2_status_pending
        || key ==ZaSearchOption.A2_status_maintenance
        ) {
		//ignored
	}else if (key == ZaSearchOption.A_objTypeSystemAccount) {
                if (value == "TRUE")  entry = "(" + key + "=" + value + ")" ;
	}else if (key == ZaSearchOption.A_objTypeAccountAdmin) {
		if (value == "TRUE")  entry = "(" + key + "=" + value + ")" ; //no * for the TRUE or FALSE value
	}else if (ZaSearchOption.A_objTypeAccountDomainAdmin && key == ZaSearchOption.A_objTypeAccountDomainAdmin){
		if (value == "TRUE")  entry = "(" + key + "=" + value + ")" ; //no * for the TRUE or FALSE value
	}else if (key == ZaSearchOption.A_domainListChecked) {
		if (value.size () > 0) {
				entry = ZaSearchBuilderController.newGetOrFilter4ListArray (
						value.getArray(),
						ZaSearchOption.DOMAIN_FILTER_ID
						);
		}
	}else if (key == ZaSearchOption.A_serverListChecked) {
		if (value.size () > 0) {
				entry = ZaSearchBuilderController.newGetOrFilter4ListArray (
						value.getArray(),
						ZaSearchOption.SERVER_FILTER_ID
						);
		}
	}else if (key == ZaSearchOption.A_cosListChecked) {
		if (value.size () > 0) {
			entry = ZaSearchBuilderController.newGetCosFilter4ListArray(value.getArray());
		}
	}else if (key == ZaSearchOption.A2_status_list) {
		if (value.length > 0) {
			entry = ZaSearchBuilderController.newGetOrFilter4ListArray(value, ZaSearchOption.STATUS_FILTER_ID);
		}
    }else if (key == ZaAccount.A_zimbraLastLogonTimestamp){
		entry = "("	+ key + op + value + ")";
	}else if (key == ZaSearchOption.A_zimbraMailForwardingAddress ){
		entry = "(" + key + "=*" + value + "*)" ;
        entry += "(" + ZaSearchOption.A_zimbraPrefMailForwardingAddress + "=*" + value + "*)" ;
        entry = "(|" + entry + ")";
    } else {
        entry = "(" + key + "=*" + value + "*)" ;
	}

	if (entry != null && entry.length > 0) {
		filter.push (entry) ;
	}

}

ZaSearchBuilderController.prototype.newGetQueryFromFilters =
function () {
	var query = "";
	var i = 0 ; //count the number of non empty valid options.

	for (var key in this._filterObj) {
        var filter = this.newGetOrFilter4SameOptionType (this._filterObj [key], key) ;
        if (filter != null && filter.length > 0) {
            query += filter ;
            i ++ ;
        }
	}

	if (i > 1) {
		query = "(&" + query + ")" ;
	}
	return query ;
}

//For the same option types
ZaSearchBuilderController.prototype.newGetOrFilter4SameOptionType =
function (arr, key) {
	var query = "";
	var numberOfFilters = 0;
	//special cases for the Never Logged In Accounts
	if (key == ZaSearchOption.LASTER_LOGIN_TIME_FILTER_ID){ //for the advanced attribute tab
		if (this._includeNeverLoggedInAccts) {
			query += "(!(" + ZaAccount.A_zimbraLastLogonTimestamp + "=*))" ;
			numberOfFilters ++ ;
		}
	}

	for (var i=0; i < arr.length; i++) {
		query += this.newGetAndFilter4EntriesInOneOption (arr[i]);
		numberOfFilters ++ ;
	}
	if (numberOfFilters > 1) {
		query = "(|" + query + ")";
	}
	return query ;
}

//for the filter entries in an option type
ZaSearchBuilderController.prototype.newGetAndFilter4EntriesInOneOption =
function (arr) {
	var query = arr.join("") ;
	if (arr.length > 1) {
		query = "(&" + query + ")";
	}
	return query ;
}

//for domain list and server list
ZaSearchBuilderController.newGetOrFilter4ListArray=
function (arr, optionId) {
	var query = "";
	for (var i =0; i < arr.length; i ++ ) {
		if (optionId == ZaSearchOption.DOMAIN_FILTER_ID) {
			query += "(" + ZaAccount.A_mailDeliveryAddress + "=*@" + arr[i] + ")"
				   + "(" + ZaAccount.A_zimbraMailAlias+ "=*@" + arr[i] + ")";
		}else if (optionId == ZaSearchOption.SERVER_FILTER_ID){
			query += "(" + ZaAccount.A_mailHost + "=" + arr[i] + ")";
		}else if (optionId == ZaSearchOption.STATUS_FILTER_ID){
			query += "(" + ZaAccount.A_accountStatus + "=" + arr[i] + ")";
		}
	}
	if (arr.length > 1 || (arr.length > 0 && optionId == ZaSearchOption.DOMAIN_FILTER_ID)) {
		query = "(|" + query + ")";
	}

	return query ;
}

ZaSearchBuilderController.newGetCosFilter4ListArray =
function (arr) {
	var query = "";
	var controller = ZaApp.getInstance().getSearchBuilderController();
	var cosids = controller._cosids;

    if(controller._includeObjectWithoutCosId) {
        query += "(!(" + ZaAccount.A_COSId + "=*))";
    }
	if(cosids && cosids.length  > 0) {
		for(var i = 0; i < arr.length && i < cosids.length; i++) {
			query += "(" + ZaAccount.A_COSId  + "=" + cosids[arr[i]] + ")";
		}
	}
        if (cosids.length > 1 || (cosids.length == 1 && controller._includeObjectWithoutCosId)) {
                query = "(|" + query + ")";
        }
	return query;
}


ZaSearchBuilderController.prototype.getFilterTreeItems =
function () {
    return [
        {text: ZaMsg.OVP_basicFilter,    filterType: ZaSearchOption.BASIC_FILTER_ID},
        {text: ZaMsg.OVP_statutsFilter, filterType: ZaSearchOption.STATUS_FILTER_ID},
        {text: ZaMsg.OVP_lltFilter,     filterType: ZaSearchOption.LASTER_LOGIN_TIME_FILTER_ID},
        {text: ZaMsg.OVP_eeaFilter,     filterType: ZaSearchOption.EXT_EMAIL_ADDRESS_FILTER_ID},
        {text: ZaMsg.OVP_cosFilter,     filterType: ZaSearchOption.COS_FILTER_ID},
        {text: ZaMsg.OVP_serverFilter,  filterType: ZaSearchOption.SERVER_FILTER_ID},
        {text: ZaMsg.OVP_domainFilter,  filterType: ZaSearchOption.DOMAIN_FILTER_ID}

    ];
}

ZaSearchBuilderController.searchFilterTreeListener =
function (ev) {
    var filterType = ev.item.getData("filterType");
    var loc = {};
    var treeItemBound = ev.item.getBounds();
    loc.x = treeItemBound.x + treeItemBound.width;
    loc.y = treeItemBound.y;
    var dialog;
    var sbController = ZaApp.getInstance().getSearchBuilderController();
    var skipDefaultValue = false;
    switch (filterType) {
        case ZaSearchOption.BASIC_FILTER_ID:
            dialog = sbController.getFilterDialogByType (ZaSearchOption.BASIC_FILTER_ID);
        break;
        case ZaSearchOption.SERVER_FILTER_ID:
            dialog = sbController.getFilterDialogByType (ZaSearchOption.SERVER_FILTER_ID, true);
            sbController.updateServerFilter(dialog);
            skipDefaultValue = true;
        break;
        case ZaSearchOption.COS_FILTER_ID:
            dialog = sbController.getFilterDialogByType (ZaSearchOption.COS_FILTER_ID);
        break;
        case ZaSearchOption.DOMAIN_FILTER_ID:
            dialog = sbController.getFilterDialogByType (ZaSearchOption.DOMAIN_FILTER_ID);
        break;
        case ZaSearchOption.EXT_EMAIL_ADDRESS_FILTER_ID:
            dialog = sbController.getFilterDialogByType (ZaSearchOption.EXT_EMAIL_ADDRESS_FILTER_ID);
        break;
        case ZaSearchOption.LASTER_LOGIN_TIME_FILTER_ID:
            dialog = sbController.getFilterDialogByType (ZaSearchOption.LASTER_LOGIN_TIME_FILTER_ID);
        break;
        case ZaSearchOption.STATUS_FILTER_ID:
            dialog = sbController.getFilterDialogByType (ZaSearchOption.STATUS_FILTER_ID);
        break;
    }
    if (dialog) {
        ZaSearchBuilderController.currentPopupDialog  = dialog;
        if (!skipDefaultValue)
            dialog._localXForm.setInstance(ZaSearchOption.getDefaultInstance(dialog._optionId));
        dialog.popup(loc);
	    var omem = DwtOutsideMouseEventMgr.INSTANCE;
	    var omemParams = {
		    id:					"ZaOptionView",
		    obj:				dialog,
		    outsideListener:	ZaSearchBuilderController.getOutsideListener()
	    }
	    omem.startListening(omemParams);
    }
}

ZaSearchBuilderController.filterDialogSet = {};
ZaSearchBuilderController.currentPopupDialog = undefined;
ZaSearchBuilderController.outSideListener = undefined;
ZaSearchBuilderController.prototype.getFilterDialogArray = function () {
    var result = [];
    for (var filterType in ZaSearchBuilderController.filterDialogSet) {
        if (ZaSearchBuilderController.filterDialogSet[filterType])
            result.push(ZaSearchBuilderController.filterDialogSet[filterType]);
    }
    return result;
}

ZaSearchBuilderController.getOutsideListener = function () {
    if (!ZaSearchBuilderController.outSideListener) {
        ZaSearchBuilderController.outSideListener = new AjxListener(null, ZaSearchBuilderController.outsideListener);
    }
    return ZaSearchBuilderController.outSideListener;
}

ZaSearchBuilderController.outsideListener = function () {
    if(ZaSearchBuilderController.currentPopupDialog) {
        ZaSearchBuilderController.currentPopupDialog.popdown();
        var omem = DwtOutsideMouseEventMgr.INSTANCE;
        omem.stopListening({id:"ZaOptionView", obj:ZaSearchBuilderController.currentPopupDialog});
        ZaSearchBuilderController.currentPopupDialog = undefined;
    }
}

ZaSearchBuilderController.prototype.getFilterDialogByType =
function (filterType, noCached) {
    if (!ZaSearchBuilderController.filterDialogSet[filterType] || noCached) {
        var w, h;
        if (filterType == ZaSearchOption.SERVER_FILTER_ID) {
            w = ZaSearchOptionView.WIDTH;
            h = 150;
        } else if (filterType == ZaSearchOption.COS_FILTER_ID ||
            filterType == ZaSearchOption.DOMAIN_FILTER_ID) {
            w = ZaSearchOptionView.WIDTH;
            h = 150;
        } else if (filterType == ZaSearchOption.EXT_EMAIL_ADDRESS_FILTER_ID) {
            w = 230;
            h= 40;
        }
        ZaSearchBuilderController.filterDialogSet[filterType] = new ZaSearchOptionDialog(ZaApp.getInstance().getAppCtxt().getShell(), filterType, w, h);
        ZaSearchBuilderController.filterDialogSet[filterType].registerCallback(DwtDialog.OK_BUTTON, this.filterOKListener, this, filterType);
    }
    return ZaSearchBuilderController.filterDialogSet[filterType];
}

ZaSearchBuilderController.prototype.filterOKListener =
function (filterType) {
    var dialog = this.getFilterDialogByType(filterType);
    dialog.popdown();
    dialog._controller.setNewQuery(dialog);
    var omem = DwtOutsideMouseEventMgr.INSTANCE;
    omem.stopListening({id:"ZaOptionView", obj:dialog});
    ZaSearchBuilderController.currentPopupDialog = undefined;
}
