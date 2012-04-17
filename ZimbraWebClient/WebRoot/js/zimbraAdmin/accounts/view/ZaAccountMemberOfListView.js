/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* This class describes a List view of an account's Member Of tab
* @class ZaAccountMemberOfListView
* @contructor ZaAccountMemberOfListView
* @author Charles Cao
**/
ZaAccountMemberOfListView = function(parent, className, posStyle, headerList){
	ZaListView.call(this, parent, className, posStyle, headerList);
}

ZaAccountMemberOfListView.prototype = new ZaListView ;
ZaAccountMemberOfListView.prototype.constructor = ZaAccountMemberOfListView ;
ZaAccountMemberOfListView.prototype.toString = function (){
	return "ZaAccountMemberOfListView";
};

ZaAccountMemberOfListView.A_name = "name" ;
//ZaAccountMemberOfListView.A_isgroup = "isgroup" ;
ZaAccountMemberOfListView.A_via = "via" ;
ZaAccountMemberOfListView.SEARCH_LIMIT = 25 ;

//modify the ZaAccount and ZaDistributionList model
ZaAccountMemberOfListView.modelItems = [
		//{id:ZaAccount.A2_isgroup, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_isgroup},
		{id:ZaAccount.A2_directMemberList, type: _LIST_, ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_directMemberList},
		{id:ZaAccount.A2_indirectMemberList, type: _LIST_, ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_indirectMemberList},
		{id:ZaAccount.A2_nonMemberList, type: _LIST_, ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_nonMemberList},
        {id:ZaAccount.A2_nonMemberListSelected, type: _LIST_, ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_nonMemberListSelected},
        {id:ZaAccount.A2_directMemberListSelected, type: _LIST_, ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_directMemberListSelected},
        {id:ZaAccount.A2_indirectMemberListSelected, type: _LIST_, ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_indirectMemberListSelected},
        {id:ZaAccount.A2_directMemberList + "_offset", ref: ZaAccount.A2_directMemberList + "_offset", type:_NUMBER_, defaultValue: 0},
		{id:ZaAccount.A2_nonMemberList + "_offset", ref: ZaAccount.A2_nonMemberList + "_offset", type:_NUMBER_, defaultValue: 0},
		{id:ZaAccount.A2_directMemberList + "_more", ref: ZaAccount.A2_directMemberList + "_more", type:_NUMBER_, defaultValue: 0},
		{id:ZaAccount.A2_nonMemberList + "_more", ref: ZaAccount.A2_nonMemberList + "_more", type:_NUMBER_, defaultValue: 0},
		{id:ZaAccount.A2_showSameDomain, type: _ENUM_, choices:ZaModel.BOOLEAN_CHOICES, 
			ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_showSameDomain, defaultValue: "FALSE" },
		{id:"query", type:_STRING_}
]
ZaAccount.myXModel.items = ZaAccount.myXModel.items.concat(ZaAccountMemberOfListView.modelItems);
ZaDistributionList.myXModel.items = ZaDistributionList.myXModel.items.concat(ZaAccountMemberOfListView.modelItems);

ZaAccountMemberOfListView.parseGetAccMembershipResponse =
function(resp) {
	var directML = [];
	var indirectML = [];
	var nonML = [];	
	if (resp.dl && (resp.dl instanceof Array)){
		var dls = resp.dl ;
		var n = resp.dl.length ;
		for (var i=0, d=0, m=0; m < n; m++ ){
			if (dls[m].via && (dls[m].via.length >0)){ //indirect dl
				indirectML[i] = { name: dls[m].name, id: dls[m].id, via: dls[m].via} ;
				i ++ ;
			} else{
				directML[d] = { name: dls[m].name, id: dls[m].id } ;
                var attrs = ZaItem.initAttrsFromJS (dls[m]) ;
                if (attrs["zimbraIsAdminGroup"] != null) {
                	directML[d]["zimbraIsAdminGroup"] = attrs["zimbraIsAdminGroup"] ; 
                }
                d ++ ;
			}
		}
	}
	return {directMemberList: directML,indirectMemberList: indirectML,nonMemberList: nonML};	
}
/**
 * @param app
 * @param val {account value corresponding to by}
 * @param by  {either by id or name} 
 * @return the memberOf object 
 * 				{ 	directMemberList: [ { name: dl1@test.com, id: 394394 } , {..}, ...] ,
 * 					indirectMemberList: [ { name: dl1@test.com, id: 394394, via: dl2@test.com} , {..}, ...] ,
 * 					nonMemberList: [ { name: dl1@test.com, id: 394394 } , {..}, ...]
 * 				}
 * 					
 */
