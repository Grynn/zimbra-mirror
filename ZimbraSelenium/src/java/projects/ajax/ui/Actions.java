package projects.ajax.ui;


public class Actions {

	// General actions
	public static final Action A_LEFTCLICK = new Action("A_CLICK");
	public static final Action A_SHIFTSELECT = new Action("A_CLICK");
	public static final Action A_CTRLSELECT = new Action("A_CLICK");
	public static final Action A_RIGHTCLICK = new Action("A_CLICK");

	// Mail page actions
	public static final Action A_MAIL_CHECKBOX = new Action("A_MAIL_CHECK");
	public static final Action A_MAIL_FLAG = new Action("A_MAIL_CHECK");
	public static final Action A_MAIL_EXPANDCONVERSATION = new Action("A_MAIL_CHECK");
	
	public static class Action {
		private final String ID;
		
		protected Action(String id) {
			this.ID = id;
		}

		@Override
		public String toString() {
			return ID;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((ID == null) ? 0 : ID.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Action other = (Action) obj;
			if (ID == null) {
				if (other.ID != null)
					return false;
			} else if (!ID.equals(other.ID))
				return false;
			return true;
		}
	}
}
