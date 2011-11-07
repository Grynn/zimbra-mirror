if(ZaSettings && ZaSettings.EnabledZimlet["com_zimbra_adminversioncheck"]){
if(window.console && window.console.log) console.log("Start loading com_zimbra_adminversioncheck.js");
function ZaVersionCheck() {
	ZaItem.call(this,"ZaVersionCheck");
	this.attrs = new Object();
	this.attrsToGet = [ZaVersionCheck.A_zimbraVersionCheckLastAttempt,
		ZaVersionCheck.A_zimbraVersionCheckLastSuccess,
		ZaVersionCheck.A_zimbraVersionCheckNotificationEmail,
		ZaVersionCheck.A_zimbraVersionCheckInterval,
		ZaVersionCheck.A_zimbraVersionCheckServer,
		ZaVersionCheck.A_zimbraVersionCheckURL]
};
ZaVersionCheck.prototype = new ZaItem;
ZaVersionCheck.prototype.constructor = ZaVersionCheck;
ZaItem.loadMethods["ZaVersionCheck"] = new Array();
ZaItem.modifyMethods["ZaVersionCheck"] = new Array();

ZaOperation.VERSION_CHECK = ++ZA_OP_INDEX;
ZaVersionCheck.INVALID_VC_RESPONSE = "versioncheck.INVALID_VC_RESPONSE";
//constants
ZaVersionCheck.A_zimbraVersionCheckLastAttempt = "zimbraVersionCheckLastAttempt";
ZaVersionCheck.A_zimbraVersionCheckLastSuccess = "zimbraVersionCheckLastSuccess";
//ZaVersionCheck.A_zimbraVersionCheckLastResponse = "zimbraVersionCheckLastResponse";
ZaVersionCheck.A_zimbraVersionCheckNotificationEmail = "zimbraVersionCheckNotificationEmail";
ZaVersionCheck.A_zimbraVersionCheckSendNotifications = "zimbraVersionCheckSendNotifications";
ZaVersionCheck.A_zimbraVersionCheckNotificationEmailFrom = "zimbraVersionCheckNotificationEmailFrom";
ZaVersionCheck.A_zimbraVersionCheckNotificationSubject = "zimbraVersionCheckNotificationSubject";
ZaVersionCheck.A_zimbraVersionCheckNotificationBody = "zimbraVersionCheckNotificationBody";
ZaVersionCheck.A_zimbraVersionCheckInterval = "zimbraVersionCheckInterval";
ZaVersionCheck.A_zimbraVersionCheckServer = "zimbraVersionCheckServer";
ZaVersionCheck.A_zimbraVersionCheckURL = "zimbraVersionCheckURL";
ZaVersionCheck.A_zimbraVersionCheckUpdates = "updates";
ZaVersionCheck.A_zimbraVersionCheckUpdateType = "type";
ZaVersionCheck.A_zimbraVersionCheckUpdateCritical = "critical";
ZaVersionCheck.A_zimbraVersionCheckUpdateVersion = "version";
ZaVersionCheck.A_zimbraVersionCheckUpdateBuildtype = "buildtype";
ZaVersionCheck.A_zimbraVersionCheckUpdateUpdateURL = "updateURL";
ZaVersionCheck.A_zimbraVersionCheckUpdateDescription = "description";
ZaVersionCheck.A_zimbraVersionCheckUpdateShortversion = "shortversion";

if(ZaSettings) {
	ZaSettings.SOFTWARE_UPDATES_VIEW = "softwareUpdatesView";
	ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.SOFTWARE_UPDATES_VIEW, label: com_zimbra_adminversioncheck.UI_Comp_versionCheck });
	ZaSettings.OVERVIEW_TOOLS_ITEMS.push(ZaSettings.SOFTWARE_UPDATES_VIEW);
	ZaSettings.VIEW_RIGHTS [ZaSettings.SOFTWARE_UPDATES_VIEW] = "adminConsoleSoftwareUpdatesRights";
}

