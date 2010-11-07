package projects.ajax.ui;

import framework.ui.AbsApplication;
import framework.ui.AbsDisplay;
import framework.util.HarnessException;
import framework.util.SleepUtil;

public class DisplayMail extends AbsDisplay {

	public static final String lSubject = "xpath=//td[@class='LabelColValue SubjectCol']";
	public static final String lDate = "xpath=//td[@class='LabelColValue DateCol']";

	
	public static final String lViewEntireMessage = "id=zv__CLV__MSG_msgTruncation_link";
	public static final String lHighlightObjects = "id=zv__CLV_highlightObjects_link";
	

	
	public DisplayMail(AbsApplication application) {
		super(application);
		
		logger.info("new " + DisplayMail.class.getCanonicalName());
		
		// Let the reading pane load
		SleepUtil.sleepLong();


	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	public Object zClickViewEntireMessage() throws HarnessException {
		logger.info(myPageName() + " zViewEntireMessage");
		
		if ( this.sIsElementPresent(lViewEntireMessage) )
			throw new HarnessException("'View Entire Message' link does not exist: "+ lViewEntireMessage);
		
		this.sClick(lViewEntireMessage);
		
		SleepUtil.sleepLong();	// Messages are usually large, let it load

		// TODO: return the new window?
		return (null);
	}

	public Object zClickHighlightObjects() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	public String zGetSubject() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public String zGetDate() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public Object zGetFrom() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public Object zGetTo() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public Object zGetCc() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public Object zGetBody() throws HarnessException {
		throw new HarnessException("implement me!");
	}

}