ZaAccountMemberOfListView.getAccountMemberShip = 
function (val, by){
	var directML = [];
	var indirectML = [];
	var nonML = [];
		 
	try {
		soapDoc = AjxSoapDoc.create("GetAccountMembershipRequest", ZaZimbraAdmin.URN, null);
		var elBy = soapDoc.set("account", val);
		elBy.setAttribute("by", by);

		//var getAccMemberShipCommand = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;
		params.noAuthToken = true;	
		var reqMgrParams = {
			controller: ZaApp.getInstance().getCurrentController ()
		}
		var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAccountMembershipResponse;
		if (resp.dl && (resp.dl instanceof Array)){
			var dls = resp.dl ;
			var n = resp.dl.length ;
			for (var i=0, d=0, m=0; m < n; m++ ){
				if (dls[m].via && (dls[m].via.length >0)){ //indirect dl
					indirectML[i] = { name: dls[m].name, id: dls[m].id, via: dls[m].via} ;
					i ++ ;
				}else{
					directML[d] = { name: dls[m].name, id: dls[m].id } ;
                    var attrs = ZaItem.initAttrsFromJS (dls[m]) ;
                    if (attrs["zimbraIsAdminGroup"] != null) {
                        directML[d]["zimbraIsAdminGroup"] = attrs["zimbraIsAdminGroup"] ; 
                    }
                    d ++ ;
				}
			}
		}
	}catch (ex){
		ZaApp.getInstance().getCurrentController()._handleException(ex,
                "ZaAccountMemberOfListView.getAccountMemberShip", null, false);
	}
	
	var memberOf = {	directMemberList: directML,
						indirectMemberList: indirectML,
						nonMemberList: nonML
					};
	return memberOf ;
}

ZaAccountMemberOfListView.getDlMemberShip = 
function (val, by){
	var directML = [];
	var indirectML = [];
	var nonML = [];
		 
	try {
		soapDoc = AjxSoapDoc.create("GetDistributionListMembershipRequest", ZaZimbraAdmin.URN, null);
		var elBy = soapDoc.set("dl", val);
		elBy.setAttribute("by", by);

		//var getDlMemberShipCommand = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		var reqMgrParams = {
			controller: ZaApp.getInstance().getCurrentController()
		}
		var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetDistributionListMembershipResponse;
		
		if (resp.dl && (resp.dl instanceof Array)){
			var dls = resp.dl ;
			var n = resp.dl.length ;
			for (var i=0, d=0, m=0; m < n; m++ ){
				//if (dls[m].isgroup) {
				if (dls[m].via && (dls[m].via.length >0)){ //indirect dl
					indirectML[i] = { name: dls[m].name, id: dls[m].id, via: dls[m].via} ;
					i ++ ;
				}else{
					directML[d] = { name: dls[m].name, id: dls[m].id } ;
					d ++ ;
				}
			}
		}

	}catch (ex){
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaAccountMemberOfListView.getDlMemberShip", null, false);
	}
	
	var memberOf = {	directMemberList: directML,
						indirectMemberList: indirectML,
						nonMemberList: nonML
					};
	return memberOf ;
}

ZaAccountMemberOfListView.cloneMemberOf = function (src) {
    var memberOf = {};
    for (var p in src[ZaAccount.A2_memberOf]) {
        memberOf [p] = [] ;
        for (var i = 0; i < src[ZaAccount.A2_memberOf][p].length; i ++) {
            var v = src[ZaAccount.A2_memberOf][p][i] ;
            if (v instanceof Object) {
                var newV = {} ;
                for (var p2 in v) {
                    newV [p2] = v[p2] ;
                }
                memberOf[p].push (newV) ;
            } else {
                memberOf [p].push (v) ;
            }
        }
    }

    return memberOf;
}

ZaAccountMemberOfListView.removeAllGroups =
function(event, listId){
	var form = this.getForm();
	var allSelections = ZaAccountMemberOfListView._getAllInList(form, listId);
	ZaAccountMemberOfListView._removeSelectedLists(form, allSelections);
};

ZaAccountMemberOfListView.removeGroups =
function (event, listId){
	var form = this.getForm();
	var selections = ZaAccountMemberOfListView._getSelections(form, listId);
	ZaAccountMemberOfListView._removeSelectedLists(form, selections);	
};

