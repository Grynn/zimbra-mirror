
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
 * <p>Java class for searchConvResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchConvResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="c" type="{urn:zimbraMail}nestedSearchConversation" minOccurs="0"/>
 *         &lt;element name="m" type="{urn:zimbraMail}messageHitInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="info" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;choice maxOccurs="unbounded" minOccurs="0">
 *                     &lt;element name="spell" type="{urn:zimbraMail}spellingSuggestionsQueryInfo"/>
 *                     &lt;element name="wildcard" type="{urn:zimbra}wildcardExpansionQueryInfo"/>
 *                   &lt;/choice>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="sortBy" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="offset" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="more" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchConvResponse", propOrder = {
    "c",
    "m",
    "info"
})
public class SearchConvResponse {

    protected NestedSearchConversation c;
    protected List<MessageHitInfo> m;
    protected SearchConvResponse.Info info;
    @XmlAttribute
    protected String sortBy;
    @XmlAttribute
    protected Integer offset;
    @XmlAttribute
    protected Boolean more;

    /**
     * Gets the value of the c property.
     * 
     * @return
     *     possible object is
     *     {@link NestedSearchConversation }
     *     
     */
    public NestedSearchConversation getC() {
        return c;
    }

    /**
     * Sets the value of the c property.
     * 
     * @param value
     *     allowed object is
     *     {@link NestedSearchConversation }
     *     
     */
    public void setC(NestedSearchConversation value) {
        this.c = value;
    }

    /**
     * Gets the value of the m property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the m property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getM().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MessageHitInfo }
     * 
     * 
     */
    public List<MessageHitInfo> getM() {
        if (m == null) {
            m = new ArrayList<MessageHitInfo>();
        }
        return this.m;
    }

    /**
     * Gets the value of the info property.
     * 
     * @return
     *     possible object is
     *     {@link SearchConvResponse.Info }
     *     
     */
    public SearchConvResponse.Info getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchConvResponse.Info }
     *     
     */
    public void setInfo(SearchConvResponse.Info value) {
        this.info = value;
    }

    /**
     * Gets the value of the sortBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Sets the value of the sortBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortBy(String value) {
        this.sortBy = value;
    }

    /**
     * Gets the value of the offset property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * Sets the value of the offset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOffset(Integer value) {
        this.offset = value;
    }

    /**
     * Gets the value of the more property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMore() {
        return more;
    }

    /**
     * Sets the value of the more property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMore(Boolean value) {
        this.more = value;
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
     *           &lt;element name="spell" type="{urn:zimbraMail}spellingSuggestionsQueryInfo"/>
     *           &lt;element name="wildcard" type="{urn:zimbra}wildcardExpansionQueryInfo"/>
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
        "spellOrWildcard"
    })
    public static class Info {

        @XmlElements({
            @XmlElement(name = "spell", type = SpellingSuggestionsQueryInfo.class),
            @XmlElement(name = "wildcard", type = WildcardExpansionQueryInfo.class)
        })
        protected List<Object> spellOrWildcard;

        /**
         * Gets the value of the spellOrWildcard property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the spellOrWildcard property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSpellOrWildcard().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link SpellingSuggestionsQueryInfo }
         * {@link WildcardExpansionQueryInfo }
         * 
         * 
         */
        public List<Object> getSpellOrWildcard() {
            if (spellOrWildcard == null) {
                spellOrWildcard = new ArrayList<Object>();
            }
            return this.spellOrWildcard;
        }

    }

}
