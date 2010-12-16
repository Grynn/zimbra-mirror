
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for serverBy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="serverBy">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="id"/>
 *     &lt;enumeration value="name"/>
 *     &lt;enumeration value="serviceHostname"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "serverBy")
@XmlEnum
public enum ServerBy {

    @XmlEnumValue("id")
    ID("id"),
    @XmlEnumValue("name")
    NAME("name"),
    @XmlEnumValue("serviceHostname")
    SERVICE_HOSTNAME("serviceHostname");
    private final String value;

    ServerBy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ServerBy fromValue(String v) {
        for (ServerBy c: ServerBy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
