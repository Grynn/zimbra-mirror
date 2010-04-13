/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

ZaApplianceSettings = function() {
	ZaItem.call(this,"ZaApplianceSettings");
	this.attrs = new Object();
	this.type = ZaItem.GLOBAL_CONFIG;
	this.load();
}

ZaApplianceSettings.prototype = new ZaItem;
ZaApplianceSettings.prototype.constructor = ZaApplianceSettings;
ZaItem.loadMethods["ZaApplianceSettings"] = new Array();
ZaItem.modifyMethods["ZaApplianceSettings"] = new Array();

ZaApplianceSettings.loadMethod = 
function(by, val) {
	var soapDoc = AjxSoapDoc.create("GetAllConfigRequest", ZaZimbraAdmin.URN, null);
	if(!this.getAttrs.all && !AjxUtil.isEmpty(this.attrsToGet)) {
		soapDoc.setMethodAttribute("attrs", this.attrsToGet.join(","));
	}	
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_ALL_CONFIG
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllConfigResponse;
	this.initFromJS(resp);	
}
ZaItem.loadMethods["ZaApplianceSettings"].push(ZaApplianceSettings.loadMethod);

ZaApplianceSettings.prototype.initFromJS = function(obj) {
	ZaItem.prototype.initFromJS.call(this, obj);
	
	if(AjxUtil.isString(this.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension])) {
		this.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension] = [this.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension]];
	}
	
	if(AjxUtil.isString(this.attrs[ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension])) {
		this.attrs[ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension] = [this.attrs[ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension]];
	}
		
	if(AjxUtil.isString(this.attrs[ZaGlobalConfig.A_zimbraMtaRelayHost])) {
		this.attrs[ZaGlobalConfig.A_zimbraMtaRelayHost] = [this.attrs[ZaGlobalConfig.A_zimbraMtaRelayHost]];
	}
	
	if(AjxUtil.isString(this.attrs[ZaGlobalConfig.A_zimbraSmtpHostname])) {
		this.attrs[ZaGlobalConfig.A_zimbraSmtpHostname] = [this.attrs[ZaGlobalConfig.A_zimbraSmtpHostname]];
	}	
	// convert available components to hidden fields for xform binding
	var components = this.attrs[ZaGlobalConfig.A_zimbraComponentAvailable];
	if (components) {
		if (AjxUtil.isString(components)) {
			components = [ components ];
		}
		for (var i = 0; i < components.length; i++) {
			var component = components[i];
			this.attrs["_"+ZaGlobalConfig.A_zimbraComponentAvailable+"_"+component] = true;
		}
	}
	
	//init list of RBLs
	this.attrs[ZaGlobalConfig.A_zimbraMtaRejectRblClient] = [];
	
	// convert restrictions to hidden fields for xform binding
	var restrictions = this.attrs[ZaGlobalConfig.A_zimbraMtaRestriction];
	if (restrictions) {
		if (AjxUtil.isString(restrictions)) {
			restrictions = [ restrictions ];
		}
		for (var i = 0; i < restrictions.length; i++) {
			if(restrictions[i].indexOf("reject_rbl_client")>-1) {
				var restriction = restrictions[i];
				var chunks = restriction.split(" ");
				if(chunks && chunks.length>0) {
					this.attrs[ZaGlobalConfig.A_zimbraMtaRejectRblClient].push(chunks[1]);
				}
			} else {
				var restriction = restrictions[i];
				this.attrs["_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_"+restriction] = true;
			}
		}
	}
	if(this.attrs[ZaGlobalConfig.A_zimbraInstalledSkin] != null && !(this.attrs[ZaGlobalConfig.A_zimbraInstalledSkin] instanceof Array)) {
		this.attrs[ZaGlobalConfig.A_zimbraInstalledSkin] = [this.attrs[ZaGlobalConfig.A_zimbraInstalledSkin]];
	}
}

ZaApplianceSettings.modifyMethod = function (mods) {
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
			//bug fix 10354: ingnore the changed ZaLicense Properties
			if ((typeof ZaLicense == "function") && (ZaSettings.LICENSE_ENABLED)){
				if (ZaUtil.findValueInObjArrByPropertyName (ZaLicense.myXModel.items, aname, "id") > -1 ){
					continue ;
				}
			}
			var attr = soapDoc.set("a", mods[aname]);
			attr.setAttribute("n", aname);
		}
	}
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	command.invoke(params);
	ZaGlobalConfig.isDirty = true;
}
ZaItem.modifyMethods["ZaApplianceSettings"].push(ZaApplianceSettings.modifyMethod);

ZaApplianceSettings.myXModel = ZaGlobalConfig.myXModel;
