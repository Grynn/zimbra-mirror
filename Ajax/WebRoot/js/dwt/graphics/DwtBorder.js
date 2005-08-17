/**
* @class
* This static class  allows you to draw "interesting" borders
*		(eg: borders that are composed of multiple images)
*
*	Note that images for the border are used in the same style as LsImage.
*
*
*	TODO: get the borders working with the LsImg scheme to do hires/lores images.
*
* @author Owen Williams
*/


//
//	DwtBorder.js
//
//	Class that allows you to draw "interesting" borders
//		(eg: borders that are composed of multiple images)
//
//	Note: you'll use this class statically, like LsImage
//
function DwtBorder() {
}


DwtBorder._borderTemplates = {};

DwtBorder.getBorderTemplate = function(style) {
	return this._borderTemplates[style];
}


DwtBorder.getBorderHtml = function (style, substitutions, innerDivId) {
	return LsBuffer.append(
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
				"<tr><td class=card_spacer_c><div class=Imgcard_TL style='overflow:hidden;'></div></td>"+
					"<td class=card_spacer_h><div class=Imgcard_TM style='width:100%;overflow:hidden;'></div></td>"+
					"<td class=card_spacer_c><div class=Imgcard_TR style='overflow:hidden;'></div></td>"+
				"</tr>"+
				"<tr><td class=card_spacer_v><div class=Imgcard_ML style='height:100%'></div></td>"+
					"<td class=card_spacer_m><div class=Imgcard_MM>"+
						"<div class=card_contents>",
		end:			"</div class=card_contents>"+
					"</td>"+
					"<td class=card_spacer_v><div class=Imgcard_MR></div></td>"+
				"</tr>"+
				"<tr><td class=card_spacer_c><div class=Imgcard_BL style='overflow:hidden;'></div></td>"+
					"<td class=card_spacer_h><div class=Imgcard_BM style='width:100%;overflow:hidden;'></div></td>"+
					"<td class=card_spacer_c><div class=Imgcard_BR style='overflow:hidden;'></div></td>"+
				"</tr>"+
			"</table>",
		width:20,
		height:20
	
	}
);

DwtBorder.registerBorder(
	"selected_card",
	{
		start:"<table class=selected_card_border_table cellspacing=0 cellpadding=0>"+
				"<tr><td class=selected_card_spacer_c><div class=Imgselected_card_TL style='overflow:hidden;'></div></td>"+
					"<td class=selected_card_spacer_h><div class=Imgselected_card_TM style='width:100%;overflow:hidden;'></div></td>"+
					"<td class=selected_card_spacer_c><div class=Imgselected_card_TR style='overflow:hidden;'></div></td>"+
				"</tr>"+
				"<tr><td class=selected_card_spacer_v><div class=Imgselected_card_ML style='height:100%'></div></td>"+
					"<td class=selected_card_spacer_m><div class=Imgselected_card_MM>"+
						"<div class=selected_card_contents>",
		end:			"</div class=selected_card_contents>"+
					"</td>"+
					"<td class=selected_card_spacer_v><div class=Imgselected_card_MR></div></td>"+
				"</tr>"+
				"<tr><td class=selected_card_spacer_c><div class=Imgselected_card_BL style='overflow:hidden;'></div></td>"+
					"<td class=selected_card_spacer_h><div class=Imgselected_card_BM style='width:100%;overflow:hidden;'></div></td>"+
					"<td class=selected_card_spacer_c><div class=Imgselected_card_BR style='overflow:hidden;'></div></td>"+
				"</tr>"+
			"</table>",
		width:19,
		height:18
	
	}
);


