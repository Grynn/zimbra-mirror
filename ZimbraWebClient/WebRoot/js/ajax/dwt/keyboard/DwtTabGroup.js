/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */


/**
 * Creates an empty tab group.
 * @constructor
 * @class
 * A tab group is used to manage keyboard focus among a group of related visual 
 * elements. It is a tree structure consisting of elements and other tab groups.
 * <p>
 * The root tab group is the only one without a parent tab group, and is the one
 * that the application interacts with. Focus listeners register with the root
 * tab group. The root tab group tracks where focus is.
 * 
 * @param {string}	name					the name of this tab group
 *
 * @author Ross Dargahi
 */
DwtTabGroup = function(name) {

	this.__members = new AjxVector();
	this.__parent = null;
	this.__name = name;
	this.__currFocusMember = null;
	this.__evtMgr = new AjxEventMgr();
};

/** 
 * Exception string that is thrown when an operation is attempted
 * on a non-root tab group.
 */
DwtTabGroup.NOT_ROOT_TABGROUP = "NOT ROOT TAB GROUP";

DwtTabGroup.__changeEvt = new DwtTabGroupEvent();

/**
 * Returns a string representation of the object.
 * 
 * @return		{string}		a string representation of the object
 */
DwtTabGroup.prototype.toString = 
function() {
	return "DwtTabGroup";
};

/**
 * Gets the name of this tab group.
 * 
 * @return	{string}	the tab group name
 */
DwtTabGroup.prototype.getName = function() {
	return this.__name;
};

/**
 * Adds a focus change listener to the root tab group. The listener is called
 * when the focus member changes. Note that the focus member hasn't actually
 * been focused yet - only its status within the tab group has changed. It is
 * up to the listener to implement the appropriate focus action.
 * 
 * @param {AjxListener} listener	a listener
 * 
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.addFocusChangeListener =
function(listener) {
	this.__checkRoot();		
	this.__evtMgr.addListener(DwtEvent.STATE_CHANGE, listener);
};

/**
 * Removes a focus change listener from the root tab group.
 * 
 * @param {AjxListener} listener	a listener
 * 
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.removeFocusChangeListener =
function(listener) {
	this.__checkRoot();		
	this.__evtMgr.removeListener(DwtEvent.STATE_CHANGE, listener);
};

/**
 * Adds a member to the tab group.
 * 
 * @param {Array|DwtControl|DwtTabGroup|HTMLElement} member	the member(s) to be added
 * @param {number} [index] 		the index at which to add the member. If omitted, the member
 * 		will be added to the end of the tab group
 */
DwtTabGroup.prototype.addMember =
function(member, index) {
	if (!member) {return;}
	var members = (member instanceof Array) ? member : [member];

	for (var i = 0, len = members.length; i < len; i++) {
		this.__members.add(members[i], index);

		// If adding a tab group, register me as its parent
		if (members[i] instanceof DwtTabGroup) {
			members[i].newParent(this);
		}
	}
};

/**
 * Adds a member to the tab group, positioned after another member.
 * 
 * @param {DwtControl|DwtTabGroup|HTMLElement} member 		the member to be added
 * @param {DwtControl|DwtTabGroup|HTMLElement} afterMember 	the member after which to add <code>member</code>
 */
DwtTabGroup.prototype.addMemberAfter =
function(newMember, afterMember) {
	this.addMember(newMember, this.__members.indexOf(afterMember) + 1);
};

/**
 * Adds a member to the tab group, positioned before another member.
 * 
 * @param {DwtControl|DwtTabGroup|HTMLElement} member 		the member to be added
 * @param {DwtControl|DwtTabGroup|HTMLElement} beforeMember 	the member before which to add <code>member</code>
 */
DwtTabGroup.prototype.addMemberBefore =
function(newMember, beforeMember) {
	this.addMember(newMember, this.__members.indexOf(beforeMember));
};

/**
 * This method removes a member from the tab group. If the member being removed
 * is currently the focus member, then we will try to set focus to the
 * previous member. If that fails, we will try the next member.
 * 
 * @param {DwtControl|DwtTabGroup|HTMLElement} member 	the member to be removed
 * @param {boolean} [checkEnabled] 		if <code>true</code>, then make sure that if we have a newly focused member it is enabled
 * @param {boolean} [skipNotify] 		if <code>true</code>, notification is not fired. This flag typically set by Dwt tab management framework when it is calling into this method
 * @return {DwtControl|DwtTabGroup|HTMLElement}	the removed member or <code>null</code> if <code>oldMember</code> is not in the tab groups hierarchy
 */
