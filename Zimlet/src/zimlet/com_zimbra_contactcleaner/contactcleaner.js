/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Zimlet that scans and deletes duplicate contacts
 * 
 */
function com_zimbra_contactcleaner() {
}

com_zimbra_contactcleaner.prototype = new ZmZimletBase();
com_zimbra_contactcleaner.prototype.constructor = com_zimbra_contactcleaner;

com_zimbra_contactcleaner.AddressBookOnlyMsg = "You can use this Zimlet from <b>within Address Book application</b> only";
com_zimbra_contactcleaner.prototype.BEGIN_AT = 0;
com_zimbra_contactcleaner.prototype.END_AT = 59;
com_zimbra_contactcleaner.prototype.PROCESS_AT_ONCE = 60;

/**
 * Called on a double-click.
 */
com_zimbra_contactcleaner.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Called on a single-click.
 */
com_zimbra_contactcleaner.prototype.singleClicked =
function() {

	if (appCtxt.getAppController().getActiveApp() != "Contacts") {
		var dlg = appCtxt.getMsgDialog();
		dlg.reset();//reset dialog since we could be using it
		dlg.setMessage(com_zimbra_contactcleaner.AddressBookOnlyMsg, DwtMessageDialog.WARNING_STYLE);
		dlg.popup();
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
	this.dupesSetIdArray = new Array;
	this._AllContactsArecc_shown = false;

	//if zimlet dialog already exists...
	if (this.conOrgDialog) {
		this._resetStaleDialog();
		this.conOrgDialog.popup();
		appCtxt.getAppController().setStatusMsg("Scanning...", ZmStatusView.LEVEL_INFO);
		var closure = cc_createClosure(this, this.scanAddressBook)
		setTimeout(closure, 2000);
		return;
	}

	this._parentView.getHtmlElement().innerHTML = this.constructContactManagerView();
	this.addContactManagerButtons();
	appCtxt.getAppController().setStatusMsg("Scanning...", ZmStatusView.LEVEL_INFO);
	this.conOrgDialog.popup();
	this._initializeProgressbarDlg();
	document.getElementById("cc_dupesWithConflictsChkbox_id").onclick = cc_createClosure(this, this.handleMergeChkbox);
	document.getElementById("cc_dupesWithPartialMatchChkbox_id").onclick = cc_createClosure(this, this.handleMergePartialChkbox);
	document.getElementById("cc_expandCollapseAllLink_id").onclick = cc_createClosure(this, this.expandcollapse_all);

	this.pbView.getHtmlElement().innerHTML = this.constructContactProgressbarView();
	this._setAllValues("BUSY");
	var closure = cc_createClosure(this, this.scanAddressBook)
	setTimeout(closure, 2000);
};

//-------------------------------- INITIALIZE UI (START) ----------------------------------------
com_zimbra_contactcleaner.prototype._initializeEmptyDlg =
function() {
	this._parentView = new DwtComposite(this.getShell());
	this._parentView.setSize("700", "450");
	this._parentView.getHtmlElement().style.overflow = "auto";
};

com_zimbra_contactcleaner.prototype._resetStaleDialog =
function() {

	document.getElementById("cc_expandCollapseAllDiv_id").className = "cc_hidden";
	this._setAllValues("BUSY");
	document.getElementById("dupeResultsDiv").innerHTML = "";
	AjxDispatcher.run("GetContactController");
	this._contactList = AjxDispatcher.run("GetContacts");
	document.getElementById("cc_dupesWithConflictsChkbox_id").onclick = cc_createClosure(this, this.handleMergeChkbox);
	document.getElementById("cc_dupesWithPartialMatchChkbox_id").onclick = cc_createClosure(this, this.handleMergePartialChkbox);
	document.getElementById("cc_expandCollapseAllLink_id").onclick = cc_createClosure(this, this.expandcollapse_all);
	this.handleMergeChkbox();
	this.handleMergePartialChkbox();
	this.setProgressbarBegin();
	this.addContactManagerButtons();
};


com_zimbra_contactcleaner.prototype._initializeProgressbarDlg =
function() {
	this.pbView = new DwtComposite(this.getShell());
	this.pbView.setSize("600", "50");
	//this.pbView.getHtmlElement().style.overflow = "auto";
	this.pbDialog = this._createDialog({title:"Processing Contacts...", view:this.pbView});

	this.abortBtnId = "pbAbortBtn";
	var abortButton = new DwtDialog_ButtonDescriptor(this.abortBtnId, "Abort", DwtDialog.ALIGN_RIGHT);
	this.pbBackgroundBtnId = "pbrunBackgroundBtn";
	var pbBackgroundButton = new DwtDialog_ButtonDescriptor(this.pbBackgroundBtnId, "Run In Background", DwtDialog.ALIGN_RIGHT);
	this.pbDialog = this._createDialog({title:"Processing Contacts...", view:this.pbView, standardButtons:[DwtDialog.OK_BUTTON],extraButtons:[pbBackgroundButton, abortButton]});
	this.pbDialog.setButtonListener(this.abortBtnId, new AjxListener(this, this.pbDialogAbortListner));
	this.pbDialog.setButtonListener(this.pbBackgroundBtnId, new AjxListener(this, this.pbRunBackgroundAbortListner));
	this.pblen = "600";

	this.pbDialog.runInBackground = false;
};
com_zimbra_contactcleaner.prototype.addContactManagerButtons =
function() {
	if (this.conOrgDialog) return;


	this._ProcessButtonId = Dwt.getNextId();
	var blk = "";
	for (var k = 0; k < 15; k++)
		blk = blk + "&nbsp;";
	var processButton = new DwtDialog_ButtonDescriptor(this._ProcessButtonId, (blk + "Merge and Move duplicates to Trash" + blk), DwtDialog.ALIGN_RIGHT);
	this.conOrgDialog = this._createDialog({title:"Contact Cleaner", view:this._parentView, standardButtons : [ DwtDialog.CANCEL_BUTTON],extraButtons:[processButton]});
	this.conOrgDialog.setButtonListener(this._ProcessButtonId, new AjxListener(this, this.cleanerProcessBtnListner));
	AjxDispatcher.run("GetContactController");
	this._contactList = AjxDispatcher.run("GetContacts");
};
//-------------------------------- INITIALIZE UI (END) --------------------------------------------------------

//-------------------------------- SCAN AB FOR DUPLICATES (START) ----------------------------------------
com_zimbra_contactcleaner.prototype.scanAddressBook =
function() {
	this.normalizeContacts();
	//handle 0 contacts
	if (this.filteredContactsArry.length == 0) {
		this._setAllValues("ZERO");
		return;
	}

	this.vector = new AjxVector();
	var html = new Array();
	var i = 0;
	document.getElementById("cc_dupesWithConflictsChkbox_id").checked = false;
	document.getElementById("cc_dupesWithPartialMatchChkbox_id").checked = false;
	var totaldupes = 0;
	this.findDupes(this.filteredContactsArry);
	this.vector = this.vec_master;


	document.getElementById("cc_expandCollapseAllDiv_id").className = "cc_hidden";
	this._dupeResultsRows = "";
	this._vectorSize = this.vector.size();
	this.totaldupes = 0;
	this.totalContactsWithDupes = 0;
	this.totalPerfectMatchCount_dup = 0;
	this.totalPerfectMatchCount_cnt = 0;
	this.totalPartialMatchCount_dup = 0;
	this.totalPartialMatchCount_cnt = 0;
	this.totalCnfltMatchCount_dup = 0;
	this.totalCnfltMatchCount_cnt = 0;
	this._setAllValues("ZERO");
	if (this._vectorSize > 0)
		document.getElementById("cc_expandCollapseAllDiv_id").className = "cc_shown";

	this._constructdupeResultsDiv(0);
	//construct dupes display View
};

com_zimbra_contactcleaner.prototype.findDupes =
function(array) {
	var _arry;
	this.resetIsOriginal = true;
	this.vec_master = this.getDupesVector(array.sort(sortContactsBy_FN), "_firstName", "_lastName");
	//this.vec_master = this.getDupesVector(array.sort(sortContactsBy_E11), "_email", "_email");
	this.resetIsOriginal = false;

	this.vec_E11 = this.getDupesVector(this.leftOverArry.sort(sortContactsBy_E11), "_email", "_email");
	this.vec_E12 = this.getDupesVector(this.leftOverArry.sort(sortContactsBy_E12), "_email", "_email2");
	this.vec_E13 = this.getDupesVector(this.leftOverArry.sort(sortContactsBy_E13), "_email", "_email3");
	this.vec_E21 = this.getDupesVector(this.leftOverArry.sort(sortContactsBy_E21), "_email2", "_email");
	this.vec_E22 = this.getDupesVector(this.leftOverArry.sort(sortContactsBy_E22), "_email2", "_email2");
	this.vec_E23 = this.getDupesVector(this.leftOverArry.sort(sortContactsBy_E23), "_email2", "_email3");
	this.vec_E31 = this.getDupesVector(this.leftOverArry.sort(sortContactsBy_E31), "_email3", "_email");
	this.vec_E32 = this.getDupesVector(this.leftOverArry.sort(sortContactsBy_E32), "_email3", "_email2");
	this.vec_E33 = this.getDupesVector(this.leftOverArry.sort(sortContactsBy_E33), "_email3", "_email3");

	this.mergeVectorsWithMaster(this.vec_E11, "_email", "_email");
	this.mergeVectorsWithMaster(this.vec_E12, "_email", "_email2");
	this.mergeVectorsWithMaster(this.vec_E13, "_email", "_email3");
	this.mergeVectorsWithMaster(this.vec_E21, "_email2", "_email");
	this.mergeVectorsWithMaster(this.vec_E22, "_email2", "_email2");
	this.mergeVectorsWithMaster(this.vec_E23, "_email2", "_email3");
	this.mergeVectorsWithMaster(this.vec_E31, "_email3", "_email");
	this.mergeVectorsWithMaster(this.vec_E32, "_email3", "_email2");
	this.mergeVectorsWithMaster(this.vec_E33, "_email3", "_email3");
}

com_zimbra_contactcleaner.prototype.mergeVectorsWithMaster =
function(_vector, currentField, masterField) {
	var y1, y2, matchfoundFlg;

	if (!_vector)
		return;

	var currVecSz = _vector.size();
	var masterVectorSz = this.vec_master.size();
	if (masterVectorSz == 0) {
		this.vec_master = _vector;
		return;
	}

	for (var i = 0; i < currVecSz; i++) {
		matchfoundFlg = false;
		var currentDupeSet = _vector.get(i);

		if (currentField != "_firstName") {
			var x1 = eval("currentDupeSet[0]." + currentField);
			var x2 = eval("currentDupeSet[0]." + masterField);
			for (var j = 0; j < masterVectorSz; j++) {
				//y1 =  this.vec_master.get(j)[0].eval(currentField);
				//y2 =  this.vec_master.get(j)[0].eval(masterField);
				y1 = eval("this.vec_master.get(" + j + ")[0]." + currentField);
				y2 = eval("this.vec_master.get(" + j + ")[0]." + masterField);
				if (((x1 == y1) && (x1 != "-" && y1 != "-")) || ((x1 == y2) && (x1 != "-" && y2 != "-"))
						|| ((x2 == y1) && (x2 != "-" && y1 != "-")) || ((x2 == y2) && (x2 != "-" && y2 != "-"))) {
					for (var n = 0; n < currentDupeSet.length; n++) {
						if (!currentDupeSet[n]._isOriginal) //dont add contacts tht is already found
							this.vec_master.get(j).push(currentDupeSet[n]);

					}
					//match found merge them
					matchfoundFlg = true;
					break;
				}
			}
		} else {
			var currentItmFieldVal = (currentDupeSet[0]._firstName + currentDupeSet[0]._lastName).toLowerCase();
			for (var j = 0; j < masterVectorSz; j++) {
				if (currentItmFieldVal == (this.vec_master.get(j)[0]._firstName + this.vec_master.get(j)[0]._lastName).toLowerCase()) {
					for (var n = 0; n < currentDupeSet.length; n++) {
						if (!currentDupeSet[n]._isOriginal) //dont add contacts tht is already found
							this.vec_master.get(j).push(currentDupeSet[n]);

					}
					//match found merge them
					matchfoundFlg = true;
					break;
				}
			}
		}

		//add this as an *additional* master dupeset
		if (!matchfoundFlg)
			this.vec_master.add(currentDupeSet);

	}

}
com_zimbra_contactcleaner.prototype.getDupesVector =
function(_arry, fld1, fld2) {
	var nxt_fld1, nxt_fld2, matchfound, x_fld1, x_fld2;

	this.leftOverArry = new Array();
	var len = _arry.length;
	var dupeArry = new Array();
	var _vector = new AjxVector();
	var startindx = 0;
	if (!_arry[startindx])
		return;

	if (this.resetIsOriginal)
		_arry[0]._isOriginal = false;


	x_fld1 = eval("_arry[" + startindx + "]." + fld1);
	x_fld2 = eval("_arry[" + startindx + "]." + fld2);
	for (var k = 1; k < len; k++) {
		if (this.resetIsOriginal)
			_arry[k]._isOriginal = false;

		matchfound = false;
		nxt_fld1 = eval("_arry[" + k + "]." + fld1);
		nxt_fld2 = eval("_arry[" + k + "]." + fld2);
		/*
		if (fld1 != "_firstName" 
			&& (((nxt_fld1 == x_fld1) && (nxt_fld1 != "-" && x_fld1 != "-")) || 
				((nxt_fld2 == x_fld2) && (nxt_fld2 != "-" && x_fld2 != "-"))|| 
				((nxt_fld1 == x_fld2) && (nxt_fld1 != "-" && x_fld2 != "-")) || 
				((nxt_fld2 == x_fld1) && (nxt_fld2 != "-" && x_fld1 != "-")))
				||
			((fld1 == "_firstName") 
			&& ((nxt_fld1 == x_fld1) && (nxt_fld1 != "-" && x_fld1 != "-"))
				&& ((nxt_fld2 == x_fld2) && (nxt_fld2 != "-" && x_fld2 != "-")))){
		*/
		if(	((nxt_fld1 == x_fld1) && (nxt_fld1 != "-" && x_fld1 != "-")) || 
			((nxt_fld2 == x_fld2) && (nxt_fld2 != "-" && x_fld2 != "-")) || 
			((nxt_fld1 == x_fld2) && (nxt_fld1 != "-" && x_fld2 != "-")) || 
			((nxt_fld2 == x_fld1) && (nxt_fld2 != "-" && x_fld1 != "-"))){
				if (_arry[k]._isOriginal) {
					this._dupesetAlreadyHasOrig = true;
				}
				dupeArry.push(_arry[k]);
				matchfound = true;
		}

		//if there is no match or last element...
		if (!matchfound || (k == len - 1)) {
			if (dupeArry.length > 0) {
				//add the original
				if (!this._dupesetAlreadyHasOrig)
					_arry[startindx]._isOriginal = true;

				dupeArry.push(_arry[startindx]);
				_vector.add(dupeArry);
			}
			//add both original OR  no-dupes..
			this.leftOverArry.push(_arry[startindx]);
			//if its the last element and there was no match, add that too
			if (!matchfound && (k == len - 1)) {
				this.leftOverArry.push(_arry[k]);
			}

			x_fld1 = eval("_arry[" + k + "]." + fld1);
			x_fld2 = eval("_arry[" + k + "]." + fld2);
			dupeArry = [];
			startindx = k;
			this._dupesetAlreadyHasOrig = false;
		}

	}
	return _vector;
}
com_zimbra_contactcleaner.prototype.normalizeContacts =
function() {

	this.parseSelection();

	if (!this._contactList)
		return;

	this.filteredContactsArry = new Array();
	var _tmpArry = this._contactList.getArray();

	for (var j = 0; j < _tmpArry.length; j++) {
		var currentContact = _tmpArry[j];
		var currentContactFoldrId = currentContact.folderId ? currentContact.folderId : currentContact.l;
		var attr = currentContact.attr ? currentContact.attr : currentContact._attrs;
		try {
			if (currentContactFoldrId != ZmFolder.ID_TRASH) {
				currentContact._email = (attr.email) ? (attr.email).toLowerCase() : "-";
				currentContact._email2 = (attr.email2) ? (attr.email2).toLowerCase() : "-";
				currentContact._email3 = (attr.email3) ? (attr.email3).toLowerCase() : "-";
				currentContact._company = (attr.company) ? (attr.company).toLowerCase() : "-";
				currentContact._firstName = (attr.firstName) ? (attr.firstName).toLowerCase() : "-";
				currentContact._lastName = (attr.lastName) ? (attr.lastName).toLowerCase() : "-";
				currentContact._notes = (attr.notes) ? attr.notes  : "-";
				currentContact._workPhone = (attr.workPhone) ? attr.workPhone : "-";
				this.filteredContactsArry.push(currentContact);
			}
		} catch(e) {
		}

	}
}
//-------------------------------- SCAN AB FOR DUPLICATES(END) --------------------------------------------------------




//-------------------------------- DISPLAY ZIMLET UI AND DUPLICATE-SET UI (START) ----------------------------------------
com_zimbra_contactcleaner.prototype.constructContactManagerView =
function() {
	//todo -	return AjxTemplate.expand("com_zimbra_contactcleaner.templates.contactcleaner#ccProcessChoicesTmplt", null);
	var m;
	var html = new Array();
	var i = 0;
	html[i++] = "<BR><BR><DIV id='cc_processingChoicesDiv' class='cc_shown'>";
	html[i++] = "<table  style=\"{border-color:black; border-width:thick}\"rules=\"groups\" frame=\"hsides\" align=center  cellspacing=0 cellpadding=10 width=90%>";
	html[i++] = "<thead>";
	html[i++] = "<TR><TH>Merge</TH><TH>Duplicate Category</TH><TH>#Contacts with duplicates</TH><TH>#Duplicates</TH></TR>";
	html[i++] = "</thead><tbody>";

	html[i++] = "<TR>";
	html[i++] = "<Td><Input type='checkbox' id=cc_dupesWithPerfectMatchChkbox_id checked=true ></Td>";
	html[i++] = "<TD  class='cc_tdclass'>Duplicates with perfect match:</TD>";
	html[i++] = "<Td  class='cc_dupeTDclassNoColor' id='cc_ContactsWithPerfectMatch_id' align=center>0</TD>";
	html[i++] = "<Td  class='cc_dupeTDclassComplete' id='cc_dupesWithPerfectMatch_id' align=center>0</TD>";
	html[i++] = "</TR>";

	html[i++] = "<TR>";
	html[i++] = "<Td><Input type='checkbox' id=cc_dupesWithPartialMatchChkbox_id  ></Td>";
	html[i++] = "<TD   class='cc_tdclass'>Duplicates with partial match:";
	html[i++] = "<br><span class=cc_hidden id=cc_reviewPartialInfo>Please review partially matched duplicates down below</span>";
	html[i++] = "</TD>";
	html[i++] = "<Td  class='cc_dupeTDclassNoColor' id='cc_ContactsWithPartialMatch_id' align=center>0</TD>";
	html[i++] = "<Td class='cc_dupeTDclassPartial' id='cc_dupesWithPartialMatch_id' align=center>0</TD>";
	html[i++] = "</TR>";

	html[i++] = "<TR>";
	html[i++] = "<Td><Input type='checkbox' id=cc_dupesWithConflictsChkbox_id ></Td>";
	html[i++] = "<TD class='cc_tdclass'>Duplicates with partial match & conflicts:";
	html[i++] = "<br><span class=cc_hidden id=cc_reviewConflctInfo>Please review conflicting duplicates down below</span>";
	html[i++] = "</TD>";
	html[i++] = "<Td  class='cc_dupeTDclassNoColor' id='cc_ContactsWithConflictsMatch_id' align=center>0</TD>";
	html[i++] = "<Td class='cc_dupeTDclassConflict' id='cc_dupesWithconflicts_id' align=center>0</Td>";
	html[i++] = "</TR>";

	html[i++] = "<TBODY>";

	html[i++] = "<TR>";
	html[i++] = "<TD></TD><TD class='cc_tdclass'>Total Duplicates: </TD>";
	html[i++] = "<Td class='cc_dupeTDclassNoColor' id='cc_totalContWithDupes_id' align=center>0</Td>";
	html[i++] = "<Td class='cc_dupeTDclassConflict' id='cc_totalDupes_id' align=center>0</Td>";
	html[i++] = "<Td></Td>";
	html[i++] = "</TR>";
	html[i++] = "</Tbody>";
	html[i++] = "</TABLE><BR><BR>";

	html[i++] = "<DIV id=\"cc_expandCollapseAllDiv_id\" class=\"cc_hidden\">";
	html[i++] = "<DIV id=\"cc_howtouse_id\" size=3 >";
	html[i++] = "<table frame=\"box\" rules=\"none\"  bgcolor='FFFFCC' cellpadding=1 cellspacing=1 width=100%><tr><td>";
	html[i++] = "NOTE: 1. Select duplicates type to merge from the table above. 2. Review corresponding duplicate-sets below";
	html[i++] = " 3. Finally, Click Process button 4. After processing is done, please logout and log back in.";
	html[i++] = "</table></tr></td>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV class=\"cc_dupeListLink\"><a href=\"#\" id= 'cc_expandCollapseAllLink_id' class = 'cc_expndclpseall'>Expand All/Collapse All</a></DIV>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV id=\"dupeResultsDiv\"></DIV>";


	return html.join("");
};

com_zimbra_contactcleaner.prototype._constructdupeResultsDiv =
function(indx) {
	var html = new Array();
	var i = 0;
	if (indx == 0) {
		this._tableTophtml = "<TABLE  BGCOLOR = WHITE border=1  align = \"center\" cellpadding=1 cellspacing=1 width=100%>";
		this._tableBtmhtml = "</TABLE>";
		this.evnflg = 0;
	}

	//for (var j = indx; j <  this._vectorSize; j++) {
	if (indx < this._vectorSize) {
		var firstDupeContct = this.vector.get(indx)[0];
		var currentdupesVector = this.vector.get(indx)
		var noOfDupesInCurrentContactVctr = currentdupesVector.length - 1;
		this._currentDupeListHasConflict = false;
		this._currentDupeListHasPartialMatch = false;
		this.totaldupes = this.totaldupes + noOfDupesInCurrentContactVctr;
		var dupeId = "dupeId" + firstDupeContct.id;
		this.dupesSetIdArray.push(dupeId);
		var contactLablesHTML = this._constructTable(currentdupesVector, dupeId);
		this._setCounterVals(indx, noOfDupesInCurrentContactVctr, dupeId);
		if (this._currentDupeListHasConflict) {
			html[i++] = "<tr  bgcolor=\"#CF3830\"><td>";
		} else if (this._currentDupeListHasPartialMatch) {
			html[i++] = "<tr  bgcolor=\"orange\"><td>";
		} else {
			html[i++] = "<tr  bgcolor=\"#FFFFCC\"><td>";
			this.evnflg = 0;
		}
		html[i++] = "<a href=\"#\"  class=\"cc_dupeListLink\" onclick=\"cc_expandcollapse('" + dupeId + "');return false;\"> ";
		if (firstDupeContct._lastName != "-") {
			html[i++] = firstDupeContct._lastName;
			html[i++] = ",";
		}
		if (firstDupeContct._firstName != "-") {
			html[i++] = firstDupeContct._firstName;
		}
		if (firstDupeContct._email != "-")
			html[i++] = " -- " + firstDupeContct._email;

		html[i++] = " (";
		html[i++] = noOfDupesInCurrentContactVctr;
		if (this.vector.get(indx)._dupesSetFlag == "Perfect_Match")
			html[i++] = " duplicate(s) found; Perfect Match ";
		else if (this.vector.get(indx)._dupesSetFlag == "Partial_Match")
			html[i++] = " duplicate(s) found; Partial Match";
		else if (this.vector.get(indx)._dupesSetFlag == "Has_Conflict")
			html[i++] = " duplicate(s) found; Has Conflict";
		html[i++] = ")";
		html[i++] = "</a>";
		html[i++] = contactLablesHTML;
		html[i++] = "</td></tr>";
		this._dupeResultsRows = this._dupeResultsRows + html.join("");
		var closure = AjxCallback.simpleClosure(this._constructdupeResultsDiv, this, indx + 1);
		setTimeout(closure, 3);
	}
	if (indx == this._vectorSize) {
		var dupeResultsDiv = document.getElementById("dupeResultsDiv");
		dupeResultsDiv.innerHTML = this._tableTophtml + this._dupeResultsRows + this._tableBtmhtml;
		appCtxt.getAppController().setStatusMsg("..Scanning Address Book complete", ZmStatusView.LEVEL_INFO);
		document.getElementById("dupeResultsDiv").className = "cc_shown";
	}
}
com_zimbra_contactcleaner.prototype._setCounterVals = function(indx, noOfDupesInCurrentContactVctr, dupeId) {
	if (!this._currentDupeListHasConflict && !this._currentDupeListHasPartialMatch) {
		this.vector.get(indx)._dupesSetFlag = "Perfect_Match";
		this.totalPerfectMatchCount_dup = this.totalPerfectMatchCount_dup + noOfDupesInCurrentContactVctr;
		document.getElementById("cc_dupesWithPerfectMatch_id").innerHTML = this.totalPerfectMatchCount_dup;
		document.getElementById("cc_ContactsWithPerfectMatch_id").innerHTML = ++this.totalPerfectMatchCount_cnt;
	} else if (this._currentDupeListHasConflict) {
		this.vector.get(indx)._dupesSetFlag = "Has_Conflict";
		this.vector.get(indx)._hasConflictingEmailField = this._hasConflictingEmailField;
		this.totalCnfltMatchCount_dup = this.totalCnfltMatchCount_dup + noOfDupesInCurrentContactVctr;
		document.getElementById("cc_dupesWithconflicts_id").innerHTML = this.totalCnfltMatchCount_dup;
		document.getElementById("cc_ContactsWithConflictsMatch_id").innerHTML = ++this.totalCnfltMatchCount_cnt;
	} else if (this._currentDupeListHasPartialMatch) {
		this.vector.get(indx)._dupesSetFlag = "Partial_Match";
		this.totalPartialMatchCount_dup = this.totalPartialMatchCount_dup + noOfDupesInCurrentContactVctr;
		document.getElementById("cc_dupesWithPartialMatch_id").innerHTML = this.totalPartialMatchCount_dup;
		document.getElementById("cc_ContactsWithPartialMatch_id").innerHTML = ++this.totalPartialMatchCount_cnt;

	}
	this.vector.get(indx)._dupesSetId = dupeId;
	document.getElementById("cc_totalContWithDupes_id").innerHTML = ++this.totalContactsWithDupes;
	document.getElementById("cc_totalDupes_id").innerHTML = this.totaldupes;
}

com_zimbra_contactcleaner.prototype._constructTable =
function(dupeArry, dupeId) {

	var elem, origelem, matchedflg, elementflg, tmpArry, confElemArry, allElemArry, elemHasConflict, elemHasNoConflict;
	var individualTblStartStr,indvidualTblEndStr,individualMergedTblStartStr, coverTblStartStr, coverTblEndStr, internalTbls;
	allElemArry = [];
	elemHasNoConflict = [];
	elemHasConflict = [];
	internalTbls = "";
	this._hasConflictingEmailField = false;
	var mgrdRowsArry = new Array();
	for (var i = 0; i < dupeArry.length; i++) {
		var tc = dupeArry[i];
		var tcAttr = tc.attr ? tc.attr : tc._attrs;
		for (var el in tcAttr){
			//lot of times contacts will have notes with a blank line, ignore those
			if(el != "notes" || (el == "notes" && tcAttr[el].replace("\n","") != "")) {
				allElemArry.push(el);
			}
		}
	}
	allElemArry = cc_unique(allElemArry);
	allElemArry = allElemArry.sort();


	individualTblStartStr = "<td><table frame=\"box\" rules=\"none\"  bgcolor='FFFFCC' cellpadding=1 cellspacing=1 width=100%>";
	individualMergedTblStartStr = "<td><table frame=\"box\" rules=\"none\"  bgcolor='gray' cellpadding=1 cellspacing=1 width=100%>";

	var mrgdArry = new Array();
	for (var j = 0; j < dupeArry.length; j++) {
		var html = new Array();
		var i = 0;
		this.getdupeContactRowEven = true;
		var currentContact = dupeArry[j];
		var attr = currentContact.attr ? currentContact.attr : currentContact._attrs;
		//	if(j > 0){
		for (var k = 0; k < allElemArry.length; k++) {
			origelem = allElemArry[k];
			confElemArry = [];
			var currentRow = "";
			elementflg = "Field_Not_Found";
			//reset flag
			if (origelem == "firstLast" || origelem == "fullName" || origelem == "fileAs")
				continue;

			//check if element exists..
			for (elem in attr) {
				if (elem == origelem) {
					elementflg = "Field_Found";
					break;
				}
			}

			//=================================================
			//check if the element/field has conflicts..
			if (elementflg == "Field_Found") {
				//if (elemHasNoConflict.indexOf(origelem) != -1)
				if (com_zimbra_contactcleaner.arrayContainsKey(elemHasNoConflict, origelem))
					elementflg = "Field_Found_No_Conflict";
				//else if (elemHasConflict.indexOf(origelem) != -1)
				else if (com_zimbra_contactcleaner.arrayContainsKey(elemHasConflict, origelem))
					elementflg = "Field_Found_Has_Conflict";
				//else if ((elemHasConflict.indexOf(origelem) == -1) && (elemHasNoConflict.indexOf(origelem) == -1)) {
				else {
					//---------------------------------------------------------------
					for (var n = 0; n < dupeArry.length; n++) {
						var tmpcont = dupeArry[n];
						var tmpattr = tmpcont.attr ? tmpcont.attr : tmpcont._attrs;
						try {
							//add current field value from all the dupes(remove newline \r\n, so its easy to match)
							var tmpelem = eval("tmpattr." + origelem);
							confElemArry.push((tmpelem.replace(/\n/g, "").replace(/\r/g, "")).toLowerCase());
						} catch(e) {
						}
					}
					if (cc_unique(confElemArry).length > 1) {

						if (origelem.indexOf("email") != -1)
							this._hasConflictingEmailField = true;

						elemHasConflict.push(origelem);
						elementflg = "Field_Found_Has_Conflict";
					} else {
						elemHasNoConflict.push(origelem);
						elementflg = "Field_Found_No_Conflict";
					}
					//---------------------------------------------------------------
				}
			}
			//=================================================

			if (elementflg == "Field_Found_No_Conflict") { //when field was found but not matched
				currentRow = this.getdupeContactRowsHTML(origelem, eval("attr." + origelem), elementflg, false, dupeId);
				html[i++] = currentRow;
				if (! com_zimbra_contactcleaner.arrayContainsKey(mgrdRowsArry, origelem))
					mgrdRowsArry[origelem] = currentRow;
			} else if (elementflg == "Field_Found_Has_Conflict") { //when field was found but not matched
				html[i++] = this.getdupeContactRowsHTML(origelem, eval("attr." + origelem), elementflg, false, dupeId);
				if (! com_zimbra_contactcleaner.arrayContainsKey(mgrdRowsArry, origelem))
					mgrdRowsArry[origelem] = this.getdupeContactRowsHTML(origelem, eval("attr." + origelem), elementflg, true, dupeId);
				this._currentDupeListHasConflict = true;
			} else if (elementflg == "Field_Not_Found") {//when field itself not found(but exists on other dupe)
				html[i++] = this.getdupeContactRowsHTML(origelem, "-", elementflg, false, dupeId);
				this._currentDupeListHasPartialMatch = true;
			}
		}
		indvidualTblEndStr = "</table></td>";
		internalTbls = internalTbls + individualTblStartStr + this.getTopRow("CONTACT " + (j + 1)) + html.join("") + indvidualTblEndStr;
	}
	coverTblEndStr = "</tr></table>";
	var mrgdCntct = this.createMergedContact(mgrdRowsArry, individualTblStartStr, indvidualTblEndStr);
	return  this.getcoverTblStartStr(dupeId, dupeArry.length + 1) + mrgdCntct + internalTbls + coverTblEndStr;
};

com_zimbra_contactcleaner.prototype.getdupeContactRowsHTML =
function(elem, val, elementflg, isMerged, dupeId) {
	var valCell, foreCol, rowbgCol;

	if (isMerged) {
		var elemId = elem + "_" + dupeId;
		if (elem != "notes")
			val = "<input id=\'" + elemId + "\' class='cc_confltFld' type=\"text\" value=\"Enter required value\"/>";
		else
			val = "<textarea id=\'" + elemId + "\' class='cc_confltFld' >Enter required value</textarea>";
	} else { //bold the email fields
		if (elem == "email" || elem == "email2" || elem == "email3")
			val = "<b>" + val + "</b>";
	}

	if (elementflg == "Field_Found_No_Conflict") { //when field was found but not matched
		foreCol = " style=\"color: black\" ";
	} else if (elementflg == "Field_Found_Has_Conflict") { //when field was found but not matched
		foreCol = " style=\"color: red\" ";
	} else if (elementflg == "Field_Not_Found") {//when field itself not found
		foreCol = " style=\"color: orange\" ";
	}

	if (this.getdupeContactRowEven) {
		rowbgCol = "bgcolor='#FFFFB3'";
		this.getdupeContactRowEven = false;
	} else {
		rowbgCol = "";
		this.getdupeContactRowEven = true;
	}
	var name = ZmContact._AB_FIELD[elem];
	if (name == undefined)
		name = elem;
	return "<tr " + rowbgCol + foreCol + " ><td><b>" + name + ":</b></td><td " + foreCol + " >" + val + "</td>";
}
com_zimbra_contactcleaner.prototype.createMergedContact =
function(mrgdRowsArry, individualTblStartStr, indvidualTblEndStr) {
	var rows = "";
	mrgdRowsArry = mrgdRowsArry.sort();
	//to align fields of this and other dupes next to eachother
	for (var el in mrgdRowsArry)
		rows = rows + mrgdRowsArry[el];

	return  individualTblStartStr + this.getTopRow("MERGED") + rows + indvidualTblEndStr;
}
com_zimbra_contactcleaner.prototype.getTopRow =
function(rowVal) {
	return "<tr><td colspan=2 class='cc_topRow' align=center>" + rowVal + "</td></tr>";
}
com_zimbra_contactcleaner.prototype.getcoverTblStartStr =
function(dupeId, colspan) {
	var html = new Array();
	var i = 0;
	html[i++] = "<table valign class=\"cc_hidden\"   id = '" + dupeId + "'>";
	if (this._currentDupeListHasConflict) {
		html[i++] = "<tr   VALIGN=\"top\" class='cc_confltChkbox'>";
		html[i++] = "<td colspan = " + colspan + "><Input  type='radio' name = '" + dupeId + "_radio" + "'  id = '" + dupeId + "_ignore" + "'  >Ignore Merging</input></td>";
		html[i++] = "</tr>";
		if (!this._hasConflictingEmailField) {
			html[i++] = "<tr   VALIGN=\"top\"class='cc_confltChkbox'>";
			html[i++] = "<td colspan = " + colspan + "><Input  type='radio' name = '" + dupeId + "_radio" + "'  id = '" + dupeId + "_addnotes" + "' checked='true' >Merge, but add conflict info to 'Notes'-section(so we dont loose data)</input></td>";
			html[i++] = "</tr>";
		} else {
			html[i++] = "<tr   VALIGN=\"top\"class='cc_confltChkbox'>";
			html[i++] = "<td colspan = " + colspan + "><Input  type='radio' name = '" + dupeId + "_radio" + "'  id = '" + dupeId + "_addnotes" + "' checked='true' >Add conflicting email info to 'email2' and 'email3' fields and the rest of the conflicting field info to'Notes'-section(so we dont loose data)</input></td>";
			html[i++] = "</tr>";
		}
		html[i++] = "<tr  VALIGN=\"top\" class='cc_confltChkbox'>";
		html[i++] = "<td colspan = " + colspan + "><Input  type='radio' name = '" + dupeId + "_radio" + "' id = '" + dupeId + "_current" + "'  >Use Below Merged Contact</input></td>";
		html[i++] = "</tr>";
	} else if (this._currentDupeListHasPartialMatch) {
		html[i++] = "<tr  VALIGN=\"top\" class='cc_confltChkbox'>";
		html[i++] = "<td ><Input colspan = " + (colspan) + " type='checkbox'  id = '" + dupeId + "_ignoreChkBox" + "'  >Ignore Merging</input></td>";
		html[i++] = "</tr>";
	}
	html[i++] = "<tr VALIGN=\"top\">";
	return   html.join("");
}
com_zimbra_contactcleaner.prototype._setAllValues =
function(busyOr0) {
	var val;
	if (busyOr0 == "BUSY") {
		val = "<img   src=\"" + this.getResource("cc_busy.gif") + "\"  />";
	} else if (busyOr0 == "ZERO") {
		val = "0";
	}
	document.getElementById("cc_dupesWithPerfectMatch_id").innerHTML = val;
	document.getElementById("cc_ContactsWithPerfectMatch_id").innerHTML = val;
	document.getElementById("cc_dupesWithconflicts_id").innerHTML = val;
	document.getElementById("cc_ContactsWithConflictsMatch_id").innerHTML = val;
	document.getElementById("cc_dupesWithPartialMatch_id").innerHTML = val;
	document.getElementById("cc_ContactsWithPartialMatch_id").innerHTML = val;
	document.getElementById("cc_totalContWithDupes_id").innerHTML = val;
	document.getElementById("cc_totalDupes_id").innerHTML = val;
}
//-------------------------------- DISPLAY DUPLICATE CONTACTS (END) ----------------------------------------




//-------------------------------- PROCESS DUPLICATE CONTACTS (START) ----------------------------------------
com_zimbra_contactcleaner.prototype.cleanerProcessBtnListner =
function(ev) {

	this.parseSelection();
	//parse the checkbox selection
	this.startAt = this.BEGIN_AT;
	this.endAt = this.END_AT;
	this.dupeContactCurrentCnt = 0;
	this.dupeContactNextCnt = 0;
	this.pbView._initialized = false;
	this.dupesVectorSize = 0;
	this.pbDialog.getButton("pbAbortBtn").setEnabled(true);

	//get the total dupes to be acually processes(depending on checkbox selection)
	this.totaldupesToProcess = 0;
	if (this._mergePerfectMatchedChk)
		this.totaldupesToProcess = this.totaldupesToProcess + this.totalPerfectMatchCount_cnt;
	if (this._mergePartiallyMatchedChk)
		this.totaldupesToProcess = this.totaldupesToProcess + this.totalPartialMatchCount_cnt;
	if (this.mergeConflictsChk)
		this.totaldupesToProcess = this.totaldupesToProcess + this.totalCnfltMatchCount_cnt;


	if (this.totaldupesToProcess == 0)
		return;

	//start processing..
	this.concleanerDlgDeleteListner(ev);


};

com_zimbra_contactcleaner.prototype.concleanerDlgDeleteListner =
function(ev) {
	var soapDoc = null;
	var dupesVector = this.vector;
	//handle 0 contacts
	if (dupesVector == undefined)
		return;
	this._matchfound = false;
	this.dupesVectorSize = dupesVector.size();


	if (this.endAt > this.dupesVectorSize) {
		this.endAt = this.dupesVectorSize - 1;
	}

	for (var i = this.startAt; i <= this.endAt; i++) {
		var dupesArry = dupesVector.get(i);
		var dupeSetType = dupesArry._dupesSetFlag;
		var _dupesSetId = dupesArry._dupesSetId;
		var diffEmails = [];
		var _hasConflictingEmailField = dupesArry._hasConflictingEmailField;
		var conflictFlg = "";
		//if corresponding chkbox not checked, ignore processing..
		if (!this._mergePerfectMatchedChk && (dupeSetType == "Perfect_Match") ||
		    !this._mergePartiallyMatchedChk && (dupeSetType == "Partial_Match") ||
		    !this.mergeConflictsChk && (dupeSetType == "Has_Conflict"))
			continue;

		//if current set has partial match but ignore merge is checked, continue to next set...
		if (dupeSetType == "Partial_Match") {
			if (document.getElementById(_dupesSetId + "_ignoreChkBox").checked)
				continue;
		}
		//if the current set has conflicts...
		if (dupeSetType == "Has_Conflict") {
			if (document.getElementById(_dupesSetId + "_ignore").checked) {
				continue;
			} else if (document.getElementById(_dupesSetId + "_addnotes").checked) {
				conflictFlg = "MERGE_ADD_NOTES";
			} else if (document.getElementById(_dupesSetId + "_current").checked) {
				conflictFlg = "MERGE_MANUAL";
			}
		}

		this._matchfound = true;
		//set _matchfound to true, indicating soap call will be made
		this.dupeContactNextCnt = this.dupeContactNextCnt + dupesArry.length - 1;
		if (!soapDoc) {
			soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
			soapDoc.setMethodAttribute("onerror", "continue");
		}

		//======================================================================
		//Delete duplicates...
		//======================================================================
		var contactActionReq = soapDoc.set("ContactActionRequest", null, null, "urn:zimbraMail");
		var doc1 = soapDoc.getDoc();
		var action = soapDoc.set("action");
		action.setAttribute("op", "move");
		action.setAttribute("l", ZmFolder.ID_TRASH);

		//collect ids of duplicate contacts that will be deleted.
		//0th is considered as original, so start from 1
		var idstr = "";
		for (var j = 1; j < dupesArry.length; j++) {
			var duplicateContact = dupesArry[j];
			if (j == 1) {
				idstr = duplicateContact.id;
			} else {
				idstr = idstr + "," + duplicateContact.id;
			}
		}
		action.setAttribute("id", idstr);
		contactActionReq.appendChild(action);

		//======================================================================
		//Merge all contacts...
		//======================================================================
		var newAttrs = new Array();
		var tagsArry = new Array();

		for (var j = 0; j < dupesArry.length; j++) {
			var duplicateContact = dupesArry[j];
			var newattr = new AjxVector();
			var attr = duplicateContact.attr ? duplicateContact.attr : duplicateContact._attrs;

			//use values of first contact..
			if (j == 0 && dupeSetType == "Perfect_Match") {
				newattr = attr;

				//just merge all fields...
			} else if (dupeSetType == "Partial_Match") {
				for (var name in attr) {
					newAttrs[name] = attr[name];
				}

				//resolve conflicting values, add conflicting value to notes section..
			} else if (conflictFlg == "MERGE_ADD_NOTES") {
				try {
					for (var name in attr) {
						//if the current set has conflicting emails, then we can add them to email2, emai3 fields(if possible)
						if (_hasConflictingEmailField && (name.indexOf("email") != -1)) {
							diffEmails.push(attr[name]);
							continue;
						}
						//if element not exist add it(like in the first loop or element is actually missing)
						if (!newAttrs[name]) {
							newAttrs[name] = attr[name];
						} else if (newAttrs[name] != attr[name]) {//if conflicting element found..
							if (newAttrs["notes"] == undefined)
								newAttrs["notes"] = "";

							newAttrs["notes"] = newAttrs["notes"] + "\r\n--ADDITIONAL INFO FROM DUPLICATE CONTACT--\r\n" + name + ": " + attr[name];
						}
					}
				} catch(e) {
				}
				//resolve conflicting values, Use the manually entered value..
			} else if (conflictFlg == "MERGE_MANUAL") {
				try {
					for (var name in attr) {
						if (document.getElementById(name + "_" + _dupesSetId)) {
							newAttrs[name] = document.getElementById(name + "_" + _dupesSetId).value;
						} else {
							newAttrs[name] = attr[name];
						}
					}
				} catch(e) {
				}

			}


			//add tag info if any..
			if (duplicateContact.tags)  //if no tags, continue
				tagsArry = tagsArry.concat(duplicateContact.tags);

		}
		//----------------------------------------------------------------------------------------------------
		//special case:
		//if we have conflicting emails, try to put them to email2,email3 fields(and the rest of emails
		// like 4rth, 5th to notes section
		if ((dupeSetType == "Has_Conflict") && _hasConflictingEmailField) {
			diffEmails = cc_unique(diffEmails);
			for (var n = 0; n < diffEmails.length; n++) {
				if (n == 0)
					newAttrs["email"] = diffEmails[n];
				else if (n == 1)
					newAttrs["email2"] = diffEmails[n];
				else if (n == 2)
					newAttrs["email3"] = diffEmails[n];
				else {
					if (newAttrs["notes"] == undefined)
						newAttrs["notes"] = "";

					newAttrs["notes"] = newAttrs["notes"] + "\r\n--ADDITIONAL INFO FROM DUPLICATE CONTACT--\r\n Email: " + diffEmails[n];
				}

			}
		}
		//----------------------------------------------------------------------------------------------------

		var modifyContactReq = soapDoc.set("ModifyContactRequest", null, null, "urn:zimbraMail");
		modifyContactReq.setAttribute("replace", "0");
		modifyContactReq.setAttribute("force", "1");
		var doc = soapDoc.getDoc();
		var cn = doc.createElement("cn");
		cn.setAttribute("id", dupesArry[0].id);
		modifyContactReq.appendChild(cn);
		for (var name in newAttrs) {
			if (name == ZmContact.F_folderId)
				continue;
			var a = soapDoc.set("a", newAttrs[name], cn);
			a.setAttribute("n", name);
		}
		modifyContactReq.appendChild(cn);

		//======================================================================
		//Preserve Tags..
		//======================================================================
		if (tagsArry.length >= 1)
			tagsArry = cc_unique(tagsArry);

		for (var n = 0; n < tagsArry.length; n++) {
			var contactActionReq2 = soapDoc.set("ContactActionRequest", null, null, "urn:zimbraMail");
			var doc2 = soapDoc.getDoc();
			var action2 = soapDoc.set("action");
			action2.setAttribute("op", "tag");
			action2.setAttribute("id", dupesArry[0].id);
			action2.setAttribute("tag", tagsArry[n]);
			contactActionReq2.appendChild(action2);
		}


	}


	//When the current set doesnt have any match(say, user has checked partial-match-only and the current set
	//(60 or70 vectors) doesnt match it, then there wont be a soap-call and hence no call to handler for recurrsion
	//So, in such cases, directly call the handler.
	if (!this._matchfound) {
		this._handleResponseDeleteDuplicates();
		this._matchfound = false;
	}

	if (soapDoc) {
		// finally, send the BatchRequest to the server
		var respCallback = new AjxCallback(this, this._handleResponseDeleteDuplicates);
		appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:respCallback});

		var msg = "Processing contacts " + (this.startAt + 1) + " to " + (this.endAt + 1);
		this.setProgressbarMsg(msg);
		this.conOrgDialog.popdown();
		if (!this.pbDialog.runInBackground)
			this.pbDialog.popup();
	}


};


