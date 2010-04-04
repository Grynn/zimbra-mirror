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

/**
* @class ZaDashBoardView
* @contructor ZaDashBoardView
* @param parent
* @author Greg Solovyev
**/
ZaDashBoardView = function(parent) {
	if (arguments.length == 0) return;
	ZaTabView.call(this, parent,"ZaDashBoardView");
	this.setScrollStyle(Dwt.SCROLL);
	this.TAB_INDEX = 0;	
	var item = {};
	this.initForm(ZaDashBoard.myXModel, this.getMyXForm({}), item);
//	this._createHTML();
}
ZaDashBoardView.mainHelpPage = "administration_console_help.htm";
ZaDashBoardView.prototype = new ZaTabView();
ZaDashBoardView.prototype.constructor = ZaDashBoardView;
ZaTabView.XFormModifiers["ZaDashBoardView"] = new Array();

ZaDashBoardView.prototype.getTabTitle =
function () {
	return com_zimbra_dashboard.DashBoard_view_title;
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaItem object to display
**/
ZaDashBoardView.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject[ZaDashBoard.settingsTab] = 1;
	this._containedObject.attrs = new Object();
	this._containedObject.type = entry.type ;

	if(entry.rights)
		this._containedObject.rights = entry.rights;
	
	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
		
	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = [].concat(entry.attrs[a]);
		} else {
			this._containedObject.attrs[a] = entry.attrs[a];
		}
	}	
	this._containedObject[ZaSearch.A_query] = "";
	this._localXForm.setInstance(this._containedObject);
	
	this.formDirtyLsnr = new AjxListener(ZaApp.getInstance().getCurrentController(), ZaXFormViewController.prototype.handleXFormChange);
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, this.formDirtyLsnr);
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, this.formDirtyLsnr);	
	
	this.updateTab();
}

ZaDashBoardView.openProfilesView = function() {
	ZaApp.getInstance().getCosListController().show(true,true);
}

ZaDashBoardView.openDomainsView = function() {
	ZaApp.getInstance().getDomainListController().show(true,true);
}

ZaDashBoardView.openAddressesView = function() {
	var query = "";
	var params = {};
	var searchListController = ZaApp.getInstance().getSearchListController() ;
	searchListController._isAdvancedSearch = false;
	params.types = [ZaSearch.ACCOUNTS,ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.RESOURCES];
	searchListController._searchFieldInput = query ;
	params.query = ZaSearch.getSearchByNameQuery(query, params.types);      
	ZaApp.getInstance().getSearchListController()._currentQuery = params.query ;
	searchListController._currentQuery = params.query ;
	this._isSearchButtonClicked = false ;
	ZaSearchListController.prototype._searchFieldCallback.call(searchListController,params);
}

ZaDashBoardView.openNewAccountDialog = function() {
	try {
		EmailAddr_XFormItem.resetDomainLists.call(ZaApp.getInstance().getCurrentController()) ;
		var newAccount = new ZaAccount();
		newAccount.loadNewObjectDefaults("name", ZaSettings.myDomainName);
		
		if(!ZaApp.getInstance().dialogs["newAccountWizard"])
			ZaApp.getInstance().dialogs["newAccountWizard"] = new ZaNewAccountXWizard(DwtShell.getShell(window),newAccount);	
        else { //update the account type if needed
            ZaApp.getInstance().dialogs["newAccountWizard"].updateAccountType () ;    
        }

		ZaApp.getInstance().dialogs["newAccountWizard"].setObject(newAccount);
		ZaApp.getInstance().dialogs["newAccountWizard"].popup();
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaAccountListController.prototype._newAccountListener", null, false);
	}	
	
}


ZaDashBoardView.openNewResourceDialog = function() {
	try {
		EmailAddr_XFormItem.resetDomainLists.call (ZaApp.getInstance().getCurrentController());
		var newResource = new ZaResource();
		newResource.loadNewObjectDefaults("name", ZaSettings.myDomainName);	
		if(!ZaApp.getInstance().dialogs["newResourceWizard"])
			ZaApp.getInstance().dialogs["newResourceWizard"] = new ZaNewResourceXWizard(DwtShell.getShell(window));	

		ZaApp.getInstance().dialogs["newResourceWizard"].setObject(newResource);
		ZaApp.getInstance().dialogs["newResourceWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newResourceListener", null, false);
	}
}

