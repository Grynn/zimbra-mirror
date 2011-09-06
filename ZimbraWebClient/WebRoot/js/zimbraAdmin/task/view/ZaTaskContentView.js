/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 9/5/11
 * Time: 1:42 AM
 * To change this template use File | Settings | File Templates.
 */

ZaTaskContentView = function(parent, entry) {
    ZaTabView.call(this, {
		parent:parent,
		iKeyName:"ZaTaskContentView",
		contextId:"TabContent"
	});
    this.initForm(ZaTask.myXModel,this.getMyXForm(entry), null);
}

ZaTaskContentView.prototype = new ZaTabView();
ZaTaskContentView.prototype.constructor = ZaTaskContentView;
ZaTabView.XFormModifiers["ZaTaskContentView"] = new Array();

ZaTaskContentView.prototype.setObject =
function(entry) {

    this._containedObject = new Object();
	this._containedObject.attrs = new Object();

    for (var a in entry.attrs) {
		var modelItem = this._localXForm.getModel().getItem(a) ;
        if ((modelItem != null && modelItem.type == _LIST_) || (entry.attrs[a] != null && entry.attrs[a] instanceof Array)) {
        	//need deep clone
            this._containedObject.attrs [a] =
                    ZaItem.deepCloneListItem (entry.attrs[a]);
        } else {
            this._containedObject.attrs[a] = entry.attrs[a];
        }
	}
	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type;

	if(entry.rights)
		this._containedObject.rights = entry.rights;

	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;

	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;

	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;

	if(entry.id)
		this._containedObject.id = entry.id;

    this._localXForm.setInstance(this._containedObject);

	this.formDirtyLsnr = new AjxListener(ZaApp.getInstance().getCurrentController(), ZaXFormViewController.prototype.handleXFormChange);
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, this.formDirtyLsnr);
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, this.formDirtyLsnr);
}

ZaTaskContentView.myXFormModifier = function(xFormObject, entry) {
    var items = {
        type:_GROUP_, numCols:1,  items:[
            {type:_OUTPUT_, value: "working in progress"},
            {ref:ZaTask.A_workingInProcess, type:_DWT_LIST_, height:"100px",
               forceUpdate: true, preserveSelection:false, multiselect:true,
               headerList:null,
               visibilityChecks:[ZaItem.hasReadPermission]
            },
            {type:_OUTPUT_, value: "Running Task"},
            {ref:ZaTask.A_runningTask, type:_DWT_LIST_, height:"100px",
               forceUpdate: true, preserveSelection:false, multiselect:true,
               headerList:null,
               visibilityChecks:[ZaItem.hasReadPermission]
            },
            {type:_SPACER_, height:"100%"}
        ]
    }
    xFormObject.tableCssStyle="width:100%;";
    xFormObject.items = [items];
}

ZaTabView.XFormModifiers["ZaTaskContentView"].push(ZaTaskContentView.myXFormModifier);