/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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

package generated.zcsclient.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for adminAttrsImpl complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adminAttrsImpl">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:zimbraAdmin}a" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adminAttrsImpl", propOrder = {
    "a"
})
@XmlSeeAlso({
    testCreateUCServiceRequest.class,
    testUpdatePresenceSessionIdRequest.class,
    testCreateZimletRequest.class,
    testCreateCalendarResourceRequest.class,
    testModifyDataSourceRequest.class,
    testCreateCosRequest.class,
    testCreateLDAPEntryRequest.class,
    testCheckAuthConfigRequest.class,
    testCreateAccountRequest.class,
    testCreateDistributionListRequest.class,
    testCreateServerRequest.class,
    testModifyCalendarResourceRequest.class,
    testModifyAccountRequest.class,
    testDeleteDataSourceRequest.class,
    testModifyDomainRequest.class,
    testModifyUCServiceRequest.class,
    testCreateDomainRequest.class,
    testCheckRightRequest.class,
    testModifyCosRequest.class,
    testAddGalSyncDataSourceRequest.class,
    testModifyLDAPEntryRequest.class,
    testGetDataSourcesRequest.class,
    testCheckGalConfigRequest.class,
    testGetAllConfigResponse.class,
    testModifyConfigRequest.class,
    testCreateGalSyncAccountRequest.class,
    testGetConfigResponse.class,
    testModifyDistributionListRequest.class,
    testGetDistributionListRequest.class,
    testModifyServerRequest.class,
    testArchiveSpec.class,
    testDataSourceSpecifier.class,
    testLdapEntryInfo.class,
    testSmimeConfigInfo.class,
    testDataSourceInfo.class,
    testGalContactInfo.class,
    testXmppComponentSpec.class,
    testNetworkInformation.class,
    testSmimeConfigModifications.class,
    testSearchNode.class,
    testXmppComponentInfo.class
})
public class testAdminAttrsImpl {

    protected List<testAttr> a;

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
     * {@link testAttr }
     * 
     * 
     */
    public List<testAttr> getA() {
        if (a == null) {
            a = new ArrayList<testAttr>();
        }
        return this.a;
    }

}
