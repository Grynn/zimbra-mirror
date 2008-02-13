/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */

YMEmoticonsPickerButton = function(parent, style, className, posStyle, id, index){

	if (arguments.length == 0) return;
	className = className || "YMEmoticonsPicker";
	var params = {parent:parent, style:style , className:className, posStyle:posStyle,
				  actionTiming:DwtButton.ACTION_MOUSEUP, id:id, index:index};
    DwtButton.call(this, params);
	this.setEmoticon();
    this.setMenu(new AjxCallback(this, this._createMenu));
};

YMEmoticonsPickerButton.prototype = new DwtButton;
YMEmoticonsPickerButton.prototype.constructor = YMEmoticonsPickerButton;

YMEmoticonsPickerButton.prototype._createMenu = function() {
        var menu = new DwtMenu({parent:this, style:DwtMenu.GENERIC_WIDGET_STYLE, inDialog:true});
        this._picker = new YMEmoticonsPicker(menu,null,null);
	    this._picker.addSelectionListener(new AjxListener(this, this._smileyPicked));
        return menu;
};

YMEmoticonsPickerButton.prototype.setEmoticon = function(id){
        var smiley = id
                ? YMEmoticonsPicker.SMILEYS[id]
                : YMEmoticonsPicker.getDefaultSmiley();

	if (smiley) {
	  	this._smileyButtonDiv.src = smiley.src;
	  	this._smiley = smiley;
	}
};

YMEmoticonsPickerButton.prototype._createHtmlFromTemplate = function(templateId, data) {

    DwtButton.prototype._createHtmlFromTemplate.call(this, templateId, data);

 	var id = Dwt.getNextId();
 	var displayHtml = "<div unselectable><img width='18' src='' id='"+ id+"_smiley'></div>";
	this.setText(displayHtml);

    this._smileyButtonDiv = document.getElementById( id+"_smiley");

    delete id;
};

YMEmoticonsPickerButton.prototype.getSmiley = function(id){
	return this._picker.getSmiley(id);
};

YMEmoticonsPickerButton.prototype.getSelectedSmiley = function(){
	return this._smiley;
};

YMEmoticonsPickerButton.prototype._smileyPicked = function(ev){

	var id = ev.detail;
	this.setEmoticon(id);

	if (this.isListenerRegistered(DwtEvent.SELECTION)) {
 		var selEv = DwtShell.selectionEvent;
 		selEv.item = this;
 		this.notifyListeners(DwtEvent.SELECTION, selEv);
 	}
};

//----------------------------------------------------------------------------------

YMEmoticonsPicker = function(parent, className, posStyle){

    if (arguments.length == 0) return;
	className = className || "DwtColorPicker";
	DwtControl.call(this, {parent:parent, className:className, posStyle:posStyle});

    this._createEmoticonsPicker(parent);

};

//Needs to act like a button, so DwtButton
YMEmoticonsPicker.prototype = new DwtControl;
YMEmoticonsPicker.prototype.constructor = YMEmoticonsPicker;
YMEmoticonsPicker.SMILEYS = Com_Zimbra_YMEmoticons.SMILEYS;

YMEmoticonsPicker.getDefaultSmiley = function() {
	for (var smiley in YMEmoticonsPicker.SMILEYS) {
		return YMEmoticonsPicker.SMILEYS[smiley];
	}
	return null;
};

YMEmoticonsPicker.prototype.getDefaultSmiley = YMEmoticonsPicker.getDefaultSmiley;

YMEmoticonsPicker.prototype._createEmoticonsPicker = function(parent){
	this._createEmoticonsTable();
	this.setSize("250px",Dwt.DEFAULT);
	this._registerHandlers();
};

YMEmoticonsPicker.prototype.addSelectionListener =
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
};

YMEmoticonsPicker.prototype.removeSelectionListener =
function(listener) {
	this.removeListener(DwtEvent.SELECTION, listener);
};

YMEmoticonsPicker.prototype.dispose =
function () {
	if (this._disposed) return;
	Dwt.disassociateElementFromObject(this.getHtmlElement().firstChild, this);
	DwtControl.prototype.dispose.call(this);
};

YMEmoticonsPicker.prototype.getSmiley = function(id){
	return YMEmoticonsPicker.SMILEYS[id];
};

YMEmoticonsPicker.EMOTICONS_PER_ROW = 10;
YMEmoticonsPicker.prototype._createEmoticonsTable = function(){

	var idx = 0;
	var html = [];
	var counter  = 0;
	html[idx++] = "<table cellpadding='2' cellspacing='3' border='0' align='center' width='250px'><tr>";
	for(var smiley in YMEmoticonsPicker.SMILEYS){
		if( counter != 0 && !(counter%10) ) html[idx++] = "</tr><tr>";
		html[idx++] = "<td id='" + smiley + "' style='background-color:#FFFFFF' width='18' height='18'>";
		html[idx++] = "<img height='18' width='18' src='"+ YMEmoticonsPicker.SMILEYS[smiley].src+"'/>";
		html[idx++] = "</td>";
		counter++;
	}
	var blankcells = (counter-1)%10;
	if( blankcells > 0 ){
		html[idx++] = "<td colspan='"+blankcells+"'></td></tr>";
	}
	html[idx++] = "</table>"

	this.getHtmlElement().innerHTML = html.join("");

};

YMEmoticonsPicker.prototype._registerHandlers = function(){

	var table = this.getHtmlElement().firstChild;
	Dwt.associateElementWithObject(table, this);
	var rows = table.rows;
	var numRows = rows.length;

	for (var i = 0; i < numRows; i++) {
		var cells = rows[i].cells;
		var numCells = cells.length
		for (var j = 0; j < numCells; j++) {
			var cell = cells[j];
			Dwt.setHandler(cell, DwtEvent.ONMOUSEDOWN, YMEmoticonsPicker._mouseDownHdlr);
			Dwt.setHandler(cell, DwtEvent.ONMOUSEUP, YMEmoticonsPicker._mouseUpHdlr);
		}
	}
};

YMEmoticonsPicker._mouseDownHdlr = function(ev) {

	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	var target = mouseEv.target;
	if (target.nodeName.toLowerCase() == "img")
		target = target.parentNode;

	mouseEv.dwtObj._downTdId = target.id;

	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev)
	return false;


};

YMEmoticonsPicker._mouseUpHdlr = function(ev) {

	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	var obj = mouseEv.dwtObj;

	var target = mouseEv.target;
	if (target.nodeName.toLowerCase() == "img")
		target = target.parentNode;

	if (obj._downTdId == target.id) {
		// If our parent is a menu then we need to have it close

		var smiley = YMEmoticonsPicker.SMILEYS[target.id];

		if(smiley) {
			// Call Listeners on mouseEv.target.id
			if (obj.isListenerRegistered(DwtEvent.SELECTION)) {
		    	var selEv = DwtShell.selectionEvent;
		    	DwtUiEvent.copy(selEv, mouseEv);
		    	selEv.item = obj;
		    	selEv.detail = target.id;
		    	obj.notifyListeners(DwtEvent.SELECTION, selEv);
		    }
		}

		if (obj.parent instanceof DwtMenu)
			DwtMenu.closeActiveMenu();

	}
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev)
	return false;

};
