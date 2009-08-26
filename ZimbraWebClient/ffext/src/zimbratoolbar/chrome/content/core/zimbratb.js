//NOTE: AjxSoapDoc was modified for this application.
window.addEventListener("load", ZMTB_Init, false);

var com_zimbra_tb_reqManager;
var com_zimbra_tb;
var com_zimbra_tb_foldManager;
var com_zimbra_tb_apptManager;

function ZMTB_Init()
 {
    //Init
    var prefManager = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
    var reqManager = com_zimbra_tb_reqManager = new ZMTB_RequestManager();
    var folderManager = com_zimbra_tb_foldManager = new ZMTB_FolderManager(reqManager);
    var folderList = new ZMTB_FolderList(reqManager, folderManager);
    var searchList = new ZMTB_SearchList(reqManager);
    var mailActions = new ZMTB_MailActions(reqManager, folderManager);
    var contactActions = new ZMTB_ContactActions(reqManager, folderManager);
    var calendarActions = new ZMTB_CalendarActions(reqManager, folderManager);
    var taskActions = new ZMTB_TaskActions(reqManager, folderManager);
    var zimbraActions = new ZMTB_ZimbraActions(reqManager, folderManager);
    com_zimbra_tb_apptManager = new ZMTB_ApptManager(reqManager);
    var updateTimer = window.setInterval('ZimTB_Update()', prefManager.getCharPref("extensions.zmtb.updatefreq") * 60 * 1000);
    var prefListener = new ZMTB_PrefListener("extensions.zmtb.", function(branch, name)
    {
        switch (name)
        {
        case "openLinksIn":
            com_zimbra_tb_reqManager.setTabPreference(prefManager.getCharPref("extensions.zmtb.openLinksIn"))
            break;
        case "updatefreq":
            window.clearInterval(updateTimer);
            updateTimer = window.setInterval('ZimTB_Update()', prefManager.getCharPref("extensions.zmtb.updatefreq") * 60 * 1000);
            break;
        case "recentSearch":
            searchList.loadRecent();
            break;
        }
	});
    //Register
    prefListener.register();
    reqManager.addUpdateListener(folderManager);
    reqManager.addUpdateListener(folderList);
    reqManager.addUpdateListener(searchList);
    reqManager.addUpdateListener(com_zimbra_tb_apptManager);
    folderManager.registerListener(folderList);
	reqManager.addUpdateListener(mailActions);
	reqManager.addUpdateListener(contactActions);
	reqManager.addUpdateListener(calendarActions);
	reqManager.addUpdateListener(taskActions);
    reqManager.newServer(prefManager.getCharPref("extensions.zmtb.hostname"), prefManager.getCharPref("extensions.zmtb.username"));
}

function ZimTB_GetRequestManager()
 {
    return com_zimbra_tb_reqManager;
}

function ZimTB_Update()
{
    com_zimbra_tb_reqManager.updateAll();
}


function ZMTB_ApptNotify(id)
{
	com_zimbra_tb_apptManager.notify(id);
}