ZaVersionCheck.myXModel = {	items:[
	{id:ZaVersionCheck.A_zimbraVersionCheckLastAttempt, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckLastAttempt, type: _DATETIME_},
    {id:ZaVersionCheck.A_zimbraVersionCheckLastSuccess, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckLastSuccess, type: _DATETIME_},
    {id:ZaVersionCheck.A_zimbraVersionCheckNotificationEmail, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckNotificationEmail, type: _STRING_},
    {id:ZaVersionCheck.A_zimbraVersionCheckNotificationEmailFrom, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckNotificationEmailFrom, type: _STRING_},
    {id:ZaVersionCheck.A_zimbraVersionCheckNotificationSubject, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckNotificationSubject, type: _STRING_},
    {id:ZaVersionCheck.A_zimbraVersionCheckNotificationBody, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckNotificationBody, type: _STRING_},
    {id:ZaVersionCheck.A_zimbraVersionCheckSendNotifications, ref:"attrs/" +  ZaVersionCheck.A_zimbraVersionCheckSendNotifications,  type:_ENUM_, choices: ZaModel.BOOLEAN_CHOICES},        
    {id:ZaVersionCheck.A_zimbraVersionCheckServer, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckServer, type: _STRING_},
	{id:ZaVersionCheck.A_zimbraVersionCheckURL, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckURL, type: _STRING_},
	{id:ZaVersionCheck.A_zimbraVersionCheckInterval, type:_MLIFETIME_, ref:"attrs/"+ZaVersionCheck.A_zimbraVersionCheckInterval},
	{id:ZaVersionCheck.A_zimbraVersionCheckUpdates, type:_LIST_, listItem:
		{type:_OBJECT_, 
			items: [
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateType, type:_STRING_},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateCritical, type:_ENUM_, choices: ZaModel.BOOLEAN_CHOICES2},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateVersion, type:_STRING_},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateBuildtype, type:_STRING_},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateUpdateURL, type:_STRING_},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateDescription, type:_STRING_},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateShortversion, type:_STRING_}
			]
		}
	}
]};

ZaVersionCheck.getAttemptTime =
function (serverStr) {
	if (serverStr) {
		return ZaItem.formatServerTime(serverStr);
	}else{
		return com_zimbra_adminversioncheck.Never;
	}
}

ZaVersionCheck.checkNow = function() {
	var params, soapDoc;
	soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
    soapDoc.setMethodAttribute("onerror", "continue");
	
	var versionCheck = soapDoc.set("VersionCheckRequest", null, null, ZaZimbraAdmin.URN);
	versionCheck.setAttribute("action","check");

	try {
		params = new Object();
		params.soapDoc = soapDoc;	
		var reqMgrParams ={
			controller:ZaApp.getInstance().getCurrentController()
		}
		var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
		if(respObj.isException && respObj.isException()) {
			ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(), "ZaVersionCheck.checkNow", null, false);
		    hasError  = true ;
        } else if(respObj.Body.BatchResponse.Fault) {
			var fault = respObj.Body.BatchResponse.Fault;
			if(fault instanceof Array)
				fault = fault[0];
			
			if (fault) {
				// JS response with fault
				var ex = ZmCsfeCommand.faultToEx(fault);
				ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaVersionCheck.checkNow", null, false);
                hasError = true ;
            }
		} 
	} catch (ex) {
		//show the error and go on
		//we should not stop the Account from loading if some of the information cannot be acces
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaVersionCheck.loadMethod", null, false);
    }		
}

