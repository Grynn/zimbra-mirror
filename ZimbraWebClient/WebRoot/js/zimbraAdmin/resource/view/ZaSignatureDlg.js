/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 5/26/11
 * Time: 8:12 PM
 * To change this template use File | Settings | File Templates.
 */


ZaEditSignatureDialog = function(parent,   w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];
	ZaXDialog.call(this, parent, null, title, w, h, null, ZaId.DLG_EDIT_SIGNATURE);
	this._containedObject = {};
	this.initForm(ZaSignature.myXModel,this.getMyXForm());
    this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaEditSignatureDialog.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaEditSignatureDialog.prototype.handleXFormChange));
}

ZaEditSignatureDialog.prototype = new ZaXDialog;
ZaEditSignatureDialog.prototype.constructor = ZaEditSignatureDialog;

ZaEditSignatureDialog.prototype.handleXFormChange = function () {
    var obj = this.getObject();

    var isEnabledOk = (!AjxUtil.isEmpty(obj[ZaSignature.A2_name])) && (!AjxUtil.isEmpty(obj[ZaSignature.A2_content]));

    this._button[DwtDialog.OK_BUTTON].setEnabled(isEnabledOk);
}

ZaEditSignatureDialog.prototype.getMyXForm =
function() {
	var xFormObject = {
		numCols:1,
		items:[
            {type:_GROUP_,isTabGroup:true,
            	items: [ //allows tab key iteration
                	{ref:ZaSignature.A2_name, type:_TEXTFIELD_, width:"300px", label:ZaMsg.Dlg_SignatureName,visibilityChecks:[],enableDisableChecks:[]},
                    {ref:ZaSignature.A2_content, type:_TEXTAREA_,
                        width:"300px", label:ZaMsg.Dlg_SignatureContent, msgName:ZaMsg.Dlg_SignatureContent,
                        labelLocation:_LEFT_,visibilityChecks:[],enableDisableChecks:[]}
                ]
            }
        ]
	};
	return xFormObject;
}


