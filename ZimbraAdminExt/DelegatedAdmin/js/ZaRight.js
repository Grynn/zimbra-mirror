/**
 * Rights object:
 *  {
 *      name: AComboRight ,
 *      id: 3ajbkdaksfeekrwklkadfkdslk
 *      attrs: {
 *          name: AComboRight ,
 *          id: 3ajbkdaksfeekrwklkadfkdslk ,
 *          type: combo ,
 *          targetType: [ "cos", "account"] ,
 *          rights: [ CreateAccount, SetPassword ] ,
 *          attrs: [only for  setAttrs, getAttrs]
 *
 *      }
 * }
 *
 *
 *
 *
 */

ZaRight = function() {
	ZaItem.call(this, "ZaRight");
	this._init();
	//The type is required. The application tab uses it to show the right icon
	this.type = ZaItem.RIGHT ;
    this.attrs = {} ; //used to keep the right object properties
}

ZaRight.prototype = new ZaItem;
ZaRight.prototype.constructor = ZaRight;
ZaItem.loadMethods["ZaRight"] = new Array();
ZaItem.initMethods["ZaRight"] = new Array();
ZaItem.modifyMethods["ZaRight"] = new Array();

ZaRight.A_id = "id" ;
ZaRight.A_name = "name" ;
ZaRight.A_desc = "desc" ;
ZaRight.A_attrs = "attrs" ;
//ZaRight.A_getAttrs = "getAttrs" ;
//ZaRight.A_setAttrs = "setAttrs" ;
ZaRight.A_rights = "rights" ;
ZaRight.A_type = "type" ;
ZaRight.A_targetType = "targetType"  ;
ZaRight.A_definedBy = "definedBy" ;

ZaRight.A2_selected_rights = "selected_rights";

ZaRight.RIGHT_TYPES = ["preset", "setAttrs", "getAttrs", "combo"];

//@return the lists of rights for the type specified
ZaRight.getCustomRightsList = function () {
    //TODO: since we can also create the getAttrs and setAttrs rights, we actually need to use the SOAP command to get the lists of rights   
    var customRights = [] ;

    for (var i = 0 ; i < 10 ; i ++) {
        var right = new ZaRight () ;
        var type ;
        var j = i % ZaZimbraRights.type.length ;
        if (j == 0) {
            type = "combo" ; //no preset for the customRights
        } else {
            type = ZaZimbraRights.type [j] ;
        }
        right.name = "CustomRight" + i ;
        right.attrs = {
            name: "CustomRight" + i ,
            type: type ,
            desc: "Prototype Custom Right " + i
        }

        var targetType ;
        if (type == "getAttrs") {
            targetType = [ "account", "cos" ] ;
            right.attrs.attrs = ["zimbraMailQuota", "zimbraQuotaWarnPercent",
                "zimbraQuotaWarnInterval", "zimbraQuotaWarnMessage"] ;
        } else if (type == "setAttrs"){
            targetType = [ "domain" ];
            right.attrs.attrs = ["zimbraFeatureIMEnabled", "zimbraFeatureCalendarEnabled",
                "zimbraFeatureMailEnabled"] ;
        } else if (type == "combo") {
            right.attrs.rights = ["createAccount", "renameAccount", "configureQuotaWithinLimit"]  ;
        }
        if (targetType) right.attrs.targetType = targetType ;
        customRights.push(right) ;
    }
//    var resp = {right: customRights };
    var list = new ZaItemList(ZaRight);
    list._vector =    AjxVector.fromArray(customRights)  ;

    return list;
}

ZaRight.rightsOvTreeModifier = function (tree) {
    var overviewPanelController = this ;
    if (!overviewPanelController) throw new Exception("ZaRight.rightsOvTreeModifier: Overview Panel Controller is not set.");

    if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RIGHT_LIST_VIEW]
            || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        overviewPanelController._rightsTi = new DwtTreeItem(overviewPanelController._configTi);
        overviewPanelController._rightsTi.setText(com_zimbra_delegatedadmin.OVP_rights);
        overviewPanelController._rightsTi.setImage("RightObject"); //TODO: Use Rights icons
		overviewPanelController._rightsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._RIGHTS_LIST_VIEW);
       
        if(ZaOverviewPanelController.overviewTreeListeners) {
            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._RIGHTS_LIST_VIEW] = ZaRight.customRightsListTreeListener;
//            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._CERTS] = ZaCert.certsRightNodeTreeListener;
        }
    }
}

