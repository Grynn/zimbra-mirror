
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for renameLDAPEntryRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="renameLDAPEntryRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="dn" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="new_dn" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "renameLDAPEntryRequest")
public class testRenameLDAPEntryRequest {

    @XmlAttribute(name = "dn", required = true)
    protected String dn;
    @XmlAttribute(name = "new_dn", required = true)
    protected String newDn;

    /**
     * Gets the value of the dn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDn() {
        return dn;
    }

    /**
     * Sets the value of the dn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDn(String value) {
        this.dn = value;
    }

    /**
     * Gets the value of the new_Dn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNew_Dn() {
        return newDn;
    }

    /**
     * Sets the value of the new_Dn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNew_Dn(String value) {
        this.newDn = value;
    }

}