DwtTabGroup.prototype.removeMember =
function(member, checkEnabled, skipNotify) {
	return this.replaceMember(member, null, checkEnabled, skipNotify);
};

/**
 * Removes all members.
 * 
 */
DwtTabGroup.prototype.removeAllMembers = function() {
	this.__members.removeAll();
};

/**
 * This method replaces a member in the tab group with a new member. If the member being
 * replaced is currently the focus member, then we will try to set focus to the
 * previous member. If that fails, we will try the next member.
 * 
 * @param {DwtControl|DwtTabGroup|HTMLElement} oldMember 	the member to be replaced
 * @param {DwtControl|DwtTabGroup|HTMLElement} newMember 	the replacing member
 * 		If this parameter is <code>null</code>, then this method effectively removes <code>oldMember</code>
 * @param {boolean} [checkEnabled] 	if <code>true</code>, then make sure that if we have a newly focused
 * 		member it is enabled
 * @param {boolean} [skipNotify] if <code>true</code>, notification is not fired. This flag is
 * 		typically set by the tab management framework when it is calling into this method
 * @return {DwtControl|DwtTabGroup|HTMLElement}	replaced member or <code>null></code> if <code>oldMember</code> is not in the tab group
 */
DwtTabGroup.prototype.replaceMember =
function(oldMember, newMember, checkEnabled, skipNotify, focusItem, noFocus) {
	var tg = this.__getTabGroupForMember(oldMember);
	if (!tg) {
		this.addMember(newMember);
		return null;
	}

	/* If we are removing the current focus member, then we need to adjust the focus
	 * member index. If the tab group is empty as a result of the removal
	 */
	var root = this.__getRootTabGroup();
	var newFocusMember;
	if (focusItem) {
		newFocusMember = focusItem;
	} else if (root.__currFocusMember == oldMember ||
		((oldMember instanceof DwtTabGroup) && oldMember.contains(root.__currFocusMember))) {

		if (newMember) {
			newFocusMember = (newMember instanceof DwtTabGroup) ? newMember.getFirstMember() : newMember;
		} else {
			newFocusMember = this.__getPrevMember(oldMember, checkEnabled);
			if (!newFocusMember) {
				newFocusMember =  this.__getNextMember(oldMember, checkEnabled);
			}
		}
	}
	if (newFocusMember && !noFocus) {
		root.__currFocusMember = newFocusMember;
//		DBG.println("kbnav", "DwtTabGroup.replaceMember: current focus member is now " + root.__currFocusMember);
		if (!skipNotify) {
			this.__notifyListeners(newFocusMember);
		}
	}

	if (newMember instanceof DwtTabGroup) {
		newMember.newParent(this);
	}
		
	return newMember ? this.__members.replaceObject(oldMember, newMember) : this.__members.remove(oldMember);
};

/**
 * Returns true if this tab group contains <code>member</code>.
 * 
 * @param {DwtControl|DwtTabGroup|HTMLElement} member	the member for which to search
 * 
 * @return {boolean}	<code>true</code> if the tab group contains member
 */
DwtTabGroup.prototype.contains =
function(member) {	
	return (Boolean(this.__getTabGroupForMember(member)));
};

/**
 * Sets a new parent for this tab group.
 * 
 * @param {DwtTabGroup} newParent 	the new parent. If the parent is <code>null</code>, then this tabGroup is the root tab group.
 */
DwtTabGroup.prototype.newParent =
function(newParent) {
	this.__parent = newParent;
};

/**
 * Gets the first member of the tab group.
 * 
 * @param {boolean} [checkEnabled]		if <code>true</code>, then return first enabled member
 *
 * @return {DwtControl|HTMLElement}	the first member of the tab group
 */
DwtTabGroup.prototype.getFirstMember =
function(checkEnabled) {
	return this.__getLeftMostMember(checkEnabled);
};

/**
 * Gets the child tab group member by its name.
 *
 * @param {string}	name		the name of the child tab group
 */
DwtTabGroup.prototype.getTabGroupMemberByName = function(name) {
	var members = this.__members.getArray();
	for (var i = 0; i < members.length; i++) {
		var member = members[i];
		if (member instanceof DwtTabGroup && member.getName() == name) {
			return member;
		}
	}
};
 
