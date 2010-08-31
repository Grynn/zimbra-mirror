package projects.zcs.ui;

import org.testng.Assert;

import projects.zcs.clients.ProvZCS;
import framework.core.SelNGBase;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

/**
 * This Class has UI-level methods related creating a task and other task
 * related functionalities e.g delete and modify Has static-final variables that
 * holds ids of icons on the toolbar(like zTasksNewBtn etc). If you are dealing
 * with the toolbar buttons, use these icons since in vmware resolutions and in
 * some languages button-labels are not displayed(but just their icons)
 * 
 * @author Krishna Kumar Sure
 * 
 */

@SuppressWarnings("static-access")
public class TaskApp extends AppPage {
	public static String zNewTasksOverviewPaneIcon = "id=ztih__main_Tasks__TASK_textCell";
	public static final String zTasksFolder = "id=zti__main_Tasks__15_imageCell";
	public static final String zTasksTab = "id=zb__App__Tasks_left_icon";
	public static final String zTasksOverviewFolder = "id=ztih__main_Tasks__TASK_table";
	public static final String zTasksNewBtn = "id=zb__TKL__NEW_MENU_left_icon";
	public static final String zTasksEditBtn = "id=zb__TKL__EDIT_left_icon";
	public static final String zTasksDeleteBtn = "id=zb__TKL__DELETE_left_icon";
	public static final String zTasksMoveBtn = "id=zb__TKL__MOVE_left_icon";
	public static final String zTasksPrintBtn = "id=zb__TKL__PRINT_left_icon";
	public static final String zTasksTagBtn = "id=zb__TKL__TAG_MENU_left_icon";
	public static final String zTasksPreviousPageBtn = "id=zb__TKL__Nav__PAGE_BACK_left_icon";
	public static final String zTasksForwardPageBtn = "id=zb__TKL__Nav__PAGE_FORWARD_left_icon";
	public static final String zMailAppBtn = "id=zb__App__Mail_left_icon";

	public static final String zTasksSaveBtn = "id=zb__TKE__SAVE_left_icon";
	public static final String zTasksCancelBtn = "id=zb__TKE__CANCEL_left_icon";
	public static final String zTasksAddAttachmentBtn = "id=zb__TKE__ATTACHMENT_left_icon";
	public static final String zTasksSpellCheckBtn = "id=zb__TKE__SPELL_CHECK_left_icon";
	public static final String zTasksFormatBtn = "id=zb__TKE__COMPOSE_FORMAT_left_icon";
	public static final String zTasksViewBtn = "id=zb__TKL__VIEW_MENU_left_icon";

	// ===========================
	// NAVIGATE METHODS
	// ===========================

	/**
	 * Navigates to tasks from MailApp
	 */
	public static void zNavigateToTasks() throws Exception {
		SleepUtil.sleep(1000);
		obj.zButton.zClick(zTasksTab);
	}

	/**
	 * Navigates to task compose page directly from mail app
	 */
	public static void zNavigateToTaskCompose() throws Exception {
		zNavigateToTasks();
		obj.zButton.zClick(zTasksNewBtn);
	}

	/**
	 * Logs in using the given username and navigates to task app
	 * 
	 * @param username
	 * @return username string
	 * @throws Exception
	 */
	public static String zLoginAndNavigateToTasks(String username)
			throws Exception {
		page.zLoginpage.zLoginToZimbraAjax(username);
		zNavigateToTasks();
		return username;
	}

