package framework.ui;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.thoughtworks.selenium.DefaultSelenium;

import framework.core.ClientSession;
import framework.core.ClientSessionFactory;
import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * The <code>AbsSeleniumObject</code> class is a base class that all "GUI"
 * objects can derive from, allowing access to the DefaultSelenium methods.
 * <p>
 * The <code>AbsSeleniumObject</code> is implemented as a thread safe (on
 * the test class level) way to access DefaultSelenium methods.
 * <p>
 * It is intended that Pages, Forms, Trees, etc. will derive from 
 * AbsSeleniumObject and call DefaultSelenium methods using AbsSeleniumObject
 * methods.  The class implementations should not use the {@link ClientSession}
 * objects directly.
 * <p>
 * Selenium methods start with a lower case "s", so that 
 * {@link DefaultSelenium#click(String)}
 * can be accessed using
 * {@link #sClick(String)}.
 * <p>
 * Zimbra specific methods start with a lower case "z", such as the
 * Zimbra-specific implementation of click {@link #zClick(String)},
 * which performs the more stable action of MOUSE_DOWN followed by
 * MOUSE_UP.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsSeleniumObject.class);

	public enum PopupButton {
		Yes, No, Cancel, Help
	}
	
	public enum ListNavButton {
		Previous, Next
	}
		
	public AbsSeleniumObject() {
		logger.info("new AbsSeleniumObject");
	}
	

	/**
	 * Zimbra: return if the specified element is visible per style coordinates
	 * @param locator
	 * @param leftLimit
	 * @param topLimit
	 * @return
	 */
	public boolean zIsVisiblePerPosition(String locator, int leftLimit, int topLimit) {

		// Check if the locator is present
		if ( !sIsElementPresent(locator) ) {
			logger.info("isVisiblePerPosition("+ locator +") element is not present");
			return (false);
		}
		
		// Find the current position
		Number left = ClientSessionFactory.session().selenium().getElementPositionLeft(locator);
		Number top = ClientSessionFactory.session().selenium().getElementPositionTop(locator);
		
		// If the position is less than the limits, then it is hidden
		boolean hidden = ( (left.intValue() < leftLimit) && (top.intValue() < topLimit) );
		logger.info("isVisiblePerPosition("+ locator +") - (left, top) = ("+ left.intValue() +", "+ top.intValue() +") (limit, limit) = ("+ leftLimit +", "+ topLimit +") "+ (!hidden));
		return (!hidden);
	}
	
	/**
	 * Send a keyboard shortcut using Robot
	 * @param keyEvent Key Event to send, e.g. KeyEvent.VK_N
	 * @throws HarnessException
	 */
	public void zPressKeyboardShortcut(int keyEvent) throws HarnessException {
		logger.info("zPressKeyboardShortcut("+ keyEvent +")");

		try {
			
			Robot zRobot = new Robot();

			SleepUtil.sleep(3000);
			zRobot.keyPress(keyEvent);
			zRobot.keyRelease(keyEvent);
			SleepUtil.sleep(3000);

		} catch (AWTException e) {
			throw new HarnessException("Unable to send keyboard shortcut "+ keyEvent, e);
		}
		
	}
	
	
	public void zTypeCharacters(String chars) throws HarnessException {
		logger.info("zTypeCharacters("+ chars +")");

		Keyboard keyboard = new Keyboard();
		keyboard.type(chars);
		
	}
	
	/**
	 * Execute mouseDown followed by mouseUp on a loator
	 * @param locator
	 * @throws HarnessException
	 */
	public void zClick(String locator) {
		logger.info("zClick("+ locator +")");
		ClientSessionFactory.session().selenium().mouseDown(locator);
		ClientSessionFactory.session().selenium().mouseUp(locator);
	}

	/**
	 * Execute select on a windowID
	 * @param windowID
	 * @throws HarnessException
	 */
	public void zSelectWindow(String windowID) throws HarnessException {
		logger.info("zSelectWindow("+ windowID +")");

        this.sSelectWindow(windowID);

        this.sWindowFocus();   

        this.sWindowMaximize();

	}
	
	
	//// ***
	// Start: Selenium methods
	//// ***
	
	/**
	 * DefaultSelenium.getHtmlSource()
	 * @param locator
	 */
	public String sGetHtmlSource() throws HarnessException {
		String htmlString = ClientSessionFactory.session().selenium().getHtmlSource();
		logger.info("getHtmlSource()");
		return (htmlString);
	}
	
	/**
	 * DefaultSelenium.getSelectedId()
	 * @param locator
	 */
	public String sGetSelectedId(String locator) {
		String id = ClientSessionFactory.session().selenium().getSelectedId(locator);
		logger.info("getSelectedId(" + locator + ") = "+ id);
		return (id);
	}
	
	/**
	 * DefaultSelenium.chooseOkOnNextConfirmation()
	 */
	public void sChooseOkOnNextConfirmation() {
		ClientSessionFactory.session().selenium().chooseOkOnNextConfirmation();
		logger.info("chooseOkOnNextConfirmation()");
	}
	
	/**
	 * DefaultSelenium.click()
	 */
	public void sClick(String locator) {
		ClientSessionFactory.session().selenium().click(locator);
		logger.info("click(" + locator + ")");
	}
	
	/**
	 * DefaultSelenium.mouseDown()
	 */
	public void sMouseDown(String locator) {
		ClientSessionFactory.session().selenium().mouseDown(locator);
		logger.info("mouseDown(" + locator + ")");
	}
	
	/**
	 * DefaultSelenium.mouseUp()
	 */
	public void sMouseUp(String locator) {
		ClientSessionFactory.session().selenium().mouseUp(locator);
		logger.info("mouseUp(" + locator + ")");
	}
	
	/**
	 * DefaultSelenium.focus()
	 */
	public void sFocus(String locator) {
		ClientSessionFactory.session().selenium().focus(locator);
		logger.info("focus(" + locator + ")");
	}
	
	/**
	 * DefaultSelenium.isElementPresent()
	 */
	public boolean sIsElementPresent(String locator) {
		boolean present = ClientSessionFactory.session().selenium().isElementPresent(locator);
		logger.info("isElementPresent(" + locator + ") = " + present);
		return (present);
	}

	/**
	 * DefaultSelenium.getXpathCount()
	 */
	public int sGetXpathCount(String xpath) {
		int count = ClientSessionFactory.session().selenium().getXpathCount(xpath).intValue();
		logger.info("getXpathCount(" + xpath + ") = " + count);
		return (count);
	}

	/**
	 * DefaultSelenium.getAttribute()
	 */
	public String sGetAttribute(String locator) {
		String attrs = ClientSessionFactory.session().selenium().getAttribute(locator);
		logger.info("getAttribute(" + locator + ") = " + attrs);
		return (attrs);
	}

	/**
	 * DefaultSelenium.isVisible()
	 */
	public boolean sIsVisible(String locator) {
		boolean visible = ClientSessionFactory.session().selenium().isVisible(locator);
		logger.info("isVisible(" + locator + ") = " + visible);
		return (visible);
	}

	/**
	 * DefaultSelenium.isChecked()
	 */
	public boolean sIsChecked(String locator) {
		boolean checked = ClientSessionFactory.session().selenium().isChecked(locator);
		logger.info("isChecked(" + locator + ") = " + checked);
		return (checked);
	}


	/**
	 * DefaultSelenium.getText()
	 */
	public String sGetText(String locator) {
		String text = ClientSessionFactory.session().selenium().getText(locator);
		logger.info("DefaultSelenium.getText(" + locator + ") = " + text);
		return (text);
	}
	
	/**
	 * DefaultSelenium.getValue()
	 */
	public String sGetValue(String locator) {
		String text = ClientSessionFactory.session().selenium().getValue(locator);
		logger.info("DefaultSelenium.getValue(" + locator + ") = " + text);
		return (text);
	}
	
	/**
	 * DefaultSelenium.type()
	 */
	public void sType(String locator, String text) {
		ClientSessionFactory.session().selenium().type(locator, text);
		logger.info("type(" + locator + ", " + text + ")");
	}

	/**
	 * DefaultSelenium.typeKeys()
	 */
	public void sTypeKeys(String locator, String text) {
		ClientSessionFactory.session().selenium().typeKeys(locator, text);
		logger.info("typeKeys(" + locator + ", " + text + ")");
	}

	/**
	 * DefaultSelenium.keyPressNative()
	 */
	public void sKeyPressNative(String code) {
		ClientSessionFactory.session().selenium().keyPressNative(code);
		logger.info("keyPressNative(" + code + ")");
	}


	/**
	 * DefaultSeleniu.selectFrame()
	 */
	public void sSelectFrame(String locator) {
		ClientSessionFactory.session().selenium().selectFrame(locator);
		logger.info("sSelectFrame(" + locator + ")");
	}
	

	/**
	 * DefaultSelenium.selectWindow()
	 */
	public void sSelectWindow(String windowID) {
		ClientSessionFactory.session().selenium().selectWindow(windowID);
		logger.info("sSelectWindow(" + windowID + ")");
	}
	
	/**
	 * DefaultSelenium.windowFocus()
	 */
	public void sWindowFocus() {
		ClientSessionFactory.session().selenium().windowFocus();
		logger.info("sWindowFocus()");
	}
	
	/**
	 * DefaultSelenium.wwindowMaximize()
	 */
	public void sWindowMaximize() {
		ClientSessionFactory.session().selenium().windowMaximize();
		logger.info("sWindowMaximize()");
	}	
	
	//// ***
	// End: Selenium methods
	//// ***

	
	//// ***
	// Start: Robot methods
	//// ***
	public class Keyboard {

	    private Robot robot;

	    public Keyboard() throws HarnessException {
	        try {
				this.robot = new Robot();
			} catch (AWTException e) {
				throw new HarnessException(e);
			}
	    }

	    public Keyboard(Robot robot) {
	        this.robot = robot;
	    }

	    public void type(String characters) {
	    	for (char c : characters.toCharArray()) {
	    		type(c);
	    	}
	    	
	    	// For some reason, need a sleep here otherwise selenium
	    	// can't recognize the typed string
	    	// e.g. if the string is foo123, then getValue(locator) will only return foo1 (23 missing)
	    	//
	    	SleepUtil.sleep(3000);
	    }

	    private void type(char character) {
	    	logger.info("Keyboard: "+ character);
	    	
	        switch (character) {
	        case 'a': doType(KeyEvent.VK_A); break;
	        case 'b': doType(KeyEvent.VK_B); break;
	        case 'c': doType(KeyEvent.VK_C); break;
	        case 'd': doType(KeyEvent.VK_D); break;
	        case 'e': doType(KeyEvent.VK_E); break;
	        case 'f': doType(KeyEvent.VK_F); break;
	        case 'g': doType(KeyEvent.VK_G); break;
	        case 'h': doType(KeyEvent.VK_H); break;
	        case 'i': doType(KeyEvent.VK_I); break;
	        case 'j': doType(KeyEvent.VK_J); break;
	        case 'k': doType(KeyEvent.VK_K); break;
	        case 'l': doType(KeyEvent.VK_L); break;
	        case 'm': doType(KeyEvent.VK_M); break;
	        case 'n': doType(KeyEvent.VK_N); break;
	        case 'o': doType(KeyEvent.VK_O); break;
	        case 'p': doType(KeyEvent.VK_P); break;
	        case 'q': doType(KeyEvent.VK_Q); break;
	        case 'r': doType(KeyEvent.VK_R); break;
	        case 's': doType(KeyEvent.VK_S); break;
	        case 't': doType(KeyEvent.VK_T); break;
	        case 'u': doType(KeyEvent.VK_U); break;
	        case 'v': doType(KeyEvent.VK_V); break;
	        case 'w': doType(KeyEvent.VK_W); break;
	        case 'x': doType(KeyEvent.VK_X); break;
	        case 'y': doType(KeyEvent.VK_Y); break;
	        case 'z': doType(KeyEvent.VK_Z); break;
	        case 'A': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
	        case 'B': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
	        case 'C': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
	        case 'D': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
	        case 'E': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
	        case 'F': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
	        case 'G': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
	        case 'H': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
	        case 'I': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
	        case 'J': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
	        case 'K': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
	        case 'L': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
	        case 'M': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
	        case 'N': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
	        case 'O': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
	        case 'P': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
	        case 'Q': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
	        case 'R': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
	        case 'S': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
	        case 'T': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
	        case 'U': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
	        case 'V': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
	        case 'W': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
	        case 'X': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
	        case 'Y': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
	        case 'Z': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;
	        case '`': doType(KeyEvent.VK_BACK_QUOTE); break;
	        case '0': doType(KeyEvent.VK_0); break;
	        case '1': doType(KeyEvent.VK_1); break;
	        case '2': doType(KeyEvent.VK_2); break;
	        case '3': doType(KeyEvent.VK_3); break;
	        case '4': doType(KeyEvent.VK_4); break;
	        case '5': doType(KeyEvent.VK_5); break;
	        case '6': doType(KeyEvent.VK_6); break;
	        case '7': doType(KeyEvent.VK_7); break;
	        case '8': doType(KeyEvent.VK_8); break;
	        case '9': doType(KeyEvent.VK_9); break;
	        case '-': doType(KeyEvent.VK_MINUS); break;
	        case '=': doType(KeyEvent.VK_EQUALS); break;
	        case '~': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE); break;
	        case '!': doType(KeyEvent.VK_EXCLAMATION_MARK); break;
	        case '@': doType(KeyEvent.VK_AT); break;
	        case '#': doType(KeyEvent.VK_NUMBER_SIGN); break;
	        case '$': doType(KeyEvent.VK_DOLLAR); break;
	        case '%': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5); break;
	        case '^': doType(KeyEvent.VK_CIRCUMFLEX); break;
	        case '&': doType(KeyEvent.VK_AMPERSAND); break;
	        case '*': doType(KeyEvent.VK_ASTERISK); break;
	        case '(': doType(KeyEvent.VK_LEFT_PARENTHESIS); break;
	        case ')': doType(KeyEvent.VK_RIGHT_PARENTHESIS); break;
	        case '_': doType(KeyEvent.VK_UNDERSCORE); break;
	        case '+': doType(KeyEvent.VK_PLUS); break;
	        case '\t': doType(KeyEvent.VK_TAB); break;
	        case '\n': doType(KeyEvent.VK_ENTER); break;
	        case '[': doType(KeyEvent.VK_OPEN_BRACKET); break;
	        case ']': doType(KeyEvent.VK_CLOSE_BRACKET); break;
	        case '\\': doType(KeyEvent.VK_BACK_SLASH); break;
	        case '{': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET); break;
	        case '}': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET); break;
	        case '|': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH); break;
	        case ';': doType(KeyEvent.VK_SEMICOLON); break;
	        case ':': doType(KeyEvent.VK_COLON); break;
	        case '\'': doType(KeyEvent.VK_QUOTE); break;
	        case '"': doType(KeyEvent.VK_QUOTEDBL); break;
	        case ',': doType(KeyEvent.VK_COMMA); break;
	        case '<': doType(KeyEvent.VK_LESS); break;
	        case '.': doType(KeyEvent.VK_PERIOD); break;
	        case '>': doType(KeyEvent.VK_GREATER); break;
	        case '/': doType(KeyEvent.VK_SLASH); break;
	        case '?': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
	        case ' ': doType(KeyEvent.VK_SPACE); break;
	        default:
	                throw new IllegalArgumentException("Cannot type character " + character);
	        }
	    }

	    private void doType(int... keyCodes) {
	        doType(keyCodes, 0, keyCodes.length);
	    }

	    private void doType(int[] keyCodes, int offset, int length) {
	        if (length == 0) {
	                return;
	        }

	        robot.keyPress(keyCodes[offset]);
	        doType(keyCodes, offset + 1, length - 1);
	        robot.keyRelease(keyCodes[offset]);
	    }

	}

}
