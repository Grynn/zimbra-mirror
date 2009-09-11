var ZMTB_RequestManager = function(zmtb)
{
	this._zmtb = zmtb;
	this._serverURL = "";
	this._username = "";
	this._password = "";
	this._listeners = [];
	this._changeToken = 0;
	this._notifySeq = 0;
	this._tabPref = "";
	this._rqCount = 0;
	this._timeout = null;
	this._serverVersion = "";
}

ZMTB_RequestManager.NS_ACCOUNT = "urn:zimbraAccount";
ZMTB_RequestManager.NS_MAIL = "urn:zimbraMail";
ZMTB_RequestManager.NS_ZIMBRA = "urn:zimbra";

ZMTB_RequestManager.prototype.getNewRqId = function()
{
	return this._rqCount++;
}

ZMTB_RequestManager.prototype.getServerVersion = function()
{
	return this._serverVersion;
}

ZMTB_RequestManager.prototype.updateAll = function()
{
	if(!ZMTBCsfeCommand.getAuthToken())
		this._authenticate();
	var sd = ZMTB_AjxSoapDoc.create("BatchRequest", ZMTB_RequestManager.NS_ZIMBRA);
	sd.set("GetFolderRequest", null, sd.getMethod(), ZMTB_RequestManager.NS_MAIL);
	sd.set("SearchRequest", {"types":"appointment", "calExpandInstStart":((new Date()).getTime()-1000*60*60*6), "calExpandInstEnd":((new Date()).getTime()+18*60*60*1000), "query":"in:calendar"}, sd.getMethod(), ZMTB_RequestManager.NS_MAIL);
	sd.set("GetTagRequest", null, sd.getMethod(), ZMTB_RequestManager.NS_MAIL);
	sd.set("GetInfoRequest", {"sections":"mbox,idents,sigs"}, sd.getMethod(), ZMTB_RequestManager.NS_ACCOUNT);
	try{
		(new ZMTBCsfeCommand()).invoke({soapDoc:sd, asyncMode:true, callback:new ZMTB_AjxCallback(this, this.parseResponse), changeToken:this._changeToken});
		clearTimeout(this._timeout);
		var This=this;
		this._timeout = setTimeout(function(){
			This._zmtb.disable();
		}, 5000);
	}catch(ex){}
}

ZMTB_RequestManager.prototype.newServer = function(host, user, pass)
{
	this.setServerURL(host);
	this.setUsername(user);
	this.setPassword(pass);
	this._zmtb.disable();
	this.reset();
	this._authenticate();
}

ZMTB_RequestManager.prototype.sendRequest = function(soapDoc)
{
	var This = this;
	if(!ZMTBCsfeCommand.getAuthToken())
		this._authenticate();
	var req = new ZMTBCsfeCommand();
	try{
		req.invoke({soapDoc:soapDoc, asyncMode:true, callback:new ZMTB_AjxCallback(this, this.parseResponse), changeToken:this._changeToken});
		clearTimeout(this._timeout);
		// this._timeout = setTimeout(function(){
		// 	This._zmtb.notify(This._zmtb.getLocalStrings().getString("requestfail"), null, "failure");
		// 	This._zmtb.disable();
		// }, 5000);
	}catch(ex){
		this._zmtb.notify(this._zmtb.getLocalStrings().getString("requestfail"), null, "failure");
	}
}

ZMTB_RequestManager.prototype.addUpdateListener = function(updatable)
{
	this._listeners.push(updatable);
}

ZMTB_RequestManager.prototype.reset = function()
{
	this._listeners.forEach(function(element){
		element.reset();
	});
	ZMTBCsfeCommand.clearAuthToken();
	this._changeToken = 0;
	this._serverVersion = "";
	// this._authenticate();
}

ZMTB_RequestManager.prototype.setServerURL = function(serverURL)
{
	this._serverURL = serverURL;
	ZMTBCsfeCommand.setServerUri(this._serverURL+"service/soap/");
}

