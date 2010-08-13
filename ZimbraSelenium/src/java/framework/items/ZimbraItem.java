package framework.items;

/**
 * Used to define a Zimbra Item
 * 
 * This class defines all common items in a mailbox, such as mail, contact, appointment, etc.
 * 
 * @author Matt Rhoades
 *
 */
public abstract class ZimbraItem {

	public String id;
	
	public ZimbraItem() {
	}
	
	/**
	 * Create a String containing the relevant object property values
	 * @return A string representation of this object
	 */
	public String printItem() {
		StringBuilder sb = new StringBuilder();
		sb.append("ZimbraItem\n");
		sb.append("id: ").append(id).append('\n');
		return (sb.toString());
	}
}
