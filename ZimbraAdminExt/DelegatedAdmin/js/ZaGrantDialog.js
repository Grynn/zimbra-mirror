ZaGrantDialog = function(parent,  app, title) {
    if (arguments.length == 0) return;
    this._standardButtons = [ DwtDialog.CANCEL_BUTTON, DwtDialog.OK_BUTTON];
    ZaXDialog.call(this, parent,null,  title, "400px", "200px");
    this._containedObject = {};

    this.systemRightsChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
    this.granteeNameChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "name", "name");

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
               { ref: ZaGrant.A_target_type, type:_OUTPUT_, label: com_zimbra_delegatedadmin.Label_target_type   },

               // make it type _DYNSELECT_
               { ref: ZaGrant.A_grantee, type: _DYNSELECT_, label: com_zimbra_delegatedadmin.Label_grantee_name ,
                   visibilityChecks:[],labelLocation:_LEFT_ ,
                   emptyText:ZaMsg.enterSearchTerm,
                   choices: this.granteeNameChoices,
                   onChange: ZaGrantDialog.setGranteeChanged,
                   dataFetcherClass:ZaSearch ,
                   dataFetcherMethod:ZaSearch.prototype.dynSelectGrantees,
                   editable: true
               },
               { ref: ZaGrant.A_grantee_type, type:_TEXTFIELD_, label: com_zimbra_delegatedadmin.Label_grantee_type ,
                    visibilityChecks:[], //temporary solution to make this element visible
                    enableDisableChecks:false,
//                    enableDisableChangeEventSources:[ZaGrant.A_right_type],
                    labelLocation:_LEFT_
               },     /*
               { ref: ZaGrant.A_grantee_type, type:_OSELECT1_, label: com_zimbra_delegatedadmin.Label_grantee_type ,
                    visibilityChecks:[], //temporary solution to make this element visible
                    labelLocation:_LEFT_, choices: ZaGrant.GRANTEE_TYPE
               },      */
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
               {ref: ZaGrant.A_right, id: ZaGrant.A_right, type: _DYNSELECT_, label: com_zimbra_delegatedadmin.Label_right_name,
                         visibilityChecks:[],
    //                   visibilityChecks: [[ZaGrantDialog.rightTypeListener, "system"]],
    //                   visibilityChangeEventSources: [ZaGrant.A_right_type] ,
                       enableDisableChecks:[[ZaGrantDialog.rightTypeListener, "system"]],
                       enableDisableChangeEventSources:[ZaGrant.A_right_type],
                       labelLocation:_LEFT_ ,
                       emptyText:ZaMsg.enterSearchTerm,
                       choices: this.systemRightsChoices, 
//                       inputPreProcessor:ZaGrantDialog.preProcessRightNames,
                       dataFetcherClass:ZaRight ,
                       dataFetcherMethod:ZaRight.prototype.dynSelectRightNames,
                       editable: true
                 },

                 {ref: ZaGrant.A_deny,  type: _CHECKBOX_ , label: com_zimbra_delegatedadmin.Col_deny ,
                   visibilityChecks:[],  
                   labelLocation:_RIGHT_, trueValue:"1", falseValue:"0" }
              ]
            }
        ]
    };
    return xFormObject;
}

ZaGrantDialog.setGranteeChanged = function (value, event, form) {
	var oldVal = this.getInstanceValue();
	if(oldVal == value)
		return;

	this.setInstanceValue(value);


    if(AjxUtil.EMAIL_FULL_RE.test(value)) {
	    //update Grantee Type
        form.parent.updateGranteeType (value) ;
    } else {
		this.setError(ZaMsg.RES_ErrorInvalidContactEmail);
		var event = new DwtXFormsEvent(form, this, value);
		form.notifyListeners(DwtEvent.XFORMS_VALUE_ERROR, event);
		return;
	} 
}

ZaGrantDialog.prototype.updateGranteeType = function (grantee) {
    try {
        var params = new Object();

        query = "(|" +
                "(" + ZaAccount.A_mail +"=" + grantee + ")" + //for account
                "(" + ZaAccount.A_zimbraMailAlias + "=" + grantee + ")" + //for dl
                ")" ;
        dataCallback = new AjxCallback(this, this.setGranteeType);
        params.types = [ZaSearch.ACCOUNTS, ZaSearch.DLS];
        params.callback = dataCallback;
        params.query = query ;
        params.controller = ZaApp.getInstance().getCurrentController();
        ZaSearch.searchDirectory(params);
    } catch (ex) {
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaGrantDialog.updateGranteeType");
    }
}

ZaGrantDialog.prototype.setGranteeType = function (resp) {
    try {
        if(!resp) {
            throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaGrantDialog.setGranteeType"));
        }
        if(resp.isException()) {
            throw(resp.getException());
        } else {
            var response = resp.getResponse().Body.SearchDirectoryResponse;
            var list = new ZaItemList(null);
            list.loadFromJS(response);
            var grantee = list.getArray() ;
            if (grantee.length != 1) {
                //either grantee doesn't exist or not unique.
            }else{
                var type = grantee[0].type ;
                var granteeType = "";
                if (type == ZaItem.ACCOUNT) {
                    granteeType = "usr" ;                     
                }else if (type == ZaItem.DL){
                    granteeType = "grp" ;
                }
                this._localXForm.setInstanceValue(granteeType, ZaGrant.A_grantee_type) ;
                this._localXForm.getItemsById (ZaGrant.A_grantee_type)[0].updateElement (granteeType);
            }
        }
    } catch (ex) {
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectDataCallback");
    }
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

ZaGrantDialog.grantGlobalGrant = function () {
    //add grant

    if(this.grantRightDlg) {
		var obj = this.grantRightDlg.getObject();

        //this.parent.setDirty(true);
        //GrantRights Right here, instead of populating to the account modification saving time
        // Advantages: 1. Avoid the double grants during the saving time
        // 2. reduce the load of the server during the account modification time
        if (ZaGrant.grantMethod (obj)) {
            //TODO: test if the grant exists in the current list already
            this.fireCreationEvent(obj);
            this.grantRightDlg.popdown();
        }
    }
    // update the global grant list


    
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