
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for zimletStatusSetting.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="zimletStatusSetting">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="enabled"/>
 *     &lt;enumeration value="disabled"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "zimletStatusSetting")
@XmlEnum
public enum testZimletStatusSetting {

    @XmlEnumValue("enabled")
    ENABLED("enabled"),
    @XmlEnumValue("disabled")
    DISABLED("disabled");
    private final String value;

    testZimletStatusSetting(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testZimletStatusSetting fromValue(String v) {
        for (testZimletStatusSetting c: testZimletStatusSetting.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
