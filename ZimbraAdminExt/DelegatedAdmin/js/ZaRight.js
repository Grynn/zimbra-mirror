ZaRight = function() {
	ZaItem.call(this, "ZaRight");
	this._init();
	//The type is required. The application tab uses it to show the right icon
	this.type = ZaItem.RIGHT ; 
}

ZaRight.prototype = new ZaItem;
ZaRight.prototype.constructor = ZaRight;
ZaItem.loadMethods["ZaRight"] = new Array();
ZaItem.initMethods["ZaRight"] = new Array();
ZaItem.modifyMethods["ZaRight"] = new Array();


ZaRight.A_name = "name" ;
ZaRight.A_desc = "desc" ;
ZaRight.A_attrs = "attrs" ;
ZaRight.A_rights = "rights" ;
ZaRight.A_type = "type" ;
ZaRight.A_targetType = "targetType"  ;

//@return the lists of rights for the type specified
ZaRight.getCustomRightsList = function () {
    //TODO: since we can also create the getAttrs and setAttrs rights, we actually need to use the SOAP command to get the lists of rights   
    var customRights = [] ;

    for (var i = 0 ; i < 10 ; i ++) {
        var type ;
        var j = i % ZaZimbraRights.type.length ;
        if (j == 0) {
            type = "combo" ; //no preset for the customRights
        } else {
            type = ZaZimbraRights.type [j] ;
        }
        var right = {
            name: "CustomRight" + i ,
            type: type ,
            desc: "Prototype Custom Right " + i
        }

        var targetType ;
        if (type == "getAttrs") {
            targetType = "account, cos" ;
        } else if (type == "setAttrs"){
            targetType = "domain" ;            
        }
        if (targetType) right.targetType = targetType ;
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

    if(ZaSettings.RIGHTS_ENABLED) {
        overviewPanelController._rightsTi = new DwtTreeItem(overviewPanelController._configTi);
        overviewPanelController._rightsTi.setText(com_zimbra_delegatedadmin.OVP_rights);
        overviewPanelController._rightsTi.setImage("Account"); //TODO: Use Rights icons
//		overviewPanelController._rightsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._RIGHTS_LIST_VIEW);

        try {
            var ti1 = new DwtTreeItem( overviewPanelController._rightsTi );
            ti1.setText(com_zimbra_delegatedadmin.TI_custom_rights);
            ti1.setImage("Server");
            ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._RIGHTS_LIST_VIEW);
//            ti1.setData(ZaOverviewPanelController._OBJ_ID, serverList[ix].id);

        } catch (ex) {
            overviewPanelController._handleException(ex, "ZaRigth.rightsOvTreeModifier", null, false);
        }

        if(ZaOverviewPanelController.overviewTreeListeners) {
            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._RIGHTS_LIST_VIEW] = ZaRight.customRightsListTreeListener;
//                ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._CERTS] = ZaCert.certsRightNodeTreeListener;
        }
    }
}
ZaRight.customRightsListTreeListener = function (ev) {
	if (AjxEnv.hasFirebug) console.log("Show the custom rigths lists ...") ;
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(
			ZaApp.getInstance().getRightsListController(),ZaRightsListViewController.prototype.show, ZaRight.getCustomRightsList());
	} else {
		ZaApp.getInstance().getRightsListController().show(ZaRights.getCustomRightsList());
	}
}
ZaRight.myXModel = {
	items: [

    ]
};

ZaRight.prototype.toString = function() {
	return this.name;
}

ZaRight.getAll =
function() {
	var soapDoc = AjxSoapDoc.create("GetAllRightsRequest", ZaZimbraAdmin.URN, null);
//	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	params.asyncMode=false;
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_ALL_RIGHT
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllRightsResponse;
	var list = new ZaItemList(ZaRight);
	list.loadFromJS(resp);
	return list;
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
		idx = this._addAttrRow(ZaItem.A_description, html, idx);
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
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
	var _by = by ? by : "id";
	var _val = val ? val : this.id
	var soapDoc = AjxSoapDoc.create("GetRightRequest", ZaZimbraAdmin.URN, null);
	if(withConfig) {
		soapDoc.getMethod().setAttribute("applyConfig", "1");
	} else {
		soapDoc.getMethod().setAttribute("applyConfig", "0");
	}
	var elBy = soapDoc.set("server", _val);
	elBy.setAttribute("by", _by);
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	params.asyncMode = false;
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_RIGHT
	}
	resp = ZaRequestMgr.invoke(params, reqMgrParams);
	this.initFromJS(resp.Body.GetRightResponse.server[0]);

	this.cos = ZaApp.getInstance().getGlobalConfig();

	if(this.attrs[ZaRight.A_zimbraMailboxServiceEnabled]) {
		this.getMyVolumes();
		this.getCurrentVolumes();
	}
}

ZaItem.loadMethods["ZaRight"].push(ZaRight.loadMethod);

ZaRight.prototype.initFromJS = function(server) {
	ZaItem.prototype.initFromJS.call(this, server);
	// convert installed/enabled services to hidden fields for xform binding
	var installed = this.attrs[ZaRight.A_zimbraServiceInstalled];
	if (installed) {
		if (AjxUtil.isString(installed)) {
			installed = [ installed ];
		}
		for (var i = 0; i < installed.length; i++) {
			var service = installed[i];
			this.attrs["_"+ZaRight.A_zimbraServiceInstalled+"_"+service] = true;
			this.attrs["_"+ZaRight.A_zimbraServiceEnabled+"_"+service] = false;
		}
	}

	var enabled = this.attrs[ZaRight.A_zimbraServiceEnabled];
	if (enabled) {
		if (AjxUtil.isString(enabled)) {
			enabled = [ enabled ];
		}
		for (var i = 0; i < enabled.length; i++) {
			var service = enabled[i];
			this.attrs["_"+ZaRight.A_zimbraServiceEnabled+"_"+service] = true;
		}
	}
	this[ZaRight.A_ServiceHostname] = this.attrs[ZaRight.A_ServiceHostname]; // a hack for New Account Wizard
	this[ZaRight.A_showVolumes] = this.attrs[ZaRight.A_zimbraMailboxServiceEnabled];
}

ZaRight.initMethod = function () {
	this.attrs = new Object();
	this.id = "";
	this.name="";
}
ZaItem.initMethods["ZaRight"].push(ZaRight.initMethod);
