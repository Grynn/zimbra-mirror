/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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
 */

ZaFp = function() {
	ZaItem.call(this, ZaEvent.S_ACCOUNT);
	this.attrs = new Object();
	this.name="";
    this.prefix = "";
}

ZaFp.prototype = new ZaItem;
ZaFp.prototype.constructor = ZaFp;

ZaFp.A_name = "name" ;
ZaFp.A_index = "index";
ZaFp.A_prefix = "prefix" ;

ZaFp.myXModel = {
	items: [
		{id:ZaFp.A_name, type:_STRING_, ref:ZaFp.A_name},
		{id:ZaFp.A_index, type:_NUMBER_, ref:ZaFp.A_index},
        {id:ZaFp.A_prefix, type:_STRING_, ref:ZaFp.A_prefix}
    ]
}

//@entry: prefix:foreignEmailAccount
//return ZaFp object
ZaFp.getObject = function (entry) {
    var obj = {} ;
    var regEx = /(.+):(.*)/  ;
    var result = entry.match(regEx) ;
    if (result != null) {
        obj[ZaFp.A_prefix] = result [1] ;
        obj[ZaFp.A_name] = result [2] ;
    }else{
        obj[ZaFp.A_name] = entry ;
    }
    return obj ;
}

ZaFp.getEntry = function (obj) {
    var entry = "" ;
    if (obj != null) {
        entry = obj [ZaFp.A_prefix] + ":" + obj [ZaFp.A_name] ;
    }
    return entry  ;
}


ZaFp.push = function (app, id) {
    var soapDoc = AjxSoapDoc.create("PushFreeBusyRequest", ZaZimbraAdmin.URN, null);
	var entry = soapDoc.set("account", "");
	entry.setAttribute("id", id);
	try {
		params = new Object();
		params.soapDoc = soapDoc;
		var reqMgrParams ={
			controller: app.getCurrentController() ,
            asyncMode: false,
            busyMsg: ZaMsg.BUSY_PUSH_FP
        }
		resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.PushFreeBusyResponse;
	} catch (ex) {
		//show the error and go on
		//we should not stop the Account from loading if some of the information cannot be accessed
		app.getCurrentController()._handleException(ex, "ZaFp.push", null, false);
	}
}