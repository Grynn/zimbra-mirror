/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
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

/**
 * Helps organize contacts by allowing action on multiple contacts
 * 
 * @author RAJA RAO DV (rrao@zimbra.com)
 */
function Com_Zimbra_contactOrganizer() {
}

Com_Zimbra_contactOrganizer.prototype = new ZmZimletBase();
Com_Zimbra_contactOrganizer.prototype.constructor = Com_Zimbra_contactOrganizer;

// Consts
Com_Zimbra_contactOrganizer.AddressBookOnlyMsg = "You can use this Zimlet from 'within Address Book' only";
Com_Zimbra_contactOrganizer.prototype.BEGIN_AT = 0;
Com_Zimbra_contactOrganizer.prototype.END_AT = 49;
Com_Zimbra_contactOrganizer.prototype.PROCESS_AT_ONCE = 50;


Com_Zimbra_contactOrganizer.prototype.singleClicked =
function() {
	if (appCtxt.getAppController().getActiveApp() != "Contacts") {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg(Com_Zimbra_contactOrganizer.AddressBookOnlyMsg, ZmStatusView.LEVEL_CRITICAL, null, transitions);
		return;
	}

	//if previous process is still running in background, show that dlg
	if (this.pbDialog) {
		if (this.pbDialog.runInBackground) {
			this.pbDialog.popup();
			this.pbDialog.runInBackground = false;
			return;
		}
	}
	this._initializeEmptyDlg();
	if (this.conOrgDialog) {
		this._contactList = AjxDispatcher.run("GetContacts");
		this.setProgressbarBegin();
		this.cleanUpMenus();
		this.setTagsList();
		return;
	}
	this.addedNewTag = false;
	this._parentView.getHtmlElement().innerHTML = this.constructContactManagerView();
	this.setTagsList();
	this._initializeProgressbarDlg();
	this.pbView.getHtmlElement().innerHTML = this.constructContactProgressbarView();

};

Com_Zimbra_contactOrganizer.prototype._initializeEmptyDlg =
function() {
	this._parentView = new DwtComposite(this.getShell());
	this._parentView.setSize("500", "240");
	this._parentView.getHtmlElement().style.overflow = "auto";
};

Com_Zimbra_contactOrganizer.prototype._initializeProgressbarDlg =
function() {
	this.pbView = new DwtComposite(this.getShell());
	this.pbView.setSize("500", "80");
	this.pbView.getHtmlElement().style.overflow = "auto";
	this.pbDialog = this._createDialog({title:"Processing Contacts...", view:this.pbView});

	this.abortBtnId = "corg_pbAbortBtn";
	var abortButton = new DwtDialog_ButtonDescriptor(this.abortBtnId, "Abort", DwtDialog.ALIGN_RIGHT);
	this.pbBackgroundBtnId = "corg_pbrunBackgroundBtn";
	var pbBackgroundButton = new DwtDialog_ButtonDescriptor(this.pbBackgroundBtnId, "Run In Background", DwtDialog.ALIGN_RIGHT);
	this.pbDialog = this._createDialog({title:"Processing Contacts...", view:this.pbView, standardButtons:[DwtDialog.OK_BUTTON],extraButtons:[pbBackgroundButton, abortButton]});
	this.pbDialog.setButtonListener(this.abortBtnId, new AjxListener(this, this.pbDialogAbortListner));
	this.pbDialog.setButtonListener(this.pbBackgroundBtnId, new AjxListener(this, this.pbRunBackgroundAbortListner));
	this.pblen = "495";

	this.pbDialog.runInBackground = false;
};

Com_Zimbra_contactOrganizer.prototype.getProgressbarLen =
function() {
	return this.pblen;
};

