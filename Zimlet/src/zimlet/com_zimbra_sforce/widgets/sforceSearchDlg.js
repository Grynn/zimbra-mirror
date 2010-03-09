function Com_Zimbra_SForceSearchDlg(zimlet) {
	this.zimlet = zimlet;
	this._shell = this.zimlet.getShell();
	this.objName == "";
	this.resultObjId = "";
	this.targetDivId = "";
}

Com_Zimbra_SForceSearchDlg.prototype.setProperties =
function(objName, targetDivId, selectMenuId, clearLinkId) {
	this._resetSearchDlg();
	this.objName =  objName;
	this.targetDivId = targetDivId;
	this.clearLinkId = clearLinkId;
	if(selectMenuId) {
		this.updateSelectMenu = true;
		this.selectMenuId = selectMenuId;		
	}
};

Com_Zimbra_SForceSearchDlg.prototype.setAssociationMenuCallback =
function(callback) {
	this.associationMenuCallback = callback;
}

Com_Zimbra_SForceSearchDlg.prototype.displaySearchDialog =
function() {
	//if zimlet dialog already exists...
	if (this.searchDialog) {
		this.searchDialog.setTitle(["Search to add/change an ", this.objName, " item"].join(""));
		this.searchDialog.popup();//simply popup the dialog
		return;
	}
	this.searchDlgView = new DwtComposite(this._shell);//creates an empty div thats a child of main shell div
	this.searchDlgView.setSize("400", "200");//set width and height
	this.searchDlgView.getHtmlElement().style.overflow = "auto";//adds scrollbar
	this.searchDlgView.getHtmlElement().innerHTML = this._createSearchDlgView();//insert  html for the dialogbox
	var title = ["Search to add/change an ", this.objName, " item"].join("");
	this.searchDialog = this.zimlet._createDialog({title:title, view:this.searchDlgView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	// add functionality to OK button
	this.searchDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._searchDlgOkBtnListner));
	this._addSearchDlgWidgets();
	//show the dialog
	this.searchDialog.popup();
};

Com_Zimbra_SForceSearchDlg.prototype._searchDlgOkBtnListner =
function() {
	var someRadioSelected = false;
	for(var i =0; i < this.searchResultRadioIds.length; i++) {
		var id = this.searchResultRadioIds[i].id;
		var name = this.searchResultRadioIds[i].name;
		if(document.getElementById(id).checked) {
			someRadioSelected = true;
			if(this.updateSelectMenu) {
				this._updateSelectmenu(name, id);
				break;
			} else {
				document.getElementById(this.targetDivId).innerHTML =  name;
				this.resultObjId = id;//store id in resultObjId variable
				document.getElementById(this.targetDivId).refObjIdValue = id;//set custom parameter
				document.getElementById(this.clearLinkId).style.display = "block";
				break;
			}
		}
	}
	if(!someRadioSelected) {
		document.getElementById(this.targetDivId).innerHTML =  "";
		this.resultObjId = "";//store id in resultObjId variable
		document.getElementById(this.targetDivId).refObjIdValue = "";//set custom parameter
		document.getElementById(this.clearLinkId).style.display = "none";
	}	
	this.updateSelectMenu = false;//reset
	this.searchResultRadioIds = [];
	this.searchDialog.popdown();
};

Com_Zimbra_SForceSearchDlg.prototype._updateSelectmenu =
function(name, id) {
	var elSel = document.getElementById(this.selectMenuId);
	var elOptNew = document.createElement('option');
	elOptNew.text = [this.objName, "-", name].join("");
	elOptNew.value =  [this.objName, "_", id].join("");
	var elOptOld = elSel.options[0];  
	if(AjxEnv.isIE) {
		elSel.add(elOptNew, 0);
	} else {
		elSel.add(elOptNew, elOptOld);
	}
	elOptNew.selected = true;


	if(this.associationMenuCallback) {
		this.associationMenuCallback.run(this);
	}
};

Com_Zimbra_SForceSearchDlg.prototype._resetSearchDlg =
function() {
	this.resultObjId = "";
	var dom = document.getElementById("sforce_searchResultsDiv");
	if(dom) {
		dom.innerHTML = "";
	}
};

Com_Zimbra_SForceSearchDlg.prototype._addSearchDlgWidgets =
function() {
	var btn = new DwtButton({parent:this._shell});
	btn.setText("Search");
	btn.addSelectionListener(new AjxListener(this, this._searchDlgSearchBtnHandler));
	document.getElementById("sforce_searchItemsButtonTd").appendChild(btn.getHtmlElement());
	Dwt.setHandler(document.getElementById("sforce_searchItemsField"), DwtEvent.ONKEYPRESS, AjxCallback.simpleClosure(this._searchDlgSearchBtnHandler, this));

};

Com_Zimbra_SForceSearchDlg.prototype._searchDlgSearchBtnHandler =
function(ev) {
	var event = ev || window.event;
	if (event.keyCode != undefined && event.keyCode != 13) {//if not enter key
		return;
	}
	if(this.objName == "") {
		this.objName = "Account";
	}
	var val = document.getElementById("sforce_searchItemsField").value;

	var callback = AjxCallback.simpleClosure(this._searchQueryListHandler, this);
	if(this.objName == "Case") {
		var q = ["select CaseNumber,Id,Subject from ",this.objName," where CaseNumber like '%",val,"%' limit 25"].join("");
	} else if(this.objName == "Contract") {
		var q = ["select ContractNumber,Id,Owner.Alias from ",this.objName," where ContractNumber like '%",val,"%' limit 25"].join("");
	}  else if(this.objName == "Product") {//objName for this is 'Product2' (not 'Product')
		var q = ["select Name,Id from Product2 where Name like '%",val,"%'"].join("");	
	}  else if(this.objName == "Solution") {
		var q = ["select SolutionName,Id from Solution where SolutionName like '%",val,"%' limit 25"].join("");	
	}   else if(this.objName == "Contact" || this.objName == "Lead") {
		var q = ["select Name,Phone,Email,Id from ",this.objName," where Name like '%",val,"%' limit 25"].join("");	
	} else {
		var q = ["select Name,Id from ",this.objName," where Name like '%",val,"%' limit 25"].join("");
	}
	document.getElementById("sforce_searchResultsDiv").innerHTML = this.zimlet._loadingSalesForceHtml;
	this.zimlet.query(q, 10, callback);
};

