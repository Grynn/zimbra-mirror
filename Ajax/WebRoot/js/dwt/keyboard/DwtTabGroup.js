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
* @param {String} name [String]  name of this tab group (optional). 
* 
* @author Ross Dargahi
*/
function DwtTabGroup(name) {
	/**@private*/
	this.__members = new AjxVector();
	/**@private*/
	this.__parent = null;
	/**@private*/
	this.__name = name
	/**@private*/
	this.__currFocusMember = null;
}

/** 
 * Exception that is thrown is performing an operation that must occur only on
 * a root tabgroup (i.e. one without a parent
 * @type String
 */
DwtTabGroup.NOT_ROOT_TABGROUP = "NOT ROOT TAB GROUP";

/**@private*/
DwtTabGroup.__changeEvt = new DwtTabGroupEvent();

/**
 * @return return a string version of the class' name
 * @type String
 */
DwtTabGroup.prototype.toString = 
function() {
	return "DwtTabGroup";
}

/**
 * Adds a focus change listener on the tab group. A focus change listener is fired
 * whenever a method causes the focus member to change. Note that change listeners
 * can only be registered on the root tab group (i.e. a tab group with no parent)
 * 
 * @param {AjxListener} listener The listener object to resgier
 * 
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 * 
 * @see AjxListener
 */
