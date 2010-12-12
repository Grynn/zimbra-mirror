/**
 * 
 */
package framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * @author Matt Rhoades
 *
 */
public class FolderItem extends com.zimbra.soap.mail.type.Folder implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

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

	public static FolderItem importFromSOAP(Element response) throws HarnessException {
		
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


	public static FolderItem importFromSOAP(ZimbraAccount account, String query) throws HarnessException {
		throw new HarnessException("implement me");
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
