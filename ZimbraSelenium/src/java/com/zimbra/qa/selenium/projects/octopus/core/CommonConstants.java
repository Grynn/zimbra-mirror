package com.zimbra.qa.selenium.projects.octopus.core;

public interface CommonConstants {
	String JPG_FILE = "samplejpg.jpg";
	String PPT_FILE = "testpptfile.ppt";
	
	String SHARE_AS_READ = "r";
	String SHARE_AS_READWRITE = "rwidx";
	String SHARE_AS_ADMIN = "rwidxa";
		
	enum SHARE_PERMISSION {SHARE_AS_READ, SHARE_AS_READWRITE, SHARE_AS_ADMIN};
	
	//TODO: add full list of filename charactesrs
	String REGEXP_FILENAME = "[0-9a-zA-Z_]+";
	
	//user name can also be an email address if display name is missing
	String REGEXP_USER = "[0-9a-zA-Z@]+";
}  