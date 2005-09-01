/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZAPL 1.1
 * 
 * The contents of this file are subject to the Zimbra AJAX Public
 * License Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra AJAX Toolkit.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class
* This static class  allows you to draw "interesting" borders
*		(eg: borders that are composed of multiple images)
*
*	Note that images for the border are used in the same style as AjxImage.
*
*
*	TODO: get the borders working with the AjxImg scheme to do hires/lores images.
*
* @author Owen Williams
*/


//
//	DwtBorder.js
//
//	Class that allows you to draw "interesting" borders
//		(eg: borders that are composed of multiple images)
//
//	Note: you'll use this class statically, like AjxImage
//
function DwtBorder() {
}


DwtBorder._borderTemplates = {};

DwtBorder.getBorderTemplate = function(style) {
	return this._borderTemplates[style];
}


DwtBorder.getBorderHtml = function (style, substitutions, innerDivId) {
	return AjxBuffer.append(
				this.getBorderStartHtml(style, substitutions),
				(innerDivId ? "<div id=" + innerDivId + "></div>" : ""),
				this.getBorderEndHtml(style, substitutions)
			);
}

DwtBorder.getBorderStartHtml = function(style, substitutions) {
	var template = this._borderTemplates[style];
	if (template == null) {
		DBG.println("DwtBorder.getBorderStartHtml(",style,"): no border template found.");
		return "";
	}
	if (template == null) return "";
	var html = template.start;
	if (substitutions != null) {
		html = DwtBorder.performSubstitutions(html, substitutions);
	}
	return html;
}

DwtBorder.getBorderEndHtml = function(style, substitutions) {
	var template = this._borderTemplates[style];
	if (template == null || template == "") return "";

	var html = template.end;
	if (substitutions != null) {
		html = DwtBorder.performSubstitutions(html, substitutions);
	}
	return html;
}


DwtBorder.getBorderHeight = function(style) {
	var template = this._borderTemplates[style];
	if (template != null) return template.height;
	return 0;
}

DwtBorder.getBorderWidth = function(style) {
	var template = this._borderTemplates[style];
	if (template != null) return template.width;
	return 0;
}


DwtBorder.performSubstitutions = function (html, substitutions) {
	for (var prop in substitutions) {
		var str = "<!--$" + prop + "-->";
		if (html.indexOf(str)) {
			html = html.split(str).join(substitutions[prop]);
		}
//MOW: Why is this here?  This will make substitution twice as slow... do we need it?
		var str = "{$"+prop+"}";
		if (html.indexOf(str)) {
			html = html.split(str).join(substitutions[prop]);
		}
	}
	return html;
}


DwtBorder.registerBorder = function (style, template) {
	this._borderTemplates[style] = template
}



DwtBorder.registerBorder(
	"1pxBlack",
	{
		start:"<div style='border:1px solid black'>",
		end:"</div>",
		width:2,
		height:2
	}
);	
	
DwtBorder.registerBorder(
	"card",	
	{
		start:"<table class=card_border_table cellspacing=0 cellpadding=0>"+
				"<tr><td class=card_spacer_TL><div class=Imgcard_TL></div></td>"+
					"<td class=Imgcard_T></td>"+
					"<td class=card_spacer_TR><div class=Imgcard_TR></div></td>"+
				"</tr>"+
				"<tr><td class=Imgcard_L></td>"+
					"<td class=card_spacer_BG>"+
						"<div class=card_contents>",
		end:			"</div class=card_contents>"+
					"</td>"+
					"<td class=Imgcard_R></td>"+
				"</tr>"+
				"<tr><td class=card_spacer_BL><div class=Imgcard_BL></div></td>"+
					"<td class=Imgcard_B></td>"+
					"<td class=card_spacer_BR><div class=Imgcard_BR></div></td>"+
				"</tr>"+
			"</table>",
		width:20,
		height:20
	
	}
);

DwtBorder.registerBorder(
	"selected_card",
	{
		start:"<table class=card_border_table cellspacing=0 cellpadding=0>"+
				"<tr><td class=card_spacer_TL><div class=Imgselected_card_TL></div></td>"+
					"<td class=Imgselected_card_T></td>"+
					"<td class=card_spacer_TR><div class=Imgselected_card_TR></div></td>"+
				"</tr>"+
				"<tr><td class=Imgselected_card_L></td>"+
					"<td class=card_spacer_BG>"+
						"<div class=card_contents>",
		end:			"</div class=card_contents>"+
					"</td>"+
					"<td class=Imgselected_card_R></td>"+
				"</tr>"+
				"<tr><td class=card_spacer_BL><div class=Imgselected_card_BL></div></td>"+
					"<td class=Imgselected_card_B></td>"+
					"<td class=card_spacer_BR><div class=Imgselected_card_BR></div></td>"+
				"</tr>"+
			"</table>",
		width:19,
		height:18
	
	}
);



