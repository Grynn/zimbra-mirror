package com.zimbra.cs.taglib.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class BindKeyTag extends ZimbraSimpleTag {

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

    private String mKey;
    private String mId;
    private String mFunc;

    public void setId(String id) { mId = id; }
    public void setKey(String key) { mKey = key; }
    public void setFunc(String func) { mFunc = func; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();

        if (mId == null && mFunc == null) {
            throw new JspTagException("The bindKey tag must have either a function or an id");
        }

        JspWriter out = jctxt.getOut();

        for (String keySeq: mKey.split(";")) {
            String keys[] = keySeq.trim().split(",");
            if (keys.length == 0)
                throw new JspTagException("invalid key binding: "+mKey);
            StringBuilder sb = new StringBuilder();
            for (String key : keys) {
                sb.append(':').append(getCode(key));
            }
            if (mFunc != null)
                out.println(String.format("bindKey('%s', %s);", sb.toString(), mFunc));
            else
                out.println(String.format("bindKey('%s', '%s');", sb.toString(), mId));
        }
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

}
