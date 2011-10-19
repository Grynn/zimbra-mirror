
package generated.zcsclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testAccountSelector;
import generated.zcsclient.zm.testGranteeChooser;


/**
 * <p>Java class for getShareInfoRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getShareInfoRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="grantee" type="{urn:zimbra}granteeChooser" minOccurs="0"/>
 *         &lt;element name="owner" type="{urn:zimbra}accountSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="internal" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="includeSelf" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getShareInfoRequest", propOrder = {
    "grantee",
    "owner"
})
public class testGetShareInfoRequest {

    protected testGranteeChooser grantee;
    protected testAccountSelector owner;
    @XmlAttribute(name = "internal")
    protected Boolean internal;
    @XmlAttribute(name = "includeSelf")
    protected Boolean includeSelf;

    /**
     * Gets the value of the grantee property.
     * 
     * @return
     *     possible object is
     *     {@link testGranteeChooser }
     *     
     */
    public testGranteeChooser getGrantee() {
        return grantee;
    }

    /**
     * Sets the value of the grantee property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGranteeChooser }
     *     
     */
    public void setGrantee(testGranteeChooser value) {
        this.grantee = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link testAccountSelector }
     *     
     */
    public testAccountSelector getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAccountSelector }
     *     
     */
    public void setOwner(testAccountSelector value) {
        this.owner = value;
    }

    /**
     * Gets the value of the internal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInternal() {
        return internal;
    }

    /**
     * Sets the value of the internal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInternal(Boolean value) {
        this.internal = value;
    }

    /**
     * Gets the value of the includeSelf property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeSelf() {
        return includeSelf;
    }

    /**
     * Sets the value of the includeSelf property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeSelf(Boolean value) {
        this.includeSelf = value;
    }

}
