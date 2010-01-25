AjxTemplate.register("com_zimbra_example_simpledialogtemplate.templates.Simple#Main", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table cellpadding=\"2\" cellspacing=\"0\" border=\"0\" width=\"100%\"><tr><td colspan=\"2\">\n";
	buffer[_i++] = "\t\t\t\tThis is a sample dialog with HTML code...\n";
	buffer[_i++] = "\t\t\t</td></tr><tr><td colspan=\"2\">&nbsp;</td></tr><tr><td><b>Text Property:</b></td><td><input type=\"text\" name=\"simpledialog_text_prop\" /></td></tr><tr><td><b>Password Property:</b></td><td><input type=\"password\" name=\"simpledialog_password_prop\" /></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "Main"
}, false);
AjxTemplate.register("com_zimbra_example_simpledialogtemplate.templates.Simple", AjxTemplate.getTemplate("com_zimbra_example_simpledialogtemplate.templates.Simple#Main"), AjxTemplate.getParams("com_zimbra_example_simpledialogtemplate.templates.Simple#Main"));

