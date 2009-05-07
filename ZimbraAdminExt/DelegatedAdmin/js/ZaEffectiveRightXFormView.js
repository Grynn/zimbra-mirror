ZaEffectiveRightsXFormView = function(parent, entry) {
	ZaTabView.call(this, parent,  "ZaEffectiveRightsXFormView");
	this.TAB_INDEX = 0;

    //decide the number of dynamic tabs in the xformModifier based on the entry
    var targets = entry.targets ;
    /*
    this.TARGETS_TAB_NO = {} ;
    for (var i = 0 ; i < targets.length; i ++) {
        var no_tabs = this.TARGETS_TAB_NO [targets[i].type] = {} ; 
        no_tabs.entries = (targets[i].entries ? targets[i].entries.length : 0) ;
        no_tabs.all = (targets[i].all ? targets[i].all.length : 0) ;
        no_tabs.inDomains = (targets[i].inDomains ? targets[i].inDomains.length : 0) ; 
    } */

	this.initForm(ZaEffectiveRights.myXModel,this.getMyXForm(targets));
}

ZaEffectiveRightsXFormView.prototype = new ZaTabView();
ZaEffectiveRightsXFormView.prototype.constructor = ZaEffectiveRightsXFormView;
ZaTabView.XFormModifiers["ZaEffectiveRightsXFormView"] = new Array();
//ZaTabView.ObjectModifiers["ZaEffectiveRightsXFormView"] = [] ;

ZaEffectiveRightsXFormView.prototype.setObject =
function(entry) {                              

    this._containedObject = {};

    this._containedObject = entry ;

    this._localXForm.setInstance(this._containedObject);
	this.updateTab();

}

ZaEffectiveRightsXFormView.getEffectiveRightsHTML = function (itemValue) {
    var html = [] ;
    if (itemValue != null) {
        for (var key in itemValue) {
            if (itemValue[key] != null) {
                for (var i = 0 ; i < itemValue[key].length; i ++) {
                    if (key == "right") {
                        html.push ("<br />" + key + ": "  + itemValue[key][i]["n"]) ;
                    } else {
                        var attrs = itemValue[key][0] ;
                        if (attrs.a) {
                            for (var j =0; j < attrs.a.length; j ++) {
                                html.push ("<br />" + key + ": " +  attrs.a[j].n) ;
                            }
                        }
                        if (attrs.all == true) {
                            html.push ("<br />" + key + ": all the attributes" ) ;
                        }
                    }
                }

            }
        }
    }

    return html.join () ;
    
}

