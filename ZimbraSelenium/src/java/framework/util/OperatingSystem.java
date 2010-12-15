package framework.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class OperatingSystem {
	private static Logger logger = LogManager.getLogger(OperatingSystem.class);
	
	public static boolean isWindows() {
		return (OperatingSystem.getSingleton().os.startsWith("Windows"));
	}
	
	public static boolean isWindowsXP() {
		return (OperatingSystem.getSingleton().os.equals("Windows XP"));
	}

	public static boolean isLinux() {
		return (OperatingSystem.getSingleton().os.startsWith("Linux"));
	}
	
	public static boolean isMac() {
		return (OperatingSystem.getSingleton().os.startsWith("Mac"));
	}

	private String os = null;
	
	// Singleton methods
	//
	
    private volatile static OperatingSystem singleton;
 
    private OperatingSystem() {
		os = System.getProperty("os.name");
		logger.info("Operating System: "+ os);

    }
 
    private static OperatingSystem getSingleton() {
        if(singleton==null) {
            synchronized(OperatingSystem.class){
                if(singleton == null) {
                    singleton = new OperatingSystem();
                }
            }
        }
        return singleton;
    }	
}
