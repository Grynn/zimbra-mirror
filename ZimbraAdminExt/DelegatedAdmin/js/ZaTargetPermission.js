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
}

ZaGrantsListView.prototype = new ZaListView;
ZaGrantsListView.prototype.constructor = ZaGrantsListView;

ZaGrantsListView.prototype.toString =
function() {
	return "ZaGrantsListView";
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
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;

        html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
        html[idx++] = AjxStringUtil.htmlEncode(grant [field]) ;
        html[idx++] = "</nobr></td>" ;
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaGrantsListView._getHeaderList =
function() {
	var headerList = [];
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	var sortable=1;
	headerList[0] = new ZaListHeaderItem(ZaGrant.A_grantee, com_zimbra_delegatedadmin.Col_grantee_name,
            null, 200, null, ZaGrant.A_grantee, true, true);

	headerList[1] = new ZaListHeaderItem(ZaGrant.A_grantee_type, com_zimbra_delegatedadmin.Col_grantee_type,
            null, 100, null  , ZaGrant.A_grantee_type, true, true);

    headerList[2] = new ZaListHeaderItem(ZaGrant.A_deny, com_zimbra_delegatedadmin.Col_deny,
                50, null, null , ZaGrant.A_deny, true, true);

    headerList[3] = new ZaListHeaderItem(ZaGrant.A_right, com_zimbra_delegatedadmin.Col_grant_right_name,
                    null, null, null , ZaGrant.A_right, true, true);

    return headerList;
}

ZaGrantsListView.grantSelectionListener = function () {
    
}

//TODO: model items definition
var grantListItem = {
    id: ZaGrant.A2_grantsList, ref: ZaGrant.A2_grantsList, type: _LIST_,
    listItems: { type: _OBJECT_, items:
        [
            {id: ZaGrant.A_grantee, type: _STRING_, ref: ZaGrant.A_grantee, required: true },    
            {id: ZaGrant.A_grantee_type, type:_LIST_, ref:  ZaGrant.A_grantee_type, required: true, choices: ZaGrant.GRANT_TYPE},
            {id: ZaGrant.A_right, type: _STRING_, ref:  ZaGrant.A_right, required: true },
            {id: ZaGrant.A_deny, type:_ENUM_, ref: ZaGrant.A_deny, choices:ZaModel.BOOLEAN_CHOICES2 }
        ]
    }
};

//TODO: xform item definition
var grantsListXFormItem  =  {
    ref: ZaGrant.A2_grantsList, type: _DWT_LIST_, width:600, height: 200,
    cssClass: "DLSource", widgetClass: ZaGrantsListView,
    headerList: ZaGrantsListView._getHeaderList (),
    hideHeader: false ,
    onSelection:ZaGrantsListView.grantSelectionListener
} ;

ZaTargetPermission.targetXFormModifier = function (xFormObject) {
    var tabBar, switchGroup ;
    for (var i=0; i < xFormObject.items.length; i ++) {
        if (xFormObject.items[i].type == _TAB_BAR_) {
            tabBar = xFormObject.items[i] ;
        }

        if (xFormObject.items[i].type == _SWITCH_) {
            switchGroup = xFormObject.items[i]
        }
    }

    if (tabBar && switchGroup) {
        var tabIx = tabBar.choices.length + 1;
        tabBar.choices.push({value:tabIx, label: com_zimbra_delegatedadmin.Tab_permission}) ;
    }

    var caseItem =
        {type:_ZATABCASE_, id:"target_form_permission_tab", numCols:1, colSizes:["600px"],
            caseKey:  tabIx,
            items:[
               {type:_SPACER_, height: "10px" },
               {type:_OUTPUT_, value: com_zimbra_delegatedadmin.Label_permission },
               grantsListXFormItem ,
//                   {type:_CELLSPACER_},
               {type:_GROUP_, numCols:3,width: 300, colSizes:["100px","20px","*"],  height: 30,
                    cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;margin-left: 200px; margin-right:auto;",
                    items: [
                        {type:_DWT_BUTTON_, label:com_zimbra_delegatedadmin.Bt_grant,width:"100px",
                            onActivate:"ZaTargetPermission.grantButtonListener.call (this);",
                            align: _RIGHT_},
                        {type:_CELLSPACER_},
//                            {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px" },
//                            {type:_CELLSPACER_},
                        {type:_DWT_BUTTON_, label:com_zimbra_delegatedadmin.Bt_revoke,width:"100px", align: _LEFT_ ,
                            onActivate:"ZaTargetPermission.revokeButtonListener.call(this);"
                        }
                    ]
                }
            ]
        }
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

    /*
    if (instance) {
		if (instance.attrs[ZaAccount.A_zimbraDomainAdminMaxMailQuota] >= 0) {
			instance [ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed] = 'TRUE';
		}else {
			instance [ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed] = null ; //null will allow the cos value to be shown
		}
		xform.refresh ();
	} */
}


//TODO: add model and xform to the target's main view as a new tab - permissions

//Domain Target
if (ZaDomain) {
    ZaDomain.myXModel.items.push(grantListItem) ;
}

if (ZaTabView.XFormModifiers["ZaDomainXFormView"]){
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
    ZaAccount.myXModel.items.push(grantListItem) ;
}

if (ZaTabView.XFormModifiers["ZaAccountXFormView"]){
    ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaTargetPermission.targetXFormModifier);
}

