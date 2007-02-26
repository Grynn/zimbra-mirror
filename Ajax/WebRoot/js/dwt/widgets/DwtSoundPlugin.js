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

// TODO:
//- Work on when to turn off the status monitor loop
//- Apply the volume menu to each new plugin
//- Use more generic status code.
//- Make player skinnable.

/**
 * This class represents a widget that plays sounds. It uses a plugin such as Quick Time
 * or Windows Media to play the sounds and to display player controls. Do not invoke the
 * constructor directly. Instead use the create() method, which will choose the right
 * concrete class based on available plugins.
 *
 * @param parent	{DwtControl} Parent widget (required)
 * @param width		{Int} Width of player (required)
 * @param height	{Int} Height of player (required)
 * @param offscreen	{Boolean} If true, the player is initially offscreen. Use an appropriate position style
 * 							  if you set this to true. (This reduces flicker, and a tendency for the QT player 
 * 							  to float in the wrong place when it's first created) (optional)
 * @param className {string} CSS class. If not provided defaults to the class name (optional)
 * @param positionType {string} Positioning style (absolute, static, or relative). If
 * 		not provided defaults to DwtControl.STATIC_STYLE (optional)
 */
function DwtSoundPlugin(parent, width, height, offscreen, className, positionType) {
	if (arguments.length == 0) return;
	className = className || "DwtSoundPlugin";
	DwtControl.call(this, parent, className, positionType);
	this._width = width;
	this._height = height;
	if (offscreen) {
		this.setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	}
};

DwtSoundPlugin.prototype = new DwtControl;
DwtSoundPlugin.prototype.constructor = DwtSoundPlugin;

// Status codes.
DwtSoundPlugin.WAITING = 1;
DwtSoundPlugin.LOADING = 2;
DwtSoundPlugin.PLAYABLE = 3;
DwtSoundPlugin.COMPLETE = 4;
DwtSoundPlugin.ERROR = 5;

/**
 * Factory method. Creates an appropriate sound player for whatever plugins are or are not installed.
 * 
 * @param parent	{DwtControl} Parent widget (required)
 * @param width		{Int} Width in pixels. (IE doesn't seem to allow anything other than a fixed width) (optional)
 * @param height	{Int} Width in pixels. (IE doesn't seem to allow anything other than a fixed height) (optional)
 * @param offscreen	{Boolean} If true, the player is initially offscreen. Use an appropriate position style
 * 							  if you set this to true. (This reduces flicker, and a tendency for the QT player 
 * 							  to float in the wrong place when it's first created) (optional)
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
DwtSoundPlugin.create =
function(parent, width, height, offscreen, className, positionType, url) {
	width = width || 200;
	height = height || 18;
	
	// See if QuickTime is available.
	if (AjxPluginDetector.detectQuickTime()) {
		return new DwtQTSoundPlugin(parent, width, height, offscreen, className, positionType, url);
	}

	// TODO: Check for Windows Media & Real Player
	// See if Windows Media is available.
//	if (AjxPluginDetector.detectWindowsMedia()) {
//		var html = ["<object classid='clsid:6BF52A52-394A-11d3-B153-00C04F79FAA6' id='", Dwt.getNextId(), "' width='", width, "' height='", height, "' autostart='false' type='audio/wav'/>"].join("");
//		var element = Dwt.parseHtmlFragment(html);
//		return new DwtWMSoundPlugin(parent, element, className, positionType);
//	}
	
	return new DwtMissingSoundPlugin(parent, width, height, offscreen, className, positionType);
};

// "Abstract" methods.
DwtSoundPlugin.prototype.play =
function() {
};
DwtSoundPlugin.prototype.pause =
function() {
};
DwtSoundPlugin.prototype.rewind =
function() {
};
DwtSoundPlugin.prototype.setTime =
function(time) {
};
DwtSoundPlugin.prototype.setVolume =
function(volume) {
};
// Fills in the event with status information.
DwtSoundPlugin.prototype._resetEvent =
function(event) {
};

/**
 * Adds a change listener to monitor the status of the sound being played.
 * The listener will be passed an event object with the following fields:
 * - status, a constant representing the loaded state of the sound
 * - duration, the length of the sound
 * - time, the current time of the sound
 * @param listener	{AjxListener} listener object
 */
DwtSoundPlugin.prototype.addChangeListener =
function(listener) {
    this.addListener(DwtEvent.ONCHANGE, listener);
    this._monitorStatus();
};

DwtSoundPlugin.prototype._monitorStatus =
function() {
	if (!this._statusAction) {
		this._statusAction = new AjxTimedAction(this, this._checkStatus);
	}
	this._statusActionId = AjxTimedAction.scheduleAction(this._statusAction, 250);
};

DwtSoundPlugin.prototype._ignoreStatus =
function() {
	if (this._statusActionId) {
		AjxTimedAction.cancelAction(this._statusActionId);
	}
};

DwtSoundPlugin.prototype._checkStatus =
function() {
	this._statusActionId = 0;
	if (!this._changeEvent) {
		this._changeEvent = new DwtEvent(true);
		this._changeEvent.dwtObj = this;
	}
	var status = this._resetEvent(this._changeEvent);
    this.notifyListeners(DwtEvent.ONCHANGE, this._changeEvent);
	this._monitorStatus();
};

