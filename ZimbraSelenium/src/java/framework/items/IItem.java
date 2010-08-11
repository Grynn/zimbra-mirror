package framework.items;

import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;

public interface IItem {

	/**
	 * Create an object based on a Zimbra SOAP Get*Response
	 * 
	 * For example, create a contact based on the GetContactResponse
	 * 
	 * @param response - The appropriate Get*Response
	 * @return the IItem object
	 * @throws HarnessException
	 */
	public IItem ImportSOAP(Element response) throws HarnessException;
	

	/**
	 * Create an object on the Zimbra server based on the object values
	 * @param account - the account used to create the objecvt
	 * @return the IItem object
	 * @throws HarnessException
	 */
	public IItem CreateSOAP(ZimbraAccount account) throws HarnessException;
	
	
}
