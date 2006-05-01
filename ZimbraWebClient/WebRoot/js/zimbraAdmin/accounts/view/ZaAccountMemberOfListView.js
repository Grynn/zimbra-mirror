/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* This class describes a List view of an account's Member Of tab
* @class ZaAccountMemberOfListView
* @contructor ZaAccountMemberOfListView
* @author Charles Cao
**/
function ZaAccountMemberOfListView(parent, className, posStyle, headerList){
	ZaListView.call(this, parent, className, posStyle, headerList);
}

ZaAccountMemberOfListView.prototype = new ZaListView ;
ZaAccountMemberOfListView.prototype.constructor = ZaAccountMemberOfListView ;
ZaAccountMemberOfListView.prototype.toString = function (){
	return "ZaAccountMemberOfListView";
};

ZaAccountMemberOfListView.A_name = "name" ;
ZaAccountMemberOfListView.A_isgroup = "isgroup" ;
ZaAccountMemberOfListView.A_via = "via" ;

/**
 * @param app
 * @param val {account value corresponding to by}
 * @param by  {either by id or name} 
 * @return the memberOf object 
 * 				{ 	directMemberList: [ { name: dl1@test.com, id: 394394, isgroup: true } , {..}, ...] ,
 * 					indirectMemberList: [ { name: dl1@test.com, id: 394394, via: dl2@test.com, isgroup: true } , {..}, ...] ,
 * 					nonMemberList: [ { name: dl1@test.com, id: 394394, isgroup: false } , {..}, ...],
 * 					isgroup: true  
 * 				}
 * 					
 */
ZaAccountMemberOfListView.getAccountMemberShip = 
function (app, val, by){
	var directML = [];
	var indirectML = [];
	var nonML = [];
		 
	try {
		soapDoc = AjxSoapDoc.create("GetAccountMembershipRequest", "urn:zimbraAdmin", null);
		var elBy = soapDoc.set("account", val);
		elBy.setAttribute("by", by);

		var getAccMemberShipCommand = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		var resp = getAccMemberShipCommand.invoke(params).Body.GetAccountMembershipResponse;
		if (resp.dl && (resp.dl instanceof Array)){
			var dls = resp.dl ;
			var n = resp.dl.length ;
			for (var i=0, d=0, m=0; m < n; m++ ){
				//if (dls[m].isgroup) {
				if (dls[m].via && (dls[m].via.length >0)){ //indirect dl
					indirectML[i] = { name: dls[m].name, id: dls[m].id, via: dls[m].via, isgroup: dls[m].isgroup} ;
					i ++ ;
				}else{
					directML[d] = { name: dls[m].name, id: dls[m].id, isgroup: dls[m].isgroup } ;
					d ++ ;
				}
			}
		}

	}catch (ex){
		app.getCurrentController()._handleException(ex, "ZaAccount.prototype.load", null, false);
	}
	
	var memberOf = {	directMemberList: directML,
						indirectMemberList: indirectML,
						nonMemberList: nonML
					};
	return memberOf ;
}


/**
 * When the showGroupOnly is checked, only the group shows. Otherwise, all the DLs will be displayed.
 */
ZaAccountMemberOfListView.onShowGroupOnlyChanged =
function(value, event, form){
	//change the memberOf instance
	var instance = form.getInstance();
	var isOnlyShowGroup = (value == "TRUE") ? true : false ;
	var memberOf = null ;
	/*
	instance.memberOf = {	directMemberList: [],
						indirectMemberList: [],
						nonMemberList: []						
						}; */
						
	//the list value must be changed to update the lists
	/*
	if (isOnlyShowGroup){
		instance [ZaAccount.A2_memberOf] = ZaAccountMemberOfListView.getGroupOnly(instance [ZaAccount.A2_memberOf]) ;
	} else { //need to retrieve the value from server again
		
	}*/
	//instance[ZaAccount.A2_memberOf][ZaAccountMemberOfListView.A_isgroup] =  isOnlyShowGroup;
	instance[ZaAccount.A2_memberOf]["showGroupOnlyAction"] = true ; //turn on the flag
	this.setInstanceValue(value);
	form.setInstance(instance);
	instance[ZaAccount.A2_memberOf]["showGroupOnlyAction"] = false ; //turn off the flag
	return value;
}

/**
 * search the directory for all the distribution lists when the search button is clicked.
 */
ZaAccountMemberOfListView.prototype.srchButtonHndlr =
function (){
	//the preassumption is that both memberOf is the name of the attr of the instance 
	var xform = this.getForm() ; //this refers to a xform item
	if (xform){
		var curInstance = xform.getInstance();
		var memberOfObj = curInstance[ZaAccount.A2_memberOf] ;
		try {
			var sortby = ZaAccount.A_name ; 
			var searchByDomain = (memberOfObj [ZaAccount.A2_showSameDomain] == "TRUE") ? true : false ;
			var domainName = null;
			if (searchByDomain){
				var curEmail = xform.parent._containedObject.name ;
				domainName = curEmail.substring (curEmail.indexOf("@")+1);
			}
			var attrs = [ZaAccount.A_name, ZaItem.A_zimbraId];
			//var attrs = [""];
			var valStr = curInstance[ZaSearch.A_query];
			var query = ZaSearch.getSearchByNameQuery(valStr);
			var params = { 	query: query ,
							sortBy: sortby,
							limit : 0,
							offset: 0,
							domain: domainName,
							applyCos: 0,
							attrs: attrs,
							types: [ZaSearch.DLS]
						 }
					
			var result = ZaSearch.searchDirectory(params).Body.SearchDirectoryResponse;
		
			var list = new ZaItemList(ZaDistributionList, null);
			list.loadFromJS(result);
			var arr = list.getArray();
			
			var nonMemberList = new Array();
			for(var i=0; i<arr.length; i++) {
				nonMemberList.push({
									name: arr[i].name,
									id: arr[i].id,
									isgroup: arr[i].isgroup					
									});
			}
				
			memberOfObj[ZaAccount.A2_nonMemberList] = nonMemberList ;			
			xform.setInstance(curInstance) ;
		}catch (ex){
			xfrom.parent._app.getCurrentController()._handleException(
				ex, "ZaAccountMemberOfListView.prototype.srchButtonHndlr");
		}	
		
		return true;
	}
}

