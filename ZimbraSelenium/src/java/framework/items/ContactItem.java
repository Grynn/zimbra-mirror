package framework.items;

import java.util.HashMap;

import com.zimbra.common.soap.Element;

import framework.util.HarnessException;
import framework.util.ZimbraAccount;

/**
 * Used to define a Zimbra Contact
 * 
 * @author Matt Rhoades
 *
 */
public class ContactItem extends ZimbraItem implements IItem {

	public String firstName = null;
	public String middleName = null;
	public String lastName = null;
	public String email = null;
	public HashMap<String, String> ContactAttributes = new HashMap<String, String>();
	
	public FolderItem AddressBook = null;
	
	public ContactItem() {
	}

	public String getAttribute(String key, String defaultValue) {
		if ( !ContactAttributes.containsKey(key) )
			return (defaultValue);
		return (ContactAttributes.get(key));
	}
	
	public String getAttribute(String key) {
		return (getAttribute(key, null));
	}
	
	public String setAttribute(String key, String value) {
		
		// Process any special attributes here
		if ( key.equals("email") )
			email = value;
		if ( key.equals("firstName"))
			firstName = value;
		if ( key.equals("middleName"))
			middleName = value;
		if ( key.equals("lastName"))
			lastName = value;
		
		// Set the map
		ContactAttributes.put(key, value);
		
		return (ContactAttributes.get(key));
	}
	
	@Override
	public IItem ImportSOAP(Element response) throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public IItem CreateSOAP(ZimbraAccount account) throws HarnessException {
		throw new HarnessException("implement me");
	}
	
	
}
