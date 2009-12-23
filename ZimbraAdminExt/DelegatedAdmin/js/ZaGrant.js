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
ZaGrant = function () {
    ZaItem.call(this, "ZaGrant") ;
    this._init () ;
    this.type = ZaItem.GRANT ;

}

ZaGrant.prototype = new ZaItem ;
ZaGrant.prototype.constructor = ZaGrant ;

ZaItem.loadMethods ["ZaGrant"] = [] ;
ZaItem.initMethods["ZaGrant"] = [] ;

ZaGrant.A_id = "id" ;
ZaGrant.A_by = "by" ;
ZaGrant.A_grantee = "grantee" ;
ZaGrant.A_grantee_id = "grantee_id" ;
ZaGrant.A_grantee_type = "grantee_type" ;
ZaGrant.A_right = "right" ;
ZaGrant.A_deny = "deny" ;
ZaGrant.A_allow = "allow" ;
ZaGrant.A_canDelegate = "canDelegate";
ZaGrant.A_target = "target" ;
ZaGrant.A_target_id = "target_id" ;
ZaGrant.A_target_type = "target_type"
ZaGrant.A_right_type = "right_type" ;
ZaGrant.A_inline_right = "inline_right" ;
ZaGrant.A_inline_verb = "verb" ;
ZaGrant.A_inline_target_type = "inline_target_type" ;
ZaGrant.A_inline_attr = "inline_attr" ;

ZaGrant.A2_grantsList = "grantsList" ;
ZaGrant.A2_grantsListSelectedItems = "grantsListSelectedItems" ;

//ZaGrant.A3_directGrantsList = "directGrantsList" ;
ZaGrant.A3_directGrantsList = ZaGrant.A2_grantsList ;     //grantsList is the directList, use the same name to avoid the extra programming work for new Admin wizard and config grants view.
ZaGrant.A3_indirectGrantsList = "indirectGrantsList" ;

ZaGrant.GRANTEE_TYPE = { usr: "usr", grp: "grp" };
ZaGrant.GRANTEE_TYPE_CHOICES = ["usr", "grp"] ;

ZaGrant.RIGHT_TYPE_CHOICES =[
    {value:"system", label:com_zimbra_delegatedadmin.Col_system_right},
    {value:"inline", label:com_zimbra_delegatedadmin.Col_inline_right}
];

ZaGrant.GLOBAL_TARGET_NAME = "globalacltarget" ;
ZaGrant.GLOBAL_CONFIG_TARGET_NAME = "globalconfig";

ZaGrant.INLINE_VERB_TYPE_CHOICES = [
    {value:"set", label:com_zimbra_delegatedadmin.Col_inline_verb_set},
    {value:"get", label:com_zimbra_delegatedadmin.Col_inline_verb_get}
]

ZaGrant.getGlobalGrantsList = function () {
    var list = new ZaItemRightList(ZaRight);
	list._vector = AjxVector.fromArray (ZaGrant.loadMethod (null, null, "global")) ;
	return list;
}

/** Used to load all the grants on a target */
ZaGrant.loadMethod = function (by, val, type) {
    var params = { target: {} };
    if (!type) type = this.type ;

    if (type == ZaItem.GLOBAL_GRANT || type == ZaItem.GLOBAL_CONFIG)  {
        params.target.val =  "" ;
    } else {
        if (val == null || val.length <= 0)  {
            //this might be a new DL, no need to load the ACL on the new DL
            return ;
        }
        params.target.val = val ;
        params.target.by = by ;
    }
    params.target.type =  type  ;
    return ZaGrant.load.call (this, params) ;
}
/*                                  
  params.isAllGrants - whether it is a call to get all the granted ACE of an grantee on all the targets 

  params.target - targets info
  params.target.by - name or id
  params.target.val - target name or id
  params.target.type - target type

  params.grantee - grantee info
  parmas.grantee.by - name or id
  params.grantee.val - grantee name or id
  params.grantee.all -
    whether to include grants granted to groups the specified grantee belongs to.
      1: include (default)
      0: do not include
  params.grantee.type - grantee type    
 */
