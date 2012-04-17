/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaDLXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaDLXFormView = function(parent, entry) {
	ZaTabView.call(this, parent, "ZaDLXFormView");
	this.dlStatusChoices = [
		{value:"enabled", label:ZaMsg.DL_Status_enabled}, 
		{value:"disabled", label:ZaMsg.DL_Status_disabled}
	];
	this.TAB_INDEX = 0;
	this.initForm(ZaDistributionList.myXModel,this.getMyXForm(entry));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_CHANGED, new AjxListener(this, ZaDLXFormView.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaDLXFormView.prototype.handleXFormChange));	
}

ZaDLXFormView.prototype = new ZaTabView();
ZaDLXFormView.prototype.constructor = ZaDLXFormView;
ZaTabView.XFormModifiers["ZaDLXFormView"] = new Array();
ZaTabView.ObjectModifiers["ZaDLXFormView"] = [] ;

ZaDLXFormView.prototype.getTitle = 
function () {
	return ZaMsg.DL_view_title;
}

ZaDLXFormView.prototype.getTabIcon =
function () {
	if (this._containedObject && this._containedObject.attrs && this._containedObject.attrs[ZaDistributionList.A_isAdminGroup]=="TRUE" ) {
		return "DistributionListGroup";
	} else {
		return "DistributionList" ;
	}

}

ZaDLXFormView.prototype.handleXFormChange = function (ev) {
	if(ev && this._localXForm.hasErrors()) { 
		ZaApp.getInstance().getCurrentController()._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
        if (appNewUI)
            ZaZimbraAdmin.getInstance().getCurrentAppBar().enableButton(ZaOperation.SAVE, false);

	} else {
		ZaApp.getInstance().getCurrentController()._toolbar.getButton(ZaOperation.SAVE).setEnabled(true);
        if (appNewUI)
            ZaZimbraAdmin.getInstance().getCurrentAppBar().enableButton(ZaOperation.SAVE, true);
	}
}

ZaDLXFormView.membersSelectionListener =    
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_membersSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_membersSelected, null);
	}

    if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaDLXFormView.removeSelectedMembers.call(this, ev);
	}
}

ZaDLXFormView.nonmemberSelectionListener =    
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_nonmembersSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_nonmembersSelected, null);
	}

    if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaAccountMemberOfListView._addSelectedLists(this.getForm(), arr);
	}
}

ZaDLXFormView.memberPoolSelectionListener =    
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_memberPoolSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_memberPoolSelected, null);
	}

    if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaDLXFormView.addSelectedAddressesToMembers.call(this, ev);
	}
}

ZaDLXFormView.directMemberSelectionListener =    
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_directMemberSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_directMemberSelected, null);
	}

    if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaAccountMemberOfListView._removeSelectedLists(this.getForm(), arr);
	}
}

ZaDLXFormView.indirectMemberSelectionListener =    
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_indirectMemberSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_indirectMemberSelected, null);
	}
}


ZaDLXFormView.removeMemberListFromPage = function ( form, memberListToRemove, pageNum ) {
	if ( !form || AjxUtil.isEmpty(memberListToRemove) ) {
		return null;
	}

	var memberHashToRemove = {};
	var i;
	for ( i = 0; i < memberListToRemove.length; i++ ) {
		var toRemove = memberListToRemove[i];
		var name = toRemove[ZaAccount.A_name];
		if ( name ) {
			memberHashToRemove[name] = toRemove;
		}
	}

	if ( !pageNum ){
		pageNum = ( form.getInstanceValue(ZaDistributionList.A2_memPagenum) || 1 );
	}

	var allMemberPages = form.getInstanceValue(ZaDistributionList.A2_allMemberPages) || [];
	if ( allMemberPages == null ) {
		return null;
	}

	--pageNum; //pageNum begins from 0, while A2_memPagenum begins from 1
	if ( allMemberPages[pageNum] == null ) {
		allMemberPages[pageNum] = [];
	}

	var allMembers = form.getInstanceValue(ZaDistributionList.A2_allMemberHash) || {};
	var oriPage = allMemberPages[pageNum];

	//remove the memberListToRemove in members list
	var newPage = [];
	for( i = 0; i < oriPage.length; i++ ){
		var toRemove = oriPage[i];
		var name = toRemove[ZaAccount.A_name];
		if ( memberHashToRemove[name] == null ) { //no found it in remove map
			newPage.push(toRemove);
		} else {
			allMembers[name] = null; //remove it in all members
		}
	}

	allMemberPages[pageNum] = newPage;
	form.setInstanceValue(allMemberPages, ZaDistributionList.A2_allMemberPages);
	form.setInstanceValue(allMembers, ZaDistributionList.A2_allMemberHash);
	return newPage;
}

ZaDLXFormView.removeMembers = function( form, memberListToRemove ) {
	var oriAddList = form.getInstanceValue(ZaDistributionList.A2_addList) || [];
	oriAddList = oriAddList.sort( ZaDistributionList.compareTwoMembers );
	memberListToRemove = memberListToRemove.sort( ZaDistributionList.compareTwoMembers );

	var newRemoveList = [];
	var i = 0, j = 0;
	var newAddList = [];
	//rule out the common members shared with oriAddList and memberListToRemove
	while( i < oriAddList.length && j < memberListToRemove.length ) {
		var toAdd = oriAddList[i];
		var toRemove = memberListToRemove[j];
		var name = toRemove[ZaAccount.A_name];
		var cmp = ZaDistributionList.compareTwoMembers(toAdd, toRemove);
		if ( cmp > 0 ) { // oriAddList[i] > memberListToRemove[j]
			newRemoveList.push( toRemove );
			j++;
		} else if ( cmp < 0 ) {  // oriAddList[i] < memberListToRemove[j]
			newAddList.push( toAdd );
			i++;
		} else { // == 0
			i++;  j++;
		}
	}

	for ( ; i < oriAddList.length; i++ ) {
		newAddList.push( oriAddList[i] );
	}

	if ( newAddList.length < oriAddList.length ) {
		newAddList._version = (oriAddList._version || 0)+ 1;
		//sth has been added then be removed, so it is shared with original oriAddList and memberListToRemove
		form.setInstanceValue( newAddList, ZaDistributionList.A2_addList );
	}

	for ( ; j < memberListToRemove.length; j++ ) {
		newRemoveList.push( memberListToRemove[j] );
	}

	var oriRemoveList = form.getInstanceValue(ZaDistributionList.A2_removeList);
	for( j = 0; j < oriRemoveList.length; j++ ){
		newRemoveList.push(oriRemoveList[j]);
	}
	newRemoveList._version = (oriRemoveList._version || 0)+ 1;
	form.setInstanceValue( newRemoveList, ZaDistributionList.A2_removeList );

	var oriPageMembers = form.getInstanceValue(ZaDistributionList.A2_memberList);
	var newPageMembers = ZaDLXFormView.removeMemberListFromPage(form, memberListToRemove) || [];
	newPageMembers._version = (oriPageMembers._version || 0) + 1;
	form.setInstanceValue( newPageMembers, ZaDistributionList.A2_memberList );

	var numMembers = form.getInstanceValue(ZaDistributionList.A2_numMembers) || 0;
	numMembers -= memberListToRemove.length;
	form.setInstanceValue( numMembers, ZaDistributionList.A2_numMembers )

	form.parent.setDirty(true);
}

//make the common elements of addList and removeList
ZaDLXFormView.removeSelectedMembers = function( event ) {
	var form = this.getForm();
	var selected = form.getInstanceValue(ZaDistributionList.A2_membersSelected);
	ZaDLXFormView.removeMembers(form, selected);
}

ZaDLXFormView.removeOnePageMembers = function(event) {
	var form = this.getForm();
	var curPageMembers = form.getInstanceValue(ZaDistributionList.A2_memberList);
	ZaDLXFormView.removeMembers(form, curPageMembers);
}


/**
* method of an XFormItem
**/
ZaDLXFormView.srchButtonHndlr = 
function(evt) {
	this.setInstanceValue([], ZaDistributionList.A2_memberPool);
	this.setInstanceValue(1, ZaDistributionList.A2_poolPagenum);
	this.setInstanceValue(1, ZaDistributionList.A2_poolNumPages);
	this.setInstanceValue(-1, ZaDistributionList.A2_totalNumInPool);
	this.setInstanceValue(null, ZaDistributionList.A2_memberPoolSelected);

	var fieldObj = this.getForm().parent;
	ZaDLXFormView.prototype.searchAccounts.call(fieldObj, null, true);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.fwdPoolButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	var currentPageNum = this.getInstanceValue(ZaDistributionList.A2_poolPagenum);
	this.setInstanceValue(currentPageNum+1, ZaDistributionList.A2_poolPagenum);
	ZaDLXFormView.prototype.searchAccounts.call(fieldObj, null, true);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.backPoolButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	var currentPageNum = this.getInstanceValue(ZaDistributionList.A2_poolPagenum)-1;
	this.setInstanceValue(currentPageNum, ZaDistributionList.A2_poolPagenum);
	ZaDLXFormView.prototype.searchAccounts.call(fieldObj, null, true);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.fwdMemButtonHndlr = 
function(evt) {
	var currentPageNum = this.getInstanceValue(ZaDistributionList.A2_memPagenum)+1;
	this.setInstanceValue(currentPageNum,ZaDistributionList.A2_memPagenum);
	var currentObj = ZaApp.getInstance().getCurrentController()._currentObject ;

	if ( currentObj && currentObj[ZaDistributionList.A2_allMemberPages] ) {
		currentObj[ZaDistributionList.A2_memPagenum] = currentPageNum ;

		//ZaDistributionList.A2_allMemberPages begins from 0, but currentPageNum begins from 1
		var memberList = currentObj[ZaDistributionList.A2_allMemberPages][currentPageNum-1] || [];
		this.setInstanceValue(memberList, ZaDistributionList.A2_memberList);
	}
}

/**                                                                 
* method of an XFormItem
**/
ZaDLXFormView.backMemButtonHndlr = 
function(evt) {
	var currentPageNum = this.getInstanceValue(ZaDistributionList.A2_memPagenum)-1;
	this.setInstanceValue(currentPageNum,ZaDistributionList.A2_memPagenum);
	var currentObj = ZaApp.getInstance().getCurrentController()._currentObject ;

	if ( currentObj && currentObj[ZaDistributionList.A2_allMemberPages] ) {
		currentObj[ZaDistributionList.A2_memPagenum] = currentPageNum ;

		//ZaDistributionList.A2_allMemberPages begins from 0, but currentPageNum begins from 1
		var memberList = currentObj[ZaDistributionList.A2_allMemberPages][currentPageNum-1] || [];
		this.setInstanceValue(memberList, ZaDistributionList.A2_memberList);
	}
}
/**
* method of the XForm
**/
ZaDLXFormView.getMemberSelection = 
function () {
	var memberItem = this.getItemsById(ZaDistributionList.A2_members )[0];
	var membersSelection = null;
	if(memberItem) {
		var membersSelection = memberItem.getSelection();
	}	
		
	if(membersSelection) {
		return membersSelection;
	} else {
		return [];
	}	
}
/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableMemberListButtons = function() {
	return (ZaDLXFormView.getMemberSelection.call(this).length>0);
};

/**                                                                  
* method of the XForm
**/
ZaDLXFormView.shouldEnableRemoveThisPageButton = function() {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaDistributionList.A2_memberList)));
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableAddAllButton = function() {
	var list = this.getItemsById(ZaDistributionList.A2_memberPool)[0].widget.getList();
	if (list != null) {
		return ( list.size() > 0);
	}
	return false;
};

