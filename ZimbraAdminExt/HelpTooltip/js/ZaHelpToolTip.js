/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 5/11/11
 * Time: 11:36 PM
 * To change this template use File | Settings | File Templates.
 */


ZaHelpTooltip = function() {

}

ZaHelpTooltip.prototype = new ZaItem();
ZaHelpTooltip.prototype.constructor = ZaHelpTooltip;

ZaHelpTooltip.A_description = "description";

ZaHelpTooltip.descriptionCache = {};
ZaHelpTooltip.cacheNumber = 0;
ZaHelpTooltip.getDescByName = function(name) {
    if(ZaHelpTooltip.descriptionCache[name]){
        return ZaHelpTooltip.descriptionCache[name];
    }
}