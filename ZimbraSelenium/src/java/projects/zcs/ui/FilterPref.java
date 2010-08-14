package projects.zcs.ui;


/**
 *This Class contains all the UI-level objects and methods for Preferences>Mail
 * Filters
 * 
 * @author Raja
 * 
 */
@SuppressWarnings("static-access")
public class FilterPref extends AppPage {

	/**
	 * Returns localised value of "all" or "any" menuItems in MailFilter
	 * dialog's "If [any|all]  of the following conditions are met:" menu.
	 * 
	 * @param allOrany
	 *            pass "all" or "any"
	 * @return
	 */
	public static String zGetLocalizedAllOrAny(String allOrany) {
		String str = localize(locator.filterCondition);
		String[] temp = str.split("#");
		String any = temp[1].split("\\|")[0];
		String all = temp[2].split("}")[0];
		if (allOrany.equals("any")) {
			return any;
		} else
			return all;
	}

}
