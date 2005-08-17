/**
* @class ZaOperation
* @contructor
* simplified version of ZmOperation
* This class encapsulates the properties of an action that can be taken on some item: image, caption, description, AjxListener
* @param caption string
* @param tt string
* @param img string path to image
* @param lsnr AjxListener
**/

function ZaOperation(id, caption, tooltip, imgId, disImgId, lsnr) {
	this.id = id;
	this.caption = caption;
	this.tt = tooltip;
	this.listener = lsnr;
	this.imageId = imgId;
	this.disImageId = disImgId;
}

ZaOperation.prototype.toString = 
function() {
		return "ZaOperation";
}

// Operations
ZaOperation.NONE = -2;		// no operations or menu items
ZaOperation.SEP = -1;		// separator
ZaOperation.NEW = 1;
ZaOperation.DELETE = 2;
ZaOperation.REFRESH = 3;
ZaOperation.EDIT = 4;
ZaOperation.CHNG_PWD = 5;
ZaOperation.CLOSE = 6;
ZaOperation.SAVE = 7;
ZaOperation.NEW_WIZARD = 8;
ZaOperation.PAGE_FORWARD = 9;
ZaOperation.PAGE_BACK = 10;
ZaOperation.DUPLICATE = 11;
ZaOperation.GAL_WIZARD = 12;
ZaOperation.AUTH_WIZARD =13;
ZaOperation.VIEW_MAIL =14;
ZaOperation.MAIL_RESTORE = 15;