ZaGrant.load = function (params) {
    var soapDoc = AjxSoapDoc.create("GetGrantsRequest", ZaZimbraAdmin.URN, null);
    var elTarget, elGrantee ; 

    if (params.target != null) {
        if ((params.target.type == ZaItem.GLOBAL_GRANT)
                || (params.target.type == ZaItem.GLOBAL_CONFIG))  {
            elTarget = soapDoc.set(ZaGrant.A_target, "") ;
        } else {
            elTarget = soapDoc.set(ZaGrant.A_target, params.target.val) ;
            elTarget.setAttribute ("by", params.target.by) ;
        }
        elTarget.setAttribute ("type", params.target.type ) ;
    }

    if (params.grantee != null) {
        elGrantee = soapDoc.set(ZaGrant.A_grantee, params.grantee.val) ;
        elGrantee.setAttribute ("by", params.grantee.by) ;
        elGrantee.setAttribute ("type", params.grantee.type ) ;

        if (params.grantee.all != null) {
            elGrantee.setAttribute ("all", params.grantee.all ) ;            
        }
    }
    
    var ctler =  ZaApp.getInstance().getCurrentController();
    try {
        var reqParams = new Object();
        reqParams.soapDoc = soapDoc;
        var reqMgrParams = {
            controller: ctler ,
            busyMsg: com_zimbra_delegatedadmin.BUSY_GET_GRANTS
        } ;

        var resp = ZaRequestMgr.invoke(reqParams, reqMgrParams).Body.GetGrantsResponse ;
        var grants = resp.grant ;
        var grantList = [] ;
        if (grants != null) {
            for (var i = 0; i < grants.length; i++) {
                var grant = new ZaGrant () ;
                var grantTarget = grants[i].target[0] ; 
                grant [ZaGrant.A_target] = grantTarget.name ;
                grant [ZaGrant.A_target_id] = grantTarget.id ;
                grant [ZaGrant.A_target_type] = grantTarget.type ;

                var grantRight = grants[i].right [0] ;

                grant [ZaGrant.A_deny] = grantRight[ZaGrant.A_deny] ? "1" : "0" ;
                grant [ZaGrant.A_allow] = grantRight[ZaGrant.A_deny] ? "0" : "1" ;
                grant [ZaGrant.A_canDelegate] = grantRight[ZaGrant.A_canDelegate] ? "1" : "0" ;
                grant [ZaGrant.A_right] = grantRight["_content"] ;

                var grantGrantee = grants[i].grantee [0] ;
                grant [ZaGrant.A_grantee] = grantGrantee ["name"] ;
                grant [ZaGrant.A_grantee_type] = grantGrantee ["type"] ;
                grant [ZaGrant.A_grantee_id] = grantGrantee.id ;
                
                grantList.push (grant) ;
            }
        }

        if (( params.target && params.target.type == ZaItem.GLOBAL_GRANT) || params.isAllGrants) {
            return grantList ;
        } else {
            this [ZaGrant.A2_grantsList] = grantList ;
        }
    }catch (ex) {
        if (ctler) { //at the initialization time, the controller may not be initialized yet
            ctler.popupErrorDialog (com_zimbra_delegatedadmin.error_get_permission + ex.msg , ex) ;
        }
    }
}

/**
 *
 * @param rightsArr
 * @param params
 *          {
               rightsArr:
               currentIndex :
               showStatus : 1 (show all status, no matter succeed or failed)
                            -1 (only show status in the end when there are errors)
                            0 (don't show status at all)
               resultObj: {
                    msg: [],
                    status: -1 (error), 0 (OK)
               }
            }
 * @param callback
 */
