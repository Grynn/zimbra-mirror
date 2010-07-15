package projects.html.clients;

/**
 * @author raodv
 *
 */


public class DocumentPage extends ZObject {
	public DocumentPage() {
		super("documentCore", "Document TOC");
	} 
	
	/* Clicks on a document  page in Documents's Table Of Contents
	 * @pageName pageName
	 */	
	public void zClick(String pageName) {
		ZObjectCore(pageName, "click");
	}

	/* Clicks on a link(Edit, History, Delete etc links) for a given page in Documents's Table Of Contents
	 * @pageName pageName
	 * @linkNameInPage link(Edit, History, Delete etc links)
	 */
	public void zClick(String pageName, String linkNameInPage) {
		ZObjectCore(pageName, "click", true, linkNameInPage);
	}
	
}	