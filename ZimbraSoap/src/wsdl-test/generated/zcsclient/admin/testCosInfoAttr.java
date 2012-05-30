
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cosInfoAttr complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cosInfoAttr">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:zimbraAdmin>attr">
 *       &lt;attribute name="c" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="pd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cosInfoAttr")
public class testCosInfoAttr
    extends testAttr
{

    @XmlAttribute(name = "c")
    protected Boolean c;
    @XmlAttribute(name = "pd")
    protected Boolean pd;

    /**
     * Gets the value of the c property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isC() {
        return c;
    }

    /**
     * Sets the value of the c property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setC(Boolean value) {
        this.c = value;
    }

    /**
     * Gets the value of the pd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPd() {
        return pd;
    }

    /**
     * Sets the value of the pd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPd(Boolean value) {
        this.pd = value;
    }

}
