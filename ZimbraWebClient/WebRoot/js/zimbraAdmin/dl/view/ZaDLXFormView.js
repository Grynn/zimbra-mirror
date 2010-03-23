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
	return "DistributionList" ;
}

ZaDLXFormView.prototype.handleXFormChange = function (ev) {
	if(ev && this._localXForm.hasErrors()) { 
		ZaApp.getInstance().getCurrentController()._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
	} else {
		ZaApp.getInstance().getCurrentController()._toolbar.getButton(ZaOperation.SAVE).setEnabled(true);
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

/**
* method of an XFormItem
**/
ZaDLXFormView.removeAllMembers = function(event) {
	var form = this.getForm();
	var tmpCurrentRemoveList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_removeList);
	var tmpCurrentMemberList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList);
	
	var newRemoveList = AjxUtil.mergeArrays(tmpCurrentRemoveList,tmpCurrentMemberList);
	newRemoveList._version = tmpCurrentRemoveList._version+1;
	
	this.setInstanceValue([], ZaDistributionList.A2_addList);
	this.setInstanceValue([], ZaDistributionList.A2_memberList);
	this.setInstanceValue(newRemoveList, ZaDistributionList.A2_removeList);

	this.getForm().parent.setDirty(true);
};

/**
* method of an XFormItem
**/
ZaDLXFormView.removeMembers = function(event) {
	var form = this.getForm();
	
	var tmpCurrentMemberList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList);
	var tmpCurrentAddList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList);
	var tmpSelectedList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_membersSelected);
	var tmpCurrentRemoveList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_removeList);
	
	var newMemberList = AjxUtil.arraySubstract(tmpCurrentMemberList, form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_membersSelected));
	newMemberList._version = tmpCurrentMemberList._version + 1;
	this.setInstanceValue(newMemberList, ZaDistributionList.A2_memberList);	
	
	
	var newAddList = AjxUtil.arraySubstract(tmpCurrentAddList,form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_membersSelected));
	newAddList._version = tmpCurrentAddList._version + 1;
	this.setInstanceValue(newAddList, ZaDistributionList.A2_addList);	
	
	
	var newRemoveList = AjxUtil.mergeArrays(tmpCurrentRemoveList,tmpSelectedList);	
	newRemoveList._version = tmpCurrentRemoveList._version+1;
	this.setInstanceValue(newRemoveList, ZaDistributionList.A2_removeList);
	
	this.getForm().parent.setDirty(true);
};

/**
* method of an XFormItem
**/
ZaDLXFormView.srchButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	fieldObj.searchAccounts(null, true);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.fwdPoolButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	var currentPageNum = this.getInstanceValue("/poolPagenum");
	this.setInstanceValue(currentPageNum+1,"/poolPagenum");
	fieldObj.searchAccounts(null, true);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.backPoolButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	var currentPageNum = this.getInstanceValue("/poolPagenum")-1;
	this.setInstanceValue(currentPageNum,"/poolPagenum");
	fieldObj.searchAccounts(null, true);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.fwdMemButtonHndlr = 
function(evt) {
	var currentPageNum = this.getInstanceValue("/memPagenum")+1;
	this.setInstanceValue(currentPageNum,"/memPagenum");
    var currentObj = ZaApp.getInstance().getCurrentController()._currentObject ;
    currentObj[ZaDistributionList.A2_memPagenum] = currentPageNum ;
    var memberList = currentObj.getMembers();
    this.setInstanceValue(memberList, ZaDistributionList.A2_memberList);    
    //	ZaDistributionList.prototype.getMembers.call (this,ZaDistributionList.MEMBER_QUERY_LIMIT ) ;
}

/**                                                                 
* method of an XFormItem
**/
ZaDLXFormView.backMemButtonHndlr = 
function(evt) {
	var currentPageNum = this.getInstanceValue("/memPagenum")-1;
	this.setInstanceValue(currentPageNum,"/memPagenum");
    var currentObj = ZaApp.getInstance().getCurrentController()._currentObject ;
    currentObj[ZaDistributionList.A2_memPagenum] = currentPageNum ;
    var memberList = currentObj.getMembers();
    this.setInstanceValue(memberList, ZaDistributionList.A2_memberList);	
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
ZaDLXFormView.shouldEnableRemoveAllButton = function() {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaDistributionList.A2_memberList)));
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableAddAllButton = function() {
	var list = this.getItemsById("memberPool")[0].widget.getList();
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
	var memberItem = this.getItemsById("memberPool")[0];
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

/**
 * method of an XFormItem
 * Currently, this manages the data, redraws the whole list, and then sets 
 * the selection. 
 * TODO - change the routine to add only the necessary rows to the list view.
 * Same is true of addAllAddresses
 */
ZaDLXFormView.addAddressesToMembers = function (event) {
 	var form = this.getForm();
	var tmpAddArray = AjxUtil.mergeArrays(form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList),
		form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberPoolSelected));
	
	tmpAddArray._version = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList)._version + 1; 
	
	var tmpMembersArray = AjxUtil.mergeArrays(form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList),
		form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberPoolSelected));
	
	tmpMembersArray._version = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList)._version + 1;
	
	this.setInstanceValue(tmpAddArray, ZaDistributionList.A2_addList);
	this.setInstanceValue(tmpMembersArray, ZaDistributionList.A2_memberList);
	this.getForm().parent.setDirty(true);	
};

