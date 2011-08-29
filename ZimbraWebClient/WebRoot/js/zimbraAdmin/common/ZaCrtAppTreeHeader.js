/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 8/17/11
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */

ZaCrtAppTreeHeader = function(parent, className, buttons) {

	DwtButton.call(this, parent, "", className, Dwt.ABSOLUTE_STYLE);
	this._history = new AjxVector();
    this.menu = new ZaPopupMenu(this);
    this.menu.setWidth(150);
    this.setMenu(this.menu);
    this.setRightImg();
}

ZaCrtAppTreeHeader.prototype = new DwtButton;
ZaCrtAppTreeHeader.prototype.constructor = ZaCrtAppTreeHeader ;
ZaCrtAppTreeHeader.defalutImg = "Help";
ZaCrtAppTreeHeader.prototype.toString =
function() {
	return "ZaCrtAppTreeHeader";
}

ZaCrtAppTreeHeader.prototype.TEMPLATE = "admin.Widgets#ZaTreeHeaderButton";

ZaCrtAppTreeHeader.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtButton.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._imgEl = document.getElementById(data.id+"_img");
};

ZaCrtAppTreeHeader.prototype.setRightImg = function (imgName) {
    if (!this._imgEl)
        return;

    var localImg = imgName || ZaCrtAppTreeHeader.defalutImg;
    this._imgEl.innerHTML = AjxImg.getImageHtml(localImg);
}

ZaCrtAppTreeHeader.prototype._handleClick =
function(ev) {
    // Nothing doing here
}

ZaCrtAppTreeHeader.prototype._isDropDownEvent =
function(ev) {
	if (this._dropDownEventsEnabled && this._dropDownEl) {
		var mouseX = ev.docX;
		var dropDownX = Dwt.toWindow(this._dropDownEl, 0, 0, window).x;
        var isAfterDropDown = true;
		if (mouseX < dropDownX) {
			isAfterDropDown = false;
		}
        var isBeforeCollapse =  true;
        if (this._imgEl) {
            var imgX =  Dwt.toWindow(this._imgEl, 0, 0, window).x;
            if (mouseX >= imgX)
                isBeforeCollapse = false;
        }
        return isAfterDropDown && isBeforeCollapse;
	}
	return false;
};

ZaCrtAppTreeHeader.prototype.setText = function (path) {
    var pathItem = path.split();
	DwtLabel.prototype.setText.call(this, path);
}

ZaCrtAppTreeHeader.prototype.addHistory = function (history) {
    if (!history)
        return false;
    this._history.add(history);
    this.updateMenu();
    return true;
}

ZaCrtAppTreeHeader.prototype.popup =
function(menu) {
	menu = menu || this.getMenu();

    if (!menu) { return; }

    var parent = menu.parent;
	var parentBounds = parent.getBounds();
	var windowSize = menu.shell.getSize();
	var menuSize = menu.getSize();
	var parentElement = parent.getHtmlElement();
	// since buttons are often absolutely positioned, and menus aren't, we need x,y relative to window
	var parentLocation = Dwt.toWindow(parentElement, 0, 0);
	var leftBorder = (parentElement.style.borderLeftWidth == "") ? 0 : parseInt(parentElement.style.borderLeftWidth);

	var x;
    var dropDownEl = parent._dropDownEl;
    if (!dropDownEl) {
	    x = parentLocation.x + leftBorder;
    } else {
        var dropDownLocation = Dwt.toWindow(dropDownEl, 0, 0);
        x = dropDownLocation.x;
    }
	x = ((x + menuSize.x) >= windowSize.x) ? windowSize.x - menuSize.x : x;

	var y;

    var horizontalBorder = (parentElement.style.borderTopWidth == "") ? 0 : parseInt(parentElement.style.borderTopWidth);
    horizontalBorder += (parentElement.style.borderBottomWidth == "") ? 0 : parseInt(parentElement.style.borderBottomWidth);
    y = parentLocation.y + parentBounds.height + horizontalBorder;

	menu.popup(0, x, y);
};

ZaCrtAppTreeHeader.prototype.createMenu = function
() {
    var i = 0;
    var mi;
    var listener = new AjxListener(this, this.goToTreeItemListener);
    for (i = 0; i < this._history.size();i ++) {
        var currentHistory = this._history.get(i);
        mi = new DwtMenuItem({
		                parent: this.menu,
		                style:		DwtMenuItem.NO_STYLE,
		                id:     ZaId.getMenuItemId(this._contextId, i + currentHistory.path)
	    });
        mi.setText(currentHistory.displayName);
        mi.setData("history", currentHistory);
        mi.addSelectionListener(listener);
    }

    mi = new DwtMenuItem({
                    parent: this.menu,
                    style:		DwtMenuItem.NO_STYLE,
                    id:     ZaId.getMenuItemId(this._contextId, "clearHistory")
    });
    mi.setText("Clear History");
    mi.setData("history", currentHistory);
    mi.addSelectionListener(new AjxListener(this, this.clearHistory));
}

ZaCrtAppTreeHeader.prototype.updateMenu =
function() {
    this.menu.removeChildren();
    this.createMenu();
}

ZaCrtAppTreeHeader.prototype.goToTreeItemListener = function (ev) {
    var historyObject =  ev.item.getData("history");
    // TODO
    var tree = ZaZimbraAdmin.getInstance().getOverviewPanelController().getOverviewPanel().getFolderTree();
    var currentDataItem = tree.setSelectionByPath(historyObject.path, false);
    window.console.log("To select " + historyObject.path);
}

ZaCrtAppTreeHeader.prototype.clearHistory = function (ev) {
	for (var i = 1; i < this._history._array.length; i++)
		this._history._array[i] = null;
	this._history._array.length = 1;
    this.updateMenu();
    window.console.log("clear history");
}

ZaTreeHistory = function (path, displayName) {
    this.path = path;
    this.displayName = displayName;
}
