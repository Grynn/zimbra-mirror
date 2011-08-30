/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 8/19/11
 * Time: 10:38 PM
 * To change this template use File | Settings | File Templates.
 */
ZaTree = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, ZaTree.PARAMS);
    // Alwyas single style in Admin Console;
    params.style = DwtTree.SINGLE_STYLE;
	DwtTree.call(this, params);
    this._rootData = new ZaTreeItemData();
    this.currentShowItem = null;
};

ZaTree.PARAMS = ["parent", "style", "className", "posStyle"];

ZaTree.prototype = new DwtTree;
ZaTree.prototype.constructor = ZaTree;

ZaTree.prototype.toString =
function() {
	return "ZaTree";
};

ZaTree.prototype._expandUp =
function(item) {
	// Nothing doing here
    //
    return;
};

ZaTree.prototype.getContainer =
function() {
    return this._container;
};

ZaTree.prototype.setRootData =
function(rootData) {
    this._rootData  = rootData;
}

ZaTree.prototype.getRootData =
function(rootData) {
    return this._rootData;
}

ZaTree.prototype.addTreeItemData =
function(treeItemData) {
    var pathItems = this.getPathItems( treeItemData.parent);
    var parentItem = this.getTreeItemData(this._rootData, pathItems, 0);
    if (parentItem) {
        parentItem.addChild(treeItemData);
        return true;
    } else {
        return false;
    }
}

ZaTree.prototype.getTreeItemData =
function(startNode, pathItems, startIndex) {
    if (pathItems.length == startIndex)
        return "";

    var currentPathItem = pathItems[startIndex];
    if (startNode.text != currentPathItem)
        return "";

    var nextStartIndex = startIndex + 1;
    if (pathItems.length == nextStartIndex)
        return startNode;

    if (startNode.childrenData.size() == 0)
        return "";

    var ret = "";
    var i;

    for (i = 0; i < startNode.childrenData.size(); i++) {
        ret = this.getTreeItemData(startNode.childrenData.get(i), pathItems, nextStartIndex);
        if (ret)
            break;
    }

    return ret;
}

ZaTree.prototype.getTreeItemDataByPath =
function(path) {
    var pathItems = this.getPathItems(path);
    var ret = this.getTreeItemData(this._rootData, pathItems, 0);
    return ret;
}

ZaTree.SEPERATOR = "/";
ZaTree.prototype.getPathItems =
function(path) {
    var temp = path.split(ZaTree.SEPERATOR);
    var ret = [];
    for (var i = 0; i < temp.length; i++) {
        if (temp[i]) {
            ret.push(temp[i]);
        }
    }
    return ret;
}

ZaTree.prototype.setSelection =
function(treeItem, skipNotify, kbNavEvent, noFocus) {
	if (!treeItem)
        return;

	// Remove currently selected items from the selection list. if <treeItem> is in that list, then note it and return
	// after we are done processing the selected list
	var a = this._selectedItems.getArray();
	var sz = this._selectedItems.size();
	var da;
	var j = 0;
	var alreadySelected = false;
	for (var i = 0; i < sz; i++) {
		if (a[i] == treeItem) {
			alreadySelected = true;
		} else {
			a[i]._setSelected(false);
			this._selectedItems.remove(a[i]);
			if (da == null) {
				da = new Array();
			}
			da[j++] = a[i];
		}
	}

	if (da && !skipNotify) {
		this._notifyListeners(DwtEvent.SELECTION, da, DwtTree.ITEM_DESELECTED, null, this._selEv, kbNavEvent);
	}

	if (alreadySelected) { return; }

    this._selectedItems.add(treeItem);

	//this._expandUp(treeItem);
	if (treeItem._setSelected(true, noFocus) && !skipNotify) {
    	this._notifyListeners(DwtEvent.SELECTION, [treeItem], DwtTree.ITEM_SELECTED, null, this._selEv, kbNavEvent);
	}
};

