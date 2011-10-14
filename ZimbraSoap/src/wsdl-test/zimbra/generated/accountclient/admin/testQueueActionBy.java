
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for queueActionBy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="queueActionBy">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="id"/>
 *     &lt;enumeration value="query"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "queueActionBy")
@XmlEnum
public enum testQueueActionBy {

    @XmlEnumValue("id")
    ID("id"),
    @XmlEnumValue("query")
    QUERY("query");
    private final String value;

    testQueueActionBy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testQueueActionBy fromValue(String v) {
        for (testQueueActionBy c: testQueueActionBy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
