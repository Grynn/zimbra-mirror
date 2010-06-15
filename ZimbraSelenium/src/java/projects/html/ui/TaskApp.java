package projects.html.ui;

import org.testng.Assert;

import projects.html.clients.ProvZCS;
import projects.html.tests.CommonTest;

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
public class TaskApp extends CommonTest {

	// ids in main task view
	public static final String zTasksTab = "id=tab_ikon_tasks";
	public static final String zTasksNewBtn = "id=NEW_TASK";
	public static final String zTaskDeleteBtn = "id=IOPDELETE";
	public static final String zTaskMoveBtn = "id=SOPMOVE";
	public static final String zTaskMoveMenu = "name=folderId";
	public static final String zTaskMoreActionsMenu = "name=actionOp";
	public static final String zTaskToolbarGoBtn = "id=SOPGO";

	// ids in task lists modify page
	public static final String zTaskNewListBtn = "id=SOPNEWCAL";
	public static final String zTaskLinkToSharedBtn = "id=SOPNEWLINK";
	public static final String zTaskListsCloseBtn = "id=OPCLOSE";

	public static final String zTaskNewListNameField = "id=newName";
	public static final String zTaskNewListColor = "id=color";
	public static final String zTaskNewListCreateBtn = "id=OPSAVE";
	public static final String zTaskNewListCancelBtn = "name=actionCancel";

	public static final String zTaskListNameField = "id=name";
	public static final String zTaskListColorDropdown = "id=folderColor";
	public static final String zTaskListSaveChangesBtn = "id=OPSAVE";
	public static final String zTaskListEmptyCheckbox = "id=emptyConfirm";
	public static final String zTaskListDeleteAllTasksBtn = "name=actionEmptyFolderConfirm";
	public static final String zTaskListDeleteConfirmCheckBox = "id=deleteConfirm";
	public static final String zTaskListDeleteListBtn = "name=actionDelete";

	// ids in Link to shared task list page
	public static final String zTaskSharedListName = "id=newName";
	public static final String zTaskSharedListOwnersEmail = "id=ownersEmail";
	public static final String zTaskSharedListOwnersListName = "id=ownersTlName";
	public static final String zTaskShareListColor = "id=color";
	public static final String zTaskShareListSaveBtn = "id=OPSAVE";
	public static final String zTaskShareListCancelBtn = "name=actionCancel";

	// ids in task compose page
	public static final String zTaskComposeSaveBtn = "id=SOPSAVE";
	public static final String zTaskComposeCancelBtn = "name=actionCancel";
	public static final String zTaskComposeSubjectField = "id=subject";
	public static final String zTaskComposeLocationField = "id=location";
	public static final String zTaskComposePriorityMenu = "id=priority";
	public static final String zTaskComposeTaskList = "id=apptFolderId";
	public static final String zTaskComposeCompletedCheckbox = "id=completed";
	public static final String zTaskComposeStatusMenu = "id=status";
	public static final String zTaskComposePercentMenu = "id=percent";
	public static final String zTaskComposeStartDate = "id=start";
	public static final String zTaskComposeEndDate = "id=end";
	public static final String zTaskComposeBody = "name=body";

	// ===========================
	// NAVIGATE METHODS
	// ===========================

