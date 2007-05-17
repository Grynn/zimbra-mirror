package com.zimbra.cs.taglib.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class BindKeyTag extends ZimbraSimpleTag {

    private String mKey;
    private String mId;

    public void setId(String id) { mId = id; }
    public void setKey(String key) { mKey = key; }

    public void doTag() throws JspException {
        KeyBindingsTag b = (KeyBindingsTag) findAncestorWithClass(this, KeyBindingsTag.class);
        if (b == null)
                throw new JspTagException("The field tag must be used within a keyBindings tag");
        b.addIdBinding(mKey, mId);
    }

}