DwtBorder.registerBorder(
	"TL_balloon",
	{
		start:	"<div class=balloon_frame>"+
				"	<table class=balloon_frame_table cellpadding=0 Xborder=1>"+
				"		<tr>"+
				"			<td class=balloon_spacer_tip rowspan=3 valign=top>"+
				"				<div class=ImgTL_balloon_tip style='position:relative;left:1'></div>"+
				"			</td>"+
				"			<td class=balloon_spacer_tc valign=bottom><div class=Imgballoon_TL></div></td>"+
				"			<td class=balloon_spacer_h valign=bottom><div class=Imgballoon_TM style='width:100%'></div></td>"+
				"			<td class=balloon_spacer_tc valign=bottom><div class=Imgballoon_TR></div></td>"+
				"		</tr>"+
				"		<tr><td class=balloon_spacer_v><div class=Imgballoon_ML style='height:100%'></div></td>"+
				"			<td class=balloon_spacer_m><div class=TL_balloon_contents>",

		end:	"			</div></td>"+
				"			<td class=balloon_spacer_v><div class=Imgballoon_MR style='height:100%'></div></td>"+
				"		</tr>"+
				"		<tr><td class=balloon_spacer_c><div class=Imgballoon_BL></div></td>"+
				"			<td class=balloon_spacer_h><div class=Imgballoon_BM style='width:100%'></div></td>"+
				"			<td class=balloon_spacer_c><div class=Imgballoon_BR></div></td>"+
				"		</tr>"+
				"	</table>"+
				"</div class=balloon_frame>",
		width:40,
		height:45
	}
);


DwtBorder.registerBorder(
	"BL_balloon",
	{
		start:	"<div class=balloon_frame>"+
				"	<table class=balloon_frame_table cellpadding=0 Xborder=1>"+
				"		<tr>"+
				"			<td class=balloon_spacer_tip rowspan=3 valign=bottom>"+
				"				<div class=ImgBL_balloon_tip style='position:relative;left:1;top:28'></div>"+
				"			</td>"+
				"			<td class=balloon_spacer_tc valign=bottom><div class=Imgballoon_TL></div></td>"+
				"			<td class=balloon_spacer_h valign=bottom><div class=Imgballoon_TM style='width:100%'></div></td>"+
				"			<td class=balloon_spacer_tc valign=bottom><div class=Imgballoon_TR></div></td>"+
				"		</tr>"+
				"		<tr><td class=balloon_spacer_v><div class=Imgballoon_ML style='height:100%'></div></td>"+
				"			<td class=balloon_spacer_m><div class=TL_balloon_contents>",

		end:	"			</div></td>"+
				"			<td class=balloon_spacer_v><div class=Imgballoon_MR style='height:100%'></div></td>"+
				"		</tr>"+
				"		<tr><td class=balloon_spacer_c><div class=Imgballoon_BL></div></td>"+
				"			<td class=balloon_spacer_h><div class=Imgballoon_BM style='width:100%'></div></td>"+
				"			<td class=balloon_spacer_c><div class=Imgballoon_BR></div></td>"+
				"		</tr>"+
				"	</table>"+
				"</div class=balloon_frame>",
		width:40,
		height:45
	}
);


DwtBorder.registerBorder(
	"TR_balloon",
	{
		start:	"<div class=balloon_frame>"+
				"	<table class=balloon_frame_table cellpadding=0 Xborder=1>"+
				"		<tr>"+
				"			<td class=balloon_spacer_tc valign=bottom><div class=Imgballoon_TL></div></td>"+
				"			<td class=balloon_spacer_h valign=bottom><div class=Imgballoon_TM style='width:100%'></div></td>"+
				"			<td class=balloon_spacer_tc valign=bottom><div class=Imgballoon_TR></div></td>"+
				"			<td class=balloon_spacer_tip rowspan=3 valign=top>"+
				"				<div class=ImgTR_balloon_tip style='position:relative;left:-13;top:-1'></div>"+
				"			</td>"+
				"		</tr>"+
				"		<tr><td class=balloon_spacer_v><div class=Imgballoon_ML style='height:100%'></div></td>"+
				"			<td class=balloon_spacer_m><div class=TL_balloon_contents>",

		end:	"			</div></td>"+
				"			<td class=balloon_spacer_v><div class=Imgballoon_MR style='height:100%'></div></td>"+
				"		</tr>"+
				"		<tr><td class=balloon_spacer_c><div class=Imgballoon_BL></div></td>"+
				"			<td class=balloon_spacer_h><div class=Imgballoon_BM style='width:100%'></div></td>"+
				"			<td class=balloon_spacer_c><div class=Imgballoon_BR></div></td>"+
				"		</tr>"+
				"	</table>"+
				"</div class=balloon_frame>",
		width:40,
		height:45
	}
);


