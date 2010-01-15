/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
ZaAllGrantsViewController = function (appCtxt, container) {
    ZaXFormViewController.call(this, appCtxt, container, "ZaAllGrantsViewController");
    this._UICreated = false;
    this._helpURL = ZaAllGrantsViewController.helpURL;

    this.tabConstructor = ZaAllGrantsXFormView;

    this.addCreationListener(new AjxListener(this, this.handleGrantCreation));
    this.addRemovalListener(new AjxListener(this, this.handleGrantRemoval));
}

ZaAllGrantsViewController.prototype = new ZaXFormViewController();
ZaAllGrantsViewController.prototype.constructor = ZaAllGrantsViewController;
ZaAllGrantsViewController.helpURL = location.pathname + ZaUtil.HELP_URL + "TODO_View_All_Effective_Rigthts.html?locid=" + AjxEnv.DEFAULT_LOCALE;

ZaController.setViewMethods["ZaAllGrantsViewController"] = new Array();
ZaController.initToolbarMethods["ZaAllGrantsViewController"] = new Array();

/**
 *    @method show
 *    @param entry - isntance of ZaAccount class
 *    @param skipRefresh - forces to skip entry.refresh() call.
 *           When getting account from an alias the account is retreived from the server using ZaAccount.load()
 *            so there is no need to refresh it.
 */

ZaAllGrantsViewController.prototype.show =
function(entry, openInNewTab, skipRefresh) {
    if ( !this.selectExistingTabByItemId(entry[ZaGrant.A_grantee_id])) {
        this._setView(entry, openInNewTab, skipRefresh);
    }
}

ZaAllGrantsViewController.initToolbarMethod =
function () {

    this._toolbarOrder.push(ZaOperation.NEW);
    this._toolbarOrder.push(ZaOperation.EDIT);
    this._toolbarOrder.push(ZaOperation.DELETE);
    this._toolbarOrder.push(ZaOperation.SEP);
    this._toolbarOrder.push(ZaOperation.CLOSE);

    this._toolbarOperations[ZaOperation.NEW] = new ZaOperation(ZaOperation.NEW,
            com_zimbra_delegatedadmin.Bt_grant, com_zimbra_delegatedadmin.Grant_New_tt,
            "Permission", "PermissionDis",
            new AjxListener(this, this.addGrantsListener));

    this._toolbarOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT,
            ZaMsg.TBB_Edit,com_zimbra_delegatedadmin.Grant_Edit_tt,
            "Properties", "PropertiesDis",
            new AjxListener(this, this.editGrantsListener));

    this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE,
            com_zimbra_delegatedadmin.Bt_revoke, com_zimbra_delegatedadmin.Grant_Delete_tt,
    "Delete", "DeleteDis",
            new AjxListener(this, this.deleteGrantsListener));
   
    this._toolbarOperations[ZaOperation.CLOSE] = new ZaOperation(ZaOperation.CLOSE,
            ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "Close", "CloseDis",
            new AjxListener(this, this.closeButtonListener));

    this._toolbarOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);

}
ZaController.initToolbarMethods["ZaAllGrantsViewController"].push(ZaAllGrantsViewController.initToolbarMethod);

ZaAllGrantsViewController.prototype.editGrantsListener = function () {
    var form = this._contentView._localXForm ;
    var directGrantsListViewItem = form.getItemsById (ZaGrant.A3_directGrantsList) [0] ;

    var selectedGrants = directGrantsListViewItem.getSelection();
    if (selectedGrants && selectedGrants.length == 1) {
        var item = selectedGrants [0] ;
        if(!this.editRigthDlg) {
            this.editRigthDlg = new ZaGrantDialog (
                    ZaApp.getInstance().getAppCtxt().getShell(),
                    ZaApp.getInstance(), com_zimbra_delegatedadmin.Title_edit_rights,
                    ZaGrant.A_grantee, true);
        }

        this.editRigthDlg.registerCallback(ZaGrantDialog.EDIT_FINISH_BUTTON,
                ZaGrantDialog.prototype.editRightAndFinish, this.editRigthDlg,
                [form, item, false]);

        var obj = ZaUtil.deepCloneObject (item, ["_evtMgr"]);
        if (obj[ZaGrant.A_right].indexOf("get.") == 0 || obj[ZaGrant.A_right].indexOf("set.")== 0) {
            obj[ZaGrant.A_right_type] = "inline" ;
            obj [ZaGrant.A_inline_right] = ZaGrantDialog.getInlineRightAttrsByName (obj[ZaGrant.A_right]) ;
        } else { //if it is not "inline", it must be "system"
            obj[ZaGrant.A_right_type] = "system" ;
        }

        this.editRigthDlg.setObject(obj);
        this.editRigthDlg.popup();
        this.editRigthDlg.refresh () ;
    }
}

