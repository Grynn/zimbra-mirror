
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updatedAlarmInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updatedAlarmInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alarmData" type="{urn:zimbraMail}alarmDataInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="calItemId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updatedAlarmInfo", propOrder = {
    "alarmData"
})
@XmlSeeAlso({
    UpdatedAppointmentAlarmInfo.class,
    UpdatedTaskAlarmInfo.class
})
public class UpdatedAlarmInfo {

    protected AlarmDataInfo alarmData;
    @XmlAttribute(required = true)
    protected String calItemId;

    /**
     * Gets the value of the alarmData property.
     * 
     * @return
     *     possible object is
     *     {@link AlarmDataInfo }
     *     
     */
    public AlarmDataInfo getAlarmData() {
        return alarmData;
    }

    /**
     * Sets the value of the alarmData property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlarmDataInfo }
     *     
     */
    public void setAlarmData(AlarmDataInfo value) {
        this.alarmData = value;
    }

    /**
     * Gets the value of the calItemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalItemId() {
        return calItemId;
    }

    /**
     * Sets the value of the calItemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalItemId(String value) {
        this.calItemId = value;
    }

}
