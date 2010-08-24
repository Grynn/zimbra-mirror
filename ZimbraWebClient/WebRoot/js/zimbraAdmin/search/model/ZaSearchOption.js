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
* @class ZaSearchOption
* @contructor ZaSearchOption
* Provides the data model and UI items for the advanced search options
* @author Charles Cao
**/
ZaSearchOption = function() {
}

ZaSearchOption.ID = 100;
ZaSearchOption.BASIC_TYPE_ID = ZaSearchOption.ID ++ ;
ZaSearchOption.OBJECT_TYPE_ID = ZaSearchOption.ID ++ ;
ZaSearchOption.DOMAIN_ID = ZaSearchOption.ID ++ ;
ZaSearchOption.SERVER_ID = ZaSearchOption.ID ++ ;
ZaSearchOption.ADVANCED_ID = ZaSearchOption.ID ++ ;
ZaSearchOption.COS_ID = ZaSearchOption.ID ++ ;

//ZaSearchOption.REMOVE_ID = ZaSearchOption.ID ++ ;

//ZaSearchOption.A_basic_query = ZaSearch.A_query ;
ZaSearchOption.A_basic_uid = ZaAccount.A_uid ;
//ZaSearchOption.A_basic_cn =  "cn" ;
ZaSearchOption.A_basic_sn =  "sn" ;
ZaSearchOption.A_basic_displayName = ZaAccount.A_displayname ;
ZaSearchOption.A_basic_zimbraId = ZaItem.A_zimbraId ;
//ZaSearchOption.A_basic_mail = ZaAccount.A_mail ;
ZaSearchOption.A_basic_status = ZaAccount.A_accountStatus ;

ZaSearchOption.A_objTypeAccount = "option_" + ZaSearch.ACCOUNTS ;
ZaSearchOption.A_objTypeAccountAdmin = ZaAccount.A_zimbraIsAdminAccount ;
ZaSearchOption.A_enableAccountLastLoginTime_From = "enable_" + ZaAccount.A_zimbraLastLogonTimestamp + "_From" ;
ZaSearchOption.A_enableAccountLastLoginTime_To = "enable_" + ZaAccount.A_zimbraLastLogonTimestamp + "_To" ;
ZaSearchOption.A_includeNeverLoginedAccounts = "include_never_login_accounts" ;
ZaSearchOption.A_accountLastLoginTime_From = ZaAccount.A_zimbraLastLogonTimestamp + "_From" ;
ZaSearchOption.A_accountLastLoginTime_To = ZaAccount.A_zimbraLastLogonTimestamp + "_To" ;
ZaSearchOption.A_accountLastLoginTime = ZaAccount.A_zimbraLastLogonTimestamp ;
ZaSearchOption.A_zimbraMailForwardingAddress = ZaAccount.A_zimbraMailForwardingAddress ;
//ZaSearchOption.A_objTypeAccountRegular = "option_" + ZaSearch.ACCOUNTS + "_regular" ;
ZaSearchOption.A_objTypeDl = "option_" + ZaSearch.DLS ;
ZaSearchOption.A_objTypeAlias = "option_" + ZaSearch.ALIASES;
ZaSearchOption.A_objTypeResource = "option_" + ZaSearch.RESOURCES;
//ZaSearchOption.A_objTypeDomain = "option_" + ZaSearch.DOMAINS ;

//ZaSearchOption.A_domainAll = "option_domain_all";
ZaSearchOption.A_domainFilter = "option_domain_filter";
ZaSearchOption.A_domainList = "option_domain_list" ;
ZaSearchOption.A_domainListChecked = "option_domain_list_checked";

//ZaSearchOption.A_serverAll = "option_server_all" ;
//ZaSearchOption.A_serverFilter = "option_server_filter";
ZaSearchOption.A_serverList = "option_server_list" ;
ZaSearchOption.A_serverListChecked = "option_server_list_checked";

