/**
 * 
 */
package framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
		throw new HarnessException("implement me");
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
