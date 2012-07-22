
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
 * <p>Java class for phoneVoiceFeaturesSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="phoneVoiceFeaturesSpec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="voicemailprefs" type="{urn:zimbraVoice}voiceMailPrefsReq"/>
 *           &lt;element name="anoncallrejection" type="{urn:zimbraVoice}anonCallRejectionReq"/>
 *           &lt;element name="calleridblocking" type="{urn:zimbraVoice}callerIdBlockingReq"/>
 *           &lt;element name="callforward" type="{urn:zimbraVoice}callForwardReq"/>
 *           &lt;element name="callforwardbusyline" type="{urn:zimbraVoice}callForwardBusyLineReq"/>
 *           &lt;element name="callforwardnoanswer" type="{urn:zimbraVoice}callForwardNoAnswerReq"/>
 *           &lt;element name="callwaiting" type="{urn:zimbraVoice}callWaitingReq"/>
 *           &lt;element name="selectivecallforward" type="{urn:zimbraVoice}selectiveCallForwardReq"/>
 *           &lt;element name="selectivecallacceptance" type="{urn:zimbraVoice}selectiveCallAcceptanceReq"/>
 *           &lt;element name="selectivecallrejection" type="{urn:zimbraVoice}selectiveCallRejectionReq"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "phoneVoiceFeaturesSpec", propOrder = {
    "voicemailprefsOrAnoncallrejectionOrCalleridblocking"
})
public class testPhoneVoiceFeaturesSpec {

    @XmlElements({
        @XmlElement(name = "selectivecallforward", type = testSelectiveCallForwardReq.class),
        @XmlElement(name = "callforward", type = testCallForwardReq.class),
        @XmlElement(name = "selectivecallrejection", type = testSelectiveCallRejectionReq.class),
        @XmlElement(name = "calleridblocking", type = testCallerIdBlockingReq.class),
        @XmlElement(name = "callforwardnoanswer", type = testCallForwardNoAnswerReq.class),
        @XmlElement(name = "callwaiting", type = testCallWaitingReq.class),
        @XmlElement(name = "callforwardbusyline", type = testCallForwardBusyLineReq.class),
        @XmlElement(name = "selectivecallacceptance", type = testSelectiveCallAcceptanceReq.class),
        @XmlElement(name = "voicemailprefs", type = testVoiceMailPrefsReq.class),
        @XmlElement(name = "anoncallrejection", type = testAnonCallRejectionReq.class)
    })
    protected List<Object> voicemailprefsOrAnoncallrejectionOrCalleridblocking;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the voicemailprefsOrAnoncallrejectionOrCalleridblocking property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the voicemailprefsOrAnoncallrejectionOrCalleridblocking property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVoicemailprefsOrAnoncallrejectionOrCalleridblocking().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testSelectiveCallForwardReq }
     * {@link testCallForwardReq }
     * {@link testSelectiveCallRejectionReq }
     * {@link testCallerIdBlockingReq }
     * {@link testCallForwardNoAnswerReq }
     * {@link testCallWaitingReq }
     * {@link testCallForwardBusyLineReq }
     * {@link testSelectiveCallAcceptanceReq }
     * {@link testVoiceMailPrefsReq }
     * {@link testAnonCallRejectionReq }
     * 
     * 
     */
    public List<Object> getVoicemailprefsOrAnoncallrejectionOrCalleridblocking() {
        if (voicemailprefsOrAnoncallrejectionOrCalleridblocking == null) {
            voicemailprefsOrAnoncallrejectionOrCalleridblocking = new ArrayList<Object>();
        }
        return this.voicemailprefsOrAnoncallrejectionOrCalleridblocking;
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

}
