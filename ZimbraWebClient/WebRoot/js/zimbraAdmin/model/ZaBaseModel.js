
/**
 * <p>Abstract class from which <b><code>ALL</code></b> model classes inherit. Defines the
 * basic functions and provides the necessary default values.</p>
 *
 * @constructor
 * @class
 *
 * @author Mohammed Shaik Hussain Ali
 *
 * @this {ZaBaseModel}
 *
 * @param init
 *
 */
ZaBaseModel = function(init) {
    // Needed to make the class inheritable
    if (arguments.length == 0) {
        return;
    }

    this._eventManager = new AjxEventMgr();
}

ZaBaseModel.prototype.isZaBaseModel = true;

ZaBaseModel.prototype.toString = function() {
    return "ZaBaseModel";
}

ZaBaseModel.prototype.addChangeListener = function(listener) {
    return this._eventManager.addListener(ZaEvent.L_MODIFY, listener);
}

ZaBaseModel.prototype.removeChangeListener = function(listener) {
    return this._eventManager.removeListener(ZaEvent.L_MODIFY, listener);
}
