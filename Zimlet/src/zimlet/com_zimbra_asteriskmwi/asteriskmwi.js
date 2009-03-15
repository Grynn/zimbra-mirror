/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

function Com_Zimbra_AsteriskMWI() {
	//DBG.println(AjxDebug.DBG2, "MWI init");
}

Com_Zimbra_AsteriskMWI.prototype.init =
function() {
	// Pre-load placeholder image
	//DBG.println(AjxDebug.DBG2, "MWI init");
	(new Image()).src = this.getResource('blank_pixel.gif');
};

Com_Zimbra_AsteriskMWI.prototype = new ZmZimletBase();
Com_Zimbra_AsteriskMWI.prototype.constructor = Com_Zimbra_AsteriskMWI;

Com_Zimbra_AsteriskMWI.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
		case "CALL":
			this.singleClicked();
			break;
		case "PREFERENCES":
			if (this._dlg_propertyEditor) {
				this._dlg_propertyEditor.dispose();
			}
			this._dlg_propertyEditor = null;
			this.createPropertyEditor();
			break;
	}
};

// Called by the Zimbra framework when the panel item was double clicked
Com_Zimbra_AsteriskMWI.prototype.doDrop = function(myobj) {
	this.setupCall(myobj);
};

// Called by the Zimbra framework when the panel item was double clicked
Com_Zimbra_AsteriskMWI.prototype.doubleClicked = function() {
	this.singleClicked();
};

Com_Zimbra_AsteriskMWI.prototype.singleClicked = function() {
	this.checkVM();
};

Com_Zimbra_AsteriskMWI.prototype.verifyPrefs = function() {
	return ((this.getUserProperty("pbxUname")) && (this.getUserProperty("pbxPass")));
};

Com_Zimbra_AsteriskMWI.prototype.checkVM = function() {

	if (!this.verifyPrefs()) {
		this.displayStatusMessage("Please populate preferences");
		if (this._dlg_propertyEditor) {
			this._dlg_propertyEditor.dispose();
		}
		this._dlg_propertyEditor = null;
		this.createPropertyEditor();
		return;
	}

	var view = new DwtComposite(this.getShell());
	var dialog_args = {
		view  : view,
		title : "Voicemail"
	};
	// TODO fix args to only have one button "dismiss" or "ok"
	// TODO - I should probably create the dialog in toolTipPoppedUp, since I don't
	// really use it here.
	var dlg = this._createDialog(dialog_args);
	this.dlg = dlg;
	dlg.setButtonListener(DwtDialog.OK_BUTTON,
		      new AjxListener(this, function() {
			      dlg.popdown();
			      dlg.dispose();
		      }));
	dlg.setButtonListener(DwtDialog.CANCEL_BUTTON,
		      new AjxListener(this, function() {
			      dlg.popdown();
			      dlg.dispose();
		      }));
    var el = view.getHtmlElement();
    var div = document.createElement("div");
    el.appendChild(div);
    this.toolTipPoppedUp(div,dlg);

};

// Thanks, Kevin

Com_Zimbra_AsteriskMWI.prototype.toolTipPoppedUp =
function(canvas, dlg) {
	canvas.innerHTML = '<div id="'+ ZmZimletBase.encodeId("asteriskVM")+'" />';

	var requestDoc = "action=login&mailbox="+this.getUserProperty("pbxUname")+"&"+
		"password="+this.getUserProperty("pbxPass");
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(this.getConfig("vmURL"));
	//DBG.println(AjxDebug.DBG2, "Com_Zimbra_AsteriskMWI URL: " + url);
	AjxRpc.invoke(requestDoc, url, null, new AjxCallback(this, Com_Zimbra_AsteriskMWI._callback, [dlg, this]), false);
};

// Private Methods

