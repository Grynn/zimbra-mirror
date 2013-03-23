/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 VMware, Inc.
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
* @constructor
* @class ZaAccountListView
* @param parent
* @param listType: the account type of the list: alias or other types
* @author Roland Schemers
* @author Greg Solovyev
**/
ZaAccountListView = function(parent,listType) {
	var className = null;
	var posStyle = DwtControl.ABSOLUTE_STYLE;
	this._listType = listType ;
	var headerList = this._getHeaderList();
	
	var listViewId;
	if(!this._listType || this._listType == ZaItem.ACCOUNT)
		listViewId = ZaId.TAB_ACCT_MANAGE;
	else if(this._listType == ZaItem.ALIAS)
		listViewId = ZaId.TAB_ALIAS_MANAGE;
	else if(this._listType == ZaItem.DL)
		listViewId = ZaId.TAB_DL_MANAGE;
	else if(this._listType == ZaItem.RESOURCE)
		listViewId = ZaId.TAB_RES_MANAGE;
	else listViewId = ZaId.TAB_UNDEF;

	ZaListView.call(this, {
		parent:parent, 
		className:className, 
		posStyle:posStyle, 
		headerList:headerList, 
		id:listViewId,
		scrollLoading:true
	});

	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	
	this._domains = {} ;
}

ZaAccountListView.prototype = new ZaListView;
ZaAccountListView.prototype.constructor = ZaAccountListView;

ZaAccountListView.prototype.toString = 
function() {
	return "ZaAccountListView";
}

ZaAccountListView.prototype.getTitle = 
function () {
	var title = ZaMsg.Addresses_view_title ;
	var cc = ZaApp.getInstance().getControllerById (this.__internalId) ;
	switch (cc._defaultType) {
		case ZaItem.DL :
			title = ZaMsg.DL_view_title; break ;
		case ZaItem.ALIAS :
			title = ZaMsg.Aliases_view_title; break ;
		case ZaItem.RESOURCE : 
			title = ZaMsg.Resourse_view_title; break ;	
		default :
			title = ZaMsg.Accounts_view_title ;
	}	
	
	return title;
}

ZaAccountListView.prototype.getTabIcon =
function () {
	var icon = null ;
	var cc = ZaApp.getInstance().getControllerById (this.__internalId) ;
	switch (cc._defaultType) {
		case ZaItem.DL :
			icon = "DistributionList"; break ;
		case ZaItem.ALIAS :
			icon = "AccountAlias" ; break ;
		case ZaItem.RESOURCE :
			icon = "Resource" ; break ;
		default :
			icon = "Account" ;
	}

	return icon ;
}

