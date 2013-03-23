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
 * <p>Java class for accountBy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="accountBy">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="adminName"/>
 *     &lt;enumeration value="appAdminName"/>
 *     &lt;enumeration value="id"/>
 *     &lt;enumeration value="foreignPrincipal"/>
 *     &lt;enumeration value="name"/>
 *     &lt;enumeration value="krb5Principal"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "accountBy")
@XmlEnum
public enum testAccountBy {

    @XmlEnumValue("adminName")
    ADMIN_NAME("adminName"),
    @XmlEnumValue("appAdminName")
    APP_ADMIN_NAME("appAdminName"),
    @XmlEnumValue("id")
    ID("id"),
    @XmlEnumValue("foreignPrincipal")
    FOREIGN_PRINCIPAL("foreignPrincipal"),
    @XmlEnumValue("name")
    NAME("name"),
    @XmlEnumValue("krb5Principal")
    KRB_5_PRINCIPAL("krb5Principal");
    private final String value;

    testAccountBy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testAccountBy fromValue(String v) {
        for (testAccountBy c: testAccountBy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
