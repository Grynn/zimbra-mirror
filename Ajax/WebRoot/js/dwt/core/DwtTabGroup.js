/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
* @constructor
* @class
* This class represents a the tab ordering that is to be used for keyboard
* navigation among a group of related visual elements. Keyboard navigation follows
* the order of the components specified in the tab group. Tab groups may of course
* be nested (thus representing groups of subcomponents in the UI). Thus tab groups
* may contain other tab groups.
* 
* @param isRoot [boolean] Set to true if this is to be the root tab group. The
* 		root tab group is important because it among other things keeps a hash
*		of all the members in the tab hierarchy so that it is efficient to 
* 		set focus explicity to an element. 
* 
* 
* @author Ross Dargahi
*
*/
function DwtTabGroup(isRoot) {
	this._members = new AjxVector();
	this._parent = null;
	this._currFocusIdx = DwtTabGroup._EOL;
}

DwtTabGroup.NOT_ROOT_TABGROUP = "NOT ROOT TAB GROUP";

DwtTabGroup._EOL = -1;

DwtTabGroup.prototype.toString = 
function() {
	return "DwtTabGroup";
}

DwtTabGroup.prototype.addChangeListener =
function(listener) {
	if (this._evtMgr == null)
		this._evtMgr = new AjxEventMgr();	
	this._evtMgr.addListener(DwtEvent.STATE_CHANGE, listener);
}

DwtTabGroup.prototype.removeChangeListener =
function(listener) {
	if (this._evtMgr == null)
		return;	
	this._evtMgr.removeListener(DwtEvent.STATE_CHANGE, listener);
}

DwtTabGroup.prototype.addMember =
function(member, index) {
	
	this.add(member, index);
	
	/* If this is the first member being added the group, then make it the
	 * current focus memberif the index of the new member is before the current
	 * focus member index, then we need to increment the index by one */
	if (this._member.size() == 0)
		this._currFocusIdx = 0;
	else if (index != null && index < this._currFocusIdx)
	 	this._currFocusIdx--;
	
	// Register me as the new parent
	if (member instanceof DwtTabGroup)
		member.newParent(this);
}

DwtTabGroup.prototype.addMemberAfter =
function(newMember, beforeMember) {
	this.addMember(newMember, this._members.indexOf(beforeMember) + 1);
}

DwtTabGroup.prototype.addMemberBefore =
function(newMember, beforeMember) {
	var idx = this._members.indexOf(beforeMember);
	this.addMember(newMember, (idx != 0) ? idx-- : 0);
}

/**
 * This method removes a member for the tab group. The pre-condition to this method
 * is that the member in currently in the tab group
 * 
 * @param member [DwtControl, HTML input element ] member to remove from the tab group
 */
DwtTabGroup.prototype.removeMember =
function(member) {
	/* If we are removing the current focus member, then we need to adjust the focus
	 * member index. If the tab group is empty as a result of the removal, then we 
	 * set the index to DwtTabGroup._EOL. If the member removed was not the first 
	 * member in the tab group, then we set the idx to the previous element, else it
	 * was the first member and we set the focus to the next element
	 */
	var idx = this._members.indexOf(member);
	var cfm = null;
	member = this.remove(member);
	if (this._currFocusIdx == idx) {
		var sz = this._members.size();
		if (sz == 0)
			this._currFocusIdx = DwtTabGroup._EOL;
		if (currFocusIdx > 0)
			this._currFocusIdx--;
		else
			this._currFocusIdx++;
	}
	this._notifyListeners(this._member.get(this._currFocusIdx));
}

DwtTabGroup.prototype.newParent =
function(newParent) {
	this._parent = newParent;
}

DwtTabGroup.prototype.getFocusMember =
function() {
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;	
	return this._getFocusMember();
}

DwtTabGroup.prototype._getFocusMember =
function() {
	var cfm = (this._currFocusIdx != DwtTabGroup._EOL) ? this._member.get(this._currFocusIdx) : null;
	if (cfm && cfm instanceof DwtTabGroup)
		return cfm._getFocusMember();
	else
		return cfm;		
}


DwtTabGroup.prototype.setFocusMember =
function() {
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;	
	return this._setFocusMember();
}

DwtTabGroup.prototype._setFocusMember =
function(member) {
	alert("NOT IMPLEMENTED");
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
	
	return this._setFocusMember(this, member);

}

