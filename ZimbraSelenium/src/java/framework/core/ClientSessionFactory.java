package framework.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ClientSessionFactory {
	private static Logger logger = LogManager.getLogger(ClientSessionFactory.class);
	
	/**
	 * Get the current client session for this test case
	 * @return
	 */
	public static ClientSession session() {
		long threadID = Thread.currentThread().getId();
		ClientSession session = getSingleton().getClientSession(Long.toString(threadID));
		logger.debug("Factory found session: "+ session.toString());
		return (session);
	}
	
	// ----
	// Singleton methods
	// ----
	private volatile static ClientSessionFactory singleton;

	private static ClientSessionFactory getSingleton() {
		if(singleton==null) {
			synchronized(ClientSessionFactory.class){
				if(singleton==null)
					singleton= new ClientSessionFactory();
			}
		}
		return (singleton);
	}
	// ----
	// End Singleton methods
	// ----

	// ----
	// Object methods
	// ----
	private Map<String, ClientSession> SessionMap = null;

	private ClientSessionFactory() {
		logger.info("New ClientSessionFactory");
		SessionMap = new HashMap<String, ClientSession>();
	}
	
	private ClientSession getClientSession(String threadID) {

		// If the session already exists for this ID, return it
		if ( SessionMap.containsKey(threadID) ) {
			return (SessionMap.get(threadID));
		}
		
		// If the session doesn't yet exist,
		// 1. Create it
		// 2. Insert it into the table
		// 3. return it
		ClientSession session = new ClientSession();
		SessionMap.put(threadID, session);
		
		return (session);
		
		
	}

	// ----
	// End Object methods
	// ----

}
