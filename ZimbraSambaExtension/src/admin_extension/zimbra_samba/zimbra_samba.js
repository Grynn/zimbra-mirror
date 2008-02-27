DBG.println(AjxDebug.DBG1,"Loaded zimbra_samba.js");

ZaZimbraAdmin._SAMBA_DOMAIN_LIST = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._SAMBA_DOMAIN_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._SAMBA_DOMAIN_LIST] = "SMBDomains_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._SAMBA_DOMAIN_VIEW] = "SMBDomains_view_title";
if(ZaMsg) {
	ZaMsg.SMBDomains_view_title = "Manage Samba Domains";
}
function Zambra() {
	
}
Zambra.ldapSuffix = "dc=com";
Zambra.ldapGroupSuffix = "ou=group";
Zambra.ldapMachineSuffix = "ou=machines";

Zambra.initSettings= function () {
	try {
		var soapDoc = AjxSoapDoc.create("GetAdminExtensionZimletsRequest", "urn:zimbraAdmin", null);	
		var command = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		var resp = command.invoke(params);
		var zimlets = null;
		try {
			if(resp && resp.Body && resp.Body.GetAdminExtensionZimletsResponse && resp.Body.GetAdminExtensionZimletsResponse.zimlets && resp.Body.GetAdminExtensionZimletsResponse.zimlets.zimlet) {
				zimlets = resp.Body.GetAdminExtensionZimletsResponse.zimlets.zimlet;
			}
		} catch (ex) {
			//go on
		}
		if(zimlets && zimlets.length > 0) {
			var cnt = zimlets.length;
			for(var ix = 0; ix < cnt; ix++) {
				if(zimlets[ix] && zimlets[ix].zimlet && zimlets[ix].zimlet[0] && zimlets[ix].zimletConfig && zimlets[ix].zimletConfig[0]) {
					var zimletConfig = zimlets[ix].zimletConfig[0];					
					if(zimletConfig.name=="zimbra_samba") {
						var global = zimletConfig.global[0];
						if(global) {
							var properties = global.property;
							var cnt2 = properties.length;							
							for (var j=0;j<cnt2;j++) {
								Zambra[properties[j].name] = properties[j]._content;
							}
						}
						break;
					}
				} else {
					continue;
				}
			}
		}	
	} catch (ex) {
		//do nothing, do not block the app from loading
	}
}

if(ZaSettings.initMethods)
	ZaSettings.initMethods.push(Zambra.initSettings);

	
Zambra.initOUs = function () {
	//check groups OU
	var soapDoc = AjxSoapDoc.create("GetLDAPEntriesRequest", "urn:zimbraAdmin", null);	
	soapDoc.set("ldapSearchBase", Zambra.ldapSuffix);
	soapDoc.set("query", Zambra.ldapGroupSuffix);	
	var getSambaDomainsCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = getSambaDomainsCommand.invoke(params).Body.GetLDAPEntriesResponse;
	if(resp && resp.LDAPEntry && resp.LDAPEntry[0]) {
		//ou exists
	} else {
		try {
			//ou does not exist - create it
			var soapDoc = AjxSoapDoc.create("CreateLDAPEntryRequest", "urn:zimbraAdmin", null);		
			var dn = [Zambra.ldapGroupSuffix,Zambra.ldapSuffix];
			soapDoc.set("dn", dn.join(","));	
			var testCommand = new ZmCsfeCommand();
			var params = new Object();
			var attr = soapDoc.set("a", "organizationalRole");
			attr.setAttribute("n", "objectClass");		
			var attr = soapDoc.set("a", "groups");
			attr.setAttribute("n", "cn");		
			
			params.soapDoc = soapDoc;	
			var resp = testCommand.invoke(params).Body.CreateLDAPEntryResponse;
		} catch (e) {
			alert("Warning! Failed to create "+dn.join(",")+" for Samba groups!");
		}
		
	}

	//check machines OU
	soapDoc = AjxSoapDoc.create("GetLDAPEntriesRequest", "urn:zimbraAdmin", null);	
	soapDoc.set("ldapSearchBase", Zambra.ldapSuffix);
	soapDoc.set("query", Zambra.ldapMachineSuffix);	
	getSambaDomainsCommand = new ZmCsfeCommand();
	params = new Object();
	params.soapDoc = soapDoc;	
	resp = getSambaDomainsCommand.invoke(params).Body.GetLDAPEntriesResponse;
	if(resp && resp.LDAPEntry && resp.LDAPEntry[0]) {
		//ou exists
	} else {
		try {
			//ou does not exist - create it
			var soapDoc = AjxSoapDoc.create("CreateLDAPEntryRequest", "urn:zimbraAdmin", null);		
			var dn = [Zambra.ldapMachineSuffix,Zambra.ldapSuffix];
			soapDoc.set("dn", dn.join(","));	
			var testCommand = new ZmCsfeCommand();
			var params = new Object();
			var attr = soapDoc.set("a", "organizationalRole");
			attr.setAttribute("n", "objectClass");		
			var attr = soapDoc.set("a", "machines");
			attr.setAttribute("n", "cn");		
			
			params.soapDoc = soapDoc;	
			var resp = testCommand.invoke(params).Body.CreateLDAPEntryResponse;
			
		} catch (e) {
			alert("Warning! Failed to create "+dn.join(",")+" for Samba machine accounts!");
		}
		
	}

}

