function DwtToolBar(parent, className, posStyle, cellSpacing, cellPadding, style) {

	if (arguments.length == 0) return;
	className = className || "DwtToolBar";
	DwtComposite.call(this, parent, className, posStyle);
	
	this._style = style ? style : DwtToolBar.HORIZ_STYLE;
	this._table = this.getDocument().createElement("table");
	this._table.border = 0;
	this._table.cellPadding = cellPadding ? cellPadding : 0;
	this._table.cellSpacing = cellSpacing ? cellSpacing : 0;
	this._menuListeners = new LsVector();
	this.getHtmlElement().appendChild(this._table);
	this._table.backgroundColor = DwtCssStyle.getProperty(this.parent.getHtmlElement(), "background-color");

	this._numFillers = 0;
}

DwtToolBar.prototype = new DwtComposite;
DwtToolBar.prototype.constructor = DwtToolBar;

DwtToolBar.HORIZ_STYLE	= 1;
DwtToolBar.VERT_STYLE	= 2;

DwtToolBar.ELEMENT		= 1;
DwtToolBar.SPACER		= 2;
DwtToolBar.SEPARATOR	= 3;
DwtToolBar.FILLER		= 4;

DwtToolBar.DEFAULT_SPACER = 10;

DwtToolBar.prototype.toString = 
function() {
	return "DwtToolBar";
}

DwtToolBar.prototype.getItem =
function(index) {
	return this._children.get(index);
}

DwtToolBar.prototype.getItemCount =
function() {
	return this._children.size();
}

DwtToolBar.prototype.getItems =
function() {
	return this._children.toArray();
}

DwtToolBar.prototype.addSpacer =
function(size, index) {
	var el = this._createSpacerElement();
	var dimension = this._style == DwtToolBar.HORIZ_STYLE ? "width" : "height";
	el.style[dimension] = size || DwtToolBar.DEFAULT_SPACER;

	this._addItem(DwtToolBar.SPACER, el, index);
	return el;
}

DwtToolBar.prototype._createSpacerElement = function() {
	return this.getDocument().createElement("div");
}

DwtToolBar.prototype.addSeparator =
function(className, index) {
	var el = this._createSeparatorElement();
	el.className = className || this._defaultSepClass;
	this._addItem(DwtToolBar.SEPARATOR, el, index);
	return el;
}

DwtToolBar.prototype._createSeparatorElement = DwtToolBar.prototype._createSpacerElement;

DwtToolBar.prototype.addFiller =
function(className, index) {
	var el = this._createFillerElement();
	el.className = className || this._defaultFillClass;
	this._addItem(DwtToolBar.FILLER, el, index);
	return el;
}

DwtToolBar.prototype._createFillerElement = DwtToolBar.prototype._createSpacerElement;

DwtToolBar.prototype._addChild =
function(child, index) {
	this._children.add(child);
	this._addItem(DwtToolBar.ELEMENT, child.getHtmlElement(), index);
}

DwtToolBar.prototype._addItem =
function(type, element, index) {

	var row, col;
	if (this._style == DwtToolBar.HORIZ_STYLE) {
		row = (this._table.rows.length != 0) ? this._table.rows[0]: this._table.insertRow(0);
		row.align = "center";
		row.vAlign = "middle";
		
		var cellIndex = index || row.cells.length;
		col = row.insertCell(cellIndex);
		col.align = "center";
		col.vAlign = "middle";
		col.noWrap = true;
		// bug fix #33 - IE defines box model differently
		if (LsEnv.isIE)
			col.style.paddingRight = "4px";

		if (type == DwtToolBar.FILLER) {
			this._numFillers++;
			var perc = Math.floor(100 / this._numFillers);
			col.style.width = [perc, "%"].join("");
		} else {
			col.style.width = "1";
		}
			
		col.appendChild(element);
	} else {
		var rowIndex = index || this._table.rows.length;
		row = this._table.insertRow(rowIndex);
		row.align = "center";
		row.vAlign = "middle";
		
		col = row.insertCell(0);
		col.align = "center";
		col.vAlign = "middle";
		col.noWrap = true;

		if (type == DwtToolBar.FILLER) {
			this._numFillers++;
			var perc = Math.floor(100 / this._numFillers);
			col.style.height = [perc, "%"].join("");
		}

		col.appendChild(element);
	}
}
