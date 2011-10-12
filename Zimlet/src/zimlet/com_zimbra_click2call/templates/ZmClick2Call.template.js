AjxTemplate.register("com_zimbra_click2call.templates.ZmClick2Call#ContactDetails", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table height=\"100%\"  width=\"100%\"><tr><td colspan=2 style='text-align:left'><div id='ringingPhoneMsgDiv'><label  id='click2Call_callingLabel' style='font-size:14px;font-weight:bold;color:maroon;'>";
	buffer[_i++] = data["callingStr"];
	buffer[_i++] = "</label></div><div id='click2callDlg_reDialMsgDiv' style='display:none;'><div><label  style='font-size:12px;font-weight:bold;color:red;'>";
	buffer[_i++] = data["connectionFailedStr"];
	buffer[_i++] = "</label></div><div id='click2CallDlg_errDiv'></div></div><div id='click2callDlg_callCompletedMsgDiv' style='display:none;'><div><label  style='font-size:12px;font-weight:bold;color:green;'>";
	buffer[_i++] = data["callSuccessfulStr"];
	buffer[_i++] = "</label></div><div id='click2CallDlg_errDiv'></div></div><div id='click2callDlg_callHungUpMsgDiv' style='display:none;'><div><label  style='font-size:12px;font-weight:bold;color:green;'>";
	buffer[_i++] = data["callHungUpStr"];
	buffer[_i++] = "</label></div><div id='click2CallDlg_errDiv'></div></div></td></tr><tr><td valign='top'><div align=center id=\"click2CallDlg_photoBGDiv\"></div></td><td valign='top'><div style='width:100px;' id=\"click2CallDlg_TextDiv\">";
	 if (data.firstName && data.lastName) { 
	buffer[_i++] = "<label style='font-size:13px;font-weight:bold;'>";
	buffer[_i++] = data.firstName ;
	buffer[_i++] = " ";
	buffer[_i++] =  data.lastName ;
	buffer[_i++] = "</label><br/><label style='font-size:12px;font-weight:bold;color:darkblue'>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(data.toPhoneNumber) ;
	buffer[_i++] = "</label><br/>";
	 } 
	 else { 
	buffer[_i++] = "<label style='font-size:12px;font-weight:bold;'>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(data.toPhoneNumber) ;
	buffer[_i++] = "</label><br/>";
	 } 
	 if (data.jobTitle) { 
	buffer[_i++] = "<label>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(data.jobTitle) ;
	buffer[_i++] = "</label><br/>";
	 } 
	 if (data.company) { 
	buffer[_i++] = "<label>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(data.company) ;
	buffer[_i++] = "</label><br/>";
	 } 
	buffer[_i++] = "</div></td></tr><tr><td colspan=2><table cellpadding=0 cellspacing=0 align=center><tr><td><div style='padding:2px;display:none;' align=center id='click2CallDlg_hangupBtnDiv'></div></td><td><div style='padding:2px;display:none' align=center id='click2CallDlg_reDialBtnDiv'></div></td></tr></table></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "ContactDetails"
}, true);
AjxPackage.define("com_zimbra_click2call.templates.ZmClick2Call");
AjxTemplate.register("com_zimbra_click2call.templates.ZmClick2Call", AjxTemplate.getTemplate("com_zimbra_click2call.templates.ZmClick2Call#ContactDetails"), AjxTemplate.getParams("com_zimbra_click2call.templates.ZmClick2Call#ContactDetails"));

AjxTemplate.register("com_zimbra_click2call.templates.ZmClick2Call#fromPhoneDlg", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div style='padding:5px;' ><table align=center><tr><td style='text-align:right'><label style='font-size:12px;font-weight:bold;'>";
	buffer[_i++] = data["toStr"];
	buffer[_i++] = "</label></td><td><input style='width:200px' type=text id='click2CallDlg_callToPHText'></text></td></tr><tr><td style='text-align:right'><label style='font-size:12px;font-weight:bold;'>";
	buffer[_i++] = data["fromStr"];
	buffer[_i++] = "</label></td><td><div style='padding-top:10px;' id='click2CallFromPhoneDlg_menuDiv'></div></td></tr></table></div><br><table align=center><tr><td id='click2CallFromPhoneDlg_callBtn'></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "fromPhoneDlg"
}, true);

AjxTemplate.register("com_zimbra_click2call.templates.ZmClick2Call#Tooltip", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table cellpadding=2 cellspacing=0 border=0><tr valign='center'><td>";
	buffer[_i++] =  AjxImg.getImageHtml("Telephone") ;
	buffer[_i++] = "</td><td><b><div style='white-space:nowrap'>";
	buffer[_i++] = data["phoneStr"];
	buffer[_i++] = ":</div></b></td><td><b><div style='white-space:nowrap'>";
	buffer[_i++] =  AjxStringUtil.htmlEncode(data.contentObjText) ;
	buffer[_i++] = "</div></b></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "Tooltip"
}, true);

