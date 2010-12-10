/**
 * 
 */
package framework.items;

import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * @author Matt Rhoades
 *
 */
public class FolderItem extends com.zimbra.soap.mail.type.Folder implements IItem {
	
	/**
	 * Create a new FolderItem object
	 */
	public FolderItem() {
	}

	/* (non-Javadoc)
	 * @see framework.items.IItem#CreateSOAP(framework.util.ZimbraAccount)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see framework.items.IItem#ImportSOAP(com.zimbra.common.soap.Element)
	 */
	@Override
	public void importFromSOAP(Element response) throws HarnessException {
		throw new HarnessException("implement me");
	}


	@Override
	public void importFromSOAP(ZimbraAccount account, String query)
			throws HarnessException {
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
