/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.soap.mail.type.Folder;


/**
 * @author Matt Rhoades
 *
 */
public class FolderItem extends com.zimbra.soap.mail.type.Folder implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	/**
	 * Logical objects that represent the default system folders
	 * @author Matt Rhoades
	 *
	 */
	public static class SystemFolder {
		public static final SystemFolder UserRoot = new SystemFolder("USER_ROOT");
		public static final SystemFolder Briefcase = new SystemFolder("Briefcase");
		public static final SystemFolder Calendar = new SystemFolder("Calendar");
		public static final SystemFolder Chats = new SystemFolder("Chats");
		public static final SystemFolder Contacts = new SystemFolder("Contacts","zti__main_Contacts__7");
		public static final SystemFolder Drafts = new SystemFolder("Drafts");
		public static final SystemFolder EmailedContacts = new SystemFolder("Emailed Contacts","zti__main_Contacts__13");
		public static final SystemFolder Inbox = new SystemFolder("Inbox");
		public static final SystemFolder Junk = new SystemFolder("Junk");
		public static final SystemFolder Sent = new SystemFolder("Sent");
		public static final SystemFolder Tasks = new SystemFolder("Tasks");
		public static final SystemFolder Trash = new SystemFolder("Trash");
				
		private String name;
		private String id;

		private SystemFolder(String foldername) {
			name = foldername;
		}

		private SystemFolder(String foldername, String id) {
			name = foldername;
			this.id=id;
		}

		public String getId(){
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SystemFolder other = (SystemFolder) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

	}
	/**
	 * Create a new FolderItem object
	 */
	public FolderItem() {
	}

	////
	// GUI methods
	////
	
	
	/**
	 * Whether or not the folder is expanded (namely whether the icon is showing expanded/collapsed)
	 * @return
	 */
	protected boolean gIsExpanded = false;
	public boolean gGetIsExpanded() {
		return (gIsExpanded);
	}

	public void gSetIsExpanded(boolean expanded) {
		gIsExpanded = expanded;
	}
	
	/**
	 * Whether or not the folder is currently selected
	 */
	protected boolean gIsSelected = false;
	public boolean gGetIsSelected() {
		return (gIsSelected);
	}
	public void gSetIsSelected(boolean selected) {
		gIsSelected = selected;
	}

	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		
		// TODO: handle all folder properties, not just name and parent
		
		// TODO: Maybe use JaxbUtil to create it?
		
		account.soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ super.getName() +"' l='"+ super.getParentId() +"'/>" +
                "</CreateFolderRequest>");
		
		Element[] response = account.soapSelectNodes("//mail:CreateFolderResponse");
		if ( response.length != 1 ) {
			throw new HarnessException("Unable to create folder "+ account.soapLastResponse());
		}
		
	}

	/**
    * Delete a folder using SOAP with the default SERVER type destination host
    * @param account Account used for deleting the folder
    * @param folderName Folder name to be deleted
    * @throws HarnessException
    */
	public static void deleteUsingSOAP(ZimbraAccount account, String folderName)
	      throws HarnessException {
	   deleteUsingSOAP(account, folderName, SOAP_DESTINATION_HOST_TYPE.SERVER, null);
	}

	/**
	 * Delete a folder using SOAP with specific account name to be added to SOAP context
	 * @param account Account used for deleting the folder
	 * @param folderName Folder name to be deleted
	 * @param destType Destination host type: CLIENT or SERVER
	 * @param accountName Account name to be added to SOAP context
	 * @throws HarnessException
	 */
	public static void deleteUsingSOAP(ZimbraAccount account, String folderName,
	      SOAP_DESTINATION_HOST_TYPE destType, String accountName) throws HarnessException {
	   account.soapSend("<GetFolderRequest xmlns='urn:zimbraMail'/>", destType, accountName);
	   String id = account.soapSelectValue("//mail:folder[@name='"+ folderName +"']", "id");
	   account.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>" +
	                       "<action id='" + id + "' op='delete'/>" +
	                    "</FolderActionRequest>", destType);
	   Element[] response = account.soapSelectNodes("//mail:FolderActionResponse");
      if ( response.length != 1 ) {
         throw new HarnessException("Unable to delete folder "+ account.soapLastResponse());
      }

      Object[] params = {"//mail:folder[@name='"+ folderName +"']", "id"};
      GeneralUtility.waitFor(null,
            account, false, "soapSelectValue", params, WAIT_FOR_OPERAND.EQ, null, 30000, 1000);
   }
	
	/**
	 * Import a FolderItem specified in a GetFolderResponse
	 * <br>
	 * The GetFolderResponse should only contain a single <folder/> element
	 * @param response
	 * @return
	 * @throws HarnessException
	 */
	public static FolderItem importFromSOAP(Element response) throws HarnessException {
		if ( response == null )
			throw new HarnessException("Element cannot be null");

		// TODO: can the ZimbraSOAP methods be used to convert this response to item?
		
		// Example response:
		//	    <GetFolderResponse xmlns="urn:zimbraMail">
		//	      <folder id="7" rev="1" s="0" i4next="258" i4ms="2" name="Contacts" ms="1" n="1" l="1" view="contact"/>
		//	    </GetFolderResponse>

		if ( response == null )
			throw new HarnessException("response was null");
		logger.debug("importFromSOAP("+ response.prettyPrint() +")");
		
		Element fElement = ZimbraAccount.SoapClient.selectNode(response, "//mail:folder");
		if ( fElement == null )
			throw new HarnessException("response did not contain folder "+ response.prettyPrint());
		
		FolderItem item = null;
		
		try {
			
			item = CreateFolderItem(fElement);
			
			return (item);
			
		} catch (NumberFormatException e) {
			throw new HarnessException("Unable to create FolderItem", e);
		} catch (ServiceException e) {
			throw new HarnessException("Unable to create FolderItem", e);
		} finally {
			if (item != null)
				logger.info(item.prettyPrint());
		}
	}

	protected static FolderItem CreateFolderItem(Element e)
			throws ServiceException {
		FolderItem item = null;

		item = new FolderItem();
		item.setId(e.getAttribute("id"));
		item.setName(e.getAttribute("name"));
		item.setParentId(e.getAttribute("l"));

		// sub folders
		List<Folder> subFolders = new ArrayList<Folder>();
		for (Element child : e.listElements(MailConstants.E_FOLDER))
			subFolders.add(CreateFolderItem(child));

		item.setSubfolders(subFolders);

		return (item);
	}	

	/**
    * Import a system folder (i.e. Inbox, Sent, Trash, Contacts, etc.) with specified destination type
    * @param account
    * @param folder
    * @param destType Destination Type
    * @param accountName Account Name to get the folder from
    * @return
    * @throws HarnessException
    */
   public static FolderItem importFromSOAP(ZimbraAccount account, SystemFolder folder,
         SOAP_DESTINATION_HOST_TYPE destType, String accountName) throws HarnessException {
      return (importFromSOAP(account, folder.name, destType, accountName));
   }

	/**
	 * Import a system folder (i.e. Inbox, Sent, Trash, Contacts, etc.) with default destination type: SERVER
	 * @param account
	 * @param folder
	 * @return
	 * @throws HarnessException
	 */
	public static FolderItem importFromSOAP(ZimbraAccount account, SystemFolder folder) throws HarnessException {
		return (importFromSOAP(account, folder.name));
	}

	/**
    * Import a folder based on folder's name with default destination type: SERVER
    * @param account
    * @param name
    * @return
    * @throws HarnessException
    */
	public static FolderItem importFromSOAP(ZimbraAccount account, String name) throws HarnessException {
	   return importFromSOAP(account, name, SOAP_DESTINATION_HOST_TYPE.SERVER, null);
	}

	/**
	 * Import a folder by name
	 * @param account
	 * @param name Folder name to be imported
	 * @param destType Destination Host Type: CLIENT or SERVER
	 * @param accountName Account Name to be added in SOAP context while importing
	 * @return (FolderItem)
	 * @throws HarnessException
	 */
	public static FolderItem importFromSOAP(ZimbraAccount account, String name,
	      SOAP_DESTINATION_HOST_TYPE destType, String accountName) throws HarnessException {
		logger.debug("importFromSOAP("+ account.EmailAddress +", "+ name +")");
		
		// Get all the folders
		account.soapSend("<GetFolderRequest xmlns='urn:zimbraMail'/>", destType, accountName);
		String id = account.soapSelectValue("//mail:folder[@name='"+ name +"']", "id");

		// cannot find folder name on the server
		if (id == null) {
           return null;
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

	/**
	 * Get the parent FolderItem
	 * @param account 
	 * @param destType Destination Host Type: CLIENT or SERVER
	 * @param accountName Account Name to be added in SOAP context while importing
	 * @return parent FolderItem
	 * @throws HarnessException
	 */
	public FolderItem getParentFolder(ZimbraAccount account,
	      SOAP_DESTINATION_HOST_TYPE destType, String accountName) throws HarnessException {
	   // Get just the folder specified
      account.soapSend(
            "<GetFolderRequest xmlns='urn:zimbraMail'>" +
               "<folder l='" + super.getParentId() + "'/>" +
            "</GetFolderRequest>",
            destType, accountName);
      Element response = account.soapSelectNode("//mail:GetFolderResponse", 1);

      return (importFromSOAP(response));
	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(FolderItem.class.getSimpleName()).append('\n');
		sb.append("Name: ").append(super.getName()).append('\n');
		sb.append("View: ").append(super.getView()).append('\n');
		sb.append("Parent ID: ").append(super.getParentId()).append('\n');
		return (sb.toString());
	}


}
