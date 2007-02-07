/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * This class represents a widget that plays sounds. Do not invoke the constructor
 * directly. Instead use the create() method.
 *
 * @param parent	{DwtControl} Parent widget (required)
 * @param width		{Int} Width of player (required)
 * @param height	{Int} Height of player (required)
 * @param className {string} CSS class. If not provided defaults to the class name (optional)
 * @param positionType {string} Positioning style (absolute, static, or relative). If
 * 		not provided defaults to DwtControl.STATIC_STYLE (optional)
 */
function DwtSoundPlayer(parent, width, height, className, positionType) {
	if (arguments.length == 0) return;
	className = className || "DwtSoundPlayer";
	DwtControl.call(this, parent, className, positionType);
	this._width = width;
	this._height = height;
};

DwtSoundPlayer.prototype = new DwtControl;
DwtSoundPlayer.prototype.constructor = DwtSoundPlayer;

/*
 * Factory method. Cereates an appropriate sound player for whatever pligins are or are not installed.
 * 
 * @param parent	{DwtControl} Parent widget (required)
 * @param width		{Int} Width in pixels. (IE doesn't seem to allow anything other than a fixed width) (optional)
 * @param height	{Int} Width in pixels. (IE doesn't seem to allow anything other than a fixed height) (optional)
 * @param className {string} CSS class. If not provided defaults to the class name (optional)
 * @param positionType {string} Positioning style (absolute, static, or relative). If
 * 		not provided defaults to DwtControl.STATIC_STYLE (optional)
 */
 
// Notes to self
// - I tried doing detection by parsing the <embed> element, then calling methods
//   like element.GetQuickTimeVersion(), but it seems like those methods aren't
//   available till later.
// - Tried setting classid on the embed element. It looks like that approach still
//   uses QT even when I want it to use WMP.
// - Using <object> with a class id forces the right player in IE, not FF
DwtSoundPlayer.create =
function(parent, width, height, className, positionType) {
	width = width || 200;
	height = height || 18;
	
	// See if QuickTime is available.
	if (AjxPluginDetector.detectQuickTime()) {
		return new DwtQTSoundPlayer(parent, width, height, className, positionType);
	}

	// TODO: Check for Windows Media & Real Player
	// See if Windows Media is available.
//	if (AjxPluginDetector.detectWindowsMedia()) {
//		var html = ["<object classid='clsid:6BF52A52-394A-11d3-B153-00C04F79FAA6' id='", Dwt.getNextId(), "' width='", width, "' height='", height, "' autostart='false' type='audio/wav'/>"].join("");
//		var element = Dwt.parseHtmlFragment(html);
//		return new DwtWMSoundPlayer(parent, element, className, positionType);
//	}
	
	return new DwtMissingSoundPlayer(parent, width, height, className, positionType);
};

// "Abstract" methods.
DwtSoundPlayer.prototype.setUrl =
function(url) {
};
DwtSoundPlayer.prototype.play =
function() {
};
DwtSoundPlayer.prototype.pause =
function() {
};
DwtSoundPlayer.prototype.rewind =
function() {
};
DwtSoundPlayer.prototype.setVolume =
function(volume) {
};

//////////////////////////////////////////////////////////////////////////////
// Sound player that goes through the QuickTime (QT) plugin.
//////////////////////////////////////////////////////////////////////////////
function DwtQTSoundPlayer(parent, width, height, className, positionType) {
	if (arguments.length == 0) return;
	className = className || "DwtSoundPlayer";
	DwtSoundPlayer.call(this, parent, width, height, className, positionType);

	this._playerId = Dwt.getNextId();
	this._createHtml("");
};

DwtQTSoundPlayer.prototype = new DwtSoundPlayer;
DwtQTSoundPlayer.prototype.constructor = DwtQTSoundPlayer;

DwtQTSoundPlayer.prototype.toString =
function() {
	return "DwtQTSoundPlayer";
};

DwtQTSoundPlayer.prototype.setUrl =
function(url) {
	var player = this._getPlayer();
	player.SetResetPropertiesOnReload(false); // Prevents autostart from being turned on.
	player.SetURL(url);
};

DwtQTSoundPlayer.prototype.play =
function() {
	var player = this._getPlayer();
	player.Play();
};

DwtQTSoundPlayer.prototype.pause =
function() {
	var player = this._getPlayer();
	player.Stop();
};

DwtQTSoundPlayer.prototype.rewind =
function() {
	var player = this._getPlayer();
	player.Rewind();
};

// Appears to be a scale of 0-256.
DwtQTSoundPlayer.prototype.setVolume =
function(volume) {
	var player = this._getPlayer();
	player.SetVolume(volume);
};