DwtBorder.registerBorder(
	"BR_balloon",
	{
		start:	"<div class=balloon_frame>"+
				"	<table class=balloon_frame_table cellpadding=0 Xborder=1>"+
				"		<tr>"+
				"			<td class=balloon_spacer_tc valign=bottom><div class=Imgballoon_TL></div></td>"+
				"			<td class=balloon_spacer_h valign=bottom><div class=Imgballoon_TM style='width:100%'></div></td>"+
				"			<td class=balloon_spacer_tc valign=bottom><div class=Imgballoon_TR></div></td>"+
				"			<td class=balloon_spacer_tip rowspan=3 valign=bottom>"+
				"				<div class=ImgBR_balloon_tip style='position:relative;left:-13;top:28'></div>"+
				"			</td>"+
				"		</tr>"+
				"		<tr><td class=balloon_spacer_v><div class=Imgballoon_ML style='height:100%'></div></td>"+
				"			<td class=balloon_spacer_m><div class=TL_balloon_contents>",

		end:	"			</div></td>"+
				"			<td class=balloon_spacer_v><div class=Imgballoon_MR style='height:100%'></div></td>"+
				"		</tr>"+
				"		<tr><td class=balloon_spacer_c><div class=Imgballoon_BL></div></td>"+
				"			<td class=balloon_spacer_h><div class=Imgballoon_BM style='width:100%'></div></td>"+
				"			<td class=balloon_spacer_c><div class=Imgballoon_BR></div></td>"+
				"		</tr>"+
				"	</table>"+
				"</div class=balloon_frame>",
		width:40,
		height:45
	}
);


DwtBorder.registerBorder(
	"crab",
	{
		start:	"<div class=balloon_frame>"+
				"	<table class=balloon_frame_table cellpadding=0 Xborder=1>"+
				"		<tr>"+
				"			<td class=balloon_spacer_tip valign=top><div style=position:relative>"+
				"				<div class=ImgTL_balloon_tip style='position:absolute;left:1'></div>"+
				"			</div></td>"+
				"			<td class=balloon_spacer_tc valign=bottom><div class=Imgballoon_TL></div></td>"+
				"			<td class=balloon_spacer_h valign=bottom><div class=Imgballoon_TM style='width:100%'></div></td>"+
				"			<td class=balloon_spacer_tc valign=bottom><div class=Imgballoon_TR></div></td>"+
				"			<td class=balloon_spacer_tip valign=top><div style=position:relative>"+
				"				<div class=ImgTR_balloon_tip style='position:absolute;left:-14;top:-1'></div>"+
				"			</div></td>"+
				"		</tr>"+
				"		<tr>"+
				"			<td class=balloon_spacer_tip valign=center><div style=position:relative>"+
				"				<div class=ImgBL_balloon_tip style='position:relative;left:1;top:10'></div>"+
				"			</div></td>"+
				"			<td class=balloon_spacer_v><div class=Imgballoon_ML style='height:100%'></div></td>"+
				"			<td class=balloon_spacer_m align=center><div class=TL_balloon_contents><center>",

		end:	"			</center></div></td>"+
				"			<td class=balloon_spacer_v><div class=Imgballoon_MR style='height:100%'></div></td>"+
				"			<td class=balloon_spacer_tip valign=center>"+
				"				<div class=ImgBR_balloon_tip style='position:relative;left:-14;top:10'></div>"+
				"			</td>"+
				"		</tr>"+
				"		<tr>"+
				"			<td class=balloon_spacer_tip valign=bottom><div style=position:relative>"+
				"				<div class=ImgBL_balloon_tip style='position:absolute;left:1;top:-43'></div>"+
				"			</div></td>"+
				"			<td class=balloon_spacer_c><div class=Imgballoon_BL></div></td>"+
				"			<td class=balloon_spacer_h><div class=Imgballoon_BM style='width:100%'></div></td>"+
				"			<td class=balloon_spacer_c><div class=Imgballoon_BR></div></td>"+
				"			<td class=balloon_spacer_tip valign=bottom><div style=position:relative>"+
				"				<div class=ImgBR_balloon_tip style='position:absolute;left:-14;top:-43'></div>"+
				"			</div></td>"+
				"		</tr>"+
				"	</table>"+
				"</div class=balloon_frame>",
		width:40,
		height:45
	}
);