ZaGrant.assignMultiGrants = function (params) {

    if (params.resultObj == null){
        params.resultObj = {
            msg: [],
            status: 0 
        } ;
    }
    
    if (params.rightsArr == null || params.rightsArr.length <= 0) {
        params.resultObj.msg.push (com_zimbra_delegatedadmin.no_rights_to_assign) ;
    } else {
        if (params.currentIndex == null || params.currentIndex < 0) {
            params.currentIndex = 0; 
        }
        if (params.currentIndex < params.rightsArr.length) {
            var grant = params.rightsArr [params.currentIndex] ;
            var callback = new AjxCallback (this, ZaGrant.assignMultiGrantsCallback, [params]) ;

            params.resultObj.msg.push(AjxMessageFormat.format(com_zimbra_delegatedadmin.msg_proposed_grants_start,
                            [ZaNewAdminWizard.getProposedGrantMsg(grant, params)]));
            ZaGrant.grantMethod (grant, callback) ;
        } else {
            params.resultObj.msg.push (com_zimbra_delegatedadmin.msg_proposed_grants_done) ;
        }
    }

    //show status

    if (!ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"]){
        ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell());
    }
    var statusDialog = ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"];
    statusDialog.setSize (400, 100);

    if (params.showStatus == 1) {
        if (!statusDialog.isPoppedUp ()) statusDialog.popup () ;
        statusDialog.setMessage (params.resultObj.msg.join(""));
    } else if ((params.showStatus == -1) && (params.currentIndex >= params.rightsArr.length)){
        if (params.resultObj.status < 0) { //there is error
            if (!statusDialog.isPoppedUp ()) statusDialog.popup () ;
            statusDialog.setMessage (params.resultObj.msg.join(""));
        }
    }

}

ZaGrant.assignMultiGrantsCallback = function (params, resp) {
    if (!resp || resp.isException()) {
        params.resultObj.status = -1; 
        params.resultObj.msg.push ("<font color='red'>" + com_zimbra_delegatedadmin.msg_proposed_grants_failed + "</font>") ;
    } else {
        params.resultObj.msg.push (com_zimbra_delegatedadmin.msg_proposed_grants_granted) ;
    }

    params.currentIndex ++ ;
    ZaGrant.assignMultiGrants.call (this, params) ;
    /*
    if (params.currentIndex >= this._containedObject[ZaNewAdmin.A_proposedGrantsList].length) {
        params.resultObj.msg.push (com_zimbra_delegatedadmin.msg_proposed_grants_done) ;
        ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"].setMessage (params.resultObj.msg.join(""));
    } else {
        ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"].setMessage (params.resultObj.msg.join(""));
        this.configProposedGrants(params) ;
    } */

}

ZaGrant.getGranteeTypeByItemType = function (objType) {
    var type = null ;
    if (objType != null) {
        if (objType == ZaItem.ACCOUNT) {
            type = ZaGrant.GRANTEE_TYPE.usr;
        } else if (objType == ZaItem.DL) {
            type = ZaGrant.GRANTEE_TYPE.grp;
        }
    }

    return type ;
}

