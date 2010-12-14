package framework.ui;



/**
 * The <code>Action</code> class defines constants that represent
 * general actions in the client apps.
 * <p>
 * This class defines constants for
 * <ol>
 * <li>Mouse actions, such as click or ctrl-click</li>
 * <li>Zimbra object actions, such as check a checkbox or flag a mail</li>
 * </ol>
 * <p>
 * Action constant names start with "A_" and take the general format
 * <code>A_PAGE_ACTION</code>,
 * where "Page" is the application name such as MAIL, ADDRESSBOOK and
 * "Action" is the general description of the action.  For non-page
 * specific Actions, "Page" is not specified.
 * <p>
 * The action constants can be used in page methods, for example:
 * <pre>
 * {@code
 * String subject = "foo123";
 * app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
 * }
 * </pre>
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public class Action {

	// General actions
	public static final Action A_LEFTCLICK = new Action("A_LEFTCLICK");
	public static final Action A_SHIFTSELECT = new Action("A_SHIFTSELECT");
	public static final Action A_CTRLSELECT = new Action("A_CTRLSELECT");
	public static final Action A_RIGHTCLICK = new Action("A_RIGHTCLICK");

	// Mail page actions
	public static final Action A_MAIL_CHECKBOX = new Action("A_MAIL_CHECKBOX");
	public static final Action A_MAIL_UNCHECKBOX = new Action("A_MAIL_UNCHECKBOX");
	public static final Action A_MAIL_FLAG = new Action("A_MAIL_FLAG");
	public static final Action A_MAIL_UNFLAG = new Action("A_MAIL_UNFLAG");
	public static final Action A_MAIL_EXPANDCONVERSATION = new Action("A_MAIL_EXPANDCONVERSATION");
	
	private final String ID;
	
	protected Action(String id) {
		this.ID = id;
	}

	@Override
	public String toString() {
		return ID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
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
		Action other = (Action) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		return true;
	}

}
