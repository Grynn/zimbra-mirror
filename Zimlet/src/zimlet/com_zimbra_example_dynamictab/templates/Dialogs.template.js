AjxTemplate.register("com_zimbra_example_dynamictab.templates.Dialogs#EditTabs-Main", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div class=\"DwtPropertyEditor\" style=\"overflow:auto\"><form method=\"POST\" action=\"test\" id=\"";
	buffer[_i++] = data["editForm_id"];
	buffer[_i++] = "\" action=\"";
	buffer[_i++] = data["editForm_action"];
	buffer[_i++] = "\" ><input type=\"hidden\" id=\"";
	buffer[_i++] = data["editForm_tabIdList_input"];
	buffer[_i++] = "\" value=\"";
	buffer[_i++] = data["editForm_tabIdList_value"];
	buffer[_i++] = "\"><table id=\"";
	buffer[_i++] = data["editForm_dynamicTabTable"];
	buffer[_i++] = "\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\"></table></form></div><div><table id=\"";
	buffer[_i++] = data["editForm_dynamicTabButtonTable"];
	buffer[_i++] = "\"></table></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "com_zimbra_example_dynamictab.templates.Dialogs#EditTabs-Main"
}, false);
AjxTemplate.register("com_zimbra_example_dynamictab.templates.Dialogs", AjxTemplate.getTemplate("com_zimbra_example_dynamictab.templates.Dialogs#EditTabs-Main"), AjxTemplate.getParams("com_zimbra_example_dynamictab.templates.Dialogs#EditTabs-Main"));

AjxTemplate.register("com_zimbra_example_dynamictab.templates.Dialogs#EditTabs-AddTab", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table cellspacing=\"2\" cellpadding=\"0\" class=\"com_zimbra_example_dynamictab_EditTabsDialog-dialogContainer\" width=\"100%\"><tr><td colspan=\"2\"><div style=\"text-decoration:underline;font-weight:bold;font-size:110%\">";
	buffer[_i++] =  com_zimbra_example_dynamictab.dialog_tab_details ;
	buffer[_i++] = "</div></td></tr><tr><td>";
	buffer[_i++] =  com_zimbra_example_dynamictab.tab_label_inputlabel ;
	buffer[_i++] = "&nbsp;<span class=\"redAsteric\">*</span></td><td class=\"field\"><div class=\"DwtInputField\" style=\"overflow: visible; position: static;\"><input type=\"text\" id=\"";
	buffer[_i++] = data["tabLabel_input"];
	buffer[_i++] = "\" value=\"";
	buffer[_i++] = data["tabLabel_value"];
	buffer[_i++] = "\" size=\"20\" maxlength=\"20\"></div></td></tr><tr><td >";
	buffer[_i++] =  com_zimbra_example_dynamictab.tab_tooltip_inputlabel ;
	buffer[_i++] = "&nbsp;<span class=\"redAsteric\">*</span></td><td class=\"field\"><div class=\"DwtInputField\" style=\"overflow: visible; position: static;\"><input type=\"text\" id=\"";
	buffer[_i++] = data["tabToolTip_input"];
	buffer[_i++] = "\" value=\"";
	buffer[_i++] = data["tabToolTip_value"];
	buffer[_i++] = "\" size=\"20\" maxlength=\"50\"></div></td></tr><tr><td >";
	buffer[_i++] =  com_zimbra_example_dynamictab.tab_url_inputlabel ;
	buffer[_i++] = "&nbsp;<span class=\"redAsteric\">*</span></td><td class=\"field\"><div class=\"DwtInputField\" style=\"overflow: visible; position: static;\"><input type=\"text\" id=\"";
	buffer[_i++] = data["tabUrl_input"];
	buffer[_i++] = "\" value=\"";
	buffer[_i++] = data["tabUrl_value"];
	buffer[_i++] = "\" size=\"20\" maxlength=\"250\"><div style=\"font-size: 90%;font-style:italic;\">";
	buffer[_i++] =  com_zimbra_example_dynamictab.tab_url_sample ;
	buffer[_i++] = "</div></div></td></tr><tr><td>&nbsp;</td><td><span id=\"";
	buffer[_i++] = data["removeLinkId"];
	buffer[_i++] = "\" onmouseover='this.style.cursor=\"pointer\"' onmouseout='this.style.cursor=\"default\"' class=\"com_zimbra_example_dynamictab_EditTabsDialog-removeLink\">";
	buffer[_i++] =  com_zimbra_example_dynamictab.dialog_link_remove ;
	buffer[_i++] = "</span></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "com_zimbra_example_dynamictab.templates.Dialogs#EditTabs-AddTab"
}, false);

AjxTemplate.register("com_zimbra_example_dynamictab.templates.Dialogs#Tab-Main", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<iframe name=\"com_zimbra_example_dynamictab-iframe\" src=\"";
	buffer[_i++] = data["iframeSrcUrl"];
	buffer[_i++] = "\" width=\"100%\" height=\"100%\"></iframe>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "com_zimbra_example_dynamictab.templates.Dialogs#Tab-Main"
}, false);

AjxTemplate.register("com_zimbra_example_dynamictab.templates.Dialogs#Tab-Toolbar", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div style=\"margin-top: 5px;font-size:10pt\"><b>";
	buffer[_i++] =  com_zimbra_example_dynamictab.tab_url_inputlabel ;
	buffer[_i++] = "&nbsp;</b>";
	buffer[_i++] = data["tabUrl"];
	buffer[_i++] = "\n";
	buffer[_i++] = "\t\t[<a href=\"#\" class=\"";
	buffer[_i++] = data["configure_link"];
	buffer[_i++] = "\" id=\"";
	buffer[_i++] = data["configure_link"];
	buffer[_i++] = "\">";
	buffer[_i++] =  com_zimbra_example_dynamictab.tab_url_edit ;
	buffer[_i++] = "</a>]</div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "com_zimbra_example_dynamictab.templates.Dialogs#Tab-Toolbar"
}, false);

AjxTemplate.register("com_zimbra_example_dynamictab.templates.Dialogs#Tab-Toolbar-Edit", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div style=\"margin-top: 5px;font-size:10pt\"><b>";
	buffer[_i++] =  com_zimbra_example_dynamictab.tab_url_inputlabel ;
	buffer[_i++] = "&nbsp;</b><input type=\"text\" id=\"";
	buffer[_i++] = data["tabUrl_input"];
	buffer[_i++] = "\" value=\"";
	buffer[_i++] = data["tabUrl_value"];
	buffer[_i++] = "\" size=\"20\" maxlength=\"250\">\n";
	buffer[_i++] = "\t\t[<a href=\"#\" class=\"";
	buffer[_i++] = data["save_link"];
	buffer[_i++] = "\" id=\"";
	buffer[_i++] = data["save_link"];
	buffer[_i++] = "\">";
	buffer[_i++] =  com_zimbra_example_dynamictab.tab_url_save ;
	buffer[_i++] = "</a>]\n";
	buffer[_i++] = "\t\t[<a href=\"#\" class=\"";
	buffer[_i++] = data["cancel_link"];
	buffer[_i++] = "\" id=\"";
	buffer[_i++] = data["cancel_link"];
	buffer[_i++] = "\">";
	buffer[_i++] =  com_zimbra_example_dynamictab.tab_url_cancel ;
	buffer[_i++] = "</a>]</div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "com_zimbra_example_dynamictab.templates.Dialogs#Tab-Toolbar-Edit"
}, false);

