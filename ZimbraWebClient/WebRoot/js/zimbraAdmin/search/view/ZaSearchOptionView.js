/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
 
/**
 * @class ZaSearchOptionView
 * @contructor ZaSearchBuilderOptionView
 * @author Charles Cao
 * @param optionId : the Search Option type ID
 * @param width : the width of the option view
 * @param position : the current option view's array index in the option view container
 * 
 * Class to create the advance search option picker view
 * 
 */

ZaSearchOptionView = function(parent,optionId, width, position){
	DwtComposite.call(this, parent, "ZaSearchOptionView", Dwt.ABSOLUTE_STYLE, true);		
	this._width = width || ZaSearchOptionView.WIDTH ;
	//var height = parent.getH () - 5;
	//var height = parent.getH() || ZaSearchOptionView.HEIGHT ; //parent.getH() doesn't work well in IE at the initialization time
	var height = ZaSearchOptionView.HEIGHT ;
	DBG.println(AjxDebug.DBG3, "Height of ZaSearchOptionView = " + height);
	var x = parent.getNextOptionX();
	var y = 0 ;
	this.setBounds (x, y, this._width, height);
	this._app = ZaApp.getInstance();
	this._position = position ;
	this._optionId = optionId ;
	this._controller = ZaApp.getInstance().getSearchBuilderController () ;
	//this._label = new DwtLabel (this._header, DwtLabel.IMAGE_LEFT | DwtLabel.ALIGN_LEFT);
	this._label;
	if (optionId == ZaSearchOption.OBJECT_TYPE_ID) {
		this._label = new ZaOperation (ZaOperation.LABEL, ZaMsg.searchByAddressType, null, "SearchAll", "SearchAll");
	}else if (optionId == ZaSearchOption.DOMAIN_ID) {
		this._label = new ZaOperation(ZaOperation.LABEL, ZaMsg.SearchFilter_Domains, null, "Domain", "DomainDis");
	}else if (optionId == ZaSearchOption.SERVER_ID) {
		this._label = new ZaOperation(ZaOperation.LABEL, ZaMsg.searchByServer, null, "Server", "ServerDis");
	}else if (optionId == ZaSearchOption.BASIC_TYPE_ID) {
		this._label = new ZaOperation (ZaOperation.LABEL, ZaMsg.searchByBasic, null, "SearchAll", "SearchAll");
	}else if (optionId == ZaSearchOption.ADVANCED_ID) {
		this._label = new ZaOperation (ZaOperation.LABEL, ZaMsg.searchByAdvanced, null, "SearchAll", "SearchAll") ;
	}else if (optionId == ZaSearchOption.COS_ID) {
                this._label = new ZaOperation (ZaOperation.LABEL, ZaMsg.searchByCOS, null, "COS", "COS");
        }
	
	var tb_items = [];
	tb_items.push(this._label);	
	tb_items.push(new ZaOperation(ZaOperation.NONE));
	tb_items.push(new ZaOperation(ZaOperation.CLOSE, null, ZaMsg.ALTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener)));    	
	
	this._header = new ZaToolBar (this, tb_items, null, null, "DwtToolBar") ;
	//this._header.setSize (this._width, 25); //set the width, will lost the right border with the proper color.
	this._header.setSize (this._width, ZaSearchOptionView.HEADER_HEIGHT);
	this.initForm (ZaSearchOption.getObjectTypeXModel(optionId), ZaSearchOption.getObjectTypeXForm (optionId, height), ZaSearchOption.getDefaultInstance(optionId));
}

ZaSearchOptionView.prototype = new DwtComposite ;
ZaSearchOptionView.prototype.constructor = ZaSearchOptionView ;

ZaSearchOptionView.HEADER_HEIGHT = 28 ;
ZaSearchOptionView.WIDTH = 200 ;
ZaSearchOptionView.BASIC_OPTION_WIDTH = 200 ;
ZaSearchOptionView.ADVANCED_OPTION_WIDTH = 320 ;
ZaSearchOptionView.MARGIN = 0 ;
ZaSearchOptionView.HEIGHT = 225 ;

ZaSearchOptionView.prototype.closeButtonListener = 
function (event){
	DBG.println (AjxDebug.DBG3, "Close options ... ");	
	//splice from option view array, update the query and dispose the component, 
	this._controller.removeOptionView(this._position, true) ;
}

ZaSearchOptionView.prototype.setPosition = 
function (p) {
	this._position = p ;	
}

ZaSearchOptionView.prototype.getPosition = 
function () {
	return this._position ;	
}

ZaSearchOptionView.prototype.initForm = 
function (xModelMetaData, xFormMetaData, xFormInstance) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "ZaSearchOptionView.prototype.initForm");

	this._localXModel = new XModel(xModelMetaData);
	//
	this._localXForm = new XForm(xFormMetaData, this._localXModel, xFormInstance , this);
	this._localXForm.setController(ZaApp.getInstance());
	this._localXForm.draw();
	this._drawn = true;
}
