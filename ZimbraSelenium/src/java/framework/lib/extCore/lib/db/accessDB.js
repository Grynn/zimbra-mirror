var zmAccessDB = function() {
	this.currentUIMsg = "";
	this.currentTimeTaken = "";
	this.paramsArry = new Array();
	this.url = "/zmBrowserPerfServer/signup";
	if (window.XMLHttpRequest) {
		this.req = new XMLHttpRequest();
	}
	else if (window.ActiveXObject) {
		this.req = new ActiveXObject("Microsoft.XMLHTTP");
	}

}

zmAccessDB.prototype.setCurrentUIMsg =
function(msg){
	this.currentUIMsg = msg;
}

zmAccessDB.prototype.reset =
function(){
	this.paramsArry = new Array();
	this.currentUIMsg = "";
	this.currentTimeTaken = "";
}
zmAccessDB.prototype.setTimeTakenAndCommit =
function(timetaken){
	this.currentTimeTaken = timetaken;
	this.storeDBVal(this.currentUIMsg, this.currentTimeTaken);
}

/*
zmAccessDB.prototype.postPerfInfo =
function(){
	var me = this;
	this.dbparams = this.paramsArry.join("&");  //join all the params with &
	//this.req.open("POST", this.url, true);

//Send the proper header information along with the request
//	this.req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
//	this.req.setRequestHeader("Content-length", this.dbparams.length);
//	this.req.setRequestHeader("Connection", "close");
//	this.req.onreadystatechange = function() {
//		me.showLogCallback();
//	};
//	this.req.send(this.dbparams);
	var hdrs = new Array();
	hdrs["content-type"] =  "application/x-www-form-urlencoded";
	hdrs["content-length"] =   this.dbparams.length;
	//hdrs["Connection"] = "close";
	var win = selenium.browserbot.getCurrentWindow();
	var url= "http://qa61.liquidsys.com:8080/zmBrowserPerfServer/signup";
	//var url=  "http://qa61.liquidsys.com:8080/zmBrowserPerfServer/signup";
	url = win.ZmZimletBase.PROXY + win.AjxStringUtil.urlComponentEncode(url);
	var myPlannerClbk = new win.AjxCallback(this, this.showLogCallback);

	win.AjxRpc.invoke(this.dbparams, url, hdrs, myPlannerClbk);//do post
}
*/

zmAccessDB.prototype.postPerfInfo =
function(){
	var me = this;
	this.dbparams = this.paramsArry.join("&");  //join all the params with &
	this.req.open("POST", "/service/proxy?target=http%3A%2F%2Fqa61.liquidsys.com%3A8080%2FzmBrowserPerfServer%2Fsignup", true);

//Send the proper header information along with the request
	this.req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	this.req.setRequestHeader("Content-length", this.dbparams.length);
	this.req.setRequestHeader("Connection", "close");
	this.req.onreadystatechange = function() {
		me.showLogCallback();
	};
	this.req.send(this.dbparams);
}

/*
zmAccessDB.prototype.postPerfInfo =
function(){
	var me = this;
	this.dbparams = this.paramsArry.join("&");  //join all the params with &
	this.req.open("POST", this.url, true);

//Send the proper header information along with the request
	this.req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	this.req.setRequestHeader("Content-length", this.dbparams.length);
	this.req.setRequestHeader("Connection", "close");
	this.req.onreadystatechange = function() {
		me.showLogCallback();
	};
	this.req.send(this.dbparams);
}
*/
/*
zmAccessDB.prototype.postPerfInfo =
function(){

	var params = new Array();
	params.push("login=1")
	params.push("mailcompose_1=1");
	params.push("mailcompose_2=1");
	params.push("mailcompose_3=1");
	params.push("compose_new_window_1=1");
	params.push("compose_new_window_2=1");
	params.push("goto_calendar=1");
	params.push("goto_contacts=1");
	params.push("goto_tasks=1");
	params.push("goto_docs=1");
	params.push("goto_pref=1");
	params.push("goto_mail=1");
	params.push("goto_calendar_2=1");
	params.push("goto_contacts_2=1");
	params.push("goto_tasks_2=1");
	params.push("goto_docs_2=1");
	params.push("goto_pref_2=1");
	params.push("goto_mail_2=1");
	params.push("cal_schedule_tab=1");
	params.push("cal_attendees_tab=1");
	params.push("cal_locations_tab=1");
	params.push("cal_resources_tab=1");
	params.push("cal_schedule_tab_2=1");
	params.push("cal_attendees_tab_2=1");
	params.push("cal_locations_tab_2=1");
	params.push("cal_resources_tab_2=1");
	params.push("pref_mail_tab=1");
	params.push("pref_compose_tab=1");
	params.push("pref_signature_tab=1");
	params.push("pref_addressbook_tab=1");
	params.push("pref_accounts_tab=1");
	params.push("pref_calendar_tab=1");
	params.push("pref_shortcuts_tab=1");
	params.push("contacts_card_view=1");
	params.push("contacts_list_view=1");
	params.push("contacts_card_view_2=1");
	params.push("contacts_list_view_2=1");
	params.push("click_sent=1");
	params.push("click_trash=1");
	params.push("click_inbox=1");
	params.push("click_drafts=1");
	params.push("click_junk=1");
	params.push("click_sent_2=1");
	params.push("click_trash_2=1");
	params.push("click_inbox_2=1");
	params.push("click_drafts_2=1");
	params.push("click_junk_2=1");
	params.push("mail_msg_view=1");
	params.push("mail_conv_view=1");
	params.push("mail_msg_view_2=1");
	params.push("mail_conv_view_2=1");
	params = params.join("&");  //join all the params with &
	this.req.open("POST", this.url, true);

//Send the proper header information along with the request
	this.req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	this.req.setRequestHeader("Content-length", params.length);
	this.req.setRequestHeader("Connection", "close");
	this.req.onreadystatechange = this.callbackCityState;
	this.req.send(params);
}
*/
zmAccessDB.prototype.showLogCallback =
function() {
	if (this.req.readyState == 4) {
		if (this.req.status == 200) {
		LOG.debug("Perf info was posted(not sure if its succeded or not)");

		}
	}
}