if(ZaSettings.initMethods)
	ZaSettings.initMethods.push(Zambra.initOUs);


ZaApp.prototype.getSambaDomainList =
function(refresh) {
	if (refresh || this._sambaDomainList == null) {
		this._sambaDomainList = ZaSambaDomain.getAll(this);
	}
	return this._sambaDomainList;	
}

ZaApp.prototype.getSambaDomainListController =
function() {
	if (this._controllers[ZaZimbraAdmin._SAMBA_DOMAIN_LIST] == null) {
		this._controllers[ZaZimbraAdmin._SAMBA_DOMAIN_LIST] = new ZaSambaDomainListController(this._appCtxt, this._container, this);
	}
	return this._controllers[ZaZimbraAdmin._SAMBA_DOMAIN_LIST];
}

ZaApp.prototype.getSambaDomainController =
function() {
	var c = new ZaSambaDomainController(this._appCtxt, this._container, this);
	var ctrl = this.getSambaDomainListController();
	c.addChangeListener(new AjxListener(ctrl, ctrl.handleChange));
	c.addCreationListener(new AjxListener(ctrl, ctrl.handleCreation));	
	c.addRemovalListener(new AjxListener(ctrl, ctrl.handleRemoval));			
	return c;
}
Zambra.sambaDomainListTreeListener = function (ev) {
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getSambaDomainListController(),ZaSambaDomainListController.prototype.show, ZaSambaDomain.getAll(this._app));
	} else {					
		this._app.getSambaDomainListController().show(ZaSambaDomain.getAll(this._app));
	}
}

Zambra.sambaDomainTreeListener = function (ev) {
	var currentSambaDomain = this._app.getSambaDomainList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID));	
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getSambaDomainController(),ZaSambaDomainController.prototype.show, currentSambaDomain);
	} else {					
		this._app.getSambaDomainController().show(currentSambaDomain);
	}
}


Zambra.ovTreeModifier = function (tree) {
	if(ZaSettings.SYSTEM_CONFIG_ENABLED) {
		this._sambaTi = new DwtTreeItem(this._configTi);
		this._sambaTi.setText("Samba Domains");
		this._sambaTi.setImage("ZimbraIcon");
		this._sambaTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SAMBA_DOMAIN_LIST);	
		
		try {
			//add server statistics nodes
			var sambaDomainList = this._app.getSambaDomainList().getArray();
			if(sambaDomainList && sambaDomainList.length) {
				var cnt = sambaDomainList.length;
				for(var ix=0; ix< cnt; ix++) {
					var ti1 = new DwtTreeItem(this._sambaTi);			
					ti1.setText(sambaDomainList[ix].name);	
					ti1.setImage("Domain");
					ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SAMBA_DOMAIN_VIEW);
					ti1.setData(ZaOverviewPanelController._OBJ_ID, sambaDomainList[ix].id);
				}
			}
		} catch (ex) {
			this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
		}
		

		if(ZaOverviewPanelController.overviewTreeListeners) {
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SAMBA_DOMAIN_LIST] = Zambra.sambaDomainListTreeListener;
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SAMBA_DOMAIN_VIEW] = Zambra.sambaDomainTreeListener;							
		}
	}
}

if(ZaOverviewPanelController.treeModifiers)
	ZaOverviewPanelController.treeModifiers.push(Zambra.ovTreeModifier);

	