/**
 * Gets the last member of the tab group.
 * 
 * @param {boolean} [checkEnabled]		if <code>true</code>, then return last enabled member
 *
 * @return {DwtControl|HTMLElement}	the last member of the tab group
 */
DwtTabGroup.prototype.getLastMember =
function(checkEnabled) {
	return this.__getRightMostMember(checkEnabled);
};
 
/**
 * Returns the current focus member.
 * 
 * @return {DwtControl|HTMLElement}	current focus member
 * 
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.getFocusMember =
function(){
	this.__checkRoot();
	return this.__currFocusMember;
};

/**
 * Sets the current focus member. 
 * 
 * @param {DwtControl|HTMLElement} member 		the member to which to set focus
 * @param {boolean} [checkEnabled] 	if <code>true</code>, then make sure the member is enabled
 * @param {boolean} [skipNotify] if <code>true</code>, notification is not fired. This flag
 * 		typically set by Dwt tab management framework when it is calling into this method
 * 
 * @return {boolean}	<code>true</code> if member was part of the tab group hierarchy, else false
 *
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.setFocusMember =
function(member, checkEnabled, skipNotify) {
	this.__checkRoot();	
	if (!this.__checkEnabled(member, checkEnabled)) {
		return false;
	}

	var tg = this.__getTabGroupForMember(member);
	if (tg) {
		this.__currFocusMember = member;
//		DBG.println("kbnav", "DwtTabGroup.setFocusMember: current focus member is now " + this.__currFocusMember);
		if (!skipNotify) {
			this.__notifyListeners(this.__currFocusMember);
		}
		return true;	
	}
	return false;
};

/**
 * This method sets and returns the next focus member in this tab group. If there is no next
 * member, sets and returns the first member in the tab group.
 * 
 * @param {boolean} [checkEnabled] 	if <code>true</code>, get the next enabled member
 * @param {boolean} [skipNotify] if <code>true</code>, notification is not fired. This flag
 * 		typically set by {@link Dwt} tab management framework when it is calling into this method
 * 
 * @return {DwtControl|HTMLElement}	new focus member or <code>null</code> if there is no focus member or if the focus
 * 		member has not changed (i.e. only one member in the tabgroup)
 *
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.getNextFocusMember =
function(checkEnabled, skipNotify) {
	this.__checkRoot();		
	return this.__setFocusMember(true, checkEnabled, skipNotify);
};

/**
 * This method sets and returns the previous focus member in this tab group. If there is no
 * previous member, sets and returns the last member in the tab group.
 * 
 * @param {boolean} [checkEnabled] 	if <code>true</code>, get the previously enabled member
 * @param {boolean} [skipNotify] if <code>true</code>, notification is not fired. This flag
 * 		typically set by Dwt tab management framework when it is calling into this method
 * 
 * @return {DwtControl|HTMLElement}	new focus member or <code>null</code> if there is no focus member or if the focus
 * 		member has not changed (i.e. only one member in the tabgroup)
 *
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.getPrevFocusMember =
function(checkEnabled, skipNotify) {
	this.__checkRoot();		
	return this.__setFocusMember(false, checkEnabled, skipNotify);
};

/**
 * Resets the the focus member to the first element in the tab group.
 * 
 * @param {boolean} [checkEnabled] 	if <code>true</code>, then pick a enabled member to which to set focus
 * @param {boolean} [skipNotify] if <code>true</code>, notification is not fired. This flag
 * 		typically set by Dwt tab management framework when it is calling into this method
 * 
 * @return {DwtControl|HTMLElement}	the new focus member
 *
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.resetFocusMember =
function(checkEnabled, skipNotify) {
	this.__checkRoot();
	var focusMember = this.__getLeftMostMember(checkEnabled);
	if ((focusMember != this.__currFocusMember) && !skipNotify) {
		this.__notifyListeners(this.__currFocusMember);
	}
//	DBG.println("kbnav", "DwtTabGroup.resetFocusMember: current focus member is now " + this.__currFocusMember);
	this.__currFocusMember = focusMember;
	
	return this.__currFocusMember;
};

/**
 * Dumps the contents of the tab group.
 * 
 * @private
 */
