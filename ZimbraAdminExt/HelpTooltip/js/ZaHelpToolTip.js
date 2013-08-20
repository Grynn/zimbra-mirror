/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
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