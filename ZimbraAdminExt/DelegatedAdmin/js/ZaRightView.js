ZaRightView = function(parent) {
	ZaTabView.call(this, parent, "ZaRightView");
    this.setScrollStyle (Dwt.SCROLL) ;
    this.TAB_INDEX = 0;
	this.initForm(ZaRight.myXModel,this.getMyXForm());
	this._localXForm.setController(ZaApp.getInstance());
}

ZaRightView.prototype = new ZaTabView();
ZaRightView.prototype.constructor = ZaRightView;
ZaTabView.XFormModifiers["ZaRightView"] = new Array();

ZaRightView.onFormFieldChanged =        
function (value, event, form) {
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	return value;
}

ZaRightView.prototype.getTabIcon = function () {
   return "RightObject"; 
}

ZaRightView.prototype.setObject =
function (entry) {
    this._containedObject = entry ;
    this._localXForm.setInstance(this._containedObject) ;
    //update the tab
    this.updateTab();
}


ZaRightView.deleteRightButtonListener = function () {
	var instance = this.getInstance();
	if(instance[ZaRight.A2_rights_selection_cache] != null) {
		var cnt = instance[ZaRight.A2_rights_selection_cache].length;
		if(cnt && instance.attrs[ZaRight.A_rights]) {
			var arr = instance.attrs[ZaRight.A_rights];
			for(var i=0;i<cnt;i++) {
				var cnt2 = arr.length-1;
				for(var k=cnt2;k>=0;k--) {
					if(arr[k]==instance.A2_rights_selection_cache[i]) {
						arr.splice(k,1);
						break;
					}
				}
			}
			this.getModel().setInstanceValue(instance, ZaRight.A_rights, arr);
			this.getModel().setInstanceValue(instance, ZaRight.A2_rights_selection_cache, []);
		}
	}
	this.getForm().parent.setDirty(true);
}

ZaRightView.addRightButtonListener =
function () {
    var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addRightDlg) {
		formPage.addRightDlg = new ZaRightSelectionDialog (
                ZaApp.getInstance().getAppCtxt().getShell(),
                ZaApp.getInstance(), com_zimbra_delegatedadmin.Title_add_rights);
		formPage.addRightDlg.registerCallback(DwtDialog.OK_BUTTON, ZaRightView.addRights, this.getForm(), null);
	}

	var obj = {};
	obj[ZaRight.A_name] = "";
	formPage.addRightDlg.setObject(obj);
	formPage.addRightDlg.popup();
}

ZaRightView.addRights = function  () {
    if(this.parent.addRightDlg) {
		this.parent.addRightDlg.popdown();
		var obj = this.parent.addRightDlg.getObject();
		if(obj[ZaRight.A_selected_rights] && obj[ZaRight.A_selected_rights].length>1) {
			var arr = this.getInstance().attrs[ZaRight.A_rights] || [];
			arr = arr.concat(obj[ZaRight.A_selected_rights]);
			this.getModel().setInstanceValue(this.getInstance(), ZaRight.A_rights, arr);
			this.getModel().setInstanceValue(this.getInstance(), ZaRight.A2_selected_rights, []);
			this.parent.setDirty(true);
		}
	}
}