DwtTabGroup.prototype.dump =
function(debugLevel) {
	if (!window.AjxDebug && window.DBG) { return; }
	this.__dump(this, debugLevel);
};

/**
 * Gets the size of the group.
 * 
 * @return	{number}	the size
 */
DwtTabGroup.prototype.size =
function() {
	return this.__members.size();
};

/**
 * Returns the previous member in the tag group.
 * 
 * @private
 */
DwtTabGroup.prototype.__getPrevMember =
function(member, checkEnabled) {
	var a = this.__members.getArray();
	// Start working from the member to the immediate left, then keep going left
	for (var i = this.__members.indexOf(member) - 1; i > -1; i--) {
		var prevMember = a[i];
		/* if sibling is not a tab group, then it is the previous child. If the
		 * sibling is a tab group, get its rightmost member.*/
		if (!(prevMember instanceof DwtTabGroup)) {
			if (this.__checkEnabled(prevMember, checkEnabled)) {
				return prevMember;
			}
		} else {
			prevMember = prevMember.__getRightMostMember(checkEnabled);
			if (prevMember && this.__checkEnabled(prevMember, checkEnabled)) {
				return prevMember;
			}
		}
	}
	/* If we have fallen through to here it is because the tab group only has 
	 * one member. So we roll up to the parent, unless we are at the root in 
	 * which case we return null. */
	return this.__parent ? this.__parent.__getPrevMember(this, checkEnabled) : null;
};

/**
 * Returns true if the given member can accept focus, or if there is no need to check.
 * If we are checking, the member must be enabled and visible if it is a control, and
 * enabled otherwise. A member may also set the "noTab" flag to take itself out of the
 * tab hierarchy.
 * 
 * @private
 */
DwtTabGroup.prototype.__checkEnabled =
function(member, checkEnabled) {
	if (!checkEnabled) return true;
	if (!member || member.noTab) return false;
	if (member instanceof DwtControl) {
		return (member.getEnabled() && member.getVisible());
	} else {
		return !member.disabled && Dwt.getVisible(member);
	}
};

/**
 * Sets and returns the next member in the tag group.
 * 
 * @private
 */
DwtTabGroup.prototype.__getNextMember =
function(member, checkEnabled) {
	var a = this.__members.getArray();
	var sz = this.__members.size();

	// Start working from the member to the immediate left of <member> rightwards
	for (var i = this.__members.indexOf(member) + 1; i < sz; i++) {
		var nextMember = a[i];
		/* if sibling is not a tab group, then it is the next child. If the
		 * sibling is a tab group, get its leftmost member.*/
		if (!(nextMember instanceof DwtTabGroup)) {
			if (this.__checkEnabled(nextMember, checkEnabled)) {
				return nextMember;
			}
		} else {
			nextMember = nextMember.__getLeftMostMember(checkEnabled);
			if (nextMember && this.__checkEnabled(nextMember, checkEnabled)) {
				return nextMember;
			}
		}
	}

	/* If we have fallen through to here it is because the tab group only has 
	 * one member or we are at the end of the list. So we roll up to the parent, 
	 * unless we are at the root in which case we return null. */
	return this.__parent ? this.__parent.__getNextMember(this, checkEnabled) : null;
};

/**
 * Finds the rightmost member of the tab group. Will recurse down
 * into contained tab groups if necessary.
 * @private
 */
DwtTabGroup.prototype.__getRightMostMember =
function(checkEnabled) {
	var a = this.__members.getArray();
	var member = null;
	
	/* Work backwards from the rightmost member. If the member is a tab group, then
	 * recurse into it. If member is not a tab group, return it as it is the 
	 * rightmost element. */
	for (var i = this.__members.size() - 1; i >= 0; i--) {
		member = a[i]
		if (!(member instanceof DwtTabGroup)) {
			if (this.__checkEnabled(member, checkEnabled)) break;
		} else {
			member = member.__getRightMostMember(checkEnabled);
			if (member && this.__checkEnabled(member, checkEnabled)) break;
		}
	}

	return (member && this.__checkEnabled(member, checkEnabled)) ? member : null;
};

/**
 *  Finds the rightmost member of the tab group. Will recurse down
 * into contained tab groups if necessary.
 * @private
 */
