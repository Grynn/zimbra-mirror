
package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for distributionListSubscribeStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="distributionListSubscribeStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="subscribed"/>
 *     &lt;enumeration value="unsubscribed"/>
 *     &lt;enumeration value="awaiting_approval"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "distributionListSubscribeStatus")
@XmlEnum
public enum testDistributionListSubscribeStatus {

    @XmlEnumValue("subscribed")
    SUBSCRIBED("subscribed"),
    @XmlEnumValue("unsubscribed")
    UNSUBSCRIBED("unsubscribed"),
    @XmlEnumValue("awaiting_approval")
    AWAITING___APPROVAL("awaiting_approval");
    private final String value;

    testDistributionListSubscribeStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static testDistributionListSubscribeStatus fromValue(String v) {
        for (testDistributionListSubscribeStatus c: testDistributionListSubscribeStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
