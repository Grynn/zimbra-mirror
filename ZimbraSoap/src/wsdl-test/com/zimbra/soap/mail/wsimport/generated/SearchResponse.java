
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
 * <p>Java class for searchResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="hit" type="{urn:zimbra}simpleSearchHit"/>
 *           &lt;element name="c" type="{urn:zimbraMail}conversationHitInfo"/>
 *           &lt;element name="m" type="{urn:zimbraMail}messageHitInfo"/>
 *           &lt;element name="chat" type="{urn:zimbraMail}chatHitInfo"/>
 *           &lt;element name="mp" type="{urn:zimbraMail}messagePartHitInfo"/>
 *           &lt;element name="cn" type="{urn:zimbraMail}contactInfo"/>
 *           &lt;element name="note" type="{urn:zimbraMail}noteHitInfo"/>
 *           &lt;element name="doc" type="{urn:zimbraMail}documentHitInfo"/>
 *           &lt;element name="w" type="{urn:zimbraMail}wikiHitInfo"/>
 *           &lt;element name="appt" type="{urn:zimbraMail}appointmentHitInfo"/>
 *           &lt;element name="task" type="{urn:zimbraMail}taskHitInfo"/>
 *         &lt;/choice>
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
 *       &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchResponse", propOrder = {
    "hitOrCOrM",
    "info"
})
public class SearchResponse {

    @XmlElements({
        @XmlElement(name = "appt", type = AppointmentHitInfo.class),
        @XmlElement(name = "task", type = TaskHitInfo.class),
        @XmlElement(name = "w", type = WikiHitInfo.class),
        @XmlElement(name = "c", type = ConversationHitInfo.class),
        @XmlElement(name = "doc", type = DocumentHitInfo.class),
        @XmlElement(name = "mp", type = MessagePartHitInfo.class),
        @XmlElement(name = "m", type = MessageHitInfo.class),
        @XmlElement(name = "cn", type = ContactInfo.class),
        @XmlElement(name = "note", type = NoteHitInfo.class),
        @XmlElement(name = "hit", type = SimpleSearchHit.class),
        @XmlElement(name = "chat", type = ChatHitInfo.class)
    })
    protected List<Object> hitOrCOrM;
    protected SearchResponse.Info info;
    @XmlAttribute
    protected String sortBy;
    @XmlAttribute
    protected Integer offset;
    @XmlAttribute
    protected Boolean more;
    @XmlAttribute
    protected Long total;

    /**
     * Gets the value of the hitOrCOrM property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hitOrCOrM property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHitOrCOrM().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AppointmentHitInfo }
     * {@link TaskHitInfo }
     * {@link WikiHitInfo }
     * {@link ConversationHitInfo }
     * {@link DocumentHitInfo }
     * {@link MessagePartHitInfo }
     * {@link MessageHitInfo }
     * {@link ContactInfo }
     * {@link NoteHitInfo }
     * {@link SimpleSearchHit }
     * {@link ChatHitInfo }
     * 
     * 
     */
    public List<Object> getHitOrCOrM() {
        if (hitOrCOrM == null) {
            hitOrCOrM = new ArrayList<Object>();
        }
        return this.hitOrCOrM;
    }

    /**
     * Gets the value of the info property.
     * 
     * @return
     *     possible object is
     *     {@link SearchResponse.Info }
     *     
     */
    public SearchResponse.Info getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchResponse.Info }
     *     
     */
    public void setInfo(SearchResponse.Info value) {
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
     * Gets the value of the total property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTotal(Long value) {
        this.total = value;
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
