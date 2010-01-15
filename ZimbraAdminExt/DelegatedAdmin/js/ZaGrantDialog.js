/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
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
ZaGrantDialog = function(parent,  app, title, by, isEditDialog) {
    if (arguments.length == 0) return;
    this._standardButtons = [ DwtDialog.CANCEL_BUTTON];
    var helpButton = new DwtDialog_ButtonDescriptor(ZaXWizardDialog.HELP_BUTTON, ZaMsg.TBB_Help,
            DwtDialog.ALIGN_LEFT, new AjxCallback(this, this._helpButtonListener));
    var addMoreButton = new DwtDialog_ButtonDescriptor(ZaGrantDialog.ADD_MORE_BUTTON , com_zimbra_delegatedadmin.btAddMore,
                DwtDialog.ALIGN_RIGHT, null);
    var addFinishButton = new DwtDialog_ButtonDescriptor(ZaGrantDialog.ADD_FINISH_BUTTON, com_zimbra_delegatedadmin.btAddFinish,
            DwtDialog.ALIGN_RIGHT, null);

    var editFinishButton = new DwtDialog_ButtonDescriptor(ZaGrantDialog.EDIT_FINISH_BUTTON, com_zimbra_delegatedadmin.btEditFinish,
            DwtDialog.ALIGN_RIGHT, null);

    if (isEditDialog) {
         this._extraButtons = [helpButton, editFinishButton];
    } else {
        this._extraButtons = [helpButton,addMoreButton,addFinishButton];
    }

    ZaXDialog.call(this, parent,null,  title, "500px", "200px");
    if (!by) by = ZaGrant.A_target ;
    this.by = by;

    if (this.by == ZaGrant.A_target) {
        this.grantListPropertyName = ZaGrant.A2_grantsList ;  //for target permission grants list view
    } else if (this.by == ZaGrant.A_grantee) {
        this.grantListPropertyName = ZaGrant.A3_directGrantsList ;    //for AllGrantsView
    }

    this._containedObject = {};

    this.systemRightsChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
    this.granteeNameChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "name", "name");

    this.initForm(ZaGrant.myXModel, this.getMyXForm());
}

ZaGrantDialog.prototype = new ZaXDialog;
ZaGrantDialog.prototype.constructor = ZaGrantDialog;

ZaGrantDialog.ADD_FINISH_BUTTON = ++DwtDialog.LAST_BUTTON;
ZaGrantDialog.ADD_MORE_BUTTON = ++DwtDialog.LAST_BUTTON;
ZaGrantDialog.EDIT_FINISH_BUTTON = ++DwtDialog.LAST_BUTTON;


