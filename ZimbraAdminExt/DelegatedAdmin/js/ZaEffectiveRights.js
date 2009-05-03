ZaEffectiveRights = function (grantee) {
    ZaItem.call (this, "ZaEffectiveRights") ;
    this.type = "ZaEffectiveRights" ;
    
    if (!grantee) return ;
    this.grantee = {
        id: grantee.id ,
        name: grantee.name 
    }

    if (grantee.type == ZaItem.ACCOUNT) {
        this.grantee.type = ZaGrant.GRANTEE_TYPE.usr;
    } else if (grantee.type == ZaItem.DL) {
        this.grantee.type = ZaGrant.GRANTEE_TYPE.grp;        
    } else {
        throw new AjxException("Invalid Grantee Type");
        return ;
    }
}

ZaEffectiveRights.prototype = new ZaItem ;
ZaEffectiveRights.prototype.constructor = ZaEffectiveRights ;

ZaEffectiveRights.A_grantee = "grantee" ;

ZaEffectiveRights.prototype.load =
function () {
    var soapDoc = AjxSoapDoc.create("GetAllEffectiveRightsRequest", ZaZimbraAdmin.URN, null);
    var elGrantee = soapDoc.set("grantee", this.grantee.id);
    elGrantee.setAttribute("type", this.grantee.type) ;
    elGrantee.setAttribute("by", "id") ;
    
    var params = {};
    params.soapDoc = soapDoc;
    params.asyncMode = false;
    var reqMgrParams = {
        controller : ZaApp.getInstance().getCurrentController(),
        busyMsg : com_zimbra_delegatedadmin.BUSY_GET_ALL_EFFECTIVE_RIGHTS
    }
    var resp = ZaRequestMgr.invoke(params, reqMgrParams);
    this.targets = resp.Body.GetAllEffectiveRightsResponse.target;
    
}

ZaEffectiveRights.myXModel = {
    items:[
        {id: ZaEffectiveRights.A_grantee, ref: ZaEffectiveRights.A_grantee, type: _OBJECT_, items: [
                {id: "id", type: _STRING_, ref: "id" },
                {id: "name", type: _STRING_, ref: "name" },
                {id: "type", type: _STRING_, ref: "type" }
            ]
        }
    ]
};





