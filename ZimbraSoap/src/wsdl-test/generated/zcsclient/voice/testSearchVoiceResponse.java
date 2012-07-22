
package generated.zcsclient.voice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for searchVoiceResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchVoiceResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vfi" type="{urn:zimbraVoice}voiceFolderSummary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="vm" type="{urn:zimbraVoice}voiceMailItem"/>
 *           &lt;element name="cl" type="{urn:zimbraVoice}callLogItem"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="sortBy" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="offset" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="more" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchVoiceResponse", propOrder = {
    "vfi",
    "vmOrCl"
})
public class testSearchVoiceResponse {

    protected List<testVoiceFolderSummary> vfi;
    @XmlElements({
        @XmlElement(name = "cl", type = testCallLogItem.class),
        @XmlElement(name = "vm", type = testVoiceMailItem.class)
    })
    protected List<testVoiceCallItem> vmOrCl;
    @XmlAttribute(name = "sortBy", required = true)
    protected String sortBy;
    @XmlAttribute(name = "offset", required = true)
    protected int offset;
    @XmlAttribute(name = "more", required = true)
    protected boolean more;

    /**
     * Gets the value of the vfi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vfi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVfi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testVoiceFolderSummary }
     * 
     * 
     */
    public List<testVoiceFolderSummary> getVfi() {
        if (vfi == null) {
            vfi = new ArrayList<testVoiceFolderSummary>();
        }
        return this.vfi;
    }

    /**
     * Gets the value of the vmOrCl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vmOrCl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVmOrCl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testCallLogItem }
     * {@link testVoiceMailItem }
     * 
     * 
     */
    public List<testVoiceCallItem> getVmOrCl() {
        if (vmOrCl == null) {
            vmOrCl = new ArrayList<testVoiceCallItem>();
        }
        return this.vmOrCl;
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
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets the value of the offset property.
     * 
     */
    public void setOffset(int value) {
        this.offset = value;
    }

    /**
     * Gets the value of the more property.
     * 
     */
    public boolean isMore() {
        return more;
    }

    /**
     * Sets the value of the more property.
     * 
     */
    public void setMore(boolean value) {
        this.more = value;
    }

}
