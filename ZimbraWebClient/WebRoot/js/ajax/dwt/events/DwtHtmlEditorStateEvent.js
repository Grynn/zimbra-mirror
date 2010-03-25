/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
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
 * 
 * @private
 */
DwtHtmlEditorStateEvent = function(init) {
	if (arguments.length == 0) return;
	DwtEvent.call(this, true);
	this.reset();
}

DwtHtmlEditorStateEvent.prototype = new DwtEvent;
DwtHtmlEditorStateEvent.prototype.constructor = DwtHtmlEditorStateEvent;

DwtHtmlEditorStateEvent.prototype.toString = 
function() {
	return "DwtHtmlEditorStateEvent";
}

DwtHtmlEditorStateEvent.prototype.reset =
function() {
	this.isBold = null;
	this.isItalic = null;
	this.isUnderline = null;
	this.isStrikeThru = null;
	this.isSuperscript = null;
	this.isSubscript = null;
	this.isOrderedList = null;
	this.isNumberedList = null;
	this.fontName = null;
	this.fontSize = null;
	this.style = null;
	this.backgroundColor = null;
	this.color = null;
	this.justification = null;
	this.direction = null;
}