// Callbacks.....
com_zimbra_contactcleaner.prototype._handleResponseDeleteDuplicates =
function(result) {	
	if (this.abortProcess) {
		var msg = "Process was aborted! " + (this.endAt + 1)  + " contacts were processed! ";
		this.setProgressbarAbortMsg(msg);
		this._showNotesDlg(msg);
	} else if (this.endAt != this.dupesVectorSize - 1) {
		document.getElementById("cc_processbarForeground").style.width = parseInt(this.getProgressbarLen() * (this.endAt / this.dupesVectorSize)) + "px";
		this.startAt = this.endAt + 1;
		this.endAt = this.endAt + this.PROCESS_AT_ONCE;
		this.dupeContactCurrentCnt = this.dupeContactNextCnt;
		this.concleanerDlgDeleteListner();
	} else {
		var msg = "Successfully completed processing " + this.dupesVectorSize + " contacts!";
		this.setProgressbarComplete(msg);
		this._showNotesDlg(msg);
	}
};

com_zimbra_contactcleaner.prototype._showNotesDlg =
function(mainMsg) {
	var notes =["<b>",mainMsg,"</b><br/><br/><b>Notes:</b>",
		" <br/>1. Please log out and log back in.",
		" <br/>2. Verify if contacts are merged properly and duplicates are moved to Trash",
		"<br/>3. If things are fine, you may have to then empty Trash to permanently remove duplicate contacts from Trash",
		" to not see them in auto-complete fields like in To or Cc compose fields"].join("");
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();//reset dialog since we could be using it
	dlg.setMessage(notes, DwtMessageDialog.INFO_STYLE);
	dlg.popup();
};
//-------------------------------- PROCESS DUPLICATE CONTACTS (END) -------------------------------------------

