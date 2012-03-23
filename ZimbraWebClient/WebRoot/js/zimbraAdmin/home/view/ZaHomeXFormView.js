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
        cssClassName:"ZaHomeTabView DwtTabView",
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
    var domainListController =  ZaApp.getInstance().getDomainListController();
    ZaDomainListController.prototype._newButtonListener.call(domainListController, ev);
}

ZaHomeXFormView.onConfigGAL = function(ev) {
    var domainList = ZaApp.getInstance().getDomainList();
    if (domainList.size() > 0) {
        var lastDomain = domainList.getVector().getLast();
        var domainListController = ZaApp.getInstance().getDomainListController();
        ZaDomainListController.prototype._openConfigGAL.call(domainListController, lastDomain);
    }
}

ZaHomeXFormView.canConfigGAL =function (ev) {
    var domainList = ZaApp.getInstance().getDomainList();
    var canConfigGAL = false;
    if (domainList.size() > 0) {
        var currentDomain;
        var vector = domainList.getVector();
        for (var i = vector.size() -1; i >= 0; i--) {
            currentDomain = vector.get(i);
            if (ZaDomain.canConfigureGal(currentDomain)) {
                canConfigGAL = true;
                break;
            }
        }
    }
    return canConfigGAL;
}

ZaHomeXFormView.onConfigAuth = function(ev) {
    var domainList = ZaApp.getInstance().getDomainList();
    if (domainList.size() > 0) {
        var lastDomain = domainList.getVector().getLast();
        var domainListController = ZaApp.getInstance().getDomainListController();
        ZaDomainListController.prototype._openAuthWiz.call(domainListController, lastDomain);
    }
}

ZaHomeXFormView.canConfigAuth =function (ev) {
    var domainList = ZaApp.getInstance().getDomainList();
    var canConfigAuth = false;
    if (domainList.size() > 0) {
        var currentDomain;
        var vector = domainList.getVector();
        for (var i = vector.size() -1; i >= 0; i--) {
            currentDomain = vector.get(i);
            if (ZaDomain.canConfigureAuth(currentDomain)) {
                canConfigAuth = true;
                break;
            }
        }
    }
    return canConfigAuth;
}

ZaHomeXFormView.onConfigDefaultCos = function() {
    var cosList = ZaApp.getInstance().getCosList();
    if (cosList.size() > 0) {
        var vector = cosList.getVector();
        var cos;
        for(var i = 0; i < vector.size(); i++) {
            cos = vector.get(i);
            if (cos.name == "default")
                break;
        }
        if (i != vector.size()) {
            ZaApp.getInstance().getCosController().show(cos);
            var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, ZaMsg.OVP_cos]);
            ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, cos.name, null, true, false, cos);
        }
    }
}

ZaHomeXFormView.onCreateAccount = function(ev) {
    ZaAccountListController.prototype._newAccountListener.call(ZaApp.getInstance().getAccountListController(), ev);
}

ZaHomeXFormView.onManageAccount = function(ev) {
    var tree = ZaZimbraAdmin.getInstance().getOverviewPanelController().getOverviewPanel().getFolderTree();
    var path = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_manageAccounts]);
    tree.setSelectionByPath(path, false);
}

ZaHomeXFormView.onViewService = function(ev) {
    var tree = ZaZimbraAdmin.getInstance().getOverviewPanelController().getOverviewPanel().getFolderTree();
    var path = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_monitor, ZaMsg.OVP_status]);
    tree.setSelectionByPath(path, false);
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

ZaHomeXFormView.onCloseSetup = function(ev) {
    var form =this.getForm();
    var setupGroupItem = form.getItemById(form.getId() + "_homeSetupGroup");
    setupGroupItem.hide();
}

ZaHomeXFormView.getWarningPanelItem = function (xFormObject) {
    return xFormObject.items[0].items[0].items[0];
}

ZaHomeXFormView.getHomeMaintenanceItem = function (xFormObject) {
    return xFormObject.items[0].items[0].items[1].items[1];
}

ZaHomeXFormView.getHomeSetupItem = function (xFormObject) {
    return xFormObject.items[0].items[0].items[2].items[0].items[1];
}

ZaHomeXFormView.getWarningPanelCol = function () {
    return ["20px", "*", "120px"];
}

ZaHomeXFormView.showStatusInfo = function () {
    return (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_STATUS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]);
}

ZaHomeXFormView.showStaticsInfo = function () {
    return (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]);
}

ZaHomeXFormView.showMT = function() {
    var value = this.getInstanceValue(ZaHome.A2_maintenanceItemNum);
    return value > 1;
}

