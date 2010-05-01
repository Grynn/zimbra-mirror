function Com_Zimbra_Preview () {

}

Com_Zimbra_Preview.prototype = new ZmZimletBase();
Com_Zimbra_Preview.prototype.constructor = Com_Zimbra_Preview;

Com_Zimbra_Preview.prototype.init = function () {
    appCtxt.set(ZmSetting.PREVIEW_ENABLED, true);
   // alert('ENABLED');
};