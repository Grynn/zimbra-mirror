ZaEffectiveRightsXFormView = function(parent) {
	ZaTabView.call(this, parent,  "ZaEffectiveRightsXFormView");
	this.TAB_INDEX = 0;
	this.initForm(ZaAccount.myXModel,this.getMyXForm());
	
}

ZaEffectiveRightsXFormView.prototype = new ZaTabView();
ZaEffectiveRightsXFormView.prototype.constructor = ZaEffectiveRightsXFormView;
ZaTabView.XFormModifiers["ZaEffectiveRightsXFormView"] = new Array();
//ZaTabView.ObjectModifiers["ZaEffectiveRightsXFormView"] = [] ;

ZaEffectiveRightsXFormView.prototype.setObject =
function(entry) {                              

    this._containedObject = {};

    this._containedObject = entry ;

    this._localXForm.setInstance(this._containedObject);
	this.updateTab();

}

ZaEffectiveRightsXFormView.myXFormModifier = function(xFormObject) {
    var headerItems = [];
    this.tabChoices = new Array();
    var cases = [];
    
    xFormObject.tableCssStyle="width:100%;";
	xFormObject.items = [
			{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header",
				items: [
					{type:_GROUP_,	numCols:4,colSizes:["90px","350px","100px","*"],items:headerItems}
				],
				cssStyle:"padding-top:5px; padding-bottom:5px"
			},
			{type:_TAB_BAR_,  ref:ZaModel.currentTab,choices:this.tabChoices,cssClass:"ZaTabBar", id:"xform_tabbar"},
			{type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:cases}
	];
}

ZaTabView.XFormModifiers["ZaEffectiveRightsXFormView"].push(ZaEffectiveRightsXFormView.myXFormModifier);

ZaEffectiveRightsXFormView.prototype.getTabToolTip =
function () {
	if (this._containedObject && this._containedObject.grantee
            && this._containedObject.grantee.name ) {
		return	AjxMessageFormat.format(com_zimbra_delegatedadmin.tt_tab_view_effective_rights,  [this._containedObject.grantee.name]) ;
	}else{
		return "" ;
	}
}

ZaEffectiveRightsXFormView.prototype.getTabIcon =
function () {
	return "RightObject" ;
}

ZaEffectiveRightsXFormView.prototype.getTabTitle =
function () {
	if (this._containedObject && this._containedObject.grantee
            && this._containedObject.grantee.name) {
		return this._containedObject.grantee.name ;
	}else{
		return "" ;
	}
}