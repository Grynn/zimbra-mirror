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
 * Constructor.
 * 
 */
function com_zimbra_bugz_HandlerObject() {
}

com_zimbra_bugz_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_bugz_HandlerObject.prototype.constructor = com_zimbra_bugz_HandlerObject;

/**
 * Called by the framework when generating the span for in-context link.
 * 
 */
com_zimbra_bugz_HandlerObject.prototype.generateSpan =
function(html, idx, obj, spanId, context) {
	// Create an <a> element that links to the bugzilla entry that was matched.
	var c = this.xmlObj("contentObject");
	var actionUrl = c && c.onClick && c.onClick.actionUrl;
	if (actionUrl) {
		var contentObj = this._createContentObj(obj, context);
		var textHtml = [];
		this._getHtmlContent(textHtml, 0, obj, context, spanId);
		var subs = {
			id: spanId,
			className: this.getClassName(obj),
			href: this._zimletContext.makeURL(actionUrl, contentObj),
			text: textHtml
		};
		html[idx++] = AjxTemplate.expand("com_zimbra_bugz.templates.Bugz#Bugz_link", subs);
	}
	return idx;
};

/**
 * Called when the in-context link is clicked.
 * 
 */
com_zimbra_bugz_HandlerObject.prototype.clicked =
function(spanElement, contentObjText, matchContext, event) {
	// Just let the browser handle the click.
	event._stopPropagation = false;
};
