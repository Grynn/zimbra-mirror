package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class CustomField extends SimpleField {
    private String title;

    private static final String TITLE = "title";
    
    public static CustomField custom(String title, String value) {
        return new CustomField(title, value);
    }

    public CustomField() {
        super(CUSTOM);
    }
    
    public CustomField(String title, String value) {
        super(CUSTOM, value);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public Element toXml(Document doc, String tag) {
        Element e = super.toXml(doc, tag);
        e.setAttribute(TITLE, title);
        return e;
    }

    @Override
    protected void parseXml(Element e) {
        super.parseXml(e);
        title = e.getAttribute(TITLE);
    }
}
