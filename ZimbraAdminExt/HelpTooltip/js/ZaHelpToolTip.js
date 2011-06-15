/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 5/11/11
 * Time: 11:36 PM
 * To change this template use File | Settings | File Templates.
 */


ZaHelpTooltip = function() {

}

ZaHelpTooltip.prototype = new ZaItem();
ZaHelpTooltip.prototype.constructor = ZaHelpTooltip;

ZaHelpTooltip.A_description = "description";

ZaHelpTooltip.descriptionCache = {};
ZaHelpTooltip.cacheNumber = 0;
ZaHelpTooltip.getDescByName = function(name) {
    if(ZaHelpTooltip.descriptionCache[name] !== undefined){
        return ZaHelpTooltip.descriptionCache[name];
    }

    if(ZaHelpTooltip.cacheNumber > 50) {
        ZaHelpTooltip.descriptionCache = {};
    }

    ZaHelpTooltip.descriptionCache[name] =  ZaHelpTooltip.getDescBySoap(name);
    return ZaHelpTooltip.descriptionCache[name];
}

ZaHelpTooltip.getDescBySoap =
function(name) {
    var soapDoc = AjxSoapDoc.create("GetAttributeInfoRequest", ZaZimbraAdmin.URN, null);
    var el = soapDoc.setMethodAttribute("attrs", name);
    var params = new Object();
    params.soapDoc = soapDoc;
    var reqMgrParams = {
			controller: ZaApp.getInstance().getCurrentController(),
			busyMsg: ZaMsg.BUSY_GET_DESC
    };
    var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAttributeInfoResponse;
    var obj = "";
    if(resp && resp.a && resp.a[0]) {
        obj = resp.a[0].desc;
    }
    return obj;
}