ZaRight.customRightsListTreeListener = function (ev) {
	if (AjxEnv.hasFirebug) console.log("Show the custom rigths lists ...") ;
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(
			ZaApp.getInstance().getRightsListController(),
                ZaRightsListViewController.prototype.show, ZaRight.getAll());
	} else {
		ZaApp.getInstance().getRightsListController().show(ZaRight.getAll());
	}
}
ZaRight.myXModel = {
	items: [
        {id: ZaRight.A_id, ref:  ZaRight.A_id, type: _STRING_},
        {id: ZaRight.A_name, ref: ZaRight.A_name, type: _STRING_, required: true},
        {id: ZaRight.A_type, ref: "attrs/" + ZaRight.A_type, type: _ENUM_, choices: ZaZimbraRights.type },
            //TODO: have a new choice list xform item to display the targetType
        {id: ZaRight.A_targetType, ref: "attrs/" + ZaRight.A_targetType, type: _LIST_, listItems: {type: _ENUM_, choices: ZaZimbraRights.targetType} },
        {id: ZaRight.A_desc, ref: "attrs/" + ZaRight.A_desc, type: _STRING_ },
        {id: ZaRight.A_definedBy, ref: "attrs/" + ZaRight.A_definedBy, type: _ENUM_, choices: ZaZimbraRights.definedBy },
        {id: ZaRight.A_attrs,  ref: "attrs/" + ZaRight.A_attrs, type: _LIST_, listItem:{type:_STRING_}} ,
//        {id: ZaRight.A_getAttrs,  ref: "attrs/" + ZaRight.A_getAttrs, type: _LIST_, listItem:{type:_STRING_}} ,
//        {id: ZaRight.A_setAttrs,  ref: "attrs/" + ZaRight.A_setAttrs, type: _LIST_, listItem:{type:_STRING_}} ,
        {id: ZaRight.A_rights,  ref: "attrs/" + ZaRight.A_rights, type: _LIST_, listItem:{type:_STRING_}}
    ]
};

ZaRight.prototype.toString = function() {
	return this.name;
}

ZaRight.getRights = function (targetType, expandAllAttrs) {
    var soapDoc = AjxSoapDoc.create("GetAllRightsRequest", ZaZimbraAdmin.URN, null);
	if (targetType) {
        soapDoc.setMethodAttribute("targetType", targetType);
    }
    if (expandAllAttrs) {
        soapDoc.setMethodAttribute("expandAllAttrs", expandAllAttrs) ;
    }
    var params = new Object();
	params.soapDoc = soapDoc;
	params.asyncMode=false;
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : com_zimbra_delegatedadmin.BUSY_GET_ALL_RIGHT
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllRightsResponse;
    var list = new ZaItemRightList(ZaRight);
	list.loadFromJS(resp);
	return list;
//    return resp.right ;
}

 //initialize and normalize the ZaRight.SYSTEM_RIGHTS
ZaRight.initSystemRights = function (allSystemRightsList) {
    if (!allSystemRightsList) return ;

    ZaRight.SYSTEM_RIGHTS = [] ;
    var arr = allSystemRightsList.getArray () ;
    for (var j = 0; j < arr.length; j ++) {
        ZaRight.SYSTEM_RIGHTS.push (arr [j].name);
    }
}

ZaRight.getAll =
function() {
    var allSystemRightsList = ZaRight.getRights () ;
    ZaRight.initSystemRights (allSystemRightsList);
    return allSystemRightsList ;
}

ZaRight.getSystemRightsByTargetType = function (targetType)  {
    if (!targetType) return ;

    if (!ZaRight.SYSTEM_RIGHTS_BY_TARGET_TYPE) {
        ZaRight.SYSTEM_RIGHTS_BY_TARGET_TYPE = {} ;
    }

    if (!ZaRight.SYSTEM_RIGHTS_BY_TARGET_TYPE[targetType]) {
        ZaRight.SYSTEM_RIGHTS_BY_TARGET_TYPE[targetType] = [];
        var lists = ZaRight.getRights(targetType) ;
        var arr = lists.getArray () ;
        for (var j = 0; j < arr.length; j ++) {
            ZaRight.SYSTEM_RIGHTS_BY_TARGET_TYPE[targetType].push (arr [j].name);
        }
    }

    return ZaRight.SYSTEM_RIGHTS_BY_TARGET_TYPE [targetType] ;
}
/**
 * @argument callArgs {value, event, callback,extraLdapQuery,form }
 */