DwtBorder.registerBorder(
	"dialog",
	{
		start:	"	<table class='dialog_table' cellpadding='0'>" +
				"	<tr><td class='border_outset_c'><div class='dialog_outset_tl'></div></td>" +
				"		<td class='border_outset_h'><div class='dialog_outset_t' style='width:100%'></div></td>" +
				"		<td class='border_outset_c'><div class='dialog_outset_tr'></div></td>" +
				"		<td rowspan=4 valign=top class='border_shadow_v'><div class='dialog_shadow_tr'></div>" +
				"						<div class='dialog_shadow_r' style='height:100%'></div>" +
				"		</td>" +
				"	</tr>" +
				"	<tr><td><div class='dialog_outset_l' style='height:100%'></div></td>" +
				"		<td id='<!--$titleId-->' class='dialog_title'><table class='dialog_table' cellpadding='0'><tr>" +
				"			<td class='dialog_title_cell'><!--$icon--></td>" +
				"			<td id='<!--$titleTextId-->' class='dialog_title_cell' style='width:100%'><!--$title--></td>" +
				"			<td class='dialog_title_cell'><div class='<!--$closeIcon2-->' style='cursor:pointer'></div></td>" +
				"			<td class='dialog_title_cell'><div class='<!--$closeIcon1-->' style='cursor:pointer'></div></td>" +
				"		</tr></table></td>" +
				"		<td><div class='dialog_outset_r' style='height:100%'></div></td>" +
				"	</tr>" +
				"	<tr><td><div class='dialog_outset_l' style='height:100%'></div></td>" +
				"		<td><div class='dialog_outset_contents'>" +
				"			<div class='dialog_inset'>" +
				"				<table class='dialog_table' cellpadding='0'>" +
				"				<tr><td class='border_inset_c'><div class='dialog_inset_tl'></div></td>" +
				"					<td class='border_h'><div class='dialog_inset_t' style='width:100%'></div></td>" +
				"					<td class='border_inset_c'><div class='dialog_inset_tr'></div></td>" +
				"				</tr>" +
				"				<tr><td class='border_v'><div class='dialog_inset_l' style='height:100%'></div></td>" +
				"					<td><div class='dialog_inset_contents'>",


		end:	"					</div></td>" +
				"					<td><div class='dialog_inset_r' style='height:100%'></div></td>" +
				"				</tr>" +
				"				<tr><td><div class='dialog_inset_bl'></div></td>" +
				"					<td><div class='dialog_inset_b' style='width:100%'></div></td>" +
				"					<td><div class='dialog_inset_br'></div></td>" +
				"				</tr>" +
				"				</table>" +
				"			</div>" +
				"		" +
				"		</div></td>" +
				"		<td><div class='dialog_outset_r' style='height:100%'></div></td>" +
				"	</tr>" +
				"	<tr><td><div class='dialog_outset_bl'></div></td>" +
				"		<td><div class='dialog_outset_b' style='width:100%'></div></td>" +
				"		<td><div class='dialog_outset_br'></div></td>" +
				"	</tr>" +
				"	<tr><td colspan=4>" +
				"		<table class='dialog_table' cellpadding='0'><tr>" +
				"			<td class='dialog_shadow_c'><div class='dialog_shadow_bl'></div><td>" +
				"			<td class='dialog_shadow_h'><div class='dialog_shadow_b' style='width:100%'></div><td>" +
				"			<td class='dialog_shadow_c'><div class='dialog_shadow_br'></div><td>" +
				"		</tr></table>" +
				"		</td>" +
				"	</tr>" +
			    "	</table>",
		width:40,
		height:45
	}
);