ZaAccountMemberOfListView.prototype._createItemHtml = function (group, now, isDndIcon){
	var html = new Array(50);
	var	div = document.createElement("div");
	div._styleClass = "Row";
	div._selectedStyleClass = div._styleClass + "-" + DwtCssStyle.SELECTED;
	div.className = div._styleClass;
	this.associateItemWithElement(group, div, DwtListView.TYPE_LIST_ITEM);
	
	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";

	html[idx++] = "<tr>";
	if(this._headerList) {
		var cnt = this._headerList.length;
		for(var i = 0; i < cnt; i++) {
			//if ()
			var id = this._headerList[i]._id;
			if(id.indexOf(ZaAccountMemberOfListView.A_name) == 0) {
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(group[ZaAccountMemberOfListView.A_name]);				
				html[idx++] = "</td>";			
			}  
			else if(id.indexOf(ZaAccountMemberOfListView.A_isgroup) == 0) {
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(group[ZaAccountMemberOfListView.A_isgroup] ? ZaMsg.Yes : ZaMsg.No);
				html[idx++] = "</td>";
			}
			else if(id.indexOf(ZaAccountMemberOfListView.A_via) == 0) {
				html[idx++] = "<td width=" + this._headerList[i]._width + ">";
				html[idx++] = AjxStringUtil.htmlEncode(group[ZaAccountMemberOfListView.A_via]);
				html[idx++] = "</td>";
			} 
			 
		}
	} else {
		html[idx++] = "<td width=100%>";
		html[idx++] = AjxStringUtil.htmlEncode(group[ZaAccountMemberOfListView.A_name]);
		html[idx++] = "</td>";
	}
	
	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
};

ZaAccountMemberOfListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");
	
	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'>",
				  AjxStringUtil.htmlEncode(ZaMsg.Account_Group_NoMember),
				  "</td></tr></table>");
	
	div.innerHTML = buffer.toString();
	this._addRow(div);
};

ZaAccMiniListView.prototype._sortColumn = function (columnItem, bSortAsc) {
	
};



/**
 * Customized Dwt_list for MemberShip list view. It is specialized, so the show group only check box can filter
 * the non group dls. 
 * 
 */
function S_Dwt_List_XFormItem(){}
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
	var isGroupOnlyCkbAction = instance[ZaAccount.A2_memberOf]["showGroupOnlyAction"];
	var isGroupOnly = instance[ZaAccount.A2_memberOf][ZaAccountMemberOfListView.A_isgroup];
	
	if (itemArray && itemArray.length > 0) {	
		//we have to compare the objects, because XForm calls this method every time an item in the list is selected
		if((itemArray.join() != existingArr.join()) || (isGroupOnlyCkbAction)) {
			var preserveSelection = this.getInheritedProperty("preserveSelection");
			var selection = null;
			if(preserveSelection) {
				selection = this.widget.getSelection();
			}		
			var cnt=itemArray.length;
			for(var i = 0; i< cnt; i++) {
				//check whether the group only is applied
				if (isGroupOnly == "TRUE" ){
					if (itemArray[i][ZaAccountMemberOfListView.A_isgroup]) {
						tmpArr.push(itemArray[i]);
					}
				}else {
					tmpArr.push(itemArray[i]);		
				}
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
function ZaAccountMemberOfsourceHeaderList (type) {
	var sourceHeaderList = new Array();
	var sortable = 0;
	
//	defaultColumnSortable = sortable ;
	sourceHeaderList[0] = new ZaListHeaderItem(ZaAccountMemberOfListView.A_name, 	ZaMsg.CLV_Name_col, 	
												null, 200, sortable++, ZaAccountMemberOfListView.A_name, true, true);
	
	var isgroupWidth = (type == ZaAccountMemberOfsourceHeaderList.INDIRECT) ? 50 : null ;
	sourceHeaderList[1] = new ZaListHeaderItem(ZaAccountMemberOfListView.A_isgroup,   	ZaMsg.Account_Group,   	
	 											null, isgroupWidth,  null,  ZaAccountMemberOfListView.A_isgroup, true, true);
	
	if (type == ZaAccountMemberOfsourceHeaderList.INDIRECT) { 																							
		sourceHeaderList[2] = new ZaListHeaderItem(ZaAccountMemberOfListView.A_via,   	ZaMsg.Group_via,   	
	 											null, null,  null,  ZaAccountMemberOfListView.A_via, true, true);
	}
	
	return sourceHeaderList ;
}

ZaAccountMemberOfsourceHeaderList.DIRECT = 1 ; //direct membership group
ZaAccountMemberOfsourceHeaderList.INDIRECT = 2; //indirect/derived membership group
ZaAccountMemberOfsourceHeaderList.NON = 3; //non membership groups.

