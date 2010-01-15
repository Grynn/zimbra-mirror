/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

function DwtHtmlEditorTest(parent) {
	this._createToolBar1(parent);
	this._createToolBar2(parent);
	this.rte = new DwtHtmlEditor(parent, null, DwtControl.ABSOLUTE_STYLE, false, null);
	this.rte.setLocation(0, 100);
	this.rte.setSize(800, 600);
	this.rte.zShow(true);
	
	this.rte.addStateChangeListener(new AjxListener(this, this._rteStateChangeListener));
	
	/*var cp = new DwtColorPicker(parent, null, DwtControl.ABSOLUTE_STYLE);
	cp.setLocation(410, 400);
	cp.zShow(true);
	*/
}

DwtHtmlEditorTest._VALUE = "value";

DwtHtmlEditorTest.run =
function() {
	var shell = new DwtShell("MainShell", false);
	var tst = new DwtHtmlEditorTest(shell);
}

DwtHtmlEditorTest.prototype._styleListener =
function(ev) {
DBG.println("Setting Style: " + ev._args.newValue);
	this.rte.setStyle(ev._args.newValue);
}

DwtHtmlEditorTest.prototype._fontNameListener =
function(ev) {
DBG.println("Setting Font Name: " + ev._args.newValue);
	this.rte.setFont(ev._args.newValue);
}

DwtHtmlEditorTest.prototype._fontSizeListener =
function(ev) {
DBG.println("Setting Font Size");
	this.rte.setFont(null, null, ev._args.newValue);
}

DwtHtmlEditorTest.prototype._directionListener =
function(ev) {
DBG.println("Changing Text Direction");
	this.rte.setTextDirection(ev.item.getData(DwtHtmlEditorTest._VALUE));
}

DwtHtmlEditorTest.prototype._indentListener =
function(ev) {
DBG.println("Indent/Outdenting");
	this.rte.setIndent(ev.item.getData(DwtHtmlEditorTest._VALUE));
}

DwtHtmlEditorTest.prototype._insElementListener =
function(ev) {
DBG.println("Inserting Element");
	this.rte.insertElement(ev.item.getData(DwtHtmlEditorTest._VALUE));
}

DwtHtmlEditorTest.prototype._justificationListener =
function(ev) {
DBG.println("Setting Justification");
	this.rte.setJustification(ev.item.getData(DwtHtmlEditorTest._VALUE));
}

DwtHtmlEditorTest.prototype._fontStyleListener =
function(ev) {
DBG.println("Setting Font Style");
	this.rte.setFont(null, ev.item.getData(DwtHtmlEditorTest._VALUE));
}

DwtHtmlEditorTest.prototype._fontColorListener =
function(ev) {
DBG.println("Setting Font Color");
	this.rte.setFont(null, null, null, ev.detail, null);
}

DwtHtmlEditorTest.prototype._fontHiliteListener =
function(ev) {
DBG.println("Setting Font Hilite");
	this.rte.setFont(null, null, null, null, ev.detail);
}

DwtHtmlEditorTest.prototype._mailFormatListener =
function(ev) {
	var fromat;
	DBG.println("FORMAT CHANGED!");
	this.rte.setMode(ev.item.getData(DwtHtmlEditorTest._VALUE), true);
}

