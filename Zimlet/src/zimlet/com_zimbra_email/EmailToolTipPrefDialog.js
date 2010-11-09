/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
*/


/**
 * Object that deals with Preferences and Preferences dialog
 * @param zimlet  Email Zimlet
 */
function EmailToolTipPrefDialog(zimlet) {
	this.zimlet = zimlet;
	this.emailZimlet_tooltipArea = this.zimlet.getUserProperty("emailZimlet_tooltipArea");
	if(!this.emailZimlet_tooltipArea) {
		this.emailZimlet_tooltipArea = EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_MEDIUM];
	}
	this.updateEmailTooltipSize();
}

EmailToolTipPrefDialog.SIZE_VERYSMALL = "VERYSMALL";
EmailToolTipPrefDialog.SIZE_SMALL = "SMALL";
EmailToolTipPrefDialog.SIZE_MEDIUM = "MEDIUM";
EmailToolTipPrefDialog.SIZE_LARGE = "LARGE";
EmailToolTipPrefDialog.SIZE_XL = "XL";

EmailToolTipPrefDialog.DIMENSIONS = [];
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_VERYSMALL]  = "220px x 130px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_SMALL]  = "230px x 140px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_MEDIUM]  = "250px x 140px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_LARGE]  = "260px x 200px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_XL]  = "270px x 210px";

/**
 * Updates Email Tooltip's tooltipWidth and tooltipHeight
 */
EmailToolTipPrefDialog.prototype.updateEmailTooltipSize =
function() {
	var size = this.emailZimlet_tooltipArea.replace(/px/ig, "");
	var arry = size.split(" x ");
	EmailTooltipZimlet.tooltipWidth = arry[0];
	EmailTooltipZimlet.tooltipHeight = arry[1];
};