ZaAccountMemberOfListView._removeSelectedLists =
function (form, listArr){
	var instance = form.getInstance();
	var directMemberList = instance[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList];
	var indirectMemberList = instance[ZaAccount.A2_memberOf][ZaAccount.A2_indirectMemberList];	
	var nonMemberList = instance[ZaAccount.A2_memberOf][ZaAccount.A2_nonMemberList];		

	var j = -1;	
	var dlName = null ;
	var indirectArrFound = null;
	
	for(var i=0; i<listArr.length; i++) {
		dlName = listArr[i][ZaAccountMemberOfListView.A_name] ;
		j = ZaUtil.findValueInObjArrByPropertyName(directMemberList, dlName, ZaAccountMemberOfListView.A_name);
		if (j >= 0 ) {
			//check whether there is derived indirect list, and display warning is yes
			indirectArrFound = ZaAccountMemberOfListView._findIndirect(indirectMemberList, dlName);
			if (indirectArrFound.length > 0){
				
				//ZaAccountMemberOfListView._toBeConfirmedList.push([directDlName, indirectArrFound]);
				var indirectDls = indirectArrFound.join("<br />");			
				msg = AjxMessageFormat.format (ZaMsg.Q_REMOVE_INDIRECT_GROUP, [dlName, indirectDls]);
				
				var confirmMessageDialog =  new ZaMsgDialog(form.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], null, ZaId.VIEW_MEMLIST);					
				
				confirmMessageDialog.setMessage(msg,  DwtMessageDialog.WARNING_STYLE);
				confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaAccountMemberOfListView._removeConfirmedList, null ,
														[form, confirmMessageDialog, dlName, indirectArrFound]) ;		
				confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaAccountMemberOfListView._closeConfirmDialog, null, [form, confirmMessageDialog]);				
				confirmMessageDialog.popup();
				
				//splice the entry in the callback method.
				continue;
			}			
			directMemberList.splice(j, 1);
            form.parent.setDirty(true);
        }
	}

    form.getModel().setInstanceValue(instance, ZaAccount.A2_directMemberList, directMemberList) ;
    form.getModel().setInstanceValue(instance, ZaAccount.A2_indirectMemberList, indirectMemberList) ;
    form.getModel().setInstanceValue(instance, ZaAccount.A2_nonMemberList, nonMemberList) ;

    if(directMemberList == null || directMemberList.length <= ZaAccountMemberOfListView.SEARCH_LIMIT)
        form.getModel().setInstanceValue(instance, ZaAccount.A2_directMemberList + "_more", 0);
};

ZaAccountMemberOfListView._closeConfirmDialog =
function (form, dialog){
	if (dialog)
		dialog.popdown();
};

ZaAccountMemberOfListView._removeConfirmedList = 
function (form, dialog, directDlName, indirectDlsNameArr){
	if (dialog) {
		var instance = form.getInstance();
		var directMemberList = instance[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList];
		var indirectMemberList = instance[ZaAccount.A2_memberOf][ZaAccount.A2_indirectMemberList];
        var nonMemberList = instance[ZaAccount.A2_memberOf][ZaAccount.A2_nonMemberList];
        
        var j = -1;
		var m = -1;
		//remove from directMemberList
		j = ZaUtil.findValueInObjArrByPropertyName(directMemberList, directDlName, ZaAccountMemberOfListView.A_name);
		if (j >= 0){		
			directMemberList.splice(j, 1);
		}
		
		for(var i=0; i<indirectDlsNameArr.length; i++) {
			j = ZaUtil.findValueInObjArrByPropertyName(indirectMemberList, indirectDlsNameArr[i], ZaAccountMemberOfListView.A_name);
			if (j>=0) 
				indirectMemberList.splice(j, 1);			
		}		
		form.parent.setDirty(true);		
		ZaAccountMemberOfListView._closeConfirmDialog(form, dialog);
        form.getModel().setInstanceValue(instance, ZaAccount.A2_directMemberList, directMemberList) ;
        form.getModel().setInstanceValue(instance, ZaAccount.A2_indirectMemberList, indirectMemberList) ;
        form.getModel().setInstanceValue(instance, ZaAccount.A2_nonMemberList, nonMemberList) ;
    }
}

ZaAccountMemberOfListView._findIndirect  =
function(arr, value, foundArr){
	var j = -1 ;
	if (!foundArr) {
		foundArr = new Array();
	}
	
	if (arr) { 
	for(var i=0; i<arr.length; i++) {
		if (arr[i][ZaAccountMemberOfListView.A_via] == value) {		
			//j = ZaAccountMemberOfListView._find(arr, value, ZaAccountMemberOfListView.A_via) ;
			foundArr.push (arr[i][ZaAccountMemberOfListView.A_name]) ;
			foundArr = ZaAccountMemberOfListView._findIndirect(arr, arr[i][ZaAccountMemberOfListView.A_name], foundArr);
		}
	}
	}
	return foundArr;			
}

ZaAccountMemberOfListView.addGroups=
function (event, listId){
	var form = this.getForm();
	var selections = ZaAccountMemberOfListView._getSelections(form, listId);
	ZaAccountMemberOfListView._addSelectedLists(form, selections);
};


