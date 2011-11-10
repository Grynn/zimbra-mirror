/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 8/29/11
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
ZaHome = function() {
	ZaItem.call(this,"ZaHome");
	this._init();
}
ZaItem.loadMethods["ZaHome"] = new Array();
ZaItem.initMethods["ZaHome"] = new Array();

ZaHome.prototype = new ZaItem;
ZaHome.prototype.constructor = ZaHome;

ZaHome.A2_version= "version";
ZaHome.A2_account = "account";
ZaHome.A2_serverNum = "serverNum";
ZaHome.A2_accountNum = "accountNum";
ZaHome.A2_cosNum = "cosNum";
ZaHome.A2_domainNum = "domainNum";
ZaHome.A2_lastCleanup = "lastCleanup";
ZaHome.A2_lastCleanupTime = "lastCleanupTime";
ZaHome.A2_lastLogPurge = "lastLogPurge";
ZaHome.A2_lastLogPurgeTime = "lastLogPurgeTime";
ZaHome.A2_expiredType = "expiredType";
ZaHome.A2_expiredMessage = "expiredMessage";
ZaHome.A2_DBCheckType = "dbCheckType";
ZaHome.A2_DBCheckMessage= "dbCheckMessage";
ZaHome.A2_serviceStatus = "serviceStatus";
ZaHome.A2_serviceStatusMessage = "serviceStatusMessage";
ZaHome.A2_activeSession = "activeSession";
ZaHome.A2_queueLength = "queueLength";
ZaHome.A2_messageCount = "messageCount";
ZaHome.A2_messageVolume = "messageVolume";

ZaHome.initMethod = function () {
	this.attrs = new Object();
	this.type = ZaItem.HOME;
}
ZaItem.initMethods["ZaHome"].push(ZaHome.initMethod);

// Fake here no soap request, just collect all kinds of information everywhere
ZaHome.loadMethod =
function () {
    var now = new Date();
    var formatter = AjxDateFormat.getDateInstance(AjxDateFormat.LONG);
    var currentTime = formatter.format(now);
    this.attrs[ZaHome.A2_account] = ZaZimbraAdmin.currentAdminAccount.attrs.mail;
    this.attrs[ZaHome.A2_version] = ZaServerVersionInfo.version;
    this.attrs[ZaHome.A2_accountNum] = ZaApp.getInstance().getAccountStats()[ZaItem.ACCOUNT];
    this.attrs[ZaHome.A2_cosNum] = ZaApp.getInstance().getCosList().size();
    this.attrs[ZaHome.A2_domainNum] = ZaApp.getInstance().getDomainList().size();
    this.attrs[ZaHome.A2_serverNum] = ZaApp.getInstance().getServerList().size();
    this.attrs[ZaHome.A2_lastCleanup] = true;
    this.attrs[ZaHome.A2_lastCleanupTime] = currentTime;
    this.attrs[ZaHome.A2_lastLogPurge] = true;
    this.attrs[ZaHome.A2_lastLogPurgeTime] = currentTime;
    this.attrs[ZaHome.A2_expiredType] = true;
    this.attrs[ZaHome.A2_expiredMessage] = ZaMsg.LBL_HomeStatusOK;
    this.attrs[ZaHome.A2_DBCheckType] = true;
    this.attrs[ZaHome.A2_DBCheckMessage] = ZaMsg.LBL_HomeStatusOK;
    this.attrs[ZaHome.A2_serviceStatusMessage] = ZaMsg.LBL_HomeStatusRunning ;
    this.attrs[ZaHome.A2_messageCount] = "120/h";
    this.attrs[ZaHome.A2_messageVolume] = "34MB/h";
}
ZaItem.loadMethods["ZaHome"].push(ZaHome.loadMethod);

ZaHome.loadStatusfo = function () {
    var status = new ZaStatus();
    status.load();
    var statusVector = status.getStatusVector();
    var serverStatus;
    if (statusVector.size() > 0) {
        this.attrs[ZaHome.A2_serviceStatus] = true;
        this.attrs[ZaHome.A2_serviceStatusMessage] = ZaMsg.LBL_HomeStatusRunning ;
        for(var i = 0; i < statusVector.size(); i++) {
            serverStatus = statusVector.get(i);
            if (serverStatus.status != 1) {
                this.attrs[ZaHome.A2_serviceStatus] = false;
                this.attrs[ZaHome.A2_serviceStatusMessage] = ZaMsg.LBL_HomeStatusFailed;
                break;
            }
        }
    } else {
        this.attrs[ZaHome.A2_serviceStatusMessage] = ZaMsg.LBL_HOmeStatusUnknown ;
    }
}
ZaItem.loadMethods["ZaHome"].push(ZaHome.loadStatusfo);

