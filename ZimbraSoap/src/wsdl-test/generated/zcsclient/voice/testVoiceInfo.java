
package generated.zcsclient.voice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for voiceInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="voiceInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="folder" type="{urn:zimbraVoice}rootVoiceFolder"/>
 *         &lt;element name="callfeatures" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="callfeature" type="{urn:zimbraVoice}callFeature" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="label" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="callable" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="editable" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="c2cDeviceId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="vm" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="used" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="limit" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "voiceInfo", propOrder = {
    "folder",
    "callfeatures"
})
public class testVoiceInfo {

    @XmlElement(required = true)
    protected testRootVoiceFolder folder;
    protected testVoiceInfo.Callfeatures callfeatures;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "label", required = true)
    protected String label;
    @XmlAttribute(name = "callable", required = true)
    protected boolean callable;
    @XmlAttribute(name = "editable", required = true)
    protected boolean editable;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "c2cDeviceId")
    protected String c2CDeviceId;
    @XmlAttribute(name = "vm", required = true)
    protected boolean vm;
    @XmlAttribute(name = "used")
    protected Long used;
    @XmlAttribute(name = "limit")
    protected Long limit;

    /**
     * Gets the value of the folder property.
     * 
     * @return
     *     possible object is
     *     {@link testRootVoiceFolder }
     *     
     */
    public testRootVoiceFolder getFolder() {
        return folder;
    }

    /**
     * Sets the value of the folder property.
     * 
     * @param value
     *     allowed object is
     *     {@link testRootVoiceFolder }
     *     
     */
    public void setFolder(testRootVoiceFolder value) {
        this.folder = value;
    }

    /**
     * Gets the value of the callfeatures property.
     * 
     * @return
     *     possible object is
     *     {@link testVoiceInfo.Callfeatures }
     *     
     */
    public testVoiceInfo.Callfeatures getCallfeatures() {
        return callfeatures;
    }

    /**
     * Sets the value of the callfeatures property.
     * 
     * @param value
     *     allowed object is
     *     {@link testVoiceInfo.Callfeatures }
     *     
     */
    public void setCallfeatures(testVoiceInfo.Callfeatures value) {
        this.callfeatures = value;
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
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the callable property.
     * 
     */
    public boolean isCallable() {
        return callable;
    }

    /**
     * Sets the value of the callable property.
     * 
     */
    public void setCallable(boolean value) {
        this.callable = value;
    }

    /**
     * Gets the value of the editable property.
     * 
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Sets the value of the editable property.
     * 
     */
    public void setEditable(boolean value) {
        this.editable = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the c2CDeviceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC2CDeviceId() {
        return c2CDeviceId;
    }

    /**
     * Sets the value of the c2CDeviceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC2CDeviceId(String value) {
        this.c2CDeviceId = value;
    }

    /**
     * Gets the value of the vm property.
     * 
     */
    public boolean isVm() {
        return vm;
    }

    /**
     * Sets the value of the vm property.
     * 
     */
    public void setVm(boolean value) {
        this.vm = value;
    }

    /**
     * Gets the value of the used property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getUsed() {
        return used;
    }

    /**
     * Sets the value of the used property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setUsed(Long value) {
        this.used = value;
    }

    /**
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLimit(Long value) {
        this.limit = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="callfeature" type="{urn:zimbraVoice}callFeature" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "callfeature"
    })
    public static class Callfeatures {

        protected List<testCallFeature> callfeature;

        /**
         * Gets the value of the callfeature property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the callfeature property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCallfeature().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testCallFeature }
         * 
         * 
         */
        public List<testCallFeature> getCallfeature() {
            if (callfeature == null) {
                callfeature = new ArrayList<testCallFeature>();
            }
            return this.callfeature;
        }

    }

}
