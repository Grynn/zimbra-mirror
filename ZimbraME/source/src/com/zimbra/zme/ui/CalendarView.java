package com.zimbra.zme.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.Util;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.ZmeListener;
import com.zimbra.zme.client.Appointment;
import com.zimbra.zme.client.Mailbox;
import com.zimbra.zme.client.ResultSet;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

public class CalendarView extends View implements ResponseHdlr, ZmeListener {

	private static final Command REFRESH = new Command(Locale.get("main.Refresh"), Command.BACK, 1);
	private static final Command ACTIONS = new Command(Locale.get("main.Actions"), Command.ITEM, 1);
	private static final Command GOTO_NEXTDAY = new Command(Locale.get("calendar.NextDay"), Command.ITEM, 1);
	private static final Command GOTO_PREVDAY = new Command(Locale.get("calendar.PrevDay"), Command.ITEM, 1);
	private static final Command GOTO_TODAY = new Command(Locale.get("calendar.Today"), Command.ITEM, 1);
	private static final Command DELETE = new Command(Locale.get("main.Delete"), Command.ITEM, 1);
	private static final Command ACCEPT = new Command(Locale.get("calendar.Accept"), Command.ITEM, 1);
	private static final Command DECLINE = new Command(Locale.get("calendar.Decline"), Command.ITEM, 1);
	private static final Command TENTATIVE = new Command(Locale.get("calendar.Tentative"), Command.ITEM, 1);
    private static final Command NEW = new Command(Locale.get("calendar.New"), Command.ITEM, 1);

	protected boolean mFragmentShowing;
	
	private Hashtable mApptSummaries;
	private ResultSet mResults;
	private StringItem mHeader;
	private ZmeStringItem mNoResultsItem;
	private Calendar mCal;
	private Date mCurrDate;
	private Command mActionInProgressCmd;
	private Command mShowFragmentCmd;

	//#ifdef polish.usePolishGui
		public CalendarView(ZimbraME midlet,
							Style style) {
			super(midlet);
			//#if true
				//# mView = new FramedForm(null, style);
			//#endif
			init();
		}

	//#else
		public CalendarView(ZimbraME midlet) {
			super(midlet);
			//#if true
				//# mView = new FramedForm(null);
			//#endif
			init();
		}
	//#endif

	/**
	 * Will load the calendar with today's date. Will use cached result set if it exists.
	 */
	public void load() {
		loadAppts(null, false);
	}
	
