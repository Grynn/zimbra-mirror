ZaEffectiveRightsXFormView = function(parent, entry) {
    ZaTabView.call(this, parent, "ZaEffectiveRightsXFormView");
    //	this.TAB_INDEX = 0;

    //decide the number of dynamic tabs in the xformModifier based on the entry
    var targets = entry.targets ;
    var grantee = entry.grantee.name ;
    /*
     this.TARGETS_TAB_NO = {} ;
     for (var i = 0 ; i < targets.length; i ++) {
     var no_tabs = this.TARGETS_TAB_NO [targets[i].type] = {} ;
     no_tabs.entries = (targets[i].entries ? targets[i].entries.length : 0) ;
     no_tabs.all = (targets[i].all ? targets[i].all.length : 0) ;
     no_tabs.inDomains = (targets[i].inDomains ? targets[i].inDomains.length : 0) ;
     } */

    this.initForm(ZaEffectiveRights.myXModel, this.getMyXForm(targets, grantee));
}

ZaEffectiveRightsXFormView.prototype = new ZaTabView();
ZaEffectiveRightsXFormView.prototype.constructor = ZaEffectiveRightsXFormView;
ZaTabView.XFormModifiers["ZaEffectiveRightsXFormView"] = new Array();
//ZaTabView.ObjectModifiers["ZaEffectiveRightsXFormView"] = [] ;

ZaEffectiveRightsXFormView.prototype.setObject =
function(entry) {

    this._containedObject = {};

    this._containedObject = entry;
    this._containedObject.id = entry.grantee.id ;

    this._localXForm.setInstance(this._containedObject);
    this.updateTab();

}

ZaEffectiveRightsXFormView.getEffectiveRightsHTML =
function (itemValue, grantee, targets, type, isByDomain) {
    var targetsLabel = ZaEffectiveRights.getTargetLabel(type) ;
    
    if (targets == "all") {
        if ( type != ZaItem.GLOBAL_CONFIG || type != ZaItem.GLOBAL_GRANT ) {
            targetsLabel = AjxMessageFormat.format (com_zimbra_delegatedadmin.lb_all, [targetsLabel]) ;    
        }
    } else {
        if (isByDomain) {
            targetsLabel = AjxMessageFormat.format (
                    com_zimbra_delegatedadmin.lb_targets_by_domain, [targetsLabel, ZaUtil.join(targets, "name", ",")])
        } else {
            targetsLabel += " - " + ZaUtil.join(targets, "name", ", ")  ;
        }
    }
    
    var getAttrs = [];
    var setAttrs = [];
    var rights = [];
    
    if (itemValue != null) {
        for (var key in itemValue) {
            if (itemValue[key] != null) {
                var attrsObj ;
                if (key == "getAttrs") {
                    attrsObj = getAttrs ;
                }else if (key == "setAttrs") {
                    attrsObj = setAttrs ;
                }
                for (var i = 0; i < itemValue[key].length; i ++) {
                    if (key == "right") {
                        rights.push (itemValue[key][i]["n"]);
                    } else {
                        var attrs = itemValue[key][0] ;
                        if (attrs.a) {
                            for (var j = 0; j < attrs.a.length; j ++) {
                                attrsObj.push (attrs.a[j].n);
                            }
                        }
                        if (attrs.all == true) {
                            attrsObj.push(com_zimbra_delegatedadmin.all_attributes);
                        }
                    }
                }
            }
        }
    }

    var html = [] ;
    html.push ("<table><colgroup><col width=150/><col width=500/></colgroup><tbody>") ;
    html.push("<tr><td style='font-weight: bold; vertical-align: top ;'>" +  com_zimbra_delegatedadmin.lb_grantee + "</td><td style='white-space: wrap; background-color: #ACC0DD !important;'>" + grantee + "</td></tr>") ;
    html.push("<tr><td style='font-weight: bold; vertical-align: top ;'>" +  com_zimbra_delegatedadmin.lb_targets + "</td><td style='background-color: #ACC0DD !important;'>" + targetsLabel + "</td></tr>") ;

    if (rights.length > 0)
        html.push("<tr><td style='font-weight: bold; vertical-align: top ;'>" +  com_zimbra_delegatedadmin.lb_rights + "</td><td style='background-color: #ACC0DD !important;'>" + rights.join( ", ") + "</td></tr>") ;

    if (getAttrs.length >0) {
        html.push("<tr><td style='font-weight: bold; vertical-align: top ;'>" +  com_zimbra_delegatedadmin.lb_readable_attrs + "</td><td style='background-color: #ACC0DD !important;'>" + getAttrs.join(", ") + "</td></tr>") ;
    }
    if (setAttrs.length > 0)
        html.push("<tr><td style='font-weight: bold; vertical-align: top ;'>" +  com_zimbra_delegatedadmin.lb_modifiable_attrs + "</td><td style='background-color: #ACC0DD !important;'>" + setAttrs.join(", ") + "</td></tr>") ;

    html.push ("</tbody></table>") ;
    return html.join("");

}

