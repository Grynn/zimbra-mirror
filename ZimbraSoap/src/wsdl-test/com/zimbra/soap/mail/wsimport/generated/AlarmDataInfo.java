
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for alarmDataInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="alarmDataInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alarm" type="{urn:zimbraMail}alarmInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nextAlarm" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="alarmInstStart" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="invId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="compNum" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="loc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "alarmDataInfo", propOrder = {
    "alarm"
})
public class AlarmDataInfo {

    protected AlarmInfo alarm;
    @XmlAttribute
    protected Long nextAlarm;
    @XmlAttribute
    protected Long alarmInstStart;
    @XmlAttribute(required = true)
    protected int invId;
    @XmlAttribute(required = true)
    protected int compNum;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String loc;

    /**
     * Gets the value of the alarm property.
     * 
     * @return
     *     possible object is
     *     {@link AlarmInfo }
     *     
     */
    public AlarmInfo getAlarm() {
        return alarm;
    }

    /**
     * Sets the value of the alarm property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlarmInfo }
     *     
     */
    public void setAlarm(AlarmInfo value) {
        this.alarm = value;
    }

    /**
     * Gets the value of the nextAlarm property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNextAlarm() {
        return nextAlarm;
    }

    /**
     * Sets the value of the nextAlarm property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNextAlarm(Long value) {
        this.nextAlarm = value;
    }

    /**
     * Gets the value of the alarmInstStart property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAlarmInstStart() {
        return alarmInstStart;
    }

    /**
     * Sets the value of the alarmInstStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAlarmInstStart(Long value) {
        this.alarmInstStart = value;
    }

    /**
     * Gets the value of the invId property.
     * 
     */
    public int getInvId() {
        return invId;
    }

    /**
     * Sets the value of the invId property.
     * 
     */
    public void setInvId(int value) {
        this.invId = value;
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
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the loc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoc() {
        return loc;
    }

    /**
     * Sets the value of the loc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoc(String value) {
        this.loc = value;
    }

}
