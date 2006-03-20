window.ACE_COMPONENT_NAME = "ZmSpreadSheet";

function Test() {
	var sh = this.shell = new DwtShell("MainShell", false, null, null, false);

 	var f1 = new ZmSpreadSheet(sh, null, "absolute");
	var model = new ZmSpreadSheetModel(10, 6);
//  	var model = new ZmSpreadSheetModel(0, 0);
//  	model.deserialize(document.getElementById("testdata").value);
	f1.setModel(model);
	new ZmSpreadSheetToolbars(f1, f1);
	f1.setZIndex(Dwt.Z_VIEW);

	// sh.addControlListener(new AjxListener(this, this._resize));
	window.onresize = ZmSpreadSheet.simpleClosure(this._resize, this);

	this.spreadSheet = f1;
	this.dataModel = model;

	this._resize();

// 	var f2 = new ZmSpreadSheet(sh, null, "absolute");
// 	f2.setModel(new ZmSpreadSheetModel(20, 8));
// 	new ZmSpreadSheetToolbars(f2, f2);
//  	f2.setBounds(20, 20 + 300 + 20, 800, 300);
// 	f2.setZIndex(Dwt.Z_VIEW);
};

Test.run = function() {
	window.ACE_SpreadSheet = new Test();
};

Test.prototype._resize = function(ev) {
	this.spreadSheet.setDisplay("none");
	var w = document.body.clientWidth;
	var h = document.body.clientHeight;
	if (!AjxEnv.isIE) {
		w -= 2;
		h -= 2;
	}
	this.spreadSheet.setDisplay("block");
	this.spreadSheet.setBounds(0, 0, w, h);
};

window.serialize = function() {
	return window.ACE_SpreadSheet.dataModel.serialize();
};

window.deserialize = function(data) {
	if (data)
		window._origData = data;
	else
		data = window._origData;
	if (!window.ACE_SpreadSheet)
		setTimeout(window.deserialize, 100);
	else {
		var model = new ZmSpreadSheetModel(0, 0);
		model.deserialize(data);
		window.ACE_SpreadSheet.dataModel = model;
		window.ACE_SpreadSheet.spreadSheet.setModel(model);
	}
};

window.getHTML = function() {
	return window.ACE_SpreadSheet.dataModel.getHtml();
};
