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

package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for connectionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="connectionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="cleartext"/>
 *     &lt;enumeration value="ssl"/>
 *     &lt;enumeration value="tls"/>
 *     &lt;enumeration value="tls_if_available"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "connectionType")
@XmlEnum
public enum testConnectionType {

    @XmlEnumValue("cleartext")
    CLEARTEXT("cleartext"),
    @XmlEnumValue("ssl")
    SSL("ssl"),
    @XmlEnumValue("tls")
    TLS("tls"),
    @XmlEnumValue("tls_if_available")
    TLS___IF___AVAILABLE("tls_if_available");
    private final String value;

    testConnectionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testConnectionType fromValue(String v) {
        for (testConnectionType c: testConnectionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
