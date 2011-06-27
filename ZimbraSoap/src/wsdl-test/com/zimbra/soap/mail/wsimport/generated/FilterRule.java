
package com.zimbra.soap.mail.wsimport.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for filterRule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filterRule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filterTests" type="{urn:zimbraMail}filterTests"/>
 *         &lt;element name="filterActions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;choice maxOccurs="unbounded" minOccurs="0">
 *                     &lt;element name="actionKeep" type="{urn:zimbraMail}filterActionKeep"/>
 *                     &lt;element name="actionDiscard" type="{urn:zimbraMail}filterActionDiscard"/>
 *                     &lt;element name="actionFileInto" type="{urn:zimbraMail}filterActionFileInto"/>
 *                     &lt;element name="actionFlag" type="{urn:zimbraMail}filterActionFlag"/>
 *                     &lt;element name="actionTag" type="{urn:zimbraMail}filterActionTag"/>
 *                     &lt;element name="actionRedirect" type="{urn:zimbraMail}filterActionRedirect"/>
 *                     &lt;element name="actionReply" type="{urn:zimbraMail}filterActionReply"/>
 *                     &lt;element name="actionNotify" type="{urn:zimbraMail}filterActionNotify"/>
 *                     &lt;element name="actionStop" type="{urn:zimbraMail}filterActionStop"/>
 *                   &lt;/choice>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="active" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filterRule", propOrder = {
    "filterTests",
    "filterActions"
})
public class FilterRule {

    @XmlElement(required = true)
    protected FilterTests filterTests;
    protected FilterRule.FilterActions filterActions;
    @XmlAttribute
    protected String name;
    @XmlAttribute(required = true)
    protected boolean active;

    /**
     * Gets the value of the filterTests property.
     * 
     * @return
     *     possible object is
     *     {@link FilterTests }
     *     
     */
    public FilterTests getFilterTests() {
        return filterTests;
    }

    /**
     * Sets the value of the filterTests property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterTests }
     *     
     */
    public void setFilterTests(FilterTests value) {
        this.filterTests = value;
    }

    /**
     * Gets the value of the filterActions property.
     * 
     * @return
     *     possible object is
     *     {@link FilterRule.FilterActions }
     *     
     */
    public FilterRule.FilterActions getFilterActions() {
        return filterActions;
    }

    /**
     * Sets the value of the filterActions property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterRule.FilterActions }
     *     
     */
    public void setFilterActions(FilterRule.FilterActions value) {
        this.filterActions = value;
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
     * Gets the value of the active property.
     * 
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     */
    public void setActive(boolean value) {
        this.active = value;
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
     *         &lt;choice maxOccurs="unbounded" minOccurs="0">
     *           &lt;element name="actionKeep" type="{urn:zimbraMail}filterActionKeep"/>
     *           &lt;element name="actionDiscard" type="{urn:zimbraMail}filterActionDiscard"/>
     *           &lt;element name="actionFileInto" type="{urn:zimbraMail}filterActionFileInto"/>
     *           &lt;element name="actionFlag" type="{urn:zimbraMail}filterActionFlag"/>
     *           &lt;element name="actionTag" type="{urn:zimbraMail}filterActionTag"/>
     *           &lt;element name="actionRedirect" type="{urn:zimbraMail}filterActionRedirect"/>
     *           &lt;element name="actionReply" type="{urn:zimbraMail}filterActionReply"/>
     *           &lt;element name="actionNotify" type="{urn:zimbraMail}filterActionNotify"/>
     *           &lt;element name="actionStop" type="{urn:zimbraMail}filterActionStop"/>
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
    @XmlType(name = "", propOrder = {
        "actionKeepOrActionDiscardOrActionFileInto"
    })
    public static class FilterActions {

        @XmlElements({
            @XmlElement(name = "actionStop", type = FilterActionStop.class),
            @XmlElement(name = "actionKeep", type = FilterActionKeep.class),
            @XmlElement(name = "actionTag", type = FilterActionTag.class),
            @XmlElement(name = "actionRedirect", type = FilterActionRedirect.class),
            @XmlElement(name = "actionDiscard", type = FilterActionDiscard.class),
            @XmlElement(name = "actionFileInto", type = FilterActionFileInto.class),
            @XmlElement(name = "actionNotify", type = FilterActionNotify.class),
            @XmlElement(name = "actionFlag", type = FilterActionFlag.class),
            @XmlElement(name = "actionReply", type = FilterActionReply.class)
        })
        protected List<Object> actionKeepOrActionDiscardOrActionFileInto;

        /**
         * Gets the value of the actionKeepOrActionDiscardOrActionFileInto property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the actionKeepOrActionDiscardOrActionFileInto property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getActionKeepOrActionDiscardOrActionFileInto().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FilterActionStop }
         * {@link FilterActionKeep }
         * {@link FilterActionTag }
         * {@link FilterActionRedirect }
         * {@link FilterActionDiscard }
         * {@link FilterActionFileInto }
         * {@link FilterActionNotify }
         * {@link FilterActionFlag }
         * {@link FilterActionReply }
         * 
         * 
         */
        public List<Object> getActionKeepOrActionDiscardOrActionFileInto() {
            if (actionKeepOrActionDiscardOrActionFileInto == null) {
                actionKeepOrActionDiscardOrActionFileInto = new ArrayList<Object>();
            }
            return this.actionKeepOrActionDiscardOrActionFileInto;
        }

    }

}
