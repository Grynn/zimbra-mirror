package com.zimbra.cs.taglib.tag.filter;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZTagLibException;
import com.zimbra.cs.zclient.ZFilterRule;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZFilterRules;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class ModifyFilterRuleTag extends ZimbraSimpleTag {

    private ZFilterRule mRule;
    private String mOriginalName;

    public void setRule(ZFilterRule rule) { mRule = rule; }
    public void setOriginalname(String originalName) { mOriginalName = originalName; }

    public void doTag() throws JspException, IOException {
        try {
            if (mOriginalName == null)
                mOriginalName = mRule.getName();

            ZMailbox mbox = getMailbox();
            ZFilterRules rules = mbox.getFilterRules(true);
            List<ZFilterRule> newRules = new ArrayList<ZFilterRule>();
            boolean origFound = false;

            for (ZFilterRule rule: rules.getRules()) {

                if (rule.getName().equalsIgnoreCase(mOriginalName)) {
                    newRules.add(mRule);
                    origFound = true;
                } else if (rule.getName().equalsIgnoreCase(mRule.getName())) {
                    throw ZTagLibException.FILTER_EXISTS("filter with name "+mRule.getName()+" already exists", null);
                } else {
                    newRules.add(rule);
                }
            }
            if (!origFound) {
                throw ZTagLibException.NO_SUCH_FILTER_EXISTS("filter with name "+mRule.getName()+" doesn't exist", null);                
            }
            mbox.saveFilterRules(new ZFilterRules(newRules));
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