/**
* Renders a single item as a DIV element.
*/
ZaAccountListView.prototype._createItemHtml =
function(account, now, isDragProxy) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(account, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'>";

	html[idx++] = "<tr>";

	var cnt = this._headerList.length;
		var dwtId = Dwt.getNextId();
	var rowId = this._listType;
	for(var i = 0; i < cnt; i++) {
		var field = this._headerList[i]._field;
		var cellWidth = this._getCellWidth(i, {});

		if(field == "type") {
			// type
			html[idx++] = "<td id=\"" + rowId + "_data_type_" + dwtId + "\" width=" + cellWidth + ">";
			switch(account.type) {
				case ZaItem.ACCOUNT:
					if(account.attrs[ZaAccount.A_zimbraIsAdminAccount]=="TRUE" ) {
						html[idx++] = AjxImg.getImageHtml("AdminUser");
					} else if (account.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount] == "TRUE") {
						html[idx++] = AjxImg.getImageHtml("DomainAdminUser");
					} else if (account.attrs[ZaAccount.A_zimbraIsSystemAccount] == "TRUE") {
						html[idx++] = AjxImg.getImageHtml("SpecialAccount");
					} else if (account.attrs[ZaAccount.A_zimbraIsSystemResource] == "TRUE") {
						html[idx++] = AjxImg.getImageHtml("SystemResource");
					} else if (account.attrs[ZaAccount.A_zimbraIsExternalVirtualAccount] == "TRUE") {
						html[idx++] = AjxImg.getImageHtml("AccountExternalVirtual");
					} else {
						if(account.isExternal) {
							html[idx++] = AjxImg.getImageHtml("AccountIMAP");	
						} else {
							html[idx++] = AjxImg.getImageHtml("Account");
						}
					}
				break;
				case ZaItem.DL:
					if (account.attrs[ZaDistributionList.A_isAdminGroup] == "TRUE") {
						html[idx++] = AjxImg.getImageHtml("DistributionListGroup");
					} else {
						html[idx++] = AjxImg.getImageHtml("DistributionList");
					}
				break;
				case ZaItem.ALIAS:
					html[idx++] = AjxImg.getImageHtml("AccountAlias");
				break;
				case ZaItem.RESOURCE:
					if (account.attrs[ZaResource.A_zimbraCalResType] == ZaResource.RESOURCE_TYPE_LOCATION){
						html[idx++] = AjxImg.getImageHtml("Location");
					}else {//equipment or other resource types
						html[idx++] = AjxImg.getImageHtml("Resource");
					}
				break;
				default:
					html[idx++] = account.type;
				break;
			}
			html[idx++] = "</td>";
		} else if(field == ZaAccount.A_name) {
			// name
			html[idx++] = "<td id=\"" + rowId + "_data_emailaddress_" + dwtId + "\" nowrap width=" + cellWidth + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.name);
			html[idx++] = "</nobr></td>";
		} else if (field == ZaAccount.A_displayname) {
			// display name
			html[idx++] = "<td id=\"" + rowId + "_data_displayname_" + dwtId + "\" nowrap width=" + cellWidth + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAccount.A_displayname]);
			html[idx++] = "</nobr></td>";	
		} else if(field == ZaAccount.A_accountStatus) {
			// status
			html[idx++] = "<td id=\"" + rowId + "_data_status_" + dwtId + "\" width=" + cellWidth + "><nobr>";
			var status = "";
			if (account.type == ZaItem.ACCOUNT) {
				status = ZaAccount._accountStatus(account.attrs[ZaAccount.A_accountStatus]);
			} else if (account.type == ZaItem.DL) {
				status = ZaDistributionList.getDLStatus (account.attrs.zimbraMailStatus);
			}else if ( account.type == ZaItem.RESOURCE) {
				status = ZaResource.getAccountStatusLabel(account.attrs[ZaAccount.A_accountStatus]);
			} 
			html[idx++] = status;
			html[idx++] = "</nobr></td>";		
		} else if (field == ZaAccount.A_zimbraLastLogonTimestamp) {
			// display last login time for accounts only
			html[idx++] = "<td id=\"" + rowId + "_data_lastlogontime_" + dwtId + "\" nowrap width=" + cellWidth + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(ZaAccount.getLastLoginTime(account.attrs[ZaAccount.A_zimbraLastLogonTimestamp]));
			html[idx++] = "</nobr></td>";	
		} else if (field == ZaAccount.A_description) {
			// description
			html[idx++] = "<td id=\"" + rowId + "_data_description_" + dwtId + "\" width=" + cellWidth + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(
				ZaItem.getDescriptionValue(account.attrs[ZaAccount.A_description] ));
			html[idx++] = "</nobr></td>";	
		} else if (field == "target" + ZaAlias.A_targetType) {
			html[idx++] = "<td id=\"" + rowId + "_data_targettype_" + dwtId + "\" width=" + cellWidth + "><nobr>";
			var targetType = account.attrs[ZaAlias.A_targetType] ;
			var targetType_desc ;
			if (targetType == ZaItem.ACCOUNT ) {
				targetType_desc = ZaMsg.aliasTargetTypeAccount ;
			}else if (targetType == ZaItem.DL) {
				targetType_desc = ZaMsg.aliasTargetTypeDL ;
			}else if (targetType = ZaItem.RESOURCE) {
				targetType_desc = ZaMsg.aliasTargetTypeResource ;
			}
			html[idx++] = AjxStringUtil.htmlEncode(targetType_desc);
			html[idx++] = "</nobr></td>";
		} else if (field == ZaAlias.A_targetAccount) {
			html[idx++] = "<td id=\"" + rowId + "_data_targetaccount_" + dwtId + "\" width=" + cellWidth + "><nobr>";
			html[idx++] = AjxStringUtil.htmlEncode(account.attrs[ZaAlias.A_targetAccount]);
			html[idx++] = "</nobr></td>";
		}
	}
		html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}

