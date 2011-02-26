/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.*;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;


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

		// TODO: can the ZimbraSOAP methods be used to convert this response to item?
		
		// Example response:
		//	        <GetFolderResponse xmlns="urn:zimbraMail">
	    //				<link id="257" rev="2" s="198" rid="257" oname="folder12986731211244" zid="e39a1429-cf7e-466d-8a69-4f23417d2ae2" name="mountpoint12986731211246" ms="2" owner="enus12986731211247@testdomain.com" n="1" l="1" perm="rw"/>
	    //			</GetFolderResponse>

		if ( response == null )
			throw new HarnessException("response was null");
		
		
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
	   return importFromSOAP(account, name, SOAP_DESTINATION_HOST_TYPE.SERVER, null);
	}

	/**
	 * Import a folder by name
	 * @param account
	 * @param name Folder name to be imported
	 * @param destType Destination Host Type: CLIENT or SERVER
	 * @param accountName Account Name to be added in SOAP context while importing
	 * @return (FolderMountpointItem)
	 * @throws HarnessException
	 */
	public static FolderMountpointItem importFromSOAP(ZimbraAccount account, String name,
	      SOAP_DESTINATION_HOST_TYPE destType, String accountName) throws HarnessException {
		logger.debug("importFromSOAP("+ account.EmailAddress +", "+ name +")");
		
		// Get all the folders
		account.soapSend("<GetFolderRequest xmlns='urn:zimbraMail'/>", destType, accountName);
		String id = account.soapSelectValue("//mail:link[@name='"+ name +"']", "id");

		if (id == null) {
			throw new HarnessException("Link with name: " + name + " is not found...");
		}

		// Get just the folder specified
		account.soapSend(
				"<GetFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder l='"+ id +"'/>" +
				"</GetFolderRequest>",
				destType, accountName);
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
