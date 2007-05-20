package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.taglib.tag.contact.ContactOpTag;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyBindingsTag extends ContactOpTag {

    // Key names
    public static final String CTRL	= "Ctrl+";
    public static final String META = "Meta+";
    public static final String ALT = "Alt+";
    public static final String SHIFT = "Shift+";
    public static final String ARROW_DOWN = "ArrowDown";
    public static final String ARROW_LEFT = "ArrowLeft";
    public static final String ARROW_RIGHT = "ArrowRight";
    public static final String ARROW_UP = "ArrowUp";
    public static final String BACKSPACE = "Backspace";
    public static final String COMMA = "Comma";
    public static final String SEMICOLON = "Semicolon";
    public static final String DELETE = "Del";
    public static final String END = "End";
    public static final String ENTER = "Enter";
    public static final String ESC = "Esc";
    public static final String HOME = "Home";
    public static final String PAGE_DOWN = "PgDown";
    public static final String PAGE_UP = "PgUp";
    public static final String SPACE = "Space";
    public static final String BACKSLASH = "Backslash";

    private static final Map<Integer, String> sKeyCodeToString = new HashMap<Integer, String>();
    private static final Map<String, Integer> sStringToKeyCode = new HashMap<String, Integer>();

    private static void mapKey(int keyCode, String str) {
        sKeyCodeToString.put(keyCode, str);
        sStringToKeyCode.put(str, keyCode);
    }

    static {
        mapKey(18, ALT);
        mapKey(40, ARROW_DOWN);
        mapKey(37, ARROW_LEFT);
        mapKey(39, ARROW_RIGHT);
        mapKey(38, ARROW_UP);
        mapKey(8, BACKSPACE);
        mapKey(188, COMMA);
        mapKey(186, SEMICOLON);
        mapKey(59, SEMICOLON);
        mapKey(17, CTRL);
        mapKey(46, DELETE);
        mapKey(35, END);
        mapKey(13, ENTER);
        mapKey(27, ESC);
        mapKey(36, HOME);
        mapKey(91, META);
        mapKey(34, PAGE_DOWN);
        mapKey(33, PAGE_UP);
        mapKey(16, SHIFT);
        mapKey(32, SPACE);
        //mapKey(9, TAB);
        mapKey(220, BACKSLASH);

        // Function keys
        for (int i = 112; i < 124; i++) {
            mapKey(i, "F" + (i - 111));
        }

        // Take advantage of the fact that keycode for capital letters are the
        // same as the charcode values i.e. ASCII code
        for (char i = 65; i < 91; i++) {
            mapKey(i, Character.toString(i));
        }

        // Numbers 0 - 9
        for (char i = 48; i < 58; i++) {
            mapKey(i, Character.toString(i));
        }

        // punctuation
        mapKey(222, "'");
        mapKey(189, "-");
        mapKey(190, ".");
        mapKey(191, "/");
        mapKey(186, ";");
        mapKey(219, "[");
        mapKey(221, "]");
        mapKey(192, "`");
        mapKey(187, "=");
    }

    protected List<KeyBinding> mBindings = new ArrayList<KeyBinding>();

    public void addIdBinding(String key, String id) throws JspTagException {
        mBindings.add(new KeyBinding(key, id, false));
    }

    public void addFuncBinding(String key, String id) throws JspTagException {
        mBindings.add(new KeyBinding(key, id, true));
    }

    protected boolean allFieldsEmpty() {
        for (Map.Entry<String,String> entry : mAttrs.entrySet()) {
            if (entry.getValue() != null && entry.getValue().trim().length() > 0 && !entry.getKey().equalsIgnoreCase(Contact.A_fileAs))
                return false;
        }
        return true;
    }

    private String getCode(String seq) throws JspTagException {
        StringBuilder code = new StringBuilder();
        if (seq.contains(ALT)) code.append('a');
        if (seq.contains(CTRL)) code.append('c');
        if (seq.contains(META)) code.append('m');
        if (seq.contains(SHIFT)) code.append('s');
        Integer kc = sStringToKeyCode.get(seq.substring(seq.lastIndexOf('+') + 1));
        if (kc == null || kc == 0) throw new JspTagException("invalid key binding: "+seq);
        code.append(kc);
        return code.toString();
    }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        getJspBody().invoke(null);

        JspWriter out = jctxt.getOut();

        // TODO: cache key sequence to key codes
        for (KeyBinding binding : mBindings) {
            for (String keySeq: binding.getKey().split(";")) {
                String keys[] = keySeq.trim().split(",");
                if (keys.length == 0) throw new JspTagException("invalid key binding: "+binding.getKey());
                StringBuilder sb = new StringBuilder();
                for (String key : keys) {
                    sb.append(':').append(getCode(key));
                }
                if (binding.isFunc())
                    out.println(String.format("bindKey('%s', %s);", sb.toString(), binding.getId()));
                else
                    out.println(String.format("bindKey('%s', '%s');", sb.toString(), binding.getId()));
            }
        }
/*
try {
            if (mAttrs.isEmpty() || allFieldsEmpty())
                throw ZTagLibException.EMPTY_CONTACT("can't create an empty contact", null);

            String id = getMailbox().createContact(mFolderid, mTagids, mAttrs);
            getJspContext().setAttribute(mVar, id, PageContext.PAGE_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e);

        }
        */
    }

    static class KeyBinding {
        private String mKey;
        private String mId;
        private boolean mIsFunc;

        public KeyBinding(String key, String id, boolean isFunc) {
            mKey = key;
            mId = id;
            mIsFunc = isFunc;
        }

        public String getKey() { return mKey; }
        public String getId() { return mId; }
        public boolean isFunc() { return mIsFunc; }
    }
}
