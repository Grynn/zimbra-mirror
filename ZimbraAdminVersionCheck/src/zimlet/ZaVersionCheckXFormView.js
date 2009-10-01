ZaVersionCheckXFormView = function(parent, entry) {
	ZaTabView.call(this, parent, "ZaVersionCheckXFormView");
	this.TAB_INDEX = 0;	
	this.criticalChoices = new XFormChoices([{value:"1",label:com_zimbra_adminversioncheck.Critical},
		{value:"0",label:com_zimbra_adminversioncheck.NotCritical}], XFormChoices.OBJECT_LIST, "value", "label");
	this.initForm(ZaVersionCheckXFormView.myXModel,this.getMyXForm(entry), null);
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

ZaVersionCheckXFormView.myXFormModifier = function(xFormObject, entry) {
	var _tab1, _tab2;
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
						editable:false, choices: ZaApp.getInstance().getServerListChoices(), 
						enableDisableChecks:[],
						visibilityChecks:[],
						tableCssStyle: "height: 15px"
				  	},
					{ref:ZaVersionCheck.A_zimbraVersionCheckURL, type:_TEXTFIELD_, 
						label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckURL, width:250
  					},
  					{type:_CHECKBOX_, ref:ZaVersionCheck.A_zimbraVersionCheckSendNotifications,
  						label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckSendNotifications,
						trueValue:"TRUE",falseValue:"FALSE",
						enableDisableChecks:[],visibilityChecks:[]
					},
					{ref:ZaVersionCheck.A_zimbraVersionCheckNotificationEmail, type:_TEXTFIELD_, 
						label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckNotificationEmail, width:250,
						visibilityChecks:[],
						enableDisableChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckSendNotifications],
						enableDisableChecks:[[XForm.checkInstanceValue,ZaVersionCheck.A_zimbraVersionCheckSendNotifications,"TRUE"]]						
  					},
  					{ref:ZaVersionCheck.A_zimbraVersionCheckNotificationEmailFrom, type:_DYNSELECT_,
  						dataFetcherClass:ZaSearch,dataFetcherTypes:[ZaSearch.ACCOUNTS],
  						dataFetcherAttrs:[ZaItem.A_zimbraId, ZaItem.A_cn, ZaAccount.A_name, ZaAccount.A_displayname, ZaAccount.A_mail],
						dataFetcherMethod:ZaSearch.prototype.dynSelectSearch,
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
			{ type: _DWT_ALERT_,
				visibilityChecks:[ZaVersionCheckXFormView.checkLastAttemptFailed],
				visibilityChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckLastAttempt,ZaVersionCheck.A_zimbraVersionCheckLastSuccess],
				containerCssStyle: "padding-bottom:0px",
				style: DwtAlert.CRITICAL,
				iconVisible: true, 
				content: com_zimbra_adminversioncheck.WARNING_LAST_ATTEMPT_FAILED,
				colSpan:"*"
			},
			{ type: _DWT_ALERT_,
				visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaVersionCheck.A_zimbraVersionCheckUpdates]],
				visibilityChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckUpdates],
				containerCssStyle: "padding-bottom:0px",
				style: DwtAlert.WARNING,
				iconVisible: true, 
				content: com_zimbra_adminversioncheck.UpdatesAreAvailable,
				colSpan:"*"
			},
			{ type: _DWT_ALERT_,
				visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaVersionCheck.A_zimbraVersionCheckUpdates]],
				visibilityChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckUpdates],
				containerCssStyle: "padding-bottom:0px",
				style: DwtAlert.WARNING,
				iconVisible: true, 
				content: com_zimbra_adminversioncheck.UpdatesAreAvailable,
				colSpan:"*"
			},	
			{type:_REPEAT_,
				ref:ZaVersionCheck.A_zimbraVersionCheckUpdates
			},
			{ type: _DWT_ALERT_,
				visibilityChecks:[[XForm.checkInstanceValueEmty,ZaVersionCheck.A_zimbraVersionCheckUpdates]],
				visibilityChangeEventSources:[ZaVersionCheck.A_zimbraVersionCheckUpdates],
				containerCssStyle: "padding-bottom:0px",
				style: DwtAlert.INFORMATION,
				iconVisible: true, 
				content: com_zimbra_adminversioncheck.ServerIsUpToDate,
				colSpan:"*"
			},					
    		{ref:ZaVersionCheck.A_zimbraVersionCheckLastAttempt,type:_OUTPUT_,
    			label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckLastAttempt
    		},
    		{ref:ZaVersionCheck.A_zimbraVersionCheckLastSuccess,type:_OUTPUT_,
    			label:com_zimbra_adminversioncheck.LBL_zimbraVersionCheckLastSuccess
    		},
    		{
    			ref:ZaVersionCheck.A_zimbraVersionCheckUpdates,
    			type:_REPEAT_,
    			labelLocation:_LEFT_,
				align:_LEFT_,
				repeatInstance:"",
				showAddButton:false,
				showRemoveButton:false,
				showAddOnNextRow:false,
				items: [
					{ 
						ref:ZaVersionCheck.A_zimbraVersionCheckUpdateShortversion, 
						type: _OUTPUT_, label:null,labelLocation:_NONE_
					},
					{ 
						ref:ZaVersionCheck.A_zimbraVersionCheckUpdateCritical, 
						type: _OUTPUT_, label:null,labelLocation:_NONE_,
						choices:this.criticalChoices
					},
					{ 
						ref:ZaVersionCheck.A_zimbraVersionCheckUpdateUpdateURL, 
						type: _URL_, label:null,labelLocation:_NONE_
					}
				]
    				
    		}
    	]
    }
    var switchItems = [case1,case2];
    xFormObject.items = [
		{type:_TAB_BAR_,  ref:ZaModel.currentTab,id:"xform_tabbar",
		 	containerCssStyle: "padding-top:0px",
			choices: tabBarChoices 
		},
		{type:_SWITCH_, items: switchItems}
	];	
}
ZaTabView.XFormModifiers["ZaVersionCheckXFormView"].push(ZaVersionCheckXFormView.myXFormModifier);