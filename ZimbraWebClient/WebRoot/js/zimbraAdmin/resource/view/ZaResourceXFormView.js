/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* This class describes a view of a single resource Account, it will be called during the edit time
* @class ZaResourceXFormView
* @contructor
* @param parent {DwtComposite}
* @param app {ZaApp}
* @author Greg Solovyev
**/
function ZaResourceXFormView (parent, app) {
	ZaTabView.call(this, parent, app, "ZaResourceXFormView");	
	this.accountStatusChoices = [
		{value:ZaResource.ACCOUNT_STATUS_ACTIVE, label:ZaResource._ACCOUNT_STATUS[ZaResource.ACCOUNT_STATUS_ACTIVE]}, 
		{value:ZaResource.ACCOUNT_STATUS_CLOSED, label:ZaResource._ACCOUNT_STATUS[ZaResource.ACCOUNT_STATUS_CLOSED]},
		{value:ZaResource.ACCOUNT_STATUS_LOCKED, label: ZaResource._ACCOUNT_STATUS[ZaResource.ACCOUNT_STATUS_LOCKED]},
		{value:ZaResource.ACCOUNT_STATUS_MAINTENANCE, label:ZaResource._ACCOUNT_STATUS[ZaResource.ACCOUNT_STATUS_MAINTENANCE]}
	];
	
	this.resTypeChoices = [
		{value:ZaResource.RESOURCE_TYPE_LOCATION, label:ZaResource.getResTypeLabel ( ZaResource.RESOURCE_TYPE_LOCATION)}, 
		{value:ZaResource.RESOURCE_TYPE_EQUIPMENT, label:ZaResource.getResTypeLabel ( ZaResource.RESOURCE_TYPE_EQUIPMENT)}
	];	
	
	this.schedulePolicyChoices = [
		{value:ZaResource.SCHEDULE_POLICY_ACCEPT_UNLESS_BUSY, label:ZaResource.getSchedulePolicyLabel ( ZaResource.SCHEDULE_POLICY_ACCEPT_UNLESS_BUSY)},
		{value:ZaResource.SCHEDULE_POLICY_ACCEPT_ALL, label:ZaResource.getSchedulePolicyLabel ( ZaResource.SCHEDULE_POLICY_ACCEPT_ALL)}
	];
	
	this.initForm(ZaResource.myXModel,this.getMyXForm());
	this._localXForm.setController(this._app);	
	//this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaResourceXFormView.prototype.handleXFormChange));
	//this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaResourceXFormView.prototype.handleXFormChange));	
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(app.getResourceController(), ZaResourceController.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(app.getResourceController(), ZaResourceController.prototype.handleXFormChange));	
	
	this._helpURL = ZaResourceXFormView.helpURL;
	
}

ZaResourceXFormView.prototype = new ZaTabView();
ZaResourceXFormView.prototype.constructor = ZaResourceXFormView;
ZaTabView.XFormModifiers["ZaResourceXFormView"] = new Array();
ZaResourceXFormView.TAB_INDEX=0;

/*
ZaResourceXFormView.prototype.setDirty = 
function (isD) {
	var saveButton = this._app.getCurrentController()._toolbar.getButton(ZaOperation.SAVE) ;
	if(isD)
		saveButton.setEnabled(true);
	else
		saveButton.setEnabled(false);
}*/

/*
ZaResourceXFormView.prototype.handleXFormChange = 
function () {
	var saveButton = this._app.getCurrentController()._toolbar.getButton(ZaOperation.SAVE) ;
	if (saveButton) {
		if(this._localXForm.hasErrors()) {
			saveButton.setEnabled(false);
		} else {
			if(this._containedObject.attrs[ZaResource.A_displayname] && this._containedObject[ZaResource.A_name].indexOf("@") > 0)
				saveButton.setEnabled(true);
		}
	}
}*/

