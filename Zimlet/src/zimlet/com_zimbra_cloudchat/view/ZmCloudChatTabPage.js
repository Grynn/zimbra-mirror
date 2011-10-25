function ZmCloudChatTabPage(parent, zimlet, name, dontShowUsersInTab) {
    DwtTabViewPage.call(this, parent);
    this.zimlet = zimlet;
    this.name = name;
	this._dontShowUsersInTab = dontShowUsersInTab;
    this.postsDiv = "cloudChatpostsDiv" + Dwt.getNextId();
    this.userListViewId = "cloudChatUsersListDiv" + Dwt.getNextId();
    this._sendBtnDiv = "cloudChat_sendBtnDiv" + Dwt.getNextId();
    this._optionsMenuDiv = "cloudChat_optionsMenuDiv" + Dwt.getNextId();
    this.inputFieldId = "cloudChatinputFieldId" + Dwt.getNextId();
	this._usersCellId =  "cloudChatUsersListTD" + Dwt.getNextId();
	this._postsCell =  "cloudChatPostsTD" + Dwt.getNextId();
    this._createHTML(name);
    this._doTranslate = false;
    this._doText2Speech = false;
}

ZmCloudChatTabPage.prototype = new DwtTabViewPage;
ZmCloudChatTabPage.prototype.constructor = ZmCloudChatTabPage;

ZmCloudChatTabPage.prototype._createHTML = function(name) {
    var html = [];
	var displayStr = "";
	var postCellStyle = "word-wrap:break-word;border-right: 1px solid gray; width:410px; border-style: solid; border-width: 0 1px 0 0;";
	var userCellStyle = "width:90px;word-wrap:break-word;display:block";
	if(this._dontShowUsersInTab) {
		postCellStyle = "word-wrap:break-word;width:510px; ";
		userCellStyle = "width:0px;word-wrap:break-word;display:none";
	}
    html.push("<div style='background:white'>",
    "<table cellpadding=1px>",
    "<td id='",this._postsCell,"'  style='",postCellStyle,"' valign=top>",
	"<div style='overflow:auto;height:235px;' id='", this.postsDiv,
    "' style='overflow:auto;background:white'></div></td>",
    "<td id='",this._usersCellId,"' style='",userCellStyle, "' valign=top>",
	"<div style='overflow:auto;height:235px;background:white;' id='",
	this.userListViewId, "'></div></td></table></div>",
    "<div><table><td><input id='",
    this.inputFieldId, "' type=text style='width:370px;height:25px;'></input></td><td id='",
    this._sendBtnDiv, "'></td><td id='",
    this._optionsMenuDiv, "'></td></table></div>");

    this.getHtmlElement().innerHTML = html.join("");

    this._appendWidgets();
};

ZmCloudChatTabPage.prototype._appendWidgets = function() {
    this.sendBtn = new DwtButton({
        parent: this.shell
    });
    this.sendBtn.setText(this.zimlet.getMessage("send"));
    document.getElementById(this._sendBtnDiv).appendChild(
    this.sendBtn.getHtmlElement());

    this.optionMenu = new DwtButton({
        parent: this.shell
    });
    this.optionMenu.setImage("Preferences");
    document.getElementById(this._optionsMenuDiv).appendChild(
    this.optionMenu.getHtmlElement());
    var menu = new ZmPopupMenu(this.optionMenu);
    //create menu
    this.optionMenu.setMenu(menu);
    //add menu to button
    menu.noMenuBar = true;
    var mi = menu.createMenuItem(Dwt.getNextId(), {
        image: "zimbraIcon",
        text: "Text2Speech",
        style: DwtMenuItem.CHECK_STYLE
    });
    mi.addSelectionListener(new AjxListener(this, this._handleText2SpeechMenuClick));

    mi = menu.createMenuItem(Dwt.getNextId(), {
        image: "zimbraIcon",
        text: "Translate",
        style: DwtMenuItem.CHECK_STYLE
    });
    mi.addSelectionListener(new AjxListener(this, this._handleTranslateMenuClick));
};



ZmCloudChatTabPage.prototype._handleText2SpeechMenuClick = function() {
    this._doText2Speech = !this._doText2Speech;
};

ZmCloudChatTabPage.prototype._handleTranslateMenuClick = function() {
    this._doTranslate = !this._doTranslate;
};


ZmCloudChatTabPage.prototype.showMe = function() {
    //just override to ensure tab-sizes are intact
    };