ZaAccountMemberOfListView.addAllGroups =
function(event, listId){
	var form = this.getForm ();
	var allSelections = ZaAccountMemberOfListView._getAllInList(form, listId);
	ZaAccountMemberOfListView._addSelectedLists(form, allSelections);
    
};

ZaAccountMemberOfListView._addSelectedLists=
function (form, listArr){	
	var instance = form.getInstance();
	var memberOf = instance[ZaAccount.A2_memberOf];

    //don't add the duplicated entry                                                           
    var nonDupArr = [] ;
    for (var i=0; i < listArr.length; i ++) {
        var j = ZaUtil.findValueInObjArrByPropertyName(
                instance[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList],
                listArr[i][ZaAccountMemberOfListView.A_name], ZaAccountMemberOfListView.A_name);
        
        if (j >= 0) {
            continue ;
        } else {
            nonDupArr.push(listArr [i]) ;
        }
    }

    if(!memberOf[ZaAccount.A2_directMemberList]){
	memberOf[ZaAccount.A2_directMemberList] = [];
    }

    memberOf[ZaAccount.A2_directMemberList] = memberOf[ZaAccount.A2_directMemberList].concat(nonDupArr);

	form.parent.setDirty(true);
    form.getModel().setInstanceValue(instance, ZaAccount.A2_directMemberList, memberOf[ZaAccount.A2_directMemberList]) ;
    form.getModel().setInstanceValue(instance, ZaAccount.A2_nonMemberList, memberOf[ZaAccount.A2_nonMemberList]) ;
    form.getModel().setInstanceValue(instance, ZaAccount.A2_indirectMemberList, memberOf[ZaAccount.A2_indirectMemberList]) ;

    if(memberOf[ZaAccount.A2_directMemberList].length > ZaAccountMemberOfListView.SEARCH_LIMIT)
        form.getModel().setInstanceValue(instance, ZaAccount.A2_directMemberList + "_more", 1);
};


ZaAccountMemberOfListView._getSelections =
function (form, listId){
	var selections = form.getItemsById(listId)[0].getSelection();
	return (selections) ? selections : [] ;
};

ZaAccountMemberOfListView._getAllInList =
function (form, listId){
	//set selections
	var dwtListItem = form.getItemsById(listId)[0].widget ;
	var allListArr =  dwtListItem.getList().getArray() ;
	dwtListItem.setSelectedItems(allListArr); //get all the lists	
	return allListArr ;	
}

/**
 * Enable/Disable Add Button or remove button based on the itemId
 */
ZaAccountMemberOfListView.shouldEnableAddRemoveButton =
function (listId){
	return  (ZaAccountMemberOfListView._getSelections(this.getForm(), listId).length > 0);
};

/**
 * Enable/Diable "Add All" or "Remove All" buttons based on the itemId
 */
ZaAccountMemberOfListView.shouldEnableAllButton =
function (listItemId){
//	var list = this.getForm().getItemsById(listItemId)[0].widget.getList();
    var list = this.getInstanceValue (listItemId) ;
    if (list != null) return ( list.length > 0);
	return false;
};

ZaAccountMemberOfListView.shouldEnableBackButton =
function(listItemId){
	var offset = this.getInstance()[listItemId + "_offset"] ;
	return ((offset && offset > 0) ? true : false) ;	
};

ZaAccountMemberOfListView.shouldEnableForwardButton =
function (listItemId){
	var more = this.getInstance()[listItemId + "_more"] ;
	return ((more && more > 0) ? true : false) ;		
};

ZaAccountMemberOfListView.addMemberList =
function (tmpObj, item) {
     try {
        var addList = [];
        if (tmpObj[ZaAccount.A2_memberOf] && tmpObj[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList]) {
            var newDirectMember = tmpObj[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList];

            //Compose the added dl list - any dl from new direct memberOf list, not in the current memberOf list
            if (newDirectMember ) {
                if (!newDirectMember instanceof Array) {
                     newDirectMember = [newDirectMember] ;
                }
                
                for (var i = 0; i < newDirectMember.length; i ++) {
                    var dlName = newDirectMember[i].name ; //dl in the new direct member
                    addList.push (newDirectMember[i]) ;
                }

                if (addList.length > 0) { //you have new membership to be added.
                    ZaAccountMemberOfListView.addNewGroupsBySoap(item, addList);
                }
            }
        }
    }catch (ex){
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaAccountMemberOfListView.addMemberList: add group failed", null, false);	//try not to halt the account modification
	}
}
ZaItem.createMethods["ZaAccount"].push (ZaAccountMemberOfListView.addMemberList) ;
ZaItem.createMethods["ZaDistributionList"].push (ZaAccountMemberOfListView.addMemberList) ;

