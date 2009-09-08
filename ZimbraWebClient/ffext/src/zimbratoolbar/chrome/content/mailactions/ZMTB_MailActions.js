var ZMTB_MailActions = function(zmtb)
{
	ZMTB_Actions.call(this, zmtb);
	zmtb.getRequestManager().addUpdateListener(this);
	this._messageComp = new ZMTB_MessageComposer(this._rqManager);
	this._folderMan.setFilter("mail", {first:[2, 5, 6, 4], exclude:[3, 14], type:"message", root:true});
	this._folderMan.setFilter("search", {first:[2, 5, 6, 4], exclude:[3, 14], type:"message", root:true, search:true});
	this._initContext();
	this._initMenu();
	this._initActions();
}

ZMTB_MailActions.prototype = new ZMTB_Actions();
ZMTB_MailActions.prototype.constructor = ZMTB_MailActions;

ZMTB_MailActions.prototype._initContext = function()
{
	var This=this;
	//Context Menu Options
	document.getElementById("ZMTB-ContextAction-AddRSS").addEventListener("command",function(){
		 This.newRSSFolderCommand();
		document.getElementById("ZimTB-NewRSS-URL").value=gContextMenu.linkURL;
		document.getElementById("ZimTB-NewRSS-Name").focus();
	}, false);
	document.getElementById("ZMTB-ContextAction-ComposeTo").addEventListener("command", function(){This._messageComp.open(document.popupNode.toString().substr(7))}, false);
	document.getElementById("contentAreaContextMenu").addEventListener("popupshowing", function(e){
		if(gContextMenu.onMailtoLink)
			document.getElementById("ZMTB-ContextAction-ComposeTo").hidden = false;
		else
			document.getElementById("ZMTB-ContextAction-ComposeTo").hidden = true;
		if(gContextMenu.onLink && (gContextMenu.linkURL.indexOf(".xml") >=0 || gContextMenu.linkURL.indexOf("format=xml") >=0) || gContextMenu.linkURL.indexOf(".rss") >=0 || gContextMenu.linkURL.indexOf(".atom") >=0)
			document.getElementById("ZMTB-ContextAction-AddRSS").hidden = false;
		else
			document.getElementById("ZMTB-ContextAction-AddRSS").hidden = true;
	}, false);
}

ZMTB_MailActions.prototype._initMenu = function()
{
	var This=this;
	//Menu items
	document.getElementById("ZimTB-NewEmail").addEventListener("command",function(event){
		var selectedText = window.content.document.getSelection().toString();
		var email = selectedText.match(/((([!#$%&'*+\-\/=?^_`{|}~\w])|([!#$%&'*+\-\/=?^_`{|}~\w][!#$%&'*+\-\/=?^_`{|}~\.\w]{0,}[!#$%&'*+\-\/=?^_`{|}~\w]))[@]\w+([-.]\w+)*\.\w+([-.]\w+)*)/);
		if(email)
			This._messageComp.open(email[0]);
		else
			This._messageComp.open();
	},false);
	document.getElementById("ZimTB-NewFolder").addEventListener("command",function(event){
		This.openActions("ZimTB-NewFolder-Bar");
		document.getElementById("ZimTB-NewFolder-Name").focus();
		This._populateList(document.getElementById("ZimTB-NewFolder-Parent"), This._folderMan.getFolders("mail"), This._localstrings.getString("mailaction_rootname"));
		for (var i=0; i < document.getElementById("ZimTB-NewFolder-Parent").itemCount; i++)
			if(document.getElementById("ZimTB-NewFolder-Parent").getItemAtIndex(i).getAttribute("value") == 1)
				document.getElementById("ZimTB-NewFolder-Parent").selectedIndex=i;
		document.getElementById("ZimTB-NewFolder-Name").value="";
	},false);
	document.getElementById("ZimTB-NewSavedSearch").addEventListener("command",function(event){
		This.openActions("ZimTB-NewSearchFolder-Bar");
		document.getElementById("ZimTB-NewSearchFolder-Name").value="";
		document.getElementById("ZimTB-NewSearchFolder-Query").value="";
		document.getElementById("ZimTB-NewSearchFolder-Name").focus();
		This._populateList(document.getElementById("ZimTB-NewSearchFolder-Parent"), This._folderMan.getFolders("search"), This._localstrings.getString("mailaction_rootname"));
		for (var i=0; i < document.getElementById("ZimTB-NewSearchFolder-Parent").itemCount; i++)
			if(document.getElementById("ZimTB-NewSearchFolder-Parent").getItemAtIndex(i).getAttribute("value") == 1)
				document.getElementById("ZimTB-NewSearchFolder-Parent").selectedIndex=i;
	},false);
	document.getElementById("ZimTB-NewRSS").addEventListener("command",function(event){
		This.newRSSFolderCommand();
		document.getElementById("ZimTB-NewRSS-Name").value="";
		document.getElementById("ZimTB-NewRSS-URL").value="";
		document.getElementById("ZimTB-NewRSS-Name").focus();
	},false);
	document.getElementById("ZimTB-ViewMail").addEventListener("command",function(event){
		This._rqManager.goToPath("?app=mail");
	},false);
}

ZMTB_MailActions.prototype._initActions = function()
{
	var This=this;
	//New Folder
	document.getElementById("ZimTB-NewFolder-Create").addEventListener("command", function(){
		if(document.getElementById("ZimTB-NewFolder-Name").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("mailaction_newfolder_needname"), null, "failure");
			return;
		}
		This.newFolder(document.getElementById("ZimTB-NewFolder-Name").value, "message", document.getElementById("ZimTB-NewFolder-Parent").selectedItem.value);
		This.hideActions();
	}, false);
	document.getElementById("ZimTB-NewFolder-Close").addEventListener("command", function(){This.hideActions()}, false);
	
	//New Search Folder
	document.getElementById("ZimTB-NewSearchFolder-Create").addEventListener("command", function(){
		if(document.getElementById("ZimTB-NewSearchFolder-Name").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("mailaction_newsaved_needname"), null, "failure");
			return;
		}
		else if(document.getElementById("ZimTB-NewSearchFolder-Query").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("mailaction_newsaved_needquery"), null, "failure");
			return;
		}
		This.newSearchFolder(document.getElementById("ZimTB-NewSearchFolder-Name").value, document.getElementById("ZimTB-NewSearchFolder-Parent").selectedItem.value, document.getElementById("ZimTB-NewSearchFolder-Query").value);
		This.hideActions();
	}, false);
	document.getElementById("ZimTB-NewSearchFolder-Close").addEventListener("command", function(){This.hideActions()}, false);
	
	//New RSS
	document.getElementById("ZimTB-NewRSS-Create").addEventListener("command", function(){
		if(document.getElementById("ZimTB-NewRSS-Name").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("mailaction_newfeed_needname"), null, "failure");
			return;
		}
		else if(document.getElementById("ZimTB-NewRSS-URL").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("mailaction_newfeed_needurl"), null, "failure");
			return;
		}
		This.newFolder(document.getElementById("ZimTB-NewRSS-Name").value, "message", document.getElementById("ZimTB-NewRSS-Parent").selectedItem.value, document.getElementById("ZimTB-NewRSS-URL").value);
		This.hideActions();
	}, false);
	document.getElementById("ZimTB-NewRSS-Close").addEventListener("command", function(){This.hideActions()}, false);
}

