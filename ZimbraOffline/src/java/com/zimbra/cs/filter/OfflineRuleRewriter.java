/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.filter;

import java.util.Iterator;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;

class OfflineRuleRewriter extends RuleRewriter {

	/**
	 * We override this to avoid folder/tag creation during filter creation time
	 * @param sb
	 * @param actionName
	 * @param element
	 * @param ruleName
	 * @throws ServiceException
	 */
	@Override
	void action(StringBuffer sb, String actionName, Element element, String ruleName) throws ServiceException {
        for (Iterator<Element> it = element.elementIterator("arg"); it.hasNext(); ) {
            Element subnode = it.next();
            String argVal = subnode.getText();
            sb.append(" \"").append(argVal).append("\"");
        }
        sb.append(";\n");
	}
}