/**
 * method of an XFormItem
**/
ZaDLXFormView.addAllAddressesToMembers = function (event) {
	var form = this.getForm();
	var tmpAddArray = AjxUtil.mergeArrays(form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList),
		form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberPool),ZaDistributionList.compareTwoMembers);
	
	tmpAddArray._version = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList)._version + 1; 
	
	var tmpMembersArray = AjxUtil.mergeArrays(form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList),
		form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberPool),ZaDistributionList.compareTwoMembers);
	
	tmpMembersArray._version = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList)._version + 1;
	
	this.setInstanceValue(tmpAddArray, ZaDistributionList.A2_addList);
	this.setInstanceValue(tmpMembersArray, ZaDistributionList.A2_memberList);	
	this.getForm().parent.setDirty(true);
};

/**
 * method of an XFormItem
**/
ZaDLXFormView.addFreeFormAddressToMembers = function (event) {
 	var form = this.getForm();
	// get the current value of the textfied
 	var val = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_optionalAdd);
 	if(!val)
 		return;
 		
 	var values = val.split(/[\r\n,;]+/);
	var cnt = values.length;
 	var members = new Array();
 	var stdEmailRegEx = /([^\<\;]*)\<([^\>]+)\>/ ;
	for (var i = 0; i < cnt; i++) {
		var tmpval = AjxStringUtil.trim(values[i],true);
		var result ;
		if (tmpval) {
			if ((result = stdEmailRegEx.exec(tmpval)) != null) {
				tmpval = result[2];
			}
			if(!AjxUtil.isValidEmailNonReg(tmpval)) {
				//how error msg
				ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.WARNING_DL_INVALID_EMAIL,[values[i]]),null,null,DwtMessageDialog.WARNING_STYLE);
				return false;
			}
			members.push(new ZaDistributionListMember(tmpval));
		}
	}

	var tmpAddArray = AjxUtil.mergeArrays(form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList),members);
	
	tmpAddArray._version = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList)._version + 1; 
	
	var tmpMembersArray = AjxUtil.mergeArrays(form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList),
		members,ZaDistributionList.compareTwoMembers);
	
	tmpMembersArray._version = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList)._version + 1;
	this.setInstanceValue(tmpMembersArray, ZaDistributionList.A2_memberList);
	this.setInstanceValue("", ZaDistributionList.A2_optionalAdd);
	this.setInstanceValue(tmpAddArray, ZaDistributionList.A2_addList);
	this.getForm().parent.setDirty(true);
};

