/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
/**
 *
 * @param parent
 * @param app
 * @param title
 * @param by - either by target or by grantee
 *             if by target, target name and type are not modifiable
 *             if by grantee, grantee name and type are not modifiable
 */
ZaGrantDialog = function(parent,  app, title, by) {
    if (arguments.length == 0) return;
    this._standardButtons = [ DwtDialog.CANCEL_BUTTON];
    var helpButton = new DwtDialog_ButtonDescriptor(ZaXWizardDialog.HELP_BUTTON, ZaMsg.TBB_Help,
            DwtDialog.ALIGN_LEFT, new AjxCallback(this, this._helpButtonListener));
    var addMoreButton = new DwtDialog_ButtonDescriptor(ZaGrantDialog.ADD_MORE_BUTTON , com_zimbra_delegatedadmin.btAddMore,
                DwtDialog.ALIGN_RIGHT, null);
    var addFinishButton = new DwtDialog_ButtonDescriptor(ZaGrantDialog.ADD_FINISH_BUTTON, com_zimbra_delegatedadmin.btAddFinish,
            DwtDialog.ALIGN_RIGHT, null);
    this._extraButtons = [helpButton,addMoreButton,addFinishButton];

    ZaXDialog.call(this, parent,null,  title, "400px", "200px");
    if (!by) by = ZaGrant.A_target ;
    this.by = by;
    this._containedObject = {};

    this.systemRightsChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
    this.granteeNameChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "name", "name");

    this.initForm(ZaGrant.myXModel, this.getMyXForm());
}

ZaGrantDialog.prototype = new ZaXDialog;
ZaGrantDialog.prototype.constructor = ZaGrantDialog;

ZaGrantDialog.ADD_FINISH_BUTTON = ++DwtDialog.LAST_BUTTON;
ZaGrantDialog.ADD_MORE_BUTTON = ++DwtDialog.LAST_BUTTON;

