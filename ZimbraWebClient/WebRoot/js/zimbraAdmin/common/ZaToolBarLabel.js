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

ZaToolBarLabel = function(parent, style, className, posStyle, id, index) {
	if (arguments.length == 0) return ;
	DwtLabel.call(this,parent, style, className, posStyle, id, index);
}

ZaToolBarLabel.prototype = new DwtLabel;
ZaToolBarLabel.prototype.constructor = ZaToolBarLabel;


ZaToolBarLabel.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtLabel.prototype._createHtmlFromTemplate.call(this, "admin.Widgets#ZaToolBarLabel", data);
};