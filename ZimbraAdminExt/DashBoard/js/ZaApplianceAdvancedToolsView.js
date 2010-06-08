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
* @class ZaApplianceAdvancedToolsView
* @contructor
* @param parent
* @param entry
* @author Greg Solovyev
**/
ZaApplianceAdvancedToolsView = function(parent, entry) {
	ZaTabView.call(this, parent, "ZaApplianceAdvancedToolsView");
	this.TAB_INDEX = 0;	
	this.initForm(ZaApplianceAdvancedTools.myXModel,this.getMyXForm(entry), null);
}

ZaApplianceAdvancedToolsView.prototype = new ZaTabView();
ZaApplianceAdvancedToolsView.prototype.constructor = ZaApplianceAdvancedToolsView;
ZaTabView.XFormModifiers["ZaApplianceAdvancedToolsView"] = new Array();

ZaApplianceAdvancedToolsView.prototype.getTitle =
function () {
	return ZaMsg.GlobalConfig_view_title;
}

ZaApplianceAdvancedToolsView.onRepeatRemove =
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
	form.parent.setDirty(true);
}

ZaApplianceAdvancedToolsView.prototype.getTabIcon =
function () {
	return "GlobalSettings";
}

ZaApplianceAdvancedToolsView.prototype.getTabTitle =
function () {
	return this.getTitle();
}

ZaApplianceAdvancedToolsView.prototype.getTabToolTip =
function () {
	return this.getTitle ();
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaItem object to display
**/
ZaApplianceAdvancedToolsView.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();
	this._containedObject.type = entry.type;
	this._containedObject.name = entry.name;
	
	if(entry.id)
		this._containedObject.id = entry.id;
	
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
	this._containedObject[ZaApplianceAdvancedTools.license] = {};
	if(entry[ZaApplianceAdvancedTools.license] && entry[ZaApplianceAdvancedTools.license].attrs) {
		
		for (var a in entry[ZaApplianceAdvancedTools.license].attrs) {
			if(entry[ZaApplianceAdvancedTools.license].attrs[a] instanceof Array) {
				this._containedObject[ZaApplianceAdvancedTools.license][a] = [].concat(entry[ZaApplianceAdvancedTools.license].attrs[a]);
			} else {
				this._containedObject[ZaApplianceAdvancedTools.license][a] = entry[ZaApplianceAdvancedTools.license].attrs[a];
			}
		}	
	}
	
	if(entry[ZaApplianceAdvancedTools.A_server]) {
		this._containedObject[ZaApplianceAdvancedTools.A_server] = entry[ZaApplianceAdvancedTools.A_server];
	}
	if(entry[ZaApplianceAdvancedTools.A_certs]) {
		this._containedObject[ZaApplianceAdvancedTools.A_certs] = entry[ZaApplianceAdvancedTools.A_certs];
	}
	this._containedObject[ZaApplianceAdvancedTools.license][ZaApplianceLicense.InstallStatusCode] = 0;
	this._containedObject[ZaApplianceAdvancedTools.license][ZaApplianceLicense.InstallStatusMsg] = "";
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);
	
	this.formDirtyLsnr = new AjxListener(ZaApp.getInstance().getCurrentController(), ZaXFormViewController.prototype.handleXFormChange);
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, this.formDirtyLsnr);
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, this.formDirtyLsnr);	
	
	this.updateTab();
}