//////////////////////////////////////////////////////////////////////////////
// Sound player that goes through the QuickTime (QT) plugin.
//////////////////////////////////////////////////////////////////////////////
function DwtQTSoundPlugin(parent, width, height, offscreen, className, positionType, url) {
	if (arguments.length == 0) return;
	className = className || "DwtSoundPlugin";
	DwtSoundPlugin.call(this, parent, width, height, offscreen, className, positionType);

	this._playerId = Dwt.getNextId();
	this._createHtml(url);
};

DwtQTSoundPlugin.prototype = new DwtSoundPlugin;
DwtQTSoundPlugin.prototype.constructor = DwtQTSoundPlugin;

DwtQTSoundPlugin.prototype.toString =
function() {
	return "DwtQTSoundPlugin";
};

DwtQTSoundPlugin.prototype.play =
function() {
	var player = this._getPlayer();
	player.Play();
};

DwtQTSoundPlugin.prototype.pause =
function() {
	var player = this._getPlayer();
	player.Stop();
};

DwtQTSoundPlugin.prototype.rewind =
function() {
	var player = this._getPlayer();
	player.Rewind();
};

DwtSoundPlugin.prototype.setTime =
function(time) {
	var player = this._getPlayer();
	player.SetTime(time);
};

// Appears to be a scale of 0-256.
DwtQTSoundPlugin.prototype.setVolume =
function(volume) {
	var player = this._getPlayer();
	player.SetVolume(volume);
};

DwtQTSoundPlugin.prototype._resetEvent =
function(event) {
	var player = this._getPlayer();
	if (!player) {
		// This seems weird, but it happens when a sound first starts up.
		// Make up some status data.
		event.status = DwtSoundPlugin.WAITING;
		event.time = 0;
		event.duration = 100;
	} else {
		var status = player.GetPluginStatus();
		switch (status) {
			case "Waiting": event.status = DwtSoundPlugin.WAITING; break;
			case "Loading": event.status = DwtSoundPlugin.LOADING; break;
			case "Playable": event.status = DwtSoundPlugin.PLAYABLE; break;
			case "Complete": event.status = DwtSoundPlugin.COMPLETE; break;
			default : event.status = DwtSoundPlugin.ERROR; event.errorDetail = status; break;
		}
		event.time = player.GetTime();
		event.duration = player.GetDuration();
	}
};

DwtQTSoundPlugin.prototype._createHtml =
function(url) {
	var html = [
		"<embed classid='clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B' ",
		"id='", this._playerId, 
		"' width='", this._width, 
		"' height='", this._height, 
		"' src='", url, 
		"' enablejavascript='true' type='audio/wav'/>"
	];
	this.getHtmlElement().innerHTML = html.join("");
};

DwtQTSoundPlugin.prototype._getPlayer =
function() {
	return document.getElementById(this._playerId);
};

/*
//////////////////////////////////////////////////////////////////////////////
// Sound player that goes through the Windows Media (WM) plugin.
//////////////////////////////////////////////////////////////////////////////

// WM Object Model Reference: http://msdn2.microsoft.com/en-us/library/bb249259.aspx
function DwtWMSoundPlugin(parent, element, className, positionType) {
	if (arguments.length == 0) return;
	className = className || "DwtSoundPlugin";
	DwtSoundPlugin.call(this, parent, className, positionType);

	this._playerId = element.id;
	this.getHtmlElement().appendChild(element);
};

DwtWMSoundPlugin.prototype = new DwtSoundPlugin;
DwtWMSoundPlugin.prototype.constructor = DwtWMSoundPlugin;

DwtWMSoundPlugin.prototype.toString =
function() {
	return "DwtWMSoundPlugin";
};

DwtWMSoundPlugin.prototype.play =
function() {
	var player = this._getPlayer();
	player.controls.play();
};

DwtWMSoundPlugin.prototype.pause =
function() {
	var player = this._getPlayer();
	player.controls.stop();
};

DwtWMSoundPlugin.prototype.rewind =
function() {
	var player = this._getPlayer();
	player.rewind();
};

DwtWMSoundPlugin.prototype.setVolume =
function(volume) {
	Make sure to use the same scale as the other plugins.
	var player = this._getPlayer();
	player.SetVolume(volume);
};

DwtWMSoundPlugin.prototype._getPlayer =
function() {
	return document.getElementById(this._playerId);
};
*/

//////////////////////////////////////////////////////////////////////////////
// Sound player for browsers without a known sound plugin.
//////////////////////////////////////////////////////////////////////////////
function DwtMissingSoundPlugin(parent, width, height, offscreen, className, positionType) {
	if (arguments.length == 0) return;
	className = className || "DwtSoundPlugin";
	DwtSoundPlugin.call(this, parent, width, height, offscreen, className, positionType);
	
	this.isPluginMissing = true;

    var args = { width: width, height: height };
    this.getHtmlElement().innerHTML = AjxTemplate.expand("ajax.dwt.templates.Widgets#DwtMissingSoundPlugin", args);
    
    this._setMouseEventHdlrs();
};

DwtMissingSoundPlugin.prototype = new DwtSoundPlugin;
DwtMissingSoundPlugin.prototype.constructor = DwtMissingSoundPlugin;

DwtMissingSoundPlugin.prototype.toString =
function() {
	return "DwtMissingSoundPlugin";
};

DwtMissingSoundPlugin.prototype.addHelpListener =
function(listener) {
	this.addListener(DwtEvent.ONMOUSEDOWN, listener);
};