ZaAccountMemberOfListView.modifyMemberList =
function (mods, tmpObj) {
     try {
        if (!tmpObj || (this[ZaAccount.A2_memberOf] == null) || !tmpObj[ZaAccount.A2_memberOf]) {
            //no need to modify the member list
            return ;
        }
        var addList = [];
        var removeList = [] ;

        var currentDirectMember = this[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList];
        var newDirectMember = tmpObj[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList];

        //Compose the added dl list - any dl from new direct memberOf list, not in the current memberOf list
        for (var i = 0; i < newDirectMember.length; i ++) {
            var dlName = newDirectMember[i].name ; //dl in the new direct member
            var j = ZaUtil.findValueInObjArrByPropertyName(currentDirectMember, dlName, ZaAccountMemberOfListView.A_name);
            if (j >= 0){
                //found in the current memberOf List, not need to add
            }else{   //need to be added
                addList.push (newDirectMember[i]) ;
            }
        }

         //Compose the remove dl list - any dl in the current memberOf list not in the new direct member list, 
         for (var m = 0; m < currentDirectMember.length; m ++) {
             var dlName = currentDirectMember[m].name ; //dl in the current direct member
             var j = ZaUtil.findValueInObjArrByPropertyName(newDirectMember, dlName, ZaAccountMemberOfListView.A_name);
             if (j >= 0){
                 //found in the new memberOf List, no need to remove
             }else{   //need to be removed
                 removeList.push (currentDirectMember[m]) ;
             }
         }


        if (addList.length > 0) { //you have new membership to be added.
            ZaAccountMemberOfListView.addNewGroupsBySoap(this, addList);
        }

         if (removeList.length >0){//you have membership to be removed
			ZaAccountMemberOfListView.removeGroupsBySoap(this, removeList);
		}

    }catch (ex){
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaAccountMemberOfListView.modifyMemberList: add group failed", null, false);	//try not to halt the account modification
	}
}
ZaItem.modifyMethods["ZaAccount"].push (ZaAccountMemberOfListView.modifyMemberList) ;
ZaItem.modifyMethods["ZaDistributionList"].push(ZaAccountMemberOfListView.modifyMemberList);

/*
 * Add the current account/dl to the new groups/dls 
 * @param addArray new groups/dls
 */
ZaAccountMemberOfListView.addNewGroupsBySoap = 
function (account, addArray) {	
	var len = addArray.length;
	var addMemberSoapDoc, r, addMemberSoapDoc;
	var command = new ZmCsfeCommand();
	for (var i = 0; i < len; ++i) {
		addMemberSoapDoc = AjxSoapDoc.create("AddDistributionListMemberRequest", ZaZimbraAdmin.URN, null);
		addMemberSoapDoc.set("id", addArray[i].id); //group id 
		addMemberSoapDoc.set("dlm", account.name); //account name
		var params = new Object();
		params.soapDoc = addMemberSoapDoc;
		params.noAuthToken = true;	
		r=command.invoke(params).Body.AddDistributionListMemberResponse;
	}
};

/**
 * remove the current account from groups
 * @params removeArray
 */
ZaAccountMemberOfListView.removeGroupsBySoap = 
function (account, removeArray){
	var len = removeArray.length;
	var addMemberSoapDoc, r, removeMemberSoapDoc;
	var command = new ZmCsfeCommand();	
	for (var i = 0; i < len; ++i) {
		removeMemberSoapDoc = AjxSoapDoc.create("RemoveDistributionListMemberRequest", ZaZimbraAdmin.URN, null);
		removeMemberSoapDoc.set("id", removeArray[i].id);
		removeMemberSoapDoc.set("dlm", account.name);
		var params = new Object();
		params.soapDoc = removeMemberSoapDoc;
		params.noAuthToken = true;	
		r=command.invoke(params).Body.RemoveDistributionListMemberResponse;		
	}
}

/**
 * search the directory for all the distribution lists when the search button is clicked.
 */
ZaAccountMemberOfListView.prototype.srchButtonHndlr =
function (){
	var item = this ;
	ZaAccountMemberOfListView.doSearch(item, 0) ;
}

ZaAccountMemberOfListView.backButtonHndlr = 
function (event, listItemId){
    var instance = this.getInstance();
	var currentOffset = this.getInstanceValue( listItemId + "_offset") ;
	if (currentOffset == null) currentOffset = 0;
	var nextOffset = 0;
	if (listItemId == ZaAccount.A2_nonMemberList) {		
		nextOffset = currentOffset - ZaAccountMemberOfListView.SEARCH_LIMIT ;  
		ZaAccountMemberOfListView.doSearch(this, nextOffset) ;
	}else{ //directMemmberList // if (listItemId == ZaAccount.A2_directMemberList)
		nextOffset = currentOffset - ZaAccountMemberOfListView.SEARCH_LIMIT ;
		this.setInstanceValue(nextOffset, listItemId + "_offset" );
		this.setInstanceValue(1, listItemId + "_more");	

        var directMemberOfList = instance [ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList] ;
        var indirectMemberOfList = instance [ZaAccount.A2_memberOf][ZaAccount.A2_indirectMemberList] ;
        this.setInstanceValue( directMemberOfList , ZaAccount.A2_directMemberList);
        this.setInstanceValue( indirectMemberOfList , ZaAccount.A2_indirectMemberList);
    }
};

