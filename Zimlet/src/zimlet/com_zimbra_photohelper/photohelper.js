/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV
 */

function com_zimbra_photohelper() {
}
com_zimbra_photohelper.prototype = new ZmZimletBase();
com_zimbra_photohelper.prototype.constructor = com_zimbra_photohelper;

com_zimbra_photohelper.prototype.init =
function() {
	this._photoLocatorURI = this.getConfig("photoLocatorURI");
	this._useProxy = this.getConfig("useProxy");
};

/**
 * Called by People search.
 * @param Object Result from GALSearch. This object must have photoname in "photoFileName" or
 *  in "jpegPhoto" property.
 *
 * Implement onPeopleSearchData and return photoUrl so that when people search is done, we can show people's photo
*/
com_zimbra_photohelper.prototype.onPeopleSearchData =
function(data) {
	var photoName = data["photoFileName"] ? data["photoFileName"] : (data["jpegPhoto"] ? data["jpegPhoto"] : "noname.jpg");
	var photoUrl = this._photoLocatorURI.replace("{photoFileName}", photoName).replace("{jpegPhoto}", photoName);
	photoUrl = this._useProxy ? ZmZimletBase.PROXY + photoUrl : photoUrl;
	data["photoUrl"] = photoUrl;
};
