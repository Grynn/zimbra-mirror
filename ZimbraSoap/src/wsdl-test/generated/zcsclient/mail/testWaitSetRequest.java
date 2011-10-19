
package generated.zcsclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testId;
import generated.zcsclient.zm.testWaitSetAddSpec;


/**
 * <p>Java class for waitSetRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="waitSetRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="add" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="a" type="{urn:zimbra}waitSetAddSpec" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="update" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="a" type="{urn:zimbra}waitSetAddSpec" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="remove" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="a" type="{urn:zimbra}id" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="waitSet" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="seq" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="block" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="defTypes" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="timeout" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "waitSetRequest", propOrder = {
    "add",
    "update",
    "remove"
})
public class testWaitSetRequest {

    protected testWaitSetRequest.Add add;
    protected testWaitSetRequest.Update update;
    protected testWaitSetRequest.Remove remove;
    @XmlAttribute(name = "waitSet", required = true)
    protected String waitSet;
    @XmlAttribute(name = "seq", required = true)
    protected String seq;
    @XmlAttribute(name = "block")
    protected Boolean block;
    @XmlAttribute(name = "defTypes")
    protected String defTypes;
    @XmlAttribute(name = "timeout")
    protected Long timeout;

    /**
     * Gets the value of the add property.
     * 
     * @return
     *     possible object is
     *     {@link testWaitSetRequest.Add }
     *     
     */
    public testWaitSetRequest.Add getAdd() {
        return add;
    }

    /**
     * Sets the value of the add property.
     * 
     * @param value
     *     allowed object is
     *     {@link testWaitSetRequest.Add }
     *     
     */
    public void setAdd(testWaitSetRequest.Add value) {
        this.add = value;
    }

    /**
     * Gets the value of the update property.
     * 
     * @return
     *     possible object is
     *     {@link testWaitSetRequest.Update }
     *     
     */
    public testWaitSetRequest.Update getUpdate() {
        return update;
    }

    /**
     * Sets the value of the update property.
     * 
     * @param value
     *     allowed object is
     *     {@link testWaitSetRequest.Update }
     *     
     */
    public void setUpdate(testWaitSetRequest.Update value) {
        this.update = value;
    }

    /**
     * Gets the value of the remove property.
     * 
     * @return
     *     possible object is
     *     {@link testWaitSetRequest.Remove }
     *     
     */
    public testWaitSetRequest.Remove getRemove() {
        return remove;
    }

    /**
     * Sets the value of the remove property.
     * 
     * @param value
     *     allowed object is
     *     {@link testWaitSetRequest.Remove }
     *     
     */
    public void setRemove(testWaitSetRequest.Remove value) {
        this.remove = value;
    }

    /**
     * Gets the value of the waitSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaitSet() {
        return waitSet;
    }

    /**
     * Sets the value of the waitSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaitSet(String value) {
        this.waitSet = value;
    }

    /**
     * Gets the value of the seq property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeq() {
        return seq;
    }

    /**
     * Sets the value of the seq property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeq(String value) {
        this.seq = value;
    }

    /**
     * Gets the value of the block property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBlock() {
        return block;
    }

    /**
     * Sets the value of the block property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBlock(Boolean value) {
        this.block = value;
    }

    /**
     * Gets the value of the defTypes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefTypes() {
        return defTypes;
    }

    /**
     * Sets the value of the defTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefTypes(String value) {
        this.defTypes = value;
    }

    /**
     * Gets the value of the timeout property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTimeout(Long value) {
        this.timeout = value;
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
     *         &lt;element name="a" type="{urn:zimbra}waitSetAddSpec" maxOccurs="unbounded" minOccurs="0"/>
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
        "a"
    })
    public static class Add {

        protected List<testWaitSetAddSpec> a;

        /**
         * Gets the value of the a property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the a property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getA().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testWaitSetAddSpec }
         * 
         * 
         */
        public List<testWaitSetAddSpec> getA() {
            if (a == null) {
                a = new ArrayList<testWaitSetAddSpec>();
            }
            return this.a;
        }

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
     *         &lt;element name="a" type="{urn:zimbra}id" maxOccurs="unbounded" minOccurs="0"/>
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
        "a"
    })
    public static class Remove {

        protected List<testId> a;

        /**
         * Gets the value of the a property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the a property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getA().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testId }
         * 
         * 
         */
        public List<testId> getA() {
            if (a == null) {
                a = new ArrayList<testId>();
            }
            return this.a;
        }

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
     *         &lt;element name="a" type="{urn:zimbra}waitSetAddSpec" maxOccurs="unbounded" minOccurs="0"/>
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
        "a"
    })
    public static class Update {

        protected List<testWaitSetAddSpec> a;

        /**
         * Gets the value of the a property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the a property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getA().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link testWaitSetAddSpec }
         * 
         * 
         */
        public List<testWaitSetAddSpec> getA() {
            if (a == null) {
                a = new ArrayList<testWaitSetAddSpec>();
            }
            return this.a;
        }

    }

}
