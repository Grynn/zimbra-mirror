
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for galMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="galMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="both"/>
 *     &lt;enumeration value="ldap"/>
 *     &lt;enumeration value="zimbra"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "galMode")
@XmlEnum
public enum testGalMode {

    @XmlEnumValue("both")
    BOTH("both"),
    @XmlEnumValue("ldap")
    LDAP("ldap"),
    @XmlEnumValue("zimbra")
    ZIMBRA("zimbra");
    private final String value;

    testGalMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testGalMode fromValue(String v) {
        for (testGalMode c: testGalMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
