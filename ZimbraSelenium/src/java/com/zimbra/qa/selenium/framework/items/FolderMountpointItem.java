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
/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.*;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.*;


/**
 * @author Matt Rhoades
 *
 */
public class FolderMountpointItem extends FolderItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	/**
	 * Remote folder ID
	 */
	protected String rid = null;
	
	/**
	 * Remote folder name
	 */
	protected String oname = null;
	
	/**
	 * Remote account id
	 */
	protected String zid = null;
	
	/**
	 * Remote account name
	 */
	protected String owner = null;

	/**
	 * Permissions
	 */
	protected String perm = null;
	

	/**
	 * Create a new FolderMountpointItem object
	 */
	public FolderMountpointItem() {
	}



	
	/**
	 * Import a FolderMountpointItem specified in a GetFolderResponse
	 * <br>
	 * The GetFolderResponse should only contain a single <folder/> element
	 * @param response
	 * @return
	 * @throws HarnessException
	 */
	public static FolderMountpointItem importFromSOAP(Element response) throws HarnessException {
		if ( response == null )
			throw new HarnessException("Element cannot be null");

		// TODO: can the ZimbraSOAP methods be used to convert this response to item?
		
		// Example response:
		//	        <GetFolderResponse xmlns="urn:zimbraMail">
	    //				<link id="257" rev="2" s="198" rid="257" oname="folder12986731211244" zid="e39a1429-cf7e-466d-8a69-4f23417d2ae2" name="mountpoint12986731211246" ms="2" owner="enus12986731211247@testdomain.com" n="1" l="1" perm="rw"/>
	    //			</GetFolderResponse>

		
		logger.debug("importFromSOAP("+ response.prettyPrint() +")");
		
		Element fElement = ZimbraAccount.SoapClient.selectNode(response, "//mail:link");
		if ( fElement == null )
			throw new HarnessException("response did not contain folder "+ response.prettyPrint());
		
		FolderMountpointItem item = null;
		
		try {
			
			item = new FolderMountpointItem();
			item.setId(fElement.getAttribute("id"));
			item.setName(fElement.getAttribute("name"));
			item.setParentId(fElement.getAttribute("l"));
			
			item.oname	= fElement.getAttribute("oname");
			item.owner 	= fElement.getAttribute("owner");
			item.perm 	= fElement.getAttribute("perm");
			item.zid 	= fElement.getAttribute("zid");
			item.rid 	= fElement.getAttribute("rid");
			
			return (item);
			
		} catch (NumberFormatException e) {
			throw new HarnessException("Unable to create FolderMountpointItem", e);
		} catch (ServiceException e) {
			throw new HarnessException("Unable to create FolderMountpointItem", e);
		} finally {
			if ( item != null )	logger.info(item.prettyPrint());
		}
	}


	public static FolderMountpointItem importFromSOAP(ZimbraAccount account, String name) throws HarnessException {
		logger.debug("importFromSOAP("+ account.EmailAddress +", "+ name +")");
		
		// Get all the folders
		account.soapSend("<GetFolderRequest xmlns='urn:zimbraMail'/>");
		String id = account.soapSelectValue("//mail:link[@name='"+ name +"']", "id");

		if (id == null) {
			throw new HarnessException("Link with name: " + name + " is not found...");
		}

		// Get just the folder specified
		account.soapSend(
				"<GetFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder l='"+ id +"'/>" +
				"</GetFolderRequest>");
		Element response = account.soapSelectNode("//mail:GetFolderResponse", 1);
				
		return (importFromSOAP(response));
	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.prettyPrint());
		sb.append("Owner: ").append(owner).append('\n');
		sb.append("Owner ID: ").append(zid).append('\n');
		sb.append("Remote Folder name: ").append(oname).append('\n');
		sb.append("Remote Folder ID: ").append(rid).append('\n');
		sb.append("Permissions: ").append(perm).append('\n');
		return (sb.toString());
	}

}