ZMTB_MailActions.prototype.enable = function()
{
	document.getElementById("ZimTB-Mail-Button").disabled = false;
}

ZMTB_MailActions.prototype.disable = function()
{
	document.getElementById("ZimTB-Mail-Button").disabled = true;
}

ZMTB_MailActions.prototype.receiveUpdate = function(responseObj)
{
	if(responseObj.Body.Fault && this.getRqObj(responseObj.Body.Fault.requestId))
	{
		// switch(responseObj.Body.Fault.Detail.Error.Code)
		// {
		// 	case "mail.INVALID_NAME":
		// 		this.notifyFail("Invalid folder name.");
		// 		break;
		// 	case "service.INVALID_REQUEST":
		// 		this.notifyFail(responseObj.Body.Fault.Reason.Text);
		// 		break;
		// 	case "mail.ALREADYEXISTS":
		// 		this.notifyFail("Folder name already exists.");
		// 		break;
		// 	case "mail.SEND_FAILURE":
		// 		this.notifyFail("Message could not be sent.");
		// 		break;
		// 	case "mail.SEND_FAILURE":
		// 		this.notifyFail("Folder name already exists.");
		// 		break;
		// }
	}
	if(responseObj.Body.CreateFolderResponse && responseObj.Body.CreateFolderResponse.folder)
	{
		for (var i=0; i < responseObj.Body.CreateFolderResponse.folder.length; i++)
		{
			if(responseObj.Body.CreateFolderResponse.folder[i].view == "message")
			{
				if(responseObj.Body.CreateFolderResponse.folder[i].url)
					this._zmtb.notify(this._localstrings.getString("mailaction_newfeed_success"), null, "success");
				else
					this._zmtb.notify(this._localstrings.getString("mailaction_newfolder_success"), null, "success");
				this._zmtb.update();
			}
		}
	}
	if(responseObj.Body.CreateSearchFolderResponse && responseObj.Body.CreateSearchFolderResponse.search)
		this._zmtb.notify(this._localstrings.getString("mailaction_newsaved_success"), null, "success");
	if(responseObj.Body.SendMsgResponse && responseObj.Body.SendMsgResponse.m)
		this._zmtb.notify(this._localstrings.getString("mailaction_newmessage_sent"), null, "success");
	if(responseObj.Body.SaveDraftResponse && responseObj.Body.SaveDraftResponse.m)
		this._zmtb.notify(this._localstrings.getString("mailaction_newmessage_saved"), null, "success");
}

ZMTB_MailActions.prototype.reset = function(){}

ZMTB_MailActions.prototype.newRSSFolderCommand = function()
{
	
	this.openActions("ZimTB-NewRSS-Bar");
	this._populateList(document.getElementById("ZimTB-NewRSS-Parent"), this._folderMan.getFolders("mail"), this._localstrings.getString("mailaction_rootname"));
	for (var i=0; i < document.getElementById("ZimTB-NewRSS-Parent").itemCount; i++)
		if(document.getElementById("ZimTB-NewRSS-Parent").getItemAtIndex(i).getAttribute("value") == 1) //Root folder ID
			document.getElementById("ZimTB-NewRSS-Parent").selectedIndex=i;
}

ZMTB_MailActions.prototype.newSearchFolder = function(name, parentId, query)
{
	var sd = ZMTB_AjxSoapDoc.create("CreateSearchFolderRequest", ZMTB_RequestManager.NS_MAIL);
	sd.set("search", {"name":name, "query":query, "l":parentId});
	this.addRqObj(this._rqManager.sendRequest(sd), null);
	this._rqManager.updateAll();
}