DwtHtmlEditorTest.prototype._createToolBar1 =
function(shell) {
	var tb = new DwtToolBar(shell, "LmToolBar", DwtControl.ABSOLUTE_STYLE, 2);
	tb.zShow(true);
	tb.setLocation(0, 0);

	this._createStyleSelect(tb);
	this._createFontFamilySelect(tb);
	this._createFontSizeMenu(tb);
	new DwtControl(tb, "vertSep");
	
	var listener = new AjxListener(this, this._fontStyleListener);
	var b = this._boldButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "LmComposeTBButton");
	b.setImage(LmImg.I_BOLD_TEXT);
	b.setToolTipContent(LmMsg.boldText);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.BOLD_STYLE);
	b.addSelectionListener(listener);
	
	b = this._italicButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "LmComposeTBButton");
	b.setImage(LmImg.I_ITALIC_TEXT);
	b.setToolTipContent(LmMsg.italicText);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.ITALIC_STYLE);
	b.addSelectionListener(listener);
	
	b = this._underlineButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "LmComposeTBButton");
	b.setImage(LmImg.I_UNDERLINE_TEXT);
	b.setToolTipContent(LmMsg.underlineText);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.UNDERLINE_STYLE);
	b.addSelectionListener(listener);
	
	b = this._strikeThruButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "LmComposeTBButton");
	b.setImage(LmImg.I_STRIKETHRU_TEXT);
	b.setToolTipContent(LmMsg.strikeThruText);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.STRIKETHRU_STYLE);
	b.addSelectionListener(listener);

	b = this._superscriptButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "LmComposeTBButton");
	b.setImage(LmImg.I_SUPERSCRIPT);
	b.setToolTipContent(LmMsg.superscript);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.SUPERSCRIPT_STYLE);
	b.addSelectionListener(listener);
	
	b = this._subscriptButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "LmComposeTBButton");
	b.setImage(LmImg.I_SUBSCRIPT);
	b.setToolTipContent(LmMsg.subscript);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.SUBSCRIPT_STYLE);
	b.addSelectionListener(listener);
}

