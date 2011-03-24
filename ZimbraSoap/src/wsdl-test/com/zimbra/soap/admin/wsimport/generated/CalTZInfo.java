
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for calTZInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="calTZInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="standard" type="{urn:zimbraAdmin}tzOnsetInfo" minOccurs="0"/>
 *         &lt;element name="daylight" type="{urn:zimbraAdmin}tzOnsetInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="stdoff" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="dayoff" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="stdname" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dayname" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "calTZInfo", propOrder = {
    "standard",
    "daylight"
})
public class CalTZInfo {

    protected TzOnsetInfo standard;
    protected TzOnsetInfo daylight;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected int stdoff;
    @XmlAttribute(required = true)
    protected int dayoff;
    @XmlAttribute
    protected String stdname;
    @XmlAttribute
    protected String dayname;

    /**
     * Gets the value of the standard property.
     * 
     * @return
     *     possible object is
     *     {@link TzOnsetInfo }
     *     
     */
    public TzOnsetInfo getStandard() {
        return standard;
    }

    /**
     * Sets the value of the standard property.
     * 
     * @param value
     *     allowed object is
     *     {@link TzOnsetInfo }
     *     
     */
    public void setStandard(TzOnsetInfo value) {
        this.standard = value;
    }

    /**
     * Gets the value of the daylight property.
     * 
     * @return
     *     possible object is
     *     {@link TzOnsetInfo }
     *     
     */
    public TzOnsetInfo getDaylight() {
        return daylight;
    }

    /**
     * Sets the value of the daylight property.
     * 
     * @param value
     *     allowed object is
     *     {@link TzOnsetInfo }
     *     
     */
    public void setDaylight(TzOnsetInfo value) {
        this.daylight = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the stdoff property.
     * 
     */
    public int getStdoff() {
        return stdoff;
    }

    /**
     * Sets the value of the stdoff property.
     * 
     */
    public void setStdoff(int value) {
        this.stdoff = value;
    }

    /**
     * Gets the value of the dayoff property.
     * 
     */
    public int getDayoff() {
        return dayoff;
    }

    /**
     * Sets the value of the dayoff property.
     * 
     */
    public void setDayoff(int value) {
        this.dayoff = value;
    }

    /**
     * Gets the value of the stdname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStdname() {
        return stdname;
    }

    /**
     * Sets the value of the stdname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStdname(String value) {
        this.stdname = value;
    }

    /**
     * Gets the value of the dayname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDayname() {
        return dayname;
    }

    /**
     * Sets the value of the dayname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDayname(String value) {
        this.dayname = value;
    }

}
