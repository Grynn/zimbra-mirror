/**
 * Contains most of the domain admin functions
 */

ZaDelegatedAdmin = function () {};

if (ZaAccount) {
    ZaAccount.A2_adminRoles = "adminRoles" ;
    ZaAccount.adminRolesModelItem = {ref: ZaAccount.A2_adminRoles ,id: ZaAccount.A2_adminRoles,
                                    type: _LIST_, listItem:{type:_EMAIL_ADDRESS_}} ;
    ZaAccount.adminAccountModelItem = {id:ZaAccount.A_zimbraIsDelegatedAdminAccount, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES,
            ref:"attrs/"+ZaAccount.A_zimbraIsDelegatedAdminAccount} ;
    ZaAccount.myXModel.items.push (ZaAccount.adminAccountModelItem) ;

    //admin roles model item
    ZaAccount.myXModel.items.push (ZaAccount.adminRolesModelItem) ;

    ZaAccount.changeAdminRoles = function (value, event, form) {
        var oldVal = this.getInstanceValue();
        if(oldVal == value)
            return;

        this.setInstanceValue(value);

        //check if the value is valid admin group
        var adminGroupChoices = this.getChoices () ;
        if (adminGroupChoices && adminGroupChoices.getChoiceByValue(value) != null) {
             var adminGroupId = adminGroupChoices.getChoiceByValue(value).id ;
        } else {
            this.setError(com_zimbra_delegatedadmin.ERROR_INVALID_ADMIN_ROLE);
            var event = new DwtXFormsEvent(form, this, value);
            form.notifyListeners(DwtEvent.XFORMS_VALUE_ERROR, event);
            return ;
        }
        
         //add this value to the  direct member
        if (!this.getInstance () [ZaAccount.A2_memberOf]) {
            this.getInstance () [ZaAccount.A2_memberOf] = {} ;          
        }

        if (!this.getInstance () [ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList]) {
            this.getInstance () [ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList] = [];   
        }

        var directMemberOfList = this.getInstance () [ZaAccount.A2_memberOf] [ZaAccount.A2_directMemberList] ;
        if (value && value.length > 0 && ZaUtil.findValueInObjArrByPropertyName(directMemberOfList, value, "name") < 0){
            directMemberOfList.push ({
                id: adminGroupId,
                name: value
            }) ;

            form.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_directMemberList, directMemberOfList) ;
        }
    }

    ZaAccount.onAdminRoleRemove = function (index, form) {
        var value = this.getInstanceValue () [index] ;
        var path = this.getRefPath();
		this.getModel().removeRow(this.getInstance(), path, index);
        this.items[index].clearError();

        //update the memberOf instance value
        if (!this.getInstance () [ZaAccount.A2_memberOf]) {
            this.getInstance () [ZaAccount.A2_memberOf] = {} ;
        }

        if (!this.getInstance () [ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList]) {
            this.getInstance () [ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList] = [];
        }
        
        var directMemberOfList = this.getInstance () [ZaAccount.A2_memberOf] [ZaAccount.A2_directMemberList] ;
        var i = ZaUtil.findValueInObjArrByPropertyName(directMemberOfList, value, "name")  ; 
        if (i >= 0){
            directMemberOfList.splice (i, 1) ;
            form.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_directMemberList, directMemberOfList) ;
            if (form.parent.setDirty) form.parent.setDirty (true) ;
        }
    }

    ZaAccount.getAdminChkBoxItem = function () {
        var adminChkBox = {
            ref:ZaAccount.A_zimbraIsDelegatedAdminAccount,type:_CHECKBOX_,
            label:ZaMsg.NAD_IsAdmin,
            bmolsnr:true,
            elementChanged :
            function(elementValue,instanceValue, event) {
                if(elementValue == "TRUE") {
                    var isSystemAdmin = this.getInstanceValue(ZaAccount.A_zimbraIsAdminAccount) ;
                    if ( isSystemAdmin && isSystemAdmin == "TRUE" )
                        this.setInstanceValue("FALSE", ZaAccount.A_zimbraIsAdminAccount);
                }
                    this.getForm().itemChanged(this, elementValue, event);
            },
            trueValue:"TRUE", falseValue:"FALSE"
        };

        return adminChkBox;
    }

    ZaAccount.getAdminRolesItem = function () {
       var adminRoleField = {
            ref: ".", type: _DYNSELECT_ ,
            dataFetcherMethod:ZaSearch.prototype.dynSelectSearchAdminGroups,
            onChange: ZaAccount.changeAdminRoles ,
            emptyText:com_zimbra_delegatedadmin.searchTermAdminGroup,
            enableDisableChecks:[],
            dataFetcherClass:ZaSearch,editable:true
       }

       var adminRolesItem = {
           ref: ZaAccount.A2_adminRoles , type: _REPEAT_,
           label: com_zimbra_delegatedadmin.Label_AssignAdminRole, labelLocation:_LEFT_ ,
           labelCssStyle:"vertical-align: top; padding-top: 3px;",
           align:_LEFT_,
           repeatInstance:"",
           showAddButton:true, showAddOnNextRow:true, addButtonWidth: 50,
           addButtonLabel:com_zimbra_delegatedadmin.NAD_Add,
           showRemoveButton:true , removeButtonWidth: 50, removeButtonLabel:com_zimbra_delegatedadmin.NAD_Remove,
           visibilityChecks:["instance.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount]==\'TRUE\' "],
           visibilityChangeEventSources: [ZaAccount.A_zimbraIsDelegatedAdminAccount] ,
           onRemove:ZaAccount.onAdminRoleRemove,
           items:[adminRoleField]
       }

        return adminRolesItem ;

    }
}

