
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for inviteComponentWithGroupInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="inviteComponentWithGroupInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}inviteComponentCommon">
 *       &lt;sequence>
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="contact" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="geo" type="{urn:zimbraMail}geoInfo" minOccurs="0"/>
 *         &lt;element name="at" type="{urn:zimbraMail}calendarAttendeeWithGroupInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="alarm" type="{urn:zimbraMail}alarmInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="xprop" type="{urn:zimbraMail}xProp" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="fr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="descHtml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="or" type="{urn:zimbraMail}calOrganizer" minOccurs="0"/>
 *         &lt;element name="recur" type="{urn:zimbraMail}recurrenceInfo" minOccurs="0"/>
 *         &lt;element name="exceptId" type="{urn:zimbraMail}exceptionRecurIdInfo" minOccurs="0"/>
 *         &lt;element name="s" type="{urn:zimbraMail}dtTimeInfo" minOccurs="0"/>
 *         &lt;element name="e" type="{urn:zimbraMail}dtTimeInfo" minOccurs="0"/>
 *         &lt;element name="dur" type="{urn:zimbraMail}durationInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "inviteComponentWithGroupInfo", propOrder = {
    "category",
    "comment",
    "contact",
    "geo",
    "at",
    "alarm",
    "xprop",
    "fr",
    "desc",
    "descHtml",
    "or",
    "recur",
    "exceptId",
    "s",
    "e",
    "dur"
})
public class testInviteComponentWithGroupInfo
    extends testInviteComponentCommon
{

    protected List<String> category;
    protected List<String> comment;
    protected List<String> contact;
    protected testGeoInfo geo;
    protected List<testCalendarAttendeeWithGroupInfo> at;
    protected List<testAlarmInfo> alarm;
    protected List<testXProp> xprop;
    protected String fr;
    protected String desc;
    protected String descHtml;
    protected testCalOrganizer or;
    protected testRecurrenceInfo recur;
    protected testExceptionRecurIdInfo exceptId;
    protected testDtTimeInfo s;
    protected testDtTimeInfo e;
    protected testDurationInfo dur;

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
     * Gets the value of the comment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the comment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getComment() {
        if (comment == null) {
            comment = new ArrayList<String>();
        }
        return this.comment;
    }

    /**
     * Gets the value of the contact property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contact property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContact().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getContact() {
        if (contact == null) {
            contact = new ArrayList<String>();
        }
        return this.contact;
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
     * {@link testCalendarAttendeeWithGroupInfo }
     * 
     * 
     */
    public List<testCalendarAttendeeWithGroupInfo> getAt() {
        if (at == null) {
            at = new ArrayList<testCalendarAttendeeWithGroupInfo>();
        }
        return this.at;
    }

    /**
     * Gets the value of the alarm property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alarm property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlarm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testAlarmInfo }
     * 
     * 
     */
    public List<testAlarmInfo> getAlarm() {
        if (alarm == null) {
            alarm = new ArrayList<testAlarmInfo>();
        }
        return this.alarm;
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
     * {@link testXProp }
     * 
     * 
     */
    public List<testXProp> getXprop() {
        if (xprop == null) {
            xprop = new ArrayList<testXProp>();
        }
        return this.xprop;
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
     * Gets the value of the descHtml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescHtml() {
        return descHtml;
    }

    /**
     * Sets the value of the descHtml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescHtml(String value) {
        this.descHtml = value;
    }

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
     * Gets the value of the recur property.
     * 
     * @return
     *     possible object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public testRecurrenceInfo getRecur() {
        return recur;
    }

    /**
     * Sets the value of the recur property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRecurrenceInfo }
     *     
     */
    public void setRecur(testRecurrenceInfo value) {
        this.recur = value;
    }

    /**
     * Gets the value of the exceptId property.
     * 
     * @return
     *     possible object is
     *     {@link testExceptionRecurIdInfo }
     *     
     */
    public testExceptionRecurIdInfo getExceptId() {
        return exceptId;
    }

    /**
     * Sets the value of the exceptId property.
     * 
     * @param value
     *     allowed object is
     *     {@link testExceptionRecurIdInfo }
     *     
     */
    public void setExceptId(testExceptionRecurIdInfo value) {
        this.exceptId = value;
    }

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link testDtTimeInfo }
     *     
     */
    public testDtTimeInfo getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDtTimeInfo }
     *     
     */
    public void setS(testDtTimeInfo value) {
        this.s = value;
    }

    /**
     * Gets the value of the e property.
     * 
     * @return
     *     possible object is
     *     {@link testDtTimeInfo }
     *     
     */
    public testDtTimeInfo getE() {
        return e;
    }

    /**
     * Sets the value of the e property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDtTimeInfo }
     *     
     */
    public void setE(testDtTimeInfo value) {
        this.e = value;
    }

    /**
     * Gets the value of the dur property.
     * 
     * @return
     *     possible object is
     *     {@link testDurationInfo }
     *     
     */
    public testDurationInfo getDur() {
        return dur;
    }

    /**
     * Sets the value of the dur property.
     * 
     * @param value
     *     allowed object is
     *     {@link testDurationInfo }
     *     
     */
    public void setDur(testDurationInfo value) {
        this.dur = value;
    }

}