/**
* Sets the object contained in the view
* @param entry - {ZaResource} object to display
**/
ZaResourceXFormView.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();


	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = new Array();
			for(var aa in entry.attrs[a]) {
				this._containedObject.attrs[a][aa] = entry.attrs[a][aa];
			}
		} else {
			this._containedObject.attrs[a] = entry.attrs[a];
		}
	}
	this._containedObject.name = entry.name;
	if(entry.id)
		this._containedObject.id = entry.id;
		
	if(ZaSettings.COSES_ENABLED) {	
		var cosList = this._app.getCosList().getArray();
		
		/**
		* If this account does not have a COS assigned to it - assign default COS
		**/
		if(this._containedObject.attrs[ZaResource.A_COSId]) {	
			for(var ix in cosList) {
				/**
				* Find the COS assigned to this account 
				**/
				if(cosList[ix].id == this._containedObject.attrs[ZaResource.A_COSId]) {
					this._containedObject.cos = cosList[ix];
					break;
				}
			}
		}
		if(!this._containedObject.cos) {
			/**
			* We did not find the COS assigned to this account,
			* this means that the COS was deleted or wasn't assigned, therefore assign default COS to this account
			**/
			for(var i in cosList) {
				/**
				* Find the COS assigned to this account 
				**/
				if(cosList[i].name == "default") {
					this._containedObject.cos = cosList[i];
					this._containedObject.attrs[ZaResource.A_COSId] = cosList[i].id;										
					break;
				}
			}
			if(!this._containedObject.cos) {
				//default COS was not found - just assign the first COS
				if(cosList && cosList.length > 0) {
					this._containedObject.cos = cosList[0];
					this._containedObject.attrs[ZaResource.A_COSId] = cosList[0].id;					
				}
			}
		}
		if(!this._containedObject.cos) {
			this._containedObject.cos = cosList[0];
		}	
	}
	
	if(ZaSettings.GLOBAL_CONFIG_ENABLED) {
		this._containedObject.globalConfig = this._app.getGlobalConfig();
	}
	
   	this._containedObject[ZaResource.A2_autodisplayname] = "FALSE";
   	
   	//set the value of the A_schedulePolicy
   	if (this._containedObject.attrs[ZaResource.A_zimbraCalResAutoAcceptDecline] == "TRUE"){
   		if (this._containedObject.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] == "TRUE"){
   			this._containedObject[ZaResource.A_schedulePolicy] = ZaResource.SCHEDULE_POLICY_ACCEPT_UNLESS_BUSY;	
   		}else if(this._containedObject.attrs[ZaResource.A_zimbraCalResAutoDeclineIfBusy] == "FALSE"){
   			this._containedObject[ZaResource.A_schedulePolicy] = ZaResource.SCHEDULE_POLICY_ACCEPT_ALL;   		
   		}else{
   			//unknown value
   			
   		}   	
   	}else{
   		//this is a delegation account
   		
   	}
   	
   				
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);
	
	//HC: enforce the dirty = false, so the save button after the save can be disabled.
	this.setDirty(false);
	
}

//generate the account name automatically
ZaResourceXFormView.generateAccountName =
function (instance, newValue) {
	var oldAccName = instance[ZaResource.A_name] ;
	var regEx = /[^a-zA-Z0-9_\-\.]/g ;
	instance[ZaResource.A_name] = newValue.replace(regEx, "") + oldAccName.substring(oldAccName.indexOf("@")) ;	
}

ZaResourceXFormView.onCOSChanged = 
function(value, event, form) {
	var cosList = form.getController().getCosList().getArray();
	var cnt = cosList.length;
	for(var i = 0; i < cnt; i++) {
		if(cosList[i].id == value) {
			form.getInstance().cos = cosList[i];
			break;
		}
	}
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	return value;
}

