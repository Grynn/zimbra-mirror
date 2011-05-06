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

if(XFormItem) {
    XFormItem.prototype.helpTooltip = true;

    XFormItem.prototype.showHelpTooltip =
    function (event) {
        var dwtEv = new DwtUiEvent(true);
        dwtEv.setFromDhtmlEvent(event);
        var content = this.getInheritedProperty("helpTooltipContent");
        if (!content) {
            var attributeName = this.getRefPath();
            if(!attributeName)
                return;

            var findSlash = attributeName.lastIndexOf("/");
            if(findSlash != -1) {
               attributeName = attributeName.substring(findSlash);
            }

            content = attributeName;
        }

        var shell = DwtShell.getShell(window);
        var tooltip = shell.getToolTip();
        tooltip.setContent(attributeName);
        tooltip.popup(dwtEv.docX, dwtEv.docY);
    }

    XFormItem.prototype.hideHelpTooltip =
    function (event) {
        var shell = DwtShell.getShell(window);
        var tooltip = shell.getToolTip();
        tooltip.popdown();
    }
}

if(Output_XFormItem) {
    Output_XFormItem.prototype.helpTooltip = false;
}

ZaHelpTooltip = function() {

}

ZaHelpTooltip.prototype = new ZaItem();
ZaHelpTooltip.prototype.constructor = ZaHelpTooltip;

ZaHelpTooltip.A_description = "description";

ZaHelpTooltip.descriptionCache = {};
ZaHelpTooltip.cacheNumber = 0;
ZaHelpTooltip.getDescByName = function(name) {
    if(ZaHelpTooltip.descriptionCache[name]){
        return ZaHelpTooltip.descriptionCache[name];
    }
}

if(window.console && window.console.log) {
    window.console.log("loaded com_zimbra_helptooltip.js")
}
