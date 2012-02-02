package com.zimbra.qa.selenium.projects.octopus.ui;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDisplay;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.items.CommentItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DisplayFileComments extends AbsDisplay {

	public static class Locators {
		public static final Locators zFileCommentsView = new Locators(
				"css=div[id=comments-stream-view]");
		public static final Locators zFileCommentsViewCloseBtn = new Locators(
				"css=div[id=comments-stream-view] img[class='icon Cancel']");

		public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public DisplayFileComments(AbsApplication application) {
		super(application);
		logger.info("new " + DisplayFileComments.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zPressButton(" + button + ")");

		tracer.trace("Click button " + button);

		if (button == null)
			throw new HarnessException("button cannot be null");

		// Default behavior variables
		String locator = null;
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		if (button == Button.B_CLOSE) {
			locator = Locators.zFileCommentsViewCloseBtn.locator;
			page = ((AppOctopusClient) MyApplication).zPageOctopus;
		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Button is not present: " + locator);

		// Default behavior, process the locator by clicking on it

		// Click it
		zClickAt(locator, "0,0");

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		if (page != null)
			page.zWaitForActive();

		return (page);
	}

	public void zDeleteComment(CommentItem comment) throws HarnessException {
		
		if ( comment == null ) 
			throw new HarnessException("The comment cannot be null");
		
		if ( comment.getLocator() == null )
			throw new HarnessException("The comment's locator is unset/null");
		

		String locator = comment.getLocator() + " span[class='comment-delete']";
		this.zClickAt(locator, "0,0");
		
		this.zWaitForBusyOverlayOctopus();
		
		
	}
	
	/**
	 * <div tabindex="-1" id="sc2553" class="comments-list-item" style=""><div class="comment-container"><div class="comment-header"><div class="profile-img-container"><img src="/profile/vmwen1@zqa-394.eng.vmware.com/image?v=0" data-handlebars-id="2555" class="profile-image" width="32" height="32"></div><div class="comment-creator"><span tabindex="-1" id="sc2558" class="" style=""></span>
                <span tabindex="-1" id="sc2561" class="" style="">
                <span class="comment-delete">x</span>
                </span>
            </div><div class="comment-time">
                <span tabindex="-1" id="sc2563" class="" style="">1 hour ago</span>
            </div></div><div class="comment-body">more comments</div></div></div><div tabindex="-1" id="sc2567" class="comments-list-item" style=""><div class="comment-container"><div class="comment-header"><div class="profile-img-container"><img src="/profile/vmwen2@zqa-394.eng.vmware.com/image?v=0" data-handlebars-id="2569" class="profile-image" width="32" height="32"></div><div class="comment-creator"><span tabindex="-1" id="sc2572" class="" style=""></span>
                <span tabindex="-1" id="sc2575" class="" style=""></span>
            </div><div class="comment-time">
                <span tabindex="-1" id="sc2577" class="" style="">1 hour ago</span>
            </div></div><div class="comment-body">comments from other</div></div></div>
	 */
	public List<CommentItem> zGetCommentsList() throws HarnessException {
		
		List<CommentItem> items = new ArrayList<CommentItem>();

		// Is this necessary?
		this.zWaitForBusyOverlayOctopus();
		
		String listLocator = "css=div[id='comments-list-view'] div[class='comments-list-item']";
		int count = this.sGetCssCount(listLocator);
		for (int i = 1; i <= count; i++) {
			
			String locator;
			String itemLocator = listLocator + ":nth-of-type("+ i +") ";
			
			CommentItem item = new CommentItem();

			// Set the locator to the item
			item.setLocator(itemLocator);
			
			
			// Get the Image
			// TODO
			
			// Get the Email
			locator = itemLocator + " div[class='comment-creator'] span";
			item.setCommentEmail(this.sGetText(locator));
			
			// Get the Time
			locator = itemLocator + " div[class='comment-time'] span";
			item.setCommentTime(this.sGetText(locator));
			
			// Get the Comment Text
			locator = itemLocator + " div[class='comment-body']";
			item.setCommentText(this.sGetText(locator));
			
			logger.info(item.prettyPrint());
			
			items.add(item);
		}
		
		return (items);
	}
	
	
	@Override
	public boolean zIsActive() throws HarnessException {

		if (!this.sIsElementPresent(Locators.zFileCommentsView.locator))
			return (false);

		if (!this.zIsVisiblePerPosition(Locators.zFileCommentsView.locator, 0,
				0))
			return (false);

		return (true);
	}

}
