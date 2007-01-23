package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZTagLibException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFilterRule;
import com.zimbra.cs.zclient.ZFilterRules;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class CreateFilterRuleTag extends ZimbraSimpleTag {

    private ZFilterRule mRule;

    public void setRule(ZFilterRule rule) { mRule = rule; }

    public void doTag() throws JspException, IOException {
        try {
            ZMailbox mbox = getMailbox();
            ZFilterRules rules = mbox.getFilterRules(true);
            for (ZFilterRule rule: rules.getRules()) {
                if (rule.getName().equalsIgnoreCase(mRule.getName()))
                    throw ZTagLibException.FILTER_EXISTS("filter with name "+mRule.getName()+" already exists", null);
            }
            rules.getRules().add(mRule);
            mbox.saveFilterRules(rules);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
