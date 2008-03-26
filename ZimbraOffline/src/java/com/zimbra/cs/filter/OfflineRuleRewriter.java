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
