package parallel;


public class ZimbraAccount {
	
	private static int counter = 0;
	protected synchronized int myCounter() {
		return (++counter);
	}

	public String emailAddress = null;
	public ZimbraAccount() {
		emailAddress = "email"+myCounter()+"@domain.com";		
	}
}
