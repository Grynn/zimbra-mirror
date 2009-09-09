var ZMTB_ContactActions = function(zmtb)
{
	ZMTB_Actions.call(this, zmtb);
	zmtb.getRequestManager().addUpdateListener(this);
	this._folderMan.setFilter("contacts", {type:"contact", exclude:[3], root:true});
	this._folderMan.setFilter("newcontact", {type:"contact", exclude:[3]});
	this._initContext();
	this._initMenu();
	this._initActions();
}

ZMTB_ContactActions.prototype = new ZMTB_Actions();
ZMTB_ContactActions.prototype.constructor = ZMTB_ContactActions;

ZMTB_ContactActions.prototype._initContext = function()
{
	var This=this;
	//Context actions
	document.getElementById("ZMTB-ContextAction-CreateContact").addEventListener("command", function(){This.newContactCommand(); document.getElementById("ZimTB-NewContact-Email").value = document.popupNode.toString().substr(7);}, false);
	document.getElementById("contentAreaContextMenu").addEventListener("popupshowing", function(e){if(gContextMenu.onMailtoLink)document.getElementById("ZMTB-ContextAction-CreateContact").hidden = false;else document.getElementById("ZMTB-ContextAction-CreateContact").hidden = true}, false);
}

ZMTB_ContactActions.prototype._initMenu = function()
{
	var This=this;
	//New Contact
	document.getElementById("ZimTB-NewContact").addEventListener("command",function(event){
		This.newContactCommand();
	},false);
	//New Address Book
	document.getElementById("ZimTB-NewAddressBook").addEventListener("command",function(event){
		This.openActions("ZimTB-NewAddrBook-Bar");
		This._populateList(document.getElementById("ZimTB-NewAddrBook-Parent"), This._folderMan.getFolders("contacts")/*, This._localstrings.getString("contactaction_rootname")*/);
		for (var i=0; i < document.getElementById("ZimTB-NewAddrBook-Parent").itemCount; i++)
			if(document.getElementById("ZimTB-NewAddrBook-Parent").getItemAtIndex(i).getAttribute("value") == 7) //Default contacts folder ID
				document.getElementById("ZimTB-NewAddrBook-Parent").selectedIndex=i;
		document.getElementById("ZimTB-NewAddrBook-Name").value="";
		document.getElementById("ZimTB-NewAddrBook-Name").focus();
	},false);
	//Link to Address Book
	document.getElementById("ZimTB-LinkToAddressBook").addEventListener("command",function(event){
		This.openActions("ZimTB-LinkToAddrBook-Bar");
		This._populateList(document.getElementById("ZimTB-LinkToAddrBook-Parent"), This._folderMan.getFolders("contacts")/*, This._localstrings.getString("contactaction_rootname")*/);
		for (var i=0; i < document.getElementById("ZimTB-LinkToAddrBook-Parent").itemCount; i++)
			if(document.getElementById("ZimTB-LinkToAddrBook-Parent").getItemAtIndex(i).getAttribute("value") == 7) //Default contacts folder ID
				document.getElementById("ZimTB-LinkToAddrBook-Parent").selectedIndex=i;
		document.getElementById("ZimTB-LinkToAddrBook-Name").value="";
		document.getElementById("ZimTB-LinkToAddrBook-Name").focus();
	},false);
	//View Contacts
	document.getElementById("ZimTB-ViewContacts").addEventListener("command",function(event){
		This._rqManager.goToPath("?app=contacts");
	},false);
}

