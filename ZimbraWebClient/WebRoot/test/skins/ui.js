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
    function AppChooser() {
        DwtToolBar.apply(this, arguments);
    }
    AppChooser.prototype = new DwtToolBar;
    AppChooser.prototype.constructor = AppChooser;
    AppChooser.prototype.TEMPLATE = "share.Widgets#ZmAppChooser";
	AppChooser.prototype.ITEM_TEMPLATE = "share.Widgets#ZmAppChooserItem";

    var direction = skin.hints.appChooser && skin.hints.appChooser.direction;
    var orientation = direction == "TB" ? DwtToolBar.VERT_STYLE : DwtToolBar.HORIZ_STYLE;
    var horizontal = orientation == DwtToolBar.HORIZ_STYLE;

    var toolbar = new AppChooser(parent, "ZmAppChooser", DwtControl.ABSOLUTE_STYLE, null, null, null, orientation);
    createAppTab(toolbar, "MailApp", horizontal && "Mail", false);
    createAppTab(toolbar, "ContactsApp", horizontal && "Address Book", false, true);
    createAppTab(toolbar, "CalendarApp", horizontal && "Calendar", true);
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

    var labelComp = new DwtLabel(toolbar, "viewLabel");
    labelComp.setText("Skin:");

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
    toolbar.enableAll(true);
    return toolbar;
}

function createOverviewTree(parent) {
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
}

function createTreeFooter(parent) {
    return new DwtCalendar(parent);
}

function createStatus(parent) {
    return null; // TODO
}

function createMain(parent) {
    var main = new DwtComposite(parent);

    // setup HTML
    var el = main.getHtmlElement();
    el.innerHTML = [
        "<h3>DWT Controls</h3>",

        "<table border=0 cellspacing=4 cellpadding=0>",
            "<tr valign=top>",
                "<th>Label:</th>",
                "<td id='main-label-text'></td>",
                "<td>&bull;</td>",
                "<td id='main-label-null'></td>",
                "<td>&bull;</td>",
                "<td id='main-label-icon-left'></td>",
                "<td>&bull;</td>",
                "<td id='main-label-icon-right'></td>",
                "<td>&bull;</td>",
                "<td id='main-label-disabled'></td>",
            "</tr>",
        "</table>",

        "<table border=0 cellspacing=4 cellpadding=0>",
            "<tr valign=top>",
                "<th rowspan=2>Button:</th>",
                "<td id='main-button-text'></td>",
                "<td>&bull;</td>",
                "<td id='main-button-null'></td>",
                "<td>&bull;</td>",
                "<td id='main-button-icon-left'></td>",
                "<td>&bull;</td>",
                "<td id='main-button-icon-right'></td>",
                "<td>&bull;</td>",
                "<td id='main-button-menu'></td>",
                "<td>&bull;</td>",
                "<td id='main-button-toggle'></td>",
                "<td>&bull;</td>",
                "<td id='main-button-disabled'></td>",
            "</tr>",
            "<tr>",
                "<td colspan='13'>",
                    "<table border=0 cellpadding=0 cellspacing=0>",
                        "<tr><td id='main-color-picker'></td>",
                    "</table>",
                "</td>",
            "</tr>",
        "</table>",

        "<table border=0 cellspacing=4 cellpadding=0>",
            "<tr valign=top>",
                "<th>Toolbar:</th>",
                "<td id='main-toolbar' width=100%></td>",
            "</tr>",
        "</table>",

        "<table border=0 cellspacing=4 cellpadding=0>",
            "<tr valign=top>",
                "<th width=1%>Tabs:</th>",
                "<td id='main-tabbar' width=100%></td>",
            "</tr>",
        "</table>",

        "<table border=0 cellspacing=4 cellpadding=0>",
            "<tr valign=top>",
                "<th>Select:</th>",
                "<td id='main-select-text'></td>",
                "<td>&bull;</td>",
                "<td id='main-select-icon-left'></td>",
                "<td>&bull;</td>",
                "<td id='main-select-icon-right'></td>",
                "<td>&bull;</td>",
                "<td id='main-select-disabled'></td>",
            "</tr>",
        "</table>",

        "<h3>Zimbra Controls</h3>",

        "<table border=0 cellspacing=4 cellpadding=0>",
            "<tr valign=top>",
                "<th>Toolbar:</th>",
                "<td id='main-zmtoolbar' width=100%></td>",
            "</tr>",
        "</table>"

    ].join("");

    // create dwt controls
    createLabels(main, DwtLabel, "main-label");
    createButtons(main, DwtButton, "main-button");
    createColorPicker(main, "main-color-picker");
    createToolbar(main, DwtToolBar, DwtToolBarButton, "main-toolbar");
    createTabs(main, DwtTabView, "main-tabbar");
    createSelects(main, DwtSelect, "main-select");

    // create zimbra controls
    createToolbar(main, ZmToolBar, DwtToolBarButton, "main-zmtoolbar");

    return main;
}

