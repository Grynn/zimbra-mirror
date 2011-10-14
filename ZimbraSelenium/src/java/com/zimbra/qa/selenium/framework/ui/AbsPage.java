package com.zimbra.qa.selenium.framework.ui;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.apache.log4j.*;

import com.zimbra.qa.selenium.framework.util.*;


/**
 * A <code>AbsPage</code> object represents any of the GUI classes, such as
 * Dialogs, Tabs, Forms, etc.
 * <p>
 * Implementing AbsPage classes must define the {@link AbsPage#zIsActive()} and
 * {@link AbsPage#zNavigateTo()} methods.  The test method classes can set a
 * "startingapp", which the harness will attempt to navigate-to before running
 * each test method.
 * <p>
 * @author Matt Rhoades
 *
 */
public abstract class AbsPage extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsPage.class);

	protected static final int PageLoadDelay = 30000; // wait 30 seconds for pages to load

	
	@Deprecated
	public enum PopupButton {
		Yes, No, Cancel, Help
	}
	
	@Deprecated
	public enum ListNavButton {
		Previous, Next
	}
		

	/**
	 * A pointer to the application that created this object
	 */
	protected AbsApplication MyApplication = null;

	/**
	 * A Keyboard object to send keyboard input to the screen
	 */
	protected Keyboard zKeyboard = new Keyboard();

	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public AbsPage(AbsApplication application) {
		MyApplication = application;
		
		logger.info("new "+ AbsPage.class.getCanonicalName());

	}
	
	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();
	
	/**
	 * Determines if this page is active, usually by detecting
	 * whether a GUI element is present or not.
	 * <p>
	 * @return true if active.  false if not.
	 * @throws HarnessException
	 */
	public abstract boolean zIsActive() throws HarnessException;

	/**
	 * Wait for this page to become active (default PageLoadDelay)
	 * @throws HarnessException
	 */
	public void zWaitForActive() throws HarnessException {
		zWaitForActive(PageLoadDelay);
	}
	
	/**
	 * Wait for this page to become active
	 * @throws HarnessException
	 */
	public void zWaitForActive(long millis) throws HarnessException {
		
		if ( zIsActive() ) {
			return; // Page became active
		}
		
		do {
			SleepUtil.sleep(SleepUtil.SleepGranularity);
			millis = millis - SleepUtil.SleepGranularity;
			if ( zIsActive() ) {
				return; // Page became active
			}
		} while (millis > SleepUtil.SleepGranularity);
		
		SleepUtil.sleep(millis);
		if ( zIsActive() ) {
			return;	// Page became active
		}

		throw new HarnessException("Page never became active");
	}
	
	/**
	 * Hover over a specified button
	 * @param button
	 * @return
	 * @throws HarnessException
	 */
	public AbsTooltip zHoverOver(Button button) throws HarnessException {
		throw new HarnessException("implement me");
	}

	private static class Coordinate {
		final int X;
		final int Y;
		
		public Coordinate(int x, int y) {
			this.X = x;
			this.Y = y;
		}
		
		/** 
		 * Print this coordinate in "x,y" format
		 */
		public String toString() {
			return (this.X + "," + this.Y);
		}
		
	}
	
	
	/**
	 * Drag and Drop a locator onto another locator
	 * @param locatorSource The locator item to drag
	 * @param locatorDestination The locator item to drop onto
	 * @throws HarnessException
	 */
	public void zDragAndDrop(String locatorSource, String locatorDestination) throws HarnessException {

		if ( !this.sIsElementPresent(locatorSource) ) {
			throw new HarnessException("locator (source) cannot be found: "+ locatorSource);
		}
		
		if ( !this.sIsElementPresent(locatorDestination) ) {
			throw new HarnessException("locator (destination) cannot be found: "+ locatorDestination);
		}
		
		SleepUtil.sleep(2000);
		
		// Get the coordinates for the locators
		Coordinate destination = new Coordinate(
				this.sGetElementPositionLeft(locatorDestination), 
				this.sGetElementPositionTop(locatorDestination));
		
		Coordinate source = new Coordinate(
				this.sGetElementPositionLeft(locatorSource), 
				this.sGetElementPositionTop(locatorSource));
		
		Coordinate relative = new Coordinate(
				destination.X - source.X,
				destination.Y - source.Y);
		
		logger.info("x,y coordinate of the objectToBeDroppedInto=" + destination);
		logger.info("x,y coordinate of the objectToBeDragged=" + source);
		logger.info("x,y coordinate of the objectToBeDroppedInto relative to objectToBeDragged = " + relative);
		
		// Hold the mouse down on the source
		this.sMouseDownAt(locatorSource, relative.toString());

		SleepUtil.sleep(1000);
		// Drag the mouse to the destination, plus the offset
		this.sMouseMoveAt(locatorDestination, relative.toString());

		// Wait a bit for things to happen
		SleepUtil.sleep(1000 * 3);

		this.sMouseMove(locatorDestination);
		this.sMouseOver(locatorDestination);

		SleepUtil.sleep(1000);
		// Release the mouse
		this.sMouseUpAt(locatorDestination, relative.toString());

		// Wait for the client to come back
		this.zWaitForBusyOverlay();		

	}

	/**
	 * Using java robot to do mouse click on the coordinate.
	 * This is needed for example to bring up the browser test window to the front most, becauase
	 * when using selenium to fire the mouse event, it won't bring the test window to the front most
	 * Usage instance: Mac OS Before Suite
	 * @param x coordinate x
	 * @param y coordinate y
	 * @throws HarnessException 
	 */
	public void zMouseClick(int x, int y) throws HarnessException {
	   Mouse mouse = new Mouse();
	   mouse.leftClick(x, y);
	}

	/**
    * An object for interfacing with the Mouse
    * @author Jeffry Hidayat
    *
    */
	public static class Mouse {
	   private static Logger logger = LogManager.getLogger(Mouse.class);
	   public Mouse() {
	      logger.info("new " + Mouse.class.getCanonicalName());
	   }

	   public void leftClick(int x, int y) throws HarnessException {
	      logger.info("leftClick(" + x + ", " + y + ")");
	      RobotMouse robotMouse = new RobotMouse();
	      robotMouse.click(x, y);
	   }

	   //// ***
      // Start: Robot methods
      //// ***
      private static class RobotMouse {
         private static Logger logger = LogManager.getLogger(RobotMouse.class);
         private Robot robot;

         public RobotMouse() throws HarnessException {
           logger.info("new " + RobotMouse.class.getCanonicalName());

           try {
              this.robot = new Robot();
           } catch (AWTException e) {
              throw new HarnessException(e);
           }
         }

         public void click(int x, int y) {
            logger.info("click(" + x + ", " + y + ")");
            robot.mouseMove(x, y);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
          }
      }
	}
	
	/**
	 * An object for interfacing with the keyboard
	 * @author Matt Rhoades
	 *
	 */
	public static class Keyboard {
		private static Logger logger = LogManager.getLogger(Keyboard.class);
		
		public Keyboard() {
			logger.info("new " + Keyboard.class.getCanonicalName());
		}

		
		/**
		 * Using Robot, type a key event
		 * @param keyEvent java.awt.event.KeyEvent
		 * @throws HarnessException
		 */
		public void zTypeKeyEvent(int keyEvent) throws HarnessException {
			logger.info("zTypeKeyEvent("+ keyEvent +")");

			RobotKeyboard keyboard = new RobotKeyboard();
			keyboard.doType(keyEvent);
			
			// So events don't run into each other (i.e. "n" followed by "m" doesn't become "nm")
			// Sleep after typing the event
			SleepUtil.sleepMedium();

		}
		
		/**
		 * Using Robot, type a series of characters
		 * @param chars
		 * @throws HarnessException
		 */
		public void zTypeCharacters(String chars) throws HarnessException {
			logger.info("zTypeCharacters("+ chars +")");

			RobotKeyboard keyboard = new RobotKeyboard();
			keyboard.type(chars);

			// For some reason, need a sleep here otherwise selenium
	    	// can't recognize the typed string
	    	// e.g. if the string is foo123, then getValue(locator) will only return foo1 (23 missing)
	    	//
			SleepUtil.sleepMedium();

		}
		

		
		//// ***
		// Start: Robot methods
		//// ***
		private static class RobotKeyboard {
			private static Logger logger = LogManager.getLogger(RobotKeyboard.class);

		    private Robot robot;

		    public RobotKeyboard() throws HarnessException {
				logger.info("new " + RobotKeyboard.class.getCanonicalName());

				try {
					this.robot = new Robot();
				} catch (AWTException e) {
					throw new HarnessException(e);
				}
		    }

//		    public RobotKeyboard(Robot robot) {
//				logger.info("new " + RobotKeyboard.class.getCanonicalName());
	//
//				this.robot = robot;
//		    }

		    // Used to make sure num lock is not pressed
		    private static boolean numLockHasBeenProcessed = false;

		    public void type(String characters) {
		    	logger.info("type("+ characters +")");
		    	if (characters.equals("<Delete>")) {
		    	   doType(KeyEvent.VK_DELETE);
		    	} else if (characters.equals("<ESC>")) {
		    	   doType(KeyEvent.VK_ESCAPE);
		    	} else if (characters.equals("<SHIFT><DEL>")) {
		    		
		    		// http://forums.oracle.com/forums/thread.sjpa?threadID=2230592&tstart=0
		    		if ( (!numLockHasBeenProcessed) && (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK)) ) {
		    			logger.info("Setting KeyEvent.VK_NUM_LOCK=false");
		    			Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK, false);
		    			numLockHasBeenProcessed = true;
		    		}

		    		doType(KeyEvent.VK_SHIFT, KeyEvent.VK_DELETE);

		    	} else {
		    	   for (char c : characters.toCharArray()) {
		    	      try {
		    		   type(c);
		    	      }catch (Exception e) {
						e.printStackTrace();
					}
		    	   }
		    	}
		    	
		    }

		    private void type(char character) {
		    	logger.info("type("+ character +")");
		    	
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
		        case '@': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_2); break;
		        case '#': doType(KeyEvent.VK_NUMBER_SIGN); break;
		        case '$': doType(KeyEvent.VK_DOLLAR); break;
		        case '%': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5); break;
		        case '^': doType(KeyEvent.VK_CIRCUMFLEX); break;
		        case '&': doType(KeyEvent.VK_AMPERSAND); break;
		        case '*': doType(KeyEvent.VK_ASTERISK); break;
		        //case '(': doType(KeyEvent.VK_LEFT_PARENTHESIS); break;
		        // case ')': doType(KeyEvent.VK_RIGHT_PARENTHESIS); break;
		        case '(': doType(KeyEvent.VK_SHIFT,KeyEvent.VK_9); break;
		        case ')': doType(KeyEvent.VK_SHIFT,KeyEvent.VK_0); break;		      
		       // case '_': doType(KeyEvent.VK_UNDERSCORE); break;
		        case '_': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS); break;
		        
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
		        case ':': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON); break;
		        case '\'': doType(KeyEvent.VK_QUOTE); break;
		        case '"': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE); break;
		        case ',': doType(KeyEvent.VK_COMMA); break;
		        case '<': doType(KeyEvent.VK_LESS); break;
		        case '.': doType(KeyEvent.VK_PERIOD); break;
		        case '>': doType(KeyEvent.VK_GREATER); break;
		        case '/': doType(KeyEvent.VK_SLASH); break;
		        case '?': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
		        case ' ': doType(KeyEvent.VK_SPACE); break;

		        // Swedish
		        case '\u00c5': doTypeAltCode("143"); break;	// Å
		        
		        // Spanish ... http://www.asciitable.com/
		        case '\u00e1': doTypeAltCode("160"); break;	// á
		        case '\u00e9': doTypeAltCode("130"); break;	// é
		        case '\u00ed': doTypeAltCode("161"); break;	// í
		        case '\u00f3': doTypeAltCode("162"); break;	// ó
		        case '\u00fa': doTypeAltCode("163"); break;	// ú
		        case '\u00d1': doTypeAltCode("165"); break;	// Ñ
		        case '\u00f1': doTypeAltCode("164"); break;	// ñ
		        
		        default:
		                throw new IllegalArgumentException("Cannot type character " + character);
		        }
		    }

		    public void doType(int... keyCodes) {
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

		    /**
		     * Type Alt+code, e.g. á = ALT+160
		     * @param code
		     */
		    private void doTypeAltCode(String code) {
		    	
		    	robot.keyPress(KeyEvent.VK_ALT);
		    	
		    	for (int i = 0; i < code.length(); i ++) {
			        switch (code.charAt(i)) {
			        case '1': doType(KeyEvent.VK_NUMPAD1); break;
			        case '2': doType(KeyEvent.VK_NUMPAD2); break;
			        case '3': doType(KeyEvent.VK_NUMPAD3); break;
			        case '4': doType(KeyEvent.VK_NUMPAD4); break;
			        case '5': doType(KeyEvent.VK_NUMPAD5); break;
			        case '6': doType(KeyEvent.VK_NUMPAD6); break;
			        case '7': doType(KeyEvent.VK_NUMPAD7); break;
			        case '8': doType(KeyEvent.VK_NUMPAD8); break;
			        case '9': doType(KeyEvent.VK_NUMPAD9); break;
			        case '0': doType(KeyEvent.VK_NUMPAD0); break;
			        }
		    	}
		    	
		    	robot.keyRelease(KeyEvent.VK_ALT);
		    }
		}


	}

}
