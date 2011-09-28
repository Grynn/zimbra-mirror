/**
 * Zimlet handler class
 */
function ZmExampleEncryptZimlet() {
}

ZmExampleEncryptZimlet.OP = "ENCRYPT_EXAMPLE_ZIMLET";

ZmExampleEncryptZimlet.prototype = new ZmZimletBase();
ZmExampleEncryptZimlet.prototype.constructor = ZmExampleEncryptZimlet;


/**
 * This method gets called by the Zimlet framework when a toolbar is created.
 *
 * @param {ZmApp} app
 * @param {ZmButtonToolBar} toolbar
 * @param {ZmController} controller
 * @param {String} viewId
 *
 */
ZmExampleEncryptZimlet.prototype.initializeToolbar =
		function(app, toolbar, controller, viewId) {
			var viewType = appCtxt.getViewTypeFromId(viewId);
			if (viewType == ZmId.VIEW_COMPOSE) {
				var op = toolbar.getOp(ZmOperation.COMPOSE_OPTIONS);
				if (op) {
					var menu = op.getMenu();
					if (menu) {
						var mi = menu.getMenuItem(ZmExampleEncryptZimlet.OP);
						if (mi) {
							mi.setChecked(false);
							appCtxt.getCurrentView().__encryptZimlet_doEncrypt = false;//reset
						} else {
							mi = menu.createMenuItem(ZmExampleEncryptZimlet.OP, {image:"Padlock", text:this.getMessage("label"), style:DwtMenuItem.CHECK_STYLE});
							mi.addSelectionListener(new AjxListener(this, this._handleEncryptMenuClick, controller, mi));
						}
					}
				}
			}
		};
/**
 * Set some unique variable on the current compose view to "true" so that
 * addCustomMimeHeaders function knows that it needs to add custom header as we are dealing
 * with multiple compose-tabs.
 *
 * @param {ZmComposeController} controller
 * @param {Event}	ev
 */
ZmExampleEncryptZimlet.prototype._handleEncryptMenuClick =
		function(controller, ev) {
			if(!ev)  {
				ev = window.event;
			}
			if(ev && ev.item && ev.item.getChecked)  {
				//set some unique variable ("__encryptZimlet_doEncrypt") on ZmComposeView
				// since we need to deal with multiple compose-tabs.
				appCtxt.getCurrentView().__encryptZimlet_doEncrypt = ev.item.getChecked();
			}
		};

/**
 * Called by the framework just before sending email.
 * @param {array} customMimeHeaders An array of custom-header objects.
 * 				  Each item in the array MUST be an object that has "name" and "_content" properties.
 * 				  This onle works from 7.1.3
 */
ZmExampleEncryptZimlet.prototype.addCustomMimeHeaders =
function(customMimeHeaders) {
	//check if the compose view has __encryptZimlet_doEncrypt set to true (is true when user selects encrypt menu)
	if(appCtxt.getCurrentView().__encryptZimlet_doEncrypt) {
		customMimeHeaders.push({name:"X-Encrypt", _content:this.getConfig("X-Encrypt")});
	}
	appCtxt.getCurrentView().__encryptZimlet_doEncrypt = false;//reset
};