Com_Zimbra_contactOrganizer.prototype.addContactManagerButtons =
function() {
	if (this.conOrgDialog)
		return;

	this._ProcessButtonId = Dwt.getNextId();
	var processButton = new DwtDialog_ButtonDescriptor(this._ProcessButtonId, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Organize&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", DwtDialog.ALIGN_RIGHT);
	this.conOrgDialog = this._createDialog({title:"Contact Organizer", view:this._parentView, standardButtons : [ DwtDialog.CANCEL_BUTTON],extraButtons:[processButton]});
	this.conOrgDialog.setButtonListener(this._ProcessButtonId, new AjxListener(this, this.organizerProcessBtnListner));
	this._contactList = AjxDispatcher.run("GetContacts");


	var contactsFileAs = [new DwtSelectOption("LcommaFC", true, "Last, First (Company)"),
		new DwtSelectOption("CLcommaF", false, "(Company) Last, First"), new DwtSelectOption("FLC", false, "First Last (Company)"),
		new DwtSelectOption("CFL", false, "(Company) First Last"), new DwtSelectOption("LcommaF", false, "Last, First"),
		new DwtSelectOption("FL", false, "First Last"), new DwtSelectOption("Comp", false, "Company")];
	this.contactsFileAsSelectDWT = new DwtSelect(this._parentView, contactsFileAs);
	var filecontactAsCell = document.getElementById("corg_filecontactAsDWT");
	if (filecontactAsCell) {
		filecontactAsCell.appendChild(this.contactsFileAsSelectDWT.getHtmlElement());
	}

};

Com_Zimbra_contactOrganizer.prototype.organizerProcessBtnListner =
function() {
	var parsingOK = this.parseSelection();
	if (!parsingOK)
		return;

	this.__oldNumContacts = 0;
	this._noOpLoopCnt = 0;
	this._totalWaitCnt = 0;
	this._contactsAreLoaded = false;
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
	appCtxt.getAppController().setStatusMsg("Please wait, scanning contacts(it might take couple of minutes)..", ZmStatusView.LEVEL_INFO, null, transitions);
	this._waitForContactToLoadAndProcess();
	this._contactsAreLoaded = true;

};

Com_Zimbra_contactOrganizer.prototype._waitForContactToLoadAndProcess = function() {
	this._contactList = AjxDispatcher.run("GetContacts");
	if (!this._contactList)
		return;

	this.__currNumContacts = this._contactList.getArray().length;
	if (this._totalWaitCnt < 2 || this._noOpLoopCnt < 3) {//minimum 2 cycles post currentCnt==oldCnt
		if (this.__oldNumContacts == this.__currNumContact) {
			this._noOpLoopCnt++;
		}
		this._totalWaitCnt++;
		this.__oldNumContacts = this.__currNumContact;
		setTimeout(AjxCallback.simpleClosure(this._waitForContactToLoadAndProcess, this), 3000);
	} else {
		this._startProcessing();//start processing
	}
};

Com_Zimbra_contactOrganizer.prototype._startProcessing =
function(ev) {
	this.filterContacts();//filter contacts based on the selection

	this.startAt = this.BEGIN_AT;
	this.endAt = this.END_AT;
	this.pbView._initialized = false;
	this.pbDialog.getButton("corg_pbAbortBtn").setEnabled(true);
	if (this.corg_fileContactsAsRadio) {
		this.conOrgDialogDoFileAsListener(ev);
	} else if (this.corg_fileContactsIntoRadio) {
		this._moveAllToAnotherFldrListener(ev);
	} else if (this.corg_tagContactsRadio) {
		this._applyTagToContactsListener(ev);
	}
};

Com_Zimbra_contactOrganizer.prototype.constructContactProgressbarView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<TABLE width=100%>";
	html[i++] = "<TR><TD width=\"1%\"><DIV id=\"corg_processBusy\" class=\"shown\"><img   src=\"" + this.getResource("co-loader.gif") + "\"  /></DIV></TD>";
	html[i++] = "<TD ALIGN=LEFT><FONT size=2><DIV id=\"corg_pbMsgDiv\"></DIV><FONT></TD></TR></TABLE>";
	html[i++] = "<DIV class = \"corg_processbarBackground\"></DIV>";
	html[i++] = "<DIV class = \"corg_processbarForeground\" id=\"corg_processBarForeground\"></DIV>";
	return html.join("");
};

