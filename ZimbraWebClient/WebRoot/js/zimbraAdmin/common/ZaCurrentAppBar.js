/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 8/26/11
 * Time: 1:43 AM
 * To change this template use File | Settings | File Templates.
 */
ZaCurrentAppBar = function(parent, className, buttons) {

	DwtButton.call(this, parent, "", className, Dwt.ABSOLUTE_STYLE);
    this._removeUnwantedEvent();
    this._currentPathItems = new Array();
    this.menu = new ZaPopupMenu(this);
    this.menu.setWidth(Dwt.__checkPxVal(150,true));
    this.actionButtons = {};
    this.itemElArray = [];
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
ZaCurrentAppBar.prototype.ACTION_ITEM_TEMPLATE = "dwt.Widgets#ZToolbarItem";

ZaCurrentAppBar.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtButton.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._typeImgEl = document.getElementById(data.id+"_typeimg");
    this._settingImgEl = document.getElementById(data.id+"_settingimg");
    this._actionEl = document.getElementById(data.id+"_action");
    this._actionItemsEl = document.getElementById(data.id+"_actionitems");
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

ZaCurrentAppBar.prototype.setActionButton =
function (opList, appBarOrder) {
    if (AjxUtil.isEmpty(opList)) {
        this._clearActionButton();
    } else {
        this._addActionButton(opList, appBarOrder);
    }
}

ZaCurrentAppBar.prototype._addActionButton =
function (opList, appBarOrder) {

    this._clearActionButton();
    var b;

    if (AjxUtil.isEmpty(appBarOrder)) {
        for(var ix in opList) {
            b = this._createActionButton(opList[ix].id, opList[ix].imageId, opList[ix].caption, opList[ix].disImageId, opList[ix].tt, true, opList[ix].className, opList[ix].type, opList[ix].menuOpList);

            b.addSelectionListener(opList[ix].listener);
        }
    } else {
        var ix;
        for(var i in appBarOrder) {
            ix = appBarOrder[i];
            b = this._createActionButton(opList[ix].id, opList[ix].imageId, opList[ix].caption, opList[ix].disImageId, opList[ix].tt, true, opList[ix].className, opList[ix].type, opList[ix].menuOpList);

            b.addSelectionListener(opList[ix].listener);
        }
    }

    var width = Dwt.getBounds(this._actionItemsEl).width;
    this.setActionItemWidth(width);
}

ZaCurrentAppBar.prototype._createActionButton =
function(buttonId, imageId, text, disImageId, toolTip, enabled, className, type, menuOpList) {
	if (!className)
		className = "DwtToolbarButton"
	var b = this.actionButtons[buttonId] = new ZaToolBarButton({
			parent:this,
			className:className,
			id:ZaId.getButtonId("ZaCurrentAppBar",ZaOperation.getStringName(buttonId))
	});
	if (imageId)
		b.setImage(imageId);
	if (text)
		b.setText(text);
	if (toolTip)
		b.setToolTipContent(toolTip);
	b.setEnabled((enabled) ? true : false);
	b.setData("_buttonId", buttonId);

    var elContainer = this._createActionItemContainer();
    elContainer.appendChild(b.getHtmlElement());
    this._addActionItem(elContainer);

	return b;
}

ZaCurrentAppBar.prototype._createActionItemContainer =
function () {
    var itemCount = this.itemElArray.length + 1;
    var itemId = [this._htmlElId, "item", itemCount].join("_");

    var data = { id: this._htmlElId, itemId: itemId};
    var html = AjxTemplate.expand(this.ACTION_ITEM_TEMPLATE, data);

    var cont = AjxStringUtil.calcDIV();
    cont.innerHTML = html;
    return cont.firstChild.rows[0].cells[0];
}

ZaCurrentAppBar.prototype._addActionItem =
function(element) {
    // Always add the current action item to the last one
	var spliceIndex = this.itemElArray.length;
	this.itemElArray.splice(spliceIndex, 0, element);
    this._actionItemsEl.appendChild(element);
}

ZaCurrentAppBar.prototype._clearActionButton =
function() {
    for(var ix = 0; ix < this.itemElArray.length; ix++) {
        this.itemElArray[ix] = undefined;
    }
    this.itemElArray = [];
    this._actionItemsEl.innerHTML = "";
    for (var actionIx in this.actionButtons) {
        this.actionButtons[actionIx] = undefined;
    }
    this.actionButtons = {};
    this.setActionItemWidth(0);
}

ZaCurrentAppBar.prototype.setActionItemWidth =
function(w) {
    this._actionEl.style.width = Dwt.__checkPxVal(w);
}

ZaCurrentAppBar.prototype.enableButton =
function (buttonId, enabled) {
    if (!this.actionButtons[buttonId])
        return;

    this.actionButtons[buttonId].setEnabled(enabled);
}

ZaCurrentAppBar.spanItemClass = "";//"overviewHeader";
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

ZaCurrentAppBar.prototype.setDisplayState =
function(state, force) {
    if (state == DwtControl.HOVER ||
        state == DwtControl.ACTIVE||
        state == DwtControl.FOCUSED)
        state = DwtControl.NORMAL;

    if (this._selected && state != DwtControl.SELECTED && !force) {
        state = [ DwtControl.SELECTED, state ].join(" ");
    }
    DwtLabel.prototype.setDisplayState.call(this, state);
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
    //
    var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);

    mouseEv._stopPropagation = true;
	mouseEv._returnValue = true;
	mouseEv.setToDhtmlEvent(ev);
	return false;
}

ZaCurrentAppBar.prototype._removeUnwantedEvent =
function () {
    var events = [].concat(AjxEnv.isIE ? [DwtEvent.ONMOUSEENTER, DwtEvent.ONMOUSELEAVE] :
										 [DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT]);
	for (var i = 0; i < events.length; i++) {
		this.removeListener(events[i], this._listeners[events[i]]);
	}
}

ZaCurrentAppBar.prototype.updateMenu =
function(popupOperations, popupOrder) {
    var oldMenu = this.getMenu();
    var isPopup = false;
    if(oldMenu) {
        isPopup = oldMenu.isPoppedUp();
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
        this.menu.setWidth(Dwt.__checkPxVal(150,true));
        this.setSettingImg();
        this.setMenu(this.menu);
    } else {
        this.clearSettingImg();
    }

    if (isPopup) {
        this.popup();
    }
}

ZaCurrentAppBar.prototype.getMenu = function () {
    return this.menu;
}
