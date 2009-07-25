//this is the permission tab view for each target
/*targets: 
    - account
    - calendar resource
    - cos
    - distribution list
    - domain
    - global config
    - global grant
    - right (TODO)
    - server
    - xmppcomponent
    - zimlet
*/


ZaTargetPermission = function () {} ;

//Mini Grants List View 
ZaGrantsListView = function(parent, className, posStyle, headerList) {
    if (arguments.length == 0) return;
	var className = className || null;
	var posStyle = posStyle || DwtControl.STATIC_STYLE;
	var headerList = headerList || ZaGrantsListView._getHeaderList();

	ZaListView.call(this, parent, className, posStyle, headerList);
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
    //disable the multiselect for Grants List view
    this.setMultiSelect(false) ;
}

ZaGrantsListView.prototype = new ZaListView;
ZaGrantsListView.prototype.constructor = ZaGrantsListView;

ZaGrantsListView.prototype.toString =
function() {
	return "ZaGrantsListView";
}

//the tab title and icon are only requried in global grants
//therefore their content is for global grants only
ZaGrantsListView.prototype.getTitle =
function () {
	return com_zimbra_delegatedadmin.GlobalGrants_view_title;
}

ZaGrantsListView.prototype.getTabIcon =
function () {
	return "GlobalPermission";
}