Com_Zimbra_contactOrganizer.prototype._createContactsFolderList =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<select id=\"corg_selectedfolders\" multiple='' size=3>";
	for (var el in this.folderNamesIdsArry) {
		html[i++] = "<option value=" + this.folderNamesIdsArry[el] + ">" + el + "</option>";
	}
	html[i++] = "<option value=" + ZmFolder.ID_TRASH + ">Trash</option>";

	html[i++] = "</select>";
	document.getElementById("corg_selectedfoldersTD").innerHTML = html.join("");
};

Com_Zimbra_contactOrganizer.prototype._createContactsChoiceList =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<select id=\"corg_contactsChoice\" onChange=\"Com_Zimbra_contactOrganizer._displayHideContainsField()\">";
	html[i++] = "<option value='allcontacts'>All Contacts</option>";
	html[i++] = "<option value='contacts_email_contains'>Contact's email contains</option>";
	html[i++] = "<option value='contacts_with_phonenumber'>Contacts with phone number</option>";
	html[i++] = "</select>";
	document.getElementById("corg_contactsChoiceTD").innerHTML = html.join("");
};

Com_Zimbra_contactOrganizer._displayHideContainsField =
function() {
	var w = document.getElementById("corg_contactsChoice").value;
	if (w == "contacts_email_contains") {
		document.getElementById("corg_contactPropInput").style.display = "block";
	} else {
		document.getElementById("corg_contactPropInput").style.display = "none";
	}
};

Com_Zimbra_contactOrganizer.prototype.constructContactManagerView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<div class='corg_hdrDiv'>STEP1: Select contacts:</div>";
	html[i++] = "<div class='corg_sectionDiv'>";
	html[i++] = "<table cellpadding=5>";
	html[i++] = "<TR>";
	html[i++] = "<TD valign='top' id='corg_contactsChoiceTD'></TD>";
	html[i++] = "<TD valign='top' ><Input id =\"corg_contactPropInput\" style=\"width:100px; display:none;\" type='text'></input></td>";
	html[i++] = "<TD valign='top'> in folder(s):</td>";
	html[i++] = "<TD valign='top' id='corg_selectedfoldersTD'></TD>";
	html[i++] = "</TR>";
	html[i++] = "</table>";
	html[i++] = "</div>";
	html[i++] = "<BR>";
	html[i++] = "<div class='corg_hdrDiv'>STEP2: Select one of the actions to perform:</div>";
	html[i++] = "<div class='corg_sectionDiv'>";
	html[i++] = "<TABLE  cellpadding=5>";
	html[i++] = "<TR>";
	html[i++] = "<TD ><input type=\"radio\"id=\"corg_fileContactsAsRadio\" name=\"action\"/>File the contacts as:</TD>";
	html[i++] = "<TD colspan=5 id = \"corg_filecontactAsDWT\"> </TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</div>";

	html[i++] = "<div class='corg_sectionDiv'>";
	html[i++] = "<TABLE  cellpadding=5>";
	html[i++] = "<TR >";
	html[i++] = "<TD ><input type=\"radio\" id=\"corg_fileContactsIntoRadio\" name=\"action\"/>Move contacts to:</TD>";
	html[i++] = "<TD colspan=5 id = \"corg_filecontactIntoDWT\" width=50%></TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</div>";

	html[i++] = "<div class='corg_sectionDiv'>";
	html[i++] = "<TABLE  cellpadding=5>";
	html[i++] = "<TR >";
	html[i++] = "<TD ><input type=\"radio\" id=\"corg_tagContactsRadio\" name=\"action\"/></TD>";
	html[i++] = "<TD  id = \"corg_applyRemoveTagDWT\"></TD>";
	html[i++] = "<TD>Tag:</TD>";
	html[i++] = "<TD colspan=2 id = \"corg_applyTagDWT\"></TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</div>";

	return html.join("");
};

Com_Zimbra_contactOrganizer.prototype.setProgressbarMsg =
function(msg) {
	document.getElementById("corg_pbMsgDiv").innerHTML = msg;
};

