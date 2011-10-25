AjxTemplate.register("com_zimbra_cloudchat.templates.ZmCloudChat#BuddyListWidget", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div style='cursor:pointer;' class='overviewHeader'><table cellpadding='0' cellspacing='0'><tr><td><div id='cloudchat_buddy_list_expand_icon' class='ImgNodeExpanded' ></div></td><td class='overviewHeader-Text' width='100%'>";
	buffer[_i++] = data["cloudChatStr"];
	buffer[_i++] = "</td><td id='cloudchat_buddy_list_pref_icon' style='padding-right:20px' class='ImgPreferences'></td></tr></table></div><div id='cloudchat_content_div' style='cursor: pointer'><div class='CloudChatInfoDiv'><div style='padding:3px;'><b>";
	buffer[_i++] = data["chatStatusStr"];
	buffer[_i++] = "</b></div><div  id='cloudChat_buddy_login_info_div' style='width:190px'></div></div><div id='cloudchat_login_widget_div'><table align=center><tr><td id='cloudChat_buddy_login_btn_cell'></td></tr></table></div><div id='cloudchat_buddyList_widget' style='cursor:pointer;display:none;'><div id='cloudchat_emailParticipants_hdr' class='overviewHeader' style='width:100%;display:none'><table cellpadding='0' cellspacing='0'><tr><td><b>";
	buffer[_i++] = data["emailParticipants"];
	buffer[_i++] = "</b></td></tr></table></div><div id='cloudchat_emailParticipants_div'></div><div id='cloudchat_buddyList_hdr' class='overviewHeader' style='width:100%'><table cellpadding='0' cellspacing='0'><tr><td><b>";
	buffer[_i++] = data["chatUsers"];
	buffer[_i++] = "</b></td></tr></table></div><div id='cloudchat_buddyList_div'></div><br><div id='cloudChat_buddy_list_actions_menu_container'><table align=center><tr><td id='cloudChat_buddy_list_actions_menu'></td></tr></table></div></div></div><br>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "BuddyListWidget"
}, true);
AjxPackage.define("com_zimbra_cloudchat.templates.ZmCloudChat");
AjxTemplate.register("com_zimbra_cloudchat.templates.ZmCloudChat", AjxTemplate.getTemplate("com_zimbra_cloudchat.templates.ZmCloudChat#BuddyListWidget"), AjxTemplate.getParams("com_zimbra_cloudchat.templates.ZmCloudChat#BuddyListWidget"));

