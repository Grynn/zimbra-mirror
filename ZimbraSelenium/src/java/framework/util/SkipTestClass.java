package framework.util;

public class SkipTestClass {
    // #className;methodName;locales;browsers;bugs;remark
    public String className;
    public String methodToSkip = "";
    public String locales = "";
    public String browsers = "";
    public String bugs = "";
    public String remark = "";

    public SkipTestClass(String skipTestInfo)  {
	// skipTestInfo should be of the format:
	//CLASS:projects.zcs.tests.tasks.Tasks;METHOD:createSimpleTaskInTaskList;LOCALE:all;BROWSERS:na;BUGS:1234;REMARK:script issue
	String[] temp = skipTestInfo.split(";");
	for(int i=0;i<temp.length;i++){
	    String str = temp[i];
	    if(str.indexOf("CLASS:") >=0){
		className = str.replace("CLASS:", "");
	    } else if(str.indexOf("METHOD:") >=0) {
		methodToSkip = str.replace("METHOD:", "");
		
	    }else if(str.indexOf("LOCALE:") >=0) {
		locales  = str.replace("LOCALE:", "");		
	    }else if(str.indexOf("BROWSERS:") >=0) {
		browsers  = str.replace("BROWSERS:", "");		
	    }else if(str.indexOf("BUGS:") >=0) {
		bugs  = str.replace("BUGS:", "");		
	    }else if(str.indexOf("REMARK:") >=0) {
		remark  = str.replace("REMARK:", "");		
	    }
	}


    }

}
