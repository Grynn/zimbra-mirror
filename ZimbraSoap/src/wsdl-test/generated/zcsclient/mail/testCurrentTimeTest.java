
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for currentTimeTest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="currentTimeTest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}filterTest">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="dateComparison" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="time" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "currentTimeTest")
public class testCurrentTimeTest
    extends testFilterTest
{

    @XmlAttribute(name = "dateComparison")
    protected String dateComparison;
    @XmlAttribute(name = "time")
    protected String time;

    /**
     * Gets the value of the dateComparison property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateComparison() {
        return dateComparison;
    }

    /**
     * Sets the value of the dateComparison property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateComparison(String value) {
        this.dateComparison = value;
    }

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTime(String value) {
        this.time = value;
    }

}
