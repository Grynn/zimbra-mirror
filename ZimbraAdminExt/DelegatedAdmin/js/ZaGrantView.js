ZaGrantView = function(parent) {
	ZaTabView.call(this, parent, "ZaGrantView");
    this.setScrollStyle (Dwt.SCROLL) ;
    this.TAB_INDEX = 0;
	this.initForm(ZaGrant.myXModel,this.getMyXForm());
	this._localXForm.setController(ZaApp.getInstance());
}

ZaGrantView.prototype = new ZaTabView();
ZaGrantView.prototype.constructor = ZaGrantView;
ZaTabView.XFormModifiers["ZaGrantView"] = new Array();

ZaGrantView.onFormFieldChanged =
function (value, event, form) {
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	return value;
}

ZaGrantView.prototype.setObject =
function (entry) {
    this._containedObject = entry ;
    //TODO: temporary solution to show all the items.
    this._containedObject.setAttrs = {} ;
    this._containedObject.setAttrs.all = true ;
    this._localXForm.setInstance(this._containedObject) ;
}

/**
* This method is added to the map {@link ZaTabView#XFormModifiers}
* @param xFormObject {Object} a definition of the form. This method adds/removes/modifies xFormObject to construct
* a Server view.
**/
ZaGrantView.myXFormModifier = function(xFormObject) {
    xFormObject.tableCssStyle = "width:100%;position:static;overflow:visible;" ;
    xFormObject.items = [
             {type:_GROUP_,  colSpan: "*", numCols: 2, colSizes: [200, "*"],
            items: [
               { type: _SPACER_ },
               //target is always com_zimbra_delegatedadmin.val_global_grant for global grants     
               { type: _OUTPUT_ , value: com_zimbra_delegatedadmin.val_global_grant, label: com_zimbra_delegatedadmin.Label_target_name },
               // make it type _DYNSELECT_
               { ref: ZaGrant.A_grantee, type: _TEXTFIELD_, label: com_zimbra_delegatedadmin.Label_grantee_name ,
                   visibilityChecks:[],
                   labelLocation:_LEFT_ },
               { ref: ZaGrant.A_grantee_type, type:_OSELECT1_, label: com_zimbra_delegatedadmin.Label_grantee_type ,
                   visibilityChecks:[],
                   labelLocation:_LEFT_, choices: ZaGrant.GRANTEE_TYPE
               },
               {ref: ZaGrant.A_right, type: _TEXTFIELD_, label: com_zimbra_delegatedadmin.Label_right_name,
                   visibilityChecks:[],
                   labelLocation:_LEFT_ },
               {ref: ZaGrant.A_deny,  type: _CHECKBOX_ , label: com_zimbra_delegatedadmin.Col_deny ,
                   visibilityChecks:[],  
                   labelLocation:_RIGHT_, trueValue:"1", falseValue:"0" } ,
               {ref: ZaGrant.A_canDelegate,  type: _CHECKBOX_ , label: com_zimbra_delegatedadmin.Col_can_grant ,
                   visibilityChecks:[],
                   labelLocation:_RIGHT_, trueValue:"1", falseValue:"0" }
              ]
            }
        ]
};
ZaTabView.XFormModifiers["ZaGrantView"].push(ZaGrantView.myXFormModifier);



/*
ZaGrantView.deleteGrantButtonListener = function () {
	var instance = this.getInstance();
	if(instance[ZaGrant.A2_grants_selection_cache] != null) {
		var cnt = instance[ZaGrant.A2_grants_selection_cache].length;
		if(cnt && instance.attrs[ZaGrant.A_grants]) {
			var arr = instance.attrs[ZaGrant.A_grants];
			for(var i=0;i<cnt;i++) {
				var cnt2 = arr.length-1;
				for(var k=cnt2;k>=0;k--) {
					if(arr[k]==instance.A2_grants_selection_cache[i]) {
						arr.splice(k,1);
						break;
					}
				}
			}
			this.getModel().setInstanceValue(instance, ZaGrant.A_grants, arr);
			this.getModel().setInstanceValue(instance, ZaGrant.A2_grants_selection_cache, []);
		}
	}
	this.getForm().parent.setDirty(true);
}

ZaGrantView.addGrantButtonListener =
function () {
    var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addGrantDlg) {
		formPage.addGrantDlg = new ZaGrantSelectionDialog (
                ZaApp.getInstance().getAppCtxt().getShell(),
                ZaApp.getInstance(), com_zimbra_delegatedadmin.Title_add_grants);
		formPage.addGrantDlg.registerCallback(DwtDialog.OK_BUTTON, ZaGrantView.addGrants, this.getForm(), null);
	}

	var obj = {};
	obj[ZaGrant.A_name] = "";
	formPage.addGrantDlg.setObject(obj);
	formPage.addGrantDlg.popup();
}

ZaGrantView.addGrants = function  () {
    if(this.parent.addGrantDlg) {
		this.parent.addGrantDlg.popdown();
		var obj = this.parent.addGrantDlg.getObject();
		if(obj[ZaGrant.A_selected_grants] && obj[ZaGrant.A_selected_grants].length>1) {
			var arr = this.getInstance().attrs[ZaGrant.A_grants] || [];
			arr = arr.concat(obj[ZaGrant.A_selected_grants]);
			this.getModel().setInstanceValue(this.getInstance(), ZaGrant.A_grants, arr);
			this.getModel().setInstanceValue(this.getInstance(), ZaGrant.A2_selected_grants, []);
			this.parent.setDirty(true);
		}
	}
}  */

