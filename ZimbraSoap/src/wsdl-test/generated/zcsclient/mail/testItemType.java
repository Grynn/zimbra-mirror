/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for itemType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="itemType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="appointment"/>
 *     &lt;enumeration value="chat"/>
 *     &lt;enumeration value="contact"/>
 *     &lt;enumeration value="conversation"/>
 *     &lt;enumeration value="document"/>
 *     &lt;enumeration value="message"/>
 *     &lt;enumeration value="tag"/>
 *     &lt;enumeration value="task"/>
 *     &lt;enumeration value="wiki"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "itemType")
@XmlEnum
public enum testItemType {

    @XmlEnumValue("appointment")
    APPOINTMENT("appointment"),
    @XmlEnumValue("chat")
    CHAT("chat"),
    @XmlEnumValue("contact")
    CONTACT("contact"),
    @XmlEnumValue("conversation")
    CONVERSATION("conversation"),
    @XmlEnumValue("document")
    DOCUMENT("document"),
    @XmlEnumValue("message")
    MESSAGE("message"),
    @XmlEnumValue("tag")
    TAG("tag"),
    @XmlEnumValue("task")
    TASK("task"),
    @XmlEnumValue("wiki")
    WIKI("wiki");
    private final String value;

    testItemType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testItemType fromValue(String v) {
        for (testItemType c: testItemType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
