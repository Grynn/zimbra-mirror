
package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for commonInstanceDataAttrs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="commonInstanceDataAttrs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="ptst" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ridZ" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tzo" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="fba" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="percentComplete" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="recur" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="hasEx" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="priority" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fb" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="transp" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="loc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="otherAtt" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="alarm" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isOrg" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="invId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="compNum" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="status" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="class" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="allDay" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="draft" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="neverSent" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="dueDate" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="tzoDue" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "commonInstanceDataAttrs")
@XmlSeeAlso({
    testLegacyInstanceDataAttrs.class,
    testInstanceDataAttrs.class
})
public class testCommonInstanceDataAttrs {

    @XmlAttribute(name = "ptst")
    protected String ptst;
    @XmlAttribute(name = "ridZ")
    protected String ridZ;
    @XmlAttribute(name = "tzo")
    protected Long tzo;
    @XmlAttribute(name = "fba")
    protected String fba;
    @XmlAttribute(name = "percentComplete")
    protected String percentComplete;
    @XmlAttribute(name = "recur")
    protected Boolean recur;
    @XmlAttribute(name = "hasEx")
    protected Boolean hasEx;
    @XmlAttribute(name = "priority")
    protected String priority;
    @XmlAttribute(name = "fb")
    protected String fb;
    @XmlAttribute(name = "transp")
    protected String transp;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "loc")
    protected String loc;
    @XmlAttribute(name = "otherAtt")
    protected Boolean otherAtt;
    @XmlAttribute(name = "alarm")
    protected Boolean alarm;
    @XmlAttribute(name = "isOrg")
    protected Boolean isOrg;
    @XmlAttribute(name = "invId")
    protected String invId;
    @XmlAttribute(name = "compNum")
    protected Integer compNum;
    @XmlAttribute(name = "status")
    protected String status;
    @XmlAttribute(name = "class")
    protected String clazz;
    @XmlAttribute(name = "allDay")
    protected Boolean allDay;
    @XmlAttribute(name = "draft")
    protected Boolean draft;
    @XmlAttribute(name = "neverSent")
    protected Boolean neverSent;
    @XmlAttribute(name = "dueDate")
    protected Long dueDate;
    @XmlAttribute(name = "tzoDue")
    protected Integer tzoDue;

    /**
     * Gets the value of the ptst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPtst() {
        return ptst;
    }

    /**
     * Sets the value of the ptst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPtst(String value) {
        this.ptst = value;
    }

    /**
     * Gets the value of the ridZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRidZ() {
        return ridZ;
    }

    /**
     * Sets the value of the ridZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRidZ(String value) {
        this.ridZ = value;
    }

    /**
     * Gets the value of the tzo property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTzo() {
        return tzo;
    }

    /**
     * Sets the value of the tzo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTzo(Long value) {
        this.tzo = value;
    }

    /**
     * Gets the value of the fba property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFba() {
        return fba;
    }

    /**
     * Sets the value of the fba property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFba(String value) {
        this.fba = value;
    }

    /**
     * Gets the value of the percentComplete property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPercentComplete() {
        return percentComplete;
    }

    /**
     * Sets the value of the percentComplete property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPercentComplete(String value) {
        this.percentComplete = value;
    }

    /**
     * Gets the value of the recur property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRecur() {
        return recur;
    }

    /**
     * Sets the value of the recur property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRecur(Boolean value) {
        this.recur = value;
    }

    /**
     * Gets the value of the hasEx property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHasEx() {
        return hasEx;
    }

    /**
     * Sets the value of the hasEx property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasEx(Boolean value) {
        this.hasEx = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriority(String value) {
        this.priority = value;
    }

    /**
     * Gets the value of the fb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFb() {
        return fb;
    }

    /**
     * Sets the value of the fb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFb(String value) {
        this.fb = value;
    }

    /**
     * Gets the value of the transp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransp() {
        return transp;
    }

    /**
     * Sets the value of the transp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransp(String value) {
        this.transp = value;
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

    /**
     * Gets the value of the otherAtt property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOtherAtt() {
        return otherAtt;
    }

    /**
     * Sets the value of the otherAtt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOtherAtt(Boolean value) {
        this.otherAtt = value;
    }

    /**
     * Gets the value of the alarm property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAlarm() {
        return alarm;
    }

    /**
     * Sets the value of the alarm property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAlarm(Boolean value) {
        this.alarm = value;
    }

    /**
     * Gets the value of the isOrg property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsOrg() {
        return isOrg;
    }

    /**
     * Sets the value of the isOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsOrg(Boolean value) {
        this.isOrg = value;
    }

    /**
     * Gets the value of the invId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvId() {
        return invId;
    }

    /**
     * Sets the value of the invId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvId(String value) {
        this.invId = value;
    }

    /**
     * Gets the value of the compNum property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCompNum() {
        return compNum;
    }

    /**
     * Sets the value of the compNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCompNum(Integer value) {
        this.compNum = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Gets the value of the allDay property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllDay() {
        return allDay;
    }

    /**
     * Sets the value of the allDay property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllDay(Boolean value) {
        this.allDay = value;
    }

    /**
     * Gets the value of the draft property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDraft() {
        return draft;
    }

    /**
     * Sets the value of the draft property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDraft(Boolean value) {
        this.draft = value;
    }

    /**
     * Gets the value of the neverSent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNeverSent() {
        return neverSent;
    }

    /**
     * Sets the value of the neverSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeverSent(Boolean value) {
        this.neverSent = value;
    }

    /**
     * Gets the value of the dueDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDueDate() {
        return dueDate;
    }

    /**
     * Sets the value of the dueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDueDate(Long value) {
        this.dueDate = value;
    }

    /**
     * Gets the value of the tzoDue property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTzoDue() {
        return tzoDue;
    }

    /**
     * Sets the value of the tzoDue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTzoDue(Integer value) {
        this.tzoDue = value;
    }

}
