<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<link href="<c:url value="/css/images,common,dwt,msgview,login,zm,spellcheck,wiki,skin.css">
	<c:param name="debug" value='1' />
</c:url>" rel="stylesheet" type="text/css">
<style>
.DwtForm { overflow: auto; }
.CustomButton { border: dashed 2px pink; background-color: lightyellow; }
.CustomButtonBorder { padding: 0.25em; }
.CustomText { text-align: center; }
.ZHover .CustomButtonBorder { background-color: lightgreen; }
.ZActive .CustomButtonBorder { background-color: lightblue; }
.ZTabPage {
	width: 100%;
}
.ZTabPage TH {
	white-space: nowrap;
	vertical-align: top;
	text-align: left;
}
.MyTabPage { margin-top: 1em; }
</style>
<jsp:include page="/public/Resources.jsp">
	<jsp:param name="res" value="I18nMsg,AjxMsg,AjxKeys" />
</jsp:include>
<jsp:include page="/public/Boot.jsp"/>
<script src="<c:url value='/js/ajax/boot/AjxTemplateCompiler.js' />"></script>
<script>
AjxEnv.DEFAULT_LOCALE = "<%=request.getLocale()%>";
AjxWindowOpener = {};
</script>
<script>
<jsp:include page="/js/ajax/util/AjxTimezoneData.js" />
</script>
<jsp:include page="/public/jsp/Ajax.jsp" />
<script>
function onLoad() {
	// setup debugging
	AjxDispatcher.require("Debug");
	DBG = new AjxDebug(AjxDebug.NONE, null, false);
	AjxWindowOpener.HELPER_URL = "${contextPath}/public/frameOpenerHelper.jsp";

	// create shell
	var shell = new DwtShell({userShell:document.getElementById("shell"),docBodyScrollable:true});

	// hide veil
	shell.setBusy(false);
	shell._veilOverlay.style.zIndex = -100;

	// create sample form
	eval("form = "+document.getElementById("form.data").value);
	var before = new Date().getTime();
	control = new DwtForm({parent:shell, form:form});
	var after = new Date().getTime();

	control.reset();

	var count = AjxUtil.keys(control._items).length;
	document.body.appendChild(document.createTextNode("created form with "+count+" controls in "+(after-before)+" milliseconds"));

	// setup tab group
	var tabGroup = new DwtTabGroup("global-tab-group");
	tabGroup.addMember(control);
	shell.getKeyboardMgr().setTabGroup(tabGroup);
}
function showForm() {
	showText(document.getElementById("form.data").value);
}
function showTemplate() {
	showText(document.getElementById("test.zforms#form1").value);
}
function showText(text) {
	var win = open("about:blank");
	var doc = win.document;
	doc.write(
		"<pre>",
		text.replace(/&/g,"&amp;").replace(/</g,"&lt;"),
		"</pre>"
	);
	doc.close();
}
</script>
</head>
<body onload="onLoad()">
<div id='templates' style="display:none">
	<textarea id="form.data">
{
	template: "test.zforms#form1",
	onupdate: function() {
		var ids = this.getDirtyItems();
		ids.sort();
		this.setValue("DIRTY", ids.join(", ") || "None");
		
		var ids = this.getInvalidItems();
		ids.sort();
		this.setValue("INVALID", ids.join(", ") || "None");
	},
	items: [
		{ id: "DIRTY", type: "DwtText" },
		{ id: "INVALID", type: "DwtText" },
		{ id: "UPDATE", type: "DwtButton", label: "Update Form", onclick: "this.update()" },
		{ id: "RESET", type: "DwtButton", label: "Reset Form", onclick: "this.reset()" },
		{ id: "CONTROLS", type: "DwtTabView", onclick: "alert(get('CONTROLS'))", items: [
			// buttons
			{ id: "BUTTONS", label: "Buttons", template: "test.zforms#buttons", items: [
				{ id: "BUTTON1", type: "DwtButton", label: "Click Me" },
				{ id: "BUTTON2", type: "DwtButton", label: "Click Me", enabled: false },
				{ id: "BUTTON3", type: "DwtButton", image: "NewMessage" },
				{ id: "BUTTON4", type: "DwtButton", label: "Click Me", image: "NewMessage" },
				{ id: "BUTTON5", type: "DwtButton", label: "Click Me", className: "CustomButton", template: "test.zforms#button" },
				{ id: "BUTTON6", type: "DwtButton", label: "Click Me", menu:
					{ id: "BUTTON6_MENU", items: [
						{ id: "BUTTON6_MENUITEM1", label: "New Tag", image: "NewTag" },
						{ id: "BUTTON6_MENUITEM2", label: "Mark All as Read", image: "ReadMessage", enabled: false },
						{ id: "BUTTON6_MENUITEM3", label: "Rename Tag", image: "Rename" },
						{ id: "BUTTON6_MENUITEM4", label: "Delete", image: "Delete" },
						{ id: "BUTTON6_MENUITEM5", label: "Tag Color", image: "TagStack", menu:
							{ id: "BUTTON6_MENUITEM5_MENU", onclick: "alert(get('BUTTON6_MENUITEM5_MENU'))", items: [
								{ id: "BUTTON6_MENUITEM5_MENUITEM1", label: "Cyan", onclick: "alert('BUTTON6_MENUITEM5_MENUITEM1 (explicit)')", image: "TagCyan" },
								{ id: "BUTTON6_MENUITEM5_MENUITEM2", label: "Blue", image: "TagBlue" },
								{ id: "BUTTON6_MENUITEM5_MENUITEM3", label: "Purple", image: "TagPurple" },
								{ id: "BUTTON6_MENUITEM5_MENUITEM4", label: "Red", image: "TagRed" },
								{ id: "BUTTON6_MENUITEM5_MENUITEM5", label: "Orange", image: "TagOrange" },
								{ id: "BUTTON6_MENUITEM5_MENUITEM6", label: "Yellow", image: "TagYellow" },
								{ id: "BUTTON6_MENUITEM3_MENUITEM7", label: "Green", image: "TagGreen" }
							]}
						}
					]}
				},
				{ id: "BUTTON7", type: "DwtButtonColorPicker" },
				{ id: "BUTTON8", type: "DwtButton", label: "Calendar", menu:
					{ id: "BUTTON7_MENU", type: "DwtCalendar" }
				},
				{ id: "BUTTON9", type: "DwtButton", label: "onclick (string)", onclick: "alert('BUTTON9')" },
				{ id: "BUTTON10", type: "DwtButton", label: "onclick (function)", onclick: function() { alert('BUTTON10'); } },
				{ id: "BUTTON11", type: "DwtButton", label: "onclick (Menu)", menu:
					{ id: "BUTTON11_MENU", onclick: "alert(get('BUTTON11_MENU'))", items: [
						{ id: "BUTTON11_MENUITEM1", label: "Item 1, w/ onclick", onclick: "alert('BUTTON11_MENUITEM1')" },
						{ id: "BUTTON11_MENUITEM2", label: "Item 2" }
					]}
				},
				{ id: "BUTTON12" },
				{ id: "BUTTON13" },
				{ id: "BUTTON14", onclick: "alert('BUTTON14')" },
				{ id: "BUTTON15", onclick: "alert('BUTTON15')" },
				{ id: "BUTTON16" },
				{ id: "BUTTON17" },
				{ id: "BUTTON18", onclick: "alert('BUTTON18')" },
				{ id: "BUTTON19", onclick: "alert('BUTTON19')" }
			]},
			// checkboxes
			{ id: "CHECKBOXES", label: "Checkboxes", template: "test.zforms#checkboxes", items: [
				{ id: "CHECKBOX1", type: "DwtCheckbox", label: "Option 1" },
				{ id: "CHECKBOX2", type: "DwtCheckbox", label: "Option 2", enabled: false },
				{ id: "CHECKBOX3", type: "DwtCheckbox", label: "Option 3", checked: true },
				{ id: "CHECKBOX4", type: "DwtCheckbox", label: "Option 4", onclick:"alert('CHECKBOX4')" },
				{ id: "CHECKBOX5" },
				{ id: "CHECKBOX6" },
				{ id: "CHECKBOX7", onclick:"alert('CHECKBOX7')" },
				{ id: "CHECKBOX8", onclick:"alert('CHECKBOX8')" }
			]},
			// input fields
			{ id: "INPUT_FIELDS", label: "Input Fields", template: "test.zforms#inputfields", items: [
				{ id: "INPUT1", type: "DwtInputField", value: "Some Text" },
				{ id: "INPUT2", type: "DwtInputField", value: "Some Text", enabled: false },
				{ id: "INPUT3", type: "DwtInputField", hint: "Some Text" },
				{ id: "INPUT4", type: "DwtInputField", value: "Some Text", cols: 80 },
				{ id: "INPUT5", type: "DwtInputField", value: "Some Text", cols: 80, rows: 2 },
				{ id: "INPUT6", type: "DwtInputField", onchange: "alert('INPUT6')" },
				{ id: "INPUT7" },
				{ id: "INPUT8" },
				{ id: "INPUT9", onchange: "alert(get('INPUT9'))" },
				{ id: "INPUT10", onchange: "alert(get('INPUT10'))" }
			]},
			/***
			// lists
			{ id: "LISTS", label: "Lists", template: "test.zforms#lists", items: [
				{ id: "LIST1", type: "CustomList" },
				{ id: "LIST1_SIZE", getter: "this.getControl('LIST1').size()" },
				{ id: "LIST1_TEXT", type: "DwtText", getter: "'Size: '+get('LIST1_SIZE')+', Selection: '+get('LIST1')" } 
			]},
			/***/
			// radio buttons
			{ id: "RADIO_BUTTONS", label: "Radio Buttons", template: "test.zforms#radiobuttons", items: [
				{ id: "RADIOS1", type: "DwtRadioButtonGroup", value: "one", items: [
					{ id: "RADIO1", label: "radio button 1", value: "one" },
					{ id: "RADIO2", label: "radio button 2", value: "two" },
					{ id: "RADIO3", label: "radio button 3", value: "three" }
				]},
				{ id: "RADIOS1_VALUE", type: "DwtText", getter: "get('RADIOS1') || 'None'" },
				{ id: "RADIO_BUTTON1", type: "DwtButton", label: "Show Value (onclick string)", onclick: "alert(get('RADIOS1'))" },
				{ id: "RADIO_BUTTON2", type: "DwtButton", label: "Show Value (onclick function)",
					onclick: function() {
						alert(this.getValue('RADIOS1'));
					}
				},
				{ id: "RADIOS2", type: "DwtRadioButtonGroup", value:"two", enabled: false, items: [
					{ id: "RADIO4", label: "radio button 1", value: "one" },
					{ id: "RADIO5", label: "radio button 2", value: "two", checked: true },
					{ id: "RADIO6", label: "radio button 3", value: "three" }
				]},
				{ id: "RADIOS2_VALUE", type: "DwtText", getter: "get('RADIOS2') || 'None'" },
				{ id: "RADIOS3", type: "DwtRadioButtonGroup", value: "three", items: [
					{ id: "RADIO7", label: "radio button 1", value: "one" },
					{ id: "RADIO8", label: "radio button 2", value: "two", enabled: false },
					{ id: "RADIO9", label: "radio button 3", value: "three", checked: true }
				]},
				{ id: "RADIOS3_VALUE", type: "DwtText", getter: "get('RADIOS3') || 'None'" },
				{ id: "RADIOS4", type: "DwtRadioButtonGroup", items: [
					{ id: "RADIO10", label: "radio button 1", value: "one", onclick: "alert('RADIO10')" },
					{ id: "RADIO11", label: "radio button 2", value: "two", onclick: "alert('RADIO11')" },
					{ id: "RADIO12", label: "radio button 3", value: "three", onclick: "alert('RADIO12')" }
				]},
				{ id: "RADIOS4_VALUE", type: "DwtText", getter: "get('RADIOS4') || 'None'" },
				{ id: "RADIOS5", type: "DwtRadioButtonGroup", onclick: "alert(get('RADIOS5'))", items: [
					{ id: "RADIO13", label: "radio button 1", value: "one" },
					{ id: "RADIO14", label: "radio button 2", value: "two" },
					{ id: "RADIO15", label: "radio button 3", value: "three" }
				]},
				{ id: "RADIOS5_VALUE", type: "DwtText", getter: "get('RADIOS5') || 'None'" },
				<%--{ id: "RADIOS6", items: [--%>
					{ id: "RADIO16" },
					{ id: "RADIO17" },
					{ id: "RADIO18" },
				<%--]},--%>
				{ id: "RADIOS6_VALUE", type: "DwtText", getter: "get('RADIOS6') || 'None'" },
				{ id: "RADIOS7", items: [
					{ id: "RADIO19" },
					{ id: "RADIO20" },
					{ id: "RADIO21" }
				]},
				{ id: "RADIOS7_VALUE", type: "DwtText", getter: "get('RADIOS7') || 'None'" }
			]},
			// rows
			{ id: "ROWS", label: "Rows", template: "test.zforms#rows", items: [
				{ id: "ROWS1", type: "DwtFormRows",
					rowitem: { type: "DwtInputField", hint: "added row" },
					items: [
						{ type: "DwtInputField", hint: "default row 1" },
						{ type: "DwtInputField", hint: "default row 2" },
					]
				},
				{ id: "ROWS2", type: "DwtFormRows",
					rowitem: { type: "DwtInputField", hint: "added row" },
					items: [
						{ type: "DwtInputField", hint: "default row 1" },
						{ type: "DwtInputField", hint: "default row 2" },
					]
				},
				{ id: "ROWS3", type: "DwtFormRows",
					minrows: 2, maxrows: 4,
					rowitem: { type: "DwtInputField", hint: "added row" }
				},
				{ id: "ROWS4", type: "DwtFormRows",
					additem: { label: "Add" },
					removeitem: { label: "Remove" },
					rowitem: { type: "DwtInputField", hint: "added row" },
					items: [
						{ type: "DwtInputField", hint: "default row 1" },
						{ type: "DwtInputField", hint: "default row 2" },
					]
				}
			]},
			// selects
			{ id: "SELECTS", label: "Selects", template: "test.zforms#selects", items: [
				{ id: "SELECT1", type: "DwtSelect", items: [
					{ value: "only-value" },
					{ value: "value-n-label", label: "Value and Label" },
					{ value: "value-label-image", label: "Value, Label, and Image", image: "NewMessage" }
				]},
				{ id: "SELECT2", type: "DwtSelect", enabled: false, items: [
					{ value: "only-value" },
					{ value: "value-n-label", label: "Value and Label" },
					{ value: "value-label-image", label: "Value, Label, and Image", image: "NewMessage" }
				]},
				{ id: "SELECT3", type: "DwtSelect", onchange: "alert(get('SELECT1'))", items: [
					{ value: "only-value" },
					{ value: "value-n-label", label: "Value and Label" },
					{ value: "value-label-image", label: "Value, Label, and Image", image: "NewMessage" }
				]},
				{ id: "SELECT4" },
				{ id: "SELECT5" },
				{ id: "SELECT6", onchange: "alert(get('SELECT6'))" },
				{ id: "SELECT7", onchange: "alert(get('SELECT7'))" }
			]},
			/***
			// tabs
			{ id: "TABS", label: "Tabs", template: "test.zforms#tabs", items: [
				{ id: "TABS1", type: "DwtTabView", onclick: "alert('TABS1')", items: [
					{ id: "TABS1_PAGE1", label: "Page 1", template: "test.zforms#page1", items: [
						{ id: "PAGE1_BUTTON", type: "DwtButton", label: "Click Me", onclick: "alert('PAGE1_BUTTON')" }
					]},
					{ id: "TABS1_PAGE2", image: "NewMessage", template: "test.zforms#page2" },
					{ id: "TABS1_PAGE3", label: "Page 3", image: "NewMessage", template: "test.zforms#page3" }
				]}
			]},
			/***/
			// static text
			{ id: "TEXT", label: "Text", template: "test.zforms#text", items: [
				{ id: "TEXT1", type: "DwtText", value: "enabled text" },
				{ id: "TEXT2", type: "DwtText", value: "disabled text", enabled: false }
			]},
			// toolbar
			{ id: "TOOLBARS", label: "Toolbars", template: "test.zforms#toolbars", items: [
				{ id: "TOOLBAR1", type: "DwtToolBar", onclick: "alert(get('TOOLBAR1'))", items: [
					{ id: "TOOLBAR1_NEW", label: "New", image: "NewMessage", menu:
						{ id: "TOOLBAR1_NEW_MENU", onclick: "alert(get('TOOLBAR1_NEW_MENU'))", items: [
							{ id: "TOOLBAR1_NEW_MENUITEM1", label: "New Email", image: "NewMessage" },
							{ id: "TOOLBAR1_NEW_MENUITEM2", label: "New Contact", image: "NewContact" },
							{ id: "TOOLBAR1_NEW_MENUITEM3", label: "New Contact Group", image: "NewGroup" },
							{ id: "TOOLBAR1_NEW_MENUITEM4", label: "New Appointment", image: "NewAppointment" },
							{ id: "TOOLBAR1_NEW_MENUITEM5", label: "New Task", image: "NewTask" },
							{ id: "TOOLBAR1_NEW_MENUITEM6", label: "New Page", image: "NewPage" },
							{ id: "TOOLBAR1_NEW_MENUITEM7", label: "New Upload File", image: "NewPage" },
							{ type: DwtMenuItem.SEPARATOR_STYLE },
							{ id: "TOOLBAR1_NEW_MENUITEM8", label: "New Address Book", image: "NewContactsFolder" },
							{ id: "TOOLBAR1_NEW_MENUITEM9", label: "New Calendar", image: "NewAppointment" },
							{ id: "TOOLBAR1_NEW_MENUITEM10", label: "New Task List", image: "NewTaskList" },
							{ id: "TOOLBAR1_NEW_MENUITEM11", label: "New Notebook", image: "NewNotebook" },
							{ id: "TOOLBAR1_NEW_MENUITEM12", label: "New Briefcase", image: "NewFolder" }
						]}
					},
					{ type: DwtToolBar.SEPARATOR, className: "vertSep" },
					{ id: "TOOLBAR1_REFRESH", label: "Get Mail", image: "Refresh" },
					{ type: DwtToolBar.SEPARATOR, className: "vertSep" },
					{ id: "TOOLBAR1_DELETE", label: "Delete", image: "Delete", enabled: false },
					{ id: "TOOLBAR1_MOVE", image: "MoveToFolder" },
					{ id: "TOOLBAR1_PRINT", image: "Print" },
					{ type: DwtToolBar.FILLER },
					{ id: "TOOLBAR1_LEFT", image: "LeftArrow", onclick:"alert('TOOLBAR1_LEFT')" },
					{ id: "TOOLBAR1_RANGE", type: "DwtText", value: "1-25" },
					{ id: "TOOLBAR1_RIGHT", image: "RightArrow", onclick:"alert('TOOLBAR1_RIGHT')" }
				]}
			]},
			// conditional
			{ id: "CONDITIONALS", label: "Conditionals", template: "test.zforms#conditionals", items: [
				{ id: "COND_CHECKBOX1", type: "DwtCheckbox", label: "Enable button" },
				{ id: "COND_BUTTON1", type: "DwtButton", label: "Click Me",
						enabled: "get('COND_CHECKBOX1')",
						onclick:"alert('COND_BUTTON1')" },
				{ id: "COND_CHECKBOX2", type: "DwtCheckbox", label: "Show button" },
				{ id: "COND_BUTTON2", type: "DwtButton", label: "Click Me", visible: "get('COND_CHECKBOX2')", onclick:"alert('COND_BUTTON2')" },
				{ id: "COND_CHECKBOX3", type: "DwtCheckbox", label: "Show element" },
				{ id: "COND_DIV3", visible: "get('COND_CHECKBOX3')" },
				{ id: "COND_INPUT4", type: "DwtInputField", hint: "Type some text", onchange: "set('COND_TEXT4',get('COND_INPUT4',''))" },
				{ id: "COND_TEXT4", type: "DwtText", value: "Entered text will appear here." }
			]}
		]}
	]
}
	</textarea>
	<textarea id="test.zforms#form1">
		<div style="padding:1em;">
			<div align="center">
				<a href="javascript:showForm()" tabindex="1000">Form Description</a> &middot;
				<a href="javascript:showTemplate()" tabindex="1010">Form Template</a>
			</div>
			<table>
				<tr><th>Dirty items:</th><td><div id="\${id}_DIRTY"></div></td></tr>
				<tr><th>Invalid items:</th><td><div id="\${id}_INVALID"></div></td></tr>
			</table>
			<div id="\${id}_UPDATE"></div>
			<div id="\${id}_RESET"></div>
			<h2>Controls</h2>
			<div id="\${id}_CONTROLS"></div>
		</div>
	</textarea>
	<textarea id="test.zforms#buttons">
		<div class="MyTabPage">
		<table>
			<tr><th>Enabled:<td><div id="\${id}_BUTTON1" tabindex="1020"></div>
			<tr><th>Disabled:<td><div id="\${id}_BUTTON2" tabindex="1030"></div>
			<tr><th>Image only:
				<td><table cellpadding="0" cellspacing="0">
						<tr><td><div id="\${id}_BUTTON3" tabindex="1040"></div></td></tr>
					</table>
			<tr><th>Image and label:<td><div id="\${id}_BUTTON4" tabindex="1050"></div>
			<tr><th>Custom:<td><div id="\${id}_BUTTON5" tabindex="1060"></div>
			<tr><th>Menu (sub-menu):<td><div id="\${id}_BUTTON6" tabindex="1070"></div>
			<tr><th>Menu (picker):
				<td style="padding-right:0.5em;"><div id="\${id}_BUTTON7" tabindex="1071"></div>
				<td><div id="\${id}_BUTTON8" tabindex="1072"></div>
			<tr><th>onclick:
				<td><div id="\${id}_BUTTON9" tabindex="1073"></div>
				<td><div id="\${id}_BUTTON10" tabindex="1074"></div>
				<td><div id="\${id}_BUTTON11" tabindex="1075"></div>
			<tr><th>&amp;lt;button>:</th>
				<td><button id="\${id}_BUTTON12">no onclick</button></td>
				<td><button id="\${id}_BUTTON13" onclick="alert('clicked!')">button onclick</button></td>
				<td><button id="\${id}_BUTTON14">item onclick</button></td>
				<td><button id="\${id}_BUTTON15" onclick="alert('clicked!')">both onclick</button></td>
			<tr><th>&amp;lt;input type=button>:</th>
				<td><input id="\${id}_BUTTON16" type='button' value='no onclick'></td>
				<td><input id="\${id}_BUTTON17" type='button' value='button onclick' onclick="alert('clicked!')"></td>
				<td><input id="\${id}_BUTTON18" type='button' value='item onclick'></td>
				<td><input id="\${id}_BUTTON19" type='button' value='both clicked' onclick="alert('clicked!')"></td>
		</table>
		</div>
	</textarea>
	<textarea id="test.zforms#checkboxes">
		<div class="MyTabPage">
		<table>
			<tr><th>Enabled:<td><div id="\${id}_CHECKBOX1" tabindex="1080"></div>
			<tr><th>Disabled:<td><div id="\${id}_CHECKBOX2" tabindex="1090"></div>
			<tr><th>Checked:<td><div id="\${id}_CHECKBOX3" tabindex="1100"></div>
			<tr><th>onclick:<td><div id="\${id}_CHECKBOX4" tabindex="1100"></div>
			<tr><th>&amp;lt;input type=checkbox>
				<td><input id="\${id}_CHECKBOX5" type=checkbox>
					<label for="\${id}_CHECKBOX5">no onclick</label>
				<td><input id="\${id}_CHECKBOX6" type=checkbox onclick='alert("clicked!")'>
					<label for="\${id}_CHECKBOX6">checkbox onclick</label>
				<td><input id="\${id}_CHECKBOX7" type=checkbox>
					<label for="\${id}_CHECKBOX7">item onclick</label>
				<td><input id="\${id}_CHECKBOX8" type=checkbox onclick='alert("clicked!")'>
					<label for="\${id}_CHECKBOX8">both onclick</label>
		</table>
		</div>
	</textarea>
	<textarea id="test.zforms#inputfields">
		<div class="MyTabPage">
		<table>
			<tr><th>Enabled:<td><div id="\${id}_INPUT1" tabindex="1110"></div>
			<tr><th>Disabled:<td><div id="\${id}_INPUT2" tabindex="1120"></div>
			<tr><th>Hint:<td><div id="\${id}_INPUT3" tabindex="1130"></div>
			<tr><th>Wide input:<td colspan="3"><div id="\${id}_INPUT4" tabindex="1140"></div>
			<tr><th>Text area:<td colspan="3"><div id="\${id}_INPUT5" tabindex="1150"></div>
			<tr><th>onchange:<td colspan="3"><div id="\${id}_INPUT6" tabindex="1150"></div>
			<tr><th>&amp;lt;input>:</th>
				<td><input id="\${id}_INPUT7" value='no onchange'></td>
				<td><input id="\${id}_INPUT8" value='input onchange' onchange="alert('changed!')"></td>
				<td><input id="\${id}_INPUT9" value='item onchange'></td>
				<td><input id="\${id}_INPUT10" value='both onchange' onchange="alert('changed!')"></td>
			</tr>
		</table>
		</div>
	</textarea>
	<textarea id="test.zforms#lists">
		<div class="MyTabPage">
		<table>
			<tr><th>Enabled:</th>
				<td><div id="\${id}_LIST1"></div>
					<div id="\${id}_LIST1_TEXT"></div>
		</table>
	</textarea>
	<textarea id="test.zforms#radiobuttons">
		<div class="MyTabPage">
		<table>
			<tr><th rowspan="2">Enabled:</th>
				<td><div id="\${id}_RADIO1" tabindex="1160"></div>
				<td><div id="\${id}_RADIO2" tabindex="1161"></div>
				<td><div id="\${id}_RADIO3" tabindex="1162"></div>
				<td>Value:<td><div id="\${id}_RADIOS1_VALUE"></div></td>
			<tr><td colspan="3">
				<table cellpadding="0" cellspacing="3">
					<tr><td><div id="\${id}_RADIO_BUTTON1" tabindex="1165"></div>
						<td><div id="\${id}_RADIO_BUTTON2" tabindex="1266"></div>
				</table>
			<tr><th>Disabled:</th>
				<td><div id="\${id}_RADIO4" tabindex="1170"></div>
				<td><div id="\${id}_RADIO5" tabindex="1171"></div>
				<td><div id="\${id}_RADIO6" tabindex="1172"></div>
				<td>Value:<td><div id="\${id}_RADIOS2_VALUE"></div></td>
			<tr><th>Partly disabled:</th>
				<td><div id="\${id}_RADIO7" tabindex="1180"></div>
				<td><div id="\${id}_RADIO8" tabindex="1181"></div>
				<td><div id="\${id}_RADIO9" tabindex="1182"></div>
				<td>Value:<td><div id="\${id}_RADIOS3_VALUE"></div></td>
			<tr><th>Radio button onclick:</th>
				<td><div id="\${id}_RADIO10" tabindex="1183"></div>
				<td><div id="\${id}_RADIO11" tabindex="1184"></div>
				<td><div id="\${id}_RADIO12" tabindex="1185"></div>
				<td>Value:<td><div id="\${id}_RADIOS4_VALUE"></div></td>
			<tr><th>Group onclick:</th>
				<td><div id="\${id}_RADIO13" tabindex="1186"></div>
				<td><div id="\${id}_RADIO14" tabindex="1187"></div>
				<td><div id="\${id}_RADIO15" tabindex="1188"></div>
				<td>Value:<td><div id="\${id}_RADIOS5_VALUE"></div></td>
			<tr><th rowspan=2>&amp;lt;input type=radio>:</th>
				<td><input id="\${id}_RADIO16" type="radio" name="RADIOS6" value='one'>
					<label for="\${id}_RADIO16">radio button 1</label>
				</td>
				<td><input id="\${id}_RADIO17" type="radio" name="RADIOS6" value='two'>
					<label for="\${id}_RADIO17">radio button 2</label>
				</td>
				<td><input id="\${id}_RADIO18" type="radio" name="RADIOS6" value='three'>
					<label for="\${id}_RADIO18">radio button 3</label>
				</td>
				<td>Value:<td><div id="\${id}_RADIOS6_VALUE"></div></td>
			</tr>
			<tr><td><input id="\${id}_RADIO19" type="radio" name="RADIOS7" value='one'>
					<label for="\${id}_RADIO19">radio button 1</label>
				</td>
				<td><input id="\${id}_RADIO20" type="radio" name="RADIOS7" value='two'>
					<label for="\${id}_RADIO20">radio button 2</label>
				</td>
				<td><input id="\${id}_RADIO21" type="radio" name="RADIOS7" value='three'>
					<label for="\${id}_RADIO21">radio button 3</label>
				</td>
				<td>Value:<td><div id="\${id}_RADIOS7_VALUE"></div></td>
			</tr>
		</table>
		</div>
	</textarea>
	<textarea id="test.zforms#rows">
		<div class="MyTabPage">
		<table>
			<tr><th>Add on all:</th><td><div id="\${id}_ROWS1"></div></td></tr>
			<tr><th>Add on last:</th><td><div id="\${id}_ROWS2"></div></td></tr>
			<tr><th>Min=2, Max=4:</th><td><div id="\${id}_ROWS3"></div></td></tr>
			<tr><th>Custom:</th><td><div id="\${id}_ROWS4"></div></td></tr>
		</table>
		</div>
	</textarea>
	<textarea id="test.zforms#selects">
		<div class="MyTabPage">
		<table>
			<tr><th>Enabled:<td><div id="\${id}_SELECT1" tabindex="1210"></div></td></tr>
			<tr><th>Disabled:<td><div id="\${id}_SELECT2" tabindex="1211"></div></td></tr>
			<tr><th>onclick:<td><div id="\${id}_SELECT3" tabindex="1211"></div></td></tr>
			<tr><th>&amp;lt;select>:</th>
				<td><select id="\${id}_SELECT4">
						<option value="one">One: no onchange</option>
						<option value="two">Two</option>
						<option value="three">Three</option>
					</select>
				</td>
				<td><select id="\${id}_SELECT5" onchange="alert('changed!')">
						<option value="one">One: select onchange</option>
						<option value="two">Two</option>
						<option value="three">Three</option>
					</select>
				</td>
				<td><select id="\${id}_SELECT6">
						<option value="one">One: item onchange</option>
						<option value="two">Two</option>
						<option value="three">Three</option>
					</select>
				</td>
				<td><select id="\${id}_SELECT7" onchange="alert('changed!')">
						<option value="one">One: both onchange</option>
						<option value="two">Two</option>
						<option value="three">Three</option>
					</select>
				</td>
			</tr>
		</table>
		</div>
	</textarea>
	<textarea id="test.zforms#tabs">
		<div class="MyTabPage">
		<table>
			<tr><td><div id="\${id}_TABS1"></div>
		</table>
		</div>
	</textarea>
	<textarea id="test.zforms#text">
		<div class="MyTabPage">
		<table>
			<tr><th>Enabled:<td><div id="\${id}_TEXT1" tabindex="1220"></div>
			<tr><th>Disabled:<td><div id="\${id}_TEXT2" tabindex="1230"></div>
		</table>
		</div>
	</textarea>
	<textarea id="test.zforms#toolbars">
		<div class="MyTabPage">
		<table width="500" border="1" cellpadding="0" cellspacing="0">
			<tr><td><div id="\${id}_TOOLBAR1" tabindex="1240"></div>
		</table>
		</div>
	</textarea>
	<textarea id="test.zforms#conditionals">
		<div class="MyTabPage">
		<table>
			<tr><th>Enable:<td>
				<table cellpadding="0" cellspacing="0">
					<tr><td><div id="\${id}_COND_CHECKBOX1" tabindex="1250"></div>
						<td><div id="\${id}_COND_BUTTON1" tabindex="1260"></div>
				</table>
			<tr><th>Visible (button):<td>
				<table cellpadding="0" cellspacing="0">
					<tr><td><div id="\${id}_COND_CHECKBOX2" tabindex="1270"></div>
						<td><div id="\${id}_COND_BUTTON2" tabindex="1280"></div>
				</table>
			<tr><th>Visible (element):<td>
				<table cellpadding="0" cellspacing="0">
					<tr><td><div id="\${id}_COND_CHECKBOX3" tabindex="1290"></div>
						<td><div id="\${id}_COND_DIV3">plain div content</div>
				</table>
			<tr><th>Update text:<td>
				<table cellpadding="0" cellspacing="0">
					<tr><td style="padding-right:.5em;"><div id="\${id}_COND_INPUT4" tabindex="1300"></div>
						<td><div id="\${id}_COND_TEXT4" tabindex="1310"></div>
					</tr>
				</table>
		</table>
		</div>
	</textarea>
	<textarea id="test.zforms#button">
		<div class="CustomButtonBorder"><div id="\${id}_title" class="CustomText"></div></div>
	</textarea>
	<textarea id="test.zforms#listrow">
		<div><table cellpadding="2" cellspacing="0">
			<tr><td>\${id}</td><td>\${item.name}</td><td>\${item.value}</td></tr>
		</table></div>
	</textarea>
	<textarea id="test.zforms#page1">
		<h3>Page 1</h3>
		<div id="\${id}_PAGE1_BUTTON"></div>
	</textarea>
	<textarea id="test.zforms#page2">
		<h3>Page 2</h3>
	</textarea>
	<textarea id="test.zforms#page3">
		<h3>Page 3</h3>
	</textarea>
	<textarea id="dwt.Widgets#DwtFormRows">
		<table border=0 cellspacing=3 cellpadding=0>
			<tbody id="\${id}_rows"></tbody>
		</table>
	</textarea>
	<textarea id="dwt.Widgets#DwtFormRow">
		<table>
			<tr id="\${id}_row">
				<td><div id="\${id}"></div></td>
				<td><div id="\${id}_remove"></div></td>
				<td><div id="\${id}_add"></div></td>
			</tr>
		</table>
	</textarea>
</div>
<script>
CustomList = function(params) {
	params.headerList = [
		new DwtListHeaderItem({field:"i",text:"Row Id"}),
		new DwtListHeaderItem({field:"n",text:"Name"}),
		new DwtListHeaderItem({field:"v",text:"Value"}),
	];
	params.rowTemplate = "test.zforms#listrow";
	DwtListView.call(this, params);
	for (var i = 0; i < 5; i++) {
		var item = { id: i+1, name: "Name "+i, value: "Value "+i, toString: function() { return this.id; } };
		this.addItem(item);
	}
};
CustomList.prototype = new DwtListView;
CustomList.prototype.constructor = CustomList;
</script>
</body>
</html>