	/**
	 * dynamically creates account, logs in using that accnt and navigates to
	 * compose
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String zLoginAndNavigateToTasks() throws Exception {
		String user1 = ProvZCS.getRandomAccount();
		return zLoginAndNavigateToTasks(user1);
	}

	/**
	 * Enters simple task details
	 * 
	 * @param subject
	 *            : Subject line for the task
	 * @param location
	 *            : Location for the task
	 * @param priority
	 *            : Priority for the task
	 * @param body
	 *            : body for the task
	 * @throws Exception
	 */
	public static void zTaskEnterSimpleDetails(String subject, String location,
			String priority, String body) throws Exception {
		zWaitTillObjectExist("editfield",
				getNameWithoutSpace(localize(locator.subjectLabel)));
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.subjectLabel)), subject);
		if (!location.equals(""))
			obj.zEditField.zType(localize(locator.locationLabel), location);
		if (!priority.equals("")) {
			obj.zButton.zClick(localize(locator.normal));
			obj.zMenuItem.zClick(priority);
		}
		if (!body.equals(""))
			obj.zEditor.zType(body);
		SleepUtil.sleep(1000); // fails in SF because it doesn't find task list
	}

	/**
	 * Enters all the task details
	 * 
	 * @param subject
	 *            : subject for the task
	 * @param location
	 *            : location for the task
	 * @param priority
	 *            : priority for the task
	 * @param taskList
	 *            : task list the task should be created in
	 * @param progress
	 *            : progress of the task
	 * @param progressPercent
	 *            : percentage complete of the task
	 * @param startDate
	 *            : start date for the task
	 * @param endDate
	 *            : end date for the task
	 * @param body
	 *            : body for the task
	 * @throws Exception
	 */
	public static void zTaskEnterDetails(String subject, String location,
			String priority, String taskList, String progress,
			String progressPercent, String startDate, String endDate,
			String body) throws Exception {

		zTaskEnterSimpleDetails(subject, location, priority, body);

		if (!taskList.equals("")) {
			obj.zFeatureMenu
					.zClick(getNameWithoutSpace(localize(locator.taskFolder)));
			obj.zMenuItem.zClick(taskList);
		}

		if (!progress.equals("")) {
			obj.zButton.zClick(localize(locator.notStarted));
			obj.zMenuItem.zClick(progress);
		}

		if (!progressPercent.equals("")) {
			if (ZimbraSeleniumProperties.getStringProperty("locale").equals(
					"fr")
					|| ZimbraSeleniumProperties.getStringProperty("locale")
							.equals("sv")
					|| ZimbraSeleniumProperties.getStringProperty("locale")
							.equals("da")
					|| ZimbraSeleniumProperties.getStringProperty("locale")
							.equals("de")) {
				obj.zButton.zClick("0 %");
				progressPercent = progressPercent + " %";
			} else {
				obj.zButton.zClick("0%");
				progressPercent = progressPercent + "%";
			}
			obj.zMenuItem.zClick(progressPercent);
		}

		if (!startDate.equals("")) {
			obj.zEditField.zType(localize(locator.startDate), startDate);
		}

		if (!endDate.equals("")) {
			obj.zEditField.zType(localize(locator.endDate), endDate);
		}
	}

	/**
	 * Creates a task with subject, location, priority and body only
	 * 
	 * @param subject
	 *            : subject for the task
	 * @param location
	 *            : location for the task
	 * @param priority
	 *            : priority for thet ask
	 * @param body
	 *            : body for the task
	 * @throws Exception
	 */
	public static void zTaskCreateSimple(String subject, String location,
			String priority, String body) throws Exception {

		obj.zButton.zClick(zTasksNewBtn);

		zTaskEnterSimpleDetails(subject, location, priority, body);

		// obj.zButton.zClick(zTasksSaveBtn);

		SelNGBase.selenium
				.get()
				.clickAt(
						"Xpath=//td[contains(@id,'zb__TKE')]/div[contains(@class,'ImgSave')]",
						"");

		zWaitTillObjectExist("button", zTasksNewBtn);
	}

	/**
	 * Creates a task with all details
	 * 
	 * @param subject
	 *            : subject for the task
	 * @param location
	 *            : location for the task
	 * @param priority
	 *            : priority for the task
	 * @param taskList
	 *            : task list the task should be created in
	 * @param progress
	 *            : progress of the task
	 * @param progressPercent
	 *            : percentage complete of the task
	 * @param startDate
	 *            : start date for the task
	 * @param endDate
	 *            : end date for the task
	 * @param body
	 *            : body for the task
	 * @throws Exception
	 */
	public static void zTaskCreate(String subject, String location,
			String priority, String body, String taskList, String progress,
			String progressPercent, String startDate, String endDate)
			throws Exception {

		obj.zButton.zClick(zTasksNewBtn);

		zTaskEnterDetails(subject, location, priority, taskList, progress,
				progressPercent, startDate, endDate, body);

		obj.zButton.zClick(zTasksSaveBtn);

		SleepUtil.sleep(1500);

	}

	/**
	 * Creates a simple task in given task list
	 * 
	 * @param subject
	 *            : subject for the task
	 * @param location
	 *            : location for the task
	 * @param priority
	 *            : priority for the task
	 * @param body
	 *            : body for the task
	 * @param taskList
	 *            : task list the task should be created in
	 * @throws Exception
	 */
	public static void zTaskCreateSimpleInTaskList(String subject,
			String location, String priority, String body, String taskList)
			throws Exception {

		zTaskCreate(subject, location, priority, body, taskList, "", "", "", "");
	}

	/**
	 * Clicks the given task name
	 * 
	 * @param taskname
	 *            : subject of the task to be selected
	 * @throws Exception
	 */
	public static void zTaskSelect(String taskname) throws Exception {
		obj.zMessageItem.zExists(taskname);
		obj.zMessageItem.zClick(taskname);
	}

	/**
	 * opens task by double clicking
	 * 
	 * @param taskname
	 *            : subject of the task to be opened
	 * @throws Exception
	 */
	public static void zTaskOpenByDblClick(String taskname) throws Exception {
		obj.zMessageItem.zExists(taskname);
		obj.zMessageItem.zDblClick(taskname);
	}

	/**
	 * @param taskname
	 *            : name of the task to be opened by toolbar button
	 * @throws Exception
	 */
	public static void zTaskOpenByToolBarEdit(String taskname) throws Exception {
		obj.zTaskItem.zExists(taskname);
		zTaskSelect(taskname);
		obj.zButton.zClick(zTasksEditBtn);
	}

	/**
	 * @param taskname
	 *            : name of the task to be opened by rt. click
	 * @throws Exception
	 */
	public static void zTaskOpenByRtClickEdit(String taskname) throws Exception {
		obj.zTaskItem.zExists(taskname);
		obj.zTaskItem.zRtClick(taskname);
		obj.zMenuItem.zClick(localize(locator.edit));
	}

	/**
	 * edits a task with the given details
	 * 
	 * @param orgSubject
	 *            : subject of the task to be modified
	 * @param newSubject
	 *            : new subject for the task
	 * @param newPriority
	 *            : new priority for the task
	 * @param newLocation
	 *            : new location for the task
	 * @param newBody
	 *            : new body for the task
	 * @param newTaskList
	 *            : new task list for the task
	 * @param newProgress
	 *            : new progress for the task
	 * @param newProgressPercent
	 *            : new progress percent for the task
	 * @param newStartDate
	 *            : new start date for the task
	 * @param newEndDate
	 *            : new end date for the task
	 * @throws Exception
	 */
	public static void zTaskEdit(String orgSubject, String newSubject,
			String newLocation, String newPriority, String newBody,
			String newTaskList, String newProgress, String newProgressPercent,
			String newStartDate, String newEndDate) throws Exception {

		zTaskOpenByDblClick(orgSubject);

		obj.zEditField.zActivate(getSubjectLabel());

		zTaskEnterDetails(newSubject, newLocation, newPriority, newTaskList,
				newProgress, newProgressPercent, newStartDate, newEndDate,
				newBody);

		obj.zButton.zClick(zTasksSaveBtn);

		SleepUtil.sleep(1000);
	}

	/**
	 * modifies a subject for the given task
	 * 
	 * @param orgSubject
	 *            : subject of the task to be modified
	 * @param newSubject
	 *            : new subject for the task
	 * @throws Exception
	 */

	public static void zTaskEditSubject(String orgSubject, String newSubject)
			throws Exception {

		zTaskEdit(orgSubject, newSubject, "", "", "", "", "", "", "", "");
	}

	/**
	 * @param taskname
	 *            : subject of the task to be deleted
	 * @throws Exception
	 */
	public static void zTaskDeleteToolbarBtn(String taskname) throws Exception {

		obj.zTaskItem.zExists(taskname);

		zTaskSelect(taskname);

		obj.zButton.zClick(zTasksDeleteBtn);

		// obj.zDialog.zVerifyAlertMessage(localize(locator.confirmTitle),localize
		// (locator.confirmCancelTask));

		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.confirmTitle));
	}

	/**
	 * @param taskname
	 *            : subject of the task to be deleted
	 * @throws Exception
	 */
	public static void zTaskDeleteByRtClick(String taskname) throws Exception {
		obj.zTaskItem.zExists(taskname);

		obj.zTaskItem.zRtClick(taskname);

		obj.zMenuItem.zClick(localize(locator.del));

		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.confirmTitle));
	}

	/**
	 * @param subject
	 *            : subject of the task to be verified
	 * @param taskList
	 *            : name of the task list the task is in
	 * @throws Exception
	 */
	public static void zTaskVerifyExistsInTaskList(String subject,
			String taskList) throws Exception {

		obj.zFolder.zClick(taskList);

		obj.zTaskItem.zExists(subject);

	}

	/**
	 * @param subject
	 *            : subject of the task to be verified not exists
	 * @param taskList
	 *            : name of the task list
	 * @throws Exception
	 */
	public static void zTaskVeriyNotExistsInTaskList(String subject,
			String taskList) throws Exception {
		obj.zFolder.zClick(taskList);

		obj.zTaskItem.zExists(subject);
	}

	/**
	 * @param taskname
	 *            : subject of the task to be moved
	 * @param sourceTaskList
	 *            : source task list name
	 * @param destinationTaskList
	 *            : destination of the task list name
	 * @throws Exception
	 */
	public static void zTaskMoveToolbar(String taskname, String sourceTaskList,
			String destinationTaskList) throws Exception {
		obj.zFolder.zClick(sourceTaskList);

		obj.zTaskItem.zExists(taskname);

		obj.zButton.zClick(zTasksMoveBtn);

		obj.zFolder.zClickInDlgByName(destinationTaskList,
				localize(locator.moveTask));

		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.moveTask));

	}

	/**
	 * @param taskname
	 *            : subject of the task to be moved
	 * @param sourceTaskList
	 *            : source task list name
	 * @param destinationTaskList
	 *            : destination task list name
	 * @throws Exception
	 */
	public static void zTaskMoveRtClick(String taskname, String sourceTaskList,
			String destinationTaskList) throws Exception {

		obj.zFolder.zClick(sourceTaskList);

		obj.zTaskItem.zRtClick(taskname);

		obj.zMenuItem.zClick(localize(locator.move));

		obj.zFolder.zClickInDlgByName(destinationTaskList,
				localize(locator.moveTask));

		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.moveTask));
	}

	/**
	 * @param taskname
	 *            : subject of the task to be tagged
	 * @param tagname
	 *            : name of the tag
	 * @param taskListName
	 *            : task list in which the task is present
	 * @throws Exception
	 */
	public static void zTaskTagToolbar(String taskname, String tagname,
			String taskListName) throws Exception {
		obj.zFolder.zClick(taskListName);

		obj.zTaskItem.zClick(taskname);

		obj.zButton.zClick(zTasksTagBtn);

		obj.zMenuItem.zClick(tagname);

	}

	/**
	 * @param taskname
	 *            : subject of the task to be tagged
	 * @param tagname
	 *            : name of the tag
	 * @param taskListName
	 *            : task list in which the task is present
	 * @throws Exception
	 */
	public static void zTaskTagRtClick(String taskname, String tagname,
			String taskListName) throws Exception {
		obj.zFolder.zClick(taskListName);

		obj.zTaskItem.zRtClick(taskname);

		obj.zMenuItem.zMouseOver(localize(locator.tagTask));

		obj.zMenuItem.zClick(tagname);

	}

	/**
	 * creates a task list using the 'New Task List' button in the overview
	 * panel
	 * 
	 * @param taskListName
	 *            : name of the task list to be created
	 * @throws Exception
	 */
	public static void zTaskListCreateNewBtn(String taskListName)
			throws Exception {
		zWaitTillObjectExist("button",
				replaceUserNameInStaticId(zNewTasksOverviewPaneIcon));
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(zNewTasksOverviewPaneIcon));
		obj.zMenuItem.zClick(localize(locator.newTaskFolder));
		obj.zEditField.zTypeInDlgByName(localize(locator.name), taskListName,
				localize(locator.createNewTaskFolder));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewTaskFolder));
	}

	/**
	 * creates a task list by right clicking on the 'Tasks' in the overview
	 * panel
	 * 
	 * @param taskListName
	 *            : name of the task list to be created
	 * @throws Exception
	 */
	public static void zTaskListCreateRtClick(String taskListName)
			throws Exception {

		obj.zFolder.zRtClick(zTasksOverviewFolder);

		obj.zMenuItem.zClick(localize(locator.newTaskFolder));

		obj.zEditField.zTypeInDlgByName(localize(locator.name), taskListName,
				localize(locator.createNewTaskFolder));

		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewTaskFolder));
	}

	/**
	 * renames a task list
	 * 
	 * @param oldTaskListName
	 *            : task list to be renamed
	 * @param newTaskListName
	 *            : new name for the task list
	 * @throws Exception
	 */
	public static void zTaskListRename(String oldTaskListName,
			String newTaskListName) throws Exception {

		obj.zTaskFolder.zRtClick(oldTaskListName);

		obj.zMenuItem.zClick(localize(locator.renameFolder));

		obj.zEditField.zTypeInDlgByName(localize(locator.newName),
				newTaskListName, localize(locator.renameFolder));

		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.renameFolder));

	}

	/**
	 * deletes the specified task list
	 * 
	 * @param taskListName
	 *            : task list to be deleted
	 * @throws Exception
	 */
	public static void zTaskListDelete(String taskListName) throws Exception {

		obj.zTaskFolder.zRtClick(taskListName);

		obj.zMenuItem.zClick(localize(locator.del));

		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.confirmTitle));

		SleepUtil.sleep(1000);
	}

	public static void zTaskVerifyPercentProgress(String taskName,
			String percentage, String progress) throws Exception {

		String taskString = obj.zTaskItem.zGetInnerText(taskName);

		Assert.assertTrue(taskString.indexOf(percentage) >= 0,
				"Percentage is not correctly shown for the task in the list. Task list shows: "
						+ taskString);

		Assert.assertTrue(taskString.indexOf(progress) >= 0,
				"Percentage is not correctly shown for the task in the list. Task list shows: "
						+ taskString);
	}

	private static String getSubjectLabel() {
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE"))
			return getNameWithoutSpace(localize(locator.subjectLabel));
		else
			return localize(locator.subjectLabel);
	}
}
