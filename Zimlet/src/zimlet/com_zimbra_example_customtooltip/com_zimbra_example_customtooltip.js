/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Defines the Zimlet handler class.
 *   
 */
function com_zimbra_example_customtooltip_HandlerObject() {
}

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_customtooltip_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_customtooltip_HandlerObject.prototype.constructor = com_zimbra_example_customtooltip_HandlerObject;

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_customtooltip_HandlerObject.prototype.init =
function() {
};

/**
 * This method is called when the tool tip is popped-up.
 * 
 * @param	{Object}	spanElement		the element
 * @param	{String}	contentObjText		the content object text
 * @param	{Hash}	matchContent		the match content (matchContent[0], matchContent.index, matchContent.input)
 * @param	{Object}	canvas			the canvas
 */
com_zimbra_example_customtooltip_HandlerObject.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContent, canvas) {
	
	// generate the HTML
	var html = new Array();
	var i = 0;
	html[i++] = "<table cellpadding=2 cellspacing=0 border=0>";
	html[i++] = ["<tr valign='center'>", "<td><b>CUSTOM TOOL TIP</b></td>", "</tr>"].join("");
	html[i++] = ["<tr valign='center'>", "<td><div style='white-space:nowrap'><b>contentObjText = </b>", contentObjText, "</div>", "</td></tr>"].join("");
	html[i++] = ["<tr valign='center'>", "<td><div style='white-space:nowrap'><b>matchContent[0] = </b>", matchContent[0], "</div>", "</td></tr>"].join("");
	html[i++] = ["<tr valign='center'>", "<td><div style='white-space:nowrap'><b>matchContent.index = </b>", matchContent.index, "</div>", "</td></tr>"].join("");
	html[i++] = ["<tr valign='center'>", "<td><div style='white-space:nowrap'><b>matchContent.input = </b>", matchContent.input, "</div>", "</td></tr>"].join("");
	html[i++] = ["</table>"].join("");
	
	// write the HTML to the tool tip canvas
	canvas.innerHTML = html.join("");
};


