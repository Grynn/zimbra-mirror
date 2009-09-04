function zimbra_posixaccount_ext () {
	
}

ZaZimbraAdmin._POSIX_GROUP_LIST = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._POSIX_GROUP_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._POSIX_GROUP_LIST] = "PSXGroups_view_title";
ZaZimbraAdmin.MSG_KEY[ZaZimbraAdmin._POSIX_GROUP_VIEW] = "PSXGroups_view_title";
if(ZaMsg) {
	ZaMsg.PSXGroups_view_title = zimbra_posixaccount.PosixGroupsListViewTitle;
}

if(ZaItem) {
	ZaItem.POSIX_ACCOUNT = "posixAccount";
}

ZaSettings.POSIX_GROUPS_LIST_VIEW = "posixGroupsListView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.POSIX_GROUPS_LIST_VIEW, label: zimbra_posixaccount.PosixGroups });

function ZaPosixAccount() {	
	ZaItem.call(this, "ZaPosixAccount");
	this.type = ZaItem.POSIX_ACCOUNT;
	this.attrs = [];
	this._init();
}
ZaPosixAccount.prototype = new ZaItem;
ZaPosixAccount.prototype.constructor = ZaPosixAccount;


ZaPosixAccount.A_gidNumber = "gidNumber";
ZaPosixAccount.A_homeDirectory = "homeDirectory";
ZaPosixAccount.A_uidNumber = "uidNumber";
ZaPosixAccount.A_gecos = "gecos";
ZaPosixAccount.A_loginShell = "loginShell";
ZaPosixAccount.A_userPassword = "userPassword";
if(ZaAccount.myXModel && ZaAccount.myXModel.items) {

	ZaAccount.myXModel.items.push({id:ZaPosixAccount.A_gidNumber,type:_NUMBER_,ref:"attrs/"+ZaPosixAccount.A_gidNumber, required:true});
	ZaAccount.myXModel.items.push({id:ZaPosixAccount.A_homeDirectory,type:_STRING_,ref:"attrs/"+ZaPosixAccount.A_homeDirectory, required:true});
	ZaAccount.myXModel.items.push({id:ZaPosixAccount.A_uidNumber,type:_NUMBER_, defaultValue:1000,ref:"attrs/"+ZaPosixAccount.A_uidNumber, required:true});
	ZaAccount.myXModel.items.push({id:ZaPosixAccount.A_loginShell,type:_STRING_,ref:"attrs/"+ZaPosixAccount.A_loginShell, required:true});
	ZaAccount.myXModel.items.push({id:ZaPosixAccount.A_gecos,type:_STRING_,ref:"attrs/"+ZaPosixAccount.A_gecos});

}

ZaPosixAccount.getNextUid = function () {
	var soapDoc = AjxSoapDoc.create("GetLDAPEntriesRequest", "urn:zimbraAdmin", null);	
	soapDoc.set("ldapSearchBase", zimbra_posixaccount_ext.ldapSuffix);
	soapDoc.set("query", "(objectClass=posixAccount)");	
	soapDoc.set("sortBy", ZaPosixAccount.A_uidNumber);	
	soapDoc.set("sortAscending", "false");		
	soapDoc.set("limit", "1");			
	var getPosixAccountsCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var nextId = !isNaN(zimbra_posixaccount_ext.uidBase) ?  parseInt(zimbra_posixaccount_ext.uidBase) + 1 : 10001;
	try {
		var resp = getPosixAccountsCommand.invoke(params).Body.GetLDAPEntriesResponse.LDAPEntry[0];
		if(resp) {
			var acc = new ZaPosixAccount(new Object());;
			acc.initFromJS(resp);
			nextId = parseInt(acc.attrs[ZaPosixAccount.A_uidNumber])+1;
		}
	} catch (ex) {
		//do nothing - fallback to default id for now, ideally should show a warning
	}
	return 	nextId;
}



if(ZaTabView.XFormModifiers["ZaAccountXFormView"]) {
	zimbra_posixaccount_ext.AccountXFormModifier= function (xFormObject,entry) {
		var cnt = xFormObject.items.length;
		var i = 0;
		for(i = 0; i <cnt; i++) {
			if(xFormObject.items[i].type=="switch") 
				break;
		}

		var posixTabIx = ++this.TAB_INDEX;			
		var tabBar = xFormObject.items[1] ;
		tabBar.choices.push({value:posixTabIx, label:zimbra_posixaccount.PosixAccount});		
		var posixAccountTab={type:_ZATABCASE_, numCols:1, caseKey:posixTabIx,
					items: [
						{type:_ZAGROUP_, 
							items:[
								{ref:ZaPosixAccount.A_gidNumber, type:_OSELECT1_, editable:false,choices:ZaApp.getInstance().getPosixGroupIdListChoices(true), msgName:zimbra_posixaccount.PosixGroup,label:zimbra_posixaccount.PosixGroup, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},														
								{ref:ZaPosixAccount.A_gidNumber, type:_TEXTFIELD_, msgName:ZaPosixAccount.A_gidNumber,label:ZaPosixAccount.A_gidNumber, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, cssClass:"admin_xform_number_input"},
								{ref:ZaPosixAccount.A_uidNumber, type:_TEXTFIELD_, msgName:ZaPosixAccount.A_uidNumber,label:ZaPosixAccount.A_uidNumber, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, cssClass:"admin_xform_number_input"},
								{ref:ZaPosixAccount.A_homeDirectory, type:_TEXTFIELD_, msgName:ZaPosixAccount.A_homeDirectory,label:ZaPosixAccount.A_homeDirectory, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:250},
								{ref:ZaPosixAccount.A_loginShell, type:_TEXTFIELD_, msgName:ZaPosixAccount.A_loginShell,label:ZaPosixAccount.A_loginShell, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:250}
							]
				}	]
			};
		xFormObject.items[i].items.push(posixAccountTab);
	}
	ZaTabView.XFormModifiers["ZaAccountXFormView"].push(zimbra_posixaccount_ext.AccountXFormModifier);	
}

