<%@taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@page import="java.io.*" %>
<%@page import="java.util.*" %>
<%
String skin = request.getParameter("skin");
if (skin == null) skin = "sand";
%>
<html>
<head>
<!-- messages and keys -->
<script src='../../js/msgs/I18nMsg,AjxMsg,ZMsg,ZmMsg.js'></script>
<script src="../../js/keys/AjxKeys,ZmKeys.js"></script>
<!-- source code -->
<jsp:include page="../../public/jsp/Boot.jsp" />
<jsp:include page="../../public/jsp/Ajax.jsp" />
<jsp:include page="../../public/jsp/Zimbra.jsp" />
<script>
var appContextPath = "<%=request.getContextPath()%>";
</script>
<jsp:include page="../../public/jsp/ZimbraCore.jsp" />
<!-- skin changer source -->
<script>
var skin = {};

var skinStylesUrl = "../../css/imgs,common,dwt,msgview,login,zm,spellcheck,wiki,@SKIN@_imgs,skin.css?skin=@SKIN@&debug=true";
var skinSourceUrl = "../../js/skin.js?skin=@SKIN@&debug=true";
var skinHtmlUrl = "../../html/skin.html?skin=@SKIN@&debug=true";

function $(id) { return document.getElementById(id); }

function launch() {
  // setup dwt
  window.DBG = new AjxDebug(AjxDebug.NONE, null, false);
  var shell = new DwtShell(null, null, false, $("main"), null);
  var appCtxt = new ZmAppCtxt();
  appCtxt.setShell(shell);

  // load skin
  loadSkin("<%=skin%>");
}

function loadSkin(skin) {
  // reload styles
  var stylesEl = $("skin-styles");
  stylesEl.onload = AjxCallback.simpleClosure(skinStylesLoaded, null, skin);
  stylesEl.href = skinStylesUrl.replace(/@SKIN@/g, skin);

  // remove old source element, if present
  var sourceEl = $("skin-source");
  if (sourceEl) sourceEl.parentNode.removeChild(sourceEl);

  // load sources
  var sourceEl = document.createElement("SCRIPT");
  sourceEl.id = "skin-source";
  sourceEl.onload = AjxCallback.simpleClosure(skinSourceLoaded, null, skin);
  sourceEl.src = skinSourceUrl.replace(/@SKIN@/g, skin);
  document.getElementsByTagName("HEAD")[0].appendChild(sourceEl);
}

function skinStylesLoaded(skin) {
//  alert("styles loaded - "+skin);
}

function skinSourceLoaded(skin) {
//  alert("source loaded - "+skin);
  var htmlUrl = skinHtmlUrl.replace(/@SKIN@/g, skin);
  var callback = new AjxCallback(null, skinHtmlLoaded, [skin]);
  AjxRpc.invoke(null, htmlUrl, null, callback, true);
}

function skinHtmlLoaded(skin, result) {
//  alert("html loaded - "+skin);
  var htmlEl = $("skin-body");
  htmlEl.innerHTML = result.text;
  populateSkin();
}

function populateSkin() {
  var shell = DwtShell.getShell(window);

  // create basic components
  var tabs = new DwtToolBar(shell, "ZmAppChooser", DwtControl.ABSOLUTE_STYLE, null, null, null, DwtToolBar.HORIZ_STYLE);
  createTabButton(tabs, "MailApp", "Mail", false);
  createTabButton(tabs, "ContactsApp", "Address Book", false);
  createTabButton(tabs, "CalendarApp", "Calendar", true);

  var skins = createSkinToolBar(shell);

  /***
  var toolbar = new ZmButtonToolBar(shell, "ZmAppToolBar");

  var navtree = new ZmTreeView({ parent: shell, type: ZmOrganizer.FOLDER });
  navtree.set({ dataTree: createFolders() })
  /***/

  // position components
  reparent(tabs, "skin_container_app_chooser");
  reparent(skins, "skin_container_current_app");
  /***
  reparent(toolbar, "toolbar");
  reparent(navtree, "navtree");
  /***/

  skin.show("skin");
  skin.hide("fullScreen");
}

function createSkinToolBar(parent) {
  var listener = new AjxListener(null, handleSkinSelected);
  var menu = new DwtMenu(parent, "ActionMenu");
  var selectEl = $("skin-selector");
  var optionEls = (selectEl && selectEl.options) || [];
  for (var i = 0; i < optionEls.length; i++) {
    var optionEl = optionEls[i];
    var menuitem = new DwtMenuItem(menu, DwtMenuItem.RADIO_STYLE, "skin");
    menuitem.setText(optionEl.value);
    menuitem.setData("value", optionEl.value);
    menuitem.setChecked(optionEl.selected);
    menuitem.addSelectionListener(listener);
  }

  var toolbar = new DwtToolBar(parent)
  var label = new DwtLabel(toolbar, "viewLabel");
  label.setText("Skin:");
  var button = new DwtButton(toolbar, "DwtToolbarButton");
  button.setText(selectEl.options[selectEl.selectedIndex].value);
  button.setMenu(menu);

  return toolbar;
}

function handleSkinSelected(evt) {
  var skin = evt.item.getData("value");
  $("skin-item-"+skin).selected = true;
  loadSkin(skin);
}

function skinSelected(selectEl) {
  var skin = selectEl.options[selectEl.selectedIndex].value;
  loadSkin(skin);
}

function reparent(comp, idOrElement) {
  comp.reparentHtmlElement(idOrElement);
}

function createTabButton(tabs, icon, text, isLast) {
  var button = new ZmChicletButton(tabs, ZmAppChooser.IMAGE[ZmAppChooser.OUTER], icon, text, isLast);
  button.setActivatedImage(ZmAppChooser.IMAGE[ZmAppChooser.OUTER_ACT]);
  button.setTriggeredImage(ZmAppChooser.IMAGE[ZmAppChooser.OUTER_TRIG]);
  button.setToolTipContent("Go to "+text);
  return button;
}

function createFolders() {
  return null; // ZmTree
}
</script>
<!-- skin resources -->
<link id='skin-styles' rel='stylesheet' type='text/css'>
<script id="skin-source"></script>
</head>
<body onload='launch()'>
<div id='main'>
<div style='display:none'>
  <select id='skin-selector' onchange='skinSelected(this)'>
    <%
    String dirname = getServletContext().getRealPath("/skins");
    File dir = new File(dirname);

      List<String> filelist = new LinkedList();
    String[] filenames = dir.list();
    for (String filename : filenames) {
      File file = new File(dir, filename);
      if (file.isDirectory() && !filename.startsWith("_")) {
        filelist.add(filename);
      }
    }

    Collections.sort(filelist);
    for (String filename : filelist) {
      String selected = filename.equals(skin) ? "selected='selected'" : ""; %>
      <option id='skin-item-<%=filename%>' value='<%=filename%>' <%=selected%>><%=filename%></option>
      <%
    }
    %>
  </select>
</div>
<div id='skin-body'></div>
</div>
</body>
</html>