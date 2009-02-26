/**
 * Contains most of the domain admin functions
 */

ZaDelegatedAdmin = function () {};

if (ZaAccount) {
    ZaAccount.A_zimbraIsAdminAccount = "zimbraIsAdminAccount" ;
    ZaAccount.myXModel.items.push (
        {id:ZaAccount.A_zimbraIsAdminAccount, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES,
            ref:"attrs/"+ZaAccount.A_zimbraIsAdminAccount}) ;
}


if (ZaTabView.XFormModifiers["ZaAccountXFormView"]) {
   ZaDelegatedAdmin.AccountXFormModifier = function (xFormObject) {

       var adminChkBox = {
            ref:ZaAccount.A_zimbraIsAdminAccount,type:_CHECKBOX_,
            label:ZaMsg.NAD_IsAdmin,
            trueValue:"TRUE", falseValue:"FALSE"
        };
        var tabs = xFormObject.items[2].items;
        var tmpItems = tabs[0].items;
        var cnt = tmpItems.length;
        for(var i = 0; i < cnt; i ++) {
           if(tmpItems[i].id == "account_form_setup_group" && tmpItems[i].items) {
               var tmpGrouperItems = tmpItems[i].items;
               var cnt2 = tmpGrouperItems.length;
               for(var j=0;j<cnt2;j++) {
                   if(tmpGrouperItems[j] && tmpGrouperItems[j].ref == ZaAccount.A_zimbraIsSystemAdminAccount) {
                       //add  Admin checkbox
                       xFormObject.items[2].items[0].items[i].items.splice(j+1,0, adminChkBox);
                       break;
                   }
               }
               break;
           }
       }
   }

    ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaDelegatedAdmin.AccountXFormModifier);
}

if (ZaDistributionList) {
    ZaDistributionList.A_isAdminGroup = "zimbraIsAdminGroup" ;
    ZaDistributionList.myXModel.items.push (
        {id:ZaDistributionList.A_isAdminGroup, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES,
            ref:"attrs/"+ZaDistributionList.A_isAdminGroup}) ;
}


if (ZaTabView.XFormModifiers["ZaDLXFormView"]) {
   ZaDelegatedAdmin.DLXFormModifier = function (xFormObject) {
       /*this item is to be added in the permission view
            var adminGroupChkBx =
                {type:_GROUP_, numCols:2,colSpan: "*", colSizes:["20px","*"],
                    cssStyle:"margin-top:10px;margin-left: 10px; margin-right:auto;",
                    items: [
                        {
                            ref: ZaDistributionList.A_isAdminGroup,type:_CHECKBOX_,
                            label:com_zimbra_delegatedadmin.NAD_IsAdminGroup,
                            enableDisableChecks:[],
                            visibilityChecks:[],
                            trueValue:"TRUE", falseValue:"FALSE"
                        }
                    ]
                }; */
       var adminGroupChkBx =
            {
                ref: ZaDistributionList.A_isAdminGroup,type:_CHECKBOX_,
                label:com_zimbra_delegatedadmin.NAD_IsAdminGroup,
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