DwtTabGroup.prototype.addFocusChangeListener =
function(listener) {
	if (this.__parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
		
	if (this.__evtMgr == null)
		/**@private*/
		this.__evtMgr = new AjxEventMgr();
			
	this.__evtMgr.addListener(DwtEvent.STATE_CHANGE, listener);
}

/**
 * Removes a focus change listener from a the tab group. This method may only
 * be called on root tab groups
 * 
 * @param {AjxListener} listener The listener object to resgier
 * 
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 *
 * @see AjxListener
 */
DwtTabGroup.prototype.removeFocusChangeListener =
function(listener) {
	if (this.__parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
		
	if (this.__evtMgr == null)
		return;	
	this.__evtMgr.removeListener(DwtEvent.STATE_CHANGE, listener);
}

/**
 * Add a member to the tab group
 * 
 * @param {DwtControl|DwtTabGroup|HTMLElement} member member to be added
 * @param {Int} index Index at which to add the member. If omitted, the member
 * 		will be added to the end of the tab group (optional)
 */
DwtTabGroup.prototype.addMember =
function(member, index) {
	this.__members.add(member, index);
	
	// Register me as the new parent
	if (member instanceof DwtTabGroup)
		member.newParent(this);
}

/**
 * Add a member to the tab group positioned after <code>afterMember</code>
 * 
 * @param {DwtControl|DwtTabGroup|HTMLElement} member member to be added
 * @param {DwtControl|DwtTabGroup|HTMLElement} afterMember member after which to add
 * 		<code>member</code>
 */
DwtTabGroup.prototype.addMemberAfter =
function(newMember, afterMember) {
	this.addMember(newMember, this.__members.indexOf(afterMember) + 1);
}

/**
 * Add a member to the tab group positioned before <code>beforeMember</code>
 * 
 * @param {DwtControl|DwtTabGroup|HTMLElement} member member to be added
 * @param {DwtControl|DwtTabGroup|HTMLElement} beforeMember member before which to add
 * 		<code>member</code>
 */
DwtTabGroup.prototype.addMemberBefore =
function(newMember, beforeMember) {
	var idx = this.__members.indexOf(beforeMember);
	this.addMember(newMember, (idx != 0) ? idx-- : 0);
}

/**
 * This method removes a member for the tab group. If the member being removed
 * is currently the focus member, then we will first try and set focus to the
 * logically previous member. If this fails, we will try the logical next member
 * 
 * @param {DwtControl|DwtTabGroup|HTMLElement} member member to be removed
 * @param {Boolean} checkEnabled true, then make sure that if we have a newly focused
 * 		member it is enabled (optional)
 * @param {Boolean} dontNotify true notification is not fired. This flag
 * 		typically set by Dwt tab mangement framework when it is calling into this 
 * 		method (optional)
 * @return removed member This may be null if <code>oldMember</code> is not in the
 * 		tab groups hierarchy
 * @type DwtControl|DwtTabGroup|HTMLElement
 */
DwtTabGroup.prototype.removeMember =
function(member, checkEnabled, dontNotify) {
	return this.replaceMember(member, null, false, dontNotify);
}

/**
 * This method replaces a member in the tab group with a new member. If the member being
 * replaced is currently the focus member, then we will first try and set focus to the
 * logically previous member. If this fails, we will try the logical next member
 * 
 * @param {DwtControl|DwtTabGroup|HTMLElement} oldMember member to be replaced
 * @param {DwtControl|DwtTabGroup|HTMLElement} newMember replacing member
 * 		If this parameter is null, then this method effectively removes <code>oldElement</code>
 * @param {Boolean} checkEnabled true, then make sure that if we have a newly focused
 * 		member it is enabled (optional)
 * @param {Boolean} dontNotify true notification is not fired. This flag
 * 		typically set by Dwt tab mangement framework when it is calling into this 
 * 		method (optional)
 * @return replaced member. This may be null if <code>oldMember</code> is not in the
 * 		tab groups hierarchy
 * @type DwtControl|DwtTabGroup|HTMLElement
 */DwtTabGroup.prototype.replaceMember =
function(oldMember, newMember, checkEnabled, dontNotify) {
	var tg = this.__getTabGroupForMember(oldMember);

	if (tg == null)
		return null;

	/* If we are removing the current focus member, then we need to adjust the focus
	 * member index. If the tab group is empty as a result of the removal
	 */
	var root = this.__getRootTabGroup();
	var focusMember = (root.__currFocusMember == oldMember 
					   || ((oldMember instanceof DwtTabGroup) && oldMember.contains(root.__currFocusMember)))
				? true : false;

	if (focusMember) {
		root.__currFocusMember = this.__getPrevMember(oldMember, checkEnabled);
		
		if (!root.__currFocusMember)
			root.__currFocusMember =  this.__getNextMember(oldMember, checkEnabled);
			
		if (!dontNotify)
			this.__notifyListeners(root.__currFocusMember);
	}
	
	if (newMember == null)	
		return this.__members.remove(oldMember);
	else
		return this.__members.replaceObject(oldMember, newMember);	
}

/**
 * Checks to see if the tab group contains <code>member</code>
 * 
 * @param {DwtControl|DwtTabGroup|HTMLElement} member member for which to search
 * 
 * @returntrue if the tab group contains member
 * @type Boolean
 */
DwtTabGroup.prototype.contains =
function(member) {	
	return (this.__getTabGroupForMember(member)) ? true : false;
}

/**
 * Set a new parent for the tab group
 * 
 * @param {DwtTabGroup} newParent the new parent. If the parent is null, then
 * 		the tabGroup becomes a "Root tabgroup"
 */
DwtTabGroup.prototype.newParent =
function(newParent) {
	this.__parent = newParent;
}

/**
 * Gets the first member in the tab group
 * 
 * @param {Boolean} checkEnabled true, then return first enabled member (optional)
 *
 * @return the first member of the tab group
 * @type DwtControl|HTMLElement
 */
 DwtTabGroup.prototype.getFirstMember =
 function(checkEnabled) {
 	return this.__getLeftMostMember(checkEnabled);
 }
 
/**
 * Gets the lst member of the tab group
 * 
 * @param {Boolean} checkEnabled true, then return last enabled member (optional)
 *
 * @return the last member of the tab group
 * @type DwtControl|HTMLElement
 */
 DwtTabGroup.prototype.getLastMember =
 function(checkEnabled) {
 	return this.__getRightMostMember(checkEnabled);
 }
 
/**
 * Gets the current focus member.
 * 
 * @return current focus member
 * @type DwtControl|HTMLElement
 * 
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.getFocusMember =
function(){
	if (this.__parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;	
	return this.__currFocusMember;
}

/**
 * Set the focus member. 
 * 
 * @param {DwtControl|HTMLElement} member The member to which to set focus. member must
 * 		must be a member of the tab group hierarchy
 * @param {Boolean} checkEnabled true, then make sure the member is enabled (optional)
 * @param {Boolean} dontNotify true notification is not fired. This flag
 * 		typically set by Dwt tab mangement framework when it is calling into this 
 * 		method (optional)
 * 
 * @return true if member was part of the tab group hierarchy, else false
 * @type Boolean
 *
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.setFocusMember =
function(member, checkEnabled, dontNotify) {
	if (this.__parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
	
	if (!this.__checkEnabled(member, checkEnabled))
		return false;

	var tg = this.__getTabGroupForMember(member);
	if (tg != null) {
		this.__currFocusMember = member;
		if (!dontNotify)
			this.__notifyListeners(this.__currFocusMember);
		return true;	
	}
	return false;
}

/**
 * This method gets then next focus member in the Tab Group. If there is not next
 * member, resets to the first member in the tab group 
 * 
 * @param {Boolean} checkEnabled true, then get the next enabled member(optional)
 * @param {Boolean} dontNotify true notification is not fired. This flag
 * 		typically set by Dwt tab mangement framework when it is calling into this 
 * 		method (optional)
 * 
 * @return new focus member or null if there is no focus member or if the focus
 * 		member has not changed (i.e. only one member in the tabgroup)
 * @type DwtControl|HTMLElement
 *
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.getNextFocusMember =
function(checkEnabled, dontNotify) {
	if (this.__parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
		
	return this.__setFocusMember(true, checkEnabled, dontNotify);
}

/**
 * This method gets then previous focus member in the Tab Group. If there is not next
 * member, then sets to the last member in the tab group
 * 
 * @param {Boolean} checkEnabled true, then get the previously enabled member (optional)
 * @param {Boolean} dontNotify true notification is not fired. This flag
 * 		typically set by Dwt tab mangement framework when it is calling into this 
 * 		method (optional)
 * 
 * @return new focus member or null if there is no focus member or if the focus
 * 		member has not changed (i.e. only one member in the tabgroup)
 * @type DwtControl|HTMLElement
 *
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.getPrevFocusMember =
function(checkEnabled, dontNotify) {
	if (this.__parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
		
	return this.__setFocusMember(false, checkEnabled, dontNotify);
}

/**
 * resets the the focus member to the first element in the tab group cascading down
 * the tab gropu hierarchy if the first member is itself a DwtTabGroup
 * 
 * @param {Boolean} checkEnabled true, then make pick a enabled member to which to
 * 		set focus (optional)
 * @param {Boolean} dontNotify true notification is not fired. This flag
 * 		typically set by Dwt tab mangement framework when it is calling into this 
 * 		method (optional)
 * 
 * @return the new focus member
 * @type DwtControl|HTMLElement
 *
 * @throws DwtTabGroup.NOT_ROOT_TABGROUP
 */
DwtTabGroup.prototype.resetFocusMember =
function(checkEnabled, dontNotify) {
	if (this.__parent != null)
		throw DwtTabGroup.NOT_ROOT_TABGROUP;
	
	this.__currFocusMember = this.__getLeftMostMember(checkEnabled);

	if (!dontNotify)
		this.__notifyListeners(this.__currFocusMember);
	
	return this.__currFocusMember;
}

/**
 * Recrusively dumps the contents of the tab group
 */
DwtTabGroup.prototype.dump =
function() {
	this.__dump(this, 0);
}

/**
 * Gets the previous member in the tag group.
 * @private
 */
DwtTabGroup.prototype.__getPrevMember =
function(member, checkEnabled) {
	a = this.__members.getArray();
	// Start working from the member to the immediate left of <member> leftwards
	for (var i = this.__members.indexOf(member) - 1; i > -1; i--) {
		var prevMember = a[i];
		/* if sibling is not a tabgroup, then it is the previous child. If the
		 * sibling is a tabgroup, get it's rightmost member if the tab group is
		 * not empty.*/
		if (!(prevMember instanceof DwtTabGroup)) {
			if (this.__checkEnabled(prevMember, checkEnabled))
				return prevMember;
		} else if ((prevMember = prevMember.__getRightMostMember(checkEnabled)) != null) {
			if (this.__checkEnabled(prevMember, checkEnabled))
				return prevMember;
		}
	}
	/* If we have fallen through to here it is because the tag group only has 
	 * one member. So we roll up to the parent, unless we are at the root in 
	 * which case we return null; */
	return (this.__parent != null) ? this.__parent.__getPrevMember(this, checkEnabled) : null;
}

DwtTabGroup.prototype.__checkEnabled =
function(member, checkEnabled) {
	if (!checkEnabled)
		return true;
	else
		return (member instanceof DwtControl)
			? member.getEnabled() : !member.disabled;
}

/**
 * Gets the next member in the tag group.
 * @private
 */
DwtTabGroup.prototype.__getNextMember =
function(member, checkEnabled) {
	var a = this.__members.getArray();
	var sz = this.__members.size();

	// Start working from the member to the immediate left of <member> rightwards
	for (var i = this.__members.indexOf(member) + 1; i < sz; i++) {
		var nextMember = a[i];
		/* if sibling is not a tabgroup, then it is the next child. If the
		 * sibling is a tabgroup, get it's rightmost member if the tab group is
		 * not empty.*/
		if (!(nextMember instanceof DwtTabGroup)) {
			if (this.__checkEnabled(nextMember, checkEnabled))
				return nextMember;
		} else if ((nextMember = nextMember.__getLeftMostMember(checkEnabled)) != null) {
			if (this.__checkEnabled(nextMember, checkEnabled))
				return nextMember;
		}
	}

	/* If we have fallen through to here it is because the tag group only has 
	 * one member or we are at the end of the list. So we roll up to the parent, 
	 * unless we are at the root in which case we return null; */
	return (this.__parent != null) ? this.__parent.__getNextMember(this, checkEnabled) : null;
}

/**
 *  Finds the right most non-tab group member of the tabgroup. will recurse down
 * into contained tabgroups.
 * @private
 */
DwtTabGroup.prototype.__getRightMostMember =
function(checkEnabled) {
	var a = this.__members.getArray();
	var member = null;
	
	/* Work backwards from the rightmost member. If the member is a tabgroup, then
	 * recurse into it. If member is not a tabgroup, return it as it is the 
	 * rightmost element */
	for (var i = this.__members.size() - 1; i >= 0; i--) {
		var member = a[i]
		if (!(member instanceof DwtTabGroup)) {
			if (this.__checkEnabled(member, checkEnabled))
				break;
		} else if (((member = member.__getRightMostMember(checkEnabled)) != null)
				&& this.__checkEnabled(member, checkEnabled)) {
			break;
		}
	}

	if (member && this.__checkEnabled(member, checkEnabled))
		return member;
	else
		return null;
}

/**
 *  Finds the right most non-tab group member of the tabgroup. will recurse down
 * into contained tabgroups.
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
		var member = a[i]
		if (!(member instanceof DwtTabGroup)) {
			if  (this.__checkEnabled(member, checkEnabled)) 
				break;
		} else if (((member = member.__getLeftMostMember(checkEnabled)) != null)
				&& this.__checkEnabled(member, checkEnabled)) {
			break;
		}
	}

	if (member && this.__checkEnabled(member, checkEnabled))
		return member;
	else
		return null;
}


/**
 * Notify's focus change listeners
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
}

/**
 * @private
 */
DwtTabGroup.prototype.__getRootTabGroup =
function() {
	var root = this;
	while (root.__parent != null)
		root = root.__parent;
	return root;
}

/**
 * @private
 */
DwtTabGroup.prototype.__dump =
function(tg, level) {
	var levelIndent = "";
	for (var i = 0; i < level; i++)
		levelIndent += "&nbsp;&nbsp;&nbsp;&nbsp;";
	
	DBG.println(levelIndent + " TABGROUP: " + tg.__name);
	levelIndent += "&nbsp;&nbsp;&nbsp;&nbsp;";
	
	var sz = tg.__members.size();
	var a = tg.__members.getArray();
	for (var i = 0; i < sz; i++) {
		if (a[i] instanceof DwtTabGroup) {
			tg.__dump(a[i], level+1);
		} else if (a[i] instanceof DwtControl) {
			DBG.println(levelIndent + "   " + a[i].toString());
		} else {
			DBG.println(levelIndent + "   " + a[i].tagName);
		}
	}
}

/**
 * set's the next/previous focus member.
 * @private
 */
DwtTabGroup.prototype.__setFocusMember =
function(next, checkEnabled, dontNotify) {
	// If there is currently no focus member, then reset to the first member
	// and return
	if (this.__currFocusMember == null)
		return this.resetFocusMember(checkEnabled, dontNotify);
	
	var tabGroup = this.__getTabGroupForMember(this.__currFocusMember);
	var m = (next) ? tabGroup.__getNextMember(this.__currFocusMember, checkEnabled) 
				   : tabGroup.__getPrevMember(this.__currFocusMember, checkEnabled);

	if (m == null) {
		m = (next) ? this.__getLeftMostMember(checkEnabled)
				   : this.__getRightMostMember(checkEnabled);

		// Test for the case where there is only one member in the tabgroup
		if (m == this.__currFocusMember)
			return null;
	}

	this.__currFocusMember = m;
	
	if (!dontNotify)
		this.__notifyListeners(this.__currFocusMember);
	return this.__currFocusMember;
}

/**
 * @private
 */
DwtTabGroup.prototype.__isFocusMember =
function(member) {
	var root = this.__getRootTabGroup();
	
}

/**
 * @private
 */
DwtTabGroup.prototype.__getTabGroupForMember =
function(member) {
	var sz = this.__members.size();
	var a = this.__members.getArray();
	var m;
	for (var i = 0; i < sz; i++) {
		m = a[i]
		if (m == member)
			return this
		else if (m instanceof DwtTabGroup && (m = m.__getTabGroupForMember(member)))
			return m;
	}
	return null;
}


