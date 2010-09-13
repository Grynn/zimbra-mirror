/**
 * Used to create an application tab and its operations, such as new a  tab, 
 * close a tab, edit the tab label.
 * 
 * It will also remember the state of the tab: hidden/shown and dirty/clean.
 * @param parent - the tab group containing all the tabs.
 * 
 * @param params :
 *  	closable - whether the close icon and action should be added
 * 		selected - whether the newly created tab should be selected 
 *		id - the tabId used to identify an unique tab.
 * 		toolTip - the tooltip of the tab
*/



ZaToolBarButton = function(params) {
        if (arguments.length == 0) return ;
        params = Dwt.getParams(arguments, ZaToolBarButton.PARAMS);
        params.className = params.className || "ZaToolBarButton";
        DwtButton.call(this, params);
}

ZaToolBarButton.PARAMS = ["parent", "style", "className", "posStyle", "actionTiming", "id", "index"];

ZaToolBarButton.prototype = new DwtButton;
ZaToolBarButton.prototype.constructor = DwtButton;


ZaToolBarButton.prototype._createHtml = function() {
    var templateId = "dwt.Widgets#ZToolbarButton";
    var data = { id: this._htmlElId };
    this._createHtmlFromTemplate(templateId, data);
};
