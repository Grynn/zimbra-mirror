AjxTemplate.register("com_zimbra_phonelookup.templates.PhoneLookup#Frame", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div align=center  style='display:block;overflow:auto;'  id='phoneLookupZimlet_MainDiv'><div class='overviewHeader LinkedInHeader' id='phoneLookupZimlet_searchBarDiv'><table cellspacing=0 cellpadding=0 align=\"center\"><tr><td><input id='phoneLookupZimlet_seachField' type='text' style='width:110px'></input></td><td id='phoneLookupZimlet_seachBtnCell'></td></tr></table></div><div id='phoneLookupZimlet_searchResultsDiv'></div></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "Frame"
}, true);
AjxPackage.define("com_zimbra_phonelookup.templates.PhoneLookup");
AjxTemplate.register("com_zimbra_phonelookup.templates.PhoneLookup", AjxTemplate.getTemplate("com_zimbra_phonelookup.templates.PhoneLookup#Frame"), AjxTemplate.getParams("com_zimbra_phonelookup.templates.PhoneLookup#Frame"));

AjxTemplate.register("com_zimbra_phonelookup.templates.PhoneLookup#RowItem", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div style='border-bottom:1px solid gray;background:white;'><table width=100%><tr><td style='font-weight:bold;font-size:12px'>Owner: </td><td>";
	buffer[_i++] = data["displayName"];
	buffer[_i++] = "</td></tr><tr><td style='font-weight:bold;font-size:12px'>Street: </td><td>";
	buffer[_i++] = data["fullStreet"];
	buffer[_i++] = "</td></tr><tr><td style='font-weight:bold;font-size:12px'>City: </td><td>";
	buffer[_i++] = data["city"];
	buffer[_i++] = "</td></tr><tr><td style='font-weight:bold;font-size:12px'>State: </td><td>";
	buffer[_i++] = data["state"];
	buffer[_i++] = "</td></tr><tr><td style='font-weight:bold;font-size:12px'>zip: </td><td>";
	buffer[_i++] = data["zip"];
	buffer[_i++] = "</td></tr><tr><td style='font-weight:bold;font-size:12px'>Country: </td><td>";
	buffer[_i++] = data["country"];
	buffer[_i++] = "</td></tr></table></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "RowItem"
}, true);

