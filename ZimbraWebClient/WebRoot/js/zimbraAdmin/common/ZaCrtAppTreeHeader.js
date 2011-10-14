/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 8/17/11
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */

ZaCrtAppTreeHeader = function(parent, className, buttons) {

	DwtButton.call(this, parent, "", className, Dwt.ABSOLUTE_STYLE);
    this.preObj = null;
	this._historyMgr = ZaZimbraAdmin.getInstance().getHisotryMgr();
    this._historyMgr.addChangeListener(new AjxListener(this, this.updateMenu));
    this.menu = new ZaPopupMenu(this);
    this.menu.setWidth(150);
    this.setMenu(this.menu);
    this.setRightImg();
}

ZaCrtAppTreeHeader.prototype = new DwtButton;
ZaCrtAppTreeHeader.prototype.constructor = ZaCrtAppTreeHeader ;
ZaCrtAppTreeHeader.defalutImg = "FastRevArrowSmall";
ZaCrtAppTreeHeader.prototype.toString =
function() {
	return "ZaCrtAppTreeHeader";
}

ZaCrtAppTreeHeader.prototype.TEMPLATE = "admin.Widgets#ZaTreeHeaderButton";

ZaCrtAppTreeHeader.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtButton.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._arrowEl = document.getElementById(data.id+"_doubleArrow");
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
    if (this._isArrowEvent(ev)) {
        var tree = ZaZimbraAdmin.getInstance().getOverviewPanelController().getOverviewPanel().getFolderTree();
        var currentDataItem = tree.setSelectionByPath(this.preObj.path, false);
    }
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

ZaCrtAppTreeHeader.prototype._isArrowEvent =
function(ev) {
    if (this._arrowEl && this._textEl) {
        var arrowBounds = Dwt.getBounds(this._arrowEl);
        var textBounds = Dwt.getBounds(this._textEl);
        var start = arrowBounds.x;
        var end = textBounds.x + textBounds.width;
        var mouseX = ev.docX;
        if (mouseX < start)
            return false;
        if (mouseX >= end)
            return false;
        return true;
    }
    return false;
}

ZaCrtAppTreeHeader.prototype.setText = function (historyObject) {
    if (historyObject.path == "Home") {
       this.preObj = historyObject;
    } else {
       var tree = ZaZimbraAdmin.getInstance().getOverviewPanelController().getOverviewPanel().getFolderTree();
       var pathItems = tree.getPathItems(historyObject.path);
       pathItems.pop();
       var displayName = pathItems[pathItems.length - 1];
       var path = tree.getPathByArray(pathItems);
       this.preObj = new ZaHistory(path, displayName);
    }

	DwtLabel.prototype.setText.call(this, this.preObj.displayName);
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
    var allHistory = this._historyMgr.getAllHistory();
    for (i = 0; i < allHistory.size();i ++) {
        var currentHistory = allHistory.get(i);
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
    historyObject.goToView();
}

ZaCrtAppTreeHeader.prototype.clearHistory = function (ev) {
    this._historyMgr.removeHistory();
}

