function DvTabView(parent, attrs, user, app) {

    DwtTabViewPage.call(this, parent);

	this._user = user;
	this._app = app;
	this._listView = new DvListView(this, attrs, app);
}

DvTabView.prototype = new DwtTabViewPage;
DvTabView.prototype.constructor = DvTabView;

DvTabView.prototype.toString = 
function() {
	return "DvTabView";
}

DvTabView.prototype.showMe =
function() {
	DwtTabViewPage.prototype.showMe.call(this);
	this._app.setCurrentUser(this._user);
}


DvTabView.prototype.resetSize = 
function(newWidth, newHeight)  {
	DwtTabViewPage.prototype.resetSize.call(this, newWidth, newHeight);
	
	this._listView.resetHeight(newHeight);
}