Com_Zimbra_contactOrganizer.prototype.parseSelection =
function() {
	this.corg_contactsChoice = document.getElementById("corg_contactsChoice").value;
	this.corg_contactPropInput = document.getElementById("corg_contactPropInput").value;
	if (this.corg_contactsChoice == "contacts_email_contains" && this.corg_contactPropInput == "") {
		return this.returnFailureMsg("Please enter email or domain information");
	}
	this.corg_selectedfolders = this._getSelectedFolders();
	if (this.corg_selectedfolders.length == 0) {
		return this.returnFailureMsg("Please Select an Address Book folder");
	}

	this.corg_fileContactsAsRadio = document.getElementById("corg_fileContactsAsRadio").checked;
	this.corg_fileContactsIntoRadio = document.getElementById("corg_fileContactsIntoRadio").checked;
	this.corg_tagContactsRadio = document.getElementById("corg_tagContactsRadio").checked;
	if (!(this.corg_fileContactsAsRadio || this.corg_fileContactsIntoRadio || this.corg_tagContactsRadio)) {
		return this.returnFailureMsg("Please Select an action to perform");
	}
	return true;
};

Com_Zimbra_contactOrganizer.prototype.returnFailureMsg =
function(msg) {
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
	appCtxt.getAppController().setStatusMsg(msg, ZmStatusView.LEVEL_CRITICAL, null, transitions);
	return false;
};

Com_Zimbra_contactOrganizer.prototype._getSelectedFolders =
function() {
	var me = document.getElementById("corg_selectedfolders");
	var selectedFolders = [];
	for (var i = 0; i < me.options.length; i++) {
		if (me.options[i].selected) {
			selectedFolders[me.options[i].value] = true;
		}
	}
	return selectedFolders;
};

Com_Zimbra_contactOrganizer.prototype._parseFileAsSelect =
function() {
	this.fileAsHash = "1";
	if (!this._contactList) return;

	var selectChoice = this.contactsFileAsSelectDWT.getValue();
	switch (selectChoice) {
		case "LcommaFC":
			this.fileAsHash = ZmContact.FA_LAST_C_FIRST_COMPANY;
			break;
		case "CLcommaF":
			this.fileAsHash = ZmContact.FA_COMPANY_LAST_C_FIRST;
			break;
		case "FLC":
			this.fileAsHash = ZmContact.FA_FIRST_LAST_COMPANY;
			break;
		case "CFL":
			this.fileAsHash = ZmContact.FA_COMPANY_FIRST_LAST;
			break;
		case "LcommaF":
			this.fileAsHash = ZmContact.FA_LAST_C_FIRST;
			break;
		case "FL":
			this.fileAsHash = ZmContact.FA_FIRST_LAST;
			break;
		case "Comp":
			this.fileAsHash = ZmContact.FA_COMPANY;
			break;
	}
};

Com_Zimbra_contactOrganizer.prototype.filterContacts =
function() {
	if (!this._contactList)
		return;

	this.filteredContactsArry = new Array();
	var _tmpArry = this._contactList.getArray();
	for (var j = 0; j < _tmpArry.length; j++) {
		var currentContact = _tmpArry[j];
		var currentContactFoldrId = currentContact.folderId ? currentContact.folderId : currentContact.l;
		if (!this.corg_selectedfolders[currentContactFoldrId]) {
			continue;
		}
		if (this.corg_fileContactsIntoRadio && (currentContactFoldrId == this.fileIntoSelectDWT.getValue())) {
			continue;
		}

		var attr = currentContact.attr ? currentContact.attr : currentContact._attrs;
		if (this.corg_contactsChoice == "contacts_email_contains") {
			if (attr.email) {
				if ((attr.email.toLowerCase()).indexOf(this.corg_contactPropInput.toLowerCase()) == -1) {
					continue;
				}
			} else {
				continue;
			}
		}

		if (this.corg_contactsChoice == "contacts_with_phonenumber") {
			if (!attr.assistantPhone && !attr.companyPhone && !attr.homePhone && !attr.homePhone2
					&& !attr.mobilePhone && !attr.workPhone && !attr.workPhone2) {
				continue;
			}
		}
		this.filteredContactsArry.push(currentContact);
	}
};

