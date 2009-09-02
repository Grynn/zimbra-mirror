var ZMTB_Toolbar = function()
{
	var This=this;
	this._rqManager = new ZMTB_RequestManager(this);
	this._folderManager = new ZMTB_FolderManager(this._rqManager);
	this._tbItems = [];
	this._disabled = false;
	this._rqManager.addUpdateListener(this._folderManager);
	this._localStrings = document.getElementById("ZMTB-LocalStrings");
	var prefManager = this._prefManager = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
    this._updateTimer = window.setInterval(function(){This.update()}, prefManager.getCharPref("extensions.zmtb.updatefreq") * 60 * 1000);
    var prefListener = new ZMTB_PrefListener("extensions.zmtb.", function(branch, name)
    {
        switch (name)
        {
	        case "openLinksIn":
	            This._rqManager.setTabPreference(prefManager.getCharPref("extensions.zmtb.openLinksIn"))
	            break;
	        case "updatefreq":
	            window.clearInterval(this._updateTimer);
	            this._updateTimer = window.setInterval(function(){This.update()}, prefManager.getCharPref("extensions.zmtb.updatefreq") * 60 * 1000);
	            break;
        }
	});
    prefListener.register();
    this._rqManager.newServer(prefManager.getCharPref("extensions.zmtb.hostname"), prefManager.getCharPref("extensions.zmtb.username"));
}

ZMTB_Toolbar.prototype.reset = function()
{
	this._rqManager.reset();
}

ZMTB_Toolbar.prototype.enable = function()
{
	// document.getElementById("ZMTB-Notifications").hidden = false;
	this._disabled = false;
	clearInterval(this._updateTimer);
	var This = this;
	this._updateTimer = window.setInterval(function(){This.update()}, This._prefManager.getCharPref("extensions.zmtb.updatefreq") * 60 * 1000);
	for (var i=0; i < this._tbItems.length; i++)
		this._tbItems[i].enable();
}

ZMTB_Toolbar.prototype.disable = function()
{
	// document.getElementById("ZMTB-Notifications").hidden = true;
	this._disabled = true;
	clearInterval(this._updateTimer);
	var This = this;
	this._updateTimer = window.setInterval(function(){This._rqManager.newServer(This._prefManager.getCharPref("extensions.zmtb.hostname"), This._prefManager.getCharPref("extensions.zmtb.username"))}, This._prefManager.getCharPref("extensions.zmtb.updatefreq") * 60 * 1000);
	for (var i=0; i < this._tbItems.length; i++)
		this._tbItems[i].disable();
}

ZMTB_Toolbar.prototype.addTBItem = function(TBItem)
{
	this._tbItems.push(TBItem);
	TBItem.disable();
}

ZMTB_Toolbar.prototype.update = function()
{
	this._rqManager.updateAll();
}

ZMTB_Toolbar.prototype.getRequestManager = function()
{
	return this._rqManager;
}

ZMTB_Toolbar.prototype.getLocalStrings = function()
{
	return this._localStrings;
}

ZMTB_Toolbar.prototype.getFolderManager = function()
{
	return this._folderManager;
}

ZMTB_Toolbar.prototype.notify = function(message, image, type)
{
	if(type == "success")
		type=document.getElementById("ZMTB-Notifications").PRIORITY_INFO_MEDIUM;
	else if(type == "failure")
		type=document.getElementById("ZMTB-Notifications").PRIORITY_CRITICAL_MEDIUM;
	if(!image)
		image = "chrome://zimbratb/skin/zimbra_z_small.png";
	clearTimeout(window.zmtb_noteTimeout);
	// document.getElementById("ZMTB-Notifications").removeAllNotifications();
	this.clearNotifications();
	var note = document.getElementById("ZMTB-Notifications").appendNotification(message, 1, image, type);
	// window.zmtb_noteTimeout = setTimeout('document.getElementById("ZMTB-Notifications").removeAllNotifications()', 3000);
	var This=this;
	window.zmtb_noteTimeout = setTimeout(function(){This.clearNotification(note)}, 3000);
}

ZMTB_Toolbar.prototype.clearNotification = function(note)
{
	for (var i = document.getElementById("ZMTB-Notifications").childNodes.length - 1; i >= 0; i--){
		if(document.getElementById("ZMTB-Notifications").childNodes[i] == note)
		{
			document.getElementById("ZMTB-Notifications").removeChild(document.getElementById("ZMTB-Notifications").childNodes[i]);
			return;
		}
	};
}

ZMTB_Toolbar.prototype.clearNotifications = function(note)
{
	for (var i = document.getElementById("ZMTB-Notifications").childNodes.length - 1; i >= 0; i--)
		document.getElementById("ZMTB-Notifications").removeChild(document.getElementById("ZMTB-Notifications").childNodes[i]);
}

function ZMTB_PrefListener(branchName, func)
 {
    var prefService = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
    var branch = prefService.getBranch(branchName);
    branch.QueryInterface(Components.interfaces.nsIPrefBranch2);
    this.register = function()
    {
        branch.addObserver("", this, false);
        branch.getChildList("", {})
        .forEach(function(name) {
            func(branch, name);
        });
    };
    this.unregister = function unregister()
    {
        if (branch)
        branch.removeObserver("", this);
    };
    this.observe = function(subject, topic, data)
    {
        if (topic == "nsPref:changed")
        	func(branch, data);
    };
}


function ZimTB_GetByClass(class)
 {
    var matches = [];
    var allTags = document.getElementsByTagName("*");
    for (i = 0; i < allTags.length; i++)
        if (allTags[i].className.indexOf(class) >= 0)
            matches.push(allTags[i]);
    return matches;
}

// ZMTB_Toolbar.prototype.includeComponent = function(URL)
// {
// 	this._loadQueue.push(URL);
// 	if(!this._loading)
// 	{
// 		document.loadOverlay(this._loadQueue.shift(), this);
// 		this._loading=true;
// 	}
// }

// ZMTB_Toolbar.prototype.observe = function(subject, topic, data)
// {
// 	if(topic == "xul-overlay-merged" && this._loadQueue.length > 0)
// 		document.loadOverlay(this._loadQueue.shift(), this);
// };
