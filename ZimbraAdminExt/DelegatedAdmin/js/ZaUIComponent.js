function ZaUIComponent () {}

ZaUIComponent.A_comp_name = "comp_name" ;
ZaUIComponent.A2_ui_comp_selection_cache = "ui_comp_selection_cache" ;

ZaUIComponent.myXModel = {
	items: [
	    {id:"name", type:_STRING_, ref:"name"},
		{id:ZaUIComponent.A_comp_name, type:_STRING_, ref:ZaUIComponent.A_comp_name}
	]
}

ZaUIComponent.addButtonListener =
function () {
    var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addUICompDlg) {
		formPage.addUICompDlg = new ZaNewUICompXDialog(ZaApp.getInstance().getAppCtxt().getShell(),
                "450px", "80px",com_zimbra_delegatedadmin.Add_UIComp_Title);
		formPage.addUICompDlg.registerCallback(DwtDialog.OK_BUTTON, ZaUIComponent.addUIComp, this.getForm(), null);
	}

	var obj = {};
	obj.name = instance.name;
	formPage.addUICompDlg.setObject(obj);
	formPage.addUICompDlg.popup();
}

ZaUIComponent.addUIComp  = function () {
	if(this.parent.addUICompDlg) {
		this.parent.addUICompDlg.popdown();
		var obj = this.parent.addUICompDlg.getObject();
		if(obj[ZaUIComponent.A_comp_name] && obj[ZaUIComponent.A_comp_name].length>0) {
			var instance = this.getInstance();
			var arr = instance.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] || [];
			arr.push (obj[ZaUIComponent.A_comp_name]);  //TODO, may need to add a list item
			this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A_zimbraAdminConsoleUIComponents, arr);
			this.getModel().setInstanceValue(this.getInstance(),ZaUIComponent.A2_ui_comp_selection_cache, []);

            if (this.parent && this.parent.setDirty) this.parent.setDirty(true);
		}
	}
}

ZaUIComponent.deleteButtonListener = function () {
  var instance = this.getInstance();
	if(instance[ZaUIComponent.A2_ui_comp_selection_cache] != null) {
		var cnt = instance[ZaUIComponent.A2_ui_comp_selection_cache].length;
		if(cnt && instance.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents]) {
			var uiCompArr = instance.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents];
		    for(var i=0;i<cnt;i++) {
				var cnt2 = uiCompArr.length-1;
				for(var k=cnt2;k>=0;k--) {
					if(uiCompArr[k]==instance[ZaUIComponent.A2_ui_comp_selection_cache][i]) {
						uiCompArr.splice(k,1);
						break;
					}
				}
			}
			this.getModel().setInstanceValue(instance, ZaAccount.A_zimbraAdminConsoleUIComponents, uiCompArr);
		}
	}
	this.getModel().setInstanceValue(instance, ZaUIComponent.A2_ui_comp_selection_cache, []);
	this.getForm().parent.setDirty(true);
}

ZaUIComponent.getCompDescription = function (comp) {
    for (var i =0 ; i < ZaSettings.ALL_UI_COMPONENTS.length; i ++) {
        if (comp == ZaSettings.ALL_UI_COMPONENTS[i].value) {
            return ZaSettings.ALL_UI_COMPONENTS[i].label ;
        }
    }

    return "" ;
}

//UI Component List View
ZaUICompListView = function(parent, className, posStyle, headerList) {
    if (arguments.length == 0) return;
	var className = className || null;
	var posStyle = posStyle || DwtControl.STATIC_STYLE;
	var headerList = headerList || ZaUICompListView._getHeaderList();

	ZaListView.call(this, parent, className, posStyle, headerList);
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
}

ZaUICompListView.prototype = new ZaListView;
ZaUICompListView.prototype.constructor = ZaUICompListView;

ZaUICompListView.prototype.toString =
function() {
	return "ZaUICompListView";
}

