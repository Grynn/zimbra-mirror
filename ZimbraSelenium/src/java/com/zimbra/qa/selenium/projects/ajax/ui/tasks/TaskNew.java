package com.zimbra.qa.selenium.projects.ajax.ui.tasks;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class TaskNew extends AbsForm {
	public static class Locators {
		public static final String zFrame = "css=iframe[id*='DWT']";
		public static final String zSaveAndCloseIconBtn = "//*[@id='DWT9_left_icon']";
		public static final String zBodyField = "css=body";
		public static final String zNameField = "css=[id^=DWT4] [input$=]";
		public static final String zEditNameField = "css=[class=DwtInputField] [input$=]";
		public static final String zSaveTask = "zb__TKE1__SAVE_left_icon";
		public static final String zTasksubjField = "//td[contains(@id,'_subject')]/div/input";
	}

	public TaskNew(AbsApplication application) {
		super(application);
		// TODO Auto-generated constructor stub
		logger.info("new " + TaskNew.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		return this.getClass().getName();
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info("DocumentBriefcaseNew.fill(ZimbraItem)");
		// TODO Auto-generated method stub
		// Make sure the item is a DocumentItem
		if (!(item instanceof TaskItem)) {
			throw new HarnessException("Invalid item type - must be taskItem");
		}

		// Convert object to DocumentItem
		TaskItem taskItem = (TaskItem) item;

		typeTaskSubject(taskItem.gettaskSubject());

	}

	public void typeTaskSubject(String gettaskSubject) {
		// TODO Auto-generated method stub
		if (sIsElementPresent(Locators.zTasksubjField))
			sType(Locators.zTasksubjField, gettaskSubject);

	}

	@Override
	public void zSubmit() throws HarnessException {
		// TODO Auto-generated method stub
		if (!this.sIsElementPresent(Locators.zSaveTask))
			throw new HarnessException("Save button is not present");
		zClick(Locators.zSaveTask);

	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}

}
