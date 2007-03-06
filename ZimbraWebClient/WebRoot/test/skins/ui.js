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
    var direction = skin.hints.app_chooser && skin.hints.app_chooser.direction;
    var orientation = direction == "TB" ? DwtToolBar.VERT_STYLE : DwtToolBar.HORIZ_STYLE;
    var horizontal = orientation == DwtToolBar.HORIZ_STYLE;
    var toolbar = new DwtToolBar(parent, "ZmAppChooser", DwtControl.ABSOLUTE_STYLE, null, null, null, orientation);
    createTabButton(toolbar, "MailApp", horizontal && "Mail", false);
    createTabButton(toolbar, "ContactsApp", horizontal && "Address Book", false);
    createTabButton(toolbar, "CalendarApp", horizontal && "Calendar", true);
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
    /***
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
    toolbar.enableAll(true);
    return toolbar;
    /***/
    return null;
    /***/
}

function createOverviewTree(parent) {
    /***
    var viewParams = {
        parent: parent,
        overviewId: "overviewId",
        type: ZmOrganizer.FOLDER,
        headerClass: "OverviewTree",
        dragSrc: null, //dragSrc,
        dropTgt: null, //dropTgt,
        treeStyle: DwtTree.SINGLE_STYLE,
        allowedTypes: [ ZmOrganizer.FOLDER ],
        allowedSubTypes: [ ZmOrganizer.FOLDER ]
    };
    var viewTree = new ZmTreeView(viewParams);
    var dataParams = {
        dataTree: null //tree        
    };
    viewTree.set(dataParams);
    return viewTree;
    /***/
    return null;
    /***/
}

function createTreeFooter(parent) {
    /***
    var footer = new DwtCalendar(parent);
    return footer;
    /***/
    return null;
    /***/
}

function createStatus(parent) {
    return null; // TODO
}

function createMain(parent) {
    var main = new DwtComposite(parent);

    var el = main.getHtmlElement();
    el.innerHTML = [
        "<h3>DWT Controls</h3>",

        "<table border=0 cellspacing=4 cellpadding=0>",
            "<tr valign=middle>",
                "<th>Label:</th>",
                "<td id='main-label-text'></td>",
                "<td id='main-label-icon-right'></td>",
                "<td id='main-label-icon-left'></td>",
                "<td id='main-label-disabled'></td>",
            "</tr>",
        "</table>",

        "<table border=0 cellspacing=4 cellpadding=0>",
            "<tr valign=middle>",
                "<th>Button:</th>",
                "<td id='main-button-text'></td>",
                "<td id='main-button-icon-right'></td>",
                "<td id='main-button-icon-left'></td>",
                "<td id='main-button-menu'></td>",
                "<td id='main-button-disabled'></td>",
            "</tr>",
        "</table>",

//        "<table border=0 cellspacing=4 cellpadding=0>",
//            "<tr valign=middle>",
//                "<th>Select:</th>",
//                "<td id='main-select-text'></td>",
//                "<td id='main-select-icon-right'></td>",
//                "<td id='main-select-icon-left'></td>",
//                "<td id='main-select-disabled'></td>",
//            "</tr>",
//        "</table>",

        "<h3>Zimbra Controls</h3>",

        "<table border=0 cellspacing=4 cellpadding=0>",
            "<tr valign=middle>",
                "<th>Toolbar:</th>",
                "<td id='main-toolbar'></td>",
            "</tr>",
        "</table>"

    ].join("");

    var labels = [
        new DwtLabel(main), "text",
        new DwtLabel(main), "icon-right",
        new DwtLabel(main), "icon-left",
        new DwtLabel(main), "disabled"
    ];
    labels[2].setImage("Person");
    labels[2].setAlign(DwtLabel.IMAGE_LEFT);
    labels[4].setImage("Person");
    labels[4].setAlign(DwtLabel.IMAGE_RIGHT);
    labels[6].setEnabled(false);
    for (var i = 0; i < labels.length; i += 2) {
        var label = labels[i];
        var text = labels[i+1];
        label.setText(text+":");
        reparent(label, "main-label-"+text);
    }

    var buttons = [
        new DwtButton(main), "text",
        new DwtButton(main), "icon-right",
        new DwtButton(main), "icon-left",
        new DwtButton(main), "menu",
        new DwtButton(main), "disabled"
    ];
    buttons[2].setImage("Person");
    buttons[2].setAlign(DwtLabel.IMAGE_LEFT);
    buttons[4].setImage("Person");
    buttons[4].setAlign(DwtLabel.IMAGE_RIGHT);
    buttons[6].setMenu(createMenu(buttons[6]));
    buttons[8].setEnabled(false);
    for (var i = 0; i < buttons.length; i += 2) {
        var button = buttons[i];
        var text = buttons[i+1];
        button.setText(text);
        reparent(button, "main-button-"+text);
    }
    
//    var selects = [
//        new DwtMenuItem(main), "text",
//        new DwtMenuItem(main), "icon-right",
//        new DwtMenuItem(main), "icon-left",
//        new DwtMenuItem(main), "disabled"
//    ];
//    selects[2].setImage("Person");
//    selects[2].setAlign(DwtLabel.IMAGE_LEFT);
//    selects[4].setImage("Person");
//    selects[4].setAlign(DwtLabel.IMAGE_RIGHT);
//    selects[6].setEnabled(false);
//    for (var i = 0; i < selects.length; i += 2) {
//        var select = selects[i];
//        var text = selects[i+1];
//        select.setText(text);
//        select.addOption("option", null, "option");
//        reparent(select, "main-select-"+text);
//    }

    var toolbar = new ZmToolBar(main);
    var buttons = [
        new ZmToolBarButton(toolbar), "text",
        new ZmToolBarButton(toolbar), null, // icon only
        new ZmToolBarButton(toolbar), "icon-right",
        new ZmToolBarButton(toolbar), "icon-left",
        new ZmToolBarButton(toolbar), "menu",
        new ZmToolBarButton(toolbar), "disabled"
    ];
    buttons[2].setImage("Person");
    buttons[4].setImage("Person");
    buttons[4].setAlign(DwtLabel.IMAGE_LEFT);
    buttons[6].setImage("Person");
    buttons[6].setAlign(DwtLabel.IMAGE_RIGHT);
    buttons[8].setMenu(createMenu(buttons[8]));
    buttons[10].setEnabled(false);
    for (var i = 0; i < buttons.length; i += 2) {
        var button = buttons[i];
        var text = buttons[i+1];
        button.setText(text);
    }
    reparent(toolbar, "main-toolbar");

    return main;
}

function createTabButton(tabs, icon, text, isLast) {
  var button = new ZmChicletButton(tabs, ZmAppChooser.IMAGE[ZmAppChooser.OUTER], icon, text, isLast);
  button.setActivatedImage(ZmAppChooser.IMAGE[ZmAppChooser.OUTER_ACT]);
  button.setTriggeredImage(ZmAppChooser.IMAGE[ZmAppChooser.OUTER_TRIG]);
  button.setToolTipContent("Go to "+text);
  return button;
}

function createMenu(button) {
    var menu = new DwtMenu(button);
    var menuitems = [
        new DwtMenuItem(menu), "text",
        new DwtMenuItem(menu), "icon",
        new DwtMenuItem(menu), "disabled"
    ];
    menuitems[2].setImage("Person");
    menuitems[4].setEnabled(false);
    for (var j = 0; j < menuitems.length; j += 2) {
        var menuitem = menuitems[j];
        var itemtext = menuitems[j+1];
        menuitem.setText(itemtext);
    }
    return menu;
}