ZaGrantsListView.prototype._createItemHtml =
function(grant, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(grant, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;

        html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
        if (field == ZaGrant.A_right) {
            if (grant[ZaGrant.A_deny] && grant[ZaGrant.A_deny] == "1") {
                html[idx++] = "-" ;
            }

            if (grant[ZaGrant.A_canDelegate] && grant[ZaGrant.A_canDelegate] == "1") {
                html[idx++] = "+" ;
            }

            html[idx ++] = grant[field] ;

        } else if (field == ZaGrant.A_deny || field == ZaGrant.A_canDelegate) {
            continue ;
        }  else {
            var value = grant [field] ;
            if (field == ZaGrant.A_grantee) {
                if (value == null || value.length < 0) {
                    value = grant [ZaGrant.A_grantee_id] ;
                }
            }
            html[idx++] = AjxStringUtil.htmlEncode(value) ;
        }
        html[idx++] = "</nobr></td>" ;
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaGrantsListView._getHeaderList =
function(width, by) {
	var headerList = [];
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	var sortable=1;
    if (!width) width = 700;
    if (!by) by = ZaGrant.A_target ;
    var index = 0 ;
    if (by == ZaGrant.A_target ) {
        headerList[index ++] = new ZaListHeaderItem(ZaGrant.A_grantee, com_zimbra_delegatedadmin.Col_grantee_name,
                null, width/4 + 50, null, ZaGrant.A_grantee, true, true);
        headerList[index ++] = new ZaListHeaderItem(ZaGrant.A_grantee_type, com_zimbra_delegatedadmin.Col_grantee_type,
                null, width/4, null  , ZaGrant.A_grantee_type, true, true);
    } else if (by  == ZaGrant.A_grantee ) {
        headerList[index ++] = new ZaListHeaderItem(ZaGrant.A_target, com_zimbra_delegatedadmin.Col_target_name,
                null, width/4 + 50, null, ZaGrant.A_target, true, true);
        headerList[index ++] = new ZaListHeaderItem(ZaGrant.A_target_type, com_zimbra_delegatedadmin.Col_target_type,
                null, width/4, null  , ZaGrant.A_target_type, true, true);
    } else if ( by == "all")  {
        headerList[index ++] = new ZaListHeaderItem(ZaGrant.A_grantee, com_zimbra_delegatedadmin.Col_grantee_name,
                     null, 200, null, ZaGrant.A_grantee, true, true);
        headerList[index ++] = new ZaListHeaderItem(ZaGrant.A_target, com_zimbra_delegatedadmin.Col_target_name,
                null, 200, null, ZaGrant.A_target, true, true);
        headerList[index ++] = new ZaListHeaderItem(ZaGrant.A_target_type, com_zimbra_delegatedadmin.Col_target_type,
                null, 100, null  , ZaGrant.A_target_type, true, true);
    }

    headerList[index ++] = new ZaListHeaderItem(ZaGrant.A_right, com_zimbra_delegatedadmin.Col_grant_right_name,
                        null, "auto", null , ZaGrant.A_right, true, true);

  /*  headerList[index ++] = new ZaListHeaderItem(ZaGrant.A_canDelegate, com_zimbra_delegatedadmin.Col_canDelegate,
                    null, 80, null , ZaGrant.A_canDelegate, true, true);

    headerList[index ++] = new ZaListHeaderItem(ZaGrant.A_deny, com_zimbra_delegatedadmin.Col_deny,
                null, "auto", null , ZaGrant.A_deny, true, true);
    */
    return headerList;
}

ZaGrantsListView.isDeleteEnabled = function () {
    var grantListItem = this.getForm().getItemsById (ZaGrant.A2_grantsList) [0] ;
    if (grantListItem && grantListItem.getSelection ()
            && grantListItem.getSelection ().length > 0) { 
        return true ;
    } else {
        return true ;    //TODO: somehow the delete button enabling is not working with enableDisableChecks condition
    }
}

ZaGrantsListView.revokeRight = function () {
    var selectedGrants = this.getItemsById (ZaGrant.A2_grantsList) [0].getSelection() ;
    if (selectedGrants && selectedGrants.length > 0) {
        var instance = this.getInstance();
        var currentGrantList = instance [ZaGrant.A2_grantsList] ;

        var targetInfo = {} ;
        targetInfo [ZaGrant.A_target] = instance.name ;
        targetInfo [ZaGrant.A_target_type] = instance.type ;
    
        for (var i = 0; i < selectedGrants.length; i ++) {
// TODO: when multiselection enabled, we need a progress dialog to show the progress
            if (ZaGrant.revokeMethod (selectedGrants[i])) {
//                var j = ZaTargetPermission.findIndexOfGrant(currentGrantList, selectedGrants[i]);
                for (var j = 0; j < currentGrantList.length; j ++) {
                    if (selectedGrants[i] == currentGrantList[j] ) {
                        currentGrantList.splice(j, 1) ;
                    }
                }
            } else {
                break ; //jump out if failed.
            }
        }
        this.getModel().setInstanceValue(instance, ZaGrant.A2_grantsList, currentGrantList);
    }

    this.parent.revokeRightDlg.popdown () ;
}

ZaGrantsListView.revokeGlobalGrant = function () {
    var selectedGrants = this._contentView.getSelection() ;
    if (selectedGrants && selectedGrants.length > 0) {
        var targetInfo = {} ;
        targetInfo [ZaGrant.A_target] = "" ;
        targetInfo [ZaGrant.A_target_type] = "global" ;

        for (var i = 0; i < selectedGrants.length; i ++) {
// TODO: when multiselection enabled, we need a progress dialog to show the progress
            if (ZaGrant.revokeMethod (selectedGrants[i])) {
                // fire the removal event.               
                this.fireRemovalEvent (selectedGrants[i]) ;
            } else {
                break ; //jump out if failed.
            }
        }
    }

    this.revokeRightDlg.popdown () ;
}

ZaGrantsListView.grantSelectionListener = function () {
    var instance = this.getForm().getInstance () ;
    var selectedGrants = this.widget.getSelection () ;
    this.getModel().setInstanceValue (instance,
            ZaGrant.A2_grantsListSelectedItems, selectedGrants) ;
}

ZaTargetPermission.grantListItem = {
    id: ZaGrant.A2_grantsList, ref: ZaGrant.A2_grantsList, type: _LIST_,
    listItems: { type: _OBJECT_, items:
        [
            {id: ZaGrant.A_target, type: _STRING_, ref: ZaGrant.A_target },
            {id: ZaGrant.A_target_type, ref: ZaGrant.A_target_type, type: _STRING_, choices: ZaZimbraRights.targetType },
            {id: ZaGrant.A_grantee, type: _STRING_, ref: ZaGrant.A_grantee, required: true },
            {id: ZaGrant.A_grantee_type, type:_LIST_, ref:  ZaGrant.A_grantee_type, required: true, choices: ZaGrant.GRANT_TYPE},
            {id: ZaGrant.A_right, type: _STRING_, ref:  ZaGrant.A_right, required: true },
            {id: ZaGrant.A_canDelegate, type:_ENUM_, ref: ZaGrant.A_canDelegate, choices:ZaModel.BOOLEAN_CHOICES2 },    
            {id: ZaGrant.A_deny, type:_ENUM_, ref: ZaGrant.A_allow, choices:ZaModel.BOOLEAN_CHOICES2 },           
            {id: ZaGrant.A_deny, type:_ENUM_, ref: ZaGrant.A_deny, choices:ZaModel.BOOLEAN_CHOICES2 }
        ]
    }
};

/**
 * xform item for the grant lists view
 * It is used in very target's permssion view and the new Administrator wizard
 * @param params
 */
ZaTargetPermission.getGrantsListXFormItem = function (params) {
    if (!params) params = {} ;
    var w = params.width ? params.width : 700 ;
    var h = params.height ? params.height : 200 ;
    var by = params.by ? params.by : ZaGrant.A_target ;
    var grantsListXFormItem  =  {
        ref: ZaGrant.A2_grantsList, id: ZaGrant.A2_grantsList, type: _DWT_LIST_,
        width:w, height: h,
        cssClass: "DLSource", widgetClass: ZaGrantsListView,
        headerList: ZaGrantsListView._getHeaderList (w, by),
        hideHeader: false ,
        onSelection:ZaGrantsListView.grantSelectionListener,
        multiselect: false  //TODO: enable multiselect in the future
    } ;

    var marginLeft = ( w - 340 ) / 2 ;
    var grantsListButtonsItem =
    {type:_GROUP_, numCols:5, colSizes:["100px","20px","100px","20px","100px"],  height: 30,
        cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;margin-left: " + marginLeft + "; margin-right:auto;",
        items: [
            {type:_DWT_BUTTON_, label:com_zimbra_delegatedadmin.Bt_grant,width:"100px",
                onActivate:"ZaTargetPermission.grantButtonListener.call (this, '" + by +"');"},
            {type:_CELLSPACER_},
            {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
                onActivate:"ZaTargetPermission.editButtonListener.call (this, '" + by +"');"},
            {type:_CELLSPACER_},
            {type:_DWT_BUTTON_, label:com_zimbra_delegatedadmin.Bt_revoke,width:"100px",// align: _LEFT_ ,
                enableDisableChangeEventSources: [ZaGrant.A2_grantsListSelectedItems, ZaGrant.A2_grantsList] ,
                enableDisableChecks:[ZaGrantsListView.isDeleteEnabled],
                onActivate:"ZaTargetPermission.revokeButtonListener.call(this);"
            }
        ]
    }

    return [ grantsListXFormItem, grantsListButtonsItem] ;
}


ZaTargetPermission.targetXFormModifier = function (xFormObject, entry) {

    //check if the UI component is enabled
//    if (! ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        var uiEnabled  = ZaTabView.isTAB_ENABLED(entry,[ZaItem.A_zimbraACE], []);
        /*if (this instanceof ZaAccountXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.ACCOUNTS_PERM_TAB] ; 
        } else if (this instanceof ZaDLXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.DL_PERM_TAB] ;
        } else if (this instanceof ZaResourceXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.RESOURCE_PERM_TAB] ;
        } else if (this instanceof ZaDomainXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.DOMAIN_PERM_TAB] ;
        } else if (this instanceof ZaCosXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.COS_PERM_TAB] ;
        } else if (this instanceof GlobalConfigXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.CONFIG_PERM_TAB] ;
        } else if (this instanceof ZaServerXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.SERVER_PERM_TAB] ;
        } else if (this instanceof ZaZimletXFormView) {
            uiEnabled = ZaSettings.ENABLED_UI_COMPONENTS [ZaSettings.ZIMLET_PERM_TAB] ;
        }*/


        if (!uiEnabled) return ;
  //  }
   
    var tabIx, tabBar, switchGroup ;
    for (var i=0; i < xFormObject.items.length; i ++) {
        if (xFormObject.items[i].type == _TAB_BAR_) {
            tabBar = xFormObject.items[i] ;
        }

        if (xFormObject.items[i].type == _SWITCH_) {
            switchGroup = xFormObject.items[i]
        }
    }

    if (tabBar && switchGroup) {
        tabIx = ++this.TAB_INDEX;
        tabBar.choices.push({value:tabIx, label: com_zimbra_delegatedadmin.Tab_permission}) ;
    }

    var caseItem =
        {type:_ZATABCASE_, id:"target_form_permission_tab", numCols:1, colSizes:["700px"],
            caseKey:  tabIx,
            items:[
                {type: _SPACER_, height: 10},
                {type: _DWT_ALERT_, width: "98%",
				    style: DwtAlert.INFORMATION, iconVisible: false,
                    content: com_zimbra_delegatedadmin.HELP_NOTES_ACL },
                {type:_TOP_GROUPER_, label: com_zimbra_delegatedadmin.Label_permission,
                    id:"permission_grouper",
                    colSizes:["700px"],numCols:1,
                    items: ZaTargetPermission.getGrantsListXFormItem () 
                } 

            ]
        } ;

    switchGroup.items.push(caseItem);
}

ZaTargetPermission.permissionViewMethod =
function (entry) {

    if (entry[ZaGrant.A2_grantsList])  {
        this._view._containedObject[ZaGrant.A2_grantsList] = entry[ZaGrant.A2_grantsList] ;
        var xform = this._view._localXForm ;
        var instance  = xform.getInstance ();
        xform.getModel().setInstanceValue(instance, ZaGrant.A2_grantsList, entry[ZaGrant.A2_grantsList]);
    }
}


//add model and xform to the target's main view as a new tab - permissions
ZaTargetPermission.grantListSelectItem = {
    ref: ZaGrant.A2_grantsListSelectedItems, id: ZaGrant.A2_grantsListSelectedItems, type:_LIST_ }

//Domain Target
if (ZaDomain) {
    ZaDomain.myXModel.items.push(ZaTargetPermission.grantListItem) ;
    ZaDomain.myXModel.items.push(ZaTargetPermission.grantListSelectItem) ;
}

if (ZaTabView.XFormModifiers["ZaDomainXFormView"]){
    ZaSettings.DOMAIN_PERM_TAB = "domainPermissionTab";
//    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.DOMAIN_PERM_TAB, label: com_zimbra_delegatedadmin.UI_Comp_domainPermTab });
    ZaTabView.XFormModifiers["ZaDomainXFormView"].push(ZaTargetPermission.targetXFormModifier);
}

if (ZaItem.loadMethods["ZaDomain"]) {
    ZaItem.loadMethods["ZaDomain"].push (ZaGrant.loadMethod) ;
}

if (ZaController.setViewMethods["ZaDomainController"]) {
	ZaController.setViewMethods["ZaDomainController"].push(ZaTargetPermission.permissionViewMethod);
}

//Account Target
if (ZaAccount) {
    ZaAccount.myXModel.items.push(ZaTargetPermission.grantListItem) ;
    ZaAccount.myXModel.items.push(ZaTargetPermission.grantListSelectItem) ;
}

if (ZaTabView.XFormModifiers["ZaAccountXFormView"]){
    ZaSettings.ACCOUNTS_PERM_TAB = "accountsPermissionTab";
//    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.ACCOUNTS_PERM_TAB, label: com_zimbra_delegatedadmin.UI_Comp_AccountsPermTab });
    ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaTargetPermission.targetXFormModifier);
}

if (ZaItem.loadMethods["ZaAccount"]) {
    ZaItem.loadMethods["ZaAccount"].push (ZaGrant.loadMethod) ;
}

/*   Grant right and delete will be separated from the account modification
if (ZaItem.modifyMethods["ZaAccount"]) {
    ZaItem.modifyMethods["ZaAccount"].push(ZaGrant.grantMethod);
}
*/

if (ZaController.setViewMethods["ZaAccountViewController"]) {
	ZaController.setViewMethods["ZaAccountViewController"].push(ZaTargetPermission.permissionViewMethod);
}


//DL Target
if (ZaDistributionList) {
    ZaDistributionList.myXModel.items.push(ZaTargetPermission.grantListItem) ;
    ZaDistributionList.myXModel.items.push(ZaTargetPermission.grantListSelectItem) ;
}

if (ZaTabView.XFormModifiers["ZaDLXFormView"]){
    ZaSettings.DL_PERM_TAB = "dlPermissionTab";
//    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.DL_PERM_TAB, label: com_zimbra_delegatedadmin.UI_Comp_dlPermTab });
    ZaTabView.XFormModifiers["ZaDLXFormView"].push(ZaTargetPermission.targetXFormModifier);
}

if (ZaItem.loadMethods["ZaDistributionList"]) {
    ZaItem.loadMethods["ZaDistributionList"].push (ZaGrant.loadMethod) ;
}

if (ZaController.setViewMethods["ZaDLController"]) {
	ZaController.setViewMethods["ZaDLController"].push(ZaTargetPermission.permissionViewMethod);
}


//Resource target
if (ZaResource) {
    ZaResource.myXModel.items.push(ZaTargetPermission.grantListSelectItem) ;
    ZaResource.myXModel.items.push(ZaTargetPermission.grantListItem) ;
}

if (ZaTabView.XFormModifiers["ZaResourceXFormView"]){
    ZaSettings.RESOURCE_PERM_TAB = "resourcePermissionTab";
//    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.RESOURCE_PERM_TAB, label: com_zimbra_delegatedadmin.UI_Comp_resourcePermTab });
    ZaTabView.XFormModifiers["ZaResourceXFormView"].push(ZaTargetPermission.targetXFormModifier);
}

if (ZaItem.loadMethods["ZaResource"]) {
    ZaItem.loadMethods["ZaResource"].push (ZaGrant.loadMethod) ;
}

if (ZaController.setViewMethods["ZaResourceController"]) {
	ZaController.setViewMethods["ZaResourceController"].push(ZaTargetPermission.permissionViewMethod);
}

//COS Target
if (ZaCos) {
    ZaCos.myXModel.items.push(ZaTargetPermission.grantListItem) ;
    ZaCos.myXModel.items.push(ZaTargetPermission.grantListSelectItem) ;
}

if (ZaTabView.XFormModifiers["ZaCosXFormView"]){
    ZaSettings.COS_PERM_TAB = "cosPermissionTab";
//    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.COS_PERM_TAB, label: com_zimbra_delegatedadmin.UI_Comp_cosPermTab });
    ZaTabView.XFormModifiers["ZaCosXFormView"].push(ZaTargetPermission.targetXFormModifier);
}

if (ZaItem.loadMethods["ZaCos"]) {
    ZaItem.loadMethods["ZaCos"].push (ZaGrant.loadMethod) ;
}

if (ZaController.setViewMethods["ZaCosController"]) {
	ZaController.setViewMethods["ZaCosController"].push(ZaTargetPermission.permissionViewMethod);
}

//GlobalConfig Target
if (ZaGlobalConfig) {
    ZaGlobalConfig.myXModel.items.push(ZaTargetPermission.grantListItem) ;
    ZaGlobalConfig.myXModel.items.push(ZaTargetPermission.grantListSelectItem) ;
}

if (ZaTabView.XFormModifiers["GlobalConfigXFormView"]){
    ZaSettings.CONFIG_PERM_TAB = "configPermissionTab";
//    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.CONFIG_PERM_TAB, label: com_zimbra_delegatedadmin.UI_Comp_configPermTab });
    ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(ZaTargetPermission.targetXFormModifier);
}

if (ZaItem.loadMethods["ZaGlobalConfig"]) {
    ZaItem.loadMethods["ZaGlobalConfig"].push (ZaGrant.loadMethod) ;
}

if (ZaController.setViewMethods["ZaGlobalConfigViewController"]) {
	ZaController.setViewMethods["ZaGlobalConfigViewController"].push(ZaTargetPermission.permissionViewMethod);
}

//Server Target
if (ZaServer) {
    ZaServer.myXModel.items.push(ZaTargetPermission.grantListItem) ;
    ZaServer.myXModel.items.push(ZaTargetPermission.grantListSelectItem) ;
}

if (ZaTabView.XFormModifiers["ZaServerXFormView"]){
    ZaSettings.SERVER_PERM_TAB = "serverPermissionTab";
//    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.SERVER_PERM_TAB, label: com_zimbra_delegatedadmin.UI_Comp_serverPermTab });
    ZaTabView.XFormModifiers["ZaServerXFormView"].push(ZaTargetPermission.targetXFormModifier);
}

if (ZaItem.loadMethods["ZaServer"]) {
    ZaItem.loadMethods["ZaServer"].push (ZaGrant.loadMethod) ;
}

if (ZaController.setViewMethods["ZaServerController"]) {
	ZaController.setViewMethods["ZaServerController"].push(ZaTargetPermission.permissionViewMethod);
}

//Zimlet Target
if (ZaZimlet) {
   ZaZimlet.myXModel.items.push(ZaTargetPermission.grantListItem) ;
    ZaZimlet.myXModel.items.push(ZaTargetPermission.grantListSelectItem) ;
}

if (ZaTabView.XFormModifiers["ZaZimletXFormView"]){
    ZaSettings.ZIMLET_PERM_TAB = "zimletPermissionTab";
//    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.ZIMLET_PERM_TAB, label: com_zimbra_delegatedadmin.UI_Comp_zimletPermTab });
    ZaTabView.XFormModifiers["ZaZimletXFormView"].push(ZaTargetPermission.targetXFormModifier);
}

if (ZaItem.loadMethods["ZaZimlet"]) {
    ZaItem.loadMethods["ZaZimlet"].push (ZaGrant.loadMethod) ;
}

if (ZaController.setViewMethods["ZaZimletViewController"]) {
	ZaController.setViewMethods["ZaZimletViewController"].push(ZaTargetPermission.permissionViewMethod);
}

ZaTargetPermission.grantButtonListener =
function (by) {
    var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.grantRightDlg) {
		formPage.grantRightDlg = new ZaGrantDialog (
                ZaApp.getInstance().getAppCtxt().getShell(),
                ZaApp.getInstance(), com_zimbra_delegatedadmin.Title_grant_rights, by);
		formPage.grantRightDlg.registerCallback(ZaGrantDialog.ADD_FINISH_BUTTON,
                ZaGrantDialog.prototype.grantRight,
                formPage.grantRightDlg, [this.getForm(), false]);
        formPage.grantRightDlg.registerCallback(ZaGrantDialog.ADD_MORE_BUTTON,
                ZaGrantDialog.prototype.grantRight, formPage.grantRightDlg, [this.getForm(), true]);
	}

	var obj = {};
    if (by == null || by == ZaGrant.A_target) {
       var targetType = instance.type ;
       obj[ZaGrant.A_target_type] = targetType ;
        if (targetType == ZaItem.GLOBAL_CONFIG) {
            obj[ZaGrant.A_target] = ZaGrant.GLOBAL_CONFIG_TARGET_NAME ;
        }else{
            obj[ZaGrant.A_target] = instance.name;
            obj[ZaGrant.A_target_id] = instance.id ;
        }
    } else if (by == ZaGrant.A_grantee) {
       var granteeType = instance[ZaNewAdmin.A_admin_type] ;
       if (granteeType == ZaItem.ACCOUNT) {
            granteeType = "usr" ;
        }else if (granteeType == ZaItem.DL){
            granteeType = "grp" ;
        }
        obj[ZaGrant.A_grantee_type] = granteeType ;
        obj[ZaGrant.A_grantee] = instance.name ;
        obj[ZaGrant.A_grantee_id] = instance.id ;
    }

    obj.setAttrs = {} ;
    obj.setAttrs.all = true ;
    formPage.grantRightDlg.setObject(obj);
	formPage.grantRightDlg.popup();
    formPage.grantRightDlg.refresh ();
}

