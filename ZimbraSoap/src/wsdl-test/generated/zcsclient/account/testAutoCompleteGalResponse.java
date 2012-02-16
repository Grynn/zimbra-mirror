
package generated.zcsclient.account;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for autoCompleteGalResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="autoCompleteGalResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cn" type="{urn:zimbraAccount}contactInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="more" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="tokenizeKey" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="paginationSupported" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autoCompleteGalResponse", propOrder = {
    "cn"
})
public class testAutoCompleteGalResponse {

    protected List<testContactInfo> cn;
    @XmlAttribute(name = "more")
    protected Boolean more;
    @XmlAttribute(name = "tokenizeKey")
    protected Boolean tokenizeKey;
    @XmlAttribute(name = "paginationSupported")
    protected Boolean paginationSupported;

    /**
     * Gets the value of the cn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testContactInfo }
     * 
     * 
     */
    public List<testContactInfo> getCn() {
        if (cn == null) {
            cn = new ArrayList<testContactInfo>();
        }
        return this.cn;
    }

    /**
     * Gets the value of the more property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMore() {
        return more;
    }

    /**
     * Sets the value of the more property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMore(Boolean value) {
        this.more = value;
    }

    /**
     * Gets the value of the tokenizeKey property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTokenizeKey() {
        return tokenizeKey;
    }

    /**
     * Sets the value of the tokenizeKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTokenizeKey(Boolean value) {
        this.tokenizeKey = value;
    }

    /**
     * Gets the value of the paginationSupported property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPaginationSupported() {
        return paginationSupported;
    }

    /**
     * Sets the value of the paginationSupported property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPaginationSupported(Boolean value) {
        this.paginationSupported = value;
    }

}
