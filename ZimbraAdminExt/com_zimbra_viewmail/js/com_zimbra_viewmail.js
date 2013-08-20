/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
/**
 * Created by IntelliJ IDEA.
 * User: qinan
 * Date: 8/19/11
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
if(ZaSettings && ZaSettings.EnabledZimlet["com_zimbra_viewmail"]){
ZaAccountViewMail = function () {}

/*ZaAccountViewMail.initExtraToolbarButton = function () {

    this._toolbarOperations[ZaOperation.VIEW_MAIL] = new ZaOperation(ZaOperation.VIEW_MAIL,
        com_zimbra_viewmail.ACTBB_ViewMail, com_zimbra_viewmail.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailbox",
        new AjxListener(this, ZaAccountViewMail._viewMailListener));

    if (!this._toolbarOrder) {
        this._toolbarOrder == [];
    }
    this._toolbarOrder.push (ZaOperation.VIEW_MAIL)  ;
}
*/


ZaAccountViewMail.initExtraPopupButton = function () {
    this._popupOperations[ZaOperation.VIEW_MAIL] = new ZaOperation(ZaOperation.VIEW_MAIL,
        com_zimbra_viewmail.ACTBB_ViewMail, com_zimbra_viewmail.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailbox",
        new AjxListener(this, ZaAccountViewMail._viewMailListener));
}

if (ZaController.initPopupMenuMethods["ZaAccountListController"]) {
    ZaController.initPopupMenuMethods["ZaAccountListController"].push(ZaAccountViewMail.initExtraPopupButton);
}

if (ZaController.initPopupMenuMethods["ZaSearchListController"]) {
    ZaController.initPopupMenuMethods["ZaSearchListController"].push(ZaAccountViewMail.initExtraPopupButton);
}

if (ZaController.initPopupMenuMethods["ZaAccountViewController"]) {
    ZaController.initPopupMenuMethods["ZaAccountViewController"].push(ZaAccountViewMail.initExtraPopupButton);
}

if (ZaController.initPopupMenuMethods["ZaDLController"]) {
    ZaController.initPopupMenuMethods["ZaDLController"].push(ZaAccountViewMail.initExtraPopupButton);
}

if (ZaController.initPopupMenuMethods["ZaResourceController"]) {
    ZaController.initPopupMenuMethods["ZaResourceController"].push(ZaAccountViewMail.initExtraPopupButton);
}

ZaAccountViewMail._viewMailListenerLauncher = ZaAccountListController._viewMailListenerLauncher;

ZaAccountViewMail._viewMailListener =
function(ev) {
	try {
		var account = null;
		if (this instanceof ZaAccountListController || this instanceof ZaSearchListController){
			var accounts = this._contentView.getSelection();
			if(!accounts || accounts.length<=0) {
				return;
			}
			account = accounts[0];

		} else if (this instanceof ZaAccountViewController || this instanceof ZaDLController || this instanceof ZaResourceController){
			account = this._currentObject;
		} else {
			return;
		}
		if (account){
			ZaAccountViewMail._viewMailListenerLauncher.call(this, account);
		}

	} catch (ex) {
		this._handleException(ex, "ZaAccountViewMail._viewMailListener", null, false);
	}
}

ZaAccountViewMail.changeActionsStateMethod =
function () {
    var cnt, item;
    if (this instanceof ZaAccountListController || this instanceof ZaSearchListController){
        item = this._contentView.getSelection()[0];
        cnt = this._contentView.getSelectionCount();
    } else if (this instanceof ZaAccountViewController || this instanceof ZaDLController || this instanceof ZaResourceController){
        item = this._currentObject;
        cnt = 1;
    }else {
        return;
    }

    if (cnt == 1) {
        if (item) {

            if ((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.DL)) {
                if(!item.targetObj)
                    item.targetObj = item.getAliasTargetObj();

                var enable = (item.targetObj.attrs[ZaDistributionList.A_mailStatus] == "enabled");
                if(this._popupOperations[ZaOperation.VIEW_MAIL])
                    this._popupOperations[ZaOperation.VIEW_MAIL].enabled = enable;

            }
            else if (item.type == ZaItem.DL) {
                var enable = (item.attrs[ZaDistributionList.A_mailStatus] == "enabled");
                if(this._popupOperations[ZaOperation.VIEW_MAIL])
                    this._popupOperations[ZaOperation.VIEW_MAIL].enabled = enable;

            }
				else if (item.type == ZaItem.ACCOUNT) {
				var enable = false;
				if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.rights)) {
					item.loadEffectiveRights("id", item.id, false);
				}
                if(!enable) {
					if(!ZaItem.hasRight(ZaAccount.VIEW_MAIL_RIGHT,item)) {
						 if(this._popupOperations[ZaOperation.VIEW_MAIL])
						 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
					}
                }
            } else if ((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.ACCOUNT))  {
				if(!item.targetObj)
					item.targetObj = item.getAliasTargetObj() ;

				var enable = false;
				if (ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.targetObj.rights)) {
					item.targetObj.loadEffectiveRights("id", item.id, false);
				}
				if(!enable) {
					if(!ZaItem.hasRight(ZaAccount.VIEW_MAIL_RIGHT,item.targetObj)) {
						 if(this._popupOperations[ZaOperation.VIEW_MAIL])
						 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
					}
                }
            } else if ((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.RESOURCE))  {
				if(!item.targetObj)
					item.targetObj = item.getAliasTargetObj() ;

				var enable = false;
				if (ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.targetObj.rights)) {
					item.targetObj.loadEffectiveRights("id", item.id, false);
				}
                if(!enable) {
                    if(!ZaItem.hasRight(ZaResource.VIEW_RESOURCE_MAIL_RIGHT,item.targetObj)) {
                         if(this._popupOperations[ZaOperation.VIEW_MAIL])
                            this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
                    }
                }
            } else if(item.type == ZaItem.RESOURCE) {
				var enable = false;
				if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.rights)) {
					item.loadEffectiveRights("id", item.id, false);
				}
				if(!enable) {
					if(!ZaItem.hasRight(ZaResource.VIEW_RESOURCE_MAIL_RIGHT,item)) {
						 if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
						 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
						 }

					}
                }
            } else if(item.type == ZaItem.DOMAIN || item.type == ZaItem.COS){
               if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
					this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
               }
            }

        } else {
			if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
				this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
			}
        }
    } else {
		if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
			this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}
    }
}
if(ZaController.changeActionsStateMethods["ZaAccountListController"]) {
    ZaController.changeActionsStateMethods["ZaAccountListController"].push(ZaAccountViewMail.changeActionsStateMethod);
}
if(ZaController.changeActionsStateMethods["ZaSearchListController"]) {
    ZaController.changeActionsStateMethods["ZaSearchListController"].push(ZaAccountViewMail.changeActionsStateMethod);
}

if(ZaController.changeActionsStateMethods["ZaAccountViewController"]) {
    ZaController.changeActionsStateMethods["ZaAccountViewController"].push(ZaAccountViewMail.changeActionsStateMethod);
}
if(ZaController.changeActionsStateMethods["ZaDLController"]) {
    ZaController.changeActionsStateMethods["ZaDLController"].push(ZaAccountViewMail.changeActionsStateMethod);
}
if(ZaController.changeActionsStateMethods["ZaResourceController"]) {
    ZaController.changeActionsStateMethods["ZaResourceController"].push(ZaAccountViewMail.changeActionsStateMethod);
}


}
