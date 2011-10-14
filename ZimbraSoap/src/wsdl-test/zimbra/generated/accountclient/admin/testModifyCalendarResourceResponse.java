
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for modifyCalendarResourceResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modifyCalendarResourceResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="calresource" type="{urn:zimbraAdmin}calendarResourceInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modifyCalendarResourceResponse", propOrder = {
    "calresource"
})
public class testModifyCalendarResourceResponse {

    @XmlElement(required = true)
    protected testCalendarResourceInfo calresource;

    /**
     * Gets the value of the calresource property.
     * 
     * @return
     *     possible object is
     *     {@link testCalendarResourceInfo }
     *     
     */
    public testCalendarResourceInfo getCalresource() {
        return calresource;
    }

    /**
     * Sets the value of the calresource property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalendarResourceInfo }
     *     
     */
    public void setCalresource(testCalendarResourceInfo value) {
        this.calresource = value;
    }

}
