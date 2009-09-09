var ZMTB_FolderList = function(zmtb)
{
	ZMTB_TBItem.call(this, zmtb); //super()
	zmtb.getRequestManager().addUpdateListener(this);
	zmtb.getFolderManager().registerListener(this);
	this._messages = [];
	this._rqManager = zmtb.getRequestManager();
	this._menuList = document.getElementById("ZimTB-Folders");
	this._watchIndex = 0;
	this._folderMan = zmtb.getFolderManager();
	var This=this;
	this._rqIds = {};
	this._menuList.addEventListener("keypress", function(e)
	{
		//If we receive a modifier, leave the list box so that an action can be performed eg New Tab
		if(e.metaKey || e.ctrlKey || e.altKey)
			e.target.blur();
		//If key is alnum, open popup
		else if(e.charCode >= 48 && e.charCode <= 122)
			e.target.menupopup.openPopup(e.target);
		This._menuList.selectedIndex = This._watchIndex;
	}, true);
	this._menuList.addEventListener("change", function(e)
	{
		This._menuList.selectedIndex = This._watchIndex;
	}, false);
}

ZMTB_FolderList.prototype = new ZMTB_TBItem();
ZMTB_FolderList.prototype.constructor = ZMTB_FolderList;

ZMTB_FolderList.prototype.enable = function()
{
	document.getElementById("ZimTB-Folders").disabled = false;
}

ZMTB_FolderList.prototype.disable = function()
{
	document.getElementById("ZimTB-Folders").disabled = true;
}

ZMTB_FolderList.prototype.updateFolders = function()
{
	var folders = this._folderMan.getFolders("default");
	this._populateList(folders);
	for (var i=0; i < this._menuList.menupopup.childNodes.length; i++)
	{
		var open = this._menuList.menupopup.childNodes[i].appendItem("Open this Folder in Zimbra", this._menuList.menupopup.childNodes[i].value);
		var This=this;
		open.addEventListener("command", function(e){This.execFolder(e.target.value)}, false);
		this._menuList.menupopup.childNodes[i].menupopup.appendChild(document.createElement("menuseparator"));
	}
	this.getMessages(folders);
}


ZMTB_FolderList.prototype.reset = function()
{
	this.resetFolderList();
}

ZMTB_FolderList.prototype.resetMessageList = function()
{
	for (var i=0; i < this._menuList.menupopup.childNodes.length; i++)
		for (var j=this._menuList.menupopup.childNodes[i].itemCount; j >=2 ; j--)
			this._menuList.menupopup.childNodes[i].removeItemAt(j);
}

ZMTB_FolderList.prototype.resetFolderList = function()
{
	for (var i=this._menuList.menupopup.childNodes.length-1; i>=0 ; i--)
		this._menuList.menupopup.removeChild(this._menuList.menupopup.childNodes[i]);
}

ZMTB_FolderList.prototype.execFolder = function(id)
{
	this._rqManager.goToPath("?app=mail&f="+id, this._scriptFolder);
}