ZaGrantDialog.prototype.getMyXForm =
function() {
    var xFormObject = {
        numCols:1,
        items:[
             {type:_GROUP_,isTabGroup:true, numCols:2, colSizes: [150, "*"], items: [ //allows tab key iteration
               { type: _SPACER_ },

                 {type: _GROUP_, colSpan: "*", numCols:2, colSizes: [150, "*"],
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
                           visibilityChecks:[],labelLocation:_LEFT_ ,  inputSize: 50,
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

                 {type: _GROUP_, colSpan: "*", numCols:2, colSizes: [150, "*"],
                    visibilityChecks:["this.getForm().parent.by == 'grantee'"],
                    items: [
                         { ref: ZaGrant.A_grantee, type: _OUTPUT_ ,
                            label: com_zimbra_delegatedadmin.Label_grantee_name },
                       /*
                        { ref: ZaGrant.A_grantee_type, type:_OUTPUT_,
                            label: com_zimbra_delegatedadmin.Label_grantee_type   },
                         */
                       { ref: ZaGrant.A_target_type, type: _OSELECT1_, choices: ZaZimbraRights.targetType,
                           label: com_zimbra_delegatedadmin.Label_target_type ,
                           onChange: ZaGrantDialog.setTargetTypeChanged ,
                           visibilityChecks:[]
                       },
                       { ref: ZaGrant.A_target, type: _TEXTFIELD_,   width:250,
                           label: com_zimbra_delegatedadmin.Label_target_name ,
                           enableDisableChecks:[ZaGrantDialog.targetTypeListener],
                           enableDisableChangeEventSources:[ZaGrant.A_target_type],
                           visibilityChecks:[]
                       }
                   ]
                 },

               { ref: ZaGrant.A_right_type, type: _OSELECT1_, label: com_zimbra_delegatedadmin.Label_right_type,
                   visibilityChecks:[],
                   labelLocation: _LEFT_, choices: ZaGrant.RIGHT_TYPE_CHOICES
               },
               { type: _GROUP_, colSpan:"*", numCols:2, colSizes: [150, "*"],
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
                            width:250,
                           type: _TEXTFIELD_, label: com_zimbra_delegatedadmin.Label_inline_attr }
                   ]
               },
               {ref: ZaGrant.A_right, id: ZaGrant.A_right, type: _DYNSELECT_, label: com_zimbra_delegatedadmin.Label_right_name,
                         visibilityChecks:[],
    //                   visibilityChecks: [[ZaGrantDialog.rightTypeListener, "system"]],
    //                   visibilityChangeEventSources: [ZaGrant.A_right_type] ,
                       enableDisableChecks:[[ZaGrantDialog.rightTypeListener, "system"]],
                       enableDisableChangeEventSources:[ZaGrant.A_right_type],
                       labelLocation:_LEFT_ ,  inputSize: 50,
                       emptyText:com_zimbra_delegatedadmin.searchTermRight,
                       choices: this.systemRightsChoices, 
//                       inputPreProcessor:ZaGrantDialog.preProcessRightNames,
                       dataFetcherClass:ZaRight ,
                       dataFetcherMethod:ZaRight.prototype.dynSelectRightNames,
                       editable: true
                 },
                 {ref: ZaGrant.A_allow, id: ZaGrant.A_allow,  type: _RADIO_ , label: com_zimbra_delegatedadmin.Col_allow ,
                     groupname: "radio_grp_deny_delegate" ,
                     onChange: ZaGrantDialog.changeDenyAllow ,
                     visibilityChecks:[],  //bmol: true ,
                     labelLocation:_RIGHT_ /*, trueValue:"1", falseValue:"0" */},
                 {type: _GROUP_, colSpan: "*", numCols: 3, colSizes: [150, 20,  "*"],
                     items: [
                         {type: _CELL_SPACER_ },    
                         {ref: ZaGrant.A_canDelegate, id: ZaGrant.A_canDelegate,  type: _CHECKBOX_ ,
                             label: com_zimbra_delegatedadmin.Col_can_grant ,
                             visibilityChangeEventSources:[ZaGrant.A_allow] ,
                             visibilityChecks:[[XForm.checkInstanceValue,ZaGrant.A_allow,"1"]],
                             labelLocation:_RIGHT_, trueValue:"1", falseValue:"0" }
                      ]
                 },
                 {ref: ZaGrant.A_deny, id: ZaGrant.A_deny, type: _RADIO_ , label: com_zimbra_delegatedadmin.Col_deny ,
                     groupname: "radio_grp_deny_delegate" ,
                     onChange: ZaGrantDialog.changeDenyAllow ,
                     visibilityChecks:[], //bmol: true ,
                     labelLocation:_RIGHT_ /*, trueValue:"1", falseValue:"0"*/ }

                 /*
                 {ref: ZaGrant.A_deny,  type: _CHECKBOX_ , label: com_zimbra_delegatedadmin.Col_deny ,
                   visibilityChecks:[], bmol: true, 
                   labelLocation:_RIGHT_, trueValue:"1", falseValue:"0" } ,
                 {ref: ZaGrant.A_canDelegate,  type: _CHECKBOX_ , label: com_zimbra_delegatedadmin.Col_can_grant ,
                    visibilityChecks:[],
                    labelLocation:_RIGHT_, trueValue:"1", falseValue:"0" } */
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

ZaGrantDialog.setTargetTypeChanged = function (value, event, form) {
	var oldVal = this.getInstanceValue();
	if(oldVal == value)
		return;

	this.setInstanceValue(value);
    var targetName ;
    if (value == ZaItem.GLOBAL_CONFIG) {
       targetName = ZaGrant.GLOBAL_CONFIG_TARGET_NAME
    } else if (value == ZaItem.GLOBAL_GRANT)  {
       targetName = ZaGrant.GLOBAL_TARGET_NAME ;
    } else {
        targetName = "" ;
    }
    
    this.setInstanceValue (targetName, ZaGrant.A_target) ;
    var targetElement = this.getForm().getItemsById (ZaGrant.A_target) ;
    for (var i = 0; i < targetElement.length ; i ++) {
        if (targetElement[i].getIsVisible) {  //there are two elements refer to ZaGrant.A_target, we only update the visible one.
            targetElement[i].updateElement (targetName) ;
        }
    }
    
//    this.getForm().getItemsById (ZaGrant.A_target)[0].updateElement (targetName);
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

/**
 *
 * @param parent: ZaGlobalGrantListViewController for global grants, form for other grants
 * @param isMore
 * @param isGlobalGrant
 */
ZaGrantDialog.prototype.grantRightMethod = function (parent, isMore, isGlobalGrant) {
    var obj = this.getObject();
    var args = {
       newGrant: obj,
       isGlobalGrant: isGlobalGrant,
       isMoreGrants: isMore ,
       parent: parent 
    } ;

    //get the currentGrantList
    var currentGrantList ;
    if (isGlobalGrant) {
        currentGrantList = parent.getList().getArray () ;
    } else {
        //parent is the xfrom ,
        var instance = parent.getInstance();
        currentGrantList = instance [this.grantListPropertyName] || [];
    }
    args.grantList = currentGrantList ;

    //GrantRights Right here, instead of populating to the account modification saving time
    // Advantages: 1. Avoid the double grants during the saving time
    // 2. reduce the load of the server during the account modification time
     if ( this.isGrantExists(args) < 0) {
        if (ZaGrant.grantMethod (obj)) {
            if (isGlobalGrant) {
                parent.fireCreationEvent(ZaUtil.deepCloneObject (obj));
            } else {
                currentGrantList.push(ZaUtil.deepCloneObject (obj)) ;
                parent.getModel().setInstanceValue(instance, this.grantListPropertyName, currentGrantList);

                parent.getModel().setInstanceValue(instance,
                    ZaGrant.A2_grantStatus, com_zimbra_delegatedadmin.GrantStatus_Grant);
                parent.setInstanceValue ("TRUE", ZaGrant.A2_showGrantStatus) ;

                //need to refresh the form to show the status change ? why the change event is not triggerred ?
                parent.refresh ();
                parent.setInstanceValue ("FALSE", ZaGrant.A2_showGrantStatus) ;  //the status will be hidden on next refresh
            }
            return true ;
        }
    }
}

ZaGrantDialog.prototype.grantRight = function (parent, isMore, isGlobalGrant) {
    if (this.grantRightMethod(parent, isMore, isGlobalGrant)){
        if (isMore) {
           var obj = this.getObject() ;
           obj [ZaGrant.A_right] = "" ;
           this.setObject (obj) ;
           this.refresh () ;
        }else{
            this.popdown();
        }
    }    
}

//the method for the "Edit and Finish" button in edit grant dialog.
//It requires revoke the old grant first
ZaGrantDialog.prototype.editRightAndFinish = function (parent, selectedGrant, isGlobalGrant) {
      var args = {
          isEditAndFinish: true ,
          parent: parent,
          isGlobalGrant: isGlobalGrant
      } ;

      if (isGlobalGrant) {
        args.grantList = parent.getList().getArray () ;
      } else {
        args.grantList = parent.getInstance() [this.grantListPropertyName] ;
      }

      args.newGrant = this.getObject () ;
    
      if(!ZaApp.getInstance().dialogs["EditGrantConfirmDialog"]) {
          ZaApp.getInstance().dialogs["EditGrantConfirmDialog"] = new ZaMsgDialog(
                  ZaApp.getInstance().getAppCtxt().getShell(), null,
                  [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON]);
      }
      ZaApp.getInstance().dialogs["EditGrantConfirmDialog"].registerCallback(
              DwtDialog.YES_BUTTON, ZaGrantDialog.prototype.editRightAndFinishCallback,
              this, [parent, selectedGrant, args]);

      var confirmMsg =  com_zimbra_delegatedadmin.confirm_edit_grants  + "<br /><br />"
          + com_zimbra_delegatedadmin.confirm_edit_grants_existing_acl
          + ZaTargetPermission.getDlMsgFromGrant([selectedGrant])
          + com_zimbra_delegatedadmin.confirm_edit_grants_new_acl
          + ZaTargetPermission.getDlMsgFromGrant([args.newGrant]) ;
      ZaApp.getInstance().dialogs["EditGrantConfirmDialog"].setMessage (confirmMsg,  DwtMessageDialog.INFO_STYLE) ;
      ZaApp.getInstance().dialogs["EditGrantConfirmDialog"].popup ();
}

ZaGrantDialog.prototype.editRightAndFinishCallback = function  (parent, selectedGrant, args) {
    var currentGrantList = args.grantList ;
    var isGlobalGrant = args.isGlobalGrant ;
    var newGrant = args.newGrant ;

     //1.revoke the edit target
    if (ZaGrant.revokeMethod (selectedGrant)) {
        if (isGlobalGrant) {
             parent.fireRemovalEvent (selectedGrant) ;
        } else {
            for (var j = 0; j < currentGrantList.length; j ++) {
                if (selectedGrant == currentGrantList[j] ) {
                    currentGrantList.splice(j, 1) ;
                }
            }
//            currentGrantList.splice(currentGrantIndex, 1) ;
            parent.getModel().setInstanceValue(parent.getInstance(), this.grantListPropertyName, currentGrantList);

        }

        //2. popdown the informational dialog
        ZaApp.getInstance().dialogs["EditGrantConfirmDialog"].popdown ();

        //3. add the new target
        if ( this.isGrantExists (args) < 0) {
            if (ZaGrant.grantMethod (newGrant)) {
                if (args.isGlobalGrant) {
                    parent.fireCreationEvent(ZaUtil.deepCloneObject (newGrant));
                } else {
                    currentGrantList.push(ZaUtil.deepCloneObject (newGrant)) ;
                    parent.getModel().setInstanceValue(parent.getInstance(), this.grantListPropertyName, currentGrantList);
                }
            }
        }

        //4. update status message and popdown the edit dialog
        if (!isGlobalGrant) {
            parent.setInstanceValue( com_zimbra_delegatedadmin.GrantStatus_Update, ZaGrant.A2_grantStatus);
            parent.setInstanceValue ("TRUE", ZaGrant.A2_showGrantStatus) ;
            //need to refresh the form to show the status change ? why the change event is not triggerred ?
            parent.refresh ();
            parent.setInstanceValue ("FALSE", ZaGrant.A2_showGrantStatus) ;  //the status will be hidden on next refresh
        }
        this.popdown ();
    }
}

/**
 *
 * @param args : {
 *         grantList - currentGrantList
 *         currentGrantIndex - index of the grant to be revoked/edited
 *         newGrant - new grant object
 *         isMoreGrants - is more grants ? -> decide if we want to popdown the add grants dialog
 *         isGlobalGrant - is global grants ? -> decide if it is a global grants, so we can have differnt methods to refresh the list and popdown the dialog.
 *         isEditAndFinish - is it triggered by the edit and finish button of the Edit Dailog 
          }
 */
ZaGrantDialog.prototype.editRightMethod = function (args) {

    var currentGrantList = args.grantList ;
    var currentGrantIndex = args.currentGrantIndex  ;
    var newGrant = args.newGrant ;
    var isMoreGrants = args.isMoreGrants ;
    var isGlobalGrant = args.isGlobalGrant  ;
    var isEditFinish = args.isEditAndFinish ;
    var parent = args.parent ;

    if (ZaGrant.revokeMethod (currentGrantList[currentGrantIndex])){
        if (isGlobalGrant) {
             parent.fireRemovalEvent (currentGrantList[currentGrantIndex]) ;
        } else {
            currentGrantList.splice(currentGrantIndex, 1) ;
            parent.getModel().setInstanceValue(parent.getInstance(), this.grantListPropertyName, currentGrantList);
        }
        if (ZaGrant.grantMethod (newGrant)) {
            if (isGlobalGrant) {
                parent.fireCreationEvent(ZaUtil.deepCloneObject (newGrant));
            }else{
                currentGrantList.push(ZaUtil.deepCloneObject (newGrant)) ;
                parent.getModel().setInstanceValue(parent.getInstance(), this.grantListPropertyName, currentGrantList);
            }
        }
    }

    ZaApp.getInstance().dialogs["EditGrantConfirmDialog"].popdown ();

    if (isMoreGrants) {
        var obj = this.getObject() ;
        obj [ZaGrant.A_right] = "" ;
        this.setObject (obj) ;
        this.refresh();
    } else {
        this.popdown () ;
    }
}


ZaGrantDialog.rightTypeListener =  function (type) {
    var rightType = this.getInstanceValue(ZaGrant.A_right_type) ;
    return (rightType == type) ;
}

ZaGrantDialog.targetTypeListener =  function () {
    var targetType = this.getInstanceValue(ZaGrant.A_target_type) ;
    var enableTargetField = true ;
    if (targetType == null || targetType == ZaItem.GLOBAL_GRANT || targetType == ZaItem.GLOBAL_CONFIG) {
        enableTargetField = false ;
    }
    return enableTargetField ;    
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

ZaGrantDialog.getInlineRightAttrsByName = function (inlineRightName) {

    var arr = inlineRightName.split(".") ;
    if (arr.length == 3) {
        var inlineAttrs = {} ;
        inlineAttrs[ZaGrant.A_inline_verb] = arr [0] ;
        inlineAttrs[ZaGrant.A_inline_target_type] = arr [1] ;
        inlineAttrs[ZaGrant.A_inline_attr] = arr [2] ;
    } else {
        ZaApp.getInstance().getCurrentController().popupErrorDialog(
                    com_zimbra_delegatedadmin.error_invalid_inline_right)
    }

    return inlineAttrs ;
}


/*
  @param: {
            grantList: the current grant list ,
            newGrant: the new grant obj in the grant dialog
            isGlobalGrant: whether it is for the global grant
            isMoreGrants: whether it is for more grants
            isEditAndFinish : whether it is from the "edit and finish" button from edit right dialog
         } ;

  @return : -1 -- doesn't exist
            >=0 -- exist
 */
ZaGrantDialog.prototype.isGrantExists = function (args) {
    var currentGrantList = args.grantList ;
    var obj = args.newGrant ;
    for (var i = 0; i < currentGrantList.length; i ++ ) {
        var cGrant = currentGrantList[i] ;
        var compKeys = [ZaGrant.A_grantee, ZaGrant.A_grantee_type,
                       ZaGrant.A_target, ZaGrant.A_target_type,
                       ZaGrant.A_right ] ;
        var isExist = i ;
        for (var j =0; j < compKeys.length; j ++) {
            var k = compKeys[j] ;
            var cv =  cGrant[k] ;
            var v = obj[k] ;

           if (cv != v) {
                isExist = -1 ;
                break ;
            }
        }

        if (isExist >= 0) {
             var isDelegateDenyChange = false ;
            //check if changing the delegate/deny attr
            var compKeys = [ZaGrant.A_canDelegate, ZaGrant.A_deny] ;
            for (var j =0; j < compKeys.length; j ++) {
                var k = compKeys[j] ;
                var cv =  cGrant[k] ;
                var v = obj[k] ;

                var cpositive = (cv == "1") ;
                var opositive = (v == "1") ;
                if (cpositive != opositive) {
                    isDelegateDenyChange  = true ;
                    break ;
                }
            }
            if (isDelegateDenyChange) {
                //popup confirm deny/delegated attr change
                if(!ZaApp.getInstance().dialogs["EditGrantConfirmDialog"]) {
                    ZaApp.getInstance().dialogs["EditGrantConfirmDialog"] = new ZaMsgDialog(
                            ZaApp.getInstance().getAppCtxt().getShell(), null,
                            [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON]);
                }
                args.currentGrantIndex = i ;
                ZaApp.getInstance().dialogs["EditGrantConfirmDialog"].registerCallback(
                        DwtDialog.YES_BUTTON, ZaGrantDialog.prototype.editRightMethod, this, [args]);

                var confirmMsg =  com_zimbra_delegatedadmin.confirm_edit_grants  + "<br /><br />"
                    + com_zimbra_delegatedadmin.confirm_edit_grants_existing_acl
                    + ZaTargetPermission.getDlMsgFromGrant([currentGrantList[i]])
                    + com_zimbra_delegatedadmin.confirm_edit_grants_new_acl
                    + ZaTargetPermission.getDlMsgFromGrant([obj]) ;
                ZaApp.getInstance().dialogs["EditGrantConfirmDialog"].setMessage (confirmMsg,  DwtMessageDialog.INFO_STYLE) ;
                ZaApp.getInstance().dialogs["EditGrantConfirmDialog"].popup ();

                return i ;
            } else {
                //popup information dialog
                var msgDialog = ZaApp.getInstance ()._appCtxt.getMsgDialog () ;
                msgDialog.setMessage(com_zimbra_delegatedadmin.grant_exist_msg
                    + ZaTargetPermission.getDlMsgFromGrant([obj])) ;
                msgDialog.popup () ;
                return isExist ;
            }
        }
    }

    return -1 ; //doesn't exist at all
}

ZaGrantDialog.changeDenyAllow = function (value, event, form) {
    var ref = this.getRef () ;
//    console.log (ref + "=" + value) ;
    //set the instance value
    if (ref == ZaGrant.A_allow) {
        this.setInstanceValue ("1") ;
        this.setInstanceValue ("0", ZaGrant.A_deny) ;
    } else if (ref == ZaGrant.A_deny) {
        this.setInstanceValue ("1") ;
        this.setInstanceValue ("0", ZaGrant.A_allow) ;
        this.setInstanceValue ("0", ZaGrant.A_canDelegate) ;
    }
}

//this function is used to make sure the deny/allow radio group can be displayed properly,
// both at the initialization time and edit time
ZaGrantDialog.prototype.refresh = function () {
    var form = this._localXForm ;
    form.refresh () ; //radio button was not properly set after the refresh, so we refresh here first
    var instance = form.getInstance () ;

    var isDeny = instance [ZaGrant.A_deny] ;
    var canDelegated = instance [ZaGrant.A_canDelegate] ;

    if (isDeny == "1") {
        var denyItem = form.getItemsById(ZaGrant.A_deny) [0] ;
        denyItem.getElement().checked = true ;
        form.setInstanceValue ("0", ZaGrant.A_allow) ;
    } else { //it is allow if not deny
        var allowItem = form.getItemsById(ZaGrant.A_allow) [0] ;
        allowItem.getElement().checked = true ;
        form.setInstanceValue ("1", ZaGrant.A_allow) ;
    }
}

