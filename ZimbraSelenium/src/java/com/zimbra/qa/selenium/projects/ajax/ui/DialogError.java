package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.ui.*;

/**
 * A <code>DialogError</code> object represents a "Error" dialog, such as "Permission 
 * denied", etc.
 * <p>
 * During construction, the div ID attribute must be specified, such as "Zimbra".
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogError extends DialogWarning {

	public static class DialogErrorID extends DialogWarning.DialogWarningID {

		/**
		 * General "Zimbra" server error (such as PERM_DENIED, etc.)
		 * See: https://bugzilla.zimbra.com/show_bug.cgi?id=57207
		 */
		public static DialogErrorID Zimbra = new DialogErrorID("DWT30");

		protected DialogErrorID(String id) {
			super(id);
		}
		
	}

	public DialogError(DialogErrorID dialogId, AbsApplication application, AbsTab tab) {
		super(dialogId, application, tab);
	}

}