ZMTB_RequestManager.prototype.getServerURL = function()
{
	return this._serverURL;
}

ZMTB_RequestManager.prototype.setUsername = function(username)
{
	this._username = username;
}

ZMTB_RequestManager.prototype.getUsername = function()
{
	return this._username;
}

ZMTB_RequestManager.prototype.setPassword = function(password)
{
	this._password = password;
}

ZMTB_RequestManager.prototype.setTabPreference = function(prefString)
{
	this._tabPref = prefString;
}

ZMTB_RequestManager.prototype.getTabPreference = function()
{
	return this._tabPref;
}

ZMTB_RequestManager.prototype.goToPath = function(path, callback, callObj)
{
	var ios = Components.classes["@mozilla.org/network/io-service;1"]
                    .getService(Components.interfaces.nsIIOService);
	var url = this.getServerURL()+path;
	//Force advanced client
	var args = url.indexOf("?");
	if(args > 0)
		url = url.substr(0, args+1) + "client=advanced&" + url.substr(args+1);
	else
		url += "?client=advanced";
	this._curLoc = url;

	if(this.getTabPreference() == "New Window")
	{
		var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator);
		var enumerator = wm.getEnumerator("navigator:browser");
		while(enumerator.hasMoreElements())
		{
			var win = enumerator.getNext();
			tabBrowser = win.getBrowser()
			for (var i=0; i < tabBrowser.browsers.length; i++) {
				if(tabBrowser.browsers[i].contentWindow.wrappedJSObject.ZmZimbraMail)
				{
					tabBrowser.selectedTab = tabBrowser.mTabs[i]
					tabBrowser.browsers[i].loadURI(url);
					win.focus();
					return;
				}
			};
		}
		var newWin = window.open(url);
		if(callback)
			newWin.gBrowser.selectedBrowser.addProgressListener(new ZMTB_BrowserListener(callback, callObj));
	}
	else if(this.getTabPreference() == "New Tab")
	{
		for (var i=0; i < gBrowser.browsers.length; i++) {
			if(gBrowser.browsers[i].contentWindow.wrappedJSObject.ZmZimbraMail)
			{
				gBrowser.selectedTab = gBrowser.mTabs[i]
				gBrowser.browsers[i].loadURI(url);
				return;
			}
		};
		gBrowser.selectedTab = gBrowser.addTab(url);
		if(callback)
			gBrowser.selectedBrowser.addProgressListener(new ZMTB_BrowserListener(callback, callObj));
	}
	else
	{
		gBrowser.loadURI(url);
		if(callback)
			gBrowser.selectedBrowser.addProgressListener(new ZMTB_BrowserListener(callback, callObj));
	}
}

ZMTB_RequestManager.prototype._authenticate = function()
{
	if(this._serverURL && this._username &&  this._password)
	{
		var This=this;
		var sd = ZMTB_AjxSoapDoc.create("AuthRequest", ZMTB_RequestManager.NS_ACCOUNT);
		sd.set("account", this._username).setAttribute("by", "name");
		sd.set("password", this._password);
		var c = new ZMTB_AjxCallback(this, this.parseResponse);
		var req = new ZMTBCsfeCommand();
		try{
			req.invoke({soapDoc:sd, asyncMode:true, callback:c, noAuthToken: true});
			clearTimeout(this._timeout);
			this._timeout = setTimeout(function(){
				This._zmtb.disable();
			}, 5000);
		}catch(ex){
			this._zmtb.notify(this._zmtb.getLocalStrings().getString("requestfail"), null, "failure");
		}
	}
}

