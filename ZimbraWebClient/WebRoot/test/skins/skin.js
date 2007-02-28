// skin data
var skin = {};

var skinStylesUrl = "../../css/imgs,common,dwt,msgview,login,zm,spellcheck,wiki,@SKIN@_imgs,skin.css?skin=@SKIN@&debug=true";
var skinSourceUrl = "../../js/skin.js?skin=@SKIN@&debug=true";
var skinHtmlUrl = "../../html/skin.html?skin=@SKIN@&debug=true";

// components data
var components = {
    logo: null,
    username: null,
    quota: null,

    search: null,
    searchBuilderToolbar: null,
    searchBuilder: null,

    app_chooser: null,
    help_button: null,
    logout_button: null,

    views: null,
    topToolbar: null,

    tree: null,
    treeFooter: null,
    status: null
};
var containers = {
    logo: "skin_container_logo",
    username: "skin_container_username",
    quota: "skin_container_quota",

    search: "skin_container_search",
    searchBuilderToolbar: "skin_container_search_builder_toolbar",
    searchBuilder: "skin_container_search_builder",

    app_chooser: "skin_container_app_chooser",
    help_button: "skin_container_help",
    logout_button: "skin_container_logoff",

    views: "skin_container_current_app",
    topToolbar: "skin_container_app_top_toolbar",

    tree: "skin_container_tree",
    treeFooter: "skin_container_tree_footer",
    status: "skin_container_status"
};

// skin loading functions
function loadSkin(skin) {
  // clear content
  var htmlEl = $("skin-body");
  htmlEl.innerHTML = "";

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

// skin layout
function populateSkin() {
  var shell = DwtShell.getShell(window);

  // create components
  components.logo = createLogo(shell);
  components.username = createUserName(shell);
  components.quota = createQuota(shell);

  components.search = createSearch(shell);
  components.searchBuilderToolbar = createSearchBuilderToolbar(shell);
  components.searchBuilder = createSearchBuilder(shell);

  components.app_chooser = createAppChooser(shell);
  components.help_button = createHelp(shell);
  components.logout_button = createLogoff(shell);

  components.views = createViewToolBar(shell);
  components.topToolbar = createAppToolBar(shell);

  components.tree = createOverviewTree(shell);
  components.treeFooter = createTreeFooter(shell);
  components.status = createStatus(shell);

  // position components
  layoutSkin();

  // show intitial view
  skin.show("skin");
  skin.hide("fullScreen");
}

function layoutSkin() {
//    console.log("-- layoutSkin");
    for (var name in components) {
        var component = components[name];
        var container = containers[name];
//        console.log("component: ",component,", container: ",container," (",$(container),")");
        if (!component || !container) continue;

        var position = (skin.hints[name] && skin.hints[name].position) || "static"; // "absolute"
        if (position == "absolute") {
            /***
            var b = Dwt.getBounds($(container));
            /***/
            var css = DwtCssStyle.getComputedStyleObject($(container));
            var b = { x: css.left, y: css.top, width: css.width, height: css.height };
            /***/
//            console.log("bounds", b);
            component.setBounds(b.x, b.y, b.width, b.height);
        }
        else {
            /***
            var el = $(container);
            var b = { x: 0, y: 0, width: el.clientWidth, height: el.clientHeight }; 
            console.log("bounds", b);
            /***/
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
