/**
 * Created by IntelliJ IDEA.
 * User: jxy
 * Date: 10/19/11
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */
/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2011, 2012, 2013 VMware, Inc.
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
ZaNewDLXWizard = function(parent, entry) {
	var height, width;
	if (AjxEnv.isWebKitBased || AjxEnv.isFirefox) {
		width = "740px"; //940px is full view, but it is too big, so cut it
		height = "465px"; //665px is full view ....
	} else {
		width = "720px"; //920px is full view, but it is too big, so cut it
		height = "455px";  //655px is full view ....
	}
	ZaXWizardDialog.call(this, parent, null, ZaMsg.DLTBB_New_tt, width, height, "ZaNewDLXWizard",null,ZaId.DLG_NEW_DL);
	this.dlStatusChoices = [
		{value:"enabled", label:ZaMsg.DL_Status_enabled},
		{value:"disabled", label:ZaMsg.DL_Status_disabled}
	]
	this.initForm(ZaDistributionList.myXModel,this.getMyXForm(entry));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_CHANGED, new AjxListener(this, ZaNewDLXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaNewDLXWizard.prototype.handleXFormChange));
}

ZaNewDLXWizard.prototype = new ZaXWizardDialog;
ZaNewDLXWizard.prototype.constructor = ZaNewDLXWizard;
ZaNewDLXWizard.prototype.toString = function() {
    return "ZaNewDLXWizard";
}
ZaXDialog.XFormModifiers["ZaNewDLXWizard"] = new Array();
ZaNewDLXWizard.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/create_an_account.htm?locid="+AjxEnv.DEFAULT_LOCALE;


ZaNewDLXWizard.prototype.getTitle =
function () {
	return ZaMsg.DLTBB_New_tt;
}

ZaNewDLXWizard.prototype.getTabIcon =
function () {
	if (this._containedObject && this._containedObject.attrs && this._containedObject.attrs[ZaDistributionList.A_isAdminGroup]=="TRUE" ) {
                return "DistributionListGroup";

        } else {
                return "DistributionList" ;
        }

}

ZaNewDLXWizard.prototype.handleXFormChange = function (ev) {
    if(ev && this._localXForm.hasErrors()) {
        this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	    this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		if(this._containedObject[ZaAccount.A_name]
                && this._containedObject[ZaAccount.A_name].indexOf("@") > 0) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
            if (this._containedObject[ZaModel.currentStep] != this._lastStep) {
                 this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
            }
            if (this._containedObject[ZaModel.currentStep] != 1) {
                 this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
            }
        }
	}
}



ZaNewDLXWizard.prototype.popup =
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
}


ZaNewDLXWizard.prototype.createDomainAndAccount = function(domainName) {
	try {
		var newDomain = new ZaDomain();
		newDomain.name=domainName;
		newDomain.attrs[ZaDomain.A_domainName] = domainName;
		var domain = ZaItem.create(newDomain,ZaDomain,"ZaDomain");
		if(domain != null) {
			ZaApp.getInstance().getCurrentController().closeCnfrmDelDlg();
			this.finishWizard();
		}
	} catch(ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaNewDLXWizard.prototype.createDomainAndAccount", null, false);
	}
}


ZaNewDLXWizard.prototype.finishWizard =
function() {
	try {
		if(!ZaDistributionList.checkValues(this._containedObject)) {
			return false;
		}
		var dl = ZaItem.create(this._containedObject, ZaDistributionList, "ZaDistributionList");
		if(dl != null) {
			ZaApp.getInstance().getDistributionListController().fireCreationEvent(this._containedObject);
			this.popdown();
            ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.DLCreated,[dl.name]));
		}
	} catch (ex) {
		switch(ex.code) {
			case ZmCsfeException.ACCT_EXISTS:
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_EXISTS);
			break;
			case ZmCsfeException.ACCT_INVALID_PASSWORD:
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_INVALID, ex);
				ZaApp.getInstance().getAppCtxt().getErrorDialog().showDetail(true);
			break;
			case ZmCsfeException.NO_SUCH_COS:
				ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_NO_SUCH_COS,[this._containedObject.attrs[ZaAccount.A_COSId]]), ex);
		    break;
			case ZmCsfeException.NO_SUCH_DOMAIN:
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].setMessage(AjxMessageFormat.format(ZaMsg.CreateDomain_q,[ZaAccount.getDomain(this._containedObject.name)]), DwtMessageDialog.WARNING_STYLE);
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].registerCallback(DwtDialog.YES_BUTTON, this.createDomainAndAccount, this, [ZaAccount.getDomain(this._containedObject.name)]);
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].registerCallback(DwtDialog.NO_BUTTON, ZaController.prototype.closeCnfrmDelDlg, ZaApp.getInstance().getCurrentController(), null);
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].popup();
			break;
			default:
				ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaNewDLXWizard.prototype.finishWizard", null, false);
			break;
		}
	}
}

ZaNewDLXWizard.prototype.goNext =
function() {
	if (this._containedObject[ZaModel.currentStep] == 1) {
		
		//check if account exists
        if (ZaSearch.isAccountExist.call(this, {name: this._containedObject[ZaAccount.A_name], popupError: true})) {
            return false ;
        }
        this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	}
	this.goPage(this._containedObject[ZaModel.currentStep] + 1);
	if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	}
}

ZaNewDLXWizard.prototype.goPrev =
function() {
	if (this._containedObject[ZaModel.currentStep] == 2) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	} else if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	}
	this.goPage(this._containedObject[ZaModel.currentStep] - 1);
}






ZaNewDLXWizard.nonmemberSelectionListener =
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


ZaNewDLXWizard.directMemberSelectionListener =
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

ZaNewDLXWizard.indirectMemberSelectionListener =
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
ZaNewDLXWizard.removeAllMembers = function(event) {
	var form = this.getForm();
	var tmpCurrentRemoveList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_removeList);
	var tmpCurrentMemberList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList);
	var tmpCurrentAddList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList);
        var removeExistedList = [];
        for(var i = 0; i < tmpCurrentMemberList.length; i++) {
                var removedItem = tmpCurrentMemberList[i];
                if(!tmpCurrentAddList || tmpCurrentAddList.length == 0 || AjxUtil.indexOf(tmpCurrentAddList,removedItem,false) < 0)
                        removeExistedList.push(removedItem);
        }

	var newRemoveList = AjxUtil.mergeArrays(tmpCurrentRemoveList,removeExistedList);
	newRemoveList._version = tmpCurrentRemoveList._version+1;

	this.setInstanceValue([], ZaDistributionList.A2_addList);
	this.setInstanceValue([], ZaDistributionList.A2_memberList);
	this.setInstanceValue(newRemoveList, ZaDistributionList.A2_removeList);

	this.getForm().parent.setDirty(true);
};

/**
* method of an XFormItem
**/
ZaNewDLXWizard.removeMembers = function(event) {
	var form = this.getForm();

	var tmpCurrentMemberList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList);
	var tmpCurrentAddList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList);
	var tmpSelectedList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_membersSelected);
	var tmpCurrentRemoveList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_removeList);
	var tmpOrigList = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_origList);

	var newMemberList = AjxUtil.arraySubstract(tmpCurrentMemberList, form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_membersSelected));
	newMemberList._version = tmpCurrentMemberList._version + 1;
	this.setInstanceValue(newMemberList, ZaDistributionList.A2_memberList);


	var newAddList = AjxUtil.arraySubstract(tmpCurrentAddList,form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_membersSelected));
	newAddList._version = tmpCurrentAddList._version + 1;
	this.setInstanceValue(newAddList, ZaDistributionList.A2_addList);

        var removeExistedList = [];
        for(var i = 0; i < tmpSelectedList.length; i++) {
                var removedItem = tmpSelectedList[i];
                if(!tmpCurrentAddList || tmpCurrentAddList.length == 0 ||AjxUtil.indexOf(tmpCurrentAddList,removedItem,false) < 0) {
			if(tmpOrigList && tmpOrigList.length > 0 && AjxUtil.indexOf(tmpOrigList, removedItem, false) >= 0)
				removeExistedList.push(removedItem);
		}
        }

	var newRemoveList = AjxUtil.mergeArrays(tmpCurrentRemoveList,removeExistedList);
	newRemoveList._version = tmpCurrentRemoveList._version+1;
	this.setInstanceValue(newRemoveList, ZaDistributionList.A2_removeList);

	this.getForm().parent.setDirty(true);
};


/**
* method of the XForm
**/
ZaNewDLXWizard.getMemberSelection =
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
ZaNewDLXWizard.shouldEnableMemberListButtons = function() {
	return (ZaNewDLXWizard.getMemberSelection.call(this).length>0);
};

/**
* method of the XForm
**/
ZaNewDLXWizard.shouldEnableRemoveAllButton = function() {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaDistributionList.A2_memberList)));
};

/**
* method of the XForm
**/
ZaNewDLXWizard.shouldEnableAddAllButton = function() {
	var list = this.getItemsById("memberPool")[0].widget.getList();
	if (list != null) {
		return ( list.size() > 0);
	}
	return false;
};

/**
* method of the XForm
**/
ZaNewDLXWizard.getMemberPoolSelection =
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
ZaNewDLXWizard.shouldEnableMemberPoolListButtons = function() {
	return (ZaNewDLXWizard.getMemberPoolSelection.call(this).length>0);
};

/**
* method of the XForm
**/
ZaNewDLXWizard.shouldEnableFreeFormButtons = function () {
	var optionalAdd = this.getInstance().optionalAdd;
	return (optionalAdd && optionalAdd.length > 0);
};



