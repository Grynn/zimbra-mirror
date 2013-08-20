/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */


/**
 * Creates a progress bar.
 * @constructor
 * @class
 * This class represents a progress bar.
 * 
 * @param {DwtComposite}	parent    the parent container
 * @param {string}	className the CSS class name
 * @param {constant}	posStyle  the position style (see {@link DwtControl})
 * 
 * @author Greg Solovyev
 * 
 * @extends		DwtComposite
 */
DwtProgressBar = function(parent, className, posStyle) {
	if (arguments.length == 0) return;
	posStyle = posStyle || DwtControl.STATIC_STYLE;
	DwtComposite.call(this, {parent:parent, posStyle:posStyle});
	this._maxValue = 100;
	this._value = 0;
	this._quotabarDiv = null;
	this._quotausedDiv = null;	

	this._progressBgColor = null;// "#66cc33";	//	MOW: removing this so the color can be skinned
												// 		set the color in the class "quotaused"
	this._progressCssClass = "quotaused";
	
	this._wholeBgColor = null;
	this._wholeCssClass = "quotabar";	
	this._createHTML();
}

DwtProgressBar.prototype = new DwtComposite;
DwtProgressBar.prototype.constructor = DwtProgressBar;


//
// Public methods
//

/**
 * Sets the progress background color.
 * 
 * @param	{string}	var		the color
 */
DwtProgressBar.prototype.setProgressBgColor = 
function(val) {
	this._progressBgColor = val;
}

/**
 * Sets the whole background color.
 * 
 * @param	{string}	var		the color
 */
DwtProgressBar.prototype.setWholeBgColor = 
function(val) {
	this._wholeBgColor = val;
}

/**
 * Sets the progress CSS class.
 * 
 * @param	{string}	var		the color
 */
DwtProgressBar.prototype.setProgressCssClass = 
function(val) {
	this._progressCssClass = val;
}

/**
 * Sets the whole CSS class.
 * 
 * @param	{string}	var		the color
 */
DwtProgressBar.prototype.setWholeCssClass = 
function(val) {
	this._wholeCssClass = val;
}

/**
 * Sets the process CSS style.
 * 
 * @param	{string}	var		the color
 */
DwtProgressBar.prototype.setProgressCssStyle = 
function(val) {
	this._progressCssStyle = val;
}

/**
 * Sets the while CSS style.
 * 
 * @param	{string}	var		the color
 */
DwtProgressBar.prototype.setWholeCssStyle  = 
function(val) {
	this._wholeCssStyle = val;
}

/**
 * Sets the progress value.
 * 
 * @param	{number}		val		the value
 */
DwtProgressBar.prototype.setValue = 
function(val) {
	this._value = parseInt(val);
	var percent;

	if(this._value == this._maxValue)
		percent = 100;
	else 
		percent = Math.min(Math.round((this._value / this._maxValue) * 100), 100);	

	if(isNaN(percent))
		percent = "0";
			
	if(!this._quotabarDiv) {
		this._quotabarDiv = document.createElement("div")
		if(this._wholeCssClass)
			this._quotabarDiv.className = this._wholeCssClass;

		if(this._wholeBgColor)
			this._quotabarDiv.backgroundColor = this._wholeBgColor;
		
		this._cell.appendChild(this._quotabarDiv);
	}
	if(!this._quotausedDiv) {
		this._quotausedDiv = document.createElement("div")
		if(this._progressCssClass)
			this._quotausedDiv.className = this._progressCssClass;
			
		if(this._progressBgColor)
			this._quotausedDiv.style.backgroundColor = this._progressBgColor;
			
		this._quotabarDiv.appendChild(this._quotausedDiv);			
	}	

	this._quotausedDiv.style.width = percent + "%";
}

/**
 * Sets the value by percentage.
 * 
 * @param	{string}		percent		the value as a percentage (for example: "10%")
 */
DwtProgressBar.prototype.setValueByPercent =
function (percent){
	this.setMaxValue(100);
	this.setValue (percent.replace(/\%/gi, ""));
}

/**
 * Gets the value.
 * 
 * @return	{number}	the value
 */
DwtProgressBar.prototype.getValue = 
function() {
	return this._value;
}

/**
 * Gets the maximum value.
 * 
 * @return	{number}	the maximum value
 */
DwtProgressBar.prototype.getMaxValue = 
function() {
	return this._maxValue;
}

/**
 * Sets the maximum value.
 * 
 * @param	{number}	val		the maximum value
 */
DwtProgressBar.prototype.setMaxValue = 
function(val) {
	this._maxValue = parseInt(val);
}

/**
 * Sets the label.
 * 
 * @param	{string}		text		the label
 * @param	{boolean}		isRightAlign	if <code>true</code>, if the label is right aligned
 */
DwtProgressBar.prototype.setLabel =
function( text, isRightAlign) {
	var labelNode = document.createTextNode(text);
	var position = isRightAlign ? -1 : 0;
	var labelCell = this._row.insertCell(position) ;
	labelCell.appendChild (labelNode);
}

//
// Protected methods
//

DwtProgressBar.prototype._createHTML = 
function() {
	this._table = document.createElement("table");
	this._table.border = this._table.cellpadding = this._table.cellspacing = 0;	

	this._row = this._table.insertRow(-1);

	//if(AjxEnv.isLinux)
		//this._row.style.lineHeight = 13;
	
	this._cell = this._row.insertCell(-1);
	
	this.getHtmlElement().appendChild(this._table);
}
