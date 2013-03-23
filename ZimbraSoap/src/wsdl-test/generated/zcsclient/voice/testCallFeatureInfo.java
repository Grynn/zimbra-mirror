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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import generated.zcsclient.zm.testTrueOrFalse;


/**
 * <p>Java class for callFeatureInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="callFeatureInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="s" use="required" type="{urn:zimbra}trueOrFalse" />
 *       &lt;attribute name="a" use="required" type="{urn:zimbra}trueOrFalse" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "callFeatureInfo")
@XmlSeeAlso({
    testAnonCallRejectionFeature.class,
    testCallerIdBlockingFeature.class,
    testFeatureWithCallerList.class,
    testVoiceMailPrefsFeature.class,
    testCallForwardBusyLineFeature.class,
    testCallWaitingFeature.class,
    testCallForwardNoAnswerFeature.class,
    testCallForwardFeature.class
})
public abstract class testCallFeatureInfo {

    @XmlAttribute(name = "s", required = true)
    protected testTrueOrFalse s;
    @XmlAttribute(name = "a", required = true)
    protected testTrueOrFalse a;

    /**
     * Gets the value of the s property.
     * 
     * @return
     *     possible object is
     *     {@link testTrueOrFalse }
     *     
     */
    public testTrueOrFalse getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTrueOrFalse }
     *     
     */
    public void setS(testTrueOrFalse value) {
        this.s = value;
    }

    /**
     * Gets the value of the a property.
     * 
     * @return
     *     possible object is
     *     {@link testTrueOrFalse }
     *     
     */
    public testTrueOrFalse getA() {
        return a;
    }

    /**
     * Sets the value of the a property.
     * 
     * @param value
     *     allowed object is
     *     {@link testTrueOrFalse }
     *     
     */
    public void setA(testTrueOrFalse value) {
        this.a = value;
    }

}
