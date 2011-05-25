
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sendInviteReplyRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sendInviteReplyRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="exceptId" type="{urn:zimbraMail}dtTimeInfo" minOccurs="0"/>
 *         &lt;element name="tz" type="{urn:zimbraMail}calTZInfo" minOccurs="0"/>
 *         &lt;element name="m" type="{urn:zimbraMail}calendarItemMsg" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="compNum" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="verb" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="updateOrganizer" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="idnt" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendInviteReplyRequest", propOrder = {
    "exceptId",
    "tz",
    "m"
})
public class SendInviteReplyRequest {

    protected DtTimeInfo exceptId;
    protected CalTZInfo tz;
    protected CalendarItemMsg m;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected int compNum;
    @XmlAttribute(required = true)
    protected String verb;
    @XmlAttribute
    protected Boolean updateOrganizer;
    @XmlAttribute
    protected String idnt;

    /**
     * Gets the value of the exceptId property.
     * 
     * @return
     *     possible object is
     *     {@link DtTimeInfo }
     *     
     */
    public DtTimeInfo getExceptId() {
        return exceptId;
    }

    /**
     * Sets the value of the exceptId property.
     * 
     * @param value
     *     allowed object is
     *     {@link DtTimeInfo }
     *     
     */
    public void setExceptId(DtTimeInfo value) {
        this.exceptId = value;
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
     * Gets the value of the m property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarItemMsg }
     *     
     */
    public CalendarItemMsg getM() {
        return m;
    }

    /**
     * Sets the value of the m property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarItemMsg }
     *     
     */
    public void setM(CalendarItemMsg value) {
        this.m = value;
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
     * Gets the value of the compNum property.
     * 
     */
    public int getCompNum() {
        return compNum;
    }

    /**
     * Sets the value of the compNum property.
     * 
     */
    public void setCompNum(int value) {
        this.compNum = value;
    }

    /**
     * Gets the value of the verb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerb() {
        return verb;
    }

    /**
     * Sets the value of the verb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerb(String value) {
        this.verb = value;
    }

    /**
     * Gets the value of the updateOrganizer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUpdateOrganizer() {
        return updateOrganizer;
    }

    /**
     * Sets the value of the updateOrganizer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUpdateOrganizer(Boolean value) {
        this.updateOrganizer = value;
    }

    /**
     * Gets the value of the idnt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdnt() {
        return idnt;
    }

    /**
     * Sets the value of the idnt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdnt(String value) {
        this.idnt = value;
    }

}
