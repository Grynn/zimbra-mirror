/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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

package com.zimbra.doc.soap;

import com.zimbra.soap.DocumentService;
import com.zimbra.doc.soap.doclet.*;
import java.util.*;

/**
 * This class represents the root data model for the SOAP API.
 * 
 * @author sposetti
 *
 */
public	abstract	class	DataModelProvider {

	/**
	 * Gets the data model.
	 * 
	 * @return		the data model
	 */
	public	Root	getRoot() {
		Root root = new Root();
		
		return	loadDataModel(root);
	}
	
	/**
	 * Loads the data model.
	 * 
	 * @param		root		the root data model
	 * @return		the loaded data model
	 */
	protected		abstract		Root		loadDataModel(Root root);
	
}