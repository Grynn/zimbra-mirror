
package generated.zcsclient.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for inviteComponentCommon complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="inviteComponentCommon">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="method" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="compNum" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="rsvp" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="priority" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="loc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="percentComplete" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="completed" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="noBlob" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="fba" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fb" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="transp" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isOrg" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="x_uid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="uid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="seq" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="d" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="calItemId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="apptId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ciFolder" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="status" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="class" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ex" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="ridZ" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="allDay" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="draft" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="neverSent" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="changes" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "inviteComponentCommon")
@XmlSeeAlso({
    testInviteComponent.class
})
public class testInviteComponentCommon {

    @XmlAttribute(name = "method", required = true)
    protected String method;
    @XmlAttribute(name = "compNum", required = true)
    protected int compNum;
    @XmlAttribute(name = "rsvp", required = true)
    protected boolean rsvp;
    @XmlAttribute(name = "priority")
    protected String priority;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "loc")
    protected String loc;
    @XmlAttribute(name = "percentComplete")
    protected String percentComplete;
    @XmlAttribute(name = "completed")
    protected String completed;
    @XmlAttribute(name = "noBlob")
    protected Boolean noBlob;
    @XmlAttribute(name = "fba")
    protected String fba;
    @XmlAttribute(name = "fb")
    protected String fb;
    @XmlAttribute(name = "transp")
    protected String transp;
    @XmlAttribute(name = "isOrg")
    protected Boolean isOrg;
    @XmlAttribute(name = "x_uid")
    protected String xUid;
    @XmlAttribute(name = "uid")
    protected String uid;
    @XmlAttribute(name = "seq")
    protected Integer seq;
    @XmlAttribute(name = "d")
    protected Long d;
    @XmlAttribute(name = "calItemId")
    protected String calItemId;
    @XmlAttribute(name = "apptId")
    protected String apptId;
    @XmlAttribute(name = "ciFolder")
    protected String ciFolder;
    @XmlAttribute(name = "status")
    protected String status;
    @XmlAttribute(name = "class")
    protected String clazz;
    @XmlAttribute(name = "url")
    protected String url;
    @XmlAttribute(name = "ex")
    protected Boolean ex;
    @XmlAttribute(name = "ridZ")
    protected String ridZ;
    @XmlAttribute(name = "allDay")
    protected Boolean allDay;
    @XmlAttribute(name = "draft")
    protected Boolean draft;
    @XmlAttribute(name = "neverSent")
    protected Boolean neverSent;
    @XmlAttribute(name = "changes")
    protected String changes;

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethod(String value) {
        this.method = value;
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
     * Gets the value of the rsvp property.
     * 
     */
    public boolean isRsvp() {
        return rsvp;
    }

    /**
     * Sets the value of the rsvp property.
     * 
     */
    public void setRsvp(boolean value) {
        this.rsvp = value;
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
     * Gets the value of the completed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompleted() {
        return completed;
    }

    /**
     * Sets the value of the completed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompleted(String value) {
        this.completed = value;
    }

    /**
     * Gets the value of the noBlob property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNoBlob() {
        return noBlob;
    }

    /**
     * Sets the value of the noBlob property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNoBlob(Boolean value) {
        this.noBlob = value;
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
     * Gets the value of the x_Uid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getX_Uid() {
        return xUid;
    }

    /**
     * Sets the value of the x_Uid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setX_Uid(String value) {
        this.xUid = value;
    }

    /**
     * Gets the value of the uid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the value of the uid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUid(String value) {
        this.uid = value;
    }

    /**
     * Gets the value of the seq property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSeq() {
        return seq;
    }

    /**
     * Sets the value of the seq property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSeq(Integer value) {
        this.seq = value;
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

    /**
     * Gets the value of the apptId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApptId() {
        return apptId;
    }

    /**
     * Sets the value of the apptId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApptId(String value) {
        this.apptId = value;
    }

    /**
     * Gets the value of the ciFolder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCiFolder() {
        return ciFolder;
    }

    /**
     * Sets the value of the ciFolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCiFolder(String value) {
        this.ciFolder = value;
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
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the ex property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEx() {
        return ex;
    }

    /**
     * Sets the value of the ex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEx(Boolean value) {
        this.ex = value;
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
     * Gets the value of the changes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChanges() {
        return changes;
    }

    /**
     * Sets the value of the changes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChanges(String value) {
        this.changes = value;
    }

}