ZaApplianceAdvancedToolsView.myXFormModifier = function(xFormObject, entry) {
	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	var _tab3, _tab4;
	
    var tabBarChoices = [];
    var switchItems = [];

	_tab3 = ++this.TAB_INDEX;
    tabBarChoices.push ({value:_tab3, label:com_zimbra_dashboard.LicenseTabTitle});
	var case3 = {type:_ZATABCASE_, caseKey:_tab3, numCols:2, colSizes:["300px","*"],id:"appliance_settings_license_tab",
		items:[
            {type: _SPACER_, height: 10},
            //license file installation successful status, need to define relavant variable
            {type: _OUTPUT_, ref: ZaApplianceLicense.InstallStatusMsg, colSpan: "2",
                    width: "600px", align: _CENTER_, cssStyle: "border: solid thin",
                    visibilityChecks:[[XForm.checkInstanceValueNot,ZaApplianceLicense.InstallStatusCode,0]],bmolsnr:true,
                    visibilityChangeEventSources:[ZaApplianceLicense.InstallStatusCode]
            },
            //title
            {type: _OUTPUT_, value: com_zimbra_dashboard.LI_INFO_TITLE , colSpan: "2", width: "600px", align: _CENTER_ },
            //Customer name
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_issuedToName, label: com_zimbra_dashboard.LB_company_name, align: _LEFT_,visibilityChecks:[],bmolsnr:true},
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_installType, label: com_zimbra_dashboard.LB_license_type, align: _LEFT_,visibilityChecks:[],bmolsnr:true},
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_licenseId, label: com_zimbra_dashboard.LB_license_id, align: _LEFT_,bmolsnr:true },
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_issuedOn, label: com_zimbra_dashboard.LB_issue_date, align: _LEFT_,
            	getDisplayValue:ZaApplianceLicense.getLocalDate,bmolsnr:true
            },
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_validFrom, label: com_zimbra_dashboard.LB_effective_date, align: _LEFT_,
            	getDisplayValue:ZaApplianceLicense.getLocalDate,bmolsnr:true
            },
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_validUntil, label: com_zimbra_dashboard.LB_expiration_date, align: _LEFT_,
            	getDisplayValue:ZaApplianceLicense.getLocalDate,bmolsnr:true
            },
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_accountsLimit, label: com_zimbra_dashboard.LB_account_limit, align: _LEFT_,visibilityChecks:[],
            	getDisplayValue:function(val) {
            		var totalAccounts = this.getInstanceValue(ZaApplianceLicense.Info_TotalAccounts);
            		var retVal = val;
            		if (totalAccounts >= 0){
            			retVal += " " + AjxMessageFormat.format(com_zimbra_dashboard.LI_ACCOUNTS_USED,[totalAccounts]);
            		} else if (totalAccounts == -1){
            			retVal += " " + com_zimbra_dashboard.LI_ACCOUNT_COUNTING ;
            		} else{
            			retVal += " " + com_zimbra_dashboard.LI_ACCOUNT_COUNT_ERROR ;
            		}
            		return retVal;
            	},bmolsnr:true
            }
        ]
    };    
	switchItems.push(case3);
    
	_tab4 = ++this.TAB_INDEX;
    tabBarChoices.push ({value:_tab4, label:com_zimbra_dashboard.CertificatesTabTitle});
    var case4 = 	
    {type:_ZATABCASE_, caseKey:_tab4, id:"appliance_settings_form_certificates_tab", colSizes:["275px","275px"],numCols:2, items:[
		//{type:_OUTPUT_,ref:ZaApplianceAdvancedTools.A_serverName, label:com_zimbra_dashboard.CERT_SERVER_NAME,labelLocation:_LEFT_},
		{ type: _DWT_ALERT_,
			style: DwtAlert.WARNING,
			iconVisible: true, bmolsnr:true, 
			content: com_zimbra_dashboard.DidNotFindAnyCertificates,
			colSpan:2,
			visibilityChecks:[[XForm.checkInstanceValueEmty,ZaApplianceAdvancedTools.A_certs]],ref:null
		},
		{type:_REPEAT_,ref:ZaApplianceAdvancedTools.A_certs,	showAddButton:false,colSpan:2,bmolsnr:true,
			visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaApplianceAdvancedTools.A_certs]],enableDisableChecks:[],
			showRemoveButton:false,
			showAddOnNextRow:false,
			items:[
			    {type:_ZAGROUP_, colSizes:["200px","*"], items:[
					{ type: _OUTPUT_,bmolsnr:true,
						style: DwtAlert.INFORMATION,colSpan:2,
						visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaApplianceSSLCert.A_type]],ref:ZaApplianceSSLCert.A_type,
						getDisplayValue:function(val) {
							return AjxMessageFormat.format(com_zimbra_dashboard.Cert_Service_title, val);
						},
						label:null,labelLocation:_NONE_
					},
					{type:_OUTPUT_,ref:ZaApplianceSSLCert.A_subject, label:com_zimbra_dashboard.CERT_INFO_SUBJECT,labelLocation:_LEFT_,visibilityChecks:[], bmolsnr:true},
					{type:_OUTPUT_,ref:ZaApplianceSSLCert.A_issuer, label:com_zimbra_dashboard.CERT_INFO_ISSUER,labelLocation:_LEFT_,visibilityChecks:[], bmolsnr:true},
					{type:_OUTPUT_,ref:ZaApplianceSSLCert.A_validation_days_ro, label:com_zimbra_dashboard.CERT_INFO_VALIDATION_DAYS,labelLocation:_LEFT_,visibilityChecks:[], bmolsnr:true},
					{type:_OUTPUT_,ref:ZaApplianceSSLCert.A_subject_alt, label:com_zimbra_dashboard.CERT_INFO_SubjectAltName,labelLocation:_LEFT_,visibilityChecks:[], bmolsnr:true}
				]}
			]
		}
	]}
    switchItems.push(case4);
    xFormObject.items = [
		{type:_TAB_BAR_,  ref:ZaModel.currentTab,id:"xform_tabbar",
		 	containerCssStyle: "padding-top:0px",
			choices: tabBarChoices 
		},
		{type:_SWITCH_, items: switchItems}
	];
};
ZaTabView.XFormModifiers["ZaApplianceAdvancedToolsView"].push(ZaApplianceAdvancedToolsView.myXFormModifier);
