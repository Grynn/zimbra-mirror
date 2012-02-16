
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for autoProvTaskControlRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="autoProvTaskControlRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="action" use="required" type="{urn:zimbraAdmin}action" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autoProvTaskControlRequest")
public class testAutoProvTaskControlRequest {

    @XmlAttribute(name = "action", required = true)
    protected testAction action;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link testAction }
     *     
     */
    public testAction getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAction }
     *     
     */
    public void setAction(testAction value) {
        this.action = value;
    }

}
