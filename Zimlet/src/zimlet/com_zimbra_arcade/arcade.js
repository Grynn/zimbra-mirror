/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////////////////
// Zimlet to play arcade games
// @author Zimlet author: Parag Shah.
// 		   Arcade games author: Paul Neave (http://www.neave.com/games/
//////////////////////////////////////////////////////////////////////////

function Com_Zimbra_Arcade() {
}

Com_Zimbra_Arcade.prototype = new ZmZimletBase();
Com_Zimbra_Arcade.prototype.constructor = Com_Zimbra_Arcade;

// Called by the Zimlet framework when the game panel item was double clicked
Com_Zimbra_Arcade.prototype.menuItemSelected = 
function(contextMenu, menuItemId, spanElement, contentObjText, canvas) {
	var view = new DwtComposite(this.getShell());
	view.setSize("465", "360");

	// find out which arcade game was selected
	var resource = contextMenu + ".swf";

	var html = new Array();
	var i = 0;

	html[i++] = "<table width=465><tr><td>";
	html[i++] = "<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/Flash/swflash.cab#version=4,0,2,0' width='465' height='360' id='fGame'>";
	html[i++] = "<param name='movie' value='";
	html[i++] = this.getResource(resource);
	html[i++] = "' />";
	html[i++] = "<param name='quality' value='high' />";
	html[i++] = "<param name='menu' value='true' />";
	html[i++] = "<embed id='fGame' src='";
	html[i++] = this.getResource(resource);
	html[i++] = "' width='465' height='360' quality='high' pluginspage='http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash' type='application/x-shockwave-flash' menu='false'>";
	html[i++] = "</embed>";
	html[i++] = "</object>";
	html[i++] = "</td></tr></table>";

	view.getHtmlElement().innerHTML = html.join("");

	// XXX: need public way of doing this!
	var title = "Play " + contextMenu;
	var dlg = this._createDialog({title:title, view:view});
	dlg.popup();
};
