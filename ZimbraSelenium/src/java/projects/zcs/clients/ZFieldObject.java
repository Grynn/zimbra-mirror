package projects.zcs.clients;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.testng.Assert;

import framework.core.SelNGBase;

public class ZFieldObject extends ZObject {
	public ZFieldObject(String coreName, String objTypeName) {
		super(coreName, objTypeName);
	}

	public void zType(String objNameOrId, String data) {
		if (data != "")
			ZObjectCore(objNameOrId, "type", true, data, "", "1", "");
	}

	public void zActivateAndType(String objNameOrId, String data) {
		SelNGBase.selenium.get().windowFocus();
		zActivate(objNameOrId);
		SelNGBase.selenium.get().windowFocus();
		enterSpaceUsingRobot();
		SelNGBase.selenium.get().windowFocus();
		zType(objNameOrId, data);
	}

	public void zType(String objNameOrId, String data, String objectNumber) {
		if (data != "")
			ZObjectCore(objNameOrId, "type", true, data, "", objectNumber, "");
	}

	public void zTypeWithKeyboard(String objNameOrId, String data) {
		this.zEnterValueInFileUpload(objNameOrId, data, "", "1");
	}

	public void zTypeWithKeyboard(String objNameOrId, String data,
			String objNumber) {
		this.zEnterValueInFileUpload(objNameOrId, data, "", objNumber);
	}

	public void zTypeInDlg(String objNameOrId, String data) {
		ZObjectCore(objNameOrId, "type", true, data, "dialog", "", "");
	}

	public void zTypeInDlg(String objNameOrId, String data, String objNumber) {
		ZObjectCore(objNameOrId, "type", true, data, "dialog", objNumber, "");
	}

	public void zTypeInDlgWithKeyboard(String objNameOrId, String data,
			String objNumber) {
		this.zEnterValueInFileUpload(objNameOrId, data, "dialog", objNumber);
	}

	public void zTypeInDlgByName(String objNameOrId, String data,
			String dialogName) {
		ZObjectCore(objNameOrId, "type", true, data, "__dialogByName__" + dialogName,
				"1", "");
	}

	public void zTypeInDlgByName(String objNameOrId, String data,
			String dialogName, String objNumber) {
		ZObjectCore(objNameOrId, "type", true, data, "__dialogByName__" + dialogName,
				objNumber, "");
	}

	public String zGetCoordinatesInDlg(String objNameOrId) {
		return ZObjectCore(objNameOrId, "getcoord", true, "", "dialog", "1", "");
	}

	public String zGetCoordinates(String objNameOrId) {
		return ZObjectCore(objNameOrId, "getcoord");
	}

