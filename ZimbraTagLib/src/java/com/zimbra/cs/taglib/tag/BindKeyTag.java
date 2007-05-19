package com.zimbra.cs.taglib.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class BindKeyTag extends ZimbraSimpleTag {

    private String mKey;
    private String mId;
    private String mFunc;

    public void setId(String id) { mId = id; }
    public void setKey(String key) { mKey = key; }
    public void setFunc(String func) { mFunc = func; }

    public void doTag() throws JspException {
        KeyBindingsTag b = (KeyBindingsTag) findAncestorWithClass(this, KeyBindingsTag.class);
        if (b == null)
                throw new JspTagException("The bindKey tag must be used within a keyBindings tag");
        if (mId != null)
            b.addIdBinding(mKey, mId);
        else if (mFunc != null)
            b.addFuncBinding(mKey, mFunc);
        else
            throw new JspTagException("The bindKey tag must have either a function or an id");

    }

}
