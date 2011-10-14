
package zimbra.generated.accountclient.mail;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import zimbra.generated.accountclient.zm.testSimpleSearchHit;
import zimbra.generated.accountclient.zm.testWildcardExpansionQueryInfo;


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
 *                     &lt;element name="suggest" type="{urn:zimbraMail}spellingSuggestionsQueryInfo"/>
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
public class testSearchResponse {

    @XmlElements({
        @XmlElement(name = "doc", type = testDocumentHitInfo.class),
        @XmlElement(name = "mp", type = testMessagePartHitInfo.class),
        @XmlElement(name = "chat", type = testChatHitInfo.class),
        @XmlElement(name = "c", type = testConversationHitInfo.class),
        @XmlElement(name = "hit", type = testSimpleSearchHit.class),
        @XmlElement(name = "task", type = testTaskHitInfo.class),
        @XmlElement(name = "m", type = testMessageHitInfo.class),
        @XmlElement(name = "note", type = testNoteHitInfo.class),
        @XmlElement(name = "appt", type = testAppointmentHitInfo.class),
        @XmlElement(name = "cn", type = testContactInfo.class),
        @XmlElement(name = "w", type = testWikiHitInfo.class)
    })
    protected List<Object> hitOrCOrM;
    protected testSearchResponse.Info info;
    @XmlAttribute(name = "sortBy")
    protected String sortBy;
    @XmlAttribute(name = "offset")
    protected Integer offset;
    @XmlAttribute(name = "more")
    protected Boolean more;
    @XmlAttribute(name = "total")
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
     * {@link testDocumentHitInfo }
     * {@link testMessagePartHitInfo }
     * {@link testChatHitInfo }
     * {@link testConversationHitInfo }
     * {@link testSimpleSearchHit }
     * {@link testTaskHitInfo }
     * {@link testMessageHitInfo }
     * {@link testNoteHitInfo }
     * {@link testAppointmentHitInfo }
     * {@link testContactInfo }
     * {@link testWikiHitInfo }
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
     *     {@link testSearchResponse.Info }
     *     
     */
    public testSearchResponse.Info getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     * 
     * @param value
     *     allowed object is
     *     {@link testSearchResponse.Info }
     *     
     */
    public void setInfo(testSearchResponse.Info value) {
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
     *           &lt;element name="suggest" type="{urn:zimbraMail}spellingSuggestionsQueryInfo"/>
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
        "suggestOrWildcard"
    })
    public static class Info {

        @XmlElements({
            @XmlElement(name = "suggest", type = String.class),
            @XmlElement(name = "wildcard", type = testWildcardExpansionQueryInfo.class)
        })
        protected List<Object> suggestOrWildcard;

        /**
         * Gets the value of the suggestOrWildcard property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the suggestOrWildcard property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSuggestOrWildcard().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * {@link testWildcardExpansionQueryInfo }
         * 
         * 
         */
        public List<Object> getSuggestOrWildcard() {
            if (suggestOrWildcard == null) {
                suggestOrWildcard = new ArrayList<Object>();
            }
            return this.suggestOrWildcard;
        }

    }

}
