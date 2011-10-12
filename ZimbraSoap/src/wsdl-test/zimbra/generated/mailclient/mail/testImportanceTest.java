
package zimbra.generated.mailclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for importanceTest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="importanceTest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}filterTest">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="imp" use="required" type="{urn:zimbraMail}importance" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "importanceTest")
public class testImportanceTest
    extends testFilterTest
{

    @XmlAttribute(name = "imp", required = true)
    protected testImportance imp;

    /**
     * Gets the value of the imp property.
     * 
     * @return
     *     possible object is
     *     {@link testImportance }
     *     
     */
    public testImportance getImp() {
        return imp;
    }

    /**
     * Sets the value of the imp property.
     * 
     * @param value
     *     allowed object is
     *     {@link testImportance }
     *     
     */
    public void setImp(testImportance value) {
        this.imp = value;
    }

}