ZaTargetPermission.revokeButtonListener = function () {
    var instance = this.getInstance();
    var form = this.getForm () ;
    var formPage = form.parent;
    var selectedGrant = form.getItemsById (ZaGrant.A2_grantsList) [0].getSelection() ;
    if (selectedGrant && selectedGrant.length > 0) {
        if(!formPage.revokeRightDlg) {
            formPage.revokeRightDlg = new ZaMsgDialog (
                    ZaApp.getInstance().getAppCtxt().getShell(),
                    null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON]);
        }
        formPage.revokeRightDlg.registerCallback(DwtDialog.YES_BUTTON,
                ZaGrantsListView.revokeRight, form, null);
        var confirmMsg =  com_zimbra_delegatedadmin.confirm_delete_grants
                + ZaTargetPermission.getDlMsgFromGrant(selectedGrant) ;
        formPage.revokeRightDlg.setMessage (confirmMsg,  DwtMessageDialog.INFO_STYLE) ;
        formPage.revokeRightDlg.popup ();
    } else {
        ZaApp.getInstance().getCurrentController().popupMsgDialog (com_zimbra_delegatedadmin.no_grant_selected_msg) ;
    }
}

//@by: grantee or target
//@isGlobalGrant : is from global grant list view or not
ZaTargetPermission.editButtonListener = function (by, isGlobalGrant) {
    var instance = this.getInstance();
    var form = this.getForm () ;
    var formPage = form.parent;
    var selectedGrant = form.getItemsById (ZaGrant.A2_grantsList) [0].getSelection() ;
    if (selectedGrant && selectedGrant.length == 1) {
        if(!formPage.editRightDlg) {
            formPage.editRightDlg = new ZaGrantDialog (
                    ZaApp.getInstance().getAppCtxt().getShell(),
                    ZaApp.getInstance(), com_zimbra_delegatedadmin.Title_edit_rights, by, true);
        }

        formPage.editRightDlg.registerCallback(ZaGrantDialog.EDIT_FINISH_BUTTON,
                ZaGrantDialog.prototype.editRightAndFinish, formPage.editRightDlg,
                [form, selectedGrant[0], isGlobalGrant]);

        var obj = ZaUtil.deepCloneObject (selectedGrant[0], ["_evtMgr"]);
        if (obj[ZaGrant.A_right].indexOf("get.") == 0 || obj[ZaGrant.A_right].indexOf("set.")== 0) {
            obj[ZaGrant.A_right_type] = "inline" ;
            obj [ZaGrant.A_inline_right] = ZaGrantDialog.getInlineRightAttrsByName (obj[ZaGrant.A_right]) ;
        } else { //if it is not "inline", it must be "system"
            obj[ZaGrant.A_right_type] = "system" ;    
        }

//        obj.setAttrs = {} ;
//        obj.setAttrs.all = true ;  
        formPage.editRightDlg.setObject(obj);
        formPage.editRightDlg.popup();
        formPage.editRightDlg.refresh ();
    } else {
        ZaApp.getInstance().getCurrentController().popupMsgDialog (
                com_zimbra_delegatedadmin.no_grant_selected_msg) ;
    }
}


