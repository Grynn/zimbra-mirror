package com.zimbra.qa.selenium.framework.items;

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * Helper class to convert QuickCommands to JSON for ModifyPrefsRequest usage
 * @author zimbra
 *
 */
public class QuickCommand extends QuickCommandBase {
	protected static Logger logger = LogManager.getLogger(QuickCommand.class);

	
	public enum QCItemTypeId {
		MSG,
		APPT,
		CONTACT,
		Null
	}

	public QuickCommand(int id, String name, String description, QCItemTypeId type, boolean isActive, ArrayList<QCAction> actions) throws HarnessException {
		table.put("id", new Integer(id));
		table.put("itemTypeId", type.toString());
		table.put("name", name);
		table.put("description", description);
		table.put("isActive", new Boolean(isActive));
		table.put("actions", actions);
	}

	public int getId() {
		return ((Integer)table.get("id"));
	}
	
	public QCItemTypeId getType() {
		String value = (String)table.get("type");
		for ( QCItemTypeId t : QCItemTypeId.values() ) {
			if ( t.toString().equalsIgnoreCase(value) ) {
				return (t);
			}
		}
		return (QCItemTypeId.Null);
	}

	public String getName() {
		return ((String)table.get("name"));
	}

	public String getDescription() {
		return ((String)table.get("description"));
	}

	public boolean isActive() {
		return ((Boolean)table.get("isActive"));
	}

	
	public static class QCAction extends QuickCommandBase {

		public enum QCTypeId {
			actionTag,
			actionFlag,
			actionFileInto,
			Null
		}

		public QCAction(int id, QCTypeId type, String value, boolean isActive) {
			table.put("id", new Integer(id));
			table.put("typeId", type.toString());
			table.put("value", value);
			table.put("isActive", new Boolean(isActive));
		}

	}


	public static void main(String[] args) throws HarnessException {
		
		ArrayList<QCAction> actions = new ArrayList<QCAction>();
		actions.add(new QCAction(1, QCAction.QCTypeId.actionTag, "257", true));
		actions.add(new QCAction(2, QCAction.QCTypeId.actionFileInto, "2", true));
		
		QuickCommand qc = new QuickCommand(1, "NAME", "DESCRIPTION", QCItemTypeId.CONTACT, true, actions);
		logger.info("QC: "+ qc.toString());

		logger.info("Done!");
	}

}
