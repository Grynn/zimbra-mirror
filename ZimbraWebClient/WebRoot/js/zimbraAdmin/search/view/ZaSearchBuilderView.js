function ZaSearchBuilderView (parent, app){
	DwtComposite.call(this, parent, "ZaSearchBuilderView", Dwt.ABSOLUTE_STYLE, true);		
	var visible = false ;
	this.zShow(visible);
	this._app = app;
	
	this._option_next_x = 0;
	this._controller = this._app.getSearchBuilderController () ;
	this.setScrollStyle(DwtControl.SCROLL);
}

ZaSearchBuilderView.prototype = new DwtComposite();
ZaSearchBuilderView.prototype.constructor = ZaSearchBuilderView;

ZaSearchBuilderView.prototype.toString = 
function() {
	return "ZaSearchBuilderView";
}

ZaSearchBuilderView.prototype.getNextOptionX = 
function (position) {
	if (position == null) {
		position = this._controller._option_views.length ;
	}
	if ( position <= 0 ){
		return ZaSearchOptionView.MARGIN; 
	}else{
		var prevOption = this._controller._option_views[position -1];
		return prevOption.getX () + prevOption.getW () + ZaSearchOptionView.MARGIN;
	}
}