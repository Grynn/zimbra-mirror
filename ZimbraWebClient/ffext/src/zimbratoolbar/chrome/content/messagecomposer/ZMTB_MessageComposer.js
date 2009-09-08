var ZMTB_MessageComposer = function(zmtb)
{
	this._rqManager = zmtb.getRequestManager();
	this._localStrings = zmtb.getLocalStrings();
	this._panel = null;
	this._toBox = null;
	this._ccBox = null;
	this._subBox = null;
	this._messBox = null;
	this._pgListener = new ZMTB_AttachProgressListener(this);
	this._dragObserver = new ZMTB_AttachDragObserver(this);
	this._attachments = [];
	this._files = [];
}

ZMTB_MessageComposer.TOFIELD = "ZMTB-MessageComposer-ToField";
ZMTB_MessageComposer.CCFIELD = "ZMTB-MessageComposer-CCField";
ZMTB_MessageComposer.SUBFIELD = "ZMTB-MessageComposer-SubField";
ZMTB_MessageComposer.MESSFIELD = "ZMTB-MessageComposer-MessField";
ZMTB_MessageComposer.SENDBUTTON = "ZMTB-MessageComposer-Send";
ZMTB_MessageComposer.SAVEBUTTON = "ZMTB-MessageComposer-Save";
ZMTB_MessageComposer.CANCELBUTTON = "ZMTB-MessageComposer-Cancel";
ZMTB_MessageComposer.ATTACHBROWSER = "ZMTB-MessageComposer-AttachBrowser";
ZMTB_MessageComposer.ERRORLABEL = "ZMTB-MessageComposer-ErrorLabel";
ZMTB_MessageComposer.FILEINPUT = "ZMTB-MessageComposer-FileInput";
ZMTB_MessageComposer.ATTACHBOX = "ZMTB-MessageComposer-AttachBox";
ZMTB_MessageComposer.LOADING = "ZMTB-MessageComposer-LoadingIcon";

ZMTB_MessageComposer.prototype.open = function(email)
{
	if(this._panel && !this._panel.closed)
		this._panel.focus();
	else
	{
		this._attachments = [];
		var x = window.screenX + 200;
		var y = window.screenY + 150;
		this._panel = window.open("chrome://zimbratb/content/messagecomposer/compose.xul", "zimbracompose", "chrome,top="+y+",left="+x+",width=450,height=450");
		var This=this;
		this._panel.addEventListener("load", function(){This._addEvents(email)}, false);
	}
}

ZMTB_MessageComposer.prototype._addEvents = function(email)
{
	var win = this._panel;
	var This = this;
	this._toBox = this._panel.document.getElementById(ZMTB_MessageComposer.TOFIELD);
	this._ccBox = this._panel.document.getElementById(ZMTB_MessageComposer.CCFIELD);
	this._subBox = this._panel.document.getElementById(ZMTB_MessageComposer.SUBFIELD);
	this._messBox = this._panel.document.getElementById(ZMTB_MessageComposer.MESSFIELD);
	this._browser = this._panel.document.getElementById(ZMTB_MessageComposer.ATTACHBROWSER);
	this._panel.document.addEventListener("keypress", function(e){if(e.keyCode == e.DOM_VK_ESCAPE)win.close()}, false);
	this._browser.addProgressListener(this._pgListener);
	if(email)
	{
		this._toBox.value = email;
		this._subBox.focus();
	}
	else
		this._toBox.focus();
	this._panel.document.getElementById(ZMTB_MessageComposer.FILEINPUT).addEventListener("change", function(e){This.receiveFile(e.target.value); e.target.value=""}, false);
	this._panel.document.getElementById(ZMTB_MessageComposer.CANCELBUTTON).addEventListener("command", function(){win.close()}, false);
	this._panel.document.getElementById(ZMTB_MessageComposer.SENDBUTTON).addEventListener("command", function(){
		if(This._toBox.value != "")
		{
			This.sendMessage();
			win.close()
		}
		else
			This._panel.document.getElementById(ZMTB_MessageComposer.ERRORLABEL).value=this._localStrings..getString("messagecomposer_error_norecpt");
	}, false);	
	this._panel.document.getElementById(ZMTB_MessageComposer.SAVEBUTTON).addEventListener("command", function(){This.saveMessage(); win.close()}, false);
	this._panel.document.getElementById(ZMTB_MessageComposer.ATTACHBOX).addEventListener("dragover", function(e){This._dragover(e)}, false);
	this._panel.document.getElementById(ZMTB_MessageComposer.ATTACHBOX).addEventListener("dragexit", function(e){This._dragexit(e)}, false);
	this._panel.document.getElementById(ZMTB_MessageComposer.ATTACHBOX).addEventListener("dragdrop", function(e){This._dragdrop(e)}, false);
}

