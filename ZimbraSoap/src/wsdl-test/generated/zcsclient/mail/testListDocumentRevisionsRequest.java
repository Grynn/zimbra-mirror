
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for listDocumentRevisionsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="listDocumentRevisionsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="doc" type="{urn:zimbraMail}listDocumentRevisionsSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "listDocumentRevisionsRequest", propOrder = {
    "doc"
})
public class testListDocumentRevisionsRequest {

    @XmlElement(required = true)
    protected testListDocumentRevisionsSpec doc;

    /**
     * Gets the value of the doc property.
     * 
     * @return
     *     possible object is
     *     {@link testListDocumentRevisionsSpec }
     *     
     */
    public testListDocumentRevisionsSpec getDoc() {
        return doc;
    }

    /**
     * Sets the value of the doc property.
     * 
     * @param value
     *     allowed object is
     *     {@link testListDocumentRevisionsSpec }
     *     
     */
    public void setDoc(testListDocumentRevisionsSpec value) {
        this.doc = value;
    }

}
