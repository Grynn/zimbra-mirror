
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import generated.zcsclient.zm.testAutoProvPrincipalBy;


/**
 * <p>Java class for principalSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="principalSelector">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="by" use="required" type="{urn:zimbra}autoProvPrincipalBy" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "principalSelector", propOrder = {
    "value"
})
public class testPrincipalSelector {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "by", required = true)
    protected testAutoProvPrincipalBy by;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the by property.
     * 
     * @return
     *     possible object is
     *     {@link testAutoProvPrincipalBy }
     *     
     */
    public testAutoProvPrincipalBy getBy() {
        return by;
    }

    /**
     * Sets the value of the by property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAutoProvPrincipalBy }
     *     
     */
    public void setBy(testAutoProvPrincipalBy value) {
        this.by = value;
    }

}
