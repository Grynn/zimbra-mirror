
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for modifySearchFolderRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modifySearchFolderRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="search" type="{urn:zimbraMail}modifySearchFolderSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modifySearchFolderRequest", propOrder = {
    "search"
})
public class testModifySearchFolderRequest {

    @XmlElement(required = true)
    protected testModifySearchFolderSpec search;

    /**
     * Gets the value of the search property.
     * 
     * @return
     *     possible object is
     *     {@link testModifySearchFolderSpec }
     *     
     */
    public testModifySearchFolderSpec getSearch() {
        return search;
    }

    /**
     * Sets the value of the search property.
     * 
     * @param value
     *     allowed object is
     *     {@link testModifySearchFolderSpec }
     *     
     */
    public void setSearch(testModifySearchFolderSpec value) {
        this.search = value;
    }

}