	/**
	 * Navigates to tasks from MailApp
	 */
	public static void zNavigateToTasks() throws Exception {
		Thread.sleep(1000);
		obj.zButton.zClick(zTasksTab);
		Thread.sleep(2000);
		zWaitTillObjectExist("button", zTasksNewBtn);
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
		page.zLoginpage.zLoginToZimbraHTML(username);
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

		obj.zEditField.zType(zTaskComposeSubjectField, subject);

		if (!location.equals(""))
			obj.zEditField.zType(zTaskComposeLocationField, location);

		if (!priority.equals("")) {
			selenium.select(zTaskComposePriorityMenu, priority);
		}

		if (!body.equals(""))
			obj.zTextAreaField.zType(zTaskComposeBody, body);

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
	 * @param status
	 *            : progress of the task
	 * @param percent
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
			String priority, String taskList, String status, String percent,
			String startDate, String endDate, String body) throws Exception {

		zTaskEnterSimpleDetails(subject, location, priority, body);

		if (!taskList.equals("")) {
			obj.zHtmlMenu.zClick(zTaskComposeTaskList, taskList);
		}

		if (!status.equals("")) {
			obj.zHtmlMenu.zClick(zTaskComposeStatusMenu, status);
		}

		if (!percent.equals("")) {
			selenium.select(zTaskComposePercentMenu, percent);
		}

		if (!startDate.equals("")) {
			obj.zEditField.zType(zTaskComposeStartDate, startDate);
		}

		if (!endDate.equals("")) {
			obj.zEditField.zType(zTaskComposeEndDate, endDate);
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
	 *            : priority for the task
	 * @param body
	 *            : body for the task
	 * @throws Exception
	 */
	public static void zTaskCreateSimple(String subject, String location,
			String priority, String body) throws Exception {

		obj.zButton.zClick(zTasksNewBtn);
		Thread.sleep(1000);
		zTaskEnterSimpleDetails(subject, location, priority, body);
		obj.zButton.zClick(zTaskComposeSaveBtn);
		Thread.sleep(1500);
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
		Thread.sleep(1000);
		zTaskEnterDetails(subject, location, priority, taskList, progress,
				progressPercent, startDate, endDate, body);
		obj.zButton.zClick(zTaskComposeSaveBtn);
		Thread.sleep(1500);
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
		obj.zMessageItem.zClickCheckBox(taskname);
	}

	/**
	 * opens task by double clicking
	 * 
	 * @param taskname
	 *            : subject of the task to be opened
	 * @throws Exception
	 */
	public static void zTaskOpenByClick(String taskname) throws Exception {
		obj.zMessageItem.zExists(taskname);
		obj.zMessageItem.zClick(taskname);
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

		zTaskOpenByClick(orgSubject);

		zTaskEnterDetails(newSubject, newLocation, newPriority, newTaskList,
				newProgress, newProgressPercent, newStartDate, newEndDate,
				newBody);

		obj.zButton.zClick(zTaskComposeSaveBtn);

		Thread.sleep(1000);
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

		obj.zMessageItem.zExists(taskname);

		zTaskSelect(taskname);

		obj.zButton.zClick(zTaskDeleteBtn);
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
		zNavigateToTasks();
		obj.zFolder.zClick(taskList);
		Thread.sleep(1000);
		obj.zMessageItem.zExists(subject);
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
		Thread.sleep(1500);
		obj.zMessageItem.zNotExists(subject);
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
		Thread.sleep(1000);
		obj.zMessageItem.zExists(taskname);
		obj.zMessageItem.zClickCheckBox(taskname);
		Thread.sleep(1000); // test fails here
		selenium.select(zTaskMoveMenu, destinationTaskList);
		Thread.sleep(1000);
		obj.zButton.zClick(zTaskMoveBtn);
		Thread.sleep(1000);
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
	public static void zTaskTagToolbar(String taskName, String tagName,
			String taskListName) throws Exception {
		if (!taskListName.equals(""))
			obj.zFolder.zClick(taskListName);
		obj.zMessageItem.zClickCheckBox(taskName);
		Thread.sleep(1000);
		obj.zHtmlMenu.zClick(zTaskMoreActionsMenu, tagName);
		Thread.sleep(1000);
	}

	/**
	 * Opens and verifies that the task details
	 * 
	 * @param subject
	 * @param location
	 * @param priority
	 * @param body
	 * @throws Exception
	 */
	public static void zTaskVerifyDetails(String subject, String location,
			String priority, String body) throws Exception {

		String actLoc, actPriority, actBody;

		zTaskOpenByClick(subject);

		actLoc = obj.zEditField.zGetInnerText(zTaskComposeLocationField);
		actPriority = obj.zHtmlMenu.zGetInnerText(zTaskComposePriorityMenu);
		actBody = obj.zTextAreaField.zGetInnerText(zTaskComposeBody);

		Assert.assertTrue(location.equals(actLoc),
				"Location of task is correct");
		Assert.assertTrue(priority.equals(actPriority),
				"Priority of task field is correct");
		Assert.assertTrue(body.equals(actBody), "Body of the task is correct");

	}

	/**
	 * navigates to the Tasks edit page
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToTaskListPage() throws Exception {
		zNavigateToTasks();
		obj.zButton.zClick("id=MTASKS");
		// obj.zFolder.zEdit(localize(locator.tasks));
	}

	/**
	 * creates a task list using the 'New Task List' button in the overview
	 * panel
	 * 
	 * @param taskListName
	 *            : name of the task list to be created
	 * @throws Exception
	 */
	public static void zTaskListCreate(String taskListName) throws Exception {
		zNavigateToTaskListPage();
		obj.zButton.zClick(zTaskNewListBtn);
		Thread.sleep(1000);
		obj.zEditField.zType(zTaskNewListNameField, taskListName);
		obj.zButton.zClick(zTaskNewListCreateBtn);
		Thread.sleep(1200);
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

		zNavigateToTaskListPage();

		// below sleep required

		Thread.sleep(3000);

		obj.zFolder.zClick(oldTaskListName);

		Thread.sleep(3000);

		obj.zEditField.zType(zTaskListNameField, newTaskListName);

		obj.zButton.zClick(zTaskListSaveChangesBtn);

	}

	/**
	 * deletes the specified task list
	 * 
	 * @param taskListName
	 *            : task list to be deleted
	 * @throws Exception
	 */
	public static void zTaskListDelete(String taskListName) throws Exception {

		zNavigateToTaskListPage();

		obj.zFolder.zClick(taskListName);

		obj.zCheckbox.zClick(zTaskListDeleteConfirmCheckBox);

		obj.zButton.zClick(zTaskListDeleteListBtn);
	}

	public static void zTaskListDeleteAllTasks(String taskList)
			throws Exception {

		zNavigateToTaskListPage();

		obj.zFolder.zClick(taskList);

		obj.zCheckbox.zClick(zTaskListEmptyCheckbox);

		obj.zButton.zClick(zTaskListDeleteAllTasksBtn);

	}

	//
	// public static void zTaskVerifyPercentProgress(String taskName,
	// String percentage, String progress) throws Exception {
	//
	// String taskString = obj.zTaskItem.zGetInnerText(taskName);
	//
	// Assert.assertTrue(taskString.indexOf(percentage)>=0,
	// "Percentage is not correctly shown for the task in the list. Task list shows: "
	// + taskString);
	//		
	// Assert.assertTrue(taskString.indexOf(progress)>=0,
	// "Percentage is not correctly shown for the task in the list. Task list shows: "
	// + taskString);
	// }
	//
	// private static String getSubjectLabel() {
	// if (config.getString("browser").equals("IE"))
	// return localize(locator.subjectLabel).replace("�:", "");
	// else
	// return localize(locator.subjectLabel);
	// }
	//	
	//	
	// // private static String getNameWithoutSpace(String key) {
	// // if (config.getString("browser").equals("IE"))
	// // return key.replace("�:", "");
	// // else
	// // return key;
	// // }
	//	
	//	
}
