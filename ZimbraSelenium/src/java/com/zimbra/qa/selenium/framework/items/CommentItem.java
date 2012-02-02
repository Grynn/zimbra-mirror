package com.zimbra.qa.selenium.framework.items;

public class CommentItem extends AItem {

	private String TheLocator = null;
	private String CommentText = null;
	private String CommentEmail = null;
	private String CommentImage = null;
	private String CommentTime = null;
	private String ParentId = null;
	
	public CommentItem() {
		
	}
	
	/**
	 * @param theLocator the theLocator to set
	 */
	public void setLocator(String locator) {
		TheLocator = locator;
	}

	/**
	 * @return the theLocator
	 */
	public String getLocator() {
		return TheLocator;
	}

	public void setParentId(String parentId) {
		ParentId = parentId;
	}

	public String getParentId() {
		return ParentId;
	}

	public void setCommentEmail(String commentEmail) {
		CommentEmail = commentEmail;
	}

	public String getCommentEmail() {
		return CommentEmail;
	}

	public void setCommentImage(String commentImage) {
		CommentImage = commentImage;
	}

	public String getCommentImage() {
		return CommentImage;
	}

	public void setCommentTime(String commentTime) {
		CommentTime = commentTime;
	}

	public String getCommentTime() {
		return CommentTime;
	}

	public void setCommentText(String commentText) {
		CommentText = commentText;
	}

	public String getCommentText() {
		return CommentText;
	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(CommentItem.class.getSimpleName()).append('\n');
		sb.append("GUI Data:\n");
		sb.append("CommentText: ").append(getCommentText()).append('\n');
		sb.append("CommentEmail: ").append(getCommentEmail()).append('\n');
		sb.append("CommentTime: ").append(getCommentTime()).append('\n');
		sb.append("CommentImage: ").append(getCommentImage()).append('\n');
		return (sb.toString());
	}

}