var dialogPieces = {
	start:AjxBuffer.concat(
				 "<table class='DialogTable' cellpadding='0' Xborder=1>",
					// top edge
					"<tr><td class='border_outset_c'><div class='Imgdialog_outset_TL'></div></td>",
						"<td colspan=3 class='Imgdialog_outset_T'></td>",
						"<td class='border_outset_c'><div class='Imgdialog_outset_TR'></div></td>",
						"<td valign=top class='border_shadow_v'>",
							"<div class='Imgshadow_big_TR' style='height:4;'></div>",
						"</td>",
					"</tr>",
					// titlebar
					"<tr><td class='Imgdialog_outset_L' style='height:100%'></td>",
						"<td colspan=3 id='<!--$titleId-->' class='DialogTitle'>",
						  "<table class='dialog_table' cellpadding='0'><tr>",
							"<td class='DialogTitleCell'><!--$icon--></td>",
							"<td id='<!--$titleTextId-->' class='DialogTitleCell'><!--$title--></td>",
							"<td class='DialogTitleCell'><div class='<!--$closeIcon2-->' style='cursor:pointer'></div></td>",
							"<td class='DialogTitleCell'><div class='<!--$closeIcon1-->' style='cursor:pointer'></div></td>",
						"</tr></table></td>",
						"<td class='Imgdialog_outset_R' style='height:100%'></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>"
				),
	
	topNoToolbar: AjxBuffer.concat(
					// top inside edge
					"<tr><td class='Imgdialog_outset_L' style='height:100%'></td>",
						"<td class='DialogBody'><div class='Imgdialog_inset_TL'></div></td>",
						"<td class='DialogBody' Xstyle='width:100%'><div class='Imgdialog_inset_T'></div></td>",
						"<td class='DialogBody'><div class='Imgdialog_inset_TR'></div></td>",
						"<td class='Imgdialog_outset_R' style='height:100%'></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>",
					// dialog center
					"<tr><td class='Imgdialog_outset_L' style='height:100%'></td>",
						"<td class='DialogBody'><div class='Imgdialog_inset_L' style='height:100%'></div></td>",
						"<td class='DialogBody'>"
				),
	
	topWithToolbar: AjxBuffer.concat(
					// top inside edge
					"<tr><td class='Imgdialog_outset_L' style='height:100%'></td>",
						"<td class='DialogToolbar'><div class='Imgdialog_inset_TL'></div></td>",
						"<td class='DialogToolbar' style='width:100%'><div class='Imgdialog_inset_T'></div></td>",
						"<td class='DialogToolbar'><div class='Imgdialog_inset_TR'></div></td>",
						"<td class='Imgdialog_outset_R' style='height:100%'></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>",
					// top toolbar
					"<tr><td class='Imgdialog_outset_L' style='height:100%'></td>",
						"<td class='DialogToolbar'><div class='Imgdialog_inset_L' style='height:20'></div></td>",
						"<td class='DialogToolbar'></td>",
						"<td class='DialogToolbar'><div class='Imgdialog_inset_R' style='height:20'></div></td>",
						"<td class='Imgdialog_outset_R' style='height:100%'></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>",
					"<tr><td class='Imgdialog_outset_L' style='height:100%'></td>",
						"<td class='Imgdialog_toolbar_sep_TL'></td>",
						"<td class='Imgdialog_toolbar_sep_T'></td>",
						"<td class='Imgdialog_toolbar_sep_TR'></td>",
						"<td class='Imgdialog_outset_R' style='height:100%'></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>",
					// dialog center
					"<tr><td class='Imgdialog_outset_L' style='height:100%'></td>",
						"<td class='DialogBody'><div class='Imgdialog_inset_L' style='height:100%'></div></td>",
						"<td class='DialogBody'>"
				),
	
	bottomNoToolbar: AjxBuffer.concat(
						"</td> ",
						"<td class='DialogBody'><div class='Imgdialog_inset_R' style='height:100%'></div></td>",
						"<td class='Imgdialog_outset_R' style='height:100%'></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>",
					// bottom inside edge
					"<tr><td class='Imgdialog_outset_L' style='height:100%'></td>",
						"<td class='DialogBody'><div class='Imgdialog_inset_BL'></div></td>",
						"<td class='DialogBody'><div class='Imgdialog_inset_B'></div></td>",
						"<td class='DialogBody'><div class='Imgdialog_inset_BR'></div></td>",
						"<td class='Imgdialog_outset_R' style='height:100%'></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>"
				),
	
	bottomWithToolbar: AjxBuffer.concat(
						"</td>",
						"<td class='DialogBody'><div class='Imgdialog_inset_R' style='height:100%'></div></td>",
						"<td class='Imgdialog_outset_R' style='height:100%'></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>",
					// bottom toolbar
					"<tr><td class='Imgdialog_outset_L' style='height:100%'></td>",
						"<td class='Imgdialog_toolbar_sep_TL'></td>",
						"<td class='Imgdialog_toolbar_sep_T'></td>",
						"<td class='Imgdialog_toolbar_sep_TR'></td>",
						"<td class='Imgdialog_outset_R' style='height:100%'></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>",
					"<tr><td class='Imgdialog_outset_L' style='height:100%'></td>",
						"<td class='DialogToolbar'><div class='Imgdialog_inset_L' style='height:20'></div></td>",
						"<td class='DialogToolbar'><div id='<!--$id-->_bottom_toolbar'></div></td>",
						"<td class='DialogToolbar'><div class='Imgdialog_inset_R' style='height:20'></div></td>",
						"<td class='Imgdialog_outset_R' style='height:100%'></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>",
					// bottom inside edge
					"<tr><td class='Imgdialog_outset_L' style='height:100%'></td>",
						"<td class='DialogToolbar'><div class='Imgdialog_inset_BL'></div></td>",
						"<td class='DialogToolbar'><div class='Imgdialog_inset_B'></div></td>",
						"<td class='DialogToolbar'><div class='Imgdialog_inset_BR'></div></td>",
						"<td class='Imgdialog_outset_R' style='height:100%'></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>"	
				),
	
	end: AjxBuffer.concat(
					// bottom edge
					"<tr><td><div class='Imgdialog_outset_bl'></div></td>",
						"<td colspan=3 class='Imgdialog_outset_b'></td>",
						"<td><div class='Imgdialog_outset_br'></div></td>",
						"<td><div class='Imgshadow_big_R' style='height:100%'></div></td>",
					"</tr>",
					// bottom shadow
					"<tr><td><div class='Imgshadow_big_BL' style='width:4;'></div><td>",
							"<div class='Imgshadow_big_B' style='width:100%'></div>",
						"</td>",
						"<td colspan=3>",
							"<div class='Imgshadow_big_B' style='width:100%'></div></td>",
						"<td class=dialog_shadow_c><div class='Imgshadow_big_BR'></div><td>",
					"</tr>",
			     "</table>"
				)
}