/**
* method of the XForm
**/
ZaDLXFormView.getMemberPoolSelection = 
function () {
	var memberItem = this.getItemsById(ZaDistributionList.A2_memberPool)[0];
	var membersSelection = null;
	if(memberItem) {
		var membersSelection = memberItem.getSelection();
	}	
		
	if(membersSelection) {
		return membersSelection;
	} else {
		return [];
	}	
}

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableMemberPoolListButtons = function() {
	return (ZaDLXFormView.getMemberPoolSelection.call(this).length>0);
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableFreeFormButtons = function () {
	var optionalAdd = this.getInstance().optionalAdd;
	return (optionalAdd && optionalAdd.length > 0);
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnablePoolForwardButton = function () {
	return (this.getInstanceValue(ZaDistributionList.A2_poolPagenum) < this.getInstanceValue(ZaDistributionList.A2_poolNumPages));
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnablePoolBackButton = function () {
	return (this.getInstanceValue(ZaDistributionList.A2_poolPagenum) > 1);
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableMemForwardButton = function () {
	return (this.getInstanceValue(ZaDistributionList.A2_memPagenum) < this.getInstanceValue(ZaDistributionList.A2_memNumPages));
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableMemBackButton = function () {
	return (this.getInstanceValue(ZaDistributionList.A2_memPagenum) > 1);
};

ZaDLXFormView.addMemberListToPage = function ( form, memberListToAdd, pageNum ) {
	if ( !form || AjxUtil.isEmpty(memberListToAdd) ) {
		return null;
	}

	if ( !pageNum ){
		pageNum = ( form.getInstanceValue(ZaDistributionList.A2_memPagenum) || 1 );
	}

	var allMemberPages = form.getInstanceValue(ZaDistributionList.A2_allMemberPages) || [];
	if ( allMemberPages == null ) {
		return null;
	}

	--pageNum; //pageNum begins from 0, while A2_memPagenum begins from 1
	if ( allMemberPages[pageNum] == null ) {
		allMemberPages[pageNum] = [];
	}

	var allMembers = form.getInstanceValue(ZaDistributionList.A2_allMemberHash) || {};

	var page = allMemberPages[pageNum];
	for ( var i = 0; i < memberListToAdd.length; i++ ) {
		var member = memberListToAdd[i];
		if ( !member || !member[ZaAccount.A_name] ) {
			continue;
		}
		var name = member[ZaAccount.A_name];
		if ( name && allMembers[name] == null ) { // has not been added
			page.push(member);
			allMembers[name] = member;
		} 
	}


	form.setInstanceValue(allMemberPages, ZaDistributionList.A2_allMemberPages);
	form.setInstanceValue(allMembers, ZaDistributionList.A2_allMemberHash);
	return page;
}

ZaDLXFormView.excludeAddressesInAllMembers = function ( form, addressList ) {
	if ( !form || AjxUtil.isEmpty(addressList) ) {
		return null;
	}

	var nonMembers = [];
	var members = [];

	var allMembers = form.getInstanceValue(ZaDistributionList.A2_allMemberHash) || {};
	for ( var i = 0; i < addressList.length; i++ ) {
		var member = addressList[i];
		if ( !member || !member[ZaAccount.A_name] ) {
			continue;
		}
		var name = member[ZaAccount.A_name];
		if ( name && allMembers[name] == null ) {
			nonMembers.push(member);
		} else {  //already been added
			members.push(name);
		}
	}

	return [members, nonMembers];
}



/**
 * method of an XFormItem
 * Currently, this manages the data, redraws the whole list, and then sets 
 * the selection. 
 */
ZaDLXFormView.addAddressesToMembers = function (form, newAddresses) {
	var list = ZaDLXFormView.excludeAddressesInAllMembers(form, newAddresses);
	var existedMembers = list[0], memberListToAdd = list[1];
	if (existedMembers.length > 0) { 
		var existed = existedMembers.join("<br>");
		var msg = AjxMessageFormat.format( ZaMsg.DLXV_MsgMemberExisted, [existed]);
		if (memberListToAdd.length > 0) {
			var add = memberListToAdd.join("<br>");
			msg += ("<br><br>");
			msg += AjxMessageFormat.format( ZaMsg.DLXV_MsgMemberCanBeAdded, [add] );
		}

		ZaApp.getInstance().getCurrentController().popupMsgDialog(msg);
	}
	if ( memberListToAdd.length <= 0 ) {
		return;
	}

	var oriPageMembers = form.getInstanceValue(ZaDistributionList.A2_memberList);
	var newPageMembers = ZaDLXFormView.addMemberListToPage(form, memberListToAdd );
	if ( !newPageMembers || !newPageMembers.length ) {
		return;
	}
	newPageMembers._version = (oriPageMembers._version || 0)+ 1;
	form.setInstanceValue(newPageMembers, ZaDistributionList.A2_memberList);

	//filter out the common members shared by oriRemoveList and memberListToAdd
	var oriRemoveList = form.getInstanceValue(ZaDistributionList.A2_removeList) || [];
	oriRemoveList = oriRemoveList.sort( ZaDistributionList.compareTwoMembers );
	memberListToAdd = memberListToAdd.sort( ZaDistributionList.compareTwoMembers );

	var newAddList = [], newRemoveList = [];
	var i = 0, j = 0;
	//rule out the common members shared with oriRemoveList and memberListToAdd
	while( i < oriRemoveList.length && j < memberListToAdd.length ) {
		var toRemove = oriRemoveList[i];
		var toAdd = memberListToAdd[j];
		var name = toAdd[ZaAccount.A_name];
		var cmp = ZaDistributionList.compareTwoMembers(toRemove, toAdd);
		if ( cmp > 0 ) { // oriRemoveList[i] > memberListToAdd[j]
			newAddList.push( toAdd );
			j++;
		} else if ( cmp < 0 ) {  // oriRemoveList[i] < memberListToAdd[j]
			newRemoveList.push( toRemove );
			i++;
		} else { // == 0
			i++;  j++;
		}
	}

	// add the rest 'to remove members'
	for ( ; i < oriRemoveList.length; i++ ) {
		newRemoveList.push( oriRemoveList[i] );
	}

	if ( newRemoveList.length < oriRemoveList.length ) {
		newRemoveList._version = (oriRemoveList._version || 0)+ 1;
		//sth has been removed then be add, so it is shared with original oriRemoveList and memberListToAdd
		form.setInstanceValue( newRemoveList, ZaDistributionList.A2_removeList );
	}

	// add the rest 'to add members'
	for ( ; j < memberListToAdd.length; j++ ) {
		newAddList.push( memberListToAdd[j] );
	}

	var oriAddList = form.getInstanceValue(ZaDistributionList.A2_addList) || [];
	for( j = 0; j < oriAddList.length; j++ ){
		newAddList.push(oriAddList[j]);
	}
	newAddList._version = (oriAddList._version || 0) + 1;
	form.setInstanceValue(newAddList, ZaDistributionList.A2_addList);
	//finally the newAddList should not include the common shared by oriRemoveList and the original members from the last saving


	var numMembers = form.getInstanceValue(ZaDistributionList.A2_numMembers) || 0;
	numMembers += (memberListToAdd.length);
	form.setInstanceValue(numMembers, ZaDistributionList.A2_numMembers);

	form.parent.setDirty(true);
};

/**
 * method of an XFormItem
**/
ZaDLXFormView.addSelectedAddressesToMembers = function (event) {
	var form = this.getForm();
	var selectedAddresses = form.getInstanceValue(ZaDistributionList.A2_memberPoolSelected);
	if(AjxUtil.isEmpty(selectedAddresses)) {
		return;
	}
	ZaDLXFormView.addAddressesToMembers(form, selectedAddresses);
};


/**
 * method of an XFormItem
**/
ZaDLXFormView.addOnePageAddressesToMembers = function (event) {
	var form = this.getForm();
	var onePageAddresses = form.getInstanceValue(ZaDistributionList.A2_memberPool);	//has been removed self and self member
	if(AjxUtil.isEmpty(onePageAddresses)) {
		return;
	}
	ZaDLXFormView.addAddressesToMembers(form, onePageAddresses);
};


/**
 * method of an XFormItem
**/
ZaDLXFormView.addFreeFormAddressToMembers = function (event) {
	var form = this.getForm();
	// get the current value of the textfied
	var freeFormAddresses = form.getInstanceValue(ZaDistributionList.A2_optionalAdd);
	if(AjxUtil.isEmpty(freeFormAddresses)) {
		return;
	}

	var freeFormAddresses = freeFormAddresses.split(/[\r\n,;]+/);
	var goodAddresses = [];
	var stdEmailRegEx = /([^\<\;]*)\<([^\>]+)\>/ ;
	for (var i = 0; i < freeFormAddresses.length; i++) {
		var address = AjxStringUtil.trim(freeFormAddresses[i],true);
		if (address) {
			var result = stdEmailRegEx.exec(address);
			if (result != null) {
				address = result[2];
			}
			if(!AjxEmailAddress.isValid(address)) {
				//handle invalid email address
				var msg = AjxMessageFormat.format(ZaMsg.WARNING_DL_INVALID_EMAIL, [address]);
				ZaApp.getInstance().getCurrentController().popupErrorDialog(msg,null,DwtMessageDialog.WARNING_STYLE);
				return;
			}
			goodAddresses.push(new ZaDistributionListMember(address));
		}
	}

	ZaDLXFormView.addAddressesToMembers(form, goodAddresses);
	this.setInstanceValue("", ZaDistributionList.A2_optionalAdd);
};

ZaDLXFormView._copyAttrFromEntry =
function ( xform, containedObject, entry ) {
	if ( !xform || !containedObject || !entry) {
		return;
	}
	for (var a in entry.attrs) {
		var modelItem = xform.getModel().getItem(a) ;
		var attr = entry.attrs[a];
		if ((modelItem != null && modelItem.type == _LIST_)
		   || ( attr != null && attr instanceof Array))
		{  //need deep clone
			containedObject.attrs[a] = ZaItem.deepCloneListItem (attr);
		} else {
			containedObject.attrs[a] = attr;
		}
	}

	if(entry.setAttrs) {
		containedObject.setAttrs = entry.setAttrs;
	}

	if(entry.getAttrs) {
		containedObject.getAttrs = entry.getAttrs;
	}

	if(entry.rights) {
		containedObject.rights = entry.rights;
	}

	if(entry._defaultValues) {
		containedObject._defaultValues = entry._defaultValues;
	}

}


ZaDLXFormView.prototype.setObject = 
function (entry) {
    this._containedObject = {attrs:{}};

    ZaDLXFormView._copyAttrFromEntry( this._localXForm, this._containedObject, entry );

    //Utility members
	this._containedObject[ZaDistributionList.A2_addList] = new Array(); //members to add
	this._containedObject[ZaDistributionList.A2_addList]._version = 1;
	this._containedObject[ZaDistributionList.A2_removeList] = new Array(); //members to remove
	this._containedObject[ZaDistributionList.A2_removeList]._version = 1;

	this._containedObject[ZaDistributionList.A2_memberPool] = entry [ZaDistributionList.A2_memberPool] || [];
	this._containedObject[ZaDistributionList.A2_memberPool]._version = 1;
	this._containedObject[ZaDistributionList.A2_poolPagenum] = this._containedObject[ZaDistributionList.A2_poolPagenum] || 1;
	this._containedObject[ZaDistributionList.A2_poolNumPages] = entry [ZaDistributionList.A2_poolNumPages] || 1;
	this._containedObject[ZaDistributionList.A2_totalNumInPool] = entry [ZaDistributionList.A2_totalNumInPool] || -1; //-1 means hasn't start searching yet 

	this._containedObject[ZaDistributionList.A2_query] = "";

	/* those followings will be evaluated in ZaDLXFormView.prototype.updateMemberList()

		this._containedObject[ZaDistributionList.A2_memberList],
		this._containedObject[ZaDistributionList.A2_memPagenum],
		this._containedObject[ZaDistributionList.A2_memNumPages],
		this._containedObject[ZaDistributionList.A2_numMembers],
		this._containedObject[ZaDistributionList.A2_allMemberHash],
		this._containedObject[ZaDistributionList.A2_allMemberPages]

		//membership related instance variables
		this._containedObject[ZaAccount.A2_memberOf] = ZaAccountMemberOfListView.cloneMemberOf(entry);
		// the origList is inited when we load the object, it won't be modified unless the first time
		// So there is no need for me to do deep clone
		this._containedObject[ZaDistributionList.A2_origList] = entry [ZaDistributionList.A2_origList];
		this._containedObject[ZaAccount.A2_directMemberList + "_more"] = entry[ZaAccount.A2_directMemberList + "_more"];
		this._containedObject[ZaAccount.A2_directMemberList + "_offset"] = entry[ZaAccount.A2_directMemberList + "_offset"];
		this._containedObject[ZaAccount.A2_indirectMemberList + "_more"] = entry[ZaAccount.A2_indirectMemberList + "_more"];
		this._containedObject[ZaAccount.A2_indirectMemberList + "_offset"] = entry[ZaAccount.A2_indirectMemberList + "_offset"];	
		this._containedObject[ZaAccount.A2_nonMemberList + "_more"] = entry[ZaAccount.A2_nonMemberList + "_more"];
		this._containedObject[ZaAccount.A2_nonMemberList + "_offset"] = entry[ZaAccount.A2_nonMemberList + "_offset"];

		this._containedObject[ZaDistributionList.A2_DLOwners] = new Array();
		if (entry[ZaDistributionList.A2_DLOwners])
			this._containedObject[ZaDistributionList.A2_DLOwners] = ZaItem.deepCloneListItem(entry[ZaDistributionList.A2_DLOwners]);

		if(entry[ZaDistributionList.A2_dlType])
			this._containedObject[ZaDistributionList.A2_dlType] = entry[ZaDistributionList.A2_dlType];

	*/


	//dl.isgroup = this.isgroup ;

	this._containedObject.name = entry.name;
        if(entry.name == ""){this._containedObject.name = ZaMsg.TBB_New;}
	this._containedObject.type = entry.type;
	this._containedObject.id = entry.id;

	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];

	if(!entry.id) {
		if(ZaItem.hasWritePermission(ZaAccount.A_zimbraIsDelegatedAdminAccount,entry)) {
			this._containedObject.attrs[ZaDistributionList.A_mailStatus] = "enabled";
		}
	}
        this.modifyContainedObject () ;
	this._localXForm.setInstance(this._containedObject);	
	if(!appNewUI)
	    this.updateTab();
}

ZaDLXFormView.prototype.srchResWithoutSelf =
function(resList, selfName) {
	var resArr = new Array();
	var tmpArr = resList.getArray();
	for(var i = 0; i < tmpArr.length; i++) {
		if(tmpArr[i].type == ZaItem.DL && tmpArr[i] == selfName) {
			continue;
		}else if(tmpArr[i].type == ZaItem.ALIAS && tmpArr[i].getAliasTargetObj() == selfName) {
			continue;
		}else resArr.push(tmpArr[i]);
	}
	return resArr;
}

ZaDLXFormView.prototype.searchAccounts = 
function (orderBy, isAscending) {
    var limit = ZaDistributionList.MEMBER_POOL_PAGE_SIZE;
    var startPageNum = this._containedObject[ZaDistributionList.A2_poolPagenum];
    var result = ZaDLXFormView.getMatchedAccounts.call( this, orderBy, isAscending, startPageNum, limit );

    this._containedObject[ZaDistributionList.A2_memberPool] = result.list;
    this._containedObject[ZaDistributionList.A2_poolNumPages] = result.numPages;
    this._containedObject[ZaDistributionList.A2_totalNumInPool] = result.searchTotal || 0;
    this._localXForm.setInstance(this._containedObject);
}

ZaDLXFormView.prototype.getAllMatchedAccounts =
function () {
    var limit = 0; //0 means unlimited
    var startPageNum = 1; //from the beginning
    var result = ZaDLXFormView.getMatchedAccounts.call(this, ZaAccount.A_name, true, startPageNum, limit );

    return result.list;
}

ZaDLXFormView.getMatchedAccounts = 
function( orderBy, isAscending, startPageNum, limit ) {
    orderBy = (orderBy !=null) ? orderBy : ZaAccount.A_name;
    isAscending = !!isAscending;
    limit = (limit >= 0) ? limit : ZaDistributionList.MEMBER_POOL_PAGE_SIZE;
    startPageNum = (startPageNum >= 0) ? startPageNum : this._containedObject[ZaDistributionList.A2_poolPagenum];

    var result = {};
    try {
        var types = [ZaSearch.ACCOUNTS, ZaSearch.DLS, ZaSearch.ALIASES];
        var queryString = ZaSearch.getSearchByNameQuery(this._containedObject[ZaDistributionList.A2_query], types, true);
        var myName = this._containedObject[ZaAccount.A_name];
        queryString = ZaDLXFormView.makeQueryStringWithoutSelf(queryString, myName);
         //remove dl or alias point to self, and self members
        var searchQueryHolder = new ZaSearchQuery(queryString, types , false, "", null, limit);
        result = ZaSearch.searchByQueryHolder(searchQueryHolder, startPageNum, orderBy, isAscending);

        if(result.list) {
            result.list = result.list.getArray();
            //make it as a list rather than ZaItemList
        } else {
            result.list = [];
            result.numPages = 0;
            result.searchTotal = 0;
        }

    } catch (ex) {
        // Only restart on error if we are not initialized and it isn't a parse error
        if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
//			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDLXFormView.prototype.searchAccounts", null, (this._inited) ? false : true);
            ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDLXFormView.prototype.searchAccounts", null, false );
        } else {
            this.popupErrorDialog(ZaMsg.queryParseError, ex);
            this._searchField.setEnabled(true);    
        }
    }
    return result;
}

ZaDLXFormView._makeAddressExcluded =
function ( address ) {
	if ( AjxUtil.isEmpty(address) ) {
		return "";
	}

	return (	"(!(mail=" + address + "))" +
		"(!(zimbraMailAddress=" + address + "))" +
		"(!(zimbraMailDeliveryAddress=" + address + "))" +
		"(!(zimbraMailForwardingAddress=" + address + "))" +
		"(!(zimbraMemberOf=" + address + "))"
	);
}

ZaDLXFormView.makeQueryStringWithoutSelf =
function (rawQueryString, myName) {
	if (rawQueryString == null) {
		rawQueryString = "";
	}

	if (!myName || !myName.length) {
		return rawQueryString;
	}

	var excludingQueryString = ZaDLXFormView._makeAddressExcluded(myName);
	//originally we want to not only exclude its name but also its members, but we find when we send the query
	//without its mebers, the server side becomes very slow when it has many members (>500), so keep it as this

	excludingQueryString = excludingQueryString ?
		"(&" + rawQueryString + "(&" + excludingQueryString + "))" :
		rawQueryString;

	return excludingQueryString;
}

ZaDLXFormView.aliasSelectionListener = 
function (ev) {
	var arr = this.widget.getSelection();	
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_alias_selection_cache, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_alias_selection_cache, null);
	}		
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaDLXFormView.editAliasButtonListener.call(this);
	}	
}

