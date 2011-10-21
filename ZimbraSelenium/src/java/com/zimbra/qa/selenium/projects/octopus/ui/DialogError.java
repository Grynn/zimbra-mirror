package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.ui.*;

public class DialogError extends DialogWarning {

	public static class DialogErrorID extends DialogWarning.DialogWarningID {

		public static final DialogErrorID ErrorDialog = new DialogErrorID("ErrorDialog");
		
		public static final DialogErrorID InvalidFolderName = new DialogErrorID("InvalidFolderName");

		protected DialogErrorID(String id) {
			super(id);
		}
		
	}

	public DialogError(DialogErrorID dialogId, AbsApplication application, AbsTab tab) {
		super(dialogId, application, tab);
	}

}
