/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
var ZMTB_Toolbar = function()
{
	var This=this;
	document.getElementById("ZimTB-Toolbar").addEventListener("DOMAttrModified", function(e)
	{
		if(e.attrName == "collapsed")
		{
			if(e.newValue == "true")
				This.disable();
			else
				This.update();
		}
	}, false);
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

	var password;
  	var passwordManager = Components.classes["@mozilla.org/login-manager;1"].getService(Components.interfaces.nsILoginManager);
	var pm = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
	var logins = passwordManager.findLogins({}, 'chrome://zimbratb', null, 'Zimbra Login');
	for (var i = 0; i < logins.length; i++)
	{
		if (logins[i].username == pm.getCharPref("extensions.zmtb.username"))
		{
		   password = logins[i].password;
		   break;
		}
	}
    this._rqManager.newServer(prefManager.getCharPref("extensions.zmtb.hostname"), prefManager.getCharPref("extensions.zmtb.username"), password);

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
		image = "chrome://zimbratb/skin/default_images/zimbra_z_small.png";
	clearTimeout(window.zmtb_noteTimeout);
	this.clearNotifications();
	var note = document.getElementById("ZMTB-Notifications").appendNotification(message, 1, image, type);
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