ZaAccountListView.prototype._getHeaderList =
function(listType) {

	var headerList = new Array();
	var sortable = 1;
	var i = 0 ;
	headerList[i++] = new ZaListHeaderItem("type", null, null, "22px", null, "objectClass", false, true);
	this._defaultColumnSortable = sortable ;
	headerList[i++] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, "210px", sortable++, ZaAccount.A_name, true, true);

    // TODO: Optimise the if...else ladder
    if (this._listType) {
        if (this._listType == ZaItem.ALIAS) {
            headerList[i++] = new ZaListHeaderItem(ZaAlias.A_targetAccount, ZaMsg.ALV_TargetName_col, null, "210px", sortable++,ZaAlias.A_targetAccount, true, true);
            headerList[i++] = new ZaListHeaderItem("target" + ZaAlias.A_targetType, ZaMsg.ALV_TargetType_col, null, "200px", sortable++,ZaAlias.A_targetType, true, true);
        } else {
            headerList[i++] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, "210px", sortable++,ZaAccount.A_displayname, true, true);

            if (this._listType == ZaItem.DL) {
                headerList[i++] = new ZaListHeaderItem(ZaAccount.A_accountStatus, ZaMsg.ALV_Status_col, null, "120px", null, ZaAccount.A_accountStatus, true, true);
            } else {
                headerList[i++] = new ZaListHeaderItem(ZaAccount.A_accountStatus, ZaMsg.ALV_Status_col, null, "120px", sortable++,ZaAccount.A_accountStatus, true, true);
            }

            headerList[i++] = new ZaListHeaderItem(ZaAccount.A_zimbraLastLogonTimestamp, ZaMsg.ALV_Last_Login, null, "195px", sortable++, ZaAccount.A_zimbraLastLogonTimestamp, true, true);
        }
    } else{
		//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible	
		headerList[i++] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, "210px", sortable++,ZaAccount.A_displayname, true, true);
		headerList[i++] = new ZaListHeaderItem(ZaAccount.A_accountStatus, ZaMsg.ALV_Status_col, null, "120px", sortable++,ZaAccount.A_accountStatus, true, true);
		headerList[i++] = new ZaListHeaderItem(ZaAccount.A_zimbraLastLogonTimestamp, ZaMsg.ALV_Last_Login, null, "195px", sortable++, ZaAccount.A_zimbraLastLogonTimestamp, true, true);
	}
	headerList[i++] = new ZaListHeaderItem(ZaAccount.A_description, ZaMsg.ALV_Description_col, null, "auto", null, null,false, true );
	
	return headerList;
}


ZaAccountListView.prototype._sortColumn = 
function(columnItem, bSortAsc) {
	try {

		if(this._listType == ZaItem.DL) {
			viewId=ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW;
		} else if (this._listType == ZaItem.RESOURCE){
			viewId=ZaZimbraAdmin._RESOURCE_LIST_VIEW;
		} else if(this._listType == ZaItem.ALIAS) {
			viewId=ZaZimbraAdmin._ALIASES_LIST_VIEW;
		} else {
			viewId=ZaZimbraAdmin._ACCOUNTS_LIST_VIEW;
		}	
		
		var acctListController = ZaApp.getInstance().getAccountListController(viewId);
			
		acctListController.setSortOrder(bSortAsc);
		acctListController.setSortField(columnItem.getSortField());
		acctListController.show();
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex);
	}
}
