package framework.util;

import java.io.File;


public class FirefoxProfile {
	
	public static String baseDir = "C:\\Documents and Settings\\"+System.getProperty("user.name")+"\\Application Data\\Mozilla\\Firefox\\Profiles";
	
	public static String getProfile() {
		System.getProperties();
	   File dir = new File(baseDir);
	   String filename = "";
	    String[] children = dir.list();
	    if (children != null){
	        for (int i=0; i<children.length; i++) {
	        	String tmp = children[i];
	        	if(tmp.indexOf(".default") > 0) {
	        		filename = baseDir + "\\" + tmp;
	        		break;
	        	}
	        }
	    } 
	    
	    return filename;
	}
	
	public static void main(String args []) {
		System.out.println(getProfile());
	}
}