ZaDelegatedAdmin.accountObjectModifer = function () {
    var directMemberOfList = this._containedObject [ZaAccount.A2_memberOf] [ZaAccount.A2_directMemberList] ;
    if (! this._containedObject [ZaAccount.A2_adminRoles]) this._containedObject [ZaAccount.A2_adminRoles] = [];
    
    for (var i = 0; i < directMemberOfList.length; i ++) {
    // TODO: enable it when GetAccountMembershipRequest returns isAdminGroup
              if (directMemberOfList[i][ZaDistributionList.A_isAdminGroup] == "TRUE")
                    this._containedObject [ZaAccount.A2_adminRoles].push (directMemberOfList[i].name) ;
            }
}

if (ZaTabView.ObjectModifiers["ZaAccountXFormView"]){
    ZaTabView.ObjectModifiers["ZaAccountXFormView"].push(ZaDelegatedAdmin.accountObjectModifer) ;
}


if (ZaTabView.XFormModifiers["ZaAccountXFormView"]) {
   ZaDelegatedAdmin.AccountXFormModifier = function (xFormObject) {
       var adminChkBox = ZaAccount.getAdminChkBoxItem ();
       var adminRolesItem = ZaAccount.getAdminRolesItem () ;

        var tabs = xFormObject.items[2].items;
        var tmpItems = tabs[0].items;
        var cnt = tmpItems.length;
        for(var i = 0; i < cnt; i ++) {
           if(tmpItems[i].id == "account_form_setup_group" && tmpItems[i].items) {
               var tmpGrouperItems = tmpItems[i].items;
               var cnt2 = tmpGrouperItems.length;
               for(var j=0;j<cnt2;j++) {
                   if(tmpGrouperItems[j] && tmpGrouperItems[j].ref == ZaAccount.A_zimbraIsAdminAccount) {
                       //add  Admin checkbox
                       xFormObject.items[2].items[0].items[i].items.splice(j+1,0, adminChkBox, adminRolesItem);
                       
                       //add the mutual exclusive action to global admin 
                       tmpGrouperItems[j].elementChanged =
								function(elementValue,instanceValue, event) {
									if(elementValue == "TRUE") {
                                        var isAdmin = this.getInstanceValue(ZaAccount.A_zimbraIsDelegatedAdminAccount) ;
                                        if ( isAdmin && isAdmin == "TRUE" )
                                            this.setInstanceValue("FALSE", ZaAccount.A_zimbraIsDelegatedAdminAccount);
								    }
								    this.getForm().itemChanged(this, elementValue, event);
								};
                       break;
                   }
               }
               break;
           }
       }
   }

    ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaDelegatedAdmin.AccountXFormModifier);
}

//for new account wizard
/*
if (ZaTabView.ObjectModifiers["ZaNewAccountXWizard"]){
    ZaTabView.ObjectModifiers["ZaNewAccountXWizard"].push(ZaDelegatedAdmin.accountObjectModifer) ;
} */


if (ZaXDialog.XFormModifiers["ZaNewAccountXWizard"]) {
   ZaDelegatedAdmin.newAccountModifier = function (xFormObject) {
       var adminChkBox = ZaAccount.getAdminChkBoxItem ();
       var adminRolesItem = ZaAccount.getAdminRolesItem () ;

        var tabs = xFormObject.items[3].items;
        var tmpItems = tabs[0].items;
        var cnt = tmpItems.length;
        for(var i = 0; i < cnt; i ++) {
           if(tmpItems[i].id == "account_wiz_setup_group" && tmpItems[i].items) {
               var tmpGrouperItems = tmpItems[i].items;
               var cnt2 = tmpGrouperItems.length;
               for(var j=0;j<cnt2;j++) {
                   if(tmpGrouperItems[j] && tmpGrouperItems[j].ref == ZaAccount.A_zimbraIsAdminAccount) {
                       //add  Admin checkbox
                      tmpItems[i].items.splice(j+1,0, adminChkBox, adminRolesItem);

                       //add the mutual exclusive action to global admin
                       tmpGrouperItems[j].elementChanged =
								function(elementValue,instanceValue, event) {
									if(elementValue == "TRUE") {
                                        var isAdmin = this.getInstanceValue(ZaAccount.A_zimbraIsDelegatedAdminAccount) ;
                                        if ( isAdmin && isAdmin == "TRUE" )
                                            this.setInstanceValue("FALSE", ZaAccount.A_zimbraIsDelegatedAdminAccount);
								    }
								    this.getForm().itemChanged(this, elementValue, event);
								};
                       break;
                   }
               }
               break;
           }
       }
   }

    ZaXDialog.XFormModifiers["ZaNewAccountXWizard"].push(ZaDelegatedAdmin.newAccountModifier);
}