function createLabels(parent, labelCtor, id) {
    var labels = [
        new labelCtor(parent), "text",
        new labelCtor(parent), null, // icon only
        new labelCtor(parent), "icon-left",
        new labelCtor(parent), "icon-right",
        new labelCtor(parent), "disabled"
    ];
    labels[2].setImage("Person");
    labels[4].setImage("Person");
    labels[4].setAlign(DwtLabel.IMAGE_LEFT);
    labels[6].setImage("Person");
    labels[6].setAlign(DwtLabel.IMAGE_RIGHT);
    labels[8].setEnabled(false);
    for (var i = 0; i < labels.length; i += 2) {
        var labelComp = labels[i];
        var text = labels[i+1];
        labelComp.setText(text);
        reparent(labelComp, id+"-"+text);
    }
}

function createButtons(parent, buttonCtor, id) {
    var buttons = [
        new buttonCtor(parent), "text",
        new buttonCtor(parent), null, // icon only
        new buttonCtor(parent), "icon-left",
        new buttonCtor(parent), "icon-right",
        new buttonCtor(parent), "menu",
        new buttonCtor(parent, DwtButton.TOGGLE_STYLE), "toggle",
        new buttonCtor(parent), "disabled"
    ];
    buttons[2].setImage("Person");
    buttons[4].setImage("Person");
    buttons[4].setAlign(DwtLabel.IMAGE_LEFT);
    buttons[6].setImage("Person");
    buttons[6].setAlign(DwtLabel.IMAGE_RIGHT);
    buttons[8].setMenu(createMenu(buttons[8]));
    buttons[12].setEnabled(false);
    for (var i = 0; i < buttons.length; i += 2) {
        var button = buttons[i];
        var text = buttons[i+1];
        button.setText(text);
        reparent(button, id+"-"+text);
        button.addSelectionListener(new AjxListener(console, console.log, text));
    }                                                                         
}

function createColorPicker(parent, id) {
    var button = new DwtButtonColorPicker(parent);
    button.setImage("FontColor");
    reparent(button, id);
}

function createToolbar(parent, toolbarCtor, buttonCtor, id) {
    var toolbar = new toolbarCtor(parent);
    var buttons = [
        new buttonCtor(toolbar), "text"
    ];
    toolbar.addSeparator();
    buttons.push(
        new buttonCtor(toolbar), null // icon only
    );
    toolbar.addSeparator();
    buttons.push(
        new buttonCtor(toolbar), "icon-left",
        new buttonCtor(toolbar), "icon-right"
    );
    toolbar.addFiller();
    buttons.push(
        new buttonCtor(toolbar), "menu",
        new buttonCtor(toolbar), "disabled"
    );
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
        button.addSelectionListener(new AjxListener(console, console.log, text));
    }
    reparent(toolbar, id);
}

function createTabs(parent, tabViewCtor, id) {
    var tabView = new tabViewCtor(parent, null, DwtControl.STATIC_STYLE);
    var tabs = [
        null, "text",
        null, "icon-left",
        null, "icon-right",
        null, "disabled"
    ];
    for (var i = 0; i < tabs.length; i += 2) {
        var text = tabs[i+1];
        var page = new DwtTabViewPage(tabView, null, DwtControl.STATIC_STYLE);
        page.getHtmlElement().innerHTML = ["<div style='margin:0.5em'>",text,"</div>"].join("");
        var tabKey = tabView.addTab(text, page);
        tabs[i] = tabView.getTab(tabKey).button;
        tabs[i].addSelectionListener(new AjxListener(console, console.log, text));
    }
    tabs[2].setImage("Person");
    tabs[2].setAlign(DwtLabel.IMAGE_LEFT);
    tabs[4].setImage("Person");
    tabs[4].setAlign(DwtLabel.IMAGE_RIGHT);
    tabs[6].setEnabled(false);
    tabView.addStateChangeListener(new AjxListener(console, console.log, "tabs"));
    reparent(tabView, id);
}