ZaAccountMemberOfListView.fwdButtonHndlr =
function(event, listItemId){
	var instance = this.getInstance();
	var currentOffset = this.getInstanceValue(listItemId + "_offset") ;	
	if (currentOffset == null) currentOffset = 0;
	var nextOffset = 0;
		
	if (listItemId == ZaAccount.A2_nonMemberList) {		
		nextOffset = currentOffset + ZaAccountMemberOfListView.SEARCH_LIMIT ;  
		ZaAccountMemberOfListView.doSearch(this, nextOffset) ;
	}else{ // if (listItemId == ZaAccount.A2_directMemberList){ //directMemmberList
		nextOffset = currentOffset + ZaAccountMemberOfListView.SEARCH_LIMIT ;
				
		if ((nextOffset + ZaAccountMemberOfListView.SEARCH_LIMIT) 
				< instance[ZaAccount.A2_memberOf][listItemId].length){
			
			this.setInstanceValue(1, listItemId + "_more");
		}else{
			this.setInstanceValue(0,  listItemId + "_more");
		}
		this.setInstanceValue(nextOffset,  listItemId + "_offset");
        var directMemberOfList = instance [ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList] ;
        var indirectMemberOfList = instance [ZaAccount.A2_memberOf][ZaAccount.A2_indirectMemberList] ; 
        this.setInstanceValue( directMemberOfList , ZaAccount.A2_directMemberList);
        this.setInstanceValue( indirectMemberOfList , ZaAccount.A2_indirectMemberList);
	}
};

ZaAccountMemberOfListView.makeQueryStringWithoutDynamicDL =
function (rawQueryString) {
	if (rawQueryString == null) {
		rawQueryString = "";
	}

	return "(&" + rawQueryString + "(!(zimbraIsACLGroup=TRUE)))";
}

/**
 * search for the dls or groups
 * 
 */                                       
ZaAccountMemberOfListView.doSearch=
function (item, offset){
	var arr = [] ;
	//the preassumption is that both memberOf is the name of the attr of the instance 
	var xform = item.getForm() ; //item refers to a xform item
    
    if (xform){
		var curInstance = xform.getInstance();
		
		if (! offset) offset = 0 ;
		
		var memberOfObj = curInstance[ZaAccount.A2_memberOf] ;
		try {
			var sortby = ZaAccount.A_name ; 
			var searchByDomain = (memberOfObj [ZaAccount.A2_showSameDomain] && (memberOfObj [ZaAccount.A2_showSameDomain] == "TRUE")) ? true : false ;
			var domainName = null;			
			
			if (searchByDomain){
				try {
					var emailChunks = curInstance[ZaAccount.A_name].split("@");
					domainName = emailChunks[1];
				//	var domainName = xform.getItemById(xform.getId()+"_case").__xform.getItemById(xform.getId()+"_dl_name_field")._domainPart;
				} catch (ex) {
					//keep the domainName null
				}
			}
			
			var attrs = [ZaAccount.A_name, ZaItem.A_zimbraId];
			//var attrs = [""];
			var valStr = curInstance[ZaSearch.A_query];
			var queryTypes = [ZaSearch.DLS] ;
			var query = ZaSearch.getSearchByNameQuery(valStr, queryTypes);
			query = ZaAccountMemberOfListView.makeQueryStringWithoutDynamicDL(query);

			var params = { 	query: query ,
							sortBy: sortby,
							limit : ZaAccountMemberOfListView.SEARCH_LIMIT,
							offset: offset,
							domain: domainName,
							applyCos: 0,
							attrs: attrs,
							types: queryTypes,
							controller: ZaApp.getInstance().getCurrentController()
						 } ;
					
			var result = ZaSearch.searchDirectory(params).Body.SearchDirectoryResponse;
//			curInstance [ZaAccount.A2_nonMemberList + "_more"] = (result.more ? 1 : 0) ;
            item.setInstanceValue ((result.more ? 1 : 0), ZaAccount.A2_nonMemberList + "_more")  ;
			var list = new ZaItemList(ZaDistributionList, null);
			list.loadFromJS(result);
			arr = list.getArray();		
			var nonMemberList = new Array();
			for(var i=0; i<arr.length; i++) {				
				nonMemberList.push({
									name: arr[i].name,
									id: arr[i].id			
									});
			}
				
//			memberOfObj[ZaAccount.A2_nonMemberList] = nonMemberList ;
			item.setInstanceValue(nonMemberList, ZaAccount.A2_nonMemberList)  ;
			//set the instance variable listItemId_offset & listItemId_more 
//			curInstance [ZaAccount.A2_nonMemberList + "_offset"] = offset;
			item.setInstanceValue (offset, ZaAccount.A2_nonMemberList + "_offset" )	;	
			//xform.setInstance(curInstance) ;
//			xform.refresh();
		}catch (ex){
			ZaApp.getInstance().getCurrentController()._handleException(
				ex, "ZaAccountMemberOfListView.prototype.srchButtonHndlr");
		}	
	}
		
	return true;	
}

