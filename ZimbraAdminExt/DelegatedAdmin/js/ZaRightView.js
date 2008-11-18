ZaRightView = function(parent) {
	ZaTabView.call(this, parent, "ZaRightView");
	this.TAB_INDEX = 0;
	this.initForm(ZaRight.myXModel,this.getMyXForm());
	this._localXForm.setController(ZaApp.getInstance());
}

ZaRightView.prototype = new ZaTabView();
ZaRightView.prototype.constructor = ZaRightView;
ZaTabView.XFormModifiers["ZaRightView"] = new Array();

ZaRightView.onFormFieldChanged =
function (value, event, form) {
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	return value;
}

ZaRightView.prototype.setObject =
function (entry) {
	
}


ZaRightView.deleteButtonListener = function () {
	var instance = this.getInstance();
	if (AjxEnv.hasFirebug) console.log("Deleting rights now")  ;
}

ZaRightView.addButtonListener =
function () {
	
}

/**
* This method is added to the map {@link ZaTabView#XFormModifiers}
* @param xFormObject {Object} a definition of the form. This method adds/removes/modifies xFormObject to construct
* a Server view.
**/
ZaRightView.myXFormModifier = function(xFormObject) {
};
ZaTabView.XFormModifiers["ZaRightView"].push(ZaRightView.myXFormModifier);