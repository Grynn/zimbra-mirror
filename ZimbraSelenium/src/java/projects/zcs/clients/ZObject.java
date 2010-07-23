package projects.zcs.clients;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import org.testng.Assert;

import com.thoughtworks.selenium.SeleniumException;

import framework.core.SelNGBase;

public class ZObject extends SelNGBase {
	protected String coreName;
	protected String objTypeName;
	protected boolean isCheckbox = false;// if true then moveMouse's xy is
	// adjusted differently for
	// zActivate

	public ZObject(String coreName, String objTypeName) {
		this.coreName = coreName;
		this.objTypeName = objTypeName;
	}

	public void zClick(String objNameOrId) {
		ZObjectCore(objNameOrId, "click");
	}

	public void zClick(String objNameOrId, String objNumber) {
		ZObjectCore(objNameOrId, "click", true, "", objNumber);
	}

	public void zClickInDlg(String objNameOrId, String objNumber) {
		ZObjectCore(objNameOrId, "click", true, "dialog", objNumber);
	}

	/**
	 * Literally clicks and activates on the object(using JAVA api)
	 * 
	 * @param objNameOrId
	 * @param objNumber
	 */
	public void zActivate(String objNameOrId, String objNumber) {
		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", "", objNumber, "");
		moveMouseAndClick(xy, this.isCheckbox);
	}

