function Test() {
	this.shell = new DwtShell("MainShell", false, null, null, false);

// 	this.shell._setMouseEventHdlrs();
// 	this.shell.addListener(DwtEvent.ONMOUSEMOVE, new AjxListener(this, this.func));

	var f = new ZmSpreadSheet(this.shell, null, "absolute");
	f.setModel(new ZmSpreadSheetModel(6, 6));
 	f.setBounds(20, 20, 800, 600);
// 	f.getHtmlElement().style.width = "800px";
// 	f._getTable().style.width = "1000px";
	f.setZIndex(700);
};

Test.run = function() {
	new Test();
};