DwtBorder.registerBorder(
	"non-modal dialog",
	{
		start:	"	<table class='dialog_table' cellpadding='0'>" +
				"	<tr><td class='border_outset_c'><div class='console_outset_tl'></div></td>" +
				"		<td class='border_outset_h'><div class='console_outset_t' style='width:100%'></div></td>" +
				"		<td class='border_outset_c'><div class='console_outset_tr'></div></td>" +
				"		<td rowspan=4 valign=top class='Xborder_shadow_v'><div class='Xdialog_shadow_tr'></div>" +
				"						<div class='Xdialog_shadow_r' style='height:100%'></div>" +
				"		</td>" +
				"	</tr>" +
				"	<tr><td><div class='console_outset_l' style='height:100%'></div></td>" +
				"		<td id='<!--$titleId-->' class='console_title'><table class='dialog_table' cellpadding='0'><tr>" +
				"			<td class='console_title_cell'><!--$icon--></td>" +
				"			<td id='<!--$titleTextId-->' class='console_title_cell' style='width:100%'><!--$title--></td>" +
				"			<td class='console_title_cell'><div class='sm_icon_dialog_close'></div></td>" +
				"		</tr></table></td>" +
				"		<td><div class='console_outset_r' style='height:100%'></div></td>" +
				"	</tr>" +
				"	<tr><td><div class='console_outset_l' style='height:100%'></div></td>" +
				"		<td><div class='console_outset_contents'>" +
				"			<div class='console_inset'>" +
				"				<table class='dialog_table' cellpadding='0'>" +
				"				<tr><td class='border_inset_c'><div class='console_inset_tl'></div></td>" +
				"					<td class='border_h'><div class='console_inset_t' style='width:100%'></div></td>" +
				"					<td class='border_inset_c'><div class='console_inset_tr'></div></td>" +
				"				</tr>" +
				"				<tr><td class='border_v'><div class='console_inset_l' style='height:100%'></div></td>" +
				"					<td><div class='console_inset_contents'>",


		end:	"					</div></td>" +
				"					<td><div class='console_inset_r' style='height:100%'></div></td>" +
				"				</tr>" +
				"				<tr><td><div class='console_inset_bl'></div></td>" +
				"					<td><div class='console_inset_b' style='width:100%'></div></td>" +
				"					<td><div class='console_inset_br'></div></td>" +
				"				</tr>" +
				"				</table>" +
				"			</div>" +
				"		" +
				"		</div></td>" +
				"		<td><div class='console_outset_r' style='height:100%'></div></td>" +
				"	</tr>" +
				"	<tr><td><div class='console_outset_bl'></div></td>" +
				"		<td><div class='console_outset_b' style='width:100%'></div></td>" +
				"		<td><div class='console_outset_br'></div></td>" +
				"	</tr>" +
				"	<tr><td colspan=4>" +
				"		<table class='dialog_table' cellpadding='0'><tr>" +
				"			<td class='Xdialog_shadow_c'><div class='dialog_shadow_bl'></div><td>" +
				"			<td class='Xdialog_shadow_h'><div class='dialog_shadow_b' style='width:100%'></div><td>" +
				"			<td class='Xdialog_shadow_c'><div class='dialog_shadow_br'></div><td>" +
				"		</tr></table>" +
				"		</td>" +
				"	</tr>" +
			    "	</table>",
		width:40,
		height:45
	}
);


DwtBorder.registerBorder(
	"h_sash",
	{	
		start:"<table width=100% cellspacing=0 cellpadding=0><tr>"+
				"<td width=4><div class=h_sash_l></div></td>"+
				"<td width=50%><div class=h_sash_m style='width:100%'></div></td>"+
				"<td width=29><div class=h_sash_grip></div></td>"+
				"<td width=50%><div class=h_sash_m style='width:100%'></div></td>"+
				"<td width=4><div class=h_sash_r></div></td>"+
				"</tr></table>"
			,
		end:"",
		width:10,	//NOT ACCURATE
		height:7
	}
);