ZaTree.prototype.setSelectionByPath =
function (path, isAddHistory, skipNotify, kbNavEvent, noFocus) {

    var dataItem = this.getTreeItemDataByPath(path);
    this.buildTree(dataItem);
    this._selectedItems.removeAll();

    var treeItem = this.currentRoot;
    this._selectedItems.add(treeItem);

    if (treeItem._setSelected(true, noFocus) && !skipNotify) {
    	this._notifyListeners(DwtEvent.SELECTION, [treeItem], DwtTree.ITEM_SELECTED, null, this._selEv, kbNavEvent);
	}
    this._updateHistory(treeItem, isAddHistory);
}

ZaTree.prototype.buildTree =
function (showRootNode) {

    this.clearItems();
    var ti, nextTi;
    this.currentRoot =  new ZaTreeItem({parent:this,className:"overviewHeader",id:showRootNode.id, forceNotifySelection:true});
	this.currentRoot.enableSelection(false);
	this.currentRoot.setText(showRootNode.text);
	this.currentRoot.setData(ZaOverviewPanelController._TID, showRootNode.mappingId);
    this.currentRoot.setData("dataItem", showRootNode);

    var i, j;
    for (i = 0; i < showRootNode.childrenData.size(); i++) {
        var currentAddNode =  showRootNode.childrenData.get(i);
        ti = new ZaTreeItem({parent:this.currentRoot,className:"AdminTreeItem",id:currentAddNode.id});
        ti.setCount(currentAddNode.count);
        ti.setText(currentAddNode.text);
        ti.setImage(currentAddNode.image);
        ti.setData(ZaOverviewPanelController._TID, currentAddNode.mappingId);
        ti.setData("dataItem", currentAddNode);
        for (var key in this._data) {
            this.setData(key, this._data[key]);
        }
        for (j = 0; j < currentAddNode.childrenData.size(); j++) {
            var currentNextNode =  currentAddNode.childrenData.get(j);
            nextTi = new ZaTreeItem({parent:ti,className:"AdminTreeItem",id:currentNextNode.id});
            nextTi.setText(currentNextNode.text);
            nextTi.setImage(currentNextNode.image);
            nextTi.setData(ZaOverviewPanelController._TID, currentNextNode.mappingId);
            nextTi.setData("dataItem", currentNextNode);
            for (var key in this._data) {
                this.setData(key, currentNextNode._data[key]);
            }
        }
    }

    this.currentRoot.setExpanded(true);
    return this.currentRoot;
}

ZaTree.prototype._itemClicked =
function(item, ev) {
	var i;
	var a = this._selectedItems.getArray();
	var numSelectedItems = this._selectedItems.size();
    var currentDataItem =  item.getData("dataItem");
	if (currentDataItem.isLeaf()) {
		if (numSelectedItems > 0) {
			for (i = 0; i < numSelectedItems; i++) {
				a[i]._setSelected(false);
			}
			// Notify listeners of deselection
			this._notifyListeners(DwtEvent.SELECTION, this._selectedItems.getArray(), DwtTree.ITEM_DESELECTED, ev, this._selEv);
			this._selectedItems.removeAll();
		}
		this._selectedItems.add(item);
		if (item._setSelected(true)) {
            this._updateHistory(item, true);
			this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_SELECTED, ev, this._selEv);
		}
	} else {
        this.buildTree(currentDataItem);
        this._selectedItems.removeAll();
        this._selectedItems.add(this.currentRoot);
        item = this.currentRoot;
		if (item._setSelected(true)) {
            this._updateHistory(item, true);
			this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_SELECTED, ev, this._selEv);
		}
    }
};

ZaTree.prototype._updateHistory =
function (treeItem, isAddHistory) {
    var text = treeItem.getText();
    var dataItem = treeItem.getData("dataItem");
    var historyObject = {path: this.getABPath(dataItem), displayName: text};
    ZaZimbraAdmin.getInstance().updateHistory(historyObject, isAddHistory);
}

ZaTree.prototype.getABPath =
function (dataItem) {
    var abPath = [];
    var currentObject = dataItem;
    while (currentObject) {
        abPath.unshift(currentObject.text);
        currentObject = currentObject.parentObject;
    }
    var ret = this.getPathByArray(abPath);
    return ret;
}

ZaTree.prototype.getPathByArray =
function (arr) {
    if (arr.length == 0)
        return "";
    return arr.join(ZaTree.SEPERATOR);
}



