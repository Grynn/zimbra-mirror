
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for countObjectsType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="countObjectsType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="userAccount"/>
 *     &lt;enumeration value="account"/>
 *     &lt;enumeration value="alias"/>
 *     &lt;enumeration value="dl"/>
 *     &lt;enumeration value="domain"/>
 *     &lt;enumeration value="cos"/>
 *     &lt;enumeration value="server"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "countObjectsType")
@XmlEnum
public enum testCountObjectsType {

    @XmlEnumValue("userAccount")
    USER_ACCOUNT("userAccount"),
    @XmlEnumValue("account")
    ACCOUNT("account"),
    @XmlEnumValue("alias")
    ALIAS("alias"),
    @XmlEnumValue("dl")
    DL("dl"),
    @XmlEnumValue("domain")
    DOMAIN("domain"),
    @XmlEnumValue("cos")
    COS("cos"),
    @XmlEnumValue("server")
    SERVER("server");
    private final String value;

    testCountObjectsType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testCountObjectsType fromValue(String v) {
        for (testCountObjectsType c: testCountObjectsType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