Com_Zimbra_contactOrganizer.prototype.setFolderList =
function() {
	var folders = appCtxt.getFolderTree().asList()[0].children.getArray();
	this.folderNamesIdsArry = [];
	var fileInto = [];
	//var folderFrom = [];
	//var folderTo = [];
	for (var i = 0; i < folders.length; i++) {
		if (folders[i].type == "ADDRBOOK") {
			this.folderNamesIdsArry[folders[i].name] = folders[i].id;
			fileInto.push(new DwtSelectOption(folders[i].id, false, folders[i].name, null, null, folders[i].getIcon()));
		}
	}
	fileInto.push(new DwtSelectOption(ZmFolder.ID_TRASH, false, "Trash", null, null, "Trash"));
	this.fileIntoSelectDWT = new DwtSelect(this._parentView, fileInto);
	document.getElementById("corg_filecontactIntoDWT").appendChild(this.fileIntoSelectDWT.getHtmlElement());
	this.addContactManagerButtons();
	this._createContactsFolderList();
	this._createContactsChoiceList();
	this.conOrgDialog.popup();
};

Com_Zimbra_contactOrganizer.prototype._handleResponseFolderList =
function(callback, result) {
	var resp = result.getResponse().GetFolderResponse;
	this.folderNamesIdsArry = [];
	var fileInto = [];
	var folders = resp.folder[0].folder;
	for (var i = 0; i < folders.length; i++) {
		this.folderNamesIdsArry[folders[i].name] = folders[i].id;
		if (folders[i].type == "ADDRBOOK") {
			//ImgContactsFolder
			fileInto.push(new DwtSelectOption(folders[i].id, false, folders[i].name, null, null, folders[i].getIcon));
		}
	}
	fileInto.push(new DwtSelectOption(ZmFolder.ID_TRASH, false, "Trash", null, null, "Trash"));
	this.fileIntoSelectDWT = new DwtSelect(this._parentView, fileInto);
	document.getElementById("corg_filecontactIntoDWT").appendChild(this.fileIntoSelectDWT.getHtmlElement());
	this.addContactManagerButtons();
	this.conOrgDialog.popup();
};


Com_Zimbra_contactOrganizer.prototype.cleanUpMenus =
function() {
	//delete all the folders and re-add them, just to get any new folders
	if (this.conOrgDialog) {
		delete this.foldersFromSelectDWT;
		delete this.fileIntoSelectDWT;
		delete this.foldersToSelectDWT;
		delete this.applyTagSelectDWT;
		delete this.applyRemoveSelectTagDWT;
		document.getElementById("corg_filecontactIntoDWT").innerHTML = "";
		document.getElementById("corg_applyTagDWT").innerHTML = "";
		document.getElementById("corg_applyRemoveTagDWT").innerHTML = "";
	}
};

Com_Zimbra_contactOrganizer.prototype.setTagsList =
function() {
	var soapDoc = AjxSoapDoc.create("GetTagRequest", "urn:zimbraMail");
	var method = soapDoc.getMethod();
	method.setAttribute("visible", "1");

	var params = {
		soapDoc: soapDoc,
		asyncMode: true,
		callback: new AjxCallback(this, this._handleResponseTagList, null)
	};
	appCtxt.getRequestMgr().sendRequest(params);
};

Com_Zimbra_contactOrganizer.prototype._handleResponseTagList =
function(callback, result) {
	var resp = result.getResponse().GetTagResponse;
	this.tagNamesIdsArry = [];
	var tagsMenuItems = [];
	var tags = resp.tag;
	var disableTagsMenu;
	if (this.addedNewTag) {
		delete this.applyTagSelectDWT;
		document.getElementById("corg_filecontactIntoDWT").innerHTML = "";
	}
	if (tags) {
		for (var i = 0; i < tags.length; i++) {
			this.tagNamesIdsArry[tags[i].name] = tags[i].id;

			tagsMenuItems.push(new DwtSelectOption(tags[i].id, false, tags[i].name, null, null, this.getTagImageName(tags[i].color)));
		}
	} else {
		tagsMenuItems.push(new DwtSelectOption("noTagsSelectId", false, "No Tags"));
		disableTagsMenu = true;
	}
	this.applyTagSelectDWT = new DwtSelect(this._parentView, tagsMenuItems);
	document.getElementById("corg_applyTagDWT").appendChild(this.applyTagSelectDWT.getHtmlElement());

	this.applyRemoveSelectTagDWT = new DwtSelect(this._parentView, [new DwtSelectOption("applyTheTag", false, "Apply"),
		new DwtSelectOption("removeTheTag", false, "Remove")]);
	document.getElementById("corg_applyRemoveTagDWT").appendChild(this.applyRemoveSelectTagDWT.getHtmlElement());

	if (disableTagsMenu)
		this.applyTagSelectDWT.disable();

	//reset tag dialog's listner
	if (this.addedNewTag) {
		this._tagdlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okButtonListener));
		this.addedNewTag = false;
	}

	this.setFolderList();

};