Com_Zimbra_SForceSearchDlg.prototype._searchQueryListHandler =
function(list) {
	var html = [];
	var i =0;
	this.searchResultRadioIds = [];
	html[i++] = "<div style='overflow:auto'>";
	html[i++] = "<table class='SForce_table' width=100% cellpadding=2 cellspacing=1>";
	if(this.objName == "Case") {
		html[i++] = "<TR class='overviewHeader sforce_steelTblBg'><TH>Select</TH><TH>CaseNumber</TH><TH>Subject</TH></TR>";
	} else 	if(this.objName == "Contract") {
		html[i++] = "<TR class='overviewHeader sforce_steelTblBg'><TH>Select</TH><TH>ContractNumber</TH><TH>Owner Alias</TH></TR>";
	}else {
		html[i++] = "<TR class='overviewHeader sforce_steelTblBg'><TH>Select</TH><TH>Name</TH></TR>";
	}
	
	var isOdd = true;
	for(var j=0; j < list.length; j++) {
		var item = list[j];
		var rId = item.Id.toString();
		if(this.objName == "Case") {
			var name = item.CaseNumber.toString();
			var subject = "";
			if(item.Subject) {
				subject = item.Subject.toString();
			}

		} else if(this.objName == "Contract") {
			var name = item.ContractNumber.toString();
			var ownerAlias = "";
			if(item.Owner) {
				ownerAlias = item.Owner.Alias.toString();
			}

		} else if(this.objName == "Solution") {
			var name = item.SolutionName.toString();
		} else if(this.objName == "Contact" || this.objName == "Lead") {
			var name = item.Name.toString();
			var h = [];
			var k =0;
			h[k++] = "<table>";
			h[k++] = ["<tr><td><strong>Name:</strong> </td><td>", name, "</td></tr>"].join("");
			var email = item.Email;
			if(email) {
				email = email.toString();
				h[k++] = ["<tr><td><strong>Email:</strong> </td><td> ", email, "</td></tr>"].join("");
			} else {
				email = "";
			}
			var phone = item.Phone;
			if(phone) {
				phone = phone.toString();
				h[k++] = ["<tr><td><strong>Phone:</strong>  </td><td>", phone, "</td></tr>"].join("");
			} else {
				phone = "";
			}
			h[k++] = "</table>";
			var info = h.join("");						
		} else {
			var name = item.Name.toString();
		}
		
		if(this.updateSelectMenu) {
			this.searchResultRadioIds.push({id:rId, name:name});
		} else {
			 if(this.objName == "Contact" || this.objName == "Lead") {
				this.searchResultRadioIds.push({id:rId, name:info});//set info
			 } else {
				this.searchResultRadioIds.push({id:rId, name:name});
			 }
		}
		if(isOdd) {
			html[i++] = ["<tr  class='RowOdd'>"].join("");
			isOdd =  false;
		} else {
			html[i++] = ["<tr  class='RowEven'>"].join("");
			isOdd =  true;
		}
		if(this.objName == "Case"){
			html[i++] = ["<td width=5px><input type ='radio' name='sforce_searchToAddRadio' id='",rId,"'></input></td><td>",	name,"</td><td>",subject,"</td>"].join("");
		} else if(this.objName == "Contract") {
			html[i++] = ["<td width=5px><input type ='radio'  name='sforce_searchToAddRadio' id='",rId,"'></input></td><td>",	name,"</td><td>",ownerAlias,"</td>"].join("");
		}else if(this.objName == "Contact" || this.objName == "Lead") {
			html[i++] = ["<td width=5px><input type ='radio'  name='sforce_searchToAddRadio' id='",rId,"'></input></td><td>",	name," <label style='color:darkBlue'>", email, "</label><label style='color:green'> ",phone,"</label></td>"].join("");
		}else {
			html[i++] = ["<td width=5px><input type ='radio'  name='sforce_searchToAddRadio' id='",rId,"'></input></td><td>",	name,"</td>"].join("");
		}

	}
	if(list.length == 0) {
		html[i++] = ["<tr><td>No results found<td></tr>"].join("");
	}
	html[i++] = "</table>";
	html[i++] = "</div>";
	document.getElementById("sforce_searchResultsDiv").innerHTML = html.join("");
};

Com_Zimbra_SForceSearchDlg.prototype._createSearchDlgView =
function() {
	var html = [];
	var i =0;
	html[i++] = "<div align='center'>";
	html[i++] = "<Strong>Tip: You can do partial or empty string search as well.<Strong>";
	html[i++] = "<div>";
	
	html[i++] = "<div align='center'>";
	html[i++] = "<table align='center' class='SForce_table'><tr><td><input type='text' id='sforce_searchItemsField' ></input></td>";
	html[i++] = "<td id='sforce_searchItemsButtonTd'></td><tr></table>";
	html[i++] = "</div>";
	html[i++] = "<div id='sforce_searchResultsDiv'>";
	html[i++] = "</div>";

	return html.join("");
	
};