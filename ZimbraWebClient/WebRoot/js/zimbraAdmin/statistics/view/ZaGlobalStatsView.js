/*
* ***** BEGIN LICENSE BLOCK *****
* Zimbra Collaboration Suite Web Client
* Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
* 
* The contents of this file are subject to the Zimbra Public License
* Version 1.4 ("License"); you may not use this file except in
* compliance with the License.  You may obtain a copy of the License at
* http://www.zimbra.com/license.
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
* ***** END LICENSE BLOCK *****
*/

/**
* @class ZaGlobalStatsView
* @contructor ZaGlobalStatsView
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaGlobalStatsView = function(parent) {

    DwtTabView.call(this, parent);
    this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
    if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_ADVANCED_STATS_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]){
        this._advancedPage = new ZaGlobalAdvancedStatsPage(this);
        this.addTab(ZaMsg.TABT_Advanced_Stats, this._advancedPage);
    }
    this._tabBar.setVisible(false); 
}

ZaGlobalStatsView.prototype = new DwtTabView;
ZaGlobalStatsView.prototype.constructor = ZaGlobalStatsView;

ZaGlobalStatsView.extTabObjects = new Array();

ZaGlobalStatsView.prototype.toString =
function() {
    return "ZaGlobalStatsView";
}



ZaGlobalStatsView.prototype.getAllServersInfo = function( ){

    try {

        var soapDoc = AjxSoapDoc.create("GetAllServersRequest", ZaZimbraAdmin.URN, null);
        soapDoc.getMethod().setAttribute("applyConfig", "false");
        soapDoc.getMethod().setAttribute("service", "mta");

        var params = new Object();
        params.soapDoc = soapDoc;
        params.asyncMode=false;

        var reqMgrParams = {
            controller : ZaApp.getInstance().getCurrentController(),
            busyMsg : ZaMsg.BUSY_GET_ALL_SERVER
        }
        var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllServersResponse;
        var allServersInfo = resp.server;

        return allServersInfo;

    }catch ( ex ){

        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaGlobalStatsView.getAllServersInfo", null, false);
    }
}

ZaGlobalStatsView.prototype.getAllServersMtaServiceStatus = function( ){

    //this._containedObject.name;
    var allServersInfo = ZaGlobalStatsView.prototype.getAllServersInfo( );

    if( !allServersInfo ){
        return null;
    }

    var oneServerDetailInfo = null;
    var oneServerBriefInfo = {};
    var i = 0;
    var j = 0;

    var isEnabled = false;
    var isInstalled = false;
    var allServersMtaServiceEnableStatus = [];

    for( i = 0; i < allServersInfo.length; i++ ){
        oneServerDetailInfo = allServersInfo[i].a;
        oneServerBriefInfo.id = allServersInfo[i].id;
        oneServerBriefInfo.name = allServersInfo[i].name;
        var oneService;
        for ( j = 0, isEnabled = isInstalled = false; j < oneServerDetailInfo.length; j++){
            oneService = oneServerDetailInfo[j];
            if( "mta" == oneService._content ){
                if( oneService.n == ZaServer.A_zimbraServiceEnabled ){

                    isEnabled = true;

                }else if ( oneService.n == ZaServer.A_zimbraServiceInstalled ){

                    isInstalled = true;

                }
            }
        }

        var isMtaEnable = (isEnabled && isInstalled);

        allServersMtaServiceEnableStatus[ oneServerBriefInfo.name ] = isMtaEnable;

    }

    return allServersMtaServiceEnableStatus;
}

ZaGlobalStatsView.prototype.isAllMtaDisable = function( ){

    var allServersMtaServiceEnableStatus = ZaGlobalStatsView.prototype.getAllServersMtaServiceStatus();
    if( !allServersMtaServiceEnableStatus ){
        return true; //no info means no zimbraServiceEnabled message sending to the admin
    }

    var i = 0;
    for ( i in allServersMtaServiceEnableStatus )
    {
        if( allServersMtaServiceEnableStatus[i] ){
            return false;
        }
    }

    return true;
}

ZaGlobalStatsView.prototype.setObject = function (entry) {
    this._containedObject = entry ;
    //this._msgCountPage.setObject(entry);
    //this._msgsVolumePage.setObject(entry);
    //this._spamPage.setObject(entry);
    if( !ZaGlobalStatsView.prototype.isAllMtaDisable()  ){

        if( this._msgCountPage == null ){
            this._msgCountPage = new ZaGlobalMessageCountPage(this);
            this.addTab(ZaMsg.TABT_InMsgs, this._msgCountPage);
        }
        this._msgCountPage.setObject(entry);

        if( this._msgsVolumePage == null ){
            this._msgsVolumePage = new ZaGlobalMessageVolumePage(this);
            this.addTab(ZaMsg.TABT_InData, this._msgsVolumePage);
        }
        this._msgsVolumePage.setObject(entry);

        if( this._spamPage == null ){
            this._spamPage = new ZaGlobalSpamActivityPage(this);
            this.addTab(ZaMsg.TABT_Spam_Activity, this._spamPage);
        }
        this._spamPage.setObject(entry);

    }

    if (this._advancedPage)
        this._advancedPage.setObject(entry);
    //    this._mobileSyncPage.setObject(entry);

    for(var i = 0; i < ZaGlobalStatsView.extTabObjects.length; i++) {
        var tabObj = ZaGlobalStatsView.extTabObjects[i];
        if(typeof(tabObj.memthod) == "function"){
            var tabPage = null;
            if(!tabObj.tabKey) {
                tabPage = new tabObj.memthod(this);
                tabObj.tabKey = this.addTab(tabObj.title,tabPage);
            }else {
                tabPage = this.getTabView(tabObj.tabKey);
            }
            if(tabPage) tabPage.setObject(entry);
        }

    }
}

ZaGlobalStatsView.prototype.getTitle =
function () {
    return ZaMsg.GlobalStats_view_title;
}

ZaGlobalStatsView.prototype.getTabTitle =
function () {
    return ZaMsg.GlobalStats_view_title;
}

ZaGlobalStatsView.prototype.getTabIcon =
function () {
    return "Statistics";
}

ZaGlobalStatsView.prototype.getTabToolTip =
function () {
    return ZaMsg.GlobalStats_view_title;
}

ZaGlobalStatsView.prototype._resetTabSizes =
function (width, height) {
    var tabBarSize = this._tabBar.getSize();
    var titleCellSize = Dwt.getSize(this.titleCell);

    var tabBarHeight = tabBarSize.y || this._tabBar.getHtmlElement().clientHeight;
    var titleCellHeight = titleCellSize.y || this.titleCell.clientHeight;

    var tabWidth = width;
    var newHeight = (height - tabBarHeight - titleCellHeight);
    var tabHeight = ( newHeight > 50 ) ? newHeight : 50;

    if(this._tabs && this._tabs.length) {
        for(var curTabKey in this._tabs) {
            if(this._tabs[curTabKey]["view"]) {
                this._tabs[curTabKey]["view"].resetSize(tabWidth, tabHeight);
            }
        }
    }
}

ZaGlobalStatsView.prototype._createHtml =
function() {
    DwtTabView.prototype._createHtml.call(this);

    //create a Title Table
    this._table = document.createElement("table") ;

    //this.getHtmlElement().appendChild(this._table) ;
    var htmlEl = this.getHtmlElement()
    htmlEl.insertBefore (this._table, htmlEl.firstChild);

    var row1;
    //var col1;
    var row2;
    var col2;
    row1 = this._table.insertRow(0);
    row1.align = "center";
    row1.vAlign = "middle";

    this.titleCell = row1.insertCell(row1.cells.length);
    this.titleCell.align = "center";
    this.titleCell.vAlign = "middle";
    this.titleCell.noWrap = true;

    this.titleCell.id = Dwt.getNextId();
    this.titleCell.align="left";
    this.titleCell.innerHTML = AjxStringUtil.htmlEncode(ZaMsg.NAD_GlobalStatistics);
    this.titleCell.className="AdminTitleBar";
}

ZaGlobalStatsView.prototype.getTabChoices =
function() {
    //var innerTabs = this._tab;
    var innerTabs = [];
    if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_ADVANCED_STATS_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]){
        innerTabs.push(ZaMsg.TABT_Advanced_Stats);
    }

    if( !ZaGlobalStatsView.prototype.isAllMtaDisable()  ){
        innerTabs.push(ZaMsg.TABT_InMsgs);
        innerTabs.push(ZaMsg.TABT_InData);
        innerTabs.push(ZaMsg.TABT_Spam_Activity);
    }

    for(var i = 0; i < ZaGlobalStatsView.extTabObjects.length; i++) {
        var tabObj = ZaGlobalStatsView.extTabObjects[i];
        if(typeof(tabObj.memthod) == "function"){
            innerTabs.push(tabObj.title);
        }
    }
    var tabChoices = [];

    //index of _tabs is based on 1 rather than 0
    for (var i = 1; i <= innerTabs.length; i++){
        tabChoices.push({ value: i,
                            label: innerTabs[i-1]
                            //label: innerTabs[i].title
                        });
    }

    return tabChoices;
}