Com_Zimbra_AsteriskMWI.prototype._displayImage = 
function(dlg, vm_src) {
	var vmEl = document.getElementById(ZmZimletBase.encodeId("asteriskVM"));

	var i;
	var j = 0;
	var msgs = [];
	while (true) {
		i = vm_src.indexOf("msgselect",j);
		if (i == -1) {
			break;
		}
		var curmsg = {
			id			: "",
			from		: "",
			duration	: 0,
			date		: ""
			};

		var k = vm_src.indexOf("<b>",i);
		var l;
		curmsg.id = vm_src.substr(k+3,4);
		
		k = vm_src.indexOf("<td>",k+7); // start of From
		k = k+4;
		l = vm_src.indexOf("</td>",k);	// end of From
		curmsg.from = vm_src.substr(k, l-k);

		k = vm_src.indexOf("<td>",l);	// start of duration
		k = k+4;
		l = vm_src.indexOf("</td>",k);	// end of duration
		curmsg.duration = vm_src.substr(k, l-k);

		k = vm_src.indexOf("<td>",l);	// start of date
		k = k+4;
		l = vm_src.indexOf("</td>",k);	// end of date
		curmsg.date = vm_src.substr(k, l-k);

		j = l;

		msgs[msgs.length] = curmsg;
	}

	this._msgs = msgs;

	var vm_html = new Array();
	j = 0;

	vm_html[j++] = "<table width='500' cellspacing=0 cellpadding=0 border=0>";
	vm_html[j++] = "<tr><td>";
	vm_html[j++] = "<div class='DwtListView-ColHeader'>";
		vm_html[j++] = "<table height='100%' width='100%' cellspacing=0 cellpadding=0 border=0>";
			vm_html[j++] = "<tr>";
				vm_html[j++] = "<td class='DwtListView-Column' width=10>";
				vm_html[j++] = "&nbsp;";
				vm_html[j++] = "</td>";
				vm_html[j++] = "<td class='DwtListView-Column' width=40>";
				vm_html[j++] = "Msg";
				vm_html[j++] = "</td>";
				vm_html[j++] = "<td class='DwtListView-Column' width=200>";
				vm_html[j++] = "From";
				vm_html[j++] = "</td>";
				vm_html[j++] = "<td class='DwtListView-Column' width=50>";
				vm_html[j++] = "Length";
				vm_html[j++] = "</td>";
				vm_html[j++] = "<td class='DwtListView-Column' width=200>";
				vm_html[j++] = "Date";
				vm_html[j++] = "</td>";
			vm_html[j++] = "</tr>";
		vm_html[j++] = "</table>";
	vm_html[j++] = "</div>";
	vm_html[j++] = "</td></tr>";

	for (i = 0; i < msgs.length; i++) {
		vm_html[j++] = "<tr><td>";
			msgs[i].rowID = Dwt.getNextId();
			vm_html[j++] = "<div class='Row' id='";
				vm_html[j++] = msgs[i].rowID;
				vm_html[j++] = "'>";
				vm_html[j++] = "<table width='100%' cellspacing=0 cellpadding=0 border=0>";
					vm_html[j++] = "<tr>";
						vm_html[j++] = "<td width=10>";
						vm_html[j++] = "&nbsp;";
						vm_html[j++] = "</td>";
						vm_html[j++] = "<td width=40>";
						vm_html[j++] = msgs[i].id;
						vm_html[j++] = "</td>";
						vm_html[j++] = "<td width=200>";
						vm_html[j++] = msgs[i].from;
						vm_html[j++] = "</td>";
						vm_html[j++] = "<td width=50>";
						vm_html[j++] = msgs[i].duration;
						vm_html[j++] = "</td>";
						vm_html[j++] = "<td width=200>";
						vm_html[j++] = msgs[i].date;
						vm_html[j++] = "</td>";
					vm_html[j++] = "</tr>";
				vm_html[j++] = "</table>";
			vm_html[j++] = "</div>";
		vm_html[j++] = "</td></tr>";
	}

	this._audioDivID = Dwt.getNextId();

	vm_html[j++] = "<tr><td>";
	vm_html[j++] = "<table width='100%' cellspacing=0 cellpadding=0 border=0>";
	vm_html[j++] = "<tr><td width=10></td><td width=490 id='";
	vm_html[j++] = this._audioDivID;
	vm_html[j++] = "'></td></tr>";
	vm_html[j++] = "</table>";
	vm_html[j++] = "</td></tr>";

	this._controlDivID = Dwt.getNextId();

	vm_html[j++] = "<tr><td>";
	vm_html[j++] = "<table width='100%' cellspacing=0 cellpadding=0 border=0>";
	vm_html[j++] = "<tr><td width=10></td><td width=490 id='";
	vm_html[j++] = this._controlDivID;
	vm_html[j++] = "'></td></tr>";
	vm_html[j++] = "</table>";
	vm_html[j++] = "</td></tr>";

	vm_html[j++] = "</table>";

	vmEl.innerHTML = vm_html.join("");
	vmEl.style.cursor = "pointer";

	var myObjID = AjxCore.assignId(this);
	for (i = 0; i < msgs.length; i++) {
		var myRow = document.getElementById(msgs[i].rowID);
		myRow.msgID = msgs[i].id;
		myRow.myObjID = myObjID;
		Dwt.setHandler(myRow, DwtEvent.ONMOUSEDOWN,
			this._selectListener);
	}

	dlg.popup();
};