ZaHomeXFormView.myXFormModifier = function(xFormObject, entry) {
    var cases = [];

    var labelChoices = [];
    var contentChoices = [];

    labelChoices.push(ZaMsg.LBL_HomeGetStared);
    contentChoices.push([]);
    var startContentChoices = contentChoices[contentChoices.length - 1];
    startContentChoices.push({});
    startContentChoices.push({});
    startContentChoices.push({});
    startContentChoices.push({});

    if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.COS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        startContentChoices[3] = {value:ZaMsg.LBL_HomeConfigureCos, onClick: ZaHomeXFormView.onConfigDefaultCos};
    }

    if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        var domainContentChoices = [];
	    if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_TOP_DOMAIN, ZaZimbraAdmin.currentAdminAccount)) {
            domainContentChoices.push({value:ZaMsg.LBL_HomeCreateDomain, onClick: ZaHomeXFormView.onCreateDomain});
        }

        if (ZaHomeXFormView.canConfigGAL()) {
            domainContentChoices.push({value:ZaMsg.LBL_HomeConfigureGAL, onClick: ZaHomeXFormView.onConfigGAL});
        }

        if (ZaHomeXFormView.canConfigAuth()) {
            domainContentChoices.push({value:ZaMsg.LBL_HomeCOnfigureAuth, onClick: ZaHomeXFormView.onConfigAuth});
        }

        if (domainContentChoices.length > 0) {
            labelChoices.push(ZaMsg.LBL_HomeSetupDomain);
            contentChoices.push(domainContentChoices);
        }
    }

    if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        labelChoices.push(ZaMsg.LBL_HomeAddAccounts);
        var addAccountChoices = [];
        addAccountChoices.push({value:ZaMsg.LBL_HomeAddAccount, onClick: ZaHomeXFormView.onCreateAccount});
        addAccountChoices.push({value:ZaMsg.LBL_HomeManageAccount, onClick: ZaHomeXFormView.onManageAccount});
        contentChoices.push(addAccountChoices);
    }

    var case1 = {type:_ZATABCASE_, numCols: 3,  colSizes:["37%", "34%", "29%"], caseKey:1,
        paddingStyle: "", width: "100%", cellpadding: 0,
//        height:"400px",  align:_LEFT_, valign:_TOP_,
        items:[
            {type:_GROUP_, colSpan: "*", numCols:1, containerCssClass:"ZaHomeWarningPanel", width:"100%", items:[

                {type:_GROUP_, numCols:3,  width:"100%", colSizes:ZaHomeXFormView.getWarningPanelCol(), containerCssClass:"ZaHomeWarnginItem",
                    visibilityChecks:[[XForm.checkInstanceValueNot,ZaHome.A2_serviceStatus,true],[ZaHomeXFormView.showStatusInfo]],
                    visibilityChangeEventSources: [ZaHome.A2_serviceStatus],
                    items:[
                        {type:_OUTPUT_, ref: ZaHome.A2_serviceStatus, bmolsnr: true,
                            getDisplayValue: function (value){
                                if (this.getInstanceValue(ZaHome.A2_serviceDetailedMessage) == ZaMsg.MSG_HomeLoading) {
                                    return "";
                                }
                                if (value === undefined) {
                                    return AjxImg.getImageHtml ("UnKnownStatus");
                                }else if (value === false) {
                                    return AjxImg.getImageHtml ("Critical");
                                } else {
                                    return AjxImg.getImageHtml ("Check");
                                }
                            },
                            valueChangeEventSources:[ZaHome.A2_serviceDetailedMessage]
                        },
                        {type:_OUTPUT_, ref: ZaHome.A2_serviceDetailedMessage, bmolsnr: true},
                        {type:_OUTPUT_, value:ZaMsg.LBL_HomeLinkServerStatus, containerCssClass:"ZaLinkedItem",onClick: ZaHomeXFormView.onViewService}
                ]}
            ]},
            {type:_GROUP_, colSpan: "*", numCols: 3,  colSizes:["33%", "34%", "33%"], width: "100%",
                containerCssClass:"ZaHomeInfoPanel",items:[
                {type:_GROUP_, numCols: 2, valign: _TOP_, items:[
                    {type:_OUTPUT_, colSpan:"2", value:ZaMsg.LBL_HomeSummary, cssClass:"ZaHomeInfoTitle"},
                    {type:_OUTPUT_, label:ZabMsg.LBL_HomeZimbraVersion, ref: ZaHome.A2_version},
                    {type:_OUTPUT_, label:ZaMsg.LBL_HomeServerNum, ref: ZaHome.A2_serverNum, bmolsnr: true},
                    {type:_OUTPUT_, label:ZaMsg.LBL_HomeAccountNum, ref: ZaHome.A2_accountNum, bmolsnr: true},
                    {type:_OUTPUT_, label:ZaMsg.LBL_HomeDomainNum, ref: ZaHome.A2_domainNum, bmolsnr: true},
                    {type:_OUTPUT_, label:ZaMsg.LBL_HomeCosNum, ref: ZaHome.A2_cosNum, bmolsnr: true}
                ]},
                {type:_GROUP_, id:"mainenance_grp", numCols: 2, valign: _TOP_, width:"100%", items:[
                    {type:_OUTPUT_, colSpan:"*", value:ZaMsg.LBL_HomeMaintenance, cssClass:"ZaHomeInfoTitle",
                        visibilityChangeEventSources:[ZaHome.A2_maintenanceItemNum],
                        visibilityChecks:[[ZaHomeXFormView.showMT]]
                    } /*
                    {type:_GROUP_, colSpan:"*", numCols:3, width: "100%", colSizes:["*", "20px", "120px"],items:[
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
                    {type:_GROUP_, colSpan:"*", numCols:3, width: "100%", colSizes:["*", "20px", "120px"],items:[
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
                    {type:_GROUP_, colSpan:"*", numCols:3, width: "100%", colSizes:["*", "20px", "120px"],items:[
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
                    ]} */
                ]},
                {type:_GROUP_, numCols: 2, colSizes:["*", "90px"], valign: _TOP_, width: "100%", items:[
                    {type:_OUTPUT_, colSpan:"2", value:ZaMsg.LBL_HomeRuntime, cssClass:"ZaHomeInfoTitle"},
                    {type:_GROUP_, colSpan:"*", label:ZaMsg.LBL_HomeService, items:[
                        {type:_OUTPUT_, ref: ZaHome.A2_serviceStatus, bmolsnr: true,
                            visibilityChecks:[[ZaHomeXFormView.showStatusInfo],
                                              [XForm.checkInstanceValueNot,ZaHome.A2_serviceStatusMessage,ZaMsg.MSG_HomeLoading]],
                            getDisplayValue: function (value){
                                if (this.getInstanceValue(ZaHome.A2_serviceStatusMessage) == ZaMsg.MSG_HomeLoading) {
                                    return "";
                                }
                                if (value === undefined) {
                                    return AjxImg.getImageHtml ("UnKnownStatus");
                                }else if (value === false) {
                                    return AjxImg.getImageHtml ("Critical");
                                } else {
                                    return AjxImg.getImageHtml ("Check");
                                }
                            },
                            visibilityChangeEventSources:[ZaHome.A2_serviceStatusMessage],
                            valueChangeEventSources:[ZaHome.A2_serviceStatusMessage]
                        },
                        {type:_OUTPUT_, ref: ZaHome.A2_serviceStatusMessage, bmolsnr: true}
                    ]},
                    {type:_OUTPUT_, label:ZaMsg.LBL_HomeActiveSession, align:_LEFT_, ref: ZaHome.A2_activeSession,
                         bmolsnr: true,
                        visibilityChecks:[[ZaHomeXFormView.showStaticsInfo]]},
                    {type:_OUTPUT_, label:ZaMsg.LBL_HomeQueueLength, ref: ZaHome.A2_queueLength, bmolsnr: true,
                        visibilityChecks:[[ZaHomeXFormView.showStaticsInfo]]}
                    /*
                    {type:_OUTPUT_, label:ZabMsg.LBL_HomeMsgCount, ref: ZaHome.A2_messageCount},
                    {type:_OUTPUT_, label:ZabMsg.LBL_HomeMsgVolume, ref: ZaHome.A2_messageVolume} */
                ]}
            ]},
            {type:_GROUP_, colSpan: "*", id:"homeSetupGroup", containerCssClass:"ZaHomeSetupPanelContainer", cssClass:"ZaHomeSetupPanel", numCols:1, items:[
                {type:_GROUP_, colSpan: "*", numCols: 3,  width:"100%", colSizes:["37%", "34%", "29%"],
                    containerCssClass:"ZaHomeSetupPanelContent", items:[
                    {type:_GROUP_, colSpan: "*", width:"100%", numCols:2, colSizes:["100%", "20px"], items:[
                        {type:_CELL_SPACER_},
                        {type:_DWT_IMAGE_, value: "ImgRemoveLineUp", containerCssStyle:"cursor: pointer;", cssStyle:"position:static;display:none;",  onClick:ZaHomeXFormView.onCloseSetup}
                    ]},
                    {type:_SETUPGROUP_, colSpan: "*", headerLabels: labelChoices, contentItems: contentChoices},
                    {type:_OUTPUT_, value: ZabMsg.LBL_HomeHelpCenter, colSpan: "*", align:_RIGHT_, onClick: ZaHomeXFormView.onHelpLink,
                        containerCssClass:"ZaLinkedItem"}
                ]}
            ]}
        ]
    };

    cases.push(case1);

    xFormObject.tableCssStyle="width:100%;";
	xFormObject.items = [
			{type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:cases}
	];
};
ZaTabView.XFormModifiers["ZaHomeXFormView"].push(ZaHomeXFormView.myXFormModifier);

ZaHomeXFormView.prototype.getBarImage = function () {
    return "Home";
}
