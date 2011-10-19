
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for searchRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}mailSearchParams">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="warmup" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchRequest")
public class testSearchRequest
    extends testMailSearchParams
{

    @XmlAttribute(name = "warmup")
    protected Boolean warmup;

    /**
     * Gets the value of the warmup property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWarmup() {
        return warmup;
    }

    /**
     * Sets the value of the warmup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWarmup(Boolean value) {
        this.warmup = value;
    }

}
