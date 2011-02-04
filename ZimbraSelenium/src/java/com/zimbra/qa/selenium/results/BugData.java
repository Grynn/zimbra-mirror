package com.zimbra.qa.selenium.results;

import java.io.*;
import java.security.*;
import java.util.*;

import org.apache.log4j.*;

public abstract class BugData {
	protected static Logger logger = LogManager.getLogger(BugStatus.class);

	/**
	 * A list of directory paths that can contain the bug report information
	 */
	protected static List<File> paths = null;
	
	/**
	 * A map of file names to file size.  Used to determine whether to reload the data
	 */
	protected static Map<String, String> hashes = new HashMap<String, String>();
	

	protected BugData() {
		logger.info("new " + BugData.class.getCanonicalName());
		
		if (paths == null) {
			paths = new ArrayList<File>();
			paths.add(new File("/opt/qa/testlogs/BugReports"));
			paths.add(new File("T:\\BugReports"));
			paths.add(new File("C:\\BugReports"));
		}
		
	}


	/**
	 * Return the database file, if it exists, in the normal data paths 
	 * @param filename
	 * @return
	 * @throws IOException 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	protected File getDatafile(String filename) throws IOException {
		File datafile = null;
		
		// Find where the database files are located
		for (File directory : paths) {
			File file = new File(directory, filename);
			if ( file.exists() ) {
				datafile = file;
				break; // Found it
			}
		}
		
		if ( datafile == null ) {
			logger.error("Unable to find the database file in the path list");
			return (null);
		}
		
		if ( isNewDatafile(datafile) )
			return (datafile);
		
		logger.error("Datafile was the same as last time.  Returning null.");
		return (null);
	}
	
	/**
	 * Checks if the datafile contains new content
	 * @param datafile
	 * @return false if the same content is contained, true otherwise
	 * @throws IOException 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	protected boolean isNewDatafile(File datafile) throws IOException {
		
		String filename = datafile.getCanonicalPath();
		String hash = getHash(datafile);
		
		if (hash == null) {
			// Unable to determine hash information.  Return TRUE by default
			return (true);
		}
				
		// Check if there was ever a record for this file
		if ( !hashes.containsKey(filename) ) {
		
			// Save the size
			hashes.put(filename, hash);
			
			// It is a new datafile.  Return TRUE
			return (true);
			
		}
		
		if ( hash.equals( hashes.get(filename) ) ) {
			// Sizes are equal.  It is an old datafile.  Return FALSE
			return (false);
		}
		
		// Hashes are not equal.  Save the new hash.  It is a new datafile.  Return TRUE
		hashes.put(filename, hash);
		return (true);
		
	}
		
	/**
	 * Determine the hash value for the datafile<p>
	 * Look for datafile.getCanonicalPath() + ".MD5", and if available return its contents.<br>
	 * If the file does not exist, return null
	 * <p>
	 * @param datafile
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	protected String getHash(File datafile) throws IOException {
		
		String hash = null;
		String filename = datafile.getCanonicalPath();
		logger.debug("Hash: checking file "+ filename);

		// Check for existing MD5 file
		File md5 = new File(filename +".MD5");
		if ( md5.exists() ) {
			
			StringBuilder sb = new StringBuilder();
			String line;
			
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(md5));
				while ( (line = reader.readLine()) != null )
					sb.append(line);
			} finally {
				if ( reader != null )
					reader.close();
			}
			
			hash = sb.toString().trim();
			logger.debug(String.format("Hash: filename(%s) hash(%s)", filename, hash));
			return (hash);
		}
		
		logger.debug("Hash: MD5 file does not exist! " + filename +".MD5");
		return (null);
		

	}
}
