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
* The root tab group (i.e. one without a parent tab group) is a special tab group
* in that it is responsible for holding the notion of the current focus member 
* within it's membership. It is also the place where focus listener may be
* registered (see method documentation)
* 
* @param name [String] Optional name of this tab group. 
* 
* @author Ross Dargahi
*/
function DwtTabGroup(name) {
	this._members = new AjxVector();
	this._parent = null;
	this._name = name
	this._currFocusMember = null;
}

DwtTabGroup.NOT_ROOT_TABGROUP = "NOT ROOT TAB GROUP";

DwtTabGroup._changeEvt = new DwtTabGroupEvent();

DwtTabGroup.prototype.toString = 
function() {
	return "DwtTabGroup";
}

/**
 * Adds a focus change listener on the tab group. A focus change listener is fired
 * whenever a method causes the focus member to change. Note that change listeners
 * can only be registered on the root tab group (i.e. a tab group with no parent)
 * 
 * @param listener [AjxListener]
 * 
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.addFocusChangeListener =
function(listener) {
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
		
	if (this._evtMgr == null)
		this._evtMgr = new AjxEventMgr();	
	this._evtMgr.addListener(DwtEvent.STATE_CHANGE, listener);
}

/**
 * Removes a focus change listener from a the tab group. This method may only
 * be called on root tab groups
 * 
 * @param listener [AjxListener]
 * 
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.removeFocusChangeListener =
function(listener) {
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
		
	if (this._evtMgr == null)
		return;	
	this._evtMgr.removeListener(DwtEvent.STATE_CHANGE, listener);
}

/**
 * Add a member to the tab group
 * 
 * @param member [DwtControl, DwtTabGroup, input]	member to be added
 * @param index [int]	Index at which to add the member
 */
DwtTabGroup.prototype.addMember =
function(member, index) {
	this._members.add(member, index);
	
	// Register me as the new parent
	if (member instanceof DwtTabGroup)
		member.newParent(this);
}

DwtTabGroup.prototype.addMemberAfter =
function(newMember, afterMember) {
	this.addMember(newMember, this._members.indexOf(afterMember) + 1);
}

DwtTabGroup.prototype.addMemberBefore =
function(newMember, beforeMember) {
	var idx = this._members.indexOf(beforeMember);
	this.addMember(newMember, (idx != 0) ? idx-- : 0);
}

/**
 * This method removes a member for the tab group. The pre-condition to this method
 * is that the member in currently in the tab group. If the member being removed
 * is currently the focus member, then we will first try and set focus to the
 * logically previous member. If this fails, we will try the logical next member
 * 
 * @param member [DwtControl, HTML input element ] member to remove from the tab group
 * @param dontNotify [Boolean]	optional, true notification is not fired. This flag
 * 		typically set by Dwt tag mangement framework when it is calling into this method
 */
DwtTabGroup.prototype.removeMember =
function(member, dontNotify) {
	/* If we are removing the current focus member, then we need to adjust the focus
	 * member index. If the tab group is empty as a result of the removal
	 */
	var root = this._getRootTabGroup();
	var focusMember = false;

	if (root._currFocusMember != null) 
		focusMember = (!(member instanceof DwtTabGroup) && root._currFocusMember == member)
			? true : this.contains(root._currFocusMember);

	if (focusMember) {
		root._currFocusMember = this._getPrevMember(member);
		
		if (!root._currFocusMember)
			root._currFocusMember =  this._getNextMember(this, member);
			
		if (!dontNotify)
			this._notifyListeners(root._currFocusMember);
	}
	
	this._members.remove(member);
}

/**
 * Checks to see if the tab group contains <member>
 * 
 * @param member member for which to search
 * 
 * @return	true if the tab group contains member
 */
DwtTabGroup.prototype.contains =
function(member) {
	
	var sz = this._members.size();
	var a = this._members.getArray();
	var m;
	for (var i = 0; i < sz; i++) {
		m = a[i]
		if (!(m instanceof DwtTabGroup) && m == member)
			return true;
		else if (m.contains(member))
			return true;
	}
	return false;
}

DwtTabGroup.prototype._isFocusMember =
function(member) {
	var root = this._getRootTabGroup();
	
}

DwtTabGroup.prototype.newParent =
function(newParent) {
	this._parent = newParent;
}


DwtTabGroup.prototype.getFocusMember =
function(){
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;	
	return this._currFocusMember;
}

/**
 * Set the focus member. 
 * 
 * @param member [DwtControl, input] The member to which to set focus. member must
 * 		must be a member of the tab group hierarchy
 * @param dontNotify [Boolean]	optional, true notification is not fired. This flag
 * 		typically set by Dwt tag mangement framework when it is calling into this method
 */
DwtTabGroup.prototype.setFocusMember =
function(member, dontNotify) {
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
	this._currFocusMember = member;
	// TODO NOTIFY!!!!!
}

/**
 * This method gets then next focus member in the Tab Group. If there is not next
 * member, resets to the first member in the tab group 
 * 
 * @param dontNotify [Boolean]	optional, true notification is not fired. This flag
 * 		typically set by Dwt tag mangement framework when it is calling into this method
 * 
 * @return new focus member or null if there is no focus member or if the focus
 * 		member has not changed (i.e. only one member in the tabgroup)
 */
DwtTabGroup.prototype.getNextFocusMember =
function(dontNotify) {
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
		
	return this._setFocusMember(true, dontNotify)
}