ZaDashBoardView.openNewDistributionListView = function() {
	try {
		EmailAddr_XFormItem.resetDomainLists.call (ZaApp.getInstance().getCurrentController());
		var newDL = new ZaDistributionList();
		newDL.rights = {};
		newDL._defaultValues = {attrs:{}};
		newDL.loadNewObjectDefaults("name", ZaSettings.myDomainName);	
		newDL.rights[ZaDistributionList.RENAME_DL_RIGHT]=true;
		newDL.rights[ZaDistributionList.REMOVE_DL_MEMBER_RIGHT]=true;
		newDL.rights[ZaDistributionList.ADD_DL_MEMBER_RIGHT]=true;
		ZaApp.getInstance().getDistributionListController().show(newDL);
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newDistributionListListener", null, false);
	}

};
	
//new button was pressed
ZaDashBoardView.openNewProfileView = function() {
	var newCos = new ZaCos();
	//load default COS
	var defCos = ZaCos.getCosByName("default");
	newCos.loadNewObjectDefaults();
	newCos.rights[ZaCos.RENAME_COS_RIGHT]=true;
	newCos.rights[ZaCos.CREATE_COS_RIGHT]=true;
	//copy values from default cos to the new cos
	for(var aname in defCos.attrs) {
		if( (aname == ZaItem.A_objectClass) || (aname == ZaItem.A_zimbraId) || (aname == ZaCos.A_name) || (aname == ZaCos.A_description) || (aname == ZaCos.A_notes) || (aname == ZaItem.A_zimbraCreateTimestamp))
			continue;			
		newCos.attrs[aname] = defCos.attrs[aname];
	}
	
	ZaApp.getInstance().getCosController().show(newCos);
}

ZaDashBoardView.openBulkProvisionDialog = function () {
     try {
		if(!ZaApp.getInstance().dialogs["bulkProvisionWizard"]) {
			ZaApp.getInstance().dialogs["bulkProvisionWizard"] = new ZaBulkProvisionWizard(DwtShell.getShell(window));
		}
		ZaApp.getInstance().dialogs["bulkProvisionWizard"].setObject(new ZaBulkProvision());
		ZaApp.getInstance().dialogs["bulkProvisionWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._bulkProvisionListener", null, false);
	}
}

ZaDashBoardView.searchAddresses = function () {
	var query = this.getInstanceValue(ZaSearch.A_query);
	var params = {};
	var searchListController = ZaApp.getInstance().getSearchListController() ;
	searchListController._isAdvancedSearch = false;
	params.types = [ZaSearch.ACCOUNTS,ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.RESOURCES];
	searchListController._searchFieldInput = query ;
	params.query = ZaSearch.getSearchByNameQuery(query, params.types);      
	ZaApp.getInstance().getSearchListController()._currentQuery = params.query ;
	searchListController._currentQuery = params.query ;
	this._isSearchButtonClicked = false ;
	ZaSearchListController.prototype._searchFieldCallback.call(searchListController,params);	
}

ZaDashBoardView.restartMailboxD = function () {
	var soapDoc = AjxSoapDoc.create("RestartMailboxDRequest", ZaZimbraAdmin.URN, null);
	var server = soapDoc.set("server","gsolovyev-mbp");
	server.setAttribute("by", "name");
	
	var busyid = Dwt.getNextId ();
	var callback = new AjxCallback(this, ZaDashBoardView.restartCallback, {busyid:busyid});

	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_FLUSH_CACHE,
		busyid:busyid
	}
		
	var reqParams = {
		serverUri:"https://localhost:7070/service/",
		soapDoc: soapDoc,
		asyncMode:true,
		callback: callback
	}
	ZaRequestMgr.invoke(reqParams, reqMgrParams) ;	
	//RestartMailboxDRequest
}

ZaDashBoardView.restartCallback = function() {
	
}

