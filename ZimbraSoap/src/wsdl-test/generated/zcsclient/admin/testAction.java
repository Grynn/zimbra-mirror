
package generated.zcsclient.admin;

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
 *     &lt;enumeration value="start"/>
 *     &lt;enumeration value="status"/>
 *     &lt;enumeration value="stop"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "action")
@XmlEnum
public enum testAction {

    @XmlEnumValue("start")
    START("start"),
    @XmlEnumValue("status")
    STATUS("status"),
    @XmlEnumValue("stop")
    STOP("stop");
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
