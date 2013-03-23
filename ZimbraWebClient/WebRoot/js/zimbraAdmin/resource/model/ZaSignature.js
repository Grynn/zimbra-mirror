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
 * Date: 5/24/11
 * Time: 10:28 PM
 * To change this template use File | Settings | File Templates.
 */

ZaSignature =
function(name, id, content, type) {
    this.id = id;
    this.name = name;
    this.content = content;
    this.type = type ? type: "text/plain";
}

ZaSignature.prototype.constructor = ZaSignature;
ZaSignature.prototype.toString =
function() {
    return this.name;
}

ZaSignature.compareObject =
function(t1, t2){
    for(var i in t1){
        if(t1[i] != t2[i]){
            return false;
        }
    }
    return true;
}

ZaSignature.A2_name = "name";
ZaSignature.A2_id = "id";
ZaSignature.A2_content = "content";
ZaSignature.A2_type = "type";

ZaSignature.myXModel = {
    items:[
        {id:ZaSignature.A2_id, type:_STRING_, ref:ZaSignature.A2_id},
        {id:ZaSignature.A2_name, type:_STRING_, ref:ZaSignature.A2_name},
        {id:ZaSignature.A2_content, type:_STRING_, ref:ZaSignature.A2_content},
        {id:ZaSignature.A2_type, type:_STRING_, ref:ZaSignature.A2_type}
    ]
}

ZaSignature.signatureTypeChoices = [
    {value:"text/plain", label:ZaMsg.resSignaturePlainType},
    {value:"text/html", label:ZaMsg.resSignatureHTMLType}
];

ZaSignature.getSignatureChoices =
function (arr) {
    var result = [];
    result.push({name:ZaMsg.VALUE_NOT_SET, id:""});
    var i = 0;
    for(i = 0; i < arr.length; i++){
       result.push({name:arr[i].name, id:arr[i].id});
    }
    return result;
}

ZaSignature.getNewSignatureChoices =
function (arr) {
    var result = [];
    result.push({name:ZaMsg.VALUE_NOT_SET, id:""});
    var i = 0;
    for(i = 0; i < arr.length; i++){
        if(arr[i].name)
            result.push({name:arr[i].name, id:arr[i].name});
    }
    return result;
}

ZaSignature.GetSignatures =
function(by, val) {
    var soapDoc = AjxSoapDoc.create("GetSignaturesRequest", "urn:zimbraAccount", null);

    var params = new Object();
    params.soapDoc = soapDoc;
    params.skipAuthCheck = false;
    if(by == "id"){
        params.accountId = val;
    } else {
        params.accountName = val;
    }

    try{
        var reqMgrParams = {
            controller : ZaApp.getInstance().getCurrentController(),
            busyMsg : ZaMsg.BUSY_GET_RESOURCE
        };

        this[ZaResource.A2_signatureList] = [];
        var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetSignaturesResponse;
        if( resp.signature) {
            for (var i in resp.signature) {
                var _content;
                var _type;
                if(resp.signature[i].content && resp.signature[i].content[0]){
                    _content =  resp.signature[i].content[0]._content;
                    _type = resp.signature[i].content[0].type;
                }
                var currentSignature = new ZaSignature(resp.signature[i].name,
                                            resp.signature[i].id,
                                            _content,
                                            _type);
                this[ZaResource.A2_signatureList].push(currentSignature);
            }

        }

    } catch(ex) {
        throw ex;
        return null;
    }
}

ZaSignature.CreateSignature =
function(by, val) {
    var soapDoc = AjxSoapDoc.create("CreateSignatureRequest", "urn:zimbraAccount", null);
    var signBy = soapDoc.set("signature", null);
    signBy.setAttribute("name", this[ZaSignature.A2_name]);
    var contentBy;
    if(this[ZaSignature.A2_type] == "text/plain"){
        contentBy = soapDoc.set("content", this[ZaSignature.A2_content], signBy);
        contentBy.setAttribute("type", "text/plain");

        contentBy = soapDoc.set("content", null, signBy);
        contentBy.setAttribute("type", "text/html");
    } else {
        contentBy = soapDoc.set("content", this[ZaSignature.A2_content], signBy);
        contentBy.setAttribute("type", "text/html");

        contentBy = soapDoc.set("content", null, signBy);
        contentBy.setAttribute("type", "text/plain");
    }
    var params = new Object();
    params.soapDoc = soapDoc;
    params.skipAuthCheck = false;
    if(by == "id"){
        params.accountId = val;
    } else {
        params.accountName = val;
    }

    try{
        var reqMgrParams = {
            controller : ZaApp.getInstance().getCurrentController(),
            busyMsg : ZaMsg.BUSY_GET_RESOURCE
        };

        var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.CreateSignatureResponse;
        if( resp.signature && resp.signature[0]) {
            this.id = resp.signature[0].id;
        }
        return this;
    } catch(ex) {
        throw ex;
        return null;
    }
}

