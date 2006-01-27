function Test() {
	this.shell = new DwtShell("MainShell", false, null, null, false);

// 	this.shell._setMouseEventHdlrs();
// 	this.shell.addListener(DwtEvent.ONMOUSEMOVE, new AjxListener(this, this.func));

	var f1 = new ZmSpreadSheet(this.shell, null, "absolute");
	f1.setModel(new ZmSpreadSheetModel(20, 8));
	new ZmSpreadSheetToolbars(f1, f1);
 	f1.setBounds(20, 20, 800, 300);
	f1.setZIndex(Dwt.Z_VIEW);

	var f2 = new ZmSpreadSheet(this.shell, null, "absolute");
	f2.setModel(new ZmSpreadSheetModel(20, 8));
	new ZmSpreadSheetToolbars(f2, f2);
 	f2.setBounds(20, 20 + 300 + 20, 800, 300);
	f2.setZIndex(Dwt.Z_VIEW);
};

Test.run = function() {
	new Test();
};
