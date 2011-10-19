
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for annotatedCosInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="annotatedCosInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraAdmin}cosInfo">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="isDefaultCos" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "annotatedCosInfo")
public class testAnnotatedCosInfo
    extends testCosInfo
{

    @XmlAttribute(name = "isDefaultCos")
    protected Boolean isDefaultCos;

    /**
     * Gets the value of the isDefaultCos property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsDefaultCos() {
        return isDefaultCos;
    }

    /**
     * Sets the value of the isDefaultCos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsDefaultCos(Boolean value) {
        this.isDefaultCos = value;
    }

}