if (ZaItem.loadMethods["ZaAccount"]) {
    ZaItem.loadMethods["ZaAccount"].push (ZaGrant.loadMethod) ;
}

if (ZaController.setViewMethods["ZaAccountViewController"]) {
	ZaController.setViewMethods["ZaAccountViewController"].push(ZaTargetPermission.permissionViewMethod);
}


//DL Target
if (ZaDistributionList) {
    ZaDistributionList.myXModel.items.push(grantListItem) ;
}

if (ZaTabView.XFormModifiers["ZaDLXFormView"]){
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
    ZaResource.myXModel.items.push(grantListItem) ;
}

if (ZaTabView.XFormModifiers["ZaResourceXFormView"]){
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
    ZaCos.myXModel.items.push(grantListItem) ;
}

if (ZaTabView.XFormModifiers["ZaCosXFormView"]){
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
    ZaGlobalConfig.myXModel.items.push(grantListItem) ;
}

if (ZaTabView.XFormModifiers["GlobalConfigXFormView"]){
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
    ZaServer.myXModel.items.push(grantListItem) ;
}

if (ZaTabView.XFormModifiers["ZaServerXFormView"]){
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
    ZaZimlet.myXModel.items.push(grantListItem) ;
}

if (ZaTabView.XFormModifiers["ZaZimletXFormView"]){
    ZaTabView.XFormModifiers["ZaZimletXFormView"].push(ZaTargetPermission.targetXFormModifier);
}

if (ZaItem.loadMethods["ZaZimlet"]) {
    ZaItem.loadMethods["ZaZimlet"].push (ZaGrant.loadMethod) ;
}

if (ZaController.setViewMethods["ZaZimletViewController"]) {
	ZaController.setViewMethods["ZaZimletViewController"].push(ZaTargetPermission.permissionViewMethod);
}

//TODO: permission actions - get/grant/revoke grants
ZaTargetPermission.grantButtonListener =
function () {
    var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.grantRightDlg) {
		formPage.grantRightDlg = new ZaGrantDialog (
                ZaApp.getInstance().getAppCtxt().getShell(),
                ZaApp.getInstance(), com_zimbra_delegatedadmin.Title_grant_rights);
		formPage.grantRightDlg.registerCallback(DwtDialog.OK_BUTTON, ZaGrantDialog.grantRight, this.getForm(), null);
	}

	var obj = {};
	obj[ZaGrant.A_target] = instance.name;
    obj[ZaGrant.A_target_type] = instance.type ;
    //TODO: temporary solution to show all the items.
    obj.setAttrs = {} ;
    obj.setAttrs.all = true ;
    formPage.grantRightDlg.setObject(obj);
	formPage.grantRightDlg.popup();
}

ZaTargetPermission.revokeButtonListener = function () {
    
}








