package com.zimbra.zme.client;

public class Contact {
	public String mEmail;
	public boolean mSelected;
	//#if (${bytes(polish.HeapSize)} >= ${bytes(1MB)}) or (polish.HeapSize == dynamic)
		public String mFirstName;
		public String mLastName;
	//#endif
}