// Here's where I tried to write methods to detect if the sound file
// didn't load successfully. Like practically everything else I try with
// QT, it doesn't work. player.GetPluginStatus() always returns the status
// of the first sound it loaded. Calls to SetResetPropertiesOnReload don't
// affect that behavior.
//
// The worst thing about this is that when a sound file fails to load, QT
// refuses to load any other sounds, and I have no way of detecting that
// something is wrong.
//
// I tried creating a new <embed> for each sound we play, but that caused
// a nasty flicker.
//
// In IE, at least you can detect there was an error. The player stops working
// but since I know there's a problem I could just create a new player.
/*
DwtQTSoundPlayer.prototype._beginStatusCheck =
function() {
	if (!this._checkStatusAction) {
		this._checkStatusAction = new AjxTimedAction(this, this._checkStatus);
	}
	AjxTimedAction.scheduleAction(this._checkStatusAction, 250);
};

DwtQTSoundPlayer.prototype._checkStatus =
function() {
	var player = this._getPlayer();
	var status = player.GetPluginStatus();
	if (status != "Complete") {
		// Do something
		document.title = player.GetURL() + ": " + status;
		
		// Check again
		this._beginStatusCheck();
	}
};
*/

DwtQTSoundPlayer.prototype._createHtml =
function() {
	var html = [
		"<embed classid='clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B' ",
		"id='", this._playerId, 
		"' width='", this._width, 
		"' height='", this._height, 
		"' src='", "../../public/SoundPlayer/Silent.wav", 
		"' autostart='false'  enablejavascript='true' type='audio/wav'/>"];
	this.getHtmlElement().innerHTML = html.join("");
};

DwtQTSoundPlayer.prototype._getPlayer =
function() {
	return document.getElementById(this._playerId);
};

/*
//////////////////////////////////////////////////////////////////////////////
// Sound player that goes through the Windows Media (WM) plugin.
//////////////////////////////////////////////////////////////////////////////

// WM Object Model Reference: http://msdn2.microsoft.com/en-us/library/bb249259.aspx
function DwtWMSoundPlayer(parent, element, className, positionType) {
	if (arguments.length == 0) return;
	className = className || "DwtSoundPlayer";
	DwtSoundPlayer.call(this, parent, className, positionType);

	this._playerId = element.id;
	this.getHtmlElement().appendChild(element);
};

DwtWMSoundPlayer.prototype = new DwtSoundPlayer;
DwtWMSoundPlayer.prototype.constructor = DwtWMSoundPlayer;

DwtWMSoundPlayer.prototype.toString =
function() {
	return "DwtWMSoundPlayer";
};

DwtWMSoundPlayer.prototype.setUrl =
function(url) {
	var player = this._getPlayer();
	if (player.URL) {
		player.controls.stop();
	}
	player.URL = url;
};

DwtWMSoundPlayer.prototype.play =
function() {
	var player = this._getPlayer();
	player.controls.play();
};

DwtWMSoundPlayer.prototype.pause =
function() {
	var player = this._getPlayer();
	player.controls.stop();
};

DwtWMSoundPlayer.prototype.rewind =
function() {
	var player = this._getPlayer();
	player.rewind();
};

DwtWMSoundPlayer.prototype.setVolume =
function(volume) {
	Make sure to use the same scale as the other plugins.
	var player = this._getPlayer();
	player.SetVolume(volume);
};

DwtWMSoundPlayer.prototype._getPlayer =
function() {
	return document.getElementById(this._playerId);
};
*/

//////////////////////////////////////////////////////////////////////////////
// Sound player for browsers without a known sound plugin.
//////////////////////////////////////////////////////////////////////////////
function DwtMissingSoundPlayer(parent, width, height, className, positionType) {
	if (arguments.length == 0) return;
	className = className || "DwtSoundPlayer";
	DwtSoundPlayer.call(this, parent, className, positionType);
	
	this.isPluginMissing = true;

    var args = { width: width, height: height };
    this.getHtmlElement().innerHTML = AjxTemplate.expand("ajax.dwt.templates.Widgets#DwtMissingSoundPlayer", args);
    
    this._setMouseEventHdlrs();
};

DwtMissingSoundPlayer.prototype = new DwtSoundPlayer;
DwtMissingSoundPlayer.prototype.constructor = DwtMissingSoundPlayer;

DwtMissingSoundPlayer.prototype.toString =
function() {
	return "DwtMissingSoundPlayer";
};

DwtMissingSoundPlayer.prototype.addHelpListener =
function(listener) {
	this.addListener(DwtEvent.ONMOUSEDOWN, listener);
};
