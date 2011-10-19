
package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for autoProvPrincipalBy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="autoProvPrincipalBy">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="dn"/>
 *     &lt;enumeration value="name"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "autoProvPrincipalBy")
@XmlEnum
public enum testAutoProvPrincipalBy {

    @XmlEnumValue("dn")
    DN("dn"),
    @XmlEnumValue("name")
    NAME("name");
    private final String value;

    testAutoProvPrincipalBy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testAutoProvPrincipalBy fromValue(String v) {
        for (testAutoProvPrincipalBy c: testAutoProvPrincipalBy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