/* This method performs an inorder traversal of the tab group hierarchy probing for
 * the path from the root TG down to <member> When it finds member it will return
 * true;
 */
DwtTabGroup.prototype._setFocusMember =
function(tabGroup, member) {
 	var sz = tabGroup._member.size(); 
 	var a = tabGroup._member.getArray();
 	for (var i = 0; i < sz; i++) {
 		var m = a[i]
 		if (m instanceof DwtTabGroup) {
 			if (tabGroup._setFocusMember(m, member)) {
 				tabGroup.currFocusIdx = i;
				return true;
 			}
 		} else if (m == member) {
 			tabGroup.currFocusIdx = i;
			return true;
 		}	
 	}
	return false;
}

DwtTabGroup.prototype.getNextFocusMember =
function() {
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;	
	return this._getNextFocusMember();
}


/*
 * (1) Current item is not a group
 *   (1.1) There is no next item
 *       (1.1.1) If I am root, then cycle back to the first element
 *       (1.1.2) set my currentMemberIdx to DwtTabGroup._EOL    
 *   (1.2) The next item is a group: call getNextFocusMember on the group
 *   (1.3) The next item is not a group. Return it
 * (2) Current item is a group
 *   (2.1) Call getNextFocusMember on the group
 */
DwtTabGroup.prototype._getNextFocusMember =
function() {
	// Reset the current focus member index if are at the End Of List
	this._currFocusIdx = (this._currFocusIdx != DwtTabGroup._EOL) ? this._currFocusIdx++ : 0;
	var cfm = this._member.get(this._currFocusIdx);
	
	if (!cfm) {
		if (this._parent == null) {
			this._currFocusIdx = 0;
			return this._getFocusMember();
		} else {
			this._currFocusIdx = DwtTabGroup._EOL;
			return null;
		}
	} else if (cfm instanceof DwtTabGroup) {
		cfm = cfm._getNextFocusMember();
		
		/* If the next focus member is null and we are the parent tab group, then
		 * cycle back to the first element
		 */
		if (!cfm && this._parent == null)
			return this._getNextFocusMember();
	} 
	return cfm;
}

/**
 * resets the the focus member to the first element in the tab group cascading down
 * the tab gropu hierarchy if the first member is itself a DwtTabGroup
 */
DwtTabGroup.prototype.resetFocusMember =
function() {
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;	
	this._resetFocusMember(this);
}

DwtTabGroup.prototype._resetFocusMember =
function() {
	this._currFocusIdx = 0;
	var cm = this._members.get(0);
	if (cm instanceof DwtTagGroup)
		cm._resetFocusMember();
}

/**
 * Recrusively dumps the contents of the tab group
 */
DwtTabGroup.prototype.dump =
function() {
	this._dump(0, this);
}

DwtTabGroup.prototype._dump =
function(tg, level) {
	var levelIndent = "";
	for (var i = 0; i < levelIndent; i++)
		levelIndent += "+++";
	
	var sz = tg._members.size();
	var a = tg._members.getArray();
	for (var i = 0; i < sz; i++) {
		if (a[i] instanceof DwtTabGroup) {
			DBG.println(levelIndent + "   === TABGROUP ===");
			tg._dump(level+1, a[i]);
		} else if (a[i] instanceof DwtControl) {
			DBG.println(levelIndent + "   " + a[i].toString());
		} else {
			DBG.println(levelIndent + "   " + a[i].tagName);
		}
	}
}


DwtTabGroup.prototype._notifyListeners =
function(newFocusMember) {
	// Only the root tab group will issue notifications
	var rootTg = (this._parent != null) ? this._parent : this;
	if (rootTg._evtMgr) {
		/* Now we need to compute the focus path from the root tab group
		 * down. If we hit this element then, the current path a has changed
		 * and we need to notify listeners*/
		var m = rootTg._members.get(rootTg._currFocusIdx);
		while (true) {
			if (m instanceof DwtTabGroup) {
				m = m._members.get(m._currFocusIdx);
			} else {
				if (m == newFocusMember) {
					var evt = DwtTabGroup._changeEvt;
					evt.reset();
					evt.tabGroup = this;
					evt.newFocusMember = rootTg.getFocusMember();
					rootTg._evtMgr.notifyListeners(DwtEvent.STATE_CHANGE, evt);
				}
				break;
			}
		}
	}
}