if(ZaXDialog.XFormModifiers["ZaNewAccountXWizard"]) {
	
	zimbra_posixaccount_ext.AccountXWizModifier= function (xFormObject, entry) {
//		var stepCounter = this.stepChoices.length;
		ZaNewAccountXWizard.POSIX_ACC_STEP = ++this.TAB_INDEX;		
		this.stepChoices.push({value:ZaNewAccountXWizard.POSIX_ACC_STEP, label:"Posix Account"});
		this._lastStep = this.stepChoices.length;


		var cnt = xFormObject.items.length;
		var i = 0;
		for(i = 0; i <cnt; i++) {
			if(xFormObject.items[i].type=="switch") 
				break;
		}
		cnt = xFormObject.items[i].items.length;
		var j = 0;
		var gotAdvanced = false;
		var gotFeatures = false;		

		var posixAccountStep={type:_CASE_, numCols:1, caseKey:ZaNewAccountXWizard.POSIX_ACC_STEP, tabGroupKey:ZaNewAccountXWizard.POSIX_ACC_STEP,		
					items: [
						{type:_ZAWIZGROUP_, 
							items:[
								{ref:ZaPosixAccount.A_gidNumber, type:_OSELECT1_, editable:false,choices:ZaApp.getInstance().getPosixGroupIdListChoices(true), msgName:zimbra_posixaccount.PosixGroup,label:zimbra_posixaccount.PosixGroup, labelLocation:_LEFT_},							
								{ref:ZaPosixAccount.A_gidNumber, type:_TEXTFIELD_, msgName:ZaPosixAccount.A_gidNumber,label:ZaPosixAccount.A_gidNumber, labelLocation:_LEFT_, cssClass:"admin_xform_number_input"},
								{ref:ZaPosixAccount.A_uidNumber, type:_TEXTFIELD_, msgName:ZaPosixAccount.A_uidNumber,label:ZaPosixAccount.A_uidNumber, labelLocation:_LEFT_, width:250,
									getDisplayValue:function () {
										var val = this.getInstanceValue();
									
										if(!val) {
											val = ZaPosixAccount.getNextUid();
											this.setInstanceValue(val);
										}	
										return val;
									}
								},
								{ref:ZaPosixAccount.A_homeDirectory, type:_TEXTFIELD_, msgName:ZaPosixAccount.A_homeDirectory,label:ZaPosixAccount.A_homeDirectory, labelLocation:_LEFT_,
									getDisplayValue:function() {
										var val = this.getInstanceValue();
										var instance = this.getInstance();
										if((val === null || val === undefined) && zimbra_posixaccount_ext.homePath && instance && instance.name) {
											var chunks = instance.name.split("@");
											if(chunks) {
												var uname = chunks[0];
												if(uname) {
													val = String(zimbra_posixaccount_ext.homePath).replace("%u",uname);
													this.setInstanceValue(val);
												}
											}
										}
										return val;
									}
								},								
								{ref:ZaPosixAccount.A_loginShell, type:_OSELECT1_, editable:true, msgName:ZaPosixAccount.A_loginShell,label:ZaPosixAccount.A_loginShell, labelLocation:_LEFT_, choices:zimbra_posixaccount_ext.shells}
							]
				}	]
			};
		xFormObject.items[i].items.push(posixAccountStep);
	}
	ZaXDialog.XFormModifiers["ZaNewAccountXWizard"].push(zimbra_posixaccount_ext.AccountXWizModifier);	
}