ZaDLXFormView.deleteAliasButtonListener = function () {
	var instance = this.getInstance();
	if(instance[ZaDistributionList.A2_alias_selection_cache] != null) {
		var cnt = instance[ZaDistributionList.A2_alias_selection_cache].length;
		if(cnt && instance.attrs[ZaAccount.A_zimbraMailAlias]) {
			var aliasArr = instance.attrs[ZaAccount.A_zimbraMailAlias];
			for(var i=0;i<cnt;i++) {
				var cnt2 = aliasArr.length-1;				
				for(var k=cnt2;k>=0;k--) {
					if(aliasArr[k]==instance[ZaDistributionList.A2_alias_selection_cache][i]) {
						aliasArr.splice(k,1);
						break;	
					}
				}
			}
			this.getModel().setInstanceValue(instance, ZaAccount.A_zimbraMailAlias, aliasArr);	
		}
	}
	this.getModel().setInstanceValue(instance, ZaDistributionList.A2_alias_selection_cache, []);
	this.getForm().parent.setDirty(true);
}

ZaDLXFormView.editAliasButtonListener =
function () {
	var instance = this.getInstance();
	if(instance.alias_selection_cache && instance.alias_selection_cache[0]) {	
		var formPage = this.getForm().parent;
		if(!formPage.editAliasDlg) {
			formPage.editAliasDlg = new ZaEditAliasXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px",ZaMsg.Edit_Alias_Title);
			formPage.editAliasDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDLXFormView.updateAlias, this.getForm(), null);						
		}
		var obj = {};
		obj[ZaAccount.A_name] = instance[ZaDistributionList.A2_alias_selection_cache][0];
		var cnt = instance.attrs[ZaAccount.A_zimbraMailAlias].length;
		for(var i=0;i<cnt;i++) {
			if(instance[ZaDistributionList.A2_alias_selection_cache][0]==instance.attrs[ZaAccount.A_zimbraMailAlias][i]) {
				obj[ZaAlias.A_index] = i;
				break;		
			}
		}
		
		formPage.editAliasDlg.setObject(obj);
		formPage.editAliasDlg.popup();		
	}
}

