
var widgetsToRender = [],
componentData = [];

function loadExtStylesheet(src, callback){
    var xhr = new XMLHttpRequest(),
    me = this;

    xhr.open('GET', src);

    xhr.onload = function() {
        var css = xhr.responseText,
        head = document.getElementsByTagName('head')[0],
        style = document.createElement('style');

        // There's bugginess in the next gradient syntax in WebKit r84622
        // This might be fixed in a later WebKit, but for now we're going to
        // strip it out here since compass generates it.
        //
        // TODO: Upgrade to later WebKit revision
        css = css.replace(/background(-image)?: ?-webkit-linear-gradient(?:.*?);/g, '');

        style.type = 'text/css';
        style.innerText = css;

        head.appendChild(style);
        callback();
    };

    xhr.send(null);
}

function updateWidgetConfig(config) {
    var xtype = config.xtype,
    ui = config.ui,
    shortcuts = Ext.shortcuts,
    configs = (xtype) ? shortcuts[xtype] : null,
    widgets = [],
    i, tpl, filename, folder;

    if (xtype && ui && configs) {
        for (i = 0; i < configs.length; i++) {
            tpl = configs[i];

            folder = (tpl.folder) ? tpl.folder.replace('{ui}', ui) : null;
            filename = (tpl.filename) ? tpl.filename.replace('{ui}', ui) : null;

            var theUI = ui;

            var key;
            for (key in tpl.config) {
                if (typeof tpl.config[key] == 'string') {
                    tpl.config[key] = tpl.config[key].replace('{ui}', ui);
                }

                if (key == "ui") {
                    theUI = tpl.config[key].replace('{ui}', ui);
                }
            }

            var obj = {
                xtype: tpl.xtype || xtype,
                config: Ext.apply({}, {
                    ui: theUI
                }, tpl.config)
            };

            if (tpl.delegate) {
                obj.delegate = tpl.delegate;
            }

            if (tpl.offsets) {
                obj.offsets = tpl.offsets;
            }

            if (tpl.reverse) {
                obj.reverse = tpl.reverse;
            }

            if (folder) {
                obj.folder = folder;
            }
            if (filename) {
                obj.filename = filename;
            }

            widgets.push(obj);
        }
    } else {
        widgets.push(config);
    }

    return widgets;
}

function expandWidgetConfigs(){
    var widgetConfigs = Ext.manifest.widgets,
        userWidgetConfigs = Ext.userManifest.widgets,
        i;

    for(i = 0; i < userWidgetConfigs.length; i++) {
        widgetConfigs.push(userWidgetConfigs[i]);
    }

    for(i = 0; i < widgetConfigs.length; i++){
        updateWidgetConfig(widgetConfigs[i]).forEach(function(w){
            widgetsToRender.push(w)
        });
    }
}

function renderWidgets(){
    var i, config, data, widget, ct, el, compData;

    for(i = 0; i < widgetsToRender.length; i++){
        data = widgetsToRender[i];
        compData = {};
        config = Ext.apply({}, data.config, data);
        widget = Ext.create(config.xtype, config);

        ct = Ext.fly(document.body).createChild({
            tag: 'div',
            cls: 'widget-container',
            style: 'position: relative; overflow: visible;'
        });

        if (config.setup) {
            config.setup.call(widget, widget, ct);
        } else {
            widget.render(ct);
        }

        //compData.widget = widget;
        compData.offsets = Ext.apply({}, config.offsets || {}, {
            top: 0,
            bottom: 0,
            left: 0,
            right: 0
        });
        compData.config = config;

        if (config.delegate) {
            el = widget.el.down(config.delegate);
        } else {
            el = widget.el;
        }

        if (config.parentCls) {
            el.parent().addCls(config.parentCls);
        }

        compData.radius = [
            parseInt(el.getStyle('border-top-left-radius'), 10),
            parseInt(el.getStyle('border-top-right-radius'), 10),
            parseInt(el.getStyle('border-bottom-right-radius'), 10),
            parseInt(el.getStyle('border-bottom-left-radius'), 10)
        ];

        compData.gradient = (el.getStyle('background-image').indexOf('-gradient') !== -1);

        compData.gradientDirection = null;
        if(compData.gradient){
            compData.gradientDirection =
            (el.getStyle('background-image').indexOf('50% 0') !== -1 ||
             el.getStyle('background-image').indexOf('top') !== -1 ||
             el.getStyle('background-image').indexOf('bottom') !== -1)
                 ? 'top'
                 : 'left';
        }

        compData.borders = {
            top: el.getBorderWidth('t'),
            right: el.getBorderWidth('r'),
            bottom: el.getBorderWidth('b'),
            left: el.getBorderWidth('l')
        };

        compData.frameWidth = Math.max(
            compData.radius[0],
            compData.radius[1],
            compData.radius[2],
            compData.radius[3]);

        compData.radius = {
            tl: compData.radius[0],
            tr: compData.radius[1],
            br: compData.radius[2],
            bl: compData.radius[3]
        };

        compData.box = el.getBox();
        compData.reverse = data.reverse;
        compData.baseCls =
        widget.baseCls.replace(Ext.baseCSSPrefix, "");

        /**
         * this object will be captured by the screenshot utility as the
         * widget data file (typically widgetdata.json)
         */
        componentData.push(compData);
    }
}

function generateWidgets(){
    expandWidgetConfigs();
    renderWidgets();
    Ext.defer(function(){
        Ext.AllWidgetsCreated = true;
    }, 1);
}

Ext.require([
    '*'
]);

Ext.onReady(function(){
    generateWidgets();
});
