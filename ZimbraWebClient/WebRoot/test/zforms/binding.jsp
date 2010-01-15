<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
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
	eval("model = "+document.getElementById("model.data").value);
	eval("form = "+document.getElementById("form.data").value);
	control = new DwtForm({parent:shell, model:model, form:form});

	// setup tab group
	var tabGroup = new DwtTabGroup("global-tab-group");
	tabGroup.addMember(control);
	shell.getKeyboardMgr().setTabGroup(tabGroup);
}
function showModel() {
	showText(document.getElementById("model.data").value);
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
	<textarea id="model.data">
{
	prefs: {
		general: {
			options: {
				login: "ajax",
				skin: "beach",
				language: null,
				timezone: null
			},
			search: {
				includeJunk: true,
				includeTrash: true,
				show: false
			},
			other: {
				_checkboxes: true,
				setCheckboxes: function(value) {
					this._checkboxes = value;
				},
				getCheckboxes: function() {
					return this._checkboxes;
				}
			}
		}
	}
}
	</textarea>
	<textarea id="form.data">
{
	template: "test.zforms#form1",
	items: [
		{ id: "LOGIN", ref: "prefs.general.options.login", type: "DwtRadioButtonGroup", items: [
			{ id: "LOGIN_AJAX", label: "Advanced (Ajax)", value: "ajax" },
			{ id: "LOGIN_HTML", label: "Standard (HTML)", value: "html" }
		]},
		{ id: "SKIN", ref: "prefs.general.options.skin", type: "DwtSelect", items: [
			{ label: "Bare", value: "bare" },
			{ label: "Beach", value: "beach" },
			{ label: "Yahoo", value: "yahoo" }
		]},
		{ id: "LANGUAGE", ref: "prefs.general.options.language", type: "DwtButton",
			label: "Select Language", menu:
			{ items: [
				{ label: "English", value: "en", menu:
					{ items: [
						{ label: "English (Australia)", value: "en_AU" },
						{ label: "English (United Kingdom)", value: "en_GB" },
						{ label: "English (United States)", value: "en_US" }
					]}
				}
			]}
		},
		{ id: "INCLUDE_JUNK", ref: "prefs.general.search.includeJunk", type: "DwtCheckbox",
			label: "Include Junk Folder in Searches" },
		{ id: "INCLUDE_TRASH", ref: "prefs.general.search.includeTrash", type: "DwtCheckbox",
			label: "Include Trash Folder in Searches" },
		{ id: "SHOW_SEARCH", ref: "prefs.general.search.show", type: "DwtCheckbox",
			label: "Show advanced search language in search toolbar" },
		{ id: "CHECKBOXES", ref: "prefs.general.other.checkboxes", type: "DwtCheckbox",
			label: "Display checkboxes to quickly select items in lists (requires refresh)" }
		/***
		/***/
	]
}
	</textarea>
	<textarea id="test.zforms#form1">
		<div style="padding:1em;">
			<div align="center">
				<a href="javascript:showModel()" tabindex="900">Model</a>
				<a href="javascript:showForm()" tabindex="1000">Form Description</a> &middot;
				<a href="javascript:showTemplate()" tabindex="1010">Form Template</a>
			</div>
			<h2>General</h2>
			<h3>Login Options</h3>
			<table>
				<tr><th rowspan="2">Login using:</th>
					<td><div id="\${id}_LOGIN_AJAX"></div></td>
				</tr>
				<tr><td><div id="\${id}_LOGIN_HTML"></div></td>
				</tr>
				<tr><th>Theme:</th>
					<td><div id="\${id}_SKIN"></div></td>
				</tr>
				<tr><th>Language:</th>
					<td><div id="\${id}_LANGUAGE"></div></td>
				</tr>
			</table>
			<h3>Searches</h3>
			<table>
				<tr><th rowspan="2">Search settings:</th>
					<td><div id="\${id}_INCLUDE_JUNK"></div></td>
				</tr>
				<tr><td><div id="\${id}_INCLUDE_TRASH"></div></td>
				</tr>
				<tr><td colspan="2"><hr></td></tr>
				<tr><th rowspan="2">Search language:</th>
					<td><div id="\${id}_SHOW_SEARCH"></div></td>
				</tr>
			</table>
			<h3>Other</h3>
			<table>
				<tr><th rowspan="2">Settings:</th>
					<td><div id="\${id}_CHECKBOXES"></div></td>
				</tr>
			</table>
		</div>
	</textarea>
</div>
</body>
</html>