//GrantRightRequest, triggered by the GrantDialog actions
ZaGrant.grantMethod = function (obj, callback) {
    if (AjxEnv.hasFirebug)
      console.log("Grant Rights ...") ;
//    var tempObj = ZaApp.getInstance().getCurrentController ()._view.GetObject ();
//    var tempGrants = tempObj [ZaGrant.A2_grantsList]  ;
    var soapDoc = AjxSoapDoc.create("GrantRightRequest", ZaZimbraAdmin.URN, null);
    var elTarget = soapDoc.set(ZaGrant.A_target, obj[ZaGrant.A_target]) ;
    elTarget.setAttribute("by", "name") ;
    elTarget.setAttribute("type", obj[ZaGrant.A_target_type]) ;

    var elGrantee = soapDoc.set(ZaGrant.A_grantee, obj[ZaGrant.A_grantee]) ;
    elGrantee.setAttribute("by", "name") ;
    elGrantee.setAttribute("type",obj[ZaGrant.A_grantee_type] ) ;

    var elRight = soapDoc.set("right", obj[ZaGrant.A_right])
    if (obj[ZaGrant.A_deny] == "1") {
        elRight.setAttribute("deny", "1") ;   
    }
    if (obj[ZaGrant.A_canDelegate] == "1") {
        elRight.setAttribute(ZaGrant.A_canDelegate, "1") ;   
    }
    var ctler =  ZaApp.getInstance().getCurrentController();
    try {
        var params = new Object();
        params.soapDoc = soapDoc;
        if (callback) {
            params.asyncMode = true;
		    params.callback = callback;
        }
        var reqMgrParams = {
            controller: ctler ,
            busyMsg: com_zimbra_delegatedadmin.BUSY_GRANTING_RIGHT
        } ;

        ZaRequestMgr.invoke(params, reqMgrParams);

        return true ;
    }catch (ex) {
        ctler.popupErrorDialog (com_zimbra_delegatedadmin.error_grant_right + " " + ex.msg , ex) ;
        return false ;
    }
}

//RevokeRightRequest
/**
 *
 * @param target - target information
 * @param obj - grant informaiton
 */
ZaGrant.revokeMethod = function (obj) {
    if (AjxEnv.hasFirebug)
      console.log("Revoke Rights ...") ;
    var soapDoc = AjxSoapDoc.create("RevokeRightRequest", ZaZimbraAdmin.URN, null);

    var targetType = obj [ZaGrant.A_target_type] ;
    if (targetType == ZaItem.GLOBAL_GRANT || targetType == ZaItem.GLOBAL_CONFIG) {
        var elTarget = soapDoc.set(ZaGrant.A_target, "") ;
    } else if (targetType == ZaItem.ZIMLET){
        var elTarget = soapDoc.set(ZaGrant.A_target, obj[ZaGrant.A_target]) ;
        elTarget.setAttribute("by", "name") ;
    } else {
        var elTarget ;
        if (obj[ZaGrant.A_target_id]) {
            elTarget = soapDoc.set(ZaGrant.A_target, obj[ZaGrant.A_target_id]) ;
            elTarget.setAttribute("by", "id") ;
        } else if (obj[ZaGrant.A_target]) {
            elTarget = soapDoc.set(ZaGrant.A_target, obj[ZaGrant.A_target]) ;
            elTarget.setAttribute("by", "name") ;
        }
    }
    elTarget.setAttribute("type", targetType) ;

    var elGrantee ;
    if (obj[ZaGrant.A_grantee_id]) {
        elGrantee = soapDoc.set(ZaGrant.A_grantee, obj[ZaGrant.A_grantee_id]) ;
        elGrantee.setAttribute("by", "id") ;
    }else if (obj[ZaGrant.A_grantee]) { //there is no grantee id when we just granted a right
        elGrantee = soapDoc.set(ZaGrant.A_grantee, obj[ZaGrant.A_grantee]) ;
        elGrantee.setAttribute("by", "name") ;
    }
    elGrantee.setAttribute("type",obj[ZaGrant.A_grantee_type] ) ;

    var elRight = soapDoc.set("right", obj[ZaGrant.A_right])
    if (obj[ZaGrant.A_deny] == "1") {
        elRight.setAttribute("deny", "1") ;
    }

    if (obj[ZaGrant.A_canDelegate] == "1") {
        elRight.setAttribute(ZaGrant.A_canDelegate, "1") ;
    }
    var ctler =  ZaApp.getInstance().getCurrentController();
    try {
        var params = new Object();
        params.soapDoc = soapDoc;
        var reqMgrParams = {
            controller: ctler ,
            busyMsg: com_zimbra_delegatedadmin.BUSY_DELETE_RIGHT
        } ;

        resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.RevokeRightResponse ;

        return true ;
    }catch (ex) {
        ctler.popupErrorDialog (com_zimbra_delegatedadmin.error_revoke_right + " "+ ex.msg , ex) ;
        return false ;
    }  
}


