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
