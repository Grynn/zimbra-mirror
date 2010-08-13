package framework.items;

public class RecipientItem {

	public enum RecipientType {
		To, Cc, Bcc, From
	}
	public String emailAddress;
	public String name;
	public RecipientType type;
	
	public RecipientItem() {
	}
	
	public RecipientItem(String email) {	
	}
	
	
}
