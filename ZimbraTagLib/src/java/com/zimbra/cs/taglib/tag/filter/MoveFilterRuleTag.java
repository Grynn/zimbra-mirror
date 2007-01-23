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
import java.util.List;

public class MoveFilterRuleTag extends ZimbraSimpleTag {

    private String mName;
    private boolean mUp;

    public void setName(String name) { mName = name; }
    public void setDirection(String direction) throws JspTagException {
        boolean up = direction.equalsIgnoreCase("up");
        boolean down = direction.equalsIgnoreCase("down");
        if (!(up || down))
            throw new JspTagException("moveFilterRule direction must be up or down", null);
        mUp = up;
    }

    public void doTag() throws JspException, IOException {
        try {
            ZMailbox mbox = getMailbox();
            ZFilterRules zrules = mbox.getFilterRules(true);
            List<ZFilterRule> rules = zrules.getRules();
            int index = -1;
            for (int i=0; i < rules.size(); i++) {
                if (rules.get(i).getName().equalsIgnoreCase(mName)) {
                    index = i;
                    break;
                }
            }
            if (index == -1)
                throw ZTagLibException.NO_SUCH_FILTER_EXISTS("filter with name "+mName+" doesn't exist", null);

            if ((index == 0 && mUp) || (index == rules.size()-1 && !mUp))
                return;
            
            ZFilterRule rule = rules.get(index);
            rules.remove(index);
            if (mUp) {
                rules.add(index - 1, rule);
            } else {
                index++;
                if (index > rules.size())
                    rules.add(rule);
                else
                    rules.add(index, rule);
            }
            mbox.saveFilterRules(new ZFilterRules(rules));
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
