package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;

public class DragAndDrop extends AbsPage {

	public DragAndDrop(AbsApplication application) {
		super(application);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}

	public static void zDragAndDrop(String source, String destination)
			throws HarnessException {

		SleepUtil.sleep(2000);
		Number x_coord1 = ClientSessionFactory.session().selenium()
				.getElementPositionLeft(destination);
		Number y_coord1 = ClientSessionFactory.session().selenium()
				.getElementPositionTop(destination);
		Number x_coord2 = ClientSessionFactory.session().selenium()
				.getElementPositionLeft(source);
		Number y_coord2 = ClientSessionFactory.session().selenium()
				.getElementPositionTop(source);
		Number x_coord = (x_coord1.intValue() - x_coord2.intValue());
		Number y_coord = (y_coord1.intValue() - y_coord2.intValue());

		String xy_coord = x_coord.toString() + "," + y_coord.toString();
		System.out.println("x,y coordinate of the objectToBeDroppedInto="
				+ x_coord1 + "," + y_coord1);
		System.out.println("x,y coordinate of the objectToBeDragged="
				+ x_coord2 + "," + y_coord2);
		System.out
				.println("x,y coordinate of the objectToBeDroppedInto relative to objectToBeDragged = "
						+ xy_coord);

		ClientSessionFactory.session().selenium().mouseDown(source);
		SleepUtil.sleep(1000);
		ClientSessionFactory.session().selenium().mouseMoveAt(destination,
				xy_coord);
		SleepUtil.sleep(1000 * 3);
		// ClientSessionFactory.session().selenium().mouseMove(destination);
		// ClientSessionFactory.session().selenium().mouseOver(destination);
		SleepUtil.sleep(2000);
		ClientSessionFactory.session().selenium().mouseUpAt(destination,
				xy_coord);
		SleepUtil.sleep(2000);

	}

}