Com_Zimbra_AsteriskMWI.prototype._selectListener = 
function(evt) {

	evt = DwtUiEvent.getEvent(evt);
	var target = evt.currentTarget?evt.currentTarget:evt.srcElement;
	var msgID = target.msgID?target.msgID:target.parentNode.parentNode.parentNode.parentNode.msgID;
	var myObjID = target.myObjID?target.myObjID:target.parentNode.parentNode.parentNode.parentNode.myObjID;
	var myObj = AjxCore.objectWithId(myObjID);

	var requestDoc = "play"+msgID+".x=1&play"+msgID+".y=1"+
		"&format=WAV"+
		"&context=default&folder=INBOX"+
		"&mailbox="+
		myObj.getUserProperty("pbxUname")+"&"+
		"password="+myObj.getUserProperty("pbxPass");
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(myObj.getConfig("vmURL"));
	AjxRpc.invoke(requestDoc, url, null, new AjxCallback(myObj, Com_Zimbra_AsteriskMWI._getAudio, [myObj]), false);
};

Com_Zimbra_AsteriskMWI._getAudio = 
function( myobj, result ) {
	var r = result.text;
	var i = r.indexOf ("<embed ");
	var j = r.indexOf ("</embed>");
	var embed = r.substr(i, (j-i)+8);
	i=embed.indexOf("vmail.cgi");
	j=embed.indexOf('"',i);
	var url = embed.substr(i,(j-i));
	var newurl = url.replace("vmail.cgi",this.getConfig("vmURL"));
	embed = embed.replace("width=400","width='100%'");
	embed = embed.replace("height=40","height=16");
	embed = embed.replace("autostart=yes","autostart='false' cache='true'");
	embed = embed.replace(url,ZmZimletBase.PROXY+AjxStringUtil.urlEncode(newurl));

	var mstart = r.indexOf("msgid=");
	mstart = mstart+6;
	var msgID = r.substr(mstart,4); 
	for (i = 0; i < myobj._msgs.length; i++) {
		//var rowEl = document.getElementById(myobj._msgs[i].rowID);
		var mystyle;
		if (msgID == myobj._msgs[i].id) {
			mystyle = "Row-selected";
		} else {
			mystyle = "Row";
		}
		document.getElementById(myobj._msgs[i].rowID).className = mystyle;
	}

	var vmEl = document.getElementById(this._audioDivID);
	vmEl.innerHTML = embed;

	// TODO add delete button
	// TODO add "move to" folder select
	// TODO add "forward to extension" select
	// TODO add "forward to email" select
	// TODO add "change folder" select

};

Com_Zimbra_AsteriskMWI._callback = 
function( dlg, myobj, result ) {
	var r = result.text;
	myobj._displayImage(dlg, r);
	//Com_Zimbra_AsteriskMWI._displayImage(dlg, r);
};
