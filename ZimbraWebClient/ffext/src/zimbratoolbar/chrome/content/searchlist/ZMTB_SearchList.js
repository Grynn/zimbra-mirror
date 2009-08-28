var ZMTB_SearchList = function(zmtb)
{
	ZMTB_TBItem.call(this, zmtb);
	zmtb.getRequestManager().addUpdateListener(this);
	this._recentSearch = [];
	this._tags = [];
	this._menuList = document.getElementById("ZimTB-SearchTerms");
	this._tagMenu = document.getElementById("ZimTB-Tags");
	this._rqManager = zmtb.getRequestManager();
	this.srchState = ZMTB_SearchList._RECENTSEARCH;
	this.customSearch = false;
	var This = this;
	this._menuList.addEventListener("keypress", function(event){if(event.keyCode==event.DOM_VK_RETURN || event.keyCode==event.DOM_VK_ENTER)This.execSearch(event.target.label)}, false);
	document.getElementById("ZMTB-SearchButton").addEventListener("command", function(){This.execSearch(This._menuList.label)}, false);
	this._menuList.addEventListener("command", function(event){event.stopPropagation(); This.execRecent(event.target.label)}, false);
	this._tagMenu.addEventListener("command", function(event){event.stopPropagation(); This.execTag(event.target.label);}, false);
	this.loadRecent();
	var prefListener = new ZMTB_PrefListener("extensions.zmtb.", function(branch, name)
    {
		if(name == "recentSearch")
			This.loadRecent();
	});
    //Register
    prefListener.register();
}

ZMTB_SearchList.prototype = new ZMTB_TBItem();
ZMTB_SearchList.prototype.constructor = ZMTB_SearchList;

ZMTB_SearchList.EMPTYSAVELIST = "No Saved Searches";
ZMTB_SearchList.EMPTYTAGLIST =  "No Tags";

ZMTB_SearchList.prototype.enable = function()
{
	document.getElementById("ZimTB-SearchTerms").disabled = false;
	document.getElementById("ZMTB-SearchButton").disabled = false;
}

ZMTB_SearchList.prototype.disable = function()
{
	document.getElementById("ZimTB-SearchTerms").disabled = true;
	document.getElementById("ZMTB-SearchButton").disabled = true;
}

ZMTB_SearchList.prototype.reset = function()
{
	for (var i=0; i < this._tagMenu.itemCount; i++)
		this._tagMenu.removeItemAt(i);
}

ZMTB_SearchList.prototype.execRecent = function(query)
{
	this._rqManager.goToPath("", this._scriptSearch, this);
}

ZMTB_SearchList.prototype.execTag = function(tag)
{
	this._menuList.value='tag:\"'+tag+'\"';
	this._rqManager.goToPath("", this._scriptSearch, this);
}

ZMTB_SearchList.prototype._scriptSearch = function(loc, doc)
{
	if(doc.getElementById("zmtb_customScript"))
		return;
	var s = doc.createElement("script");
	s.id = "zmtb_customScript";
	var q = this._menuList.label.replace(/\"/g,'\\"');
	var t = doc.createTextNode('window.appCtxt.getSearchController().search({"query":"'+q+'"})');
	s.appendChild(t);
	doc.body.appendChild(s);
}

ZMTB_SearchList.prototype.execSearch = function(query)
{
	//Open tab and search
	this._rqManager.goToPath("", this._scriptSearch, this);
	// this._rqManager.goToPath("?app=mail&q="+query, this._scriptSearch);
	//Then save it
	this.addToRecent(query);
	this.loadRecent();
}

ZMTB_SearchList.prototype.receiveUpdate = function(responseObj)
{
	if(responseObj.code)
		return;
	if(responseObj.Header.context.refresh)
	{
		this._tags =[];
		for (var i=0; i < responseObj.Header.context.refresh.tags.length; i++)
			this._tags.push({name:responseObj.Header.context.refresh.tags[i].name, value:responseObj.Header.context.refresh.tags[i].id});
		this.loadTags();
	}
	if(responseObj.Body.GetTagResponse || (responseObj.Body.BatchResponse && responseObj.Body.BatchResponse.GetTagResponse))
	{
		var resp = (responseObj.Body.GetTagResponse?responseObj.Body.GetTagResponse:responseObj.Body.BatchResponse.GetTagResponse[0]);
		this._tags = [];
		if(resp.tag)
			for (var i=0; i < resp.tag.length; i++)
				this._tags.push({name:resp.tag[i].name, value:resp.tag[i].id, color:resp.tag[i].color});
		this.loadTags();
	}
}

ZMTB_SearchList.prototype.addToList = function(array, list)
{
	array.forEach(function(element)
	{
		var el = list.appendItem(element.name, element.value);
		el.className = (element.class?element.class:"");
	});
}

ZMTB_SearchList.prototype.resetList = function()
{
	for (var i=this._menuList.menupopup.childNodes.length-1; i > 2; i--)
		this._menuList.menupopup.removeChild(this._menuList.menupopup.childNodes[i]);
}

ZMTB_SearchList.prototype.addToRecent = function(query)
{
	var prefManager = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
	var recent = prefManager.getCharPref("extensions.zmtb.recentSearch");
	if(recent == "")
		recent = escape(query);
	else
		recent = escape(query)+", "+recent;
	//Maintain length
	if(recent.split(", ").length > 10)
	{
		var last = recent.lastIndexOf(",");
		recent = recent.substr(0, last);
	}
	prefManager.setCharPref("extensions.zmtb.recentSearch", recent);
}

ZMTB_SearchList.prototype.loadRecent = function(event)
{
	var prefManager = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
	var recent = prefManager.getCharPref("extensions.zmtb.recentSearch");
	var srchlist = recent.split(", ");
	this.resetList();
	this._recentSearch = [];
	for (var i=0; i < srchlist.length; i++)
		if(srchlist[i]!="")
			this._recentSearch.push({name:unescape(srchlist[i]), value:srchlist[i]});
	this.addToList(this._recentSearch, this._menuList);
}


ZMTB_SearchList.prototype.loadTags = function()
{
	for (var i=this._tagMenu.itemCount; i >=0; i--)
		this._tagMenu.removeItemAt(i);
	for (var i=0; i < this._tags.length; i++)
		this._tags[i].class = "menuitem-iconic ZimTB-Tag-Color-"+this._tags[i].color;
	this.addToList(this._tags, this._tagMenu);
	if(this._tagMenu.itemCount==0)
	{
		var m = this._tagMenu.appendItem(ZMTB_SearchList.EMPTYTAGLIST);
		m.setAttribute("disabled", "true");
	}
}