ZaSignature.CreateAccountSignature =
function(tmpObj, resource){
    if (!AjxUtil.isEmpty(tmpObj[ZaResource.A2_signatureList])) {
        for(var i = 0; i < tmpObj[ZaResource.A2_signatureList].length; i++) {
            var current = tmpObj[ZaResource.A2_signatureList][i];
            var newSign;
            if (current[ZaSignature.A2_name] && current[ZaSignature.A2_content]){
                newSign = new ZaSignature(current[ZaSignature.A2_name], "",
                                          current[ZaSignature.A2_content],
                                          current[ZaSignature.A2_type]);
                ZaSignature.CreateSignature.call(newSign, "id", resource.id);
                tmpObj[ZaResource.A2_signatureList][i] = newSign;
            }
        }
        var mods = {};
        var index;
        if (tmpObj.attrs[ZaResource.A_zimbraPrefCalendarAutoAcceptSignatureId]) {
            index = ZaUtil.findValueInObjArrByPropertyName(tmpObj[ZaResource.A2_signatureList],
                        tmpObj.attrs[ZaResource.A_zimbraPrefCalendarAutoAcceptSignatureId]);
            if (index != -1 && tmpObj[ZaResource.A2_signatureList][index].id) {
                mods[ZaResource.A_zimbraPrefCalendarAutoAcceptSignatureId] = tmpObj[ZaResource.A2_signatureList][index].id;
            }
        }

        if (tmpObj.attrs[ZaResource.A_zimbraPrefCalendarAutoDenySignatureId]) {
            index = ZaUtil.findValueInObjArrByPropertyName(tmpObj[ZaResource.A2_signatureList],
                        tmpObj.attrs[ZaResource.A_zimbraPrefCalendarAutoDenySignatureId]);
            if (index != -1 && tmpObj[ZaResource.A2_signatureList][index].id) {
                mods[ZaResource.A_zimbraPrefCalendarAutoDenySignatureId] = tmpObj[ZaResource.A2_signatureList][index].id;
            }
        }

        if (tmpObj.attrs[ZaResource.A_zimbraPrefCalendarAutoDeclineSignatureId]) {
            index = ZaUtil.findValueInObjArrByPropertyName(tmpObj[ZaResource.A2_signatureList],
                        tmpObj.attrs[ZaResource.A_zimbraPrefCalendarAutoDeclineSignatureId]);
            if (index != -1 && tmpObj[ZaResource.A2_signatureList][index].id) {
                mods[ZaResource.A_zimbraPrefCalendarAutoDeclineSignatureId] = tmpObj[ZaResource.A2_signatureList][index].id;
            }
        }

        if(!AjxUtil.isEmpty(mods))
            ZaResource.modifyMethod.call(resource, mods);
    }

    return resource;
}

ZaSignature.ModifySignature =
function(by, val) {
    var soapDoc = AjxSoapDoc.create("ModifySignatureRequest", "urn:zimbraAccount", null);
    var signBy = soapDoc.set("signature", null);
    signBy.setAttribute("name", this[ZaSignature.A2_name]);
    signBy.setAttribute("id", this[ZaSignature.A2_id]);
    var contentBy;
    if(this[ZaSignature.A2_type] == "text/plain"){
        contentBy = soapDoc.set("content", this[ZaSignature.A2_content], signBy);
        contentBy.setAttribute("type", "text/plain");

        contentBy = soapDoc.set("content", null, signBy);
        contentBy.setAttribute("type", "text/html");
    } else {
        contentBy = soapDoc.set("content", this[ZaSignature.A2_content], signBy);
        contentBy.setAttribute("type", "text/html");

        contentBy = soapDoc.set("content", null, signBy);
        contentBy.setAttribute("type", "text/plain");
    }

    var params = new Object();
    params.soapDoc = soapDoc;
    params.skipAuthCheck = false;
    if(by == "id"){
        params.accountId = val;
    } else {
        params.accountName = val;
    }

    try{
        var reqMgrParams = {
            controller : ZaApp.getInstance().getCurrentController(),
            busyMsg : ZaMsg.BUSY_GET_RESOURCE
        };

        ZaRequestMgr.invoke(params, reqMgrParams).Body.ModifySignatureResponse;

    } catch(ex) {
        throw ex;
        return null;
    }
}


ZaSignature.DeleteSignature =
function(by, val) {
    var soapDoc = AjxSoapDoc.create("DeleteSignatureRequest", "urn:zimbraAccount", null);
    var signBy = soapDoc.set("signature", null);
    signBy.setAttribute("id", this[ZaSignature.A2_id]);

    var params = new Object();
    params.soapDoc = soapDoc;
    params.skipAuthCheck = false;
    if(by == "id"){
        params.accountId = val;
    } else {
        params.accountName = val;
    }

    try{
        var reqMgrParams = {
            controller : ZaApp.getInstance().getCurrentController(),
            busyMsg : ZaMsg.BUSY_GET_RESOURCE
        };

        var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.DeleteSignatureResponse;

    } catch(ex) {
        throw ex;
        return null;
    }
}