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
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaAlias(app) {
	ZaItem.call(this, app);
	this.attrs = new Object();
	this.id = "";
	this.name="";
}

ZaAlias.prototype = new ZaItem;
ZaAlias.prototype.constructor = ZaAlias;
ZaAlias.A_AliasTargetId = "zimbraAliasTargetId";
ZaAlias.A_targetAccount = "targetAccount";

ZaItem._ATTR[ZaAlias.A_targetAccount] = ZaMsg.attrDesc_aliasFor;

ZaAlias.prototype.remove = 
function() {
	var soapDoc = AjxSoapDoc.create("RemoveAccountAliasRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.attrs[ZaAlias.A_AliasTargetId]);
	soapDoc.set("alias", this.name);	
	ZmCsfeCommand.invoke(soapDoc, null, null, null, true);	
}

/**
* Returns HTML for a tool tip for this account.
*/
ZaAlias.prototype.getToolTip =
function() {
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0'>";
		html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
		html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap; overflow:hidden;width:350' >";
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' style='width:100%;'>";
		html[idx++] = "<tr valign='center'>";
		html[idx++] = "<td><b>" + AjxStringUtil.htmlEncode(this.name) + "</b></td>";
		html[idx++] = "<td align='right'>";
		html[idx++] = AjxImg.getImageHtml("AccountAlias");		
		html[idx++] = "</td>";
		html[idx++] = "</table></div></td></tr>";
		html[idx++] = "<tr></tr>";
		//get my account
		var account = this._app.getAccountList().getItemById(this.attrs[ZaAlias.A_AliasTargetId]);
		if(account) {
			idx = this._addRow(ZaItem._attrDesc(ZaAlias.A_targetAccount), 
						account.attrs[ZaAccount.A_displayname], html, idx);
		
		}
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}
