/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 8/26/11
 * Time: 1:43 AM
 * To change this template use File | Settings | File Templates.
 */
ZaCurrentAppBar = function(parent, className, buttons) {

	DwtButton.call(this, parent, "", className, Dwt.ABSOLUTE_STYLE);
    this._currentPathItems = new Array();
    this.menu = new ZaPopupMenu(this);
    this.menu.setWidth(150);
    this.setMenu(this.menu);
    this.setSettingImg();
}

ZaCurrentAppBar.prototype = new DwtButton;
ZaCurrentAppBar.prototype.constructor = ZaCurrentAppBar ;
ZaCurrentAppBar.defalutImg = "Help";
ZaCurrentAppBar.prototype.toString =
function() {
	return "ZaCurrentAppBar";
}


ZaCurrentAppBar.prototype.TEMPLATE = "admin.Widgets#ZaCurrentAppBar";

ZaCurrentAppBar.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtButton.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._imgEl = document.getElementById(data.id+"_img");
};

ZaCurrentAppBar.prototype.setSettingImg = function (imgName) {
    if (!this._imgEl)
        return;

    var localImg = imgName || ZaCurrentAppBar.defalutImg;
    this._imgEl.innerHTML = AjxImg.getImageHtml(localImg);
}

ZaCurrentAppBar.prototype.setText =
function(path) {
    var text = "";
    var temp = path.split(ZaTree.SEPERATOR);
    this._currentPathItems = temp;
    for (var i = 0; i < temp.length; i++) {
        text +=this._getSinglePathItem(temp[i]);
        if (i != temp.length - 1)
            text += "-";
    }
    DwtButton.prototype.setText.call(this, text);
}

ZaCurrentAppBar.spanItemClass = "overviewHeader";
ZaCurrentAppBar.prototype._getSinglePathItem =
function(path) {
    var text = new Array(10);
    var i = 0;
    text[i++] = "<span class=";
    text[i++] = ZaCurrentAppBar.spanItemClass;
    text[i++] = ">";
    text[i++] = path;
    text[i++] = "</span>";
    return text.join("");
}

ZaCurrentAppBar.prototype.popup =
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
        x = dropDownLocation.x + 20;
    }
	x = ((x - menuSize.x) >= 0) ? x - menuSize.x : x;

	var y;

    var horizontalBorder = (parentElement.style.borderTopWidth == "") ? 0 : parseInt(parentElement.style.borderTopWidth);
    horizontalBorder += (parentElement.style.borderBottomWidth == "") ? 0 : parseInt(parentElement.style.borderBottomWidth);
    y = parentLocation.y + parentBounds.height + horizontalBorder;

	menu.popup(0, x, y);
};