ZaGrant.myXModel = {
	items: [
        {id: ZaGrant.A_grantee, type: _EMAIL_ADDRESS_, ref: ZaGrant.A_grantee, required: true },
        {id: ZaGrant.A_grantee_id, type: _STRING_, ref: ZaGrant.A_grantee_id },
        {id: ZaGrant.A_grantee_type, type:_STRING_, ref:  ZaGrant.A_grantee_type, required: true, choices: ZaGrant.GRANT_TYPE},
        {id: ZaGrant.A_right, type: _STRING_, ref:  ZaGrant.A_right, required: true },
        {id: ZaGrant.A_deny, type:_ENUM_, ref: ZaGrant.A_deny, choices:ZaModel.BOOLEAN_CHOICES2 },
        {id: ZaGrant.A_allow, type:_ENUM_, ref: ZaGrant.A_allow, choices:ZaModel.BOOLEAN_CHOICES2 },
        {id: ZaGrant.A_canDelegate, type:_ENUM_, ref: ZaGrant.A_canDelegate, choices:ZaModel.BOOLEAN_CHOICES2 },
        {id: ZaGrant.A_target_type, ref: ZaGrant.A_target_type, type: _STRING_, choices: ZaZimbraRights.targetType },
        {id: ZaGrant.A_target, type:_STRING_, ref: ZaGrant.A_target, required: true } ,
        {id: ZaGrant.A_right_type, type:_ENUM_, ref: ZaGrant.A_right_type, required:true, choices: ZaGrant.RIGHT_TYPE_CHOICES },
        {id: ZaGrant.A2_grantsList, type:_LIST_, ref: ZaGrant.A2_grantsList},
        {id: ZaGrant.A2_grantsListSelectedItems, type:_LIST_, ref: ZaGrant.A2_grantsListSelectedItems},
        {id: ZaGrant.A_inline_right, ref: ZaGrant.A_inline_right, type: _OBJECT_, items: [
                {id: ZaGrant.A_inline_verb, type: _STRING_, ref: ZaGrant.A_inline_verb, choices: ZaGrant.INLINE_VERB_TYPE_CHOICES},
                {id: ZaGrant.A_inline_target_type, type: _STRING_, ref: ZaGrant.A_inline_target_type, choices: ZaZimbraRights.inlineTargetType},
                {id: ZaGrant.A_inline_attr, type: _STRING_, ref: ZaGrant.A_inline_attr }
            ]
        }
    ]
};
           

ZaGrant.globalGrantsListTreeListener = function (ev) {
	if (AjxEnv.hasFirebug) console.log("Show the global grants lists ...") ;
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(
			ZaApp.getInstance().getGlobalGrantListController(),
                ZaGlobalGrantListViewController.prototype.show,
                ZaGrant.getGlobalGrantsList());
	} else {
		ZaApp.getInstance().getGlobalGrantListController().show(
                ZaGrant.getGlobalGrantsList());
	}
}

ZaGrant.grantsOvTreeModifier = function (tree) {
    var overviewPanelController = this ;
    if (!overviewPanelController) throw new Exception("ZaGrant.grantsOvTreeModifier: Overview Panel Controller is not set.");

    if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_PERMISSION_VIEW]
            || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        overviewPanelController._rightsTi = new DwtTreeItem({parent:overviewPanelController._configTi,className:"AdminTreeItem"});
        overviewPanelController._rightsTi.setText(com_zimbra_delegatedadmin.OVP_global_grants);
        overviewPanelController._rightsTi.setImage("GlobalPermission"); //TODO: Use Grants icons
		overviewPanelController._rightsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW);

        if(ZaOverviewPanelController.overviewTreeListeners) {
            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW] = ZaGrant.globalGrantsListTreeListener;
        }
    }
}


