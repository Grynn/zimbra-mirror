/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007 Zimbra, Inc.  All Rights Reserved.
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
 * @class
 * This Mailbox class provides a wrapper & API to ZClientMobile. 
 * 
 * Concurrency control is the responsibility of the clients using this class.
 * 
 * @author Ross Dargahi
 */

/**
 * 
 */

package com.zimbra.zme.client;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.ZmeException;
import com.zimbra.zme.ui.MailListView;
import com.zimbra.zme.ui.MailItem;
import com.zimbra.zme.ui.MsgItem;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class Mailbox implements Runnable {

	public static final Object AUTH = new Object();
	public static final Object CREATESEARCHFOLDER = new Object();
	public static final Object DELETEITEM = new Object();
	public static final Object GETAPPTSUMMARIES = new Object();
	public static final Object GETCONTACTS = new Object();
	public static final Object GETFOLDERS = new Object();
	public static final Object GETTAGS = new Object();
	public static final Object LOADMAILBOX = new Object();
	public static final Object GETMSG = new Object();
	public static final Object GETSEARCHFOLDERS = new Object();
	public static final Object FLAGITEM = new Object();
	public static final Object MARKITEMUNREAD = new Object();
	public static final Object SEARCHCONV = new Object();
	public static final Object SEARCHMAIL = new Object();
    public static final Object SEARCHMAILREST = new Object();
	public static final Object SENDMSG = new Object();
	public static final Object TAGITEM = new Object();
    public static final Object MOVEITEM = new Object();
    public static final Object CREATEAPPT = new Object();
    public static final Object MODIFYAPPT = new Object();
    public static final Object INVITEREPLY = new Object();
    public static final Object GETAPPT = new Object();
    public static final Object GETINFO = new Object();

    // No parameter for Item action
    //private static final String NOPARAM = "";
    
	private static final Object P1 = new Object();
	private static final Object P2 = new Object();
	
    // Null argument
    private static final Object NULL_ARG = new Object();

	public ZimbraME mMidlet;
    public String mServerUrl;
    public String mSetAuthCookieUrl;
    public String mRestUrl;
    public String mAuthToken;
    public String mSessionId;
    public Vector mSavedSearches;
    public Vector mTags;
    public Vector mContacts;
    public Folder mRootFolder;

    private Vector mQueue;
    private Hashtable mThreadClients;

    public Mailbox(int numSvcThreads) 
    		throws ZmeException {
		startSvcThreads(numSvcThreads);
    }

    /**
     * Lookup Folder by mailItem id.
     * 
     * @param id
     * @param f
     * @return folder
     */
    public Folder lookupFolder(String id, Folder f) {
        if (id != null && f != null) {
            if (f.mId.compareTo(id) == 0)
                return f;
            Enumeration e = f.mSubfolders.elements();
            while (e.hasMoreElements()) {
                Folder ret = lookupFolder(id, (Folder)e.nextElement());
                if (ret != null)
                    return ret;
            }
        }
        return null;
    }
    
    /**
     * Lookup Tag by mailItem id.
     * 
     * @param id
     * @return tag
     */
    public Tag lookupTag(String id) {
        Enumeration e = mTags.elements();
        while (e.hasMoreElements()) {
            Tag t = (Tag) e.nextElement();
            if (t.mId.compareTo(id) == 0)
                return t;
        }
        return null;
    }
    
    /**
     * Adds a contact to the contacts for this mailbox. Note this contact will be blown away
     * if contacts are reloaded and if it is not saved on the server
     * 
     * @param c The contact to add
     */
    public void addContact(Contact c) {
    	if (mContacts == null)
    		mContacts = new Vector();
    	//TODO add the contact in the right location
    	mContacts.addElement(c);
    }
    
    /**
     * Create a saved search
     * @param name
     * @param query
     * @param respHdlr
     */
    public void createSavedSearch(String name,
    							  String query,
    							  ResponseHdlr respHdlr) {
    	synchronized (mQueue) {
    		Stack s = new Stack();
    		s.push(query);
    		s.push(name);
    		s.push(mAuthToken);
    		s.push(CREATESEARCHFOLDER);
    		s.push(respHdlr);
    		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
    	}
    }
    
    /**
     * Marks an item read/unread
     * @param itemId
     * @param respHdlr
     */
    public void deleteItem(String itemId,
    					   ResponseHdlr respHdlr){
    	synchronized (mQueue) {
    		Stack s = new Stack();
     		s.push(itemId);
    		s.push(mAuthToken);
    		s.push(DELETEITEM);
    		s.push(respHdlr);
    		s.push(P2);
			mQueue.addElement(s);
			mQueue.notify();
		}
     }


    /**
     * Flags/Unflags a mail item
     * @param itemId
     * @param flag
     * @param respHdlr
     */
    public void flagItem(String itemId,
    					 boolean flag,
    					 ResponseHdlr respHdlr) {    	
    	synchronized (mQueue) {
    		Stack s = new Stack();
    		s.push(new Boolean(flag));
     		s.push(itemId);
    		s.push(mAuthToken);
    		s.push(FLAGITEM);
    		s.push(respHdlr);
    		s.push(P2);
    		mQueue.addElement(s);
    		mQueue.notify();
		}
     }
    
    public void getApptSummaries(Date start,
    							 Date end,
    							 ResultSet results,
    							 ResponseHdlr respHdlr){	
		synchronized (mQueue) {
			Stack s = new Stack();
			s.push(results);
			s.push(end);
			s.push(start);
			s.push(mAuthToken);
			s.push(GETAPPTSUMMARIES);
			s.push(respHdlr);
    		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
		}
	}

    
    public void getContacts(ResponseHdlr respHdlr) {
    	synchronized (mQueue) {
    		Stack s = new Stack();
    		s.push(mAuthToken);
    		s.push(GETCONTACTS);
    		s.push(respHdlr);
    		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
		}
    }
    
    /**
     * Gets the mailboxes tags
     * @param respHdlr
     */
    public void getFolders(ResponseHdlr respHdlr) {
    	synchronized (mQueue) {
    		Stack s = new Stack();
    		s.push(mAuthToken);
    		s.push(GETFOLDERS);
    		s.push(respHdlr);
    		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
		}    	
    }
    
   
    /**
     * Gets the mailboxes tags
     * @param respHdlr
     */
    public void getTags(ResponseHdlr respHdlr) {
    	synchronized (mQueue) {
    		Stack s = new Stack();
    		s.push(mAuthToken);
    		s.push(GETTAGS);
    		s.push(respHdlr);
    		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
		}    	
    }
    
   /**
     * Get mailbox info. This includes: Flags, tags
     * @param responseHdlr
     */
    public void loadMailbox(ResponseHdlr respHdlr) {
    	synchronized (mQueue) {
    		Stack s = new Stack();
       		s.push(mAuthToken);
    		s.push(LOADMAILBOX);
    		s.push(respHdlr);
       		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
		}
    }
    
    /**
     * Gets the list of saved searches
     * @param responseHdlr
     */
    public void getSavedSearches(ResponseHdlr respHdlr) { 
    	synchronized (mQueue) {
    		Stack s = new Stack();
    		s.push(mAuthToken);
    		s.push(GETSEARCHFOLDERS);
    		s.push(respHdlr);
    		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
		}
    }
    
    
    /**
     * Marks an item read/unread
     * @param itemId
     * @param flag
     * @param respHdlr
     */
    public void markItemUnread(String itemId,
    					 	   boolean unread,
    					 	   ResponseHdlr respHdlr){
    	synchronized (mQueue) {
    		Stack s = new Stack();
    		s.push(new Boolean(unread));
     		s.push(itemId);
    		s.push(mAuthToken);
    		s.push(MARKITEMUNREAD);
    		s.push(respHdlr);
    		s.push(P2);
			mQueue.addElement(s);
			mQueue.notify();
		}
     }

    
	/**
	 * Logs into a mailbox 
	 *  
	 * @param username
	 * @param passwd
	 * @param respHdlr The response handler will have its <i>handleResponse</i> method called
	 * when the operation is completed (either successfully or not)
	 */
    public void login(String username,
                      String passwd,
                      ResponseHdlr respHdlr) {
    	synchronized (mQueue) {
    		Stack s = new Stack();
    		s.push(passwd);
    		s.push(username);
    		s.push(AUTH);
    		s.push(respHdlr);
    		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
		}
     }


    public void loadMsg(MsgItem m) {
    	synchronized (mQueue) {
    		Stack s = new Stack();
    		s.push(m);
    		s.push(mAuthToken);
    		s.push(GETMSG);
    		s.push(m);
    		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
		}
    }
    
    public void searchMailRest(
               String user,
               String folder,
               MailItem lastItem,
               int numResults,
               MailListView container,
               ResultSet results,
               ResponseHdlr respHdlr) 
    {
        synchronized (mQueue) {
            Stack s = new Stack();
            s.push(results);
            if (lastItem == null)
                s.push(NULL_ARG);
            else
                s.push(lastItem);
            s.push(new Integer(numResults));
            s.push(folder);
            s.push(user);
            s.push(SEARCHMAILREST);
            s.push(respHdlr);
            s.push(P1);
            mQueue.addElement(s);
            mQueue.notify();
        }
    }
    
    public void searchMail(String query,
    					   boolean byConv,
    					   MailItem lastItem,
    					   int numResults,
    					   MailListView container,
    					   ResultSet results,
    					   ResponseHdlr respHdlr) {
    	synchronized (mQueue) {
    		Stack s = new Stack();
    		s.push(results);
    		if (lastItem == null)
    			s.push(NULL_ARG);
    		else
    			s.push(lastItem);
    		s.push(new Integer(numResults));
    		s.push(new Boolean(byConv));
    		s.push(query);
    		s.push(mAuthToken);
    		s.push(SEARCHMAIL);
    		s.push(respHdlr);
    		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
		}
    }
    
    public void searchConv(String convId,
						   boolean expandFirstUnreadMsg,
						   MailItem lastItem,
    					   int numResults,
    					   MailListView container,
    					   ResultSet results,
    					   ResponseHdlr respHdlr) {
    	synchronized (mQueue) {
    		Stack s = new Stack();
			s.push(results);
    		if (lastItem == null)
    			s.push(NULL_ARG);
    		else
    			s.push(lastItem);
    		s.push(new Integer(numResults));
    		s.push(new Boolean(expandFirstUnreadMsg));
    		s.push(convId);
    		s.push(mAuthToken);
    		s.push(SEARCHCONV);
    		s.push(respHdlr);
    		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
		}    	
    }
    
    public void sendMsg(Vector toAddrs,
						Vector ccAddrs,
						Vector bccAddrs,
						String subject,
						String body,
						String originalId,
						boolean isForward,
						ResponseHdlr respHdlr){

		//#debug
    	System.out.println("Mailbox.sendMsg");
    	
     	synchronized (mQueue) {
			Stack s = new Stack();
			s.push(new Boolean(isForward));
			s.push((originalId != null) ? originalId : NULL_ARG);
			s.push((body != null) ? body : NULL_ARG);
			s.push((subject != null) ? subject : NULL_ARG);
			s.push((bccAddrs != null) ? bccAddrs : NULL_ARG);
			s.push((ccAddrs != null) ? ccAddrs : NULL_ARG);
			s.push((toAddrs != null) ? toAddrs : NULL_ARG);
			s.push(mAuthToken);
			s.push(SENDMSG);
			s.push(respHdlr);
    		s.push(P1);
			mQueue.addElement(s);
			mQueue.notify();
     	}
    }
	
    public void sendInviteReply(String itemId, String compNum, String exceptionDate, String action, ResponseHdlr respHdlr) {
        Stack s = new Stack();
        s.push(action);
        s.push((exceptionDate == null) ? NULL_ARG : exceptionDate);
        s.push((compNum == null) ? "0" : compNum);
        s.push(itemId);
        s.push(mAuthToken);
        s.push(INVITEREPLY);
        s.push(respHdlr);
        s.push(P1);
        synchronized (mQueue) {
            mQueue.addElement(s);
            mQueue.notify();
        }
    }

    
    public void tagItem(String itemId,
    					String[] tagIds,
    					ResponseHdlr respHdlr){
    	synchronized (mQueue) {
    		Stack s = new Stack();
    		s.push(tagIds);
     		s.push(itemId);
    		s.push(mAuthToken);
    		s.push(TAGITEM);
    		s.push(respHdlr);
    		s.push(P2);
			mQueue.addElement(s);
			mQueue.notify();
		}
     }

    public void moveItem(String itemId,
            String destFolderId,
            ResponseHdlr respHdlr) {
        synchronized (mQueue) {
            Stack s = new Stack();
            s.push(destFolderId);
            s.push(itemId);
            s.push(mAuthToken);
            s.push(MOVEITEM);
            s.push(respHdlr);
            s.push(P2);
            mQueue.addElement(s);
            mQueue.notify();
        }
    }

    public void createAppt(Appointment appt, ResponseHdlr respHdlr) {
        Stack s = new Stack();
        s.push(appt);
        s.push(mAuthToken);
        s.push(CREATEAPPT);
        s.push(respHdlr);
        s.push(P1);
        synchronized (mQueue) {
            mQueue.addElement(s);
            mQueue.notify();
        }
    }
    
    public void modifyAppt(Appointment appt, ResponseHdlr respHdlr) {
        Stack s = new Stack();
        s.push(appt);
        s.push(mAuthToken);
        s.push(MODIFYAPPT);
        s.push(respHdlr);
        s.push(P1);
        synchronized (mQueue) {
            mQueue.addElement(s);
            mQueue.notify();
        }
    }
    
    public void getAppt(Appointment appt, ResponseHdlr respHdlr) {
        Stack s = new Stack();
        s.push(appt);
        s.push(mAuthToken);
        s.push(GETAPPT);
        s.push(respHdlr);
        s.push(P1);
        synchronized (mQueue) {
            mQueue.addElement(s);
            mQueue.notify();
        }
    }
    
    public void getInfo(ResponseHdlr respHdlr) {
        Stack s = new Stack();
        s.push(mAuthToken);
        s.push(GETINFO);
        s.push(respHdlr);
        s.push(P1);
        synchronized (mQueue) {
            mQueue.addElement(s);
            mQueue.notify();
        }
    }
    
    /**
     * Cancels any outstanding operation
     * @throws IOException
     */
    public void cancelOp() {   	
    	closeConnection();
    }


    /**
     * Handles interfacing with the various commands (in a separate thread). The public mailbox
     * apis (e.g. <i>login</i>) push the appropriate parameters onto a stack object (<i>mStack</i>)
     * and then notify the worker thread which then delegates the operation to the righ command api
     * by examining the parameters on the stack
     */
    public void run() {
    	ResponseHdlr hdlr;
    	Object op;
    	Object respObj;
		Stack s;
		boolean shutdown = false;
		String threadName = Thread.currentThread().toString();
		ZClientMobile client = null;;

		//#debug
		System.out.println("Mailbox.run: starting thread: " + threadName);
		
    	try {
			client = new ZClientMobile(this);
		} catch (ZmeException e1) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): Couldn't construct ZClientMobile object. Exiting");
			return;
		}
    	
		mThreadClients.put(threadName, client);
    	while(true) {
    		hdlr = null;
    		s = null;
    		synchronized(mQueue) {
    			try {
    				if (mQueue.size() == 0) {
    					if (!shutdown) {
		    		    	//#debug
		    		    	System.out.println("Mailbox.run(" + threadName + "): about to wait");
							mQueue.wait();
					    	//#debug
					    	System.out.println("Mailbox.run(" + threadName + "): woken up");
    					} else {
    						//#debug
    						System.out.println("Mailbox.rum(" + threadName + "): exiting run method");
    						return;
    					}
    				}
				} catch (InterruptedException e) {
					//Terminate
					//#debug
					System.out.println("Mailbox.run: terminating thread " + threadName);
					shutdown = true;
				} finally {
			    	//#debug
			    	System.out.println("Mailbox.run(" + threadName + "): processing request");
			    	int sz = mQueue.size();
			    	int i;
                    if (sz == 0)
                        continue;
			    	for (i = sz - 1; i >= 0; i--) {
			    		if (((Stack)mQueue.elementAt(i)).peek() == P1)
			    			break;
			    	}
			    	if (i < 0)
			    		i = sz - 1;
			    	
			    	//#debug
			    	System.out.println("Servicing queue element: " + i + " of " + sz);
			    	
			    	s = (Stack)mQueue.elementAt(i);
			    	mQueue.removeElementAt(i);
				}
    		}
    		// Pop off the priority
    		s.pop();
    		hdlr = (ResponseHdlr)s.pop();
    		op = s.pop();
    		
    		/*
    		try {
    			if (op == FLAGITEM)
    				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
    		
            try {
	    		handleOp(op, s, client, threadName);
	    		respObj = this;
	    	} catch (Exception ex) {
	    		respObj = ex;
                /*
                ex.printStackTrace();
                */
	    	} catch (Error e) {
	    		respObj = null;
	    		//#debug
	    		System.out.println("ERROR: " + e);
	    		Thread t = new Thread(this);
	    		t.start();
	    	}

	    	try {
	    	    if (respObj != null) {
	    	        closeConnection();    			
	    	        //#debug
	    	        System.out.println("Mailbox.run(" + threadName + "): calling response handler");
	    	        hdlr.handleResponse(op, respObj);
	    	    }
	    	} catch (Exception ex) {
                //#debug
                System.out.println("Error handling response: " + ex);
	    	}
    	}
    }

    private void handleOp(Object op,
    					  Stack s,
    					  ZClientMobile client,
    					  String threadName) 
    		throws IOException, 
    			   ZmeException, 
    			   ZmeSvcException {
    	if (op == AUTH) {
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): Login");
    		client.beginRequest(null, false);
    		client.login((String)s.pop(), (String)s.pop());
    		client.endRequest();
    		//client.setAuthCookie(mAuthToken);
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): Login done");
    	} else if (op == CREATESEARCHFOLDER) {
    		//#debug
			System.out.println("Mailbox.run(" + threadName + "): CreateSearchFolder");
			client.beginRequest((String)s.pop(), false);
			client.createSearchFolder((String)s.pop(), (String)s.pop());
			client.endRequest();
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): CreateSearchFolder done");
    	} else if (op == DELETEITEM) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): DeleteItem");
			client.beginRequest((String)s.pop(), false);
			client.doItemAction((String)s.pop(), "move", "l", ZClientMobile.ID_FOLDER_TRASH);
			client.endRequest();
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): Delete done");
	    } else if (op == FLAGITEM) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): FlagItem");
			client.beginRequest((String)s.pop(), false);
			client.doItemAction((String)s.pop(), 
								 (((Boolean)s.pop()).booleanValue()) ? "flag": "!flag", 
								 null, null);
			client.endRequest();
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): FlagItem done");
		} else if (op == GETAPPTSUMMARIES) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetApptSummaries");
			client.beginRequest((String)s.pop(), false);
			client.getApptSummaries((Date)s.pop(), // start
									 (Date)s.pop(), // end
									 (ResultSet)s.pop()); // resultSet
			client.endRequest();
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetApptSummaries done");			    			
		} else if (op == GETCONTACTS) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetContacts");
			client.beginRequest((String)s.pop(), false);
			client.getContacts();
			client.endRequest();
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetContacts done");			    			
		} else if (op == GETFOLDERS) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetFolders");
			client.beginRequest((String)s.pop(), false);
			client.getFolders();
			client.endRequest();
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetFolders done");				
		} else if (op == GETMSG) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetMsg");
			client.beginRequest((String)s.pop(), false);
			client.getMsg((MsgItem)s.pop());
			client.endRequest();
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetMsg done");
		} else if (op == GETSEARCHFOLDERS) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetSearchFolders");
			client.beginRequest((String)s.pop(), false);
			client.getSearchFolders();
			client.endRequest();
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetSearchFolders done");	
		} else if (op == GETTAGS) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetTags");
			client.beginRequest((String)s.pop(), false);
			client.getTags();
			client.endRequest();
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): GetTags done");				
		} else if (op == LOADMAILBOX) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): LoadMailbox");
			client.beginRequest((String)s.pop(), true);
			client.getInfo();
			client.getFolders();
			client.getTags();
			client.endRequest();
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): LoadMailbox done");			    			
		} else if (op == MARKITEMUNREAD) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): MarkItemUnread");
			client.beginRequest((String)s.pop(), false);
			client.doItemAction((String)s.pop(), 
								   (((Boolean)s.pop()).booleanValue()) ? "!read": "read", null, null);
			client.endRequest();
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): MarkItemUnread done");
		} else if (op == SEARCHCONV) {
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): SearchConv");
			client.beginRequest((String)s.pop(), false);
			String convId = (String)s.pop();
			boolean expandFirstUnreadMsg = ((Boolean)s.pop()).booleanValue();
			int numResults = ((Integer)s.pop()).intValue();
	        Object o = (Object)s.pop();
	        MailItem lastItem = (o == NULL_ARG) ? null : (MailItem)o;
			client.searchConv(convId, expandFirstUnreadMsg, numResults, lastItem, (ResultSet)s.pop());
			client.endRequest();
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): searchConv done");
			
		} else if (op == SEARCHMAIL) {
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): Search");
			client.beginRequest((String)s.pop(), false);
	        String query = (String)s.pop();
	        boolean byConv = ((Boolean)s.pop()).booleanValue();
	        int numResults = ((Integer)s.pop()).intValue();
	        Object o = (Object)s.pop();
 			MailItem lastItem = (o == NULL_ARG) ? null : (MailItem)o;
		    client.search(query, byConv, numResults, lastItem, (ResultSet)s.pop());
			client.endRequest();
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): Search done");
        } else if (op == SEARCHMAILREST) {
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): REST Search");
            String user = (String)s.pop();
            String folder = (String)s.pop();
            int numResults = ((Integer)s.pop()).intValue();
            Object o = (Object)s.pop();
            MailItem lastItem = (o == NULL_ARG) ? null : (MailItem)o;
            client.searchFolderRest(user, folder, numResults, lastItem, (ResultSet)s.pop());
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): Search done");
		} else if (op == SENDMSG) {
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): SendMsg");
			client.beginRequest((String)s.pop(), false);
			Object o;			
			client.sendMsg(((o = s.pop()) != NULL_ARG) ? (Vector)o : null, // toAddrs
	    					((o = s.pop()) != NULL_ARG) ? (Vector)o : null, // ccAddrs 
	    					((o = s.pop()) != NULL_ARG) ? (Vector)o : null, // bccAddrs 
	    					((o = s.pop()) != NULL_ARG) ? (String)o : null, // subject
	    					((o = s.pop()) != NULL_ARG) ? (String)o : null, // body 
	    					((o = s.pop()) != NULL_ARG) ? (String)o : null, // originalId
	    					((Boolean)s.pop()).booleanValue()); //isForward
			client.endRequest();
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): SendMsg done");	
		} else if (op == TAGITEM) {
			//#debug
			System.out.println("Mailbox.run(" + threadName + "): TagItem");
			tagItem(s, client);
	    	//#debug
	    	System.out.println("Mailbox.run(" + threadName + "): TagItem done");
        } else if (op == MOVEITEM) {
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): MoveItem");
            client.beginRequest((String)s.pop(), false);
            client.doItemAction((String)s.pop(), "move", "l", (String)s.pop());
            client.endRequest();
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): Move done");
        } else if (op == CREATEAPPT) {
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): CreateAppt");
            client.beginRequest((String)s.pop(), false);
            client.createAppt((Appointment)s.pop());
            client.endRequest();
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): CreateAppt done");
        } else if (op == MODIFYAPPT) {
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): ModifyAppt");
            client.beginRequest((String)s.pop(), false);
            client.modifyAppt((Appointment)s.pop());
            client.endRequest();
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): ModifyAppt done");
        } else if (op == INVITEREPLY) {
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): InviteReply");
            client.beginRequest((String)s.pop(), false);
            Object o;
            client.sendInviteReply((String)s.pop(), 
                                   (String)s.pop(), 
                                   ((o = s.pop()) != NULL_ARG) ? (String)o : null,
                                   (String)s.pop());
            client.endRequest();
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): InviteReply done");
        } else if (op == GETAPPT) {
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): GetAppt");
            client.beginRequest((String)s.pop(), false);
            client.getAppt((Appointment)s.pop());
            client.endRequest(true);
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): GetAppt done");
        } else if (op == GETINFO) {
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): GetInfo");
            client.beginRequest((String)s.pop(), false);
            client.getInfo();
            client.endRequest();
            //#debug
            System.out.println("Mailbox.run(" + threadName + "): GetInfo done");
		}
    }
    
    private void tagItem(Stack s,
    					 ZClientMobile client) 
    		throws IOException, 
    			   ZmeException, 
    			   ZmeSvcException {
    	
		String authToken = (String)s.pop();
		String id = (String)s.pop();
		String[] tagIds = (String[])s.pop();
		
		StringBuffer tagIdStr = null;
		
		for (int i = 0; i < tagIds.length; i++) {
			if (tagIdStr == null)
				tagIdStr = new StringBuffer();
			else
				tagIdStr.append(',');
			tagIdStr.append(tagIds[i]);
		}
		
		client.beginRequest(authToken, false);
		client.doItemAction(id, "update", "t", tagIdStr.toString());
		
		client.endRequest();
    }
    
    /**
     * Starts the mailbox objects worker thread. This thread is used for network
     * communications.
     */
    private void startSvcThreads(int numWorkers) {
    	mQueue = new Vector();
    	mThreadClients = new Hashtable();
    	
    	for (int i = 0; i < numWorkers; i++) {
    		Thread t = new Thread(this);
    		t.start();
    	}
    }
           
    private synchronized void closeConnection() {
    	ZClientMobile c = (ZClientMobile)mThreadClients.get(Thread.currentThread().toString());
    	if (c != null)
    		c.cancel();
    }
    
}