// COS setting
ZaSearchOption.A_cosFilter = "option_cos_filter";
ZaSearchOption.A_cosList = "option_cos_list" ;
ZaSearchOption.A_cosListChecked = "option_cos_list_checked";

	
ZaSearchOption.getObjectTypeXModel = 
function (optionId){
	var xmodel = {
		items: []
	}

    var basicItems = [
			//{id: ZaSearchOption.A_basic_query, ref: "options/" + ZaSearchOption.A_basic_query, type: _STRING_},
			{id: ZaSearchOption.A_basic_uid, ref: "options/" + ZaSearchOption.A_basic_uid, type: _STRING_},
			{id: ZaSearchOption.A_objTypeAccountAdmin, ref: "options/" + ZaSearchOption.A_objTypeAccountAdmin, type: _STRING_},
			//{id: ZaSearchOption.A_basic_cn, ref: "options/" + ZaSearchOption.A_basic_cn, type: _STRING_},
			{id: ZaSearchOption.A_basic_sn, ref: "options/" + ZaSearchOption.A_basic_sn, type: _STRING_},
			{id: ZaSearchOption.A_basic_displayName, ref: "options/" + ZaSearchOption.A_basic_displayName, type: _STRING_},
			{id: ZaSearchOption.A_basic_zimbraId, ref: "options/" + ZaSearchOption.A_basic_zimbraId, type: _STRING_},
			//{id: ZaSearchOption.A_basic_mail, ref: "options/" + ZaSearchOption.A_basic_mail, type: _STRING_}
			//{id: ZaSearchOption.A_accountLastLoginTime, ref: "options/" + ZaSearchOption.A_accountLastLoginTime, type: _STRING_},
			{id: ZaSearchOption.A_basic_status, ref: "options/" + ZaSearchOption.A_basic_status, type: _STRING_}
		];
		
	//network build
	if (ZaSearchOption.A_objTypeAccountDomainAdmin) {
		basicItems.push (
			{id: ZaSearchOption.A_objTypeAccountDomainAdmin, ref: "options/" + ZaSearchOption.A_objTypeAccountDomainAdmin, type: _STRING_}
		);
	}
	
	var objTypeItems = [
			{id: ZaSearchOption.A_objTypeAccount, ref: "options/" + ZaSearchOption.A_objTypeAccount, type: _STRING_},
			//{id: ZaSearchOption.A_objTypeAccountRegular, ref: "options/" + ZaSearchOption.A_objTypeAccountRegular, type: _STRING_},
			{id: ZaSearchOption.A_objTypeDl, ref: "options/" + ZaSearchOption.A_objTypeDl, type: _STRING_},
			{id: ZaSearchOption.A_objTypeAlias, ref: "options/" + ZaSearchOption.A_objTypeAlias, type: _STRING_},
			{id: ZaSearchOption.A_objTypeResource, ref: "options/" + ZaSearchOption.A_objTypeResource, type: _STRING_},
			{id: ZaSearchOption.A_objTypeDomain, ref: "options/" + ZaSearchOption.A_objTypeDomain, type: _STRING_}
		
		];
	
	var domainItems = [	
			//{id: ZaSearchOption.A_domainAll, ref: "options/" + ZaSearchOption.A_domainAll, type: _STRING_},
			{id: ZaSearchOption.A_domainFilter, ref: "options/" + ZaSearchOption.A_domainFilter, type: _STRING_},
			{id: ZaSearchOption.A_domainListChecked, ref: "options/" + ZaSearchOption.A_domainListChecked, type:_LIST_},
			{id: ZaSearchOption.A_domainList, ref: "options/" + ZaSearchOption.A_domainList, type:_LIST_}
		];
	
	var serverItems = [
			//{id: ZaSearchOption.A_serverAll, ref: "options/" + ZaSearchOption.A_serverAll, type: _STRING_},
			//{id: ZaSearchOption.A_serverFilter, ref: "options/" + ZaSearchOption.A_serverFilter, type: _STRING_},
			{id: ZaSearchOption.A_serverListChecked, ref: "options/" + ZaSearchOption.A_serverListChecked, type:_LIST_},
			{id: ZaSearchOption.A_serverList, ref: "options/" + ZaSearchOption.A_serverList, type:_LIST_}		
		];
	
	var advancedItems = [
			
			//Should not have the options path since they are only flags and will not be included in the ldap search attrs
			{id: ZaSearchOption.A_enableAccountLastLoginTime_From, ref: ZaSearchOption.A_enableAccountLastLoginTime_From, type: _STRING_ },
			{id: ZaSearchOption.A_enableAccountLastLoginTime_To, ref: ZaSearchOption.A_enableAccountLastLoginTime_To, type: _STRING_ },
			{id: ZaSearchOption.A_includeNeverLoginedAccounts, ref: ZaSearchOption.A_includeNeverLoginedAccounts, type: _STRING_ },
			
			//last login time
			{id: ZaSearchOption.A_accountLastLoginTime_From, ref: "options/" + ZaSearchOption.A_accountLastLoginTime_From, type:_DATETIME_},	
			{id: ZaSearchOption.A_accountLastLoginTime_To, ref: "options/" + ZaSearchOption.A_accountLastLoginTime_To, type:_DATETIME_},
            {id: ZaSearchOption.A_zimbraMailForwardingAddress, ref: "options/" + ZaSearchOption.A_zimbraMailForwardingAddress, type:_STRING_}
	];

        var cosItems = [
                        {id: ZaSearchOption.A_cosFilter, ref: "options/" + ZaSearchOption.A_cosFilter, type: _STRING_},
                        {id: ZaSearchOption.A_cosListChecked, ref: "options/" + ZaSearchOption.A_cosListChecked, type:_LIST_},
                        {id: ZaSearchOption.A_cosList, ref: "options/" + ZaSearchOption.A_cosList, type:_LIST_}
        ];

	if (optionId == ZaSearchOption.OBJECT_TYPE_ID) { 
		xmodel.items = objTypeItems ; 
	}else if (optionId == ZaSearchOption.DOMAIN_ID) {
		xmodel.items = domainItems;
	}else if (optionId == ZaSearchOption.SERVER_ID) {
		xmodel.items = serverItems;
	}else if (optionId == ZaSearchOption.BASIC_TYPE_ID) {
		xmodel.items = basicItems ;
	}else if (optionId == ZaSearchOption.ADVANCED_ID) {
		xmodel.items = advancedItems ;
	}else if (optionId == ZaSearchOption.COS_ID) {
                xmodel.items = cosItems ;
        }
	
	return xmodel ;
}

