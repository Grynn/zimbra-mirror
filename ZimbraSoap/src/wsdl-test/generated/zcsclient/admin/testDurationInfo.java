
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for durationInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="durationInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="neg" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="w" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="d" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="h" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="m" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="s" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="related" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="count" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "durationInfo")
public class testDurationInfo {

    @XmlAttribute(name = "neg")
    protected Boolean neg;
    @XmlAttribute(name = "w")
    protected Integer w;
    @XmlAttribute(name = "d")
    protected Integer d;
    @XmlAttribute(name = "h")
    protected Integer h;
    @XmlAttribute(name = "m")
    protected Integer m;
    @XmlAttribute(name = "s")
    protected Integer s;
    @XmlAttribute(name = "related")
    protected String related;
    @XmlAttribute(name = "count")
    protected Integer count;

    /**
     * Gets the value of the neg property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNeg() {
        return neg;
    }

    /**
     * Sets the value of the neg property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeg(Boolean value) {
        this.neg = value;
    }

    /**
     * Gets the value of the w property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getW() {
        return w;
    }

    /**
     * Sets the value of the w property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setW(Integer value) {
        this.w = value;
    }

    /**
     * Gets the value of the d property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getD() {
        return d;
    }

    /**
     * Sets the value of the d property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setD(Integer value) {
        this.d = value;
    }

    /**
     * Gets the value of the h property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getH() {
        return h;
    }

    /**
     * Sets the value of the h property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setH(Integer value) {
        this.h = value;
    }

    /**
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setM(Integer value) {
        this.m = value;
    }

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setS(Integer value) {
        this.s = value;
    }

    /**
     * Gets the value of the related property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelated() {
        return related;
    }

    /**
     * Sets the value of the related property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelated(String value) {
        this.related = value;
    }

    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCount(Integer value) {
        this.count = value;
    }

}
