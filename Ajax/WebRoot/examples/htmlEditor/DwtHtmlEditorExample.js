function DwtHtmlEditorExample(parent) {
	this._createToolBar1(parent);
	this._createToolBar2(parent);
	this.rte = new DwtHtmlEditor(parent, null, DwtControl.ABSOLUTE_STYLE, false, null);
	this.rte.setLocation(0, 58);
	this.rte.setSize("100%", "100%");
	this.rte.zShow(true);
	
	this.rte.addStateChangeListener(new LsListener(this, this._rteStateChangeListener));	
}

DwtHtmlEditorExample._VALUE = "value";

DwtHtmlEditorExample.run =
function() {
	var shell = new DwtShell("MainShell", false);
	var tst = new DwtHtmlEditorExample(shell);
}

DwtHtmlEditorExample.prototype._styleListener =
function(ev) {
DBG.println("Setting Style: " + ev._args.newValue);
	this.rte.setStyle(ev._args.newValue);
}

DwtHtmlEditorExample.prototype._fontNameListener =
function(ev) {
DBG.println("Setting Font Name: " + ev._args.newValue);
	this.rte.setFont(ev._args.newValue);
}

DwtHtmlEditorExample.prototype._fontSizeListener =
function(ev) {
DBG.println("Setting Font Size");
	this.rte.setFont(null, null, ev._args.newValue);
}

DwtHtmlEditorExample.prototype._directionListener =
function(ev) {
DBG.println("Changing Text Direction");
	this.rte.setTextDirection(ev.item.getData(DwtHtmlEditorExample._VALUE));
}

DwtHtmlEditorExample.prototype._indentListener =
function(ev) {
DBG.println("Indent/Outdenting");
	this.rte.setIndent(ev.item.getData(DwtHtmlEditorExample._VALUE));
}

DwtHtmlEditorExample.prototype._insElementListener =
function(ev) {
DBG.println("Inserting Element");
	this.rte.insertElement(ev.item.getData(DwtHtmlEditorExample._VALUE));
}

DwtHtmlEditorExample.prototype._justificationListener =
function(ev) {
DBG.println("Setting Justification");
	this.rte.setJustification(ev.item.getData(DwtHtmlEditorExample._VALUE));
}

DwtHtmlEditorExample.prototype._fontStyleListener =
function(ev) {
DBG.println("Setting Font Style");
	this.rte.setFont(null, ev.item.getData(DwtHtmlEditorExample._VALUE));
}

DwtHtmlEditorExample.prototype._fontColorListener =
function(ev) {
DBG.println("Setting Font Color");
	this.rte.setFont(null, null, null, ev.detail, null);
}

DwtHtmlEditorExample.prototype._fontHiliteListener =
function(ev) {
DBG.println("Setting Font Hilite");
	this.rte.setFont(null, null, null, null, ev.detail);
}

DwtHtmlEditorExample.prototype._mailFormatListener =
function(ev) {
	var fromat;
	DBG.println("FORMAT CHANGED!");
	this.rte.setMode(ev.item.getData(DwtHtmlEditorExample._VALUE), true);
}

DwtHtmlEditorExample.prototype._createToolBar1 =
function(shell) {
	var tb = new DwtToolBar(shell, "ToolBar", DwtControl.ABSOLUTE_STYLE, 2);
	tb.zShow(true);
	tb.setLocation(0, 0);

	this._createStyleSelect(tb);
	this._createFontFamilySelect(tb);
	this._createFontSizeMenu(tb);
	new DwtControl(tb, "vertSep");
	
	var listener = new LsListener(this, this._fontStyleListener);
	var b = this._boldButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "TBButton");
	b.setImage(ExImg.I_BOLD_TEXT);
	b.setToolTipContent(ExMsg.boldText);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.BOLD_STYLE);
	b.addSelectionListener(listener);
	
	b = this._italicButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "TBButton");
	b.setImage(ExImg.I_ITALIC_TEXT);
	b.setToolTipContent(ExMsg.italicText);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.ITALIC_STYLE);
	b.addSelectionListener(listener);
	
	b = this._underlineButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "TBButton");
	b.setImage(ExImg.I_UNDERLINE_TEXT);
	b.setToolTipContent(ExMsg.underlineText);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.UNDERLINE_STYLE);
	b.addSelectionListener(listener);
	
	b = this._strikeThruButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "TBButton");
	b.setImage(ExImg.I_STRIKETHRU_TEXT);
	b.setToolTipContent(ExMsg.strikeThruText);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.STRIKETHRU_STYLE);
	b.addSelectionListener(listener);

	b = this._superscriptButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "TBButton");
	b.setImage(ExImg.I_SUPERSCRIPT);
	b.setToolTipContent(ExMsg.superscript);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.SUPERSCRIPT_STYLE);
	b.addSelectionListener(listener);
	
	b = this._subscriptButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "TBButton");
	b.setImage(ExImg.I_SUBSCRIPT);
	b.setToolTipContent(ExMsg.subscript);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.SUBSCRIPT_STYLE);
	b.addSelectionListener(listener);
}

