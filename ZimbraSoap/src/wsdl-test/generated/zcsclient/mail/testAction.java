
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for action.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="action">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="revoke"/>
 *     &lt;enumeration value="expire"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "action")
@XmlEnum
public enum testAction {

    @XmlEnumValue("revoke")
    REVOKE("revoke"),
    @XmlEnumValue("expire")
    EXPIRE("expire");
    private final String value;

    testAction(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testAction fromValue(String v) {
        for (testAction c: testAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
