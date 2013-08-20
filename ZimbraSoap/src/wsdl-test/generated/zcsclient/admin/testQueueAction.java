/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for queueAction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="queueAction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="hold"/>
 *     &lt;enumeration value="release"/>
 *     &lt;enumeration value="delete"/>
 *     &lt;enumeration value="requeue"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "queueAction")
@XmlEnum
public enum testQueueAction {

    @XmlEnumValue("hold")
    HOLD("hold"),
    @XmlEnumValue("release")
    RELEASE("release"),
    @XmlEnumValue("delete")
    DELETE("delete"),
    @XmlEnumValue("requeue")
    REQUEUE("requeue");
    private final String value;

    testQueueAction(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testQueueAction fromValue(String v) {
        for (testQueueAction c: testQueueAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
