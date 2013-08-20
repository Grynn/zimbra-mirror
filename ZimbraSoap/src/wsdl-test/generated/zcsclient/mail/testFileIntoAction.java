/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package generated.zcsclient.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fileIntoAction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fileIntoAction">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:zimbraMail}filterAction">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="folderPath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileIntoAction")
public class testFileIntoAction
    extends testFilterAction
{

    @XmlAttribute(name = "folderPath")
    protected String folderPath;

    /**
     * Gets the value of the folderPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderPath() {
        return folderPath;
    }

    /**
     * Sets the value of the folderPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderPath(String value) {
        this.folderPath = value;
    }

}
