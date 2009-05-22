ZaAllGrantsXFormView = function(parent, entry) {
    ZaTabView.call(this, parent, "ZaAllGrantsXFormView");
    this.initForm(ZaAllGrantsXFormView.getXModel (), this.getMyXForm());
}

ZaAllGrantsXFormView.prototype = new ZaTabView();
ZaAllGrantsXFormView.prototype.constructor = ZaAllGrantsXFormView;
ZaTabView.XFormModifiers["ZaAllGrantsXFormView"] = new Array();

ZaAllGrantsXFormView.prototype.setObject =
function(entry) {
    this._containedObject = entry;

    this._localXForm.setInstance(this._containedObject);
    this.updateTab();
}


ZaAllGrantsXFormView.grantSelectionListener = function () {
    var instance = this.getForm().getInstance () ;
    var selectedGrants = this.widget.getSelection () ;
    this.getModel().setInstanceValue (instance,
            ZaGrant.A2_grantsListSelectedItems, selectedGrants) ;
}

ZaAllGrantsXFormView.getXModel = function () {
    return {
        items: [
            {id: ZaGrant.A_grantee, type: _EMAIL_ADDRESS_, ref: ZaGrant.A_grantee, required: true },
            {id: ZaGrant.A_grantee_id, type: _STRING_, ref: ZaGrant.A_grantee_id },
            {id: ZaGrant.A_grantee_type, type:_STRING_, ref:  ZaGrant.A_grantee_type, required: true, choices: ZaGrant.GRANT_TYPE},
            {id: ZaGrant.A2_grantsListSelectedItems, ref: ZaGrant.A2_grantsListSelectedItems, type:_LIST_ },
            {id: ZaGrant.A3_directGrantsList, type:_LIST_, ref: ZaGrant.A3_directGrantsList,
                    listItems: { type: _OBJECT_, items:
                        [
                            {id: ZaGrant.A_target, type: _STRING_, ref: ZaGrant.A_target },
                            {id: ZaGrant.A_target_type, ref: ZaGrant.A_target_type, type: _STRING_, choices: ZaZimbraRights.targetType },
                            {id: ZaGrant.A_grantee, type: _STRING_, ref: ZaGrant.A_grantee, required: true },
                            {id: ZaGrant.A_grantee_type, type:_LIST_, ref:  ZaGrant.A_grantee_type, required: true, choices: ZaGrant.GRANT_TYPE},
                            {id: ZaGrant.A_right, type: _STRING_, ref:  ZaGrant.A_right, required: true },
                            {id: ZaGrant.A_canDelegate, type:_ENUM_, ref: ZaGrant.A_canDelegate, choices:ZaModel.BOOLEAN_CHOICES2 },
                            {id: ZaGrant.A_deny, type:_ENUM_, ref: ZaGrant.A_deny, choices:ZaModel.BOOLEAN_CHOICES2 }
                        ]
                    }
            },
            {id: ZaGrant.A3_indirectGrantsList, type:_LIST_, ref: ZaGrant.A3_indirectGrantsList,
                    listItems: { type: _OBJECT_, items:
                        [
                            {id: ZaGrant.A_target, type: _STRING_, ref: ZaGrant.A_target },
                            {id: ZaGrant.A_target_type, ref: ZaGrant.A_target_type, type: _STRING_, choices: ZaZimbraRights.targetType },
                            {id: ZaGrant.A_grantee, type: _STRING_, ref: ZaGrant.A_grantee, required: true },
                            {id: ZaGrant.A_grantee_type, type:_LIST_, ref:  ZaGrant.A_grantee_type, required: true, choices: ZaGrant.GRANT_TYPE},
                            {id: ZaGrant.A_right, type: _STRING_, ref:  ZaGrant.A_right, required: true },
                            {id: ZaGrant.A_canDelegate, type:_ENUM_, ref: ZaGrant.A_canDelegate, choices:ZaModel.BOOLEAN_CHOICES2 },
                            {id: ZaGrant.A_deny, type:_ENUM_, ref: ZaGrant.A_deny, choices:ZaModel.BOOLEAN_CHOICES2 }
                        ]
                    }
            }    
        ]
    }
}

