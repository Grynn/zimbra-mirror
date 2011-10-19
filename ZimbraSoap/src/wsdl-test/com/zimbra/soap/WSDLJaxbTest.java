/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.soap;

import java.io.ByteArrayOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;

import generated.zcsclient.mail.testActionSelector;
import generated.zcsclient.mail.testContactActionSelector;
import generated.zcsclient.mail.testConvActionRequest;
import generated.zcsclient.mail.testFolderActionSelector;
import generated.zcsclient.mail.testNoteActionSelector;

/**
 * Unit test for {@link GetInfoResponse} which exercises
 * translation to and from Element
 *
 * @author Gren Elliot
 */
public class WSDLJaxbTest {
    private static final Logger LOG = Logger.getLogger(WSDLJaxbTest.class);

    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        LOG.setLevel(Level.INFO);
    }

    @BeforeClass
    public static void init() throws Exception {
        // JAXBContext jaxb = JAXBContext.newInstance(GetInfoResponse.class);
        // unmarshaller = jaxb.createUnmarshaller();
        // getInfoResp = (GetInfoResponse) unmarshaller.unmarshal(
        //     JaxbToElementTest.class.getResourceAsStream("GetInfoResponse.xml"));
    }

    /**
     * Explore handling of Jaxb classes which specify an @XmlElement with
     * a super class.  How do subclasses get treated with this?
     */
    @Test
    public void ConvActionRequestJaxbSubclassHandlingTest() throws Exception {
        testFolderActionSelector fas = new testFolderActionSelector();
        fas.setId("ids");
        fas.setOp("op");
        fas.setL("folder");
        fas.setRecursive(true);
        fas.setUrl("http://url");
        testConvActionRequest car = new testConvActionRequest();
        car.setAction(fas);

        Class<?> ctxClasses[] = new Class<?>[] {
            testConvActionRequest.class };
        JAXBContext jaxb = JAXBContext.newInstance(ctxClasses);
        Marshaller marshaller = jaxb.createMarshaller();
        DOMResult domRes = new DOMResult();
        // Specifying namespace in QName seems to cause problems,
        // however, correct namespace seems to get there, presumably
        // from package-info
        JAXBElement<testConvActionRequest> jbe = new JAXBElement <testConvActionRequest>(
                new QName("ConvActionRequest"),
                testConvActionRequest.class, car);
        marshaller.marshal(jbe, domRes);
        // marshaller.marshal(car, domRes); 
        Node docNode = domRes.getNode();
        String eXml = domToString((org.w3c.dom.Document) docNode);
        LOG.info("ConvActionRequestJaxbSubclassHandling: marshalled XML=" +
                eXml);
        Assert.assertTrue("Xml should contain recursive attribute",
                eXml.contains("recursive=\"true\""));
        Unmarshaller unmarshaller = jaxb.createUnmarshaller();
        org.w3c.dom.Document doc = toW3cDom(eXml);
        jbe = unmarshaller.unmarshal(doc, testConvActionRequest.class);
        car = jbe.getValue();
        testActionSelector as = car.getAction();
        Assert.assertEquals("Folder attribute value",
                    "folder", as.getL());
        if (as instanceof testFolderActionSelector) {
            fas = (testFolderActionSelector)as;
            Assert.assertEquals("Url attribute value",
                        "http://url", fas.getUrl());
        } else if (as instanceof testNoteActionSelector) {
            Assert.fail("got a NoteActionSelector");
        } else if (as instanceof testContactActionSelector) {
            Assert.fail("got a ContactActionSelector");
        } else {
            Assert.fail("Failed to get back a FolderActionSelector");
        }
    }

    public static org.w3c.dom.Document toW3cDom(String xml)
    throws org.xml.sax.SAXException, java.io.IOException {
        return toW3cDom(new java.io.ByteArrayInputStream(xml.getBytes()));
    }

    public static org.w3c.dom.Document toW3cDom(java.io.InputStream is) 
        throws org.xml.sax.SAXException, java.io.IOException {
        javax.xml.parsers.DocumentBuilderFactory factory =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        }
        catch (javax.xml.parsers.ParserConfigurationException ex) {
        }  
        org.w3c.dom.Document doc = builder.parse(is);
        is.close();
        return doc;
    }

    public static String domToString(org.w3c.dom.Document document) {
        try {
            Source xmlSource = new DOMSource(document);
            StreamResult result = new StreamResult(new ByteArrayOutputStream());
            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes"); //Java XML Indent
            transformer.transform(xmlSource, result);
            return result.getOutputStream().toString();
        } catch (TransformerFactoryConfigurationError factoryError) {
            LOG.error("Error creating TransformerFactory", factoryError);
        } catch (TransformerException transformerError) {
            LOG.error( "Error transforming document", transformerError);
        }
        return null;
    }

}
