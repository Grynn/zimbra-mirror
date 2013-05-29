var page = require('webpage').create(),
    system  = require('system'),
    fs = require('fs');

/**
 *  error handler logic, updates should be below this section
 */
page.onConsoleMessage = function(msg){
  console.log(msg);
};

function handleError(err, stack) {
    console.log("== Unhandled Error ==");
    phantom.defaultErrorHandler(err, stack);
    phantom.exit(2);
}

page.onError = phantom.onError = handleError;

/* end error handler setup */

if (system.args.length < 2) {
    console.log("usage:");
    console.log("\tsencha slice capture -page <path to html file> [-image-file <image name> -widget-file <widget data file>]:");
    phantom.exit(1);
}

/**
 * args:
 * 0 => this script's file name
 * 1 => the html file to render (on windows, be mindful of '\\' chars)
 * 2 => the name of the screen shot image (default: screenshot.png)
 * 3 => the name of the widget data file (default: widgetdata.json)
 */
var url = system.args[1].replace("\\", "/"),
    screenCapFileName = ((system.args.length > 2) && system.args[2]) || "screenshot.png",
    widgetDataFile = ((system.args.length > 3) && system.args[3]) || "widgetdata.json";

console.log("loading page " + url);

function waitFor(test, ready, timeout) {
    var maxtimeOutMillis = timeout ? timeout : 30 * 1000,
        start = new Date().getTime(),
        condition = false,
        interval = setInterval(function() {
            if ((new Date().getTime() - start < maxtimeOutMillis) && !condition) {
                condition = test();
            } else {
                if (!condition) {
                    console.log('failed to render widgets within 30 sec.');
                    phantom.exit(1);
                } else {
                    clearInterval(interval);
                    ready();
                }
            }
        }, 100);
}

page.open(url, function(status){
    if (status === 'success') {
        page.evaluate(function() {
            if (document.addEventListener) {
                document.addEventListener('DOMContentLoaded', function () {
                    // This is very important for getting transparency on corners.
                    document.body.style.backgroundColor = 'transparent';
                });
            }
            document.body.style.backgroundColor = 'transparent';


            window.generateSlicerManifest = function() {
                var elements = document.body.querySelectorAll('.x-slicer-target');
                var widgets = [];
                var slicesRe = /^'x-slicer\:(.+)'$/;
                var urlRe = /url[(]([^)]+)[)]/;

                function getData (el) {
                    var data = el.getAttribute('data-slicer');
                    if (data) {
                        return JSON.parse(data);
                    }
                    return null;
                }

                function getSlices (entry, src) {
                    var content = src && src.content;
                    var slices = entry.slices;
                    if (content) {
                        var m = slicesRe.exec(content);
                        if (m && m[1]) {
                            var sliceStrings = m[1].split(', ');
                            forEach(sliceStrings, function(str){
                                // Each string looks like a url, with a schema, followed by some 'other' string, either a path or
                                // some other token
                                var colon = str.indexOf(':');
                                if (colon == -1) return;
                                var schema = str.slice(0, colon);
                                var path = str.slice(colon + 1);
                                if (schema == "stretch") {
                                    // The stretch property is used to modify other slices for this widget, store it on its own
                                    entry.stretch = path;
                                } else {
                                    // The path indicates the desired output file to create for this type of slice operation
                                    if (!!slices[schema] && 'url(' + slices[schema] + ')' != path) {
                                        err("The widget " + entry.id + " declares two " + schema + " with two different urls");
                                    }
                                    // From SASS, this path is in the form of url(path), whereas we only want to pass along the inner
                                    // part of the path
                                    var urlMatch = urlRe.exec(path);
                                    if (urlMatch && urlMatch[1]) {
                                        slices[schema.replace(/-/g,'_')] = urlMatch[1];
                                    } else {
                                        err("The widget " + entry.id + "'s " + schema + " slice's url cannot be parsed: " + path);
                                    }
                                }
                            });
                        }
                    }
                }

                function err(str) {
                    console.error(str);
                    throw str;
                }

                function forEach (it, fn) {
                    for (var i = 0; i < it.length; ++i) {
                        fn(it[i]);
                    }
                }

                function copyProps (dest, src) {
                    var out = dest || {};
                    if (!!src) {
                        for (var key in src) {
                            var val = src[key];
                            if (typeof(val) == "object") {
                                out[key] = copyProps(out[key], val);
                            } else {
                                out[key] = val;
                            }
                        }
                    }

                    return out;
                }

                forEach(elements, function (el) {
                    var view = el.ownerDocument.defaultView;
                    var style = view.getComputedStyle(el, null);
                    var bg = style['background-image'];
                    var box = el.getBoundingClientRect();

                    var entry = {
                        box: {
                            x: window.scrollX + box.left,
                            y: window.scrollY + box.top,
                            w: box.right - box.left,
                            h: box.bottom - box.top
                        },
                        radius: {
                            tl: parseInt(style['border-top-left-radius'], 10) || 0,
                            tr: parseInt(style['border-top-right-radius'], 10) || 0,
                            br: parseInt(style['border-bottom-right-radius'], 10) || 0,
                            bl: parseInt(style['border-bottom-left-radius'], 10) || 0
                        },
                        border: {
                            t: parseInt(style['border-top-width'], 10) || 0,
                            r: parseInt(style['border-right-width'], 10) || 0,
                            b: parseInt(style['border-bottom-width'], 10) || 0,
                            l: parseInt(style['border-left-width'], 10) || 0
                        }
                    };


                    if (bg.indexOf('-gradient') !== -1) {
                        if (bg.indexOf('50% 0') !== -1 || bg.indexOf('top') !== -1 ||
                                                          bg.indexOf('bottom') !== -1) {
                            entry.gradient = 'top';
                        } else {
                            entry.gradient = 'left';
                        }
                    }

                    // Reads from sass to get data
                    entry.slices = {};
                    getSlices(entry, view.getComputedStyle(el, ':after'));

                    if (!!el.id) {
                        entry.id = el.id;

                        // Merge with existing properties in global widgetSlices array, favoring widgetSlices
                        if (!!window.widgetSlices) {
                            entry = copyProps((window.widgetSlices && window.widgetSlices[el.id]), entry);
                            delete window.widgetSlices[el.id];
                        }
                    }


                    widgets.push(entry);
                });

                if (!!window.widgetSlices) {
                    for (var id in window.widgetSlices) {
//                        widgets.push(window.widgetSlices[id]);
                        console.error("Widget Slice detected without corresponding element : " + id);
                    }
                }

                var slicerManifest = window.slicerManifest = getData(document.body) || {};
                slicerManifest.widgets = widgets;
                if (!slicerManifest.format) {
                    // legacy support sets format to "1.0"
                    slicerManifest.format = '2.0';
                }
                window['widgetsReady'] = true;
                return slicerManifest;
            };

        });
        
        waitFor(function() {
            return page.evaluate(function() {
                return !!(window['widgetsReady']);
            });
        }, function() {
            try {
                console.log('Capturing screenshot');
                page.render(screenCapFileName);
                data = page.evaluate(function() {
                    if (!window["slicerManifest"]) {
                        window.generateSlicerManifest()
                    }
                    return window.slicerManifest;
                });

                if (data) {
                    console.log('Saving slicer widget manifest');
                    fs.write(widgetDataFile, JSON.stringify(data, null, '  '), 'w');
                }

                console.log('Capture complete');
                phantom.exit();
            } catch (e) {
                console.log("Error capturing page : " + e);
                phantom.exit(100);
            }
        });
    } else {
        console.log('Failed to load page');
        phantom.exit(100);
    }
});
