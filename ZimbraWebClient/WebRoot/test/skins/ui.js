// register operations
ZmOperation.registerOp("REPLY_MENU", {textKey:"reply", tooltipKey:"replyTooltip", image:"Reply"});
ZmOperation.registerOp("FORWARD_MENU", {textKey:"forward", tooltipKey:"forwardTooltip", image:"Forward"});
ZmOperation.registerOp("SPAM", {textKey:"junk", tooltipKey:"junkTooltip", image:"SpamFolder"});

// factory functions
function createLogo(parent) {
    return null; // TODO
}

function createUserName(parent) {
    return null; // TODO
}

function createQuota(parent) {
    return null; // TODO
}

function createSearch(parent) {
    return null; // TODO
}

function createSearchBuilderToolbar(parent) {
    return null; // TODO
}

function createSearchBuilder(parent) {
    return null; // TODO
}

function createAppChooser(parent) {
  var toolbar = new DwtToolBar(parent, "ZmAppChooser", DwtControl.ABSOLUTE_STYLE, null, null, null, DwtToolBar.HORIZ_STYLE);
  createTabButton(toolbar, "MailApp", "Mail", false);
  createTabButton(toolbar, "ContactsApp", "Address Book", false);
  createTabButton(toolbar, "CalendarApp", "Calendar", true);
  return toolbar;
}

function createHelp(parent) {
    return null; // TODO
}

function createLogoff(parent) {
    return null; // TODO
}

function createViewToolBar(parent) {
  var listener = new AjxListener(null, handleSkinSelected);

  var toolbar = new DwtToolBar(parent)

  var label = new DwtLabel(toolbar, "viewLabel");
  label.setText("Skin:");

  var button = new DwtButton(toolbar, null, "DwtToolbarButton");
  var selectEl = $("skin-selector");
  button.setText(selectEl.options[selectEl.selectedIndex].value);

  var menu = new DwtMenu(button, "ActionMenu");
  var optionEls = (selectEl && selectEl.options) || [];
  for (var i = 0; i < optionEls.length; i++) {
    var optionEl = optionEls[i];
    var menuitem = new DwtMenuItem(menu, DwtMenuItem.RADIO_STYLE, "skin");
    menuitem.setText(optionEl.value);
    menuitem.setData("value", optionEl.value);
    menuitem.setChecked(optionEl.selected);
    menuitem.addSelectionListener(listener);
  }
  button.setMenu(menu);

  return toolbar;
}

function createAppToolBar(parent) {
  var buttons = [
      ZmOperation.NEW_MENU, ZmOperation.REFRESH, ZmOperation.TAG_MENU,
      "vertSep",
      ZmOperation.DELETE, ZmOperation.MOVE_TO_FOLDER, ZmOperation.PRINT,
      "vertSep",
      ZmOperation.REPLY_MENU, ZmOperation.FORWARD_MENU,
      "vertSep",
      ZmOperation.SPAM//, DETACH
      // spacer
      // NAV BUTTONS
  ];
  var params = {
      parent: parent,
      buttons: buttons
  }
  var toolbar = new ZmButtonToolBar(params);
  return toolbar;
}

function createOverviewTree(parent) {
    return null; // TODO
}

function createTreeFooter(parent) {
    return null; // TODO
}

function createStatus(parent) {
    return null; // TODO
}

function createTabButton(tabs, icon, text, isLast) {
  var button = new ZmChicletButton(tabs, ZmAppChooser.IMAGE[ZmAppChooser.OUTER], icon, text, isLast);
  button.setActivatedImage(ZmAppChooser.IMAGE[ZmAppChooser.OUTER_ACT]);
  button.setTriggeredImage(ZmAppChooser.IMAGE[ZmAppChooser.OUTER_TRIG]);
  button.setToolTipContent("Go to "+text);
  return button;
}