//-------------------------------------- HELPER FUNCTIONS(START) ----------------------------------------------
//create closure..
function cc_createClosure(object, method) {
	var shim = function() {
		method.apply(object, arguments);
	}
	return shim;
}

com_zimbra_contactcleaner.arrayContainsElement =
function(array, val) {
	for (var i = 0; i < array.length; i++) {
		if (array[i] == val) {
			return true;
		}
	}
	return false;
}
com_zimbra_contactcleaner.arrayContainsKey =
function(array, key) {
	for (var currentKey in array) {
		if (currentKey == key) {
			return true;
		}
	}
	return false;
}

function cc_unique(b) {
	var a = [], i, l = b.length;
	for (i = 0; i < l; i++) {
		if (!com_zimbra_contactcleaner.arrayContainsElement(a, b[i])) {
			a.push(b[i]);
		}
	}
	return a;
}


function cc_expandcollapse(postid) {
	var whichpost = document.getElementById(postid);
	if (whichpost.className == "cc_shown") {
		whichpost.className = "cc_hidden";
	} else {
		whichpost.className = "cc_shown";
	}
}
com_zimbra_contactcleaner.prototype.expandcollapse_all = function() {
	if (this.dupesSetIdArray) {
		for (var i = 0; i < this.dupesSetIdArray.length; i++) {
			if (this._AllContactsArecc_shown) {
				document.getElementById(this.dupesSetIdArray[i]).className = "cc_hidden";
			} else {
				document.getElementById(this.dupesSetIdArray[i]).className = "cc_shown";
			}
		}
		this._AllContactsArecc_shown = !this._AllContactsArecc_shown;

	}
}


