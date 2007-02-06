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
 * This class represents a widget that plays sounds.
 *
 * @param parent	{DwtControl} Parent widget (required)
 * @param className {string} CSS class. If not provided defaults to the class name (optional)
 * @param positionType {string} Positioning style (absolute, static, or relative). If
 * 		not provided defaults to DwtControl.STATIC_STYLE (optional)
 */


function DwtSoundPlayer(parent, className, positionType) {
	if (arguments.length == 0) return;
	className = className || "DwtSoundPlayer";
	DwtComposite.call(this, parent, className, positionType);

	this._soundManager = soundManager;
	this._soundManager.createMovie();
	AjxTimedAction.scheduleAction(new AjxTimedAction(this, this._delayedInit), 1);
	this._state = DwtSoundPlayer.STOPPED;
	this._soundUrl = null;
	this._onFinishClosure = AjxCallback.simpleClosure(this._onFinish, this);
	this._whilePlayingClosure = AjxCallback.simpleClosure(this._whilePlaying, this);
	this._volume = 50; // Volume is measured on a scale of 0..100

	this._createHtml();
	this.setEnabled(false);
};

DwtSoundPlayer.prototype = new DwtComposite;
DwtSoundPlayer.prototype.constructor = DwtSoundPlayer;

DwtSoundPlayer.prototype.toString =
function() {
	return "DwtSoundPlayer";
};

// Can this be changed to just playing/not, and using some cursor position
// to determine stopped vs. paused?
DwtSoundPlayer.STOPPED = 0;
DwtSoundPlayer.PLAYING = 1;
DwtSoundPlayer.PAUSED = 2;

DwtSoundPlayer.prototype.setSound =
function(sound) {
	this.stop();
	this._soundUrl = sound;
	this.setEnabled(sound != null);
};

DwtSoundPlayer.prototype.play =
function() {
	if (this._state == DwtSoundPlayer.STOPPED) {
		var args = {
			id: this._soundUrl, 
			url: this._soundUrl, 
			volume: this._volume,
			onfinish: this._onFinishClosure,
			whileplaying: this._whilePlayingClosure
		};
		this._soundManager.play(this._soundUrl, args);
		this._setState(DwtSoundPlayer.PLAYING);
	} else if (this._state == DwtSoundPlayer.PAUSED) {
		this._soundManager.resume(this._soundUrl);
		this._setState(DwtSoundPlayer.PLAYING);
	} // else just keep playing
};

DwtSoundPlayer.prototype.pause =
function() {
	if (this._state == DwtSoundPlayer.PLAYING) {
		this._soundManager.pause(this._soundUrl);
		this._setState(DwtSoundPlayer.PAUSED);
	}
};

DwtSoundPlayer.prototype.stop =
function() {
	if (this._state == DwtSoundPlayer.PLAYING || this._state == DwtSoundPlayer.PAUSED) {
		this._soundManager.stop(this._soundUrl);
		this._setState(DwtSoundPlayer.STOPPED);
	}
};

DwtSoundPlayer.prototype.rewind =
function() {
	if (this._state == DwtSoundPlayer.PLAYING || this._state == DwtSoundPlayer.PAUSED) {
		this._getSound().setPosition(0);		
	}
};

DwtSoundPlayer.prototype.setVolume =
function(volume) {
	this._volume = volume;
	var sound = this._getSound();
	if (sound) {
		sound.setVolume(volume);
	}
};

DwtSoundPlayer.prototype.setEnabled =
function(enabled) {
	DwtComposite.prototype.setEnabled.call(this, enabled);
	this._playButton.setEnabled(enabled);
	this._pauseButton.setEnabled(enabled);
	this._position.setEnabled(enabled);
};

// Returns the SMSound that is currently loaded, or null if none are loaded.
DwtSoundPlayer.prototype._getSound =
function() {
	return this._soundManager.sounds[this._soundUrl];
};


DwtSoundPlayer.prototype._updatePosition =
function(percent) {
	this._position.setValue(percent);
};

DwtSoundPlayer.prototype._onFinish =
function() {
	this._updatePosition(100);
	this._setState(DwtSoundPlayer.STOPPED);
};

DwtSoundPlayer.prototype._whilePlaying =
function() {
	var sound = this._getSound();
	var percent = (sound.position / sound.duration) * 100;
	this._updatePosition(percent);
};

DwtSoundPlayer.prototype._setState =
function(state) {
	this._state = state;
	this._playButton.setToggled(state == DwtSoundPlayer.PLAYING);
	this._pauseButton.setToggled(state == DwtSoundPlayer.PAUSED);
};

DwtSoundPlayer.prototype._delayedInit =
function() {
	this._soundManager.init();
	if (this._soundManager._disabled) {
		// TODO: I think this is where I handle a flashless  browser.
	}
};

DwtSoundPlayer.prototype._createHtml =
function() {
	var element = this.getHtmlElement();
    var id = this._htmlElId;
    element.innerHTML = AjxTemplate.expand("ajax.dwt.templates.Widgets#DwtSoundPlayer", id);
    
	this._playButton = new DwtButton(this, DwtButton.TOGGLE_STYLE, "DwtSoundPlayerButton");
	this._playButton.replaceElement(id + "_play");
	this._playButton.setText("|>");
	this._playButton.setToolTipContent(ZmMsg.play);
	this._playButton.addSelectionListener(new AjxListener(this, this.play));

	this._pauseButton = new DwtButton(this, DwtButton.TOGGLE_STYLE, "DwtSoundPlayerButton");
	this._pauseButton.replaceElement(id + "_pause");
	this._pauseButton.setText("||");
	this._pauseButton.setToolTipContent(ZmMsg.pause);
	this._pauseButton.addSelectionListener(new AjxListener(this, this.pause));

	this._position = new DwtProgressBar(this);
	this._position.replaceElement(id + "_postition");
	this._position.setMaxValue(100);
	this._position.setValue(0);

	this._volumeButton = new DwtButton(this, null, "DwtSoundPlayerButton");
	this._volumeButton.replaceElement(id + "_volume");
	this._volumeButton.setText("v");
	this._volumeButton.setToolTipContent(ZmMsg.volume);
};


