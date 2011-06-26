AjxTemplate.register("com_zimbra_salesdemo.templates.SalesDemo#TEMPLATE_FOR_SAP_SEM_ITEM_TOOLTIP", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table align=center cellpadding='0' cellspacing='0' border='0'><tr><td><b>SEM: </b>Product Acme SEM chart for 2011</td></tr><tr><td><b>BU: </b>Desktop</td></tr><tr><td><img class='SalesZimletTooltipImageCSS' src='";
	buffer[_i++] = data["zimletBaseUrl"];
	buffer[_i++] = "/img/SAP_SEM_tooltipAndDlg.png'></img></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "TEMPLATE_FOR_SAP_SEM_ITEM_TOOLTIP"
}, true);
AjxPackage.define("com_zimbra_salesdemo.templates.SalesDemo");
AjxTemplate.register("com_zimbra_salesdemo.templates.SalesDemo", AjxTemplate.getTemplate("com_zimbra_salesdemo.templates.SalesDemo#TEMPLATE_FOR_SAP_SEM_ITEM_TOOLTIP"), AjxTemplate.getParams("com_zimbra_salesdemo.templates.SalesDemo#TEMPLATE_FOR_SAP_SEM_ITEM_TOOLTIP"));

AjxTemplate.register("com_zimbra_salesdemo.templates.SalesDemo#TEMPLATE_FOR_SAP_SEM_ITEM_DIALOG", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table align=center cellpadding='0' cellspacing='0' border='0'><tr><td><b>SAP Cockpit:</b></td></tr><tr><td><img class='SalesZimletDialogImageCSS' src='";
	buffer[_i++] = data["zimletBaseUrl"];
	buffer[_i++] = "/img/SAP_SEM_tooltipAndDlg.png'></img></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "TEMPLATE_FOR_SAP_SEM_ITEM_DIALOG"
}, true);

AjxTemplate.register("com_zimbra_salesdemo.templates.SalesDemo#TEMPLATE_FOR_SAP_SEM_ITEM_TOOLBAR", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table align=center cellpadding='0' cellspacing='0' border='0'><tr><td><b>SEM: </b>Product Acme SEM chart for 2011</td></tr><tr><td><b>BU: </b>Desktop</td></tr><tr><td><img class='SalesZimletToolbarImageCSS' src='";
	buffer[_i++] = data["zimletBaseUrl"];
	buffer[_i++] = "/img/SAP_SEM_toolbar.png'></img></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "TEMPLATE_FOR_SAP_SEM_ITEM_TOOLBAR"
}, true);

AjxTemplate.register("com_zimbra_salesdemo.templates.SalesDemo#TEMPLATE_FOR_Q2_FORECAST_TOOLTIP", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table align=center cellpadding='0' cellspacing='0' border='0'><tr><td><img class='SalesZimletTooltipImageCSS' src='";
	buffer[_i++] = data["zimletBaseUrl"];
	buffer[_i++] = "/img/q2_forecast_tooltipAndDlg.png'></img></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "TEMPLATE_FOR_Q2_FORECAST_TOOLTIP"
}, true);

AjxTemplate.register("com_zimbra_salesdemo.templates.SalesDemo#TEMPLATE_FOR_Q2_FORECAST_DIALOG", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table align=center cellpadding='0' cellspacing='0' border='0'><tr><td><img class='SalesZimletDialogImageCSS' src='";
	buffer[_i++] = data["zimletBaseUrl"];
	buffer[_i++] = "/img/q2_forecast_tooltipAndDlg.png'></img></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "TEMPLATE_FOR_Q2_FORECAST_DIALOG"
}, true);

AjxTemplate.register("com_zimbra_salesdemo.templates.SalesDemo#TEMPLATE_FOR_Q2_FORECAST_TOOLBAR", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table align=center cellpadding='0' cellspacing='0' border='0'><tr><td><b>SEM: </b>Product Acme SEM chart for 2011</td></tr><tr><td><b>BU: </b>Desktop</td></tr><tr><td><img class='SalesZimletToolbarImageCSS' src='";
	buffer[_i++] = data["zimletBaseUrl"];
	buffer[_i++] = "/img/q2_forecast_toobar.jpg'></img></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "TEMPLATE_FOR_Q2_FORECAST_TOOLBAR"
}, true);

AjxTemplate.register("com_zimbra_salesdemo.templates.SalesDemo#TEMPLATE_FOR_EPIC_TOOLTIP", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table align=center cellpadding='0' cellspacing='0' border='0'><tr><td><b>Patient Name: </b>John Doe</td></tr><tr><td><b>Phone: </b>+1 650 123 1234</td></tr><tr><td><b>Problem: </b>Broken Leg</td></tr><tr><td><b>Blood Group: </b>B+</td></tr><tr><td><b>Patient Since: </b>10/10/2010</td></tr><tr><td><img class='SalesZimletTooltipImageCSS' src='";
	buffer[_i++] = data["zimletBaseUrl"];
	buffer[_i++] = "/img/epic_broken_leg_tooltipAndDlg.jpg'></img></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "TEMPLATE_FOR_EPIC_TOOLTIP"
}, true);

AjxTemplate.register("com_zimbra_salesdemo.templates.SalesDemo#TEMPLATE_FOR_EPIC_DIALOG", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table align=center cellpadding='0' cellspacing='0' border='0'><tr><td><b>Patient Name: </b>John Doe</td></tr><tr><td><b>Phone: </b>+1 650 123 1234</td></tr><tr><td><b>Problem: </b>Broken Leg</td></tr><tr><td><b>Blood Group: </b>B+</td></tr><tr><td><b>Patient Since: </b>10/10/2010</td></tr><tr><td><img class='SalesZimletDialogImageCSS' src='";
	buffer[_i++] = data["zimletBaseUrl"];
	buffer[_i++] = "/img/epic_broken_leg_tooltipAndDlg.jpg'></img></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "TEMPLATE_FOR_EPIC_DIALOG"
}, true);

AjxTemplate.register("com_zimbra_salesdemo.templates.SalesDemo#TEMPLATE_FOR_EPIC_TOOLBAR", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<table align=center cellpadding='0' cellspacing='0' border='0'><tr><td><b>Patient Name: </b>John Doe</td></tr><tr><td><b>Phone: </b>+1 650 123 1234</td></tr><tr><td><b>Problem: </b>Broken Leg</td></tr><tr><td><b>Blood Group: </b>B+</td></tr><tr><td><b>Patient Since: </b>10/10/2010</td></tr><tr><td><img class='SalesZimletToolbarImageCSS' src='";
	buffer[_i++] = data["zimletBaseUrl"];
	buffer[_i++] = "/img/epic_broken_leg_tooltipAndDlg.jpg'></img></td></tr></table>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "TEMPLATE_FOR_EPIC_TOOLBAR"
}, true);