ZaDashBoardView.myXFormModifier = function(xFormObject,entry) {	
    var tabBarChoices = [];
    var switchItems = [];
    var _tab1 = ++this.TAB_INDEX;
    var _tab2 = ++this.TAB_INDEX;
    var _tab3 = ++this.TAB_INDEX;
    tabBarChoices.push({value:_tab1, label:com_zimbra_dashboard.TABT_Attachments});
    tabBarChoices.push({value:_tab2, label:com_zimbra_dashboard.TABT_Advanced});
    //tabBarChoices.push({value:_tab3, label:com_zimbra_dashboard.TABT_Advanced});
    var case1 = {type:_ZATABCASE_, caseKey:_tab1, id:"dashboard_form_attachment_tab", numCols:2, colSizes: ["300px","500px"], 
    		caseVarRef:ZaDashBoard.settingsTab,visibilityChangeEventSources:[ZaDashBoard.settingsTab],hMargin:40,
    		items:[		
			{type:_OUTPUT_,colSpan:2,value:"Some description of what this section is about with a link to help topic about settings"},    		       
			{type:_GROUP_,  numCols: 1,
				items:[				       
				    {type:_SPACER_, height:"10"},
    				{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder",  //height: 400,
						items:[
							{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
						   		items: [
									{type:_OUTPUT_, value:ZaMsg.NAD_GlobalBlockedExtensions, cssClass:"RadioGrouperLabel"},
									{type:_CELLSPACER_}
								]
							},
							{ref:ZaGlobalConfig.A_zimbraMtaBlockedExtension, type:_DWT_LIST_, height:"200px",
								cssClass: "VAMIDLTarget", 
								onSelection:GlobalConfigXFormView.blockedExtSelectionListener
							},
							{type:_SPACER_, height:"5"},
							{type:_GROUP_, width:"100%", numCols:2, colSizes:["100px","100px"],
								items:[
									{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:120,
										onActivate:"GlobalConfigXFormView.removeAllExt.call(this)",
									   	enableDisableChecks:[GlobalConfigXFormView.shouldEnableRemoveAllButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
								   		enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaBlockedExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
									},
									{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:120,
									   	onActivate:"GlobalConfigXFormView.removeExt.call(this)",
									   	enableDisableChecks:[GlobalConfigXFormView.shouldEnableRemoveButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
								   		enableDisableChangeEventSources:[ZaGlobalConfig.A2_blocked_extension_selection,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
								    },
									
								]
							}
						]
    				}
				]
			 },
			 {type: _GROUP_,  numCols: 1,
				items: [				        
				    {type:_SPACER_, height:"10"},
					{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder",   //height: 400,
						items:[
							{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
							   	items: [
									{type:_OUTPUT_, value:ZaMsg.NAD_GlobalCommonExtensions, cssClass:"RadioGrouperLabel"},
									{type:_CELLSPACER_}
								]
							},
							{ref:ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension, type:_DWT_LIST_, height:"200px",
								cssClass: "VAMIDLSource",
								onSelection:GlobalConfigXFormView.commonExtSelectionListener
							},
						    {type:_SPACER_, height:"5"},
						    {type:_GROUP_, numCols:2, colSizes:["220px","220px"],
								items: [
								   	{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddSelected, 
										onActivate:"GlobalConfigXFormView.addCommonExt.call(this)",
										enableDisableChecks:[GlobalConfigXFormView.shouldEnableAddButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
										enableDisableChangeEventSources:[ZaGlobalConfig.A2_common_extension_selection,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
									},
								    {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, 
										onActivate:"GlobalConfigXFormView.addAllCommonExt.call(this)",
										enableDisableChecks:[GlobalConfigXFormView.shouldEnableAddAllButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
										enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
									}
								 ]
						    },
						    {type:_SPACER_},	
						    {type:_GROUP_, numCols:3, colSizes:["110px","110px","220px"],
								items: [
									{type:_TEXTFIELD_, cssStyle:"width:60px;", ref:ZaGlobalConfig.A_zimbraNewExtension,
										label:ZaMsg.NAD_Attach_NewExtension,
										visibilityChecks:[],
										enableDisableChecks:[[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
										enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaBlockedExtension]
									},								    
									{type:_DWT_BUTTON_, label:ZaMsg.NAD_Attach_AddExtension, 
										onActivate:"GlobalConfigXFormView.addNewExt.call(this)",
										enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaGlobalConfig.A_zimbraNewExtension],[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
										enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraNewExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
									}
								 ]
						    }						    
						]
					  }
			    	]
			    }
			]};
    var case2 = {type:_ZATABCASE_, caseKey:_tab2, id:"dashboard_form_advanced_tab", numCols:2, colSizes: ["200px","auto"],
    		caseVarRef:ZaDashBoard.settingsTab,visibilityChangeEventSources:[ZaDashBoard.settingsTab],hMargin:40,
    		items:[ 
    	{type:_OUTPUT_,colSpan:2,value:"Some description of what this section is about with a link to help topic about settings"},    		       
    	{type:_SPACER_, height:"10",colSpan:2},
    	{ ref: ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, type: _CHECKBOX_,
    		label: ZaMsg.LBL_zimbraMtaBlockedExtensionWarnRecipient,
    		trueValue:"TRUE", falseValue:"FALSE"
    	},	    		       
	    { ref: ZaGlobalConfig.A_zimbraMtaRelayHost, type: _REPEAT_,
  	  		label: ZaMsg.NAD_MTA_RelayMTA,
	  		labelLocation:_LEFT_,
	  		align:_LEFT_,
	  		repeatInstance:"",
			showAddButton:true, 
			showRemoveButton:true, 
			showAddOnNextRow:true,
			addButtonLabel:ZaMsg.Add_zimbraSmtpHostname, 
			removeButtonLabel:ZaMsg.Remove_zimbraSmtpHostname,
			removeButtonCSSStyle: "margin-left: 50px",
	  		items: [
				{ref:".",label:null,labelLocation:_NONE_,
					type:_HOSTPORT_,
					onClick: "ZaController.showTooltip",
			 		toolTipContent: ZaMsg.tt_MTA_RelayMTA,
			 		onMouseout: "ZaController.hideTooltip"
				}
			]
  		}                                                                                                                   
     ]}; 
    switchItems.push(case1);
    switchItems.push(case2);
    //switchItems.push(case3);
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	xFormObject.numCols=1;
	xFormObject.colSizes=["auto"];
	xFormObject.items = [
		{type:_TOP_GROUPER_, label:com_zimbra_dashboard.Services, id:"dashboard_settings_group",
			numCols:4, colSizes:["auto","auto","auto","auto"],visibilityChecks:[],enableDisableChecks:[],
			items:[
			       {type:_GROUP_,numCols:5,colSizes:["auto","auto","auto","auto","auto"],
			    	   items:[
			    	          {type:_AJX_IMAGE_,src:"Check", visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_LDAP+"status",1]]},
			    	          {type:_AJX_IMAGE_,src:"Cancel", visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_LDAP+"status",1]]},
			    	          {type:_OUTPUT_, value:ZaStatus.SVC_LDAP},                            
			    	          {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Stop,enableDisableChecks:[],
			    	        	  visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_LDAP+"status",1]]
			    	          },
			        	      {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Start,enableDisableChecks:[],
			        	       	  visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_LDAP+"status",1]]
			        	      }
			    	    ]
			       },
			       {type:_GROUP_,numCols:5,colSizes:["auto","auto","auto","auto","auto"],
			    	   items:[
			    	          {type:_AJX_IMAGE_,src:"Check", visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_MAILBOX+"status",1]]},
			    	          {type:_AJX_IMAGE_,src:"Cancel", visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_MAILBOX+"status",1]]},
			    	          {type:_OUTPUT_, value:ZaStatus.SVC_MAILBOX},                            
			    	          {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Stop,enableDisableChecks:[],
			    	        	  visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_MAILBOX+"status",1]]
			    	          },
			        	      {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Start,enableDisableChecks:[],
			        	       	  visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_MAILBOX+"status",1]]
			        	      }
			    	    ]
			       },	
			       {type:_GROUP_,numCols:5,colSizes:["auto","auto","auto","auto","auto"],
			    	   items:[
			    	          {type:_AJX_IMAGE_,src:"Check", visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_MTA+"status",1]]},
			    	          {type:_AJX_IMAGE_,src:"Cancel", visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_MTA+"status",1]]},
			    	          {type:_OUTPUT_, value:ZaStatus.SVC_MTA},                            
			    	          {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Stop,enableDisableChecks:[],
			    	        	  visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_MTA+"status",1]]
			    	          },
			        	      {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Start,enableDisableChecks:[],
			        	       	  visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_MTA+"status",1]]
			        	      }
			    	    ]
			       },
			       {type:_GROUP_,numCols:5,colSizes:["auto","auto","auto","auto","auto"],
			    	   items:[
			    	          {type:_AJX_IMAGE_,src:"Check", visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_LOGGER+"status",1]]},
			    	          {type:_AJX_IMAGE_,src:"Cancel", visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_LOGGER+"status",1]]},
			    	          {type:_OUTPUT_, value:ZaStatus.SVC_LOGGER},                            
			    	          {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Stop,enableDisableChecks:[],
			    	        	  visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_LOGGER+"status",1]]
			    	          },
			        	      {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Start,enableDisableChecks:[],
			        	       	  visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_LOGGER+"status",1]]
			        	      }
			    	    ]
			       },
			       {type:_GROUP_,numCols:5,colSizes:["auto","auto","auto","auto","auto"],
			    	   items:[
			    	          {type:_AJX_IMAGE_,src:"Check", visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_CONVERTD+"status",1]]},
			    	          {type:_AJX_IMAGE_,src:"Cancel", visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_CONVERTD+"status",1]]},
			    	          {type:_OUTPUT_, value:ZaStatus.SVC_CONVERTD},                            
			    	          {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Stop,enableDisableChecks:[],
			    	        	  visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_CONVERTD+"status",1]]
			    	          },
			        	      {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Start,enableDisableChecks:[],
			        	       	  visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_CONVERTD+"status",1]]
			        	      }
			    	    ]
			       },			       
			       {type:_GROUP_,numCols:5,colSizes:["auto","auto","auto","auto","auto"],
			    	   items:[
			    	          {type:_AJX_IMAGE_,src:"Check", visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_AS+"status",1]]},
			    	          {type:_AJX_IMAGE_,src:"Cancel", visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_AS+"status",1]]},
			    	          {type:_OUTPUT_, value:ZaStatus.SVC_AS},                            
			    	          {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Stop,enableDisableChecks:[],
			    	        	  visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_AS+"status",1]]
			    	          },
			        	      {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Start,enableDisableChecks:[],
			        	       	  visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_AS+"status",1]]
			        	      }
			    	    ]
			       },
			       {type:_GROUP_,numCols:5,colSizes:["auto","auto","auto","auto","auto"],
			    	   items:[
			    	          {type:_AJX_IMAGE_,src:"Check", visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_AV+"status",1]]},
			    	          {type:_AJX_IMAGE_,src:"Cancel", visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_AV+"status",1]]},
			    	          {type:_OUTPUT_, value:ZaStatus.SVC_AV},                            
			    	          {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Stop,enableDisableChecks:[],
			    	        	  visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_AV+"status",1]]
			    	          },
			        	      {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Start,enableDisableChecks:[],
			        	       	  visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_AV+"status",1]]
			        	      }
			    	    ]
			       },	
			       {type:_GROUP_,numCols:5,colSizes:["auto","auto","auto","auto","auto"],
			    	   items:[
			    	          {type:_AJX_IMAGE_,src:"Check", visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_STATS+"status",1]]},
			    	          {type:_AJX_IMAGE_,src:"Cancel", visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_STATS+"status",1]]},
			    	          {type:_OUTPUT_, value:ZaStatus.SVC_STATS},                            
			    	          {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Stop,enableDisableChecks:[],
			    	        	  visibilityChecks:[[XForm.checkInstanceValue,"serviceMap/"+ZaStatus.SVC_STATS+"status",1]]
			    	          },
			        	      {type:_DWT_BUTTON_, label:com_zimbra_dashboard.Start,enableDisableChecks:[],
			        	       	  visibilityChecks:[[XForm.checkInstanceValueNot,"serviceMap/"+ZaStatus.SVC_STATS+"status",1]]
			        	      }
			    	    ]
			       }			       
			]
		}, 	                     
        {type:_TOP_GROUPER_, label:com_zimbra_dashboard.AddressesGrouper, id:"dashboard_addresses_group",
        	numCols: 4, colSizes: ["200px","200px","200px","200px"],visibilityChecks:[],enableDisableChecks:[],
	    	items:[  
	    	    {type:_OUTPUT_,colSpan:4,value:"Some description of what this section is about with a link to help topic about email addresses"},
		    	{type:_SPACER_,colSpan:4},	    	
	    	    {type:_GROUP_,colSpan:3,numCols:3,colSizes:["100px","600px","100px"],
	    	    	items:[
	    	    	    {type:_TEXTFIELD_,label:com_zimbra_dashboard.LookupAddress,labelLocation:_LEFT_,
	    	    	    	cssStyle:"overflow: hidden;", width:"100%",ref:ZaSearch.A_query,
	    	    	    	enableDisableChecks:[],visibilityChecks:[]
	    	    	    },
	    	    	    {type:_DWT_BUTTON_,label:ZaMsg.search,icon:"Search",enableDisableChecks:[],visibilityChecks:[],
	    	    	    	onActivate:ZaDashBoardView.searchAddresses}
	    	    	]	
	    	    },
	    	    {type:_DWT_BUTTON_, label:com_zimbra_dashboard.ManageAddresses, icon:"Account", width:80,
	    	    	onActivate:"ZaDashBoardView.openAddressesView();",enableDisableChecks:[],visibilityChecks:[]},
	    	    {type:_SPACER_,colSpan:4},
	    	    {type:_DWT_BUTTON_, label:com_zimbra_dashboard.ACTBB_BulkProvision, icon:"BulkProvision", width:80,
	    	    	onActivate:"ZaDashBoardView.openBulkProvisionDialog()",enableDisableChecks:[],visibilityChecks:[]},
	    	    {type:_DWT_BUTTON_, label:com_zimbra_dashboard.NewAccount, icon:"Account", width:80,
	    	    	onActivate:"ZaDashBoardView.openNewAccountDialog()",enableDisableChecks:[],visibilityChecks:[]},
	    	    {type:_DWT_BUTTON_, label:com_zimbra_dashboard.NewDL, icon:"DistributionList",width:80,
	    	    	onActivate:"ZaDashBoardView.openNewDistributionListView()",enableDisableChecks:[],visibilityChecks:[]},
	    	    {type:_DWT_BUTTON_, label:com_zimbra_dashboard.NewCalResource,icon:"Resource", width:80,
	    	    	onActivate:"ZaDashBoardView.openNewResourceDialog()",enableDisableChecks:[],visibilityChecks:[]},	    	    
	    	    {type:_SPACER_,colSpan:4},	    	    
	    	    {type:_DWT_BUTTON_, label:com_zimbra_dashboard.ManageProfiles, icon:"COS", width:80,
		    	   	onActivate:"ZaDashBoardView.openProfilesView()",enableDisableChecks:[],visibilityChecks:[]},
		    	{type:_DWT_BUTTON_, label:com_zimbra_dashboard.NewProfile, icon:"NewCOS", width:80,
		    	    	onActivate:"ZaDashBoardView.openNewProfileView()",enableDisableChecks:[],visibilityChecks:[]},	
		    	{type:_CELLSPACER_},{type:_CELLSPACER_},
		    	{type:_SPACER_,colSpan:4},	 
		    	{type:_DWT_BUTTON_, label:com_zimbra_dashboard.ManageDomains, icon:"Domain", width:80,
	    	    	onActivate:"ZaDashBoardView.openDomainsView()",enableDisableChecks:[],visibilityChecks:[]
	    	    },
	    	    {type:_DWT_BUTTON_, label:com_zimbra_dashboard.NewDomain, icon:"Domain", width:80,
	    	    	onActivate:"alert()",enableDisableChecks:[],visibilityChecks:[]},
	    	    {type:_SPACER_,colSpan:2}
	    	]
        },
        {type:_TOP_GROUPER_, label:com_zimbra_dashboard.Settings, id:"dashboard_settings_group",
        	numCols: 1, colSizes: ["auto"],visibilityChecks:[],enableDisableChecks:[],
	    	items:[
	    		{type:_TAB_BAR_,  ref:ZaDashBoard.settingsTab,id:"dashboard_settings_tabbar",
	    		 	containerCssStyle: "padding-top:0px",
	    			choices: tabBarChoices 
	    		},
	    		{type:_SWITCH_, items: switchItems, width:"800px"}	    	    
	    	]
        }          
	];
	
}
ZaTabView.XFormModifiers["ZaDashBoardView"].push(ZaDashBoardView.myXFormModifier);