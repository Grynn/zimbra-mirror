var ZMTB_ZimbraActions = function(zmtb)
{
	this.super(zmtb)
	var This = this;
	document.getElementById("ZimTB-OpenAdvanced").addEventListener("click",function(event){
		This._rqManager.goToPath("");
	},false);
	document.getElementById("ZimTB-OpenStandard").addEventListener("click",function(event){
		This._rqManager.goToPath("zimbra/h/");
	},false);
	document.getElementById("ZimTB-OpenPreferences").addEventListener("click",function(event){
		This.openPrefsCommand();
	},false);
	document.getElementById("ZimTB-Refresh-Button").addEventListener("command", function(){This._zmtb.update()}, false);
}

ZMTB_ZimbraActions.prototype = new ZMTB_Actions();
ZMTB_ZimbraActions.prototype.constructor = ZMTB_ZimbraActions;
ZMTB_ZimbraActions.prototype.super = ZMTB_Actions;

ZMTB_ZimbraActions.prototype.enable = function()
{
	document.getElementById("ZimTB-OpenAdvanced").disabled = false;
	document.getElementById("ZimTB-OpenStandard").disabled = false;
}

ZMTB_ZimbraActions.prototype.disable = function()
{
	document.getElementById("ZimTB-OpenAdvanced").disabled = true;
	document.getElementById("ZimTB-OpenStandard").disabled = true;
}

ZMTB_ZimbraActions.prototype.openPrefsCommand = function(name, parentId, query)
{
	var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator);
	var enumerator = wm.getEnumerator("");
	while(enumerator.hasMoreElements())
	{
		var win = enumerator.getNext();
		if(win.location == "chrome://zimbratb/content/preferences/preferences.xul")
		{
			win.focus();
			return;
		}
	}
	window.openDialog("chrome://zimbratb/content/preferences/preferences.xul", "preferences", "centerscreen, chrome, titlebar");
}