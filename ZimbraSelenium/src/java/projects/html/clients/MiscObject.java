package projects.html.clients;

/**
 * Special class that can be used to work with unique objects like: miniCal,
 * timeZoneMenu,CalendarGrid, Toolbar etc. The objects are identified by a
 * hierarchy(or nested) of classNames or ids.
 * 
 * NOTE:
 * 
 * You can pass: 
 * 
 * 1. class1 if the object doesnt need nested classes.
 * 
 * 2.if not, use nested. class1/id2/class3 or
 * class1/class2/class3 or id1/id2/id3 or any combination 
 * 
 * - It also supports wildcards to identify ids. e.g. we can
 * pass: *myId instead of DWT1_myId 
 * 
 * example:
 * calendar timezone menu.. 
 * str = obj.zMiscObj.zExistsDontWait(
 * "ZmApptComposeView ZWidget/*tzoneSelect/ZSelectAutoSizingContainer ZHasDropDown"
 * ); where: "ZmApptComposeView ZWidget" is a className 
 * *tzoneSelect is part of the id of an element within ZmApptComposeView ZWidget
 * and ZSelectAuto.. is the 3rd class, which we are interested in.
 * 
 * miniCalendar..
 * obj.zMiscObj.zExistsDontWait("DwtCalendar")

 */
public class MiscObject extends ZObject {
    public MiscObject() {
	// use this for views, or some unique objects like miniCalendar,
	// readingPane etc ojbects
	super("miscZObjectCore_html", "Generic Object");
    }




	
}