
ZaClientUploadController = function(appCtxt, container) {
    ZaXFormViewController.call(this, appCtxt, container, "ZaClientUploadController");
    this._UICreated = false;
    this.tabConstructor = ZaClientUploadXFormView;
}

ZaClientUploadController.prototype = new ZaXFormViewController();
ZaClientUploadController.prototype.constructor = ZaClientUploadController;

ZaController.setViewMethods["ZaClientUploadController"] = [];

ZaClientUploadController.setViewMethod = function (item) {
    if(!this._UICreated) {
        this._contentView = this._view = new this.tabConstructor(this._container,item);
        var elements = new Object();
        elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
        ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
        this._UICreated = true;
        ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
    }
    ZaApp.getInstance().pushView(this.getContentViewId());

    //item.load();
    try {
        //item[ZaModel.currentTab] = "1"
        this._view.setDirty(false);
        this._view.setObject(item);
    } catch (ex) {
        this._handleException(ex, "ZaClientUploadController.prototype.show", null, false);
    }
    this._currentObject = item;
}
ZaController.setViewMethods["ZaClientUploadController"].push(ZaClientUploadController.setViewMethod) ;
