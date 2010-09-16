package projects.admin.ui;

/**
 * The "Edit Zimlet" page has the same functionality as "Edit Admin Extension"
 * @author zimbra
 *
 */
public class PageEditZimlet extends PageEditAdminExtension {

	public PageEditZimlet(AbsApplication application) {
		super(application);
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}


}
