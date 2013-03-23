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

package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for licenseStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="licenseStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NOT_INSTALLED"/>
 *     &lt;enumeration value="NOT_ACTIVATED"/>
 *     &lt;enumeration value="IN_FUTURE"/>
 *     &lt;enumeration value="EXPIRED"/>
 *     &lt;enumeration value="INVALID"/>
 *     &lt;enumeration value="LICENSE_GRACE_PERIOD"/>
 *     &lt;enumeration value="ACTIVATION_GRACE_PERIOD"/>
 *     &lt;enumeration value="OK"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "licenseStatus")
@XmlEnum
public enum testLicenseStatus {

    @XmlEnumValue("NOT_INSTALLED")
    NOT___INSTALLED("NOT_INSTALLED"),
    @XmlEnumValue("NOT_ACTIVATED")
    NOT___ACTIVATED("NOT_ACTIVATED"),
    @XmlEnumValue("IN_FUTURE")
    IN___FUTURE("IN_FUTURE"),
    EXPIRED("EXPIRED"),
    INVALID("INVALID"),
    @XmlEnumValue("LICENSE_GRACE_PERIOD")
    LICENSE___GRACE___PERIOD("LICENSE_GRACE_PERIOD"),
    @XmlEnumValue("ACTIVATION_GRACE_PERIOD")
    ACTIVATION___GRACE___PERIOD("ACTIVATION_GRACE_PERIOD"),
    OK("OK");
    private final String value;

    testLicenseStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testLicenseStatus fromValue(String v) {
        for (testLicenseStatus c: testLicenseStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