ZMTB_MessageComposer.prototype._dragover = function(e)
{
	nsDragAndDrop.dragOver(e, this._dragObserver);
	this._panel.document.getElementById(ZMTB_MessageComposer.ATTACHBOX).className="ZMTB-DragOver";
};

ZMTB_MessageComposer.prototype._dragdrop = function(e)
{
	this._panel.document.getElementById(ZMTB_MessageComposer.ATTACHBOX).className="";
	nsDragAndDrop.drop(e, this._dragObserver);
};

ZMTB_MessageComposer.prototype._dragexit = function(e)
{
	this._panel.document.getElementById(ZMTB_MessageComposer.ATTACHBOX).className="";
};

ZMTB_MessageComposer.prototype.sendMessage = function()
{
	var sd = ZMTB_AjxSoapDoc.create("SendMsgRequest", ZMTB_RequestManager.NS_MAIL);
	var attach = "";
	var soapEmails = [];
	var tos = this._toBox.value.split(",");
	var ccs = this._ccBox.value.split(",");
	for (var i=0; i < tos.length; i++) {
		soapEmails.push({"t":"t", "a":tos[i]});
	};
	for (var i=0; i < ccs.length; i++) {
		soapEmails.push({"t":"c", "a":ccs[i]});	
	};
	
	for (var i=0; i < this._attachments.length; i++) 
	{
		if(!this._attachments[i].send)
			continue;
		if(attach == "")
			attach = this._attachments[i].aid;
		else
			attach += ","+this._attachments[i].aid;
	}
	var mess = {"e":soapEmails, "su":this._subBox.value, "mp":{"ct":"text/plain","content":this._messBox.value}};
	if(attach != "")
		mess["attach"] = {"aid":attach};
	var m = sd.set("m", mess);
	this._rqManager.sendRequest(sd);
	this._attachments = [];
};

ZMTB_MessageComposer.prototype.saveMessage = function()
{
	var sd = ZMTB_AjxSoapDoc.create("SaveDraftRequest", ZMTB_RequestManager.NS_MAIL);
	var attach = "";
	var soapEmails = [];
	var tos = this._toBox.value.split(",");
	var ccs = this._ccBox.value.split(",");
	for (var i=0; i < tos.length; i++) {
		soapEmails.push({"t":"t", "a":tos[i]});
	};
	for (var i=0; i < ccs.length; i++) {
		soapEmails.push({"t":"c", "a":ccs[i]});	
	};
	
	for (var i=0; i < this._attachments.length; i++) 
	{
		if(!this._attachments[i].send)
			continue;
		if(attach == "")
			attach = this._attachments[i].aid;
		else
			attach += ","+this._attachments[i].aid;
	}
	var mess = {"e":soapEmails, "su":this._subBox.value, "mp":{"ct":"text/plain","content":this._messBox.value}};
	if(attach != "")
		mess["attach"] = {"aid":attach};
	var m = sd.set("m", mess);
	this._rqManager.sendRequest(sd);
	this._attachments = [];
};

ZMTB_MessageComposer.prototype.receiveFile = function(file)
{
	this._files = [file];
	this._browser.loadURI("chrome://zimbratb/content/messagecomposer/upload.html");
};

ZMTB_MessageComposer.prototype.receiveFiles = function(files)
{
	this._files = files;
	this._browser.loadURI("chrome://zimbratb/content/messagecomposer/upload.html");
};