ZaDLXFormView.prototype.setObject = 
function (entry) {
    this._containedObject = {attrs:{}};

	this._containedObject[ZaDistributionList.A2_memberList] = new Array();
	this._containedObject[ZaDistributionList.A2_memberList]._version = 1;
	if(entry[ZaDistributionList.A2_memberList]) {
		var memberList;
		memberList = entry[ZaDistributionList.A2_memberList];
 		for (var i = 0 ; i < memberList.length; ++i) {
 			this._containedObject[ZaDistributionList.A2_memberList].push(memberList[i]);
 		}
	}

	for (var a in entry.attrs) {
        var modelItem = this._localXForm.getModel().getItem(a) ;
        if ((modelItem != null && modelItem.type == _LIST_)
           || (entry.attrs[a] != null && entry.attrs[a] instanceof Array))
        {  //need deep clone
            this._containedObject.attrs [a] =
                    ZaItem.deepCloneListItem (entry.attrs[a]);
        } else {
            this._containedObject.attrs[a] = entry.attrs[a];
        }
    }
    
    //Utility members
	this._containedObject[ZaDistributionList.A2_addList] = new Array(); //members to add
	this._containedObject[ZaDistributionList.A2_addList]._version = 1;
	this._containedObject[ZaDistributionList.A2_removeList] = new Array(); //members to remove
	this._containedObject[ZaDistributionList.A2_removeList]._version = 1;
	this._containedObject[ZaDistributionList.A2_poolPagenum] = 1;
	this._containedObject[ZaDistributionList.A2_poolNumPages] = entry [ZaDistributionList.A2_poolNumPages];
	this._containedObject[ZaDistributionList.A2_memPagenum] = 1;
	this._containedObject[ZaDistributionList.A2_memNumPages] = entry [ZaDistributionList.A2_memNumPages];
	this._containedObject[ZaDistributionList.A2_query] = "";
	//membership related instance variables
	this._containedObject[ZaAccount.A2_memberOf] = ZaAccountMemberOfListView.cloneMemberOf(entry);

	this._containedObject[ZaAccount.A2_directMemberList + "_more"] = entry[ZaAccount.A2_directMemberList + "_more"];
	this._containedObject[ZaAccount.A2_directMemberList + "_offset"] = entry[ZaAccount.A2_directMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_indirectMemberList + "_more"] = entry[ZaAccount.A2_indirectMemberList + "_more"];
	this._containedObject[ZaAccount.A2_indirectMemberList + "_offset"] = entry[ZaAccount.A2_indirectMemberList + "_offset"];	
	this._containedObject[ZaAccount.A2_nonMemberList + "_more"] = entry[ZaAccount.A2_nonMemberList + "_more"];
	this._containedObject[ZaAccount.A2_nonMemberList + "_offset"] = entry[ZaAccount.A2_nonMemberList + "_offset"];

	//dl.isgroup = this.isgroup ;
	
	if(entry.rights)
		this._containedObject.rights = entry.rights;

	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
		
	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type;
	this._containedObject.id = entry.id;
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
	
	this._containedObject[ZaDistributionList.A2_publishedShares] = [];
	if(!AjxUtil.isEmpty(entry[ZaDistributionList.A2_publishedShares]) && !AjxUtil.isEmpty(entry[ZaDistributionList.A2_publishedShares].getArray())) {
		for(var i=0;i<entry[ZaDistributionList.A2_publishedShares].getArray().length;i++) {
			this._containedObject[ZaDistributionList.A2_publishedShares][i] = entry[ZaDistributionList.A2_publishedShares].getArray()[i];
		}
		this._containedObject[ZaDistributionList.A2_publishedShares]._version = 1;
	}
	this.modifyContainedObject () ;	
	this._localXForm.setInstance(this._containedObject);	
	
	this.updateTab();
}

ZaDLXFormView.prototype.searchAccounts = 
function (orderby, isascending) {
	try {
		orderby = (orderby !=null) ? orderby : ZaAccount.A_name;
		var types = [ZaSearch.ACCOUNTS,ZaSearch.DLS,ZaSearch.ALIASES] ; 
		var  searchQueryHolder = new ZaSearchQuery(ZaSearch.getSearchByNameQuery(this._containedObject["query"], types,true),
                                types , false, "",null,10);
		var result = ZaSearch.searchByQueryHolder(searchQueryHolder, this._containedObject["poolPagenum"], orderby, isascending);
		if(result.list) {
			this._containedObject.memberPool = result.list.getArray();
		}
		this._containedObject.poolNumPages = result.numPages;
		this._localXForm.setInstance(this._containedObject);

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

ZaDLXFormView.shareSelectionListener = 
function (ev) {
	var arr = this.widget.getSelection();	
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_published_share_selection_cache, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_published_share_selection_cache, null);
	}		
}

ZaDLXFormView.publishShareCallback = function () {
	var tmp = new ZaDistributionList(this.getModel().getInstanceValue(this.getInstance(), "id"),this.getModel().getInstanceValue(this.getInstance(), ZaAccount.A_name));
	tmp.getPublishedShareInfo();
	var tmpArr = tmp[ZaDistributionList.A2_publishedShares] ? tmp[ZaDistributionList.A2_publishedShares].getArray() : [];
	var oldArr = this.getModel().getInstanceValue(this.getInstance(),ZaDistributionList.A2_publishedShares);
	if(!AjxUtil.isEmpty(oldArr)) {
		tmpArr._version = oldArr._version + 1;
	} else {
		tmpArr._version = 1;
	}
	this.getModel().setInstanceValue(this.getInstance(),ZaDistributionList.A2_publishedShares,tmpArr);	
	if(this.parent.publishShareDlg)
		this.parent.publishShareDlg.popdown();
}

ZaDLXFormView.publishNewShareButtonListener = function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.publishShareDlg) {
		formPage.publishShareDlg = new ZaPublishShareXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "300px",ZaMsg.Share_PublishNewTitle);
		formPage.publishShareDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDLXFormView.publishShareCallback, this.getForm(), null);						
	}
	
	formPage.publishShareDlg.setObject(instance);
	formPage.publishShareDlg.popup();	
}

ZaDLXFormView.upublishShareButtonListener = function () {
	var form = this.getForm();
	var dl = this.getInstance();
	var shares = this.getInstanceValue(ZaDistributionList.A2_published_share_selection_cache);
	ZaDistributionList.publishShare.call(dl,shares,true, new AjxCallback(form,ZaDLXFormView.unpublishShareCallback));		
}

