
package com.zimbra.soap.mail.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for searchConvRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchConvRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbra}searchParamsInfo">
 *       &lt;sequence>
 *         &lt;element name="header" type="{urn:zimbra}attributeName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tz" type="{urn:zimbraMail}calTZInfo" minOccurs="0"/>
 *         &lt;element name="locale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cursor" type="{urn:zimbra}cursorInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nest" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="cid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="needExp" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchConvRequest", propOrder = {
    "header",
    "tz",
    "locale",
    "cursor"
})
public class SearchConvRequest
    extends SearchParamsInfo
{

    protected List<AttributeName> header;
    protected CalTZInfo tz;
    protected String locale;
    protected CursorInfo cursor;
    @XmlAttribute
    protected Boolean nest;
    @XmlAttribute(required = true)
    protected String cid;
    @XmlAttribute
    protected Boolean needExp;

    /**
     * Gets the value of the header property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the header property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeader().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributeName }
     * 
     * 
     */
    public List<AttributeName> getHeader() {
        if (header == null) {
            header = new ArrayList<AttributeName>();
        }
        return this.header;
    }

    /**
     * Gets the value of the tz property.
     * 
     * @return
     *     possible object is
     *     {@link CalTZInfo }
     *     
     */
    public CalTZInfo getTz() {
        return tz;
    }

    /**
     * Sets the value of the tz property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalTZInfo }
     *     
     */
    public void setTz(CalTZInfo value) {
        this.tz = value;
    }

    /**
     * Gets the value of the locale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the value of the locale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocale(String value) {
        this.locale = value;
    }

    /**
     * Gets the value of the cursor property.
     * 
     * @return
     *     possible object is
     *     {@link CursorInfo }
     *     
     */
    public CursorInfo getCursor() {
        return cursor;
    }

    /**
     * Sets the value of the cursor property.
     * 
     * @param value
     *     allowed object is
     *     {@link CursorInfo }
     *     
     */
    public void setCursor(CursorInfo value) {
        this.cursor = value;
    }

    /**
     * Gets the value of the nest property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNest() {
        return nest;
    }

    /**
     * Sets the value of the nest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNest(Boolean value) {
        this.nest = value;
    }

    /**
     * Gets the value of the cid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCid() {
        return cid;
    }

    /**
     * Sets the value of the cid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCid(String value) {
        this.cid = value;
    }

    /**
     * Gets the value of the needExp property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNeedExp() {
        return needExp;
    }

    /**
     * Sets the value of the needExp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeedExp(Boolean value) {
        this.needExp = value;
    }

}
