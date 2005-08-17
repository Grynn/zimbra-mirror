function NewImageTest(parent) {
}

NewImageTest.I_FLAG_ON  = ["FlagOnIcon", 16, 16];
NewImageTest.I_FLAG_OFF  = ["FlagOffIcon", 16, 16];

NewImageTest.run =
function() {
	var shell = new DwtShell();
	//shell.getHtmlElement().innerHTML = AjxImg.getImageHtml(NewImageTest.I_FLAG_ON);
	var div = document.createElement("div");
	AjxImg.setImage(div, NewImageTest.I_FLAG_ON);
	shell.getHtmlElement().appendChild(div);
}