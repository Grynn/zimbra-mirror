var ZMTB_MessageComposer = function(requestManager, browser)
{
	this._rqManager = requestManager;
	this._panel = null;
	this._toBox = null;
	this._ccBox = null;
	this._subBox = null;
	this._messBox = null;
	// this._browser = browser;
	this._pgListener = new ZMTB_AttachProgressListener(this);
	this._dragObserver = new ZMTB_AttachDragObserver(this);
	// this._browser.addProgressListener(this._pgListener);
	this._attachments = [];
	this._files = [];
}

ZMTB_MessageComposer.prototype.open = function(email)
{
	if(this._panel && !this._panel.closed)
		this._panel.focus();
	else
	{
		this._attachments = [];
		this._panel = window.open("chrome://zimbratb/content/messagecomposer/compose.xul", "zimbracompose", "chrome,centerscreen,width=450,height=450");
		var This=this;
		this._panel.addEventListener("load", function(){This._addEvents(email)}, false);
	}
}

ZMTB_MessageComposer.prototype._addEvents = function(email)
{
	var win = this._panel;
	var This = this;
	this._toBox = this._panel.document.getElementById("zmc_to");
	this._ccBox = this._panel.document.getElementById("zmc_cc");
	this._subBox = this._panel.document.getElementById("zmc_sub");
	this._messBox = this._panel.document.getElementById("zmc_mess");
	this._browser = this._panel.document.getElementById("ZMTB_AttachBrowser");
	this._browser.addProgressListener(this._pgListener);
	if(email)
	{
		this._toBox.value = email;
		this._subBox.focus();
	}
	else
		this._toBox.focus();
	this._panel.document.getElementById("zmc_fileInput").addEventListener("change", function(e){This.receiveFile(e.target.value); e.target.value=""}, false);
	this._panel.document.getElementById("zmc_close").addEventListener("command", function(){win.close()}, false);
	this._panel.document.getElementById("zmc_send").addEventListener("command", function(){
		if(This._toBox.value != "")
		{
			This.sendMessage();
			win.close()
		}
		else
			This._panel.document.getElementById("zmc_error").value="No recipient."
	}, false);	
	this._panel.document.getElementById("zmc_save").addEventListener("command", function(){This.saveMessage(); win.close()}, false);
	this._panel.document.getElementById("zmc_attach").addEventListener("dragover", function(e){This._dragover(e)}, false);
	this._panel.document.getElementById("zmc_attach").addEventListener("dragexit", function(e){This._dragexit(e)}, false);
	this._panel.document.getElementById("zmc_attach").addEventListener("dragdrop", function(e){This._dragdrop(e)}, false);
	//this._panel.document.getElementById("zmc_attach").ondragdrop = function(e){This._panel.document.getElementById("zmc_attach").style.backgroundColor="red"; Components.utils.reportError("dropped");nsDragAndDrop.drop(e, attachObserver)};
}

ZMTB_MessageComposer.prototype._dragover = function(e)
{
	nsDragAndDrop.dragOver(e, this._dragObserver);
	this._panel.document.getElementById("zmc_attach").className="zmtbhover";
};

ZMTB_MessageComposer.prototype._dragdrop = function(e)
{
	this._panel.document.getElementById("zmc_attach").className="";
	nsDragAndDrop.drop(e, this._dragObserver);
};

ZMTB_MessageComposer.prototype._dragexit = function(e)
{
	this._panel.document.getElementById("zmc_attach").className="";
};

ZMTB_MessageComposer.prototype.sendMessage = function()
{
	var sd = ZMTB_AjxSoapDoc.create("SendMsgRequest", ZMTB_RequestManager.NS_MAIL);
	// var sd = AjxSoapDoc.create("SendMsgRequest", ZMTB_RequestManager.NS_MAIL);
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
	
	// <SendMsgRequest [suid="{send-uid}"] [needCalendarSentByFixup="0|1"] [noSave="0|1"]>
	//   <m [f="!|?"] [origid="..." rt="r|w"] [idnt="{identity-id}"]>
	//     <e t="{type}" a="{email-address}" p="{personal-name}" [add="1"]/>+
	//     <su>{subject}</su>*
	//     [<irt>{Message-ID header for message being replied to}</irt>]
	//     <mp ct="{content-type}" [ci="{content-id}"]>
	//       <content>...</content>
	//     </mp>
	//     <attach [aid="{attach-upload-id}"]>
	//       [<m id="{message-id}"/>]*
	//       [<mp mid="{message-id}" part="{part-id}"/>]*
	//       [<cn id="{contact-id}"/>]*
	//     </attach>
	//   </m>
	// </SendMsgRequest>
};

ZMTB_MessageComposer.prototype.saveMessage = function()
{
	var sd = ZMTB_AjxSoapDoc.create("SaveDraftRequest", ZMTB_RequestManager.NS_MAIL);
	// var sd = AjxSoapDoc.create("SaveDraftRequest", ZMTB_RequestManager.NS_MAIL);
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
	Components.utils.reportError("Status is: "+status);
	if(status == 0)
		this._panel.document.getElementById("zmc_error").value="";
	else
		this._panel.document.getElementById("zmc_error").value="Cannot connect to server.";
}

ZMTB_MessageComposer.prototype.processPage = function(URI, doc)
{
	//Moved upload browser to compose window, needed hack
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
		this._panel.document.getElementById("zmc_error").value="Uploading...";
		this._panel.document.getElementById("zmc_loading").hidden=false;
		
	}
	else if(this._rqManager.getServerURL().indexOf(URI.host) >= 0)
	{
		this._panel.document.getElementById("zmc_error").value="";
		this._panel.document.getElementById("zmc_loading").hidden=true;
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
	}
}

ZMTB_MessageComposer.prototype._updateAttachments = function()
{
	// Components.utils.reportError(doc.getElementsByName("fileUpload").length);
	var fileDiv = this._panel.document.getElementById("zmc_attach");
	for (var i=fileDiv.childNodes.length-1; i>=0; i--) {
		fileDiv.removeChild(fileDiv.childNodes[i]);
	}
	var hbox = fileDiv.appendChild(this._panel.document.createElement("html:div"));
	hbox.id="zmc_attach_inner";
	for (var i=0; i < this._attachments.length; i++)
	{
		var chkbox = hbox.appendChild(this._panel.document.createElement("checkbox"));
		chkbox.style.styleFloat = "left";
		chkbox.label = this._attachments[i].name;
		chkbox.checked = true;
		chkbox.id = this._attachments[i].aid;
		var This=this;
		chkbox.addEventListener("command", function(e){if(e.target.checked)This.getAttachment(e.target.id).send = true; else This.getAttachment(e.target.id).send = false}, false);
		// var label = fileDiv.appendChild(this._panel.document.createElement("label"));
		// label.value = this._attachments[i].name;
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