ZMTB_FolderList.prototype._scriptFolder = function(loc, doc)
{
	if(doc.getElementById("zmtb_customScript"))
		return;
	var s = doc.createElement("script");
	s.id = "zmtb_customScript";
	var q = loc.path.match(/\bf=([^&#]+)\b/);
	var t = doc.createTextNode('window.appCtxt.getSearchController().search({"query":"inid:'+unescape(q[1])+'"})');
	s.appendChild(t);
	doc.body.appendChild(s);
}

ZMTB_FolderList.prototype.execMessage = function(message)
{
	this._rqManager.goToPath("?app=mail&view=msg&id="+message.id);
}

ZMTB_FolderList.prototype.receiveUpdate = function(responseObj)
{
	if(responseObj.Body.SearchResponse && responseObj.Body.SearchResponse.m)
	{
		this._messages = [];
		this._addMessages(responseObj.Body.SearchResponse.m);
		this._populateFolders();
	}
	else if(responseObj.Body.BatchResponse)
	{
		if(responseObj.Body.BatchResponse.SearchResponse)
		{
			this._messages = [];
			for (var i=0; i < responseObj.Body.BatchResponse.SearchResponse.length; i++)
			{
				if(responseObj.Body.BatchResponse.SearchResponse[i].m)
				{
					if(this.getRqObj(responseObj.Body.BatchResponse.SearchResponse[i].requestId))
					{
						var fid = this.getRqObj(responseObj.Body.BatchResponse.SearchResponse[i].requestId).folder;
						var menu = this._getItemInMenu(fid);
						menu.style.fontWeight = "bold";
						this._addSearchMessages(responseObj.Body.BatchResponse.SearchResponse[i].m, this.getRqObj(responseObj.Body.BatchResponse.SearchResponse[i].requestId));
					}
					else
						this._addMessages(responseObj.Body.BatchResponse.SearchResponse[i].m);
				}
			}
			this._populateFolders();
		}
	}	
}

ZMTB_FolderList.prototype.receiveError = function(error)
{
}

ZMTB_FolderList.prototype.getMessages = function(folders)
{
	var sd = ZMTB_AjxSoapDoc.create("BatchRequest", ZMTB_RequestManager.NS_ZIMBRA);
	for (var i=0; i < folders.length; i++)
	{
		if(folders[i].unread > 0)
			sd.set("SearchRequest", {"types":"message", "limit":"20", "query":"is:unread and inid:"+folders[i].id}, sd.getMethod(), ZMTB_RequestManager.NS_MAIL);
		else if(folders[i].query)
		{
			var rid = this._rqManager.getNewRqId();
			this.addRqObj(rid, {folder:folders[i].id});
			sd.set("SearchRequest", {"requestId":rid, "types":"message", "limit":"20", "query":"is:unread AND "+folders[i].query}, sd.getMethod(), ZMTB_RequestManager.NS_MAIL);
		}
	}
	this.addRqObj(this._rqManager.sendRequest(sd), null);
}

ZMTB_FolderList.prototype._addSearchMessages = function(messages, rqObj)
{
	for (var i=0; i < messages.length; i++)
		this._messages.push({id:messages[i].id, from:(messages[i].e[0].p?messages[i].e[0].p:messages[i].e[0].a), sub:messages[i].su, folder:rqObj.folder});
}

ZMTB_FolderList.prototype._addMessages = function(messages)
{
	for (var i=0; i < messages.length; i++)
		this._messages.push({id:messages[i].id, from:(messages[i].e[0].p?messages[i].e[0].p:messages[i].e[0].a), sub:messages[i].su, folder:messages[i].l});
}

ZMTB_FolderList.prototype._populateFolders = function()
{
	this.resetMessageList();
	this._messages.forEach(function(element)
	{
		this._addToFolder(element, element.folder);
	}, this);
}

ZMTB_FolderList.prototype._addToFolder = function(message, folderId)
{
	if(this._getMenuIndex(folderId)>=0)
	{
		var This = this;
		var item = this._menuList.menupopup.childNodes[this._getMenuIndex(folderId)].appendItem(message.from+" - "+message.sub, message.id);
		item.addEventListener("command", function(){This.execMessage(message)}, false);
	}
}

ZMTB_FolderList.prototype._populateList = function(folders)
{
	this.resetFolderList();
	
	///Using new folder manager
	for (var i=0; i < folders.length; i++)
		this._addToMenu(folders[i], "ZimTB-"+folders[i].name+"-Folder");
	this._menuList.selectedIndex = 0;
	var pm = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
	var df = pm.getCharPref("extensions.zmtb.defaultWatch");
	for (var i=0; i < this._menuList.itemCount; i++)
		if(this._menuList.getItemAtIndex(i).value == df)
			this._menuList.selectedIndex = this._watchIndex = i;
	if(!this._menuList.selectedIndex)
		this._menuList.selectedIndex = 0;
	this._menuList.className = this._menuList.selectedItem.className;
	this._menuList.style.fontWeight = this._menuList.selectedItem.style.fontWeight;
}

ZMTB_FolderList.prototype._addToMenu = function(folder, class)
{
	var mL = (this._folderMan.getDepth(folder.id)*20).toString()+"px";
	var m = document.createElement("menu");
	if(!folder.query)
		m.setAttribute("label", folder.name + "("+folder.unread+")");
	else
	{
		m.setAttribute("label", folder.name);
		m.setAttribute("query", folder.query);
	}
	m.setAttribute("value", folder.id);
	this._menuList.menupopup.appendChild(m);
	m.style.marginLeft = mL;
	if(folder.unread > 0)
		m.style.fontWeight="bold";
	else
		m.style.fontWeight="normal";
	if(class)
		m.className = class + " menu-iconic ZimTB-Mail-Folder";
	if(folder.rss)
		m.className = "ZimTB-RSS-Folder menu-iconic";
	else if(folder.query)
		m.className = "ZMTB-Search-Folder menu-iconic";
}

ZMTB_FolderList.prototype._getMenuIndex = function(id)
{
	for(var i=0; i<this._menuList.menupopup.childNodes.length; i++)
		if(this._menuList.menupopup.childNodes[i].value == id)
			return i;
}

ZMTB_FolderList.prototype._inMenu = function(id)
{
	for (var i=0; i < this._menuList.menupopup.childNodes.length; i++)
		if(id == this._menuList.menupopup.childNodes[i].value)
			return true;
	return false;
}

ZMTB_FolderList.prototype._getItemInMenu = function(id)
{
	for (var i=0; i < this._menuList.menupopup.childNodes.length; i++)
		if(this._menuList.menupopup.childNodes[i].value == id)
			return this._menuList.menupopup.childNodes[i];
	return null;
}