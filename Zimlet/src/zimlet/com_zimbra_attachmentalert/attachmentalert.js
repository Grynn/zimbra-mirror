/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
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
 * 
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////////////////////
// Zimlet that checks for attach* word in email and also if there is an attachment.
// if the email doesnt have an attachment, throws missing-attachment alret dialog  
// @author Zimlet author: Raja Rao DV(rrao@zimbra.com)
//////////////////////////////////////////////////////////////////////////////

function com_zimbra_attachmentalert() {
}

com_zimbra_attachmentalert.prototype = new ZmZimletBase();
com_zimbra_attachmentalert.prototype.constructor = com_zimbra_attachmentalert;

com_zimbra_attachmentalert.prototype.init =
function() {
	this.turnONAttachmentAlertZimlet = this.getUserProperty("turnONAttachmentAlertZimlet") == "true";
	if (!this.turnONAttachmentAlertZimlet)
		return;
};

com_zimbra_attachmentalert.prototype.emailErrorCheck =
function(mail, boolAndErrorMsgArray) {

	if (!this.turnONAttachmentAlertZimlet)
		return;

	//check if we have some attachments..
	if(mail._filteredFwdAttIds){
		if(mail._filteredFwdAttIds.length > 0)
			return null;//has attachments, dont bother
	}
	if(mail._forAttIds){
		if(mail._forAttIds.length > 0)
			return null;//has attachments, dont bother
	}
	var newMailContent = mail.textBodyContent;

	//handle special case..
	if(newMailContent.indexOf("attachments?") >0 || newMailContent.indexOf("attachment?")>0
		|| newMailContent.indexOf("attachment(s)?")>0) {
	 return;
	}

	var newMailArry =  this._getBeforeAfterArray(newMailContent);
	var newMailLen = newMailArry.length;
	if(newMailLen == 0)
		return;
	
	//origmail is used to see if the 'attach*' is actually from previous mail(replied/fwded)
	var origMailArry = [];
	var origMailLen = 0;
	if(mail.isReplied || mail.isForwarded) {
		origMailArry =  this._getBeforeAfterArray(mail._origMsg.getBodyContent());	
		origMailLen = origMailArry.length;

		var hasAttachmentStr = "";
		for(var i =0; i < newMailLen; i++) {
			hasAttachmentStr = true;
			var newMailStr = newMailArry[i];
			for(var j =0; j < origMailLen; j++) {
				var origMailStr = origMailArry[j];
				if(origMailStr == newMailStr) {
					hasAttachmentStr = false;
					break;
				}
			}
		}
		if(!hasAttachmentStr)
			return null;
	}



	//there is a word "attach*" in new mail but not in old-mail
	return boolAndErrorMsgArray.push({hasError:true, errorMsg:"No Attachment(s) Found. You might have forgotten to attach it. Continue anyway?", zimletName:"com_zimbra_attachmentalert"});
};

com_zimbra_attachmentalert.prototype._getBeforeAfterArray = 
function(content) {
	var arry = content.toLowerCase().split("attach");
	var len = arry.length;
	var beforeAfterTxtArry = new Array();
	if(len == 1)
		return beforeAfterTxtArry;

	for(var i = 0; i < len/2; i=i+2) {
		var beforeTxt = "";
		var afterTxt =  "";
		var txt1 = arry[i];
		var txt2 = arry[i+1];
		//get before and after text of 'attach'
		if(txt1.length >= 5)
			beforeTxt = txt1.substring(txt1.length-6, txt1.length-1);
		if(txt2.length >= 5)
			afterTxt = txt2.substring(0, 4);
		
		if(txt2.indexOf("?") ==-1)//ignore questions(use txt2 for complete string)
			beforeAfterTxtArry.push(beforeTxt+afterTxt);//store 5-chars before and 5-char after 'attach'
	}

	return beforeAfterTxtArry;
}

com_zimbra_attachmentalert.prototype.doubleClicked = function() {
	this.singleClicked();
};


com_zimbra_attachmentalert.prototype.singleClicked = function() {
    this.showPrefDialog();
};

com_zimbra_attachmentalert.prototype.showPrefDialog =
function() {
    //if zimlet dialog already exists...
    if (this.pbDialog) {
        this.pbDialog.popup();
        return;
    }
    this.pView = new DwtComposite(this.getShell());
    this.pView.getHtmlElement().innerHTML = this.createPrefView();

    if (this.getUserProperty("turnONAttachmentAlertZimlet") == "true") {
        document.getElementById("turnONAttachmentAlertZimlet_chkbx").checked = true;
    }

    this.pbDialog = this._createDialog({title:"'Attachment Alert' Zimlet Preferences", view:this.pView, standardButtons:[DwtDialog.OK_BUTTON]});
    this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
    this.pbDialog.popup();

};


com_zimbra_attachmentalert.prototype.createPrefView =
function() {
    var html = new Array();
    var i = 0;
    html[i++] = "<DIV>";
    html[i++] = "<input id='turnONAttachmentAlertZimlet_chkbx'  type='checkbox'/>Enable 'Attachment Alert' Zimlet (Changing this would refresh browser)";
    html[i++] = "</DIV>";
    return html.join("");

};

com_zimbra_attachmentalert.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
    if (document.getElementById("turnONAttachmentAlertZimlet_chkbx").checked) {
		if(!this.turnONAttachmentAlertZimlet){
			this._reloadRequired = true;
		}
        this.setUserProperty("turnONAttachmentAlertZimlet", "true", true);
    } else {
        this.setUserProperty("turnONAttachmentAlertZimlet", "false", true);
		if(this.turnONAttachmentAlertZimlet)
			this._reloadRequired = true;
    }
    this.pbDialog.popdown();

	if(this._reloadRequired) {
		window.onbeforeunload = null;
		var url = AjxUtil.formatUrl({});
		ZmZimbraMail.sendRedirect(url);
	}
};