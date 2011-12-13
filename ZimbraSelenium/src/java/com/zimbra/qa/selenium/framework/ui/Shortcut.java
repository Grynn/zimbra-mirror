package com.zimbra.qa.selenium.framework.ui;



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
	public static final Shortcut S_RIGHTCLICK = new Shortcut("S_RIGHTCLICK",			",");
	
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
	public static final Shortcut S_NEWITEM_IN_NEW_WINDOW = new Shortcut("S_NEWITEM_IN_NEW_WINDOW",			"n");	// Same shortcut as S_NEWITEM, but harness returns a separate window
	public static final Shortcut S_NEWMESSAGE_IN_NEW_WINDOW = new Shortcut("S_NEWMESSAGE_IN_NEW_WINDOW",	"nm");	// Same shortcut as S_NEWMESSAGE, but harness returns a separate window
	public static final Shortcut S_NEWMESSAGE2_IN_NEW_WINDOW = new Shortcut("S_NEWMESSAGE2_IN_NEW_WINDOW",	"c");	// Same shortcut as S_NEWMESSAGE2, but harness returns a separate window
	public static final Shortcut S_NEWCONTACT = new Shortcut("S_NEWCONTACT",		"nc");
	public static final Shortcut S_MOVE = new Shortcut("S_MOVE",					"m");
	public static final Shortcut S_MAIL_TAG = new Shortcut("S_MAIL_TAG",			"t");
	public static final Shortcut S_MAIL_REMOVETAG = new Shortcut("S_MAIL_REMOVETAG",		"u");
	public static final Shortcut S_ESCAPE = new Shortcut("S_ESCAPE",		"Esc");

	// Open the assistnat
	public static final Shortcut S_ASSISTANT = new Shortcut("S_ASSISTANT", "`");

	// Mail shortcuts
	public static final Shortcut S_MAIL_GETMAIL = new Shortcut("S_MAIL_GETMAIL",	"=");
	public static final Shortcut S_MAIL_INBOX = new Shortcut("S_MAIL_INBOX",		"i");
	public static final Shortcut S_MAIL_INBOX2 = new Shortcut("S_MAIL_INBOX2",		"vi");
	public static final Shortcut S_MAIL_DRAFTS = new Shortcut("S_MAIL_DRAFTS",		"vd");
	public static final Shortcut S_MAIL_MOVETOTRASH = new Shortcut("S_MAIL_MOVETOTRASH",	".t");
	public static final Shortcut S_MAIL_HARDELETE = new Shortcut("S_MAIL_MOVETOTRASH",	"<SHIFT><DEL>");
	public static final Shortcut S_MAIL_MOVETOINBOX = new Shortcut("S_MAIL_MOVETOINBOX",	".i");
	public static final Shortcut S_MAIL_MARKFLAG = new Shortcut("S_MAIL_MARKFLAG", 	"mf");
	public static final Shortcut S_MAIL_MARKREAD = new Shortcut("S_MAIL_MARKFLAG", 	"mr");
	public static final Shortcut S_MAIL_MARKUNREAD = new Shortcut("S_MAIL_MARKFLAG",		"mu");
	public static final Shortcut S_MAIL_MARKSPAM = new Shortcut("S_MAIL_MARKSPAM",		"ms");

	// Folders shortcuts
	public static final Shortcut S_NEWFOLDER = new Shortcut("S_NEWFOLDER", "nf");
	
	// Tag shortcuts
	public static final Shortcut S_NEWTAG = new Shortcut("S_NEWTAG", "nt");
	
	// Calendar shortcuts
	public static final Shortcut S_NEWCALENDAR = new Shortcut("S_NEWCALENDAR", "nl");

	// Briefcase shortcuts
	public static final Shortcut S_NEWDOCUMENT = new Shortcut("S_NEWDOCUMENT", "nd");
	public static final Shortcut S_DELETE = new Shortcut("S_DELETE", "<Delete>");
	public static final Shortcut S_BACKSPACE = new Shortcut("S_BACKSPACE", "<Backspace>");

	//Task shortcut
	public static final Shortcut S_NEWTASK = new Shortcut("S_NEWTASK", "nk");
	public static final Shortcut S_TASK_HARDELETE = new Shortcut("S_MAIL_MOVETOTRASH",	"<SHIFT><DEL>");
	public static final Shortcut S_PRINTTASK = new Shortcut("S_PRINTTASK", "p");

	// Shortcut properties
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