ZaAllGrantsXFormView.prototype.getMyXForm = function() {
    var xFormObject = {} ;
    var headerItems = [];
    headerItems.push({type:_OUTPUT_, ref:ZaGrant.A_grantee , labelLocation:_LEFT_,label:com_zimbra_delegatedadmin.Col_admin_name + ": ",visibilityChecks:[ZaItem.hasReadPermission]});
    headerItems.push({type:_OUTPUT_, ref:ZaGrant.A_grantee_id, labelLocation:_LEFT_,label:com_zimbra_delegatedadmin.Col_admin_id + ": ",visibilityChecks:[ZaItem.hasReadPermission]});

    var tabIndex = 0 ;
    this.directGrantsTabId = ++tabIndex ; 
    var tabChoices = [
        {value: this.directGrantsTabId, label: com_zimbra_delegatedadmin.lb_tab_directGrants },
        {value: ++tabIndex, label: com_zimbra_delegatedadmin.lb_tab_indirectGrants }
    ] ;


    tabIndex = 0 ; //reset the tabIndex to be used by cases
    var cases =[
        { type: _ZATABCASE_, caseKey: ++tabIndex, numCols:1, // colSizes:["700px"],
            id:"direct_all_grants_tab",
            items:[
                    {
                        ref: ZaGrant.A3_directGrantsList, id: ZaGrant.A3_directGrantsList, type: _DWT_LIST_,
                        width:"100%", 
                        cssClass: "MBXList", widgetClass: ZaGrantsListView,
                        headerList: ZaGrantsListView._getHeaderList (null, "all"),
                        hideHeader: false ,
                        onSelection:ZaAllGrantsXFormView.grantSelectionListener,
                        multiselect: false  //TODO: enable multiselect in the future
                    }
                ]
            },

        { type:_ZATABCASE_,caseKey: ++tabIndex, numCols:1, id:"indirect_all_grants_tab",
            items:[
                    {
                        ref: ZaGrant.A3_indirectGrantsList, id: ZaGrant.A3_indirectGrantsList, type: _DWT_LIST_,
                        width:"100%",
                        cssClass: "MBXList", widgetClass: ZaGrantsListView,
                        headerList: ZaGrantsListView._getHeaderList (null, "all"),
                        hideHeader: false ,
                        multiselect: false  //TODO: enable multiselect in the future
                    }
                ]
            }
    ] ;

    var ffTableStyle = "width:100%;overflow:visible;position:absolute;" ;
    var tableStyle = 	AjxEnv.isIE  ? ffTableStyle + "height:100%;" : ffTableStyle ;

    xFormObject.tableCssStyle = tableStyle;
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
            onChange: ZaAllGrantsXFormView.changeTab,
            choices:tabChoices,
            cssClass:"ZaTabBar",
            id:"xform_tabbar"
        },
        {
            type:_SWITCH_,
            align:_LEFT_,
            valign:_TOP_,
            items:cases
        }
    ];

    return xFormObject;
}


//ZaTabView.XFormModifiers["ZaAllGrantsXFormView"].push(ZaAllGrantsXFormView.myXFormModifier);

ZaAllGrantsXFormView.prototype.getTabToolTip =
function () {
    if (this._containedObject && this._containedObject.grantee ) {
        return AjxMessageFormat.format(com_zimbra_delegatedadmin.tt_tab_config_grants,
                [this._containedObject.grantee]);
    } else {
        return "";
    }
}

ZaAllGrantsXFormView.prototype.getTabIcon =
function () {
    return "GlobalPermission";
}

ZaAllGrantsXFormView.prototype.getTabTitle =
function () {
    if (this._containedObject && this._containedObject.grantee ){
        return this._containedObject.grantee;
    } else {
        return "";
    }
} ;

ZaAllGrantsXFormView.changeTab  = function (value, event, form)
{
    var instance = form.getInstance() ;
    var controller = ZaApp.getInstance ().getCurrentController () ;
    var addBt = controller._toolbar.getButton (ZaOperation.NEW)  ;
    var deleteBt = controller._toolbar.getButton (ZaOperation.DELETE );
    
    if (value == form.parent.directGrantsTabId) {
        //enable add and delete button
        deleteBt.setEnabled (true) ;
    } else {
        //disable add and delete button
        deleteBt.setEnabled (false) ;
    }

    this.setInstanceValue(value);
} ;
