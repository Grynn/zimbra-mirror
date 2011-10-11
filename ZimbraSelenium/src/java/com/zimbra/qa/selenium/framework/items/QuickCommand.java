package com.zimbra.qa.selenium.framework.items;

import java.util.*;

import org.apache.log4j.*;

import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * Helper class to convert QuickCommands to JSON for ModifyPrefsRequest usage
 * @author zimbra
 *
 */
public class QuickCommand {
	protected static Logger logger = LogManager.getLogger(QuickCommand.class);

	/**
	 * The type of item this Quick Command applies to
	 */
	public enum QCItemTypeId {
		MSG,
		APPT,
		CONTACT,
		Null
	}

	private int ID = 1;
	private String Name = null;
	private String Description = null;
	private QCItemTypeId Type = null;
	private boolean IsActive = true;
	private ArrayList<QCAction> Actions = new ArrayList<QCAction>();
	
	public QuickCommand() {
	}
	
	public QuickCommand(String name, String description, QCItemTypeId type, boolean isActive) throws HarnessException {
		Name = name;
		Description = description;
		Type = type;
		IsActive = isActive;
	}

	protected String addEntry(String k, Object v) {
		String key = "\"" + k + "\"";
		String value = v.toString();
		
		if ( v instanceof String)
			value = "\""+ v.toString() +"\"";		// In the client, Strings are surrounded by quotes
		
		if ( v instanceof List<?>)
			value = v.toString().replace(" ", "");	// In the client, there are no spaces between list entries
		
		return (key + ":" + value);
	}

	public String toString() {
		LinkedHashMap<String, Object> table = new LinkedHashMap<String, Object>();

		// Convert this item to the base table
		table.put("id", new Integer(getID()));
		table.put("itemTypeId", getType().toString());
		table.put("name", getName());
		table.put("description", getDescription());
		table.put("isActive", new Boolean(isActive()));
		table.put("actions", getActions());
		
		StringBuilder sb = null;
		for (Map.Entry<String, Object> entry : table.entrySet()) {
			if ( sb == null ) {
				sb = new StringBuilder();
				sb.append(addEntry(entry.getKey(), entry.getValue()));
			} else {
				sb.append(",").append(addEntry(entry.getKey(), entry.getValue()));
			}
		}
		return (String.format("{%s}", sb == null ? "" : sb.toString()));
		
	}
	
	public int getID() {
		ID = 1;	// TODO: How is the ID used?  It seems to always be 1.
		return ID;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public QCItemTypeId getType() {
		return Type;
	}

	public void setType(QCItemTypeId type) {
		Type = type;
	}

	public boolean isActive() {
		return IsActive;
	}

	public void setActive(boolean isActive) {
		IsActive = isActive;
	}

	public ArrayList<QCAction> addAction(QCAction action) {
		Actions.add(action);
		return (Actions);
	}
	
	public ArrayList<QCAction> getActions() {
		
		// Reset all the IDs
		int id = 1;
		for(QCAction a : Actions) {
			a.setID(id++);
		}
		
		return Actions;
	}

	public void addActions(ArrayList<QCAction> actions) {
		Actions = actions;
	}



	public static class QCAction {

		public enum QCTypeId {
			actionTag,
			actionFlag,
			actionFileInto,
			Null
		}

		private int ID = 1;
		private QCTypeId Type = QCTypeId.Null;
		private String Value = null;
		private boolean IsActive = true;
		
		public QCAction() {	
		}
		
		public QCAction(QCTypeId type, String value, boolean isActive) {

			Type = type;
			Value = value;
			IsActive = isActive;
			
		}

		protected String addEntry(String k, Object v) {
			String key = "\"" + k + "\"";
			String value = v.toString();
			
			if ( v instanceof String)
				value = "\""+ v.toString() +"\"";		// In the client, Strings are surrounded by quotes
			
			if ( v instanceof List<?>)
				value = v.toString().replace(" ", "");	// In the client, there are no spaces between list entries
			
			return (key + ":" + value);
		}

		public String toString() {
			LinkedHashMap<String, Object> table = new LinkedHashMap<String, Object>();

			table.put("id", new Integer(getID()));
			table.put("typeId", getType().toString());
			table.put("value", getValue());
			table.put("isActive", new Boolean(isActive()));
			
			StringBuilder sb = null;
			for (Map.Entry<String, Object> entry : table.entrySet()) {
				if ( sb == null ) {
					sb = new StringBuilder();
					sb.append(addEntry(entry.getKey(), entry.getValue()));
				} else {
					sb.append(",").append(addEntry(entry.getKey(), entry.getValue()));
				}
			}
			return (String.format("{%s}", sb == null ? "" : sb.toString()));
			
		}
		
		public int getID() {
			return (ID);
		}
		
		public void setID(int id) {
			ID = id;
		}

		public QCTypeId getType() {
			return Type;
		}

		public void setType(QCTypeId type) {
			Type = type;
		}

		public String getValue() {
			return Value;
		}

		public void setValue(String value) {
			Value = value;
		}

		public boolean isActive() {
			return IsActive;
		}

		public void setActive(boolean isActive) {
			IsActive = isActive;
		}


	}


	public static void main(String[] args) throws HarnessException {
		
		ArrayList<QCAction> actions = new ArrayList<QCAction>();
		actions.add(new QCAction(QCAction.QCTypeId.actionTag, "257", true));
		actions.add(new QCAction(QCAction.QCTypeId.actionFileInto, "2", true));
		
		QuickCommand qc = new QuickCommand("NAME", "DESCRIPTION", QCItemTypeId.CONTACT, true);
		qc.addAction(new QCAction(QCAction.QCTypeId.actionTag, "257", true));
		qc.addAction(new QCAction(QCAction.QCTypeId.actionFileInto, "2", true));
		
		logger.info("QC: "+ qc.toString());

		logger.info("Done!");
	}

}
