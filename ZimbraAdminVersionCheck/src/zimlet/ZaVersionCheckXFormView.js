ZaVersionCheckXFormView = function(parent, entry) {
	ZaTabView.call(this, parent, "ZaVersionCheckXFormView");
	this.TAB_INDEX = 0;	
	this.criticalChoices = new XFormChoices([{value:"1",label:com_zimbra_adminversioncheck.Critical},
		{value:"0",label:com_zimbra_adminversioncheck.NotCritical}], XFormChoices.OBJECT_LIST, "value", "label");
	this.initForm(ZaVersionCheck.myXModel,this.getMyXForm(entry), null);
}

ZaVersionCheckXFormView.prototype = new ZaTabView();
ZaVersionCheckXFormView.prototype.constructor = ZaVersionCheckXFormView;
ZaTabView.XFormModifiers["ZaVersionCheckXFormView"] = new Array();

ZaVersionCheckXFormView.prototype.getTitle =
function () {
	return com_zimbra_adminversioncheck.VersionCheck_view_title;
}

ZaVersionCheckXFormView.prototype.getTabIcon =
function () {
	return "Refresh";
}

ZaVersionCheckXFormView.prototype.getTabTitle =
function () {
	return this.getTitle();
}

ZaVersionCheckXFormView.prototype.getTabToolTip =
function () {
	return this.getTitle ();
}

ZaVersionCheckXFormView.CONFIG_TAB_ATTRS = [];
ZaVersionCheckXFormView.CONFIG_TAB_RIGHTS = [];

ZaVersionCheckXFormView.STATUS_TAB_ATTRS = [];
ZaVersionCheckXFormView.STATUS_TAB_RIGHTS = [];

ZaVersionCheckXFormView.checkLastAttemptFailed = function() {
	return (this.getInstanceValue(ZaVersionCheck.A_zimbraVersionCheckLastAttempt) != this.getInstanceValue(ZaVersionCheck.A_zimbraVersionCheckLastSuccess));	
}

ZaVersionCheckXFormView.prototype.setObject =
function(entry) {
    this._containedObject = new Object();
	this._containedObject.attrs = new Object();
    for (var a in entry.attrs) {
		var modelItem = this._localXForm.getModel().getItem(a) ;
        if ((modelItem != null && modelItem.type == _LIST_) || (entry.attrs[a] != null && entry.attrs[a] instanceof Array)) {  
        	//need deep clone
            this._containedObject.attrs [a] =
                    ZaItem.deepCloneListItem (entry.attrs[a]);
        } else {
            this._containedObject.attrs[a] = entry.attrs[a];
        }
	}
	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type;

	if(entry.rights)
		this._containedObject.rights = entry.rights;

	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
	
	if(entry.id)
		this._containedObject.id = entry.id;
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
			
	this._containedObject[ZaVersionCheck.A_zimbraVersionCheckUpdates] = [];
	if(entry[ZaVersionCheck.A_zimbraVersionCheckUpdates]) {
		this._containedObject[ZaVersionCheck.A_zimbraVersionCheckUpdates] = entry[ZaVersionCheck.A_zimbraVersionCheckUpdates]; 
	}
	
	this._localXForm.setInstance(this._containedObject);
	//update the tab
    if (!appNewUI)
	    this.updateTab();
}

