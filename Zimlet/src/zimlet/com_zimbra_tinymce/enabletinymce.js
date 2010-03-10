function Com_Zimbra_EnableTinyMCE () {
    
}

Com_Zimbra_EnableTinyMCE.prototype = new ZmZimletBase();
Com_Zimbra_EnableTinyMCE.prototype.constructor = Com_Zimbra_DnDExt;

Com_Zimbra_EnableTinyMCE.prototype.init = function () {
    window.isTinyMCE = true;

    var cmd = window.newWindowCommand;
    if(cmd == 'compose') {
      setTimeout(function() {
                isTinyMCE      = true;
            }, 1000);
    }

};