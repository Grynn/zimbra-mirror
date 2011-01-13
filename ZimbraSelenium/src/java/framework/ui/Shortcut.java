package framework.ui;



/**
 * The <code>Shortcut</code> class defines constants that represent
 * keyboard shortcuts.
 * <p>
 * Shortcut constant names start with "S_" and take the general format
 * <code>A_PAGE_ACTION</code>,
 * where "Page" is the application name such as MAIL, ADDRESSBOOK and
 * "Action" is the general description of the action.  For non-page
 * specific Actions, "Page" is not specified.
 * <p>
 * The action constants can be used in page methods, for example:
 * <pre>
 * {@code
 * app.zPageMail.zKeyboardShortcut(Shortcut.S_NEWMESSAGE);
 * }
 * </pre>
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public class Shortcut {

	// General shortcuts
	public static final Shortcut S_GOTOMAIL = new Shortcut("S_GOTOMAIL",			"gm");
	public static final Shortcut S_GOTOCONTACTS = new Shortcut("S_GOTOCONTACTS",	"ga");
	public static final Shortcut S_GOTOCALENDAR = new Shortcut("S_GOTOCALENDAR",	"gc");
	public static final Shortcut S_GOTOTASKS = new Shortcut("S_GOTOTASKS",			"gt");
	public static final Shortcut S_GOTOPREFS = new Shortcut("S_GOTOPREFS",			"gp");
	public static final Shortcut S_GOTOBRIEFCASE = new Shortcut("S_GOTOBRIEFCASE",	"gb");

	public static final Shortcut S_NEWITEM = new Shortcut("S_NEWITEM",				"n");
	public static final Shortcut S_NEWMESSAGE = new Shortcut("S_NEWMESSAGE",		"nm");
	public static final Shortcut S_NEWMESSAGE2 = new Shortcut("S_NEWMESSAGE2",		"c");
	public static final Shortcut S_COMPOSENEWWINDOW = new Shortcut("S_COMPOSENEWWINDOW",	"C");
	public static final Shortcut S_NEWCONTACT = new Shortcut("S_NEWCONTACT",		"nc");
	public static final Shortcut S_MOVE = new Shortcut("S_MOVE",					"m");
	public static final Shortcut S_MAIL_TAG = new Shortcut("S_MAIL_TAG",			"t");
	public static final Shortcut S_MAIL_REMOVETAG = new Shortcut("S_MAIL_REMOVETAG",		"u");

	
	// Mail shortcuts
	public static final Shortcut S_MAIL_GETMAIL = new Shortcut("S_MAIL_GETMAIL",	"=");
	public static final Shortcut S_MAIL_INBOX = new Shortcut("S_MAIL_INBOX",		"i");
	public static final Shortcut S_MAIL_INBOX2 = new Shortcut("S_MAIL_INBOX2",		"vi");
	public static final Shortcut S_MAIL_DRAFTS = new Shortcut("S_MAIL_DRAFTS",		"vd");
	public static final Shortcut S_MAIL_MOVETOTRASH = new Shortcut("S_MAIL_MOVETOTRASH",	".t");
	public static final Shortcut S_MAIL_MOVETOINBOX = new Shortcut("S_MAIL_MOVETOINBOX",	".i");
	public static final Shortcut S_MAIL_MARKFLAG = new Shortcut("S_MAIL_MARKFLAG", 	"mf");
	public static final Shortcut S_MAIL_MARKREAD = new Shortcut("S_MAIL_MARKFLAG", 	"mr");
	public static final Shortcut S_MAIL_MARKUNREAD = new Shortcut("S_MAIL_MARKFLAG",		"mu");
	
	private final String ID;
	private final String Keys;
	
	protected Shortcut(String id, String keys) {
		this.ID = id;
		this.Keys = keys;
	}

	public String getKeys() {
		return (Keys);
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
		Shortcut other = (Shortcut) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		return true;
	}

}