zmAccessDB.prototype.storeDBVal =
function(uiname,val){
	var dbname = "";
	if(uiname=="Login to Mail app..")dbname="login";
	if(uiname=="Open Mail Compose..")dbname="mailcompose_1";
	if(uiname=="Open Mail Compose(2nd time)..")dbname="mailcompose_2";
	if(uiname=="Open Mail Compose(3rd time)..")dbname="mailcompose_3";
	if(uiname=="Open Mail Compose (in New Window)..")dbname="compose_new_window_1";
	if(uiname=="Open Mail Compose (in New Window) 2nd time..")dbname="compose_new_window_2";
	if(uiname== "go to 'Calendar'..")dbname="goto_calendar";
	if(uiname== "go to 'Address Book'..")dbname="goto_contacts";
	if(uiname== "go to 'Tasks'..")dbname="goto_tasks";
	if(uiname== "go to 'Documents'..")dbname="goto_docs";
	if(uiname== "go to 'Preferences'..")dbname="goto_pref";
	if(uiname== "go to 'Mail'..")dbname="goto_mail";
	if(uiname== "go to 'Calendar' 2nd time..")dbname="goto_calendar_2";
	if(uiname== "go to 'Address Book' 2nd time..")dbname="goto_contacts_2";
	if(uiname== "go to 'Tasks' 2nd time..")dbname="goto_tasks_2";
	if(uiname== "go to 'Documents' 2nd time..")dbname="goto_docs_2";
	if(uiname== "go to 'Preferences' 2nd time..")dbname="goto_pref_2";
	if(uiname== "go to 'Mail' 2nd time..")dbname="goto_mail_2";
	if(uiname=="Cal_Schedule tab..")dbname="cal_schedule_tab";
	if(uiname=="Cal_FindAttendees tab..")dbname="cal_attendees_tab";
	if(uiname=="Cal_FindLocations tab..")dbname="cal_locations_tab";
	if(uiname=="Cal_FindResources tab..")dbname="cal_resources_tab";
	if(uiname=="Cal_Schedule tab(2nd time)..")dbname="cal_schedule_tab_2";
	if(uiname=="Cal_FindAttendees tab(2nd time)..")dbname="cal_attendees_tab_2";
	if(uiname=="Cal_FindAttendees tab(2nd time)..")dbname="cal_locations_tab_2";
	if(uiname=="Cal_FindResources tab(2nd time)..")dbname="cal_resources_tab_2";
	if(uiname=="Click Preferences_Mail tab")dbname="pref_mail_tab";
	if(uiname=="Click Preferences_Composing tab")dbname="pref_compose_tab";
	if(uiname=="Click Preferences_Signatures tab")dbname="pref_signature_tab";
	if(uiname=="Click Preferences_Address Book tab")dbname="pref_addressbook_tab";
	if(uiname=="Click Preferences_Accounts tab")dbname="pref_accounts_tab";
	if(uiname=="Click Preferences_Calendar tab")dbname="pref_calendar_tab";
	if(uiname=="Click Preferences_Shortcuts tab")dbname="pref_shortcuts_tab";
	if(uiname=="Change to Contacts_Card view..")dbname="contacts_card_view";
	if(uiname=="Change to Contacts_List view..")dbname="contacts_list_view";
	if(uiname=="Change to Contacts_Card view(2nd time)..")dbname="contacts_card_view_2";
	if(uiname=="Change to Contacts_List view(2nd time)..")dbname="contacts_list_view_2";
	if(uiname=="Change to Message-view..")dbname="mail_msg_view";
	if(uiname=="Change to Conversation-view..")dbname="mail_conv_view";
	if(uiname=="Change to Message-view(2nd time)..")dbname="mail_msg_view_2";
	if(uiname=="Change to Conversation-view(2nd time)..")dbname="mail_conv_view_2";
	if(uiname=="Click Sent folder..")dbname="click_sent";
	if(uiname=="Click Trash folder..")dbname="click_trash";
	if(uiname=="Click Inbox folder..")dbname="click_inbox";
	if(uiname=="Click Drafts folder..")dbname="click_drafts";
	if(uiname=="Click Junk folder..")dbname="click_junk";
	if(uiname=="Click Sent folder(2nd time)..")dbname="click_sent_2";
	if(uiname=="Click Trash folder(2nd time)..")dbname="click_trash_2";
	if(uiname=="Click Inbox folder(2nd time)..")dbname="click_inbox_2";
	if(uiname=="Click Drafts folder(2nd time)..")dbname="click_drafts_2";
	if(uiname=="Click Junk folder(2nd time)..")dbname="click_junk_2";
	if(uiname=="version")dbname="version";
	if(uiname=="browser")dbname="browser";

	if(dbname == "") {
		LOG.error("Couldnot find dbname corresponding to uiname("+ uiname+")");
		return;
	}
	this.paramsArry.push(dbname + "=" + val);

}

var ZM_DB_OBJ = new zmAccessDB();