ZaRight.prototype.dynSelectRightNames = function (callArgs) {
	try {
		var value = callArgs["value"] || "";
		//var event = callArgs["event"];
		var callback = callArgs["callback"];
		//var extraLdapQuery = callArgs["extraLdapQuery"];
		var form = callArgs["form"];
		
        var targetType = form.getInstance()[ZaGrant.A_target_type] ;
        var systemRightsByTarget = ZaRight.getSystemRightsByTargetType (targetType) ;

        var choices = [];
        if (systemRightsByTarget) {
            //filter the choices by user input
            for (var i = 0; i < systemRightsByTarget.length; i ++) {
    //            if (systemRightsByTarget[i].indexOf (value) > 0) {
                if (systemRightsByTarget[i].toLowerCase().indexOf (value.toLowerCase()) >= 0) {    //start with
                    choices.push(systemRightsByTarget[i])   ;
                }
            }
        }

        if (callback)
            callback.run(choices) ;
        
    } catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaRight.prototype.dynSelectRightNames");
	}
}

ZaRight.modifyMethod = function (tmpObj) {

}
ZaItem.modifyMethods["ZaRight"].push(ZaRight.modifyMethod);


/**
* Returns HTML for a tool tip for this domain.
*/
ZaRight.prototype.getToolTip =
function() {
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0'>";
		html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
		html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap; overflow:hidden;width:350'>";
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' style='width:100%;'>";
		html[idx++] = "<tr valign='center'>";
		html[idx++] = "<td><b>" + AjxStringUtil.htmlEncode(this.name) + "</b></td>";
		html[idx++] = "<td align='right'>";
		html[idx++] = AjxImg.getImageHtml("Right");
		html[idx++] = "</td>";
		html[idx++] = "</table></div></td></tr>";
		html[idx++] = "<tr></tr>";
		idx = this._addAttrRow(ZaRight.A_desc, com_zimbra_delegatedadmin.Col_right_desc + ": ", html, idx);
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}

// Adds a row to the tool tip.
ZaRight.prototype._addAttrRow =
function(name, label, html, idx) {
	var value = this.attrs[name];
	if (value != null) {
		html[idx++] = "<tr valign='top'><td align='left' style='padding-right: 5px;'><b>";
		html[idx++] = AjxStringUtil.htmlEncode(label) ;
		html[idx++] = "</b></td><td align='left'><div style='white-space:nowrap; overflow:hidden;'>";
		html[idx++] = AjxStringUtil.htmlEncode(value);
		html[idx++] = "</div></td></tr>";
	}
	return idx;
}

ZaRight.prototype.remove =
function() {
	var soapDoc = AjxSoapDoc.create("DeleteRightRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_DELETE_RIGHT
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams);
}

ZaRight.prototype.refresh =
function() {
	this.load();
}


ZaRight.loadMethod =
function(by, val, withConfig) {
   	var soapDoc = AjxSoapDoc.create("GetRightRequest", ZaZimbraAdmin.URN, null);
	soapDoc.getMethod().setAttribute("expandAllAttrs", "1") ;
	var elRight = soapDoc.set("right", this.name);
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	params.asyncMode = false;
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : com_zimbra_delegatedadmin.BUSY_GET_RIGHT
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams);
    this.initFromJS(resp.Body.GetRightResponse.right[0]);
                                                  
}
ZaItem.loadMethods["ZaRight"].push(ZaRight.loadMethod);



ZaRight.prototype.initFromJS = function(right) {
    if (!right) return ;
    for ( var ix in right ) {
        if (ix == "name") {
            this.name = right.name ;
            this.attrs.name = right.name ;
        } else if (ix == "desc") {
            if (right[ix] && right[ix][0]) {
                this.attrs [ix] = right[ix][0]._content ;
            }
        } else if ( ix == "rights" || ix == "attrs") {
            this.attrs [ix] = [] ;
            if (right[ix] && right[ix][0]) {
                var elVals ;
                if (ix == "rights") {
                    elVals = right[ix][0].r ;
                    for (var j = 0; j < elVals.length; j ++) {
                        this.attrs[ix].push (elVals[j].n) ;
                    }
                }else if (ix == "attrs")  {
                    elVals = right[ix][0];
                    for (var property in elVals) {
                        this.attrs[ix].push(property) ;
                    }
                }
            }
        } else {
            this.attrs [ix] = right [ix] ;            
        }
    }
}

ZaRight.initMethod = function () {
	this.attrs = new Object();
	this.id = "";
	this.name="";
}
ZaItem.initMethods["ZaRight"].push(ZaRight.initMethod);

ZaRight.compareTargetTypeAsc = function (a, b) {
    return ZaItem.compareAttrAsc(a, b, ZaRight.A_targetType) ;
}

ZaRight.compareTargetTypeDesc = function (a, b) {
    return ZaItem.compareAttrDesc(a, b, ZaRight.A_targetType) ;
}