ZaSearchOption.getObjectTypeXForm = 
function (optionId, height){
	var marginTop = ZaSearchOptionView.HEADER_HEIGHT + 8 ;
    var accountStatusChoices = [
           {value:ZaAccount.ACCOUNT_STATUS_ACTIVE, label:ZaAccount.getAccountStatusMsg(ZaAccount.ACCOUNT_STATUS_ACTIVE)},
           {value:ZaAccount.ACCOUNT_STATUS_CLOSED, label:ZaAccount.getAccountStatusMsg(ZaAccount.ACCOUNT_STATUS_CLOSED)},
           {value:ZaAccount.ACCOUNT_STATUS_LOCKED, label:ZaAccount.getAccountStatusMsg(ZaAccount.ACCOUNT_STATUS_LOCKED)},
           {value:ZaAccount.ACCOUNT_STATUS_LOCKOUT, label:ZaAccount.getAccountStatusMsg(ZaAccount.ACCOUNT_STATUS_LOCKOUT)},
           {value:ZaAccount.ACCOUNT_STATUS_PENDING, label:ZaAccount.getAccountStatusMsg(ZaAccount.ACCOUNT_STATUS_PENDING)},
           {value:ZaAccount.ACCOUNT_STATUS_MAINTENANCE, label:ZaAccount.getAccountStatusMsg(ZaAccount.ACCOUNT_STATUS_MAINTENANCE)}

       ];
	
    var xform = {
			numCols:2, width: 150, cssClass: "ZaSearchOptionOverview",
			cssStyle: "margin-top: " + marginTop + "px", 
			items: []
	}
	
	var basicItems = [
		 { type: _TEXTFIELD_, ref:  ZaSearchOption.A_basic_uid,
			label: ZaMsg.search_option_uid, align: _LEFT_, width: 100, 
			onChange: ZaSearchBuilderController.handleOptions,
		  	toolTipContent: ZaMsg.tt_search_option_uid,
		  	enableDisableChecks:[],visibilityChecks:[]
		 },
		 { type: _TEXTFIELD_, ref:  ZaSearchOption.A_basic_sn,
			label: ZaMsg.search_option_sn, align: _LEFT_, width: 100, 
			onChange: ZaSearchBuilderController.handleOptions,
			enableDisableChecks:[],visibilityChecks:[]
		 },
		 { type: _TEXTFIELD_, ref:  ZaSearchOption.A_basic_displayName,
			label: ZaMsg.search_option_displayName, align: _LEFT_, width: 100, 
			onChange: ZaSearchBuilderController.handleOptions,
			enableDisableChecks:[],visibilityChecks:[]
		 },
		 { type: _TEXTFIELD_, ref:  ZaSearchOption.A_basic_zimbraId,
			label: ZaMsg.search_option_zimbraId, align: _LEFT_, width: 100, 
			onChange: ZaSearchBuilderController.handleOptions,
			enableDisableChecks:[],visibilityChecks:[]
		 },
		 { type:_OSELECT1_, ref:ZaSearchOption.A_basic_status, editable:false, 
		 	msgName:ZaMsg.NAD_AccountStatus,label:ZaMsg.NAD_AccountStatus, 
		 	labelLocation:_LEFT_, choices:accountStatusChoices,
		 	onChange: ZaSearchBuilderController.handleOptions,
		 	enableDisableChecks:[],visibilityChecks:[]
		 },
		 {	type: _GROUP_, name:"special search cases",
		 	 colSpan: "2", numCols:2, width: 150, items: []
		 } /*,
		 { type: _GROUP_, width: 150, numCols: 1, colSpan: "*", items:[
		 		{type:_OUTPUT_, value: "Last Access Time: " },
		 		{ref:ZaSearchOption.A_accountLastLoginTime, type:_DWT_DATETIME_,
		 			label:"", labelLocation:_LEFT_
				}
			]
		 } */
	];
	
	var i = basicItems.length ;
	
	//if (!ZaSettings.isDomainAdmin) {
		var adminOnlyItem =  
			 	{ type: _CHECKBOX_, ref:  ZaSearchOption.A_objTypeAccountAdmin,
					trueValue:"TRUE", falseValue:"FALSE",
					label: ZaMsg.SearchFilter_Accounts_admin, 
					align: _LEFT_, labelLocation:_RIGHT_, 
					onChange: ZaSearchBuilderController.handleOptions,
					bmolsnr:true, enableDisableChecks:[],visibilityChecks:[]
				 };
				 
		basicItems[i-1].items.push (adminOnlyItem) ;
	//}
	
	if (ZaSearchOption.A_objTypeAccountDomainAdmin) {
			var domainAdminObjTypeItem = { 
					type: _CHECKBOX_, ref:  ZaSearchOption.A_objTypeAccountDomainAdmin,
					trueValue:"TRUE", falseValue:"FALSE",
					label: ZaMsg.SearchFilter_Accounts_domainadmin, 
					align: _LEFT_, labelLocation:_RIGHT_, 
					onChange: ZaSearchBuilderController.handleOptions,
					bmolsnr:true,enableDisableChecks:[],visibilityChecks:[], labelWrap:true
				 } ;
			basicItems[i-1].items.push( domainAdminObjTypeItem ) ;
		}
	
	var objTypeItems = [
		{ type: _CHECKBOX_, ref:  ZaSearchOption.A_objTypeAccount,
			trueValue:"TRUE", falseValue:"FALSE",
			label: ZaMsg.SearchFilter_Accounts, 
			align: _LEFT_, labelLocation:_RIGHT_, 
			onChange: ZaSearchBuilderController.handleOptions,
			enableDisableChecks:[],visibilityChecks:["(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])"]
		 },
		 { type: _CHECKBOX_, ref:  ZaSearchOption.A_objTypeDl,
			trueValue:"TRUE", falseValue:"FALSE",
			label: ZaMsg.SearchFilter_DLs, 
			align: _LEFT_, labelLocation:_RIGHT_, 
			onChange: ZaSearchBuilderController.handleOptions,
			enableDisableChecks:[],visibilityChecks:["(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])"]
		 },
		 { type: _CHECKBOX_, ref:  ZaSearchOption.A_objTypeAlias,
			trueValue:"TRUE", falseValue:"FALSE",
			label: ZaMsg.SearchFilter_Aliases, 
			align: _LEFT_, labelLocation:_RIGHT_, 
			onChange: ZaSearchBuilderController.handleOptions,
			enableDisableChecks:[],visibilityChecks:["(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])"]
		 },
		 { type: _CHECKBOX_, ref:  ZaSearchOption.A_objTypeResource,
			trueValue:"TRUE", falseValue:"FALSE",
			label: ZaMsg.SearchFilter_Resources, 
			align: _RIGHT_, labelLocation:_RIGHT_, 
			onChange: ZaSearchBuilderController.handleOptions,
			enableDisableChecks:[],visibilityChecks:["(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])"]
		 }/** Hide the domain search for now,
		 { type: _CHECKBOX_, ref:  ZaSearchOption.A_objTypeDomain,
			trueValue:"TRUE", falseValue:"FALSE",
			label: ZaMsg.SearchFilter_Domains, 
			align: _RIGHT_, labelLocation:_RIGHT_, 
			onChange: ZaSearchBuilderController.handleOptions
		 } **/
	] ;
	
	
	
	var domainItems = [
	/*
		{ type: _CHECKBOX_, ref:  ZaSearchOption.A_domainAll,
			trueValue:"TRUE", falseValue:"FALSE",
			label: ZaMsg.search_option_all_domain, 
			align: _RIGHT_, labelLocation:_RIGHT_, 
			onChange: ZaSearchBuilderController.handleOptions
		 },
		{ type: _SEPARATOR_ , width: 150 },*/
		{ type: _TEXTFIELD_, ref:  ZaSearchOption.A_domainFilter,
			label: ZaMsg.search_option_filter, align: _LEFT_, width: ZaSearchOptionView.WIDTH - 50, 
			inputHelp: ZaMsg.search_option_filter_input_help_domain,
		  	toolTipContent: ZaMsg.tt_domain_search_option_filter,
			onChange: ZaSearchBuilderController.filterDomains,
			enableDisableChecks:[],visibilityChecks:[]
		 },
		 
		 {type: _OUTPUT_, value: ZaMsg.no_domain_found_msg, colSpan: "*",
	 		visibilityChecks:[[XForm.checkInstanceValueEmty,ZaSearchOption.A_domainList]] 
		 },
		 {type: _GROUP_, width: ZaSearchOptionView.WIDTH, colSpan: "*", height: height - 30 - 25 - 5, 
		 	cssStyle: "overflow:auto; position:absolute;margin-top: 5px;",
			visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaSearchOption.A_domainList]],
		 	items :[
				 
				 {type: _DWT_LIST_, ref: ZaSearchOption.A_domainList,  width: ZaSearchOptionView.WIDTH - 2, height: height - 30 - 25,  
					 forceUpdate: true, widgetClass: ZaOptionList, 
					 multiselect: true, preserveSelection: true, 					 	
					 onSelection: ZaSearchBuilderController.filterSelectionListener
				 }
		 	]
		 }
	];
		
	var serverItems = [

		 {type: _GROUP_, width: ZaSearchOptionView.WIDTH, colSpan: "*", height: height - 30, 
		 	cssStyle: "overflow:auto; position:absolute;",
		 	items :[
				 {type: _DWT_LIST_, ref: ZaSearchOption.A_serverList,  width: ZaSearchOptionView.WIDTH - 2, height: height - 30,  	
					 forceUpdate: true, widgetClass: ZaOptionList, 
					 multiselect: true, preserveSelection: true, 
					 onSelection: ZaSearchBuilderController.filterSelectionListener
				 }
		 	]
		 }
	];

	//COS

        var cosItems = [

                { type: _TEXTFIELD_, ref:  ZaSearchOption.A_cosFilter,
                        label: ZaMsg.search_option_filter, align: _LEFT_, width: ZaSearchOptionView.WIDTH - 50,
                        inputHelp: ZaMsg.search_option_filter_input_help_cos,
                        toolTipContent: ZaMsg.tt_cos_search_option_filter,
                        onChange: ZaSearchBuilderController.filterCOSES,
                        enableDisableChecks:[],visibilityChecks:[]
                 },

                 {type: _OUTPUT_, value: ZaMsg.no_cos_found_msg, colSpan: "*",
                        visibilityChecks:[[XForm.checkInstanceValueEmty,ZaSearchOption.A_cosList]]
                 },
                 {type: _GROUP_, width: ZaSearchOptionView.WIDTH, colSpan: "*", height: height - 30 - 25 - 5,
                        cssStyle: "overflow:auto; position:absolute;margin-top: 5px;",
                        visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaSearchOption.A_cosList]],
                        items :[

                                 {type: _DWT_LIST_, ref: ZaSearchOption.A_cosList,  width: ZaSearchOptionView.WIDTH - 2, height: height - 30 - 25,
                                         forceUpdate: true, widgetClass: ZaOptionList,
                                         multiselect: true, preserveSelection: true,
                                         onSelection: ZaSearchBuilderController.filterSelectionListener
                                 }
                        ]
                 }
        ];
	
	var advancedItems = [
		{ type: _GROUP_,  numCols: 2, items:[
		 		{type:_OUTPUT_, colSpan: "*", cssClass: "ZaSearchOptionViewSubHeader", 
		 			value: ZaMsg.search_option_lastAccessTime, width: 280  },
		 		{type: _GROUP_, numCols: 5, colSpan: "*", 
		 			items: [
		 				{type: _CELL_SPACER_, width: 40 },
		 				{type: _CHECKBOX_, ref: ZaSearchOption.A_enableAccountLastLoginTime_From, 
			 				label: ZaMsg.enable_search_option_label_from,
			 				trueValue:"TRUE", falseValue:"FALSE",
			 				align: _LEFT_, labelLocation:_RIGHT_, 
							onChange: ZaSearchBuilderController.handleOptions,
							enableDisableChecks:[],visibilityChecks:[] },
		 				{type: _CHECKBOX_, ref: ZaSearchOption.A_enableAccountLastLoginTime_To, 
			 				label: ZaMsg.enable_search_option_label_to ,
			 				trueValue:"TRUE", falseValue:"FALSE",
			 				align: _LEFT_, labelLocation:_RIGHT_, 
							onChange: ZaSearchBuilderController.handleOptions,
							enableDisableChecks:[],visibilityChecks:[] }
		 			]
		 		},
		 		{type: _GROUP_, numCols: 3, colSpan: "*", 
		 			items: [
		 				{type: _CELL_SPACER_, width: 40 },
		 				{type: _CHECKBOX_, ref: ZaSearchOption.A_includeNeverLoginedAccounts,
			 				label: ZaMsg.includeNeverLoginedAccounts,
			 				trueValue:"TRUE", falseValue:"FALSE",
			 				align: _LEFT_, labelLocation:_RIGHT_, 
							onChange: ZaSearchBuilderController.handleOptions,
							enableDisableChecks:[],visibilityChecks:[], labelWrap: true }
		 			]
		 		},
		 		{type: _GROUP_, colSpan: "*", numCols: 2, colSize: ["60px", "auto"],
		 			items: [
			 		{ref:ZaSearchOption.A_accountLastLoginTime_From, colSpan: "*", type:_DWT_DATETIME_,
			 			onChange: ZaSearchBuilderController.handleOptions,
			 			visibilityChecks:[[XForm.checkInstanceValue,ZaSearchOption.A_enableAccountLastLoginTime_From,"TRUE"]],
			 			bmolsnr:true,firstDayOfWeek:ZaZimbraAdmin.FIRST_DAY_OF_WEEK,
                        visibilityChangeEventSources: [ZaSearchOption.A_enableAccountLastLoginTime_From] ,
			 			label:ZaMsg.search_option_label_from, labelLocation:_LEFT_
					},
					{ref:ZaSearchOption.A_accountLastLoginTime_To, colSpan: "*", type:_DWT_DATETIME_,
			 			onChange: ZaSearchBuilderController.handleOptions,	
			 			visibilityChecks:[[XForm.checkInstanceValue,ZaSearchOption.A_enableAccountLastLoginTime_To,"TRUE"]],
                        visibilityChangeEventSources: [ZaSearchOption.A_enableAccountLastLoginTime_To] ,			 				 				
			 			bmolsnr:true,firstDayOfWeek:ZaZimbraAdmin.FIRST_DAY_OF_WEEK,
                        label:ZaMsg.search_option_label_to, labelLocation:_LEFT_
					}]
		 		},
				{type:_SPACER_}, //used to avoid the missing border of the calendar
                {
                    type: _TEXTFIELD_, ref:  ZaSearchOption.A_zimbraMailForwardingAddress,
                    label: ZaMsg.LB_External_mail, align: _LEFT_, width: 150, 
                    onChange: ZaSearchBuilderController.handleOptions,
                    toolTipContent: ZaMsg.LB_External_mail_tt,
                    enableDisableChecks:[],visibilityChecks:[]
                }
			]
		 }
	]
	
	if (optionId == ZaSearchOption.OBJECT_TYPE_ID) { 
		xform.items = objTypeItems ; 
	}else if (optionId == ZaSearchOption.DOMAIN_ID) {
		xform.items = domainItems;
	}else if (optionId == ZaSearchOption.SERVER_ID) {
		xform.items = serverItems;
	}else if (optionId == ZaSearchOption.BASIC_TYPE_ID) {
		xform.items = basicItems ;
		xform.width = ZaSearchOptionView.BASIC_OPTION_WIDTH ;
	}else if (optionId == ZaSearchOption.ADVANCED_ID) {
		xform.items = advancedItems ;
		xform.width = ZaSearchOptionView.ADVANCED_OPTION_WIDTH ;
	}else if (optionId == ZaSearchOption.COS_ID) {
		xform.items = cosItems;
	}
	
	return xform ;
}

