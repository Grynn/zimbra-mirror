/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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

/**
 * Constructor.
 * 
 * @author Raja Rao DV (rrao@zimbra.com)
 */
function ZmAboutZimlet() {
}

ZmAboutZimlet.prototype = new ZmZimletBase();
ZmAboutZimlet.prototype.constructor = ZmAboutZimlet;

ZmAboutZimlet.prototype._getContent = function() {
	var subs = {
			version : appCtxt.getSettings().getInfoResponse.version,
			userAgent : [this.getMessage("userAgent"), " ", navigator.userAgent].join(""),
			copyright: this.getMessage("copyright")
		};
		return AjxTemplate.expand(
				"com_zimbra_about.templates.About#DialogView", subs);

};

/**
 * Called when user single-clicks on the panel
 */
ZmAboutZimlet.prototype.singleClicked = function() {
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();
	var content = this._getContent();
	dlg.setTitle(this.getMessage("label"));
	dlg.setContent(content);
	dlg.popup();
};

/**
 * Called when user double-clicks on the panel
 */
ZmAboutZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};
