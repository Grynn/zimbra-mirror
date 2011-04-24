/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;

/**
 * This class represents a new document item
 * 
 * 
 */
public class FileItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);
	
	/**
	 * Create a file item
	 */
	public FileItem(String path) {		
		filePath = path;
		String[] arr = filePath.split("/");
		fileName = arr[arr.length - 1].trim();
	}
	
	/**
	 * The file name
	 */
	private String fileName;

	/**
	 * The file path
	 */
	private String filePath;
	
	/**
	 * The file if
	 */
	//private String fileId;
	
	/**
	 * The status of this file
	 */
	public boolean isSaved;

	/**
	 * The version of this file
	 */
	public int version;

	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String path) {
		filePath = path;;
	}

	@Override
	public String getName() {
		return fileName;
	}
	
	public void setFileName(String name) {
		fileName = name;;
	}
	
	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(DocumentItem.class.getSimpleName()).append('\n');
		sb.append("Doc name: \n").append(fileName).append('\n');
		sb.append("Doc text: \n").append(fileName).append("\n");
		return (sb.toString());
	}

	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		// TODO Auto-generated method stub
		
	}	
}
