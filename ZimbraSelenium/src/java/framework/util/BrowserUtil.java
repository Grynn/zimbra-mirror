package framework.util;

import framework.core.SelNGBase;

public class BrowserUtil extends SelNGBase {
    public static String userAgent = "";

    public static void setBrowserAgent() {
	//ps: getZimbraVersion is being passed just to trick selenium.call to return string(because of get*)
	if (userAgent.equals(""))
	    userAgent = SelNGBase.selenium.get().getEval("navigator.userAgent;");
    }

    public static String getBrowserName() {
	setBrowserAgent();
	String browserName = "";
	if (userAgent.indexOf("Firefox/") >= 0){
	    browserName = "FF " + userAgent.split("Firefox/")[1];
		String[] temp = browserName.split(" ");
		browserName = temp[0]+ " "+ temp[1];
	} else if (userAgent.indexOf("MSIE") >= 0) {
	    String[] arry = userAgent.split(";");
	    for (int t = 0; t < arry.length; t++) {
		if (arry[t].indexOf("MSIE") >= 0) {
		    browserName = arry[t];
		    break;
		}
	    }
	} else if (userAgent.indexOf("Safari") >= 0) {
	    String[] arry = userAgent.split("/");
	    for (int t = 0; t < arry.length; t++) {
		if (arry[t].indexOf("Safari") >= 0) {
		    String [] tmp = arry[t].split(" ");
		    browserName = tmp[1] + " " +tmp[0];
		    break;
		}
	    }
	}
	return browserName;
    }

}