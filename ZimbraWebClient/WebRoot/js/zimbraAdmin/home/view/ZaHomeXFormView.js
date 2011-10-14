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
	this.initForm(ZaHome.myXModel,this.getMyXForm(entry), null);
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

ZaHomeXFormView.onCreateDomain = function(ev) {
    ZaDomainListController.prototype._newButtonListener.call(ZaApp.getInstance().getCurrentController(), ev);
}

ZaHomeXFormView.onCreateAccount = function(ev) {
    ZaAccountListController.prototype._newAccountListener.call(ZaApp.getInstance().getCurrentController(), ev);
}

ZaHomeXFormView.onSearchZimbraHelp = function(ev) {
    var url = "http://support.zimbra.com/help/index.php";
    window.open(url, "_blank");
}

ZaHomeXFormView.onDownloadGuide = function(ev) {
    ZaZimbraAdmin.prototype._dwListener.call(ZaZimbraAdmin.getInstance());
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

    var startChoices = [{label: "1", value: "create domain...", onClick: ZaHomeXFormView.onCreateDomain },
                        {label: "2", value: "create new account...", onClick: ZaHomeXFormView.onCreateAccount},
                        {label: "3", value: "..."}];
    var migrationChoices = [{label: "1", value: "Step one"}];
    var helpChoices = [{value:"Search Zimbra Help", onClick:ZaHomeXFormView.onSearchZimbraHelp},
                       {value:"Download Zimbra Administrator Guide", onClick:ZaHomeXFormView.onDownloadGuide}];
    var case1 = {type:_ZATABCASE_, numCols: 4,  colSizes:["25%", "25%", "25%", "25%"], caseKey:1,
//        height:"400px",  align:_LEFT_, valign:_TOP_,
        getCustomWidth: ZaHomeXFormView.prototype. getCustomWidth,
        items:[
            {type:_SPACER_, colSpan: "4", height:20},
            {type:_OUTPUT_, label:"VMWare Zimbra Version:", ref: ZaHome.A2_version},
            {type:_OUTPUT_, label:"Resouce Usage:", value: "tenative"},
            {type:_OUTPUT_, label:"Accounts:", ref: ZaHome.A2_account},
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

ZaHomeXFormView.prototype.getCustomWidth = function () {
    return "100%";
}