DwtBorder.registerBorder(
	"calendar_appt",
	{	
		start:[	
				"<table width=100% height=100% cellspacing=0 cellpadding=0>",
			  	"<tr>",
					"<td class=appt_c><div class=Imgappt<!--$newState-->_TL></div></td>",
					"<td class=appt_h><div class=Imgappt<!--$newState-->_TM></div></td>",
					"<td class=appt_c><div class=Imgappt<!--$newState-->_TR></div></td>",
					"<td class=appt_shadow_v rowspan=3 valign=top>",
						"<div class=Imgappt<!--$newState-->_shadow_TR></div>",
						"<div class=Imgappt<!--$newState-->_shadow_R></div>",
					"</td>",
				"</tr>",
 			  	"<tr height=100%>",
					"<td><div class=Imgappt<!--$newState-->_ML></div></td>",
					"<td class=appt<!--$newState-->_header valign=top>",
						"<div class=appt_body>",
							"<table width=100% height=1 cellspacing=0 cellpadding=2>",
							"<tr class=appt<!--$newState--><!--$color-->_header>",
								"<td class=appt<!--$newState-->_name>",
									"<!--$name-->",
								"</td><td class=appt<!--$newState-->_tag>",
									"<!--$tag-->",
							"</tr>",
							"</table>",
							"<table width=100% height=100% cellspacing=0 cellpadding=2>",
							"<tr valign=top class=appt<!--$newState--><!--$color-->_body>",
								"<td class=appt_time><!--$starttime--></td>",
								"<td class=appt_status-<!--$statusKey-->><!--$status--></td>",
							"</tr>",
							"<tr class=appt<!--$newState--><!--$color-->_body>",
								"<td colspan=2 class=appt_location><!--$location--></td>",
							"</tr>",
							"</table>",
						"</div>",
					"</td>",
					"<td><div class=Imgappt<!--$newState-->_MR></div></td>",
				"</tr>",
			  	"<tr>",
					"<td><div class=Imgappt<!--$newState-->_BL></div></td>",
					"<td><div class=Imgappt<!--$newState-->_BM></div></td>",
					"<td><div class=Imgappt<!--$newState-->_BR></div></td>",
				"</tr>",
				"<tr>",		// shadow bottom
					"<td class=appt_shadow_v colspan=4>",
						"<table width=100% cellspacing=0 cellpadding=0><tr>",
							"<td class=appt_shadow_c><div class=Imgappt<!--$newState-->_shadow_BL></div></td>",
							"<td class=appt_shadow_h><div class=Imgappt<!--$newState-->_shadow_B></div></td>",
							"<td class=appt_shadow_h><div class=Imgappt<!--$newState-->_shadow_BR></div></td>",
						"</tr></table>",
					"</td>",
				"</tr>",
				"</table>"
			].join(""),
		end: "",
		width:10,	//NOT ACCURATE
		height:7
	}
);


DwtBorder.registerBorder(
	"calendar_appt_30",
	{	
		start:[
				"<table width=100% cellspacing=0 cellpadding=0>",
			  	"<tr>",
					"<td width=8><div class=Imgappt_30<!--$newState-->_L></div></td>",
					"<td width=100% class=Imgappt_30<!--$newState-->_M style='overflow:visible' valign=top>",
						"<div class=appt_30_body>",
						"<div class=appt_30<!--$newState--><!--$color-->_header>",
							"<table width=100% cellspacing=0 cellpadding=2>",
							"<tr>",
								"<td class=appt_30<!--$newState-->_name>",
									"<!--$name-->",
								"</td><td class=appt<!--$newState-->_tag>",
									"<!--$tag-->",
							"</tr>",
							"</table>",
						"</div>",
						"</div>",
					"</td>",
					"<td><div class=Imgappt_30<!--$newState-->_R></div></td>",
					"<td class=appt_shadow_v valign=top>",
						"<div class=Imgappt<!--$newState-->_shadow_TR></div>",
						"<div class=Imgappt<!--$newState-->_shadow_R></div>",
					"</td>",
				"</tr>",
				"<tr>",		// shadow bottom
					"<td class=appt_shadow_v colspan=4>",
						"<table width=100% cellspacing=0 cellpadding=0><tr>",
							"<td class=appt_shadow_c><div class=Imgappt<!--$newState-->_shadow_BL></div></td>",
							"<td class=appt_shadow_h><div class=Imgappt<!--$newState-->_shadow_B></div></td>",
							"<td class=appt_shadow_h><div class=Imgappt<!--$newState-->_shadow_BR></div></td>",
						"</tr></table>",
					"</td>",
				"</tr>",
				"</table>"
		].join(""),
		
		end:	"",
		width:10,	//NOT ACCURATE
		height:7
	}
);
