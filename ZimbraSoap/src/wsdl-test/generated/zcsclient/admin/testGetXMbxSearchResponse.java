
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getXMbxSearchResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getXMbxSearchResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="searchtask" type="{urn:zimbraAdmin}searchNode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getXMbxSearchResponse", propOrder = {
    "searchtask"
})
public class testGetXMbxSearchResponse {

    @XmlElement(required = true)
    protected testSearchNode searchtask;

    /**
     * Gets the value of the searchtask property.
     * 
     * @return
     *     possible object is
     *     {@link testSearchNode }
     *     
     */
    public testSearchNode getSearchtask() {
        return searchtask;
    }

    /**
     * Sets the value of the searchtask property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSearchNode }
     *     
     */
    public void setSearchtask(testSearchNode value) {
        this.searchtask = value;
    }

}
