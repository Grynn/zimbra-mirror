ZaGrantDialog = function(parent,  app, title) {
    if (arguments.length == 0) return;
    this._standardButtons = [ DwtDialog.CANCEL_BUTTON, DwtDialog.OK_BUTTON];
    ZaXDialog.call(this, parent,null,  title, "400px", "150px");
    this._containedObject = {};

    this.initForm(ZaGrant.myXModel, this.getMyXForm());
}

ZaGrantDialog.prototype = new ZaXDialog;
ZaGrantDialog.prototype.constructor = ZaGrantDialog;



ZaGrantDialog.prototype.getMyXForm =
function() {
    var xFormObject = {
        numCols:1,
        items:[
             {type:_GROUP_,isTabGroup:true, numCols:2, colSizes: [100, "*"], items: [ //allows tab key iteration
               { type: _SPACER_ },
               { ref: ZaGrant.A_target, type: _OUTPUT_ , label: com_zimbra_delegatedadmin.Label_target_name },
               { ref: ZaGrant.A_target_type, type:_OUTPUT_, label: com_zimbra_delegatedadmin.Label_target_type ,
                   labelLocation:_LEFT_, choices: ZaZimbraRights.targetType 
               },
               // make it type _DYNSELECT_
               { ref: ZaGrant.A_grantee, type: _TEXTFIELD_, label: com_zimbra_delegatedadmin.Label_grantee_name ,
                   labelLocation:_LEFT_ },
               { ref: ZaGrant.A_grantee_type, type:_OSELECT1_, label: com_zimbra_delegatedadmin.Label_grantee_type ,
                   labelLocation:_LEFT_, choices: ZaGrant.GRANTEE_TYPE
               },
               {ref: ZaGrant.A_right, type: _TEXTFIELD_, label: com_zimbra_delegatedadmin.Label_right_name,
                    labelLocation:_LEFT_ },
               {ref: ZaGrant.A_deny,  type: _CHECKBOX_ , label: com_zimbra_delegatedadmin.Col_deny ,
                     labelLocation:_RIGHT_, trueValue:"1", falseValue:"0" }
              ]
            }
        ]
    };
    return xFormObject;
}

ZaGrantDialog.grantRight = function () {
    if(this.parent.grantRightDlg) {
		this.parent.grantRightDlg.popdown();
		var obj = this.parent.grantRightDlg.getObject();
        var instance = this.getInstance();
        var currentGrantList = instance [ZaGrant.A2_grantsList] || [];
        //TODO: test if the grant exists in the current list already
        currentGrantList.push(obj) ;

        this.getModel().setInstanceValue(this.getInstance(), ZaGrant.A2_grantsList, currentGrantList);
        this.parent.setDirty(true);
	}
}