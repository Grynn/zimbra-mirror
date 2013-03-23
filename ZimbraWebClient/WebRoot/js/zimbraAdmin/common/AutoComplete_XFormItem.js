/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
 
/**	
* @class defines XFormItem type _AUTO_COMPLETE_LIST_
* @contructor
* @author Charles Cao
**/

AutoCompleteList_XFormItem = function() {}
XFormItemFactory.createItemType("_AUTO_COMPLETE_LIST_", "auto_complete_list", 
										AutoCompleteList_XFormItem, WidgetAdaptor_XFormItem);

AutoCompleteList_XFormItem.prototype.widgetClass = ZaAutoCompleteListView;


AutoCompleteList_XFormItem.prototype.constructWidget = function () {
	
	var autoCompleteListViewClass = this.getInheritedProperty("widgetClass");
//	var locCallback = new AjxCallback (this, AutoCompleteList_XFormItem.prototype._getAcListLoc);
	var locCallback = new AjxCallback (this, this._getAcListLoc);
	var compCallback = new AjxCallback(this, this.getInheritedProperty("compCallback"));
	//var dataLoadCallback = new AjxCallback (this, dataLoaderClass.prototype._getDataCallback);
	
	var params = { 	//parent: this.getForm() ,
					parent: this.getForm().shell,					
					className: this.getCssClass(),
					dataLoaderClass: this.getInheritedProperty("dataLoaderClass"),
					dataLoaderMethod: this.getInheritedProperty("dataLoaderMethod"), //method that searches for matches (e.g. sends search request to server)
					matchValue: this.getInheritedProperty("matchValue"), //the name of the property in the match list to be used to do the comparison
					matchText: this.getInheritedProperty("matchText"),//the name of the property in the match list to be displayed in the field
					//inputFieldElement: this.getForm().getItemsById (this.getInheritedProperty("inputFieldElementId"))[0].getElement(),
					inputFieldXFormItem: this.getForm().getItemsById (this.getInheritedProperty("inputFieldElementId"))[0],
					//dataLoadCallback: dataLoadCallback,
					locCallback: locCallback, 
					compCallback: compCallback,//called when a value is selected from the list of suggestions
					separator: ""  					
				  };
								
	var widget = new autoCompleteListViewClass(params);
		
	return widget;
};

AutoCompleteList_XFormItem.prototype.insertWidget = function (form, widget, parentElement) {
	//the autocomplete list always belong to the shell
	//so we actually don't need to reparent the element
	return ;
}

AutoCompleteList_XFormItem.prototype.updateWidget = function (newValue) {}

AutoCompleteList_XFormItem.prototype._getAcListLoc =
function(ev) {
	var element = ev.element;
	var loc = Dwt.getLocation(element);
	var height = Dwt.getSize(element).y;
	return (new DwtPoint(loc.x, loc.y + height));
};





