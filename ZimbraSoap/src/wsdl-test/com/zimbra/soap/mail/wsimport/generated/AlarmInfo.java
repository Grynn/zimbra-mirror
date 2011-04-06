
package com.zimbra.soap.mail.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for alarmInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="alarmInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="trigger" type="{urn:zimbraMail}alarmTriggerInfo" minOccurs="0"/>
 *         &lt;element name="repeat" type="{urn:zimbraMail}durationInfo" minOccurs="0"/>
 *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="attach" type="{urn:zimbraMail}calendarAttach" minOccurs="0"/>
 *         &lt;element name="summary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="at" type="{urn:zimbraMail}calendarAttendee" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="xprop" type="{urn:zimbraMail}xProp" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="action" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "alarmInfo", propOrder = {
    "trigger",
    "repeat",
    "desc",
    "attach",
    "summary",
    "at",
    "xprop"
})
public class AlarmInfo {

    protected AlarmTriggerInfo trigger;
    protected DurationInfo repeat;
    protected String desc;
    protected CalendarAttach attach;
    protected String summary;
    protected List<CalendarAttendee> at;
    protected List<XProp> xprop;
    @XmlAttribute(required = true)
    protected String action;

    /**
     * Gets the value of the trigger property.
     * 
     * @return
     *     possible object is
     *     {@link AlarmTriggerInfo }
     *     
     */
    public AlarmTriggerInfo getTrigger() {
        return trigger;
    }

    /**
     * Sets the value of the trigger property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlarmTriggerInfo }
     *     
     */
    public void setTrigger(AlarmTriggerInfo value) {
        this.trigger = value;
    }

    /**
     * Gets the value of the repeat property.
     * 
     * @return
     *     possible object is
     *     {@link DurationInfo }
     *     
     */
    public DurationInfo getRepeat() {
        return repeat;
    }

    /**
     * Sets the value of the repeat property.
     * 
     * @param value
     *     allowed object is
     *     {@link DurationInfo }
     *     
     */
    public void setRepeat(DurationInfo value) {
        this.repeat = value;
    }

    /**
     * Gets the value of the desc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the value of the desc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesc(String value) {
        this.desc = value;
    }

    /**
     * Gets the value of the attach property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarAttach }
     *     
     */
    public CalendarAttach getAttach() {
        return attach;
    }

    /**
     * Sets the value of the attach property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarAttach }
     *     
     */
    public void setAttach(CalendarAttach value) {
        this.attach = value;
    }

    /**
     * Gets the value of the summary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Sets the value of the summary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSummary(String value) {
        this.summary = value;
    }

    /**
     * Gets the value of the at property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the at property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CalendarAttendee }
     * 
     * 
     */
    public List<CalendarAttendee> getAt() {
        if (at == null) {
            at = new ArrayList<CalendarAttendee>();
        }
        return this.at;
    }

    /**
     * Gets the value of the xprop property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xprop property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXprop().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XProp }
     * 
     * 
     */
    public List<XProp> getXprop() {
        if (xprop == null) {
            xprop = new ArrayList<XProp>();
        }
        return this.xprop;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

}