ZMTB_ContactActions.prototype._initActions = function()
{
	var This=this;
	//New Address Book
	document.getElementById("ZimTB-NewAddrBook-Create").addEventListener("command", function(){
		if(document.getElementById("ZimTB-NewAddrBook-Name").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("contactaction_newaddrbook_needname"), null, "failure");
			return;
		}
		This.newFolder(document.getElementById("ZimTB-NewAddrBook-Name").value, "contact", document.getElementById("ZimTB-NewAddrBook-Parent").selectedItem.value);
		This.hideActions();
	}, false);
	document.getElementById("ZimTB-NewAddrBook-Close").addEventListener("command", function(){This.hideActions()}, false);
	
	//Link to Address Book
	document.getElementById("ZimTB-LinkToAddrBook-Create").addEventListener("command", function(){
		if(document.getElementById("ZimTB-LinkToAddrBook-Name").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("contactaction_linkedaddrbook_needname"), null, "failure");
			return;
		}
		else if(document.getElementById("ZimTB-LinkToAddrBook-Owner").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("contactaction_linkedaddrbook_needowner"), null, "failure");
			return;
		}
		else if(document.getElementById("ZimTB-LinkToAddrBook-Path").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("contactaction_linkedaddrbook_needpath"), null, "failure");
			return;
		}
		This.newLinked(document.getElementById("ZimTB-LinkToAddrBook-Name").value, "contact", document.getElementById("ZimTB-LinkToAddrBook-Parent").selectedItem.value, document.getElementById("ZimTB-LinkToAddrBook-Owner").value, document.getElementById("ZimTB-LinkToAddrBook-Path").value)
		This.hideActions();
	}, false);
	document.getElementById("ZimTB-LinkToAddrBook-Close").addEventListener("command", function(){This.hideActions()}, false);
	
	//New Contact
	document.getElementById("ZimTB-NewContact-Create").addEventListener("command", function(){
		var contactObj = {
			lastName:document.getElementById("ZimTB-NewContact-Last").value,
			middleName:document.getElementById("ZimTB-NewContact-Middle").value,
			firstName:document.getElementById("ZimTB-NewContact-First").value,
			company:document.getElementById("ZimTB-NewContact-Company").value,
			jobTitle:document.getElementById("ZimTB-NewContact-JobTitle").value,
			// Email:document.getElementById("ZimTB-NewContact-Email").value,
			Street:document.getElementById("ZimTB-NewContact-Street").value,
			City:document.getElementById("ZimTB-NewContact-City").value,
			State:document.getElementById("ZimTB-NewContact-State").value,
			PostalCode:document.getElementById("ZimTB-NewContact-Zip").value,
			Country:document.getElementById("ZimTB-NewContact-Country").value,
			Phone:document.getElementById("ZimTB-NewContact-Phone").value,
			Fax:document.getElementById("ZimTB-NewContact-Fax").value
		};
		//Zimbra 6 accepts homeEmail or workEmail, zimbra 5 accepts only email
		if(This._zmtb.getRequestManager().getServerVersion().charAt(0) == "6")
			contactObj.Email = document.getElementById("ZimTB-NewContact-Email").value;
		else
			contactObj.email = document.getElementById("ZimTB-NewContact-Email").value;
		var send = false;
		for(var val in contactObj)
			if(contactObj[val] != "")
				send = true;
		if(!send)
		{
			This._zmtb.notify("New Contact needs information.", null, "failure");
			return;
		}
		This.newContact(contactObj, document.getElementById("ZimTB-NewContact-AddressBooks").selectedItem.value,
		document.getElementById("ZimTB-NewContact-Location").selectedItem.value);
		This.hideActions();
	}, false);
	document.getElementById("ZimTB-NewContact-Close").addEventListener("command", function(){This.hideActions()}, false);
}

ZMTB_ContactActions.prototype.enable = function()
{
	document.getElementById("ZimTB-Contacts-Button").disabled = false;
}

ZMTB_ContactActions.prototype.disable = function()
{
	document.getElementById("ZimTB-Contacts-Button").disabled = true;
}

ZMTB_ContactActions.prototype.receiveUpdate = function(responseObj)
{
	if(responseObj.Body.CreateContactResponse && responseObj.Body.CreateContactResponse.cn)
	{
		this._zmtb.notify("Contact created successfully.", null, "success");
	}
	if(responseObj.Body.CreateFolderResponse && responseObj.Body.CreateFolderResponse.folder)
	{
		for (var i=0; i < responseObj.Body.CreateFolderResponse.folder.length; i++)
		{
			if(responseObj.Body.CreateFolderResponse.folder[i].view == "contact")
					this._zmtb.notify(this._localstrings.getString("contactaction_newaddrbook_success"), null, "success");
		};
	}
	if(responseObj.Body.CreateMountpointResponse && responseObj.Body.CreateMountpointResponse.link)
	{
		for (var i=0; i < responseObj.Body.CreateMountpointResponse.link.length; i++)
			if(responseObj.Body.CreateMountpointResponse.link[i].view == "contact")
				this._zmtb.notify(this._localstrings.getString("contactaction_linkedaddrbook_success"), null, "success");
	}
}