	public void zExists(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "exists");
		Assert.assertEquals("true", actual, objTypeName + "(" + objNameOrId
				+ ") Not Found.");
	}

	public void zExistsInDlg(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "exists", true, "", "dialog", "", "");
		Assert.assertEquals(actual, "true", objTypeName + "(" + objNameOrId
				+ ") doesn't exist in dialog or no dialog was found");
	}

	public void zExistsInDlgByName(String objNameOrId, String dialogName) {
		String actual = ZObjectCore(objNameOrId, "exists", true, "", "__dialogByName__" + dialogName, "", "");
		Assert.assertEquals(actual, "true",
							objTypeName + "(" + objNameOrId	+ ") doesn't exist in dialog(" + dialogName + ")");
	}

	// Internal methods...
	private void zEnterValueInFileUpload(String objNameOrId, String data,
			String dialog, String objNumber) {
		String browserName = SelNGBase.currentBrowserName;
		if ((browserName.indexOf("MSIE") >= 0)
				&& !(browserName.indexOf("MSIE 8") >= 0)) {
			handleFileUploadIE(objNameOrId, data, dialog, objNumber);
		} else if ((browserName.indexOf("Safari") >= 0)
				|| (browserName.indexOf("FF 3") >= 0)
				|| (browserName.indexOf("MSIE 8") >= 0)) {
			handleFileUploadSafariOrFF3(objNameOrId, data, dialog, objNumber);
		} else {
			handleFileUploadGeneral(objNameOrId, data, dialog, objNumber);
		}
	}

	private void handleFileUploadGeneral(String objNameOrId, String data,
			String dialog, String objNumber) {

		ZObjectCore(objNameOrId, "click", true, "", dialog, objNumber, "");
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_SHIFT);
			Thread.sleep(1000);
			this.enterUsingKeys(data);
			robot.keyPress(KeyEvent.VK_ENTER);
			Thread.sleep(500);

		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleFileUploadIE(String objNameOrId, String data,
			String dialog, String objNumber) {

		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", dialog, objNumber, "");
		moveMouseAndTypeDirectlyInBrowser(xy, data);
	}

	private void handleFileUploadSafariOrFF3(String objNameOrId, String data,
			String dialog, String objNumber) {

		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", dialog, objNumber, "");

		try {
			Thread.sleep(500);
			if ((SelNGBase.currentBrowserName.indexOf("MSIE 8") >= 0)) {
				moveMouseAndDblClick(xy);
			} else {
				moveMouseAndClick(xy);
			}
			enterUsingKeys(data);
			Robot robot;
			robot = new Robot();
			Thread.sleep(500);
			robot.keyPress(KeyEvent.VK_ENTER);
			Thread.sleep(1000);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void moveMouseAndTypeDirectlyInBrowser(String xy, String data) {
		moveMouseAndClick(xy);
		enterUsingKeys(data);
	}

	public void enterSpaceUsingRobot() {
		Robot robot;
		try {
			robot = new Robot();

			robot.keyPress(KeyEvent.VK_SPACE);
			robot.keyRelease(KeyEvent.VK_SPACE);
			// robot.keyPress(KeyEvent.VK_BACK_SPACE);

		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enterUsingKeys(String data) {

		Robot robot;
		try {
			robot = new Robot();

			int strLen = data.length();

			for (int i = 0; i < strLen; i++) {
				String chStr = String.valueOf(data.charAt(i));
				if (chStr.equals(":")) {
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_SEMICOLON);
					robot.keyRelease(KeyEvent.VK_SEMICOLON);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				} else
					robot.keyPress(getKeyValue(chStr));
				robot.delay(100);

			}
			Thread.sleep(100);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int getKeyValue(String ch) {
		if (ch.toLowerCase().equals("a"))
			return KeyEvent.VK_A;
		else if (ch.toLowerCase().equals("b"))
			return KeyEvent.VK_B;
		else if (ch.toLowerCase().equals("c"))
			return KeyEvent.VK_C;
		else if (ch.toLowerCase().equals("d"))
			return KeyEvent.VK_D;
		else if (ch.toLowerCase().equals("e"))
			return KeyEvent.VK_E;
		else if (ch.toLowerCase().equals("f"))
			return KeyEvent.VK_F;
		else if (ch.toLowerCase().equals("g"))
			return KeyEvent.VK_G;
		else if (ch.toLowerCase().equals("h"))
			return KeyEvent.VK_H;
		else if (ch.toLowerCase().equals("i"))
			return KeyEvent.VK_I;
		else if (ch.toLowerCase().equals("j"))
			return KeyEvent.VK_J;
		else if (ch.toLowerCase().equals("k"))
			return KeyEvent.VK_K;
		else if (ch.toLowerCase().equals("l"))
			return KeyEvent.VK_L;
		else if (ch.toLowerCase().equals("m"))
			return KeyEvent.VK_M;
		else if (ch.toLowerCase().equals("n"))
			return KeyEvent.VK_N;
		else if (ch.toLowerCase().equals("o"))
			return KeyEvent.VK_O;
		else if (ch.toLowerCase().equals("p"))
			return KeyEvent.VK_P;
		else if (ch.toLowerCase().equals("q"))
			return KeyEvent.VK_Q;
		else if (ch.toLowerCase().equals("r"))
			return KeyEvent.VK_R;
		else if (ch.toLowerCase().equals("s"))
			return KeyEvent.VK_S;
		else if (ch.toLowerCase().equals("t"))
			return KeyEvent.VK_T;
		else if (ch.toLowerCase().equals("u"))
			return KeyEvent.VK_U;
		else if (ch.toLowerCase().equals("v"))
			return KeyEvent.VK_V;
		else if (ch.toLowerCase().equals("w"))
			return KeyEvent.VK_W;
		else if (ch.toLowerCase().equals("x"))
			return KeyEvent.VK_X;
		else if (ch.toLowerCase().equals("y"))
			return KeyEvent.VK_Y;
		else if (ch.toLowerCase().equals("z"))
			return KeyEvent.VK_Z;
		else if (ch.equals("0"))
			return KeyEvent.VK_0;
		else if (ch.equals("1"))
			return KeyEvent.VK_1;
		else if (ch.equals("2"))
			return KeyEvent.VK_2;
		else if (ch.equals("3"))
			return KeyEvent.VK_3;
		else if (ch.equals("4"))
			return KeyEvent.VK_4;
		else if (ch.equals("5"))
			return KeyEvent.VK_5;
		else if (ch.equals("6"))
			return KeyEvent.VK_6;
		else if (ch.equals("7"))
			return KeyEvent.VK_7;
		else if (ch.equals("8"))
			return KeyEvent.VK_8;
		else if (ch.equals("9"))
			return KeyEvent.VK_9;
		else if (ch.equals("\\"))
			return KeyEvent.VK_BACK_SLASH;
		else if (ch.equals("/"))
			return KeyEvent.VK_SLASH;
		else if (ch.equals(":"))
			return KeyEvent.VK_COLON;
		else if (ch.equals("."))
			return KeyEvent.VK_PERIOD;
		else if (ch.equals("-"))
			return KeyEvent.VK_MINUS;

		return 0;
	}
}
