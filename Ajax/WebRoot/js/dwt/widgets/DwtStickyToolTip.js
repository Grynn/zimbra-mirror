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


function DwtStickyToolTip(shell, className, posStyle) {
	className = className || "DwtStickyToolTip";
	posStyle = posStyle || Dwt.ABSOLUTE_STYLE;
	DwtBaseDialog.call(this, shell, className, posStyle, null, DwtBaseDialog.MODELESS);

	this._contentDiv = document.getElementById(this._htmlElId+"_contents");	
	this._contentView = new DwtControl(this);
}
DwtStickyToolTip.prototype = new DwtBaseDialog;
DwtStickyToolTip.prototype.constructor = DwtStickyToolTip;

DwtStickyToolTip.prototype.setTitle = function(title) {
	this._title = title;
	var element = document.getElementById(this._htmlElId+"_title");
	element.innerHTML = title;
}

DwtStickyToolTip.prototype.setContent = function(content) {
	this._contentView.setContent(content);
	this.setView(this._contentView);
}

DwtStickyToolTip.prototype.popup = function(x, y) {
	DwtBaseDialog.prototype.popup.call(this, new DwtPoint(x, y));
}

DwtStickyToolTip.prototype._positionDialog = function (loc) {
	var element = this.getHtmlElement();
	var baseId = this._htmlElId;
	var clip = false;
	
	DwtToolTip.prototype._positionElement.call(this, element, loc.x, loc.y, baseId, clip);
}

DwtStickyToolTip.prototype._createHtml = function() {
	DwtBaseDialog.prototype._createHtml.call(this);
	
	var close = new DwtButton(this, DwtLabel.IMAGE_RIGHT | DwtLabel.ALIGN_RIGHT, "DwtStickyToolTipClose");
	// REVISIT: Do this right...
	close.setImage(["ImgRedCircleClose"]);
	close.addSelectionListener(new AjxListener(this, this._handleClose));
	
	var container = document.getElementById(this._htmlElId+"_close");
	var closeEl = close.getHtmlElement();
	container.appendChild(closeEl);
}
DwtStickyToolTip.prototype._handleClose = function(event) {
	this.popdown();
	this.dispose();
}

DwtStickyToolTip.prototype._getStartBorder = function () {
	// REVISIT
	if (!this._titleHandleId) this._titleHandleId = Dwt.getNextId();
	if (!this._contentId) this._contentId = Dwt.getNextId();
	if (!this._titleCellId) this._titleCellId = Dwt.getNextId();
	
	var borderStyle = "SemiModalDialog";
	//var substitutions = {title : this._title, titleTextId: this._titleCellId, titleId: this._titleHandleId};
	var substitutions = { id: this._htmlElId, title: this._title };
	return DwtBorder.getBorderStartHtml(borderStyle, substitutions);
};

DwtStickyToolTip.prototype._getEndBorder = function () {
	var borderStyle = "SemiModalDialog";
	var substitutions = { id: this._htmlElId };
	return DwtBorder.getBorderEndHtml(borderStyle, substitutions);
};

DwtStickyToolTip.prototype._getContentHtml = function () {
	return "";
};
