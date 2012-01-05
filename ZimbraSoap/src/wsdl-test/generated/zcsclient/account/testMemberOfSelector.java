
package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for memberOfSelector.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="memberOfSelector">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="all"/>
 *     &lt;enumeration value="none"/>
 *     &lt;enumeration value="directOnly"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "memberOfSelector")
@XmlEnum
public enum testMemberOfSelector {

    @XmlEnumValue("all")
    ALL("all"),
    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("directOnly")
    DIRECT_ONLY("directOnly");
    private final String value;

    testMemberOfSelector(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testMemberOfSelector fromValue(String v) {
        for (testMemberOfSelector c: testMemberOfSelector.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