ZaTargetPermission.getDlMsgFromGrant =
function (grantsList) {
	var dlgMsg =  "<br><table>";
    var keys = [ZaGrant.A_target, ZaGrant.A_grantee, ZaGrant.A_right, ZaGrant.A_deny, ZaGrant.A_canDelegate] ;
    for (var i=0; i < grantsList.length; i ++) {
        var grant = grantsList [i] ;

        for (var j =0; j < keys.length; j ++) {
            var key = keys [j] ;
            dlgMsg += "<tr>";
             if (key == ZaGrant.A_target)  {
                dlgMsg += "<td>" + com_zimbra_delegatedadmin.Label_target_name + "</td>";
                var targetName = grant[ZaGrant.A_target] ;
                if (grant[ZaGrant.A_target_type] == ZaItem.GLOBAL_GRANT) {
                    targetName = ZaGrant.GLOBAL_TARGET_NAME  ;
                } else if (grant[ZaGrant.A_target_type] == ZaItem.GLOBAL_CONFIG) {
                    targetName = ZaGrant.GLOBAL_CONFIG_TARGET_NAME; 
                }
                dlgMsg += "<td>" + targetName + "</td>" ;
            } else if (key ==ZaGrant.A_grantee)  {
                var label = com_zimbra_delegatedadmin.Label_grantee_name ;
                var value = grant[ZaGrant.A_grantee] ;
                if ( value == null || value.length <= 0) {
                    label = com_zimbra_delegatedadmin.Label_grantee_id ;
                    value = grant[ZaGrant.A_grantee_id] ;
                }
                dlgMsg += "<td>" + label + "</td>";
                dlgMsg += "<td>" + value + "</td>" ;
            } else if (key == ZaGrant.A_right) {
                dlgMsg += "<td>" + com_zimbra_delegatedadmin.Label_right_name + "</td>";
                dlgMsg += "<td>"
                        + (grant[ZaGrant.A_canDelegate] == "1" ? "+" : "")
                        + (grant[ZaGrant.A_deny] == "1" ? "-" : "")
                        + grant[ZaGrant.A_right] + "</td>" ;
            } 
            dlgMsg += "</tr>";
        }
    }
    dlgMsg += "</table>";
    
    return dlgMsg ;
}


