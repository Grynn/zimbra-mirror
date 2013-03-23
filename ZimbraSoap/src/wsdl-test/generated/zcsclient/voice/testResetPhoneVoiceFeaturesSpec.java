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
 * <p>Java class for resetPhoneVoiceFeaturesSpec complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resetPhoneVoiceFeaturesSpec">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
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
@XmlType(name = "resetPhoneVoiceFeaturesSpec", propOrder = {
    "anoncallrejectionOrCalleridblockingOrCallforward"
})
public class testResetPhoneVoiceFeaturesSpec {

    @XmlElements({
        @XmlElement(name = "selectivecallacceptance", type = testSelectiveCallAcceptanceReq.class),
        @XmlElement(name = "selectivecallforward", type = testSelectiveCallForwardReq.class),
        @XmlElement(name = "anoncallrejection", type = testAnonCallRejectionReq.class),
        @XmlElement(name = "selectivecallrejection", type = testSelectiveCallRejectionReq.class),
        @XmlElement(name = "callforwardnoanswer", type = testCallForwardNoAnswerReq.class),
        @XmlElement(name = "calleridblocking", type = testCallerIdBlockingReq.class),
        @XmlElement(name = "callwaiting", type = testCallWaitingReq.class),
        @XmlElement(name = "callforwardbusyline", type = testCallForwardBusyLineReq.class),
        @XmlElement(name = "callforward", type = testCallForwardReq.class)
    })
    protected List<Object> anoncallrejectionOrCalleridblockingOrCallforward;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the anoncallrejectionOrCalleridblockingOrCallforward property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the anoncallrejectionOrCalleridblockingOrCallforward property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnoncallrejectionOrCalleridblockingOrCallforward().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link testSelectiveCallAcceptanceReq }
     * {@link testSelectiveCallForwardReq }
     * {@link testAnonCallRejectionReq }
     * {@link testSelectiveCallRejectionReq }
     * {@link testCallForwardNoAnswerReq }
     * {@link testCallerIdBlockingReq }
     * {@link testCallWaitingReq }
     * {@link testCallForwardBusyLineReq }
     * {@link testCallForwardReq }
     * 
     * 
     */
    public List<Object> getAnoncallrejectionOrCalleridblockingOrCallforward() {
        if (anoncallrejectionOrCalleridblockingOrCallforward == null) {
            anoncallrejectionOrCalleridblockingOrCallforward = new ArrayList<Object>();
        }
        return this.anoncallrejectionOrCalleridblockingOrCallforward;
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