ZMTB_MessageComposer.prototype.processStatus = function(status)
{
	Components.utils.reportError(status);
	if(status == 0)
		this._panel.document.getElementById(ZMTB_MessageComposer.ERRORLABEL).value="";
	else if(status == 2152857613)
	{
		this._panel.document.getElementById(ZMTB_MessageComposer.ERRORLABEL).value=this._localStrings.getString("messagecomposer_error_noattach");
		this._panel.document.getElementById(ZMTB_MessageComposer.LOADING).hidden=true;
	}
	else
	{
		this._panel.document.getElementById(ZMTB_MessageComposer.ERRORLABEL).value=this._localStrings.getString("messagecomposer_error_noconnect");
		this._panel.document.getElementById(ZMTB_MessageComposer.LOADING).hidden=true;
	}
}

ZMTB_MessageComposer.prototype.processPage = function(URI, doc)
{
	//Moved upload browser to compose window, hacking
	URI = this._browser.currentURI;
	doc = this._browser.contentDocument;
	//End hack
	
	if(URI.host == "zimbratb" && doc.getElementsByName("fileUpload").length==0)
	{
		var filediv = doc.getElementById("zmfiles");
		var files = this._files;
		if (files.length > 0)
			for (var i = 0; i < files.length; i++)
			{
				var input = doc.createElement('input');
				input.type = 'file';
				input.name = "fileUpload";
				input.value = files[i];
				filediv.appendChild(input);
			}
		doc.getElementById("zmupload").action = this._rqManager.getServerURL()+"service/upload?fmt=raw,extended";
		doc.getElementById("zmupload").submit();
		this._panel.document.getElementById(ZMTB_MessageComposer.ERRORLABEL).value=this._localStrings.getString("messagecomposer_error_uploading");
		this._panel.document.getElementById(ZMTB_MessageComposer.LOADING).hidden=false;
	}
	else if(this._rqManager.getServerURL().indexOf(URI.host) >= 0)
	{
		this._panel.document.getElementById(ZMTB_MessageComposer.ERRORLABEL).value="";
		this._panel.document.getElementById(ZMTB_MessageComposer.LOADING).hidden=true;
		var resp = doc.body.firstChild.data;
		if(resp.indexOf('"aid":"') >=0 && resp.indexOf('"filename":"') >=0)
		{
			var start = resp.indexOf('"aid":"')+7;
			var aid = resp.substr(start, resp.indexOf('"', start)-start);
			start = resp.indexOf('"filename":"')+12;
			var filename = resp.substr(start, resp.indexOf('"', start)-start);
			for (var i=0; i < this._attachments.length; i++)
				if(this._attachments[i].aid==aid)
					return;
			this._attachments.push({aid:aid, name:filename, send:true});
			this._updateAttachments();
		}
		else
			this._panel.document.getElementById(ZMTB_MessageComposer.ERRORLABEL).value=this._localStrings.getString("messagecomposer_error_noattach");
	}
}

ZMTB_MessageComposer.prototype._updateAttachments = function()
{
	var fileDiv = this._panel.document.getElementById(ZMTB_MessageComposer.ATTACHBOX);
	for (var i=fileDiv.childNodes.length-1; i>=0; i--) {
		fileDiv.removeChild(fileDiv.childNodes[i]);
	}
	var hbox = fileDiv.appendChild(this._panel.document.createElement("html:div"));
	hbox.id=ZMTB_MessageComposer.ATTACHBOX+"-inner";
	for (var i=0; i < this._attachments.length; i++)
	{
		var chkbox = hbox.appendChild(this._panel.document.createElement("checkbox"));
		chkbox.style.styleFloat = "left";
		chkbox.label = this._attachments[i].name;
		chkbox.checked = true;
		chkbox.id = this._attachments[i].aid;
		var This=this;
		chkbox.addEventListener("command", function(e){if(e.target.checked)This.getAttachment(e.target.id).send = true; else This.getAttachment(e.target.id).send = false}, false);
	}
	if(this._attachments.length == 0)
	{
		var draghere = fileDiv.appendChild(this._panel.document.createElement("label"))
		draghere.value="Drag attachments here.";
	}
}

