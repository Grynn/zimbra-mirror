ZaGrant = function () {
    ZaItem.call(this, "ZaGrant") ;
    this._init () ;
    this.type = ZaItem.GRANT ;

}

ZaGrant.prototype = new ZaItem ;
ZaGrant.prototype.constructor = ZaGrant ;

ZaItem.loadMethods ["ZaGrant"] = [] ;
ZaItem.initMethods["ZaGrant"] = [] ;

ZaGrant.A_id = "id" ;
ZaGrant.A_by = "by" ;
ZaGrant.A_grantee = "grantee" ;
ZaGrant.A_grantee_type = "grantee_type" ;
ZaGrant.A_right = "right" ;
ZaGrant.A_deny = "deny" ;
ZaGrant.A_target = "target" ;
ZaGrant.A_target_type = "target_type"
ZaGrant.A_right_type = "right_type" ;
ZaGrant.A_inline_right = "inline_right" ;
ZaGrant.A_inline_verb = "verb" ;
ZaGrant.A_inline_target_type = "inline_target_type" ;
ZaGrant.A_inline_attr = "inline_attr" ;


ZaGrant.A2_grantsList = "grantsList" ;

ZaGrant.GRANTEE_TYPE = ["usr", "grp"] ;

ZaGrant.RIGHT_TYPE_CHOICES =[
    {value:"system", label:com_zimbra_delegatedadmin.Col_system_right},
    {value:"inline", label:com_zimbra_delegatedadmin.Col_inline_right}
];

ZaGrant.INLINE_VERB_TYPE_CHOICES = [
    {value:"getAttr", label:com_zimbra_delegatedadmin.Col_inline_verb_set},
    {value:"setAttr", label:com_zimbra_delegatedadmin.Col_inline_verb_get}
]

 /*
 A sample grant on a target:
   {
      type: usr or grp
      id: 75b0677b-6ed1-4f0a-a37e-e5b24e4c2d22  (grantee-zimbraId)
      name: user1 (grantee-name)
      right: createAccount (right-name)
      deny: 1 | 0 (default)
  }

  */
ZaGrant.getSampleGrants = function () {
    var grant1 = new ZaGrant ()  ;
    var grant2 = new ZaGrant () ;
    grant1.grantee_type =  "usr" ;
    grant1.id = "75b0677b-6ed1-4f0a-a37e-e5b24e4c2d22" ;
    grant1.grantee = "user1@ccaomac.zimbra.com" ;
    grant1.right = "createAccount" ;
    grant1.deny = "1" ;

    grant2.grantee_type = "grp";
    grant2.id = "75b0677b-6ed1-4f0a-a37e-e5b24e4c2d23";
    grant2.grantee = "dl1@ccaomac.zimbra.com";
    grant2.right = "deleteAccount";
    grant2.deny =  "1" ;
    return [
        grant1, grant2
         /*
        { grantee_type: "usr",
          id: "75b0677b-6ed1-4f0a-a37e-e5b24e4c2d22",
          grantee: "user1@ccaomac.zimbra.com",
          right: "createAccount" ,
          deny:  "1"                                     Z
        } ,
        { grantee_type: "grp",
          id: "75b0677b-6ed1-4f0a-a37e-e5b24e4c2d23",
          grantee: "dl1@ccaomac.zimbra.com",
          right: "deleteAccount" ,
          deny:  "1"
        }  */
    ]
}


ZaGrant.getSampleGrantsList = function () {
    var list = new ZaItemList(ZaGrant);
    list._vector = AjxVector.fromArray(ZaGrant.getSampleGrants())  ;
    return list ;         
}

ZaGrant.loadMethod = function (by, val) {
// TODO: use GetGrantsRequest   
    var grants = ZaGrant.getSampleGrants () ;
//    var list = new ZaItemList(ZaGrant) ;
//    list._vector = AjxVector.fromArray(grants) ;
    this [ZaGrant.A2_grantsList] = grants ;
}

//GrantRightRequest
ZaGrant.grantMethod = function (obj) {

}

//RevokeRightRequest
ZaGrant.revokeMethod = function () {

}


ZaGrant.myXModel = {
	items: [
        {id: ZaGrant.A_grantee, type: _STRING_, ref: ZaGrant.A_grantee, required: true },
        {id: ZaGrant.A_grantee_type, type:_STRING_, ref:  ZaGrant.A_grantee_type, required: true, choices: ZaGrant.GRANT_TYPE},
        {id: ZaGrant.A_right, type: _STRING_, ref:  ZaGrant.A_right, required: true },
        {id: ZaGrant.A_deny, type:_ENUM_, ref: ZaGrant.A_deny, choices:ZaModel.BOOLEAN_CHOICES2 },
        {id: ZaGrant.A_target_type, ref: ZaGrant.A_target_type, type: _STRING_, choices: ZaZimbraRights.targetType },
        {id: ZaGrant.A_target, type:_STRING_, ref: ZaGrant.A_target, required: true } ,
        {id: ZaGrant.A_right_type, type:_ENUM_, ref: ZaGrant.A_right_type, required:true, choices: ZaGrant.RIGHT_TYPE_CHOICES },

        {id: ZaGrant.A_inline_right, ref: ZaGrant.A_inline_right, type: _OBJECT_, items: [
                {id: ZaGrant.A_inline_verb, type: _STRING_, ref: ZaGrant.A_inline_verb, choices: ZaGrant.INLINE_VERB_TYPE_CHOICES},
                {id: ZaGrant.A_inline_target_type, type: _STRING_, ref: ZaGrant.A_inline_target_type, choices: ZaZimbraRights.targetType},
                {id: ZaGrant.A_inline_attr, type: _STRING_, ref: ZaGrant.A_inline_attr }
            ]
        }
    ]
};


ZaGrant.globalGrantsListTreeListener = function (ev) {
	if (AjxEnv.hasFirebug) console.log("Show the global grants lists ...") ;
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(
			ZaApp.getInstance().getGlobalGrantListController(),ZaGlobalGrantListViewController.prototype.show, ZaGrant.getSampleGrantsList());
	} else {
		ZaApp.getInstance().getGlobalGrantListController().show(ZaGrant.getSampleGrantsList());
	}
}

ZaGrant.grantsOvTreeModifier = function (tree) {
    var overviewPanelController = this ;
    if (!overviewPanelController) throw new Exception("ZaGrant.grantsOvTreeModifier: Overview Panel Controller is not set.");

    if(ZaSettings.GRANTS_ENABLED) {
        overviewPanelController._rightsTi = new DwtTreeItem(overviewPanelController._configTi);
        overviewPanelController._rightsTi.setText(com_zimbra_delegatedadmin.OVP_global_grants);
        overviewPanelController._rightsTi.setImage("Account"); //TODO: Use Grants icons
		overviewPanelController._rightsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW);

        if(ZaOverviewPanelController.overviewTreeListeners) {
            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW] = ZaGrant.globalGrantsListTreeListener;
        }
    }
}


