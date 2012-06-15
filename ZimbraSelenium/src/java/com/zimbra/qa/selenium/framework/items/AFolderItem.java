package com.zimbra.qa.selenium.framework.items;

import java.util.List;

public abstract class AFolderItem extends AItem {

	private String _ParentId = null;
	private String _Name = null;
	private List<AFolderItem> _Subfolders = null;
	
	public void setParentId(String id) {
		_ParentId = id;
	}

	public String getParentId() {
		return (_ParentId);
	}
	

	public void setName(String name) {
		_Name = name;
	}
	
	public String getName() {
		return (_Name);
	}

	public List<AFolderItem> getSubfolders() {
		return (_Subfolders);
	}
	
	public void setSubfolders(List<AFolderItem> subfolders) {
		_Subfolders = subfolders;
	}

	public String getView() {
		// TODO Auto-generated method stub
		return null;
	}


}