ZMTB_MessageComposer.prototype.getAttachment = function(aid)
{
	for (var i=0; i < this._attachments.length; i++) {
		if(this._attachments[i].aid == aid)
			return this._attachments[i];
	};
}

var ZMTB_AttachDragObserver = function(callObj) {this.callObj = callObj};

ZMTB_AttachDragObserver.prototype.getSupportedFlavours = function()
{
    var flavours = new FlavourSet();
    flavours.appendFlavour("application/x-moz-file","nsIFile");
	flavours.appendFlavour("application/x2-moz-file");
	flavours.appendFlavour("text/unicode");
    return flavours;
};

ZMTB_AttachDragObserver.prototype.onDragOver = function(event, flavour, session){};

ZMTB_AttachDragObserver.prototype.onDrop = function(evt, transferData, session) {
	var td = transferData.flavour? transferData: transferData.first.first;
	var files = [];
	if (td.flavour.contentType == "application/x-moz-file")
	{
		if(transferData.dataList && transferData.dataList.length > 1)
		{
			for(var i = 0; i < transferData.dataList.length; i++)
			{
				var td = transferData.dataList[i];
				for(var j = 0; j < td.dataList.length; j++)
				{
					var fd = td.dataList[j];
					if(fd.flavour.contentType == "application/x-moz-file")
					{
						files.push(fd.data.path);
					}
				}
			}
		}
		else
			files.push(td.data.path);
		this.callObj.receiveFiles(files);
	} 
	else if (td.flavour.contentType == "text/unicode")
	{
		if(transferData.dataList && transferData.dataList.length > 1)
		{
			for(var i = 0; i < transferData.dataList.length; i++)
			{
				var td = transferData.dataList[i];
				for(var j = 0; j < td.dataList.length; j++)
				{
					var fd = td.dataList[j];
					if(fd.flavour.contentType == "text/unicode")
					{
						var filePath = fd.data;
						var splitType = "file:///";
						if(filePath.substr(0,8) == "file:///") {
							splitType = "file:///";
						} else if(s.substr(0,7) == "file://") {
							splitType = "file://";	
						}
						var filePaths = filePath.split(splitType);
						files.push(splitType+filePaths[i+1]);
					}
				}
			}
		}
		else
			files.push(td.data);
		this.callObj.receiveFiles(files);
	}
		
};	
const STATE_START = Components.interfaces.nsIWebProgressListener.STATE_START;
const STATE_STOP = Components.interfaces.nsIWebProgressListener.STATE_STOP;
var ZMTB_AttachProgressListener = function(callObj) {this.callObj = callObj};

ZMTB_AttachProgressListener.prototype.QueryInterface = function(aIID)
{
	if (aIID.equals(Components.interfaces.nsIWebProgressListener) || aIID.equals(Components.interfaces.nsISupportsWeakReference) || aIID.equals(Components.interfaces.nsISupports))
		return this;
	throw Components.results.NS_NOINTERFACE;
};

ZMTB_AttachProgressListener.prototype.onStateChange = function(aWebProgress, aRequest, aFlag, aStatus)
{
	if(!(aFlag & STATE_STOP))
		return;
	if(!aWebProgress.isLoadingDocument)
	{
		this.callObj.processStatus(aStatus);
		this.callObj.processPage(aWebProgress.currentURI, aWebProgress.document);
	}
};

ZMTB_AttachProgressListener.prototype.onLocationChange = function(aProgress, aRequest, aURI){};
ZMTB_AttachProgressListener.prototype.onProgressChange = function(aWebProgress, aRequest, curSelf, maxSelf, curTot, maxTot){};
ZMTB_AttachProgressListener.prototype.onStatusChange = function(aWebProgress, aRequest, aStatus, aMessage){};
ZMTB_AttachProgressListener.prototype.onSecurityChange = function(aWebProgress, aRequest, aState){};