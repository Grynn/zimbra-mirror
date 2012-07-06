package com.zimbra.qa.selenium.framework.items;

public class UploadProgressItem extends AItem{

	public String uploadProgressBarLocator=null;
	public String uploadFileName=null;
	public String uploadFileSize=null;
	public String uploadProgresSize=null;
	public String uploadStop=null;
	public String uploadBarValue=null;


	public UploadProgressItem()
	{

	}

	public void setLocator(String locator)
	{
		uploadProgressBarLocator=locator;
	}
	public String getLocator()
	{
		return uploadProgressBarLocator;
	}
	public void setFileName(String filename)
	{
		this.uploadFileName=filename;
	}
	public String getFileName()
	{
		return this.uploadFileName;
	}
	public void setUploadFileSize(String fileSize)
	{
		int index = fileSize.indexOf("of");
		uploadFileSize=fileSize.substring(index+2, fileSize.length()).trim();
		uploadProgresSize=fileSize.substring(0, index).trim();
	}
	public String getUploadFileSize()
	{
		return this.uploadFileSize;
	}
	public void setUploadBarValue(String value)
	{
		uploadBarValue=value;
	}
	public String getUploadBarValue()
	{
		return this.uploadBarValue;
	}
	public String getUploadProgressSize()
	{
		return this.uploadProgresSize;
	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(UploadProgressItem.class.getSimpleName()).append('\n');
		sb.append("GUI Data:\n");
		sb.append("UploadFileName: ").append(getFileName()).append('\n');
		sb.append("UploadFileSize: ").append(getUploadFileSize()).append('\n');
		sb.append("UploadProgressBarValue: ").append(getUploadFileSize()).append('\n');
		return (sb.toString());
	}
}