DwtTabGroup.prototype.__getLeftMostMember =
function(checkEnabled) {
	var sz = this.__members.size();
	var a = this.__members.getArray();
	var member = null;

	/* Work forwards from the leftmost member. If the member is a tabgroup, then
	 * recurse into it. If member is not a tabgroup, return it as it is the 
	 * rightmost element */
	for (var i = 0; i < sz; i++) {
		member = a[i]
		if (!(member instanceof DwtTabGroup)) {
			if  (this.__checkEnabled(member, checkEnabled)) break;
		} else {
			member = member.__getLeftMostMember(checkEnabled);
			if (member && this.__checkEnabled(member, checkEnabled)) break;
		}
	}

	return (member && this.__checkEnabled(member, checkEnabled)) ? member : null;
};

/**
 * Notifies focus change listeners.
 * @private
 */
DwtTabGroup.prototype.__notifyListeners =
function(newFocusMember) {
	// Only the root tab group will issue notifications
	var rootTg = this.__getRootTabGroup();
	if (rootTg.__evtMgr) {
		var evt = DwtTabGroup.__changeEvt;
		evt.reset();
		evt.tabGroup = this;
		evt.newFocusMember = newFocusMember;
		rootTg.__evtMgr.notifyListeners(DwtEvent.STATE_CHANGE, evt);
	}
};

/**
 * @private
 */
DwtTabGroup.prototype.__getRootTabGroup =
function() {
	var root = this;
	while (root.__parent) {
		root = root.__parent;
	}
	
	return root;
}

/**
 * @private
 */
DwtTabGroup.prototype.__dump =
function(tg, debugLevel, level) {
	level = level || 0;
	var levelIndent = "";
	for (var i = 0; i < level; i++) {
		levelIndent += "&nbsp;&nbsp;&nbsp;&nbsp;";
	}
	
	debugLevel = debugLevel || AjxDebug.DBG1;
	DBG.println(debugLevel, levelIndent + " TABGROUP: " + tg.__name);
	levelIndent += "&nbsp;&nbsp;&nbsp;&nbsp;";
	
	var sz = tg.__members.size();
	var a = tg.__members.getArray();
	for (var i = 0; i < sz; i++) {
		if (a[i] instanceof DwtTabGroup) {
			tg.__dump(a[i], debugLevel, level + 1);
		} else if (a[i].toString) {
			DBG.println(debugLevel, levelIndent + "   " + a[i].toString());
		} else {
			DBG.println(debugLevel, levelIndent + "   " + a[i].tagName);
		}
	}
};

/**
 * Sets the next or previous focus member.
 * @private
 */
DwtTabGroup.prototype.__setFocusMember =
function(next, checkEnabled, skipNotify) {
	// If there is currently no focus member, then reset to the first member
	// and return
	if (!this.__currFocusMember) {
		return this.resetFocusMember(checkEnabled, skipNotify);
	}
	
	var tabGroup = this.__getTabGroupForMember(this.__currFocusMember);
	if (!tabGroup) {
		DBG.println(AjxDebug.DBG1, "tab group not found for focus member");
		return null;
	}
	var m = (next) ? tabGroup.__getNextMember(this.__currFocusMember, checkEnabled) 
				   : tabGroup.__getPrevMember(this.__currFocusMember, checkEnabled);

	if (!m) {
		m = (next) ? this.__getLeftMostMember(checkEnabled)
				   : this.__getRightMostMember(checkEnabled);

		// Test for the case where there is only one member in the tabgroup
		if (m == this.__currFocusMember) {
			return null;
		}
	}

	this.__currFocusMember = m;
	
//	DBG.println("kbnav", "DwtTabGroup._setFocusMember: current focus member is now " + this.__currFocusMember);
	if (!skipNotify) {
		this.__notifyListeners(this.__currFocusMember);
	}
	
	return this.__currFocusMember;
};

/**
 * @private
 */
DwtTabGroup.prototype.__getTabGroupForMember =
function(member) {
	if (!member) return null;
	var sz = this.__members.size();
	var a = this.__members.getArray();
	var m;
	for (var i = 0; i < sz; i++) {
		m = a[i];
		if (m == member) {
			return this;
		} else if (m instanceof DwtTabGroup && (m = m.__getTabGroupForMember(member))) {
			return m;
		}
	}
	return null;
};

/**
 * Throws an exception if this is not the root tab group.
 * 
 * @private
 */
DwtTabGroup.prototype.__checkRoot =
function() {
	if (this.__parent) {
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
	}
};
