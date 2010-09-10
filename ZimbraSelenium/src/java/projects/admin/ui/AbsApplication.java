package projects.admin.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class defines an abstract Zimbra "Application"
 * @author Matt Rhoades
 *
 */
public abstract class AbsApplication {
	protected static Logger logger = LogManager.getLogger(AbsPage.class);

	public AbsApplication() {
		logger.info("new " + AbsApplication.class.getCanonicalName());

	}
}
