/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
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
 * <p>Java class for checkLicenseStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="checkLicenseStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ok"/>
 *     &lt;enumeration value="no"/>
 *     &lt;enumeration value="inGracePeriod"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "checkLicenseStatus")
@XmlEnum
public enum testCheckLicenseStatus {

    @XmlEnumValue("ok")
    OK("ok"),
    @XmlEnumValue("no")
    NO("no"),
    @XmlEnumValue("inGracePeriod")
    IN_GRACE_PERIOD("inGracePeriod");
    private final String value;

    testCheckLicenseStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testCheckLicenseStatus fromValue(String v) {
        for (testCheckLicenseStatus c: testCheckLicenseStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
