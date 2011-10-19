
package generated.zcsclient.zm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for targetType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="targetType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="account"/>
 *     &lt;enumeration value="calresource"/>
 *     &lt;enumeration value="cos"/>
 *     &lt;enumeration value="dl"/>
 *     &lt;enumeration value="group"/>
 *     &lt;enumeration value="domain"/>
 *     &lt;enumeration value="server"/>
 *     &lt;enumeration value="xmppcomponent"/>
 *     &lt;enumeration value="zimlet"/>
 *     &lt;enumeration value="config"/>
 *     &lt;enumeration value="global"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "targetType")
@XmlEnum
public enum testTargetType {

    @XmlEnumValue("account")
    ACCOUNT("account"),
    @XmlEnumValue("calresource")
    CALRESOURCE("calresource"),
    @XmlEnumValue("cos")
    COS("cos"),
    @XmlEnumValue("dl")
    DL("dl"),
    @XmlEnumValue("group")
    GROUP("group"),
    @XmlEnumValue("domain")
    DOMAIN("domain"),
    @XmlEnumValue("server")
    SERVER("server"),
    @XmlEnumValue("xmppcomponent")
    XMPPCOMPONENT("xmppcomponent"),
    @XmlEnumValue("zimlet")
    ZIMLET("zimlet"),
    @XmlEnumValue("config")
    CONFIG("config"),
    @XmlEnumValue("global")
    GLOBAL("global");
    private final String value;

    testTargetType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testTargetType fromValue(String v) {
        for (testTargetType c: testTargetType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