ZaDLXFormView.updateAlias = function () {
	if(this.parent.editAliasDlg) {
		this.parent.editAliasDlg.popdown();
		var obj = this.parent.editAliasDlg.getObject();
		var instance = this.getInstance();
		var arr = instance.attrs[ZaAccount.A_zimbraMailAlias];
		if(obj[ZaAlias.A_index] >=0 && arr[obj[ZaAlias.A_index]] != obj[ZaAccount.A_name] ) {			
			arr[obj[ZaAlias.A_index]] = obj[ZaAccount.A_name];
			this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A_zimbraMailAlias, arr); 
			this.getModel().setInstanceValue(this.getInstance(),ZaDistributionList.A2_alias_selection_cache, new Array());
			this.parent.setDirty(true);	
		}
	}
}

ZaDLXFormView.addAliasButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addAliasDlg) {
		formPage.addAliasDlg = new ZaEditAliasXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px",ZaMsg.Add_Alias_Title);
		formPage.addAliasDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDLXFormView.addAlias, this.getForm(), null);						
	}
	
	var obj = {};
	obj[ZaAccount.A_name] = "";
	obj[ZaAlias.A_index] = - 1;
	formPage.addAliasDlg.setObject(obj);
	formPage.addAliasDlg.popup();		
}

ZaDLXFormView.addAlias  = function () {
	if(this.parent.addAliasDlg) {
		this.parent.addAliasDlg.popdown();
		var obj = this.parent.addAliasDlg.getObject();
		if(obj[ZaAccount.A_name] && obj[ZaAccount.A_name].length>1) {
			var instance = this.getInstance();
			var arr = instance.attrs[ZaAccount.A_zimbraMailAlias]; 
			arr.push(obj[ZaAccount.A_name]);
			this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A_zimbraMailAlias, arr);
			this.getModel().setInstanceValue(this.getInstance(),ZaDistributionList.A2_alias_selection_cache, new Array());
			this.parent.setDirty(true);
		}
	}
}

ZaDLXFormView.isEditAliasEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaDistributionList.A2_alias_selection_cache)) && this.getInstanceValue(ZaAccount.A2_alias_selection_cache).length==1);
}

ZaDLXFormView.isDeleteAliasEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaDistributionList.A2_alias_selection_cache)));
}


ZaDLXFormView.ownerSelectionListener =
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_owners_selection_cache, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_owners_selection_cache, null);
	}
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaDLXFormView.editOwnerButtonListener.call(this);
	}
}

ZaDLXFormView.deleteOwnerButtonListener = function () {
	var instance = this.getInstance();
	if(instance[ZaDistributionList.A2_owners_selection_cache] != null) {
		var cnt = instance[ZaDistributionList.A2_owners_selection_cache].length;
		if(cnt && instance[ZaDistributionList.A2_DLOwners]) {
			var aliasArr = instance[ZaDistributionList.A2_DLOwners];
			for(var i=0;i<cnt;i++) {
				var cnt2 = aliasArr.length-1;
				for(var k=cnt2;k>=0;k--) {
					if(aliasArr[k]==instance[ZaDistributionList.A2_owners_selection_cache][i]) {
						aliasArr.splice(k,1);
						break;
					}
				}
			}
			this.getModel().setInstanceValue(instance, ZaDistributionList.A2_DLOwners, aliasArr);
		}
	}
	this.getModel().setInstanceValue(instance, ZaDistributionList.A2_owners_selection_cache, []);
	this.getForm().parent.setDirty(true);
}

ZaDLXFormView.editOwnerButtonListener =
function () {
	var instance = this.getInstance();
	if(instance[ZaDistributionList.A2_owners_selection_cache] && instance[ZaDistributionList.A2_owners_selection_cache][0]) {
		var formPage = this.getForm().parent;
		if(!formPage.editOwnerDlg) {
			formPage.editOwnerDlg = new ZaEditAliasXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "450px", "150px", ZaMsg.Edit_Owner_Title);
			formPage.editOwnerDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDLXFormView.updateOwner, this.getForm(), null);
		}
		var obj = {};
		obj[ZaAccount.A_name] = instance[ZaDistributionList.A2_owners_selection_cache][0];
		var cnt = instance[ZaDistributionList.A2_DLOwners].length;
		for(var i=0;i<cnt;i++) {
			if(instance[ZaDistributionList.A2_owners_selection_cache][0]==instance[ZaDistributionList.A2_DLOwners][i]) {
				obj[ZaAlias.A_index] = i;
				break;
			}
		}

		formPage.editOwnerDlg.setObject(obj);
		formPage.editOwnerDlg.popup();
	}
}

ZaDLXFormView.updateOwner = function () {
	if(this.parent.editOwnerDlg) {
		this.parent.editOwnerDlg.popdown();
		var obj = this.parent.editOwnerDlg.getObject();
		var instance = this.getInstance();
		var arr = instance[ZaDistributionList.A2_DLOwners];
		if(obj[ZaAlias.A_index] >=0 && arr[obj[ZaAlias.A_index]] != obj[ZaAccount.A_name] ) {
            if(!ZaDLXFormView.checkOwner(obj[ZaAccount.A_name]))
                return;
			arr[obj[ZaAlias.A_index]] = obj[ZaAccount.A_name];
			this.getModel().setInstanceValue(this.getInstance(),ZaDistributionList.A2_DLOwners, arr);
			this.getModel().setInstanceValue(this.getInstance(),ZaDistributionList.A2_owners_selection_cache, new Array());
			this.parent.setDirty(true);
		}
	}
}

ZaDLXFormView.addOwnerButtonListener =
function () {
	var formPage = this.getForm().parent;
	if(!formPage.addOwnerDlg) {
		formPage.addOwnerDlg = new ZaEditAliasXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "450px", "150px", ZaMsg.Add_Owner_Title);
		formPage.addOwnerDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDLXFormView.addOwner, this.getForm(), null);
	}

	var obj = {};
	obj[ZaAccount.A_name] = "";
	obj[ZaAlias.A_index] = - 1;
	formPage.addOwnerDlg.setObject(obj);
	formPage.addOwnerDlg.popup();
}

ZaDLXFormView.addOwner  = function () {
	if(this.parent.addOwnerDlg) {
		this.parent.addOwnerDlg.popdown();
		var obj = this.parent.addOwnerDlg.getObject();
		if(obj[ZaAccount.A_name] && obj[ZaAccount.A_name].length>1) {
            if(!ZaDLXFormView.checkOwner(obj[ZaAccount.A_name]))
                return;
			var instance = this.getInstance();
			var arr = instance[ZaDistributionList.A2_DLOwners];
			arr.push(obj[ZaAccount.A_name]);
			this.getModel().setInstanceValue(this.getInstance(),ZaDistributionList.A2_DLOwners, arr);
			this.getModel().setInstanceValue(this.getInstance(),ZaDistributionList.A2_owners_selection_cache, new Array());
			this.parent.setDirty(true);
		}
	}
}

ZaDLXFormView.checkOwner = function (accountName) {
    var ret = false;
    try {
        ret = ZaSearch.isAccountExist({name: accountName, popupError: false});
    } catch (ex) {

    }
    if (!ret)
        ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_NO_SUCH_ACCOUNT);

    return ret;

}

ZaDLXFormView.isEditOwnerEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaDistributionList.A2_owners_selection_cache)) && this.getInstanceValue(ZaDistributionList.A2_owners_selection_cache).length==1);
}

ZaDLXFormView.isDeleteOwnerEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaDistributionList.A2_owners_selection_cache)));
}

ZaDLXFormView.isDynamicDL = function () {
    return this.getInstanceValue(ZaDistributionList.A2_dlType) === ZaDistributionList.DYNAMIC_DL_TYPE;
}

ZaDLXFormView.isNotDynamicDL = function () {
    return !ZaDLXFormView.isDynamicDL.call(this);
}

ZaDLXFormView.isNotACLGroup = function () {
    return this.getInstanceValue(ZaDistributionList.A_zimbraIsACLGroup ) === "FALSE";
}

ZaDLXFormView.NOTES_TAB_ATTRS = [ZaAccount.A_notes];
ZaDLXFormView.NOTES_TAB_RIGHTS = [];

ZaDLXFormView.MEMBEROF_TAB_ATTRS = [];
ZaDLXFormView.MEMBEROF_TAB_RIGHTS = [ZaDistributionList.GET_DL_MEMBERSHIP_RIGHT];

ZaDLXFormView.ALIASES_TAB_ATTRS = [ZaAccount.A_zimbraMailAlias];
ZaDLXFormView.ALIASES_TAB_RIGHTS = [ZaDistributionList.ADD_DL_ALIAS_RIGHT,ZaDistributionList.REMOVE_DL_ALIAS_RIGHT];

ZaDLXFormView.OWNER_TAB_ATTRS = [];
ZaDLXFormView.OWNER_TAB_RIGHTS = [];

ZaDLXFormView.PREF_TAB_ATTRS = [ZaDistributionList.A_zimbraPrefReplyToEnabled, ZaDistributionList.A_zimbraPrefReplyToDisplay,
    ZaDistributionList.A_zimbraPrefReplyToAddress];
ZaDLXFormView.PREF_TAB_RIGHTS = [];