DwtHtmlEditorTest.prototype._createToolBar2 =
function(shell) {
	var tb = new DwtToolBar(shell, "LmToolBar", DwtControl.ABSOLUTE_STYLE, 2);
	tb.zShow(true);
	tb.setLocation(0, 30);
	
	var listener = new AjxListener(this, this._justificationListener);
	var b = this._leftJustifyButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "LmComposeTBButton");
	b.setImage(LmImg.I_LEFT_JUSTIFY);
	b.setToolTipContent(LmMsg.leftJustify);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.JUSTIFY_LEFT);
	b.addSelectionListener(listener);
	
	b = this._centerJustifyButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "LmComposeTBButton");
	b.setImage(LmImg.I_CENTER_JUSTIFY);
	b.setToolTipContent(LmMsg.centerJustify);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.JUSTIFY_CENTER);
	b.addSelectionListener(listener);

	b = this._rightJustifyButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "LmComposeTBButton");
	b.setImage(LmImg.I_RIGHT_JUSTIFY);
	b.setToolTipContent(LmMsg.rightJustify);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.JUSTIFY_RIGHT);
	b.addSelectionListener(listener);
	
	b = this._fullJustifyButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "LmComposeTBButton");
	b.setImage(LmImg.I_FULL_JUSTIFY);
	b.setToolTipContent(LmMsg.justify);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.JUSTIFY_FULL);
	b.addSelectionListener(listener);
	
	new DwtControl(tb, "vertSep");

	var insElListener = new AjxListener(this, this._insElementListener);
	b = this._listButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE,  "LmComposeTBButton");
	b.setToolTipContent(LmMsg.bulletedList);
	b.setImage(LmImg.I_LIST);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.UNORDERED_LIST);
	b.addSelectionListener(insElListener);
	
	b = this._numberedListButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "LmComposeTBButton");
	b.setToolTipContent(LmMsg.numberedList);
	b.setImage(LmImg.I_NUMBERED_LIST);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.ORDERED_LIST);
	b.addSelectionListener(insElListener);

	listener = new AjxListener(this, this._indentListener);	
	b = this._outdentButton = new DwtButton(tb, null, "LmComposeTBButton");
	b.setToolTipContent(LmMsg.outdent);
	b.setImage(LmImg.I_OUTDENT);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.OUTDENT);
	b.addSelectionListener(insElListener);
	
	b = this._indentButton = new DwtButton(tb, null, "LmComposeTBButton");
	b.setToolTipContent(LmMsg.indent);
	b.setImage(LmImg.I_INDENT);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.INDENT);
	b.addSelectionListener(insElListener);
	
	new DwtControl(tb, "vertSep");

	b = this._fontColorButton = new DwtButton(tb, null, "LmComposeTBButton");
	b.setImage(LmImg.I_FONT_COLOR);
	b.setToolTipContent(LmMsg.fontColor);
	var m = new DwtMenu(b, DwtMenu.COLOR_PICKER_STYLE);
	var cp = new DwtColorPicker(m);
	cp.addSelectionListener(new AjxListener(this, this._fontColorListener));
	b.setMenu(m);
	
	b = this._fontBackgroundButton = new DwtButton(tb, null, "LmComposeTBButton");
	b.setImage(LmImg.I_FONT_BACKGROUND);
	b.setToolTipContent(LmMsg.fontBackground);
	m = new DwtMenu(b, DwtMenu.COLOR_PICKER_STYLE);
	cp = new DwtColorPicker(m);
	cp.addSelectionListener(new AjxListener(this, this._fontHiliteListener));
	b.setMenu(m);
	
	new DwtControl(tb, "vertSep");
	
	b = this._horizRuleButton = new DwtButton(tb, null, "LmComposeTBButton");
	b.setImage(LmImg.I_HORIZ_RULE);
	b.setToolTipContent(LmMsg.horizRule);
	b.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.HORIZ_RULE);
	b.addSelectionListener(insElListener);
	
	b = this._insertTableButton = new DwtButton(tb, null, "LmComposeTBButton");
	b.setImage(LmImg.I_INSERT_TABLE);
	b.setToolTipContent(LmMsg.insertTable);
	//b.addSelectionListener(insElListener);

	b = this._insertTableButton = new DwtButton(tb, null, "LmComposeTBButton");
	b.setImage(LmImg.I_URL);
	b.setToolTipContent(LmMsg.insertLink);
	//b.addSelectionListener(insElListener);

	b = this._insertTableButton = new DwtButton(tb, null, "LmComposeTBButton");
	b.setImage(LmImg.I_IMAGE);
	b.setToolTipContent(LmMsg.insertImage);
	//b.addSelectionListener(insElListener);
	
	new DwtControl(tb, "vertSep");
	
	b = this._toggleModeButton = new DwtButton(tb, null, "LmComposeTBButton");
	b.setText(LmMsg.format);
	b.setToolTipContent(LmMsg.format);
	
	var m = new DwtMenu(b);
	b.setMenu(m);

	var mi = new DwtMenuItem(m, DwtMenuItem.RADIO_STYLE);
	mi.setImage(LmImg.I_HTML);
	mi.setText(LmMsg.htmlDocument);
	mi.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.HTML);
	mi.addSelectionListener(new AjxListener(this, this._mailFormatListener));
	
	mi = new DwtMenuItem(m, DwtMenuItem.RADIO_STYLE);
	mi.setImage(LmImg.I_DOCUMENT);
	mi.setText(LmMsg.plainText);
	mi.setData(DwtHtmlEditorTest._VALUE, DwtHtmlEditor.TEXT);
	mi.addSelectionListener(new AjxListener(this, this._mailFormatListener));	
}

DwtHtmlEditorTest.prototype._createStyleSelect =
function(tb) {
	var listener = new AjxListener(this, this._styleListener);
	var s = this._styleSelect = new DwtSelect(tb, null, "LmComposeTBSelect");
	s.addChangeListener(listener);
	
	s.addOption("Normal", true, DwtHtmlEditor.PARAGRAPH);
	s.addOption("Heading 1", false, DwtHtmlEditor.H1);
	s.addOption("Heading 2", false, DwtHtmlEditor.H2);
	s.addOption("Heading 3", false, DwtHtmlEditor.H3);
	s.addOption("Heading 4", false, DwtHtmlEditor.H4);
	s.addOption("Heading 5", false, DwtHtmlEditor.H5);
	s.addOption("Heading 6", false, DwtHtmlEditor.H6);
	s.addOption("Address", false, DwtHtmlEditor.ADDRESS);
	s.addOption("Preformatted", false, DwtHtmlEditor.PREFORMATTED);
}