//ZaEffectiveRightsXFormView.myXFormModifier = function(xFormObject) {
ZaEffectiveRightsXFormView.prototype.getMyXForm = function(targets) {
    var xFormObject = {} ;
    var headerItems = [];
    headerItems.push({type:_OUTPUT_, ref:ZaEffectiveRights.A_grantee + "/name", labelLocation:_LEFT_,label:com_zimbra_delegatedadmin.Col_grantee_name + ": ",visibilityChecks:[ZaItem.hasReadPermission]});
    headerItems.push({type:_OUTPUT_, ref:ZaEffectiveRights.A_grantee + "/id", labelLocation:_LEFT_,label:com_zimbra_delegatedadmin.Col_grantee_id + ": ",visibilityChecks:[ZaItem.hasReadPermission]});
    /*
    var _tabAccount = ++this.TAB_INDEX;
    var _tabDL = ++this.TAB_INDEX;
    var _tabCalresource = ++this.TAB_INDEX;
    var _tabCos = ++this.TAB_INDEX;
    var _tabDomain = ++this.TAB_INDEX;
    var _tabServer = ++this.TAB_INDEX;
    var _tabZimlet = ++this.TAB_INDEX;
    var _tabConfig = ++this.TAB_INDEX;
    var _tabGlobal = ++this.TAB_INDEX;
    //subtabs
    var _tabAll = ++this.TAB_INDEX;
    
    var _tabInDomains = ++this.TAB_INDEX;    
    var _tabEntries = ++this.TAB_INDEX;
    
    this.tabChoices = new Array();
    this.tabChoices.push ({value: _tabAccount, label:ZaMsg.OVP_accounts}) ;
    this.tabChoices.push ({value: _tabDL, label:ZaMsg.OVP_distributionLists}) ;
    this.tabChoices.push ({value: _tabCalresource, label:ZaMsg.OVP_resources}) ;
    this.tabChoices.push ({value: _tabCos, label:ZaMsg.OVP_cos}) ;
    this.tabChoices.push ({value: _tabDomain, label:ZaMsg.OVP_domains}) ;
    this.tabChoices.push ({value: _tabServer, label:ZaMsg.OVP_servers}) ;
    this.tabChoices.push ({value: _tabZimlet, label:ZaMsg.OVP_zimlets}) ;
    this.tabChoices.push ({value: _tabConfig, label:ZaMsg.OVP_global}) ;
    this.tabChoices.push ({value: _tabGlobal, label:com_zimbra_delegatedadmin.OVP_global_grants}) ;
    
    var cases = [];

    var allTab = {value:_tabAll, label: com_zimbra_delegatedadmin.Tab_All} ;
    var inDomainsTab = {value:_tabInDomains, label: com_zimbra_delegatedadmin.Tab_InDomains};
    var byAccountsTab = {value:_tabEntries, label: com_zimbra_delegatedadmin.Tab_ByAccounts};

    var allTabPage = { type: _CASE_, caseVarRef:ZaEffectiveRights.A2_account_currentTab, caseKey: _tabAll, numCols:1,
                        items:[
                            {type:_OUTPUT_, ref: ZaEffectiveRights.A_targets , getDisplayValue:  ZaEffectiveRightsXFormView.getEffectiveRightsHTML
                            }
                        ]
                    };

    var inDomainsPage = { type: _CASE_, caseVarRef:ZaEffectiveRights.A2_account_currentTab, caseKey: _tabInDomains, numCols:1,
                        items:[
                            {type:_OUTPUT_, ref: ZaEffectiveRights.A_targets,
                                getDisplayValue:  ZaEffectiveRightsXFormView.getEffectiveRightsHTML }
                        ]
                    };

    var byAccountsPage = { type: _CASE_, caseVarRef:ZaEffectiveRights.A2_account_currentTab, caseKey: _tabEntries, numCols:1,
                        items:[
                            {type:_OUTPUT_, ref: ZaEffectiveRights.A_targets ,
                                getDisplayValue:  ZaEffectiveRightsXFormView.getEffectiveRightsHTML }
                        ]
                    };

    var caseAccount =  {type:_ZATABCASE_,caseKey:_tabAccount, numCols:1};
    var caseAccountItems = [
        {type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header",
				items: [
                    {type:_OUTPUT_, value: AjxMessageFormat.format (com_zimbra_delegatedadmin.er_of_target, [ZaItem.ACCOUNT]) }
				],
				cssStyle:"padding-left: 10px; padding-top:5px; padding-bottom:5px"
		},    
        {type:_TAB_BAR_, ref: ZaEffectiveRights.A2_account_currentTab,
            choices: [allTab, inDomainsTab, byAccountsTab],cssClass:"ZaTabBar", id:"account_tabbar"},
        {type: _SWITCH_, align: _LEFT_, valign: _TOP_,
            items: [allTabPage, inDomainsPage, byAccountsPage] }
    ];

    caseAccount.items = caseAccountItems ;    
    cases.push (caseAccount) ;
     */
    var topTabCases = [] ;
    var topTabChoices = [];
    var topTabIndex = [] ;
    var topTabLabel = [] ;
    
   for (var i = 0; i< targets.length; i ++) { //targets: account, dl ... etc.
       var type = targets[i].type ;

       if (type == ZaItem.ACCOUNT) {
            topTabLabel[i] = ZaMsg.OVP_accounts ;
        }else if (type == ZaItem.DL) {
            topTabLabel[i] = ZaMsg.OVP_distributionLists ;
        }else if (type == ZaItem.RESOURCE) {
            topTabLabel[i] = ZaMsg.OVP_resources ;
        }else if (type == ZaItem.DOMAIN) {
            topTabLabel[i] = ZaMsg.OVP_domains ;
        }else if (type == ZaItem.COS) {
            topTabLabel[i] = ZaMsg.OVP_cos ;
        }else if (type == ZaItem.SERVER) {
            topTabLabel[i] = ZaMsg.OVP_servers ;
        }else if (type == ZaItem.ZIMLET) {
            topTabLabel[i] = ZaMsg.OVP_zimlets ;
        }else if (type == ZaItem.GLOBAL_CONFIG) {
            topTabLabel[i] = ZaMsg.OVP_global ;
        }else if (type == ZaItem.GLOBAL_GRANT) {
            topTabLabel[i] = com_zimbra_delegatedadmin.OVP_global_grants ;
        }else {
            continue ;
        }

        topTabIndex[i] = ++this.TAB_INDEX ;
       topTabChoices.push ({value: topTabIndex[i], label:topTabLabel[i]}) ;

       topTabCases[i]  = {
            type:_ZATABCASE_,caseKey: topTabIndex[i], numCols:1,
            items:[
                {type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header",
                        items: [
                            {type:_OUTPUT_, value: AjxMessageFormat.format (com_zimbra_delegatedadmin.er_of_target, [type]) }
                        ],
                        cssStyle:"padding-left: 10px; padding-top:5px; padding-bottom:5px"
                },
                    //must use different tab_bar ref for a different row of tabs
                {type:_TAB_BAR_, ref:ZaEffectiveRights.A2_2currentTab + i, choices: [],cssClass:"ZaTabBar", id:"_tabbar"},
                {type: _SWITCH_, align: _LEFT_, valign: _TOP_, items: [] }
            ]
       };


         var secondTabCases = topTabCases[i].items[2].items ;
         var secondTabChoices = topTabCases[i].items[1].choices ;


       for (var k in targets[i]) { // type, inDomains, entires, all

           var targetRights = targets[i][k] ;
           var currentIndex = ++this.TAB_INDEX ;
           
           if (k == "all") {
                secondTabChoices.push({value: currentIndex, label: com_zimbra_delegatedadmin.Tab_All})  ;
            } else if ( k == "inDomains" ) {
                secondTabChoices.push({value: currentIndex, label: com_zimbra_delegatedadmin.Tab_InDomains})  ;
            } else if ( k == "entries" ) {
                secondTabChoices.push({value: currentIndex, label: com_zimbra_delegatedadmin.Tab_ByAccounts})  ;
            } else  {
                continue ;                
            }

           var tempSecondCase = {
                type:_ZATABCASE_,caseVarRef: ZaEffectiveRights.A2_2currentTab + i  ,caseKey: currentIndex, numCols:1, items:[]
           } ;

           if (targetRights.length > 1) {
               //needs third row of tabs
                tempSecondCase.items = [
                    {type:_TAB_BAR_, ref:ZaEffectiveRights.A2_3currentTab + i + k , choices: [],cssClass:"ZaTabBar", id:"_tabbar"},
                    {type: _SWITCH_, align: _LEFT_, valign: _TOP_, items: [] }
                ] ;

               var thirdTabChoices = tempSecondCase.items[0].choices;
               var thirdTabCases = tempSecondCase.items[1].items;
           }
           secondTabCases.push(tempSecondCase) ;

           //construct the tempSecondCase below

           for (var m = 0; m < targetRights.length; m ++) {
               var right ;
               var title = null ;
               if (k == "all") {
                   right = targetRights[m] ;
                } else if (k == "inDomains") {
                   right = targetRights[m].rights [0] ;
                   title = targetRights[m].domain ; 
               }  else if ( k == "entries")  {
                   right = targetRights[m].rights [0] ;
                   title = targetRights[m].entry ;
               }

               var titleLabel = "";
               var strArr = [] ;
               if (title != null) {
                   for (var n =0 ; n < title.length; n ++) {
                        strArr.push (title[n].name) ;
                   }
                   titleLabel = strArr.join(",") ;
               }

               var html = ZaEffectiveRightsXFormView.getEffectiveRightsHTML (right) ;

               if (targetRights.length == 1) { //no subtabs
                   tempSecondCase.items.push ( {  type: _OUTPUT_, value: titleLabel } ) ;
                   tempSecondCase.items.push ( {  type: _OUTPUT_, value: html}) ;
               }else{    //need third subtabs
                   var currentIndex = ++this.TAB_INDEX  ;
                   thirdTabChoices.push({value: currentIndex, label: titleLabel}) ;
                   thirdTabCases.push (
                       {    type:_ZATABCASE_,caseVarRef: ZaEffectiveRights.A2_3currentTab + i + k,caseKey: currentIndex, numCols:1,
                            items:[
                                {  type: _OUTPUT_, value: html}
                            ]
                       });
               }
           }
        }

    }
    
    xFormObject.tableCssStyle="width:100%;";
	xFormObject.items = [
			{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header",
				items: [
					{type:_GROUP_,	numCols:4,colSizes:["90px","350px","100px","*"],items:headerItems}
				],
				cssStyle:"padding-top:5px; padding-bottom:5px"
			},
			{type:_TAB_BAR_,  ref:ZaModel.currentTab,choices:topTabChoices,cssClass:"ZaTabBar", id:"xform_tabbar"},
		    {type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:topTabCases}
	];

    return xFormObject ;
}

//ZaTabView.XFormModifiers["ZaEffectiveRightsXFormView"].push(ZaEffectiveRightsXFormView.myXFormModifier);

ZaEffectiveRightsXFormView.prototype.getTabToolTip =
function () {
	if (this._containedObject && this._containedObject.grantee
            && this._containedObject.grantee.name ) {
		return	AjxMessageFormat.format(com_zimbra_delegatedadmin.tt_tab_view_effective_rights,  [this._containedObject.grantee.name]) ;
	}else{
		return "" ;
	}
}

ZaEffectiveRightsXFormView.prototype.getTabIcon =
function () {
	return "RightObject" ;
}

ZaEffectiveRightsXFormView.prototype.getTabTitle =
function () {
	if (this._containedObject && this._containedObject.grantee
            && this._containedObject.grantee.name) {
		return this._containedObject.grantee.name ;
	}else{
		return "" ;
	}
}


//--------------------------------------------------------------------------------------------------------
//



