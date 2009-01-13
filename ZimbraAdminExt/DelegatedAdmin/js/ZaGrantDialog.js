ZaGrantDialog = function(parent,  app, title) {
    if (arguments.length == 0) return;
    this._standardButtons = [ DwtDialog.CANCEL_BUTTON, DwtDialog.OK_BUTTON];
    ZaXDialog.call(this, parent,null,  title, "400px", "200px");
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
             {type:_GROUP_,isTabGroup:true, numCols:2, colSizes: [165, "*"], items: [ //allows tab key iteration
               { type: _SPACER_ },
               { ref: ZaGrant.A_target, type: _OUTPUT_ , label: com_zimbra_delegatedadmin.Label_target_name },
               { ref: ZaGrant.A_target_type, type:_OUTPUT_, label: com_zimbra_delegatedadmin.Label_target_type ,
                   labelLocation:_LEFT_, choices: ZaZimbraRights.targetType 
               },
                { ref: ZaGrant.A_grantee_type, type:_OSELECT1_, label: com_zimbra_delegatedadmin.Label_grantee_type ,
                    visibilityChecks:[], //temporary solution to make this element visible
                    labelLocation:_LEFT_, choices: ZaGrant.GRANTEE_TYPE
               },
               // make it type _DYNSELECT_
               { ref: ZaGrant.A_grantee, type: _TEXTFIELD_, label: com_zimbra_delegatedadmin.Label_grantee_name ,
                   visibilityChecks:[],
                   labelLocation:_LEFT_ },
               { ref: ZaGrant.A_right_type, type: _OSELECT1_, label: com_zimbra_delegatedadmin.Label_right_type,
                   visibilityChecks:[],
                   labelLocation: _LEFT_, choices: ZaGrant.RIGHT_TYPE_CHOICES
               },
               { type: _GROUP_, colSpan:"*", numCols:2, colSizes: [165, "*"],
                   visibilityChecks: [[ZaGrantDialog.rightTypeListener, "inline"]],
                   visibilityChangeEventSources: [ZaGrant.A_right_type] ,
                   items:
                   [
                       {ref: ZaGrant.A_inline_right + "/" + ZaGrant.A_inline_verb, type: _OSELECT1_,
                           visibilityChecks:[],
                            onChange: ZaGrantDialog.composeInlineRight,
                           label: com_zimbra_delegatedadmin.Label_inline_verb, required: true},
                       {ref: ZaGrant.A_inline_right + "/" + ZaGrant.A_inline_target_type,
                           visibilityChecks:[],
                           onChange: ZaGrantDialog.composeInlineRight,  required: true,
                           type: _OSELECT1_, label: com_zimbra_delegatedadmin.Label_inline_target_type },
                       {ref: ZaGrant.A_inline_right + "/" + ZaGrant.A_inline_attr,
                           visibilityChecks:[],
                           onChange: ZaGrantDialog.composeInlineRight,  required: true,
                           type: _TEXTFIELD_, label: com_zimbra_delegatedadmin.Label_inline_attr }
                   ]
               },

               {ref: ZaGrant.A_right, id: ZaGrant.A_right, type: _TEXTFIELD_, label: com_zimbra_delegatedadmin.Label_right_name,
                     visibilityChecks:[],
//                   visibilityChecks: [[ZaGrantDialog.rightTypeListener, "system"]],
//                   visibilityChangeEventSources: [ZaGrant.A_right_type] ,
                   enableDisableChecks:[[ZaGrantDialog.rightTypeListener, "system"]],
                   enableDisableChangeEventSources:[ZaGrant.A_right_type],
                   labelLocation:_LEFT_ },
               {ref: ZaGrant.A_deny,  type: _CHECKBOX_ , label: com_zimbra_delegatedadmin.Col_deny ,
                   visibilityChecks:[],  
                   labelLocation:_RIGHT_, trueValue:"1", falseValue:"0" }
              ]
            }
        ]
    };
    return xFormObject;
}

ZaGrantDialog.grantRight = function () {
    if(this.parent.grantRightDlg) {
		var obj = this.parent.grantRightDlg.getObject();
        var instance = this.getInstance();
        var currentGrantList = instance [ZaGrant.A2_grantsList] || [];

        //this.parent.setDirty(true);
        //GrantRights Right here, instead of populating to the account modification saving time
        // Advantages: 1. Avoid the double grants during the saving time
        // 2. reduce the load of the server during the account modification time
        if (ZaGrant.grantMethod (obj)) {
            //TODO: test if the grant exists in the current list already
            currentGrantList.push(obj) ;
            this.getModel().setInstanceValue(this.getInstance(), ZaGrant.A2_grantsList, currentGrantList);
            this.parent.grantRightDlg.popdown();
        }
    }
}

ZaGrantDialog.rightTypeListener =  function (type) {
    var rightType = this.getInstanceValue(ZaGrant.A_right_type) ;
    return (rightType == type) ;
}

ZaGrantDialog.composeInlineRight = function (value, event, form) {
//    console.log ("Compose Inline right") ;
    this.setInstanceValue (value) ;
    var rightName = ZaGrantDialog.getInlineRightName.call(this) ;
    this.setInstanceValue (rightName, ZaGrant.A_right) ;
    form.getItemsById (ZaGrant.A_right)[0].updateElement (rightName);
//    form.refresh ();
}

ZaGrantDialog.getInlineRightName = function (instance) {
    var verb = this.getInstanceValue (ZaGrant.A_inline_right + "/" + ZaGrant.A_inline_verb) || "";
    var targetType = this.getInstanceValue (ZaGrant.A_inline_right + "/" + ZaGrant.A_inline_target_type) || "" ;
    var attr = this.getInstanceValue (ZaGrant.A_inline_right + "/" + ZaGrant.A_inline_attr) || "";

    return verb +"." + targetType + "." + attr ;

}