
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for calendarAttendeeWithGroupInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="calendarAttendeeWithGroupInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}calendarAttendee">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="isGroup" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="exp" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "calendarAttendeeWithGroupInfo")
public class testCalendarAttendeeWithGroupInfo
    extends testCalendarAttendee
{

    @XmlAttribute(name = "isGroup")
    protected Boolean isGroup;
    @XmlAttribute(name = "exp")
    protected Boolean exp;

    /**
     * Gets the value of the isGroup property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsGroup() {
        return isGroup;
    }

    /**
     * Sets the value of the isGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsGroup(Boolean value) {
        this.isGroup = value;
    }

    /**
     * Gets the value of the exp property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExp() {
        return exp;
    }

    /**
     * Sets the value of the exp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExp(Boolean value) {
        this.exp = value;
    }

}