ZaDLXFormView.myXFormModifier = function(xFormObject, entry) {	
	var sourceHeaderList = new Array();
	var sortable=1;
	sourceHeaderList[0] = new ZaListHeaderItem("type", ZaMsg.ALV_Type_col, null, "34px", sortable++, "objectClass", true, true);
	sourceHeaderList[1] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, "200px", sortable++, ZaAccount.A_name, true, true);
	//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	sourceHeaderList[2] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, null, sortable++,ZaAccount.A_displayname, true, true);
	//sourceHeaderList[3] = new ZaListHeaderItem(null, null, null, "10px", null, null, false, true);
	var membersHeaderList = new Array();
	membersHeaderList[0] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, "100%", sortable++, ZaAccount.A_name, true, true);

	var directMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.DIRECT);
	var indirectMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.INDIRECT);
	var nonMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.NON);
    
    this.tabChoices = new Array();
	
	var _tab1, _tab2, _tab3, _tab4, _tab5, _tab6;
	_tab1 = ++this.TAB_INDEX;
	this.tabChoices.push({value:_tab1, label:ZaMsg.DLXV_TabMembers});

	if(ZaTabView.isTAB_ENABLED(entry,ZaDLXFormView.NOTES_TAB_ATTRS, ZaDLXFormView.NOTES_TAB_RIGHTS)) {
		_tab2 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab2, label:ZaMsg.DLXV_TabNotes});	
	}

	if(ZaTabView.isTAB_ENABLED(entry,ZaDLXFormView.MEMBEROF_TAB_ATTRS, ZaDLXFormView.MEMBEROF_TAB_RIGHTS)) {
		_tab3 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab3, label:ZaMsg.TABT_MemberOf});	
	}

	if(ZaTabView.isTAB_ENABLED(entry,ZaDLXFormView.ALIASES_TAB_ATTRS, ZaDLXFormView.ALIASES_TAB_RIGHTS)) {
		_tab4 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab4, label:ZaMsg.TABT_Aliases});	
	}

    if(ZaTabView.isTAB_ENABLED(entry,ZaDLXFormView.OWNER_TAB_ATTRS, ZaDLXFormView.OWNER_TAB_RIGHTS)) {
        _tab5 = ++this.TAB_INDEX;
        this.tabChoices.push({value:_tab5, label:ZaMsg.TABT_Owners});
    }

	if(ZaTabView.isTAB_ENABLED(entry,ZaDLXFormView.PREF_TAB_ATTRS, ZaDLXFormView.PREF_TAB_RIGHTS)) {
		_tab6 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab6, label:ZaMsg.TABT_Preferences});
	}

	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	xFormObject.numCols=5;
	xFormObject.colSizes = [10,"auto", 20, "auto", 10];
	xFormObject.itemDefaults = {
			_INPUT_: { cssClass:"inputBorder" },
			_TEXTAREA_: {cssClass: "inputBorder"},
			_TEXTFIELD_: {cssClass: "inputBorder", containerCssStyle:"width:100%"}
	    };
	    
	var cases = [];

	var hMsgMemberNum, hMemberList, wMemberList, hMemberPool, wMemberPool, wRightPanel;
	if (AjxEnv.isWebKitBased || AjxEnv.isFirefox ) {
		hMsgMemberNum = 30;
		hMemberList = 310;
		wMemberList = "99%";
		hMemberPool = 342;
		wMemberPool = "99%";
	} else /* if (AjxEnv.isIE || others... ) */ {
		hMsgMemberNum = 25;
		hMemberList = 308;
		wMemberList = "100%";
		hMemberPool = 338;
		wMemberPool = "100%";
	}
	wRightPanel = (appNewUI) ? "100%" : "98%";
	var case1 =
	{
		type : _ZATABCASE_, caseKey : _tab1, numCols : 2, id : "dl_form_members",
		colSizes : ["440px", "440px"], cssStyle : "table-layout:fixed; padding-top:5px;",
		items : [
		{
			type : _GROUP_, width : "98%", numCols : 1, items : [
			{
				type : _SPACER_, height : "5"
			}
			,
			{
				type : _GROUP_, width : "98%", id : "dl_form_members_general_group", numCols : 2, colSizes : [130, "*"], items : [
				{
					ref : ZaAccount.A_name,
					type : _EMAILADDR_,
					msgName : ZaMsg.MSG_LabelListName,
					label : ZaMsg.LBL_LabelListName,
					forceUpdate : true,
					tableCssStyle : "width:100%",
					inputWidth : "100",
					domainPartWidth : "100%",
					id : "dl_name_field",
					nameContainerCss : "width:100px",
					domainContainerWidth : "100%",
					midContainerCss : "width:20px",
					visibilityChecks : [],
					enableDisableChecks : [[XFormItem.prototype.hasRight, ZaDistributionList.RENAME_DL_RIGHT]]
				}
				,
				{
					type : _SPACER_, height : "3"
				}
				,
				{
					ref : ZaAccount.A_displayname,
					type : _TEXTFIELD_,
					label : ZaMsg.NAD_DisplayName,
					msgName : ZaMsg.NAD_DisplayName,
					align : _LEFT_,
					cssClass : "admin_xform_name_input",
					width : "100%"
				}
				,
				{
					type : _SPACER_, height : "3"
				}
				,
				{
					ref : "description",
					msgName : ZaMsg.NAD_Description,
					label : ZaMsg.NAD_Description,
					labelLocation : _LEFT_,
					align : _LEFT_,
					type : _TEXTFIELD_,
					enableDisableChecks : [ZaItem.hasWritePermission] ,
					visibilityChecks : [ZaItem.hasReadPermission],
					cssClass : "admin_xform_name_input",
					width : "100%"
				}
				,
				{
					ref : "zimbraMailStatus",
					type : _CHECKBOX_,
					trueValue : "enabled",
					falseValue : "disabled",
					align : _LEFT_,
					nowrap : false,
					labelWrap : true,
					label : ZaMsg.DLXV_LabelEnabled,
					msgName : ZaMsg.DLXV_LabelEnabled,
					labelLocation : _LEFT_,
					labelCssClass : "xform_label",
					cssStyle : "padding-left:0px"
				}
				,
				{
					ref : ZaAccount.A_zimbraHideInGal,
					type : _CHECKBOX_,
					trueValue : "TRUE",
					falseValue : "FALSE",
					label : ZaMsg.LBL_zimbraHideInGal,
					labelLocation : _LEFT_,
					labelCssClass : "xform_label",
					labelWrap : true,
					align : _LEFT_,
					nowrap : false,
					msgName : ZaMsg.LBL_zimbraHideInGal,
					cssStyle : "padding-left:0px"
				}
				]
			}
			,
			{
				type : _GROUP_, colSpan : "*", width : "98%", colSizes : ["130px", "*"],
				labelCssClass : "xform_label", cssStyle : "padding-left:0px",
				items : [
				{
					ref : ZaDistributionList.A2_dlType,
					type : _WIZ_CHECKBOX_,
					trueValue : ZaDistributionList.DYNAMIC_DL_TYPE,
					falseValue : ZaDistributionList.STATIC_DL_TYPE,
					label : ZaMsg.LBL_DL_Type,
					labelLocation : _LEFT_,
					labelCssClass : "xform_label",
					labelWrap : true,
					align : _LEFT_,
					nowrap : false,
					msgName : ZaMsg.LBL_DL_Type,
					subLabel : "",
					visibilityChecks : [],
					enableDisableChecks : false
				}
				,
				{
					ref : ZaDistributionList.A_zimbraIsACLGroup,
					type : _WIZ_CHECKBOX_,
					trueValue : "TRUE",
					falseValue : "FALSE",
					label : ZaMsg.LBL_ACL_Group,
					labelLocation : _LEFT_,
					labelCssClass : "xform_label",
					align : _LEFT_,
					subLabel : "",
					visibilityChangeEventSources : [ZaDistributionList.A2_dlType],
					visibilityChecks : [ZaDLXFormView.isDynamicDL],
					enableDisableChecks : []
				}
				,
				{
					type : _INPUT_,
					ref : ZaDistributionList.A_memberOfURL,
					label : ZaMsg.LBL_Member_URL,
					labelLocation : _LEFT_,
					labelCssClass : "xform_label",
					width : "100%",
					visibilityChangeEventSources : [ZaDistributionList.A2_dlType],
					visibilityChecks : [ZaDLXFormView.isDynamicDL],
					enableDisableChangeEventSources : [ZaDistributionList.A_zimbraIsACLGroup],
					enableDisableChecks : [ZaDLXFormView.isNotACLGroup]
				}
				]
			}
			,
			{
				type : _SPACER_, height : "20"
			}
			,
			{
				type : _GROUPER_,
				borderCssClass : "LeftGrouperBorder",
				width : "100%",
				numCols : 1,
				colSizes : ["auto"],
				label : ZaMsg.DLXV_LabelListMembers,
				items : [
				{
					type : _GROUP_,
					width : "100%",
					height : hMsgMemberNum, numCols : 3, colSizes : [ "*", "20px", "104px"], items : [
					{
						type : _OUTPUT_,
						ref : ZaDistributionList.A2_numMembers,
						align : _LEFT_,
						valueChangeEventSources : [ZaDistributionList.A2_numMembers, ZaDistributionList.A2_memNumPages],
						getDisplayValue : ZaDLXFormView.showMembersNum
					}
					,
					{
						type : _CELLSPACER_
					}
					,
					{
						type : _GROUP_,
						width : "100%",
						numCols : 3,
						colSizes : ["32px", "40px", "32px"],
						visibilityChangeEventSources : [ZaDistributionList.A2_memNumPages],
						visibilityChecks : [[ZaDLXFormView.isMoreThanOnePage,
						ZaDistributionList.A2_memNumPages]],
						items : [
						{
							type : _DWT_BUTTON_,
							label : null,
							labelLocation : _NONE_,
							width : "100%",
							id : "backButton",
							icon : "LeftArrow",
							disIcon : "LeftArrowDis",
							enableDisableChecks : [ZaDLXFormView.shouldEnableMemBackButton],
							enableDisableChangeEventSources : [ZaDistributionList.A2_memberList, ZaDistributionList.A2_memNumPages, ZaDistributionList.A2_memPagenum],
							onActivate : "ZaDLXFormView.backMemButtonHndlr.call(this,event)"
						}
						,
						{
							type : _OUTPUT_,
							ref : ZaDistributionList.A2_memPagenum,
							valueChangeEventSources : [ZaDistributionList.A2_memPagenum, ZaDistributionList.A2_memNumPages],
							align : _CENTER_,
							getDisplayValue : ZaDLXFormView.showMembersPaging
						}
						,
						{
							type : _DWT_BUTTON_,
							label : null,
							labelLocation : _NONE_,
							width : "100%",
							id : "fwdButton",
							icon : "RightArrow",
							disIcon : "RightArrowDis",
							enableDisableChecks : [ZaDLXFormView.shouldEnableMemForwardButton],
							enableDisableChangeEventSources : [ZaDistributionList.A2_memberList,
							ZaDistributionList.A2_memNumPages,
							ZaDistributionList.A2_memPagenum],
							onActivate : "ZaDLXFormView.fwdMemButtonHndlr.call(this,event)"
						}
						]
					}
					]
				}
				,
				{
					type : _SPACER_, height : "5"
				}
				,
				{
					ref : ZaDistributionList.A2_memberList,
					type : _DWT_LIST_, height : hMemberList,
					width : wMemberList,
					cssClass : "DLTarget",
					cssStyle : "margin-left: 5px; ",
					widgetClass : ZaAccMiniListView,
					headerList : membersHeaderList,
					hideHeader : true,
					onSelection : ZaDLXFormView.membersSelectionListener,
					bmolsnr : true
				}
				,
				{
					type : _SPACER_, height : "8"
				}
				,
				{
					type : _GROUP_, width : "99%",
					numCols : 3, colSizes : ["45%", "10%", "45%"], items : [
					{
						type : _DWT_BUTTON_,
						label : ZaMsg.DLXV_ButtonRemoveSelected,
						id:"removeButton",
						onActivate : "ZaDLXFormView.removeSelectedMembers.call(this,event)",
						nowrap : true,
						cssStyle : "padding:0px;",
						enableDisableChangeEventSources : [ZaDistributionList.A2_membersSelected],
						enableDisableChecks : [
							[XForm.checkInstanceValueNotEmty, ZaDistributionList.A2_membersSelected],
							[XFormItem.prototype.hasRight, ZaDistributionList.REMOVE_DL_MEMBER_RIGHT]
						]
					}
					,
					{
						type : _CELLSPACER_
					}
					,
					{
						type : _DWT_BUTTON_,
						label : ZaMsg.DLXV_ButtonRemovePage,
						nowrap : true,
						cssStyle : "padding:0px;",
						visibilityChangeEventSources : [ZaDistributionList.A2_numMembers],
						visibilityChecks : [[XForm.checkInstanceValueNot, ZaDistributionList.A2_numMembers, 0]],
						enableDisableChangeEventSources : [ZaDistributionList.A2_memberList],
						enableDisableChecks : [
							ZaDLXFormView.shouldEnableRemoveThisPageButton,
							[XFormItem.prototype.hasRight, ZaDistributionList.REMOVE_DL_MEMBER_RIGHT]
						],
						onActivate : "ZaDLXFormView.removeOnePageMembers.call(this,event)"
					}
					]
				}
				]
			}
			]
		}
		,
		{
			type : _ZARIGHT_GROUPER_, numCols : 1, width : wRightPanel, label : ZaMsg.DLXV_GroupLabelAddMembers,
			items : [
			{
				type : _GROUP_, width : "100%", numCols : 3, colSizes : [ "*", "10px", "104px"],
				items : [
				{
					type : _GROUP_, width : "100%", items : [
					{
						type : _TEXTFIELD_,
						cssClass : "admin_xform_name_input",
						ref : ZaSearch.A_query,
						label : ZaMsg.DLXV_LabelFind,
						labelLocation : _LEFT_,
						labelCssStyle : "white-space: nowrap;",
						visibilityChecks : [],
						enableDisableChecks : [],
						width : "100%",
						align : _LEFT_,
						elementChanged : function(elementValue, instanceValue, event)
						{
							var charCode = event.charCode;
							if (charCode == 13 || charCode == 3)
							{
								ZaDLXFormView.srchButtonHndlr.call(this);
							}
							else
							{
								this.getForm().itemChanged(this, elementValue, event);
							}
						}
					}
					]
				}
				,
				{
					type : _CELLSPACER_
				}
				,
				{
					type : _DWT_BUTTON_,
					label : ZaMsg.DLXV_ButtonSearch,
					onActivate : ZaDLXFormView.srchButtonHndlr
				}
				]
			}
			,
			{
				type : _SPACER_, height : "5", colSpan:"*"
			}
			,
			{
				type : _GROUP_, width : "100%", numCols : 3, colSizes : [ "*", "10px", "104px"],
				items : [
				{
					type : _GROUP_, items : [
					{
						type : _OUTPUT_,
						ref : ZaDistributionList.A2_totalNumInPool,
						align : _LEFT_,
						visibilityChecks : [[ZaDLXFormView.checkTotalNumInPool, -1]],
						getDisplayValue : ZaDLXFormView.showSearchFoundNum,
						nowrap : false
					}
					]
				}
				,
				{
					type : _CELLSPACER_
				}
				,
				{
					type : _GROUP_,
					visibilityChangeEventSources : [ZaDistributionList.A2_poolNumPages],
					visibilityChecks : [[ZaDLXFormView.isMoreThanOnePage,
					ZaDistributionList.A2_poolNumPages]],
					numCols : 3,
					colSizes : ["32px", "40px", "32px"],
					items : [
					{
						type : _DWT_BUTTON_,
						label : null,
						labelLocation : _NONE_,
						id : "backButton",
						icon : "LeftArrow",
						disIcon : "LeftArrowDis",
						enableDisableChecks : [ZaDLXFormView.shouldEnablePoolBackButton],
						enableDisableChangeEventSources : [ZaDistributionList.A2_poolPagenum],
						onActivate : "ZaDLXFormView.backPoolButtonHndlr.call(this,event)"
					}
					,
					{
						type : _OUTPUT_,
						ref : ZaDistributionList.A2_poolPagenum,
						align : _CENTER_,
						getDisplayValue : ZaDLXFormView.showSearchFoundPaging 
					}
					, // label : ZaMsg.Next, nowrap : true,
					{
						type : _DWT_BUTTON_,
						label : null,
						labelLocation : _NONE_,
						id : "fwdButton",
						icon : "RightArrow",
						disIcon : "RightArrowDis",
						enableDisableChecks : [ZaDLXFormView.shouldEnablePoolForwardButton],
						enableDisableChangeEventSources : [ZaDistributionList.A2_poolPagenum],
						onActivate : "ZaDLXFormView.fwdPoolButtonHndlr.call(this,event)"
					}
					]
				}
				]
			}
			,
			{
				type : _SPACER_, height : "5"
			}
			,
			{
				ref : ZaDistributionList.A2_memberPool,
				type : _DWT_LIST_,
				height : hMemberPool,
				width : wMemberPool,
				cssClass : "DLSource",
				forceUpdate : true,
				widgetClass : ZaAccMiniListView,
				headerList : sourceHeaderList,
				hideHeader : false,
				onSelection : ZaDLXFormView.memberPoolSelectionListener
			}
			,
			{
				type : _SPACER_, height : "5", colSpan:"*"
			}
			,
			{
				type : _GROUP_, width : wRightPanel, items : [
				{
					type : _GROUP_,
					width : "99%",
					numCols : 3, colSizes : [ "45%", "10%", "45%"], items : [
					{
						type : _DWT_BUTTON_,
						label : ZaMsg.DLXV_ButtonAddSelected,
						nowrap : true,
						onActivate : "ZaDLXFormView.addSelectedAddressesToMembers.call(this,event)",
						enableDisableChangeEventSources : [ZaDistributionList.A2_memberPoolSelected],
						enableDisableChecks : [[XForm.checkInstanceValueNotEmty,
						ZaDistributionList.A2_memberPoolSelected],
						[XFormItem.prototype.hasRight,
						ZaDistributionList.ADD_DL_MEMBER_RIGHT]]
					}
					,
					{
						type : _CELLSPACER_
					}
					,
					{
						type : _DWT_BUTTON_,
						label : ZaMsg.DLXV_ButtonAddPage,
						nowrap : true,
						onActivate : "ZaDLXFormView.addOnePageAddressesToMembers.call(this,event)",
						visibilityChangeEventSources : [ZaDistributionList.A2_totalNumInPool],
						visibilityChecks : [[ZaDLXFormView.checkTotalNumInPool, 1]],
						enableDisableChangeEventSources : [ZaDistributionList.A2_memberPool],
						enableDisableChecks : [
							[XForm.checkInstanceValueNotEmty, ZaDistributionList.A2_memberPool],
							[XFormItem.prototype.hasRight, ZaDistributionList.ADD_DL_MEMBER_RIGHT]
						]
					}
					]
				}
				,
				{
					type : _SPACER_
				}
				]
			}
			,
			{
				type : _SPACER_, height : "5"
			}
			,
			{
				type : _OUTPUT_,
				value : ZaMsg.DLXV_GroupLabelEnterAddressBelow,
				visibilityChecks : [[XFormItem.prototype.hasRight, ZaDistributionList.ADD_DL_MEMBER_RIGHT]]
			}
			,
			{
				type : _SPACER_, height : "5"
			}
			,
			{
				ref : ZaDistributionList.A2_optionalAdd,
				type : _TEXTAREA_,
				width : wRightPanel,
				height : 98,
				bmolsnr : true,
				visibilityChecks : [[XFormItem.prototype.hasRight, ZaDistributionList.ADD_DL_MEMBER_RIGHT]],
				enableDisableChecks : []
			}
			,
			{
				type : _SPACER_, height : "5", visibilityChecks : [[XFormItem.prototype.hasRight, ZaDistributionList.ADD_DL_MEMBER_RIGHT]]
			}
			,
			{
				type : _GROUP_,
				numCols : 2,
				width : wRightPanel,
				colSizes : [80, "auto"],
				visibilityChecks : [[XFormItem.prototype.hasRight, ZaDistributionList.ADD_DL_MEMBER_RIGHT]],
				items : [
				{
					type : _DWT_BUTTON_,
					label : ZaMsg.DLXV_ButtonAddFromFreeForm,
					width : "100%",
					onActivate : "ZaDLXFormView.addFreeFormAddressToMembers.call(this,event)",
					enableDisableChecks : [[XForm.checkInstanceValueNotEmty,
					ZaDistributionList.A2_optionalAdd]],
					enableDisableChangeEventSources : [ZaDistributionList.A2_optionalAdd]
				}
				,
				{
					type : _OUTPUT_,
					value : ZaMsg.DLXV_SeparateAddresses,
					align : "right"
				}
				]
			}
			]
		}
		,
		{
			type : _CELLSPACER_
		}
		]
	}
	;
	cases.push(case1);

	if(_tab2) {				
		var case2 = 
		{type:_ZATABCASE_, caseKey:_tab2, colSizes:[10, "auto"], colSpan:"*",
         paddingStyle:(appNewUI? "padding-left:15px;":null), cellpadding:(appNewUI?2:0),
			items:[
			    {type:_SPACER_, height:5},
			    {type:_SPACER_, height:5},
			    {type:_CELLSPACER_, width:10 },
			    {type: _OUTPUT_, value:ZaMsg.DLXV_LabelNotes, cssStyle:"align:left"},
			    {type:_CELLSPACER_, width:10 },
			    {ref:ZaAccount.A_notes, type:_TEXTAREA_, width:"90%", height:"400", labelCssStyle:"vertical-align: top"}
			]
		};
		cases.push(case2);
	}
	if(_tab3) {	
		var spaceHeight = "7";
                if(AjxEnv.isIE){
                       spaceHeight = "3";
                }

		var case3 = {type:_ZATABCASE_, numCols:2, colSpan:"*", caseKey:_tab3, colSizes: ["450px","420px"],
			items: [
				//layout rapper around the direct/indrect list	
				{type: _GROUP_, width: "98%", numCols: 1, //colSizes: ["auto", 20],
					items: [
					    {type:_SPACER_, height:"5"}, 							
						//direct member group
						{type:_ZALEFT_GROUPER_, numCols:1, label:ZaMsg.Account_DirectGroupLabel,containerCssStyle: "padding-top:5px", width: "100%",  //height: 400,
							items:[
								{ref: ZaAccount.A2_directMemberList, type: _S_DWT_LIST_, width: "98%", height: 200,
									cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
									headerList: directMemberOfHeaderList, defaultColumnSortable: 0,
									onSelection:ZaDLXFormView.directMemberSelectionListener,
									forceUpdate: true 
								},
								{type:_SPACER_, height:"5"},
								{type:_GROUP_, width:"100%", numCols:8, colSizes:[65,10,65,55,75,10,65,10], 
									items:[
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, 
									      enableDisableChangeEventSources:[ZaDistributionList.A2_directMemberList],
									      enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_directMemberList]],
										  onActivate:"ZaAccountMemberOfListView.removeAllGroups.call(this,event, ZaAccount.A2_directMemberList)"
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, id:"removeButton",
									      onActivate:"ZaAccountMemberOfListView.removeGroups.call(this,event, ZaAccount.A2_directMemberList)",
									      enableDisableChangeEventSources:[ZaDistributionList.A2_directMemberSelected],
									      enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_directMemberSelected]]
									    },
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Previous, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
											onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_directMemberList]],
											enableDisableChangeEventSources:[ZaAccount.A2_directMemberList + "_offset"]													 
									    },								       
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Next, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
											onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
											enableDisableChangeEventSources:[ZaAccount.A2_directMemberList + "_more"],
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_directMemberList]]
									    },								       
										{type:_CELLSPACER_}									
									]
                                                               } 	
							]
						},		
						{type:_SPACER_, height:"10"},	
						//indirect member group
						{type:_ZALEFT_GROUPER_, numCols:1, width: "100%", label:ZaMsg.Account_IndirectGroupLabel, containerCssStyle: "padding-top:5px",
							items:[
								{ref: ZaAccount.A2_indirectMemberList, type: _S_DWT_LIST_, width: "98%", height: 200,
									cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
									headerList: indirectMemberOfHeaderList, defaultColumnSortable: 0,
									onSelection:ZaDLXFormView.indirectMemberSelectionListener,
									forceUpdate: true 
								},
								{type:_SPACER_, height:"5"},
								{type:_GROUP_, width:"100%", numCols:8, colSizes:[65,10,65,55,75,10,65,10], 
									items:[
										{type:_CELLSPACER_},
										{type:_CELLSPACER_},
										{type:_CELLSPACER_},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Previous, id:"indirectBackButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
											onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_indirectMemberList]],
											enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList+"_offset"]
									    },								       
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Next, id:"indirectFwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
											onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_indirectMemberList]],
											enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList+"_more"]
									    },								       
										{type:_CELLSPACER_}									
									]
								}
							]
						}
					]
				},
				//non member group
				//layout rapper around the elements						
				{type: _GROUP_, width: "98%", numCols: 1, //colSizes: ["auto", 20],
					items: [
					    {type:_SPACER_, height:"5"}, 							
						{type:_ZARIGHT_GROUPER_, numCols:1, width: "100%", label:ZaMsg.Account_NonGroupLabel, containerCssStyle: "padding-top:5px",
							items:[
								{type:_GROUP_, numCols:3, width:"98%", 
								   items:[
										{ref:"query", type:_TEXTFIELD_, width:"100%", cssClass:"admin_xform_name_input",  
											nowrap:false,labelWrap:true,
											label:ZaMsg.DLXV_LabelFind,labelCssStyle:"white-space: nowrap;",
											visibilityChecks:[],enableDisableChecks:[],
											elementChanged: function(elementValue,instanceValue, event) {
											  var charCode = event.charCode;
											  if (charCode == 13 || charCode == 3) {
											      ZaAccountMemberOfListView.prototype.srchButtonHndlr.call(this);
											  } else {
											      this.getForm().itemChanged(this, elementValue, event);
											  }
								      		}
										},
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonSearch, width:80,
										   onActivate:ZaAccountMemberOfListView.prototype.srchButtonHndlr
										},
										{ref: ZaAccount.A2_showSameDomain, type: _CHECKBOX_, align:_RIGHT_, 												
												label:null,labelLocation:_NONE_, trueValue:"TRUE", falseValue:"FALSE",
												visibilityChecks:[]
										},										
										{type:_OUTPUT_, value:ZaMsg.NAD_SearchSameDomain,colSpan:2}
									]
						         },
						        {type:_SPACER_, height:spaceHeight},
								{ref: ZaAccount.A2_nonMemberList, type: _S_DWT_LIST_, width: "98%", height: 440,
									cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
									headerList: nonMemberOfHeaderList, defaultColumnSortable: 0,
									onSelection:ZaDLXFormView.nonmemberSelectionListener,
									//createPopupMenu: 
									forceUpdate: true },
									
								{type:_SPACER_, height:"5"},	
								//add action buttons
								{type:_GROUP_, width:"100%", numCols:8, colSizes:[55,10,65,10,65,10,55,10],
									items: [
									   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, 
										onActivate:"ZaAccountMemberOfListView.addGroups.call(this,event, ZaAccount.A2_nonMemberList)",
										enableDisableChangeEventSources:[ZaDistributionList.A2_nonmembersSelected],
										enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_nonmembersSelected]]
									   },
									   {type:_CELLSPACER_},
									   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll,
										onActivate:"ZaAccountMemberOfListView.addAllGroups.call(this,event, ZaAccount.A2_nonMemberList)",
										enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList],
										enableDisableChecks:[[XForm.checkInstanceValueNotEmty, ZaAccount.A2_nonMemberList]]
									   },
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Previous, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
											enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList+"_offset"],
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton, ZaAccount.A2_nonMemberList]],
											onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"
										},								       
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Next, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
										 	enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList + "_more"],
										 	enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_nonMemberList]],
											onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"									
										},								       
										{type:_CELLSPACER_}	
									  ]
							    }								
							]
						}
					]
				}
			]
		};
		cases.push(case3);
              
	}		
				
	if(_tab4) {
		var addAliasButton = {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
						onActivate:"ZaDLXFormView.addAliasButtonListener.call(this);"
					};
		if(entry.id) { 
			addAliasButton.enableDisableChecks = [[XFormItem.prototype.hasRight,ZaDistributionList.ADD_DL_ALIAS_RIGHT]];
		} else {
			addAliasButton.enableDisableChecks = [[ZaItem.hasWritePermission, ZaAccount.A_zimbraMailAlias]];
		}
		
		var editAliasButton = {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
				enableDisableChangeEventSources:[ZaDistributionList.A2_alias_selection_cache],
				onActivate:"ZaDLXFormView.editAliasButtonListener.call(this);",id:"editAliasButton"
			};
		if(entry.id) {
			editAliasButton.enableDisableChecks = [ZaDLXFormView.isEditAliasEnabled,[XFormItem.prototype.hasRight,ZaDistributionList.REMOVE_DL_ALIAS_RIGHT],[XFormItem.prototype.hasRight,ZaAccount.ADD_DL_ALIAS_RIGHT]];	
		} else {
			editAliasButton.enableDisableChecks = [ZaDLXFormView.isEditAliasEnabled,[ZaItem.hasWritePermission, ZaAccount.A_zimbraMailAlias]];
		}
		
		var deleteAliasButton = {type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
				onActivate:"ZaDLXFormView.deleteAliasButtonListener.call(this);",id:"deleteAliasButton",
				
				enableDisableChangeEventSources:[ZaDistributionList.A2_alias_selection_cache]
			};
		if(entry.id) {
			deleteAliasButton.enableDisableChecks=[ZaDLXFormView.isDeleteAliasEnabled,[XFormItem.prototype.hasRight,ZaDistributionList.REMOVE_DL_ALIAS_RIGHT]];
		} else {
			deleteAliasButton.enableDisableChecks=[ZaDLXFormView.isDeleteAliasEnabled,[ZaItem.hasWritePermission, ZaAccount.A_zimbraMailAlias]];
		}
		var case4 = {type:_ZATABCASE_, width:"100%", numCols:1, colSizes:["auto"],caseKey:_tab4,
		items: [
			{type:_SPACER_, height:"9"},
			{type:_GROUPER_, borderCssClass:"LeftGrouperBorder",
				width:"100%", numCols:1,colSizes:["auto"],
				label:ZaMsg.NAD_EditDLAliasesGroup,
				items :[
					{ref:ZaAccount.A_zimbraMailAlias, type:_DWT_LIST_, height:"200", width:"350px", 
						forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource", 
						headerList:null,onSelection:ZaDLXFormView.aliasSelectionListener
					},
 					{type:_GROUP_, numCols:6, colSizes:["100px","10px","100px","10px","100px","auto"],
						cssStyle:"margin:10px;padding-bottom:0;",
						items: [
							deleteAliasButton,
							{type:_CELLSPACER_},
							editAliasButton,
							{type:_CELLSPACER_},
							addAliasButton
						]
					}
				]
			}
		]
	};
	cases.push(case4);
	}

	if(_tab5) {
		var addOwnerButton = {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
						onActivate:"ZaDLXFormView.addOwnerButtonListener.call(this);"
					};

		var editOwnerButton = {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
				enableDisableChangeEventSources:[ZaDistributionList.A2_owners_selection_cache],
                enableDisableChecks:[ZaDLXFormView.isEditOwnerEnabled],
				onActivate:"ZaDLXFormView.editOwnerButtonListener.call(this);"
			};

		var deleteOwnerButton = {type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
				onActivate:"ZaDLXFormView.deleteOwnerButtonListener.call(this);",
				enableDisableChangeEventSources:[ZaDistributionList.A2_owners_selection_cache],
                enableDisableChecks:[ZaDLXFormView.isDeleteOwnerEnabled]
			};

		var case5 = {type:_ZATABCASE_, width:"100%", numCols:1, colSizes:["auto"],caseKey:_tab5,
		items: [
			{type:_SPACER_, height:"9"},
			{type:_GROUPER_, borderCssClass:"LeftGrouperBorder",
				width:"100%", numCols:1,colSizes:["auto"],
				label: ZaMsg.DLXV_GroupLabelDLOwners,
				items :[
					{ref:ZaDistributionList.A2_DLOwners, type:_DWT_LIST_, height:"200", width:"350px",
						forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource",
						headerList:null,onSelection:ZaDLXFormView.ownerSelectionListener
					},
					{type:_GROUP_, numCols:6, colSizes:["100px","10px","100px","10px","100px","auto"],
						cssStyle:"margin:10px;padding-bottom:0;",
						items: [
							deleteOwnerButton,
							{type:_CELLSPACER_},
							editOwnerButton,
							{type:_CELLSPACER_},
							addOwnerButton
						]
					}
				]
			}
		]
	};
	cases.push(case5);
	}

	if(_tab6) {
		var case6 =
		{type:_ZATABCASE_, caseKey:_tab6, colSpan:"*",
			paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
			items:[
                {type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_MailOptionsReceiving, id:"dl_pref_replyto_group",
                    colSpan: "*", numCols: 2, colSizes:[275, "*"],
                    visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,[
                            ZaDistributionList.A_zimbraPrefReplyToEnabled,
                            ZaDistributionList.A_zimbraPrefReplyToDisplay,
                            ZaDistributionList.A_zimbraPrefReplyToAddress
                    ]]],
                    visibilityChangeEventSources:[],
                    items: [
                        {ref:ZaDistributionList.A_zimbraPrefReplyToEnabled, type:_CHECKBOX_,
                            visibilityChecks:[[ZaItem.hasReadPermission, ZaDistributionList.A_zimbraPrefReplyToEnabled]],
                            label:ZaMsg.DLXV_ReplayToEnabled, trueValue:"TRUE", falseValue:"FALSE"
                        },
                        {ref:ZaDistributionList.A_zimbraPrefReplyToDisplay, type:_TEXTFIELD_,
                            label:ZaMsg.DLXV_ReplayToAddrDisplay, labelLocation:_LEFT_, containerCssStyle:"padding-left:3px;",
                            emptyText: ZaMsg.DLXV_ReplayToAddrEmptyText,
                            visibilityChecks:[[ZaItem.hasReadPermission, ZaDistributionList.A_zimbraPrefReplyToDisplay]],
                            enableDisableChecks:[[XForm.checkInstanceValue,ZaDistributionList.A_zimbraPrefReplyToEnabled,"TRUE"],
                            [ZaItem.hasWritePermission,ZaDistributionList.A_zimbraPrefReplyToAddress]],
                            enableDisableChangeEventSources:[ZaDistributionList.A_zimbraPrefReplyToEnabled],width:"15em"
                        },
                        {type:_DYNSELECT_, ref:ZaDistributionList.A_zimbraPrefReplyToAddress, dataFetcherClass:ZaSearch,
                            dataFetcherMethod:ZaSearch.prototype.dynSelectSearch,
                            dataFetcherTypes:[ZaSearch.ACCOUNTS, ZaSearch.RESOURCES, ZaSearch.DLS],
                            dataFetcherAttrs:[ZaItem.A_zimbraId, ZaItem.A_cn, ZaAccount.A_name, ZaAccount.A_displayname, ZaAccount.A_mail],
                            label:ZaMsg.DLXV_ReplayToAddr,labelLocation:_LEFT_,
                            emptyText: ZaMsg.DLXV_ReplayToAddrEmptyText,
                            width:"24em", inputWidth:"32em", editable:true, forceUpdate:true,
                            choices:new XFormChoices([], XFormChoices.OBJECT_LIST, "name", "name"),
                            visibilityChecks:[[ZaItem.hasReadPermission, ZaDistributionList.A_zimbraPrefReplyToAddress]],
                            enableDisableChangeEventSources:[ZaDistributionList.A_zimbraPrefReplyToEnabled],
                            enableDisableChecks:[[XForm.checkInstanceValue,ZaDistributionList.A_zimbraPrefReplyToEnabled,"TRUE"],
                            [ZaItem.hasWritePermission,ZaDistributionList.A_zimbraPrefReplyToAddress]],
                            onChange: function(value, event, form){
                                if (value instanceof ZaItem ) {
                                    this.setInstanceValue(value.name);
                                } else {
                                    this.setInstanceValue(value);
                                }
                            }
                        }
                   ]
                }
			]
		};
		cases.push(case6);
	}

    var headerItems = [{type:_AJX_IMAGE_, src:"Group_32", label:null, rowSpan:3},
						{type:_OUTPUT_, ref:"name", label:null,cssClass:"AdminTitle", height:"auto", width:350, rowSpan:3, cssStyle:"word-wrap:break-word;overflow:hidden"}
						] ;

    if (ZaItem.hasReadPermission (ZaItem.A_zimbraId, entry)) 
        headerItems.push (  {type:_OUTPUT_, ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID}) ;

    if (ZaItem.hasReadPermission (ZaItem.A_zimbraCreateTimestamp, entry))
        headerItems.push({type:_OUTPUT_, ref:ZaItem.A_zimbraCreateTimestamp,
							label:ZaMsg.LBL_zimbraCreateTimestamp, labelLocation:_LEFT_,
							getDisplayValue:function() {
								var val = ZaItem.formatServerTime(this.getInstanceValue());
								if(!val)
									return ZaMsg.Server_Time_NA;
								else
									return val;
							}	
						});

    if (ZaItem.hasReadPermission (ZaDistributionList.A_mailStatus, entry))
        headerItems.push (  {type:_OUTPUT_, ref:ZaDistributionList.A_mailStatus, label:ZaMsg.NAD_ResourceStatus,
								choices: this.dlStatusChoices
						}) ;


	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","*","80px","*"],
					items: headerItems
				}
			]
		},
		{type:_TAB_BAR_, choices:this.tabChoices,
            cssStyle:(appNewUI?"display:none;":""),
			ref: ZaModel.currentTab, colSpan:"*",cssClass:"ZaTabBar", id:"xform_tabbar"
		},
		{type:_SWITCH_,items:cases}
	];	
};

