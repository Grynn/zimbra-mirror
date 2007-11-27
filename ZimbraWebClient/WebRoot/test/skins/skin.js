// skin data
var skin = {};

var skinStylesUrl = "../../css/imgs,common,dwt,msgview,login,zm,spellcheck,wiki,@SKIN@_imgs,skin.css?skin=@SKIN@&debug=true&v=@VERSION@";
var skinSourceUrl = "../../js/skin.js?skin=@SKIN@&debug=true&v=@VERSION@";
var skinHtmlUrl = "../../html/skin.html?skin=@SKIN@&debug=true&v=@VERSION@";

// components data
var components = {
    logo: null,
    username: null,
    quota: null,

    search: null,
    searchBuilderToolbar: null,
    searchBuilder: null,

    appChooser: null,
    helpButton: null,
    logoutButton: null,

    views: null,
    topToolbar: null,

    tree: null,
    treeFooter: null,
    status: null,

    main: null
};
var containers = {
    logo: "skin_container_logo",
    username: "skin_container_username",
    quota: "skin_container_quota",

    search: "skin_container_search",
    searchBuilderToolbar: "skin_container_search_builder_toolbar",
    searchBuilder: "skin_container_search_builder",

    appChooser: "skin_container_app_chooser",
    helpButton: "skin_container_help",
    logoutButton: "skin_container_logoff",

    views: "skin_container_current_app",
    topToolbar: "skin_container_app_top_toolbar",

    tree: "skin_container_tree",
    treeFooter: "skin_container_tree_footer",
    status: "skin_container_status",

    main: "skin_container_app_main"
};

// skin loading functions
function loadSkin(skin) {
    // clear content
    var htmlEl = $("skin-body");
    htmlEl.innerHTML = "";

    for (var name in components) {
        var component = components[name];
        if (component) {
            component.dispose();
        }
    }

    // reload styles
    var stylesEl = $("skin-styles");
    if (isIE) {
        var handler = AjxCallback.simpleClosure(skinStylesLoadedIE, null, skin, stylesEl);
        stylesEl.attachEvent("onreadystatechange", handler);
    }
    else {
        stylesEl.onload = AjxCallback.simpleClosure(skinStylesLoaded, null, skin);
    }
    stylesEl.href = skinStylesUrl.replace(/@SKIN@/g, skin).replace(/@VERSION@/g, new Date().getTime());

    // reset packages and templates
    AjxPackage._packages = {};
    AjxTemplate._templates = {};

    // remove old source element, if present
    var sourceEl = $("skin-source");
    if (sourceEl) sourceEl.parentNode.removeChild(sourceEl);

    // load sources
    var sourceEl = document.createElement("SCRIPT");
    sourceEl.id = "skin-source";
    if (isIE) {
        var handler = AjxCallback.simpleClosure(skinSourceLoadedIE, null, skin, sourceEl);
        sourceEl.attachEvent("onreadystatechange", handler);
    }
    else {
        sourceEl.onload = AjxCallback.simpleClosure(skinSourceLoaded, null, skin);
    }
    sourceEl.src = skinSourceUrl.replace(/@SKIN@/g, skin).replace(/@VERSION@/g, new Date().getTime());
    document.getElementsByTagName("HEAD")[0].appendChild(sourceEl);
}

function skinStylesLoadedIE(skin, script) {
    if (script.readyState.match(/loaded|complete/)) {
        skinStylesLoaded(skin);
    }
}

function skinStylesLoaded(skin) {
//    alert("styles loaded - "+skin);
}

function skinSourceLoadedIE(skin, script) {
    if (script.readyState.match(/loaded|complete/)) {
        skinSourceLoaded(skin);
    }
}

function skinSourceLoaded(skin) {
//    alert("source loaded - "+skin);
    var htmlUrl = skinHtmlUrl.replace(/@SKIN@/g, skin).replace(/@VERSION@/g, new Date().getTime());
    var callback = new AjxCallback(null, skinHtmlLoaded, [skin]);
    AjxRpc.invoke("", htmlUrl, null, callback, true);
}

function skinHtmlLoaded(skin, result) {
//    alert("html loaded - "+skin);
    var htmlEl = $("skin-body");
    htmlEl.innerHTML = result.text;
    populateSkin();
}

// skin layout
function populateSkin() {
    var shell = DwtShell.getShell(window);

    // create components
//    components.logo = createLogo(shell);
//    components.username = createUserName(shell);
//    components.quota = createQuota(shell);
//
//    components.search = createSearch(shell);
//    components.searchBuilderToolbar = createSearchBuilderToolbar(shell);
//    components.searchBuilder = createSearchBuilder(shell);
//
    components.appChooser = createAppChooser(shell);
//    components.helpButton = createHelp(shell);
//    components.logoutButton = createLogoff(shell);
//
    components.views = createViewToolBar(shell);
//    components.topToolbar = createAppToolBar(shell);
//
//    components.tree = createOverviewTree(shell);
//    components.treeFooter = createTreeFooter(shell);
//    components.status = createStatus(shell);

    components.main = createMain(shell);

    // position components
    layoutSkin();

    // show intitial view
    skin.show("skin");
    skin.hide("fullScreen");
}

function layoutSkin() {
    for (var name in components) {
        var component = components[name];
        var container = containers[name];
        if (!component || !container) continue;

        var position = (skin.hints[name] && skin.hints[name].position) || "static"; // "absolute"
        if (position == "absolute") {
            /***
            var b = Dwt.getBounds($(container));
            /***/
            var css = DwtCssStyle.getComputedStyleObject($(container));
            var b = { x: css.left, y: css.top, width: css.width, height: css.height };
            /***/
            component.setBounds(b.x, b.y, b.width, b.height);
        }
        else {
            reparent(component, container);
        }
    }
}

// skin selection
function handleSkinSelected(evt) {
    var skin = evt.item.getData("value");
    $("skin-item-"+skin).selected = true;
    loadSkin(skin);
}

function skinSelected(selectEl) {
    var skin = selectEl.options[selectEl.selectedIndex].value;
    loadSkin(skin);
}
