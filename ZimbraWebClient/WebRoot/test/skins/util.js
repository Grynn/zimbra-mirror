var isIE = navigator.userAgent.match("MSIE");

function $(id) { return document.getElementById(id); }

function reparent(comp, idOrElement) {
    if (!comp) return;

    if (comp instanceof DwtControl) {
        comp.reparentHtmlElement(idOrElement);
    }
    else {
        var el = typeof idOrElement == "string" ? $(idOrElement) : idOrElement;
        el.appendChild(comp);
    }
}

function showToast(s) {
    clearTimeout(window.toastId);
    var el = document.getElementById("toast");
    if (!el) {
        el = document.createElement("DIV");
        el.id = "toast";
        document.body.appendChild(el);
    }
    el.innerHTML = s;
    el.style.display = "block";
    window.toastId = setTimeout(hideToast, 3000);
}
function hideToast() {
    var el = document.getElementById("toast");
    if (el) {
        el.style.display = "none";
    }
}

if (!window.console) {
    window.console = {};
}
if (!console.log) {
    console.log = function() {
        var a = [];
        for (var i = 0; i < arguments.length; i++) {
            a.push(arguments[i]);
        }
        var s = a.join("");
        showToast(s);
    };
}
