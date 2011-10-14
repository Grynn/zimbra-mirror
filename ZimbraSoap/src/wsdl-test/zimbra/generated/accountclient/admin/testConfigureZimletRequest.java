
package zimbra.generated.accountclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for configureZimletRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configureZimletRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="content" type="{urn:zimbraAdmin}attachmentIdAttrib"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configureZimletRequest", propOrder = {
    "content"
})
public class testConfigureZimletRequest {

    @XmlElement(required = true)
    protected testAttachmentIdAttrib content;

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link testAttachmentIdAttrib }
     *     
     */
    public testAttachmentIdAttrib getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAttachmentIdAttrib }
     *     
     */
    public void setContent(testAttachmentIdAttrib value) {
        this.content = value;
    }

}
