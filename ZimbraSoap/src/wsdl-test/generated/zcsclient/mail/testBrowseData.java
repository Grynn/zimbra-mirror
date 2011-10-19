
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for browseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="browseData">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="h" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="freq" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "browseData", propOrder = {
    "value"
})
public class testBrowseData {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "h")
    protected String h;
    @XmlAttribute(name = "freq", required = true)
    protected int freq;

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
     * Gets the value of the h property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getH() {
        return h;
    }

    /**
     * Sets the value of the h property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setH(String value) {
        this.h = value;
    }

    /**
     * Gets the value of the freq property.
     * 
     */
    public int getFreq() {
        return freq;
    }

    /**
     * Sets the value of the freq property.
     * 
     */
    public void setFreq(int value) {
        this.freq = value;
    }

}
