package projects.zcs.bin;

import java.util.ArrayList;

public class ClientMachine
{
    public  String machineName;
    public  ArrayList<String> browsers= new ArrayList<String>();
    public ClientMachine(String mName){
	machineName = mName;
    }
    public ClientMachine(String mName, String osType,String commaSaperatedBrowsers){
	machineName = mName;
	String [] b = commaSaperatedBrowsers.split(",");
	for(int i=0;i<b.length;i++){
	    browsers.add(b[i]);
	}
	
    }
}
