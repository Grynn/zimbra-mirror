
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tzFixupRuleMatchDate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tzFixupRuleMatchDate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="mon" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="mday" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tzFixupRuleMatchDate")
public class testTzFixupRuleMatchDate {

    @XmlAttribute(name = "mon", required = true)
    protected int mon;
    @XmlAttribute(name = "mday", required = true)
    protected int mday;

    /**
     * Gets the value of the mon property.
     * 
     */
    public int getMon() {
        return mon;
    }

    /**
     * Sets the value of the mon property.
     * 
     */
    public void setMon(int value) {
        this.mon = value;
    }

    /**
     * Gets the value of the mday property.
     * 
     */
    public int getMday() {
        return mday;
    }

    /**
     * Sets the value of the mday property.
     * 
     */
    public void setMday(int value) {
        this.mday = value;
    }

}