/**
 * method of an XFormItem
 * Currently, this manages the data, redraws the whole list, and then sets
 * the selection.
 * TODO - change the routine to add only the necessary rows to the list view.
 * Same is true of addAllAddresses
 */
ZaNewDLXWizard.addAddressesToMembers = function (event) {
 	var form = this.getForm();
	//Don't allow add self as member
	var selectedAddArray = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberPoolSelected);
	var newSelectedArray = [];
	for(var i = 0; i < selectedAddArray.length; i++) {
		var selectedItem = selectedAddArray[i];
		if(selectedItem.name != form.getInstance().name)
			newSelectedArray.push(selectedItem);
	}

	var tmpAddArray = AjxUtil.mergeArrays(form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList),
		newSelectedArray,ZaDistributionList.compareTwoMembers);

	tmpAddArray._version = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList)._version + 1;

	var tmpMembersArray = AjxUtil.mergeArrays(form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList),
		newSelectedArray,ZaDistributionList.compareTwoMembers);

	tmpMembersArray._version = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList)._version + 1;

	this.setInstanceValue(tmpAddArray, ZaDistributionList.A2_addList);
	this.setInstanceValue(tmpMembersArray, ZaDistributionList.A2_memberList);

	this.getForm().parent.setDirty(true);
};

/**
 * method of an XFormItem
**/
ZaNewDLXWizard.addAllAddressesToMembers = function (event) {
	var form = this.getForm();
	//Don't allow add self as member
        var selectedAddArray = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberPool);
        var newSelectedArray = [];
        for(var i = 0; i < selectedAddArray.length; i++) {
                var selectedItem = selectedAddArray[i];
                if(selectedItem.name != form.getInstance().name)
                        newSelectedArray.push(selectedItem);
        }
	var tmpAddArray = AjxUtil.mergeArrays(form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList),
		newSelectedArray,ZaDistributionList.compareTwoMembers);

	tmpAddArray._version = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_addList)._version + 1;

	var tmpMembersArray = AjxUtil.mergeArrays(form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList),
		newSelectedArray,ZaDistributionList.compareTwoMembers);

	tmpMembersArray._version = form.getModel().getInstanceValue(form.getInstance(),ZaDistributionList.A2_memberList)._version + 1;

	this.setInstanceValue(tmpAddArray, ZaDistributionList.A2_addList);
	this.setInstanceValue(tmpMembersArray, ZaDistributionList.A2_memberList);
	this.getForm().parent.setDirty(true);
};


ZaNewDLXWizard.prototype.setObject =
function (entry) {
	this._containedObject = new ZaDistributionList();
	this._containedObject.attrs = new Object();
	this._containedObject[ZaDistributionList.A2_memberList] = new Array();
	this._containedObject[ZaDistributionList.A2_memberList]._version = 1;


    if(entry._uuid) {
        this._containedObject._uuid = entry._uuid;
    }

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
	this._containedObject[ZaDistributionList.A2_addList] = entry[ZaDistributionList.A2_addList]||new Array(); //members to add
	this._containedObject[ZaDistributionList.A2_addList]._version = 1;
	this._containedObject[ZaDistributionList.A2_removeList] = entry[ZaDistributionList.A2_removeList]||new Array(); //members to remove
	this._containedObject[ZaDistributionList.A2_removeList]._version = 1;
	this._containedObject[ZaDistributionList.A2_poolPagenum] = entry[ZaDistributionList.A2_poolPagenum]||1;
	this._containedObject[ZaDistributionList.A2_poolNumPages] = entry [ZaDistributionList.A2_poolNumPages];
	this._containedObject[ZaDistributionList.A2_memPagenum] =entry[ZaDistributionList.A2_memPagenum]|| 1;
	this._containedObject[ZaDistributionList.A2_memNumPages] = entry [ZaDistributionList.A2_memNumPages];
	this._containedObject[ZaDistributionList.A2_query] =entry[ZaDistributionList.A2_query]|| "";
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
    this._containedObject[ZaModel.currentStep] = entry[ZaModel.currentStep] || 1;
	//dl.isgroup = this.isgroup ;

	//new ZaDistributionList don't have members, so here two are empty list
	this._containedObject[ZaDistributionList.A2_allMemberHash] = new Array();
	this._containedObject[ZaDistributionList.A2_allMemberPages] = new Array();


	if(entry.rights)
		this._containedObject.rights = entry.rights;

	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;

	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;

	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;

    this._containedObject[ZaDistributionList.A2_DLOwners] = new Array();
    if (entry[ZaDistributionList.A2_DLOwners])
        this._containedObject[ZaDistributionList.A2_DLOwners] = ZaItem.deepCloneListItem(entry[ZaDistributionList.A2_DLOwners]);

	this._containedObject.name = entry.name;
        if(entry.name == ""){this._containedObject.name = ZaMsg.TBB_New;}
	this._containedObject.type = entry.type;
	this._containedObject.id = entry.id;
    this._containedObject[ZaAccount.A2_autoMailServer] = entry[ZaAccount.A2_autoMailServer];

    if(entry[ZaDistributionList.A2_dlType])
        this._containedObject[ZaDistributionList.A2_dlType] = entry[ZaDistributionList.A2_dlType];

	if(!entry.id) {
		if(ZaItem.hasWritePermission(ZaAccount.A_zimbraIsDelegatedAdminAccount,entry)) {
			this._containedObject.attrs[ZaDistributionList.A_mailStatus] = "enabled";
		}
	}
        //this.modifyContainedObject () ;
	this._localXForm.setInstance(this._containedObject);
}

ZaNewDLXWizard.prototype.srchResWithoutSelf =
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

ZaNewDLXWizard.prototype.searchAccounts =
function (orderby, isascending) {
	try {
		orderby = (orderby !=null) ? orderby : ZaAccount.A_name;
		var types = [ZaSearch.ACCOUNTS,ZaSearch.DLS,ZaSearch.ALIASES] ;
		var  searchQueryHolder = new ZaSearchQuery(ZaSearch.getSearchByNameQuery(this._containedObject["query"], types,true),
                                types , false, "",null,10);
		var result = ZaSearch.searchByQueryHolder(searchQueryHolder, this._containedObject["poolPagenum"], orderby, isascending);
		if(result.list) {
			this._containedObject.memberPool = this.srchResWithoutSelf(result.list, this._containedObject[ZaAccount.A_name]);
		}
		this._containedObject.poolNumPages = result.numPages;
		this._localXForm.setInstance(this._containedObject);

	} catch (ex) {
		// Only restart on error if we are not initialized and it isn't a parse error
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
//			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaNewDLXWizard.prototype.searchAccounts", null, (this._inited) ? false : true);
			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaNewDLXWizard.prototype.searchAccounts", null, false );
		} else {
			this.popupErrorDialog(ZaMsg.queryParseError, ex);
			this._searchField.setEnabled(true);
		}
	}
}

ZaNewDLXWizard.aliasSelectionListener =
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_alias_selection_cache, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_alias_selection_cache, null);
	}
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaNewDLXWizard.editAliasButtonListener.call(this);
	}
}

ZaNewDLXWizard.deleteAliasButtonListener = function () {
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

ZaNewDLXWizard.editAliasButtonListener =
function () {
	var instance = this.getInstance();
	if(instance.alias_selection_cache && instance.alias_selection_cache[0]) {
		var formPage = this.getForm().parent; if(!formPage.editAliasDlg) {
			formPage.editAliasDlg = new ZaEditAliasXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px",ZaMsg.Edit_Alias_Title);
			formPage.editAliasDlg.registerCallback(DwtDialog.OK_BUTTON, ZaNewDLXWizard.updateAlias, this.getForm(), null);
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

ZaNewDLXWizard.updateAlias = function () {
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

ZaNewDLXWizard.addAliasButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addAliasDlg) {
		formPage.addAliasDlg = new ZaEditAliasXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px",ZaMsg.Add_Alias_Title);
		formPage.addAliasDlg.registerCallback(DwtDialog.OK_BUTTON, ZaNewDLXWizard.addAlias, this.getForm(), null);
	}

	var obj = {};
	obj[ZaAccount.A_name] = "";
	obj[ZaAlias.A_index] = -1;
	formPage.addAliasDlg.setObject(obj);
	formPage.addAliasDlg.popup();
}

ZaNewDLXWizard.addAlias  = function () {
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

ZaNewDLXWizard.isEditAliasEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaDistributionList.A2_alias_selection_cache)) && this.getInstanceValue(ZaAccount.A2_alias_selection_cache).length==1);
}

ZaNewDLXWizard.isDeleteAliasEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaDistributionList.A2_alias_selection_cache)));
}


ZaNewDLXWizard.NOTES_TAB_ATTRS = [ZaAccount.A_notes];
ZaNewDLXWizard.NOTES_TAB_RIGHTS = [];

ZaNewDLXWizard.MEMBEROF_TAB_ATTRS = [];
ZaNewDLXWizard.MEMBEROF_TAB_RIGHTS = [ZaDistributionList.GET_DL_MEMBERSHIP_RIGHT];

ZaNewDLXWizard.ALIASES_TAB_ATTRS = [ZaAccount.A_zimbraMailAlias];
ZaNewDLXWizard.ALIASES_TAB_RIGHTS = [ZaDistributionList.ADD_DL_ALIAS_RIGHT,ZaDistributionList.REMOVE_DL_ALIAS_RIGHT];

ZaNewDLXWizard.OWNER_TAB_ATTRS = [];
ZaNewDLXWizard.OWNER_TAB_RIGHTS = [];