ZaAllGrantsViewController.prototype.addGrantsListener = function () {
    var newGrant = new ZaGrant();
	if(!this.grantRightDlg) {
		this.grantRightDlg = new ZaGrantDialog (
                ZaApp.getInstance().getAppCtxt().getShell(),
                ZaApp.getInstance(), com_zimbra_delegatedadmin.Title_grant_rights,
                ZaGrant.A_grantee);
	} ;

    this.grantRightDlg.registerCallback(ZaGrantDialog.ADD_FINISH_BUTTON,
                ZaGrantDialog.prototype.grantRight,
                this.grantRightDlg, [this._contentView._localXForm, false]);
        this.grantRightDlg.registerCallback(ZaGrantDialog.ADD_MORE_BUTTON,
               ZaGrantDialog.prototype.grantRight,
                this.grantRightDlg, [this._contentView._localXForm, true]);

	var obj = {};
	obj[ZaGrant.A_grantee] = this._currentObject [ZaGrant.A_grantee];
    obj[ZaGrant.A_grantee_type] = this._currentObject [ZaGrant.A_grantee_type];

    obj.setAttrs = {} ;
    obj.setAttrs.all = true ;
    this.grantRightDlg.setObject(obj);
	this.grantRightDlg.popup();
    this.grantRightDlg.refresh ();
}


ZaAllGrantsViewController.prototype.revokeGrant = function () {
    var form = this._contentView._localXForm ;
    var directGrantsListViewItem = form.getItemsById (ZaGrant.A3_directGrantsList) [0] ;
    var selectedGrants = directGrantsListViewItem.getSelection();
    if (selectedGrants && selectedGrants.length > 0) {
        for (var i = 0; i < selectedGrants.length; i ++) {
// TODO: when multiselection enabled, we need a progress dialog to show the progress
            if (ZaGrant.revokeMethod (selectedGrants[i])) {
// fire the removal event.
                this.fireRemovalEvent (selectedGrants[i]) ;
            } else {
                break ; //jump out if failed.
            }
        }
    }

    this.revokeRightDlg.popdown () ;
}

ZaAllGrantsViewController.prototype.deleteGrantsListener = function () {
    var form = this._contentView._localXForm ;
    var directGrantsListViewItem = form.getItemsById (ZaGrant.A3_directGrantsList) [0] ;

    var selectedGrant = directGrantsListViewItem.getSelection();
    if (selectedGrant && selectedGrant.length >= 1) {
        if(!this.revokeRightDlg) {
            this.revokeRightDlg = new ZaMsgDialog (
                    ZaApp.getInstance().getAppCtxt().getShell(),
                    null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON]);
        }
        this.revokeRightDlg.registerCallback(DwtDialog.YES_BUTTON, this.revokeGrant, this, null);
        var confirmMsg =  com_zimbra_delegatedadmin.confirm_delete_grants + ZaTargetPermission.getDlMsgFromGrant(selectedGrant) ;
        this.revokeRightDlg.setMessage (confirmMsg,  DwtMessageDialog.INFO_STYLE) ;
        this.revokeRightDlg.popup ();
    } else {
        ZaApp.getInstance().getCurrentController().popupMsgDialog (com_zimbra_delegatedadmin.no_grant_selected_msg) ;
    }
}

/**
 *    @method setViewMethod
 *    @param entry - isntance of ZaAccount class
 */
ZaAllGrantsViewController.setViewMethod =
function(entry) {
    try {
        if (!this._UICreated) {
            this._initToolbar();
            //make sure these are last
            this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
            this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
            this._toolbarOrder.push(ZaOperation.NONE);
            this._toolbarOrder.push(ZaOperation.HELP);

            this._toolbar = new ZaToolBar(this._container, this._toolbarOperations, this._toolbarOrder);
            this._editBt = this._toolbar.getButton (ZaOperation.EDIT);
            this._deleteBt = this._toolbar.getButton (ZaOperation.DELETE );
           
            this._contentView = this._view = new this.tabConstructor(this._container, entry);
            var elements = new Object();
            elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
            elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;

            var tabParams = {
                openInNewTab: true,
                tabId: this.getContentViewId()
            }

            ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams);
            this._UICreated = true;
            //associate the controller with the view by viewId
            ZaApp.getInstance()._controllers[this.getContentViewId()] = this;
        }

        ZaApp.getInstance().pushView(this.getContentViewId());

        entry[ZaModel.currentTab] = "1";
        this._currentObject = entry;
        this._view.setObject(entry);


    } catch (ex) {
        this._handleException(ex, "ZaAllGrantsViewController.prototype._setView", null, false);
    }
}
ZaController.setViewMethods["ZaAllGrantsViewController"].push(ZaAllGrantsViewController.setViewMethod);



/**
* @param ev
* This listener is invoked by ZaGrantController or any other controller that can create an ZaGrant object
**/
ZaAllGrantsViewController.prototype.handleGrantCreation =
function (ev) {
	if(ev) {
		//add the new ZaGrant to the controlled list
        var grant = ev.getDetails() ;
		if (grant != null) {
            var directGrantsList = this._currentObject [ZaGrant.A3_directGrantsList] ;
            directGrantsList.push (grant) ;

			if (this._contentView)  {
                var xform = this._contentView._localXForm ;
                xform.setInstanceValue (directGrantsList,ZaGrant.A3_directGrantsList ) ;
            }
		}
	}
}

/**
* @param ev
* This listener is invoked by ZaGrantController or any other controller that can remove an ZaGrant object
**/
ZaAllGrantsViewController.prototype.handleGrantRemoval =
function (ev) {
	if(ev) {
        var grant = ev.getDetails() ;
              if (grant != null) {
              var directGrantsList = this._currentObject [ZaGrant.A3_directGrantsList] ;
              AjxUtil.arrayRemove (directGrantsList, grant) ;
              if (this._contentView)  {
                var xform = this._contentView._localXForm ;
                xform.setInstanceValue (directGrantsList,ZaGrant.A3_directGrantsList ) ;
              }                    
		}
	}
}