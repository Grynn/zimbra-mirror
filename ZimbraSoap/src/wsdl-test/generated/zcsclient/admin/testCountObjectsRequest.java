
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for countObjectsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="countObjectsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="domain" type="{urn:zimbraAdmin}domainSelector" minOccurs="0"/>
 *         &lt;element name="ucservice" type="{urn:zimbraAdmin}ucServiceSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{urn:zimbraAdmin}countObjectsType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "countObjectsRequest", propOrder = {
    "domain",
    "ucservice"
})
public class testCountObjectsRequest {

    protected testDomainSelector domain;
    protected testUcServiceSelector ucservice;
    @XmlAttribute(name = "type", required = true)
    protected testCountObjectsType type;

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link testDomainSelector }
     *     
     */
    public testDomainSelector getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDomainSelector }
     *     
     */
    public void setDomain(testDomainSelector value) {
        this.domain = value;
    }

    /**
     * Gets the value of the ucservice property.
     * 
     * @return
     *     possible object is
     *     {@link testUcServiceSelector }
     *     
     */
    public testUcServiceSelector getUcservice() {
        return ucservice;
    }

    /**
     * Sets the value of the ucservice property.
     * 
     * @param value
     *     allowed object is
     *     {@link testUcServiceSelector }
     *     
     */
    public void setUcservice(testUcServiceSelector value) {
        this.ucservice = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link testCountObjectsType }
     *     
     */
    public testCountObjectsType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCountObjectsType }
     *     
     */
    public void setType(testCountObjectsType value) {
        this.type = value;
    }

}
