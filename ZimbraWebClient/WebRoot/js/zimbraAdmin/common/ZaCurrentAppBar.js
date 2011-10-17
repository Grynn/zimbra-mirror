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
    this.clearTypeImg();
}

ZaCurrentAppBar.prototype = new DwtButton;
ZaCurrentAppBar.prototype.constructor = ZaCurrentAppBar ;
ZaCurrentAppBar.defalutImg = "Configure";

ZaCurrentAppBar.prototype.toString =
function() {
	return "ZaCurrentAppBar";
}


ZaCurrentAppBar.prototype.TEMPLATE = "admin.Widgets#ZaCurrentAppBar";

ZaCurrentAppBar.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtButton.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._typeImgEl = document.getElementById(data.id+"_typeimg");
    this._settingImgEl = document.getElementById(data.id+"_settingimg");
};

ZaCurrentAppBar.prototype.setSettingImg = function (imgName) {
    if (!this._settingImgEl)
        return;

    var localImg = imgName || ZaCurrentAppBar.defalutImg;
    this._settingImgEl.innerHTML = AjxImg.getImageHtml(localImg);
}

ZaCurrentAppBar.prototype.clearSettingImg = function () {
    if (!this._settingImgEl)
        return;

    this._settingImgEl.innerHTML = "";
}

ZaCurrentAppBar.prototype.setTypeImg = function (imgName) {
    if (!this._typeImgEl)
        return;
    if (!imgName)
        return;
    this._typeImgEl.innerHTML = AjxImg.getImageHtml(imgName);
    if (!Dwt.getVisible (this._typeImgEl))
        Dwt.setVisible (this._typeImgEl, true);
}

ZaCurrentAppBar.prototype.clearTypeImg = function () {
    if (!this._typeImgEl)
        return;

    this._typeImgEl.innerHTML = "";
    if (Dwt.getVisible (this._typeImgEl))
        Dwt.setVisible (this._typeImgEl, false);
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
    if (x > 13)
        x = x -13; //here is 13px is for extra padding.
	var y;

    var horizontalBorder = (parentElement.style.borderTopWidth == "") ? 0 : parseInt(parentElement.style.borderTopWidth);
    horizontalBorder += (parentElement.style.borderBottomWidth == "") ? 0 : parseInt(parentElement.style.borderBottomWidth);
    y = parentLocation.y + parentBounds.height + horizontalBorder;

	menu.popup(0, x, y);
};

ZaCurrentAppBar.prototype._isDropDownEvent =
function(ev) {
	if (this._dropDownEventsEnabled && this._dropDownEl) {
		var mouseX = ev.docX;
        if (this._settingImgEl) {
            var imgX =  Dwt.toWindow(this._settingImgEl, 0, 0, window).x;
            if (mouseX >= imgX)
                return true;
        }
	}
	return false;
};

ZaCurrentAppBar.prototype._handleClick =
function(ev) {
    // Nothing doing here
}

ZaCurrentAppBar.prototype.updateMenu =
function(popupOperations, popupOrder) {
    var oldMenu = this.getMenu();
    if(oldMenu) {
        this.setMenu("");
        this.menu = "";
        try {
            delete oldMenu;
        }catch(ex){
            //nothing doing here to avoid delete exception;
        }
    }

    if (popupOperations) {
        this.menu = new ZaPopupMenu(this, "ActionMenu", null, popupOperations, ZaId.CURRENT_APP_BAR, ZaId.MENU_POP, popupOrder);
        this.menu.setWidth(150);
        this.setSettingImg();
        this.setMenu(this.menu);
    } else {
        this.clearSettingImg();
    }

}
