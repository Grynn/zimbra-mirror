package projects.zcs.clients;

/**
 * Special class that can be used to work with unique objects like: miniCal,
 * timeZoneMenu,CalendarGrid, Toolbar etc. The objects are identified by a
 * hierarchy(or nested) of classNames or ids.
 * 
 * NOTE:
 * 
 * You can pass: 
 * 
 * 1. class1 if the object doesnt need nested classes. but it must
 * have z-index value 
 * 
 * 2.if not, use nested. class1/id2/class3 or
 * class1/class2/class3 or id1/id2/id3 or any combination as long as class1 or
 * id1 has z-index. 
 * 
 * - It also supports wildcards to identify ids. e.g. we can
 * pass: *myId instead of DWT1_myId 
 * 
 * example:
 * calendar timezone menu.. 
 * str = obj.zMiscObj.zExistsDontWait(
 * "ZmApptComposeView ZWidget/*tzoneSelect/ZSelectAutoSizingContainer ZHasDropDown"
 * ); where: "ZmApptComposeView ZWidget" is a className with z-index
 * *tzoneSelect is part of the id of an element within ZmApptComposeView ZWidget
 * and ZSelectAuto.. is the 3rd class, which we are interested in.
 * 
 * miniCalendar..
 * obj.zMiscObj.zExistsDontWait("DwtCalendar")
 * 
 * CalendarGrid..
 * obj.zMiscObj.zDblClickXY("ZmCalViewMgr/ImgCalendarDayGrid","50,50");
 */
public class MiscObject extends ZObject {
    public MiscObject() {
	// use this for views, or some unique objects like miniCalendar,
	// readingPane etc ojbects
	super("miscZObjectCore", "Generic Object");
    }

    /**
     * Clicks on an object at X,Y length away from *object's* top-left corner.
     * 
     * @param classNameOridWithZIndx
      * @param classNameOridWithZIndx
     *            : class1WithZindex or idWithZIndex or class1WithZIndex/class2/class3 etc
     * 
     * @param XCommaY
     *            "50,50"
     */
    public void zClickXY(String classNameOridWithZIndx, String XCommaY) {
	ZObjectCore(classNameOridWithZIndx, "click", "", "true", XCommaY, "");
    }

    /**
     * DblClicks on an object at X,Y length away from *object's* top-left
     * corner.
     * 
     * @param classNameOridWithZIndx
     * @param classNameOridWithZIndx
     *            : class1WithZindex or idWithZIndex or class1WithZIndex/class2/class3 etc
     * 
     * @param XCommaY
     *            "50,50"
     */
    public void zDblClickXY(String classNameOridWithZIndx, String XCommaY) {
	ZObjectCore(classNameOridWithZIndx, "dblclick", "", "true", XCommaY, "");
    }

    /**
     * RtClicks on an object at X,Y length away from *object's* top-left corner.
     * 
     * @param classNameOridWithZIndx
     *            : class1WithZindex or idWithZIndex or class1WithZIndex/class2/class3 etc
     * 
     * @param XCommaY
     *            "50,50"
     */
    public void zRtClickXY(String classNameOridWithZIndx, String XCommaY) {
	ZObjectCore(classNameOridWithZIndx, "rtclick", "", "true", XCommaY, "");
    }

	public void zType(String objNameOrId, String data) {
		if (data != "")
			ZObjectCore(objNameOrId, "type", data, "", "1", "");
	}
}