ZMTB_ContactActions.prototype.receiveError = function(error)
{
	switch(error.code)
	{
		// switch(responseObj.Body.Fault.Detail.Error.Code)
		// {
		// 	case "mail.INVALID_NAME":
		// 		this.notifyFail("Invalid folder name.");
		// 		break;
		// 	case "mail.INVALID_NAME":
		// 		this.notifyFail("Invalid folder name.");
		// 		break;
		// }
	}
}

ZMTB_ContactActions.prototype.reset = function(){}


ZMTB_ContactActions.prototype.newContactCommand = function()
{
	var tboxes = document.getElementById("ZimTB-NewContact-Bar").getElementsByTagName("textbox");
	for (var i=0; i < tboxes.length; i++)
		tboxes[i].value="";
	var selectedText = window.content.document.getSelection().toString();
	var email = selectedText.match(/((([!#$%&'*+\-\/=?^_`{|}~\w])|([!#$%&'*+\-\/=?^_`{|}~\w][!#$%&'*+\-\/=?^_`{|}~\.\w]{0,}[!#$%&'*+\-\/=?^_`{|}~\w]))[@]\w+([-.]\w+)*\.\w+([-.]\w+)*)/);
	if(email)
		document.getElementById("ZimTB-NewContact-Email").value = email[0];
	var phone = selectedText.match(/(\d[ -\.]?)?(\d{3}[ -\.]?)?\d{3}[ -\.]?\d{4}(x\d+)?/);
	if(phone)
		document.getElementById("ZimTB-NewContact-Phone").value = phone[0];
	this.openActions("ZimTB-NewContact-Bar");
	this._populateList(document.getElementById("ZimTB-NewContact-AddressBooks"), this._folderMan.getFolders("newcontact"));
	//Set default folder
	for (var i=0; i < document.getElementById("ZimTB-NewContact-AddressBooks").itemCount; i++)
		if(document.getElementById("ZimTB-NewContact-AddressBooks").getItemAtIndex(i).getAttribute("value") == 7) //Default contacts folder ID
			document.getElementById("ZimTB-NewContact-AddressBooks").selectedIndex=i;
	document.getElementById("ZimTB-NewContact-Last").value = "";
	document.getElementById("ZimTB-NewContact-Middle").value = "";
	document.getElementById("ZimTB-NewContact-First").value = "";
	document.getElementById("ZimTB-NewContact-Company").value = "";
	document.getElementById("ZimTB-NewContact-JobTitle").value = "";
	document.getElementById("ZimTB-NewContact-Email").value = "";
	document.getElementById("ZimTB-NewContact-Street").value = "";
	document.getElementById("ZimTB-NewContact-City").value = "";
	document.getElementById("ZimTB-NewContact-Zip").value = "";
	document.getElementById("ZimTB-NewContact-Country").value = "";
	document.getElementById("ZimTB-NewContact-Phone").value = "";
	document.getElementById("ZimTB-NewContact-Fax").value = "";
	document.getElementById("ZimTB-NewContact-Last").focus();
}

ZMTB_ContactActions.prototype.newContact = function(cObj, folderId, location)
{
	var sd = ZMTB_AjxSoapDoc.create("CreateContactRequest", ZMTB_RequestManager.NS_MAIL);
	var as = [];
	var i=0;
	var cn = sd.set("cn", {"l":folderId})
	for(var a in cObj)
	{
		var n=a;
		if(cObj[a] == "")
			continue;
		//If the property name begins with an uppercase letter, it needs a location prefix [work|home]
		if(a.match(/^[A-Z]+\S*/g))
			n=location+a;
		var el = sd.set("a", cObj[a], cn)
		el.setAttribute("n", n);
		i++;
	}
	this._rqManager.sendRequest(sd);
	this._rqManager.updateAll();
}

ZMTB_ContactActions.prototype.newAddressBookCommand = function()
{
	this.openActions("ZimTB-NewAddrBook-Bar");
	this._populateList(document.getElementById("ZimTB-NewAddrBook-Parent"), this._addrBooks);
	
}

ZMTB_ContactActions.prototype.linkToAddressBookCommand = function()
{
	this.openActions("ZimTB-LinkToAddrBook-Bar");
	this._populateList(document.getElementById("ZimTB-LinkToAddrBook-Parent"), this._addrBooks);
}

ZMTB_ContactActions.prototype.newLinkedAddrBook = function(name, parentId, owner, path)
{
	this.newLinked(name, "contact", parentId, owner, path);
}