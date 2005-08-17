/**
* @class ZaCosServerPoolPage
* @contructor
**/
function ZaCosServerPoolPage (parent, app) {
	if (arguments.length == 0) return;
	DwtTabViewPage.call(this, parent);
	this._fieldIds = new Object(); //stores the ids of all the form elements
	/*
	* _fieldIds[] - this is a map, the keys are fields names of the ZaCos object
	* and values are ids of the corresponding form fields 
	*/	
	this._app = app;
	this._rendered=false;
//	this._initialized = false;
	this.isNewObject=false;	
	this.setScrollStyle(DwtControl.SCROLL);			
}

ZaCosServerPoolPage.prototype = new DwtTabViewPage;
ZaCosServerPoolPage.prototype.constructor = ZaCosServerPoolPage;

/**
* Public methods
**/

ZaCosServerPoolPage.prototype.toString = 
function() {
	return "ZaCosServerPoolPage";
}

ZaCosServerPoolPage.prototype.resetSize = 
function(newWidth, newHeight) {
	if(this._rendered) {
		DwtTabViewPage.prototype.resetSize.call(this, newWidth, newHeight);
	}
}

ZaCosServerPoolPage.prototype.showMe = 
function() {
	if(!this._rendered) {
		this._createHTML();
		this._createUI(); 	//initialize DWT widgets on the page
	}

/*	if(!this._initialized) {
		this._createUI(); 	//initialize DWT widgets on the page
	}	
*/	
	if(this.isNewObject) {
		this._setFields();
	}	
	DwtTabViewPage.prototype.showMe.call(this);
}

ZaCosServerPoolPage.prototype.setEnabled = 
function(flag) {
	if(!this._rendered) {
		return;		
	}
}

ZaCosServerPoolPage.prototype.setDirty = 
function (isD) {
	if(isD) {
		if(this._app.getCurrentController().getToolBar()) {
			if(this._app.getCurrentController().getToolBar().getButton(ZaOperation.SAVE)) {
				this._app.getCurrentController().getToolBar().getButton(ZaOperation.SAVE).setEnabled(true);
			}
		}
	}
	this._isDirty = isD;
}

/**
* @param item - ZaCos object 
* copies attribute values from object to form fields
**/
ZaCosServerPoolPage.prototype.setFields = 
function (item) {
	this.isNewObject=true;
	this._currentObject=item;
}

/**
* protected and private methods
**/


/**
* @method _setFields
* transfers the values from internal object (_containedObject)
* to the form fields
**/
ZaCosServerPoolPage.prototype._setFields = 
function() {

	var sourceArray = this._app.getServerList().getVector().getArray();
	var hostVector = new ZaItemVector();
	if(this._currentObject.attrs && this._currentObject.attrs[ZaCos.A_zimbraMailHostPool]) {
		if(this._currentObject.attrs[ZaCos.A_zimbraMailHostPool] instanceof Array) {
			for(sname in this._currentObject.attrs[ZaCos.A_zimbraMailHostPool]) {
				var newServer = new ZaServer();
				newServer.load("id", this._currentObject.attrs[ZaCos.A_zimbraMailHostPool][sname]);
				hostVector.add(newServer);
			}
		} else if(typeof(this._currentObject.attrs[ZaCos.A_zimbraMailHostPool]) == 'string'){
			var newServer = new ZaServer();
			newServer.load("id", this._currentObject.attrs[ZaCos.A_zimbraMailHostPool]);
			hostVector.add(newServer);
		}
	} 
	this._targetListView.set(hostVector);
	var sourceVector = new ZaItemVector();
	for(var ix in sourceArray) {
		if(!hostVector.contains(sourceArray[ix])) {
			sourceVector.add(sourceArray[ix]);
		}
	}
	this._sorceListView.set(sourceVector);
	this.isNewObject=false;	
}


/**
* @method _getFields
* transfers the values from form fields (_containedObject)
* to the internal object
**/
ZaCosServerPoolPage.prototype.getFields = 
function (item) {
	item.attrs[ZaCos.A_zimbraMailHostPool] = new Array();
	var list = this._targetListView.getList().getArray();
	if(list && list.length) {
		for (var ix = 0; ix < list.length; ix++) {
			item.attrs[ZaCos.A_zimbraMailHostPool].push(list[ix].id);
		}
	}
}

ZaCosServerPoolPage.prototype._createUI = 
function() {
	//remove button
	this._removeButton = this._setupButton(this._removeButtonId, ZaMsg.NAD_Remove);
	this._removeButton.addSelectionListener(new AjxListener(this, this._removeButtonListener));
	this._removeButton.setEnabled(false);
	var removeDiv = Dwt.getDomObj(this.getDocument(), this._removeDivId);
	removeDiv.appendChild(this._removeButton.getHtmlElement());

	this._addButton = this._setupButton(this._addButtonId, ZaMsg.NAD_Add);
	this._addButton.addSelectionListener(new AjxListener(this, this._addButtonListener));
	this._addButton.setEnabled(false);
	var addDiv = Dwt.getDomObj(this.getDocument(), this._addDivId);
	addDiv.appendChild(this._addButton.getHtmlElement());
	
	var targetListDiv = Dwt.getDomObj(this.getDocument(), this._targetListId);
	this._targetListView = new ZaCosServerPoolPage_ZaListView(this);
	targetListDiv.appendChild(this._targetListView.getHtmlElement());
	
	//var size = Dwt.getSize(targetListDiv);
	//this._targetListView.setSize(size.x, size.y);
	this._targetListView.addSelectionListener(new AjxListener(this, this._targetListener));
	
	//source list
	var sorceListDiv = Dwt.getDomObj(this.getDocument(), this._sourceListId);
	this._sorceListView = new ZaCosServerPoolPage_ZaListView(this);
	sorceListDiv.appendChild(this._sorceListView.getHtmlElement());
	//var size = Dwt.getSize(sorceListDiv);
	//this._sorceListView.setSize(size.x, size.y);
	this._sorceListView.addSelectionListener(new AjxListener(this, this._sorceListener));
	this._rendered = true;
}

