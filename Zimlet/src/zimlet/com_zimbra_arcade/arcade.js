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
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
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

Com_Zimbra_Arcade.prototype.init = 
function() {
	if (ZmAssistant && ZmAssistant.register) ZmAssistant.register(new Com_Zimbra_Arcade_Asst(this._appCtxt));
};

// Called by the Zimlet framework when the game panel item was double clicked
Com_Zimbra_Arcade.prototype.menuItemSelected = 
function(contextMenu, menuItemId, spanElement, contentObjText, canvas) {
	this.showDialog(contextMenu);
};

Com_Zimbra_Arcade.prototype.showDialog = 
function(game) {
	var view = new DwtComposite(this.getShell());
	view.setSize("465", "360");

	// find out which arcade game was selected
	var resource = game + ".swf";

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
	var title = "Play " + game;
	var dlg = this._createDialog({title:title, view:view});
	var buttonListener = new AjxListener(this, this._buttonListener);
	dlg.setButtonListener(DwtDialog.OK_BUTTON, buttonListener);
	dlg.setButtonListener(DwtDialog.CANCEL_BUTTON, buttonListener);
	dlg.popup();
};

Com_Zimbra_Arcade.prototype._buttonListener = 
function(ev) {
	// clear out the dialog so the flash game dies (kills the sound)
	ev.item.parent._contentDiv.innerHTML = "";
	ev.item.parent.popdown();
};



//////////////////////////////////////////////////////////////////////////
// Zimlet assistant class
// - used by the Assistant dialog to run games via "command-line"
//////////////////////////////////////////////////////////////////////////
function Com_Zimbra_Arcade_Asst(appCtxt) {
	if (arguments.length == 0) return;
	// XXX: localize later (does NOT belong in ZmMsg.properties)
	ZmAssistant.call(this, appCtxt, "Play Game", "play");

	this._validGames = ["asteroids", "frogger", "hexxagon", "pacman",
						"simon", "snake", "space invaders",
						"tetris", "tictactoe"] ;

};

Com_Zimbra_Arcade_Asst.prototype = new ZmAssistant();
Com_Zimbra_Arcade_Asst.prototype.constructor = Com_Zimbra_Arcade_Asst;


Com_Zimbra_Arcade_Asst.prototype.okHandler =
function(dialog) {
	// get reference to the arcade zimlet
	var zm = this._appCtxt.getZimletMgr();
	var arcadeZimlet = zm ? zm._ZIMLETS_BY_ID["com_zimbra_arcade"] : null;

	if (arcadeZimlet && this._game) {
		arcadeZimlet.handlerObject.showDialog(this._game);
	}

	// return true to close the assistant dialog
	return true;
};

Com_Zimbra_Arcade_Asst.prototype.handle =
function(dialog, verb, args) {
	
	this._game = null;

	var matched = ZmAssistant.matchWord(args, this._validGames);
	var isValidGame = matched.length == 1;
	if (isValidGame) this._game = matched[0];
	if (matched.length == 0) matched = this._validGames;
	
	var games = matched.join(", ");
	this._setField("Game", games, !isValidGame, true);	
	dialog._setOkButton(AjxMsg.ok, true, isValidGame);
};