Com_Zimbra_contactOrganizer.prototype.getTagImageName =
function(imageNumber) {
	switch (imageNumber) {
		case 1:
			return ZmTag.COLOR_ICON[ZmOrganizer.C_BLUE];
		case 2:
			return ZmTag.COLOR_ICON[ZmOrganizer.C_CYAN];
			break;
		case 3:
			return ZmTag.COLOR_ICON[ZmOrganizer.C_GREEN];
			break;
		case 4:
			return ZmTag.COLOR_ICON[ZmOrganizer.C_PURPLE];
			break;
		case 5:
			return ZmTag.COLOR_ICON[ZmOrganizer.C_RED];
			break;
		case 6:
			return ZmTag.COLOR_ICON[ZmOrganizer.C_YELLOW];
			break;
		case 7:
			return ZmTag.COLOR_ICON[ZmOrganizer.C_PINK];
			break;
		case 8:
			return ZmTag.COLOR_ICON[ZmOrganizer.C_GRAY];
			break;
		case 9:
		case 0:
			return ZmTag.COLOR_ICON[ZmOrganizer.C_ORANGE];
			break;
	}

};

Com_Zimbra_contactOrganizer.prototype._moveAllToAnotherFldrListener =
function() {
	if (this.filteredContactsArry.length == 0) {
		appCtxt.getAppController().setStatusMsg("No Contacts found or matched your  filter criteria", ZmStatusView.LEVEL_CRITICAL);
		return;
	}

	var _contactObjArry = this.filteredContactsArry;
	this.totalContactsCount = _contactObjArry.length;
	if (this.endAt > _contactObjArry.length) {
		this.endAt = _contactObjArry.length - 1;
	}

	var toFldrId;
	if (this.corg_fileContactsIntoRadio) {
		this._moveToAB = this.fileIntoSelectDWT.getValue();
		toFldrId = this._moveToAB;
	}
	var soapDoc = null;
	if (!soapDoc) {
		soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
		soapDoc.setMethodAttribute("onerror", "continue");
	}
	var contactActionReq = soapDoc.set("ContactActionRequest", null, null, "urn:zimbraMail");
	var action = soapDoc.set("action", null, null, "urn:zimbraMail");
	action.setAttribute("op", "move");

	//collect ids of duplicate contacts that will be deleted.
	var idstr = "";
	for (var j = this.startAt; j <= this.endAt; j++) {
		var ct = _contactObjArry[j];
		if (j == this.startAt) {
			idstr = ct.id;
		} else {
			idstr = idstr + "," + ct.id;
		}
	}
	action.setAttribute("id", idstr);
	action.setAttribute("l", toFldrId);

	contactActionReq.appendChild(action);
	if (soapDoc) {
		// finally, send the BatchRequest to the server
		var respCallback = new AjxCallback(this, this._handleResponseMoveAll);
		appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:respCallback});
	}
	var msg = "Processing contacts " + this.startAt + " to " + this.endAt + " out of " + this.totalContactsCount;
	this.setProgressbarMsg(msg);
	this.conOrgDialog.popdown();
	if (!this.pbDialog.runInBackground)
		this.pbDialog.popup();

};

