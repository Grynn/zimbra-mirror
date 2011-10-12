/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.soap.admin;

import java.io.File;
import java.util.List;

import com.sun.xml.ws.developer.WSBindingProvider;

import zimbra.generated.adminclient.admin.*;
import zimbra.generated.adminclient.ws.service.AdminService;

import com.zimbra.soap.Utility;

import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLVolumeAdminTest {

    // The AdminService interface is the Java type bound to
    // the portType section of the WSDL document.
    private final static String testVolume = "wsdlTestVolume";
    private final static String testVolumePath = "/opt/zimbra/wsdlTestVolume";
    private static AdminService eif = null;

    @BeforeClass
    public static void init() throws Exception {
        Utility.setUpToAcceptAllHttpsServerCerts();
        eif = Utility.getAdminSvcEIF();
        oneTimeTearDown();
    }

    @AfterClass
    public static void oneTimeTearDown() throws Exception {
        // one-time cleanup code
        Utility.deleteVolumeIfExists(testVolume);
        try {
            new File(testVolumePath).deleteOnExit();
        } catch (Exception ex) {
            System.err.println("Exception " + ex.toString() + 
            " thrown inside oneTimeTearDown");
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createVolumeTest() throws Exception {
        Utility.deleteVolumeIfExists(testVolume);
        Utility.ensureVolumeExists(testVolume, testVolumePath);
    }

    @Test
    public void getAllVolumesTest() throws Exception {
        testGetAllVolumesRequest req = new testGetAllVolumesRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetAllVolumesResponse resp = eif.getAllVolumesRequest(req);
        Assert.assertNotNull("GetAllVolumesResponse object", resp);
        List <testVolumeInfo> volumes = resp.getVolume();
        Assert.assertNotNull("GetAllVolumesResponse list of volumes", volumes);
        int len = volumes.size();
        Assert.assertTrue("Number of GetAllVolumesResponse children is " +
                len + " - should be at least 2", len >= 2);
        testVolumeInfo firstVolume = volumes.get(0);
        short id = firstVolume.getId();
        Assert.assertTrue("First Volume id is " +
                id + " - should be at least 1", id >= 1);
        Assert.assertNotNull("First Volume rootpath", firstVolume.getRootpath());
        Assert.assertEquals("First Volume rootpath 1st char", "/", firstVolume.getRootpath().substring(0, 1));
        long compressionThreshold = firstVolume.getCompressionThreshold();
        Assert.assertTrue("First Volume compressionThreshold is " +
                compressionThreshold + " - should be at least 256",
                compressionThreshold >= 256);
        Assert.assertNotNull("First Volume name", firstVolume.getName());
        firstVolume.isCompressBlobs();  // Just making sure it is there
        Assert.assertNotNull("First Volume isCurrent", firstVolume.isIsCurrent());
        short volType = firstVolume.getType();
        // TODO - tighter check here?
        Assert.assertTrue("First Volume type is " +
                volType + " - should be at least 1", volType >= 1);
        Short mgbits = firstVolume.getMgbits();
        Assert.assertTrue("First Volume mgbits is " +
                mgbits + " - should be at least 4", mgbits >= 4);
        Short mbits = firstVolume.getMbits();
        Assert.assertTrue("First Volume mbits is " +
                mbits + " - should be at least 4", mbits >= 4);
        Short fgbits = firstVolume.getFgbits();
        Assert.assertTrue("First Volume fgbits is " +
                fgbits + " - should be at least 4", fgbits >= 4);
        Short fbits = firstVolume.getFbits();
        Assert.assertTrue("First Volume fbits is " +
                fbits + " - should be at least 4", fbits >= 4);
    }

    @Test
    public void getCurrentVolumesTest() throws Exception {
        testGetCurrentVolumesRequest req = new testGetCurrentVolumesRequest();
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetCurrentVolumesResponse resp = eif.getCurrentVolumesRequest(req);
        Assert.assertNotNull("GetCurrentVolumesResponse object", resp);
        List<testCurrentVolumeInfo> volumes = resp.getVolume();
        Assert.assertNotNull("GetCurrentVolumesResponse list of volumes", volumes);
        int len = volumes.size();
        Assert.assertTrue("Number of GetCurrentVolumesResponse children is " +
                len + " - should be at least 2", len >= 2);
        testCurrentVolumeInfo firstVolume = volumes.get(0);
        short id = firstVolume.getId();
        Assert.assertTrue("First Volume id is " +
                id + " - should be at least 1", id >= 1);
        firstVolume.getType();
    }

    @Test
    public void modifyVolumeTest() throws Exception {
        Short volId = Utility.ensureVolumeExists(testVolume, testVolumePath);
        testGetVolumeRequest req = new testGetVolumeRequest();
        req.setId(volId);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetVolumeResponse resp = eif.getVolumeRequest(req);
        Assert.assertNotNull(resp);
        testVolumeInfo volumeInfo = resp.getVolume();
        volumeInfo.setCompressBlobs(false);
        testModifyVolumeRequest modReq = new testModifyVolumeRequest();
        modReq.setId(volId);
        modReq.setVolume(volumeInfo);
        testModifyVolumeResponse modResp = eif.modifyVolumeRequest(modReq);
        Assert.assertNotNull("ModifyVolumeResponse object", modResp);
    }

    // TODO:Don't know how to delete a volume once it has been made current
    //      If discover a method, then can re-enable this - but will want
    //      cleanup for it.
    // @Test
    public void setCurrentVolume() throws Exception {
        Short volId = Utility.ensureVolumeExists(testVolume, testVolumePath);
        testGetVolumeRequest req = new testGetVolumeRequest();
        req.setId(volId);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetVolumeResponse resp = eif.getVolumeRequest(req);
        Assert.assertNotNull(resp);
        testVolumeInfo volumeInfo = resp.getVolume();
        volumeInfo.setCompressBlobs(false);
        testSetCurrentVolumeRequest setReq = new testSetCurrentVolumeRequest();
        setReq.setId(volId);
        setReq.setType(volumeInfo.getType());
        testSetCurrentVolumeResponse setResp = eif.setCurrentVolumeRequest(setReq);
        Assert.assertNotNull("SetCurrentVolumeResponse object", setResp);
    }

    @Test
    public void getVolumeTest() throws Exception {
        Short volId = Utility.ensureVolumeExists(testVolume, testVolumePath);
        testGetVolumeRequest req = new testGetVolumeRequest();
        req.setId(volId);
        Utility.addSoapAdminAuthHeader((WSBindingProvider)eif);
        testGetVolumeResponse resp = eif.getVolumeRequest(req);
        Assert.assertNotNull(resp);
        testVolumeInfo volumeInfo = resp.getVolume();
        Assert.assertNotNull("<volume> object", volumeInfo);
        Assert.assertEquals("GetVolumeResponse <volume> 'name' attribute",
                testVolume, volumeInfo.getName());
        Short testVolumeId = volumeInfo.getId();
        Assert.assertEquals(
                "GetVolumeResponse <volume> 'id' attribute ",
                volId, testVolumeId);
    }

    @Test
    public void deleteVolumeTest() throws Exception {
        Short volId = Utility.ensureVolumeExists(testVolume, testVolumePath);
        testDeleteVolumeRequest delReq = new testDeleteVolumeRequest();
        delReq.setId(volId);
        testDeleteVolumeResponse delResp = eif.deleteVolumeRequest(delReq);
        Assert.assertNotNull("DeleteVolumeResponse object", delResp);
    }

}
