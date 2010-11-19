package framework.items;


/**
 * The <code>ZimbraItem</code> class provides a skeletal implementation
 * for all basic mailbox items.
 * <p>
 * The subclasses to {@link ZimbraItem} are used by the test case methods
 * to hold item properties.  For example, a test case may use a ZimbraItem
 * object that represents a mail message and save the subject, recipient, and
 * mail body.  The test method can then subsequently use the ZimbraItem
 * properties to fill out a "compose" window, or verify a displayed mail,
 * or to validate the element values of a SOAP response.
 * <p>
 * @author Matt Rhoades
 *
 */
public abstract class ZimbraItem {

	/**
	 * The unique (per mailbox) Zimbra ID for the object
	 * <p>
	 * This value may be null if the ID has not yet been determined.
	 * <p>
	 */
	public String id;
	
	public ZimbraItem() {
		id = null;
	}
	
	/**
	 * Create a String containing the relevant object property values
	 * <p>
	 * Useful for debugging, such as <code>logger.info("New message: "+ m.prettyPrint());</code>
	 * <p>
	 * @return A string representation of this object
	 */
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(ZimbraItem.class.getSimpleName()).append('\n');
		sb.append("id: ").append(id).append('\n');
		return (sb.toString());
	}
	
}