ZMTB_RequestManager.prototype.parseResponse = function(result)
{
	clearTimeout(this._timeout);
	try{
		var rd = result.getResponse();
	}catch(ex)
	{
		if(ex.code == ZMTBCsfeException.NETWORK_ERROR)
			this._zmtb.disable();
		return;
	}
	if(rd.code)
	{
		var notify = true;
		switch(rd.code)
		{
			case "NETWORK_ERROR":
			case "account.CHANGE_PASSWORD":
			case "account.AUTH_FAILED":
				this._zmtb.disable();
				notify = false;
				break;
		}
		if(notify)
			this._zmtb.notify(this._zmtb.getLocalStrings().getString("requestfail"), null, "failure");
		this._listeners.forEach(function(element){
			element.receiveError(rd);
		});
		return;
	}
	this._zmtb.enable();
	if(rd.Body.BatchResponse && rd.Body.BatchResponse.GetInfoResponse && rd.Body.BatchResponse.GetInfoResponse[0].version)
		this._serverVersion = rd.Body.BatchResponse.GetInfoResponse[0].version;
	// if(rd.Header.context.refresh && rd.Header.context.refresh.version)
	// 	this._serverVersion = rd.Header.context.refresh.version;
	if(rd.Header.context.notify)
	{
		for (var i=0; i < rd.Header.context.notify.length; i++)
			if(this._notifySeq < rd.Header.context.notify[i].seq)
				this._notifySeq = rd.Header.context.notify[i].seq;
	}
	if(rd.Header.context.change)
	{
		var ct = rd.Header.context.change.token;
		if(ct>this._changeToken)
			this._changeToken = ct;
	}
	if(rd.Body.AuthResponse && rd.Body.AuthResponse.authToken)
	{
		var token;
		if(typeof(rd.Body.AuthResponse.authToken) == "object")
			token = rd.Body.AuthResponse.authToken[0]._content;
		else
			token = rd.Body.AuthResponse.authToken;
		ZMTBCsfeCommand.setAuthToken(token, rd.Body.AuthResponse.lifetime);
		var ios = Components.classes["@mozilla.org/network/io-service;1"].getService(Components.interfaces.nsIIOService);
	    var cookieUri = ios.newURI(this.getServerURL(), null, null);
	    var cookieSvc = Components.classes["@mozilla.org/cookieService;1"].getService(Components.interfaces.nsICookieService);
		var cookieString = "ZM_AUTH_TOKEN="+token+";expires=session";
		if(this.getServerURL().indexOf("https") >=0)
			cookieString += ";secure"
	    cookieSvc.setCookieString(cookieUri, null, cookieString, null);
		this.updateAll();
	}
	this._listeners.forEach(function(element){
		element.receiveUpdate(rd);
	});
}

var ZMTB_BrowserListener = function(callback, callObj)
{
	this._callback = callback;
	this._callObj = callObj;
}

ZMTB_BrowserListener.prototype.QueryInterface = function(aIID)
{
	if (aIID.equals(Components.interfaces.nsIWebProgressListener) || aIID.equals(Components.interfaces.nsISupportsWeakReference) || aIID.equals(Components.interfaces.nsISupports))
		return this;
	throw Components.results.NS_NOINTERFACE;
};

ZMTB_BrowserListener.prototype.onStateChange = function(aWebProgress, aRequest, aFlag, aStatus)
{
	if(!(aFlag & STATE_STOP))
		return;
	if(!aWebProgress.isLoadingDocument && aWebProgress.currentURI)
	{
		aWebProgress.removeProgressListener(this);
		if(!this._callObj)
			callObj = this;
		else
			callObj = this._callObj;
		this._callback.call(callObj, aWebProgress.currentURI, aWebProgress.document)
		this._callback(aWebProgress.currentURI, aWebProgress.document);
	}
};

ZMTB_BrowserListener.prototype.onLocationChange = function(aProgress, aRequest, aURI){};
ZMTB_BrowserListener.prototype.onProgressChange = function(aWebProgress, aRequest, curSelf, maxSelf, curTot, maxTot){};
ZMTB_BrowserListener.prototype.onStatusChange = function(aWebProgress, aRequest, aStatus, aMessage){};
ZMTB_BrowserListener.prototype.onSecurityChange = function(aWebProgress, aRequest, aState){};