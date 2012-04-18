/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 8/19/11
 * Time: 2:59 AM
 * To change this template use File | Settings | File Templates.
 */

ZaTreeItem = function(params) {
    if (arguments.length == 0) { return; }

    params.expandNodeImage = params.expandNodeImage || "Blank_16";
    params.collapseNodeImage= params.collapseNodeImage || "AdminCollapse";
    params = Dwt.getParams(arguments, ZaTreeItem.PARAMS);
    this._parentInTree = params.parent;
    this._countParam = params.count;
    this.forceNode = (params.forceNode === undefined? false : params.forceNode) ;
    DwtTreeItem.call(this, params);
}

ZaTreeItem.PARAMS = ["parent", "index", "text", "imageInfo", "deferred", "className", "posStyle",
					  "forceNotifySelection", "forceNotifyAction", "count", "forceNode"];

ZaTreeItem.prototype = new DwtTreeItem;
ZaTreeItem.prototype.constructor = ZaTreeItem;

ZaTreeItem.prototype.TEMPLATE = "admin.Widgets#ZaTreeItem";

ZaTreeItem.prototype.dispose =
function() {
	this._countCell = null;
	DwtTreeItem.prototype.dispose.call(this);
};

ZaTreeItem.prototype.toString =
function() {
	return "ZaTreeItem";
};

ZaTreeItem.prototype._initialize =
function(index, realizeDeferred, forceNode) {
    forceNode = forceNode || this.forceNode;
    DwtTreeItem.prototype._initialize.call(this, index, realizeDeferred, forceNode);

	this._countCell = document.getElementById(this._htmlElId + "_countCell");
	// initialize count
	if (this._countCell && (this._countInfoParam !== undefined)) {
	    this._countCell.innerHTML = this._countInfoParam;
        // only make css take effect when it has value;
        this._countCell.className = "AdminTreeItem-Count";
		this._countInfo = this._countInfoParam;
	}

    if (this._nodeCell) {
        var imgEl = AjxImg.getImageElement(this._nodeCell);
        if (imgEl) {
            Dwt.clearHandler(imgEl, DwtEvent.ONMOUSEDOWN);
            Dwt.clearHandler(imgEl, DwtEvent.ONMOUSEUP);
        }
    }
}

ZaTreeItem.prototype.getCount =
function() {
	return this._countInfo;
};

/**
 * Sets the image.
 *
 * @param	{string}	imageInfo		the image
 */
ZaTreeItem.prototype.setCount =
function(countInfo) {
	if (this._initialized) {
		if (this._countCell) {
			this._countCell.innerHTML = countInfo;
            // only make css take effect when it has value;
            this._countCell.className = "AdminTreeItem-Count";
		}
		this._countInfo = countInfo;
	} else {
		this._countInfoParam = countInfo;
	}
};

ZaTreeItem.prototype.setExpanded =
function(expanded, recurse, skipNotify) {
	// Go up the chain, ensuring that parents are expanded/initialized
	if (expanded) {
		// Realize any deferred children
		this._realizeDeferredChildren();
	}

	// If we have children, then allow for expanding/collapsing
	if (this.getNumChildren()) {
        if (this._expanded != expanded) {
			this._expand(expanded, null, true);
		}
	}
};

ZaTreeItem.prototype.isClickOnItem =
function(ev) {
    var x = ev.docX;
    var y = ev.docY;
    var selfBound =  Dwt.getBounds(this._itemDiv);
    var ret = (selfBound.x <= ev.docX) &&
              (ev.docX <= selfBound.x + selfBound.width) &&
              (location.y <= ev.docY) &&
              (ev.docY <= selfBound.y + selfBound.height);
    return ret;


}

// type 0: local
// type 1: alias
ZaTreeItemData = function(params) {
    if (arguments.length == 0) { return; }

    params = Dwt.getParams(arguments, ZaTreeItemData.PARAMS);
    this.parent = params.parent;
    this.parentObject = params.parentObject || "";
    this.relatedObject = params.relatedObject || [];
    this.recentObject= params.recentObject || [];
    this.type = params.type || 0;
    if (this.type == 1) {
        this.path = params.path;
    }
    this.id = params.id;
    this.className = params.className || "AdminTreeItem";
    this.text = params.text;
    this.image = params.image;
    this.siblings = new AjxVector();
    this.forceNode = params.forceNode ;
    this.isShowHistory = (params.isShowHistory === undefined?  true: params.isShowHistory);
    this.index = params.index;
    this.defaultSelectedItem = params.defaultSelectedItem;
    this.count = params.count;
    this.canShowOnRoot = (params.canShowOnRoot === undefined? true : params.canShowOnRoot) ;
    this.mappingId = params.mappingId;
    this.callback = params.callback;
    this.buildPath = (params.buildPath ? params.buildPath : undefined);
    this._data = {};
    this.childrenData = new AjxVector();
}

ZaTreeItemData.PARAMS = ["parent", "id", "text", "image", "index", "count", "mappingId", "callback", "relatedObject", "recentObject", "type", "path", "canShowOnRoot", "forceNode", "isShowHistory", "buildPath", "className", "defaultSelectedItem"];

ZaTreeItemData.prototype.addChild =
function(child, index) {
	this.childrenData.add(child, index);
    child.parentObject = this;
};

ZaTreeItemData.prototype.addSilbings =
function(sibling, index) {
	this.siblings.add(sibling, index);
};

ZaTreeItemData.prototype.setData =
function(key, value) {
    this._data[key] = value;
};

ZaTreeItemData.prototype.getData =
function(key) {
    return this._data[key];
};

ZaTreeItemData.prototype.removeChild =
function(child, index) {
	this.childrenData.remove(child);
};

ZaTreeItemData.prototype.isLeaf =
function() {
    var isRealLeaf = this.childrenData.size() == 0;
    var isFakeLeaf = !this.canShowOnRoot;
   return (isRealLeaf || isFakeLeaf);
}

ZaTreeItemData.prototype.getChildrenNum =
function() {
   return this.childrenData.size();
}

ZaTreeItemData.prototype.isAlias =
function(child, index) {
	if (this.type == 1)
        return true;
    else
        return false;
};

ZaTreeItemData.prototype.getRealPath =
function(child, index) {
	return this.path;
};

ZaTreeItemData.prototype.addRelatedObject =
function(relatedObject) {
    this.relatedObject = relatedObject;
}

ZaTreeItemData.prototype.addRecentObject =
function(recentObject) {
    this.recentObject = recentObject;
}
