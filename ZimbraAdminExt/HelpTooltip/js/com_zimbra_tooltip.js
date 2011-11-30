/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

if(window.console && window.console.log) {
    window.console.log("Starting loading com_zimbra_helptooltip.js")
}

if(ZaSettings && ZaSettings.EnabledZimlet["com_zimbra_tooltip"]){

if(XFormItem) {
    XFormItem.prototype.helpTooltip = true;

    XFormItem.prototype.showHelpTooltip =
    function (event) {
        var dwtEv = new DwtUiEvent(true);
        dwtEv.setFromDhtmlEvent(event);
        var content = this.getInheritedProperty("helpTooltipContent");
        if (!content) {
            var attributeName = this.getRefPath();
            if(!attributeName) {
                attributeName = this.getInheritedProperty("attributeName");
            }

            if(!attributeName) {
                return;
            }

            var findSlash = attributeName.lastIndexOf("/");
            if(findSlash != -1) {
               attributeName = attributeName.substring(findSlash);
            }

            content = attributeName;
        }

        if(!content)
            return;

        this._helpToolTipExt = new ZaHelpToolTipAdminExtension();
        this._helpToolTipExt.handleHover(dwtEv.docX, dwtEv.docY, content);
    }

    XFormItem.prototype.hideHelpTooltip =
    function (event) {
        if(!this._helpToolTipExt)
           return;

        this._helpToolTipExt.hoverOut();
    }
}

}

ZaHelpToolTipAdminExtension = function() {
};

ZaHelpToolTipAdminExtension.prototype.handleHover =
function(x, y, attributeName) {
    this.hoverOver = true;
    var shell = DwtShell.getShell(window);
    var tooltip = shell.getToolTip();
    tooltip.setContent("<div id=\"ZaHelpToolTipAdminExtension\"></div>", true);
    this.x = x;
    this.y = y;
    this.tooltip = tooltip;
    Dwt.setHandler(tooltip._div, DwtEvent.ONMOUSEOUT, AjxCallback.simpleClosure(this.hoverOut, this));
    this.canvas =   document.getElementById("ZaHelpToolTipAdminExtension");
	this.slideShow = new ZaToolTipView(this, this.canvas, attributeName);
    tooltip.popup(this.x, this.y, true);
}

ZaHelpToolTipAdminExtension.prototype.redraw =
function() {
    if(!this.tooltip)
        return;
    if(!this.x)
        return;
    if(!this.y)
        return;
    this.tooltip.popup(this.x, this.y, true);
}

ZaHelpToolTipAdminExtension.prototype.hoverOut =
function() {
	if(!this.tooltip) {	return;	}

	this._hoverOver =  false;
	this.tooltip._poppedUp = false;//makes the tooltip sticky
	setTimeout(AjxCallback.simpleClosure(this.popDownIfMouseNotOnSlide, this), 700);
}

ZaHelpToolTipAdminExtension.prototype.popDownIfMouseNotOnSlide =
function() {
    if(this._hoverOver) {
        return;
    } else if(this.slideShow && this.slideShow.isMouseOverTooltip) {
        return;
	} else if(this.tooltip) {
        this.tooltip._poppedUp = true;//makes the tooltip non-sticky
        this.tooltip.popdown();
    }
}

if(window.console && window.console.log) {
    window.console.log("loaded com_zimbra_helptooltip.js")
}