Com_Zimbra_contactOrganizer.prototype._applyTagToContactsListener =
function() {
	if (this.filteredContactsArry.length == 0) {
		appCtxt.getAppController().setStatusMsg("No Contacts found or matched your  filter criteria", ZmStatusView.LEVEL_CRITICAL);
		return;
	}
	var _contactObjArry = this.filteredContactsArry;
	this.totalContactsCount = _contactObjArry.length;
	if (this.endAt > _contactObjArry.length) {
		this.endAt = _contactObjArry.length - 1;
	}
	var applyTagId = this.applyTagSelectDWT.getValue();
	var applyOrRemoveTag = this.applyRemoveSelectTagDWT.getValue();

	var soapDoc = null;
	if (!soapDoc) {
		soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
		soapDoc.setMethodAttribute("onerror", "continue");
	}
	var contactActionReq = soapDoc.set("ContactActionRequest", null, null, "urn:zimbraMail");
	var action = soapDoc.set("action");
	if (applyOrRemoveTag == "applyTheTag") {
		action.setAttribute("op", "tag");
	} else if (applyOrRemoveTag == "removeTheTag") {
		action.setAttribute("op", "!tag");
	}

	//collect ids of duplicate contacts that will be deleted.
	var idstr = "";
	for (var j = this.startAt; j <= this.endAt; j++) {
		var ct = _contactObjArry[j];
		if (j == this.startAt) {
			idstr = ct.id;
		} else {
			idstr = idstr + "," + ct.id;
		}
	}

	action.setAttribute("id", idstr);
	action.setAttribute("tag", applyTagId);
	contactActionReq.appendChild(action);

	if (soapDoc) {
		// finally, send the BatchRequest to the server
		var respCallback = new AjxCallback(this, this._handleResponseApplyTag);
		appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:respCallback});
	}
	var msg = "Processing contacts " + this.startAt + " to " + this.endAt + " out of " + this.totalContactsCount;
	this.setProgressbarMsg(msg);
	this.conOrgDialog.popdown();
	if (!this.pbDialog.runInBackground)
		this.pbDialog.popup();

};

Com_Zimbra_contactOrganizer.prototype.conOrgDialogDoFileAsListener =
function() {
	if (this.filteredContactsArry.length == 0) {
		appCtxt.getAppController().setStatusMsg("No Contacts found or matched your  filter criteria", ZmStatusView.LEVEL_CRITICAL);
		return;
	}
	this._parseFileAsSelect();
	var soapDoc = null;
	var _contactObjArry = this.filteredContactsArry;
	this.totalContactsCount = _contactObjArry.length;
	if (this.endAt > _contactObjArry.length) {
		this.endAt = _contactObjArry.length - 1;
	}
	for (var i = this.startAt; i <= this.endAt; i++) {
		if (!soapDoc) {
			soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
			soapDoc.setMethodAttribute("onerror", "continue");
		}

		var modifyContactReq = soapDoc.set("ModifyContactRequest", null, null, "urn:zimbraMail");
		modifyContactReq.setAttribute("replace", "0");
		modifyContactReq.setAttribute("force", "1");
		var doc = soapDoc.getDoc();
		var cn = doc.createElement("cn");
		cn.setAttribute("id", _contactObjArry[i].id);
		modifyContactReq.appendChild(cn);
		var fn = doc.createElement("a");
		fn.setAttribute("n", "fileAs");
		fn.appendChild(doc.createTextNode(this.fileAsHash));
		cn.appendChild(fn);
	}

	if (soapDoc) {
		// finally, send the BatchRequest to the server
		var respCallback = new AjxCallback(this, this._handleResponseFileAs);
		appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:respCallback});
	}
	var msg = "Processing contacts " + this.startAt + " to " + this.endAt + " out of " + this.totalContactsCount ;
	this.setProgressbarMsg(msg);
	this.conOrgDialog.popdown();
	if (!this.pbDialog.runInBackground)
		this.pbDialog.popup();

};

// Callbacks.....
Com_Zimbra_contactOrganizer.prototype._handleResponseFileAs =
function() {
	var msg = "";
	if (this.abortProcess) {
		msg = "Process was aborted! " + this.endAt + " out of " + this.totalContactsCount + "contacts were processed! Please log out and log back in.";
		this.setProgressbarAbortMsg(msg);
	} else if (this.endAt != this.totalContactsCount - 1) {
		document.getElementById("corg_processBarForeground").style.width = parseInt(this.getProgressbarLen() * (this.endAt / this.totalContactsCount)) + "px";
		this.startAt = this.endAt + 1;
		this.endAt = this.endAt + this.PROCESS_AT_ONCE;
		this.conOrgDialogDoFileAsListener();
	} else {
		msg = "Successfully completed processing " + this.totalContactsCount + " contacts! Please log out and log back in.";
		this.setProgressbarComplete(msg);
	}
};

