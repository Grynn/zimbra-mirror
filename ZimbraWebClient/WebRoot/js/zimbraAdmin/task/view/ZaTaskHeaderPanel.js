/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 9/5/11
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */

ZaTaskHeaderPanel = function(parent) {
    DwtComposite.call(this, parent, "TaskHeaderPanel", Dwt.ABSOLUTE_STYLE);
    this._expanded =true;
    this.getHtmlElement().innerHTML = this.getImgHtml();
    this.getHtmlElement().onclick = AjxCallback.simpleClosure(ZaTaskHeaderPanel.__handleClick, this);
}

ZaTaskHeaderPanel.expandedImg =  "ImgCollapseRight";
ZaTaskHeaderPanel.collapsedImg =  "ImgCollapseLeft";

ZaTaskHeaderPanel.prototype = new DwtComposite;
ZaTaskHeaderPanel.prototype.constructor = ZaTaskHeaderPanel;

ZaTaskHeaderPanel.prototype.getImgHtml = function() {
   if (this._expanded) {
       return ["<div class='", ZaTaskHeaderPanel.expandedImg, "' ></div>"].join("");
   } else {
       return ["<div class='", ZaTaskHeaderPanel.collapsedImg, "' ></div>"].join("");
   }
}

ZaTaskHeaderPanel.__handleClick =
function(ev) {
    this._expanded = !this._expanded;
    this.getHtmlElement().innerHTML = this.getImgHtml();
    ZaZimbraAdmin.getInstance().getTaskController().setExpanded(this._expanded);
}