ZaDLXFormView.unpublishShareCallback = function (respObj) {
	
	if(respObj.isException && respObj.isException()) {
		ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(),"ZaDLXFormView.unpublishShareCallback", null, false);
	} else if(respObj.getResponse().Body.BatchResponse.Fault) {
		var fault = respObj.getResponse().Body.BatchResponse.Fault;
		if(fault instanceof Array)
			fault = fault[0];
			
		if (fault) {
			// JS response with fault
			var ex = ZmCsfeCommand.faultToEx(fault);
			ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaDLXFormView.unpublishShareCallback", null, false);
		}
	}
	
	var dl = this.getInstance();
	var oldList = this.getModel().getInstanceValue(dl,ZaDistributionList.A2_publishedShares);
	ZaDistributionList.prototype.getPublishedShareInfo.call(dl);
	var list = this.getModel().getInstanceValue(dl,ZaDistributionList.A2_publishedShares);
	if(!list) {
		list = new Array();
	} else {
		list = list.getArray();
	}
	list._version = oldList ? oldList._version+1 : 2;
	this.getModel().setInstanceValue(dl,ZaDistributionList.A2_publishedShares,list);
	
	var newSelectionCache = new Array();
	var oldSelectionCache = this.getModel().getInstanceValue(dl,ZaDistributionList.A2_published_share_selection_cache);
	if(oldSelectionCache)
		newSelectionCache._version = oldSelectionCache._version+1;
	this.getModel().setInstanceValue(dl,ZaDistributionList.A2_published_share_selection_cache,newSelectionCache);
	
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

ZaDLXFormView.NOTES_TAB_ATTRS = [ZaAccount.A_notes];
ZaDLXFormView.NOTES_TAB_RIGHTS = [];

ZaDLXFormView.MEMBEROF_TAB_ATTRS = [];
ZaDLXFormView.MEMBEROF_TAB_RIGHTS = [ZaDistributionList.GET_DL_MEMBERSHIP_RIGHT];

ZaDLXFormView.ALIASES_TAB_ATTRS = [ZaAccount.A_zimbraMailAlias];
ZaDLXFormView.ALIASES_TAB_RIGHTS = [ZaDistributionList.ADD_DL_ALIAS_RIGHT,ZaDistributionList.REMOVE_DL_ALIAS_RIGHT];