DwtHtmlEditorExample.prototype._createToolBar2 =
function(shell) {
	var tb = new DwtToolBar(shell, "ToolBar", DwtControl.ABSOLUTE_STYLE, 2);
	tb.zShow(true);
	tb.setLocation(0, 30);
	
	var listener = new LsListener(this, this._justificationListener);
	var b = this._leftJustifyButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "TBButton");
	b.setImage(ExImg.I_LEFT_JUSTIFY);
	b.setToolTipContent(ExMsg.leftJustify);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.JUSTIFY_LEFT);
	b.addSelectionListener(listener);
	
	b = this._centerJustifyButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "TBButton");
	b.setImage(ExImg.I_CENTER_JUSTIFY);
	b.setToolTipContent(ExMsg.centerJustify);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.JUSTIFY_CENTER);
	b.addSelectionListener(listener);

	b = this._rightJustifyButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "TBButton");
	b.setImage(ExImg.I_RIGHT_JUSTIFY);
	b.setToolTipContent(ExMsg.rightJustify);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.JUSTIFY_RIGHT);
	b.addSelectionListener(listener);
	
	b = this._fullJustifyButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "TBButton");
	b.setImage(ExImg.I_FULL_JUSTIFY);
	b.setToolTipContent(ExMsg.justify);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.JUSTIFY_FULL);
	b.addSelectionListener(listener);
	
	new DwtControl(tb, "vertSep");

	var insElListener = new LsListener(this, this._insElementListener);
	b = this._listButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE,  "TBButton");
	b.setToolTipContent(ExMsg.bulletedList);
	b.setImage(ExImg.I_BULLETED_LIST);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.UNORDERED_LIST);
	b.addSelectionListener(insElListener);
	
	b = this._numberedListButton = new DwtButton(tb, DwtButton.TOGGLE_STYLE, "TBButton");
	b.setToolTipContent(ExMsg.numberedList);
	b.setImage(ExImg.I_NUMBERED_LIST);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.ORDERED_LIST);
	b.addSelectionListener(insElListener);

	listener = new LsListener(this, this._indentListener);	
	b = this._outdentButton = new DwtButton(tb, null, "TBButton");
	b.setToolTipContent(ExMsg.outdent);
	b.setImage(ExImg.I_OUTDENT);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.OUTDENT);
	b.addSelectionListener(insElListener);
	
	b = this._indentButton = new DwtButton(tb, null, "TBButton");
	b.setToolTipContent(ExMsg.indent);
	b.setImage(ExImg.I_INDENT);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.INDENT);
	b.addSelectionListener(insElListener);
	
	new DwtControl(tb, "vertSep");

	b = this._fontColorButton = new DwtButton(tb, null, "TBButton");
	b.setImage(ExImg.I_FONT_COLOR);
	b.setToolTipContent(ExMsg.fontColor);
	var m = new DwtMenu(b, DwtMenu.COLOR_PICKER_STYLE);
	var cp = new DwtColorPicker(m);
	cp.addSelectionListener(new LsListener(this, this._fontColorListener));
	b.setMenu(m);
	
	b = this._fontBackgroundButton = new DwtButton(tb, null, "TBButton");
	b.setImage(ExImg.I_FONT_BACKGROUND);
	b.setToolTipContent(ExMsg.fontBackground);
	m = new DwtMenu(b, DwtMenu.COLOR_PICKER_STYLE);
	cp = new DwtColorPicker(m);
	cp.addSelectionListener(new LsListener(this, this._fontHiliteListener));
	b.setMenu(m);
	
	new DwtControl(tb, "vertSep");
	
	b = this._horizRuleButton = new DwtButton(tb, null, "TBButton");
	b.setImage(ExImg.I_HORIZ_RULE);
	b.setToolTipContent(ExMsg.horizRule);
	b.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.HORIZ_RULE);
	b.addSelectionListener(insElListener);
	
	new DwtControl(tb, "vertSep");
	
	b = this._toggleModeButton = new DwtButton(tb, null, "TBButton");
	b.setText(ExMsg.format);
	b.setToolTipContent(ExMsg.format);
	
	var m = new DwtMenu(b);
	b.setMenu(m);

	var mi = new DwtMenuItem(m, DwtMenuItem.RADIO_STYLE);
	mi.setImage(ExImg.I_HTML);
	mi.setText(ExMsg.htmlDocument);
	mi.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.HTML);
	mi.addSelectionListener(new LsListener(this, this._mailFormatListener));
	
	mi = new DwtMenuItem(m, DwtMenuItem.RADIO_STYLE);
	mi.setImage(ExImg.I_DOCUMENT);
	mi.setText(ExMsg.plainText);
	mi.setData(DwtHtmlEditorExample._VALUE, DwtHtmlEditor.TEXT);
	mi.addSelectionListener(new LsListener(this, this._mailFormatListener));	
}

DwtHtmlEditorExample.prototype._createStyleSelect =
function(tb) {
	var listener = new LsListener(this, this._styleListener);
	var s = this._styleSelect = new DwtSelect(tb, null);
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


DwtHtmlEditorExample.prototype._createFontFamilySelect =
function(tb) {
	var listener = new LsListener(this, this._fontNameListener);
	var s = this._fontFamilySelect = new DwtSelect(tb, null);
	s.addChangeListener(listener);
	
	s.addOption("Arial", false, DwtHtmlEditor.ARIAL);
	s.addOption("Times New Roman", true, DwtHtmlEditor.TIMES);
	s.addOption("Courier New", false, DwtHtmlEditor.COURIER);
	s.addOption("Verdana", false, DwtHtmlEditor.VERDANA);
}

DwtHtmlEditorExample.prototype._createFontSizeMenu =
function(tb) {
	var listener = new LsListener(this, this._fontSizeListener);
	var s = this._fontSizeSelect = new DwtSelect(tb, null);
	s.addChangeListener(listener);
	
	s.addOption("1 (8pt)", false, 1);
	s.addOption("2 (10pt)", false, 2);
	s.addOption("3 (12pt)", true, 3);
	s.addOption("4 (14pt)", false, 4);
	s.addOption("5 (18pt)", false, 5);
	s.addOption("6 (24pt)", false, 6);
	s.addOption("7 (36pt)", false, 7);
}

DwtHtmlEditorExample.prototype._rteStateChangeListener =
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