	public void loadAppts4Date(int year,
						  	   int month,
						  	   int day,
						  	   boolean reload) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		loadAppts(c, reload);
	}
	
	private void loadAppts(Calendar c,
						   boolean reload) {
		/* if c is null, then goto today. If c != mCal, then go to the day specified in c. If c == mCal
		 * then we are being called from one of the goto{Next, Prev}Day methods which set the
		 * value of mCal*/
		if (c == null)
			mCal.setTime(new Date());
		else if (c != mCal)
			mCal.setTime(c.getTime());
		
		setHeader(mCal);
		
		mCal.set(Calendar.HOUR_OF_DAY, 0);
		mCal.set(Calendar.MINUTE, 0);
		mCal.set(Calendar.SECOND, 0);
		mCal.set(Calendar.MILLISECOND, 0);
		mCurrDate = mCal.getTime();
		
		mCal.set(Calendar.HOUR_OF_DAY, 23);
		mCal.set(Calendar.MINUTE, 59);
		mCal.set(Calendar.SECOND, 59);
		mCal.set(Calendar.MILLISECOND, 999);
		
		/* Get any cached results for the date in question. If no cache hit, then create a cache entry. 
		 * Note this strategy assumes that a given CalendarView can only have a single request outstanding
		 * which is fine. Might want to enforce this*/
		// TODO Enforce single outstanding request for CalendarView?
		String key = Long.toString(mCurrDate.getTime());
		mResults = (ResultSet)mApptSummaries.get(key);
		if (mResults == null || reload) {
			mResults = new ResultSet();
			mApptSummaries.put(key, mResults);
			Dialogs.popupWipDialog(mMidlet, this, Locale.get("calendar.LoadingAppts"));
			mMidlet.mMbox.getApptSummaries(mCurrDate, mCal.getTime(), mResults, this);
		} else {
			renderResults();
		}
	}
	
	private void setHeader(Calendar cal) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(Util.DAY_OF_WEEK[cal.get(Calendar.DAY_OF_WEEK) - 1]).append(" ");
		
		for (int i = 0; i < Util.DATE_FMT.length; i++) {
			switch (Util.DATE_FMT[i]) {
				case 'm':
					sb.append(cal.get(Calendar.MONTH) + 1);
					break;
				case 'd':
					sb.append(cal.get(Calendar.DAY_OF_MONTH));
					break;
				case 'y':
					sb.append(Integer.toString(cal.get(Calendar.YEAR)));
					break;
			}
			if (i < Util.DATE_FMT.length - 1)
				sb.append(Util.DATE_SEP);
		}
		mHeader.setText(sb.toString());
	}

	public void commandAction(Command cmd, 
							  Displayable d) {
		if (d == mView) {
			if (cmd == GOTO_NEXTDAY) {
				gotoNextDay();
			} else if (cmd == GOTO_PREVDAY) {
				gotoPrevDay();
			} else if (cmd == GOTO_TODAY) {
				gotoToday();
			} else if (cmd == REFRESH) {
				loadAppts(mCal, true);
			} else if (cmd == DELETE || cmd == TENTATIVE || cmd == DECLINE || cmd == ACCEPT) {
				CalendarItem c = null;
				//#if true
					//# c = (CalendarItem)(((FramedForm)mView).getCurrentItem());
				//#endif
				if (c.mAppt.mRecurring && !c.mAppt.mIsException) {
					mActionInProgressCmd = cmd;
					Dialogs.popupInstOrSeriesDialog(mMidlet, this);
				} else {
					if (cmd == DELETE)
						deleteAppt(c, false);
					else 
						setMyStatus(c, cmd, false);
				}
            } else if (cmd == NEW) {
                mMidlet.gotoNewApptView(mView);
			} else {
				mMidlet.commandAction(cmd, mView);
			}
		} else if (d == Dialogs.mInstOrSeriesD) {
			if (cmd == Dialogs.OK) {
				boolean series = !Dialogs.instSelected();
				CalendarItem c = null;
				//#if true
					//# c = (CalendarItem)(((FramedForm)mView).getCurrentItem());
				//#endif
				if (mActionInProgressCmd == DELETE)
					deleteAppt(c, series);
				else
					setMyStatus(c, mActionInProgressCmd, series);		
			}
			mMidlet.mDisplay.setCurrent(mView);
		} else if (d == Dialogs.mWipD) {
			mMidlet.mMbox.cancelOp();
			mMidlet.mDisplay.setCurrent(mView);
		} else {
			mMidlet.commandAction(cmd, d);
		}
	}
	
	protected void keyPressed(int keyCode,
			   			   	  int gameAction,
			   			   	  Item item) {
		if (gameAction == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6)
			gotoNextDay();
		else if (gameAction == Canvas.LEFT && keyCode != Canvas.KEY_NUM4)
			gotoPrevDay();
		else
			mMidlet.keyPressed(keyCode, mView);
	}
	
	public void handleResponse(Object op, 
							   Object resp) {
		//#debug
		System.out.println("CalendarView.handleResponse");
		
		if (resp instanceof Mailbox) {
			//#debug 
			System.out.println("CalendarView.handleResponse: Get appts successful");
			renderResults();
		} else {
			mMidlet.handleResponseError(resp, this);
		}
	}

	public void action(Object source, 
					   Object data) {
		if (source == mMidlet.mSettings) {
			showTicker(mMidlet.mSettings.getShowApptTicker());
			if (mShowTicker) {
				Item item = null;
				//#if true
					//# item = mView.getCurrentItem();
				//#endif
				if (item != null) {
					if (item instanceof CalendarItem) {
						CalendarItem c = (CalendarItem)item;
						mTicker.setString((c.mAppt.mFragment != null) ? c.mAppt.mFragment : "");
					}
				} else { //Empty list
					mTicker.setString("");
				}
			}
		}	
		
	}
	
	// Called by CalendarItem when it get's focus
	protected void itemHasFocus(CalendarItem item) {
		Appointment a = item.mAppt;
		if (mShowTicker)
			mTicker.setString((a.mFragment != null) ? a.mFragment : "");
		
		if (a.mAmIOrganizer || a.mApptStatus == Appointment.EVT_CANCELLED) {
			//#style DisabledMenuItem
			UiAccess.setAccessible(mView, TENTATIVE, false);
			//#style DisabledMenuItem
			UiAccess.setAccessible(mView, ACCEPT, false);
			//#style DisabledMenuItem
			UiAccess.setAccessible(mView, DECLINE, false);
		} else {
			switch (a.mMyStatus) {
				case Appointment.NEEDS_ACTION: 
					//#style MenuItem
					UiAccess.setAccessible(mView, TENTATIVE, true);
					//#style MenuItem
					UiAccess.setAccessible(mView, ACCEPT, false);
					//#style MenuItem
					UiAccess.setAccessible(mView, DECLINE, true);
					break;					
				case Appointment.ACCEPTED:
					//#style MenuItem
					UiAccess.setAccessible(mView, TENTATIVE, true);
					//#style DisabledMenuItem
					UiAccess.setAccessible(mView, ACCEPT, false);
					//#style MenuItem
					UiAccess.setAccessible(mView, DECLINE, true);
					break;
				case Appointment.TENTATIVE:
					//#style DisabledMenuItem
					UiAccess.setAccessible(mView, TENTATIVE, false);
					//#style MenuItem
					UiAccess.setAccessible(mView, ACCEPT, true);
					//#style MenuItem
					UiAccess.setAccessible(mView, DECLINE, true);
					break;
				case Appointment.DECLINED:
					//#style MenuItem
					UiAccess.setAccessible(mView, TENTATIVE, true);
					//#style MenuItem
					UiAccess.setAccessible(mView, ACCEPT, true);
					//#style DisabledMenuItem
					UiAccess.setAccessible(mView, DECLINE, false);
					break;
			}
		}
		
	}

	private void renderResults() {
		//#debug
		System.out.println("CalendarView.renderResults: Rendering results");
		
		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		
		//Clear out the current list if it is a new set of data
		f.deleteAll();

		Vector results = mResults.mResults;
		Appointment a;
		CalendarItem ci;
		if (results.size() > 0) {
			//#debug
			System.out.println("CalendarView.renderResults: Have results. Size: " + results.size());		
			for (Enumeration e = results.elements() ; e.hasMoreElements() ;) {
				a = (Appointment)e.nextElement();
				//#style CalendarItem
				ci = new CalendarItem(mMidlet, a, this);
				f.append(ci);
			}
			//#style DisabledMenuItem
			UiAccess.setAccessible(f, ACTIONS, true);
		} else {
			//#debug
			System.out.println("CalendarView.renderResults: No Results");
			f.append(mNoResultsItem);
			if (f.getTicker() != null)
				f.getTicker().setString("");
			//#style DisabledMenuItem
			UiAccess.setAccessible(f, ACTIONS, false);
		}
		mMidlet.mDisplay.setCurrent(mView);	
	}

	private void gotoNextDay() {
		mCurrDate.setTime(mCurrDate.getTime() + Util.MSEC_PER_DAY);
		mCal.setTime(mCurrDate);
		loadAppts(mCal, false);
	}
	
	private void gotoPrevDay() {
		mCurrDate.setTime(mCurrDate.getTime() - Util.MSEC_PER_DAY);		
		mCal.setTime(mCurrDate);
		loadAppts(mCal, false);
	}
	
	private void gotoToday() {
		load();
	}
	
	private void deleteAppt(CalendarItem c,
							boolean series) {
		//#debug
		System.out.println("Deleting: " + series);	
		//TODO IF SERIES MAKE SURE TO DELETE ALL INSTANCES IN CACHED VIEWS
	}

	private void setMyStatus(CalendarItem c,
            				 Command newStatus,
            				 boolean series) {
		int newStatusVal;
		if (newStatus == ACCEPT)
			newStatusVal = Appointment.ACCEPTED;
		else if (newStatus == DECLINE)
			newStatusVal = Appointment.DECLINED;
		else
			newStatusVal = Appointment.TENTATIVE;
		
		//#debug
		System.out.println("Setting Status: " + newStatusVal + " - " + series);	
		//IF SERIES MAKE SURE TO UPDATE ALL INSTANCES IN CACHED VIEWS
	}

	private void init() {
		mApptSummaries = new Hashtable();
		mCal = Calendar.getInstance();
		mMidlet.mSettings.addListener(this);
		
		//#style NoResultItem
		mNoResultsItem = new ZmeStringItem(mMidlet, this, Locale.get("calendar.NoApptsToday"));
		
		FramedForm f = null;
		
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		
		//#style CalendarHeader
		mHeader = new StringItem(null, "");
		f.append(Graphics.TOP, mHeader);

		showTicker(mMidlet.mSettings.getShowApptTicker());
		f.setCommandListener(this);
		setMenu();
	}

	private void setMenu() {
		mShowFragmentCmd = new Command(Locale.get("mailList.Fragment"), Command.ITEM, 1);
		
		//#if polish.hasCommandKeyEvents || (polish.key.LeftSoftKey:defined && polish.key.RightSoftKey:defined)
			//#define tmp.hasCmdKeyEvts
		//#endif
		
		FramedForm f = null;
		
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		
		mView.addCommand(ACTIONS);

		f.addSubCommand(ACCEPT, ACTIONS);
		f.addSubCommand(DECLINE, ACTIONS);
		f.addSubCommand(TENTATIVE, ACTIONS);
		
		//#ifdef tmp.hasCmdKeyEvts
			//#style SevenMenuItem
			f.addSubCommand(DELETE, ACTIONS);
		//#else
			f.addSubCommand(DELETE, ACTIONS);
		//#endif
			
		
		f.addCommand(ZimbraME.GOTO);
		
		f.addSubCommand(GOTO_NEXTDAY, ZimbraME.GOTO);
		f.addSubCommand(GOTO_PREVDAY, ZimbraME.GOTO);
		f.addSubCommand(GOTO_TODAY, ZimbraME.GOTO);
		//#ifdef tmp.hasCmdKeyEvts
			//#style NineMenuItem
			f.addSubCommand(mShowFragmentCmd, ZimbraME.GOTO);
		//#else
			f.addSubCommand(mShowFragmentCmd, ZimbraME.GOTO);
		//#endif


		
		//#ifdef tmp.hasCmdKeyEvts
			//#style ZeroMenuItem
			f.addSubCommand(ZimbraME.GOTO_INBOX, ZimbraME.GOTO);
		//#else
			f.addSubCommand(ZimbraME.GOTO_INBOX, ZimbraME.GOTO);
		//#endif
			
		f.addSubCommand(ZimbraME.GOTO_SETTINGS, ZimbraME.GOTO);
				
		//#ifdef tmp.hasCmdKeyEvts
			//#style OneMenuItem
			f.addCommand(ZimbraME.SEARCH);
		//#else
			f.addCommand(ZimbraME.SEARCH);
		//#endif
		
		//#ifdef tmp.hasCmdKeyEvts
			//#style TwoMenuItem
			f.addCommand(NEW);
		//#else
			f.addCommand(NEW);
		//#endif
			
		f.addCommand(REFRESH);
			
		//#undefine tmp.hasCmdKeyEvts
	}

}
