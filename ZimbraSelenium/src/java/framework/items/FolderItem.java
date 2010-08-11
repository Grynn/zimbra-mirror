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
public class FolderItem extends ZimbraItem implements IItem {
	
	public String name;
	
	public FolderItem() {
	}

	/* (non-Javadoc)
	 * @see framework.items.IItem#CreateSOAP(framework.util.ZimbraAccount)
	 */
	@Override
	public IItem CreateSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}

	/* (non-Javadoc)
	 * @see framework.items.IItem#ImportSOAP(com.zimbra.common.soap.Element)
	 */
	@Override
	public IItem ImportSOAP(Element response) throws HarnessException {
		throw new HarnessException("implement me");
	}

}