//ZaEffectiveRightsXFormView.myXFormModifier = function(xFormObject) {
ZaEffectiveRightsXFormView.prototype.getMyXForm = function(targets, grantee) {
    var xFormObject = {} ;
    var headerItems = [];
    headerItems.push({type:_OUTPUT_, ref:ZaEffectiveRights.A_grantee + "/name", labelLocation:_LEFT_,label:com_zimbra_delegatedadmin.Label_grantee_name,visibilityChecks:[ZaItem.hasReadPermission]});
    headerItems.push({type:_OUTPUT_, ref:ZaEffectiveRights.A_grantee + "/id", labelLocation:_LEFT_,label:com_zimbra_delegatedadmin.Label_grantee_id,visibilityChecks:[ZaItem.hasReadPermission]});
    var topTabCases = [] ;
    var topTabChoices = [];
    var topTabIndex = 0 ;
    var topTabLabel = [] ;


    for (var i = 0; i < targets.length; i ++) { //targets: account, dl ... etc.
        var type = targets[i].type ;

        topTabLabel[i] = ZaEffectiveRights.getTargetLabel (type) ;
        if (! topTabLabel[i]) {
            continue;
        }

        topTabIndex ++;
        topTabChoices.push({value: topTabIndex, label:topTabLabel[i]});
        var secondTabCaseRef = ZaModel.currentTab + "_" + topTabIndex ;

        topTabCases[i] = {
            type:_ZATABCASE_,caseKey: topTabIndex, numCols:1,
            overflow: "hidden", tabLevel : 1, headerLevel: 1,
            items:[
                {
                    type:_GROUP_,
                    cssClass:"ZmSelectedHeaderBg",
                    colSpan: "*",
                    id:"xform_header",
                    items: [
                        {
                            type:_OUTPUT_,
                            value: AjxMessageFormat.format(
                                    com_zimbra_delegatedadmin.er_of_target, [grantee, type])
                        }
                    ],
                    cssStyle:"padding-left: 10px; padding-top:5px; padding-bottom:5px"
                },
                //must use different tab_bar ref for a different row of tabs
                {
                    type:_TAB_BAR_,
                    ref:secondTabCaseRef,
                    choices: [],
                    onChange: ZaEffectiveRights.changeTab,
                    cssClass:"ZaTabBar",
                    id:"_tabbar"
                },
                {
                    type: _SWITCH_,
                    align: _LEFT_,
                    valign: _TOP_,
                    items: []
                }
            ]
        };

        var secondTabCases = topTabCases[i].items[2].items ;
        var secondTabChoices = topTabCases[i].items[1].choices ;
        var secondTabIndex = 0 ;

        for (var k in targets[i]) { // type, inDomains, entires, all
            if (!(k == "all" || k == "inDomains" || k == "entries" )) { //ignore other keywords (such as type)
                continue;
            }

            var targetRights = targets[i][k] ;

            var allLabel = com_zimbra_delegatedadmin.Tab_All ;
            var inDomainsLabel = com_zimbra_delegatedadmin.Tab_InDomains ;
            var byEntriesLabel = com_zimbra_delegatedadmin.Tab_ByEntries ;
            secondTabIndex ++;

            var tempSecondCase = {
                type:_ZATABCASE_,caseVarRef: secondTabCaseRef  ,
                overflow: "hidden", tabLevel:2,headerLevel: 2,
                caseKey: secondTabIndex, numCols:1,
                items:[]
            } ;

            var thirdTabCaseRef = secondTabCaseRef + "_" + secondTabIndex ;

            if (targetRights.length > 1) {
                //needs third row of tabs
                tempSecondCase.items = [
                    {
                        type:_TAB_BAR_,
                        ref: thirdTabCaseRef ,
                        choices: [],
                        onChange: ZaEffectiveRights.changeTab,
                        cssClass:"ZaTabBar",
                        id:"_tabbar"
                    },
                    {
                        type: _SWITCH_,
                        align: _LEFT_,
                        valign: _TOP_,
                        items: []
                    }
                ];

                var thirdTabChoices = tempSecondCase.items[0].choices;
                var thirdTabCases = tempSecondCase.items[1].items;
                var thirdTabIndex = 0 ;
            } else {  //not third row of tabs and rewrite the tab label
                if (k == "inDomains") {
                    inDomainsLabel += ": " + ZaUtil.join(targetRights[0].domain, "name", ",");
                } else if (k == "entries") {
                    byEntriesLabel += ": " + ZaUtil.join(targetRights[0].entry, "name", ",");
                }
            }

            if (k == "all") {
                secondTabChoices.push({value: secondTabIndex, label: allLabel });
            } else if (k == "inDomains") {
                secondTabChoices.push({value: secondTabIndex, label: inDomainsLabel});
            } else if (k == "entries") {
                secondTabChoices.push({value: secondTabIndex, label: byEntriesLabel });
            }

            secondTabCases.push(tempSecondCase);

            //construct the tempSecondCase below

            for (var m = 0; m < targetRights.length; m ++) {
                var right ;
                var title = null ;
                var titleLabel = "";
                var isByDomains = false ;
                if (k == "all") {
                    right = targetRights[m];
                    title = "all" ;
                } else if (k == "inDomains") {
                    right = targetRights[m].rights [0];
                    title = targetRights[m].domain;
                    isByDomains = true ;
                } else if (k == "entries") {
                    right = targetRights[m].rights [0];
                    title = targetRights[m].entry;
                }


                var strArr = [] ;
                if (title != null && title != "all") {
                    titleLabel = ZaUtil.join(title, "name", ", ");
                }

                var html = ZaEffectiveRightsXFormView.getEffectiveRightsHTML(right, grantee, title, type, isByDomains) ;

                if (targetRights.length == 1) { //no subtabs
//                    tempSecondCase.items.push({  type: _OUTPUT_, value: titleLabel });
                    tempSecondCase.items.push({  type: _OUTPUT_, value: html});
                } else {    //need third subtabs
                    thirdTabIndex ++;
                    thirdTabChoices.push({value: thirdTabIndex, label: titleLabel});
                    thirdTabCases.push(
                    {
                        type:_ZATABCASE_,caseVarRef: thirdTabCaseRef,
                        tabLevel: 3, headerLevel: 2,
                        caseKey: thirdTabIndex, numCols:1,
                        items:[
                            {
                                type: _OUTPUT_,
                                value: html
                            }
                        ]
                    });
                }
            }
        }

        if (secondTabCases.length == 0) { //no rights
            secondTabCases.push(
                    { type: _OUTPUT_ ,
					  value:  AjxMessageFormat.format(com_zimbra_delegatedadmin.er_no_rights, [grantee, type])
					});
        }
    }

    xFormObject.tableCssStyle = "width:100%;";
    xFormObject.items = [
        {
            type:_GROUP_,
            cssClass:"ZmSelectedHeaderBg",
            colSpan: "*",
            id:"xform_header",
            items: [
                {
                    type:_GROUP_,
                    numCols:4,
                    colSizes:["90px","350px","100px","*"],
                    items:headerItems
                }
            ],
            cssStyle:"padding-top:5px; padding-bottom:5px"
        },
        {
            type:_TAB_BAR_,
            ref:ZaModel.currentTab,
            choices:topTabChoices,
            onChange: ZaEffectiveRights.changeTab,
            cssClass:"ZaTabBar",
            id:"xform_tabbar"
        },
        {
            type:_SWITCH_,
            align:_LEFT_,
            valign:_TOP_,
            items:topTabCases
        }
    ];

    return xFormObject;
}


//ZaTabView.XFormModifiers["ZaEffectiveRightsXFormView"].push(ZaEffectiveRightsXFormView.myXFormModifier);

ZaEffectiveRightsXFormView.prototype.getTabToolTip =
function () {
    if (this._containedObject && this._containedObject.grantee
            && this._containedObject.grantee.name) {
        return AjxMessageFormat.format(com_zimbra_delegatedadmin.tt_tab_view_effective_rights, [this._containedObject.grantee.name]);
    } else {
        return "";
    }
}

ZaEffectiveRightsXFormView.prototype.getTabIcon =
function () {
    return "RightObject";
}

ZaEffectiveRightsXFormView.prototype.getTabTitle =
function () {
    if (this._containedObject && this._containedObject.grantee
            && this._containedObject.grantee.name) {
        return this._containedObject.grantee.name;
    } else {
        return "";
    }
}





    //--------------------------------------------------------------------------------------------------------
    //



