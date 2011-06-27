
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for smimeStoreType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="smimeStoreType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CONTACT"/>
 *     &lt;enumeration value="GAL"/>
 *     &lt;enumeration value="LDAP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "smimeStoreType", namespace = "urn:zimbra")
@XmlEnum
public enum SmimeStoreType {

    CONTACT,
    GAL,
    LDAP;

    public String value() {
        return name();
    }

    public static SmimeStoreType fromValue(String v) {
        return valueOf(v);
    }

}
