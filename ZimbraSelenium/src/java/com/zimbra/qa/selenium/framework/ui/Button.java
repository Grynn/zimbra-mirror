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
	public static final Button B_GEAR_BOX=new Button("B_GEAR_BOX");
	public static final Button B_NEW_IN_NEW_WINDOW = new Button("B_NEW_IN_NEW_WINDOW");
	public static final Button B_DELETE = new Button("B_DELETE");
	public static final Button B_MOVE = new Button("B_MOVE");
	public static final Button B_PRINT = new Button("B_PRINT");
	public static final Button B_TAG = new Button("B_TAG");
	public static final Button B_SAVE = new Button("B_SAVE");
	public static final Button B_RENAME = new Button("B_RENAME");
	public static final Button B_SHARE = new Button("B_SHARE");
	public static final Button B_CLOSE = new Button("B_CLOSE");
	public static final Button B_ACTIONS = new Button("B_ACTIONS");
	public static final Button B_REDIRECT = new Button("B_REDIRECT");
	public static final Button B_MUTE = new Button("B_MUTE");
	public static final Button B_HELP = new Button("B_HELP");


	public static final Button O_NEW = new Button("O_NEW");
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
	public static final Button O_PRINT_TASKFOLDER = new Button("O_PRINT_TASKFOLDER");

	// Accept decline options
	public static final Button O_ACCEPT_NOTIFY_ORGANIZER = new Button("O_ACCEPT_NOTIFY_ORGANIZER");
	public static final Button O_ACCEPT_EDIT_REPLY = new Button("O_ACCEPT_EDIT_REPLY");
	public static final Button O_ACCEPT_DONT_NOTIFY_ORGANIZER = new Button("O_ACCEPT_DONTNOTIFY_ORGANIZER");
	public static final Button O_TENTATIVE_NOTIFY_ORGANIZER = new Button("O_TENTATIVE_NOTIFY_ORGANIZER");
	public static final Button O_TENTATIVE_EDIT_REPLY = new Button("O_TENTATIVE_EDIT_REPLY");
	public static final Button O_TENTATIVE_DONT_NOTIFY_ORGANIZER = new Button("O_TENTATIVE_DONTNOTIFY_ORGANIZER");
	public static final Button O_DECLINE_NOTIFY_ORGANIZER = new Button("O_DECLINE_NOTIFY_ORGANIZER");
	public static final Button O_DECLINE_EDIT_REPLY = new Button("O_DECLINE_EDIT_REPLY");
	public static final Button O_DECLINE_DONT_NOTIFY_ORGANIZER = new Button("O_DECLINE_DONTNOTIFY_ORGANIZER");
	
	// General dialog buttons
	public static final Button B_YES = new Button("B_YES");
	public static final Button B_NO = new Button("B_NO");
	public static final Button B_CANCEL = new Button("B_CANCEL");
	public static final Button B_OK = new Button("B_OK");
	public static final Button B_ADD = new Button("B_ADD");
	public static final Button B_BACK = new Button("B_BACK");
	public static final Button B_NEXT = new Button("B_NEXT");
	public static final Button O_EDIT_LINK = new Button("O_EDIT_LINK");
	public static final Button O_REVOKE_LINK = new Button("O_REVOKE_LINK");
	public static final Button O_RESEND_LINK = new Button("O_RESEND_LINK");
	public static final Button B_MORE_DETAILS = new Button("B_MORE_DETAILS");
	public static final Button B_SENDCANCELLATION = new Button("B_SENDCANCELLATION");
	public static final Button B_EDITMESSAGE = new Button("B_EDITMESSAGE");
	public static final Button B_CANCEL_CONFIRMDELETE = new Button("B_CANCEL_CONFIRMDELETE");

	// Zimbra Desktop's Accounts page
	public static final Button B_VALIDATE_AND_SAVE = new Button("B_VALIDATE_AND_SAVE");

	// MailPage buttons and pulldown options
	public static final Button B_GETMAIL = new Button("B_GETMAIL");
	public static final Button B_LOADFEED = new Button("B_LOADFEED");
	public static final Button B_REPLY = new Button("B_REPLY");
	public static final Button B_REPLYALL = new Button("B_REPLYALL");
	public static final Button B_FORWARD = new Button("B_FORWARD");
	public static final Button B_RESPORTSPAM = new Button("B_RESPORTSPAM");
	public static final Button B_RESPORTNOTSPAM = new Button("B_RESPORTNOTSPAM");
	public static final Button B_NEWWINDOW = new Button("B_NEWWINDOW");
	public static final Button B_LISTVIEW = new Button("B_LISTVIEW");

	public static final Button O_LISTVIEW_BYCONVERSATION = new Button("O_LISTVIEW_BYCONVERSATION");
	public static final Button O_LISTVIEW_BYMESSAGE = new Button("O_LISTVIEW_BYMESSAGE");
	public static final Button O_LISTVIEW_READINGPANEBOTTOM = new Button("O_LISTVIEW_READINGPANEBOTTOM");
	public static final Button O_LISTVIEW_READINGPANERIGHT = new Button("O_LISTVIEW_READINGPANERIGHT");
	public static final Button O_LISTVIEW_READINGPANEOFF = new Button("O_LISTVIEW_READINGPANEOFF");

	// MailPage list buttons (sort by options)
	public static final Button B_MAIL_LIST_SORTBY_FLAGGED = new Button("B_MAIL_LIST_SORTBY_FLAGGED");;
	public static final Button B_MAIL_LIST_SORTBY_FROM = new Button("B_MAIL_LIST_SORTBY_FROM");;
	public static final Button B_MAIL_LIST_SORTBY_ATTACHMENT = new Button("B_MAIL_LIST_SORTBY_ATTACHMENT");
	public static final Button B_MAIL_LIST_SORTBY_SUBJECT = new Button("B_MAIL_LIST_SORTBY_SUBJECT");;
	public static final Button B_MAIL_LIST_SORTBY_SIZE = new Button("B_MAIL_LIST_SORTBY_SIZE");;
	public static final Button B_MAIL_LIST_SORTBY_RECEIVED = new Button("B_MAIL_LIST_SORTBY_RECEIVED");;

	// MailPage context menu
	public static final Button O_MARK_AS_READ = new Button("O_MARK_AS_READ");
	public static final Button O_MARK_AS_UNREAD = new Button("O_MARK_AS_UNREAD");
	public static final Button O_REPLY = new Button("O_REPLY");
	public static final Button O_REPLY_TO_ALL = new Button("O_REPLY_TO_ALL");
	public static final Button O_FORWARD = new Button("O_FORWARD");
	public static final Button O_EDIT_AS_NEW = new Button("O_EDIT_AS_NEW");
	public static final Button O_TAG_MESSAGE = new Button("O_TAG_MESSAGE");
	// public static final Button O_DELETE = new Button("O_DELETE");
	public static final Button O_MOVE = new Button("O_MOVE");
	public static final Button O_PRINT = new Button("O_PRINT");
	public static final Button O_MARK_AS_SPAM = new Button("O_MARK_AS_SPAM");
	public static final Button O_SHOW_ORIGINAL = new Button("O_SHOW_ORIGINAL");
	public static final Button O_NEW_FILTER = new Button("O_NEW_FILTER");
	public static final Button O_CREATE_APPOINTMENT = new Button("O_CREATE_APPOINTMENT");
	public static final Button O_CREATE_TASK = new Button("O_CREATE_TASK");
	public static final Button O_CLEAR_SEARCH_HIGHLIGHTS = new Button("O_CLEAR_SEARCH_HIGHLIGHTS");

	// Compose mail buttons and pulldown options
	public static final Button B_SEND = new Button("B_SEND");
	public static final Button B_SAVE_DRAFT = new Button("B_SAVE_DRAFT");
	public static final Button B_ADD_ATTACHMENT = new Button("B_ADD_ATTACHMENT");
	public static final Button B_SPELL_CHECK = new Button("B_SPELL_CHECK");
	public static final Button B_SIGNATURE = new Button("B_SIGNATURE");
	public static final Button B_OPTIONS = new Button("B_OPTIONS");
	public static final Button B_PRIORITY = new Button("B_PRIORITY");
	public static final Button B_TO = new Button("B_TO");
	public static final Button B_CC = new Button("B_CC");
	public static final Button B_BCC = new Button("B_BCC");
	public static final Button B_SHOWBCC = new Button("B_SHOWBCC");
	public static final Button B_REMOVE = new Button("B_REMOVE");
	public static final Button B_SHOW_NAMES_FROM = new Button("B_SHOW_NAMES_FROM");

	public static final Button O_SEND_SEND = new Button("O_SEND_SEND");
	public static final Button O_SEND_SEND_LATER = new Button("O_SEND_SEND_LATER");
	public static final Button O_SIGNATURE_DO_NOT_ADD_SIGNATURE = new Button("O_SIGNATURE_DO_NOT_ADD_SIGNATURE");
	public static final Button O_ADD_SIGNATURE = new Button("O_ADD_SIGNATURE");
	public static final Button O_OPTION_FORMAT_AS_HTML = new Button("O_OPTION_FORMAT_AS_HTML");
	public static final Button O_OPTION_FORMAT_AS_TEXT = new Button("O_OPTION_FORMAT_AS_TEXT");
	public static final Button O_OPTION_REQUEST_READ_RECEIPT = new Button("O_OPTION_REQUEST_READ_RECEIPT");
	public static final Button O_PRIORITY_HIGH = new Button("O_PRIORITY_HIGH");
	public static final Button O_PRIORITY_NORMAL = new Button("O_PRIORITY_NORMAL");
	public static final Button O_PRIORITY_LOW = new Button("O_PRIORITY_LOW");
	public static final Button O_CONTACTS = new Button("O_CONTACTS");
	public static final Button O_PERSONAL_AND_SHARED_CONTACTS = new Button("O_PERSONAL_AND_SHARED_CONTACTS");
	public static final Button O_GLOBAL_ADDRESS_LIST = new Button("O_GLOBAL_ADDRESS_LIST");

	// Dumpster dialog
	public static final Button B_RECOVER_DELETED_ITEMS = new Button("B_RECOVER_DELETED_ITEMS");
	public static final Button B_RECOVER_TO = new Button("B_RECOVER_TO");

	
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

	//Octopus buttons
	public static final Button B_TAB_MY_FILES = new Button("B_TAB_MY_FILES");
	public static final Button B_MY_FILES = new Button("B_MY_FILES");
	public static final Button B_MY_FILES_LIST_ITEM = new Button("B_MY_FILES_LIST_ITEM");
	public static final Button O_FOLDER_SHARE =  new Button("O_FOLDER_SHARE");
	public static final Button O_FILE_SHARE =  new Button("O_FILE_SHARE");
	public static final Button B_STOP_SHARING =  new Button("B_STOP_SHARING");
	public static final Button B_LEAVE_THIS_SHARED_FOLDER =  new Button("O_LEAVE_THIS_SHARED_FOLDER");
	public static final Button B_SHOW_MESSAGE = new Button("B_SHOW_MESSAGE");
	public static final Button B_EXPAND = new Button("B_EXPAND");
	public static final Button B_COLLAPSE = new Button("B_COLLAPSE");
	public static final Button B_HISTORY =  new Button("B_HISTORY");
	public static final Button B_COMMENTS =  new Button("B_COMMENTS");
	
	public static final Button B_TAB_SHARING = new Button("B_TAB_SHARING");
	public static final Button B_IGNORE =  new Button("B_IGNORE");
	public static final Button B_ADD_TO_MY_FILES = new Button("B_ADD_TO_MY_FILES");

	public static final Button B_TAB_FAVORITES = new Button("B_TAB_FAVORITES");
	public static final Button O_FAVORITE =  new Button("O_FAVORITE");
	public static final Button O_NOT_FAVORITE =  new Button("O_NOT_FAVORITE");
	public static final Button B_WATCH =  new Button("B_WATCH");
	public static final Button B_UNWATCH =  new Button("B_UNWATCH");

	public static final Button B_TAB_HISTORY = new Button("B_TAB_HISTORY");
	public static final Button O_ALL_TYPES =  new Button("O_ALL_TYPES");
	public static final Button O_FAVORITES =  new Button("O_FAVORITES");
	public static final Button O_COMMENT =  new Button("O_COMMENT");
	public static final Button O_SHARING =  new Button("O_SHARING");
	public static final Button O_NEW_VERSION =  new Button("O_NEW_VERSION");

	public static final Button B_TAB_TRASH = new Button("B_TAB_TRASH");
	
	public static final Button B_TAB_SEARCH = new Button("B_TAB_SEARCH");
	
	public static final Button B_SETTINGS = new Button("B_SETTINGS");

	public static final Button B_DONE = new Button("B_DONE");
	
	public static final Button B_UNLINK_AND_WIPE = new Button("B_UNLINK_AND_WIPE");
	
	//Briefcase buttons
	public static final Button B_UPLOAD_FILE = new Button("B_UPLOAD_FILE");
	public static final Button B_EDIT_FILE = new Button("B_EDIT_FILE");
	public static final Button B_OPEN_IN_SEPARATE_WINDOW = new Button("B_OPEN_IN_SEPARATE_WINDOW");
	public static final Button B_LAUNCH_IN_SEPARATE_WINDOW = new Button("B_LAUNCH_IN_SEPARATE_WINDOW");
	public static final Button O_SEND_AS_ATTACHMENT = new Button("O_SEND_AS_ATTACHMENT");
	public static final Button O_SEND_LINK = new Button("O_SEND_LINK");
	public static final Button O_EDIT = new Button("O_EDIT");
	public static final Button O_OPEN = new Button("O_OPEN");
	public static final Button O_DELETE = new Button("O_DELETE");
	public static final Button O_TAG_FILE = new Button("O_TAG_FILE");
	public static final Button O_RENAME = new Button("O_RENAME");
	public static final Button O_CHECK_IN_FILE = new Button("O_CHECK_IN_FILE");
	public static final Button O_DISCARD_CHECK_OUT = new Button("O_DISCARD_CHECK_OUT");
	public static final Button B_TREE_EDIT_PROPERTIES = new Button("B_TREE_EDIT_PROPERTIES");

	
    //Addressbook button
	public static final Button B_EDIT = new Button("zb__CNS__EDIT");
	public static final Button B_FILEAS = new Button("td$=_FILE_AS_select_container");
	
	public static final Button B_CONTACTGROUP = new Button("zmi__Contacts__CONTACTGROUP_MENU");
  
	public static final Button O_SEARCH_MAIL_SENT_TO_CONTACT = new Button("O_SEARCH_MAIL_SENT_TO_CONTACT");
	public static final Button O_SEARCH_MAIL_RECEIVED_FROM_CONTACT = new Button("O_SEARCH_MAIL_RECEIVED_FROM_CONTACT");
	 
	//Addressbook alphabet bar buttons
	public static final Button B_AB_ALL = new Button("0"); //_idx="0"
	public static final Button B_AB_123 = new Button("1");
	public static final Button B_AB_A = new Button("2");
	public static final Button B_AB_B = new Button("3");
	public static final Button B_AB_C = new Button("4");
	public static final Button B_AB_D = new Button("5");
	public static final Button B_AB_E = new Button("6");
	public static final Button B_AB_F = new Button("7");
	public static final Button B_AB_G = new Button("8");
	public static final Button B_AB_H = new Button("9");
	public static final Button B_AB_I = new Button("10");
	public static final Button B_AB_J = new Button("11");
	public static final Button B_AB_K = new Button("12");
	public static final Button B_AB_L = new Button("13");
	public static final Button B_AB_M = new Button("14");
	public static final Button B_AB_N = new Button("15");
	public static final Button B_AB_O = new Button("16");
	public static final Button B_AB_P = new Button("17");
	public static final Button B_AB_Q = new Button("18");
	public static final Button B_AB_R = new Button("19");
	public static final Button B_AB_S = new Button("20");
	public static final Button B_AB_T = new Button("21");
	public static final Button B_AB_U = new Button("22");
	public static final Button B_AB_V = new Button("23");
	public static final Button B_AB_W = new Button("24");
	public static final Button B_AB_X = new Button("25");
	public static final Button B_AB_Y = new Button("26");
	public static final Button B_AB_Z = new Button("27");

	

	//TODO: add more
	
	// Task buttons
	public static final Button B_TASK_FILTERBY = new Button("B_TASK_FILTERBY");
	public static final Button B_TASK_MARKCOMPLETED = new Button("B_TASK_MARKCOMPLETED");
	public static final Button O_TASK_TODOLIST = new Button("O_TASK_TODOLIST");
	
	// Tree buttons
	public static final Button B_TREE_FOLDERS_OPTIONS = new Button("B_TREE_FOLDERS_PROPERTIES");
	public static final Button B_TREE_SEARCHES_OPTIONS = new Button("B_TREE_FOLDERS_PROPERTIES");
	public static final Button B_TREE_TAGS_OPTIONS = new Button("B_TREE_TAGS_PROPERTIES");

	public static final Button B_TREE_NEWFOLDER = new Button("B_TREE_NEWFOLDER");
	public static final Button B_TREE_NEWADDRESSBOOK = new Button("B_TREE_NEWADDRESSBOOK");
	public static final Button B_TREE_NEWCALENDAR = new Button("B_TREE_NEWCALENDAR");
	public static final Button B_TREE_NEW_EXTERNAL_CALENDAR = new Button("B_TREE_NEW_EXTERNAL_CALENDAR");
	public static final Button B_TREE_NEWTASKLIST = new Button("B_TREE_NEWTASKLIST");
	public static final Button B_TREE_NEWBRIEFCASE = new Button("B_TREE_NEWBRIEFCASE");
	public static final Button B_TREE_BRIEFCASE_EXPANDCOLLAPSE = new Button("B_TREE_BRIEFCASE_EXPANDCOLLAPSE");
	public static final Button B_TREE_NEWTAG = new Button("B_TREE_NEWTAG");
	public static final Button B_TREE_RENAMETAG = new Button("B_TREE_RENAMETAG");
	public static final Button B_TREE_DELETE = new Button("B_TREE_DELETE");
	public static final Button B_TREE_EDIT = new Button("B_TREE_EDIT");
	public static final Button B_TREE_FIND_SHARES = new Button("B_TREE_FIND_SHARES");
	// Tree buttons (Mail folders)
	public static final Button B_TREE_FOLDER_MARKASREAD = new Button("B_TREE_FOLDER_MARKASREAD");
	public static final Button B_TREE_FOLDER_EXPANDALL = new Button("B_TREE_FOLDER_EXPANDALL");
	public static final Button B_TREE_FOLDER_EMPTY = new Button("B_TREE_FOLDER_EMPTY");
	public static final Button B_TREE_FOLDER_GET_EXTERNAL = new Button("B_TREE_FOLDER_GET_EXTERNAL");
	public static final Button B_TREE_SHOW_REMAINING_FOLDERS = new Button("B_TREE_SHOW_REMAINING_FOLDERS");

	// Mail 'Display' buttons
	public static final Button B_ACCEPT = new Button("B_ACCEPT");
	public static final Button B_ACCEPT_DROPDOWN = new Button("B_ACCEPT_DROPDOWN");
	public static final Button B_DECLINE = new Button("B_DECLINE");
	public static final Button B_DECLINE_DROPDOWN = new Button("B_DECLINE_DROPDOWN");
	public static final Button B_TENTATIVE = new Button("B_TENTATIVE");
	public static final Button B_TENTATIVE_DROPDOWN = new Button("B_TENTATIVE_DROPDOWN");
	public static final Button B_ACCEPT_SHARE = new Button("B_ACCEPT_SHARE");;
	public static final Button B_DECLINE_SHARE = new Button("B_DECLINE_SHARE");;

	public static final Button B_PROPOSE_NEW_TIME = new Button("B_PROPOSE_NEW_TIME");
	public static final Button B_VIEW_ENTIRE_MESSAGE = new Button("B_VIEW_ENTIRE_MESSAGE");
	public static final Button B_HIGHLIGHT_OBJECTS = new Button("B_HIGHLIGHT_OBJECTS");
	
	// Calendar
	public static final Button B_SAVEANDCLOSE = new Button("B_SAVEANDCLOSE");
	public static final Button B_REFRESH = new Button("B_REFRESH");
	
	public static final Button B_VIEW = new Button("B_VIEWTOOLBAR");
	public static final Button O_LISTVIEW_DAY = new Button("O_LISTVIEW_DAY");
	public static final Button O_LISTVIEW_WORKWEEK = new Button("O_LISTVIEW_WORKWEEK");
	public static final Button O_LISTVIEW_WEEK = new Button("O_LISTVIEW_WEEK");
	public static final Button O_LISTVIEW_MONTH = new Button("O_LISTVIEW_MONTH");
	public static final Button O_LISTVIEW_LIST = new Button("O_LISTVIEW_LIST");
	public static final Button O_LISTVIEW_SCHEDULE = new Button("O_LISTVIEW_SCHEDULE");
	public static final Button O_LISTVIEW_FREEBUSY = new Button("O_LISTVIEW_FREEBUSY");
	
	public static final Button O_VIEW_DAY_MENU = new Button("O_VIEW_DAY_MENU");
	public static final Button O_VIEW_WORK_WEEK_MENU = new Button("O_VIEW_WORK_WEEK_MENU");
	public static final Button O_VIEW_WEEK_MENU = new Button("O_VIEW_WEEK_MENU");
	public static final Button O_VIEW_MONTH_MENU = new Button("O_VIEW_MONTH_MENU");
	public static final Button O_VIEW_LIST_MENU = new Button("O_VIEW_LIST_MENU");
	public static final Button O_VIEW_SCHEDULE_MENU = new Button("O_VIEW_SCHEDULE_MENU");
	public static final Button O_OPEN_MENU = new Button("O_OPEN_MENU");
	public static final Button O_PRINT_MENU = new Button("O_PRINT_MENU");
	public static final Button O_ACCEPT_MENU = new Button("O_ACCEPT_MENU");
	public static final Button O_TENTATIVE_MENU = new Button("O_TENTATIVE_MENU");
	public static final Button O_DECLINE_MENU = new Button("O_DECLINE_MENU");
	public static final Button O_EDIT_REPLY_MENU = new Button("O_EDIT_REPLY_MENU");
	public static final Button O_EDIT_REPLY_ACCEPT_SUB_MENU = new Button("O_EDIT_REPLY_ACCEPT_SUB_MENU");
	public static final Button O_EDIT_REPLY_TENTATIVE_SUB_MENU = new Button("O_EDIT_REPLY_TENTATIVE_SUB_MENU");
	public static final Button O_EDIT_REPLY_DECLINE_SUB_MENU = new Button("O_EDIT_REPLY_DECLINE_SUB_MENU");
	public static final Button O_PROPOSE_NEW_TIME_MENU = new Button("O_PROPOSE_NEW_TIME_MENU");
	public static final Button O_CREATE_A_COPY_MENU = new Button("O_CREATE_A_COPY_MENU");
	public static final Button O_REPLY_MENU = new Button("O_REPLY_MENU");
	public static final Button O_REPLY_TO_ALL_MENU = new Button("O_REPLY_TO_ALL_MENU");
	public static final Button O_FORWARD_MENU = new Button("O_FORWARD_MENU");
	public static final Button O_DELETE_MENU = new Button("O_DELETE_MENU");
	public static final Button O_CANCEL_MENU = new Button("O_CANCEL_MENU");
	public static final Button O_MOVE_MENU = new Button("O_MOVE_MENU");
	public static final Button O_TAG_APPOINTMENT_MENU = new Button("O_TAG_APPOINTMENT_MENU");
	public static final Button O_TAG_APPOINTMENT_NEW_TAG_SUB_MENU = new Button("O_TAG_APPOINTMENT_NEW_TAG_SUB_MENU");
	public static final Button O_TAG_APPOINTMENT_REMOVE_TAG_SUB_MENU = new Button("O_TAG_APPOINTMENT_REMOVE_TAG_SUB_MENU");
	public static final Button O_SHOW_ORIGINAL_MENU = new Button("O_SHOW_ORIGINAL_MENU");
	public static final Button O_QUICK_COMMANDS_MENU = new Button("O_QUICK_COMMANDS_MENU");
	public static final Button O_INSTANCE_MENU = new Button("O_INSTANCE_MENU");
	public static final Button O_SERIES_MENU = new Button("O_SERIES_MENU");
	public static final Button O_OPEN_INSTANCE_MENU = new Button("O_OPEN_INSTANCE_MENU");
	public static final Button O_FORWARD_INSTANCE_MENU = new Button("O_FORWARD_INSTANCE_MENU");
	public static final Button O_DELETE_INSTANCE_MENU = new Button("O_DELETE_INSTANCE_MENU");
	public static final Button O_OPEN_SERIES_MENU = new Button("O_OPEN_SERIES_MENU");
	public static final Button O_FORWARD_SERIES_MENU = new Button("O_FORWARD_SERIES_MENU");
	public static final Button O_NEW_APPOINTMENT_MENU = new Button("O_NEW_APPOINTMENT_MENU");
	public static final Button O_NEW_ALL_DAY_APPOINTMENT_MENU = new Button("O_NEW_ALL_DAY_APPOINTMENT_MENU");
	public static final Button O_GO_TO_TODAY_MENU = new Button("O_GO_TO_TODAY_MENU");
	public static final Button O_VIEW_MENU = new Button("O_VIEW_MENU");
	public static final Button O_VIEW_DAY_SUB_MENU = new Button("O_VIEW_DAY_SUB_MENU");
	public static final Button O_VIEW_WORK_WEEK_SUB_MENU = new Button("O_VIEW_WORK_WEEK_SUB_MENU");
	public static final Button O_VIEW_WEEK_SUB_MENU = new Button("O_VIEW_WEEK_SUB_MENU");
	public static final Button O_VIEW_MONTH_SUB_MENU = new Button("O_VIEW_MONTH_SUB_MENU");
	public static final Button O_VIEW_LIST_SUB_MENU = new Button("O_VIEW_LIST_SUB_MENU");
	public static final Button O_VIEW_SCHEDULE_SUB_MENU = new Button("O_VIEW_SCHEDULE_SUB_MENU");
	
	public static final Button B_OPEN_THIS_INSTANCE = new Button("B_OPEN_THIS_INSTANCE");
	public static final Button B_OPEN_THE_SERIES = new Button("B_OPEN_THE_SERIES");

	// Calendar dialogs
	public static final Button B_SEND_CANCELLATION = new Button("B_SEND_CANCELLATION");
	public static final Button B_EDIT_CANCELLATION = new Button("B_EDIT_CANCELLATION");

	// Calendar tree
	public static final Button B_RELOAD = new Button("B_RELOAD");

	// Preferences
	public static final Button B_CHANGE_PASSWORD 		= new Button("B_CHANGE_PASSWORD");
	public static final Button B_NEW_FILTER 			= new Button("B_NEW_FILTER");
	public static final Button B_NEW_QUICK_COMMAND		= new Button("B_NEW_QUICK_COMMAND");
	public static final Button B_EDIT_QUICK_COMMAND		= new Button("B_EDIT_QUICK_COMMAND");
	public static final Button B_DELETE_QUICK_COMMAND	= new Button("B_DELETE_QUICK_COMMAND");


	//// Admin Console
	
	// Accounts buttons
	public static final Button O_ACCOUNTS_ACCOUNT = new Button("O_ACCOUNTS_ACCOUNT");

	// Distribution List  buttons
	public static final Button O_DISTRIBUTIUONLISTS_DISTRIBUTIONLIST=new Button("O_DISTRIBUTIUONLISTS_DISTRIBUTIONLIST");
	
	// Aliases buttons
	public static final Button O_ALIASES_ALIAS = new Button("O_ALIASES_ALIAS");
	
	// Resources button
	public static final Button O_RESOURCES_RESOURCE = new Button("O_RESOURCES_RESOURCE");
	

	
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
