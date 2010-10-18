package projects.zcs.clients;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import org.testng.Assert;



import framework.core.*;
import framework.util.*;

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

	public void zClick(String objNameOrId)  throws HarnessException  {
		ZObjectCore(objNameOrId, "click");
	}

	public void zClick(String objNameOrId, String objNumber)  throws HarnessException {
		ZObjectCore(objNameOrId, "click", true, "", objNumber);
	}

	public void zClickInDlg(String objNameOrId, String objNumber) throws HarnessException {
		ZObjectCore(objNameOrId, "click", true, "dialog", objNumber);
	}

	/**
	 * Literally clicks and activates on the object(using JAVA api)
	 * 
	 * @param objNameOrId
	 * @param objNumber
	 */
	public void zActivate(String objNameOrId, String objNumber) throws HarnessException  {
		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", "", objNumber, "");
		moveMouseAndClick(xy, this.isCheckbox);
	}

	/**
	 * Literally clicks and activates on the object(using JAVA api)
	 * 
	 * @param objNameOrId
	 */
	public void zActivate(String objNameOrId)  throws HarnessException {
		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", "", "", "");
		moveMouseAndClick(xy, this.isCheckbox);
	}
	/**
	 * Literally double-clicks and activates on the object(using JAVA api)
	 * 
	 * @param objNameOrId
	 */
	public void zActivateByDoubleClick(String objNameOrId)  throws HarnessException {
		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", "", "", "");
		moveMouseAndDblClick(xy, this.isCheckbox);
	}
	/**
	 * Literally clicks and activates on the object(using JAVA api) in dialog
	 * 
	 * @param objNameOrId
	 * @param objNumber
	 */
	public void zActivateInDlg(String objNameOrId, String objNumber) throws HarnessException  {
		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", "dialog",
				objNumber, "");
		moveMouseAndClick(xy, this.isCheckbox);
	}

	/**
	 * Literally clicks and activates on the object(using JAVA api) in dialog
	 * 
	 * @param objNameOrId
	 */
	public void zActivateInDlg(String objNameOrId)  throws HarnessException {
		String xy = ZObjectCore(objNameOrId, "getcoord", true, "", "dialog", "", "");
		moveMouseAndClick(xy, this.isCheckbox);
	}

	public void zClickInDlgByName(String objNameOrId, String dialogName,
			String objNumber)  throws HarnessException  {
		ZObjectCore(objNameOrId, "click", true, "__dialogByName__" + dialogName,
				objNumber);
	}

	public void zClickInDlg(String objNameOrId)  throws HarnessException {
		ZObjectCore(objNameOrId, "click", true, "dialog", "");
	}

	public void zClickInDlgByName(String objNameOrId, String dialogName) throws HarnessException  {
		ZObjectCore(objNameOrId, "click", true, "__dialogByName__" + dialogName, "");
	}

	public void zDblClick(String objNameOrId)  throws HarnessException {
		ZObjectCore(objNameOrId, "dblclick");
	}

	public void zRtClick(String objNameOrId)  throws HarnessException {
		ZObjectCore(objNameOrId, "rtclick");
	}

	public void zShiftClick(String objNameOrId)  throws HarnessException {
		ZObjectCore(objNameOrId, "shiftclick");
	}

	public void zCtrlClick(String objNameOrId) throws HarnessException  {
		ZObjectCore(objNameOrId, "ctrlclick");
	}

	public void zMouseOver(String objNameOrId) throws HarnessException  {
		ZObjectCore(objNameOrId, "mouseover");
	}

	public String zGetInnerText(String objNameOrId, String objNumber) throws HarnessException  {
		return ZObjectCore(objNameOrId, "gettext", true, "", objNumber);
	}

	public String zGetInnerText(String objNameOrId) throws HarnessException  {
		return ZObjectCore(objNameOrId, "gettext", true, "", "");
	}

	public String zGetInnerTextInDlg(String objNameOrId) throws HarnessException  {
		return ZObjectCore(objNameOrId, "gettext", true, "dialog", "");
	}

	public String zGetInnerHTML(String objNameOrId, String objNumber)  throws HarnessException {
		return ZObjectCore(objNameOrId, "gethtml", true, "", objNumber);
	}

	public String zGetInnerTextInDlgByName(String objNameOrId, String dialogName) throws HarnessException  {
		return ZObjectCore(objNameOrId, "gettext", true, "__dialogByName__"
				+ dialogName, "");
	}

	public String zGetInnerTextInDlgByName(String objNameOrId,
			String dialogName, String objNumber) throws HarnessException {
		return ZObjectCore(objNameOrId, "gettext", true, "__dialogByName__"
				+ dialogName, objNumber);
	}

	public String zGetInnerHTML(String objNameOrId) throws HarnessException  {
		return ZObjectCore(objNameOrId, "gethtml", true, "", "");
	}

	public void zNotExists(String objNameOrId) throws HarnessException  {
		Assert.assertEquals(ZObjectCore(objNameOrId, "notexists"), "true");
	}

	public String zNotExistsDontWait(String objNameOrId) throws HarnessException  {
		return ZObjectCore(objNameOrId, "notexists", false);
	}

	public void zWait(String objNameOrId)  throws HarnessException {
		this.zWait(objNameOrId, "", "");
	}

	public void zWait(String objNameOrId, String panel, String param1) throws HarnessException {
		// don't call core(since it could go one of the core might be calling
		// this(chicken and egg)
		ClientSessionFactory.session().selenium().call(coreName, objNameOrId, "wait", true, panel, param1);
	}

	public void zExists(String objNameOrId) throws HarnessException  {
		Assert.assertEquals(ZObjectCore(objNameOrId, "exists"), "true");
	}

	public String zExistsDontWait(String objNameOrId)  throws HarnessException {
		// this method doesnt wait and also doesnt fail if object doesnt exist
		return ZObjectCore(objNameOrId, "exists", false);

	}

	public void zExistsInDlg(String objNameOrId)  throws HarnessException {
		String actual = ZObjectCore(objNameOrId, "exists", true, "dialog", "");
		Assert.assertEquals(actual, "true", objTypeName + "(" + objNameOrId
				+ ") doesn't exist in dialog or no dialog was found");
	}

	public String zExistsInDlgDontWait(String objNameOrId)  throws HarnessException {
		return ZObjectCore(objNameOrId, "exists", false, "dialog", "");
	}

	public String zExistsInDlgByNameDontWait(String objNameOrId,
			String dialogName)  throws HarnessException {
		return ZObjectCore(objNameOrId, "exists", false, "__dialogByName__"
				+ dialogName, "");
	}

	public void zExistsInDlgByName(String objNameOrId, String dialogName)  throws HarnessException {
		String actual = ZObjectCore(objNameOrId, "exists", true, "__dialogByName__"
				+ dialogName, "");
		Assert.assertEquals(actual, "true", objTypeName + "(" + objNameOrId
				+ ") doesn't exist in dialog(" + dialogName + ")");
	}

	public void zIsEnabled(String objNameOrId) throws HarnessException  {
		String actual = ZObjectCore(objNameOrId, "enabled");
		Assert.assertEquals(actual, "true", objTypeName + "(" + objNameOrId
				+ ") is disabled");
	}

	public void zIsDisabled(String objNameOrId) throws HarnessException  {
		String actual = ZObjectCore(objNameOrId, "disabled");
		Assert.assertEquals(actual, "true", objTypeName + "(" + objNameOrId
				+ ") is enabled(instead of disabled)");
	}

	protected String ZObjectCore(String objNameOrId, String action)  throws HarnessException  {
		return ZObjectCore(objNameOrId, action, true);
	}

	protected String ZObjectCore(String objNameOrId, String action, Boolean wait)  throws HarnessException  {
		return ZObjectCore(objNameOrId, action, wait, "", "");
	}
	
	protected String ZObjectCore(String objNameOrId, String action, Boolean wait,
			String panel, String param1) throws HarnessException  {
		return ZObjectCore(objNameOrId, action, wait, panel, param1, "", "");
	}

	protected String ZObjectCore(String objNameOrId, String action, Boolean wait,
			String panel, String param1, String param2, String param3)  throws HarnessException {
		return ClientSessionFactory.session().selenium().call(coreName, objNameOrId, action, wait, panel, param1, param2, param3);
	}

	public static String zVerifyObjDisplayed(String nameOrIdWithZIndex) throws HarnessException  {
		// action "get" is mentioned just to indicate to selenium.call that its
		// a getMethod
		return ClientSessionFactory.session().selenium().call("verifyZObjectDisplayed", nameOrIdWithZIndex,
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
		String browserName = ClientSessionFactory.session().currentBrowserName();
		Robot robot;
		try {
			robot = new Robot();
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			// SleepUtil.sleep(1000);
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
			SleepUtil.sleep(1000);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
