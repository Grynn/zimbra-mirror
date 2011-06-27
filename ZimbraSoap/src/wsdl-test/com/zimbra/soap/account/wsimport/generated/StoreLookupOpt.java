
package com.zimbra.soap.account.wsimport.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for storeLookupOpt.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="storeLookupOpt">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ANY"/>
 *     &lt;enumeration value="ALL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "storeLookupOpt", namespace = "urn:zimbra")
@XmlEnum
public enum StoreLookupOpt {

    ANY,
    ALL;

    public String value() {
        return name();
    }

    public static StoreLookupOpt fromValue(String v) {
        return valueOf(v);
    }

}
