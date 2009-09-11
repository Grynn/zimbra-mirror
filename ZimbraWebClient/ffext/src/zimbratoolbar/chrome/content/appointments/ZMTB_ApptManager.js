var ZMTB_ApptManager = function(zmtb)
{
	this._notificationBox = document.getElementById("ZMTB-Appointments");
	ZMTB_TBItem.call(this, zmtb);
	this.reset();
	this._rqManager = zmtb.getRequestManager();
	this._rqManager.addUpdateListener(this);
	var This = this;
	this._animateInterval = {};
}

ZMTB_ApptManager.HOURSPREV = "6";
ZMTB_ApptManager.HOURSPAST = "18";
ZMTB_ApptManager.ALMINUTESPAST = "5";

ZMTB_ApptManager.prototype.reset = function()
{
	if(!this._appts)
		this._appts = [];
	else
	{
		this._clearTimers();
		this._appts = [];
	}
	this._snoozes = [];
}

ZMTB_ApptManager.prototype.enable = function()
{
	this._notificationBox.hidden = false;
}

ZMTB_ApptManager.prototype.disable = function()
{
	this._notificationBox.hidden = true;
}

ZMTB_ApptManager.prototype.receiveUpdate = function(responseObj)
{
	if(responseObj.Body.BatchResponse && responseObj.Body.BatchResponse.SearchResponse)
	{
		for (var i=0; i < responseObj.Body.BatchResponse.SearchResponse.length; i++)
			if(responseObj.Body.BatchResponse.SearchResponse[i].appt)
			{
				this._clearTimers();
				this._appts = [];
				var appts = responseObj.Body.BatchResponse.SearchResponse[i].appt;
				for (var j=0; j < appts.length; j++) {
					//If alarm time is set to 0 on an appt, no alarm will go off
					var alarm = 0;

					//Set alarm time if appt is set to ring
					if(appts[j].alarmData)
						alarm = appts[j].alarmData[0].nextAlarm;

					//Add appt to array
					this._appts.push({id:appts[j].id, name:appts[j].name, date:appts[j].inst[0].s, duration:appts[j].dur, loc:appts[j].loc, info:(appts[j].fr?appts[j].fr:""), alarm:alarm, folder:appts[j].l, compNum:appts[j].compNum, invId:appts[j].invId});

					//If the appt was reset, clear the snooze
					if(this._getSnoozeById(appts[j].id) && appts[j].inst[0].s != this._getSnoozeById(appts[j].id).date)
					{
						// Components.utils.reportError("Appt coming in has date: "+ appts[j].inst[0].s + ", but snooze has date: "+this._getSnoozeById(appts[j].id).date);
						this.clearSnooze(appts[j].id);
					}
				};
			}
		//this._checkSnoozes();
		this._setTimers();
	}
	if(responseObj.Body.SearchResponse && responseObj.Body.SearchResponse.appt)
	{
		var appts = responseObj.Body.SearchResponse.appt;
		this._clearTimers();
		this._appts = [];
		for (var i=0; i < appts.length; i++) {
			//If alarm time is set to 0 on an appt, no alarm will go off
			var alarm = 0;
			
			//Set alarm time if appt is set to ring
			if(appts[i].alarmData)
				alarm = appts[i].alarmData[0].nextAlarm;
			
			//Add appt to array
			this._appts.push({id:appts[i].id, name:appts[i].name, date:appts[i].inst[0].s, duration:appts[i].dur, loc:appts[i].loc, info:(appts[i].fr?appts[i].fr:""), alarm:alarm, folder:appts[i].l, compNum:appts[i].compNum, invId:appts[i].invId});
			
			//If the appt was reset, clear the snooze
			if(this._getSnoozeById(appts[i].id) && appts[i].s != this._getSnoozeById(appts[i].id).date)
				this.clearSnooze(appts[i].id);
		};
		//this._checkSnoozes();
		this._setTimers();
	}
}

ZMTB_ApptManager.prototype.receiveError = function(error)
{
}

ZMTB_ApptManager.prototype.notify = function(id)
{
	var appt = (this._getSnoozeById(id)?this._getSnoozeById(id):this._getApptById(id));
	var notifyBox = this._notificationBox;
	var date = new Date();
	date.setTime(appt.date);
	var hours = date.getHours();
	var minutes = (date.getMinutes()>9?date.getMinutes():"0"+date.getMinutes());
	var ampm = "AM";
	if(hours >= 12)
	{
		ampm = "PM";
		if(hours > 12)
			hours = hours - 12;
	}
	else if(hours == "0")
		hours = "12";
	var timestring = hours.toString()+":"+minutes+" "+ampm;
	var This = this;
	//
	var message = "Appointment: "+appt.name+" - "+appt.loc+" "+timestring;
	var note = document.getElementById("ZMTB-Appt-Box").cloneNode(true);
	note.setAttribute("id", "");
	document.getElementById("ZMTB-Appointments").appendChild(note);
	note.value = id;
	note.getElementsByClassName("ZMTB-Appt-Dismiss")[0].addEventListener("command", function(event){This.dismiss(note)}, false);
	var snoozes = note.getElementsByClassName("ZimTB-Snooze-Choice");
	for (var i=0; i < snoozes.length; i++)
		snoozes[i].addEventListener("command", function(event){This.snooze(note, event.target.value)}, false);
	note.getElementsByClassName("ZMTB-Appt-Message")[0].value = message;
	
	this._openNotification(note);
	
	// notifyBox.appendNotification(message, appt.id, "chrome://zimbratb/skin/calendar.gif", notifyBox.PRIORITY_INFO_MED, [{label:"Snooze", popup:"ZimTB-Snooze-Menu", accessKey:"S"}, {label:"Dismiss", callback:function(note, button){This.dismiss(note, button)}, accessKey:"D"}]);
}

