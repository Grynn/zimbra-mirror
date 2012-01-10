/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
/*
* ZmVoicePrefsPhoneList
* The list of phone accounts.
*/
ZmVoicePrefsPhoneList = function(parent) {
	var headerList = [new DwtListHeaderItem({field:1, text:ZmMsg.number})];
	DwtListView.call(this, {parent:parent, className:"ZmVoicePrefsPhoneList", headerList:headerList});

	this.multiSelectEnabled = false;
};

ZmVoicePrefsPhoneList.prototype = new DwtListView;
ZmVoicePrefsPhoneList.prototype.constructor = ZmVoicePrefsPhoneList;

ZmVoicePrefsPhoneList.prototype.toString =
function() {
	return "ZmVoicePrefsPhoneList";
};

ZmVoicePrefsPhoneList.prototype._getCellContents =
function(htmlArr, idx, phone, field, colIdx, params) {
	htmlArr[idx++] = AjxStringUtil.htmlEncode(phone.getDisplay(), true);
	return idx;
};
