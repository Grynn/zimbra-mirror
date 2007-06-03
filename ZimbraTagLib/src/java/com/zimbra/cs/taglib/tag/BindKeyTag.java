package com.zimbra.cs.taglib.tag;

import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.taglib.bean.ZUserAgentBean;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;
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

    private static final String BINDKEY_CACHE = "BindKeyTag.CACHE";


    private String mKey;
    private String mMessage;
    private String mBasename = "/keys/ZhKeys";
    private String mId;
    private String mFunc;
    private String mUrl;

    public void setId(String id) { mId = id; }
    public void setKey(String key) { mKey = key; }
    public void setMessage(String message) { mMessage = message; }
    public void setFunc(String func) { mFunc = func; }
    public void setUrl(String url) { mUrl = url; }
    public void setBasename(String basename) { mBasename = basename; }

    private String resolveMessageKey(JspContext ctxt, String message) {
        ZUserAgentBean ua = GetUserAgentTag.getUserAgent(ctxt);

        if (ua != null) {
            String os = ua.getIsOsWindows() ? ".win" : ua.getIsOsMac() ? ".mac" : ua.getIsOsLinux() ? ".linux" : null;
            if (os != null) {
                String key = LocaleSupport.getLocalizedMessage((PageContext)ctxt, message+os, mBasename);
                if (!key.startsWith("???")) return key;
            }
        }
        return LocaleSupport.getLocalizedMessage((PageContext)ctxt, message, mBasename);
    }

    private String getJavaScriptForMessage(JspContext ctxt, String message) throws JspTagException {
        Map<String,String> cache = (Map<String,String>) ctxt.getAttribute(BINDKEY_CACHE, PageContext.SESSION_SCOPE);
        if (cache == null) {
            cache = new HashMap<String,String>();
            ctxt.setAttribute(BINDKEY_CACHE, cache, PageContext.SESSION_SCOPE);
        }

        String js = cache.get(message);
        if (js == null) {
            //System.out.println("bindKey: cache miss for: "+message);
            String key = resolveMessageKey(ctxt, message);
            if (key.startsWith("???")) {
                System.err.print("bindKey: unresolved prop: "+message);
                return null;
            } else {
                js = getJavaScriptForKey(key);
                cache.put(message, js);
            }
        } else {
            //System.out.println("bindKey: cache hit for: "+message);
        }
        return js;
    }

    private String getJavaScriptForKey(String k) throws JspTagException {
        StringBuilder js = new StringBuilder();

        for (String keySeq: k.split(";")) {
            String keys[] = keySeq.trim().split(",");
            if (keys.length == 0)
                throw new JspTagException("invalid key binding: "+k);
            StringBuilder sb = new StringBuilder();
            for (String key : keys) {
                sb.append(':').append(getCode(key.trim()));
            }
            if (mFunc != null)
                js.append(String.format("bindKey('%s', %s);%n", sb.toString(), mFunc));
            else if (mUrl != null)
                js.append(String.format("bindKey('%s', function(){ window.location=\"%s\";});%n", StringUtil.jsEncode(sb.toString()), mUrl));
            else
                js.append(String.format("bindKey('%s', '%s');%n", sb.toString(), mId));
        }
        return js.toString();
    }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();

        if (mId == null && mFunc == null && mUrl == null) {
            throw new JspTagException("The bindKey tag must have either a function, url, or an id");
        }

        if (mKey == null && mMessage == null) {
            throw new JspTagException("The bindKey tag must have either a key or a message attribute");
        }

        String js = mMessage != null ? getJavaScriptForMessage(jctxt, mMessage) : getJavaScriptForKey(mKey);
        if (js != null) {
            JspWriter out = jctxt.getOut();
            out.write(js);
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