function createAppTab(tabs, icon, text, isLast, selected) {
    var button = new ZmChicletButton(tabs, /*ZmAppChooser.IMAGE[ZmAppChooser.OUTER]*/null, icon, text, isLast);
    button.setSelected(selected);
//    button.setActivatedImage(ZmAppChooser.IMAGE[ZmAppChooser.OUTER_ACT]);
//    button.setTriggeredImage(ZmAppChooser.IMAGE[ZmAppChooser.OUTER_TRIG]);
    button.setToolTipContent("Go to "+text);
    button.addSelectionListener(new AjxListener(console, console.log, text));
    return button;
}

function createSelects(parent, selectCtor, id) {
    var selects = [
        new selectCtor(parent), "text",
        new selectCtor(parent), "icon-right",
        new selectCtor(parent), "icon-left",
        new selectCtor(parent), "disabled"
    ];
    selects[2].setImage("Person");
    selects[2].setAlign(DwtLabel.IMAGE_LEFT);
    selects[4].setImage("Person");
    selects[4].setAlign(DwtLabel.IMAGE_RIGHT);
    selects[6].setEnabled(false);

    var values = [ "one", "twothreefour", "five", "sixseven", "eight" ];
    for (var i = 0; i < selects.length; i += 2) {
        var select = selects[i];
        var text = selects[i+1];
        select.setText(text);
        for (var j = 0; j < values.length; j++) {
            select.addOption(values[j], null, "value"+j);
        }
        reparent(select, id+"-"+text);
    }
}

function createMenu(parent) {
    var menu = new DwtMenu(parent);
    var menuitems = [
        new DwtMenuItem(menu, DwtMenuItem.NO_STYLE), "text",
        new DwtMenuItem(menu, DwtMenuItem.NO_STYLE), "icon",
        new DwtMenuItem(menu, DwtMenuItem.NO_STYLE), "disabled",
        new DwtMenuItem(menu, DwtMenuItem.CHECK_STYLE), "check-style",
        new DwtMenuItem(menu, DwtMenuItem.CHECK_STYLE), "check-style",
        new DwtMenuItem(menu, DwtMenuItem.RADIO_STYLE, "radioId"), "radio-style"
    ];
    menuitems[2].setImage("Person");
    menuitems[4].setEnabled(false);
    menuitems[6].setChecked(true);
    menuitems[10].setChecked(true);
    menuitems.push(
        new DwtMenuItem(menu, DwtMenuItem.RADIO_STYLE, "radioId"), "radio-style",
        new DwtMenuItem(menu, DwtMenuItem.SEPARATOR_STYLE), "separator-style",
        new DwtMenuItem(menu, DwtMenuItem.CASCADE_STYLE), "cascade-style",
        new DwtMenuItem(menu, DwtMenuItem.CASCADE_STYLE), "cascade-style",
        new DwtMenuItem(menu, DwtMenuItem.CASCADE_STYLE), "cascade-disabled",
        new DwtMenuItem(menu, DwtMenuItem.PUSH_STYLE), "push-style",
        new DwtMenuItem(menu, DwtMenuItem.SELECT_STYLE), "select-style"
    );
    menuitems[16].setMenu(createSubMenu(menuitems[16], "one"));
    menuitems[18].setMenu(createSubMenu(menuitems[18], "two"));
    menuitems[20].setMenu(createSubMenu(menuitems[18], "three"));
    menuitems[20].setEnabled(false);
    for (var j = 0; j < menuitems.length; j += 2) {
        var menuitem = menuitems[j];
        var itemtext = menuitems[j+1];
        menuitem.setText(itemtext);
        menuitem.addSelectionListener(new AjxListener(console, console.log, itemtext));
    }
    return menu;
}

function createSubMenu(parent, name) {
    var menu = new DwtMenu(parent);
    var menuitems = [
        new DwtMenuItem(menu, DwtMenuItem.NO_STYLE), name,
        new DwtMenuItem(menu, DwtMenuItem.NO_STYLE), name,
        new DwtMenuItem(menu, DwtMenuItem.NO_STYLE), name
    ];
    for (var j = 0; j < menuitems.length; j += 2) {
        var menuitem = menuitems[j];
        var itemtext = menuitems[j+1];
        menuitem.setText(itemtext);
        menuitem.addSelectionListener(new AjxListener(console, console.log, itemtext));
    }
    return menu;
};
