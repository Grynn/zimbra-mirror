
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getLoggerStatsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getLoggerStatsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hostname" type="{urn:zimbraAdmin}hostName" minOccurs="0"/>
 *         &lt;element name="stats" type="{urn:zimbraAdmin}statsSpec" minOccurs="0"/>
 *         &lt;element name="startTime" type="{urn:zimbraAdmin}timeAttr" minOccurs="0"/>
 *         &lt;element name="endTime" type="{urn:zimbraAdmin}timeAttr" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getLoggerStatsRequest", propOrder = {
    "hostname",
    "stats",
    "startTime",
    "endTime"
})
public class GetLoggerStatsRequest {

    protected HostName hostname;
    protected StatsSpec stats;
    protected TimeAttr startTime;
    protected TimeAttr endTime;

    /**
     * Gets the value of the hostname property.
     * 
     * @return
     *     possible object is
     *     {@link HostName }
     *     
     */
    public HostName getHostname() {
        return hostname;
    }

    /**
     * Sets the value of the hostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link HostName }
     *     
     */
    public void setHostname(HostName value) {
        this.hostname = value;
    }

    /**
     * Gets the value of the stats property.
     * 
     * @return
     *     possible object is
     *     {@link StatsSpec }
     *     
     */
    public StatsSpec getStats() {
        return stats;
    }

    /**
     * Sets the value of the stats property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatsSpec }
     *     
     */
    public void setStats(StatsSpec value) {
        this.stats = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link TimeAttr }
     *     
     */
    public TimeAttr getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeAttr }
     *     
     */
    public void setStartTime(TimeAttr value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     * @return
     *     possible object is
     *     {@link TimeAttr }
     *     
     */
    public TimeAttr getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeAttr }
     *     
     */
    public void setEndTime(TimeAttr value) {
        this.endTime = value;
    }

}
