
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dataSourceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="dataSourceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="pop3"/>
 *     &lt;enumeration value="imap"/>
 *     &lt;enumeration value="caldav"/>
 *     &lt;enumeration value="contacts"/>
 *     &lt;enumeration value="yab"/>
 *     &lt;enumeration value="rss"/>
 *     &lt;enumeration value="cal"/>
 *     &lt;enumeration value="gal"/>
 *     &lt;enumeration value="xsync"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "dataSourceType")
@XmlEnum
public enum DataSourceType {

    @XmlEnumValue("pop3")
    POP_3("pop3"),
    @XmlEnumValue("imap")
    IMAP("imap"),
    @XmlEnumValue("caldav")
    CALDAV("caldav"),
    @XmlEnumValue("contacts")
    CONTACTS("contacts"),
    @XmlEnumValue("yab")
    YAB("yab"),
    @XmlEnumValue("rss")
    RSS("rss"),
    @XmlEnumValue("cal")
    CAL("cal"),
    @XmlEnumValue("gal")
    GAL("gal"),
    @XmlEnumValue("xsync")
    XSYNC("xsync");
    private final String value;

    DataSourceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataSourceType fromValue(String v) {
        for (DataSourceType c: DataSourceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