ZaAccountMemberOfListView.join =
function (memberListArr){
	var result = [];
	for(var i=0; i<memberListArr.length; i++) {
		if (memberListArr[i].name) {
			result.push(memberListArr[i].name);
		}
	}
	return result.join();
}
ZaAccountMemberOfListView.prototype._createItemHtml = function (group, now, isDragProxy){
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(group, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'>";

	html[idx++] = "<tr>";
	if(this._headerList) {
		var cnt = this._headerList.length;
		for(var i = 0; i < cnt; i++) {
			//if ()
			var field = this._headerList[i]._field;
			if(field == ZaAccountMemberOfListView.A_name) {
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(group[ZaAccountMemberOfListView.A_name]);				
				html[idx++] = "</td>";			
			}  /*
			else if(field == ZaAccountMemberOfListView.A_isgroup) {
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(group[ZaAccountMemberOfListView.A_isgroup] ? ZaMsg.Yes : ZaMsg.No);
				html[idx++] = "</td>";
			}*/
			else if(field == ZaAccountMemberOfListView.A_via) {
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(group[ZaAccountMemberOfListView.A_via]);
				html[idx++] = "</td>";
			} 
			 
		}
	} else {
		html[idx++] = "<td width=100%><nobr>";
		html[idx++] = AjxStringUtil.htmlEncode(group[ZaAccountMemberOfListView.A_name]);
		html[idx++] = "</nobr></td>";
	}
	
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
};

ZaAccountMemberOfListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");
	var msg = "";
	if (this.getCurrentListId().indexOf(ZaAccount.A2_indirectMemberList) >= 0) {
		msg = ZaMsg.Account_Group_NoInDirectMember;

	}else if (this.getCurrentListId().indexOf(ZaAccount.A2_directMemberList) >= 0){
		msg = ZaMsg.Account_Group_NoDirectMember;
	}
	
	buffer.append(
				  "<table width='99%' cellspacing='0' cellpadding='1' style='table-layout:fixed'>",
				  "<tr>",
				  "<td class='NoResults' style='white-space:normal; word-wrap:break-word; word-break:break-all;' >",
				  AjxStringUtil.htmlEncode(msg),
				  "</td>",
				  "</tr>",
				  "</table>"
	);
	
	div.innerHTML = buffer.toString();
	this._addRow(div);
};

ZaAccountMemberOfListView.prototype.setCurrentListId =
function(id){
	this._currentListId = id;
}

ZaAccountMemberOfListView.prototype.getCurrentListId =
function(){
	return this._currentListId;
}

/**
 * Customized Dwt_list for MemberShip list view. It is specialized, so the show group only check box can filter
 * the non group dls. 
 * 
 */
S_Dwt_List_XFormItem = function(){}
XFormItemFactory.createItemType("_S_DWT_LIST_", "s_dwt_list", S_Dwt_List_XFormItem, Dwt_List_XFormItem);


/**
 * This function overrides the Dwt_List_XFormItem.prototype.setItems
 * @param itemArray - the list array to be displayed
 */
