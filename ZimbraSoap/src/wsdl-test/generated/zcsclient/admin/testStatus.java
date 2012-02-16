
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for status.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="status">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="started"/>
 *     &lt;enumeration value="running"/>
 *     &lt;enumeration value="idle"/>
 *     &lt;enumeration value="stopped"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "status")
@XmlEnum
public enum testStatus {

    @XmlEnumValue("started")
    STARTED("started"),
    @XmlEnumValue("running")
    RUNNING("running"),
    @XmlEnumValue("idle")
    IDLE("idle"),
    @XmlEnumValue("stopped")
    STOPPED("stopped");
    private final String value;

    testStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testStatus fromValue(String v) {
        for (testStatus c: testStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