zimbra_posixaccount_ext.initSettings= function () {
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
					if(zimletConfig.name=="zimbra_posixaccount") {
						var global = zimletConfig.global[0];
						if(global) {
							var properties = global.property;
							var cnt2 = properties.length;							
							for (var j=0;j<cnt2;j++) {
								zimbra_posixaccount_ext[properties[j].name] = properties[j]._content;
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
	ZaSettings.initMethods.push(zimbra_posixaccount_ext.initSettings);

zimbra_posixaccount_ext.initOUs = function () {
	
	var soapDoc = AjxSoapDoc.create("GetLDAPEntriesRequest", "urn:zimbraAdmin", null);	
	soapDoc.set("ldapSearchBase", zimbra_posixaccount_ext.ldapSuffix);
	soapDoc.set("query", zimbra_posixaccount_ext.ldapGroupSuffix);	
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
			var dn = [zimbra_posixaccount_ext.ldapGroupSuffix,zimbra_posixaccount_ext.ldapSuffix];
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

}

if(ZaSettings.initMethods)
	ZaSettings.initMethods.push(zimbra_posixaccount_ext.initOUs);

zimbra_posixaccount_ext.initDefaults = function () {
	zimbra_posixaccount_ext.shells = ["/bin/bash"];
	if(zimbra_posixaccount_ext.loginShells) {
		var chunks = zimbra_posixaccount_ext.loginShells.split(",");
		if(chunks && chunks.length) {
			zimbra_posixaccount_ext.shells = chunks;
		} else {
			zimbra_posixaccount_ext.shells = [zimbra_posixaccount_ext.loginShells];
		}
	}
}

if(ZaSettings.initMethods)
	ZaSettings.initMethods.push(zimbra_posixaccount_ext.initDefaults);
	
if(ZmCsfeException)	 {
	ZmCsfeException.DN_EXISTS =  "zimblraldaputils.DN_EXISTS";
}

ZaApp.prototype.getPosixGroupList =
function(refresh) {
	if (refresh || this._posixGroupList == null) {
		this._posixGroupList = ZaPosixGroup.getAll(this);
	}
	return this._posixGroupList;	
}

ZaApp.prototype.getPosixGroupListController =
function(viewId, newController) {
	if(!viewId)
		viewId = ZaZimbraAdmin._POSIX_GROUP_LIST;
			
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	} else if (viewId || newController) {
		var c = this._controllers[viewId] = new ZaPosixGroupListController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.getPosixGroupController =
function(viewId) {
	
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	} else{
		var c = this._controllers[viewId] = new ZaPosixGroupController(this._appCtxt, this._container, this);
		var ctrl = this.getPosixGroupListController();

		c.addChangeListener(new AjxListener(ctrl, ctrl.handleChange));
		c.addCreationListener(new AjxListener(ctrl, ctrl.handleCreation));						
		c.addRemovalListener(new AjxListener(ctrl, ctrl.handleRemoval));
		return c ;
	}
}
	

zimbra_posixaccount_ext.posixGroupListTreeListener = function (ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getPosixGroupListController(),ZaPosixGroupListController.prototype.show, ZaPosixGroup.getAll());
	} else {					
		ZaApp.getInstance().getPosixGroupListController().show(ZaPosixGroup.getAll());
	}
}

zimbra_posixaccount_ext.posixGroupTreeListener = function (ev) {
	var currentPosixGroup = ZaApp.getInstance().getPosixGroupList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID));	
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getPosixGroupController(),ZaPosixGroupController.prototype.show, currentPosixGroup);
	} else {					
		ZaApp.getInstance().getPosixGroupController().show(currentPosixGroup);
	}
}
	
zimbra_posixaccount_ext.ovTreeModifier = function (tree) {
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.POSIX_GROUPS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		if(!this._configTi) {
			this._configTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
			this._configTi.enableSelection(false);
			this._configTi.setText(ZaMsg.OVP_configuration);
			this._configTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SYS_CONFIG);		
		}
		
		this._posixGroupTi = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem"});
		this._posixGroupTi.setText(zimbra_posixaccount.PosixGroups);
		this._posixGroupTi.setImage("ZimbraIcon");
		this._posixGroupTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._POSIX_GROUP_LIST);	
		
		try {
			//add server statistics nodes
			var posixGroupList = ZaApp.getInstance().getPosixGroupList(true).getArray();
			if(posixGroupList && posixGroupList.length) {
				var cnt = posixGroupList.length;
				for(var ix=0; ix< cnt; ix++) {
					var ti1 = new DwtTreeItem({parent:this._posixGroupTi,className:"AdminTreeItem"});			
					ti1.setText(posixGroupList[ix].name);	
					ti1.setImage("Domain");
					ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._POSIX_GROUP_VIEW);
					ti1.setData(ZaOverviewPanelController._OBJ_ID, posixGroupList[ix].id);
				}
			}
		} catch (ex) {
			this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
		}
		

		if(ZaOverviewPanelController.overviewTreeListeners) {
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._POSIX_GROUP_LIST] = zimbra_posixaccount_ext.posixGroupListTreeListener;
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._POSIX_GROUP_VIEW] = zimbra_posixaccount_ext.posixGroupTreeListener;							
		}
	}
}

if(ZaOverviewPanelController.treeModifiers)
	ZaOverviewPanelController.treeModifiers.push(zimbra_posixaccount_ext.ovTreeModifier);
	