/**
* This method is added to the map {@link ZaTabView#XFormModifiers}
* @param xFormObject {Object} a definition of the form. This method adds/removes/modifies xFormObject to construct
* a Server view.
**/
ZaRightView.myXFormModifier = function(xFormObject) {
    xFormObject.tableCssStyle="width:100%;position:static;overflow:visible;";

	xFormObject.items = [
        {type:_GROUP_,  colSpan: "*", numCols: 2, colSizes: [200, "*"],
            items: [
                { ref: ZaRight.A_name, type: _OUTPUT_ , label: com_zimbra_delegatedadmin.Col_right_name+ ": " },
                { ref: ZaRight.A_desc, type: _OUTPUT_, label: com_zimbra_delegatedadmin.Col_right_desc + ": " },
                { ref: ZaRight.A_type, type:_OUTPUT_, label: com_zimbra_delegatedadmin.Col_right_type + ": " },
                { ref: ZaRight.A_targetType, type:_OUTPUT_,
                    visibilityChecks: [ZaRightView.isShowTargetType],
                    visibilityChangeEventSources: [ZaRight.A_type] ,
                    label: com_zimbra_delegatedadmin.Label_target_type },
                    
                {type:_SPACER_, height: "10px" },
            //Rights View

              { type:_GROUP_, colSpan: "*", colSizes: ["200px", "*"], numCols: 2,
                  visibilityChecks: [ZaRightView.isShowRights],
                  visibilityChangeEventSources: [ZaRight.A_type] ,
                items: [
                  {type:_OUTPUT_, value: com_zimbra_delegatedadmin.Label_rights,
                      valign: _CENTER_, align: _RIGHT_ },
                  {ref:ZaRight.A_rights, type:_DWT_LIST_, height:200, width:"300px",
                        forceUpdate: true, cssClass: "DLSource",
                        widgetClass: ZaRightsMiniListView,
    //                        headerList:acctLimitsHeaderList,
                        hideHeader: false
                    } ,
                    {type: _SPACER_, height: 10 }
                ]
             },
            
                //get attributes view
              { type:_GROUP_, colSpan: "*", colSizes: ["200px", "*"], numCols: 2,
                  visibilityChecks: [ZaRightView.isShowGetAttrs],
                  visibilityChangeEventSources: [ZaRight.A_type] ,
                items: [
                  {type:_OUTPUT_, value: com_zimbra_delegatedadmin.Label_getAttr,
                      valign: _CENTER_, align: _RIGHT_ },
                  {ref:ZaRight.A_attrs, type:_DWT_LIST_, height:"200", width:"300px",
                        forceUpdate: true, cssClass: "DLSource",
                        widgetClass: ZaRightsAttrsListView,
//                        headerList:ZaRightsAttrsListView._getHeaderList(ZaRight.A_getAttrs),
                        hideHeader: false
                  } ,
                  {type: _SPACER_, height: 10 }
                ]
             },

    //        setAttributes view
            { type:_GROUP_, colSpan: "*", colSizes: ["200px", "*"], numCols: 2,
                 visibilityChecks: [ZaRightView.isShowSetAttrs],
                  visibilityChangeEventSources: [ZaRight.A_type] ,
                items: [
                  {type:_OUTPUT_, value: com_zimbra_delegatedadmin.Label_setAttr,
                      valign: _CENTER_, align: _RIGHT_ },
                  {ref:ZaRight.A_attrs, type:_DWT_LIST_, height:"200", width:"300px",
                        forceUpdate: true, cssClass: "DLSource",
                        widgetClass: ZaRightsAttrsListView,
//                        headerList: ZaRightsAttrsListView._getHeaderList(ZaRight.A_setAttrs),
                        hideHeader: false
                  },
                  {type: _SPACER_, height: 10 }
                    /*  No custom rights are supported,
                    {type:_CELLSPACER_},
                    {type:_GROUP_, numCols:5, width:"300px", colSizes:["80px","auto","80px","auto","80px"],
                        cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
                        items: [
                            {type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px"},
                            {type:_CELLSPACER_},
                            {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px"},
                            {type:_CELLSPACER_},
                            {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px"}
                        ]
                    }*/
                ]
             }
           ]
        }
    ]
};
ZaTabView.XFormModifiers["ZaRightView"].push(ZaRightView.myXFormModifier);

ZaRightView.isShowTargetType =  function () {
    var type = this.getInstanceValue(ZaRight.A_type) ;
    return (type != "combo") ;
}

ZaRightView.isShowGetAttrs =  function () {
    var type = this.getInstanceValue(ZaRight.A_type) ;
    return (type == "getAttrs") ;
}

ZaRightView.isShowSetAttrs =  function () {
    var type = this.getInstanceValue(ZaRight.A_type) ;
    return (type == "setAttrs") ;
}

ZaRightView.isShowRights =  function () {
    var type = this.getInstanceValue(ZaRight.A_type) ;
    return (type == "combo") ;
}