DwtBorder.registerBorder(
	"dialog",
	{
		start:	dialogPieces.start + dialogPieces.topNoToolbar,
		end: dialogPieces.bottomNoToolbar + dialogPieces.end,
		width:40,
		height:45
	}
);

DwtBorder.registerBorder(
	"dialogWithTopToolbar",
	{
		start:	dialogPieces.start + dialogPieces.topWithToolbar,
		end: dialogPieces.bottomNoToolbar + dialogPieces.end,
		width:40,
		height:45
	}
);

DwtBorder.registerBorder(
	"dialogWithBottomToolbar",
	{
		start:	dialogPieces.start + dialogPieces.topNoToolbar,
		end: dialogPieces.bottomWithToolbar + dialogPieces.end,
		width:40,
		height:45
	}
);

DwtBorder.registerBorder(
	"dialogWithBothToolbars",
	{
		start:	dialogPieces.start + dialogPieces.topWithToolbar,
		end: dialogPieces.bottomWithToolbar + dialogPieces.end,
		width:40,
		height:45
	}
);






DwtBorder.registerBorder(
	"h_sash",
	{	
		start: AjxBuffer.concat(
				"<table width=100% cellspacing=0 cellpadding=0><tr>",
					"<td><div  class=Imgh_sash_TL></div></td>",
					"<td class=Imgh_sash_T style='width:50%'></td>",
					"<td><div class=Imgh_sash_grip></div></td>",
					"<td class=Imgh_sash_T style='width:50%'></td>",
					"<td><div  class=Imgh_sash_TR></div></td>",
				"</tr></table>"
			),
		end:"",
		width:10,	//NOT ACCURATE
		height:7
	}
);