/**
 * This method gets then previous focus member in the Tab Group. If there is not next
 * member, then sets to the last member in the tab group
 * 
 * @param dontNotify [Boolean]	optional, true notification is not fired. This flag
 * 		typically set by Dwt tag mangement framework when it is calling into this method
 * 
 * @return new focus member or null if there is no focus member or if the focus
 * 		member has not changed (i.e. only one member in the tabgroup)
 */
DwtTabGroup.prototype.getPrevFocusMember =
function(dontNotify) {
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
		
	return this._setFocusMember(false, dontNotify)
}

DwtTabGroup.prototype._setFocusMember =
function(next, dontNotify) {
	var m = (next) ? this._getNextMember() : this._getPrevMember();
	
	if (m == null) {
		m = (next) ? this._getLeftMostMember(): this._getRightMostMember();

		// Test for the case where there is only one member in the tabgroup
		if (m == this._currFocusMember)
			return null;
	} else {
		this._currFocusMember = m
	}
	
	if (!dontNotify)
		this._notifyListeners(this._currFocusMember);
	
	return this._currFocusMember;
}



/**
 * resets the the focus member to the first element in the tab group cascading down
 * the tab gropu hierarchy if the first member is itself a DwtTabGroup
 * 
 * @param dontNotify [Boolean]	optional, true notification is not fired. This flag
 * 		typically set by Dwt tag mangement framework when it is calling into this method
 */
DwtTabGroup.prototype.resetFocusMember =
function(dontNotify) {
	if (this._parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
	
	m = this._getLeftMostMember();	
	
	// If the reset was to the current focus member, then do nothing
	if (this._currFocusMember != m) {
		this._currFocusMember = m;
		if (!dontNotify)
			this._notifyListeners(this._currFocusMember);
	}
	
	return m;

}

/**
 * Recrusively dumps the contents of the tab group
 */
DwtTabGroup.prototype.dump =
function() {
	this._dump(this, 0);
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

/* Gets the previous member in the tag group.
 */
DwtTabGroup.prototype._getPrevMember =
function(member) {
	a = this._members.getArray();
	// Start working from the member to the immediate left of <member> leftwards
	for (var i = this._members.indexOf(member) - 1; i > -1; i--) {
		var prevMember = a[i];
		/* if sibling is not a tabgroup, then it is the previous child. If the
		 * sibling is a tabgroup, get it's rightmost member if the tab group is
		 * not empty.*/
		if (!(prevMember instanceof DwtTabGroup))
			return prevMember;
		else if ((prevMember = prevMember._getRightMostMember()) != null)
				return prevMember;
	}
	
	/* If we have fallen through to here it is because the tag group only has 
	 * one member. So we roll up to the parent, unless we are at the root in 
	 * which case we return null; */
	return (this._parent != null) ? this._parent._getPrevMember(this) : null;
}

/* Gets the previous member in the tag group.
 */
DwtTabGroup.prototype._getNextMember =
function(member) {
	var a = this._members.getArray();
	var sz = this._members.size();
	// Start working from the member to the immediate left of <member> leftwards
	for (var i = this._members.indexOf(member) + 1; i < sz; i++) {
		var nextMember = a[i];
		/* if sibling is not a tabgroup, then it is the nextious child. If the
		 * sibling is a tabgroup, get it's rightmost member if the tab group is
		 * not empty.*/
		if (!(nextMember instanceof DwtTabGroup))
			return nextMember;
		else if ((nextMember = nextMember._getLeftMostMember()) != null)
				return nextMember;
	}
	
	/* If we have fallen through to here it is because the tag group only has 
	 * one member. So we roll up to the parent, unless we are at the root in 
	 * which case we return null; */
	return (this._parent != null) ? this._parent._getNextMember(this) : null;
}

/* Finds the right most non-tab group member of the tabgroup. will recurse down
 * into contained tabgroups.
 */
DwtTabGroup.prototype._getRightMostMember =
function() {
	var a = this._members.getArray();
	var member = null;
	
	/* Work backwards from the rightmost member. If the member is a tabgroup, then
	 * recurse into it. If member is not a tabgroup, return it as it is the 
	 * rightmost element */
	for (var i = this._members.size() - 1; i >= 0; i--) {
		var member = a[i]
		if (!(member instanceof DwtTabGroup))
			break;
		else if ((member = member._getRightMostMember()) != null)
			break;
	}

	return member;	
}

/* Finds the right most non-tab group member of the tabgroup. will recurse down
 * into contained tabgroups.
 */
DwtTabGroup.prototype._getLeftMostMember =
function() {
	
	var sz = this._members.size();
	var a = this._members.getArray();
	var member = null;
	
	/* Work backwards from the rightmost member. If the member is a tabgroup, then
	 * recurse into it. If member is not a tabgroup, return it as it is the 
	 * rightmost element */
	for (var i = 0; i < sz; i++) {
		var member = a[i]
		if (!(member instanceof DwtTabGroup))
			break;
		else if ((member = member._getLeftMostMember()) != null)
			break;
	}

	return member;	
}

DwtTabGroup.prototype._notifyListeners =
function(newFocusMember) {
	// Only the root tab group will issue notifications
	var rootTg = this._getRootTabGroup();
	if (rootTg._evtMgr) {
		var evt = DwtTabGroup._changeEvt;
		evt.reset();
		evt.tabGroup = this;
		evt.newFocusMember = rootTg._currFocusMember;
		rootTg._evtMgr.notifyListeners(DwtEvent.STATE_CHANGE, evt);
	}
}

DwtTabGroup.prototype._getRootTabGroup =
function() {
	var root = this;
	while (root._parent != null)
		root = root._parent;
	return root;
}

