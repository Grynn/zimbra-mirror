/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;


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
		public static final SystemFolder Contacts = new SystemFolder("Contacts");
		public static final SystemFolder Drafts = new SystemFolder("Drafts");
		public static final SystemFolder EmailedContacts = new SystemFolder("Emailed Contacts");
		public static final SystemFolder Inbox = new SystemFolder("Inbox");
		public static final SystemFolder Junk = new SystemFolder("Junk");
		public static final SystemFolder Sent = new SystemFolder("Sent");
		public static final SystemFolder Tasks = new SystemFolder("Tasks");
		public static final SystemFolder Trash = new SystemFolder("Trash");
				
		private String name;
		private SystemFolder(String foldername) {
			name = foldername;
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
	 * Import a FolderItem specified in a GetFolderResponse
	 * <br>
	 * The GetFolderResponse should only contain a single <folder/> element
	 * @param response
	 * @return
	 * @throws HarnessException
	 */
	public static FolderItem importFromSOAP(Element response) throws HarnessException {
		logger.debug("importFromSOAP("+ response.prettyPrint() +")");

		// TODO: can the ZimbraSOAP methods be used to convert this response to item?
		
		// Example response:
		//	    <GetFolderResponse xmlns="urn:zimbraMail">
		//	      <folder id="7" rev="1" s="0" i4next="258" i4ms="2" name="Contacts" ms="1" n="1" l="1" view="contact"/>
		//	    </GetFolderResponse>

		if ( response == null )
			throw new HarnessException("response was null");
		
		Element fElement = ZimbraAccount.SoapClient.selectNode(response, "//mail:folder");
		if ( fElement == null )
			throw new HarnessException("response did not contain folder "+ response.prettyPrint());
		
		FolderItem item = null;
		
		try {
			
			item = new FolderItem();
			item.setId(Integer.parseInt(fElement.getAttribute("id")));
			item.setName(fElement.getAttribute("name"));
			
			return (item);
			
		} catch (NumberFormatException e) {
			throw new HarnessException("Unable to create FolderItem", e);
		} catch (ServiceException e) {
			throw new HarnessException("Unable to create FolderItem", e);
		} finally {
			if ( item != null )	logger.info(item.prettyPrint());
		}
	}


	/**
	 * Import a system folder (i.e. Inbox, Sent, Trash, Contacts, etc.)
	 * @param account
	 * @param folder
	 * @return
	 * @throws HarnessException
	 */
	public static FolderItem importFromSOAP(ZimbraAccount account, SystemFolder folder) throws HarnessException {
		return (importFromSOAP(account, folder.name));
	}
	
	/**
	 * Import a folder by name
	 * @param account
	 * @param folder
	 * @return
	 * @throws HarnessException
	 */
	public static FolderItem importFromSOAP(ZimbraAccount account, String name) throws HarnessException {
		logger.debug("importFromSOAP("+ account.EmailAddress +", "+ name +")");
		
		// Get all the folders
		account.soapSend("<GetFolderRequest xmlns='urn:zimbraMail'/>");
		String id = account.soapSelectValue("//mail:folder[@name='"+ name +"']", "id");
		
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
		sb.append(FolderItem.class.getSimpleName()).append('\n');
		sb.append("Name: ").append(super.getName()).append('\n');
		sb.append("View: ").append(super.getView()).append('\n');
		sb.append("Parent ID: ").append(super.getParentId()).append('\n');
		return (sb.toString());
	}

}