ZaCosServerPoolPage.prototype._getField = 
function(obj, field, attr) {
	var elem = Dwt.getDomObj(this.getDocument(), this._fieldIds[field]);
	if(elem != null)
		obj.attrs[attr] = elem.value;

}

// Creates a DwtButton and adds a few props to it
ZaCosServerPoolPage.prototype._setupButton =
function(id, name) {
	var button = new DwtButton(this);
	button.setText(name);
	button.id = id;
	button.setHtmlElementId(id);
	button._activeClassName = button._origClassName + " " + DwtCssStyle.ACTIVE;
	button._nonActiveClassName = button._origClassName;
	return button;
}

ZaCosServerPoolPage.prototype._createHTML = 
function () {
	var idx = 0;
	var html = new Array(50);
	this._sourceListId = Dwt.getNextId();
	this._targetListId = Dwt.getNextId();
	this._addButtonId = Dwt.getNextId();
	this._addDivId = Dwt.getNextId();
	this._removeButtonId = Dwt.getNextId();
	this._removeDivId = Dwt.getNextId();
	
	html[idx++] = "<div class='ZaCosView'>";	
	html[idx++] = "<table style='width:50ex' cellspacing='0' cellpadding='0' border='0'>";
	html[idx++] = "<tr>";
	// source list
	html[idx++] = "<td align='left'><div style='width:20ex' id='" + this._sourceListId + "' class='serverPickList'></div></td>";
	// buttons
	html[idx++] = "<td valign='middle' align='center' style='width:10ex'>";
	//add button
	html[idx++] = "<div id='" + this._addDivId + "'></div>";
	html[idx++] = "<br />";
	// remove button
	html[idx++] = "<div id='" + this._removeDivId + "'></div>";
	html[idx++] = "</td>";

	// target list
	html[idx++] = "<td align='right'><div style='width:20ex' id='" + this._targetListId + "' class='serverPickList'></div></td>";	
	html[idx++] = "</tr></table></div>";
	this.getHtmlElement().innerHTML = html.join("");
//	this._rendered = true;
}

ZaCosServerPoolPage.prototype._targetListener = 
function (ev) {
	this._removeButton.setEnabled(true);
}

ZaCosServerPoolPage.prototype._sorceListener = 
function () {
	this._addButton.setEnabled(true);
}

ZaCosServerPoolPage.prototype._addButtonListener = 
function (ev) {
	//get selected item
	var selected = this._sorceListView.getSelection();
	//add it to the target list
	for (var i in selected) {
		this._targetListView.getList().add(selected[i]);
		this._sorceListView.getList().remove(selected[i]);
	}
	//call setUI.
	this._sorceListView.setUI();
	this._targetListView.setUI();
	//reset button states
	this._addButton.setEnabled(false);
	this._removeButton.setEnabled(false);	
	this.setDirty(true);
}

ZaCosServerPoolPage.prototype._removeButtonListener = 
function () {
	//get selected item
	var selected = this._targetListView.getSelection();
	//add it to the source list
	for (var i in selected) {
		this._sorceListView.getList().add(selected[i]);
		this._targetListView.getList().remove(selected[i]);
	}
	//call setUI.
	this._sorceListView.setUI();
	this._targetListView.setUI();
	//reset button states
	this._removeButton.setEnabled(false);
	this._addButton.setEnabled(false);
	this.setDirty(true);
}

function ZaCosServerPoolPage_ZaListView(parent) {
	if (arguments.length == 0) return;
	ZaListView.call(this, parent);
}


ZaCosServerPoolPage_ZaListView.prototype = new ZaListView;
ZaCosServerPoolPage_ZaListView.prototype.constructor = ZaCosServerPoolPage_ZaListView;

ZaCosServerPoolPage_ZaListView.prototype.toString = 
function() {
	return "ZaCosServerPoolPage_ZaListView";
}

// abstract methods
ZaCosServerPoolPage_ZaListView.prototype._createItemHtml = 
function(item) {
	var html = new Array(50);
	var	div = this.getDocument().createElement("div");
	div._styleClass = "Row";
	div._selectedStyleClass = div._styleClass + "-" + DwtCssStyle.SELECTED;
	div.className = div._styleClass;
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);		
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='1'>";
	html[idx++] = "<tr>";
	// name
	html[idx++] = "<td>&nbsp;";
	html[idx++] = AjxStringUtil.htmlEncode(item.name);
	html[idx++] = "</td>";
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaCosServerPoolPage_ZaListView.prototype._setNoResultsHtml = 
function() {
	var	div = this.getDocument().createElement("div");
	div.innerHTML = "<table width='100%' cellspacing='0' cellpadding='1'><tr><td class='NoResults'><br>&nbsp</td></tr></table>";
	this._parentEl.appendChild(div);
}