ZaGrantDialog.prototype.getMyXForm =
function() {
    var xFormObject = {
        numCols:1,
        items:[
             {type:_GROUP_,isTabGroup:true, numCols:2, colSizes: [165, "*"], items: [ //allows tab key iteration
               { type: _SPACER_ },

                 {type: _GROUP_, colSpan: "*", numCols:2, colSizes: [165, "*"],
                     visibilityChecks:["this.getForm().parent.by == 'target'"],
                     items : [
                       { ref: ZaGrant.A_target, type: _OUTPUT_ ,
                           label: com_zimbra_delegatedadmin.Label_target_name },
                      /*
                       { ref: ZaGrant.A_target_type, type:_OUTPUT_,
                           label: com_zimbra_delegatedadmin.Label_target_type   },
                        */
                       // make it type _DYNSELECT_
                       { ref: ZaGrant.A_grantee, type: _DYNSELECT_, label: com_zimbra_delegatedadmin.Label_grantee_name ,
                           visibilityChecks:[],labelLocation:_LEFT_ ,
                           emptyText:com_zimbra_delegatedadmin.searchTermGrantee,
                           choices: this.granteeNameChoices,
                           onChange: ZaGrantDialog.setGranteeChanged,
                           dataFetcherClass:ZaSearch ,
                           dataFetcherMethod:ZaSearch.prototype.dynSelectGrantees,
                           editable: true
                       }/*,

                       { ref: ZaGrant.A_grantee_type, type:_TEXTFIELD_, label: com_zimbra_delegatedadmin.Label_grantee_type ,
                            visibilityChecks:[], //temporary solution to make this element visible
                            enableDisableChecks:false,bmolsnr:true,
        //                    enableDisableChangeEventSources:[ZaGrant.A_right_type],
                            labelLocation:_LEFT_
                       }, */
                     ]
                 },

                 {type: _GROUP_, colSpan: "*", numCols:2, colSizes: [165, "*"],
                    visibilityChecks:["this.getForm().parent.by == 'grantee'"],
                    items: [
                         { ref: ZaGrant.A_grantee, type: _OUTPUT_ ,
                            label: com_zimbra_delegatedadmin.Label_grantee_name },
                        { ref: ZaGrant.A_grantee_type, type:_OUTPUT_,
                            label: com_zimbra_delegatedadmin.Label_grantee_type   },

                       { ref: ZaGrant.A_target_type, type: _OSELECT1_, choices: ZaZimbraRights.targetType,
                           label: com_zimbra_delegatedadmin.Label_target_type ,
                           visibilityChecks:[]
                       },
                       { ref: ZaGrant.A_target, type: _TEXTFIELD_,
                           label: com_zimbra_delegatedadmin.Label_target_name ,
                           visibilityChecks:[]
                       }
                   ]
                 },

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
                       emptyText:com_zimbra_delegatedadmin.searchTermRight,
                       choices: this.systemRightsChoices, 
//                       inputPreProcessor:ZaGrantDialog.preProcessRightNames,
                       dataFetcherClass:ZaRight ,
                       dataFetcherMethod:ZaRight.prototype.dynSelectRightNames,
                       editable: true
                 },

                 {ref: ZaGrant.A_deny,  type: _CHECKBOX_ , label: com_zimbra_delegatedadmin.Col_deny ,
                   visibilityChecks:[], bmol: true, 
                   labelLocation:_RIGHT_, trueValue:"1", falseValue:"0" } ,
                 {ref: ZaGrant.A_canDelegate,  type: _CHECKBOX_ , label: com_zimbra_delegatedadmin.Col_can_grant ,
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


    if((value.lastIndexOf ("@")==value.indexOf ("@")) && (value.indexOf ("@")>0)) {
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
                //this._localXForm.getItemsById (ZaGrant.A_grantee_type)[0].updateElement (granteeType);
            }
        }
    } catch (ex) {
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectDataCallback");
    }
}

ZaGrantDialog.grantRightMethod = function () {
     if(this.parent.grantRightDlg) {
		var obj = this.parent.grantRightDlg.getObject();
        var instance = this.getInstance();
        var currentGrantList = instance [ZaGrant.A2_grantsList] || [];
        //TODO: add an auto-dim message or diable all the fields

        //this.parent.setDirty(true);
        //GrantRights Right here, instead of populating to the account modification saving time
        // Advantages: 1. Avoid the double grants during the saving time
        // 2. reduce the load of the server during the account modification time
        if (ZaGrant.grantMethod (obj)) {
            //TODO: test if the grant exists in the current list already
            currentGrantList.push(ZaUtil.deepCloneObject (obj)) ;
            this.getModel().setInstanceValue(this.getInstance(), ZaGrant.A2_grantsList, currentGrantList);
            return true ;
        }
    }

    return false ;
}

ZaGrantDialog.grantRight = function () {
    if (ZaGrantDialog.grantRightMethod.call (this)){
        this.parent.grantRightDlg.popdown();
    }    
}

ZaGrantDialog.grantMoreRight = function () {
   if (ZaGrantDialog.grantRightMethod.call (this)){
       var dialog = this.parent.grantRightDlg ;
       var obj = dialog.getObject() ;
       obj [ZaGrant.A_right] = "" ; 
       dialog.setObject (obj) ;
   }
}

ZaGrantDialog.grantGlobalGrantMethod = function () {
    if(this.grantRightDlg) {
		var obj = this.grantRightDlg.getObject();

        //this.parent.setDirty(true);
        //GrantRights Right here, instead of populating to the account modification saving time
        // Advantages: 1. Avoid the double grants during the saving time
        // 2. reduce the load of the server during the account modification time
        if (ZaGrant.grantMethod (obj)) {
            //TODO: test if the grant exists in the current list already
            this.fireCreationEvent(ZaUtil.deepCloneObject (obj));
            return true;
        }
    }

    return false ;
}


ZaGrantDialog.grantGlobalGrant = function () {
    if (ZaGrantDialog.grantGlobalGrantMethod.call (this)){
        this.grantRightDlg.popdown();
    }
}

ZaGrantDialog.grantMoreGlobalGrant = function () {
    if (ZaGrantDialog.grantGlobalGrantMethod.call (this)){
       var dialog = this.grantRightDlg ;
       var obj = dialog.getObject() ;
       obj [ZaGrant.A_right] = "" ;
       dialog.setObject (obj) ;
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