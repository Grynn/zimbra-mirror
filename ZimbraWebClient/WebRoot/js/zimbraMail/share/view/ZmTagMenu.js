function ZmTagMenu(parent) {

	// create a menu (though we don't put anything in it yet) so that parent widget shows it has one
	ZmPopupMenu.call(this, parent);

	parent.setMenu(this);
	this._changeListener = new AjxListener(this, this._tagChangeListener);
	this._addHash = new Object();
	this._removeHash = new Object();
	this._evtMgr = new AjxEventMgr();
	this._desiredState = true;
}

ZmTagMenu.prototype = new ZmPopupMenu;
ZmTagMenu.prototype.constructor = ZmTagMenu;

ZmTagMenu.KEY_TAG_EVENT = "_tagEvent_";
ZmTagMenu.KEY_TAG_ADDED = "_tagAdded_";

ZmTagMenu.prototype.toString = 
function() {
	return "ZmTagMenu";
}

ZmTagMenu.prototype.addSelectionListener = 
function(listener) {
	this._evtMgr.addListener(DwtEvent.SELECTION, listener);
}

ZmTagMenu.prototype.removeSelectionListener = 
function(listener) {
	this._evtMgr.removeListener(DwtEvent.SELECTION, listener);    	
}

ZmTagMenu.prototype.setEnabled =
function(enabled) {
	// If there are no tags, then enable later
	this._desiredState = enabled;
	if (enabled && !this._tagList)
		return;
	this.parent.setEnabled(enabled);
}

// Dynamically set the list of tags that can be added/removed based on the given list of items.
ZmTagMenu.prototype.set =
function(items, tagList) {
	if (this._tagList)
		this._tagList.removeChangeListener(this._changeListener);
	this._tagList = tagList;
	tagList.addChangeListener(this._changeListener);
	var rootTag = tagList.root;
	this.parent.setEnabled(true);

	// reset the menu
	this.removeChildren();

	var addRemove = this._getAddRemove(items, rootTag);
	this._render(rootTag, addRemove);
}

// Given a list of items, produce two lists: one of tags that could be added (any tag
// that the entire list doesn't have), and one of tags that could be removed (any tag
// that any item has).
ZmTagMenu.prototype._getAddRemove = 
function(items, tagList) {
	// find out how many times each tag shows up in the items
	var tagCount = new Object();
	for (var i = 0; i < items.length; i++) {
		var item = items[i];
		if (!item) continue;
		if (item.tags && item.tags.length) {
			for (var j = 0; j < item.tags.length; j++) {
				var tagId = item.tags[j];
				tagCount[tagId] = tagCount[tagId] ? tagCount[tagId] + 1 : 1;
			}
		}
	}
	var add = new Object();
	var remove = new Object();
	// any tag held by fewer than all the items can be added
	var a = tagList.children.getArray();
	for (var i = 0; i < a.length; i++) {
		var tagId = a[i].id;
		if (!tagCount[tagId] || (tagCount[tagId] < items.length)) {
			add[tagId] = true;
		}
	}
	// any tag we saw can be removed
	for (var tagId in tagCount)
		remove[tagId] = true;

	return {add: add, remove: remove};
}