ZaTabView.XFormModifiers["ZaDLXFormView"].push(ZaDLXFormView.myXFormModifier);

ZaDLXFormView.prototype.getTabChoices =
function() {
    return this.tabChoices;
}

ZaDLXFormView.isMoreThanOnePage =
function(refOfPageNum) {
    var form = this.getForm();
    var pageNumInPool = form.getInstanceValue(refOfPageNum) || 0;
    return pageNumInPool > 1;
}

ZaDLXFormView.showSearchFoundNum = function(value){
    if (AjxUtil.isEmpty(value)) {
        return;
    }

    return AjxMessageFormat.format(ZaMsg.DLXV_MsgSearchFound, [value]);
}

ZaDLXFormView.showSearchFoundPaging  = function(value){
    var curPageNum = value || 1;
    var totalPageNum = this.getForm().getInstanceValue(ZaDistributionList.A2_poolNumPages) || 1;
    return curPageNum + "/" + totalPageNum;
}

ZaDLXFormView.showMembersNum = function(value){
    if ( value == null ){
        return ZaMsg.splashScreenLoading;
    }
    if ( value < 0 ){
        value = 0;
    }
    return AjxMessageFormat.format(ZaMsg.DLXV_MsgMemberNum, [value]);
}

ZaDLXFormView.showMembersPaging = function(value){
    var curPageNum = value || 1;
    var totalPagenum = this.getForm().getInstanceValue(ZaDistributionList.A2_memNumPages) || 1;
    return curPageNum + "/" + totalPagenum;
}

