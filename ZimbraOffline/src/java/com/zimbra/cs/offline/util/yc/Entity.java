/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.util.yc;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.zimbra.cs.offline.util.Xml;

public abstract class Entity {
    
    public Element toXml(Document doc) {
        throw new UnsupportedOperationException();
    }

    public void extractFromXml(Element e) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        Element e = toXml(Xml.newDocument());
        return e != null ? Xml.toString(e) : super.toString();
    }
}