ZaUICompListView.prototype._createItemHtml =
function(comp, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(comp, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;

        html[idx++] = "<td align='left' width=" + this._headerList[i]._width + "><nobr>";
        if (field == ZaAccount.A_zimbraAdminConsoleUIComponents) {
            html[idx++] = AjxStringUtil.htmlEncode(comp) ;
        }else if (field == ZaAccount.A_description)   {
            html[idx++] = AjxStringUtil.htmlEncode(ZaUIComponent.getCompDescription(comp)) ;            
        }
        html[idx++] = "</nobr></td>" ;
	}
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaUICompListView._getHeaderList =
function() {
	var headerList = [];
//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
//	var sortable=1;
	headerList[0] = new ZaListHeaderItem(ZaAccount.A_zimbraAdminConsoleUIComponents, com_zimbra_delegatedadmin.Col_comp_name,
            null, 200, null, ZaAccount.A_zimbraAdminConsoleUIComponents, true, true);
	headerList[1] = new ZaListHeaderItem(ZaAccount.A_description, ZaMsg.ALV_Description_col,
            null, null, null  , ZaAccount.A_description, true, true);

    return headerList;
}

ZaUICompListView.uiCompSelectionListener = function () {
    var instance = this.getForm().getInstance () ;
    var selectedComps = this.widget.getSelection () ;
    this.getModel().setInstanceValue (instance,
           ZaUIComponent.A2_ui_comp_selection_cache, selectedComps) ;
}


//zimbraAdminConsoleUIComponents   view in account permission view
ZaUIComponent.accountTargetXFormModifier = function (xFormObject) {
    var switchGroupItems ;
    for (var i=0; i < xFormObject.items.length; i ++) {
        if (xFormObject.items[i].type == _SWITCH_) {
            switchGroupItems = xFormObject.items[i].items ;
            break ;
        }
    }

    var permissionView;
    for (var j=0; j < switchGroupItems.length; j ++) {
        if ((switchGroupItems[j].type == _ZATABCASE_ )
                && (switchGroupItems[j].id == "target_form_permission_tab")) {
            permissionView = switchGroupItems[j] ;
        }
    }

    var componentUIItems = [
        {type:_TOP_GROUPER_, label: com_zimbra_delegatedadmin.Label_ui_comp, id:"permission_ui_comp_grouper",
            colSizes:["700px"],numCols:1
        }
    ];
    componentUIItems.items = ZaUIComponent.getUIComponentsXFormItem () ;
    
    permissionView.items = permissionView.items.concat(componentUIItems);
    return ;
}

ZaUIComponent.UIComponentsItem = {
     id: ZaAccount.A_zimbraAdminConsoleUIComponents, ref: "attrs/" + ZaAccount.A_zimbraAdminConsoleUIComponents,
     type: _LIST_, listItems: { type: _STRING_ }
};


ZaUIComponent.getUIComponentsXFormItem  = function (params) {
    if (!params) params = {};
    var w = params.width ? params.width : 700 ;
    var h = params.height ? params.height : 200 ;
    var list = {
        ref: ZaAccount.A_zimbraAdminConsoleUIComponents, type: _DWT_LIST_,
        width:w, height: h,
        cssClass: "DLSource", widgetClass: ZaUICompListView,
        headerList: ZaUICompListView._getHeaderList (),
        onSelection:ZaUICompListView.uiCompSelectionListener,
        forceUpdate: true, preserveSelection:false, hideHeader: false
    } ;

    var marginLeft = ( w - 220 ) / 2 ;

    var buttons =  {
        type:_GROUP_, numCols:3,width: 350, colSizes:["100px","20px","*"],  height: 30,
        cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;margin-left: " + marginLeft + "; margin-right:auto;",
        items: [
            {type:_DWT_BUTTON_, label:com_zimbra_delegatedadmin.Bt_grant,width:"100px",
                onActivate:"ZaUIComponent.addButtonListener.call (this);",
                align: _RIGHT_},
            {type:_CELLSPACER_},
            {type:_DWT_BUTTON_, label:com_zimbra_delegatedadmin.Bt_revoke,width:"100px", align: _LEFT_ ,
                onActivate:"ZaUIComponent.deleteButtonListener.call(this);"
            }
        ]
    };

    return [list, buttons] ;
}

ZaUIComponent.uiCompViewMethod =
function (entry) {
    this._view._containedObject.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents]
                = entry.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] || [];

    /*
    if (! (this._view._containedObject[ZaAccount.A_zimbraAdminConsoleUIComponents] instanceof Array)) {
        this._view._containedObject[ZaAccount.A_zimbraAdminConsoleUIComponents] = [this._view._containedObject[ZaAccount.A_zimbraAdminConsoleUIComponents]] ;        
    } */
    var xform = this._view._localXForm ;
    var instance  = xform.getInstance ();
    xform.getModel().setInstanceValue(instance, ZaAccount.A_zimbraAdminConsoleUIComponents,
             this._view._containedObject.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents]);
}

ZaUIComponent.uiCompObjectModifer = function () {
    if (this.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents]) {
        if(!(this.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] instanceof Array)) {
            this.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] = [this.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents]];
        }
    }
}

if (ZaAccount) {
    ZaAccount.myXModel.items.push(ZaUIComponent.UIComponentsItem);
}

if (ZaItem.ObjectModifiers["ZaAccount"]){
    ZaItem.ObjectModifiers["ZaAccount"].push(ZaUIComponent.uiCompObjectModifer) ;
}

if (ZaTabView.XFormModifiers["ZaAccountXFormView"]){
    ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaUIComponent.accountTargetXFormModifier);
}

if (ZaController.setViewMethods["ZaAccountViewController"]) {
	ZaController.setViewMethods["ZaAccountViewController"].push(ZaUIComponent.uiCompViewMethod);
}


//add UI component dialog
ZaNewUICompXDialog = function(parent,   w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];
	ZaXDialog.call(this, parent, null, title, w, h);
	this._containedObject = {};
	this.initForm(ZaUIComponent.myXModel,this.getMyXForm());
    this._helpURL = ZaNewUICompXDialog.helpURL;
}

ZaNewUICompXDialog.prototype = new ZaXDialog;
ZaNewUICompXDialog.prototype.constructor = ZaNewUICompXDialog;
ZaNewUICompXDialog.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/adding_ui_component.htm?locid="+AjxEnv.DEFAULT_LOCALE;

ZaNewUICompXDialog.prototype.getMyXForm =
function() {
	var xFormObject = {
		numCols:1,
		items:[
          {type:_GROUP_,isTabGroup:true, items: [ //allows tab key iteration
                {ref:"name", type:_OUTPUT_, label:com_zimbra_delegatedadmin.Label_admin_account,
                            visibilityChecks:[],enableDisableChecks:[]},
                {ref:ZaUIComponent.A_comp_name, type:_OSELECT1_, label:com_zimbra_delegatedadmin.Label_ui_comp_name,
                    choices:ZaSettings.ALL_UI_COMPONENTS,
                    visibilityChecks:[],enableDisableChecks:[]}
            ]
          }
        ]
	};
	return xFormObject;
}
