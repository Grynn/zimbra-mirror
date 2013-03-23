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

package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for adsConnectionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="adsConnectionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="cleartext"/>
 *     &lt;enumeration value="ssl"/>
 *     &lt;enumeration value="tls"/>
 *     &lt;enumeration value="tls_is_available"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "adsConnectionType")
@XmlEnum
public enum testAdsConnectionType {

    @XmlEnumValue("cleartext")
    CLEARTEXT("cleartext"),
    @XmlEnumValue("ssl")
    SSL("ssl"),
    @XmlEnumValue("tls")
    TLS("tls"),
    @XmlEnumValue("tls_is_available")
    TLS___IS___AVAILABLE("tls_is_available");
    private final String value;

    testAdsConnectionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testAdsConnectionType fromValue(String v) {
        for (testAdsConnectionType c: testAdsConnectionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
