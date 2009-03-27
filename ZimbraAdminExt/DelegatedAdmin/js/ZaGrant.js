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

ZaGrant.GRANTEE_TYPE = ["usr", "grp"] ;

ZaGrant.RIGHT_TYPE_CHOICES =[
    {value:"system", label:com_zimbra_delegatedadmin.Col_system_right},
    {value:"inline", label:com_zimbra_delegatedadmin.Col_inline_right}
];

ZaGrant.INLINE_VERB_TYPE_CHOICES = [
    {value:"set", label:com_zimbra_delegatedadmin.Col_inline_verb_set},
    {value:"get", label:com_zimbra_delegatedadmin.Col_inline_verb_get}
]

ZaGrant.getGlobalGrantsList = function () {
    var list = new ZaItemRightList(ZaRight);
	list._vector = AjxVector.fromArray (ZaGrant.loadMethod (null, null, "global")) ;
	return list;
}

ZaGrant.loadMethod = function (by, val, type) {
    var soapDoc = AjxSoapDoc.create("GetGrantsRequest", ZaZimbraAdmin.URN, null);
    if (!type) type = this.type ;
    
    var elTarget ;
    if (type == ZaItem.GLOBAL_GRANT || type == ZaItem.GLOBAL_CONFIG)  {
        elTarget = soapDoc.set(ZaGrant.A_target, "") ;
    } else {
        elTarget = soapDoc.set(ZaGrant.A_target, val) ;
        elTarget.setAttribute ("by", by) ;
    }
    elTarget.setAttribute ("type", type ) ;

    var ctler =  ZaApp.getInstance().getCurrentController();
    
    try {
        var params = new Object();
        params.soapDoc = soapDoc;
        var reqMgrParams = {
            controller: ctler ,
            busyMsg: com_zimbra_delegatedadmin.BUSY_GET_GRANTS
        } ;

        var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetGrantsResponse ;
        var grants = resp.grant ;
        var grantList = [] ;
        if (grants != null) {
            for (var i = 0; i < grants.length; i++) {
                var grant = new ZaGrant () ;

                /*if (by == "id") {
                    grant [ZaGrant.A_target_id] = val ;                    
                } else if (by == "name") {
                    grant [ZaGrant.A_target] = val ;
                } else if (type == "global") {
                    grant [ZaGrant.A_target] = "global" ;                    
                } */

                if (type == ZaItem.GLOBAL_GRANT || type == ZaItem.GLOBAL_CONFIG) {
                    grant [ZaGrant.A_target] = type ;
                } else {
                    grant [ZaGrant.A_target] = this.name ;
                    grant [ZaGrant.A_target_id] = this.id ;
                }
                grant [ZaGrant.A_target_type] = type ;
                
                for (var key in grants[i]) {
                    if (key == "deny") {
                        grant [ZaGrant.A_deny] = grants[i][key] ? "1" : "0" ;
                    } else if (key == ZaGrant.A_canDelegate) {
                        grant [ZaGrant.A_canDelegate] = grants[i][key] ? "1" : "0" ;
                    } else if (key == "name") {
                        grant [ZaGrant.A_grantee] = grants[i][key] ;
                    } else if (key == "type") {
                        grant [ZaGrant.A_grantee_type] = grants[i][key] ;
                    } else if (key == "right") {
                        grant [ZaGrant.A_right] = grants[i][key] ;
                    } else if (key == "id") {
                        grant [ZaGrant.A_grantee_id] = grants[i][key] ;
                    }
                }
                grantList.push (grant) ;
            }
        }

        if (type == "global") {
            return grantList ;
        } else {
            this [ZaGrant.A2_grantsList] = grantList ;
        }
    }catch (ex) {
        if (ctler) { //at the initialization time, the controller may not be initialized yet
            ctler.popupErrorDialog (com_zimbra_delegatedadmin.error_grant_right + ex.msg , ex) ;
        }
    }
}

//GrantRightRequest, triggered by the GrantDialog actions
ZaGrant.grantMethod = function (obj) {
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
        var reqMgrParams = {
            controller: ctler ,
            busyMsg: com_zimbra_delegatedadmin.BUSY_GRANTING_RIGHT
        } ;

        resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GrantRightResponse ;

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
        {id: ZaGrant.A_canDelegate, type:_ENUM_, ref: ZaGrant.A_canDelegate, choices:ZaModel.BOOLEAN_CHOICES2 },
        {id: ZaGrant.A_target_type, ref: ZaGrant.A_target_type, type: _STRING_, choices: ZaZimbraRights.targetType },
        {id: ZaGrant.A_target, type:_STRING_, ref: ZaGrant.A_target, required: true } ,
        {id: ZaGrant.A_right_type, type:_ENUM_, ref: ZaGrant.A_right_type, required:true, choices: ZaGrant.RIGHT_TYPE_CHOICES },

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
        overviewPanelController._rightsTi = new DwtTreeItem(overviewPanelController._configTi);
        overviewPanelController._rightsTi.setText(com_zimbra_delegatedadmin.OVP_global_grants);
        overviewPanelController._rightsTi.setImage("GlobalPermission"); //TODO: Use Grants icons
		overviewPanelController._rightsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW);

        if(ZaOverviewPanelController.overviewTreeListeners) {
            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW] = ZaGrant.globalGrantsListTreeListener;
        }
    }
}