ZaSearchOption.getDefaultInstance =
function (optionId) {
	var optionInstance = {} ;
	optionInstance["options"] = {} ;
	
	if (optionId == ZaSearchOption.OBJECT_TYPE_ID) { 
		optionInstance["options"][ZaSearchOption.A_objTypeAccount] = "TRUE" ;
		optionInstance["options"][ZaSearchOption.A_objTypeAlias] = "TRUE" ;
		optionInstance["options"][ZaSearchOption.A_objTypeDl] = "TRUE" ;
		optionInstance["options"][ZaSearchOption.A_objTypeResource] = "TRUE" ;
		//optionInstance["options"][ZaSearchOption.A_accountLastLoginTime_From] = 
		//optionInstance["options"][ZaSearchOption.A_objTypeDomain] = "FALSE" ;
	}else if (optionId == ZaSearchOption.DOMAIN_ID) {
		//optionInstance["options"][ZaSearchOption.A_domainAll] = "TRUE" ;
	}else if (optionId == ZaSearchOption.SERVER_ID) {
		//optionInstance["options"][ZaSearchOption.A_serverAll] = "TRUE" ;
	}else if (optionId == ZaSearchOption.BASIC_TYPE_ID) {
		//no default value
	}else if (optionId == ZaSearchOption.COS_ID) {
		// no default value
	}else if (optionId == ZaSearchOption.ADVANCED_ID) {
		optionInstance[ZaSearchOption.A_enableAccountLastLoginTime_From] = "FALSE" ;
		optionInstance[ZaSearchOption.A_enableAccountLastLoginTime_To] = "FALSE" ;
		optionInstance[ZaSearchOption.A_includeNeverLoginedAccounts] = "FALSE" ;
	}
	
	return optionInstance ;
}

ZaSearchOption.getDefaultObjectTypes =
function () {
	var searchTypes = [];
	searchTypes[0]= [ZaSearch.ACCOUNTS, ZaSearch.ALIASES, ZaSearch.DLS,  ZaSearch.RESOURCES]
	return searchTypes ;
}

/////////////////////////////////////////////////////////////////////////////////////
//the list view for the domain and server filter
ZaOptionList = function(parent,className) {
	//DwtListView.call(this, parent, null, Dwt.STATIC_STYLE);
	DwtListView.call(this, parent, null, Dwt.ABSOLUTE_STYLE);
}

ZaOptionList.prototype = new DwtListView;
ZaOptionList.prototype.constructor = ZaOptionList;

ZaOptionList.prototype.toString = 
function() {
	return "ZaOptionList";
}

ZaOptionList.prototype._createItemHtml =
function(item) {
	var html = new Array(10);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'><tr><td width=20>"
	html[idx++] = "<input type=checkbox value='" + item + "' /></td>" ;
	html[idx++] = "<td>"+ item + "</td></tr></table>";
	
	div.innerHTML = html.join("");
	return div;
}