DwtBorder.registerBorder(
	"calendar_appt",
	{	
		start:AjxBuffer.concat(	
			"<div id='<!--$id-->_body' class='appt_body appt<!--$newState--><!--$color-->_body'>",
				"<table style='width:100%;height:100%'cellspacing=0 cellpadding=2>",
				"<tr class=appt<!--$newState--><!--$color-->_header>",
					"<td class=appt_time id='<!--$id-->_st'><!--$starttime--></td>",
					"<td class=appt_status-<!--$statusKey--> style='text-align:right'><!--$status--></td>",
//					"<td class=appt<!--$newState-->_tag><!--$tag--></td>",
				"</tr>",
				"<tr valign=top>",
					"<td colspan=2 class=appt<!--$newState-->_name style='height:100%'>",
						"<!--$name-->",
						"<BR>",
						"<!--$location-->",
					"</td>",
				"<tr>",
					"<td colspan=2 class=appt_end_time id='<!--$id-->_et'><!--$endtime--></td>",
				"</tr>",
				"</table>",
			"</div>"
			),
		end: "",
		width:10,	//NOT ACCURATE
		height:7
	}
);

DwtBorder.registerBorder(
	"calendar_appt_30",
	{	
		start:AjxBuffer.concat(
			"<div id='<!--$id-->_body' class='appt_30_body appt_30<!--$newState--><!--$color-->_header'>",
				"<table width=100% cellspacing=0 cellpadding=2>",
				"<tr>",
					"<td class=appt_30<!--$newState-->_name><!--$name--></td>",
//					"<td class=appt<!--$newState-->_tag><!--$tag--></td>",
				"</tr>",
				"</table>",
			"</div>"
		),
		end:	"",
		width:4,
		height:4
	}
);


DwtBorder.registerBorder( 
	"hover", 
	{ 
		start: AjxBuffer.concat(
				"<div id='{$id}_tip_t' class='hover_tip_top Imghover_tip_top'></div>",
				"<table class=hover_frame_table border=0 cellspacing=0 cellpadding=0>", 
					"<tr>", 
						"<td id='{$id}_border_tl' class=Imghover_TL></td>", 
						"<td id='{$id}_border_tm' class=Imghover_T></td>", 
						"<td id='{$id}_border_tr' class=Imghover_TR></td>", 
					"</tr>", 
					"<tr>",
						"<td id='{$id}_border_ml' class='Imghover_L'></td>", 
						"<td id='{$id}_border_mm' class=Imghover_BG><div id='{$id}_contents' class=hover_contents>"
			),
		end: AjxBuffer.concat(
						"</div></td>", 
						"<td id='{$id}_border_mr' class=Imghover_R></td>", 
					"</tr>", 
					"<tr>",
						"<td id='{$id}_border_bl' class=Imghover_BL></div></td>", 
						"<td id='{$id}_border_bm' class=Imghover_B></td>", 
						"<td id='{$id}_border_br' class=Imghover_BR></div></td>", 
					"</tr>", 
				"</table>",
				"<div id='{$id}_tip_b' class='hover_tip_bottom Imghover_tip_bottom'></div>"
			)
	} 
);



DwtBorder.registerBorder( 
	"hover_IE", 
	{ 
		start:	AjxBuffer.concat(
				"<div id='{$id}_tip_t' class='hover_tip_top ImgIE_hover_tip_top'></div>",
				"<table class=hover_frame border=0 cellspacing=0 cellpadding=0>", 
					"<tr>", 
						"<td id='{$id}_border_tl'><div class=ImgIE_hover_TL></div></td>", 
						"<td id='{$id}_border_tm'><div class=ImgIE_hover_T style='width:100%'>&nbsp;</div></td>", 
						"<td id='{$id}_border_tr'><div class=ImgIE_hover_TR></div></td>", 
					"</tr>", 
					"<tr>",
						"<td id='{$id}_border_ml'><div class=ImgIE_hover_L style='height:100%'></div></td>", 
						"<td id='{$id}_border_mm' class=ImgIE_hover_BG><div id='{$id}_contents' class=hover_contents>"
			), 
		end:	AjxBuffer.concat(
					"</div></td>", 
						"<td id='{$id}_border_mr'><div class=ImgIE_hover_R style='height:100%'></div></td>", 
					"</tr>", 
					"<tr>",
						"<td id='{$id}_border_bl'><div class=ImgIE_hover_BL></div></td>", 
						"<td id='{$id}_border_bm'><div class=ImgIE_hover_B style='width:100%'>&nbsp;</div></td>", 
						"<td id='{$id}_border_br'><div class=ImgIE_hover_BR></div></td>", 
					"</tr>", 
				"</table>",
				"<div id='{$id}_tip_b' class='hover_tip_bottom ImgIE_hover_tip_bottom'></div>"
			)
	} 
);