ZaDLXFormView.SHARES_TAB_ATTRS = [];
ZaDLXFormView.SHARES_TAB_RIGHTS = [ZaDistributionList.GET_DL_SHARE_INFO_RIGHT,ZaDistributionList.PUBLISH_SHARE_RIGHT];


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
	
	var _tab1, _tab2, _tab3, _tab4, _tab5;
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

	if(ZaTabView.isTAB_ENABLED(entry,ZaDLXFormView.SHARES_TAB_ATTRS, ZaDLXFormView.SHARES_TAB_RIGHTS)) {
		_tab5 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab5, label:ZaMsg.Share_TabTitle});	
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
	var case1 = 
	{type:_ZATABCASE_,  caseKey:_tab1,  numCols:2,  colSizes: ["50%","50%"], id: "dl_form_members",
		  items:[
			 {type:_GROUP_, width: "98%", numCols: 1, 
				items:[	
				    {type:_SPACER_, height:"5"}, 						    
				    {type: _GROUP_, width: "98%", id: "dl_form_members_general_group", numCols: 2, colSizes:[100, "*"], items: [
				    		{ref:ZaAccount.A_name, type:_EMAILADDR_, msgName:ZaMsg.MSG_LabelListName, label: ZaMsg.LBL_LabelListName, 
				    			forceUpdate:true, tableCssStyle: "width:100", inputWidth:"100",
								id:"dl_name_field",
								enableDisableChecks:[[XFormItem.prototype.hasRight,ZaDistributionList.RENAME_DL_RIGHT]],
								visibilityChecks:[]
							},
						    {ref:ZaAccount.A_displayname, type:_TEXTFIELD_, label:ZaMsg.NAD_DisplayName, msgName:ZaMsg.NAD_DisplayName,width:"100%",
						    	visibilityChecks:[],
                                cssClass:"admin_xform_name_input", align: _LEFT_
						    },
                            ZaItem.descriptionXFormItem,
							{ref: "zimbraMailStatus", type:_CHECKBOX_, trueValue:"enabled", falseValue:"disabled", align:_LEFT_,
								visibilityChecks:[],nowrap:false,labelWrap:true,
                                label:ZaMsg.DLXV_LabelEnabled, msgName:ZaMsg.DLXV_LabelEnabled, labelLocation:_LEFT_,
								labelCssClass:"xform_label", cssStyle:"padding-left:0px"
							},	
   							{ref:ZaAccount.A_zimbraHideInGal, type:_CHECKBOX_, trueValue:"TRUE", falseValue:"FALSE", align:_LEFT_,
								visibilityChecks:[],nowrap:false,labelWrap:true,
                                label:ZaMsg.LBL_zimbraHideInGal, msgName:ZaMsg.LBL_zimbraHideInGal, labelLocation:_LEFT_,labelCssClass:"xform_label", cssStyle:"padding-left:0px"
							}
						]
					},
			        {type:_SPACER_, height:"3"},
			        {type:_OUTPUT_, value:ZaMsg.DLXV_LabelListMembers,  cssClass:"xform_label_left",
						width: AjxEnv.isIE ? 100 : 94, cssStyle:"text-align: right;"
					},
					{ref:ZaDistributionList.A2_memberList, type:_DWT_LIST_, height:"338", width:"98%", 
						cssClass: "DLTarget", cssStyle:"margin-left: 5px; ",
						widgetClass:ZaAccMiniListView, headerList:membersHeaderList,hideHeader:true,
						onSelection:ZaDLXFormView.membersSelectionListener,bmolsnr:true
					},
			        {type:_SPACER_, height:"8"},
				    {type:_GROUP_, width:"98%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
						items:[
							{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:80, 
							   enableDisableChangeEventSources:[ZaDistributionList.A2_memberList],
							   enableDisableChecks:[ZaDLXFormView.shouldEnableRemoveAllButton,[XFormItem.prototype.hasRight,ZaDistributionList.REMOVE_DL_MEMBER_RIGHT]],
							   onActivate:"ZaDLXFormView.removeAllMembers.call(this,event)"
							 },
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:80, id:"removeButton",
						      onActivate:"ZaDLXFormView.removeMembers.call(this,event)",
						       enableDisableChangeEventSources:[ZaDistributionList.A2_membersSelected],
						       enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_membersSelected],[XFormItem.prototype.hasRight,ZaDistributionList.REMOVE_DL_MEMBER_RIGHT]]
						    },
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
								onActivate:"ZaDLXFormView.backMemButtonHndlr.call(this,event)", 
								enableDisableChangeEventSources:[ZaDistributionList.A2_memberList],
								enableDisableChecks:[ZaDLXFormView.shouldEnableMemBackButton]
						    },								       
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
								onActivate:"ZaDLXFormView.fwdMemButtonHndlr.call(this,event)",
								enableDisableChangeEventSources:[ZaDistributionList.A2_memberList], 
								enableDisableChecks:[ZaDLXFormView.shouldEnableMemForwardButton]
						    },								       
							{type:_CELLSPACER_}									
						]
					},
					{type:_SPACER_, height:"5"}
			    ]
		    },
			{type:_ZARIGHT_GROUPER_, numCols:1, width: "100%", label:ZaMsg.DLXV_GroupLabelAddMembers,	
				items:[			      
			       {type:_GROUP_, numCols:3, width:"98%", 
					   items:[
							{type:_TEXTFIELD_, cssClass:"admin_xform_name_input", ref:ZaSearch.A_query, label:ZaMsg.DLXV_LabelFind,
						      visibilityChecks:[],enableDisableChecks:[],
						      elementChanged: function(elementValue,instanceValue, event) {
								  var charCode = event.charCode;
								  if (charCode == 13 || charCode == 3) {
								      ZaDLXFormView.srchButtonHndlr.call(this);
								  } else {
								      this.getForm().itemChanged(this, elementValue, event);
								  }
					      		}
							},
							{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonSearch, width:80,
							   onActivate:ZaDLXFormView.srchButtonHndlr
							}
						]
			       },
			       {type:_SPACER_, height:"5"},
				   {ref:ZaDistributionList.A2_memberPool, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
				   		forceUpdate: true, widgetClass:ZaAccMiniListView, headerList:sourceHeaderList,hideHeader:false,
				   		onSelection:ZaDLXFormView.memberPoolSelectionListener
				   	},
			       {type:_SPACER_, height:"5"},
			       {type:_GROUP_, width:"98%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5],
					items: [
					   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, width:80,
						onActivate:"ZaDLXFormView.addAddressesToMembers.call(this,event)",
						enableDisableChangeEventSources:[ZaDistributionList.A2_memberPoolSelected],
						enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_memberPoolSelected],[XFormItem.prototype.hasRight,ZaDistributionList.ADD_DL_MEMBER_RIGHT]]
						},
					   {type:_CELLSPACER_},
					   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:80,
						onActivate:"ZaDLXFormView.addAllAddressesToMembers.call(this,event)",
						enableDisableChangeEventSources:[ZaDistributionList.A2_memberPool],
						enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_memberPool],[XFormItem.prototype.hasRight,ZaDistributionList.ADD_DL_MEMBER_RIGHT]]
						},
						{type:_CELLSPACER_},
						{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
							enableDisableChecks:[ZaDLXFormView.shouldEnablePoolBackButton],
							enableDisableChangeEventSources:[ZaDistributionList.A2_poolPagenum],
							onActivate:"ZaDLXFormView.backPoolButtonHndlr.call(this,event)"
						},								       
						{type:_CELLSPACER_},
						{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
						 	enableDisableChecks:[ZaDLXFormView.shouldEnablePoolForwardButton],
						 	enableDisableChangeEventSources:[ZaDistributionList.A2_poolPagenum],
							onActivate:"ZaDLXFormView.fwdPoolButtonHndlr.call(this,event)"									
						},								       
						{type:_CELLSPACER_}	
					  ]
			       },
			       
			       {type:_OUTPUT_, value:ZaMsg.DLXV_GroupLabelEnterAddressBelow,
			       		visibilityChecks:[[XFormItem.prototype.hasRight,ZaDistributionList.ADD_DL_MEMBER_RIGHT]]
			       },
			       {ref:ZaDistributionList.A2_optionalAdd, type:_TEXTAREA_,width:"98%", height:98,bmolsnr:true,
						visibilityChecks:[[XFormItem.prototype.hasRight,ZaDistributionList.ADD_DL_MEMBER_RIGHT]],
						enableDisableChecks:[]
			       },
			       {type:_SPACER_, height:"5",
			       		visibilityChecks:[[XFormItem.prototype.hasRight,ZaDistributionList.ADD_DL_MEMBER_RIGHT]]
			       },
			       {type:_GROUP_, numCols:2, width:"98%", colSizes:[80,"auto"],
			       		visibilityChecks:[[XFormItem.prototype.hasRight,ZaDistributionList.ADD_DL_MEMBER_RIGHT]],
						items: [
							{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromFreeForm, width:"100%",
								onActivate:"ZaDLXFormView.addFreeFormAddressToMembers.call(this,event)",
								enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_optionalAdd]],
								enableDisableChangeEventSources:[ZaDistributionList.A2_optionalAdd]									
							},
						   {type:_OUTPUT_, value:"Separate addresses with comma or return", align:"right"}
						]
			       }				       
				]
		    }
		  ]
		};

		cases.push(case1);
		
	if(_tab2) {				
		var case2 = 
		{type:_ZATABCASE_, caseKey:_tab2, colSizes:[10, "auto"], colSpan:"*",
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
		var case3 = {type:_ZATABCASE_, numCols:2, colSpan:"*", caseKey:_tab3, colSizes: ["50%", "50%"],
			items: [
				//layout rapper around the direct/indrect list						
				{type: _GROUP_, width: "98%", numCols: 1, //colSizes: ["auto", 20],
					items: [
					    {type:_SPACER_, height:"5"}, 							
						//direct member group
						{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "96%",  //height: 400,
							items:[
								{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.Account_DirectGroupLabel, cssClass:"RadioGrouperLabel"},
										{type:_CELLSPACER_}
									]
								},
								{ref: ZaAccount.A2_directMemberList, type: _S_DWT_LIST_, width: "100%", height: 200,
									cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
									headerList: directMemberOfHeaderList, defaultColumnSortable: 0,
									onSelection:ZaDLXFormView.directMemberSelectionListener,
									forceUpdate: true 
								},
								{type:_SPACER_, height:"5"},
								{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
									items:[
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:80,
									      enableDisableChangeEventSources:[ZaDistributionList.A2_directMemberList],
									      enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_directMemberList]],
										  onActivate:"ZaAccountMemberOfListView.removeAllGroups.call(this,event, ZaAccount.A2_directMemberList)"
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:80, id:"removeButton",
									      onActivate:"ZaAccountMemberOfListView.removeGroups.call(this,event, ZaAccount.A2_directMemberList)",
									      enableDisableChangeEventSources:[ZaDistributionList.A2_directMemberSelected],
									      enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_directMemberSelected]]
									    },
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
											onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_directMemberList]],
											enableDisableChangeEventSources:[ZaAccount.A2_directMemberList + "_offset"]													 
									    },								       
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
											onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
											enableDisableChangeEventSources:[ZaAccount.A2_directMemberList + "_offset"],
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_directMemberList]]
									    },								       
										{type:_CELLSPACER_}									
									]
								}		
							]
						},		
						{type:_SPACER_, height:"10"},	
						//indirect member group
						{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "98%", //colSizes:["auto"], height: "48%",
							items:[
								{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.Account_IndirectGroupLabel, cssClass:"RadioGrouperLabel"},
										{type:_CELLSPACER_}
									]
								},
								{ref: ZaAccount.A2_indirectMemberList, type: _S_DWT_LIST_, width: "100%", height: 200,
									cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
									headerList: indirectMemberOfHeaderList, defaultColumnSortable: 0,
									onSelection:ZaDLXFormView.indirectMemberSelectionListener,
									forceUpdate: true 
								},
								{type:_SPACER_, height:"5"},
								{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
									items:[
										{type:_CELLSPACER_},
										{type:_CELLSPACER_},
										{type:_CELLSPACER_},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"indirectBackButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
											onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
											enabeDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_indirectMemberList]],
											enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList+"_offset"]
									    },								       
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"indirectFwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
											onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
											enabeDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_indirectMemberList]],
											enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList+"_offset"]
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
						{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "96%", //colSizes:["auto"], height: "98%",
							items:[
								{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.DL_NonGroupLabel, width: AjxEnv.isIE ? "248px": null, cssClass:"RadioGrouperLabel"},
										{type:_CELLSPACER_}
									]
								},
								{type:_GROUP_, numCols:3, colSizes:[40, "auto",75], width:"98%", 
								   items:[
										{ref:"query", type:_TEXTFIELD_, width:"100%", cssClass:"admin_xform_name_input",  
											nowrap:false,labelWrap:true,
											label:ZaMsg.DLXV_LabelFind,
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
						        {type:_SPACER_, height:"5"},
								{ref: ZaAccount.A2_nonMemberList, type: _S_DWT_LIST_, width: "100%", height: 440,
									cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
									headerList: nonMemberOfHeaderList, defaultColumnSortable: 0,
									onSelection:ZaDLXFormView.nonmemberSelectionListener,
									//createPopupMenu: 
									forceUpdate: true },
									
								{type:_SPACER_, height:"5"},	
								//add action buttons
								{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5],
									items: [
									   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, width:80,
										onActivate:"ZaAccountMemberOfListView.addGroups.call(this,event, ZaAccount.A2_nonMemberList)",
										enableDisableChangeEventSources:[ZaDistributionList.A2_nonmembersSelected],
										enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_nonmembersSelected]]
									   },
									   {type:_CELLSPACER_},
									   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:80,
										onActivate:"ZaAccountMemberOfListView.addAllGroups.call(this,event, ZaAccount.A2_nonMemberList)",
										enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList],
										enableDisableChecks:[[XForm.checkInstanceValueNotEmty, ZaAccount.A2_nonMemberList]]
									   },
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
											enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList+"_offset"],
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton, ZaAccount.A2_nonMemberList]],
											onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"
										},								       
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
										 	enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList + "_offset"],
										 	enableDisableChecks:[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_nonMemberList],
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
		var case4 = {type:_ZATABCASE_, width:"100%", numCols:1, colSizes:["auto"],caseKey:_tab4,
		items: [
				{type:_ZA_TOP_GROUPER_, borderCssClass:"LowPadedTopGrouperBorder",
					 width:"100%", numCols:1,colSizes:["auto"],
					label:ZaMsg.NAD_EditDLAliasesGroup,
					items :[
						{ref:ZaAccount.A_zimbraMailAlias, type:_DWT_LIST_, height:"200", width:"350px", 
							forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource", 
							headerList:null,onSelection:ZaDLXFormView.aliasSelectionListener
						},
						{type:_GROUP_, numCols:5, width:"350px", colSizes:["100px","auto","100px","auto","100px"], 
							cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
							items: [
								{type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
									onActivate:"ZaDLXFormView.deleteAliasButtonListener.call(this);",id:"deleteAliasButton",
									enableDisableChecks:[ZaDLXFormView.isDeleteAliasEnabled,[XFormItem.prototype.hasRight,ZaDistributionList.REMOVE_DL_ALIAS_RIGHT]],
									enableDisableChangeEventSources:[ZaDistributionList.A2_alias_selection_cache]
								},
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
									enableDisableChangeEventSources:[ZaDistributionList.A2_alias_selection_cache],
									enableDisableChecks:[ZaDLXFormView.isEditAliasEnabled,[XFormItem.prototype.hasRight,ZaDistributionList.REMOVE_DL_ALIAS_RIGHT],[XFormItem.prototype.hasRight,ZaAccount.ADD_DL_ALIAS_RIGHT]],
									onActivate:"ZaDLXFormView.editAliasButtonListener.call(this);",id:"editAliasButton"
								},
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
									enableDisableChecks:[[XFormItem.prototype.hasRight,ZaDistributionList.ADD_DL_ALIAS_RIGHT]],
									onActivate:"ZaDLXFormView.addAliasButtonListener.call(this);"
								}
							]
						}
					]
				}
			]
		};
		cases.push(case4);
	}
	
	if(_tab5) {
		var shareHeaderList = new Array();
		shareHeaderList[0] = new ZaListHeaderItem(ZaShare.A_folderPath, ZaMsg.Shares_FolderPath, null, "100px", null, null, false, true);
		shareHeaderList[1] = new ZaListHeaderItem(ZaShare.A_ownerName, ZaMsg.Shares_OwnerName, null, "106px", null, null, false, true);
		shareHeaderList[2] = new ZaListHeaderItem(ZaShare.A_granteeName, ZaMsg.Shares_GranteeName, null, "106px", null, null, false, true);
		
				
		var case5 = {type:_ZATABCASE_, numCols:1, colSpan:"*", caseKey:_tab5, colSizes: ["100%"],
		items:[
			{type:_SPACER_, height:"5"},
			{type: _GROUP_, width: "98%", id: "dl_shares_options_grp", numCols: 2, colSizes:["275px","*"],
				items: [
					{ref:ZaDistributionList.A_zimbraDistributionListSendShareMessageToNewMembers, type:_CHECKBOX_,
					  msgName:ZaMsg.DL_zimbraDistributionListSendShareMessageToNewMembers,
					  label:ZaMsg.DL_zimbraDistributionListSendShareMessageToNewMembers, trueValue:"TRUE", falseValue:"FALSE"
					},
					{ref:ZaDistributionList.A_zimbraDistributionListSendShareMessageFromAddress, type:_TEXTFIELD_,width:250,
						msgName:ZaMsg.DL_zimbraDistributionListSendShareMessageFromAddress,label:ZaMsg.DL_zimbraDistributionListSendShareMessageFromAddress, labelLocation:_LEFT_, align:_LEFT_
					}
				]
			},
			
			{type:_SPACER_, height:"5"},  
			{type:_ZAALLSCREEN_GROUPER_, numCols:1, width:"98%", label:ZaMsg.Shares_ListTitle,  
			items: [
		    	{ref:ZaDistributionList.A2_publishedShares, bmolsnr:true,
		    		type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource",onSelection:ZaDLXFormView.shareSelectionListener,
				   	multiselect:true, widgetClass:ZaSharesListView, headerList:shareHeaderList
				},
				{type:_GROUP_, numCols:4, width:"350px", colSizes:["150px","5px","150px","auto"], 
					cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
					items: [
						{type:_DWT_BUTTON_, label:ZaMsg.Shares_PublishNew,width:"150px",
							id:"deleteShareButton",onActivate:"ZaDLXFormView.publishNewShareButtonListener.call(this,event)",
							enableDisableChecks:[[XFormItem.prototype.hasRight,ZaDistributionList.PUBLISH_SHARE_RIGHT]]
						},
						{type:_CELLSPACER_},
						{type:_DWT_BUTTON_, label:ZaMsg.Shares_UnPublish,width:"210px",
							id:"deleteShareButton",onActivate:"ZaDLXFormView.upublishShareButtonListener.call(this,event)",
							enableDisableChangeEventSources:[ZaDistributionList.A2_published_share_selection_cache],
							enableDisableChecks:[[XFormItem.prototype.hasRight,ZaDistributionList.PUBLISH_SHARE_RIGHT],[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_published_share_selection_cache]]
						},
						{type:_CELLSPACER_}							
					]
				}
			]}
		]};		
		cases.push(case5);
	}	
	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
					items: [
						{type:_AJX_IMAGE_, src:"Group_32", label:null, rowSpan:3},
						{type:_OUTPUT_, ref:"name", label:null,cssClass:"AdminTitle", rowSpan:3},
						{type:_OUTPUT_, ref:"id", label:ZaMsg.NAD_ZimbraID},
						{type:_OUTPUT_, ref:"zimbraMailStatus", label:ZaMsg.NAD_AccountStatus,
								choices: this.dlStatusChoices
						},
						{type:_OUTPUT_, ref:ZaItem.A_zimbraCreateTimestamp, 
							label:ZaMsg.LBL_zimbraCreateTimestamp, labelLocation:_LEFT_,
							getDisplayValue:function() {
								var val = ZaItem.formatServerTime(this.getInstanceValue());
								if(!val)
									return ZaMsg.Server_Time_NA;
								else
									return val;
							},
							visibilityChecks:[ZaItem.hasReadPermission]	
						}						
					]
				}
			],
			cssStyle:"padding-top:5px; padding-bottom:5px"
		},
		{type:_TAB_BAR_, choices:this.tabChoices,
			ref: ZaModel.currentTab, colSpan:"*",cssClass:"ZaTabBar", id:"xform_tabbar"
		},
		{type:_SWITCH_,items:cases}
	];	
};

ZaTabView.XFormModifiers["ZaDLXFormView"].push(ZaDLXFormView.myXFormModifier);