ZaHome.loadActiveSesson = function () {
    var serverList = ZaApp.getInstance().getServerList().getArray();
    var totalSession = 0;
    if(serverList && serverList.length) {
        var sessionType = ["soap", "admin", "imap"];
        var cnt = serverList.length;
        for(var i=0; i< cnt; i++) {
            var serverInfo = serverList[i];
            for (var j = 0; j < sessionType.length; j++) {
                var soapDoc = AjxSoapDoc.create("GetSessionsRequest", ZaZimbraAdmin.URN, null);
                var params = {};
                params.type = sessionType[j];

                soapDoc.getMethod().setAttribute("type", params.type);

                params.fresh = 1;
                soapDoc.getMethod().setAttribute("refresh", params.fresh);

                soapDoc.getMethod().setAttribute("limit", ZaServerSessionStatsPage.PAGE_LIMIT);

                params.offset = 0 ;

                soapDoc.getMethod().setAttribute("offset", params.offset);

                params.sortBy = "nameAsc";

                soapDoc.getMethod().setAttribute("sortBy", params.sortBy);

                var getSessCmd = new ZmCsfeCommand ();
                params.soapDoc = soapDoc ;
                params.targetServer = serverInfo.id ;

                var resp = getSessCmd.invoke(params);
                if (resp && resp.Body && resp.Body.GetSessionsResponse) {
                    var sessionStats = resp.Body.GetSessionsResponse;
                    totalSession += sessionStats.total;
                }
            }
        }
    }
    this.attrs[ZaHome.A2_activeSession] = totalSession;
}
ZaItem.loadMethods["ZaHome"].push(ZaHome.loadActiveSesson);

ZaHome.loadQueueLength = function () {
    var mtaList = ZaApp.getInstance().getPostQList().getArray();
    var totalQueueLength = 0;
    if(mtaList && mtaList.length) {
        var cnt = mtaList.length;
        try {
            for (var i = 0; i < cnt; i++) {
                var currentMta = mtaList[i];
                var soapDoc = AjxSoapDoc.create("GetMailQueueInfoRequest", ZaZimbraAdmin.URN, null);
                var attr = soapDoc.set("server", "");
                attr.setAttribute("name", currentMta.name);
                var command = new ZmCsfeCommand();
                var params = new Object();
                params.soapDoc = soapDoc;
                var resp = command.invoke(params);
                var body = resp.Body;
                if(body && body.GetMailQueueInfoResponse.server && body.GetMailQueueInfoResponse.server[0]) {
                    var queue =  body.GetMailQueueInfoResponse.server[0].queue;
                    for ( var j in queue) {
                        if (queue[j].n) {
                            totalQueueLength += parseInt(queue[j].n);
                        }
                    }
                }
            }
        } catch (ex) {
            // Won't do anything here to avoid disturbe the loading process.
        }

    }
    this.attrs[ZaHome.A2_queueLength] = totalQueueLength;
}

ZaItem.loadMethods["ZaHome"].push(ZaHome.loadQueueLength);

ZaHome.myXModel = {
    items: [
        {id:ZaHome.A2_version,type:_STRING_,  ref:"attrs/" + ZaHome.A2_version},
    	{id:ZaHome.A2_account,type:_STRING_, ref:"attrs/" + ZaHome.A2_account},
    	{id:ZaHome.A2_domainNum,type:_STRING_, ref:"attrs/" + ZaHome.A2_domainNum},
    	{id:ZaHome.A2_cosNum,type:_STRING_, ref:"attrs/" + ZaHome.A2_cosNum},
    	{id:ZaHome.A2_serverNum,type:_STRING_, ref:"attrs/" + ZaHome.A2_serverNum},
    	{id:ZaHome.A2_accountNum,type:_STRING_, ref:"attrs/" + ZaHome.A2_accountNum},
        {id:ZaHome.A2_lastCleanupTime, type:_STRING_, ref: "attrs/" + ZaHome.A2_lastCleanupTime},
        {id:ZaHome.A2_lastCleanup, type:_ENUM_, ref: "attrs/" + ZaHome.A2_lastCleanupTime, choices: ZaModel.BOOLEAN_CHOICES},
        {id:ZaHome.A2_lastLogPurgeTime, type:_STRING_, ref: "attrs/" + ZaHome.A2_lastLogPurgeTime},
        {id:ZaHome.A2_lastLogPurge, type:_ENUM_, ref: "attrs/" + ZaHome.A2_lastLogPurge, choices: ZaModel.BOOLEAN_CHOICES},
        {id:ZaHome.A2_expiredMessage, type:_STRING_, ref: "attrs/" + ZaHome.A2_expiredMessage},
        {id:ZaHome.A2_expiredType, type:_ENUM_, ref: "attrs/" + ZaHome.A2_expiredType, choices: ZaModel.BOOLEAN_CHOICES},
        {id:ZaHome.A2_DBCheckMessage, type:_STRING_, ref: "attrs/" + ZaHome.A2_DBCheckMessage},
        {id:ZaHome.A2_DBCheckType, type:_ENUM_, ref: "attrs/" + ZaHome.A2_DBCheckType, choices: ZaModel.BOOLEAN_CHOICES},
        {id:ZaHome.A2_serviceStatusMessage, type:_STRING_, ref: "attrs/" + ZaHome.A2_serviceStatusMessage},
        {id:ZaHome.A2_serviceStatus, type:_ENUM_, ref: "attrs/" + ZaHome.A2_serviceStatus, choices: ZaModel.BOOLEAN_CHOICES},
        {id:ZaHome.A2_activeSession, type:_NUMBER_, ref:"attrs/" + ZaHome.A2_activeSession},
        {id:ZaHome.A2_queueLength, type:_NUMBER_, ref:"attrs/" + ZaHome.A2_queueLength},
        {id:ZaHome.A2_messageCount, type:_NUMBER_, ref:"attrs/" + ZaHome.A2_messageCount},
        {id:ZaHome.A2_messageVolume, type:_NUMBER_, ref:"attrs/" + ZaHome.A2_messageVolume}
    ]
}

