
package com.zimbra.soap.account.wsimport.generated;

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
@XmlType(name = "accountBy", namespace = "urn:zimbra")
@XmlEnum
public enum AccountBy {

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

    AccountBy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccountBy fromValue(String v) {
        for (AccountBy c: AccountBy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
