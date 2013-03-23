/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

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
 * <p>Java class for phoneVoiceFeaturesInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="phoneVoiceFeaturesInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="voicemailprefs" type="{urn:zimbraVoice}voiceMailPrefsFeature"/>
 *           &lt;element name="anoncallrejection" type="{urn:zimbraVoice}anonCallRejectionFeature"/>
 *           &lt;element name="calleridblocking" type="{urn:zimbraVoice}callerIdBlockingFeature"/>
 *           &lt;element name="callforward" type="{urn:zimbraVoice}callForwardFeature"/>
 *           &lt;element name="callforwardbusyline" type="{urn:zimbraVoice}callForwardBusyLineFeature"/>
 *           &lt;element name="callforwardnoanswer" type="{urn:zimbraVoice}callForwardNoAnswerFeature"/>
 *           &lt;element name="callwaiting" type="{urn:zimbraVoice}callWaitingFeature"/>
 *           &lt;element name="selectivecallforward" type="{urn:zimbraVoice}selectiveCallForwardFeature"/>
 *           &lt;element name="selectivecallacceptance" type="{urn:zimbraVoice}selectiveCallAcceptanceFeature"/>
 *           &lt;element name="selectivecallrejection" type="{urn:zimbraVoice}selectiveCallRejectionFeature"/>
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
@XmlType(name = "phoneVoiceFeaturesInfo", propOrder = {
    "voicemailprefsOrAnoncallrejectionOrCalleridblocking"
})
public class testPhoneVoiceFeaturesInfo {

    @XmlElements({
        @XmlElement(name = "selectivecallrejection", type = testSelectiveCallRejectionFeature.class),
        @XmlElement(name = "callforward", type = testCallForwardFeature.class),
        @XmlElement(name = "callwaiting", type = testCallWaitingFeature.class),
        @XmlElement(name = "callforwardbusyline", type = testCallForwardBusyLineFeature.class),
        @XmlElement(name = "anoncallrejection", type = testAnonCallRejectionFeature.class),
        @XmlElement(name = "calleridblocking", type = testCallerIdBlockingFeature.class),
        @XmlElement(name = "selectivecallacceptance", type = testSelectiveCallAcceptanceFeature.class),
        @XmlElement(name = "voicemailprefs", type = testVoiceMailPrefsFeature.class),
        @XmlElement(name = "callforwardnoanswer", type = testCallForwardNoAnswerFeature.class),
        @XmlElement(name = "selectivecallforward", type = testSelectiveCallForwardFeature.class)
    })
    protected List<testCallFeatureInfo> voicemailprefsOrAnoncallrejectionOrCalleridblocking;
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
     * {@link testSelectiveCallRejectionFeature }
     * {@link testCallForwardFeature }
     * {@link testCallWaitingFeature }
     * {@link testCallForwardBusyLineFeature }
     * {@link testAnonCallRejectionFeature }
     * {@link testCallerIdBlockingFeature }
     * {@link testSelectiveCallAcceptanceFeature }
     * {@link testVoiceMailPrefsFeature }
     * {@link testCallForwardNoAnswerFeature }
     * {@link testSelectiveCallForwardFeature }
     * 
     * 
     */
    public List<testCallFeatureInfo> getVoicemailprefsOrAnoncallrejectionOrCalleridblocking() {
        if (voicemailprefsOrAnoncallrejectionOrCalleridblocking == null) {
            voicemailprefsOrAnoncallrejectionOrCalleridblocking = new ArrayList<testCallFeatureInfo>();
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
