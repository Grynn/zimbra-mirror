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
ZaHome.A2_lastBackup = "lastBackup";
ZaHome.A2_lastBackupTime = "lastBackupTime";
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
    this.attrs[ZaHome.A2_account] = ZaZimbraAdmin.currentAdminAccount.attrs.mail;
    this.attrs[ZaHome.A2_version] = ZaServerVersionInfo.version;
    this.attrs[ZaHome.A2_accountNum] = ZaApp.getInstance().getAccountStats()[ZaItem.ACCOUNT];
    this.attrs[ZaHome.A2_cosNum] = ZaApp.getInstance().getCosList().size();
    this.attrs[ZaHome.A2_domainNum] = ZaApp.getInstance().getDomainList().size();
    this.attrs[ZaHome.A2_serverNum] = ZaApp.getInstance().getServerList().size();
    this.attrs[ZaHome.A2_lastBackup] = true;
    this.attrs[ZaHome.A2_lastBackupTime] = "2011";
    this.attrs[ZaHome.A2_lastCleanup] = true;
    this.attrs[ZaHome.A2_lastCleanupTime] = "2012";
    this.attrs[ZaHome.A2_lastLogPurge] = false;
    this.attrs[ZaHome.A2_lastLogPurgeTime] = "2013";
    this.attrs[ZaHome.A2_expiredType] = false;
    this.attrs[ZaHome.A2_expiredMessage] = "OK";
    this.attrs[ZaHome.A2_DBCheckType] = false;
    this.attrs[ZaHome.A2_DBCheckMessage] = "OK";
    this.attrs[ZaHome.A2_serviceStatusMessage] = "Running";
    this.attrs[ZaHome.A2_serviceStatus] = true;
    this.attrs[ZaHome.A2_activeSession] = 3;
    this.attrs[ZaHome.A2_messageCount] = "120/h";
    this.attrs[ZaHome.A2_messageVolume] = "34MB/h";
}

ZaItem.loadMethods["ZaHome"].push(ZaHome.loadMethod);

ZaHome.myXModel = {
    items: [
        {id:ZaHome.A2_version,type:_STRING_,  ref:"attrs/" + ZaHome.A2_version},
    	{id:ZaHome.A2_account,type:_STRING_, ref:"attrs/" + ZaHome.A2_account},
    	{id:ZaHome.A2_domainNum,type:_STRING_, ref:"attrs/" + ZaHome.A2_domainNum},
    	{id:ZaHome.A2_cosNum,type:_STRING_, ref:"attrs/" + ZaHome.A2_cosNum},
    	{id:ZaHome.A2_serverNum,type:_STRING_, ref:"attrs/" + ZaHome.A2_serverNum},
    	{id:ZaHome.A2_accountNum,type:_STRING_, ref:"attrs/" + ZaHome.A2_accountNum},
        {id:ZaHome.A2_lastBackupTime, type:_STRING_, ref: "attrs/" + ZaHome.A2_lastBackupTime},
        {id:ZaHome.A2_lastBackup, type:_ENUM_, ref: "attrs/" + ZaHome.A2_lastBackupTime, choices: ZaModel.BOOLEAN_CHOICES},
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
        {id:ZaHome.A2_messageCount, type:_NUMBER_, ref:"attrs/" + ZaHome.A2_messageCount},
        {id:ZaHome.A2_messageVolume, type:_NUMBER_, ref:"attrs/" + ZaHome.A2_messageVolume}
    ]
}