ZaDLXFormView.checkTotalNumInPool = function(base){
	if (AjxUtil.isEmpty(base)) {
		base = 0;
	}
	var totalNumInPool = this.getForm().getInstanceValue(ZaDistributionList.A2_totalNumInPool);
	if ( totalNumInPool == null ) {
		totalNumInPool = -1;
	}
	return totalNumInPool > base;
}


ZaDLXFormView.prototype.updateMemberList = function(entry) {
	if (AjxUtil.isEmpty(entry)) { //entry is a updated dl 
		return;
	}

	var xform = this.getMyForm();  //make it when loading
	if (xform) {
		var instance = xform.getInstance() || {};

		//all its members
		instance [ZaDistributionList.A2_allMemberHash] = entry [ZaDistributionList.A2_allMemberHash] || {};
		instance [ZaDistributionList.A2_numMembers] = entry [ZaDistributionList.A2_numMembers] || 0;

		var allMemberPages = entry [ZaDistributionList.A2_allMemberPages] || [];
		instance [ZaDistributionList.A2_allMemberPages] = allMemberPages;

		var firstPage = (allMemberPages && allMemberPages.length) ? allMemberPages[0] : [];
		firstPage._version = 1;
		instance [ZaDistributionList.A2_memberList] = entry [ZaDistributionList.A2_memberList] = firstPage;

		instance [ZaDistributionList.A2_memPagenum ] = 1; //show first page
		instance [ZaDistributionList.A2_memNumPages] = entry [ZaDistributionList.A2_memNumPages] || 1;

		//membership related instance variables
		instance [ZaAccount.A2_memberOf] = ZaAccountMemberOfListView.cloneMemberOf(entry);

		instance [ZaAccount.A2_directMemberList + "_more"] = entry [ZaAccount.A2_directMemberList + "_more"];
		instance [ZaAccount.A2_directMemberList + "_offset"] = entry [ZaAccount.A2_directMemberList + "_offset"];
		instance [ZaAccount.A2_indirectMemberList + "_more"] = entry [ZaAccount.A2_indirectMemberList + "_more"];
		instance [ZaAccount.A2_indirectMemberList + "_offset"] = entry [ZaAccount.A2_indirectMemberList + "_offset"];
		instance [ZaAccount.A2_nonMemberList + "_more"] = entry [ZaAccount.A2_nonMemberList + "_more"];
		instance [ZaAccount.A2_nonMemberList + "_offset"] = entry [ZaAccount.A2_nonMemberList + "_offset"];

		//dl owners
		if (entry[ZaDistributionList.A2_DLOwners]) {
			instance [ZaDistributionList.A2_DLOwners] = ZaItem.deepCloneListItem(entry[ZaDistributionList.A2_DLOwners]);
		}

		//whether is dynamic group
		if(entry[ZaDistributionList.A2_dlType]) {
			instance [ZaDistributionList.A2_dlType] = entry[ZaDistributionList.A2_dlType];
		}

		if ( instance.id != entry.id ) {
			instance.id = entry.id;
		}

		//copy the attrs
		ZaDLXFormView._copyAttrFromEntry( xform, instance, entry );
		xform.setInstance(instance);
	}
}
