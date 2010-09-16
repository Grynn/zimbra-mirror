package projects.admin.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Item {
	protected static Logger logger = LogManager.getLogger(Item.class);

	public Item() {
		logger.info("New " + this.getClass().getName());
	}
}
