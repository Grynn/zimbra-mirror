
package com.zimbra.soap.mail.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAppointmentResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAppointmentResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="appt" type="{urn:zimbraMail}calendarItemInfo"/>
 *           &lt;element name="task" type="{urn:zimbraMail}taskItemInfo"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAppointmentResponse", propOrder = {
    "appt",
    "task"
})
public class GetAppointmentResponse {

    protected CalendarItemInfo appt;
    protected TaskItemInfo task;

    /**
     * Gets the value of the appt property.
     * 
     * @return
     *     possible object is
     *     {@link CalendarItemInfo }
     *     
     */
    public CalendarItemInfo getAppt() {
        return appt;
    }

    /**
     * Sets the value of the appt property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalendarItemInfo }
     *     
     */
    public void setAppt(CalendarItemInfo value) {
        this.appt = value;
    }

    /**
     * Gets the value of the task property.
     * 
     * @return
     *     possible object is
     *     {@link TaskItemInfo }
     *     
     */
    public TaskItemInfo getTask() {
        return task;
    }

    /**
     * Sets the value of the task property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskItemInfo }
     *     
     */
    public void setTask(TaskItemInfo value) {
        this.task = value;
    }

}