	/**
	 * Literally clicks and activates on the object(using JAVA api)
	 * 
	 * @param objNameOrId
	 */
	public void zActivate(String objNameOrId) {
		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", "", "", "");
		moveMouseAndClick(xy, this.isCheckbox);
	}
	/**
	 * Literally double-clicks and activates on the object(using JAVA api)
	 * 
	 * @param objNameOrId
	 */
	public void zActivateByDoubleClick(String objNameOrId) {
		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", "", "", "");
		moveMouseAndDblClick(xy, this.isCheckbox);
	}
	/**
	 * Literally clicks and activates on the object(using JAVA api) in dialog
	 * 
	 * @param objNameOrId
	 * @param objNumber
	 */
	public void zActivateInDlg(String objNameOrId, String objNumber) {
		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", "dialog",
				objNumber, "");
		moveMouseAndClick(xy, this.isCheckbox);
	}

	/**
	 * Literally clicks and activates on the object(using JAVA api) in dialog
	 * 
	 * @param objNameOrId
	 */
	public void zActivateInDlg(String objNameOrId) {
		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", "dialog", "", "");
		moveMouseAndClick(xy, this.isCheckbox);
	}

	public void zClickInDlgByName(String objNameOrId, String dialogName,
			String objNumber) {
		ZObjectCore(objNameOrId, "click", true, "__dialogByName__" + dialogName,
				objNumber);
	}

	public void zClickInDlg(String objNameOrId) {
		ZObjectCore(objNameOrId, "click", true, "dialog", "");
	}

	public void zClickInDlgByName(String objNameOrId, String dialogName) {
		ZObjectCore(objNameOrId, "click", true, "__dialogByName__" + dialogName, "");
	}

	public void zDblClick(String objNameOrId) {
		ZObjectCore(objNameOrId, "dblclick");
	}

	public void zRtClick(String objNameOrId) {
		ZObjectCore(objNameOrId, "rtclick");
	}

	public void zShiftClick(String objNameOrId) {
		ZObjectCore(objNameOrId, "shiftclick");
	}

	public void zCtrlClick(String objNameOrId) {
		ZObjectCore(objNameOrId, "ctrlclick");
	}

	public void zMouseOver(String objNameOrId) {
		ZObjectCore(objNameOrId, "mouseover");
	}

	public String zGetInnerText(String objNameOrId, String objNumber) {
		return ZObjectCore(objNameOrId, "gettext", true, "", objNumber);
	}

	public String zGetInnerText(String objNameOrId) {
		return ZObjectCore(objNameOrId, "gettext", true, "", "");
	}

	public String zGetInnerTextInDlg(String objNameOrId) {
		return ZObjectCore(objNameOrId, "gettext", true, "dialog", "");
	}

	public String zGetInnerHTML(String objNameOrId, String objNumber) {
		return ZObjectCore(objNameOrId, "gethtml", true, "", objNumber);
	}

	public String zGetInnerTextInDlgByName(String objNameOrId, String dialogName) {
		return ZObjectCore(objNameOrId, "gettext", true, "__dialogByName__"
				+ dialogName, "");
	}

	public String zGetInnerTextInDlgByName(String objNameOrId,
			String dialogName, String objNumber) {
		return ZObjectCore(objNameOrId, "gettext", true, "__dialogByName__"
				+ dialogName, objNumber);
	}

	public String zGetInnerHTML(String objNameOrId) {
		return ZObjectCore(objNameOrId, "gethtml", true, "", "");
	}

	public void zNotExists(String objNameOrId) {
		Assert.assertEquals("true", ZObjectCore(objNameOrId, "notexists", false));

		try {
			String actual = ZObjectCore(objNameOrId, "notexists", false);
			Assert.assertEquals("true", actual, objTypeName + "(" + objNameOrId
					+ ") Found, which should not be present.");
		} catch (SeleniumException e) {
			// ignore window or frame is closed exception
			if (e.getMessage().indexOf("is closed") == -1)
				e.printStackTrace();

		}

	}

	public String zNotExistsDontWait(String objNameOrId) {
		return ZObjectCore(objNameOrId, "notexists", false);
	}

	public void zWait(String objNameOrId) {
		this.zWait(objNameOrId, "", "");
	}

	public void zWait(String objNameOrId, String panel, String param1) {
		// don't call core(since it could go one of the core might be calling
		// this(chicken and egg)
		selenium.call(coreName, objNameOrId, "wait", true, panel, param1);
	}

	public void zExists(String objNameOrId) {
		Assert.assertEquals("true", ZObjectCore(objNameOrId, "exists"));
	}

	public String zExistsDontWait(String objNameOrId) {
		// this method doesnt wait and also doesnt fail if object doesnt exist
		return ZObjectCore(objNameOrId, "exists", false);

	}

	public void zExistsInDlg(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "exists", true, "dialog", "");
		Assert.assertEquals(actual, "true", objTypeName + "(" + objNameOrId
				+ ") doesn't exist in dialog or no dialog was found");
	}

	public String zExistsInDlgDontWait(String objNameOrId) {
		return ZObjectCore(objNameOrId, "exists", false, "dialog", "");
	}

	public String zExistsInDlgByNameDontWait(String objNameOrId,
			String dialogName) {
		return ZObjectCore(objNameOrId, "exists", false, "__dialogByName__"
				+ dialogName, "");
	}

	public void zExistsInDlgByName(String objNameOrId, String dialogName) {
		String actual = ZObjectCore(objNameOrId, "exists", true, "__dialogByName__"
				+ dialogName, "");
		Assert.assertEquals(actual, "true", objTypeName + "(" + objNameOrId
				+ ") doesn't exist in dialog(" + dialogName + ")");
	}

	public void zIsEnabled(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "enabled");
		Assert.assertEquals(actual, "true", objTypeName + "(" + objNameOrId
				+ ") is disabled");
	}

	public void zIsDisabled(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "disabled");
		Assert.assertEquals(actual, "true", objTypeName + "(" + objNameOrId
				+ ") is enabled(instead of disabled)");
	}

	protected String ZObjectCore(String objNameOrId, String action) {
		return ZObjectCore(objNameOrId, action, true);
	}

	protected String ZObjectCore(String objNameOrId, String action, Boolean wait) {
		return ZObjectCore(objNameOrId, action, wait, "", "");
	}
	
	protected String ZObjectCore(String objNameOrId, String action, Boolean wait,
			String panel, String param1) {
		return ZObjectCore(objNameOrId, action, wait, panel, param1, "", "");
	}

	protected String ZObjectCore(String objNameOrId, String action, Boolean wait,
			String panel, String param1, String param2, String param3) {
		return selenium.call(coreName, objNameOrId, action, wait, panel, param1, param2, param3);
	}

	public static String zVerifyObjDisplayed(String nameOrIdWithZIndex) {
		// action "get" is mentioned just to indicate to selenium.call that its
		// a getMethod
		return selenium.call("verifyZObjectDisplayed", nameOrIdWithZIndex,
				"get", false, null, null, null, null);
	}

	public String adjustXY(String xy, boolean isCheckbox) {
		String[] tmp = xy.split(",");
		int x = Integer.parseInt(tmp[0]);
		int y = Integer.parseInt(tmp[1]);
		if (!isCheckbox) {
			x = x + 16;
			y = y - 16;
		} else {
			x = x + 12;
			y = y - 16;
		}
		return x + "," + y;
	}

	public void moveMouseAndClick(String xy) {
		moveMouseAndAct(xy, false, "click");
	}
	public void moveMouseAndDblClick(String xy) {
		moveMouseAndAct(xy, false, "dblclick");
	}

	public void moveMouseAndClick(String xy, boolean isCheckbox) {
		moveMouseAndAct(xy, isCheckbox, "click");
	}
	public void moveMouseAndDblClick(String xy, boolean isCheckbox) {
		moveMouseAndAct(xy, isCheckbox, "dblclick");
	}
	public void moveMouseAndAct(String xy, boolean isCheckbox, String action) {
		xy = adjustXY(xy, isCheckbox);
		String[] tmp = xy.split(",");
		int x = Integer.parseInt(tmp[0]);
		int y = Integer.parseInt(tmp[1]);
		String browserName = SelNGBase.currentBrowserName;
		Robot robot;
		try {
			robot = new Robot();
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			// Thread.sleep(1000);
			robot.mouseMove(x, y);
			if (action.equals("click")) {
				if (browserName.indexOf("Safari") >= 0) {
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mousePress(InputEvent.BUTTON1_MASK);// needs this
																// extra
					// mouse down in
					// some cases in SF3
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				} else {// for ff3 just do normal mouse-down/mouse-up
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				}
			} else 	if (action.equals("dblclick")) {
				if (browserName.indexOf("Safari") >= 0) {
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mousePress(InputEvent.BUTTON1_MASK);//safari needs this extra
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				} else {
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				}
			}
			Thread.sleep(1000);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