function sortContactsBy_E11(a, b) {
	var x = a._email;
	var y = b._email;
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}
function sortContactsBy_E12(a, b) {
	var x = a._email;
	var y = b._email2;
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}
function sortContactsBy_E13(a, b) {
	var x = a._email;
	var y = b._email3;
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortContactsBy_E21(a, b) {
	var x = a._emai12;
	var y = b._email;
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}
function sortContactsBy_E22(a, b) {
	var x = a._emai12;
	var y = b._email2;
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}
function sortContactsBy_E23(a, b) {
	var x = a._emai12;
	var y = b._email3;
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}
function sortContactsBy_E31(a, b) {
	var x = a._emai13;
	var y = b._email;
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}
function sortContactsBy_E32(a, b) {
	var x = a._emai13;
	var y = b._email2;
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}


function sortContactsBy_E33(a, b) {
	var x = a._emai13;
	var y = b._email3;
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}
function sortContactsBy_FN(a, b) {
	var x = (a._firstName) + (a._lastName);
	var y = (b._firstName) + (b._lastName);
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}


com_zimbra_contactcleaner.prototype.handleMergeChkbox =
function() {

	if (document.getElementById("cc_dupesWithConflictsChkbox_id").checked) {
		document.getElementById("cc_reviewConflctInfo").style.color = "red";
		document.getElementById("cc_reviewConflctInfo").className = "cc_reviewConflt";
	} else {
		document.getElementById("cc_reviewConflctInfo").className = "cc_hidden";
	}
};
com_zimbra_contactcleaner.prototype.handleMergePartialChkbox =
function() {

	if (document.getElementById("cc_dupesWithPartialMatchChkbox_id").checked) {
		document.getElementById("cc_reviewPartialInfo").style.color = "orange";
		document.getElementById("cc_reviewPartialInfo").className = "cc_reviewConflt";
	} else {
		document.getElementById("cc_reviewPartialInfo").className = "cc_hidden";
	}
};

com_zimbra_contactcleaner.prototype.parseSelection =
function() {
	this._mergePerfectMatchedChk = document.getElementById("cc_dupesWithPerfectMatchChkbox_id").checked;
	this._mergePartiallyMatchedChk = document.getElementById("cc_dupesWithPartialMatchChkbox_id").checked;
	this.mergeConflictsChk = document.getElementById("cc_dupesWithConflictsChkbox_id").checked;
	//this.mergeAndAddNoteChk = document.getElementById("MergeAndAddNote").checked;

};
//-------------------------------------- HELPER FUNCTIONS(END) ----------------------------------------------


//-------------------------------------- PROGRESS BAR RELATED(START) ----------------------------------------------
//Progressbar related listners...
com_zimbra_contactcleaner.prototype.constructContactProgressbarView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<TABLE width=100%>";
	html[i++] = "<TR><TD width=\"1%\"><DIV id=\"cc_processBusy\" class=\"cc_shown\"><img   src=\"" + this.getResource("cc_busy.gif") + "\"  /></DIV></TD>";
	html[i++] = "<TD ALIGN=LEFT><FONT size=2><DIV id=\"cc_pbMsgDiv\"></DIV><FONT></TD></TR></TABLE>";
	html[i++] = "<DIV class = \"cc_processbarBackground\"></DIV>";
	html[i++] = "<DIV class = \"cc_processbarForeground\" id=\"cc_processbarForeground\"></DIV>";
	return html.join("");
};

