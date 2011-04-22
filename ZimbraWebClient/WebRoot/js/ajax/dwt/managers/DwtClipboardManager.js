/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * @overview
 * This file contains the ClipBoard related classes.
 *
 */

/**
 * Manages clipboard related operations
 * @constructor
 * @class
 * A manager class that can be used within the application
 * for clipboard operations like copy/paste.
 * Not all browsers support clipboard operations dues to security reasons.
 * The clipboard manager class can display a warning and help the user in the copy operation.
 *
 * @author Prashant Jain
 *
 * @param {Array}	buttonInfo		the transfer button IDs and labels
 *
 * @extends		DwtDialog
 */

DwtClipboardManager = function() {};

DwtClipboardManager.prototype.copyToClipboard =
function(text) {
    if (!text) return;

    //we need a temporary TextArea to work with. We will create one if ClipboardWarningDialog.COPY_HELPER_TEXTAREA
    //is null else reuse it.
    //Note: First time ClipboardWarningDialog.COPY_HELPER_TEXTAREA will be null hence we create a new one which will be released the next time if
    //ClipboardWarningDialog is shown even once.
    if (!DwtClipboardManager.COPY_HELPER_TEXTAREA) {DwtClipboardManager.COPY_HELPER_TEXTAREA = document.createElement("textarea")};

    DwtClipboardManager.COPY_HELPER_TEXTAREA.value = text;
    try {
        //as of 2011/04, createTextRange() is supported by IE and Opera only.
        copiedText = DwtClipboardManager.COPY_HELPER_TEXTAREA.createTextRange();
        copiedText.execCommand("Copy");
    } catch (e) {
        DwtClipboardWarningDialog.getInstance().showWarning(text);
    }
}

DwtClipboardManager.getInstance =
function() {
	return DwtClipboardManager.INSTANCE = DwtClipboardManager.INSTANCE || new DwtClipboardManager();
};






DwtClipboardWarningDialog =
function() {
    DwtDialog.call(this, {parent:DwtShell.getShell(window), title:AjxMsg.clipboardWarningTitle, standardButtons:[DwtDialog.CANCEL_BUTTON]});
}
DwtClipboardWarningDialog.prototype = new DwtDialog;
DwtClipboardWarningDialog.prototype.constructor = DwtClipboardWarningDialog;

DwtClipboardWarningDialog.getInstance =
function() {
	return DwtClipboardWarningDialog.INSTANCE = DwtClipboardWarningDialog.INSTANCE || new DwtClipboardWarningDialog();
};

DwtClipboardWarningDialog.prototype.showWarning =
function(text) {
    if (!this.contentCreated) {
        this.setContent(this._contentHtml());
        this._textArea = document.getElementById("clipboardWarningDialog_textarea");
        DwtClipboardManager.COPY_HELPER_TEXTAREA = this._textArea;
        this.contentCreated = true;
    }
    //this._textArea.focus();
    this._textArea.value = text;
    this.popup();
 
    this._textArea.focus();
    this._textArea.select();
}

DwtClipboardWarningDialog.prototype._contentHtml =
function() {
    var html = [];
	var idx = 0;
    var msg1 = AjxMsg.clipboardNotSupportedBrowser;
    var msg2 = AjxEnv.isMac ? AjxMsg.clipboardCopyKeysMac : AjxMsg.clipboardCopyKeys;
    var msg3 = AjxMessageFormat.format(AjxMsg.clipboardCopyAlternate, msg2);
    
	html[idx++] = "<textarea id='clipboardWarningDialog_textarea' style='width:100%; height:100px; min-width:200px'></textarea>";
	html[idx++] = "<div>";
    html[idx++] = msg1 + "<BR><BR>";
    html[idx++] = msg3;
    html[idx++] = "</div>"
	return html.join("");
};

DwtClipboardWarningDialog.prototype.getKeyMapName =
function() {
	return "DwtClipboardWarningDialog";
};

DwtClipboardWarningDialog.prototype.handleKeyAction =
function(actionCode, ev) {
	switch (actionCode) {
		case DwtKeyMap.CANCEL:
            var cancelDialogClosure = AjxCallback.simpleClosure(this.cancelDialog, this, actionCode, ev);
            //we cannot close the warning dialog right away because user might have pressed Ctrl+C.
            //if we close it now, the text will not be copied to the clipboard.
            //Hence, let the text get copied to the clipboard and close the window after 40 milliseconds.
            setTimeout(cancelDialogClosure, 40);
            if ((ev.metaKey || ev.ctrlKey) && ev.keyCode == 67) {
                // user attempted to copy the text by Ctrl+C or Cmd+C.
                // Hence, we return false instead of true because Ctrl+C/Cmd+C is a command
                // that we want the operating system/browser to handle and copy the text to clipBoard.
                return false;
            }
			break;
		default:
			return false;
	}

	return true;
};

DwtClipboardWarningDialog.prototype.cancelDialog =
function(actionCode, evt) {
    DwtDialog.prototype.handleKeyAction.apply(this, arguments);
}