ZMTB_ApptManager.prototype._openNotification = function(note)
{
	note.style.height = "0px";
	note.hidden = false;
	this._animateInterval[note.value] = setInterval(this._animateNote, 10, this, note, "open");
}

ZMTB_ApptManager.prototype._closeNotification = function(note)
{
	this._animateInterval[note.value] = setInterval(this._animateNote, 10, this, note, "close");
}

ZMTB_ApptManager.prototype._animateNote = function(This, note, openClose)
{
	if(openClose == "open")
	{
		if(parseInt(note.style.height)>=32)
		{
			clearInterval(This._animateInterval[note.value]);
			return;
		}
		note.style.height = (parseInt(note.style.height)+3).toString()+"px";
	}
	else if(openClose == "close")
	{
		if(parseInt(note.style.height) <= 0)
		{
			This._notificationBox.removeChild(note);
			clearInterval(This._animateInterval[note.value]);
			return;
		}
		note.style.height = (parseInt(note.style.height)-3).toString()+"px";
	}
}



ZMTB_ApptManager.prototype.dismiss = function(notification)
{
	this.clearSnooze(notification.value);
	this._dismissAppt(notification.value);
	this._closeNotification(notification);
}

ZMTB_ApptManager.prototype.snooze = function(notification, minutes)
{
	var curS = this._getSnoozeById(notification.value);
	if(curS)
		curS.alarm = (new Date).getTime() + (minutes*60*1000);
	else
	{
		this._getApptById(notification.value).alarm = (new Date).getTime() + (minutes*60*1000);
		this._snoozes.push(this._getApptById(notification.value));
	}
	this.clearAppt(notification.value);
	this._setTimers();
	this._closeNotification(notification);
	//this._dismissAppt(notification.value);
}


ZMTB_ApptManager.prototype.clearSnooze = function(id)
{
	for (var i = this._snoozes.length - 1; i >= 0; i--)
		if(this._snoozes[i].id == id)
		{
			window.clearTimeout(this._snoozes[i].timer);
			this._snoozes.splice(i, 1);
			return;
		}
}

ZMTB_ApptManager.prototype.clearAppt = function(id)
{
	for (var i = this._appts.length - 1; i >= 0; i--)
		if(this._appts[i].id == id)
		{
			window.clearTimeout(this._appts[i].timer);
			this._appts.splice(i, 1);
			return;
		}
}

ZMTB_ApptManager.prototype._dismissAppt = function(apptId)
{
	var sd = ZMTB_AjxSoapDoc.create("DismissCalendarItemAlarmRequest", ZMTB_RequestManager.NS_MAIL);
	var appt = sd.set("appt",{"id":apptId, "dismissedAt":(new Date()).getTime()});
	this._rqManager.sendRequest(sd);
}

ZMTB_ApptManager.prototype._setTimers = function()
{
	var This = this;
	for (var i=0; i < this._appts.length; i++) 
	{
		//Alarm must be in the future but not more than ZMTB_ApptManager.HOURSPAST hours away
		if((this._appts[i].alarm - (new Date).getTime()) > 0 && (this._appts[i].alarm - (new Date).getTime()) < (ZMTB_ApptManager.HOURSPAST * 60 * 60 * 1000)  && !this._appts[i].timer)
		{
			//Components.utils.reportError("Timer set for: "+ this._appts[i].name + " With timeout: "+(this._appts[i].alarm - (new Date).getTime()).toString());
			this._appts[i].timer = window.setTimeout(function(id){This.notify(id)}, this._appts[i].alarm - (new Date).getTime(), this._appts[i].id);
			// this._appts[i].timer = window.setTimeout("ZMTB_ApptNotify("+this._appts[i].id+")", this._appts[i].alarm - (new Date).getTime());
		}
	};
	for (var i=0; i < this._snoozes.length; i++) 
	{
		if((this._snoozes[i].alarm - (new Date).getTime()) > 0)
		{
			//Must check and reset for snoozes as their alarm times will change
			if(this._snoozes[i].timer)
				window.clearTimeout(this._snoozes[i].timer);
			this._snoozes[i].timer = window.setTimeout(function(id){This.notify(id)}, this._snoozes[i].alarm - (new Date).getTime(), this._snoozes[i].id);
			// this._snoozes[i].timer = window.setTimeout("ZMTB_ApptNotify("+this._snoozes[i].id+")", this._snoozes[i].alarm - (new Date).getTime());
			//Components.utils.reportError("Snooze set for: "+ this._snoozes[i].name + " With timeout: "+(this._snoozes[i].alarm - (new Date).getTime()).toString());
		}
	}
}

ZMTB_ApptManager.prototype._clearTimers = function()
{
	for (var i=0; i < this._appts.length; i++)
		if(this._appts[i].timer)
			window.clearTimeout(this._appts[i].timer);
}

ZMTB_ApptManager.prototype._checkSnoozes = function()
{
	for (var i=0; i < this._snoozes.length; i++)
		//Check if the appointment has been dismissed
		if(!this._getApptById(this._snoozes[i].id))
			this.clearSnooze(this._snoozes[i].id);
}

ZMTB_ApptManager.prototype._getApptById = function(id)
{
	for (var i=0; i < this._appts.length; i++)
	{
		if(this._appts[i].id == id)
			return this._appts[i];
	}
}

ZMTB_ApptManager.prototype._getSnoozeById = function(id)
{
	for (var i=0; i < this._snoozes.length; i++)
		if(this._snoozes[i].id == id)
			return this._snoozes[i];
}