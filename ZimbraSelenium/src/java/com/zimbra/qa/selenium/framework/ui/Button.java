package com.zimbra.qa.selenium.framework.ui;




/**
 * The <code>Button</code> class defines constants that represent
 * general buttons in the client apps.
 * <p>
 * <p>
 * Action constant names start with "B_" for buttons or "O_" for
 * optional context menu button, and take the general format
 * <code>B_PAGE_TEXT</code>,
 * where "Page" is the application name such as MAIL, ADDRESSBOOK, and
 * "Text" is the displayed English text on the button.  For non-page
 * specific Buttons, the "Page" is not specified.
 * <p>
 * The action constants can be used in page methods, for example:
 * <pre>
 * {@code
 * // Click on the NEW button to compose a new mail
 * app.zPageMail.zToolbarPressButton(Button.B_TAG, Button.O_TAG_REMOVETAG);
 * }
 * </pre>
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public class Button {

	// General buttons and pulldown options
	public static final Button B_NEW = new Button("B_NEW");
	public static final Button B_DELETE = new Button("B_DELETE");
	public static final Button B_MOVE = new Button("B_MOVE");
	public static final Button B_PRINT = new Button("B_PRINT");
	public static final Button B_TAG = new Button("B_TAG");
	public static final Button B_SAVE = new Button("B_SAVE");
	public static final Button B_RENAME = new Button("B_RENAME");
	public static final Button B_SHARE = new Button("B_SHARE");
	
	public static final Button O_NEW_MESSAGE = new Button("O_NEW_MESSAGE");
	public static final Button O_NEW_CONTACT = new Button("O_NEW_CONTACT");
	public static final Button O_NEW_CONTACTGROUP = new Button("O_NEW_CONTACTGROUP");
	public static final Button O_NEW_APPOINTMENT = new Button("O_NEW_APPOINTMENT");
	public static final Button O_NEW_TASK = new Button("O_NEW_TASK");
	public static final Button O_NEW_DOCUMENT = new Button("O_NEW_DOCUMENT");
	public static final Button O_NEW_FOLDER = new Button("O_NEW_FOLDER");
	public static final Button O_NEW_TAG = new Button("O_NEW_TAG");
	public static final Button O_NEW_ADDRESSBOOK = new Button("O_NEW_ADDRESSBOOK");
	public static final Button O_NEW_CALENDAR = new Button("O_NEW_CALENDAR");
	public static final Button O_NEW_TASKFOLDER = new Button("O_NEW_TASKFOLDER");
	public static final Button O_NEW_BRIEFCASE = new Button("O_NEW_BRIEFCASE");
	public static final Button O_TAG_NEWTAG = new Button("O_TAG_NEWTAG");
	public static final Button O_TAG_REMOVETAG = new Button("O_TAG_REMOVETAG");


	// General dialog buttons
	public static final Button B_YES = new Button("B_YES");
	public static final Button B_NO = new Button("B_NO");
	public static final Button B_CANCEL = new Button("B_CANCEL");
	public static final Button B_OK = new Button("B_OK");

	// MailPage buttons and pulldown options
	public static final Button B_GETMAIL = new Button("B_GETMAIL");
	public static final Button B_REPLY = new Button("B_REPLY");
	public static final Button B_REPLYALL = new Button("B_REPLYALL");
	public static final Button B_FORWARD = new Button("B_FORWARD");
	public static final Button B_RESPORTSPAM = new Button("B_RESPORTSPAM");
	public static final Button B_RESPORTNOTSPAM = new Button("B_RESPORTNOTSPAM");
	public static final Button B_NEWWINDOW = new Button("B_NEWWINDOW");
	public static final Button B_LISTVIEW = new Button("B_LISTVIEW");

	public static final Button O_LISTVIEW_BYCONVERSATION = new Button("O_LSITVIEW_BYCONVERSATION");
	public static final Button O_LISTVIEW_BYMESSAGE = new Button("O_LSITVIEW_BYMESSAGE");
	public static final Button O_LISTVIEW_READINGPANEBOTTOM = new Button("O_LSITVIEW_READINGPANEBOTTOM");
	public static final Button O_LISTVIEW_READINGPANERIGHT = new Button("O_LSITVIEW_READINGPANERIGHT");
	public static final Button O_LISTVIEW_READINGPANEOFF = new Button("O_LSITVIEW_READINGPANEOFF");

	// Compose mail buttons and pulldown options
	public static final Button B_SEND = new Button("B_SEND");
	public static final Button B_SAVE_DRAFT = new Button("B_SAVE_DRAFT");
	public static final Button B_ADD_ATTACHMENT = new Button("B_ADD_ATTACHMENT");
	public static final Button B_SPELL_CHECK = new Button("B_SPELL_CHECK");
	public static final Button B_SIGNATURE = new Button("B_SIGNATURE");
	public static final Button B_OPTIONS = new Button("B_OPTIONS");
	public static final Button B_PRIORITY = new Button("B_PRIORITY");
	public static final Button B_SHOWBCC = new Button("B_SHOWBCC");

	public static final Button O_SEND_SEND_LATER = new Button("O_SEND_SEND_LATER");
	public static final Button O_SIGNATURE_DO_NOT_ADD_SIGNATURE = new Button("O_SIGNATURE_DO_NOT_ADD_SIGNATURE");
	public static final Button O_OPTION_FORMAT_AS_HTML = new Button("O_OPTION_FORMAT_AS_HTML");
	public static final Button O_OPTION_FORMAT_AS_TEXT = new Button("O_OPTION_FORMAT_AS_TEXT");
	public static final Button O_OPTION_REQUEST_READ_RECEIPT = new Button("O_OPTION_REQUEST_READ_RECEIPT");
	public static final Button O_PRIORITY_HIGH = new Button("O_PRIORITY_HIGH");
	public static final Button O_PRIORITY_NORMAL = new Button("O_PRIORITY_NORMAL");
	public static final Button O_PRIORITY_LOW = new Button("O_PRIORITY_LOW");

	// SearchPage buttons and pulldown options
	public static final Button B_SEARCHTYPE = new Button("B_SEARCHTYPE");
	public static final Button B_SEARCH = new Button("B_SEARCH");
	public static final Button B_SEARCHSAVE = new Button("B_SEARCHSAVE");
	public static final Button B_SEARCHADVANCED = new Button("B_SEARCHADVANCED");
	
	public static final Button O_SEARCHTYPE_ALL = new Button("O_SEARCHTYPE_ALL");
	public static final Button O_SEARCHTYPE_EMAIL = new Button("O_SEARCHTYPE_EMAIL");
	public static final Button O_SEARCHTYPE_CONTACTS = new Button("O_SEARCHTYPE_CONTACTS");
	public static final Button O_SEARCHTYPE_GAL = new Button("O_SEARCHTYPE_GAL");
	public static final Button O_SEARCHTYPE_APPOINTMENTS = new Button("O_SEARCHTYPE_APPOINTMENTS");
	public static final Button O_SEARCHTYPE_TASKS = new Button("O_SEARCHTYPE_TASKS");
	public static final Button O_SEARCHTYPE_FILES = new Button("O_SEARCHTYPE_FILES");
	public static final Button O_SEARCHTYPE_INCLUDESHARED = new Button("O_SEARCHTYPE_INCLUDESHARED");

	//Briefcase buttons
	public static final Button B_UPLOAD_FILE = new Button("B_UPLOAD_FILE");
	public static final Button B_EDIT_FILE = new Button("B_EDIT_FILE");
	public static final Button B_OPEN_IN_SEPARATE_WINDOW = new Button("B_OPEN_IN_SEPARATE_WINDOW");
	
    //Addressbook button
	public static final Button B_EDIT = new Button("zb__CNS__EDIT");
	
	// Tree buttons
	public static final Button B_TREE_NEWFOLDER = new Button("B_TREE_NEWFOLDER");
	public static final Button B_TREE_NEWADDRESSBOOK = new Button("B_TREE_NEWADDRESSBOOK");
	public static final Button B_TREE_NEWCALENDAR = new Button("B_TREE_NEWCALENDAR");
	public static final Button B_TREE_NEWTASKLIST = new Button("B_TREE_NEWTASKLIST");
	public static final Button B_TREE_NEWBRIEFCASE = new Button("B_TREE_NEWBRIEFCASE");
	public static final Button B_TREE_BRIEFCASE_EXPANDCOLLAPSE = new Button("B_TREE_BRIEFCASE_EXPANDCOLLAPSE");
	public static final Button B_TREE_NEWTAG = new Button("B_TREE_NEWTAG");
	
	// Tree buttons (Mail folders)
	public static final Button B_TREE_FOLDER_MARKASREAD = new Button("B_TREE_FOLDER_MARKASREAD");
	public static final Button B_TREE_FOLDER_EXPANDALL = new Button("B_TREE_FOLDER_EXPANDALL");
	public static final Button B_TREE_FOLDER_EMPTY = new Button("B_TREE_FOLDER_EMPTY");

	
	// Button properties
	private final String ID;

	protected Button(String id) {
		this.ID = id;
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
		
		Button other = (Button) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ID;
	}


}
