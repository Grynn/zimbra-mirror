
package zimbra.generated.adminclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for filterTestImportance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filterTestImportance">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}filterTestInfo">
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
@XmlType(name = "filterTestImportance")
public class testFilterTestImportance
    extends testFilterTestInfo
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
