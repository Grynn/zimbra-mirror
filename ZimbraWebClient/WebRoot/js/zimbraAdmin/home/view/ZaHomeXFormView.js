/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 8/26/11
 * Time: 3:47 AM
 * To change this template use File | Settings | File Templates.
 */
ZaHomeXFormView = function(parent, entry) {
	ZaTabView.call(this, {
		parent:parent,
		iKeyName:"ZaHomeXFormView",
		contextId:ZaId.VIEW_HOME
	});
	this.initForm(ZaResource.myXModel,this.getMyXForm(entry), null);
	this._localXForm.setController(ZaApp.getInstance());
}

ZaHomeXFormView.prototype = new ZaTabView();
ZaHomeXFormView.prototype.constructor = ZaHomeXFormView;
ZaTabView.XFormModifiers["ZaHomeXFormView"] = new Array();

ZaHomeXFormView.prototype.setObject =
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

	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];

    this._localXForm.setInstance(this._containedObject);
}

ZaHomeXFormView.myXFormModifier = function(xFormObject, entry) {
    var cases = [];

    var nameGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_ResourceNameGrouper, id:"resource_form_name_group",
            colSizes:["275px","*"],numCols:2,items:[
            {ref:"name", type:_TEXTFIELD_, msgName:ZaMsg.NAD_ResourceName,
                label:ZaMsg.NAD_ResourceName, labelLocation:_LEFT_, width: "200px" },
            {ref:"name", type:_EMAILADDR_, msgName:ZaMsg.NAD_ResAccountName,label:ZaMsg.NAD_ResAccountName
            }]
    };

    var startChoices = [{label: "1", value: "create domain..."},
                        {label: "2", value: "create new account..."},
                        {label: "3", value: "..."}];
    var migrationChoices = [{label: "1", value: "Step one"}];
    var helpChoices = [{value:"Search Zimbra Help"}, {value:"Download Zimbra Administrator Guide"}];
    var case1 = {type:_ZATABCASE_, numCols: 4,  colSizes:["25%", "25%", "25%", "25%"], caseKey:1,
//        height:"400px",  align:_LEFT_, valign:_TOP_,
        items:[
            {type:_SPACER_, colSpan: "4", height:20},
            {type:_OUTPUT_, label:"VMWare Zimbra Version:", value: "7.0.1"},
            {type:_OUTPUT_, label:"Resouce Usage:", value: "tenative"},
            {type:_OUTPUT_, label:"Accounts:", value: "test@zimbra.com"},
            {type:_SPACER_, colSpan: "2"},
            {type:_OUTPUT_, label: "Status:", value: "running"},
            {type:_SPACER_, colSpan: "2"},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
            {type:_HOMEGROUP_, headerLabel: "Get Started with Zimbra", colSpan: "2", valign:_TOP_, contentChoices: startChoices},
            {type:_HOMEGROUP_, headerLabel: "Migrate Accounts", colSpan: "2", valign:_TOP_, contentChoices:  migrationChoices},
            {type:_HOMEGROUP_, headerLabel: "Zimbra Help", colSpan:"2", valign:_TOP_,  contentChoices: helpChoices}
        ]
    };

    cases.push(case1);

    xFormObject.tableCssStyle="width:100%;";
	xFormObject.items = [
			{type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:cases}
	];
};
ZaTabView.XFormModifiers["ZaHomeXFormView"].push(ZaHomeXFormView.myXFormModifier);