DwtHtmlEditorTest.prototype._createFontFamilySelect =
function(tb) {
	var listener = new AjxListener(this, this._fontNameListener);
	var s = this._fontFamilySelect = new DwtSelect(tb, null, "LmComposeTBSelect");
	s.addChangeListener(listener);
	
	s.addOption("Arial", false, DwtHtmlEditor.ARIAL);
	s.addOption("Times New Roman", true, DwtHtmlEditor.TIMES);
	s.addOption("Courier New", false, DwtHtmlEditor.COURIER);
	s.addOption("Verdana", false, DwtHtmlEditor.VERDANA);
}

DwtHtmlEditorTest.prototype._createFontSizeMenu =
function(tb) {
	var listener = new AjxListener(this, this._fontSizeListener);
	var s = this._fontSizeSelect = new DwtSelect(tb, null, "LmComposeTBSelect");
	s.addChangeListener(listener);
	
	s.addOption("1 (8pt)", false, 1);
	s.addOption("2 (10pt)", false, 2);
	s.addOption("3 (12pt)", true, 3);
	s.addOption("4 (14pt)", false, 4);
	s.addOption("5 (18pt)", false, 5);
	s.addOption("6 (24pt)", false, 6);
	s.addOption("7 (36pt)", false, 7);
}

DwtHtmlEditorTest.prototype._rteStateChangeListener =
function(ev) {

/*	DBG.println("Bold: " + ev.isBold);
	DBG.println("Underline: " + ev.isUnderline);
	DBG.println("Italic: " + ev.isItalic);
	DBG.println("StrikeThru: " + ev.isStrikeThru);
	DBG.println("Superscript: " + ev.isSuperscript);
	DBG.println("Subscript: " + ev.isSuperscript);
	DBG.println("Font Family: " + ev.fontFamily);
	DBG.println("Font Size: " + ev.fontSize);
	DBG.println("Style: " + ev.style);
	DBG.println("Color: " + ev.color);
	DBG.println("Background: " + ev.backgroundColor);
	DBG.println("Justification: " + ev.justification);
	DBG.println("Ordered List: " + ev.isOrderedList);
	DBG.println("UnorderedList: " + ev.isUnorderedList);
	DBG.println("<HR>");
*/	
	this._boldButton.setToggled(ev.isBold);
	this._underlineButton.setToggled(ev.isUnderline);
	this._italicButton.setToggled(ev.isItalic);
	this._strikeThruButton.setToggled(ev.isStrikeThru);
	this._subscriptButton.setToggled(ev.isSubscript);
	this._superscriptButton.setToggled(ev.isSuperscript);
	
	this._numberedListButton.setToggled(ev.isOrderedList);
	this._listButton.setToggled(ev.isUnorderedList);

	if (ev.style)
		this._styleSelect.setSelectedValue(ev.style);

	if (ev.fontFamily)
		this._fontFamilySelect.setSelectedValue(ev.fontFamily);
		
	if (ev.fontSize && ev.fontFamily != "")
		this._fontSizeSelect.setSelectedValue(ev.fontSize);
	
	if (ev.justification == DwtHtmlEditor.JUSTIFY_LEFT) {
		this._leftJustifyButton.setToggled(true);
		this._centerJustifyButton.setToggled(false);
		this._rightJustifyButton.setToggled(false);
		this._fullJustifyButton.setToggled(false);		
	} else if (ev.justification == DwtHtmlEditor.JUSTIFY_CENTER) {
		this._leftJustifyButton.setToggled(false);
		this._centerJustifyButton.setToggled(true);
		this._rightJustifyButton.setToggled(false);
		this._fullJustifyButton.setToggled(false);		
	} else if (ev.justification == DwtHtmlEditor.JUSTIFY_RIGHT) {
		this._leftJustifyButton.setToggled(false);
		this._centerJustifyButton.setToggled(false);
		this._rightJustifyButton.setToggled(true);
		this._fullJustifyButton.setToggled(false);		
	} else if (ev.justification == DwtHtmlEditor.JUSTIFY_FULL) {
		this._leftJustifyButton.setToggled(false);
		this._centerJustifyButton.setToggled(false);
		this._rightJustifyButton.setToggled(false);
		this._fullJustifyButton.setToggled(true);		
	}
}