ZaVersionCheckXFormView.myXFormModifier = function(xFormObject, entry) {
	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	var _tab1, _tab2;
	_tab1 = ++this.TAB_INDEX;
	_tab2 = ++this.TAB_INDEX;
    var tabBarChoices = [
    	{value:_tab1, label:com_zimbra_adminversioncheck.TABT_ConfigPage},
    	{value:_tab2, label:com_zimbra_adminversioncheck.TABT_UpdatesPage}
    ] ;
    var case1 = {type:_ZATABCASE_, caseKey:_tab1,
		colSizes:["auto"],numCols:1,
		items:[
			{type:_ZAGROUP_,
				items:[
					{ref:ZaVersionCheck.A_zimbraVersionCheckServer, type: _OSELECT1_, 
						label:com_zimbra_adminversioncheck.VersionCheckServer,
						editable:false, choices: ZaApp.getInstance().getServerIdListChoices(), 
						enableDisableChecks:[],
						visibilityChecks:[],
						tableCssStyle: "height: 15px",
						required:true
				  	},
					{ type: _DWT_ALERT_,colSpan:"*",
					  containerCssStyle: "padding-bottom:0px",
					  style: DwtAlert.WARNING,
					  iconVisible: false,
					  ref:ZaVersionCheck.A_zimbraVersionCheckServer,
					  getDisplayValue:function(val) {
					  	return AjxMessageFormat.format(com_zimbra_adminversioncheck.Alert_Crontab, [ZaApp.getInstance().getServerMap()[val]]);
					  },
					  bmolsnr:true
					  //content: com_zimbra_adminversioncheck.Alert_Crontab
					},				  	
				  	{ref:ZaVersionCheck.A_zimbraVersionCheckInterval,
				  		type:_LIFETIME_, 
				  		label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckInterval,labelLocation:_LEFT_
				  	},
					{ref:ZaVersionCheck.A_zimbraVersionCheckURL, type:_TEXTFIELD_, 
						label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckURL, width:250, required:true
  					},
  					{type:_CHECKBOX_, ref:ZaVersionCheck.A_zimbraVersionCheckSendNotifications,
  						label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckSendNotifications,
						trueValue:"TRUE",falseValue:"FALSE",
						enableDisableChecks:[],visibilityChecks:[]
					},
					{ type: _DWT_ALERT_,colSpan:"*",
						  containerCssStyle: "padding-bottom:0px",
						  style: DwtAlert.WARNING,
						  iconVisible: false, content:com_zimbra_adminversioncheck.NoteSendingNotificationFromAddress
					},
					{ref:ZaVersionCheck.A_zimbraVersionCheckNotificationEmail, type:_TEXTFIELD_, 
						label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckNotificationEmail, width:250,
						visibilityChecks:[],
						enableDisableChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckSendNotifications],
						enableDisableChecks:[[XForm.checkInstanceValue,ZaVersionCheck.A_zimbraVersionCheckSendNotifications,"TRUE"]]						
  					},
  					{ref:ZaVersionCheck.A_zimbraVersionCheckNotificationEmailFrom, type:_TEXTFIELD_,
						visibilityChecks:[],
						enableDisableChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckSendNotifications],
						enableDisableChecks:[[XForm.checkInstanceValue,ZaVersionCheck.A_zimbraVersionCheckSendNotifications,"TRUE"]],
  						label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckNotificationEmailFrom
  					},
					{ref:ZaVersionCheck.A_zimbraVersionCheckNotificationSubject, type:_TEXTFIELD_, 
						label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckNotificationSubject, width:250,
						visibilityChecks:[],
						enableDisableChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckSendNotifications],
						enableDisableChecks:[[XForm.checkInstanceValue,ZaVersionCheck.A_zimbraVersionCheckSendNotifications,"TRUE"]]						
  					},  					
					{type:_TEXTAREA_, ref:ZaVersionCheck.A_zimbraVersionCheckNotificationBody,
						label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckNotificationBody,
						visibilityChecks:[],
						enableDisableChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckSendNotifications],
						enableDisableChecks:[[XForm.checkInstanceValue,ZaVersionCheck.A_zimbraVersionCheckSendNotifications,"TRUE"]]
					}  					
                ]
			}
		]
	};    
    var case2 = {type:_ZATABCASE_, caseKey:_tab2,
    	colSizes:["auto"],numCols:1,
    	items:[
			{type:_ZAGROUP_,colSizes:["275","400"],items:[
	    		{ref:ZaVersionCheck.A_zimbraVersionCheckLastAttempt,type:_OUTPUT_,
	    			label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckLastAttempt,
	    			getDisplayValue:function(val) {
	    				return ZaVersionCheck.getAttemptTime(val);
	    			}
	    		},
	    		{ref:ZaVersionCheck.A_zimbraVersionCheckLastSuccess,type:_OUTPUT_,
	    			label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckLastSuccess,
	    			getDisplayValue:function(val) {
	    				return ZaVersionCheck.getAttemptTime(val);
	    			}	    			
	    		},
	    		{
	    			ref:ZaVersionCheck.A_zimbraVersionCheckUpdates,
	    			type:_REPEAT_,
	    			colSpan:"*",
	    			bmolsnr:true,
					align:_LEFT_,
					repeatInstance:"",
					showAddButton:false,
					showRemoveButton:false,
					showAddOnNextRow:false,
					visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaVersionCheck.A_zimbraVersionCheckUpdates]],
					visibilityChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckUpdates],
					items: [
						{type:_GROUP_,
							numCols:3,
							colSizes:["275","100","300"],				
							items:[
								{ 
									ref:ZaVersionCheck.A_zimbraVersionCheckUpdateShortversion, 
									type: _OUTPUT_, label:null,labelLocation:_NONE_, containerCssStyle:"text-align:right"
								},
								{ 
									ref:ZaVersionCheck.A_zimbraVersionCheckUpdateCritical, 
									type: _OUTPUT_, label:null,labelLocation:_NONE_,
									choices:this.criticalChoices, containerCssStyle:"text-align:center"
								},
								{ 
									ref:ZaVersionCheck.A_zimbraVersionCheckUpdateUpdateURL, 
									type: _URL_, label:null,labelLocation:_NONE_, containerCssStyle:"text-align:center"
								}
							]
						}
					]
	    				
	    		}]
			}
    	]
    }
    var switchItems = [case1,case2];
    xFormObject.items = [
		{ type: _DWT_ALERT_,cssClass: "DwtTabTable",
			visibilityChecks:[ZaVersionCheckXFormView.checkLastAttemptFailed],
			visibilityChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckLastAttempt,ZaVersionCheck.A_zimbraVersionCheckLastSuccess],
			containerCssStyle: "padding-bottom:0px",
			style: DwtAlert.CRITICAL,
			iconVisible: true, 
			content: com_zimbra_adminversioncheck.WARNING_LAST_ATTEMPT_FAILED,
			colSpan:"*"
		},
		{ type: _DWT_ALERT_,cssClass: "DwtTabTable",
			visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaVersionCheck.A_zimbraVersionCheckUpdates]],
			visibilityChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckUpdates],
			containerCssStyle: "padding-bottom:0px",
			style: DwtAlert.WARNING,
			iconVisible: true, 
			content: com_zimbra_adminversioncheck.UpdatesAreAvailable,
			colSpan:"*"
		},
		{ type: _DWT_ALERT_,cssClass: "DwtTabTable",
			visibilityChecks:[[XForm.checkInstanceValueEmty,ZaVersionCheck.A_zimbraVersionCheckUpdates]],
			visibilityChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckUpdates],
			containerCssStyle: "padding-bottom:0px",
			style: DwtAlert.INFORMATION,
			iconVisible: true, 
			content: com_zimbra_adminversioncheck.ServerIsUpToDate,
			colSpan:"*"
		},	
		{type:_TAB_BAR_,  ref:ZaModel.currentTab,id:"xform_tabbar",
		 	containerCssStyle: "padding-top:0px",
			choices: tabBarChoices 
		},
		{type:_SWITCH_, items: switchItems}
	];	
}
ZaTabView.XFormModifiers["ZaVersionCheckXFormView"].push(ZaVersionCheckXFormView.myXFormModifier);