Com_Zimbra_contactOrganizer.prototype._handleResponseMoveAll =
function() {
	var msg = "";
	if (this.abortProcess) {
		msg = "Process was aborted! " + this.endAt + " out of " + this.totalContactsCount + "contacts were processed! Please log out and log back in.";
		this.setProgressbarAbortMsg(msg);
	} else if ((this.endAt != this.totalContactsCount - 1) && (!this.abortProcess)) {
		document.getElementById("corg_processBarForeground").style.width = parseInt(this.getProgressbarLen() * (this.endAt / this.totalContactsCount)) + "px";
		this.startAt = this.endAt + 1;
		this.endAt = this.endAt + this.PROCESS_AT_ONCE;
		this._moveAllToAnotherFldrListener();
	} else {
		msg = "Successfully completed processing " + this.totalContactsCount + " contacts! Please log out and log back in.";
		this.setProgressbarComplete(msg);
	}
};

Com_Zimbra_contactOrganizer.prototype._handleResponseApplyTag =
function() {
	var msg = "";
	if (this.abortProcess) {
		msg = "Process was aborted! " + this.endAt + " out of " + this.totalContactsCount + "contacts were processed! Please log out and log back in.";
		this.setProgressbarAbortMsg(msg);
	} else if (this.endAt != this.totalContactsCount - 1) {
		document.getElementById("corg_processBarForeground").style.width = parseInt(this.getProgressbarLen() * (this.endAt / this.totalContactsCount)) + "px";
		this.startAt = this.endAt + 1;
		this.endAt = this.endAt + this.PROCESS_AT_ONCE;
		this._applyTagToContactsListener();
	} else {
		msg = "Successfully completed processing " + this.totalContactsCount + " contacts! Please log out and log back in.";
		this.setProgressbarComplete(msg);
	}
};

//Progressbar related listners...
Com_Zimbra_contactOrganizer.prototype.setProgressbarComplete =
function(msg) {
	this.pbDialog.getButton("corg_pbAbortBtn").setEnabled(false);//disable Abort btn
	this.pbDialog.getButton("corg_pbrunBackgroundBtn").setEnabled(false); //disable runInBg btn
	document.getElementById("corg_processBarForeground").style.width = this.getProgressbarLen() + "px";
	document.getElementById("corg_processBusy").className = "corg_texthidden";
	document.getElementById("corg_pbMsgDiv").innerHTML = this.formatMsg(msg);
	this.pbDialog.popup();
	this.pbDialog.runInBackground = false;
};

Com_Zimbra_contactOrganizer.prototype.setProgressbarAbortMsg =
function(msg) {
	document.getElementById("corg_processBusy").className = "corg_texthidden";
	document.getElementById("corg_pbMsgDiv").innerHTML = this.formatMsg(msg);
};

Com_Zimbra_contactOrganizer.prototype.setProgressbarBegin =
function() {
	document.getElementById("corg_processBarForeground").style.width = "0px";
	document.getElementById("corg_processBusy").className = "corg_shown";
	document.getElementById("corg_pbMsgDiv").innerHTML = "";
};

Com_Zimbra_contactOrganizer.prototype.pbDialogAbortListner =
function() {
	var msg = "Aborting...";
	this.abortProcess = true;
	this.pbDialog.runInBackground = false;
	this.setProgressbarAbortMsg(msg);
	this.pbDialog.getButton("corg_pbAbortBtn").setEnabled(false);//disable Abort btn
	this.pbDialog.getButton("corg_pbrunBackgroundBtn").setEnabled(false);// disable runInBg btn
};

Com_Zimbra_contactOrganizer.prototype.pbRunBackgroundAbortListner =
function() {
	this.pbDialog.runInBackground = true;
	this.pbDialog.popdown();
};

Com_Zimbra_contactOrganizer.prototype.formatMsg =
function(msg) {
	return  msg; //currently no formatting is done.
};