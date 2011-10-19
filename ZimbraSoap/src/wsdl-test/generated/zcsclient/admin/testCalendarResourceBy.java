
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for calendarResourceBy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="calendarResourceBy">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="id"/>
 *     &lt;enumeration value="foreignPrincipal"/>
 *     &lt;enumeration value="name"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "calendarResourceBy")
@XmlEnum
public enum testCalendarResourceBy {

    @XmlEnumValue("id")
    ID("id"),
    @XmlEnumValue("foreignPrincipal")
    FOREIGN_PRINCIPAL("foreignPrincipal"),
    @XmlEnumValue("name")
    NAME("name");
    private final String value;

    testCalendarResourceBy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testCalendarResourceBy fromValue(String v) {
        for (testCalendarResourceBy c: testCalendarResourceBy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
