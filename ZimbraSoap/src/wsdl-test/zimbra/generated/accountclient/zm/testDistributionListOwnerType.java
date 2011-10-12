
package zimbra.generated.accountclient.zm;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for distributionListOwnerType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="distributionListOwnerType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="usr"/>
 *     &lt;enumeration value="grp"/>
 *     &lt;enumeration value="all"/>
 *     &lt;enumeration value="dom"/>
 *     &lt;enumeration value="gst"/>
 *     &lt;enumeration value="key"/>
 *     &lt;enumeration value="pub"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "distributionListOwnerType")
@XmlEnum
public enum testDistributionListOwnerType {

    @XmlEnumValue("usr")
    USR("usr"),
    @XmlEnumValue("grp")
    GRP("grp"),
    @XmlEnumValue("all")
    ALL("all"),
    @XmlEnumValue("dom")
    DOM("dom"),
    @XmlEnumValue("gst")
    GST("gst"),
    @XmlEnumValue("key")
    KEY("key"),
    @XmlEnumValue("pub")
    PUB("pub");
    private final String value;

    testDistributionListOwnerType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testDistributionListOwnerType fromValue(String v) {
        for (testDistributionListOwnerType c: testDistributionListOwnerType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
