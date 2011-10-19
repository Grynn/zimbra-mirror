
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createSearchFolderRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createSearchFolderRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="search" type="{urn:zimbraMail}newSearchFolderSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createSearchFolderRequest", propOrder = {
    "search"
})
public class testCreateSearchFolderRequest {

    @XmlElement(required = true)
    protected testNewSearchFolderSpec search;

    /**
     * Gets the value of the search property.
     * 
     * @return
     *     possible object is
     *     {@link testNewSearchFolderSpec }
     *     
     */
    public testNewSearchFolderSpec getSearch() {
        return search;
    }

    /**
     * Sets the value of the search property.
     * 
     * @param value
     *     allowed object is
     *     {@link testNewSearchFolderSpec }
     *     
     */
    public void setSearch(testNewSearchFolderSpec value) {
        this.search = value;
    }

}
