/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 8/26/11
 * Time: 3:47 AM
 * To change this template use File | Settings | File Templates.
 */
ZaHomeXFormView = function(parent, entry) {
	ZaTabView.call(this, {
		parent:parent,
		iKeyName:"ZaHomeXFormView",
		contextId:ZaId.VIEW_HOME
	});
	this.initForm(ZaHome.myXModel,this.getMyXForm(entry), null);
	this._localXForm.setController(ZaApp.getInstance());
}

ZaHomeXFormView.prototype = new ZaTabView();
ZaHomeXFormView.prototype.constructor = ZaHomeXFormView;
ZaTabView.XFormModifiers["ZaHomeXFormView"] = new Array();

ZaHomeXFormView.prototype.setObject =
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

    this._localXForm.setInstance(this._containedObject);
}

ZaHomeXFormView.onCreateDomain = function(ev) {
    ZaDomainListController.prototype._newButtonListener.call(ZaApp.getInstance().getCurrentController(), ev);
}

ZaHomeXFormView.onConfigGAL = function(ev) {
    ZaDomainListController.prototype._newButtonListener.call(ZaApp.getInstance().getCurrentController(), ev);
}

ZaHomeXFormView.onCreateAccount = function(ev) {
    ZaAccountListController.prototype._newAccountListener.call(ZaApp.getInstance().getCurrentController(), ev);
}

ZaHomeXFormView.onSearchZimbraHelp = function(ev) {
    var url = "http://support.zimbra.com/help/index.php";
    window.open(url, "_blank");
}

ZaHomeXFormView.onDownloadGuide = function(ev) {
    ZaZimbraAdmin.prototype._dwListener.call(ZaZimbraAdmin.getInstance());
}