ZaVersionCheck.loadMethod = 
function(by, val) {
	var params, soapDoc;
	soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
    soapDoc.setMethodAttribute("onerror", "continue");
    var getConfigDoc = soapDoc.set("GetAllConfigRequest", null, null, ZaZimbraAdmin.URN);	
	if(!this.getAttrs.all && !AjxUtil.isEmpty(this.attrsToGet)) {
		getConfigDoc.setAttribute("attrs", this.attrsToGet.join(","));
	}	

	var versionCheck = soapDoc.set("VersionCheckRequest", null, null, ZaZimbraAdmin.URN);
	versionCheck.setAttribute("action","status");
	try {
		params = new Object();
		params.soapDoc = soapDoc;	
		var reqMgrParams ={
			controller:ZaApp.getInstance().getCurrentController()
		}
		var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
		if(respObj.isException && respObj.isException()) {
			ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(), "ZaVersionCheck.loadMethod", null, false);
		    hasError  = true ;
        } else if(respObj.Body.BatchResponse.Fault) {
			var fault = respObj.Body.BatchResponse.Fault;
			if(fault instanceof Array)
				fault = fault[0];
			
			if (fault) {
				// JS response with fault
				var ex = ZmCsfeCommand.faultToEx(fault);
				ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaVersionCheck.loadMethod", null, false);
                hasError = true ;
            }
		} 
		var batchResp = respObj.Body.BatchResponse;
			
		if(batchResp.GetAllConfigResponse) {
			resp = batchResp.GetAllConfigResponse[0];
			this.initFromJS(resp);
		}
			
		if(batchResp.VersionCheckResponse) {
			var resp = batchResp.VersionCheckResponse[0];
			this[ZaVersionCheck.A_zimbraVersionCheckUpdates] = [];
			if(resp && resp.versionCheck && resp.versionCheck[0] && resp.versionCheck[0].updates) {
				if(resp.versionCheck[0].updates instanceof Array && resp.versionCheck[0].updates.length>0 && 
				resp.versionCheck[0].updates[0].update && resp.versionCheck[0].updates[0].update.length>0) {
					var cnt = resp.versionCheck[0].updates[0].update.length;
					for(var i = 0; i< cnt; i++) {
						this[ZaVersionCheck.A_zimbraVersionCheckUpdates].push(resp.versionCheck[0].updates[0].update[i]);
					}
				}
			}
		}
				
			
	} catch (ex) {
		//show the error and go on
		//we should not stop the Account from loading if some of the information cannot be acces
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaVersionCheck.loadMethod", null, false);
    }		
}
ZaItem.loadMethods["ZaVersionCheck"].push(ZaVersionCheck.loadMethod);

ZaVersionCheck.modifyMethod = function (mods) {
	var soapDoc = AjxSoapDoc.create("ModifyConfigRequest", ZaZimbraAdmin.URN, null);
	for (var aname in mods) {
		//multy value attribute
		if(mods[aname] instanceof Array) {
			var cnt = mods[aname].length;
			if(cnt > 0) {
				for(var ix=0; ix <cnt; ix++) {
					if(mods[aname][ix] instanceof String)
						var attr = soapDoc.set("a", mods[aname][ix].toString());
					else if(mods[aname][ix] instanceof Object)
						var attr = soapDoc.set("a", mods[aname][ix].toString());
					else 
						var attr = soapDoc.set("a", mods[aname][ix]);
						
					attr.setAttribute("n", aname);
				}
			} 
			else {
				var attr = soapDoc.set("a");
				attr.setAttribute("n", aname);
			}
		} else {
			var attr = soapDoc.set("a", mods[aname]);
			attr.setAttribute("n", aname);
		}
	}
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	command.invoke(params);
}
ZaItem.modifyMethods["ZaVersionCheck"].push(ZaVersionCheck.modifyMethod);

ZaZimbraAdmin._VERSION_CHECK_VIEW = ZaZimbraAdmin.VIEW_INDEX++;

ZaApp.prototype.getVersionCheckViewController =
function() {
	if (this._controllers[ZaZimbraAdmin._VERSION_CHECK_VIEW] == null)
		this._controllers[ZaZimbraAdmin._VERSION_CHECK_VIEW] = new ZaVersionCheckViewController(this._appCtxt, this._container);
	return this._controllers[ZaZimbraAdmin._VERSION_CHECK_VIEW];
}

ZaVersionCheck.versionCheckTreeListener = function (ev) {
	var versionCheck = new ZaVersionCheck();
	
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getVersionCheckViewController(),ZaVersionCheckViewController.prototype.show, [versionCheck]);
	} else {					
		ZaApp.getInstance().getVersionCheckViewController().show(versionCheck);
	}
}

