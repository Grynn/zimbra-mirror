/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 8/29/11
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
ZaHome = function() {
	ZaItem.call(this,"ZaHome");
	this._init();
}
ZaItem.loadMethods["ZaHome"] = new Array();
ZaItem.initMethods["ZaHome"] = new Array();

ZaHome.prototype = new ZaItem;
ZaHome.prototype.constructor = ZaHome;

ZaHome.A2_version= "version";
ZaHome.A2_account = "account";

ZaHome.initMethod = function () {
	this.attrs = new Object();
	this.type = ZaItem.HOME;
}
ZaItem.initMethods["ZaHome"].push(ZaHome.initMethod);

// Fake here no soap request, just collect all kinds of information everywhere
ZaHome.loadMethod =
function () {
    this.attrs[ZaHome.A2_account] = ZaZimbraAdmin.currentAdminAccount.attrs.mail;
    this.attrs[ZaHome.A2_version] = ZaServerVersionInfo.version;
}

ZaItem.loadMethods["ZaHome"].push(ZaHome.loadMethod);

ZaHome.myXModel = {
    items: [
        {id:ZaHome.A2_version,type:_STRING_,  ref:"attrs/" + ZaHome.A2_version},
    	{id:ZaHome.A2_account,type:_STRING_, ref:"attrs/" + ZaHome.A2_account}
    ]
}

