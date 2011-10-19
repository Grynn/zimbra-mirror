
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rightType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="rightType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="preset"/>
 *     &lt;enumeration value="getAttrs"/>
 *     &lt;enumeration value="setAttrs"/>
 *     &lt;enumeration value="combo"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "rightType")
@XmlEnum
public enum testRightType {

    @XmlEnumValue("preset")
    PRESET("preset"),
    @XmlEnumValue("getAttrs")
    GET_ATTRS("getAttrs"),
    @XmlEnumValue("setAttrs")
    SET_ATTRS("setAttrs"),
    @XmlEnumValue("combo")
    COMBO("combo");
    private final String value;

    testRightType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testRightType fromValue(String v) {
        for (testRightType c: testRightType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
