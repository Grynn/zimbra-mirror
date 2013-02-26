/**
 * @overview
 * A link that is a button. Currently used only for the "help" link/button
 *
 * @author Eran Yarkon
 *
 * @extends	DwtButton

 */
DwtLinkButton = function(params) {
	params.className = params.className || "ZButtonLink";
	this._noDropDown = params.noDropDown;
	DwtButton.call(this, params);
};


DwtLinkButton.prototype = new DwtButton;
DwtLinkButton.prototype.constructor = DwtLinkButton;

DwtLinkButton.prototype.TEMPLATE = "dwt.Widgets#ZLinkButton";

// defaults for drop down images (set here once on prototype rather than on each button instance)
DwtLinkButton.prototype._dropDownImg 	= null; //no longer using HelpPullDownArrow - we do the arrow via pixel-high divs
DwtLinkButton.prototype._dropDownDepImg	= null; //same as above
DwtLinkButton.prototype._dropDownHovImg = null; //same as above

DwtLinkButton.prototype.toString =
function() {
	return "DwtLinkButton";
};

DwtLinkButton.prototype._createHtmlFromTemplate = function(templateId, data) {
	data = data || {};
	data.noDropDown = this._noDropDown;
	DwtButton.prototype._createHtmlFromTemplate.call(this, templateId, data);
};

DwtLinkButton.prototype._createElement =
function() {
	return document.createElement("SPAN")
};