ZaDelegatedAdmin.accountViewMethod =
function (entry) {
    if (entry.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount]
            && entry.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount] == "TRUE" ) {
        this._view._containedObject[ZaAccount.A2_adminRoles] = [] ;
        //Get the isAdminAccount DLs from the directMemberList
        var allDirectMemberOfs = this._view._containedObject [ZaAccount.A2_memberOf] [ZaAccount.A2_directMemberList] ;
        for (var i = 0; i < allDirectMemberOfs.length; i ++) {
// TODO: enable it when GetAccountMembershipRequest returns isAdminGroup
        if (allDirectMemberOfs[i][ZaDistributionList.A_isAdminGroup] == "TRUE")
                this._view._containedObject[ZaAccount.A2_adminRoles].push (allDirectMemberOfs[i].name) ;
        }

        var xform = this._view._localXForm ;
        var instance  = xform.getInstance ();
        xform.getModel().setInstanceValue(instance,ZaAccount.A2_adminRoles,
                 this._view._containedObject[ZaAccount.A2_adminRoles]);
    }
}

if (ZaController.setViewMethods["ZaAccountViewController"]) {
	ZaController.setViewMethods["ZaAccountViewController"].push(ZaDelegatedAdmin.accountViewMethod);
}

if (ZaDistributionList) {
    ZaDistributionList.myXModel.items.push (
        {id:ZaDistributionList.A_isAdminGroup, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES,
            ref:"attrs/"+ZaDistributionList.A_isAdminGroup}) ;
}
     

if (ZaTabView.XFormModifiers["ZaDLXFormView"]) {
   ZaDelegatedAdmin.DLXFormModifier = function (xFormObject) {
       var adminGroupChkBx =
            {
                ref: ZaDistributionList.A_isAdminGroup,type:_CHECKBOX_,
                label:com_zimbra_delegatedadmin.NAD_IsAdminGroup + ": ",
                labelLocation:_LEFT_,  align:_LEFT_,
				labelCssClass:"xform_label", cssStyle:"padding-left:0px",
                enableDisableChecks:[],
                visibilityChecks:[],
                trueValue:"TRUE", falseValue:"FALSE"
            }  ;
       
       var switchGroupItems ;
        for (var i=0; i < xFormObject.items.length; i ++) {
            if (xFormObject.items[i].type == _SWITCH_) {
                switchGroupItems = xFormObject.items[i].items ;
                break ;
            }
        }

        var membersView, tmpGroup;
        for (var j=0; j < switchGroupItems.length; j ++) {
            if ((switchGroupItems[j].type == _ZATABCASE_ )
                    && (switchGroupItems[j].id == "dl_form_members")) {
                membersView = switchGroupItems[j].items[0].items ;
                for (var m=0; m < membersView.length; m ++) {
                    if (membersView[m].id == "dl_form_members_general_group") {
                        for (var n=0; n < membersView[m].items.length; n ++ ) {
                            if (membersView[m].items[n].ref == "zimbraMailStatus") {
                                membersView[m].items.splice (n,0, adminGroupChkBx) ;
                                break;
                            }
                        }
                        break ;
                    }
                }
                break ;
            }
        }

//        permissionView.items.splice(0, 0, adminGroupChkBx);
   }

   ZaTabView.XFormModifiers["ZaDLXFormView"].push(ZaDelegatedAdmin.DLXFormModifier);

}

if (ZaSearch) {
	/**
 	* @argument callArgs {value, event, callback}
 	*/
    ZaSearch.prototype.dynSelectSearchAdminGroups =  function (callArgs) {
        var extraLdapQuery = "(zimbraIsAdminGroup=TRUE)" ;
		var value = callArgs["value"];
		var event = callArgs["event"];
		var callback = callArgs["callback"];
		        
        ZaSearch.prototype.dynSelectSearchGroups.call (this, {value:value, event:event, callback:callback, extraLdapQuery:extraLdapQuery}) ;
    }
}






