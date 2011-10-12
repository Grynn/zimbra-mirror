
package zimbra.generated.accountclient.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDistributionListResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDistributionListResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:zimbraAccount}dl" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="isMember" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isOwner" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDistributionListResponse", propOrder = {
    "dl"
})
public class testGetDistributionListResponse {

    protected testDistributionListInfo dl;
    @XmlAttribute(name = "isMember", required = true)
    protected boolean isMember;
    @XmlAttribute(name = "isOwner", required = true)
    protected boolean isOwner;

    /**
     * Gets the value of the dl property.
     * 
     * @return
     *     possible object is
     *     {@link testDistributionListInfo }
     *     
     */
    public testDistributionListInfo getDl() {
        return dl;
    }

    /**
     * Sets the value of the dl property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDistributionListInfo }
     *     
     */
    public void setDl(testDistributionListInfo value) {
        this.dl = value;
    }

    /**
     * Gets the value of the isMember property.
     * 
     */
    public boolean isIsMember() {
        return isMember;
    }

    /**
     * Sets the value of the isMember property.
     * 
     */
    public void setIsMember(boolean value) {
        this.isMember = value;
    }

    /**
     * Gets the value of the isOwner property.
     * 
     */
    public boolean isIsOwner() {
        return isOwner;
    }

    /**
     * Sets the value of the isOwner property.
     * 
     */
    public void setIsOwner(boolean value) {
        this.isOwner = value;
    }

}