com_zimbra_contactcleaner.prototype.getProgressbarLen =
function() {
	return this.pblen;
};

com_zimbra_contactcleaner.prototype.setProgressbarMsg =
function(msg) {
	document.getElementById("cc_pbMsgDiv").innerHTML = msg;
};


com_zimbra_contactcleaner.prototype.setProgressbarComplete =
function(msg, notes) {
	this.pbDialog.getButton("pbAbortBtn").setEnabled(false);
	//disable Abort btn
	this.pbDialog.getButton("pbrunBackgroundBtn").setEnabled(false);
	//disable runInBg btn
	document.getElementById("cc_processbarForeground").style.width = this.getProgressbarLen() + "px";
	document.getElementById("cc_processBusy").className = "cc_hidden";
	document.getElementById("cc_pbMsgDiv").innerHTML = this.formatMsg(msg);


	this.pbDialog.popup();
	this.pbDialog.runInBackground = false;
};

com_zimbra_contactcleaner.prototype.setProgressbarAbortMsg =
function(msg) {
	document.getElementById("cc_processBusy").className = "cc_hidden";
	document.getElementById("cc_pbMsgDiv").innerHTML = this.formatMsg(msg);
};

com_zimbra_contactcleaner.prototype.setProgressbarBegin =
function() {
	document.getElementById("cc_processbarForeground").style.width = "0px";
	document.getElementById("cc_processBusy").className = "cc_shown";
	document.getElementById("cc_pbMsgDiv").innerHTML = "";
};


com_zimbra_contactcleaner.prototype.pbDialogAbortListner =
function(ev) {
	var msg = "Aborting...";
	this.abortProcess = true;
	this.pbDialog.runInBackground = false;
	this.setProgressbarAbortMsg(msg);
	this.pbDialog.getButton("pbAbortBtn").setEnabled(false);
	//disable Abort btn
	this.pbDialog.getButton("pbrunBackgroundBtn").setEnabled(false);
	// disable runInBg btn

}

com_zimbra_contactcleaner.prototype.pbRunBackgroundAbortListner =
function(ev) {
	this.pbDialog.runInBackground = true;
	this.pbDialog.popdown();

}
com_zimbra_contactcleaner.prototype.formatMsg =
function(msg) {
	return  msg;
	//currently no formatting is done.
}
//-------------------------------------- PROGRESS BAR RELATED(END) ----------------------------------------------