ZaNewDLXWizard.PREF_TAB_ATTRS = [ZaDistributionList.A_zimbraPrefReplyToEnabled, ZaDistributionList.A_zimbraPrefReplyToDisplay,
    ZaDistributionList.A_zimbraPrefReplyToAddress];
ZaNewDLXWizard.PREF_TAB_RIGHTS = [];

ZaNewDLXWizard.myXFormModifier = function(xFormObject, entry) {
	var sourceHeaderList = new Array();
	var sortable=1;
	sourceHeaderList[0] = new ZaListHeaderItem("type", ZaMsg.ALV_Type_col, null, null, null, "objectClass", true, true);
	sourceHeaderList[1] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, null, sortable++, ZaAccount.A_name, true, true);
	//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	sourceHeaderList[2] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, null, sortable++,ZaAccount.A_displayname, true, true);
	//sourceHeaderList[3] = new ZaListHeaderItem(null, null, null, "10px", null, null, false, true);
	var membersHeaderList = new Array();
	membersHeaderList[0] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, "100%", sortable++, ZaAccount.A_name, true, true);

	var directMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.DIRECT);
	var indirectMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.INDIRECT);
	var nonMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.NON);

    this.TAB_INDEX = 0;
    this.stepChoices = [];

	var _tab1, _tab2, _tab3, _tab4, _tab5, _tab6;
	_tab1 = ++this.TAB_INDEX;
	this.stepChoices.push({value:_tab1, label:ZaMsg.DLXV_TabMembers});

	if(ZaTabView.isTAB_ENABLED(entry,ZaNewDLXWizard.NOTES_TAB_ATTRS, ZaNewDLXWizard.NOTES_TAB_RIGHTS)) {
		_tab2 = ++this.TAB_INDEX;
		this.stepChoices.push({value:_tab2, label:ZaMsg.DLXV_TabProperties});
	}

	if(ZaTabView.isTAB_ENABLED(entry,ZaNewDLXWizard.MEMBEROF_TAB_ATTRS, ZaNewDLXWizard.MEMBEROF_TAB_RIGHTS)) {
		_tab3 = ++this.TAB_INDEX;
		this.stepChoices.push({value:_tab3, label:ZaMsg.TABT_MemberOf});
	}

	if(ZaTabView.isTAB_ENABLED(entry,ZaNewDLXWizard.ALIASES_TAB_ATTRS, ZaNewDLXWizard.ALIASES_TAB_RIGHTS)) {
		_tab4 = ++this.TAB_INDEX;
		this.stepChoices.push({value:_tab4, label:ZaMsg.TABT_Aliases});
	}

	if(ZaTabView.isTAB_ENABLED(entry,ZaNewDLXWizard.OWNER_TAB_ATTRS, ZaNewDLXWizard.OWNER_TAB_RIGHTS)) {
		_tab5 = ++this.TAB_INDEX;
		this.stepChoices.push({value:_tab5, label:ZaMsg.TABT_Owners});
	}

	if(ZaTabView.isTAB_ENABLED(entry,ZaNewDLXWizard.PREF_TAB_ATTRS, ZaNewDLXWizard.PREF_TAB_RIGHTS)) {
		_tab6 = ++this.TAB_INDEX;
		this.stepChoices.push({value:_tab6, label:ZaMsg.TABT_Preferences});
	}

	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	xFormObject.numCols=5;
	xFormObject.colSizes = [10,"auto", 20, "auto", 10];
	xFormObject.itemDefaults = {
			_INPUT_: { cssClass:"inputBorder" },
			_TEXTAREA_: {cssClass: "inputBorder"},
			_TEXTFIELD_: {cssClass: "inputBorder", containerCssStyle:"width:95%"}
	    };

	var cases = [];

	var hMsgMemberNum, hMemberList, wMemberList, hMemberPool, wMemberPool, wRightPanel;

    hMsgMemberNum = 30;
    hMemberList = 200;
    wMemberList = "98%";
    hMemberPool = 250;
    wMemberPool = "98%";

    wRightPanel = "100%";

    var case1 = {
        type: _CASE_,
        caseKey: _tab1,
        numCols: 2,
        id: "dl_form_members",
        colSizes: ["320px", "380px"],
        cssStyle: "padding-top:5px; table-layout:fixed;",
        items: [
            {
                type: _GROUP_,
                width: "98%",
                numCols: 1,
                items: [
                    {
                        type: _SPACER_,
                        height: "5"
                    },
                    {
                        type: _GROUP_,
                        width: "96%",
                        id: "dl_form_members_general_group",
                        numCols: 2,
                        colSizes: [100, "*"],
                        items: [
                            {
                                ref: ZaAccount.A_name,
                                type: _EMAILADDR_,
                                msgName: ZaMsg.MSG_LabelListName,
                                label: ZaMsg.LBL_LabelListName,
                                forceUpdate: true,
                                tableCssStyle: "width: 100%",
                                inputWidth: "70px",
                                domainPartWidth: "80px",
                                id: "dl_name_field",
                                nameContainerCss: "width: 70px",
                                domainContainerWidth: "80px",
                                midContainerCss: "width: 20px",
                                visibilityChecks: [],
                                enableDisableChecks: [
                                    [
                                        XFormItem.prototype.hasRight,
                                        ZaDistributionList.RENAME_DL_RIGHT
                                    ]
                                ]
                            },
                            {
                                type: _SPACER_,
                                height: "3"
                            },
                            {
                                ref: ZaAccount.A_displayname,
                                type: _TEXTFIELD_,
                                label: ZaMsg.NAD_DisplayName,
                                msgName: ZaMsg.NAD_DisplayName,
                                align: _LEFT_,
                                cssClass: "admin_xform_name_input",
                                width: "95%"
                            },
                            {
                                type: _SPACER_,
                                height: "3"
                            },
                            {
                                ref: "description",
                                msgName: ZaMsg.NAD_Description,
                                label: ZaMsg.NAD_Description,
                                labelLocation: _LEFT_,
                                align: _LEFT_,
                                type: _TEXTAREA_,
                                enableDisableChecks: [ZaItem.hasWritePermission],
                                visibilityChecks: [ZaItem.hasReadPermission],
                                cssClass: "admin_xform_name_input",
                                cssStyle: "resize: none",
                                width: "100%",
                                height: "100"
                            }
                        ]
                    },
                    {
                        type: _SPACER_,
                        height: "20"
                    },
                    {
                        type: _GROUPER_,
                        borderCssClass: "LeftGrouperBorder",
                        width: "100%",
                        numCols: 1,
                        colSizes: ["auto"],
                        label: ZaMsg.DLXV_LabelListMembers,
                        items: [
                            {
                                type: _GROUP_,
                                width: "100%",
                                height: hMsgMemberNum,
                                numCols: 3,
                                colSizes: [ "*", "20px", "104px"],
                                items: [
                                    {
                                        type: _OUTPUT_,
                                        ref: ZaDistributionList.A2_numMembers,
                                        align: _LEFT_,
                                        valueChangeEventSources: [
                                            ZaDistributionList.A2_numMembers,
                                            ZaDistributionList.A2_memNumPages
                                        ],
                                        getDisplayValue: ZaDLXFormView.showMembersNum
                                    },
                                    {
                                        type: _CELLSPACER_
                                    },
                                    {
                                        type: _GROUP_,
                                        width: "100%",
                                        numCols: 3,
                                        colSizes: ["32px", "40px", "32px"],
                                        visibilityChangeEventSources: [ZaDistributionList.A2_memNumPages],
                                        visibilityChecks: [
                                            [
                                                ZaDLXFormView.isMoreThanOnePage,
                                                ZaDistributionList.A2_memNumPages
                                            ]
                                        ],
                                        items: [
                                            {
                                                type: _DWT_BUTTON_,
                                                label: null,
                                                labelLocation: _NONE_,
                                                width: "100%",
                                                id: "backButton",
                                                icon: "LeftArrow",
                                                disIcon: "LeftArrowDis",
                                                enableDisableChecks: [ZaDLXFormView.shouldEnableMemBackButton],
                                                enableDisableChangeEventSources: [
                                                    ZaDistributionList.A2_memberList,
                                                    ZaDistributionList.A2_memNumPages,
                                                    ZaDistributionList.A2_memPagenum
                                                ],
                                                onActivate: "ZaDLXFormView.backMemButtonHndlr.call(this,event)"
                                            },
                                            {
                                                type: _OUTPUT_,
                                                ref: ZaDistributionList.A2_memPagenum,
                                                valueChangeEventSources: [
                                                    ZaDistributionList.A2_memPagenum,
                                                    ZaDistributionList.A2_memNumPages
                                                ],
                                                align: _CENTER_,
                                                getDisplayValue: ZaDLXFormView.showMembersPaging
                                            },
                                            {
                                                type: _DWT_BUTTON_,
                                                label: null,
                                                labelLocation: _NONE_,
                                                width: "100%",
                                                id: "fwdButton",
                                                icon: "RightArrow",
                                                disIcon: "RightArrowDis",
                                                enableDisableChecks: [ZaDLXFormView.shouldEnableMemForwardButton],
                                                enableDisableChangeEventSources: [
                                                    ZaDistributionList.A2_memberList,
                                                    ZaDistributionList.A2_memNumPages,
                                                    ZaDistributionList.A2_memPagenum
                                                ],
                                                onActivate: "ZaDLXFormView.fwdMemButtonHndlr.call(this,event)"
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                type: _SPACER_,
                                height: "5"
                            },
                            {
                                ref: ZaDistributionList.A2_memberList,
                                type: _DWT_LIST_,
                                height: hMemberList,
                                width: wMemberList,
                                cssClass: "DLTarget",
                                cssStyle: "margin-left: 5px; ",
                                widgetClass: ZaAccMiniListView,
                                headerList: membersHeaderList,
                                hideHeader: true,
                                onSelection: ZaDLXFormView.membersSelectionListener,
                                bmolsnr: true
                            },
                            {
                                type: _SPACER_,
                                height: "8"
                            },
                            {
                                type: _GROUP_,
                                width: "98%",
                                numCols: 3,
                                cssStyle: "table-layout:fixed",
                                items: [
                                    {
                                        type: _DWT_BUTTON_,
                                        id: "removeButton",
                                        label: ZaMsg.DLXV_ButtonRemoveSelected,
                                        width: "140px", //in IE should use it to control width
                                        nowrap: true,
                                        cssStyle: "padding:0px;",
                                        enableDisableChangeEventSources: [ZaDistributionList.A2_membersSelected],
                                        enableDisableChecks: [
                                            [
                                                XForm.checkInstanceValueNotEmty,
                                                ZaDistributionList.A2_membersSelected
                                            ],
                                            [
                                                XFormItem.prototype.hasRight,
                                                ZaDistributionList.REMOVE_DL_MEMBER_RIGHT
                                            ]
                                        ],
                                        onActivate: "ZaDLXFormView.removeSelectedMembers.call(this,event)"
                                    },
                                    {
                                        type: _CELLSPACER_,
                                        width: "10px" //in IE should use it to control width
                                    },
                                    {
                                        type: _DWT_BUTTON_,
                                        label: ZaMsg.DLXV_ButtonRemovePage,
                                        width: "140px", //in IE should use it to control width
                                        nowrap: true,
                                        cssStyle: "padding:0px;",
                                        visibilityChangeEventSources: [ZaDistributionList.A2_numMembers],
                                        visibilityChecks: [
                                            [
                                                XForm.checkInstanceValueNot,
                                                ZaDistributionList.A2_numMembers,
                                                0
                                            ]
                                        ],
                                        enableDisableChangeEventSources: [ZaDistributionList.A2_memberList],
                                        enableDisableChecks: [
                                            ZaDLXFormView.shouldEnableRemoveThisPageButton,
                                            [
                                                XFormItem.prototype.hasRight,
                                                ZaDistributionList.REMOVE_DL_MEMBER_RIGHT
                                            ]
                                        ],
                                        onActivate: "ZaDLXFormView.removeOnePageMembers.call(this,event)"
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        type: _SPACER_,
                        height: "5"
                    }
                ]
            },
            {
                type: _ZARIGHT_GROUPER_,
                numCols: 1,
                width: wRightPanel,
                label: ZaMsg.DLXV_GroupLabelAddMembers,
                items: [
                    {
                        type: _GROUP_,
                        width: "100%",
                        numCols: 3,
                        colSizes: ["64%", "6%", "30%"],
                        items: [
                            {
                                type: _GROUP_,
                                width: "100%",
                                numCols: 2,
                                colSizes: ["20%", "80%"],
                                items: [
                                    {
                                        type: _TEXTFIELD_,
                                        cssClass: "admin_xform_name_input",
                                        ref: ZaSearch.A_query,
                                        label: ZaMsg.DLXV_LabelFind,
                                        labelLocation: _LEFT_,
                                        labelCssStyle: "white-space: nowrap;",
                                        visibilityChecks: [],
                                        enableDisableChecks: [],
                                        width: "95%",
                                        align: _LEFT_,
                                        elementChanged: function(elementValue, instanceValue, event) {
                                            var charCode = event.charCode;
                                            if (charCode == 13 || charCode == 3) {
                                                ZaDLXFormView.srchButtonHndlr.call(this);
                                            } else {
                                                this.getForm().itemChanged(this, elementValue, event);
                                            }
                                        }
                                    }
                                ]
                            },
                            {
                                type: _CELLSPACER_
                            },
                            {
                                type: _DWT_BUTTON_,
                                label: ZaMsg.DLXV_ButtonSearch,
                                onActivate: ZaDLXFormView.srchButtonHndlr
                            }
                        ]
                    },
                    {
                        type: _SPACER_,
                        height: "5",
                        colSpan: "*"
                    },
                    {
                        type: _GROUP_,
                        width: "100%",
                        numCols: 3,
                        colSizes: [ "*", "10px", "104px"],
                        items: [
                            {
                                type: _GROUP_,
                                items: [
                                    {
                                        type: _OUTPUT_,
                                        ref: ZaDistributionList.A2_totalNumInPool,
                                        align: _LEFT_,
                                        visibilityChecks: [
                                            [
                                                ZaDLXFormView.checkTotalNumInPool,
                                                -1
                                            ]
                                        ],
                                        getDisplayValue: ZaDLXFormView.showSearchFoundNum,
                                        nowrap: false
                                    }
                                ]
                            },
                            {
                                type: _CELLSPACER_
                            },
                            {
                                type: _GROUP_,
                                visibilityChangeEventSources: [ZaDistributionList.A2_poolNumPages],
                                visibilityChecks: [
                                    [
                                        ZaDLXFormView.isMoreThanOnePage,
                                        ZaDistributionList.A2_poolNumPages
                                    ]
                                ],
                                numCols: 3,
                                colSizes: ["32px", "40px", "32px"],
                                width: "100%",
                                items: [
                                    {
                                        type: _DWT_BUTTON_,
                                        label: null,
                                        labelLocation: _NONE_,
                                        id: "backButton",
                                        icon: "LeftArrow",
                                        disIcon: "LeftArrowDis",
                                        enableDisableChecks: [ZaDLXFormView.shouldEnablePoolBackButton],
                                        enableDisableChangeEventSources: [ZaDistributionList.A2_poolPagenum],
                                        onActivate: "ZaDLXFormView.backPoolButtonHndlr.call(this,event)"
                                    },
                                    {
                                        type: _OUTPUT_,
                                        ref: ZaDistributionList.A2_poolPagenum,
                                        align: _CENTER_,
                                        getDisplayValue: ZaDLXFormView.showSearchFoundPaging
                                    },
                                    {
                                        type: _DWT_BUTTON_,
                                        label: null,
                                        labelLocation: _NONE_,
                                        id: "fwdButton",
                                        icon: "RightArrow",
                                        disIcon: "RightArrowDis",
                                        enableDisableChecks: [ZaDLXFormView.shouldEnablePoolForwardButton],
                                        enableDisableChangeEventSources: [ZaDistributionList.A2_poolPagenum],
                                        onActivate: "ZaDLXFormView.fwdPoolButtonHndlr.call(this,event)"
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        type: _SPACER_,
                        height: "5"
                    },
                    {
                        ref: ZaDistributionList.A2_memberPool,
                        type: _DWT_LIST_,
                        height: hMemberPool,
                        width:  wMemberPool,
                        cssClass: "DLSource",
                        forceUpdate: true,
                        widgetClass: ZaAccMiniListView,
                        headerList: sourceHeaderList,
                        hideHeader: false,
                        onSelection: ZaDLXFormView.memberPoolSelectionListener
                    },
                    {
                        type: _SPACER_,
                        height: "5",
                        colSpan: "*"
                    },
                    {
                        type: _GROUP_,
                        width: wRightPanel,
                        items: [
                            {
                                type: _GROUP_,
                                width: "98%",
                                numCols: 3,
                                cssStyle: "table-layout:fixed",
                                items: [
                                    {
                                        type: _DWT_BUTTON_,
                                        label: ZaMsg.DLXV_ButtonAddSelected,
                                        width: "140px", //in IE should use it to control width
                                        nowrap: true,
                                        onActivate: "ZaDLXFormView.addSelectedAddressesToMembers.call(this,event)",
                                        enableDisableChangeEventSources: [ZaDistributionList.A2_memberPoolSelected],
                                        enableDisableChecks: [
                                            [
                                                XForm.checkInstanceValueNotEmty,
                                                ZaDistributionList.A2_memberPoolSelected
                                            ],
                                            [
                                                XFormItem.prototype.hasRight,
                                                ZaDistributionList.ADD_DL_MEMBER_RIGHT
                                            ]
                                        ]
                                    },
                                    {
                                        type: _CELLSPACER_,
                                        width: "10px" //in IE should use it to control width
                                    },
                                    {
                                        type: _DWT_BUTTON_,
                                        label: ZaMsg.DLXV_ButtonAddPage,
                                        width: "140px", //in IE should use it to control width
                                        nowrap: true,
                                        onActivate: "ZaDLXFormView.addOnePageAddressesToMembers.call(this,event)",
                                        visibilityChangeEventSources: [ZaDistributionList.A2_totalNumInPool],
                                        visibilityChecks: [
                                            [
                                                ZaDLXFormView.checkTotalNumInPool,
                                                1
                                            ]
                                        ],
                                        enableDisableChangeEventSources: [ZaDistributionList.A2_memberPool],
                                        enableDisableChecks: [
                                            [
                                                XForm.checkInstanceValueNotEmty,
                                                ZaDistributionList.A2_memberPool
                                            ],
                                            [
                                                XFormItem.prototype.hasRight,
                                                ZaDistributionList.ADD_DL_MEMBER_RIGHT
                                            ]
                                        ]
                                    }
                                ]
                            },
                            {
                                type: _SPACER_
                            }
                        ]
                    },
                    {
                        type: _SPACER_,
                        height: "5"
                    },
                    {
                        type: _OUTPUT_,
                        value: ZaMsg.DLXV_GroupLabelEnterAddressBelow,
                        visibilityChecks: [
                            [
                                XFormItem.prototype.hasRight,
                                ZaDistributionList.ADD_DL_MEMBER_RIGHT
                            ]
                        ]
                    },
                    {
                        type: _SPACER_,
                        height: "5"
                    }
                    ,
                    {
                        ref: ZaDistributionList.A2_optionalAdd,
                        type: _TEXTAREA_,
                        cssStyle: "resize: none",
                        width: "98%",
                        height: 98,
                        bmolsnr: true,
                        visibilityChecks: [
                            [
                                XFormItem.prototype.hasRight,
                                ZaDistributionList.ADD_DL_MEMBER_RIGHT
                            ]
                        ],
                        enableDisableChecks: []
                    },
                    {
                        type: _SPACER_,
                        height: "5",
                        visibilityChecks: [
                            [
                                XFormItem.prototype.hasRight,
                                ZaDistributionList.ADD_DL_MEMBER_RIGHT
                            ]
                        ]
                    },
                    {
                        type: _GROUP_,
                        numCols: 2,
                        colSizes: ["20%", "80%"],
                        width: wRightPanel,
                        visibilityChecks: [
                            [
                                XFormItem.prototype.hasRight,
                                ZaDistributionList.ADD_DL_MEMBER_RIGHT
                            ]
                        ],
                        items: [
                            {
                                type: _DWT_BUTTON_,
                                label: ZaMsg.DLXV_ButtonAddFromFreeForm,
                                onActivate: "ZaDLXFormView.addFreeFormAddressToMembers.call(this,event)",
                                enableDisableChecks: [
                                    [
                                        XForm.checkInstanceValueNotEmty,
                                        ZaDistributionList.A2_optionalAdd
                                    ]
                                ],
                                enableDisableChangeEventSources: [ZaDistributionList.A2_optionalAdd]
                            },
                            {
                                type: _OUTPUT_,
                                value: ZaMsg.DLXV_SeparateAddresses
                            }
                        ]
                    }
                ]
            }
        ]
    };

    cases.push(case1);

    if(_tab2) {
        var case2 = {
            type: _CASE_,
            caseKey: _tab2,
            colSpan: "*",
            items: [
                {
                    type: _GROUPER_,
                    label: ZaMsg.DLXV_LabelProperties,
                    id: "dl_properties",
                    colSpan: "*",
                    numCols: 2,
                    colSizes: [175, "*"],
                    visibilityChangeEventSources: [],
                    items: [
                        {
                            ref: "zimbraMailStatus",
                            type: _WIZ_CHECKBOX_,
                            trueValue: "enabled",
                            falseValue: "disabled",
                            align: _LEFT_,
                            nowrap: false,
                            labelWrap: true,
                            label: ZaMsg.DLXV_LabelEnabled,
                            msgName: ZaMsg.DLXV_LabelEnabled,
                            labelLocation: _LEFT_,
                            labelCssClass: "xform_label",
                            cssStyle: "padding-left:0px;margin-bottom-10px;"
                        },
                        {
                            ref: ZaAccount.A_zimbraHideInGal,
                            type: _WIZ_CHECKBOX_,
                            trueValue: "TRUE",
                            falseValue: "FALSE",
                            label: ZaMsg.LBL_zimbraHideInGal,
                            labelLocation: _LEFT_,
                            labelCssClass: "xform_label",
                            labelWrap: true,
                            align: _LEFT_,
                            nowrap: false,
                            msgName: ZaMsg.LBL_zimbraHideInGal,
                            cssStyle: "padding-left: 0px; margin-bottom: 10px;"
                        },
                        {
                            type: _GROUP_,
                            numCols: 3,
                            colSizes: ["25%", "10%", "65%"],
                            nowrap: true,
                            label: ZaMsg.NAD_MailServer,
                            labelLocation: _LEFT_,
                            visibilityChecks: [
                                [
                                    ZaItem.hasWritePermission,
                                    ZaAccount.A_mailHost
                                ]
                            ],
                            items: [
                                {
                                    ref: ZaAccount.A_mailHost,
                                    type: _OSELECT1_,
                                    label: null,
                                    editable: false,
                                    choices: ZaApp.getInstance().getServerListChoices(),
                                    enableDisableChecks: [ZaAccount.isAutoMailServer],
                                    enableDisableChangeEventSources: [ZaAccount.A2_autoMailServer],
                                    visibilityChecks: [],
                                    cssStyle: "margin-left: 5px;"
                                },
                                {
                                    ref: ZaAccount.A2_autoMailServer,
                                    type: _WIZ_CHECKBOX_,
                                    msgName: ZaMsg.NAD_Auto,
                                    label: ZaMsg.NAD_Auto,
                                    trueValue: "TRUE",
                                    falseValue: "FALSE",
                                    visibilityChecks: [],
                                    labelLocation: _RIGHT_,
                                    align: _RIGHT_,
                                    subLabel: "",
                                    enableDisableChecks: []
                                }
                            ]
                        },
                        {
                            type: _GROUP_,
                            colSpan: "*",
                            colSizes: [ "175px", "*"],
                            labelCssClass: "xform_label",
                            cssStyle: "padding-left: 0px",
                            items: [
                                {
                                    ref: ZaDistributionList.A2_dlType,
                                    type: _WIZ_CHECKBOX_,
                                    trueValue: ZaDistributionList.DYNAMIC_DL_TYPE,
                                    falseValue: ZaDistributionList.STATIC_DL_TYPE,
                                    label: ZaMsg.LBL_DL_Type,
									labelLocation: _LEFT_,
                                    labelCssClass: "xform_label",
                                    cssStyle: "margin-bottom-10px;",
                                    labelWrap: true,
                                    align: _LEFT_,
                                    nowrap: false,
                                    msgName: ZaMsg.LBL_DL_Type,
                                    subLabel: "",
                                    visibilityChecks: [],
                                    enableDisableChecks: [],
                                    elementChanged: function (elementValue, instanceValue, event) {
                                        var memberItem = this.getParentItem().items[2];
                                        if (elementValue == ZaDistributionList.STATIC_DL_TYPE) {
                                            if (memberItem.hasError()) {
                                                memberItem.clearError();
                                            }
                                            memberItem.setInstanceValue(null);
                                        } else {
                                            if (this.getInstanceValue(ZaDistributionList.A_zimbraIsACLGroup) != "TRUE") {
                                                memberItem.setError(AjxMsg.valueIsRequired);
                                            }
                                        }
                                        this.getForm().itemChanged(this, elementValue, event);
                                    }
                                },
                                {
                                    ref: ZaDistributionList.A_zimbraIsACLGroup,
                                    type: _WIZ_CHECKBOX_,
                                    trueValue: "TRUE",
                                    falseValue: "FALSE",
                                    label: ZaMsg.LBL_ACL_Group,
                                    labelLocation: _LEFT_,
                                    labelCssClass: "xform_label",
                                    align: _LEFT_,
                                    subLabel: "",
                                    visibilityChangeEventSources: [ZaDistributionList.A2_dlType],
                                    visibilityChecks: [ZaDLXFormView.isDynamicDL],
                                    enableDisableChecks: [],
                                    elementChanged: function (elementValue, instanceValue, event) {
                                        var memberItem = this.getParentItem().items[2];
                                        if (elementValue == "TRUE") {
                                            if (memberItem.hasError()) {
                                                memberItem.clearError();
                                            }
                                            memberItem.setInstanceValue(null);
                                        } else {
                                            memberItem.setError(AjxMsg.valueIsRequired);
                                        }
                                        this.getForm().itemChanged(this, elementValue, event);
                                    }
                                },
                                {
                                    type: _INPUT_,
                                    ref: ZaDistributionList.A_memberOfURL,
                                    label: ZaMsg.LBL_Member_URL,
                                    labelLocation: _LEFT_,
                                    labelCssClass: "xform_label",
                                    cssStyle: "margin-left: 5px;",
                                    width: "80%",
                                    bmolsnr: true,
                                    visibilityChangeEventSources: [ZaDistributionList.A2_dlType],
                                    visibilityChecks: [ZaDLXFormView.isDynamicDL],
                                    enableDisableChangeEventSources: [ZaDistributionList.A_zimbraIsACLGroup],
                                    enableDisableChecks: [ZaDLXFormView.isNotACLGroup]
                                }
                            ]
                        },
                        {
                            type: _SPACER_,
                            height: "5"
                        },
                        {
                            type: _GROUP_,
                            numCols: 2,
                            colSizes: [ "5%", "*"],
                            label: ZaMsg.DLXV_SubscriptionRequest,
                            labelLocation: _LEFT_,
                            items: [
                                {
                                    ref: ZaDistributionList.A_zimbraDistributionListSubscriptionPolicy,
                                    type: _RADIO_,
                                    groupname: "subscription_settings",
                                    msgName: ZaMsg.DLXV_SubscriptionPolicyAccept,
                                    label: ZaMsg.DLXV_SubscriptionPolicyAccept,
                                    onChange: ZaTabView.onFormFieldChanged,
                                    updateElement: function () {
                                        this.getElement().checked = (this.getInstanceValue(ZaDistributionList.A_zimbraDistributionListSubscriptionPolicy) == ZaDistributionList.A2_zimbraDLSubscriptionPolicyAccept);
                                    },
                                    elementChanged: function(elementValue,instanceValue, event) {
                                        this.getForm().itemChanged(
                                            this,
                                            ZaDistributionList.A2_zimbraDLSubscriptionPolicyAccept,
                                            event
                                        );
                                    }
                                },
                                {
                                    ref: ZaDistributionList.A_zimbraDistributionListSubscriptionPolicy,
                                    type: _RADIO_,
                                    groupname: "subscription_settings",
                                    msgName: ZaMsg.DLXV_SubscriptionPolicyApproval,
                                    label: ZaMsg.DLXV_SubscriptionPolicyApproval,
                                    onChange: ZaTabView.onFormFieldChanged,
                                    updateElement: function () {
                                        this.getElement().checked = (this.getInstanceValue(ZaDistributionList.A_zimbraDistributionListSubscriptionPolicy) == ZaDistributionList.A2_zimbraDLSubscriptionPolicyApproval);
                                    },
                                    elementChanged: function(elementValue,instanceValue, event) {
                                        this.getForm().itemChanged(
                                            this,
                                            ZaDistributionList.A2_zimbraDLSubscriptionPolicyApproval,
                                            event
                                        );
                                    }
                                },
                                {
                                    ref: ZaDistributionList.A_zimbraDistributionListSubscriptionPolicy,
                                    type: _RADIO_,
                                    groupname: "subscription_settings",
                                    msgName: ZaMsg.DLXV_SubscriptionPolicyReject,
                                    label: ZaMsg.DLXV_SubscriptionPolicyReject,
                                    onChange: ZaTabView.onFormFieldChanged,
                                    updateElement: function () {
                                        this.getElement().checked = (this.getInstanceValue(ZaDistributionList.A_zimbraDistributionListSubscriptionPolicy) == ZaDistributionList.A2_zimbraDLSubscriptionPolicyReject);
                                    },
                                    elementChanged: function(elementValue,instanceValue, event) {
                                        this.getForm().itemChanged(
                                            this,
                                            ZaDistributionList.A2_zimbraDLSubscriptionPolicyReject,
                                            event
                                        );
                                    }
                                }
                            ]
                        },
                        {
                            type: _SPACER_,
                            height: "5"
                        },
                        {
                            type: _GROUP_,
                            numCols: 2,
                            colSizes: [ "5%", "*"],
                            label: ZaMsg.DLXV_UnsubscriptionRequest,
                            labelLocation: _LEFT_,
                            items: [
                                {
                                    ref: ZaDistributionList.A_zimbraDistributionListUnsubscriptionPolicy,
                                    type: _RADIO_,
                                    groupname: "unsubscription_settings",
                                    msgName: ZaMsg.DLXV_SubscriptionPolicyAccept,
                                    label: ZaMsg.DLXV_SubscriptionPolicyAccept,
                                    onChange: ZaTabView.onFormFieldChanged,
                                    updateElement: function () {
                                        this.getElement().checked = (this.getInstanceValue(ZaDistributionList.A_zimbraDistributionListUnsubscriptionPolicy) == ZaDistributionList.A2_zimbraDLSubscriptionPolicyAccept);
                                    },
                                    elementChanged: function (elementValue,instanceValue, event) {
                                        this.getForm().itemChanged(
                                            this,
                                            ZaDistributionList.A2_zimbraDLSubscriptionPolicyAccept,
                                            event
                                        );
                                    }
                                },
                                {
                                    ref: ZaDistributionList.A_zimbraDistributionListUnsubscriptionPolicy,
                                    type: _RADIO_,
                                    groupname: "unsubscription_settings",
                                    msgName: ZaMsg.DLXV_SubscriptionPolicyApproval,
                                    label: ZaMsg.DLXV_SubscriptionPolicyApproval,
                                    onChange: ZaTabView.onFormFieldChanged,
                                    updateElement: function () {
                                        this.getElement().checked = (this.getInstanceValue(ZaDistributionList.A_zimbraDistributionListUnsubscriptionPolicy) == ZaDistributionList.A2_zimbraDLSubscriptionPolicyApproval);
                                    },
                                    elementChanged: function (elementValue,instanceValue, event) {
                                        this.getForm().itemChanged(
                                            this,
                                            ZaDistributionList.A2_zimbraDLSubscriptionPolicyApproval,
                                            event
                                        );
                                    }
                                },
                                {
                                    ref: ZaDistributionList.A_zimbraDistributionListUnsubscriptionPolicy,
                                    type: _RADIO_,
                                    groupname: "unsubscription_settings",
                                    msgName: ZaMsg.DLXV_SubscriptionPolicyReject,
                                    label: ZaMsg.DLXV_SubscriptionPolicyReject,
                                    onChange: ZaTabView.onFormFieldChanged,
                                    updateElement: function () {
                                        this.getElement().checked = (this.getInstanceValue(ZaDistributionList.A_zimbraDistributionListUnsubscriptionPolicy) == ZaDistributionList.A2_zimbraDLSubscriptionPolicyReject);
                                    },
                                    elementChanged: function (elementValue,instanceValue, event) {
                                        this.getForm().itemChanged(
                                            this,
                                            ZaDistributionList.A2_zimbraDLSubscriptionPolicyReject,
                                            event
                                        );
                                    }
                                }
                            ]
                        },
                        {
                            type: _SPACER_,
                            height: "5"
                        },
                        {
                            ref: ZaAccount.A_notes,
                            type: _TEXTAREA_,
                            label: ZaMsg.DLXV_LabelNotes,
                            labelLocation: _LEFT_,
                            width: "400",
                            height: "100",
                            labelCssStyle: "vertical-align: top",
                            cssStyle: "margin-left: 5px; resize: none;"
                        }
                    ]
                }
            ]
        };

        cases.push(case2);
    }

    if(_tab3) {
        var case3 = {
            type: _CASE_,
            caseKey: _tab3,
            numCols: 2,
            colSpan: "*",
            colSizes: [400, 400],
            items: [
                {
                    type: _DWT_ALERT_,
                    containerCssStyle: "padding-bottom:0;",
                    style: DwtAlert.INFO,
                    colSpan: "*",
                    iconVisible: false,
                    content: ZaMsg.WARNING_DYNAMIC_DL_MEMBER,
                    visibilityChecks: [
                        [
                            XForm.checkInstanceValue,
                            ZaDistributionList.A2_dlType,
                            ZaDistributionList.DYNAMIC_DL_TYPE
                        ]
                    ],
                    visibilityChangeEventSources: [ZaDistributionList.A2_dlType]
                },
                {
                    type: _GROUP_,
                    width: "98%",
                    numCols: 1,
                    visibilityChecks: [
                        [
                            XForm.checkInstanceValue,
                            ZaDistributionList.A2_dlType,
                            ZaDistributionList.STATIC_DL_TYPE
                        ]
                    ],
                    visibilityChangeEventSources: [ZaDistributionList.A2_dlType],
                    items: [
                        {
                            type: _SPACER_,
                            height: "5"
                        },
                        {
                            type: _ZALEFT_GROUPER_,
                            numCols: 1,
                            label: ZaMsg.Account_DirectGroupLabel,
                            containerCssStyle: "padding-top: 5px",
                            width: "100%",
                            items: [
                                {
                                    ref: ZaAccount.A2_directMemberList,
                                    type: _S_DWT_LIST_,
                                    width: "98%",
                                    height: 200,
                                    cssClass: "DLSource",
                                    widgetClass: ZaAccountMemberOfListView,
                                    headerList: directMemberOfHeaderList,
                                    defaultColumnSortable: 0,
                                    onSelection: ZaNewDLXWizard.directMemberSelectionListener,
                                    forceUpdate: true
                                },
                                {
                                    type: _SPACER_,
                                    height: "5"
                                },
                                {
                                    type: _GROUP_,
                                    width: "100%",
                                    numCols: 8,
                                    colSizes: [70, 5, 65, 5, 75, 5, 60, 5],
                                    items: [
                                        {
                                            type: _DWT_BUTTON_,
                                            label: ZaMsg.DLXV_ButtonRemoveAll,
                                            enableDisableChangeEventSources: [ZaDistributionList.A2_directMemberList],
                                            enableDisableChecks: [
                                                [
                                                    XForm.checkInstanceValueNotEmty,
                                                    ZaDistributionList.A2_directMemberList
                                                ]
                                            ],
                                            onActivate: "ZaAccountMemberOfListView.removeAllGroups.call(this,event, ZaAccount.A2_directMemberList)"
                                        },
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _DWT_BUTTON_,
                                            label: ZaMsg.DLXV_ButtonRemove,
                                            id: "removeButton",
                                            onActivate: "ZaAccountMemberOfListView.removeGroups.call(this,event, ZaAccount.A2_directMemberList)",
                                            enableDisableChangeEventSources: [ZaDistributionList.A2_directMemberSelected],
                                            enableDisableChecks: [
                                                [
                                                    XForm.checkInstanceValueNotEmty,
                                                    ZaDistributionList.A2_directMemberSelected
                                                ]
                                            ]
                                        },
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _DWT_BUTTON_,
                                            label: ZaMsg.Previous,
                                            id: "backButton",
                                            icon: "LeftArrow",
                                            disIcon: "LeftArrowDis",
                                            onActivate: "ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)",
                                            enableDisableChecks: [
                                                [
                                                    ZaAccountMemberOfListView.shouldEnableBackButton,
                                                    ZaAccount.A2_directMemberList
                                                ]
                                            ],
                                            enableDisableChangeEventSources: [ZaAccount.A2_directMemberList + "_offset"]
                                        },
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _DWT_BUTTON_,
                                            label: ZaMsg.Next,
                                            id: "fwdButton",
                                            icon: "RightArrow",
                                            disIcon: "RightArrowDis",
                                            onActivate: "ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)",
                                            enableDisableChangeEventSources: [ZaAccount.A2_directMemberList + "_more"],
                                            enableDisableChecks: [
                                                [
                                                    ZaAccountMemberOfListView.shouldEnableForwardButton,
                                                    ZaAccount.A2_directMemberList
                                                ]
                                            ]
                                        },
                                        {
                                            type: _CELLSPACER_
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            type: _SPACER_,
                            height: "10"
                        },
                        {
                            type: _ZALEFT_GROUPER_,
                            numCols: 1,
                            width: "100%",
                            label: ZaMsg.Account_IndirectGroupLabel,
                            containerCssStyle: "padding-top: 5px",
                            items: [
                                {
                                    ref: ZaAccount.A2_indirectMemberList,
                                    type: _S_DWT_LIST_,
                                    width: "98%",
                                    height: 200,
                                    cssClass: "DLSource",
                                    widgetClass: ZaAccountMemberOfListView,
                                    headerList: indirectMemberOfHeaderList,
                                    defaultColumnSortable: 0,
                                    onSelection: ZaNewDLXWizard.indirectMemberSelectionListener,
                                    forceUpdate: true
                                },
                                {
                                    type: _SPACER_,
                                    height: "5"
                                },
                                {
                                    type: _GROUP_,
                                    width: "100%",
                                    numCols: 8,
                                    colSizes: [70, 5, 65, 5, 75, 5, 60, 5],
                                    items: [
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _DWT_BUTTON_,
                                            label: ZaMsg.Previous,
                                            id: "indirectBackButton",
                                            icon: "LeftArrow",
                                            disIcon: "LeftArrowDis",
                                            onActivate: "ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)",
                                            enableDisableChecks: [
                                                [
                                                    ZaAccountMemberOfListView.shouldEnableBackButton,
                                                    ZaAccount.A2_indirectMemberList
                                                ]
                                            ],
                                            enableDisableChangeEventSources: [ZaAccount.A2_indirectMemberList+"_offset"]
                                        },
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _DWT_BUTTON_,
                                            label: ZaMsg.Next,
                                            id: "indirectFwdButton",
                                            icon: "RightArrow",
                                            disIcon: "RightArrowDis",
                                            onActivate: "ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)",
                                            enableDisableChecks: [
                                                [
                                                    ZaAccountMemberOfListView.shouldEnableForwardButton,
                                                    ZaAccount.A2_indirectMemberList
                                                ]
                                            ],
                                            enableDisableChangeEventSources: [ZaAccount.A2_indirectMemberList+"_more"]
                                        },
                                        {
                                            type: _CELLSPACER_
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    type: _GROUP_,
                    width: "98%",
                    numCols: 1,
                    visibilityChecks: [
                        [
                            XForm.checkInstanceValue,
                            ZaDistributionList.A2_dlType,
                            ZaDistributionList.STATIC_DL_TYPE
                        ]
                    ],
                    visibilityChangeEventSources: [ZaDistributionList.A2_dlType],
                    items: [
                        {
                            type: _ZARIGHT_GROUPER_,
                            numCols: 1,
                            width: "100%",
                            label: ZaMsg.Account_NonGroupLabel,
                            containerCssStyle: "padding-top: 5px",
                            items: [
                                {
                                    type: _GROUP_,
                                    width: "100%",
                                    numCols: 3,
                                    colSizes: ["64%", "6%", "30%"],
                                    items: [
                                        {
                                            type: _GROUP_,
                                            width: "100%",
                                            numCols: 2,
                                            colSizes: ["20%", "80%"],
                                            items: [
                                                {
                                                    ref: "query",
                                                    type: _TEXTFIELD_,
                                                    width: "95%",
                                                    cssClass: "admin_xform_name_input",
                                                    nowrap: false,
                                                    labelWrap: true,
                                                    label: ZaMsg.DLXV_LabelFind,
                                                    visibilityChecks: [],
                                                    enableDisableChecks: [],
                                                    elementChanged: function (elementValue,instanceValue, event) {
                                                        var charCode = event.charCode;
                                                        if (charCode == 13 || charCode == 3) {
                                                            ZaAccountMemberOfListView.prototype.srchButtonHndlr.call(this);
                                                        } else {
                                                            this.getForm().itemChanged(this, elementValue, event);
                                                        }
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _DWT_BUTTON_,
                                            label: ZaMsg.DLXV_ButtonSearch,
                                            onActivate: ZaAccountMemberOfListView.prototype.srchButtonHndlr
                                        }
                                    ]
                                },
                                {
                                    type: _GROUP_,
                                    width: "100%",
                                    numCols: 2,
                                    items: [
                                        {
                                            ref: ZaAccount.A2_showSameDomain,
                                            type: _WIZ_CHECKBOX_,
                                            label: ZaMsg.NAD_SearchSameDomain,
                                            labelLocation: _RIGHT_,
                                            trueValue: "TRUE",
                                            falseValue: "FALSE",
                                            visibilityChecks: []
                                        }
                                    ]
                                },
                                {
                                    type: _SPACER_,
                                    height: "5"
                                },
                                {
                                    ref: ZaAccount.A2_nonMemberList,
                                    type: _S_DWT_LIST_,
                                    width: "98%",
                                    height: 440,
                                    cssClass: "DLSource",
                                    widgetClass: ZaAccountMemberOfListView,
                                    headerList: nonMemberOfHeaderList,
                                    defaultColumnSortable: 0,
                                    onSelection: ZaNewDLXWizard.nonmemberSelectionListener,
                                    forceUpdate: true
                                },
                                {
                                    type: _SPACER_,
                                    height: "5"
                                },
                                {
                                    type: _GROUP_,
                                    width: "100%",
                                    numCols: 8,
                                    colSizes: [70, 5, 65, 5, 75, 5, 60, 5],
                                    items: [
                                        {
                                            type: _DWT_BUTTON_,
                                            label: ZaMsg.DLXV_ButtonAddFromList,
                                            onActivate: "ZaAccountMemberOfListView.addGroups.call(this,event, ZaAccount.A2_nonMemberList)",
                                            enableDisableChangeEventSources: [ZaDistributionList.A2_nonmembersSelected],
                                            enableDisableChecks: [
                                                [
                                                    XForm.checkInstanceValueNotEmty,
                                                    ZaDistributionList.A2_nonmembersSelected
                                                ]
                                            ]
                                        },
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _DWT_BUTTON_,
                                            label: ZaMsg.DLXV_ButtonAddAll,
                                            onActivate: "ZaAccountMemberOfListView.addAllGroups.call(this,event, ZaAccount.A2_nonMemberList)",
                                            enableDisableChangeEventSources: [ZaAccount.A2_nonMemberList],
                                            enableDisableChecks: [
                                                [
                                                    XForm.checkInstanceValueNotEmty,
                                                    ZaAccount.A2_nonMemberList
                                                ]
                                            ]
                                        },
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _DWT_BUTTON_,
                                            label: ZaMsg.Previous,
                                            id: "backButton",
                                            icon: "LeftArrow",
                                            disIcon: "LeftArrowDis",
                                            enableDisableChangeEventSources: [ZaAccount.A2_nonMemberList + "_offset"],
                                            enableDisableChecks: [
                                                [
                                                    ZaAccountMemberOfListView.shouldEnableBackButton,
                                                    ZaAccount.A2_nonMemberList
                                                ]
                                            ],
                                            onActivate: "ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"
                                        },
                                        {
                                            type: _CELLSPACER_
                                        },
                                        {
                                            type: _DWT_BUTTON_,
                                            label: ZaMsg.Next,
                                            id: "fwdButton",
                                            icon: "RightArrow",
                                            disIcon: "RightArrowDis",
                                            enableDisableChangeEventSources: [ZaAccount.A2_nonMemberList + "_more"],
                                            enableDisableChecks: [
                                                [
                                                    ZaAccountMemberOfListView.shouldEnableForwardButton,
                                                    ZaAccount.A2_nonMemberList
                                                ]
                                            ],
                                            onActivate: "ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"
                                        },
                                        {
                                            type: _CELLSPACER_
                                        }
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

    if (_tab4) {
        var addAliasButton = {
            type: _DWT_BUTTON_,
            label: ZaMsg.NAD_Add,
            width: "100px",
            onActivate: "ZaNewDLXWizard.addAliasButtonListener.call(this);"
        };

        if (entry.id) {
            addAliasButton.enableDisableChecks = [
                [
                    XFormItem.prototype.hasRight,
                    ZaDistributionList.ADD_DL_ALIAS_RIGHT
                ]
            ];
        } else {
            addAliasButton.enableDisableChecks = [
                [
                    ZaItem.hasWritePermission,
                    ZaAccount.A_zimbraMailAlias
                ]
            ];
        }

        var editAliasButton = {
            type: _DWT_BUTTON_,
            label: ZaMsg.TBB_Edit,
            width: "100px",
            enableDisableChangeEventSources: [ZaDistributionList.A2_alias_selection_cache],
            onActivate: "ZaNewDLXWizard.editAliasButtonListener.call(this);",
            id: "editAliasButton"
        };

        if (entry.id) {
            editAliasButton.enableDisableChecks = [
                ZaNewDLXWizard.isEditAliasEnabled,
                [
                    XFormItem.prototype.hasRight,
                    ZaDistributionList.REMOVE_DL_ALIAS_RIGHT
                ],
                [
                    XFormItem.prototype.hasRight,
                    ZaAccount.ADD_DL_ALIAS_RIGHT
                ]
            ];
        } else {
            editAliasButton.enableDisableChecks = [
                ZaNewDLXWizard.isEditAliasEnabled,
                [
                    ZaItem.hasWritePermission,
                    ZaAccount.A_zimbraMailAlias
                ]
            ];
        }

        var deleteAliasButton = {
            type: _DWT_BUTTON_,
            label: ZaMsg.TBB_Delete,
            width: "100px",
            onActivate: "ZaNewDLXWizard.deleteAliasButtonListener.call(this);",
            id: "deleteAliasButton",
            enableDisableChangeEventSources: [ZaDistributionList.A2_alias_selection_cache]
        };

        if (entry.id) {
            deleteAliasButton.enableDisableChecks = [
                ZaNewDLXWizard.isDeleteAliasEnabled,
                [
                    XFormItem.prototype.hasRight,
                    ZaDistributionList.REMOVE_DL_ALIAS_RIGHT
                ]
            ];
        } else {
            deleteAliasButton.enableDisableChecks = [
                ZaNewDLXWizard.isDeleteAliasEnabled,
                [
                    ZaItem.hasWritePermission,
                    ZaAccount.A_zimbraMailAlias
                ]
            ];
        }

        var case4 = {
            type: _CASE_,
            caseKey: _tab4,
            width: "100%",
            numCols: 1,
            items: [
                {
                    type: _GROUPER_,
                    borderCssClass: "LeftGrouperBorder",
                    width: "100%",
                    numCols: 1,
                    label: ZaMsg.NAD_EditDLAliasesGroup,
                    items: [
                        {
                            ref: ZaAccount.A_zimbraMailAlias,
                            type: _DWT_LIST_,
                            height: "200",
                            width: "98%",
                            forceUpdate: true,
                            preserveSelection: false,
                            multiselect: true,
                            cssClass: "DLSource",
                            headerList: null,
                            onSelection: ZaNewDLXWizard.aliasSelectionListener
                        },
                        {
                            type: _GROUP_,
                            numCols: 6,
                            colSizes: ["100px", "10px", "100px", "10px", "100px", "auto"],
                            cssStyle: "margin-bottom: 10px; padding-bottom: 0px; margin-top: 10px; margin-left: 10px; margin-right: 10px;",
                            items: [
                                deleteAliasButton,
                                {
                                    type: _CELLSPACER_
                                },
                                editAliasButton,
                                {
                                    type: _CELLSPACER_
                                },
                                addAliasButton
                            ]
                        }
                    ]
                }
            ]
        };

        cases.push(case4);
    }

    if (_tab5) {
        var case5 = {
            type: _CASE_,
            caseKey: _tab5,
            numCols: 1,
            items: [
                {
                    ref: ZaDistributionList.A2_DLOwners,
                    type: _REPEAT_,
                    label: null,
                    showAddButton: true,
                    showRemoveButton: true,
                    addButtonLabel: ZaMsg.BTN_Add_Owner,
                    showAddOnNextRow: true,
                    removeButtonLabel: ZaMsg.BTN_Remove_Owner,
                    removeButtonCSSStyle: "margin-left: 50px",
                    items: [
                        {
                            ref: ".",
                            type: _EMAILADDR_,
                            label: null,
                            enableDisableChecks: [],
                            visibilityChecks: []
                        }
                    ]
                }
            ]
        };

        cases.push(case5);
    }

    if (_tab6) {
        var case6 = {
            type: _CASE_,
            caseKey: _tab6,
            numCols: 1,
            items: [
                {
                    type: _GROUPER_,
                    label: ZaMsg.NAD_MailOptionsReceiving,
                    id: "dl_pref_replyto_group",
                    colSpan: "*",
                    numCols: 2,
                    colSizes: [225, "*"],
                    visibilityChecks: [
                        [
                            ZATopGrouper_XFormItem.isGroupVisible,
                            [
                                ZaDistributionList.A_zimbraPrefReplyToEnabled,
                                ZaDistributionList.A_zimbraPrefReplyToDisplay,
                                ZaDistributionList.A_zimbraPrefReplyToAddress
                            ]
                        ]
                    ],
                    visibilityChangeEventSources: [],
                    items: [
                        {
                            ref: ZaDistributionList.A_zimbraPrefReplyToEnabled,
                            type: _WIZ_CHECKBOX_,
                            visibilityChecks: [
                                [
                                    ZaItem.hasReadPermission,
                                    ZaDistributionList.A_zimbraPrefReplyToEnabled
                                ]
                            ],
                            label: ZaMsg.DLXV_ReplayToEnabled,
                            trueValue: "TRUE",
                            falseValue: "FALSE"
                        },
                        {
                            ref: ZaDistributionList.A_zimbraPrefReplyToDisplay,
                            type: _TEXTFIELD_,
                            label: ZaMsg.DLXV_ReplayToAddrDisplay,
                            emptyText: ZaMsg.DLXV_ReplayToAddrEmptyText,
                            visibilityChecks: [
                                [
                                    ZaItem.hasReadPermission,
                                    ZaDistributionList.A_zimbraPrefReplyToDisplay
                                ]
                            ],
                            enableDisableChecks: [
                                [
                                    XForm.checkInstanceValue,
                                    ZaDistributionList.A_zimbraPrefReplyToEnabled,
                                    "TRUE"
                                ],
                                [
                                    ZaItem.hasWritePermission,
                                    ZaDistributionList.A_zimbraPrefReplyToAddress
                                ]
                            ],
                            enableDisableChangeEventSources: [ZaDistributionList.A_zimbraPrefReplyToEnabled]
                        },
                        {
                            type: _DYNSELECT_,
                            ref: ZaDistributionList.A_zimbraPrefReplyToAddress,
                            dataFetcherClass: ZaSearch,
                            dataFetcherMethod: ZaSearch.prototype.dynSelectSearch,
                            dataFetcherTypes: [
                                ZaSearch.ACCOUNTS,
                                ZaSearch.RESOURCES,
                                ZaSearch.DLS
                            ],
                            dataFetcherAttrs: [
                                ZaItem.A_zimbraId,
                                ZaItem.A_cn,
                                ZaAccount.A_name,
                                ZaAccount.A_displayname,
                                ZaAccount.A_mail
                            ],
                            label: ZaMsg.DLXV_ReplayToAddr,
                            emptyText: ZaMsg.DLXV_ReplayToAddrEmptyText,
                            editable: true,
                            forceUpdate: true,
                            choices: new XFormChoices(
                                [],
                                XFormChoices.OBJECT_LIST,
                                "name",
                                "name"
                            ),
                            visibilityChecks: [
                                [
                                    ZaItem.hasReadPermission,
                                    ZaDistributionList.A_zimbraPrefReplyToAddress
                                ]
                            ],
                            enableDisableChangeEventSources: [ZaDistributionList.A_zimbraPrefReplyToEnabled],
                            enableDisableChecks: [
                                [
                                    XForm.checkInstanceValue,
                                    ZaDistributionList.A_zimbraPrefReplyToEnabled,
                                    "TRUE"
                                ],
                                [
                                    ZaItem.hasWritePermission,
                                    ZaDistributionList.A_zimbraPrefReplyToAddress
                                ]
                            ],
                            onChange: function (value, event, form) {
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

    var headerItems = [
        {
            type: _AJX_IMAGE_,
            src: "Group_32",
            label: null,
            rowSpan: 3
        },
        {
            type: _OUTPUT_,
            ref: "name",
            label: null,
            cssClass: "AdminTitle",
            height: "auto",
            width: 350,
            rowSpan: 3,
            cssStyle: "word-wrap: break-word; overflow: hidden"
        }
    ];

    if (ZaItem.hasReadPermission (ZaItem.A_zimbraId, entry)) {
        headerItems.push (
            {
                type: _OUTPUT_,
                ref: ZaItem.A_zimbraId,
                label: ZaMsg.NAD_ZimbraID
            }
        );
    }

    if (ZaItem.hasReadPermission (ZaItem.A_zimbraCreateTimestamp, entry)) {
        headerItems.push(
            {
                type: _OUTPUT_,
                ref: ZaItem.A_zimbraCreateTimestamp,
                label: ZaMsg.LBL_zimbraCreateTimestamp,
                labelLocation: _LEFT_,
                getDisplayValue: function() {
                    var val = ZaItem.formatServerTime(this.getInstanceValue());
                    if (!val) {
                        return ZaMsg.Server_Time_NA;
                    }
                    return val;
                }
            }
        );
    }

    if (ZaItem.hasReadPermission (ZaDistributionList.A_mailStatus, entry)) {
        headerItems.push (
            {
                type: _OUTPUT_,
                ref: ZaDistributionList.A_mailStatus,
                label: ZaMsg.NAD_ResourceStatus,
                choices: this.dlStatusChoices
            }
        );
    }

    this._lastStep = this.stepChoices.length;

    xFormObject.items = [
        {
            type: _OUTPUT_,
            colSpan: 2,
            valign: _TOP_,
            cssStyle: "white-space: normal",
            ref: ZaModel.currentStep,
            choices: this.stepChoices,
            valueChangeEventSources: [ZaModel.currentStep]
        },
        {
            type: _SEPARATOR_,
            align: _CENTER_,
            valign: _TOP_
        },
        {
            type: _SPACER_,
            align: _CENTER_,
            valign: _TOP_
        },
        {
            type: _SWITCH_,
            width: 700,
            align: _LEFT_,
            valign: _TOP_,
            cssStyle: "white-space: normal",
            items:cases
        }
    ];
};

ZaXDialog.XFormModifiers["ZaNewDLXWizard"].push(ZaNewDLXWizard.myXFormModifier);
ZaNewDLXWizard.isAutoDisplayname = function () {
        return(this.getInstanceValue(ZaResource.A2_autoLocationName)=="FALSE");
}

