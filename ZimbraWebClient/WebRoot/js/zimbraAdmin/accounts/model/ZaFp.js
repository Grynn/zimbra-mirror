/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010, 2013 Zimbra Software, LLC.
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
//ZaFp.A_providers = "providers" ;

ZaFp.INTEROP_PROVIDER_CHOICES = [] ;

ZaFp.getXModel = function ()
{
    var model = { items:
      [
    	{id:"getAttrs",type:_LIST_},
    	{id:"setAttrs",type:_LIST_},
    	{id:"rights",type:_LIST_},      
		{id:ZaFp.A_name, type:_STRING_, ref:ZaFp.A_name},
		{id:ZaFp.A_index, type:_NUMBER_, ref:ZaFp.A_index},
        {id:ZaFp.A_prefix, type:_STRING_, ref:ZaFp.A_prefix}
      ]};
    return model ;
}

//@entry: prefix:foreignEmailAccount
//return ZaFp object
ZaFp.getObject = function (entry) {
    var obj = {} ;
    //var regEx = /(.+):(.*)/  ;
    //var result = entry.match(regEx) ;
    var found = false;
    for (var i=0; i < ZaFp.INTEROP_PROVIDER_CHOICES.length; i ++) {
       if (entry.indexOf(ZaFp.INTEROP_PROVIDER_CHOICES[i].value) == 0) {
           obj[ZaFp.A_prefix] = ZaFp.INTEROP_PROVIDER_CHOICES[i].value;
           obj[ZaFp.A_name] = entry.substr(ZaFp.INTEROP_PROVIDER_CHOICES[i].value.length);
           found = true ;
           break;
       }
    }
    if (! found) {
        obj[ZaFp.A_name] = entry ;
    }

    return obj;
}

ZaFp.getEntry = function (obj) {
    var entry = "" ;
    if (obj != null) {
        entry = obj [ZaFp.A_prefix] + obj [ZaFp.A_name] ;
    }
    return entry  ;
}


ZaFp.push = function (id) {
    var soapDoc = AjxSoapDoc.create("PushFreeBusyRequest", ZaZimbraAdmin.URN, null);
	var entry = soapDoc.set("account", "");
	entry.setAttribute("id", id);
	try {
		params = new Object();
		params.soapDoc = soapDoc;
		var reqMgrParams ={
			controller: ZaApp.getInstance().getCurrentController() ,
            asyncMode: false,
            busyMsg: ZaMsg.BUSY_PUSH_FP
        }
		resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.PushFreeBusyResponse;
        ZaApp.getInstance().getCurrentController().popupMsgDialog (ZaMsg.PUSH_SUCCEED, true);
    } catch (ex) {
		//show the error and go on
		//we should not stop the Account from loading if some of the information cannot be accessed
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaFp.push", null, false);
	}
}

ZaFp.getProviders = function () {
    var soapDoc = AjxSoapDoc.create("GetAllFreeBusyProvidersRequest", ZaZimbraAdmin.URN, null);
    try {
		params = new Object();
		params.soapDoc = soapDoc;
		var reqMgrParams ={
			controller: ZaApp.getInstance().getCurrentController() ,
            asyncMode: false,
            busyMsg: ZaMsg.BUSY_GET_INTEROP_PROVIDERS
        }
		var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllFreeBusyProvidersResponse;
	    ZaFp.INTEROP_PROVIDER_CHOICES = [] ;
        var providers = resp.provider ;

        if (providers != null) {
            for (var i=0; i < providers.length; i ++) {
                ZaFp.INTEROP_PROVIDER_CHOICES.push (
                    { value: providers[i].prefix,
                      label: providers[i].name }
                );
            }
        }
        
    } catch (ex) {
		//show the error and go on
		//we should not stop the Account from loading if some of the information cannot be accessed
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaFp.push", null, false);
	}
}

//@entries: foreign principal entries
//@prefix: foreign principal prefixes
ZaFp.findDupPrefixFp = function (entries, prefix) {
    for (var i =0 ; i < entries.length; i ++) {
        if (entries[i].indexOf(prefix) == 0) {
            return true;
        }
    }

    return false;
}