ZaResourceXFormView.onRepeatRemove = 
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
	form.parent.setDirty(true);
}
/**
* This method is added to the map {@link ZaTabView#XFormModifiers}
* @param xFormObject {Object} a definition of the form. This method adds/removes/modifies xFormObject to construct
* an Account view. 
**/
ZaResourceXFormView.myXFormModifier = function(xFormObject) {	

	var domainName;
	if(ZaSettings.DOMAINS_ENABLED)
		domainName = this._app.getDomainList().getArray()[0].name;
	else 
		domainName = ZaSettings.myDomainName;

	var headerItems = [{type:_AJX_IMAGE_, src:"Person_32", label:null, rowSpan:2},{type:_OUTPUT_, ref:ZaResource.A_displayname, label:null,cssClass:"AdminTitle", rowSpan:2}];
	if(ZaSettings.COSES_ENABLED) {
		headerItems.push({type:_OUTPUT_, ref:ZaResource.A_COSId, labelLocation:_LEFT_, label:ZaMsg.NAD_ClassOfService, choices:this._app.getCosListChoices()});
	}
	if(ZaSettings.SERVERS_ENABLED) {
		headerItems.push({type:_OUTPUT_, ref:ZaResource.A_mailHost, labelLocation:_LEFT_,label:ZaMsg.NAD_MailServer});
	}
	headerItems.push({type:_OUTPUT_,  ref:ZaResource.A_accountStatus, label:ZaMsg.NAD_AccountStatus, labelLocation:_LEFT_, choices:this.accountStatusChoices});
	headerItems.push({type:_OUTPUT_, ref:ZaResource.A_name, label:ZaMsg.NAD_Email, labelLocation:_LEFT_, required:false});
	headerItems.push({type:_OUTPUT_,  ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID});
	/* Quota doesn't apply to the resource account
	headerItems.push({type:_OUTPUT_, ref:ZaResource.A2_mbxsize, label:ZaMsg.usedQuota,
						getDisplayValue:function() {
							var val = this.getInstanceValue();
							if(!val) 
								val = "0 MB ";
							else {
								val = Number(val / 1048576).toFixed(3) + " MB ";
							}
							var quotaUsed = "";
							
							if(this.getInstance() != null)
								quotaUsed = this.getInstanceValue(ZaResource.A2_quota);
								
							val += ZaMsg.Of + " " + quotaUsed + " MB";									
							return val;
						}
					});
	*/

	var tabChoices = new Array();
	var _tab1 = ++ZaResourceXFormView.TAB_INDEX;
	var _tab2 = ++ZaResourceXFormView.TAB_INDEX;				
	
	tabChoices.push({value:_tab1, label:ZaMsg.TABT_ResourceProperties});
	tabChoices.push({value:_tab2, label:ZaMsg.TABT_ResLocationContact});

	var cases = [];
	var case1 = {type:_CASE_,  relevant:("instance[ZaModel.currentTab] == " + _tab1), height:"400px",  align:_LEFT_, valign:_TOP_};
	
	var case1Items = 
		[	{ref:ZaResource.A_displayname, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ResourceName,label:ZaMsg.NAD_ResourceName, labelLocation:_LEFT_, width: "200px" },			
			{ref:ZaResource.A_zimbraCalResType, type:_OSELECT1_, msgName:ZaMsg.NAD_ResType,label:ZaMsg.NAD_ResType, labelLocation:_LEFT_, choices:this.resTypeChoices},		
			{ref:ZaResource.A_name, type:_EMAILADDR_, msgName:ZaMsg.NAD_AccountName,label:ZaMsg.NAD_AccountName, labelLocation:_LEFT_}			
		];
	
	if(ZaSettings.COSES_ENABLED) {
		case1Items.push(
			{ref:ZaResource.A_COSId, type:_OSELECT1_, msgName:ZaMsg.NAD_ClassOfService,
				label:ZaMsg.NAD_ClassOfService, labelLocation:_LEFT_, 
				choices:this._app.getCosListChoices(), onChange:ZaResourceXFormView.onCOSChanged
			}
		);
	}
	case1Items.push({ref:ZaResource.A_accountStatus, type:_OSELECT1_, editable:false, msgName:ZaMsg.NAD_AccountStatus,label:ZaMsg.NAD_AccountStatus, labelLocation:_LEFT_, choices:this.accountStatusChoices});
	
	//scheduling policy
	case1Items.push({ref:ZaResource.A_schedulePolicy, type:_OSELECT1_, msgName:ZaMsg.NAD_ResType,label:ZaMsg.NAD_SchedulePolicy, labelLocation:_LEFT_, width: "300px", choices:this.schedulePolicyChoices});	
	case1Items.push({ref:ZaResource.A_zimbraCalResAutoDeclineRecurring, type:_CHECKBOX_, msgName:ZaMsg.NAD_DeclineRecurring,label:ZaMsg.NAD_DeclineRecurring,relevantBehavior:_HIDE_, 
					labelCssClass:"xform_label", align:_LEFT_, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE"});
	
	if(ZaSettings.SERVERS_ENABLED) {
		case1Items.push({type:_GROUP_, numCols:2, nowrap:true, label:ZaMsg.NAD_MailServer, labelLocation:_LEFT_,
							items: [
								{ ref: ZaResource.A_mailHost, type: _OSELECT1_, label: null, editable:true, choices: this._app.getServerListChoices2(), 
									relevant:"instance[ZaResource.A2_autoMailServer]==\"FALSE\" && form.getController().getServerListChoices2().getChoices().values.length != 0",
									relevantBehavior:_DISABLE_
							  	},
								{ref:ZaResource.A2_autoMailServer, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"}
							]
						});
	}
	
	//Notes
	case1Items.push({ref:ZaResource.A_notes, type:_TEXTAREA_, width: "300px", msgName:ZaMsg.NAD_Notes,label:ZaMsg.NAD_Notes, labelLocation:_LEFT_});
	
	case1.items = case1Items;
	cases.push(case1);
	
	
	var case2={type:_CASE_, numCols:2, relevant:("instance[ZaModel.currentTab] == " + _tab2),colSizes:["150px","300px"],
					items: [
						{ref:ZaResource.A_zimbraCalResSite, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Site,label:ZaMsg.NAD_Site, labelLocation:_LEFT_, width:150},
						{ref:ZaResource.A_zimbraCalResBuilding, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Building,label:ZaMsg.NAD_Building, labelLocation:_LEFT_, width:150},						
						{ref:ZaResource.A_zimbraCalResFloor, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Floor,label:ZaMsg.NAD_Floor, labelLocation:_LEFT_, width:150},						
						{ref:ZaResource.A_zimbraCalResRoom, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Room,label:ZaMsg.NAD_Room, labelLocation:_LEFT_, width:150},
						{ref:ZaResource.A_locationDisplayName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_LocationDisplayName,label:ZaMsg.NAD_LocationDisplayName, labelLocation:_LEFT_, width:150},
						{ref:ZaResource.A_street, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Street,label:ZaMsg.NAD_Street, labelLocation:_LEFT_, width:150},
						{ref:ZaResource.A_city, type:_TEXTFIELD_, msgName:ZaMsg.NAD_city ,label:ZaMsg.NAD_city, labelLocation:_LEFT_, width:150},
						{ref:ZaResource.A_state, type:_TEXTFIELD_, msgName:ZaMsg.NAD_state ,label:ZaMsg.NAD_state, labelLocation:_LEFT_, width:150},
						{ref:ZaResource.A_country, type:_TEXTFIELD_, msgName:ZaMsg.country ,label:ZaMsg.NAD_country, labelLocation:_LEFT_, width:150},
						{ref:ZaResource.A_zip, type:_TEXTFIELD_, msgName:ZaMsg.zip ,label:ZaMsg.NAD_zip, labelLocation:_LEFT_, width:150},
						{type:_SEPARATOR_, colSpan: "2"},
						{ref:ZaResource.A_zimbraCalResContactName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ContactName,label:ZaMsg.NAD_ContactName, labelLocation:_LEFT_, width:150},
						{ref:ZaResource.A_zimbraCalResContactEmail, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ContactEmail,label:ZaMsg.NAD_ContactEmail, labelLocation:_LEFT_, width:150},
						{ref:ZaResource.A_zimbraCalResContactPhone, type:_TEXTFIELD_, msgName:ZaMsg.NAD_ContactPhone,label:ZaMsg.NAD_ContactPhone, labelLocation:_LEFT_, width:150}
					]
				};
	cases.push(case2);
		
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	xFormObject.items = [
			{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", 
				items: [
					{type:_GROUP_,	numCols:4,colSizes:["90px","350px","100px","200px"],items:headerItems}
				],
				cssStyle:"padding-top:5px; padding-bottom:5px"
			},
			{type:_TAB_BAR_,  ref:ZaModel.currentTab,choices:tabChoices,cssClass:"ZaTabBar"},
			{type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:cases}
	];
};
ZaTabView.XFormModifiers["ZaResourceXFormView"].push(ZaResourceXFormView.myXFormModifier);