S_Dwt_List_XFormItem.prototype.setItems = function (itemArray){
	var list = this.widget.getList();
	var existingArr = []; //the list in the current view
	var tmpArr = new Array();
	if (list) {
		existingArr = list.getArray();
	}
	tmpArr = new Array();
	var instance = this.getForm().getInstance();
	var isGroupOnlyCkbAction = instance[ZaAccount.A2_memberOf] ? instance[ZaAccount.A2_memberOf]["showGroupOnlyAction"] : false;
	var isGroupOnly = instance[ZaAccount.A2_memberOf] ? instance[ZaAccount.A2_memberOf][ZaAccountMemberOfListView.A_isgroup] : false;
	
	//set the current list id in widget which is used to display the proper noResultMessage
	this.widget.setCurrentListId(this.id);
	
	if (itemArray && itemArray.length > 0) {	
		var offset = 0 ;
		var more = 0;
		var len = itemArray.length ;
		if (this.id.indexOf(ZaAccount.A2_indirectMemberList) >= 0){
			offset = instance [ZaAccount.A2_indirectMemberList + "_offset"] ;
			if (offset == null) offset = 0;
			more = instance [ ZaAccount.A2_indirectMemberList + "_more"] ;
			if (more == null) more = 0;
			if (more > 0 && offset + ZaAccountMemberOfListView.SEARCH_LIMIT <= len) {
				len = offset + ZaAccountMemberOfListView.SEARCH_LIMIT ;
			}
		}else if (this.id.indexOf(ZaAccount.A2_directMemberList) >= 0){
			offset = instance [ZaAccount.A2_directMemberList + "_offset"] ;
			if (offset == null) offset = 0;
			more = instance [ ZaAccount.A2_directMemberList + "_more"] ;
			if (more == null) more = 0;
			if (more > 0 && offset + ZaAccountMemberOfListView.SEARCH_LIMIT <= len) {
				len = offset + ZaAccountMemberOfListView.SEARCH_LIMIT ;
			}
		}
		
		
		//filter out the itemArray first based on the checkboxes
		var filteredItemArray = new Array();
		var j = -1;		
		for(var i=offset; i<len; i++) {					
			if (this.id.indexOf(ZaAccount.A2_nonMemberList) >= 0){ 
				//filter out the dl itself in the DL View
				if (instance.id == itemArray[i].id) continue ;
				
				//filter out the directMember in nonMemberList
				j = ZaUtil.findValueInObjArrByPropertyName(
						instance[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList], 
						itemArray[i][ZaAccountMemberOfListView.A_name], ZaAccountMemberOfListView.A_name);
				if (j >= 0) {
					continue ;
				}
			}
			
			filteredItemArray.push(itemArray[i]);					
		}
				
		//we have to compare the objects, because XForm calls this method every time an item in the list is selected
		if(ZaAccountMemberOfListView.join(filteredItemArray) != ZaAccountMemberOfListView.join (existingArr) ) {
			var preserveSelection = this.getInheritedProperty("preserveSelection");
			var selection = null;
			if(preserveSelection) {
				selection = this.widget.getSelection();
			}		
			var cnt=filteredItemArray.length;
			for(var i = 0; i< cnt; i++) {
				tmpArr.push(filteredItemArray[i]);		
			}
			//add the default sort column
			this.widget.set(AjxVector.fromArray(tmpArr), this.getInheritedProperty("defaultColumnSortable"));
			if(preserveSelection && selection) {
				this.widget.setSelectedItems(selection);
			}
		}		
	}else{
		//display the empty list (no result html)
		this.widget.set(AjxVector.fromArray([])); 
	}
};
   
/**
* This class describes a header for the ZaAccountMemberOfList
* @class ZaAccountMemberOfListView
* @contructor ZaAccountMemberOfListView
* @author Charles Cao
**/
ZaAccountMemberOfsourceHeaderList = function(type, nameDefaultWidth) {
	var sourceHeaderList = new Array();
	var sortable = 0;
	
//	defaultColumnSortable = sortable ;
    if (!nameDefaultWidth) {
        nameDefaultWidth = 230;
    }
	var nameWidth = (type == ZaAccountMemberOfsourceHeaderList.INDIRECT) ? nameDefaultWidth : null ;
	sourceHeaderList[0] = new ZaListHeaderItem(ZaAccountMemberOfListView.A_name, 	ZaMsg.CLV_Name_col, 	
												null, nameWidth, null, ZaAccountMemberOfListView.A_name, false, true);
	
	/*
	var isgroupWidth = (type == ZaAccountMemberOfsourceHeaderList.INDIRECT) ? 80 : null ;
	sourceHeaderList[1] = new ZaListHeaderItem(ZaAccountMemberOfListView.A_isgroup,   	ZaMsg.Account_Group,   	
	 											null, isgroupWidth,  null,  ZaAccountMemberOfListView.A_isgroup, true, true);
	*/
	if (type == ZaAccountMemberOfsourceHeaderList.INDIRECT) { 																							
		sourceHeaderList[1] = new ZaListHeaderItem(ZaAccountMemberOfListView.A_via,   	ZaMsg.Group_via,   	
	 											null, "auto",  null,  ZaAccountMemberOfListView.A_via, false, true);
	}
	
	return sourceHeaderList ;
}

ZaAccountMemberOfsourceHeaderList.DIRECT = 1 ; //direct membership group
ZaAccountMemberOfsourceHeaderList.INDIRECT = 2; //indirect/derived membership group
ZaAccountMemberOfsourceHeaderList.NON = 3; //non membership groups.