// Create the list of tags that can be added, and the submenu with the list of
// tags that can be removed.
ZmTagMenu.prototype._render =
function(tagList, addRemove) {
	var sz = tagList.size();
	var a = tagList.children.getArray();
	var removeList = new Array();
	for (var i = 0; i < sz; i++) {
		var tag = a[i];
		var tagId = tag.id;
		if (addRemove.add[tagId])
			this._addNewTag(this, tag, true, null, this._addHash);
		if (addRemove.remove[tagId])
			removeList.push(tagId);
	}

	if (this._tagList.size())
		new DwtMenuItem(this, DwtMenuItem.SEPARATOR_STYLE);

	// add static "New Tag" menu item
	var miNew = new DwtMenuItem(this);
	miNew.setText(AjxStringUtil.htmlEncode(LmMsg.newTag));
	miNew.setImage(ZmImg.I_NEW_TAG);
	miNew.setData(ZmTagMenu.KEY_TAG_EVENT, ZmEvent.E_CREATE);
	miNew.addSelectionListener(new AjxListener(this, this._menuItemSelectionListener));

	// add static "Remove Tag" menu item
	var miRemove = new DwtMenuItem(this);
	miRemove.setEnabled(false);
	miRemove.setText(AjxStringUtil.htmlEncode(LmMsg.removeTag));
	miRemove.setImage(ZmImg.I_DELETE_TAG);

	if (removeList.length > 0) {
		miRemove.setEnabled(true);
		var removeMenu = null;
		if (removeList.length > 1) {
			for (i = 0; i < removeList.length; i++) {
				if (!removeMenu) {
					removeMenu = new DwtMenu(miRemove, null, this._className);
					miRemove.setMenu(removeMenu);
				}
				var tag = tagList.getById(removeList[i]);
				this._addNewTag(removeMenu, tag, false, null, this._removeHash);
			}
		} else if (removeList.length == 1) {
			var tag = tagList.getById(removeList[0]);
			miRemove.setData(ZmTagMenu.KEY_TAG_EVENT, ZmEvent.E_TAGS);
			miRemove.setData(ZmTagMenu.KEY_TAG_ADDED, false);
			miRemove.setData(Dwt.KEY_OBJECT, tag);
			miRemove.addSelectionListener(new AjxListener(this, this._menuItemSelectionListener));
		}		

		// if multiple removable tags, offer "Remove All"
		if (removeList.length > 1) {
			new DwtMenuItem(removeMenu, DwtMenuItem.SEPARATOR_STYLE);
			var mi = new DwtMenuItem(removeMenu);
			mi.setText(LmMsg.allTags);
			mi.setImage(ZmImg.I_MINI_TAG_STACK);
			mi.setData(ZmTagMenu.KEY_TAG_EVENT, ZmEvent.E_REMOVE_ALL);
			mi.setData(Dwt.KEY_OBJECT, removeList);
			mi.addSelectionListener(new AjxListener(this, this._menuItemSelectionListener));
		}
	}
}

ZmTagMenu.prototype._addNewTag =
function(menu, newTag, add, index, tagHash) {
	var mi = new DwtMenuItem(menu, null, null, index);
	mi.setText(newTag.getName(false));
	mi.setImage(ZmTag.COLOR_ICON[newTag.color]);
	mi.setData(ZmTagMenu.KEY_TAG_EVENT, ZmEvent.E_TAGS);
	mi.setData(ZmTagMenu.KEY_TAG_ADDED, add);
	mi.setData(Dwt.KEY_OBJECT, newTag);
	mi.addSelectionListener(new AjxListener(this, this._menuItemSelectionListener));
	tagHash[newTag.id] = mi;
}

ZmTagMenu.prototype._tagChangeListener =
function(ev) {
	if (ev.type != ZmEvent.S_TAG)
		return;
	if (ev.event == ZmEvent.E_RENAME) {
		DBG.println(AjxDebug.DBG2, "TAG RENAME");
	} else if (ev.event == ZmEvent.E_DELETE) {
		var mi;
		if (mi = this._addHash[ev.source.id])
			mi.dispose();
		if (mi = this._removeHash[ev.source.id])
			mi.dispose();
		// Check to see if we have any tags left. If not, disable "myTags"
		if (this.getItemCount() == 0) {
			this.parent.setMenu(null);
			this.parent.setEnabled(false);
		}		
	} else if (ev.event == ZmEvent.E_CREATE) {
		var index = ZmTreeView.getSortIndex(this, ev.source, ZmTag.sortCompare);
		this._addNewTag(this, ev.source, true, index, this._addHash);
		this.parent.setEnabled(this._desiredState); // in case this is first child
	} else if (ev.event == ZmEvent.E_MODIFY) {
		var tag = ev.source;
		var fields = ev.getDetail("fields");
		var mi;
		if (fields && fields[ZmOrganizer.F_COLOR]) {
			if (mi = this._addHash[tag.id])
				mi.setImage(ZmTag.COLOR_ICON[tag.color]);
			if (mi = this._removeHash[ev.source.id])
				mi.setImage(ZmTag.COLOR_ICON[tag.color]);
		}
		if ((fields && fields[ZmOrganizer.F_NAME]) || (fields && fields[ZmOrganizer.F_UNREAD])) {
			if (mi = this._addHash[tag.id])
				mi.setText(tag.getName(false));
			if (mi = this._removeHash[tag.id])
				mi.setText(tag.getName(false));
		}
	}
}

ZmTagMenu.prototype._menuItemSelectionListener =
function(ev) {
	// Only notify if the node is one of our nodes
	if (ev.item.getData(ZmTagMenu.KEY_TAG_EVENT)) {
		this._evtMgr.notifyListeners(DwtEvent.SELECTION, ev.item);
	}
}
