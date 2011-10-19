
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for calendaringData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="calendaringData">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}commonCalendaringData">
 *       &lt;sequence>
 *         &lt;element name="or" type="{urn:zimbraMail}calOrganizer" minOccurs="0"/>
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="geo" type="{urn:zimbraMail}geoInfo" minOccurs="0"/>
 *         &lt;element name="fr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inst" type="{urn:zimbraMail}instanceDataInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="alarmData" type="{urn:zimbraMail}alarmDataInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="d" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "calendaringData", propOrder = {
    "or",
    "category",
    "geo",
    "fr",
    "inst",
    "alarmData"
})
@XmlSeeAlso({
    testAppointmentData.class,
    testTaskData.class
})
public class testCalendaringData
    extends testCommonCalendaringData
{

    protected testCalOrganizer or;
    protected List<String> category;
    protected testGeoInfo geo;
    protected String fr;
    protected List<testInstanceDataInfo> inst;
    protected testAlarmDataInfo alarmData;
    @XmlAttribute(name = "d")
    protected Long d;

    /**
     * Gets the value of the or property.
     * 
     * @return
     *     possible object is
     *     {@link testCalOrganizer }
     *     
     */
    public testCalOrganizer getOr() {
        return or;
    }

    /**
     * Sets the value of the or property.
     * 
     * @param value
     *     allowed object is
     *     {@link testCalOrganizer }
     *     
     */
    public void setOr(testCalOrganizer value) {
        this.or = value;
    }

    /**
     * Gets the value of the category property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the category property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCategory() {
        if (category == null) {
            category = new ArrayList<String>();
        }
        return this.category;
    }

    /**
     * Gets the value of the geo property.
     * 
     * @return
     *     possible object is
     *     {@link testGeoInfo }
     *     
     */
    public testGeoInfo getGeo() {
        return geo;
    }

    /**
     * Sets the value of the geo property.
     * 
     * @param value
     *     allowed object is
     *     {@link testGeoInfo }
     *     
     */
    public void setGeo(testGeoInfo value) {
        this.geo = value;
    }

    /**
     * Gets the value of the fr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFr() {
        return fr;
    }

    /**
     * Sets the value of the fr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFr(String value) {
        this.fr = value;
    }

    /**
     * Gets the value of the inst property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inst property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInst().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testInstanceDataInfo }
     * 
     * 
     */
    public List<testInstanceDataInfo> getInst() {
        if (inst == null) {
            inst = new ArrayList<testInstanceDataInfo>();
        }
        return this.inst;
    }

    /**
     * Gets the value of the alarmData property.
     * 
     * @return
     *     possible object is
     *     {@link testAlarmDataInfo }
     *     
     */
    public testAlarmDataInfo getAlarmData() {
        return alarmData;
    }

    /**
     * Sets the value of the alarmData property.
     * 
     * @param value
     *     allowed object is
     *     {@link testAlarmDataInfo }
     *     
     */
    public void setAlarmData(testAlarmDataInfo value) {
        this.alarmData = value;
    }

    /**
     * Gets the value of the d property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getD() {
        return d;
    }

    /**
     * Sets the value of the d property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setD(Long value) {
        this.d = value;
    }

}
