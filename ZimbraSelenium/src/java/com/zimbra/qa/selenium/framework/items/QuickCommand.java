package com.zimbra.qa.selenium.framework.items;

import java.util.*;

import net.sf.json.*;

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
	public enum ItemTypeId {
		MSG,
		APPT,
		CONTACT,
		Null
	}

	private int ID = 1;
	private String Name = null;
	private String Description = null;
	private ItemTypeId Type = null;
	private boolean IsActive = true;
	private ArrayList<QuickCommandAction> Actions = new ArrayList<QuickCommandAction>();
	
	public QuickCommand() {
	}
	
	public QuickCommand(String name, String description, ItemTypeId type, boolean isActive) throws HarnessException {
		Name = name;
		Description = description;
		Type = type;
		IsActive = isActive;
	}

	/**
	 * Marshall this object into JSON
	 */
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("itemTypeId", getType().toString());
		json.put("name", getName());
		json.put("description", getDescription());
		json.put("isActive", isActive());
		
		JSONArray actions = new JSONArray();
		for (QuickCommandAction a : getActions()) {
			actions.add(a.toJSON());
		}

		json.put("actions", actions);
		return (json);
	}
	
	/**
	 * Marshall this object into String (suitable for use with ModifyPrefsRequest)
	 */
	public String toString() {
		return (toJSON().toString());
	}
	
	/**
	 * Unmarshall this object from JSONObject
	 */
	public static QuickCommand fromJSON(JSONObject json) {
		
		QuickCommand command = new QuickCommand();
		
		command.setId(json.getInt("id"));
		command.setName(json.getString("name"));
		command.setDescription(json.getString("description"));
		command.setType(ItemTypeId.valueOf(json.getString("itemTypeId")));
		command.setActive(json.getBoolean("isActive"));
		
		JSONArray actions = json.getJSONArray("actions");
		for (Object o : actions) {
			QuickCommandAction a = QuickCommandAction.fromJSON((JSONObject)o);
			command.addAction(a);
		}

		return (command);

	}
	
	/**
	 * Unmarshall this object from String
	 */
	public static QuickCommand fromJSON(String pref) {
		return (fromJSON((JSONObject) JSONSerializer.toJSON(pref)));
	}

	public int getId() {
		return ID;
	}

	public void setId(int id) {
		ID = id;
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

	public ItemTypeId getType() {
		return Type;
	}

	public void setType(ItemTypeId type) {
		Type = type;
	}

	public boolean isActive() {
		return IsActive;
	}

	public void setActive(boolean isActive) {
		IsActive = isActive;
	}

	public ArrayList<QuickCommandAction> addAction(QuickCommandAction action) {
		Actions.add(action);
		return (Actions);
	}
	
	public ArrayList<QuickCommandAction> getActions() {
		
		// Reset all the IDs
		int id = 1;
		for(QuickCommandAction a : Actions) {
			a.setId(id++);
		}
		
		return Actions;
	}

	public void addActions(ArrayList<QuickCommandAction> actions) {
		Actions = actions;
	}



	public static class QuickCommandAction {

		public enum TypeId {
			actionTag,
			actionFlag,
			actionFileInto,
			Null
		}

		private int ID = 1;
		private TypeId Type = TypeId.Null;
		private String Value = null;
		private boolean IsActive = true;
		
		public QuickCommandAction() {	
		}
		
		public QuickCommandAction(TypeId type, String value, boolean isActive) {

			this.setType(type);
			this.setValue(value);
			this.setActive(isActive);
			
		}

		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			json.put("id", getId());
			json.put("typeId", getType().toString());
			json.put("value", getValue());
			json.put("isActive", isActive());
			return (json);
		}
		
		public String toString() {
			return (toJSON().toString());
		}
		
		public static QuickCommandAction fromJSON(JSONObject json) {
			
			QuickCommandAction action = new QuickCommandAction();
			
			action.setId(json.getInt("id"));
			action.setType(TypeId.valueOf(json.getString("typeId")));
			action.setValue(json.getString("value"));
			action.setActive(json.getBoolean("isActive"));
			
			return (action);

		}
		
		public static QuickCommandAction fromJSON(String string) {
			return (fromJSON((JSONObject) JSONSerializer.toJSON(string)));
		}

		public void setId(int id) {
			this.ID = id;
		}

		public int getId() {
			return ID;
		}

		public void setType(TypeId type) {
			this.Type = type;
		}

		public TypeId getType() {
			return Type;
		}

		public void setValue(String value) {
			this.Value = value;
		}

		public String getValue() {
			return Value;
		}

		public void setActive(boolean isActive) {
			this.IsActive = isActive;
		}

		public boolean isActive() {
			return (this.IsActive);
		}




	}


	public static void main(String[] args) throws HarnessException {
		
		ArrayList<QuickCommandAction> actions = new ArrayList<QuickCommandAction>();
		actions.add(new QuickCommandAction(QuickCommandAction.TypeId.actionTag, "257", true));
		actions.add(new QuickCommandAction(QuickCommandAction.TypeId.actionFileInto, "2", true));
		
		QuickCommand qc = new QuickCommand("NAME", "DESCRIPTION", ItemTypeId.CONTACT, true);
		qc.addAction(new QuickCommandAction(QuickCommandAction.TypeId.actionTag, "257", true));
		qc.addAction(new QuickCommandAction(QuickCommandAction.TypeId.actionFileInto, "2", true));
		
		logger.info("QC: "+ qc.toString());

		/**
		 *     <ModifyPrefsRequest xmlns="urn:zimbraAccount">
		 *     	<pref name="zimbraPrefQuickCommand">{"id":1,"itemTypeId":"MSG","name":"qcname","description":"qcdescription","isActive":true,"actions":[{"id":1,"typeId":"actionTag","value":"257","isActive":true},{"id":2,"typeId":"actionFlag","value":"unread","isActive":true}]}</pref>
		 *      <pref name="zimbraPrefQuickCommand">{"id":2,"itemTypeId":"CONTACT","name":"qcname2","description":"qcdescription2","isActive":true,"actions":[{"id":1,"typeId":"actionTag","value":"258","isActive":true},{"id":2,"typeId":"actionFileInto","value":"2","isActive":true}]}</pref>
		 *     </ModifyPrefsRequest>
		 */
		
		String pref = "{\"id\":1,\"itemTypeId\":\"MSG\",\"name\":\"qcname\",\"description\":\"qcdescription\",\"isActive\":true,\"actions\":[{\"id\":1,\"typeId\":\"actionTag\",\"value\":\"257\",\"isActive\":true},{\"id\":2,\"typeId\":\"actionFlag\",\"value\":\"unread\",\"isActive\":true}]}";
		QuickCommand imported = QuickCommand.fromJSON(pref);

		logger.info("QC: ID "+ imported.getId());
		logger.info("QC: Name "+ imported.getName());
		logger.info("QC: Description "+ imported.getDescription());
		logger.info("QC: Active "+ imported.isActive());

		for (QuickCommandAction a : imported.getActions()) {
			logger.info("Action: ID "+ a.getId());
			logger.info("Action: Type "+ a.getType());
			logger.info("Action: Value "+ a.getValue());
		}
		
		logger.info(imported.toString());

		logger.info("Done!");
	}

}