ZaItemRightList = function (contructor) {
    ZaItemList.call (this, contructor) ;
}

ZaItemRightList.prototype = new ZaItemList ;
ZaItemRightList.prototype.constructor = ZaItemRightList ;

ZaItemRightList.prototype.toString =
function() {
	return "ZaItemRightList";
}

ZaItemRightList.prototype.loadFromJS =
function (resp) {
    if(!resp || !resp.right)
		return;

    for (i = 0; i < resp.right.length; i ++) {
        var item;
        if(this._constructor) {
            item = new this._constructor() ;
        }
        item.type = ZaItem.RIGHT ;
        item.initFromJS(resp.right[i]) ;
        this.add (item) ;
    }
}

if (ZaSearch) {
/**
 * @argument callArgs {value, event, callback}
 */	
    ZaSearch.prototype.dynSelectGrantees = function (callArgs) {
        try {
			var value = callArgs["value"];
			var event = callArgs["event"];
			var callback = callArgs["callback"];
		        	
            var params = new Object();
            params.types = [ZaSearch.ACCOUNTS,ZaSearch.DLS, ZaSearch.ALIASES];
            var query = ZaSearch.getSearchByNameQuery(value,  params.types);
            query = "(&" + query
                    + "(|"
                    + "(" + ZaDistributionList.A_isAdminGroup + "=TRUE)"
                    + "(" + ZaAccount.A_zimbraIsAdminAccount + "=TRUE)"
                    + "(" + ZaAccount.A_zimbraIsDelegatedAdminAccount + "=TRUE)"
                    + ")"
                    + ")"
            dataCallback = new AjxCallback(this, this.dynSelectGranteeCallback, callback);

            params.callback = dataCallback;
            params.sortBy = ZaAccount.A_name;
            params.query = query ;
            params.controller = ZaApp.getInstance().getCurrentController();
            ZaSearch.searchDirectory(params);
        } catch (ex) {
            ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectDataFetcher");
        }
    }

    ZaSearch.prototype.dynSelectGranteeCallback = function (callback, resp) {
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


}








