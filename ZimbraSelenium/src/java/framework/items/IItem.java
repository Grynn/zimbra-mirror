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
	 * @param response - The response envelope
	 * @return the IItem object
	 * @throws HarnessException
	 */
	public void importFromSOAP(Element response) throws HarnessException;
	
	/**
	 * Create an object based on a Zimbra SOAP Get*Response
	 * 
	 * For example, create a contact based on the GetContactResponse
	 * 
	 * @param account - the account that contains the object
	 * @param query - A zimbra search string to find the object
	 * @throws HarnessException on error or if multiple objects were found
	 */
	public void importFromSOAP(ZimbraAccount account, String query) throws HarnessException;
	

	/**
	 * Create an object on the Zimbra server based on the object values
	 * @param account - the account used to create the object
	 * @throws HarnessException
	 */
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException;
	
}
