package parallel;

public class ZimbraApplication {

	protected ZimbraAccount loggedInAccount = null;
	public ZimbraApplication() {
	}
	
	public void login(ZimbraAccount a) {
		loggedInAccount = a;
	}
	
}