ZaVersionCheck.versionCheckTreeModifier = function (tree) {
	var overviewPanelController = this ;
	if (!overviewPanelController) throw new Exception("ZaCert.versionCheckTreeModifier: Overview Panel Controller is not set.");	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SOFTWARE_UPDATES_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        if (!appNewUI) {
            if(!this._toolsTi) {
                this._toolsTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
                this._toolsTi.enableSelection(false);
                this._toolsTi.setText(ZaMsg.OVP_tools);
                this._toolsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._TOOLS);
            }

            this._versionCheckTi = new DwtTreeItem({parent:this._toolsTi,className:"AdminTreeItem"});
            this._versionCheckTi.setText(com_zimbra_adminversioncheck.OVP_versionCheck);
            this._versionCheckTi.setImage("AdminRefresh");
            this._versionCheckTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._VERSION_CHECK_VIEW);
        } else {
            var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_toolMig]);

            var ti = new ZaTreeItemData({
                                    parent:parentPath,
                                    id:ZaId.getTreeItemId(ZaId.PANEL_APP,"magHV",null, "VersionCheckHV"),
                                    text: com_zimbra_adminversioncheck.OVP_versionCheck,
                                    mappingId: ZaZimbraAdmin._VERSION_CHECK_VIEW});
            tree.addTreeItemData(ti);
        }
		if(ZaOverviewPanelController.overviewTreeListeners) {
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._VERSION_CHECK_VIEW] = ZaVersionCheck.versionCheckTreeListener;
		}
	}
}

if(ZaOverviewPanelController.treeModifiers)
	ZaOverviewPanelController.treeModifiers.push(ZaVersionCheck.versionCheckTreeModifier);
}

if (ZaHome && ZaHome.myXModel) {
    ZaHome.A2_versionUpdateAvailable = "versionUpdateAvailable";
    ZaHome.A2_updateMessage = "A2_updateMessage";
    ZaHome.myXModel.items.push(
        {id:ZaHome.A2_versionUpdateAvailable, type:_ENUM_, ref: "attrs/" + ZaHome.A2_versionUpdateAvailable, choices: ZaModel.BOOLEAN_CHOICES}
    );
    ZaHome.myXModel.items.push(
        {id:ZaHome.A2_updateMessage, type:_STRING_, ref: "attrs/" + ZaHome.A2_updateMessage}
    );
    ZaHome.loadVersionMethod =
    function () {

        // TODO using the load method to get version information
        this.attrs[ZaHome.A2_versionUpdateAvailable] = false;
        this.attrs[ZaHome.A2_updateMessage] = "Zimbra 8 is available";
    }
    ZaItem.loadMethods["ZaHome"].push(ZaHome.loadVersionMethod);
}

if(ZaTabView.XFormModifiers["ZaHomeXFormView"]) {

    ZaHomeXFormView.onViewVersionUpdate = function(ev) {
        var tree = ZaZimbraAdmin.getInstance().getOverviewPanelController().getOverviewPanel().getFolderTree();
        var path = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_toolMig, com_zimbra_adminversioncheck.OVP_versionCheck]);
        tree.setSelectionByPath(path, false);
    }

    ZaVersionCheck.HomeXFormModifier = function(xFormObject) {
        var infoItem = xFormObject.items[0].items[0].items[0].items;
        infoItem.push(
            {type:_GROUP_, numCols:3,  width:"100%", colSizes:["80px", "*", "100px"],
                visibilityChecks:[[XForm.checkInstanceValue,ZaHome.A2_versionUpdateAvailable,true]],
                items:[
                {type:_OUTPUT_, ref: ZaHome.A2_versionUpdateAvailable,
                    getDisplayValue: function (value){
                        if (value) {
                            return AjxImg.getImageHtml ("Information");
                        }
                    }
                },
                {type:_OUTPUT_, ref: ZaHome.A2_updateMessage},
                {type:_OUTPUT_, value:com_zimbra_adminversioncheck.LBL_ViewUpdate, containerCssStyle:"cursor:pointer;color:white",onClick: ZaHomeXFormView.onViewVersionUpdate}
            ]});

    }

    ZaTabView.XFormModifiers["ZaHomeXFormView"].push(ZaVersionCheck.HomeXFormModifier);
}