ZaHomeXFormView.onHelpLink = function (ev) {
    ZaZimbraAdmin.prototype._helpListener.call(ZaZimbraAdmin.getInstance());
}
ZaHomeXFormView.myXFormModifier = function(xFormObject, entry) {
    var cases = [];

    var labelChoices = [];
    var contentChoices = [];

    labelChoices.push(ZaMsg.LBL_HomeGetStared);
    var startContentChoices = [];
    startContentChoices.push({value:ZaMsg.LBL_HomeInstallLicense, onClick: ZaHomeXFormView.onSearchZimbraHelp});
    startContentChoices.push({value:ZaMsg.LBL_HomeConfigBackup, onClick: ZaHomeXFormView.onSearchZimbraHelp});
    startContentChoices.push({value:ZaMsg.LBL_HomeInstallCert, onClick: ZaHomeXFormView.onSearchZimbraHelp});
    startContentChoices.push({value:ZaMsg.LBL_HomeConfigureCos, onClick: ZaHomeXFormView.onSearchZimbraHelp});
    contentChoices.push(startContentChoices);

    labelChoices.push(ZaMsg.LBL_HomeSetupDomain);
    var domainContentChoices = [];
    domainContentChoices.push({value:ZaMsg.LBL_HomeCreateDomain, onClick: ZaHomeXFormView.onCreateDomain});
    domainContentChoices.push({value:ZaMsg.LBL_HomeConfigureGAL, onClick: ZaHomeXFormView.onSearchZimbraHelp});
    domainContentChoices.push({value:ZaMsg.LBL_HomeCOnfigureAuth, onClick: ZaHomeXFormView.onSearchZimbraHelp});
    contentChoices.push(domainContentChoices);

    labelChoices.push(ZaMsg.LBL_HomeAddAccounts);
    var addAccountChoices = [];
    addAccountChoices.push({value:ZaMsg.LBL_HomeAddAccount, onClick: ZaHomeXFormView.onCreateAccount});
    addAccountChoices.push({value:ZaMsg.LBL_HomeManageAccount, onClick: ZaHomeXFormView.onSearchZimbraHelp});
    addAccountChoices.push({value:ZaMsg.LBL_HomeMigration, onClick: ZaHomeXFormView.onSearchZimbraHelp});
    contentChoices.push(addAccountChoices);

    var case1 = {type:_ZATABCASE_, numCols: 3,  colSizes:["33%", "34%", "33%"], caseKey:1,
//        height:"400px",  align:_LEFT_, valign:_TOP_,
        getCustomWidth: ZaHomeXFormView.prototype.getCustomWidth,
        items:[
            {type:_GROUP_, colSpan: "*", numCols:1, containerCssStyle:"background-color:green", width:"100%", items:[
                {type:_GROUP_, numCols:3,  width:"100%", colSizes:["80px", "*", "100px"], items:[
                    {type:_AJX_IMAGE_, src: "Critical"},
                    {type:_OUTPUT_, value:"3 Services are not running"},
                    {type:_OUTPUT_, value:ZaMsg.LBL_HomeLinkServerStatus, containerCssStyle:"cursor:pointer;color:white",onClick: ZaHomeXFormView.onSearchZimbraHelp}
                ]},
                {type:_GROUP_, numCols:3,  width:"100%", colSizes:["80px", "*", "100px"], items:[
                    {type:_AJX_IMAGE_, src: "Information"},
                    {type:_OUTPUT_, value:"Zimbra Version 8.1 is availble"},
                    {type:_OUTPUT_, value:ZaMsg.LBL_HomeLinkViewUpdate, containerCssStyle:"cursor:pointer;color:white",onClick: ZaHomeXFormView.onSearchZimbraHelp}
                ]},
                {type:_GROUP_, numCols:3,  width:"100%", colSizes:["80px", "*", "100px"], items:[
                    {type:_AJX_IMAGE_, src: "Warning"},
                    {type:_OUTPUT_, value:"License expired date is 11/29/2011"},
                    {type:_OUTPUT_, value:ZaMsg.LBL_HomeManageLicense, containerCssStyle:"cursor:pointer;color:white",onClick: ZaHomeXFormView.onSearchZimbraHelp}
                ]}
            ]},
            {type:_GROUP_, numCols: 2, items:[
                {type:_OUTPUT_, colSpan:"2", value:ZaMsg.LBL_HomeSummary, cssStyle:"font-size:22px;text-align:center; color: grey"},
                {type:_OUTPUT_, label:ZaMsg.LBL_HomeZimbraVersion, ref: ZaHome.A2_version},
                {type:_OUTPUT_, label:ZaMsg.LBL_HomeServerNum, ref: ZaHome.A2_serverNum},
                {type:_OUTPUT_, label:ZaMsg.LBL_HomeAccountNum, ref: ZaHome.A2_accountNum},
                {type:_OUTPUT_, label:ZaMsg.LBL_HomeDomainNum, ref: ZaHome.A2_domainNum},
                {type:_OUTPUT_, label:ZaMsg.LBL_HomeCosNum, ref: ZaHome.A2_cosNum}
            ]},
            {type:_GROUP_, numCols: 2, width:"100%", items:[
                {type:_OUTPUT_, colSpan:"*", value:ZaMsg.LBL_HomeMaintenance, cssStyle:"font-size:22px; text-align:center; color: grey"},
                {type:_GROUP_, colSpan:"*", numCols:3, width: "100%", colSizes:["*", "30px", "60px"],items:[
                    {type:_OUTPUT_, value:ZaMsg.LBL_HomeLastBackup},
                    {type:_OUTPUT_, ref: ZaHome.A2_lastBackup,
                        getDisplayValue: function (value){
                            if (value) {
                                return AjxImg.getImageHtml ("Check");
                            } else {
                                return AjxImg.getImageHtml ("Cancel");
                            }
                        }
                    },
                    {type:_OUTPUT_, ref: ZaHome.A2_lastBackupTime}
                ]},
                {type:_GROUP_, colSpan:"*", numCols:3, width: "100%", colSizes:["*", "30px", "60px"],items:[
                    {type:_OUTPUT_, value:ZaMsg.LBL_HomeLastCleanup},
                    {type:_OUTPUT_, ref: ZaHome.A2_lastCleanup,
                        getDisplayValue: function (value){
                            if (value) {
                                return AjxImg.getImageHtml ("Check");
                            } else {
                                return AjxImg.getImageHtml ("Cancel");
                            }
                        }
                    },
                    {type:_OUTPUT_, ref: ZaHome.A2_lastCleanupTime}
                ]},
                {type:_GROUP_, colSpan:"*", numCols:3, width: "100%", colSizes:["*", "30px", "60px"],items:[
                    {type:_OUTPUT_, value:ZaMsg.LBL_HomeLastLogPurge},
                    {type:_OUTPUT_, ref: ZaHome.A2_lastLogPurge,
                        getDisplayValue: function (value){
                            if (value) {
                                return AjxImg.getImageHtml ("Check");
                            } else {
                                return AjxImg.getImageHtml ("Cancel");
                            }
                        }
                    },
                    {type:_OUTPUT_, ref: ZaHome.A2_lastLogPurgeTime}
                ]},
                {type:_GROUP_, colSpan:"*", numCols:3, width: "100%", colSizes:["*", "30px", "60px"],items:[
                    {type:_OUTPUT_, value: ZaMsg.LBL_HomeExpiredCerts},
                    {type:_OUTPUT_, ref: ZaHome.A2_expiredType,
                        getDisplayValue: function (value){
                            if (value) {
                                return AjxImg.getImageHtml ("Check");
                            } else {
                                return AjxImg.getImageHtml ("Cancel");
                            }
                        }
                    },
                    {type:_OUTPUT_, ref: ZaHome.A2_expiredMessage}
                ]},
                {type:_GROUP_, colSpan:"*", numCols:3, width: "100%", colSizes:["*", "30px", "60px"],items:[
                    {type:_OUTPUT_, value: ZaMsg.LBL_HomeDBCheck},
                    {type:_OUTPUT_, ref: ZaHome.A2_DBCheckType,
                        getDisplayValue: function (value){
                            if (value) {
                                return AjxImg.getImageHtml ("Check");
                            } else {
                                return AjxImg.getImageHtml ("Cancel");
                            }
                        }
                    },
                    {type:_OUTPUT_, ref: ZaHome.A2_DBCheckMessage}
                ]}
            ]},
            {type:_GROUP_, numCols: 2, colSizes:["*", "90px"], width: "100%", items:[
                {type:_OUTPUT_, colSpan:"2", value:ZaMsg.LBL_HomeRuntime, cssStyle:"font-size:22px;text-align:center; color: grey"},
                {type:_GROUP_, colSpan:"*", numCols:3, width: "100%", colSizes:["*", "30px", "60px"],items:[
                    {type:_OUTPUT_, value:ZaMsg.LBL_HomeService},
                    {type:_OUTPUT_, ref: ZaHome.A2_serviceStatus,
                        getDisplayValue: function (value){
                            if (value) {
                                return AjxImg.getImageHtml ("Check");
                            } else {
                                return AjxImg.getImageHtml ("Cancel");
                            }
                        }
                    },
                    {type:_OUTPUT_, ref: ZaHome.A2_serviceStatusMessage}
                ]},
                {type:_OUTPUT_, label:ZaMsg.LBL_HomeActiveSession, align:_LEFT_, ref: ZaHome.A2_activeSession},
                {type:_OUTPUT_, label:ZaMsg.LBL_HomeQueueLength, ref: ZaHome.A2_queueLength},
                {type:_OUTPUT_, label:ZaMsg.LBL_HomeMsgCount, ref: ZaHome.A2_messageCount},
                {type:_OUTPUT_, label:ZaMsg.LBL_HomeMsgVolume, ref: ZaHome.A2_messageVolume}
            ]},
            {type:_SPACER_, colSpan: "*", height:20},
            {type:_SETUPGROUP_, colSpan: "*", headerLabels: labelChoices, contentItems: contentChoices},
            {type:_SPACER_, colSpan: "*", height:20 },
            {type:_OUTPUT_, value: ZaMsg.LBL_HomeHelpCenter, colSpan: "*", align:_RIGHT_, onClick: ZaHomeXFormView.onHelpLink,
                containerCssStyle:"cursor:pointer;color:blue"}
        ]
    };

    cases.push(case1);

    xFormObject.tableCssStyle="width:100%;";
	xFormObject.items = [
			{type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:cases}
	];
};
ZaTabView.XFormModifiers["ZaHomeXFormView"].push(ZaHomeXFormView.myXFormModifier);

ZaHomeXFormView.prototype.getCustomWidth = function () {
    return "100%";
}

ZaHomeXFormView.prototype.getBarImage = function () {
    return "Home";
}