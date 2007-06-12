package com.zimbra.zme.client;

import com.zimbra.zme.ui.ConvItem;
import com.zimbra.zme.ui.MsgItem;
import com.zimbra.zme.ui.CollectionItem;

import de.enough.polish.ui.TreeItem;

public interface ItemFactory {
	public ConvItem createConvItem();
	public MsgItem createMsgItem();
	public TreeItem createFolderItem();
}
