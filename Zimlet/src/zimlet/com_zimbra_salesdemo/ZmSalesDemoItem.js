function ZmSalesDemoItem(textToMatch) {
	this.textToMatch = textToMatch.toLowerCase();
}

ZmSalesDemoItem.prototype.setTooltipTemplateId = function(tooltipTemplateId){
	this.tooltipTemplateId = tooltipTemplateId;
};

ZmSalesDemoItem.prototype.setContextMenuItem = function(name, icon){
	if(!this.contextMenuItems) {
		this.contextMenuItems = [];
	}
	this.contextMenuItems.push({name:name, icon:icon});
};

ZmSalesDemoItem.prototype.setToolbarButton = function(name, icon){
	if(!this.toolbarButtons) {
		this.toolbarButtons = [];
	}
	this.toolbarButtons.push({name:name, icon:icon});
};


ZmSalesDemoItem.prototype.setDialogTemplateId = function(dialogTemplateId){
	this.dialogTemplateId = dialogTemplateId;
};

ZmSalesDemoItem.prototype.setToolbar = function(toolbarIcon, toolbarName, toolbarTemplateId){
	this.showToolbar = true;
	this.toolbarIcon = toolbarIcon;
	this.toolbarName = toolbarName;
	this.toolbarTemplateId = toolbarTemplateId;
};