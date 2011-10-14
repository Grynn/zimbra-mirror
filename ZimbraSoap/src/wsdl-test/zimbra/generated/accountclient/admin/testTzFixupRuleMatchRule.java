
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tzFixupRuleMatchRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tzFixupRuleMatchRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="mon" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="week" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="wkday" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tzFixupRuleMatchRule")
public class testTzFixupRuleMatchRule {

    @XmlAttribute(name = "mon", required = true)
    protected int mon;
    @XmlAttribute(name = "week", required = true)
    protected int week;
    @XmlAttribute(name = "wkday", required = true)
    protected int wkday;

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
     * Gets the value of the week property.
     * 
     */
    public int getWeek() {
        return week;
    }

    /**
     * Sets the value of the week property.
     * 
     */
    public void setWeek(int value) {
        this.week = value;
    }

    /**
     * Gets the value of the wkday property.
     * 
     */
    public int getWkday() {
        return wkday;
    }

    /**
     * Sets the value of the wkday property.
     * 
     */
    public void setWkday(int value) {
        this.wkday = value;
    }

}
